package com.huahan.hhbaseutils.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import com.huahan.hhbaseutils.HHViewHelper;
import com.huahan.hhbaseutils.R;
import com.huahan.hhbaseutils.imp.CityInfoImp;
import com.huahan.hhbaseutils.rippleview.MaterialRippleLayout;

import java.util.HashMap;
import java.util.List;

public class HHCityAdapter extends HHBaseAdapter<CityInfoImp>
{

	//当一条点击的时候执行的监听器
	private OnItemClickListener mItemClickListener;
	//用于保存索引位置的map集合
	private HashMap<String, Integer> mIndexMap=new HashMap<String, Integer>();
	public HHCityAdapter(Context context, List<CityInfoImp> list)
	{
		super(context, list);
		// TODO Auto-generated constructor stub
		for (int i = 0; i < list.size(); i++)
		{
			CityInfoImp imp = list.get(i);
			if (mIndexMap.containsKey(imp.getCityIndex()))
			{
				continue;
			}
			mIndexMap.put(imp.getCityIndex(), i);
		}
	}
	/**
	 * 设置每条点击的时候执行的监听器
	 * @param listener
	 */
	public void setOnItemClickListener(OnItemClickListener listener)
	{
		mItemClickListener=listener;
	}
	@SuppressLint("DefaultLocale") @Override
	public View getView(final int position,View convertView, ViewGroup parent)
	{
		// TODO Auto-generated method stub
		ViewHolder viewHolder=null;
		if (convertView==null)
		{
			convertView=View.inflate(getContext(), R.layout.hh_item_select_city, null);
			viewHolder=new ViewHolder();
			viewHolder.nameTextView=HHViewHelper.getViewByID(convertView, R.id.hh_tv_city_name);
			viewHolder.indexTextView=HHViewHelper.getViewByID(convertView, R.id.hh_tv_city_index);
			viewHolder.nameLayout=HHViewHelper.getViewByID(convertView, R.id.hh_mrl_name);
			convertView.setTag(viewHolder);
		}else {
			viewHolder=(ViewHolder) convertView.getTag();
		}
		if (showIndex(position))
		{
			viewHolder.indexTextView.setVisibility(View.VISIBLE);
			viewHolder.indexTextView.setText(getList().get(position).getCityIndex().toUpperCase());
		}else {
			viewHolder.indexTextView.setVisibility(View.GONE);
		}
		if (mItemClickListener!=null)
		{
			final View view=convertView;
			viewHolder.nameLayout.setOnClickListener(new OnClickListener()
			{
				
				@Override
				public void onClick(View v)
				{
					// TODO Auto-generated method stub
					mItemClickListener.onItemClick(null, view, position, 0);
				}
			});
		}
		viewHolder.nameTextView.setText(getList().get(position).getCityName());
		return convertView;
	}
	private class ViewHolder
	{
		TextView nameTextView;
		TextView indexTextView;
		MaterialRippleLayout nameLayout;
	}
	/**
	 * 代表的是是否应该显示索引
	 * @param posi
	 * @return
	 */
	private boolean showIndex(int posi)
	{
		if (posi>0)
		{
			CityInfoImp infoImp = getList().get(posi);
			CityInfoImp tempImp = getList().get(posi-1);
			if (infoImp.getCityIndex().equalsIgnoreCase(tempImp.getCityIndex()))
			{
				return false;
			}
			
		}
		return true;
	}
	/**
	 * 获取当前索引的位置
	 * @param indexLetter
	 * @return
	 */
	public int getIndexPosition(String indexLetter)
	{
		if (mIndexMap.containsKey(indexLetter))
		{
			return mIndexMap.get(indexLetter);
		}
		return -1;
	}

}
