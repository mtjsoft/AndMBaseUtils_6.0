package com.huahan.hhbaseutils.frag;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.huahan.hhbaseutils.imp.HHLoadViewImp;
import com.huahan.hhbaseutils.manager.HHLoadViewManager;
import com.huahan.hhbaseutils.model.HHLoadState;

public abstract class HHBaseDataFragment extends HHFragment implements HHLoadViewImp
{

	private HHLoadViewManager mLoadViewManager;
	@Override
	@SuppressLint("InflateParams")
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view=super.onCreateView(inflater, container, savedInstanceState);
		mLoadViewManager=new HHLoadViewManager(this, this);
		if(!initOnCreate())
		{
			changeLoadState(HHLoadState.LOADING);
		}
		return view;
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
