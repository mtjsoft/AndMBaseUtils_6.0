package com.huahan.hhbaseutils.ui;

import android.graphics.Color;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.huahan.hhbaseutils.HHDensityUtils;
import com.huahan.hhbaseutils.HHViewHelper;
import com.huahan.hhbaseutils.R;
import com.huahan.hhbaseutils.adapter.HHImageBrowerAdapter;
import com.huahan.hhbaseutils.imp.HHSmallBigImageImp;
import com.huahan.hhbaseutils.model.HHFabActionModel;
import com.huahan.hhbaseutils.view.fabtoolbar.FABToolbarLayout;
import com.huahan.utils.view.scaleimage.ScaleViewPager;

import java.util.List;

public abstract class HHImageBrowerActivity extends HHBaseActivity implements OnClickListener
{

	/**
	 * 显示的图片的列表,参数FLAG_IMAGE_LIST表示的图片的列表的大小不允许为null或者size为0；
	 * 如果传入的参数FLAG_IMAGE_POSITION小于0或者大于列表的大小，则自动为0，且默认为0；
	 */
	public static final String FLAG_IMAGE_LIST="flag_image_list";
	
	/**
	 * 首次进入的时候应该显示那张图片，位置从0开始
	 */
	public static final String FLAG_IMAGE_POSITION="flag_image_position";
	/**
	 * 默认图片的ID
	 */
	public static final String FLAG_DEFAULT_IMAGE_ID="flag_default_image_id";
	/**
	 * 不是Wifi的时候是否加载图片，默认在不是Wifi的情况下不加载图片
	 */
	public static final String FLAG_LOAD_IMAGE_NOT_WIFI="flag_load_image_not_wifi";
	//保存了图片的信息列表
	private List<? extends HHSmallBigImageImp> mImageList;
	//显示图片的ViewPager
	private ScaleViewPager mViewPager;
	//显示图片位置的TextView
	private TextView mPositionTextView;
	/**
	 * FloatButton显示的图片
	 */
	
	private FloatingActionButton mFloatingActionButton;
	private LinearLayout mToolbarLayout;
	private FABToolbarLayout mFabToolbarLayout;
	@Override
	public View initView()
	{
		View view=View.inflate(this, R.layout.hh_activity_image_brower, null);
		mViewPager=HHViewHelper.getViewByID(view, R.id.hh_vp_image_brower);
		mPositionTextView=HHViewHelper.getViewByID(view, R.id.hh_tv_brower_position);
		mFloatingActionButton=HHViewHelper.getViewByID(view, R.id.hh_id_fab_float_button);
		mToolbarLayout=HHViewHelper.getViewByID(view, R.id.hh_id_fab_toolbar);
		mFabToolbarLayout=HHViewHelper.getViewByID(view, R.id.hh_fab_image_brower);
		return view;
	}
	public ViewPager getViewPager()
	{
		return mViewPager;
	}
	@SuppressWarnings("unchecked")
	@Override
	public void initValues()
	{
		mFloatingActionButton.setImageResource(getFloatActionButtonImageID());
		mImageList=(List<? extends HHSmallBigImageImp>) getIntent().getSerializableExtra(FLAG_IMAGE_LIST);
		int defaultImageID=getIntent().getIntExtra(FLAG_DEFAULT_IMAGE_ID, R.drawable.hh_default_image);
		int position=getIntent().getIntExtra(FLAG_IMAGE_POSITION, 0);
		boolean loadImageNotWifi=getIntent().getBooleanExtra(FLAG_LOAD_IMAGE_NOT_WIFI, false);
		if (mImageList==null||mImageList.size()==0)
		{
			throw new RuntimeException("please check flag FLAG_IMAGE_LIST,and the image list can not be null or size is 0");
		}
		if (position<0||position>mImageList.size()-1)
		{
			position=0;
		}
		mViewPager.setAdapter(new HHImageBrowerAdapter(this,defaultImageID,loadImageNotWifi));
		mViewPager.setCurrentItem(position,true);
		List<HHFabActionModel> list = getActionTextsAndImages();
		if (list!=null&&list.size()!=0)
		{
			for (int i = 0; i < list.size(); i++)
			{
				View view=getItemStyle(i);
				if (view instanceof TextView)
				{
					TextView textView=(TextView) view;
					textView.setCompoundDrawablesWithIntrinsicBounds(0, list.get(i).getImageID(), 0, 0);
					String title=list.get(i).getTitle();
					textView.setText(TextUtils.isEmpty(title)?"":title);
					textView.setGravity(Gravity.CENTER);
				}else if (view instanceof ImageView) 
				{
					ImageView imageView=(ImageView) view;
					imageView.setScaleType(ScaleType.CENTER_INSIDE);
					imageView.setImageResource(list.get(i).getImageID());
				}
				view.setId(i+1);
				view.setOnClickListener(new OnClickListener()
				{
					
					@Override
					public void onClick(View v)
					{
						onActionItemClicked(v.getId());
					}
				});
				LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT);
				params.weight=1;
				mToolbarLayout.addView(view,params);
			}
		}
		
		
	}
	public abstract void onActionItemClicked(int position);
	@Override
	public void initListeners()
	{
		
	}

	@Override
	public void processHandlerMsg(Message msg)
	{
		
	}
	/**
	 * 返回当前页面显示的图片的列表
	 * @return
	 */
	public List<? extends HHSmallBigImageImp> getImageList()
	{
		return mImageList;
	}

	@Override
	public void onClick(View v)
	{
		Log.i("chenyuan", "点击事件======");
		hide();
	}

	public void hide()
	{
		if (mFabToolbarLayout.isToolbar())
		{
			mFabToolbarLayout.hide();
		}
	}
	protected int getFloatActionButtonImageID()
	{
		return R.drawable.abc_ic_menu_moreoverflow_mtrl_alpha;
	}
	/**
	 * 获取ActionItem的集合
	 * @return
	 */
	protected abstract List<HHFabActionModel> getActionTextsAndImages();
	/**
	 * 设置ActionItem显示的样式
	 * @return
	 */
	protected View getItemStyle(int position)
	{
		TextView textView=new TextView(this);
		textView.setTextSize(16);
		textView.setTextColor(Color.WHITE);
		textView.setCompoundDrawablePadding(HHDensityUtils.dip2px(this, 3));
		return textView;
	}
	/**
	 * 判断页面点击是否关闭当前页面
	 * @return
	 */
	public boolean isClickFinish()
	{
		List<HHFabActionModel> list = getActionTextsAndImages();
		if (list!=null&&list.size()>0)
		{
			return false;
		}
		return true;
	}
	
	

}
