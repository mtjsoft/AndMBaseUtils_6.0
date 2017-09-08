package com.huahan.hhbaseutils.model;

/**
 * 加载页面显示的时候显示的信息，需要配置显示的文字和现实的图片
 * @author yuan
 *
 */
public class HHLoadViewInfo
{
	private String mMsgInfo;
	private int mDrawableID=0;
	public String getMsgInfo()
	{
		return mMsgInfo;
	}
	public void setMsgInfo(String mMsgInfo)
	{
		this.mMsgInfo = mMsgInfo;
	}
	public int getDrawableID()
	{
		return mDrawableID;
	}
	public void setDrawableID(int mDrawableID)
	{
		this.mDrawableID = mDrawableID;
	}
	public HHLoadViewInfo(String msgInfo, int drawableID)
	{
		super();
		this.mMsgInfo = msgInfo;
		this.mDrawableID = drawableID;
	}
	
}
