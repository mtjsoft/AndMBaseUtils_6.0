package com.huahan.hhbaseutils.imp;

import java.util.List;

public interface HHWindowTypeListImp
{
	/**
	 * 获取显示的名称
	 * @return
	 */
	String getTypeListName();
	/**
	 * 获取item对应的ID
	 * @return
	 */
	String getTypeListID();
	/**
	 * 获取item被选中的状态
	 * @return
	 */
	boolean getTypeListSelectState();
	/**
	 * 设置item被选中的状态
	 * @return
	 */
	boolean setTypeListSelectState(boolean selectState);
	/**
	 * 获取item下的子类别
	 * @return
	 */
	List<HHWindowTypeListImp> getTypeChildList();
	
}
