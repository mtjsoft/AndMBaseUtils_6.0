package com.huahan.hhbaseutils.view.letterview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.huahan.hhbaseutils.R;

public class HHLetterListView extends View {
	OnTouchingLetterChangedListener mOnTouchingLetterChangedListener;

	String[] mLetter = { "*", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J",
			"K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W",
			"X", "Y", "Z" };
	int choose = -1;
	Paint paint = new Paint();
	boolean showBkg = false;
	// 默认的字体的大小
	private int mTextSize = 14;
	// 时候已经初始化变量。一些参数是在绘制的时候初始化的，这些变量只用初始化一次就可以了，没有必要初始化两次
	private boolean mHasInit = false;
	// 文本的宽度
	private float mTextWidth;
	// 每一个索引的高度
	private int mIndexHeight;
	// 文本的高度
	private float mTextHeight;
	// 绘制文本的时候，文本的属性
	private FontMetrics mMetrics;

	/**
	 * 定义了构造函数
	 * 
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public HHLetterListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public HHLetterListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public HHLetterListView(Context context) {
		super(context);
		init();
	}

	/**
	 * 初始化
	 */
	@SuppressWarnings("deprecation")
	private void init() {
		paint.setColor(getResources().getColor(R.color.black_dim));
	}

	/**
	 * 绘制显示的索引
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (showBkg) {
			canvas.drawColor(Color.parseColor("#40000000"));
		}
		if (!mHasInit) {
			paint.setAntiAlias(true);
			int height = getHeight();
			mIndexHeight = height / mLetter.length;
			mMetrics = paint.getFontMetrics();
			mTextHeight = mMetrics.bottom - mMetrics.top;
			mTextWidth = paint.measureText("M");
			while (mTextHeight < mIndexHeight) {
				mTextSize += 2;
				paint.setTextSize(mTextSize);
				mMetrics = paint.getFontMetrics();
				mTextHeight = mMetrics.bottom - mMetrics.top;
				mTextWidth = paint.measureText("M");
			}
			paint.setTextSize(mTextSize - 2);
			mHasInit = true;
		}
		for (int i = 0; i < mLetter.length; i++) {
			canvas.drawText(mLetter[i], (getWidth() - mTextWidth) / 2, i
					* mIndexHeight + (mIndexHeight - mTextHeight) / 2
					- mMetrics.top, paint);
		}
	}

	/**
	 * 重写了触摸事件的分发事件，这个方法的执行是为了寻找是那个索引被选中了
	 */
	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		// 获取触摸事件的动作
		final int action = event.getAction();
		// 获取当前时间的y坐标
		final float y = event.getY();
		final int oldChoose = choose;
		final OnTouchingLetterChangedListener listener = mOnTouchingLetterChangedListener;
		// 判断当前的y坐标属于哪个索引的区域
		final int c = (int) (y / getHeight() * mLetter.length);
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			showBkg = true;
			if (oldChoose != c && listener != null) {
				if (c >= 0 && c < mLetter.length) {
					listener.onTouchingLetterChanged(mLetter[c]);
					choose = c;
					invalidate();
				}
			}
			break;
		case MotionEvent.ACTION_MOVE:
			if (oldChoose != c && listener != null) {
				if (c >= 0 && c < mLetter.length) {
					listener.onTouchingLetterChanged(mLetter[c]);
					choose = c;
					invalidate();
				}
			}
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			showBkg = false;
			choose = -1;
			invalidate();
			break;
		}
		return true;
	}

	/**
	 * 设置选择的索引改变的时候执行的监听器
	 * 
	 * @param onTouchingLetterChangedListener
	 */
	public void setOnTouchingLetterChangedListener(
			OnTouchingLetterChangedListener onTouchingLetterChangedListener) {
		this.mOnTouchingLetterChangedListener = onTouchingLetterChangedListener;
	}

	/**
	 * 定义了一个监听器，这个监听器用户监听用户选择的索引改变的时候执行的代码
	 * 
	 * @author yuan
	 * 
	 */
	public interface OnTouchingLetterChangedListener {
		public void onTouchingLetterChanged(String s);
	}
	/**
	 * 设置显示索引
	 * @param showLetter
	 */
	public void setShowLetter(String[] showLetter) {
		this.mLetter = showLetter;
		invalidate();
	}
}
