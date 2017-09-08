package com.huahan.hhbaseutils.model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class HHSystemPhotoModel implements Serializable
{
	private String dirName;
	private int size;
	private String displayName;
	private int orientation;
	private long addDate;
	private String filePath;
	/**
	 * 新添加的字段，不在数据库中，表示的是图片是否被选择了
	 */
	private boolean isSelect=false;
	
	public boolean isSelect()
	{
		return isSelect;
	}
	public void setSelect(boolean isSelect)
	{
		this.isSelect = isSelect;
	}
	public String getDirName()
	{
		return dirName;
	}
	public void setDirName(String dirName)
	{
		this.dirName = dirName;
	}
	public int getSize()
	{
		return size;
	}
	public void setSize(int size)
	{
		this.size = size;
	}
	public String getDisplayName()
	{
		return displayName;
	}
	public void setDisplayName(String displayName)
	{
		this.displayName = displayName;
	}
	public int getOrientation()
	{
		return orientation;
	}
	public void setOrientation(int orientation)
	{
		this.orientation = orientation;
	}
	public long getAddDate()
	{
		return addDate;
	}
	public void setAddDate(long addDate)
	{
		this.addDate = addDate;
	}
	public String getFilePath()
	{
		return filePath;
	}
	public void setFilePath(String filePath)
	{
		this.filePath = filePath;
	}
	@Override
	public String toString()
	{
		return "SystemPhotoModel [dirName=" + dirName + ", size=" + size
				+ ", displayName=" + displayName + ", orientation="
				+ orientation + ", addDate=" + addDate + ", filePath="
				+ filePath + "]";
	}
	
	
}
