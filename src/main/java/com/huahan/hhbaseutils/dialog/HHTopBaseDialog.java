package com.huahan.hhbaseutils.dialog;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;

@SuppressWarnings("rawtypes")
public abstract class HHTopBaseDialog<T extends HHTopBaseDialog<T>> extends HHBottomTopBaseDialog
{
	public HHTopBaseDialog(Context context, View animateView)
	{
		super(context);
		setAnimateView(animateView);
		//定义Dialog显示的时候执行的动画效果
		setInnerShowAnim(new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, -1, Animation.RELATIVE_TO_SELF, 0));
		//定义Dialog隐藏的时候执行的动画效果
		setInnerDismissAnim(new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, -1));
	}

	public HHTopBaseDialog(Context context)
	{
		this(context, null);
	}

	@Override
	protected void onStart()
	{
		super.onStart();
		getTopLayout().setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
		//设置dialog中view在window的上边显示
		getTopLayout().setGravity(Gravity.TOP);
		getWindow().setGravity(Gravity.TOP);
		//设置padding值
		getTopLayout().setPadding(getPaddingLeft(), getPaddingTop(), getPaddingRight(), getPaddingBottom());
	}

	@Override
	public void onAttachedToWindow()
	{
		super.onAttachedToWindow();
		//当附加到window上的时候，执行显示的动画效果
		showWithAnim();
	}

	@Override
	public void dismiss()
	{
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
