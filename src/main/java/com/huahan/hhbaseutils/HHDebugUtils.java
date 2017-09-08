package com.huahan.hhbaseutils;

import android.content.Context;
import android.os.Process;

import com.huahan.hhbaseutils.constant.HHConstantParam;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.HashMap;
import java.util.Map;

/**
 * Debug的工具类，目前的主要功能是纪录项目的bug信息，同时提交项目的bug信息。bug信息会保存在本地，本地路径为
 * sdcard下huahan文件夹中bug文件夹中对应包名的txt文件。
 * <li>重要信息不允许在本地文件中纪录，该bug文件主要用来记录bug信息</li>
 * @author yuan
 *
 */
public class HHDebugUtils
{
	private static final String tag=HHDebugUtils.class.getSimpleName();
	/**
	 * 项目编号
	 */
	private static int mProjectCode=0;
	/**
	 * 私有化构造函数
	 */
	private HHDebugUtils(){}
	//创建一个实例对象
	private static HHDebugUtils mDebugUtils;
	
	/**
	 * 获取实例
	 */
	public static HHDebugUtils getInstanced(int projectCode)
	{
		if (mDebugUtils==null)
		{
			mProjectCode=projectCode;
			mDebugUtils=new HHDebugUtils();
		}
		return mDebugUtils;
	}
	/**
	 * 写入debug信息
	 * @param context			上下文对象
	 * @param e					错误信息
	 */	
	private void writeBugInfo(Context context,Throwable e)
	{
		byte[] bugArray=getExceptionStackTraceInfo(e);
		HHLog.e(tag, new String(bugArray));
		writeApplicationInfo(context, bugArray);
		String time=HHFormatUtils.getNowFormatString(HHConstantParam.DEFAULT_TIME_FORMAT);
		String bug=new String(bugArray);
		reportBug(time+"\n"+bug);
		Process.killProcess(Process.myPid());
	}
	/**
	 * 检查bug存放的文件夹是否存在，如果不存在就创建这个文件夹
	 */
	private void checkFile()
	{
		File file=new File(HHConstantParam.DEFAULT_BUG_INFO);
		if (!file.exists())
		{
			file.mkdirs();
		}
	}
	/**
	 * 设置未捕获异常的一个捕获器，用于显示错误信息
	 * @param context
	 */
	public void setExceptionHandler(final Context context)
	{
		Thread.currentThread().setUncaughtExceptionHandler(new UncaughtExceptionHandler()
		{
			
			@Override
			public void uncaughtException(Thread thread, Throwable ex)
			{
				HHLog.i(tag, "setExceptionHandler receive application uncatch exception");
				writeBugInfo(context, ex);
			}
		});
	}
	/**
	 * 获取异常的栈轨迹信息，也就是获取异常在logcat中打印的异常信息
	 * @param e				异常
	 * @return
	 */
	public byte[] getExceptionStackTraceInfo(Throwable e)
	{
		ByteArrayOutputStream bos=new ByteArrayOutputStream();
		PrintWriter writer=new PrintWriter(bos);
		e.printStackTrace(writer);
		writer.flush();
		writer.close();
		return bos.toByteArray();
	}
	/**
	 * 向本地文件中写入App的信息
	 * @param context
	 * @param info				写入信息的字节数组
	 */
	public void writeApplicationInfo(Context context,byte[] info)
	{
		checkFile();
		try
		{
			String nowTime=HHFormatUtils.getNowFormatString("yyyy-MM-dd", true);
			File file=new File(HHConstantParam.DEFAULT_BUG_INFO+context.getApplicationContext().getPackageName()+"-"+nowTime+".txt");
			FileOutputStream fos=new FileOutputStream(file, true);
			String time="\n"+HHFormatUtils.getNowFormatString("HH:mm:ss", true)+"\n";
			fos.write(time.getBytes());
			fos.write(info);
			fos.flush();
			fos.close();
		} catch (Exception e1)
		{
			e1.printStackTrace();
			HHLog.i(tag, "writeApplicationInfo:",e1);
		}
	}
	/**
	 * 想本地文件中写入App信息
	 * @param context
	 * @param info			App信息
	 */
	public void writeApplicationInfo(Context context,String info)
	{
		writeApplicationInfo(context, info.getBytes());
	}
	/**
	 * 发送bug信息
	 * @param bug
	 */
	private void reportBug(final String bug)
	{
		new Thread(new Runnable()
		{
			
			@Override
			public void run()
			{
				Map<String, String> map=new HashMap<String, String>();
				map.put("project_id", mProjectCode+"");
				map.put("bug_content", bug);
				String sendPostRequest_B = HHWebDataUtils.sendPostRequest("http://bbs.huahansoft.com/projectbugadd", map);
				HHLog.i(tag, "report bug:"+sendPostRequest_B);
			}
		}).start();
	}
}
