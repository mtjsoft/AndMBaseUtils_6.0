package com.huahan.hhbaseutils.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.support.annotation.IdRes;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.huahan.hhbaseutils.HHDensityUtils;
import com.huahan.hhbaseutils.HHLog;
import com.huahan.hhbaseutils.HHPagerTask;
import com.huahan.hhbaseutils.R;

import java.security.InvalidParameterException;

/**
 * 显示小圆点，表示当前选择的位置
 *
 * @author yuan
 */
@SuppressLint("Recycle")
public class HHSelectCircleView extends RadioGroup {

    private static final String tag = HHSelectCircleView.class.getSimpleName();
    //默认的子控件的大小，单位是dp
    private static final int DEAFULT_CHILD_SIZE = 8;
    //默认情况下子控件之间的间距，单位是dp
    private static final int DEFAULT_CHILD_MARGIN = 8;
    //默认显示的颜色值
    private static final int DEFAULT_NORMAL_COLOR = Color.WHITE;
    //默认选中显示的颜色值
    private static final int DEFAULT_SELECT_COLOR = Color.GRAY;
    //子控件显示的宽度
    private int mChildWidth = 0;
    //子控件显示的高度
    private int mChildHeight = 0;
    //两个子控件之间的间距
    private int mChildMargin = 0;
    //正常情况下显示的颜色
    private int mNormalColor = DEFAULT_NORMAL_COLOR;
    //选中的时候现实的颜色
    private int mSelectColor = DEFAULT_SELECT_COLOR;
    //正常情况下显示的drawable
    private Drawable mNormalDrawable = null;
    //选中情况下显示的drawable
    private Drawable mSelectDrawable = null;
    //是否显示圆点
    private boolean mIsCircle = true;
    //是否是自定义
    private boolean mIsCustom = false;
    //自定义选中情况下显示的drawable
    private int mSelectCustomDrawable;
    //是否自动切换viewpager，默认false
    private ViewPager viewPager;
    private HHPagerTask pagerTask;

