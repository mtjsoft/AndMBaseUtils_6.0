package com.huahan.hhbaseutils.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;

import com.huahan.hhbaseutils.HHDensityUtils;
import com.huahan.hhbaseutils.R;
import com.huahan.hhbaseutils.adapter.HHAutoRecycleViewPagerAdapter;
import com.huahan.hhbaseutils.adapter.HHOnPageChangeAdapter;
import com.huahan.hhbaseutils.imp.HHImageViewPagerImp;
import com.huahan.hhbaseutils.model.HHWeakHandler;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 实现自动循环滚动的ViewPager
 * @author yuan
 *
 */
public class HHAutoRecycleViewPager extends FrameLayout
{

	//定义控件在x和y轴上的比例，如果其中一个为0，或者小于0则显示实际大小
	private int mXAcept=0;
	private int mYAcept=0;
	//默认情况下距离底部的距离
	private static final int DEFAULT_BOTTOM_MARGIN=15;
	//默认循环的时间
	private static final int DEFAULT_TIME_SPAN=3000;
	//显示圆点的View
	private HHSelectCircleView mCircleView;
	//显示的Pager
	private ViewPager mViewPager;
	//Viewpager显示数据的适配器
	private HHAutoRecycleViewPagerAdapter mPagerAdapter;
	//timer,用于执行循环显示图片
	private Timer mTimer;
	//timer执行的时间间隔
	private long mTimeSpan=0;
	private HHWeakHandler<Context> mHandler;
	public HHAutoRecycleViewPager(Context context, AttributeSet attrs, int defStyleAttr)
	{
		super(context, attrs, defStyleAttr);
		TypedArray attributes = context.obtainStyledAttributes(attrs,R.styleable.HHAutoRecycleViewPager);
		mXAcept=attributes.getInt(R.styleable.HHAutoRecycleViewPager_x_acept, 0);
		mYAcept=attributes.getInt(R.styleable.HHAutoRecycleViewPager_y_acept, 0);
		mTimeSpan=attributes.getInt(R.styleable.HHAutoRecycleViewPager_time_span, DEFAULT_TIME_SPAN);
		attributes.recycle();
		//实例化ViewPager并添加到容器中
		mViewPager=new ViewPager(context);
		addViewPager();
		//实例化CircleView并添加到容器中
		mCircleView=new HHSelectCircleView(context, attrs);
		FrameLayout.LayoutParams params=new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.bottomMargin=HHDensityUtils.dip2px(context, DEFAULT_BOTTOM_MARGIN);
		params.gravity=Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL;
		addView(mCircleView, params);
		mHandler=new HHWeakHandler<Context>(context)
		{
			
			@Override
			public void processHandlerMessage(Message msg)
			{
				int currentItem = (mViewPager.getCurrentItem()+1)%mPagerAdapter.getCount();
				mViewPager.setCurrentItem(currentItem, true);
			}
		};
	}
	public HHAutoRecycleViewPager(Context context, AttributeSet attrs)
	{
		this(context, attrs,0);
	}
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		if (mXAcept>0&&mYAcept>0)
		{
			int width=getMeasuredWidth();
			int height=(int) ((float)width*mYAcept/mXAcept);
			LayoutParams layoutParams = (LayoutParams) mViewPager.getLayoutParams();
			layoutParams.width=width;
			layoutParams.height=height;
			setMeasuredDimension(width, height);
		
		}
	}
	/**
	 * 添加viewpager到容器中
	 */
	private void addViewPager()
	{
		LayoutParams pagerParams=new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		pagerParams.gravity=Gravity.LEFT|Gravity.TOP;
		addView(mViewPager,pagerParams);
	}
	/**
	 * 设置显示的数据
	 * @param list					显示的数据
	 * @param defaultImageID		显示的默认图片
	 */
	public void setAdapterData(List<? extends HHImageViewPagerImp> list,int defaultImageID)
	{
		if (mPagerAdapter==null)
		{
			mPagerAdapter=new HHAutoRecycleViewPagerAdapter(list, getContext(), defaultImageID);
		}else {
			mPagerAdapter=(HHAutoRecycleViewPagerAdapter) mViewPager.getAdapter();
		}
		mViewPager.setAdapter(mPagerAdapter);
		if (list!=null&&list.size()>1)
		{
			mCircleView.clear();
			mCircleView.addChild(list.size());
			start();
		}
		mViewPager.addOnPageChangeListener(new HHOnPageChangeAdapter()
		{

			@Override
			public void onPageSelected(int arg0)
			{
				mCircleView.setSelectPosition(arg0);
			}
			
		});
		
	}
	/**
	 * 移除圆点的View
	 */
	public void removeCircleView()
	{
		removeView(mCircleView);
	}
	/**
	 * 设置当Item被点击的时候执行的监听器，方法调用结果onItemClick(null, imageView, position, 0)
	 * @param listener
	 */
	public void setOnItemClickListener(OnItemClickListener listener)
	{
		mPagerAdapter.setOnItemClickListener(listener);
	}
	/**
	 * 取消timer
	 */
	public void cancel()
	{
		if (mTimer!=null)
		{
			mTimer.cancel();
		}
	}
	/**
	 * 开启timer
	 */
	public void start()
	{
		if (mTimer!=null)
		{
			mTimer.cancel();
			mTimer=null;
		}
		mTimer=new Timer();
		mTimer.schedule(new TimerTask()
		{
			
			@Override
			public void run()
			{
				mHandler.sendEmptyMessage(0);
			}
		}, mTimeSpan, mTimeSpan);
	}
}
