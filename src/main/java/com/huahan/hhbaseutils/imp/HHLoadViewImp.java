package com.huahan.hhbaseutils.imp;

import com.huahan.hhbaseutils.manager.HHLoadViewManager;
import com.huahan.hhbaseutils.model.HHLoadState;


public interface HHLoadViewImp
{
	/**
	 * 调用页面加载的方法
	 */
	void onPageLoad();
	/**
	 * 1：用于在OnCreateView中完成一些需要在Fragment创建的 时候完成的操作<br/>
	 * 2：在Activity的OnCreate方法中调用，由于initView，initValue，initListener方法不在oncreate方法中执行，但是有些控件的实例化和初始化
	 * ，添加监听器方法必须在oncreate中执行，所以添加该方法用于在oncreate方法中实现这些功能，该方法在initOther中调用<br/>
	 * 3:返回false则执行后续的方法，true不执行。一般情况下该方法后执行的是显示成加载状态
	 */
	boolean initOnCreate();
	/**
	 * 改变加载状态
	 */
	public void changeLoadState(HHLoadState state);
	/**
	 * 获取加载页面的管理器
	 */
	public HHLoadViewManager getLoadViewManager();
}
