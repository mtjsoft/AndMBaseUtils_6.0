package com.huahan.hhbaseutils.view.swipe;


import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.ListAdapter;

import com.huahan.hhbaseutils.imp.SwipeMenuChangeImp;
import com.huahan.hhbaseutils.view.refreshlist.HHRefreshListView;


public class SwipeRefreshListView extends HHRefreshListView
{
//	private static final String tag=SwipeRefreshListView.class.getSimpleName();
	private static final int TOUCH_STATE_NONE = 0;
	private static final int TOUCH_STATE_X = 1;
	private static final int TOUCH_STATE_Y = 2;

	private int MAX_Y = 5;
	private int MAX_X = 3;
	private float mDownX;
	private float mDownY;
	private int mTouchState;
	private int mTouchPosition;
	private SwipeMenuLayout mTouchView;
	private OnSwipeListener mOnSwipeListener;

	private SwipeMenuCreator mMenuCreator;
	private OnMenuItemClickListener mOnMenuItemClickListener;
	private Interpolator mCloseInterpolator;
	private Interpolator mOpenInterpolator;
	private SwipeMenuChangeImp mChangeMenuImp;
	//是否可以滑动
	private boolean mSwipeEnable=true;

	public SwipeRefreshListView(Context context)
	{
		super(context);
		init();
	}

	public SwipeRefreshListView(Context context, AttributeSet attrs,
			int defStyle)
	{
		super(context, attrs, defStyle);
		init();
	}

