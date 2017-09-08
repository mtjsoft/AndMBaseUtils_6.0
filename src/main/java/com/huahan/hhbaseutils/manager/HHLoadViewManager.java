package com.huahan.hhbaseutils.manager;

import android.graphics.drawable.AnimationDrawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.huahan.hhbaseutils.HHViewHelper;
import com.huahan.hhbaseutils.R;
import com.huahan.hhbaseutils.constant.HHConstantParam;
import com.huahan.hhbaseutils.imp.HHLoadViewImp;
import com.huahan.hhbaseutils.imp.HHPageBaseOper;
import com.huahan.hhbaseutils.model.HHLoadState;
import com.huahan.hhbaseutils.model.HHLoadViewInfo;
import com.huahan.hhbaseutils.model.HHLoadViewStateRecord;

import java.util.HashMap;
import java.util.Map;

/**
 * 控制页面加载状态
 * @author yuan
 *
 */
public class HHLoadViewManager
{
	// 正在加载，加载失败和没有数据的View
	private View mLoaddingView;
	// mLoaddingView中显示图片的View
	private ImageView mLoaddingImageView;
	// mLoaddingView中显示文字的View
	private TextView mLoadContentTextView;
	// 显示动画的drawable
	private AnimationDrawable mAnimationDrawable;
	// mLoaddingView点击事件的散列表
	private Map<HHLoadState, HHLoadViewStateRecord> mStateListenerMap=new HashMap<HHLoadState, HHLoadViewStateRecord>();
	//控制各个状态下显示的图片和文字
	private Map<HHLoadState, HHLoadViewInfo> mLoadViewInfoMap=new HashMap<HHLoadState, HHLoadViewInfo>();
	//接口实现，用于获取统一的数据
	private HHPageBaseOper mBasePageOper;
	//接口实现，统一调用页面加载
	private HHLoadViewImp mLoadViewImp;
	
	//构造方法
	public HHLoadViewManager(HHPageBaseOper pageBaseOper,HHLoadViewImp loadViewImp)
	{
		super();
		this.mBasePageOper=pageBaseOper;
		this.mLoadViewImp=loadViewImp;
		//实例化加载的页面
		mLoaddingView = View.inflate(mBasePageOper.getPageContext(), R.layout.hh_include_base_loadding, null);
		mLoaddingImageView = HHViewHelper.getViewByID(mLoaddingView, R.id.hh_img_loadding);
		mLoadContentTextView = HHViewHelper.getViewByID(mLoaddingView, R.id.hh_tv_loadding);
	}
	
