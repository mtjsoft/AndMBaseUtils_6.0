package com.huahan.hhbaseutils.dialog;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.view.View;
import android.view.animation.Interpolator;

public abstract class HHBaseAnimatorSet
{

	/**
	 * 动画默认的执行时间
	 */
	private long mAnimDuration = 500;
	/**
	 * 保存了动画的集合
	 */
	private AnimatorSet mAnimatorSet = new AnimatorSet();
	/**
	 * 动画的插值器
	 */
	private Interpolator mInterpolator;
	/**
	 * 动画开始的延时时间
	 */
	private long mAnimDelay;
	/**
	 * 动画执行的监听器
	 */
	private AnimatorListener mAnimListener;

	/**
	 * 给指定的View设置动画
	 * 
	 * @param view
	 */
	public abstract void setAnimation(View view);

	protected void start(final View view)
	{
		/** 设置动画中心点:pivotX--->X轴方向动画中心点,pivotY--->Y轴方向动画中心点 */
		// ViewHelper.setPivotX(view, view.getMeasuredWidth() / 2.0f);
		// ViewHelper.setPivotY(view, view.getMeasuredHeight() / 2.0f);
		reset(view);
		// 给View设置动画
		setAnimation(view);
		// 设置动画的执行时间
		mAnimatorSet.setDuration(mAnimDuration);
		//设置动画的插值器
		if (mInterpolator != null)
		{
			mAnimatorSet.setInterpolator(mInterpolator);
		}
		//设置动画开始的延时时间
		if (mAnimDelay > 0)
		{
			mAnimatorSet.setStartDelay(mAnimDelay);
		}
		//设置动画的监听器
		if (mAnimListener != null)
		{
			mAnimatorSet.addListener(new Animator.AnimatorListener()
			{
				@Override
				public void onAnimationStart(Animator animator)
				{
					mAnimListener.onAnimationStart(animator);
				}

				@Override
				public void onAnimationRepeat(Animator animator)
				{
					mAnimListener.onAnimationRepeat(animator);
				}

				@Override
				public void onAnimationEnd(Animator animator)
				{
					mAnimListener.onAnimationEnd(animator);
				}

				@Override
				public void onAnimationCancel(Animator animator)
				{
					mAnimListener.onAnimationCancel(animator);
				}
			});
		}
		//开始动画
		mAnimatorSet.start();
	}

	/**
	 * 重置View的基本状态
	 * 
	 * @param view
	 *            需要重置的View
	 */
	public static void reset(View view)
	{
		// 设置View的透明度为1
		view.setAlpha(1);
		// 设置View的X轴的缩放比例是1，即不缩放
		view.setScaleX(1);
		// 设置View的Y轴的缩放比例是1，即不缩放
		view.setScaleY(1);
		// 设置View在X轴移动距离是0
		view.setTranslationX(0);
		// 设置View在Y轴移动距离是0
		view.setTranslationY(0);
		// 设置旋转角度是0
		view.setRotation(0);
		view.setRotationY(0);
		view.setRotationX(0);
	}

	/**
	 * 设置动画的时间
	 * @param duration
	 * @return
	 */
	public HHBaseAnimatorSet duration(long duration)
	{
		this.mAnimDuration = duration;
		return this;
	}

	/**
	 * 设置动画开始的延时时间
	 * @param delay
	 * @return
	 */
	public HHBaseAnimatorSet delay(long delay)
	{
		this.mAnimDelay = delay;
		return this;
	}

	/**
	 * 设置动画的插值器
	 * @param interpolator
	 * @return
	 */
	public HHBaseAnimatorSet interpolator(Interpolator interpolator)
	{
		this.mInterpolator = interpolator;
		return this;
	}

	/**
	 * 设置动画的监听器
	 * @param listener
	 * @return
	 */
	public HHBaseAnimatorSet listener(AnimatorListener listener)
	{
		this.mAnimListener = listener;
		return this;
	}

	/**
	 * 设置动画在那个View上执行
	 * @param view
	 */
	public void playOn(View view)
	{
		start(view);
	}
	/**
	 * 获取当前动画的集合
	 * @return
	 */
	public AnimatorSet getAnimatorSet()
	{
		return mAnimatorSet;
	}
	/**
	 * 定义了动画执行的监听器
	 * @author yuan
	 *
	 */
	public interface AnimatorListener
	{
		void onAnimationStart(Animator animator);

		void onAnimationRepeat(Animator animator);

		void onAnimationEnd(Animator animator);

		void onAnimationCancel(Animator animator);
	}
}
