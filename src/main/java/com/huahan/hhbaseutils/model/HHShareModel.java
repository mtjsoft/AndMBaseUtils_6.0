package com.huahan.hhbaseutils.model;

import android.graphics.Bitmap;

/**
 * 分享的内容
 * @author yuan
 *
 */
public class HHShareModel
{
	private String title;
	private String description;
	private String linkUrl;
	private Bitmap thumpBitmap;
	private String imageUrl;
	private int qqShareType=0;//0分享到qq;1分享到空间
	private int wxShareType=0;//0默认网页；1分享图片
	private int sinaShareType=0;//0默认网页；1分享图片
	
	
	public int getWxShareType() {
		return wxShareType;
	}
	public void setWxShareType(int wxShareType) {
		this.wxShareType = wxShareType;
	}
	public int getSinaShareType() {
		return sinaShareType;
	}
	public void setSinaShareType(int sinaShareType) {
		this.sinaShareType = sinaShareType;
	}
	public int getQqShareType() {
		return qqShareType;
	}
	public void setQqShareType(int qqShareType) {
		this.qqShareType = qqShareType;
	}
	public String getImageUrl()
	{
		return imageUrl;
	}
	public void setImageUrl(String imageUrl)
	{
		this.imageUrl = imageUrl;
	}
	public String getTitle()
	{
		return title;
	}
	public void setTitle(String title)
	{
		this.title = title;
	}
	public String getDescription()
	{
		return description;
	}
	public void setDescription(String description)
	{
		this.description = description;
	}
	public String getLinkUrl()
	{
		return linkUrl;
	}
	public void setLinkUrl(String linkUrl)
	{
		this.linkUrl = linkUrl;
	}
	public Bitmap getThumpBitmap()
	{
		return thumpBitmap;
	}
	public void setThumpBitmap(Bitmap thumpBitmap)
	{
		this.thumpBitmap = thumpBitmap;
	}
	
}
