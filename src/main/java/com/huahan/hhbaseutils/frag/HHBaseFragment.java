package com.huahan.hhbaseutils.frag;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class HHBaseFragment extends HHFragment
{
	@Override
	@SuppressLint("InflateParams")
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		View view=super.onCreateView(inflater, container, savedInstanceState);
		View initView = initView();
		setBaseView(initView);
		initValues();
		initListeners();
		return view;
	}
}
