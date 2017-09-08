package com.huahan.hhbaseutils.imp;

import android.graphics.Bitmap;
/**
 * 加载图片的监听器,当直接加载内存中的图片的时候是不会调用onSizeChangedListener的
 * @author yuan
 *
 */
public interface LoadImageListener extends DownLoadListener
{
	/**
	 * 当获取到Bitmap时执行的方法
	 * @param bitmap		获取到的Bitmap
	 */
	void onGetBitmap(Bitmap bitmap);
	
}
