package com.huahan.hhbaseutils.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.huahan.hhbaseutils.imp.HHWindowTypeListImp;

import java.util.List;

public class HHWindowTypeAdapter extends HHBaseAdapter<HHWindowTypeListImp>
{

	public HHWindowTypeAdapter(Context context, List<HHWindowTypeListImp> list)
	{
		super(context, list);
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		return null;
	}
	private class ViewHolder
	{
		TextView nameTextView;
	}

}
