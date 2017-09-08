package com.huahan.hhbaseutils.view.refreshlist;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import com.huahan.hhbaseutils.HHViewHelper;
import com.huahan.hhbaseutils.R;
import com.huahan.hhbaseutils.view.spinnerload.SpinnerLoader;

import java.text.SimpleDateFormat;
import java.util.Date;






/**
 * listview，下拉刷新
 * @author 陈
 *
 */
@SuppressLint({ "SimpleDateFormat", "ClickableViewAccessibility" }) 
public class HHRefreshListView extends HandyListView {
	private final static int RELEASE_TO_REFRESH = 0;
	private final static int PULL_TO_REFRESH = 1;
	private final static int REFRESHING = 2;
	private final static int DONE = 3;
	private final static int LOADING = 4;
	private final static int RATIO = 3;
	private int refreshID=R.string.hh_pull_refresh;
	private int lastID=R.string.hh_last_refresh;
	private int realseID=R.string.hh_realse_refresh;
	private int refreshingID=R.string.hh_refresh_ing;
	private View mHeader;

	private HandyTextView mHtvTitle;
	private HandyTextView mHtvTime;
	private ImageView mIvArrow;
	private SpinnerLoader mIvLoading;


	private android.view.animation.RotateAnimation mPullAnimation;
	private android.view.animation.RotateAnimation mReverseAnimation;

	private boolean mIsRecored;

	private int mHeaderHeight;

	private int mStartY;

	private int mState;

	private boolean mIsBack;
	private OnRefreshListener mOnRefreshListener;
	private boolean mIsRefreshable;

	public HHRefreshListView(Context context) {
		super(context);
		init();
	}

