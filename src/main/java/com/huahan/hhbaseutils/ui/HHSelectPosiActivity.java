package com.huahan.hhbaseutils.ui;

import android.os.Message;
import android.view.View;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapStatusChangeListener;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData.Builder;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.huahan.hhbaseutils.HHLocationUtils;
import com.huahan.hhbaseutils.HHLocationUtils.LocationListener;
import com.huahan.hhbaseutils.HHLog;
import com.huahan.hhbaseutils.HHViewHelper;
import com.huahan.hhbaseutils.R;

/**
 * 选择地点的Activity,使用的百度地图<br/>
 * <li>使用百度地图首先需要在清单文件中添加配置信息</li>
 * <pre>
 *  &lt;meta-data  
 *       android:name="com.baidu.lbsapi.API_KEY"  
 *       android:value="开发者 key" /&gt; 
 * </pre> 
 * 开发者key需要自己去百度申请并替换
 * <li>配置需要的权限信息</li>
 * <pre>
 *   &lt;uses-permission android:name="android.permission.INTERNET"/&gt;
 *   &lt;uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/&gt;
 *   &lt;uses-permission android:name="com.android.launcher.permission.READ_SETTINGS" /&gt;
 *   &lt;uses-permission android:name="android.permission.WAKE_LOCK"/&gt;
 *   &lt;uses-permission android:name="android.permission.CHANGE_WIFI_STATE" /&gt;
 *   &lt;uses-permission android:name="android.permission.ACCESS_WIFI_STATE" /&gt;
 *   &lt;uses-permission android:name="android.permission.GET_TASKS" /&gt;
 *   &lt;uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/&gt;
 *   &lt;uses-permission android:name="android.permission.WRITE_SETTINGS" /&gt;
 * </pre>
 * @author yuan
 *
 */
public class HHSelectPosiActivity extends HHBaseActivity
{

	private static final String tag=HHSelectPosiActivity.class.getSimpleName();
	//显示地图的控件
	private MapView mMapView;
	//地图的控制类
	private BaiduMap mBaiduMap;
	//是否显示地图方法缩小的工具
	private boolean mShowZoomTool=false;
	//是否显示地理标尺
	private boolean mShowScaleTool=false;
	//是否显示自己的当前位置
	private boolean mShowMyLocationOverlay=true;
	//显示当前的位置
	private TextView mPosiTextView;
	//用于地理位置反编码
	private GeoCoder mGeoCoder;
	@Override
	public View initView()
	{
		//百度地图的初始化，由于该百度地图的各个组件的初始化操作都需要调用该方法，因此建议该方法在Application的初始化中调用
		//另外这个方法的调用必须在setContentView之前调用
		SDKInitializer.initialize(getApplicationContext());
		View view=View.inflate(this, R.layout.hh_activity_select_posi, null);
		mMapView=HHViewHelper.getViewByID(view, R.id.hh_mapview);
		mPosiTextView=HHViewHelper.getViewByID(view, R.id.hh_tv_posi);
		return view;
	}

	@Override
	public void initValues()
	{
		mBaiduMap= mMapView.getMap();
		mMapView.showZoomControls(mShowZoomTool);
		mMapView.showScaleControl(mShowScaleTool);
		
	}

	@Override
	public void initListeners()
	{
		//地图状态改变的时候执行的监听器
		mBaiduMap.setOnMapStatusChangeListener(new OnMapStatusChangeListener()
		{
			@Override
			public void onMapStatusChangeStart(MapStatus arg0)
			{
			}
			@Override
			public void onMapStatusChangeFinish(MapStatus arg0)
			{
				HHLog.i(tag, "map status change:"+mBaiduMap.getMapStatus().target.latitude+","+mBaiduMap.getMapStatus().target.longitude);
				
			}
			@Override
			public void onMapStatusChange(MapStatus arg0)
			{
			}
		});
		if (mShowMyLocationOverlay)
		{
			requestLocation();
		}
	}
	/**
	 * 请求定位，获取定位信息
	 */
	private void requestLocation()
	{
		HHLocationUtils.getInstance(this).requestLocation(new LocationListener()
		{
			
			@Override
			public void onGetLocation(BDLocation bdLocation)
			{
				if (HHLocationUtils.getLocationResult(bdLocation))
				{
					HHLog.i(tag, "location success.la:"+bdLocation.getLatitude()+",lo:"+bdLocation.getLongitude()+",address:"+bdLocation.getAddrStr());
					mBaiduMap.setMyLocationEnabled(true);
					MyLocationConfiguration configuration=new MyLocationConfiguration(LocationMode.NORMAL, false, null);
					Builder builder=new Builder();
					builder.latitude(bdLocation.getLatitude());
					builder.longitude(bdLocation.getLongitude());
					mBaiduMap.setMyLocationConfigeration(configuration);
					mBaiduMap.setMyLocationData(builder.build());
					MapStatusUpdate update=MapStatusUpdateFactory.newLatLng(new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude()));
					mBaiduMap.animateMapStatus(update);
				}else {
					HHLog.i(tag, "location failed.location type is:"+bdLocation.getLocType());
				}
			}
		});
	}
	@Override
	public void processHandlerMsg(Message msg)
	{
		
	}
	private void getMapCenter()
	{
		
	}
	//------------设置地图的生命周期方法--------------
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		if (mMapView!=null)
		{
			mMapView.onDestroy();
		}
	}
	@Override
	protected void onResume()
	{
		super.onResume();
		if (mMapView!=null)
		{
			mMapView.onResume();
		}
	}
	@Override
	protected void onPause()
	{
		super.onPause();
		if (mMapView!=null)
		{
			mMapView.onPause();
		}
	}
	//------------结束设置地图的生命周期方法-------------------
	

}
