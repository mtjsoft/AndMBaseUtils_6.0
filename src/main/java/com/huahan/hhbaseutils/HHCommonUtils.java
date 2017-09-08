package com.huahan.hhbaseutils;

import android.app.Activity;
import android.app.Application;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.text.TextUtils;
import android.view.View;

import com.huahan.hhbaseutils.ui.HHApplication;

import java.net.URLDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HHCommonUtils
{
	/**
	 * 是否精确匹配
	 * @param mPattern
	 * @param content
	 * @return
	 */
	public static boolean isPattern(String mPattern,String content)
	{
		Pattern pattern = Pattern.compile(mPattern,
				Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(content);
		if (matcher.find()) {
			return true;
		}
		
		return false;
	}
	/**
	 * 判断是否包括某一字符串
	 * 
	 * @param content
	 * @param keyWord
	 * @return
	 */
	public static boolean isInclude(String content, String keyWord) {
		// 返回指定字符在此字符串中第一次出现处的索引，没有的话返回-1
		if (content.indexOf(keyWord) != -1) {
			return true;
		}
		return false;
	}
	/**
	 * 判断一个地址是不是网络地址
	 * 
	 * @param url
	 * @return
	 */
	public static boolean isHttpUrl(String url) {
		if (!TextUtils.isEmpty(url)) {
			if (url.startsWith("http://") || url.startsWith("www.")
					|| url.startsWith("https://")) {
				return true;
			}
		}
		return false;
	}
	/**O
	 * 对字符串进行url编码，使用的是utf-8de字符集
	 * @param decode
	 * @return
	 */
	public static String urlDecode(String decode)
	{
		if (!TextUtils.isEmpty(decode))
		{
			try
			{
				decode=URLDecoder.decode(decode, "utf-8");
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		return decode;
	}
	/**
	 * 渲染一个View的背景图片为app的主色调
	 * @param activity		
	 * @param view			需要渲染的View
	 * @return				true：已经渲染了View的背景
	 */
	@SuppressWarnings("deprecation")
	public static boolean tintViewBackground(Activity activity,View view)
	{
		Application application = activity.getApplication();
		if(application instanceof HHApplication)
		{
			int[][] colorState=new int[1][];
			colorState[0]=new int[]{};
			int[] color=new int[1];
			color[0]=((HHApplication)application).getHHApplicationInfo().getMainColor();
			ColorStateList colorStateList=new ColorStateList(colorState, color);
			Drawable drawable = view.getBackground();
			if (drawable!=null)
			{
				
				DrawableCompat.setTintList(drawable, colorStateList);
				DrawableCompat.setTintMode(drawable, Mode.SRC_IN);
				view.setBackgroundDrawable(drawable);
				return true;
			}
			
		}
		return false;
	}
}
