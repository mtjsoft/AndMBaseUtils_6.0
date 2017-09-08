package com.huahan.hhbaseutils.dialog;

import android.animation.Animator;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.LinearLayout;

import com.huahan.hhbaseutils.HHLog;
import com.huahan.hhbaseutils.HHScreenUtils;

public abstract class HHBaseDialog<T extends HHBaseDialog<T>> extends Dialog
{
	/**
	 * 打印日志信息的tag
	 */
	private String mLogTag;
	/**
	 * 保存上下文信息
	 */
	private Context mContext;
	/**
	 * 设备的像素密度
	 */
	protected DisplayMetrics mDisplayMetrics;
	/**
	 * 设置点击对话框以外区域,是否允许对话框消失dismiss
	 */
	protected boolean mCancelOutSize;
	/**
	 * 宽度比例
	 */
	protected float mWidthScale = 1;
	/**
	 * 高度比例
	 */
	protected float mHeightScale;
	/**
	 * 对话框显示的动画效果
	 */
	private HHBaseAnimatorSet mShowAnimatorSet;
	/**
	 * 对话框消失的动画效果
	 */
	private HHBaseAnimatorSet mDismissAnimatorSet;
	/**
	 * 对话框内View的顶层容器
	 */
	protected LinearLayout mTopLayout;
	/**
	 * 用于控制对话框的高度，
	 */
	protected LinearLayout mControlLayout;
	/**
	 * 显示的动画是否正在执行
	 */
	private boolean mIsShowAnimRun;
	/**
	 * 消失的动画是否正在执行
	 */
	private boolean mIsDismissAnimRun;
	/**
	 * 最大高度
	 */
	protected float mMaxHeight;
	/**
	 * 是否按照PopupWindow的样式来显示对话框
	 */
	private boolean mIsPopupStyle;

	/**
	 * 构造函数
	 * 
	 * @param context
	 */
	public HHBaseDialog(Context context)
	{
		super(context);
		setDialogTheme();
		this.mContext = context;
		this.mLogTag = this.getClass().getSimpleName();
		HHLog.i(mLogTag, "dialog constructor");
	}

	public HHBaseDialog(Context context, boolean isPopupStyle)
	{
		this(context);
		this.mIsPopupStyle = isPopupStyle;
	}

