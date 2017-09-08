package com.huahan.hhbaseutils.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public abstract class HHPagerAdapter<T> extends PagerAdapter
{
	private List<T> mList;
	private Context mContext;
	
	public HHPagerAdapter(List<T> list, Context context)
	{
		super();
		this.mList = list;
		this.mContext = context;
	}
	@Override
	public int getCount()
	{
		return mList==null?0:mList.size();
	}
	@Override
	public boolean isViewFromObject(View arg0, Object arg1)
	{
		return arg0==arg1;
	}
	@Override
	public void destroyItem(ViewGroup container, int position, Object object)
	{
		container.removeView((View) object);
	}
	
	@Override
	public Object instantiateItem(ViewGroup container, int position)
	{
		T model=mList.get(position);
		return instantiateItem(container,position,model);
	}
	/**
	 * 实例化一个对象
	 * @param container			容器
	 * @param position			位置
	 * @param model				该位置对应的model
	 * @return
	 */
	public abstract Object instantiateItem(ViewGroup container,int position,T model);
	
	protected Context getContext()
	{
		return mContext;
	}
}
