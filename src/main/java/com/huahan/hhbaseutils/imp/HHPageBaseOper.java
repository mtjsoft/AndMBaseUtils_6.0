package com.huahan.hhbaseutils.imp;

import android.content.Context;
import android.os.Message;
import android.view.View;
import android.widget.FrameLayout;

/**
 * 定义了页面的基本操作，主要实现在Activity和Fragment
 * @author yuan
 *
 */
public interface HHPageBaseOper
{
	/**
	 * 加载页面显示的布局
	 * @return
	 */
	View initView();
	/**
	 * 对页面的基本控件添加基本的值
	 */
	void initValues();
	/**
	 * 给控件添加监听器
	 */
	void initListeners();
	/**
	 * 设置页面显示的基本布局
	 * @param view
	 */
	void setBaseView(View view);
	
	/**
	 * 后去页面的Context对象
	 * @return
	 */
	Context getPageContext();
	/**
	 * 获取当前页面显示的视图
	 */
	View getBaseView();
	/**
	 * 获取当前页面显示内容的Layout
	 * @return
	 */
	FrameLayout getBaseContainerLayout();
	/**
	 * 把视图添加到内容的容器当中
	 * @param view
	 */
	void addViewToContainer(View view);
	/**
	 * 处理handler消息
	 * @param msg
	 */
	void processHandlerMsg(Message msg);
}	
