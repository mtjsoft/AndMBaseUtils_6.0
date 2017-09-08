package com.huahan.hhbaseutils.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.huahan.hhbaseutils.HHImageUtils.Builder;
import com.huahan.hhbaseutils.R;
import com.huahan.hhbaseutils.imp.HHImageViewPagerImp;

import java.util.List;

public class HHAutoRecycleViewPagerAdapter extends PagerAdapter
{

	private List<? extends HHImageViewPagerImp> mList;
	private Context mContext;
	private int mDefaultImageID=0;
	private OnItemClickListener mListener;
	
	/**
	 * 获取Adapter 的实例
	 * @param list
	 * @param context
	 * @param defaultImageID		默认显示的图片，如果小于1，则使用框架默认的图片
	 */
	public HHAutoRecycleViewPagerAdapter(List<? extends HHImageViewPagerImp> list, Context context, int defaultImageID)
	{
		super();
		this.mList = list;
		this.mContext = context;
		this.mDefaultImageID = defaultImageID;
		if (this.mDefaultImageID<1)
		{
			this.mDefaultImageID=R.drawable.hh_default_image;
		}
	}
	/**
	 * 设置显示的数据
	 * @param list
	 */
	public void setDataList(List<? extends HHImageViewPagerImp> list)
	{
		this.mList=list;
	}
	/**
	 * 设置默认图片的ID 
	 * @param defaultImageID
	 */
	public void setDefaultImageID(int defaultImageID)
	{
		this.mDefaultImageID=defaultImageID;
	}
	/**
	 * 设置当Item被点击的时候执行的监听器
	 * @param listener
	 */
	public void setOnItemClickListener(OnItemClickListener listener)
	{
		this.mListener=listener;
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
	public Object instantiateItem(ViewGroup container,final int position)
	{
		final ImageView imageView=new ImageView(mContext);
		imageView.setScaleType(ScaleType.CENTER_CROP);
		imageView.setImageResource(mDefaultImageID);
		LayoutParams params=new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
		imageView.setLayoutParams(params);
		imageView.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				if (mListener!=null)
				{
					mListener.onItemClick(null, imageView, position, 0);
				}	
			}
		});
		HHImageViewPagerImp imp = mList.get(position);
		Builder.getNewInstance(imageView, imp.getDefaultImage()).defaultImageID(mDefaultImageID).loadImageNotWifi(true).load();
		container.addView(imageView);
		return imageView;
	}
	

	
}
