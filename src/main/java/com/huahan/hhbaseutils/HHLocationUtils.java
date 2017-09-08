package com.huahan.hhbaseutils;

import android.content.Context;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;

public class HHLocationUtils
{
	private static final String tag=HHLocationUtils.class.getSimpleName();
	//定位
	private LocationClient mLocationClient;
	private LocationListener mListener;
	private static int mTime=0;
	//私有化构造函数
	private HHLocationUtils(){}
	//工具类的实例
	private static HHLocationUtils mHHLocationUtils;
	public static HHLocationUtils getInstance(Context context)
	{
		synchronized (HHLocationUtils.class)
		{
			if (mHHLocationUtils==null)
			{
				mHHLocationUtils=new HHLocationUtils();
				mHHLocationUtils.init(context);
			}
			return mHHLocationUtils;
		}
		
	}
	public static HHLocationUtils getInstance(Context context,int time)
	{
		mTime=time;
		return getInstance(context);
	}
	/**
	 * 初始化
	 * @param context
	 */
	private void init(Context context)
	{
		mLocationClient=new LocationClient(context.getApplicationContext());
		mLocationClient.registerLocationListener(new MyLocationListener());
		initOption();
	}
	/**
	 * 初始化设置的参数，默认是设置值定位一次
	 */
	private void initOption()
	{
		 	LocationClientOption option = new LocationClientOption();
	        option.setLocationMode(LocationMode.Hight_Accuracy);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
	        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系，
	        option.setScanSpan(mTime);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
	        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
	        option.setOpenGps(true);//可选，默认false,设置是否使用gps
	        option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
	        option.setIgnoreKillProcess(true);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
	        mLocationClient.setLocOption(option);
	}
	public void requestLocation(LocationListener listener)
	{
		mListener=listener;
		if(!mLocationClient.isStarted())
		{
			mLocationClient.start();
		}
		int requestLocation = mLocationClient.requestLocation();
		HHLog.i(tag, "requestLocation:request code is "+requestLocation);
	}
	private class MyLocationListener implements BDLocationListener
	{

		@Override
		public void onReceiveLocation(BDLocation arg0)
		{
			// TODO Auto-generated method stub
			HHLog.i(tag, "location:"+arg0.getLocType());
			//定位一次以后关闭定位的服务
			mLocationClient.stop();
			if (mListener!=null)
			{
				mListener.onGetLocation(arg0);
			}
		}
		
	}
	public interface LocationListener
	{
		public void onGetLocation(BDLocation bdLocation);
	}
	/**
	 * 获取定位的结果
	 * @param location			定位的结果
	 * @return					true：一般情况下是定位成功了，已经获取到了定位的结果；false：定位失败，
	 */
	public static boolean getLocationResult(BDLocation location)
	{
		int locationType=location.getLocType();
		HHLog.i(tag, "getLocationResult=="+locationType);
		switch (locationType) {
		case BDLocation.TypeCacheLocation:
		case BDLocation.TypeGpsLocation:
		case BDLocation.TypeNetWorkLocation:
//		case BDLocation.TypeOffLineLocation://离线定位结果
			return true;
		default:
			break;
		}
		return false;
	}
	
}
