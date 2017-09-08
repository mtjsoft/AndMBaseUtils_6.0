package com.huahan.hhbaseutils.ui;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huahan.hhbaseutils.HHSystemUtils;
import com.huahan.hhbaseutils.HHTipUtils;
import com.huahan.hhbaseutils.HHViewHelper;
import com.huahan.hhbaseutils.R;
import com.huahan.hhbaseutils.adapter.HHSelectPhotoAdapter;
import com.huahan.hhbaseutils.adapter.HHSelectPhotoTypeAdapter;
import com.huahan.hhbaseutils.anim.AnimationAdapter;
import com.huahan.hhbaseutils.anim.CustomTransAnimation;
import com.huahan.hhbaseutils.anim.CustomTransAnimation.OnAnimUpdateListener;
import com.huahan.hhbaseutils.constant.HHConstantParam;
import com.huahan.hhbaseutils.imp.OnItemClickListener;
import com.huahan.hhbaseutils.manager.HHDefaultTopViewManager;
import com.huahan.hhbaseutils.model.HHGridItemDecoration;
import com.huahan.hhbaseutils.model.HHLoadState;
import com.huahan.hhbaseutils.model.HHSelectPhotoHolder;
import com.huahan.hhbaseutils.model.HHSystemPhotoModel;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * 选择图片的界面
 * @author yuan
 */
public class HHSelectPhotoActivity extends HHBaseDataActivity implements OnItemClickListener
{
	//动画效果执行的时间
	private static final int WINDOW_ANIMATION_DURATION=200;
	public static final String DEFAULT_FILE_PATH="huahan_camera";
//	private static final String tag=HHSelectPhotoActivity.class.getSimpleName();
	/**
	 * 返回的结果
	 */
	public static final String FLAG_RESULT="flag_result";
	/**
	 * 设置选取图片的最大值
	 */
	public static final String FLAG_SELECT_COUNT="flag_select_count";
	/**
	 * 确定按钮颜色值
	 */
	public static final String SURE_COLOR="flag_sure_color";
	//最多可以选取多少张图片
	private int mSureColor=-1;
	//获取到的相册的集合
	private List<HHSystemPhotoModel> mSystemPhotoModels;
	//显示的ListView
	private RecyclerView mRecyclerView;
	//ListView的adapter
	private HHSelectPhotoAdapter mAdapter;
	//发送的消息
	private static final int GET_SYSTEM_PHOTO=0;
	//最多可以选取多少张图片
	private int mSelectCount=1;
	//暂时保存着相机图片的保存的路径和裁剪的时候保存的路径
	private String mSavePath="";
	//保存用户选择的图片的路径
	private ArrayList<String> mResultList=new ArrayList<String>();
	//保存图片的分组信息
	private Map<String, List<HHSystemPhotoModel>> mTypeMap=new HashMap<String, List<HHSystemPhotoModel>>();
	//确定的按钮
	private TextView mSureTextView;
	//显示目录的按钮
	private TextView mTypeTextView;
	//显示图片类别的listview
	private ListView mTypeListView;
	//包含类别的Layout
	private RelativeLayout mTypeLayout;
	@Override
	public boolean initOnCreate()
	{
		//判断默认的缓存的路径是否存在，如果不存在的话就创建这个路径,这个路径主要是为了缓存相机拍照的图片
		File file=new File(HHConstantParam.DEFAULT_CACHE_CAMERA);
		if (!file.exists())
		{
			file.mkdirs();
		}
		return false;
	}
	
