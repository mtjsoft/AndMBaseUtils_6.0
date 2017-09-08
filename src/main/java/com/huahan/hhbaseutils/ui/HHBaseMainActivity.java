package com.huahan.hhbaseutils.ui;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;

import com.huahan.hhbaseutils.HHDensityUtils;
import com.huahan.hhbaseutils.HHScreenUtils;
import com.huahan.hhbaseutils.HHViewHelper;
import com.huahan.hhbaseutils.R;

public abstract class HHBaseMainActivity extends HHBaseActivity {

	// 底部布局
	private RelativeLayout mBottomLayout;
	private RadioGroup mItemGroup;
	/**
	 * 当前选中的是那个Fragment
	 */
	private int mItemPosi = -1;
	// fragment的管理器
	private FragmentManager mFragManager;
	// 表示的是当前的Fragment
	private Fragment mCurrentFragment;

	@Override
	public View initView() {
		View view = View.inflate(this, R.layout.hh_activity_base_main, null);
		return view;
	}

	@SuppressWarnings("deprecation")
	@SuppressLint("UseSparseArrays")
	@Override
	public void initValues() {
		View view = View.inflate(this, R.layout.hh_include_base_main_bottom,
				null);
		mBottomLayout = HHViewHelper.getViewByID(view, R.id.hh_rl_base_main);
		mItemGroup = HHViewHelper.getViewByID(view, R.id.hh_rg_base_main);
		mFragManager = getSupportFragmentManager();
		addViewToBottom(view);
		addItem(getDrawableIDs(), getItemNames());
		mItemGroup.setBackgroundDrawable(getMainBottomBackgroundDrawable());
	}

	/**
	 * 把View添加到底部的Layout当中
	 * 
	 * @param view
	 */
	private void addViewToBottom(View view) {
		LinearLayout.LayoutParams mParams = new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		getBaseBottomLayout().addView(view, mParams);
		getBaseBottomLayout().setBackgroundColor(Color.RED);
	}

	/**
	 * 把Item添加到Group中
	 * 
	 * @param item
	 */
	private void addItemToGroup(View item) {
		RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(0,
				RadioGroup.LayoutParams.MATCH_PARENT);
		params.gravity = Gravity.CENTER;
		params.weight = 1;
		mItemGroup.addView(item, params);
	}

	/**
	 * 添加显示的Item
	 * 
	 * @param drawableIDs
	 *            显示的图片
	 * @param itemNames
	 *            显示的标题
	 */
	protected void addItem(int[] drawableIDs, String[] itemNames) {
		if (drawableIDs == null || itemNames == null
				|| drawableIDs.length != itemNames.length) {
			throw new RuntimeException(
					"please check getDrawableIDs() and getItemNames() method");
		}
		for (int i = 0; i < itemNames.length; i++) {
			RadioButton item = getItemStyle();
			item.setButtonDrawable(new ColorDrawable(Color.TRANSPARENT));
			item.setGravity(Gravity.CENTER);
			// 设置的ID必须是一个正整数
			item.setId(i + 1);
			item.setText(itemNames[i]);
			item.setCompoundDrawablesWithIntrinsicBounds(0, drawableIDs[i], 0,
					0);
			item.setCompoundDrawablePadding(HHDensityUtils.dip2px(getPageContext(),2));
			addItemToGroup(item);
		}

	}

	/**
	 * 获取指定位置的fragment，position从0开始
	 * 
	 * @param position
	 * @return
	 */
	public Fragment getFragmentAtPosition(int position) {
		Fragment fragment = mFragManager.findFragmentByTag(position + 1 + "");
		return fragment;
	}

