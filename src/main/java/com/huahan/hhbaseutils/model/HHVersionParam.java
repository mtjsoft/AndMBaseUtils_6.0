package com.huahan.hhbaseutils.model;

import android.content.Context;

import com.huahan.hhbaseutils.imp.OnVersionItemClickedListener;

import java.lang.ref.WeakReference;
import java.util.HashMap;

/**
 * 版本更新的工具类的参数信息
 * @author yuan
 *
 */
public class HHVersionParam
{
	/**
	 * 显示加载的对话框等信息，默认是显示的
	 */
	public boolean showLoadding=true;
	/**
	 * 版本更新的界面中当Item被点击的时候执行的监听器方法
	 */
	public OnVersionItemClickedListener listener=null;
	/**
	 * 保存着宿主页面的若引用
	 */
	public WeakReference<Context> context;
	/**
	 * 传递的参数信息
	 */
	public HashMap<String, String> paramMap=new HashMap<String, String>();
	public HHVersionConvertImp convertFactory=null;
	
	
}
