package com.huahan.hhbaseutils.imp;


/**
 * 下载进度变化的时候执行的监听器
 * @author yuan
 *
 */
public interface DownLoadListener
{
	/**
	 * 当下载进度变化的时候执行
	 * @param progress			下载进度
	 * @param downloadSize		已下载的大小
	 */
	void onSizeChangedListener(int progress,int downloadSize);
}
