package com.huahan.hhbaseutils.imp;

import android.app.Dialog;

/**
 * 版本更新中item被点击的时候执行的监听器方法
 * @author yuan
 *
 */
public interface OnVersionItemClickedListener
{
	/**
	 * 当版本更新中的按钮被点击的时候执行的监听器
	 * @param isSure				被点击的是不是确定按钮
	 * @param dialog				当前显示的对话框
	 */
	void onVersionItemClick(boolean isSure,Dialog dialog);
}
