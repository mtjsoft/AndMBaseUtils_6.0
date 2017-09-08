package com.huahan.hhbaseutils.imp;


/**
 * 接口，包含了获取小图和获取大图的方法,该接口主要用于在HHImageBrowerActivity中显示图片使用
 * @author yuan
 *
 */
public interface HHSmallBigImageImp extends HHImageViewPagerImp
{
	
	/**
	 * 获取大图
	 * @return
	 */
	String getBigImage();
}
