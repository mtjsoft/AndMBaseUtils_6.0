package com.huahan.utils.view.scaleimage;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class ScaleViewPager extends ViewPager
{

	public ScaleViewPager(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	public ScaleViewPager(Context context)
	{
		super(context);
		// TODO Auto-generated constructor stub
	}
	@Override
	public boolean onInterceptTouchEvent(MotionEvent arg0)
	{
		// TODO Auto-generated method stub
		try
		{
			return super.onInterceptTouchEvent(arg0);
		} catch (Exception e)
		{
			return false;
		}
		
	}

}