	/**
	 * 改变当前的加载状态<br/>
	 * 如果不需要修改显示的图片资源则drawableID传0，如果不需要修改显示状态的文本，stateMsg传null
	 * 
	 * @param state
	 *            当前的加载状态
	 * @param drawableID
	 *            显示状态的图片
	 * @param stateMsg
	 *            显示状态的文字
	 */
	public void changeLoadState(HHLoadState state)
	{
		//还原用户原来设置的点击事件
		bindListener(state);
		switch (state)
		{
		case LOADING:
			changeTipViewInfo(state);
			// 如果mLoaddingImageView显示的背景图是一个AnimationDrawable的实例，则开启动画效果
			if (mLoaddingImageView.getBackground() instanceof AnimationDrawable)
			{
				mAnimationDrawable = (AnimationDrawable) mLoaddingImageView.getBackground();
				mLoaddingImageView.post(new Runnable()
				{

					@Override
					public void run()
					{
						mAnimationDrawable.start();
					}
				});
			}
			mLoadViewImp.onPageLoad();
			break;
		case FAILED:
		case NODATA:
			changeTipViewInfo(state);
			break;
		case SUCCESS:
			//加载成功的时候，判断页面是否添加了主要的视图，如果没有添加，则需要添加主要的视图
			if (mBasePageOper.getBaseView() == null)
			{
				loadBaseView();
			}
			//判断当前的加载的视图在页面中的位置，如果当前的加载视图确实是在页面中存在就把当前的页面从页面中移除掉
			FrameLayout baseContainerLayout = mBasePageOper.getBaseContainerLayout();
			int indexOfChild = baseContainerLayout.indexOfChild(mLoaddingView);
			if (indexOfChild != -1)
			{
				baseContainerLayout.removeViewAt(indexOfChild);
			}
			stopLoaddingAnim();
			break;
		default:
			break;
		}
	}
	/**
	 * 设置加载的View和提示的View显示的提示的图片和文本
	 * @param state					加载的状态
	 * @param drawableID			显示的图片
	 * @param stateMsg				显示的文本
	 */
	public void setLoaddingViewInfo(HHLoadState state,int drawableID,String stateMsg)
	{
		//在Map查找对应状态保存的加载视图的信息，如果查找到的信息为null，则需要重新创建一个对象，并且把该对象
		//保存到Map集合中，如果该对象存在的话，则需要重新设置该对象的信息
		HHLoadViewInfo loadViewInfo = mLoadViewInfoMap.get(state);
		if (loadViewInfo==null)
		{
			loadViewInfo=new HHLoadViewInfo(stateMsg,drawableID);
			mLoadViewInfoMap.put(state, loadViewInfo);
		}else {
			loadViewInfo.setDrawableID(drawableID);
			loadViewInfo.setMsgInfo(stateMsg);
		}
		
	}
	/**
	 * 移除加载的View的提示信息
	 * @param state 加载的状态
	 */
	public void removeLoadingViewInfo(HHLoadState state)
	{
		if (mLoadViewInfoMap!=null)
		{
			mLoadViewInfoMap.remove(state);
		}
	}
	/**
	 * 绑定监听器
	 * 
	 * @param state
	 *            加载的状态
	 * @param drawableID
	 *            显示的图片的ID
	 * @param stateMsg
	 *            显示的提示的信息
	 */
	private void bindListener(final HHLoadState state)
	{
		switch (state)
		{
		//当当前的状态是长在加载或者加载成功的时候，取消所有控件的点击事件
		case LOADING:
		case SUCCESS:
			mLoaddingView.setOnClickListener(null);
			mLoaddingImageView.setOnClickListener(null);
			break;
		case FAILED:
			//当获取数据失败的时候，判断用户时候给加载的页面设置了相应的点击事件，如果设置了点击事件，就使用
			//用户自己的点击事件，如果没有设置点击事件，则添加默认的点击事件，默认的点击事件是点击以后重新显示
			//正在加载的页面并且重新加载数据
			if (!bindChildListener(state))
			{
				LoadClickListener listener = new LoadClickListener();
				mLoaddingView.setOnClickListener(listener);
				mLoaddingImageView.setOnClickListener(listener);
			}
			break;
		case NODATA:
			//当获取到数据，但是不需要显示的时候判断用户时候设置了相应的点击事件，如果用户设置了点击事件，则使用
			//用户设置的点击事件，如果没有设置点击事件，则使用默认的事件，默认的事件就是不给控件添加任何事件
			if (!bindChildListener(state))
			{
				mLoaddingImageView.setOnClickListener(null);
				mLoaddingView.setOnClickListener(null);
			}
			break;

		default:
			break;
		}
	}
	/**
	 * 绑定监听器
	 * 
	 * @param state
	 *            加载状态
	 * @return		true代表给控件绑定了点击事件，false表示没有给控件绑定点击事件
	 */
	private boolean bindChildListener(HHLoadState state)
	{
		//判断时候给当前的状态添加了点击事件，如果添加了点击事件，就重新给控件绑定特定的点击事件
		if (mStateListenerMap != null && mStateListenerMap.get(state) != null)
		{
			//获取特定状态绑定的状态
			HHLoadViewStateRecord record = mStateListenerMap.get(state);
			//判断是不是只是给ImageView设置了点击事件，并且根据设置的状态给控件添加点击事件
			if (record.isJustImageView())
			{
				mLoaddingImageView.setOnClickListener(record.getOnClickListener());
				mLoaddingView.setOnClickListener(null);
			} else
			{
				mLoaddingImageView.setOnClickListener(null);
				mLoaddingView.setOnClickListener(record.getOnClickListener());
			}
			return true;
		}
		return false;
	}
	/**
	 * 暂停当前的加载动画
	 */
	private void stopLoaddingAnim()
	{
		if (mAnimationDrawable != null && mAnimationDrawable.isRunning())
		{
			mAnimationDrawable.stop();
		}
	}
	/**
	 * 给LoaddingView设置监听器。对于给Load和Success状态设置的监听器是无效的，所以不要给这两个状态设置监听器<br/>
	 * 设置的监听器会在调用changeLoadState方法时进行绑定，不是直接在该方法内直接绑定
	 * 
	 * @param state
	 *            加载状态
	 * @param listener
	 *            点击的监听器
	 * @param justImageView
	 *            是否是给显示图片的ImageView设置监听器，默认情况下是整个区域
	 */
	public void setLoaddingViewClickListener(HHLoadState state, OnClickListener listener, boolean justImageView)
	{
		if (state == HHLoadState.LOADING || state == HHLoadState.SUCCESS)
		{
			return;
		}
		HHLoadViewStateRecord record = mStateListenerMap.get(state);
		if (record == null)
		{
			record = new HHLoadViewStateRecord(listener, justImageView);
			mStateListenerMap.put(state, record);
		} else
		{
			record.setJustImageView(justImageView);
			record.setOnClickListener(listener);
		}
	}
	/**
	 * 用于加载显示的视图，也就是initView中加载的视图
	 */
	private void loadBaseView()
	{

		View initView = mBasePageOper.initView();
		mBasePageOper.setBaseView(initView);
		mBasePageOper.initValues();
		mBasePageOper.initListeners();
		stopLoaddingAnim();

	}
	/**
	 * 设置提示的视图显示的内容
	 * 
	 * @param drawableID
	 *            显示的图片的ID
	 * @param stateMsg
	 *            显示的文本的内容
	 * @param defaultDrawable
	 *            默认显示的图片
	 * @param defaultTip
	 *            默认显示的文本
	 */
	private void changeTipViewInfo(HHLoadState state)
	{
		//首先需要停止当前动画效果
		stopLoaddingAnim();
		HHLoadViewInfo hhLoadViewInfo = mLoadViewInfoMap.get(state);
		//定义变量，保存显示的图片资源和显示的文本信息
		int drawableID=0;
		String msg="";
		//用户没有为单独的页面设置显示的图片和现实的文本
		if (hhLoadViewInfo==null)
		{
			HHLoadViewInfo loadViewInfo = HHConstantParam.loadViewMap.get(state);
			//用户没有为整个App设置显示的图片和文本
			if (loadViewInfo==null)
			{
				//设置默认的图片和文本
				switch (state)
				{
				case FAILED:
					drawableID=R.drawable.hh_loadding_error;
					msg=getString(R.string.hh_load_failed);
					break;
				case NODATA:
					drawableID=R.drawable.hh_loadding_no_data;
					msg=getString(R.string.hh_no_data);
					break;
				case LOADING:
					drawableID=R.drawable.hh_loadding_anim;
					msg=getString(R.string.hh_loading);
					break;
				default:
					break;
				}
			}else {
				//用户为整个App设置了显示的文本和图片
				drawableID=loadViewInfo.getDrawableID();
				msg=loadViewInfo.getMsgInfo();
			}
		}else {
			//用户为单独的页面设置了显示的文本和显示的图片
			drawableID=hhLoadViewInfo.getDrawableID();
			msg=hhLoadViewInfo.getMsgInfo();
		}
		//设置控件显示的图片和文本
		mLoaddingImageView.setBackgroundResource(drawableID);
		mLoadContentTextView.setText(msg);
		//判断加载的视图是不是已经加载到了页面当中，如果没有加载到页面当中，就把加载的视图添加到页面中
		FrameLayout baseContainerLayout = mBasePageOper.getBaseContainerLayout();
		int indexOfChild = baseContainerLayout.indexOfChild(mLoaddingView);
		if (indexOfChild == -1)
		{
			mBasePageOper.addViewToContainer(mLoaddingView);
		}
	}
	/**
	 * 根据ID获取字符串
	 * @param resID		资源的ID
	 * @return
	 */
	private String getString(int resID)
	{
		return mBasePageOper.getPageContext().getString(resID);
	}
	/**
	 * 加载失败的时候点击执行的默认的监听器
	 * @author yuan
	 */
	private class LoadClickListener implements OnClickListener
	{
	
		public LoadClickListener()
		{
			super();
		}
		@Override
		public void onClick(View v)
		{
			changeLoadState(HHLoadState.LOADING);
		}

	}
	
}
