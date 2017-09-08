package com.huahan.hhbaseutils.model;

import android.view.View.OnClickListener;
/**
 * 该类记录了LoadView的点击事件
 * @author yuan
 *
 */
public class HHLoadViewStateRecord
{
	private OnClickListener mListener;
	private boolean mJustImageView;

	public OnClickListener getOnClickListener()
	{
		return mListener;
	}

	public void setOnClickListener(OnClickListener onClickListener)
	{
		this.mListener = onClickListener;
	}

	public boolean isJustImageView()
	{
		return mJustImageView;
	}

	public void setJustImageView(boolean justImageView)
	{
		this.mJustImageView = justImageView;
	}

	public HHLoadViewStateRecord(OnClickListener onClickListener, boolean justImageView)
	{
		super();
		this.mListener = onClickListener;
		this.mJustImageView = justImageView;
	}
}
