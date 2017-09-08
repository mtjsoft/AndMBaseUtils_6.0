package com.huahan.hhbaseutils.ui;

import com.huahan.hhbaseutils.imp.HHLoadViewImp;
import com.huahan.hhbaseutils.manager.HHLoadViewManager;
import com.huahan.hhbaseutils.model.HHLoadState;

/**
 * 该Activity的使用和以前的用法有区别，请仔细阅读文档。<br/>
 * 1:initView,initValue,initListener方法不在OnCreate方法中调用，这些方法的调用实在第一次调用
 * changeLoadState方法的时候调用的（状态为获取数据成功的时候）,所以需要自己把不相关的，需要在oncreate方法中执行
 * 的代码添加到initOnCreate方法中，以免出现异常<br/>
 * 2：以前使用的方法onFirstLoad...方法废除，统一使用changeLoadState方法，具体参数参看文档<br/>
 * 
 * @author yuan
 * 
 */
public abstract class HHBaseDataActivity extends HHActivity implements HHLoadViewImp
{

	
	private HHLoadViewManager mLoadViewManager;	
	@Override
	protected void initOther()
	{
		// TODO Auto-generated method stub
		super.initOther();
		mLoadViewManager=new HHLoadViewManager(this, this);
		if(!initOnCreate())
		{
			changeLoadState(HHLoadState.LOADING);
		}
	}
	@Override
	public void changeLoadState(HHLoadState state)
	{
		mLoadViewManager.changeLoadState(state);
	}
	@Override
	public HHLoadViewManager getLoadViewManager()
	{
		return mLoadViewManager;
	}
	
}