    public HHSelectCircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(LinearLayout.HORIZONTAL);
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.HHSelectCircleView);
        mChildWidth = attributes.getDimensionPixelSize(R.styleable.HHSelectCircleView_child_width, HHDensityUtils.dip2px(context, DEAFULT_CHILD_SIZE));
        mChildHeight = attributes.getDimensionPixelSize(R.styleable.HHSelectCircleView_child_height, HHDensityUtils.dip2px(context, DEAFULT_CHILD_SIZE));
        mChildMargin = attributes.getDimensionPixelSize(R.styleable.HHSelectCircleView_child_margin, HHDensityUtils.dip2px(context, DEFAULT_CHILD_MARGIN));
        mIsCircle = attributes.getBoolean(R.styleable.HHSelectCircleView_is_circle, true);
        mIsCustom = attributes.getBoolean(R.styleable.HHSelectCircleView_is_custom, false);
        mNormalColor = attributes.getColor(R.styleable.HHSelectCircleView_normal_color, DEFAULT_NORMAL_COLOR);
        mSelectColor = attributes.getColor(R.styleable.HHSelectCircleView_select_color, DEFAULT_SELECT_COLOR);
        mSelectCustomDrawable = attributes.getInt(R.styleable.HHSelectCircleView_custom_drawable, R.drawable.hh_selector_custom);
        HHLog.i(tag, "child width:" + mChildWidth);
        attributes.recycle();
        mNormalDrawable = getSpecialDrawable(true);
        mSelectDrawable = getSpecialDrawable(false);
    }

    private Drawable getSpecialDrawable(boolean isNormal) {
        if (mIsCircle) {
            int width = Math.min(mChildHeight, mChildWidth);
            RoundedBitmapDrawable create = RoundedBitmapDrawableFactory.create(getResources(), getCircleDrawableBitmap(isNormal, width));
            create.setCircular(true);
            return create;
        } else {
            ColorDrawable drawable = new ColorDrawable(isNormal ? mNormalColor : mSelectColor);
            drawable.setBounds(0, 0, mChildWidth, mChildHeight);
            return drawable;
        }
    }

    private Bitmap getCircleDrawableBitmap(boolean isNormal, int width) {
        Bitmap bitmap = Bitmap.createBitmap(width, width, Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(isNormal ? mNormalColor : mSelectColor);
        return bitmap;
    }

    public HHSelectCircleView(Context context) {
        super(context);
        setOrientation(LinearLayout.HORIZONTAL);
        initView();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        mChildHeight = HHDensityUtils.dip2px(getContext(), DEAFULT_CHILD_SIZE);
        mChildWidth = HHDensityUtils.dip2px(getContext(), DEAFULT_CHILD_SIZE);
        mChildMargin = HHDensityUtils.dip2px(getContext(), DEFAULT_CHILD_MARGIN);
        mNormalDrawable = getSpecialDrawable(true);
        mSelectDrawable = getSpecialDrawable(false);
    }

    /**
     * 设置是否自定义
     */
    public void setIsCustom(boolean b) {
        mIsCustom = b;
    }

    /**
     * 添加子View
     *
     * @param count 添加的数量
     */
    @SuppressWarnings("deprecation")
    public void addChild(int count) {
        if (count < 1) {
            throw new InvalidParameterException("count must be biger than 0");
        }
        for (int index = 0; index < count; ++index) {
            RadioButton button = new RadioButton(getContext());
            button.setId(index);
            button.setButtonDrawable(new ColorDrawable(Color.TRANSPARENT));
            if (mIsCustom) {
                button.setBackgroundResource(mSelectCustomDrawable);
            } else {
                StateListDrawable drawable = new StateListDrawable();
                drawable.addState(new int[]{android.R.attr.state_checked}, mSelectDrawable);
                drawable.addState(new int[]{}, mNormalDrawable);
                button.setBackgroundDrawable(drawable);
            }
            RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(mChildWidth, mChildHeight);
            if (index != 0) {
                params.leftMargin = mChildMargin;
            }
            addView(button, params);
        }
    }

    /**
     * 设置选中的位置
     *
     * @param position
     */
    public void setSelectPosition(int position) {
        if (getChildCount() > position) {
            for (int i = 0; i < getChildCount(); i++) {
                RadioButton button = (RadioButton) getChildAt(i);
                if (i == position) {
                    RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(mIsCustom ? mChildWidth * 2 : mChildWidth, mChildHeight);
                    if (i != 0) {
                        params.leftMargin = mChildMargin;
                    }
                    button.setChecked(true);
                    button.setLayoutParams(params);
                } else {
                    RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(mChildWidth, mChildHeight);
                    if (i != 0) {
                        params.leftMargin = mChildMargin;
                    }
                    button.setLayoutParams(params);
                }
            }
        }
    }

    /**
     * 设置自动切换
     *
     * @param automaticTime 自动切换时间
     */
    public void setAutomatic(int automaticTime) {
        if (viewPager != null && viewPager.getAdapter().getCount() > 1) {
            pagerTask = new HHPagerTask(automaticTime, viewPager.getAdapter().getCount(), viewPager);
            pagerTask.startChange();
        }
    }

    /**
     * 清除所有
     */
    public void clear() {
        removeAllViews();
    }


    public void setViewPager(final ViewPager viewPager) {
        if (viewPager != null) {
            this.viewPager = viewPager;
            final int count = viewPager.getAdapter().getCount();
            if (count > 0) {
                addChild(count);
            } else {
                clear();
            }
            setSelectPosition(viewPager.getCurrentItem());
            this.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                    if (count > i) {
                        viewPager.setCurrentItem(i);
                    }
                }
            });
            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                }

                @Override
                public void onPageSelected(int position) {
                    setSelectPosition(position);
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                }
            });
        }
    }
}
