package com.huahan.hhbaseutils.manager;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.huahan.hhbaseutils.R;
import com.huahan.hhbaseutils.imp.HHTopViewManagerImp;
import com.huahan.hhbaseutils.model.HHTopViewInfo;
import com.huahan.hhbaseutils.ui.HHActivity;

/**
 * 一个管理类，这个管理类管理者用户界面头部的显示的样式,默认使用的是默认的显示的样式<br/>
 * 头部的显示现在有两种样式<br/>
 * 1:显示的是默认的显示样式，使用HHDefaultTopViewManager类管理显示的内容<br/>
 * 2：显示的是搜索的样式，使用HHSearchTopViewManager类管理显示的内容<br/>
 * 3:getAvalibleManager方法返回当前可用的管理器，需要强制转换成特定的管理器
 * @author yuan
 *
 */
public final class HHUiTopManager
{
	public static final HHTopViewInfo mTopViewInfo=new HHTopViewInfo();
	//表示当前显示的是那个Activity
	private HHActivity mActivity;
	//是否使用ActionBar
	private boolean mUseActionBar=false;
	//头部显示的View
	private View mTopView;
	private HHTopViewManagerImp mAvalibleManagerImp;
	private HHToolBarManager mToolBarManager;
	/**
	 * 实例化HHUiTopManager，默认是不适用ActionBar的
	 * @param activity
	 */
	public HHUiTopManager(HHActivity activity)
	{
		this(activity, false);
	}
	/**
	 * 定义的
	 * @author yuan
	 *
	 */
	public enum TitleMode
	{
		LEFT,
		CENTER
	}
	/**
	 * 头部显示的样式
	 * @author yuan
	 *
	 */
	public enum TopMode
	{
		/**
		 * 默认的样式
		 */
		DEFAULT,
		/**
		 * 搜索的样式
		 */
		SEARCH
	}
	/**
	 * 实例化HHUiTopManager
	 * @param activity
	 * @param useActionBar		是否使用ActionBar
	 */
	public HHUiTopManager(HHActivity activity,boolean useActionBar)
	{
		this.mActivity=activity;
		this.mUseActionBar=useActionBar;
		init();
	}
	/**
	 * 执行初始化操作
	 */
	private void init()
	{
		if (HHUiTopManager.mTopViewInfo.useToolbar||mUseActionBar)
		{
			mToolBarManager=new HHToolBarManager(mActivity);
		}
	}
	/**
	 * 设置是否使用ToolBar
	 * @param isUse
	 */
	public void setUseToolBar(boolean isUse)
	{
		this.mUseActionBar=isUse;
		if (mUseActionBar&&mToolBarManager==null)
		{
			mToolBarManager=new HHToolBarManager(mActivity);
		}
	}
	
	public void showTopView(TopMode mode)
	{
		switch (mode)
		{
		case DEFAULT:
			mAvalibleManagerImp=new HHDefaultTopViewManager(mActivity);
			mTopView=mAvalibleManagerImp.getTopView();
			break;
		case SEARCH:
			break;

		default:
			break;
		}
		LinearLayout baseTopLayout = mActivity.getBaseTopLayout();
		baseTopLayout.removeAllViews();
		LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, mActivity.getResources().getDimensionPixelSize(R.dimen.hh_top_height));
		baseTopLayout.addView(mTopView,layoutParams);
	}
	public HHTopViewManagerImp getAvalibleManager()
	{
		return mAvalibleManagerImp;
	}
	public HHToolBarManager getToolBarManager()
	{
		return mToolBarManager;
	}
	
	
}
