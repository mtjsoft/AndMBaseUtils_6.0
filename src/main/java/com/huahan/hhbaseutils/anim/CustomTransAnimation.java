package com.huahan.hhbaseutils.anim;

import android.view.animation.Transformation;
import android.view.animation.TranslateAnimation;

import com.huahan.hhbaseutils.HHLog;

public class CustomTransAnimation extends TranslateAnimation
{

	public CustomTransAnimation(int fromXType, float fromXValue, int toXType, float toXValue, int fromYType, float fromYValue, int toYType, float toYValue)
	{
		super(fromXType, fromXValue, toXType, toXValue, fromYType, fromYValue, toYType, toYValue);
	}
	private OnAnimUpdateListener mListener;
	private static final String tag=CustomTransAnimation.class.getSimpleName();
	public interface OnAnimUpdateListener
	{
		void onAnimUpdate(float time);
	}
	
	public void setOnAnimUpdateListener(OnAnimUpdateListener listener)
	{
		this.mListener=listener;
	}
	@Override
	protected void applyTransformation(float interpolatedTime, Transformation t)
	{
		super.applyTransformation(interpolatedTime, t);
		HHLog.i(tag, "interpolatedTime :"+interpolatedTime);
		if (mListener!=null)
		{
			mListener.onAnimUpdate(interpolatedTime);
		}
	}
	

}
