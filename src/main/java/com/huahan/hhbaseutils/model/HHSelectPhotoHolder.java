package com.huahan.hhbaseutils.model;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.huahan.hhbaseutils.HHViewHelper;
import com.huahan.hhbaseutils.R;

public class HHSelectPhotoHolder extends RecyclerView.ViewHolder
{

	public HHSelectPhotoHolder(View view)
	{
		super(view);
		this.view=view;
		photoImageView=HHViewHelper.getViewByID(view, R.id.hh_img_select_photo);
		boxImageView=HHViewHelper.getViewByID(view, R.id.hh_img_box);
	}
	public ImageView photoImageView;
	public ImageView boxImageView;
	public View view;
}
