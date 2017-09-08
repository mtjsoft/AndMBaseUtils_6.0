package com.huahan.hhbaseutils.dialog;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;

@SuppressWarnings("rawtypes")
public abstract class HHBottomTopBaseDialog<T extends HHBottomTopBaseDialog<T>> extends HHBaseDialog
{
	/**
	 * 执行动画的View
	 */
	private View mAnimateView;
	/**
	 * 窗口进入的时候执行的动画集合
	 */
	private HHBaseAnimatorSet mWindowInAnimatorSet;
	/**
	 * 窗口退出的时候执行的动画集合
	 */
	private HHBaseAnimatorSet mWindowOutAnimatorSet;
	/**
	 * 显示的时候执行的动画效果
	 */
	protected Animation mInnerShowAnim;
	/**
	 * 隐藏的时候显示的动画效果
	 */
	protected Animation mInnerDismissAnim;
	/**
	 * 动画执行的时间
	 */
	protected long mInnerAnimDuration = 350;
	/**
	 * 显示的动画效果是否正在执行
	 */
	protected boolean mIsInnerShowAnim;
	/**
	 * 隐藏的动画是否正在执行
	 */
	protected boolean mIsInnerDismissAnim;
	private int mLeft, mTop, mRight, mBottom;

	public HHBottomTopBaseDialog(Context context)
	{
		super(context);
	}
	/**
	 * 设置显示的时候执行的动画效果
	 * @param animation
	 */
	public void setInnerShowAnim(Animation animation)
	{
		this.mInnerShowAnim=animation;
	}
	/**
	 * 设置隐藏的时候执行的动画效果
	 * @param animation
	 */
	public void setInnerDismissAnim(Animation animation)
	{
		this.mInnerDismissAnim=animation;
	}
	/**
	 * 设置内部动画执行的时间
	 * @param innerAnimDuration
	 *            动画执行的时间
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public T innerAnimDuration(long innerAnimDuration)
	{
		this.mInnerAnimDuration = innerAnimDuration;
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public T padding(int left, int top, int right, int bottom)
	{
		this.mLeft = left;
		this.mTop = top;
		this.mRight = right;
		this.mBottom = bottom;
		return (T) this;
	}
	public int getPaddingLeft()
	{
		return mLeft;
	}
	public int getPaddingRight()
	{
		return mRight;
	}
	public int getPaddingTop()
	{
		return mTop;
	}
	public int getPaddingBottom()
	{
		return mBottom;
	}
	/**
	 * 显示对话框
	 * animation(设置dialog和animateView显示动画)
	 */
	protected void showWithAnim()
	{
		if (mInnerShowAnim != null)
		{
			//设置内部显示的动画效果的时间
			mInnerShowAnim.setDuration(mInnerAnimDuration);
			//设置动画执行的监听器，并且纪录当前动画执行的状态
			mInnerShowAnim.setAnimationListener(new AnimationListener()
			{
				@Override
				public void onAnimationStart(Animation animation)
				{
					mIsInnerShowAnim = true;
				}

				@Override
				public void onAnimationRepeat(Animation animation)
				{

				}
				@Override
				public void onAnimationEnd(Animation animation)
				{
					mIsInnerShowAnim = false;
				}
			});
			getControlLayout().startAnimation(mInnerShowAnim);
		}
		//如果设置了动画执行的View
		if (mAnimateView != null)
		{
			//如果设置了窗口进入的动画
			if (getWindowInAnimSet() != null)
			{
				mWindowInAnimatorSet = getWindowInAnimSet();
				//设置动画执行的时间和在那个View上执行动画
				mWindowInAnimatorSet.duration(mInnerAnimDuration).playOn(mAnimateView);
			}
		}
	}

	/**
	 * 隐藏
	 */
	protected void dismissWithAnim()
	{
		if (mInnerDismissAnim != null)
		{
			mInnerDismissAnim.setDuration(mInnerAnimDuration);
			mInnerDismissAnim.setAnimationListener(new AnimationListener()
			{
				@Override
				public void onAnimationStart(Animation animation)
				{
					mIsInnerDismissAnim = true;
				}

				@Override
				public void onAnimationRepeat(Animation animation)
				{
				}

				@Override
				public void onAnimationEnd(Animation animation)
				{
					mIsInnerDismissAnim = false;
					superDismiss();
				}
			});
			getControlLayout().startAnimation(mInnerDismissAnim);
		} else
		{
			superDismiss();
		}

		if (mAnimateView != null)
		{
			if (getWindowOutAnimSet() != null)
			{
				mWindowOutAnimatorSet = getWindowOutAnimSet();
				mWindowOutAnimatorSet.duration(mInnerAnimDuration).playOn(mAnimateView);
			}
		}
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev)
	{
		//当动画正在执行的时候阻止触摸事件的传递
		if (mIsInnerDismissAnim || mIsInnerShowAnim)
		{
			return true;
		}
		return super.dispatchTouchEvent(ev);
	}

	@Override
	public void onBackPressed()
	{
		//当动画正在执行的时候阻止返回事件的执行
		if (mIsInnerDismissAnim || mIsInnerShowAnim)
		{
			return;
		}
		super.onBackPressed();
	}
	/**
	 * 获取窗口进入的时候显示的动画效果
	 * @return
	 */
	protected abstract HHBaseAnimatorSet getWindowInAnimSet();
	/**
	 * 获取窗口离开的时候显示的动画效果
	 * @return
	 */
	protected abstract HHBaseAnimatorSet getWindowOutAnimSet();
	/**
	 * 设置动画绑定的View
	 * @param view
	 */
	protected void setAnimateView(View view)
	{
		this.mAnimateView=view;
	}
	/**
	 * 获取动画绑定的View
	 * @return
	 */
	public View getAnimateView()
	{
		return mAnimateView;
	}
}
