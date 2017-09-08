package com.huahan.hhbaseutils.ui;

import android.content.Context;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.baidu.location.BDLocation;
import com.huahan.hhbaseutils.HHLocationUtils;
import com.huahan.hhbaseutils.HHLocationUtils.LocationListener;
import com.huahan.hhbaseutils.HHLog;
import com.huahan.hhbaseutils.HHTipUtils;
import com.huahan.hhbaseutils.HHViewHelper;
import com.huahan.hhbaseutils.R;
import com.huahan.hhbaseutils.adapter.HHCityAdapter;
import com.huahan.hhbaseutils.imp.CityInfoImp;
import com.huahan.hhbaseutils.model.HHLoadState;
import com.huahan.hhbaseutils.model.HHLocationCity;
import com.huahan.hhbaseutils.view.letterview.HHLetterListView;
import com.huahan.hhbaseutils.view.letterview.HHLetterListView.OnTouchingLetterChangedListener;
import com.huahan.hhbaseutils.view.spinnerload.SpinnerLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * 选择城市的界面
 * 
 * @author yuan
 * 
 */
public abstract class HHSelectCityActivity extends HHBaseDataActivity implements OnItemClickListener, OnClickListener, OnTouchingLetterChangedListener
{
	private static final String tag=HHSelectCityActivity.class.getName();
	/**
	 * 正在定位
	 */
	private static final int LOCATION_ING=0;
	/**
	 * 定位失败
	 */
	private static final int LOCATION_FAILED=1;
	/**
	 * 定位成功
	 */
	private static final int LOCATION_SUCCESS=2;
	/**
	 * 定位成功,匹配成功
	 */
	private static final int MATCH_SUCCESS=3;
	/**
	 * 定位成功,匹配失败
	 */
	private static final int MATCH_FAILED=4;
	/**
	 * 城市返回
	 */
	public static final String FLAG_RESULT="flag_result";
	/**
	 * 定位返回
	 */
	public static final String LOCATION_RESULT="location_result";
	//获取城市列表的what
	private static final int GET_CITY_LIST = 0;
	//定位成功的what
	private static final int GET_LOCATION_CITY = 1;
	//显示城市列表
	private ListView mCityListView;
	//显示索引的列表
	private HHLetterListView mIndexListView;
	//保存获取的城市信息
	private List<CityInfoImp> mList;
	//保存adapter的值
	private List<CityInfoImp> mIndexList;
	//显示当前的定位状态
	private TextView mStateTextView;
	//显示当前定位的城市
	private TextView mCityTextView;
	//显示正在定位的ProgressBar
	private SpinnerLoader mLoader;
	//搜索linearlayout
	private LinearLayout mSearchLayout;
	//背景图片
	private int mSearchBgImage=R.drawable.hh_shape_edit_border_bg;
	//搜索editView
	private EditText mSearchEditText;
	//搜索图标图片
	private int mSearchImage=R.drawable.hh_search_city;
	//listview显示的headerview
	private View mHeaderView;
	//保存了定位的结果
	private HHLocationCity mLocationCity;
	//显示当前的索引的TextView
	private TextView mIndexTextView;
	//显示城市列表的适配器
	private HHCityAdapter mAdapter;
	//定位城市匹配城市列表位置
	private int posi=-1;

	@Override
	public View initView()
	{
		View view = View.inflate(getPageContext(), R.layout.hh_activity_select_city, null);
		mCityListView = HHViewHelper.getViewByID(view, R.id.hh_lv_city);
		mIndexListView = HHViewHelper.getViewByID(view, R.id.hh_lv_index);
		mHeaderView=View.inflate(getPageContext(), R.layout.hh_include_city_header, null);
		mStateTextView=HHViewHelper.getViewByID(mHeaderView, R.id.hh_tv_city_location);
		mCityTextView=HHViewHelper.getViewByID(mHeaderView, R.id.hh_tv_city);
		mLoader=HHViewHelper.getViewByID(mHeaderView, R.id.hh_sl_progress);
		mSearchLayout=HHViewHelper.getViewByID(mHeaderView, R.id.hh_ll_search);
		mSearchEditText=HHViewHelper.getViewByID(mHeaderView, R.id.hh_et_city_search);
		mIndexTextView=HHViewHelper.getViewByID(view, R.id.hh_tv_main_index);
		return view;
	}

