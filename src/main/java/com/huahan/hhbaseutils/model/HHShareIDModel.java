package com.huahan.hhbaseutils.model;

/**
 * 分享的各个平台的AppID
 * @author yuan
 *
 */
public class HHShareIDModel
{
	private String weixin;
	private String qq;
	private String sina;
	private String qqName;
	
	public String getQqName()
	{
		return qqName;
	}
	public void setQqName(String qqName)
	{
		this.qqName = qqName;
	}
	public String getWeixin()
	{
		return weixin;
	}
	public void setWeixin(String weixin)
	{
		this.weixin = weixin;
	}
	public String getQq()
	{
		return qq;
	}
	public void setQq(String qq)
	{
		this.qq = qq;
	}
	public String getSina()
	{
		return sina;
	}
	public void setSina(String sina)
	{
		this.sina = sina;
	}
	
}