	public HHRefreshListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public HHRefreshListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	@SuppressLint("InflateParams") 
	private void init() {
		mHeader = mInflater.inflate(R.layout.hh_include_pull_to_refresh,null);
		mHtvTitle = HHViewHelper.getViewByID(mHeader, R.id.hh_tv_refresh_title);
		mHtvTime = HHViewHelper.getViewByID(mHeader, R.id.hh_tv_refresh_time);
		mIvArrow = HHViewHelper.getViewByID(mHeader, R.id.hh_img_arrow_up);
		mIvLoading = HHViewHelper.getViewByID(mHeader, R.id.hh_img_arrow_load);
	
		measureView(mHeader);
		addHeaderView(mHeader);

		mHeaderHeight = mHeader.getMeasuredHeight();
		mHeader.setPadding(0, -1 * mHeaderHeight, 0, 0);
		mHeader.invalidate();
		
		mHtvTitle.setText(refreshID);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String date = format.format(new Date());
		mHtvTime.setText(getContext().getString(lastID) + date);

		mPullAnimation = new android.view.animation.RotateAnimation(0, -180,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		mPullAnimation.setInterpolator(new LinearInterpolator());
		mPullAnimation.setDuration(250);
		mPullAnimation.setFillAfter(true);

		mReverseAnimation = new android.view.animation.RotateAnimation(-180, 0,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		mReverseAnimation.setInterpolator(new LinearInterpolator());
		mReverseAnimation.setDuration(200);
		mReverseAnimation.setFillAfter(true);
		mState = DONE;
		mIsRefreshable = false;
	}

	@Override
	public void onDown(MotionEvent ev) {
		if (mIsRefreshable) {
			if (mFirstVisibleItem == 0 && !mIsRecored) {
				mIsRecored = true;
				mStartY = mDownPoint.y;
			}
		}
	}

	@Override
	public void onMove(MotionEvent ev) {
		if (mIsRefreshable) {
			if (!mIsRecored && mFirstVisibleItem == 0) {
				mIsRecored = true;
				mStartY = mMovePoint.y;
			}
			if (mState != REFRESHING && mIsRecored && mState != LOADING) {
				if (mState == RELEASE_TO_REFRESH) {
					//setSelection(0);
					if (((mMovePoint.y - mStartY) / RATIO < mHeaderHeight)
							&& (mMovePoint.y - mStartY) > 0) {
						mState = PULL_TO_REFRESH;
						changeHeaderViewByState();
					} else if (mMovePoint.y - mStartY <= 0) {
						mState = DONE;
						changeHeaderViewByState();
					}
				}
				if (mState == PULL_TO_REFRESH) {
					//setSelection(0);
					if ((mMovePoint.y - mStartY) / RATIO >= mHeaderHeight) {
						mState = RELEASE_TO_REFRESH;
						mIsBack = true;
						changeHeaderViewByState();
					} else if (mMovePoint.y - mStartY <= 0) {
						mState = DONE;
						changeHeaderViewByState();
					}
				}
				if (mState == DONE) {
					if (mMovePoint.y - mStartY > 0) {
						mState = PULL_TO_REFRESH;
						changeHeaderViewByState();
					}
				}
				if (mState == PULL_TO_REFRESH) {
					mHeader.setPadding(0, -1 * mHeaderHeight
							+ (mMovePoint.y - mStartY) / RATIO, 0, 0);
				}
				if (mState == RELEASE_TO_REFRESH) {
					mHeader.setPadding(0, (mMovePoint.y - mStartY) / RATIO
							- mHeaderHeight, 0, 0);
				}

			}

		}
	}

	@Override
	public void onUp(MotionEvent ev) {
		if (mState != REFRESHING && mState != LOADING) {
			if (mState == PULL_TO_REFRESH) 
			{
				mState = DONE;
				changeHeaderViewByState();
			}
			if (mState == RELEASE_TO_REFRESH) {
				mState = REFRESHING;
				changeHeaderViewByState();
				onRefresh();

			}
		}
		mIsRecored = false;
		mIsBack = false;
	}

	private void measureView(View child) {
		ViewGroup.LayoutParams p = child.getLayoutParams();
		if (p == null) {
			p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
		}
		int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
		int lpHeight = p.height;
		int childHeightSpec;
		if (lpHeight > 0) {
			childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight,
					MeasureSpec.EXACTLY);
		} else {
			childHeightSpec = MeasureSpec.makeMeasureSpec(0,
					MeasureSpec.UNSPECIFIED);
		}
		child.measure(childWidthSpec, childHeightSpec);
	}

	private void changeHeaderViewByState() {
		switch (mState) {
		case RELEASE_TO_REFRESH:
			mIvArrow.setVisibility(View.VISIBLE);
			mIvLoading.setVisibility(View.GONE);
			mHtvTitle.setVisibility(View.VISIBLE);
			mHtvTime.setVisibility(View.VISIBLE);
			mIvArrow.clearAnimation();
			mIvArrow.startAnimation(mPullAnimation);
			mIvLoading.clearAnimation();
			mHtvTitle.setText(realseID);
			break;
		case PULL_TO_REFRESH:
			mIvArrow.setVisibility(View.VISIBLE);
			mIvLoading.setVisibility(View.GONE);
			mHtvTitle.setVisibility(View.VISIBLE);
			mHtvTime.setVisibility(View.VISIBLE);
			mIvLoading.clearAnimation();
			mIvArrow.clearAnimation();
			if (mIsBack) {
				mIsBack = false;
				mIvArrow.clearAnimation();
				mIvArrow.startAnimation(mReverseAnimation);
				mHtvTitle.setText(refreshID);
			} else {
				mHtvTitle.setText(refreshID);
			}
			break;

		case REFRESHING:
			mHeader.setPadding(0, 0, 0, 0);
			mIvLoading.setVisibility(View.VISIBLE);
			mIvArrow.setVisibility(View.GONE);
			mIvArrow.clearAnimation();
			mHtvTitle.setText(refreshingID);
			mHtvTime.setVisibility(View.VISIBLE);

			break;
		case DONE:
			mHeader.setPadding(0, -1 * mHeaderHeight, 0, 0);

			mIvLoading.setVisibility(View.GONE);
			mIvArrow.clearAnimation();
			mIvLoading.clearAnimation();
			mIvArrow.setImageResource(R.drawable.hh_refresh_arrow_down);
			mHtvTitle.setText(refreshID);
			mHtvTime.setVisibility(View.VISIBLE);
			break;
		}
	}

	public void onRefreshComplete() {
		mState = DONE;
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String date = format.format(new Date());
		mHtvTime.setText(getContext().getString(lastID)+ date);
		changeHeaderViewByState();
	}

	private void onRefresh() {
		if (mOnRefreshListener != null) {
			mOnRefreshListener.onRefresh();
		}
	}

	public void onManualRefresh() 
	{
		if (mIsRefreshable) {
			mState = REFRESHING;
			changeHeaderViewByState();
			onRefresh();
		}
	}

	public void setOnRefreshListener(OnRefreshListener l) 
	{
		mOnRefreshListener = l;
		if(mOnRefreshListener==null)
		{
			mIsRefreshable = false;
		}else {
			mIsRefreshable = true;
		}
		
	}
	public interface OnRefreshListener 
	{
		public void onRefresh();
	}

}
