package com.huahan.hhbaseutils.manager;

import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

public class HHAnimBuilder
{
	private static final int ANIM_DURATION = 500;
	/**
	 * 创建一个透明度的动画，并且给参数view设置这个动画
	 * @param view			实现动画的view
	 * @return
	 */
	public static Animation buildAlphAnimation(View view)
	{

		AlphaAnimation alphaAnimation = new AlphaAnimation(0.2f, 1.0f);
		alphaAnimation.setDuration(ANIM_DURATION);
		if (view != null)
		{
			view.setAnimation(alphaAnimation);
		}
		return alphaAnimation;
	}
	/**
	 *  创建一个位移的动画，并且给参数view设置这个动画
	 * @param view			实现动画的view
	 * @return
	 */
	public static Animation buildTranslateAnimation(View view)
	{
		TranslateAnimation transAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, -1, Animation.RELATIVE_TO_SELF, 0,
				Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
		
		transAnimation.setDuration(ANIM_DURATION);
		if (view != null)
		{
			view.setAnimation(transAnimation);
		}
		return transAnimation;
	}
	/**
	 * 创建一个透明度动画
	 * @param durationMillis		动画持续的时间
	 * @return
	 */
	public static Animation buildAlphaAnimation(long durationMillis)
	{
		AlphaAnimation animation=new AlphaAnimation(0.2f, 1.0f);
		animation.setDuration(durationMillis);
		animation.setFillAfter(true);
		return animation;
	}

	

}
