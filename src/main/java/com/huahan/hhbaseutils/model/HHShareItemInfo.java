package com.huahan.hhbaseutils.model;

/**
 * 分享的时候显示的window上显示的分享的每一个平台的信息，主要包括显示的图标
 * 和现实的文字，另外还有一个显示的顺序
 * @author yuan
 *
 */
public class HHShareItemInfo implements Comparable<HHShareItemInfo>
{
	private int drawableID;
	private int nameID;
	private int order;
	private int id;
	
	public int getId()
	{
		return id;
	}
	public void setId(int id)
	{
		this.id = id;
	}
	public int getDrawableID()
	{
		return drawableID;
	}
	public void setDrawableID(int drawableID)
	{
		this.drawableID = drawableID;
	}
	public int getNameID()
	{
		return nameID;
	}
	public void setNameID(int nameID)
	{
		this.nameID = nameID;
	}
	public int getOrder()
	{
		return order;
	}
	public void setOrder(int order)
	{
		this.order = order;
	}
	
	public HHShareItemInfo()
	{
		super();
	}
	/**
	 * 构造函数
	 * @param drawableID			显示的图标
	 * @param nameID				显示的文字
	 * @param order					显示的顺序
	 * @param id					平台的ID
	 */
	public HHShareItemInfo(int drawableID, int nameID, int order, int id)
	{
		super();
		this.drawableID = drawableID;
		this.nameID = nameID;
		this.order = order;
		this.id = id;
	}
	@Override
	public int compareTo(HHShareItemInfo another)
	{
		// TODO Auto-generated method stub
		return (order-another.getOrder());
	}
	
}
