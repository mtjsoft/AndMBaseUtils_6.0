package com.huahan.hhbaseutils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;


public class HHConfigUtils
{
	
//	private static final String tag=HHConfigUtils.class.getSimpleName();
//	private static JSONObject getConfigJson(Context context,String objectName) throws IOException, JSONException
//	{
//		InputStream inputStream = HHStreamUtils.getInputStreamFromAssets(context, HHConstantParam.FILE_CONFIG);
//		String json=HHStreamUtils.convertStreamToString(inputStream);
//		JSONObject jsonObject=new JSONObject(json);
//		jsonObject=jsonObject.getJSONObject(objectName);
//		return jsonObject;
//	}
	/**
	 * 设置本地配置文件的值
	 * @param context				上下文对象
	 * @param configFile			配置文件的名称
	 * @param paramName				设置的值的名称
	 * @param paramValue			设置的值
	 */
	public static void setConfigInfo(Context context,String configFile,String paramName,String paramValue)
	{
		SharedPreferences preferences = context.getSharedPreferences(configFile, Context.MODE_PRIVATE);
		Editor edit = preferences.edit();
		edit.putString(paramName, paramValue);
		edit.commit();
	}
}