	private void setDialogTheme()
	{
		// 不现实标题
		requestWindowFeature(Window.FEATURE_NO_TITLE);// android:windowNoTitle
		// 设置背景
		getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));// android:windowBackground
		// 设置背景显示阴影
		getWindow().addFlags(LayoutParams.FLAG_DIM_BEHIND);// android:backgroundDimEnabled默认是true的
	}

	// 当创建Dialog的时候执行的事件
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		// 获取对话框显示的最大的高度
		mDisplayMetrics = mContext.getResources().getDisplayMetrics();
		mMaxHeight = mDisplayMetrics.heightPixels - HHScreenUtils.getStatusBarHeight(mContext);
		// 实例化对话框显示的跟布局
		mTopLayout = new LinearLayout(mContext);
		// 设置内容居中
		mTopLayout.setGravity(Gravity.CENTER);
		// 实例化内容的容器
		mControlLayout = new LinearLayout(mContext);
		// 设置内容的垂直排列的
		mControlLayout.setOrientation(LinearLayout.VERTICAL);
		// 添加View
		mControlLayout.addView(onCreateView());
		// 设置对话框的样式,如果是popupwindow的样式的话，就设置对话框的大小是包裹内容
		//如果不是popupwindow的样式话，就设置对话框的大小是屏幕的大小减去状态栏的高度
		if (mIsPopupStyle)
		{
			setContentView(mTopLayout, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		} else
		{
			setContentView(mTopLayout, new ViewGroup.LayoutParams(mDisplayMetrics.widthPixels, (int) mMaxHeight));
		}
		//设置点击对话框的外边的时候就隐藏对话框
		setCanceledOnTouchOutside(true);
		//给最外层设置一个点击事件，当设置点击外边隐藏对话框的时候，在隐藏对话框
		mTopLayout.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				
				if (mCancelOutSize)
				{
					dismiss();
				}
			}
		});

	}
	@Override
	public void onAttachedToWindow()
	{
		
		super.onAttachedToWindow();
		//当dialog被附加到window上时，执行的事件
		//----------
		setUiBeforeShow();
		//计算对话框中View显示的大小
		int width=0;
        if (mWidthScale <=0)
        {
            width = ViewGroup.LayoutParams.WRAP_CONTENT;
        } else
        {
        	if (mWidthScale>=1)
			{
				mWidthScale=1;
			}
            width = (int) (mDisplayMetrics.widthPixels * mWidthScale);
        }
        int height=0;
        if (mHeightScale <=0)
        {
            height = ViewGroup.LayoutParams.WRAP_CONTENT;
        } else if (mHeightScale >=1)
        {
            height = ViewGroup.LayoutParams.MATCH_PARENT;
        } else
        {
            height = (int) (mMaxHeight * mHeightScale);
        }
        //设置内容显示的大小
        mControlLayout.setLayoutParams(new LinearLayout.LayoutParams(width, height));
        //如果对话框显示的动画存在的话,执行动画，否则的话就回复控件的基本状态
        if (mShowAnimatorSet != null)
        {
            mShowAnimatorSet.listener(new HHBaseAnimatorSet.AnimatorListener()
            {
                @Override
                public void onAnimationStart(Animator animator)
                {
                    mIsShowAnimRun = true;
                }

                @Override
                public void onAnimationRepeat(Animator animator)
                {
                }

                @Override
                public void onAnimationEnd(Animator animator)
                {
                    mIsShowAnimRun = false;
                }

                @Override
                public void onAnimationCancel(Animator animator)
                {
                    mIsShowAnimRun = false;
                }
            }).playOn(mControlLayout);
        } else
        {
            HHBaseAnimatorSet.reset(mControlLayout);
        }
	}
	@Override
    public void dismiss()
    {
		//如果对话框隐藏的动画存在的话就执行隐藏的动画，否则的话就显示默认的隐藏的操作
        if (mDismissAnimatorSet != null)
        {
            mDismissAnimatorSet.listener(new HHBaseAnimatorSet.AnimatorListener()
            {
                @Override
                public void onAnimationStart(Animator animator)
                {
                    mIsDismissAnimRun = true;
                }

                @Override
                public void onAnimationRepeat(Animator animator)
                {
                }

                @Override
                public void onAnimationEnd(Animator animator)
                {
                    mIsDismissAnimRun = false;
                    superDismiss();
                }

                @Override
                public void onAnimationCancel(Animator animator)
                {
                	mIsDismissAnimRun = false;
                    superDismiss();
                }
            }).playOn(mControlLayout);
        } else
        {
            superDismiss();
        }
    }

    /**
     * 执行对话框
     */
    public void superDismiss()
    {
        super.dismiss();
    }
	@Override
	public void setCanceledOnTouchOutside(boolean cancel)
	{
		this.mCancelOutSize=cancel;
		super.setCanceledOnTouchOutside(cancel);
	}

    /**
     * 显示对话框
     * @param animStyle			对话框显示的时候的动画样式
     */
    public void show(int animStyle)
    {
        Window window = getWindow();
        window.setWindowAnimations(animStyle);
        show();
    }
    /**
     * 设置对话框显示的位置，只有在显示为popupwindow样式的时候才有用
     * @param gravity			设置Gravity
     * @param x					偏移量x
     * @param y					偏移量y
     */
    public void showAtLocation(int gravity, int x, int y)
    {
        if (mIsPopupStyle)
        {
            Window window = getWindow();
            LayoutParams params = window.getAttributes();
            window.setGravity(gravity);
            params.x = x;
            params.y = y;
        }
        show();
    }
    /**
     *设置对话框显示的位置，默认显示位置是屏幕的左上角
     * @param x			x轴的偏移量
     * @param y			y轴的偏移量
     */
    public void showAtLocation(int x, int y)
    {
        int gravity = Gravity.LEFT | Gravity.TOP;
        showAtLocation(gravity, x, y);
    }
    /**
     * 设置是否显示背景的阴影
     * @param isDimEnabled		是否启用背景的阴影效果
     * @return
     */
    @SuppressWarnings("unchecked")
	public T dimEnabled(boolean isDimEnabled)
    {
        if (isDimEnabled)
        {
            getWindow().addFlags(LayoutParams.FLAG_DIM_BEHIND);
        } else
        {
            getWindow().clearFlags(LayoutParams.FLAG_DIM_BEHIND);
        }
        return (T)this;
    }
    /**
     * 设置宽度的比例
     * @param widthScale		宽度的比例
     * @return
     */
    @SuppressWarnings("unchecked")
	public T widthScale(float widthScale)
    {
        this.mWidthScale = widthScale;
        return (T)this;
    }
    /**
     * 设置显示的时候显示的动画效果
     * @param showAnim			显示的动画
     * @return
     */
    @SuppressWarnings("unchecked")
	public T showAnim(HHBaseAnimatorSet showAnim)
    {
        this.mShowAnimatorSet = showAnim;
        return (T) this;
    }
    /**
     * 设置对话框隐藏的时候显示的动画效果
     * @param dismissAnim		隐藏的动画
     * @return
     */
    @SuppressWarnings("unchecked")
	public T dismissAnim(HHBaseAnimatorSet dismissAnim)
    {
        this.mDismissAnimatorSet = dismissAnim;
        return (T) this;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev)
    {
    	//如果动画正在执行则不传递任何触摸事件
        if (mIsDismissAnimRun || mIsShowAnimRun)
        {
            return true;
        }
        return super.dispatchTouchEvent(ev);
    }
    @Override
    public void onBackPressed()
    {
    	//当动画正在执行的时候，点击返回的时候不执行任何事件
        if (mIsDismissAnimRun || mIsShowAnimRun)
        {
            return;
        }
        super.onBackPressed();
    }
    /**
     * 设置高度的比例
     * @param heightScale	高度的比例
     * @return
     */
    @SuppressWarnings("unchecked")
	public T heightScale(float heightScale)
    {
        this.mHeightScale = heightScale;
        return (T) this;
    }
	/**
	 * 创建View
	 * 
	 * @return
	 */
	public abstract View onCreateView();

	/**
	 * 设置UI在dialog显示之前
	 */
	public abstract void setUiBeforeShow();
	/**
	 * 获取对话框中显示的View所在的Layout
	 * @return
	 */
	protected LinearLayout getControlLayout()
	{
		return mControlLayout;
	}
	/**
	 * 获取当前的Dialog显示的跟视图
	 * @return
	 */
	protected LinearLayout getTopLayout()
	{
		return mTopLayout;
	}
	
}
