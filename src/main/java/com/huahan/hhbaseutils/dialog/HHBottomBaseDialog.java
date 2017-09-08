package com.huahan.hhbaseutils.dialog;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;

@SuppressWarnings("rawtypes")
public abstract class HHBottomBaseDialog<T extends HHBottomBaseDialog<T>> extends HHBottomTopBaseDialog
{

	/**
	 * 获取一个实例
	 * @param context			上下文对象
	 * @param animateView		动画绑定的View
	 */
	public HHBottomBaseDialog(Context context, View animateView)
	{
		super(context);
		
		setAnimateView(animateView);
		//设置显示的动画效果是从底部向上移动，直到完全显示出来
		setInnerShowAnim(new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 1f, Animation.RELATIVE_TO_SELF, 0));
		//设置隐藏的动画效果是从当前位置开始向下移动，直到隐藏
		setInnerDismissAnim(new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 1));
	}
	public HHBottomBaseDialog(Context context)
	{
		this(context, null);
	}

	@Override
	protected void onStart()
	{
		super.onStart();
		getTopLayout().setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
		//设置内容显示在容器的底部
		getTopLayout().setGravity(Gravity.BOTTOM);
		//设置对话框显示的在window的底部
		getWindow().setGravity(Gravity.BOTTOM);
		//设置padding
		getTopLayout().setPadding(getPaddingLeft(), getPaddingTop(), getPaddingRight(), getPaddingBottom());
	}

	@Override
	public void onAttachedToWindow()
	{
		super.onAttachedToWindow();
		//当对话框绑定到window上时，显示动画效果
		showWithAnim();
	}

	@Override
	public void dismiss()
	{
		//当取消的时候执行动画效果
		dismissWithAnim();
	}

	private HHBaseAnimatorSet mWindowInAnimatorSet;
	private HHBaseAnimatorSet mWindowOutAnimatorSet;

	@Override
	protected HHBaseAnimatorSet getWindowInAnimSet()
	{
		if (mWindowInAnimatorSet == null)
		{
			mWindowInAnimatorSet = new WindowInAs();
		}
		return mWindowInAnimatorSet;
	}

	@Override
	protected HHBaseAnimatorSet getWindowOutAnimSet()
	{
		if (mWindowOutAnimatorSet == null)
		{
			mWindowOutAnimatorSet = new WindowOutAs();
		}
		return mWindowOutAnimatorSet;
	}

	private class WindowInAs extends HHBaseAnimatorSet
	{
		@Override
		public void setAnimation(View view)
		{
			ObjectAnimator oa1 = ObjectAnimator.ofFloat(view, "scaleX", 1f, 0.9f);
			ObjectAnimator oa2 = ObjectAnimator.ofFloat(view, "scaleY", 1f, 0.9f);
			getAnimatorSet().playTogether(oa1, oa2);
		}
	}

	private class WindowOutAs extends HHBaseAnimatorSet
	{
		@Override
		public void setAnimation(View view)
		{
			ObjectAnimator oa1 = ObjectAnimator.ofFloat(view, "scaleX", 0.9f, 1f);
			ObjectAnimator oa2 = ObjectAnimator.ofFloat(view, "scaleY", 0.9f, 1f);
			getAnimatorSet().playTogether(oa1, oa2);
		}
	}

}
