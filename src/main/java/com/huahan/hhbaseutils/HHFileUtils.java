package com.huahan.hhbaseutils;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.FileChannel;

/**
 * 文件的帮助类
 * 
 * @author yuan
 * 
 */
public class HHFileUtils
{
	private static final String tag = HHFileUtils.class.getName();

	/**
	 * 获取网络文件的大小，该接口需要访问网络
	 * 
	 * @param urlFile
	 *            文件的地址
	 * @return 获取失败返回0
	 */
	public static int getUrlFileSize(String urlFile)
	{
		HttpURLConnection conn = null;
		try
		{
			URL url = new URL(urlFile);
			conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(5000);
			conn.setDoInput(true);
			int size = conn.getContentLength();
			conn.disconnect();
			return size;
		} catch (Exception e)
		{
			HHLog.e(tag, "getUrlFileSize", e);
			if (conn != null)
			{
				conn.disconnect();
			}
		}
		return 0;
	}

	/**
	 * 获取文件的大小
	 * 
	 * @param filename
	 *            文件的路径
	 * @return
	 */
	public static long getFileSize(String filename)
	{
		File file = new File(filename);
		long size = 0;
		if (file.isDirectory())
		{
			File[] files = file.listFiles();
			for (File f : files)
			{
				size += getFileSize(f.getAbsolutePath());
			}
		} else
		{
			size = file.length();
		}
		return size;
	}

	/**
	 * 创建一个目录
	 * 
	 * @param filename
	 *            文件的名称
	 * @return
	 */
	public static File createDir(String filename)
	{
		File file = new File(filename);
		if (!file.exists())
		{
			file.mkdirs();
		}
		return file;
	}

	/**
	 * 判断一个文件是否存在
	 * 
	 * @param filePath
	 *            文件或者文件夹的路径
	 * @return	  true：文件存在
	 */
	public static boolean isFileExist(String filePath)
	{
		File file = new File(filePath);
		return file.exists();
	}

	/**
	 * 删除文件，删除失败（如果删除的是文件夹，则其中一个文件删除失败则表示删除失败） 的情况下返回false。如果文件不存在或者删除成功则返回true
	 * 
	 * @param filePath
	 *            文件的路径
	 * @return
	 */
	public static boolean deleteFile(String filePath)
	{
		boolean isSuccess = true;
		File file = new File(filePath);
		if (file.exists())
		{
			if (file.isDirectory())
			{
				File[] listFiles = file.listFiles();
				for (int j = 0; j < listFiles.length; j++)
				{
					isSuccess = deleteFile(listFiles[j].getAbsolutePath()) && isSuccess;
				}
				isSuccess=file.delete()&&isSuccess;
			} else
			{
				isSuccess = file.delete() && isSuccess;
			}
		}
		return isSuccess;
	}

	/**
	 * 复制文件(可以复制文件也可以复制文件夹)
	 * @return		复制成功返回true，出现异常返回false
	 */
	public static boolean copyFile(String filePath, String desPath)
	{
		try
		{
			File file = new File(filePath);
			if (file.exists())
			{
				File desFile = new File(desPath);
				if (file.isFile())
				{
					FileInputStream fis = new FileInputStream(file);
					FileChannel inChannel = fis.getChannel();
					FileOutputStream fos = new FileOutputStream(desFile);
					FileChannel outChannel = fos.getChannel();
					inChannel.transferTo(0, inChannel.size(), outChannel);
					inChannel.close();
					outChannel.close();
					fis.close();
					fos.close();
				} else if (file.isDirectory())
				{
					desFile = new File(desPath + file.getName() + "/");
					desPath = desFile.getAbsolutePath() + "/";
					if (!desFile.exists())
					{
						desFile.mkdirs();
					}
					File[] listFiles = file.listFiles();
					for (File f : listFiles)
					{
						copyFile(f.getAbsolutePath(), desPath + f.getName());
					}
				}
			} else
			{
				Log.i(tag, "copyFile==" + filePath);
			}
			return true;
		} catch (Exception e)
		{
			e.printStackTrace();
			Log.i(tag, "copyFile ==" + e.getMessage() + "===" + e.getClass());
		}
		return false;
	}
	/**
	 * 根据uri获取路径
	 * @param context
	 * @param uri
	 * @return
	 */
	public String getUriPath(Context context, final Uri uri) {
		return HHGetPathByUriUtils.getInstance().getPath(context, uri);
	}
}
