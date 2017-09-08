package com.huahan.hhbaseutils.model;

import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;

import com.huahan.hhbaseutils.HHLog;

import java.lang.ref.WeakReference;

/**
 * 该类继承自Handler，在内部使用了一个WeakReference来引用Activity或者Fragment，
 * 通过对对象的若引用来防止因为多线程的原因引起的内存泄漏问题
 * @author yuan
 *
 * @param <T>
 */
public abstract class HHWeakHandler<T> extends Handler
{
	private static final String tag=HHWeakHandler.class.getSimpleName();
	/**
	 * 保存了T的一个弱引用，防止因为多线的原因造成内存泄漏
	 */
	private WeakReference<T> mFragmentReference;
	public HHWeakHandler(T t)
	{
		super();
		this.mFragmentReference = new WeakReference<T>(t);
	}
	@Override
	public void handleMessage(Message msg)
	{
		super.handleMessage(msg);
		HHLog.i(tag, "reference is:"+mFragmentReference.get());
		if (mFragmentReference.get()==null)
		{
			return ;
		}
		if (mFragmentReference.get() instanceof Fragment)
		{
			Fragment fragment = (Fragment) mFragmentReference.get();
			if (fragment.getContext()==null)
			{
				return ;
			}
		}
		processHandlerMessage(msg);
		
	}
	/**
	 * 处理消息,在该方法执行之前首先是判断了Fragment是否被销毁，如果fragment被销毁了，
	 * 则不会执行该方法，直接退出
	 * @param msg			发送的消息
	 */
	public abstract void processHandlerMessage(Message msg);
}
