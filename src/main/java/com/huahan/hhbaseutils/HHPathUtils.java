package com.huahan.hhbaseutils;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import java.io.File;

public class HHPathUtils {
	private static HHPathUtils instance = null;
	private File voicePath=null;
	
	public static HHPathUtils getInstance() {
		if (instance == null)
			instance = new HHPathUtils();
		return instance;
	}
	/**
	 * 初始化路径
	 * @param context
	 */
	private void initDirs(Context context,String cache_dir_name)
	{
		voicePath=creatVoicePath(context, cache_dir_name);
		if (!voicePath.exists()) {
			voicePath.mkdirs();
		}
	}
	/**
	 * 创建文件
	 * @param context
	 * @param cache_dir_name
	 * @return
	 */
	private static File creatVoicePath(Context context,String cache_dir_name)
	{
		return new File(getBaseCacheDir(context, cache_dir_name), "voice/");
	}
	/**
	 * 根目录
	 * @param context
	 * @param cache_dir_name
	 * @return
	 */
	private static String getBaseCacheDir(Context context,String cache_dir_name)
	{
		String packageName=context.getPackageName();
		String cacheDirName=cache_dir_name;
		if (TextUtils.isEmpty(cacheDirName)) {
			cacheDirName="huahan";
		}
		if (HHSystemUtils.isSDExist())
		{
			return Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+cacheDirName+"/";
		}else {
			return "/data/data/"+packageName+"/"+cacheDirName+"/";
		}
	}
	public File getVoicePath(Context context,String cache_dir_name)
	{
		initDirs(context, cache_dir_name);
		return voicePath;
	}
}
