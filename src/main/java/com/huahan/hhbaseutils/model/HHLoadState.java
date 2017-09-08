package com.huahan.hhbaseutils.model;
/**
 * 定义当前页面加载数据的状态<br/>
 * 
 * @author yuan
 * 
 */
public enum HHLoadState
{
	/**
	 * 正在加载
	 */
	LOADING,
	/**
	 * 加载失败
	 */
	FAILED,
	/**
	 * 没有数据
	 */
	NODATA,
	/**
	 * 加载成功
	 */
	SUCCESS,
}
