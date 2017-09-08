package com.huahan.hhbaseutils;

import android.app.Activity;
import android.content.Context;
import android.view.Window;
import android.view.WindowManager.LayoutParams;

import java.lang.reflect.Field;
import java.util.HashMap;

public class HHScreenUtils
{
	private static final String tag=HHScreenUtils.class.getName();
	public static final String SCREEN_HEIGHT="height";
	public static final String SCREEN_WIDTH="width";
	/**
     * 获取屏幕的宽度
     * @param context 上下文对象
     * @return
     */
    public static int getScreenWidth(Context context)
    {
    	return context.getResources().getDisplayMetrics().widthPixels;
    }
    /**
     * 获取屏幕的高度
     * @param context 上下文对象
     * @return
     */
    public static int getScreenHeight(Context context)
    {
    	return context.getResources().getDisplayMetrics().heightPixels;
    }
    /**
     * 获取屏幕的高度和宽度
     * @param context 上下文对象
     * @return 返回一个HashMap<String,Integer>,获取宽度用SCREEN_WIDTH，获取高度用SCREEN_HEIGHT
     */
    public static HashMap<String, Integer> getScreenHeightAndWidth(Context context)
    {
    	HashMap<String, Integer> map=new HashMap<String, Integer>();
    	map.put(SCREEN_HEIGHT, getScreenHeight(context));
    	map.put(SCREEN_WIDTH, getScreenWidth(context));
    	return map;
    }
    /**
     * 获取状态栏的高度,如果获取失败则返回0
     * @return
     */
    public static int getStatusBarHeight(Context context)
    {
    	Class<?> c = null;  
        Object obj = null;  
        Field field = null;  
        int x = 0;  
        int statusBarHeight = 0;  
        try  
        {  
            c = Class.forName("com.android.internal.R$dimen");  
            obj = c.newInstance();  
            field = c.getField("status_bar_height");  
            x = Integer.parseInt(field.get(obj).toString());  
            statusBarHeight = context.getResources().getDimensionPixelSize(x);  
            return statusBarHeight;  
        }  
        catch (Exception e)  
        {  
        	HHLog.i(tag, "获取屏幕状态栏的高度失败:==="+e.getMessage()+"==="+e.getClass());
            e.printStackTrace();  
        }  
        return statusBarHeight;  
    }
	/**
	 * 改变Activity的背景的透明度，以显示变暗的效果
	 * @param activity 			显示分享的Activity
	 * @param alpha				Activity显示的透明度，1为不透明，显示正常的效果
	 */
	public static void setWindowDim(Activity activity, float alpha)
	{
		Window window = activity.getWindow();
		LayoutParams attributes = window.getAttributes();
		attributes.alpha = alpha;
		window.setAttributes(attributes);
	}

    
}
