package com.huahan.hhbaseutils.manager;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.huahan.hhbaseutils.R;
import com.huahan.hhbaseutils.frag.HHFragment;
import com.huahan.hhbaseutils.imp.HHTopViewManagerImp;
import com.huahan.hhbaseutils.manager.HHUiTopManager.TopMode;

public class HHFragTopManager
{
	private HHFragment mFragment;
	private View mTopView;
	private HHTopViewManagerImp mTopViewManagerImp;
	public HHFragTopManager(HHFragment fragment)
	{
		this.mFragment=fragment;
	}
	public void showTopView(TopMode mode)
	{
		switch (mode)
		{
		case DEFAULT:
			mTopViewManagerImp=new HHFragDefaulTopManager(mFragment);
			mTopView=mTopViewManagerImp.getTopView();
			break;
		case SEARCH:
			
			break;

		default:
			break;
		}
		LinearLayout baseTopLayout = mFragment.getBaseTopLayout();
		baseTopLayout.removeAllViews();
		LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, mFragment.getResources().getDimensionPixelSize(R.dimen.hh_top_height));
		baseTopLayout.addView(mTopView,layoutParams);
	}
	/**
	 * 获取可用的头部管理器
	 * @return
	 */
	public HHTopViewManagerImp getAvalibleManager()
	{
		return mTopViewManagerImp;
	}
}
