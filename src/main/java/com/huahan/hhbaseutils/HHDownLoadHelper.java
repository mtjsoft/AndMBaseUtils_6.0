package com.huahan.hhbaseutils;

import com.huahan.hhbaseutils.imp.DownLoadListener;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 下载文件的帮助类
 * @author yuan
 *
 */
public class HHDownLoadHelper
{
	private static final String tag=HHDownLoadHelper.class.getName();
	/**
	 * 下载文件，设置的连接超时是5秒钟
	 * @param fileUrl				文件的url路径
	 * @param savePath				文件的保存路径
	 * @param listener				下载进度的监听器
	 * @return 下载成功的话返回true，否则返回失败
	 */
	
	public static boolean download(String fileUrl,String savePath,DownLoadListener listener)
	{
		try
		{
			HHLog.i(tag, "save path:"+savePath);
			URL url=new URL(fileUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(5000);
			conn.setDoInput(true);
			int totalSize=conn.getContentLength();
			InputStream inputStream = conn.getInputStream();
			HHStreamUtils.writeStreamToFile(inputStream, savePath,totalSize,listener);
			return true;
		} catch (Exception e)
		{
			HHLog.e(tag, "download ", e);
		}
		return false;
	}
	/**
	 * 下载文件，设置的超时时间是5秒钟
	 * @param fileUrl			文件的Url地址
	 * @param savePath			文件的保存地址
	 * @return
	 */
	public static boolean download(String fileUrl,String savePath)
	{	
		return download(fileUrl, savePath,null);
	}
	/**
	 * 下载文件，使用临时文件
	 * @param fileUrl			文件的路径
	 * @param savePath			文件的保存路径
	 * @param listener			文件下载进度的监听器
	 * @return
	 */
	public static boolean downloadWithTemp(String fileUrl,String savePath,DownLoadListener listener)
	{
		int size=HHFileUtils.getUrlFileSize(fileUrl);
		int downloadSize=0;
		try
		{
			File file=new File(savePath+".temp");
		
			if (file.exists())
			{
				RandomAccessFile randomAccessFile=new RandomAccessFile(file, "rwd");
				if (randomAccessFile.length()>0)
				{
					downloadSize=randomAccessFile.readInt();
				}
				randomAccessFile.close();
			}
			HHLog.i(tag,"downloadWithTemp: file size is "+size+",download size is"+downloadSize);
			URL url=new URL(fileUrl);
			HttpURLConnection conn=(HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(5000);
			conn.setDoInput(true);
			conn.setRequestProperty("Range", "bytes="+downloadSize+"-"+size);
			InputStream stream=conn.getInputStream();
			HHStreamUtils.writeStreamToRandomAccessFile(stream, savePath, size, listener);
			return true;
		} catch (Exception e)
		{
			HHLog.e(tag, "downloadWithTemp", e);
		}
		return false;
	}
	/**
	 * 下载文件，使用临时文件	
	 * @param fileUrl		文件的url地址
	 * @param savePath		文件的保存路径
	 * @return
	 */
	public static boolean downloadWithTemp(String fileUrl,String savePath)
	{
		return downloadWithTemp(fileUrl, savePath,null);
	}

	
}