	public SwipeRefreshListView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init();
	}

	private void init()
	{
		MAX_X = dp2px(MAX_X);
		MAX_Y = dp2px(MAX_Y);
		mTouchState = TOUCH_STATE_NONE;
	}
	public void setChangeMenuImp(SwipeMenuChangeImp changeImp)
	{
		this.mChangeMenuImp=changeImp;
	}
	@Override
	public void setAdapter(ListAdapter adapter)
	{
		super.setAdapter(new SwipeMenuAdapter(getContext(), adapter)
		{
			@Override
			public void createMenu(SwipeMenu menu,int posi)
			{
				if (mMenuCreator != null)
				{
					mMenuCreator.create(menu,posi);
				}
				
			}
			



			@Override
			public boolean changeMenu(SwipeMenu menu, int position) 
			{
				if (mChangeMenuImp!=null)
				{
					mChangeMenuImp.changeMenu(menu,position);
					return false;
				}
				if (mMenuCreator != null)
				{
					return mMenuCreator.create(menu,position);
				}
				return true;
			}




			@Override
			public void onItemClick(SwipeMenuView view, SwipeMenu menu,
					int index)
			{
				boolean flag = false;
				if (mOnMenuItemClickListener != null)
				{
					flag = mOnMenuItemClickListener.onMenuItemClick(
							view.getPosition(), menu, index);
				}
				if (mTouchView != null && !flag)
				{
					mTouchView.smoothCloseMenu();
				}
			}
		});
	}

	public void setCloseInterpolator(Interpolator interpolator)
	{
		mCloseInterpolator = interpolator;
	}

	public void setOpenInterpolator(Interpolator interpolator)
	{
		mOpenInterpolator = interpolator;
	}

	public Interpolator getOpenInterpolator()
	{
		return mOpenInterpolator;
	}

	public Interpolator getCloseInterpolator()
	{
		return mCloseInterpolator;
	}
	private float downX=0.0f;
	private float downY=0.0f;
	

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev)
	{
		switch (ev.getAction())
		{
		case MotionEvent.ACTION_DOWN:
			downX=ev.getX();
			downY=ev.getY();
			onTouchEvent(ev);
			break;
		case MotionEvent.ACTION_MOVE:
			float upX=ev.getX();
			float upY=ev.getY();
			if (Math.abs(upX-downX)>10||Math.abs(upY-downY)>10)
			{
				return true;
			}
			break;

		default:
			break;
		}
		return super.onInterceptTouchEvent(ev);
	}
	public void setSwipeEnable(boolean enable)
	{
		this.mSwipeEnable=enable;
		if (enable==false)
		{
			Log.i("chenyuan","first:"+getFirstVisiblePosition()+",last:"+getLastVisiblePosition());
			for (int i = getFirstVisiblePosition(); i <=getLastVisiblePosition(); i++)
			{
				View childAt = getChildAt(i-getFirstVisiblePosition());
				Log.i("chenyuan", "view:"+childAt);
				if (childAt instanceof SwipeMenuLayout)
				{
					SwipeMenuLayout layout=(SwipeMenuLayout) childAt;
					if (layout.isOpen())
					{
						layout.smoothCloseMenu();
					}
				}
			}
		}
	}
	@SuppressLint("ClickableViewAccessibility") 
	@Override
	public boolean onTouchEvent(MotionEvent ev)
	{
			int action = MotionEventCompat.getActionMasked(ev);
			action = ev.getAction();
			switch (action)
			{
			case MotionEvent.ACTION_DOWN:
				int oldPos = mTouchPosition;
				mDownX = ev.getX();
				mDownY = ev.getY();
				mTouchState = TOUCH_STATE_NONE;

				mTouchPosition = pointToPosition((int) ev.getX(),
						(int) ev.getY());

				if (mTouchPosition == oldPos && mTouchView != null
						&& mTouchView.isOpen())
				{
					mTouchState = TOUCH_STATE_X;
					mTouchView.setSwipeEnable(mSwipeEnable);
					mTouchView.onSwipe(ev);
					return true;
				}

				View view = getChildAt(mTouchPosition
						- getFirstVisiblePosition());
			
				if (mTouchView != null && mTouchView.isOpen())
				{
					mTouchView.smoothCloseMenu();
					mTouchView = null;
					return super.onTouchEvent(ev);
				}
				if (view instanceof SwipeMenuLayout)
				{
					mTouchView = (SwipeMenuLayout) view;
				}
				if (mTouchView != null)
				{
					mTouchView.setSwipeEnable(mSwipeEnable);
					mTouchView.onSwipe(ev);
				}
				break;
			case MotionEvent.ACTION_MOVE:
				float dy = Math.abs((ev.getY() - mDownY));
				float dx = Math.abs((ev.getX() - mDownX));
//				if (dy>dx)
//				{
//					return super.onTouchEvent(ev);
//				}
				//当前触摸在一个已展开的view上
				if (mTouchState == TOUCH_STATE_X)
				{
					if (mTouchView != null)
					{
						mTouchView.onSwipe(ev);
					}
					getSelector().setState(new int[] { 0 });
					ev.setAction(MotionEvent.ACTION_CANCEL);
					super.onTouchEvent(ev);
					return true;
				} else if (mTouchState == TOUCH_STATE_NONE)
				{
					if (Math.abs(dy) > MAX_Y)
					{
						mTouchState = TOUCH_STATE_Y;
					} else if (dx > MAX_X)
					{
						mTouchState = TOUCH_STATE_X;
						if (mOnSwipeListener != null)
						{
							mOnSwipeListener.onSwipeStart(mTouchPosition);
						}
					}
				}
				break;
			case MotionEvent.ACTION_UP:
				if (mTouchState == TOUCH_STATE_X)
				{
					if (mTouchView != null)
					{
						mTouchView.onSwipe(ev);
						if (!mTouchView.isOpen())
						{
							mTouchPosition = -1;
							mTouchView = null;
						}
					}
					if (mOnSwipeListener != null)
					{
						mOnSwipeListener.onSwipeEnd(mTouchPosition);
					}
					ev.setAction(MotionEvent.ACTION_CANCEL);
					super.onTouchEvent(ev);
					return true;
				}
				break;
			}
			return super.onTouchEvent(ev);
	}

	
	public void smoothOpenMenu(int position)
	{
		if (position >= getFirstVisiblePosition()
				&& position <= getLastVisiblePosition())
		{
			View view = getChildAt(position - getFirstVisiblePosition());
			if (view instanceof SwipeMenuLayout)
			{
				mTouchPosition = position;
				if (mTouchView != null && mTouchView.isOpen())
				{
					mTouchView.smoothCloseMenu();
				}
				mTouchView = (SwipeMenuLayout) view;
				mTouchView.smoothOpenMenu();
			}
		}
	}

	private int dp2px(int dp)
	{
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				getContext().getResources().getDisplayMetrics());
	}

	public void setMenuCreator(SwipeMenuCreator menuCreator)
	{
		this.mMenuCreator = menuCreator;
	}

	public void setOnMenuItemClickListener(
			OnMenuItemClickListener onMenuItemClickListener)
	{
		this.mOnMenuItemClickListener = onMenuItemClickListener;
	}

	public void setOnSwipeListener(OnSwipeListener onSwipeListener)
	{
		this.mOnSwipeListener = onSwipeListener;
	}

	public static interface OnMenuItemClickListener
	{
		boolean onMenuItemClick(int position, SwipeMenu menu, int index);
	}

	public static interface OnSwipeListener
	{
		void onSwipeStart(int position);
		void onSwipeEnd(int position);
	}
}
