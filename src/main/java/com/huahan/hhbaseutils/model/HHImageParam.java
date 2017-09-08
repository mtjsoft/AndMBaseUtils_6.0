package com.huahan.hhbaseutils.model;

import com.huahan.hhbaseutils.imp.HHImageDecorator;
import com.huahan.hhbaseutils.imp.LoadImageListener;

public class HHImageParam
{
	/**
	 * 显示的图片的路径
	 */
	public String filePath=null;
	/**
	 * 图片的保存路径，默认为null，此时在工具类内部默认加载到默认的位置
	 */
	public String savePath=null;
	/**
	 * 期望的图片的宽度，默认为0
	 */
	public int widthDes=0;
	/**
	 * 期望的图片的高度，默认为0
	 */
	public int heightDes=0;
	/**
	 * 是否缓存到内存，默认是true
	 */
	public boolean cacheToMemory=true;
	/**
	 * 是否使用本地文件，默认是true
	 */
	public boolean isUseLocalFile=true;
	/**
	 * 图片显示出来的时候显示的动画效果，暂时没有效果，传0
	 */
	public int showAnim=0;
	/**
	 * 图片显示的特效，暂时没有效果，传0
	 */
	public int imageEffect=0;
	/**
	 * 加载图片的监听器，默认为null
	 */
	public LoadImageListener listener=null;
	/**
	 * 不是Wifi的时候是否加载图片，默认在不是Wifi的情况下不加载图片
	 */
	public boolean loadImageNotWifi=false;
	/**
	 * 占位图片的ID，加载图片的时候显示的默认的图片，默认为0，此时不显示默认图片
	 */
	public int defaultImageID=0;
	/**
	 * 图片的修饰器
	 */
	public HHImageDecorator imageDecorator;
	public HHImageParam(String filePath,String savePath, int widthDes, int heightDes, boolean cacheToMemory, boolean isUseLocalFile, int showAnim, int imageEffect, LoadImageListener listener)
	{
		super();
		this.filePath = filePath;
		this.savePath=savePath;
		this.widthDes = widthDes;
		this.heightDes = heightDes;
		this.cacheToMemory = cacheToMemory;
		this.isUseLocalFile = isUseLocalFile;
		this.showAnim = showAnim;
		this.imageEffect = imageEffect;
		this.listener = listener;
	}
	public HHImageParam()
	{
		super();
	}

}
