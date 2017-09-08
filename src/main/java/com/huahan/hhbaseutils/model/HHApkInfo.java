package com.huahan.hhbaseutils.model;

import android.graphics.drawable.Drawable;

public class HHApkInfo
{
	private String versionName;
	private int versionCode;
	private String packageName;
	private Drawable logo;
	private String appName;
	public String getVersionName()
	{
		return versionName;
	}
	public void setVersionName(String versionName)
	{
		this.versionName = versionName;
	}
	public int getVersionCode()
	{
		return versionCode;
	}
	public void setVersionCode(int versionCode)
	{
		this.versionCode = versionCode;
	}
	public String getPackageName()
	{
		return packageName;
	}
	public void setPackageName(String packageName)
	{
		this.packageName = packageName;
	}
	public Drawable getLogo()
	{
		return logo;
	}
	public void setLogo(Drawable logo)
	{
		this.logo = logo;
	}
	public String getAppName()
	{
		return appName;
	}
	public void setAppName(String appName)
	{
		this.appName = appName;
	}
	
}
