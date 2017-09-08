package com.huahan.hhbaseutils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * 网络状态的帮助类
 * @author yuan
 *
 */
public class HHNetWorkUtils
{
	private static final String tag=HHNetWorkUtils.class.getName();
	public static final int NETWORK_TYPE_WIFI=0;
	public static final int NETWORK_TYPE_2G=1;
	public static final int NETWORK_TYPE_3G=2;
	public static final int NETWORK_TYPE_4G=3;
	public static final int NETWORK_TYPE_UNKNOWN=4;
	/**
	 * 网络提供商:中国移动
	 */
	public static final int NETWORK_PROVIDER_CHINA_MOBILE=0;
	/**
	 * 网络提供商:中国联通
	 */
	public static final int NETWORK_PROVIDER_CHINA_UNION=1;
	/**
	 * 网络提供商:中国电信
	 */
	public static final int NETWORK_PROVIDER_CHINA_TELECOMS=2;
	/**
	 * 网络提供商:未知网络提供者
	 */
	public static final int NETWORK_PROVIDER_UNKNOWN=3;
	
	/**
	 * 获取本地的Mac地址
	 * @param context
	 * @return
	 */
	public static String getLocalMacAddress(Context context)
	{
		 WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);  
	     WifiInfo info = wifi.getConnectionInfo();  
	     return info.getMacAddress();  
	}
	/**
	 * 获取手机的IP地址
	 * @return
	 */
	public static String getIP()
	{
		try
		{
			Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
			while(interfaces.hasMoreElements())
			{
				NetworkInterface nextElement = interfaces.nextElement();
				Enumeration<InetAddress> inetAddresses = nextElement.getInetAddresses();
				while (inetAddresses.hasMoreElements())
				{
					InetAddress inetAddress = inetAddresses.nextElement();
					if (!inetAddress.isLoopbackAddress())
					{
						return inetAddress.getHostAddress();
					}
					
				}
			}
		} catch (SocketException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			HHLog.e(tag, "getIP", e);
		}
		
		return null;
	}
	/**
	 * 判断当前的网络是否可用<br/>
	 * 调用该方法需要添加权限：android.permission.ACCESS_NETWORK_STATE
	 * @param context
	 * @return
	 */
	public static boolean isNetWorkAvilable(Context context)
	{
		NetworkInfo netWorkInfo = getAgvilableNetWorkInfo(context);
		return netWorkInfo!=null&&netWorkInfo.isConnected();
	}
	/**
	 * 获取当前可用的网络对象,返回默认传输数据的网络
	 * @param context
	 * @return
	 */
	public static NetworkInfo getAgvilableNetWorkInfo(Context context)
	{
		ConnectivityManager manager=(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		return manager.getActiveNetworkInfo();
	}
	/**
	 * 获取当前的网络类型是wifi还是2g,3g,4g网络<br/>
	 * 未连接网络的时候也可以获取网络的类型
	 * @param context
	 * @return
	 */
	public static int getNetWorkType(Context context)
	{
	
		NetworkInfo agvilableNetWorkInfo = getAgvilableNetWorkInfo(context);
		if (agvilableNetWorkInfo!=null)
		{
			if(agvilableNetWorkInfo.getType()==ConnectivityManager.TYPE_WIFI)
			{
				return NETWORK_TYPE_WIFI;
			}
		}
		TelephonyManager manager=(TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		int networkType = manager.getNetworkType();
		switch (networkType)
		{
		case TelephonyManager.NETWORK_TYPE_GPRS:
		case TelephonyManager.NETWORK_TYPE_EDGE:
		case TelephonyManager.NETWORK_TYPE_CDMA:
		case TelephonyManager.NETWORK_TYPE_1xRTT:
			return NETWORK_TYPE_2G;
		case TelephonyManager.NETWORK_TYPE_UMTS:
		case TelephonyManager.NETWORK_TYPE_EVDO_0:
		case TelephonyManager.NETWORK_TYPE_EVDO_A:
		case TelephonyManager.NETWORK_TYPE_EHRPD:
		case TelephonyManager.NETWORK_TYPE_HSDPA:
		case TelephonyManager.NETWORK_TYPE_HSUPA:
		case TelephonyManager.NETWORK_TYPE_HSPA:
		case TelephonyManager.NETWORK_TYPE_EVDO_B:
		case TelephonyManager.NETWORK_TYPE_HSPAP:
			return NETWORK_TYPE_3G;
		case TelephonyManager.NETWORK_TYPE_LTE:
			return NETWORK_TYPE_4G;
		default:
			break;
		}
		return NETWORK_TYPE_UNKNOWN;
	}

	/**
	 * 获取网络提供者
	 * @param context
	 * @return
	 */
	public static int getNetWorkProvider(Context context)
	{
			int provider =NETWORK_PROVIDER_UNKNOWN;  
        	TelephonyManager telephonyManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);  
            String imsi = telephonyManager.getSubscriberId();  
            if (imsi == null)
            {  
                if (TelephonyManager.SIM_STATE_READY == telephonyManager.getSimState()) 
                {  
                    String operator = telephonyManager.getSimOperator();  
                    if (operator != null)
                    {  
                        if (operator.equals("46000")  || operator.equals("46002")|| operator.equals("46007"))
                        {  
                            provider =NETWORK_PROVIDER_CHINA_MOBILE; 
                        } else if (operator.equals("46001"))
                        {  
                            provider = NETWORK_PROVIDER_CHINA_UNION;  
                        } else if (operator.equals("46003")) {  
                            provider = NETWORK_PROVIDER_CHINA_TELECOMS;  
                        }  
                    }  
                }  
            } else {  
                if (imsi.startsWith("46000")||imsi.startsWith("46002")||imsi.startsWith("46007")) 
                {  
                    provider = NETWORK_PROVIDER_CHINA_MOBILE;  
                } else if (imsi.startsWith("46001"))
                {  
                    provider =NETWORK_PROVIDER_CHINA_UNION;  
                } else if (imsi.startsWith("46003"))
                {  
                    provider = NETWORK_PROVIDER_CHINA_TELECOMS;  
                }  
            }  
        return provider;  
	}
	/**
	 * 获取网络的附加信息<br/>
	 * 1：如果连接的是wifi,则显示连接的wifi的名字<br/>
	 * 2：如果连接的是手机网络，则显示网络的类型
	 * @return
	 */
	public static String getNetWrokExtraInfo(Context context)
	{
		NetworkInfo info = getAgvilableNetWorkInfo(context);
		if (info!=null)
		{
			return info.getExtraInfo();
		}
		return null;
	}
	/**
	 * 判断wifi时候连接
	 * @param context
	 * @return	true已连接;false未连接
	 */
	public static boolean isWifiConnected(Context context)
	{
		NetworkInfo netWorkInfo = getAgvilableNetWorkInfo(context);
		if(netWorkInfo!=null&&netWorkInfo.getType()==ConnectivityManager.TYPE_WIFI)
		{
			return true;
		}
		return false;
	}
	/**
	 * 判断当前的Wifi是否可用
	 * @param context
	 * @return
	 */
	public static boolean isWifiEnabled(Context context)
	{
		WifiManager manager=(WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		return manager.isWifiEnabled();
	}
	/**
	 * 获取当前Wifi的状态
	 * @param context
	 * @return	返回值是WifiManager.WIFI_STATE_XXX
	 */
	public static int getWifiState(Context context)
	{
		WifiManager manager=(WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		return manager.getWifiState();
	}

	/**
	 * 获取数据连接的状态
	 * @param context
	 * @return
	 */
	public static boolean isMobileDataEnabled(Context context)
	{
		TelephonyManager manager=(TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		int state=manager.getDataState();
		if (state==TelephonyManager.DATA_CONNECTED||state==TelephonyManager.DATA_CONNECTING)
		{
			return true;
		}
		return false;
	}
	
}
