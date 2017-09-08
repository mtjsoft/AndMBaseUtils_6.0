package com.huahan.hhbaseutils.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.huahan.hhbaseutils.HHViewHelper;
import com.huahan.hhbaseutils.R;
import com.huahan.hhbaseutils.model.HHShareItemInfo;

import java.util.List;

public class HHShareExAdapter extends HHBaseAdapter<HHShareItemInfo>
{
	public HHShareExAdapter(Context context, List<HHShareItemInfo> list)
	{
		super(context, list);
	}
	@SuppressLint("ViewHolder") @Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		HHShareItemInfo model = getList().get(position);
		View view=View.inflate(getContext(), R.layout.hh_item_window_share, null);
		TextView typeTextView=HHViewHelper.getViewByID(view,R.id.tv_share_type);
		typeTextView.setCompoundDrawablesWithIntrinsicBounds(0, model.getDrawableID(), 0, 0);
		typeTextView.setText(model.getNameID());
		return view;
	}
}
