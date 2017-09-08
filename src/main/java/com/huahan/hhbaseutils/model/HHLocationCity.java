package com.huahan.hhbaseutils.model;

import com.huahan.hhbaseutils.imp.CityInfoImp;

/**
 * 定位结果的类
 * @author yuan
 *
 */
@SuppressWarnings("serial")
public class HHLocationCity implements CityInfoImp
{
	private String cityId;
	private String cityName;
	private double la;
	private double lo;
	private String info;
	
	
	public String getInfo() {
		return info;
	}
	public void setInfo(String info) {
		this.info = info;
	}
	public String getCityId()
	{
		return cityId;
	}
	public void setCityId(String cityId)
	{
		this.cityId = cityId;
	}
	public double getLa()
	{
		return la;
	}
	public void setLa(double la)
	{
		this.la = la;
	}
	public double getLo()
	{
		return lo;
	}
	public void setLo(double lo)
	{
		this.lo = lo;
	}
	@Override
	public String getCityName()
	{
		// TODO Auto-generated method stub
		return cityName;
	}
	public void setCityName(String cityName)
	{
		this.cityName=cityName;
	}
	@Override
	public String getCityIndex()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getCityID()
	{
		// TODO Auto-generated method stub
		return cityId;
	}
	@Override
	public int compareTo(CityInfoImp another)
	{
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public String getLocationLa()
	{
		// TODO Auto-generated method stub
		return la+"";
	}
	@Override
	public String getLocationLo()
	{
		// TODO Auto-generated method stub
		return lo+"";
	}
	@Override
	public String getLocationInfo() {
		// TODO Auto-generated method stub
		return info;
	}

}