	@Override
	public void onPageLoad()
	{
		//从系统中获取图片
		getSystemPhoto();
	}
	@Override
	public View initView()
	{
		View view=View.inflate(getPageContext(), R.layout.hh_activity_select_photo, null);
		mRecyclerView=HHViewHelper.getViewByID(view, R.id.hh_rv_select_photo);
		mRecyclerView.setHasFixedSize(true);
		mRecyclerView.addItemDecoration(new HHGridItemDecoration(getPageContext()));
		mTypeTextView=HHViewHelper.getViewByID(view, R.id.hh_tv_select_photo_name);
		mTypeLayout=HHViewHelper.getViewByID(view, R.id.hh_rl_select_photo_type);
		mTypeListView=HHViewHelper.getViewByID(view, R.id.hh_lv_photo_type);
		return view;
	}
	@SuppressWarnings("deprecation")
	@Override
	public void initValues()
	{
		//获取选择图片的数量，默认情况下的数量为1，如果用户传的数量小于1，则设置为1
		mSelectCount=getIntent().getIntExtra(FLAG_SELECT_COUNT, 1);
		mSureColor=getIntent().getIntExtra(SURE_COLOR, -1);
		if (mSelectCount<1)
		{
			mSelectCount=1;
		}
		//设置当前页面的标题和相应的提示信息
		HHDefaultTopViewManager manager = (HHDefaultTopViewManager) getTopManager().getAvalibleManager();
		setPageTitle(R.string.hh_select_photo);
		mSureTextView = manager.getMoreTextView();
		if (mSelectCount!=1)
		{
			if (mSureColor==-1)
			{
				mSureTextView.setTextColor(getResources().getColorStateList(R.color.hh_color_select_photo_sure));
			}else {
				mSureTextView.setTextColor(getResources().getColorStateList(mSureColor));
			}
			mSureTextView.setText(String.format(getString(R.string.hh_select_photo_info), mResultList.size(),mSelectCount));
			setInfoStyle(mSureTextView);
			mSureTextView.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					setSelectPhotoResult();
				}
			});
		}
		//设置图片分类列表的数据，并且设置为不可见，同时因为还没有选择图片，设置确定按钮为不可点击
		mTypeListView.setAdapter(new HHSelectPhotoTypeAdapter(this, mTypeMap,mSystemPhotoModels));
		mTypeLayout.setVisibility(View.INVISIBLE);
		mSureTextView.setEnabled(false);
		//重写默认的返回事件
		manager.getBackTextView().setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				onBackPressed();
			}
		});
	}
	
	@Override
	public void onBackPressed()
	{
		if (mTypeLayout.getVisibility()==View.VISIBLE)
		{
			hideTypeList();
		}else {
			finish();
		}
	}

	@Override
	public void initListeners()
	{
		//当图片分类的TextView被点击的时候设置是否显示分裂列表
		mTypeTextView.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				if (mTypeLayout.getVisibility()==View.INVISIBLE)
				{
					showTypeList();
				}else {
					hideTypeList();
				}
			}
		});
		//设置点击空白区域的时候，隐藏分类列表
		mTypeLayout.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				hideTypeList();
			}
		});
		//点击分类列表的时候执行的时间，需要重新设置显示的图片
		mTypeListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				
				//获取到分类列表的Adapter，重新设置选中的位置
				HHSelectPhotoTypeAdapter adapter=(HHSelectPhotoTypeAdapter) mTypeListView.getAdapter();
				adapter.setNewCheckPosition(position);
				//重新设置图片列表显示的图片
				mAdapter=new HHSelectPhotoAdapter(getPageContext(), adapter.getDataList().get(position).getPhotoList(), mSelectCount);
				mRecyclerView.setAdapter(mAdapter);
				//需要重新绑定监听器
				mAdapter.setOnItemClickListener(HHSelectPhotoActivity.this);
				//隐藏分类列表
				hideTypeList();
			}
		});
	}
	/**
	 * 显示图片类别列表
	 */
	private void showTypeList()
	{
		//设置按钮的状态为不可用，可以防止重复点击，动画显示的问题
		mTypeTextView.setEnabled(false);
		//显示分类的Layout
		mTypeLayout.setVisibility(View.VISIBLE);
		//设置动画效果
		CustomTransAnimation animation=new CustomTransAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF,0, Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0);
		animation.setDuration(WINDOW_ANIMATION_DURATION);
		animation.setFillAfter(true);
		animation.setAnimationListener(new AnimationAdapter()
		{

			@Override
			public void onAnimationEnd(Animation animation)
			{
				super.onAnimationEnd(animation);
				//当动画执行结束需要重新设置按钮的可用状态，以免下次点击的时候不可用
				mTypeTextView.setEnabled(true);
			}
			
		});
		animation.setOnAnimUpdateListener(new OnAnimUpdateListener()
		{
			
			@Override
			public void onAnimUpdate(float time)
			{
				//根据动画的执行时间，动态的改变分类的Layout的背景
				Drawable background = mTypeLayout.getBackground();
				if (background!=null)
				{
					background.setAlpha((int) (time*230+25));
				}
			}
		});
		mTypeListView.startAnimation(animation);
		
	}
	/**
	 * 隐藏图片列表
	 */
	private void hideTypeList()
	{
		//设置Layout不可用，以免出现重复点击的问题
		mTypeLayout.setEnabled(false);
		//实例化动画
		CustomTransAnimation animation=new CustomTransAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF,0, Animation.RELATIVE_TO_SELF,0, Animation.RELATIVE_TO_SELF,1.0f);
		animation.setDuration(WINDOW_ANIMATION_DURATION);
		animation.setFillAfter(true);
		animation.setOnAnimUpdateListener(new OnAnimUpdateListener()
		{
			
			@Override
			public void onAnimUpdate(float time)
			{
				//根据动画执行时间来设置背景
				Drawable background = mTypeLayout.getBackground();
				if (background!=null)
				{
					background.setAlpha(255-(int) (time*230+25));
				}
			}
		});
		animation.setAnimationListener(new AnimationAdapter()
		{

			@Override
			public void onAnimationEnd(Animation animation)
			{
				super.onAnimationEnd(animation);
				//动画执行结束以后，重新设置Layout为可用
				mTypeLayout.setVisibility(View.INVISIBLE);
				mTypeLayout.setEnabled(true);
			}
			
		});
		mTypeListView.startAnimation(animation);
	}
	@Override
	public void processHandlerMsg(Message msg)
	{
		switch (msg.what)
		{
		case GET_SYSTEM_PHOTO:
			//改变当前的加载状态为加载成功，同时设置显示的数据
			changeLoadState(HHLoadState.SUCCESS);
			mAdapter=new HHSelectPhotoAdapter(getPageContext(), mSystemPhotoModels,mSelectCount);
			GridLayoutManager manager=new GridLayoutManager(this, 3);
			mRecyclerView.setLayoutManager(manager);
			mRecyclerView.setAdapter(mAdapter);
			mAdapter.setOnItemClickListener(this);
			break;
		default:
			break;
		}
	}
	/**
	 * 获取相册里边的图片的集合
	 */
	private void getSystemPhoto()
	{
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				//获取数据
				mSystemPhotoModels=HHSystemUtils.getSystemPhotoList(getPageContext());
				if (mSystemPhotoModels!=null)
				{
					//对数据按照所在目录的不同进行分类
					for (HHSystemPhotoModel model : mSystemPhotoModels)
					{
						List<HHSystemPhotoModel> list = mTypeMap.get(model.getDirName());
						if (list==null)
						{
							list=new ArrayList<HHSystemPhotoModel>();
							mTypeMap.put(model.getDirName(), list);
						}
						list.add(model);
					}
				}
				//添加一个默认的图片，用于显示相机
				HHSystemPhotoModel model=new HHSystemPhotoModel();
				model.setFilePath(DEFAULT_FILE_PATH);
				mSystemPhotoModels.add(0,model);
				sendHandlerMessage(GET_SYSTEM_PHOTO);
			}
		}).start();
	}
	@Override
	public void onItemClicked(HHSelectPhotoHolder viewHolder, int position)
	{
		//获取到当前显示的数据
		List<HHSystemPhotoModel> dataList = mAdapter.getDataList();
		HHSystemPhotoModel model = dataList.get(position);
		//判断是不是相机的图标，如果是的话执行相机的操作
		if (position==0&&model.getFilePath().equals(DEFAULT_FILE_PATH))
		{
				mSavePath=HHConstantParam.DEFAULT_CACHE_CAMERA+System.currentTimeMillis()+".jpg";
				HHSystemUtils.getImageFromCamera(this, mSavePath);
		}else {
			//如果只可以选择一张图片，则点击图片以后直接返回
			if (mSelectCount==1)
			{
				mResultList.add(model.getFilePath());
				setSelectPhotoResult();
			}else {
				//根据图片的选中状态来重新设置选中状态，同时根据选择图片的数量来设置确定按钮是否可用
				if (model.isSelect())
				{
					mResultList.remove(model.getFilePath());
					model.setSelect(!model.isSelect());
					viewHolder.boxImageView.setImageResource(R.drawable.hh_select_photo);
					if (mResultList.size()==0)
					{
						mSureTextView.setEnabled(false);
					}
				}else if(mResultList.size()<mSelectCount)
				{
					model.setSelect(!model.isSelect());
					mResultList.add(model.getFilePath());
					viewHolder.boxImageView.setImageResource(R.drawable.hh_select_photo_se);
					mSureTextView.setEnabled(true);
				}else {
					//如果当前选中的图片的数量已经达到最大的数量，则提示用户
					HHTipUtils.getInstance().showToast(getPageContext(), String.format(getString(R.string.hh_select_photo_max),  mSelectCount));
				}
				//设置当前选择图片的状态
				mSureTextView.setText(String.format(getString(R.string.hh_select_photo_info), mResultList.size(),mSelectCount));

			}
		}
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode==RESULT_OK)
		{
			switch (requestCode)
			{
			case HHSystemUtils.GET_CAMERA_IMAGE:
				//相机拍摄图片完成以后，直接返回
				mResultList.clear();
				mResultList.add(mSavePath);
				setSelectPhotoResult();
				break;
			default:
				break;
			}
		}
	}
	//返回数据给上一个页面
	private void setSelectPhotoResult()
	{
		Intent intent=new Intent();
		intent.putStringArrayListExtra(FLAG_RESULT, mResultList);
		setResult(RESULT_OK, intent);
		finish();
	}
	/**
	 * 获取可以选择的图片的总数
	 * @return
	 */
	protected int getSelectCount()
	{
		return mSelectCount;
	}
	/**
	 * 获取当前选择的图片的集合
	 * @return
	 */
	protected ArrayList<String> getResultList()
	{
		return mResultList;
	}
	/**
	 * 设置确定按钮的显示的样式
	 * @param infoView
	 */
	protected void setInfoStyle(TextView infoView)
	{
		
	}
}
