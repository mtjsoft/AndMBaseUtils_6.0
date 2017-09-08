package com.huahan.hhbaseutils.manager;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.huahan.hhbaseutils.HHDensityUtils;
import com.huahan.hhbaseutils.HHViewHelper;
import com.huahan.hhbaseutils.R;
import com.huahan.hhbaseutils.frag.HHFragment;
import com.huahan.hhbaseutils.imp.HHTopViewManagerImp;
import com.huahan.hhbaseutils.manager.HHUiTopManager.TitleMode;
import com.huahan.hhbaseutils.ui.HHApplication;

public class HHFragDefaulTopManager implements HHTopViewManagerImp {
	private HHFragment mFragment;
	private TextView mTitleTextView;
	private TextView mBackTextView;
	private TextView mMoreTextView;
	private LinearLayout mMoreLayout;
	private TextView mLineTextView;
	private View mTopView;

	public HHFragDefaulTopManager(HHFragment fragment) {
		this.mFragment = fragment;
	}

	/**
	 * 获取头部显示的View
	 * 
	 * @return
	 */
	@Override
	public View getTopView() {
		if (mTopView == null) {
			mTopView = View.inflate(mFragment.getActivity(),
					R.layout.hh_include_top_default, null);
			setTopDefaultBackground();
			mTitleTextView = HHViewHelper.getViewByID(mTopView,
					R.id.hh_tv_top_title);
			mBackTextView = HHViewHelper.getViewByID(mTopView,
					R.id.hh_tv_top_back);
			mMoreLayout = HHViewHelper.getViewByID(mTopView,
					R.id.hh_ll_top_more);
			mMoreTextView = HHViewHelper.getViewByID(mTopView,
					R.id.hh_tv_top_more);
			mBackTextView.setVisibility(View.INVISIBLE);
			mLineTextView = HHViewHelper.getViewByID(mTopView,
					R.id.hh_tv_top_line);
			setDefaultTopInfo();
		}
		return mTopView;
	}

	/**
	 * 设置标题的显示的样式，是显示在左边还是显示在中间
	 * 
	 * @param mode
	 */
	public void setTitleMode(TitleMode mode) {
		setTitleMode(mode, 10);
	}

	/**
	 * 设置标题显示的样式
	 * 
	 * @param mode
	 *            标题的样式
	 * @param dis
	 *            当标题的样式是靠左显示的时候，这个值表示的是距离左边控件的距离
	 */
	public void setTitleMode(TitleMode mode, int dis) {
		LayoutParams layoutParams = (LayoutParams) mTitleTextView
				.getLayoutParams();
		if (mode == TitleMode.LEFT) {
			layoutParams
					.addRule(RelativeLayout.RIGHT_OF, mBackTextView.getId());
			layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
			layoutParams.leftMargin = HHDensityUtils.dip2px(
					mFragment.getActivity(), dis);
		} else {
			layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
		}
		mTitleTextView.setLayoutParams(layoutParams);
	}

	/**
	 * 设置返回键点击的时候执行的点击事件
	 * 
	 * @param listener
	 */
	public void setOnBackClickListener(OnClickListener listener) {
		mBackTextView.setOnClickListener(listener);
	}

	/**
	 * 设置头部的北京图片
	 * 
	 * @param drawableID
	 *            背景图片的ID
	 */
	public void setTopBackground(int drawableID) {
		mTopView.setBackgroundResource(drawableID);
	}

	/**
	 * 设置头部的背景颜色
	 * 
	 * @param color
	 *            背景颜色
	 */
	public void setTopBackgroundColor(int color) {
		mTopView.setBackgroundColor(color);
	}

	/**
	 * 设置默认显示的背景颜色
	 */
	private void setTopDefaultBackground() {
		if (mFragment.getActivity().getApplication() instanceof HHApplication) {
			HHApplication application = (HHApplication) mFragment.getActivity()
					.getApplication();
			mTopView.setBackgroundColor(application.getHHApplicationInfo()
					.getMainColor());
		}
	}

	/**
	 * 设置当MoreTextView点击时候执行的点击事件
	 * 
	 * @param listener
	 *            点击的时候执行的点击事件
	 */
	public void setOnMoreClickListener(OnClickListener listener) {
		mMoreTextView.setOnClickListener(listener);
	}

	public TextView getBackTextView() {
		return mBackTextView;
	}

	public TextView getTitleTextView() {
		return mTitleTextView;
	}

	public LinearLayout getMoreLayout() {
		return mMoreLayout;
	}

	public TextView getMoreTextView() {
		return mMoreTextView;
	}

	public void setTitle(String title) {
		mTitleTextView.setText(title);
	}

	@Override
	public void setDefaultTopInfo() {
		if (HHUiTopManager.mTopViewInfo.backDrawable != 0) {
			mBackTextView
					.setBackgroundResource(HHUiTopManager.mTopViewInfo.backDrawable);
		}
		if (HHUiTopManager.mTopViewInfo.backLeftDrawable != 0) {
			mBackTextView.setCompoundDrawablesWithIntrinsicBounds(
					HHUiTopManager.mTopViewInfo.backLeftDrawable, 0, 0, 0);
		}
		if (HHUiTopManager.mTopViewInfo.titleSize > 0) {
			mTitleTextView.setTextSize(HHUiTopManager.mTopViewInfo.titleSize);
		}
		mTitleTextView.setTextColor(HHUiTopManager.mTopViewInfo.titleTextColor);
		setTitleMode(HHUiTopManager.mTopViewInfo.titleMode);
		if (HHUiTopManager.mTopViewInfo.topLineColor != 0) {
			mLineTextView
					.setBackgroundColor(HHUiTopManager.mTopViewInfo.topLineColor);
		}
		if (HHUiTopManager.mTopViewInfo.topLineHeight != 0) {
			setFragLineHeight(HHUiTopManager.mTopViewInfo.topLineHeight);
		}

	}

	/**
	 * 设置头部线条的高度
	 */
	public void setFragLineHeight(int height) {
		LayoutParams layoutParams = (LayoutParams) mLineTextView
				.getLayoutParams();
		layoutParams.width = LayoutParams.MATCH_PARENT;
		layoutParams.height = HHDensityUtils.dip2px(mFragment.getActivity(),
				height);
		layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		mLineTextView.setLayoutParams(layoutParams);
	}

}
