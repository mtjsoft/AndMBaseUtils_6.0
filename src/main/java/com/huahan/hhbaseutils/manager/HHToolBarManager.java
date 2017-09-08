package com.huahan.hhbaseutils.manager;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.huahan.hhbaseutils.HHViewHelper;
import com.huahan.hhbaseutils.R;
import com.huahan.hhbaseutils.manager.HHUiTopManager.TitleMode;
import com.huahan.hhbaseutils.ui.HHActivity;

public class HHToolBarManager
{
	private HHActivity mActivity;
	private Toolbar mToolbar;
	private TextView mTitleTextView;
	public HHToolBarManager(HHActivity activity)
	{
		this.mActivity=activity;
		addDefaultView();
	}
	
	private void addDefaultView()
	{
		mToolbar=(Toolbar) View.inflate(mActivity, R.layout.hh_item_base_toolbar, null);
		mTitleTextView=HHViewHelper.getViewByID(mToolbar, R.id.hh_id_top_title);
		mTitleTextView.setTextSize(HHUiTopManager.mTopViewInfo.titleSize);
		mTitleTextView.setTextColor(HHUiTopManager.mTopViewInfo.titleTextColor);
		mToolbar.setTitleTextColor(HHUiTopManager.mTopViewInfo.titleTextColor);
		mActivity.setSupportActionBar(mToolbar);
		LinearLayout.LayoutParams layoutParams=new LayoutParams(LayoutParams.MATCH_PARENT, mActivity.getResources().getDimensionPixelSize(R.dimen.hh_top_height));
		mActivity.getBaseTopLayout().addView(mToolbar, layoutParams);
		if (HHUiTopManager.mTopViewInfo.backLeftDrawable!=0)
		{
			mToolbar.setNavigationIcon(HHUiTopManager.mTopViewInfo.backLeftDrawable);
		}
		mActivity.getSupportActionBar().setHomeButtonEnabled(true);
		mToolbar.inflateMenu(R.menu.hh_top_menu);
	}
	public void setBackIcon(int resID)
	{
		mToolbar.setNavigationIcon(resID);
	}
	public void setBackIcon(Drawable drawable)
	{
		mToolbar.setNavigationIcon(drawable);
	}
	public void setTitle(TitleMode mode,String title)
	{
		switch (mode)
		{
		case LEFT:
			mToolbar.setTitle(title);
			mTitleTextView.setText("");
			break;
		case CENTER:
			mTitleTextView.setText(title);
			mToolbar.setTitle("");
			break;

		default:
			break;
		}
		mActivity.setSupportActionBar(mToolbar);
	}
	public void setTitle(String title)
	{
		if (HHUiTopManager.mTopViewInfo.titleMode==TitleMode.LEFT)
		{
			mToolbar.setTitle(title);
			mTitleTextView.setText("");
		}else {
			mTitleTextView.setText(title);
			mToolbar.setTitle("");
		}
		mActivity.setSupportActionBar(mToolbar);
	}
	public void addView()
	{
		Menu menu = mToolbar.getMenu();
		MenuItem add = menu.add("");
		add.setIcon(R.drawable.ic_launcher);
		Log.i("chenyuan", "menu是"+menu);
	}
	
}
