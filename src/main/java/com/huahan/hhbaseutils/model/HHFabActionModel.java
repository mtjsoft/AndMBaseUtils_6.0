package com.huahan.hhbaseutils.model;

/**
 * ImageBrowerActivity中显示操作的Model
 * @author yuan
 *
 */
public class HHFabActionModel
{
	private String title;
	private int imageID;
	public String getTitle()
	{
		return title;
	}
	public void setTitle(String title)
	{
		this.title = title;
	}
	public int getImageID()
	{
		return imageID;
	}
	public void setImageID(int imageID)
	{
		this.imageID = imageID;
	}
	public HHFabActionModel(String title, int imageID)
	{
		super();
		this.title = title;
		this.imageID = imageID;
	}
	public HHFabActionModel()
	{
		super();
	}
	
	
}
