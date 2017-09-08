package com.huahan.hhbaseutils.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.huahan.hhbaseutils.HHSystemUtils;
import com.huahan.hhbaseutils.HHViewHelper;
import com.huahan.hhbaseutils.R;

//import com.huahan.hhbaseutils.R;

public class HHShareAdapter extends BaseAdapter
{

	private int titles[]=null;
	private int icons[]=null;
	private Context context;
	public HHShareAdapter(Activity activity)
	{
		// TODO Auto-generated constructor stub
		this.context=activity;
		titles=new int[]{
				HHSystemUtils.getResourceID(context, "share_wx", "string"),
				HHSystemUtils.getResourceID(context, "share_wx_timeline", "string"),
				HHSystemUtils.getResourceID(context, "share_qq", "string"),
				HHSystemUtils.getResourceID(context, "share_sina", "string"),
				};
		icons=new int[]{
				HHSystemUtils.getResourceID(context, "hh_share_wx", "drawable"),
				HHSystemUtils.getResourceID(context, "hh_share_wx_timeline", "drawable"),
				HHSystemUtils.getResourceID(context, "hh_share_qq", "drawable"),
				HHSystemUtils.getResourceID(context, "hh_share_sina", "drawable"),
				};
	}

	@Override
	public int getCount()
	{
		// TODO Auto-generated method stub
		return titles.length;
	}

	@Override
	public Object getItem(int position)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position)
	{
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		// TODO Auto-generated method stub
		View view=View.inflate(context, R.layout.hh_item_window_share, null);
//		View view=View.inflate(context, R.layout.item_window_share, null);
		TextView typeTextView=HHViewHelper.getViewByID(view,R.id.tv_share_type);
//		TextView typeTextView=HHViewHelper.getViewByID(view, R.id.tv_share_type);
		typeTextView.setCompoundDrawablesWithIntrinsicBounds(0, icons[position], 0, 0);
		typeTextView.setText(titles[position]);
		return view;
	}

}
