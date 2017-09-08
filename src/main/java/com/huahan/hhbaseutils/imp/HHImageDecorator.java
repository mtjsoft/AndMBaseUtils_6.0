package com.huahan.hhbaseutils.imp;

import android.graphics.Bitmap;

public interface HHImageDecorator
{
	/**
	 * 修饰bitmap以获取不同样式的Bitmap
	 * @param bitmap
	 * @return
	 */
	Bitmap decorateImage(Bitmap bitmap);
}