	@Override
	public void initValues()
	{
		mIndexTextView.setVisibility(View.GONE);
		if (getSearchBgImage()!=0)
		{
			mSearchLayout.setBackgroundResource(getSearchBgImage());
		}
		if (getSearchImage()!=0)
		{
			mSearchEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, getSearchImage(), 0);
		}
		mSearchEditText.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
	}

	@Override
	public void initListeners()
	{
		mCityTextView.setOnClickListener(this);
		mIndexListView.setOnTouchingLetterChangedListener(this);
		mSearchEditText.setOnEditorActionListener(new OnEditorActionListener()
		{
			
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
			{
				// TODO Auto-generated method stub
				if (actionId==EditorInfo.IME_ACTION_SEARCH)
				{
					if (mList!=null)
					{
						HHTipUtils.getInstance().showProgressDialog(getPageContext(), R.string.hh_search_city_now);
						getSearchList(mSearchEditText.getText().toString().trim().toUpperCase());
					}
					InputMethodManager manager=(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					manager.hideSoftInputFromWindow(mSearchEditText.getWindowToken(), 0);
				}
				return false;
			}
		});
	}

	@Override
	public boolean initOnCreate()
	{
		
		return false;
	}

	@Override
	public void onPageLoad()
	{
		getCityList();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id)
	{
		// 由于是在adapter中实现的，这里的position是不包含listview的headerview和footerview的
		CityInfoImp mCityInfoImp=mIndexList.get(position);
		HHLocationCity mHhLocationCity=new HHLocationCity();
		mHhLocationCity.setCityId(mCityInfoImp.getCityID());
		mHhLocationCity.setCityName(mCityInfoImp.getCityName());
		if (mLocationCity!=null) {
			mHhLocationCity.setLa(mLocationCity.getLa());
			mHhLocationCity.setLo(mLocationCity.getLo());
			mHhLocationCity.setInfo(mLocationCity.getInfo());
		}
		getCityInfo(mHhLocationCity);
	}

	@Override
	public void onClick(View v)
	{
		if (v.getTag()!=null)
		{
			int locationState=(Integer) v.getTag();
			if (locationState==LOCATION_SUCCESS||locationState==MATCH_SUCCESS)
			{
				getCityInfo(mLocationCity);
			}else if (locationState==LOCATION_FAILED)
			{
				changeLocationInfo(LOCATION_ING);
			}
			else if (locationState==MATCH_FAILED)
			{
				HHTipUtils.getInstance().showToast(getPageContext(), R.string.hh_location_city_open_no_hint);
			}
		}
	}

	protected abstract void getCityInfo(CityInfoImp infoImp);
	@Override
	public void onTouchingLetterChanged(String s)
	{
		int posi=mAdapter.getIndexPosition(s);
		if (posi!=-1)
		{
			mCityListView.setSelection(posi+mCityListView.getHeaderViewsCount());
			mIndexTextView.setText(s);
			mIndexTextView.setVisibility(View.VISIBLE);
			getHandler().removeCallbacks(mIndexLetterTask);
			getHandler().postDelayed(mIndexLetterTask,1000);
		}
	}
	/**
	 *	异步任务，用于隐藏当前显示的索引
	 */
	private Runnable mIndexLetterTask=new Runnable()
	{
		
		@Override
		public void run()
		{
			if (mIndexTextView!=null)
			{
				mIndexTextView.setVisibility(View.GONE);
			}
			
		}
	};
	/**
	 * 获取城市列表
	 */
	private void getCityList()
	{
		new Thread(new Runnable()
		{

			@Override
			public void run()
			{
				mList = getCityListInThread();
				sendHandlerMessage(GET_CITY_LIST);
			}
		}).start();
	}
	protected abstract List<CityInfoImp> getCityListInThread();
	/**
	 * 设置定位信息
	 * @param locationState
	 */
	private void changeLocationInfo(int locationState)
	{
		mCityTextView.setTag(locationState);
		switch (locationState)
		{
		case LOCATION_ING://正在定位
			mLoader.setVisibility(View.VISIBLE);
			mStateTextView.setText(R.string.hh_location_ing);
			mCityTextView.setVisibility(View.GONE);
			requestLocation();
			break;
		case LOCATION_SUCCESS://定位成功
			mLoader.setVisibility(View.GONE);
			mStateTextView.setText(R.string.hh_location_success);
			mCityTextView.setVisibility(View.VISIBLE);
			HHLog.i(tag, "changeLocationInfo=="+mLocationCity.getCityName());
			mCityTextView.setText(mLocationCity.getCityName());
			break;
		case LOCATION_FAILED://定位失败
			mLoader.setVisibility(View.GONE);
			mStateTextView.setText(R.string.hh_location_failed);
			mCityTextView.setVisibility(View.VISIBLE);
			mCityTextView.setText(getString(R.string.hh_location_again));
			break;
		case MATCH_SUCCESS://定位成功,匹配成功
			mLoader.setVisibility(View.GONE);
			mStateTextView.setText(R.string.hh_location_success);
			mCityTextView.setVisibility(View.VISIBLE);
			mLocationCity.setCityId(mList.get(posi).getCityID());
			mCityTextView.setText(mLocationCity.getCityName());
			break;
		case MATCH_FAILED://定位成功,匹配失败
			mLoader.setVisibility(View.GONE);
			mStateTextView.setText(R.string.hh_location_success);
			mCityTextView.setVisibility(View.VISIBLE);
			mCityTextView.setText(mLocationCity.getCityName()+getString(R.string.hh_location_city_open_no));
			break;

		default:
			break;
		}
	}
	/**
	 * 请求定位
	 */
	private void requestLocation()
	{
		HHLocationUtils.getInstance(getPageContext()).requestLocation(new LocationListener()
		{
			
			@Override
			public void onGetLocation(BDLocation bdLocation)
			{
				// TODO Auto-generated method stub
				//定位成功
				if(HHLocationUtils.getLocationResult(bdLocation))
				{
					if (mLocationCity==null)
					{
						mLocationCity=new HHLocationCity();
					}
					mLocationCity.setCityName(bdLocation.getCity());
					mLocationCity.setLa(bdLocation.getLatitude());
					mLocationCity.setLo(bdLocation.getLongitude());
					mLocationCity.setInfo(bdLocation.getStreet());
					sendHandlerMessage(GET_LOCATION_CITY);
				}else {
					changeLocationInfo(LOCATION_FAILED);
				}
				
			}
		});
		
	}

	/**
	 * 搜索
	 * @param city_list
	 * @param letter_list
	 * @param key_word
	 */
	private void getSearchList(String key_word)
	{
		HHTipUtils.getInstance().dismissProgressDialog();
		mIndexList.clear();
		for (int i = 0; i < mList.size(); i++)
		{
			if (mList.get(i).getCityName().indexOf(key_word)!=-1||mList.get(i).getCityIndex().indexOf(key_word)!=-1)
			{
				mIndexList.add(mList.get(i));
			}
		}
		mAdapter.notifyDataSetChanged();
	}
	/**
	 * 是否开通
	 * @param city_list
	 * @param location_city
	 * @return
	 */
	private boolean isHave()
	{
		for (int i = 0; i < mList.size(); i++)
		{
			//循环查找城市列表每个字符串是否包含于定位城市中
			if (mLocationCity.getCityName().indexOf(mList.get(i).getCityName())!=-1)
			{
				//查找到了就返回真，不在继续查询
				posi=i;
				return true;
			}
		}
		return false;
	}
	/**
	 * 获取搜索背景图片
	 * @return
	 */
	protected abstract int getSearchBgImage();
	/**
	 * 获取搜索图片
	 * @return
	 */
	protected abstract int getSearchImage();
	/**
	 * 消息处理
	 */
	@Override
	public void processHandlerMsg(Message msg)
	{
		switch (msg.what)
		{
		case GET_CITY_LIST:
			if (mList==null)
			{
				changeLoadState(HHLoadState.FAILED);
			}else {
				changeLoadState(HHLoadState.SUCCESS);
				mIndexList=new ArrayList<CityInfoImp>();
				mIndexList.addAll(mList);
				mAdapter = new HHCityAdapter(getPageContext(),mIndexList);
				//由于实现了水波纹效果，这个控件的使用拦截了触摸事件的传递，使得ListVie的onItemClickedListener事件没有
				//正确的执行，为了实现这个功能在adapter中添加了这个监听器，通过OnClickListener间接的实现了这个功能，但是
				//现在的onitemclickedlistener里边的参数是不全的，只有view和position两个参数是有意义的，其他的参数要么是
				//null，要么是没有实际意义的参数
				mAdapter.setOnItemClickListener(this);
				mCityListView.addHeaderView(mHeaderView);
				mCityListView.setAdapter(mAdapter);
				//改变现在的状态为正在定位
				changeLocationInfo(LOCATION_ING);
			}
			break;
		case GET_LOCATION_CITY:
			if (mList!=null&&mList.size()>0)
			{
				if (isHave())
				{
					changeLocationInfo(MATCH_SUCCESS);
				}else {
					changeLocationInfo(MATCH_FAILED);
				}
			}else {
				changeLocationInfo(LOCATION_SUCCESS);
			}
			break;
		default:
			break;
		}
	}
}
