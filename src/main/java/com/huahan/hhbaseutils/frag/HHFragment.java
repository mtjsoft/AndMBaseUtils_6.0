package com.huahan.hhbaseutils.frag;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.huahan.hhbaseutils.HHLog;
import com.huahan.hhbaseutils.HHViewHelper;
import com.huahan.hhbaseutils.R;
import com.huahan.hhbaseutils.imp.HHPageBaseOper;
import com.huahan.hhbaseutils.imp.HHTopViewManagerImp;
import com.huahan.hhbaseutils.manager.HHFragDefaulTopManager;
import com.huahan.hhbaseutils.manager.HHFragTopManager;
import com.huahan.hhbaseutils.manager.HHUiTopManager.TopMode;
import com.huahan.hhbaseutils.model.HHWeakHandler;

/**
 * 不建议继承这个类
 * @author yuan
 *
 */
public abstract class HHFragment extends Fragment implements HHPageBaseOper
{

	private static final String tag=HHFragment.class.getSimpleName();
	//定义的是整个页面的根布局
	private RelativeLayout mParentLayout;
	//定义的是头部和底部
	private LinearLayout mTopLayout,mBottomLayout;
	//定义的是中间显示的内容
	private FrameLayout mContainerLayout;
	//页面显示的View
	private View mBaseView;
	//保存页面数据
	private Bundle mSavedInstanceBundle;
	//表示的是Fragment是否已经执行了onDestory方法
	private boolean mIsDestory=false;
	private HHFragTopManager mTopManager;
	
	//当Fragment创建的时候执行的方法
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		mSavedInstanceBundle=savedInstanceState;
	}
	//当Activity创建的时候执行的方法
	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		mSavedInstanceBundle=savedInstanceState;
	}
	@SuppressLint("InflateParams") @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view=inflater.inflate(R.layout.hh_activity_main, null);
		initOther(view);
		//头部管理器的初始化必须放在onCreateView方法内，内部可能会改变UI显示
		mTopManager=new HHFragTopManager(this);
		initTopLayout();
		return view;
	}
	protected void initTopLayout()
	{
		mTopManager.showTopView(TopMode.DEFAULT);
	}
	
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		mIsDestory=true;
	}
	/**
	 * 获取可用的头部管理器，需要在OncreateView中调用
	 * @return
	 */
	public HHTopViewManagerImp getAvalibleTopManager()
	{
		return mTopManager.getAvalibleManager();
	}
	/**
	 * 判断当前的Fragment时候已经被销毁
	 * @return
	 */
	protected boolean isDestory()
	{
		return mIsDestory;
	}
	/**
	 * 设置被销毁的状态
	 * @param state			是否被销毁
	 */
	protected void setDestoryState(boolean state)
	{
		mIsDestory=state;
	}
	/**
	 * 在onCreate中调用，用于初始化整个页面的基本结构<br/>
	 */
	protected void initOther(View view)
	{
		//获取基本架构的基本控件
		mParentLayout=HHViewHelper.getViewByID(view, R.id.hh_rl_base_parent);
		mBottomLayout=HHViewHelper.getViewByID(view, R.id.hh_ll_base_bottom);
		mTopLayout=HHViewHelper.getViewByID(view, R.id.hh_ll_base_top);
		mContainerLayout=HHViewHelper.getViewByID(view, R.id.hh_fl_base_container);
	}
	protected RelativeLayout getBaseParentLayout()
	{
		return mParentLayout;
	}
	/**
	 * 获取当前页面显示的上边的布局
	 * @return
	 */
	public LinearLayout getBaseTopLayout()
	{
		return mTopLayout;
	}
	/**
	 * 获取当前页面显示的下边的布局
	 * @return
	 */
	protected LinearLayout getBaseBottomLayout()
	{
		return mBottomLayout;
	}
	/**
	 * 把创建的View添加到显示内容的中间容器.<br/>
	 * @param index				插入的位置
	 * @param view				插入的视图
	 */
	protected void addViewToContainer(int index,View view)
	{
		mContainerLayout.addView(view, index,new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
	}
	/**
	 * 获取保存的Fragment的一些信息，当Fragment被重新创建的时候，保存的信息可以通过该方法获取保存
	 * 的信息的集合
	 * @return
	 */
	protected Bundle getSavedInstanceBundle()
	{
		return mSavedInstanceBundle;
	}
	@Override
	public void setBaseView(View view)
	{
		if (mBaseView!=null)
		{
			mContainerLayout.removeView(mBaseView);
		}
		mBaseView=view;
		addViewToContainer(0,view);
	}
	@Override
	public Context getPageContext()
	{
		return getContext();
	}
	@Override
	public View getBaseView()
	{
		
		return mBaseView;
	}
	
	@Override
	public FrameLayout getBaseContainerLayout()
	{
		return mContainerLayout;
	}
	@Override
	public void addViewToContainer(View view)
	{
		addViewToContainer(-1,view);
	}
	/**
	 * 获取该Fragment绑定的Handler对象
	 * @return
	 */
	public Handler getHandler()
	{
		return handler;
	}
	/**
	 * 发送handler消息
	 * @param what
	 */
	public void sendHandlerMessage(int what)
	{
		HHLog.i(tag,"send empty msg");
		handler.sendEmptyMessage(what);
	}
	/**
	 * 发送Handler消息
	 * @param msg
	 */
	public void sendHandlerMessage(Message msg)
	{
		handler.sendMessage(msg);
	}
	/**
	 * 定义了一个handler对象来处理发送的handler消息
	 */
	private HHWeakHandler<Fragment> handler=new HHWeakHandler<Fragment>(this)
	{
		
		@Override
		public void processHandlerMessage(Message msg)
		{
			processHandlerMsg(msg);
		}
	};
	/**
	 * 根据View的ID，在parentView中查找ID为viewID的View
	 * @param parentView
	 * @param viewID
	 * @return
	 */
	public <T> T getViewByID(View parentView,int viewID)
	{
		return HHViewHelper.getViewByID(parentView, viewID);
	}
	/**
	 * 设置当前页面显示的标题（只有在当前页面使用的是HHDefaultTopViewManager的时候才有效果）
	 * @param pageTitle
	 */
	public void setPageTitle(String pageTitle)
	{
		HHTopViewManagerImp avalibleManager = mTopManager.getAvalibleManager();
		if (avalibleManager instanceof HHFragDefaulTopManager)
		{
			HHFragDefaulTopManager defaultTopViewManager=(HHFragDefaulTopManager) avalibleManager;
			defaultTopViewManager.getTitleTextView().setText(pageTitle);
		}
	}
	/**
	 * 设置当前页面的标题
	 * @param resID		标题的资源文件的ID
	 */
	public void setPageTitle(int resID)
	{
		setPageTitle(getString(resID));
	}
}
