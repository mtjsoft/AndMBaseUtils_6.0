package com.huahan.hhbaseutils.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * 重写ListView，实现listview的高度为所有子Item的高度的总和
 * @author yuan
 *
 */
public class HHAtMostListView extends ListView
{
	public HHAtMostListView(Context context, AttributeSet attrs, int defStyle) 
	{
		super(context, attrs, defStyle);
	}
	public HHAtMostListView(Context context, AttributeSet attrs) 
	{
		super(context, attrs);
	}
	public HHAtMostListView(Context context) 
	{
		super(context);
	}
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		int expandSpec=MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE>>2, MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}
}
