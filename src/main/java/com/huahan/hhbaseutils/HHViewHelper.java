package com.huahan.hhbaseutils;

import android.app.Activity;
import android.view.View;

public class HHViewHelper
{
	@SuppressWarnings("unchecked")
	public static <T> T getViewByID(View parent,int id)
	{
		return (T)parent.findViewById(id);
	}
	@SuppressWarnings("unchecked")
	public static <T> T getViewByID(Activity activity,int id)
	{
		return (T)activity.findViewById(id);
	}
	
}
