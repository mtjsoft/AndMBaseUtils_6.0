package com.huahan.hhbaseutils.imp;

import java.io.Serializable;

/**
 * 城市信息，共HHSelectCity页面使用,使用这个选择城市界面定义的Model必须实现这个接口
 * @author yuan
 *
 */
public interface CityInfoImp extends Serializable,Comparable<CityInfoImp>
{
	/**
	 * 获取城市街道信息
	 * @return
	 */
	public String getLocationInfo();
	/**
	 * 获取城市的经度
	 * @return
	 */
	public String getLocationLa();
	/**
	 * 获取城市的纬度
	 * @return
	 */
	public String getLocationLo();
	/**
	 * 获取城市的名称
	 * @return
	 */
	public String getCityName();
	/**
	 * 获取城市的索引
	 * @return
	 */
	public String getCityIndex();
	/**
	 * 获取城市的ID
	 * @return
	 */
	public String getCityID();
}
