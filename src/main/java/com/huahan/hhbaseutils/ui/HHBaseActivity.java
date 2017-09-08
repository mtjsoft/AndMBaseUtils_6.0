package com.huahan.hhbaseutils.ui;

import android.os.Bundle;
import android.view.View;


public abstract class HHBaseActivity extends HHActivity
{
	@Override
	protected void onCreate(Bundle savedInstanceBundle)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceBundle);
		View initView = initView();
		setBaseView(initView);
		initValues();
		initListeners();
	}
}
