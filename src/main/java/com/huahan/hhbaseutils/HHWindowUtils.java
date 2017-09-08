package com.huahan.hhbaseutils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.huahan.hhbaseutils.imp.HHWindowTypeListImp;

import java.util.List;

public class HHWindowUtils
{
	public static enum TypeListMode{SINGAL,GROUP}
	/**
	 * 纪录显示的数据
	 */
	private List<? extends HHWindowTypeListImp> mList;
	/**
	 * 表示的是父列表正常和选中或者点击的时候显示的背景颜色或者图片
	 */
	private int[] mParentStateColor;
	private int[] mChildStateColor;
	private PopupWindow mWindow;
	
	public HHWindowUtils()
	{
		
	}
	public HHWindowUtils data(List<? extends HHWindowTypeListImp> dataList)
	{
		this.mList=dataList;
		return this;
	}
//	public void 
	
	
	public PopupWindow build(Context context,TypeListMode mode)
	{
		mWindow=new PopupWindow(context);
		View contentView=View.inflate(context, R.layout.hh_window_typelist_double, null);
		ListView parentListView=HHViewHelper.getViewByID(contentView, R.id.hh_lv_window_type_1);
		ListView childListView=HHViewHelper.getViewByID(contentView, R.id.hh_lv_window_type_2);
		
		mWindow.setContentView(contentView);
		mWindow.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
		mWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
		mWindow.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#aa000000")));
		mWindow.setOutsideTouchable(true);
		mWindow.setFocusable(true);
		mWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
//		window.setAnimationStyle(animationStyle)
		return mWindow;
	}
	
	
}
