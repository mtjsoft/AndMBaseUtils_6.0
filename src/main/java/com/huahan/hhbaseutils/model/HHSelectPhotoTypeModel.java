package com.huahan.hhbaseutils.model;

import java.util.List;

public class HHSelectPhotoTypeModel
{
	private String dirName;
	private List<HHSystemPhotoModel> photoList;
	private boolean isChecked=false;
	public String getDirName()
	{
		return dirName;
	}
	public void setDirName(String dirName)
	{
		this.dirName = dirName;
	}
	public List<HHSystemPhotoModel> getPhotoList()
	{
		return photoList;
	}
	public void setPhotoList(List<HHSystemPhotoModel> photoList)
	{
		this.photoList = photoList;
	}
	public boolean isChecked()
	{
		return isChecked;
	}
	public void setChecked(boolean isChecked)
	{
		this.isChecked = isChecked;
	}
	public HHSelectPhotoTypeModel(String dirName, List<HHSystemPhotoModel> photoList)
	{
		super();
		this.dirName = dirName;
		this.photoList = photoList;
	}
	public HHSelectPhotoTypeModel()
	{
		super();
	}
	
	
	
}