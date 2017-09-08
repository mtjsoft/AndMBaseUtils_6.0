package com.huahan.hhbaseutils;

/**
 * Created by chen on 2016/5/20.
 */

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Looper;

import com.huahan.hhbaseutils.constant.HHConstantParam;
import com.huahan.hhbaseutils.ui.HHActivity;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.HashMap;
import java.util.Map;

/**
 * UncaughtException处理类,当程序发生Uncaught异常的时候,有该类来接管程序,并记录发送错误报告.
 * 需要在Application中注册，为了要在程序启动器就监控整个程序。
 */
public class HHCrashHandler implements UncaughtExceptionHandler {
    public static final String TAG = "CrashHandler";
    //系统默认的UncaughtException处理类
    private UncaughtExceptionHandler mDefaultHandler;
    //CrashHandler实例
    private static HHCrashHandler instance;
    //程序的Context对象
    private Context mContext;
    //项目名称
    private String projectName = "";
    //BUG上传地址
    private final String requestUrl = HHConstantParam.BUG_IP;

    /**
     * 保证只有一个CrashHandler实例
     */
    private HHCrashHandler() {
    }

    /**
     * 获取CrashHandler实例 ,单例模式
     */
    public static HHCrashHandler getInstance() {
        if (instance == null)
            instance = new HHCrashHandler();
        return instance;
    }

    /**
     * 初始化
     *
     * @param context
     * @param projectName 项目名称
     */
    public void init(Context context, String projectName) {
        mContext = context;
        this.projectName = projectName;
        //获取系统默认的UncaughtException处理器
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        //设置该CrashHandler为程序的默认处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     * 当UncaughtException发生时会转入该函数来处理
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (!handleException(ex) && mDefaultHandler != null) {
            //如果用户没有处理则让系统默认的异常处理器来处理
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            try {
                Thread.sleep(3000);// 3秒钟后重启应用
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            AlarmManager mgr = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
            //换成自己的重启页面
            Intent intent = new Intent(mContext, HHActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("crash", true);
            PendingIntent restartIntent = PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_ONE_SHOT);
            mgr.set(AlarmManager.RTC, System.currentTimeMillis(), restartIntent);//重启应用
            //退出程序
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);
            System.gc();
        }
    }

    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
     *
     * @param ex
     * @return true:如果处理了该异常信息;否则返回false.
     */
    private boolean handleException(Throwable ex) {
        if (ex == null) {
            return false;
        }
        //使用Toast来显示异常信息
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                //换成自己的异常崩溃提示语
                HHTipUtils.getInstance().showToast(mContext, R.string.hh_net_error);
                Looper.loop();
            }
        }.start();
        //保存错误信息到文件中
        saveCatchInfo2File(ex);
        return true;
    }

    /**
     * 保存错误信息到文件中
     *
     * @param ex
     * @return 返回文件名称, 便于将文件传送到服务器
     */
    private void saveCatchInfo2File(Throwable ex) {
        StringBuffer sb = new StringBuffer();
        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();
        String result = writer.toString();
        sb.append(result);
        //发送给开发人员，bug上传
        uploadAppError(sb.toString());
    }

    private void uploadAppError(final String error) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                Map<String, String> map = new HashMap<String, String>();
                map.put("project_name", projectName);
                map.put("error", error);
                map.put("phone_makers", HHSystemUtils.getPhoneMaker() + "==versionName==" + HHAppUtils.getVerName(mContext));
                map.put("phone_model", HHSystemUtils.getPhoneType());
                HHWebDataUtils.sendPostRequest(requestUrl, map);
            }
        }).start();
    }
}
