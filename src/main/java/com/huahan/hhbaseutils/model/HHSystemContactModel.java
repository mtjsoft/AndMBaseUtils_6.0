package com.huahan.hhbaseutils.model;

/**
 * 保存系统联系人的基本信息
 * @author yuan
 *
 */
public class HHSystemContactModel
{
	private String id;
	private String name;
	private String phoneNumber;
	public String getId()
	{
		return id;
	}
	public void setId(String id)
	{
		this.id = id;
	}
	public String getName()
	{
		return name;
	}
	public void setName(String name)
	{
		this.name = name;
	}
	public String getPhoneNumber()
	{
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber)
	{
		this.phoneNumber = phoneNumber;
	}
	
}