	@Override
	public void initListeners() {
		mItemGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (onItemChecked(checkedId - 1)) {
					FragmentTransaction transaction = mFragManager
							.beginTransaction();
					Fragment fragment = mFragManager
							.findFragmentByTag(checkedId + "");
					if (fragment == null) {
						fragment = getFragment(checkedId - 1);
						transaction.add(R.id.hh_fl_base_main, fragment,
								checkedId + "");
					}
					mItemPosi = checkedId - 1;
					// fragment.onResume();
					if (mCurrentFragment != null) {
						mCurrentFragment.onPause();
						transaction.hide(mCurrentFragment);
					}
					mCurrentFragment = fragment;
					transaction.show(mCurrentFragment);
					transaction.commitAllowingStateLoss();
				} else {
					mItemGroup.check(mItemPosi + 1);
				}

			}
		});

		mItemPosi = 0;
		checkItem(mItemPosi);
	}

	/**
	 * 如果返回false，则阻止后续的操作，同时选中之前选中的按钮；true则继续执行后续操作；
	 * 
	 * @param position
	 * @return
	 */
	protected boolean onItemChecked(int position) {
		return true;
	}

	/**
	 * 选中某个Item
	 * 
	 * @param posi
	 *            位置
	 */
	protected void checkItem(int posi) {
		mItemPosi = posi;
		RadioButton childAt = (RadioButton) mItemGroup.getChildAt(mItemPosi);
		childAt.setChecked(true);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("item_posi", mItemPosi);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		mItemPosi = savedInstanceState.getInt("item_posi", 0);
		checkItem(mItemPosi);
	}

	/**
	 * 获取显示的图片的ID
	 * 
	 * @return
	 */
	protected abstract int[] getDrawableIDs();

	/**
	 * 返回Item显示的文本信息
	 * 
	 * @return
	 */
	protected abstract String[] getItemNames();

	/**
	 * 返回Item显示的样式的模板，如果重写了该方法，添加子Item的时候，创建的Item的Item会使用该方法返回的样式
	 * 
	 * @return
	 */
	protected abstract RadioButton getItemStyle();

	/**
	 * 获取需要显示的Fragment
	 * 
	 * @param position
	 *            当前的位置,从0开始
	 */
	protected abstract Fragment getFragment(int position);

	/**
	 * 设置底部的背景
	 * 
	 * @return
	 */
	protected abstract Drawable getMainBottomBackgroundDrawable();

	/**
	 * 返回底部的Layout
	 * 
	 * @return
	 */
	protected RelativeLayout getMainBottomLayout() {
		return mBottomLayout;
	}

	// /**
	// * 把一个View添加到底部Layout中，一般用于显示未读消息的提示
	// * @param view
	// * @param itemPosi
	// * @param rightMargin
	// * @param topMargin
	// */
	// protected void addViewToMainBottom(View view,int itemPosi,int
	// rightMargin,int topMargin)
	// {
	// View childAt = mItemGroup.getChildAt(itemPosi);
	// int[] location=new int[2];
	// childAt.getLocationOnScreen(location);
	// Log.i("chenyuan", "x:"+location[0]+",y:"+location[1]);
	// RelativeLayout.LayoutParams params=new
	// RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
	// RelativeLayout.LayoutParams.WRAP_CONTENT);
	//
	// }
	/**
	 * 把一个View添加到底部Layout中，一般用于显示未读消息的提示
	 *
	 * @param view
	 * @param itemPosi
	 * @param rightMargin
	 * @param topMargin
	 */
	protected void addViewToMainBottom(View view, int itemPosi,
			int rightMargin, int topMargin, int wid) {
		int width = HHScreenUtils.getScreenWidth(getPageContext())
				/ mItemGroup.getChildCount();
		int w_h = HHDensityUtils.dip2px(getPageContext(), wid);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				w_h, w_h);
		params.setMargins(width * (itemPosi + 1) - rightMargin, topMargin, 0, 0);
		view.setLayoutParams(params);
		mBottomLayout.addView(view);
	}

	/**
	 * 返回底部的RadioGroup
	 * 
	 * @return
	 */
	protected RadioGroup getMainBottomGroup() {
		return mItemGroup;
	}

	public int getItemPosi() {
		return this.mItemPosi;
	}

}
