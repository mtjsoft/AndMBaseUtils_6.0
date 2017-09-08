package com.huahan.hhbaseutils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.huahan.hhbaseutils.model.HHSystemContactModel;
import com.huahan.hhbaseutils.model.HHSystemPhotoModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 系统的帮助类
 * @author yuan
 *
 */
public class HHSystemUtils
{

	public static final int GET_CAMERA_IMAGE=1000;
	public static final int GET_ALBUM_IMAGE=1001;
	public static final int GET_CROP_IMAGE = 1002;
	/**
	 * 应用的语言
	 */
	public static final String APP_LANGUAGE="app_language";
	/**
	 * 判断SD卡是否装载
	 * @return	true,sd卡已装载；false，sd卡未装载
	 */
	public static boolean isSDExist()
	{
		String state = Environment.getExternalStorageState();
		if (state.equals(Environment.MEDIA_MOUNTED))
		{
			return true;
		}
		return false;
	}
	/**
	 * 判断手机是否有root权限
	 * @return
	 */
	public static boolean isRoot()
	{
		boolean root = false;  
            if ((!new File("/system/bin/su").exists())  
                    && (!new File("/system/xbin/su").exists())) 
            {  
                root = false;  
            } else {  
                root = true;  
            }  
        return root;  
	}
	/**
	 * 获取手机的IMSI号
	 * @param context
	 * @return
	 */
	public static String getIMSI(Context context)
	{
		 TelephonyManager telephonyManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);  
         String imsi = telephonyManager.getSubscriberId();  
         return imsi;
	}
	/**
	 * 获取手机的IMEI号
	 * @param context
	 * @return
	 */
	public static String getIMEI(Context context)
	{
		TelephonyManager telephonyManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);  
		String imsi = telephonyManager.getDeviceId();  
		return imsi;
	}
	/**
	 * 获取手机号
	 * @param context
	 * @return	手机号是和手机卡相关的，如果手机卡中没有写入手机号的信息则获取不到手机号
	 */
	public static String getPhoneNumber(Context context)
	{
		TelephonyManager telephonyManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);  
        String number = telephonyManager.getLine1Number();
        return number;
	}
	/**
	 * 获取手机的型号，如lenovo-a750等
	 * @return
	 */
	public static String getPhoneType()
	{
		return Build.MODEL;
	}
	/**
	 * 获取手机的制造厂商
	 * @return
	 */
	public static String getPhoneMaker()
	{
		return Build.MANUFACTURER;
	}
	/**
	 * 获取资源的ID
	 * @param context		上下文对象
	 * @param name			资源的名称（图片的话，不用带后缀名）
	 * @param type			资源的类型
	 * @return
	 * 0：表示的是没有找到相应的资源文件
	 */
	public static int getResourceID(Context context,String name,String type)
	{
		int identifier = context.getResources().getIdentifier(context.getPackageName()+":"+type+"/"+name, null, null);
		return identifier;
	}
	/**
	 * 获取正在运行的服务
	 * @param context 上下文对象
	 * @param maxNum 最多获取的服务的个数
	 * @return
	 */
	public static List<RunningServiceInfo> getRunningService(Context context,int maxNum)
	{
		List<RunningServiceInfo> list=new ArrayList<ActivityManager.RunningServiceInfo>();
		ActivityManager manager=(ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		list=manager.getRunningServices(maxNum);
		return list;
	}
	/**
	 * 判断某个服务是否开启
	 * @param list			正在运行的服务的集合
	 * @param className		查看的服务的类名
	 * @return
	 */
	public  static boolean isServiceStarted(Context context,String className)
	{
		List<RunningServiceInfo> list = getRunningService(context, 30);
		for (int i = 0; i < list.size(); i++)
		{
			if (className.equals(list.get(i).service.getClassName()))
			{
				return true;
			}
		}
		return false;
	}
	/**
	 * 判断当前的Intent是否可用，也就是判断有没有应用可以处理这个Intent
	 * @param context
	 * @param intent
	 * @return
	 */
	public static boolean isIntentAvailable(Context context, Intent intent) 
	{
		final PackageManager packageManager = context.getPackageManager();
		List<ResolveInfo> list = packageManager.queryIntentActivities(intent,
				PackageManager.MATCH_DEFAULT_ONLY);
		return list.size() > 0;
	}
	/**
	 * 判断一个Action为指定Action的Intent是否可用
	 * @param context
	 * @param action
	 * @return
	 */
	public static boolean isIntentAvailable(Context context, String action) 
	{
		Intent intent=new Intent(action);
		return isIntentAvailable(context, intent);
	}
	/**
	 * 显示软键盘
	 * @param context
	 * @param v
	 */
	public static void showSystemKeyBoard(Context context,View v)
	{
		  InputMethodManager imm = (InputMethodManager) (
				  context.getSystemService(Context.INPUT_METHOD_SERVICE));
		  imm.showSoftInput(v, 0);
	}
	/**
	 * 隐藏软键盘
	 * @param context 		上下文对象
	 * @param v				获取焦点的View
	 */
	public static void hideSystemKeyBoard(Context context,View v)
	{
		  InputMethodManager imm = (InputMethodManager) (
				  context.getSystemService(Context.INPUT_METHOD_SERVICE));
		  imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
	}
	/**
	 * 打开或者关闭软键盘，如果打开的话就关闭；如果是关闭的就打开
	 * @param context
	 */
	public static void toogleKeyboard(Context context) 
	{
		InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);  
		if (imm.isActive()) 
		{ 
			imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS); 
		} 
	}
	/**
	 * 判断GPS是否开启，只要gps或者agps开启一个就认为开启了gps
	 * @param context
	 * @return
	 */
	public static boolean isGPSOpen(Context context)
	{
		LocationManager manager=(LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		boolean flag=manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		boolean flag1=manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		if (flag1||flag)
		{
			return true;
		}
		return false;
	}
	/**
	 * 强制开启gps
	 * @param context
	 */
	public static void openGPS(Context context)
	{
		Intent GPSIntent = new Intent();
		GPSIntent.setClassName("com.android.settings",
		"com.android.settings.widget.SettingsAppWidgetProvider");
		GPSIntent.addCategory("android.intent.category.ALTERNATIVE");
		GPSIntent.setData(Uri.parse("custom:3"));
		try {
			PendingIntent.getBroadcast(context, 0, GPSIntent, 0).send();
		} catch (CanceledException e) 
		{
			e.printStackTrace();
		}

	}
	/**
	 * 通过相机获取图片，使用startActivityForResult的形式开启系统的相机
	 * requestCode使用的是GET_CAMERA_IMAGE，值为1000
	 * 
	 * @param activity					开启相机的Activity
	 * @param fileSavePath				文件的保存路径，如果不需要不存图片，则参数可以为null或者""
	 */
	public static void getImageFromCamera(Activity activity,String fileSavePath)
	{
		Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		if (!TextUtils.isEmpty(fileSavePath))
		{
			File file=new File(fileSavePath);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
		}
		activity.startActivityForResult(intent, GET_CAMERA_IMAGE);
		
	}
	/**
	 * 通过相册获取图片，使用startActivityForResult的形式开启系统的相册
	 * requestCode使用的是GET_ALBUM_IMAGE，值为1001
	 * 
	 * @param activity					开启相册的Activity
	 */
	public static void getImageFromAlbum(Activity activity)
	{
		Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("image/*");
		activity.startActivityForResult(intent, GET_ALBUM_IMAGE);;
	}
	/**
	 * 裁剪图片
	 * @param activity			启动裁剪图片的Activity
	 * @param uri				图片的uri路径
	 * @param savePath			裁剪后图片的保存路径，如果不想保存的话传null或者“”
	 * @param aspectX			裁剪时X轴的比例
	 * @param aspectY			裁剪时Y轴的比例
	 * @param outputX			裁剪图片输出的X轴的长度
	 * 如果savePath为null或者“”，在onActivityResult中接收系统默认返回的是裁剪的缩略图，使用data.getExtras().get("data");接收。
	 * 该方法以startActivityForResult的方式启动系统的裁剪工具，requestCode为GET_CROP_IMAGE，值为1002
	 */
	public static void cropImage(Activity activity,Uri uri,String savePath,int aspectX,int aspectY,int outX)
	{
		
        Intent intent = new Intent("com.android.camera.action.CROP");  
        intent.setDataAndType(uri, "image/*");  
        //下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪  
        intent.putExtra("crop", "true");  
        // aspectX aspectY 是宽高的比例  
        intent.putExtra("aspectX", aspectX);  
        intent.putExtra("aspectY", aspectY);  
        // outputX outputY 是裁剪图片宽高  
        intent.putExtra("outputX", outX);  
        intent.putExtra("outputY", outX*aspectY/aspectX);  
        if (TextUtils.isEmpty(savePath))
		{
			intent.putExtra("return-data", true);
		}else {
			 File saveFile=new File(savePath);
			 intent.putExtra("return-data", false); 
		     intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(saveFile));
		}
       
        activity.startActivityForResult(intent, GET_CROP_IMAGE);  
	}
	/**
	 * 获取手机中注册的图片
	 * @return			返回手机中在数据库中注册的图片。如果没有图片，返回一个空的集合。
	 */
	public static List<HHSystemPhotoModel> getSystemPhotoList(Context context)
	{
		ContentResolver resolver=context.getContentResolver();
		Cursor cursor = resolver.query(Media.EXTERNAL_CONTENT_URI, new String[]{Media.BUCKET_DISPLAY_NAME,Media.SIZE,Media.ORIENTATION,Media.DISPLAY_NAME,Media.DATE_ADDED,Media.DATA}, null, null, Media.DATE_ADDED+" desc");
		List<HHSystemPhotoModel> list=new ArrayList<HHSystemPhotoModel>();
		if (cursor.getCount()>0)
		{
			
			while(cursor.moveToNext())
			{
				String filePath=cursor.getString(cursor.getColumnIndex(Media.DATA));
				if (HHFileUtils.isFileExist(filePath))
				{
					HHSystemPhotoModel model=new HHSystemPhotoModel();
					model.setAddDate(cursor.getLong(cursor.getColumnIndex(Media.DATE_ADDED)));
					model.setDirName(cursor.getString(cursor.getColumnIndex(Media.BUCKET_DISPLAY_NAME)));
					model.setDisplayName(cursor.getString(cursor.getColumnIndex(Media.DISPLAY_NAME)));
					model.setFilePath(filePath);
					model.setOrientation(cursor.getInt(cursor.getColumnIndex(Media.ORIENTATION)));
					model.setSize(cursor.getInt(cursor.getColumnIndex(Media.SIZE)));
					list.add(model);
				}
			}
		}
		cursor.close();
		return list;
	}
	/**
	 * 获取Java虚拟机可以获取的最大的内存
	 * @return
	 */
	public static long getMaxMemory()
	{
		return Runtime.getRuntime().maxMemory();
	}
	/**
	 * 获取Java虚拟机已经从操作系统获取的内存
	 * @return
	 */
	public static long getTotalMemory()
	{
		return Runtime.getRuntime().totalMemory();
	}
	/**
	 * 获取Java虚拟机从操作系统获取的但是还未使用的内存
	 */
	public static long getFreeMemory()
	{
		return Runtime.getRuntime().freeMemory();
	}
	/**
	 * 获取当前可用的内存
	 * @return
	 */
	public static long getNowAvalibleMemory()
	{
		long avalible=getMaxMemory()-getTotalMemory()+getFreeMemory();
		return avalible;
	}
	/**
	 * 获取手机联系人,获取联系人信息需要添加权限<br/>
	 * <code>
	 *  &lt;uses-permission android:name="android.permission.READ_CONTACTS"/&gt;
	 *  </code>
	 * @param context
	 * @return
	 */
	public static List<HHSystemContactModel> getSystemContactList(Context context)
	{
		ContentResolver resolver=context.getContentResolver();
		List<HHSystemContactModel> list=new ArrayList<HHSystemContactModel>();
		Cursor cursor = resolver.query(Phone.CONTENT_URI, null, null, null, null);
		if (cursor.getCount()>0)
		{
			while(cursor.moveToNext())
			{
				HHSystemContactModel model=new HHSystemContactModel();
				model.setName(cursor.getString(cursor.getColumnIndex(Phone.DISPLAY_NAME)));
				model.setPhoneNumber(cursor.getString(cursor.getColumnIndex(Phone.NUMBER)));
				model.setId(cursor.getString(cursor.getColumnIndex(Phone._ID)));
				list.add(model);
			}
		}
		cursor.close();
		return list;
	}
	public static void changeLanguage(Context context,Locale locale)
	{
		
		Resources resources = context.getResources();
		Configuration configuration = resources.getConfiguration();
		if (configuration.locale.getLanguage().equals(locale.getLanguage()))
		{
			return ;
		}
		DisplayMetrics displayMetrics = resources.getDisplayMetrics();
		configuration.locale=locale;
		resources.updateConfiguration(configuration, displayMetrics);
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		Editor edit = preferences.edit();
		edit.putString(APP_LANGUAGE, locale.getLanguage());
		edit.commit();
	}
	/**
	 * 获取当前的语言
	 * @param context		上下文对象
	 * @return
	 */
	public static Locale getAppLanguage(Context context)
	{
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		String language = preferences.getString(APP_LANGUAGE, "");
		if (TextUtils.isEmpty(language))
		{
			return context.getResources().getConfiguration().locale;
		}else {
			Locale locale=new Locale(language);
			return locale;
		}
	}
}
