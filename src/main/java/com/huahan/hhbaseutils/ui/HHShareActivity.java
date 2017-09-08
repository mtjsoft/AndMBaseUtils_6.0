package com.huahan.hhbaseutils.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetManager;
import android.graphics.drawable.BitmapDrawable;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;

import com.huahan.hhbaseutils.HHLog;
import com.huahan.hhbaseutils.HHScreenUtils;
import com.huahan.hhbaseutils.HHStreamUtils;
import com.huahan.hhbaseutils.HHTipUtils;
import com.huahan.hhbaseutils.HHViewHelper;
import com.huahan.hhbaseutils.R;
import com.huahan.hhbaseutils.adapter.HHShareExAdapter;
import com.huahan.hhbaseutils.constant.HHConstantParam;
import com.huahan.hhbaseutils.imp.HHShareQQImp;
import com.huahan.hhbaseutils.model.HHShareIDModel;
import com.huahan.hhbaseutils.model.HHShareItemInfo;
import com.huahan.hhbaseutils.model.HHShareModel;
import com.huahan.hhbaseutils.rippleview.MaterialRippleLayout;
import com.huahan.hhbaseutils.task.HHShareToQqTask;
import com.huahan.hhbaseutils.task.HHShareToQzoneTask;
import com.huahan.hhbaseutils.task.HHShareToSinaTask;
import com.huahan.hhbaseutils.task.HHShareToWXTask;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.constant.WBConstants;
import com.tencent.connect.common.Constants;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * 用户分享的界面<br/>
 * 关于分享的平台的图标和文字的信息，可以通过HHShareItemInfo这个类进行定制；在默认情况下提供微信，朋友圈，qq，新浪微博的
 * 图标和显示的文字信息，同时固定了这些平台的id,分别是0，1，2，3，这些平台对应的order默认和他们的顺序保持一致。这些平台的id是固定的不可改变的，但是可以更改这些平台的显示
 * 的图标和文字，以及显示的顺序。在显示的顺序上是按照从小到大的顺序显示的<br/>
 * <li>分享需要配置的ID和显示的AppName信息，需要早assets文件夹中创建一个share.json文件，该文件的基本内容如下</li>
 * {"weixin": "","qq": "","sina": "","qq_name":""}
 * <li>分享的时候需要的权限信息
 * <pre>
 *  &lt;uses-permission android:name="android.permission.INTERNET" /&gt;
 *  &lt;uses-permission android:name="android.permission.MODIFY_AUDIO_SETTINGS" /&gt;
 *  &lt;uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" /&gt;
 *  &lt;uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" /&gt;
 *  &lt;uses-permission android:name="android.permission.READ_PHONE_STATE" /&gt;    
 *  &lt;uses-permission android:name="android.permission.ACCESS_WIFI_STATE" /&gt;
 * </pre>
 * </li>
 * <li>微信分享配置信息，需要创建一个新包xxx.xxxx.wxapi,在该包下创建一个Activity，该Activity需要继承HHWXEntryActivity来实现微信分享结果的回调,并在清单文件中
 * 添加下面的IntentFilter信息</li>
 *  <pre>
 *    &lt;intent-filter>
 *            &lt;action android:name="android.intent.action.VIEW"/&gt;
 *            &lt;category android:name="android.intent.category.DEFAULT"/&gt;
 *            &lt;data android:scheme="sdksample"/&gt;
 *    &lt;/intent-filter&gt;
 *   </pre>
 * <li>QQ分享的配置信息，需要在清单文件中添加如下的额配置信息:</li>
 * <pre>
 *   &lt;activity
 *        android:name="com.tencent.tauth.AuthActivity"
 *        android:launchMode="singleTask"
 *        android:noHistory="true" &gt;
 *        &lt;intent-filter&gt;
 *             &lt;action android:name="android.intent.action.VIEW" /&gt;
 *             &lt;category android:name="android.intent.category.DEFAULT" /&gt;
 *             &lt;category android:name="android.intent.category.BROWSABLE" /&gt;
 *             &lt;data android:scheme="tencent1101328749" /&gt;
 *        &lt;/intent-filter&gt;
 *   &lt;/activity&gt;
 *   &lt;activity
 *        android:name="com.tencent.connect.common.AssistActivity"
 *        android:configChanges="orientation|keyboardHidden"
 *        android:screenOrientation="portrait"
 *        android:theme="@android:style/Theme.Translucent.NoTitleBar" /&gt;
 * </pre>
 * 对于<code>com.tencent.tauth.AuthActivity</code>中的<code>&lt;data android:scheme="tencent1101328749" /&gt;</code>需要自己修改为自己申请的
 * QQ的ID，也就是需要把1101328749替换
 * <li>新浪微博分享的配置信息，需要在清单文件中添加如下的配置信息</li>
 * <pre>
 *       &lt;activity android:name="com.sina.weibo.sdk.component.WeiboSdkBrowser" 
 *          android:configChanges="keyboardHidden|orientation"
 *           android:windowSoftInputMode="adjustResize"
 *           android:exported="false" &gt;
 *       &lt;/activity&gt;
 *       &lt;!-- 手机短信注册页面 --&gt;
 *       &lt;activity android:name="com.sina.weibo.sdk.register.mobile.MobileRegisterActivity" 
 *           android:configChanges="keyboardHidden|orientation"
 *           android:screenOrientation="portrait"
 *           android:windowSoftInputMode="adjustResize"
 *           android:exported="false" &gt;
 *       &lt;/activity&gt;
 *        &lt;!-- 注册选择国家页面 --&gt;
 *       &lt;activity android:name="com.sina.weibo.sdk.register.mobile.SelectCountryActivity" 
 *           android:configChanges="keyboardHidden|orientation"
 *           android:windowSoftInputMode="adjustResize"
 *           android:exported="false" &gt;
 *       &lt;/activity&gt;
 *       &lt;service android:name="com.sina.weibo.sdk.net.DownloadService"
 *           android:exported="false"&gt;
 *       &lt;/service&gt; 
 *</pre>  
 * 同时对于每一个调用新浪分享的界面都需要在这个分享界面的清单文件中添加如下的IntentFilter信息
 * <pre>
 *      &lt;intent-filter&gt;
 *            &lt;action android:name="com.sina.weibo.sdk.action.ACTION_SDK_REQ_ACTIVITY" /&gt;
 *            &lt;category android:name="android.intent.category.DEFAULT" /&gt;
 *      &lt;/intent-filter>  
 * </pre>      
 * 如果不添加这些信息，可能出现的问题：1：分享无法接收到分享的结果（如果不需要接收分享结果的话也可以不添加）；2：如果手机安装了客户端可能造成无法分享<br/>
 * 如果添加这些信息可能造成的问题:1:存在多个页面的情况，调用分享会造成出错或者调起其他不相干的分享页面
 * 		
 * 
 * @author yuan
 * 
 */
public abstract class HHShareActivity extends HHBaseImageActivity implements IWeiboHandler.Response,HHShareQQImp
{
	// 保存了各个平台分享的ID
	private HHShareIDModel mShareIDModel;
	// 打印的日志信息的tag
	private static final String tag = HHShareActivity.class.getSimpleName();
	// 微信分享的接口
	private IWXAPI mWXApi;
	// QQ分享的接口
	private Tencent mTencent;
	// 新浪微博
	private IWeiboShareAPI mWeiboShareSDK;
	// 显示的分享的window对象
	private PopupWindow mShareWindow;
	//接受微信分享结果的接收者
	private WeiXinShareReceiver mReceiver;
	//分享位置
	private static int sharePosition=-1;
	/**
	 * 获取分享的时候设置的配置信息
	 * 
	 * @param context
	 */
	private void getShareID(Context context)
	{
		AssetManager manager = context.getAssets();
		try
		{
			InputStream open = manager.open("share.json");
			String streamToString = HHStreamUtils.convertStreamToString(open);
			JSONObject jsonObject = new JSONObject(streamToString);
			mShareIDModel = new HHShareIDModel();
			mShareIDModel.setQq(jsonObject.optString("qq"));
			mShareIDModel.setSina(jsonObject.optString("sina"));
			mShareIDModel.setWeixin(jsonObject.optString("weixin"));
			mShareIDModel.setQqName(jsonObject.optString("qq_name"));
		} catch (Exception e)
		{
			HHLog.i(tag, "getShareID", e);
			mShareIDModel = null;
		}
	}
	/**
	 * 注册App
	 * 
	 * @param context
	 */
	private void registerApp(Context context)
	{
		mWXApi = WXAPIFactory.createWXAPI(context, mShareIDModel.getWeixin(), false);
		mWXApi.registerApp(mShareIDModel.getWeixin());
		mTencent = Tencent.createInstance(mShareIDModel.getQq(), context);
		mWeiboShareSDK = WeiboShareSDK.createWeiboAPI(context, mShareIDModel.getSina());
		mWeiboShareSDK.registerApp();
		mReceiver=new WeiXinShareReceiver();
		IntentFilter intentFilter=new IntentFilter();
		intentFilter.addAction(HHConstantParam.ACTION_SHARE_CANCEL);
		intentFilter.addAction(HHConstantParam.ACTION_SHARE_FAILED);
		intentFilter.addAction(HHConstantParam.ACTION_SHARE_SUCCESS);
		registerReceiver(mReceiver, intentFilter);
	}

	/**
	 * 分享到微信
	 * 
	 * @param model
	 *            分享的内容
	 * @param isTimeLine
	 *            true的时候分享到微信朋友圈，false的时候分享到微信好友
	 */
	private void shareToWX(final HHShareModel model, final boolean isTimeLine)
	{
		HHTipUtils.getInstance().showProgressDialog(this, R.string.hh_send_request_ing);
		HHShareToWXTask task = new HHShareToWXTask(model, isTimeLine, mWXApi, getHandler());
		new Thread(task).start();
	}
	/**
	 * 分享到QQ
	 * 
	 * @param model
	 *            分享的内容
	 */
	private void shareToQQ(HHShareModel model)
	{
		HHTipUtils.getInstance().showProgressDialog(this, R.string.hh_send_request_ing);
		HHShareToQqTask task = new HHShareToQqTask(model, mShareIDModel.getQqName(), mTencent, this, getHandler());
		new Thread(task).start();
	}
	/**
	 * 分享到qq空间
	 * @param model
	 */
	private void shareToQzone(HHShareModel model)
	{
		HHTipUtils.getInstance().showProgressDialog(this, R.string.hh_send_request_ing);
		HHShareToQzoneTask task = new HHShareToQzoneTask(model, mShareIDModel.getQqName(), mTencent, this, getHandler());
		new Thread(task).start();
	}

	/**
	 * 分享到新浪微博
	 * 
	 * @param model
	 */
	private void shareToSina(final HHShareModel model)
	{
		HHTipUtils.getInstance().showProgressDialog(this, R.string.hh_send_request_ing);
		HHShareToSinaTask task = new HHShareToSinaTask(model, mShareIDModel.getSina(), mWeiboShareSDK, this, getHandler());
		new Thread(task).start();
	}
	/**
	 * 显示分享的window对象
	 */
	protected void showShareWindow(final HHShareModel model)
	{
		showShareWindow(model, null,null);
	}
	/**
	 *	显示分享的window，默认添加默认的平台
	 * @param model
	 * @param shareItemList				分享的平台的信息，键值为这个平台对应的ID
	 */
	protected void showShareWindow(final HHShareModel model, HashMap<Integer,HHShareItemInfo> shareItemList,final OnShareItemClickListener listener)
	{
		showShareWindow(model, shareItemList, listener, true);
	}
	/**
	 * 显示分享的window
	 * @param model					分享的Model
	 * @param shareItemList			分享的平台的信息
	 * @param listener				分享平台的点击事件
	 * @param addAllDefault			是否添加默认的平台。true，添加，则shareItemList中不含有默认的平台的时候，就会默认添加默认平台
	 */
	@SuppressWarnings("deprecation")
	protected void showShareWindow(final HHShareModel model, HashMap<Integer,HHShareItemInfo> shareItemList,final OnShareItemClickListener listener,boolean addAllDefault)
	{
		if (mShareWindow != null && mShareWindow.isShowing())
		{
			return;
		}
		if (mShareWindow == null)
		{
			//获取分享的信息
			getShareID(getApplicationContext());
			//如果获取分享的信息失败的情况下，抛出异常提示用户去检查配置信息
			if (mShareIDModel == null)
			{
				throw new RuntimeException("please check file at assets/share.json");
			}
			//注册微信，QQ和新浪微博等信息
			registerApp(getApplicationContext());
			final List<HHShareItemInfo> initShareItemInfo = initShareItemInfo(shareItemList, addAllDefault, model.getQqShareType());
			mShareWindow = new PopupWindow(getApplicationContext());
			View contentView = View.inflate(this, R.layout.hh_window_share, null);
			mShareWindow.setContentView(contentView);
			mShareWindow.setWidth(HHScreenUtils.getScreenWidth(this));
			GridView gridView = HHViewHelper.getViewByID(contentView, R.id.gv_share);
			MaterialRippleLayout cancelTextView = HHViewHelper.getViewByID(contentView, R.id.rv_cancel);
			gridView.setOnItemClickListener(new OnItemClickListener()
			{
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id)
				{
					mShareWindow.dismiss();
					sharePosition=position;
					HHShareItemInfo info = initShareItemInfo.get(position);
					if (info.getId()<=HHConstantParam.SHARE_TYPE_MAX_ID)
					{
						share(info.getId(), model);
					}else if(listener!=null)
					{
						listener.onShareItemClicked(info.getId());
					}
				}
			});
			cancelTextView.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					mShareWindow.dismiss();
				}
			});
			HHShareExAdapter adapter = new HHShareExAdapter(this,initShareItemInfo);
			gridView.setAdapter(adapter);
			mShareWindow.setHeight(-2);
			mShareWindow.setOutsideTouchable(true);
			mShareWindow.setFocusable(true);
			mShareWindow.setBackgroundDrawable(new BitmapDrawable());
			mShareWindow.setAnimationStyle(R.style.hh_window_share_anim);
			mShareWindow.setOnDismissListener(new OnDismissListener()
			{
				@Override
				public void onDismiss()
				{
					HHScreenUtils.setWindowDim(HHShareActivity.this, 1.0f);
				}
			});
		}
		HHScreenUtils.setWindowDim(this, 0.7f);
		mShareWindow.showAtLocation(getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);
	}
	private List<HHShareItemInfo> initShareItemInfo(HashMap<Integer, HHShareItemInfo> shareItemMap,boolean addAllDefault,int qqShareType)
	{
		List<HHShareItemInfo> mShareItemInfoList = new ArrayList<HHShareItemInfo>();
		if (qqShareType==HHConstantParam.SHARE_TYPE_QQ_DEFAULT) {
			mShareItemInfoList=initShareItemInfo(shareItemMap, addAllDefault);
		}else if (qqShareType==HHConstantParam.SHARE_TYPE_QZONE){
			mShareItemInfoList=initShareItemInfoByQzone(shareItemMap, addAllDefault);
		}
		return mShareItemInfoList;
	}
	/**
	 * 初始化分享的平台显示的信息
	 * 
	 * @param shareItemMap			
	 *            平台显示的信息。键值是平台的ID
	 * @param addAllDefault
	 * 			  是否添加默认的平台；如果shareItemMap为null，则添加默认的平台。true：shareItemMap中不含有默认平台，就添加默认平台
	 */
	@SuppressLint("UseSparseArrays") 
	private List<HHShareItemInfo> initShareItemInfo(HashMap<Integer, HHShareItemInfo> shareItemMap,boolean addAllDefault)
	{
	
			//保存了所有的平台的信息
			List<HHShareItemInfo> mShareItemInfoList = new ArrayList<HHShareItemInfo>();
			if (shareItemMap==null)
			{
				HHShareItemInfo wxInfo = new HHShareItemInfo(R.drawable.hh_share_wx, R.string.share_wx, 0, HHConstantParam.SHARE_TYPE_WX);
				HHShareItemInfo timeLineInfo = new HHShareItemInfo(R.drawable.hh_share_wx_timeline, R.string.share_wx_timeline, 1, HHConstantParam.SHARE_TYPE_TIMELINE);
				HHShareItemInfo qqInfo = new HHShareItemInfo(R.drawable.hh_share_qq, R.string.share_qq, 2, HHConstantParam.SHARE_TYPE_QQ);
				HHShareItemInfo sinaInfo = new HHShareItemInfo(R.drawable.hh_share_sina, R.string.share_sina, 3, HHConstantParam.SHARE_TYPE_SINA);
				mShareItemInfoList.add(wxInfo);
				mShareItemInfoList.add(timeLineInfo);
				mShareItemInfoList.add(qqInfo);
				mShareItemInfoList.add(sinaInfo);
				
				
			}else {
				HashMap<Integer, HHShareItemInfo> map = new HashMap<Integer, HHShareItemInfo>();
				map.putAll(shareItemMap);
				if (addAllDefault)
				{
					if (!map.containsKey(HHConstantParam.SHARE_TYPE_WX))
					{
						HHShareItemInfo wxInfo = new HHShareItemInfo(R.drawable.hh_share_wx, R.string.share_wx, 0, HHConstantParam.SHARE_TYPE_WX);
						map.put(HHConstantParam.SHARE_TYPE_WX, wxInfo);
					}
					if (!map.containsKey(HHConstantParam.SHARE_TYPE_TIMELINE))
					{
						HHShareItemInfo timeLineInfo = new HHShareItemInfo(R.drawable.hh_share_wx_timeline, R.string.share_wx_timeline, 1, HHConstantParam.SHARE_TYPE_TIMELINE);
						map.put(HHConstantParam.SHARE_TYPE_TIMELINE, timeLineInfo);
					}
					if (!map.containsKey(HHConstantParam.SHARE_TYPE_QQ))
					{
						HHShareItemInfo qqInfo = new HHShareItemInfo(R.drawable.hh_share_qq, R.string.share_qq, 2, HHConstantParam.SHARE_TYPE_QQ);
						map.put(HHConstantParam.SHARE_TYPE_QQ, qqInfo);
					}
					if (!map.containsKey(HHConstantParam.SHARE_TYPE_SINA))
					{
						HHShareItemInfo sinaInfo = new HHShareItemInfo(R.drawable.hh_share_sina, R.string.share_sina, 3, HHConstantParam.SHARE_TYPE_SINA);
						map.put(HHConstantParam.SHARE_TYPE_SINA, sinaInfo);
					}
				}
				Collection<HHShareItemInfo> values = map.values();
				Iterator<HHShareItemInfo> iterator = values.iterator();
				while (iterator.hasNext())
				{
					HHShareItemInfo next = iterator.next();
					mShareItemInfoList.add(next);
				}
		}
			Collections.sort(mShareItemInfoList);
			return mShareItemInfoList;
	}
	private List<HHShareItemInfo> initShareItemInfoByQzone(HashMap<Integer, HHShareItemInfo> shareItemMap,boolean addAllDefault)
	{
	
			//保存了所有的平台的信息
			List<HHShareItemInfo> mShareItemInfoList = new ArrayList<HHShareItemInfo>();
			if (shareItemMap==null)
			{
				HHShareItemInfo wxInfo = new HHShareItemInfo(R.drawable.hh_share_wx, R.string.share_wx, 0, HHConstantParam.SHARE_TYPE_WX);
				HHShareItemInfo timeLineInfo = new HHShareItemInfo(R.drawable.hh_share_wx_timeline, R.string.share_wx_timeline, 1, HHConstantParam.SHARE_TYPE_TIMELINE);
				HHShareItemInfo qqInfo = new HHShareItemInfo(R.drawable.hh_share_qzone, R.string.share_qzone, 2, HHConstantParam.SHARE_TYPE_QQ);
				HHShareItemInfo sinaInfo = new HHShareItemInfo(R.drawable.hh_share_sina, R.string.share_sina, 3, HHConstantParam.SHARE_TYPE_SINA);
				mShareItemInfoList.add(wxInfo);
				mShareItemInfoList.add(timeLineInfo);
				mShareItemInfoList.add(qqInfo);
				mShareItemInfoList.add(sinaInfo);
				
				
			}else {
				HashMap<Integer, HHShareItemInfo> map = new HashMap<Integer, HHShareItemInfo>();
				map.putAll(shareItemMap);
				if (addAllDefault)
				{
					if (!map.containsKey(HHConstantParam.SHARE_TYPE_WX))
					{
						HHShareItemInfo wxInfo = new HHShareItemInfo(R.drawable.hh_share_wx, R.string.share_wx, 0, HHConstantParam.SHARE_TYPE_WX);
						map.put(HHConstantParam.SHARE_TYPE_WX, wxInfo);
					}
					if (!map.containsKey(HHConstantParam.SHARE_TYPE_TIMELINE))
					{
						HHShareItemInfo timeLineInfo = new HHShareItemInfo(R.drawable.hh_share_wx_timeline, R.string.share_wx_timeline, 1, HHConstantParam.SHARE_TYPE_TIMELINE);
						map.put(HHConstantParam.SHARE_TYPE_TIMELINE, timeLineInfo);
					}
					if (!map.containsKey(HHConstantParam.SHARE_TYPE_QQ))
					{
						HHShareItemInfo qqInfo = new HHShareItemInfo(R.drawable.hh_share_qzone, R.string.share_qzone, 2, HHConstantParam.SHARE_TYPE_QQ);
						map.put(HHConstantParam.SHARE_TYPE_QQ, qqInfo);
					}
					if (!map.containsKey(HHConstantParam.SHARE_TYPE_SINA))
					{
						HHShareItemInfo sinaInfo = new HHShareItemInfo(R.drawable.hh_share_sina, R.string.share_sina, 3, HHConstantParam.SHARE_TYPE_SINA);
						map.put(HHConstantParam.SHARE_TYPE_SINA, sinaInfo);
					}
				}
				Collection<HHShareItemInfo> values = map.values();
				Iterator<HHShareItemInfo> iterator = values.iterator();
				while (iterator.hasNext())
				{
					HHShareItemInfo next = iterator.next();
					mShareItemInfoList.add(next);
				}
		}
			Collections.sort(mShareItemInfoList);
			return mShareItemInfoList;
	}

	/**
	 * 分享
	 * 
	 * @param position
	 *            用户点击的时候那个平台
	 * @param model
	 *            分享的数据
	 */
	private void share(int id, HHShareModel model)
	{
		switch (id)
		{
		case 0:// 微信
			shareToWX(model, false);
			break;
		case 1:// 朋友圈
			shareToWX(model, true);
			break;
		case 2:// QQ
			if (model.getQqShareType()==HHConstantParam.SHARE_TYPE_QQ_DEFAULT) {
				shareToQQ(model);
			}else if (model.getQqShareType()==HHConstantParam.SHARE_TYPE_QZONE) {
				shareToQzone(model);
			}
			break;
		case 3:// 新浪
			shareToSina(model);
			break;
		default:
			break;
		}
	}
	/**
	 * 单个分享
	 * @param id
	 * @param model
	 */
	protected void shareSingle(int id,HHShareModel model)
	{
		//获取分享的信息
		getShareID(getApplicationContext());
		//如果获取分享的信息失败的情况下，抛出异常提示用户去检查配置信息
		if (mShareIDModel == null)
		{
			throw new RuntimeException("please check file at assets/share.json");
		}
		//注册微信，QQ和新浪微博等信息
		registerApp(getApplicationContext());
		share(id, model);
	}
	/**
	 * 用户点击分享的平台的时候执行的代码，需要注意的是该监听器只执行用户自己添加的平台。
	 * 类库中支持的平台的分享事件由类库自己处理，用户只需要处理自己添加的平台
	 * @author yuan
	 *
	 */
	public interface OnShareItemClickListener
	{
		/**
		 * 分享的平台的ID，只是用户自己添加的平台的ID，微信这些类库集成的点击事件在类库中自行处理，
		 * 不交给用户自己处理
		 * @param id
		 */
		void onShareItemClicked(int id);
	}
	/**
	 * 获取对应平台分享的时候显示的信息,只返回类库显示实现的平台，其他平台返回null
	 * @param type				平台的类型,对应的值为HHConstantParam.SHARE_TYPE_WX等
	 * @return
	 */
	protected HHShareItemInfo getShareItemInfoOfType(int type)
	{
		HHShareItemInfo info=null;
		switch (type)
		{
		case HHConstantParam.SHARE_TYPE_WX:
			info=new HHShareItemInfo(R.drawable.hh_share_wx, R.string.share_wx, 0, type);
			break;
		case HHConstantParam.SHARE_TYPE_TIMELINE:
			info=new HHShareItemInfo(R.drawable.hh_share_wx_timeline, R.string.share_wx_timeline, 1, type);
			break;
		case HHConstantParam.SHARE_TYPE_QQ:
			info=new HHShareItemInfo(R.drawable.hh_share_qq, R.string.share_qq, 2, type);
			break;
		case HHConstantParam.SHARE_TYPE_SINA:
			info=new HHShareItemInfo(R.drawable.hh_share_sina, R.string.share_sina, 3, type);
			break;
		default:
			break;
		}
		return info;
	}
	@Override
	public void processHandlerMsg(Message msg)
	{
		HHTipUtils.getInstance().dismissProgressDialog();
		switch (msg.what)
		{
		case HHConstantParam.SEND_SHARE_REQUEST_FAILED:
//			HHTipUtils.getInstance().dismissProgressDialog();
			HHTipUtils.getInstance().showToast(getPageContext(), R.string.hh_send_request_failed);
			HHLog.i(tag, "share failed reason is :"+(String)msg.obj);
			break;
		case HHConstantParam.SEND_SHARE_REQUEST_SUCCESS:
//			HHTipUtils.getInstance().dismissProgressDialog();
			break;
		default:
			break;
		}
	}
	/**
	 * 定义一个广播接收者用户处理微信分享的结果
	 * @author yuan
	 *
	 */
	private class WeiXinShareReceiver extends BroadcastReceiver
	{

		@Override
		public void onReceive(Context context, Intent intent)
		{
			String action=intent.getAction();
			if (HHConstantParam.ACTION_SHARE_SUCCESS.equals(action))
			{
				shareResult(HHConstantParam.SHARE_TYPE_WX, HHConstantParam.SHARE_RESULT_SUCCESS);
			}else if (HHConstantParam.ACTION_SHARE_FAILED.equals(action))
			{
				shareResult(HHConstantParam.SHARE_TYPE_WX, HHConstantParam.SHARE_RESULT_FAILED);
			}else if(HHConstantParam.ACTION_SHARE_CANCEL.equals(action))
			{
				shareResult(HHConstantParam.SHARE_TYPE_WX, HHConstantParam.SHARE_RESULT_CANCEL);
			}
		}
		
	}
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		if (mReceiver!=null)
		{
			unregisterReceiver(mReceiver);
		}
	}
	@Override
	protected void onNewIntent(Intent intent)
	{
		super.onNewIntent(intent);
		if (mWeiboShareSDK!=null)
		{
			mWeiboShareSDK.handleWeiboResponse(intent, this);
		}
	}
	/**
	 * 新浪微博分享
	 */
	@Override
	public void onResponse(BaseResponse arg0)
	{
		Log.i("chenyuan", "error code:"+arg0.errCode+",msg:"+arg0.errMsg+",package:"+arg0.reqPackageName);
		switch (arg0.errCode)
		{
		case WBConstants.ErrorCode.ERR_OK:
			shareResult(HHConstantParam.SHARE_TYPE_SINA, HHConstantParam.SHARE_RESULT_SUCCESS);
			break;
		case WBConstants.ErrorCode.ERR_FAIL:
			shareResult(HHConstantParam.SHARE_TYPE_SINA, HHConstantParam.SHARE_RESULT_FAILED);
			break;
		case WBConstants.ErrorCode.ERR_CANCEL:
			HHLog.i(tag, "sina cancel :"+arg0.errMsg);
			shareResult(HHConstantParam.SHARE_TYPE_SINA, HHConstantParam.SHARE_RESULT_CANCEL);
			break;

		default:
			break;
		}
	}
	/**
	 * 显示分享的结果,由于微信和朋友圈的分享暂时无法分开，统归到微信中
	 * @param type				分享的平台，HHConstantParam.SHARE_TYPE_WX等值
	 * @param state				分享的状态，HHConstantParam.SHARE_RESULT_SUCCESS等值
	 */
	protected void shareResult(int type,int state)
	{
		onShareFinishListener(type, state);
		switch (state)
		{
		case HHConstantParam.SHARE_RESULT_SUCCESS:
			HHTipUtils.getInstance().showToast(this, R.string.hh_share_success);
			break;
		case HHConstantParam.SHARE_RESULT_FAILED:
			HHTipUtils.getInstance().showToast(this, R.string.hh_share_failed);
			break;
		case HHConstantParam.SHARE_RESULT_CANCEL:
			HHTipUtils.getInstance().showToast(this, R.string.hh_share_cancel);
			break;

		default:
			break;
		}
	}
	@Override
	public void onCancel()
	{
		shareResult(HHConstantParam.SHARE_TYPE_QQ, HHConstantParam.SHARE_RESULT_CANCEL);
	}
	@Override
	public void onComplete(Object arg0)
	{
		HHLog.i(tag, "qq share or login success.result is :"+arg0);
		shareResult(HHConstantParam.SHARE_TYPE_QQ, HHConstantParam.SHARE_RESULT_SUCCESS);
	}
	@Override
	public void onError(UiError arg0)
	{
		shareResult(HHConstantParam.SHARE_TYPE_QQ, HHConstantParam.SHARE_RESULT_FAILED);
		HHLog.i(tag, "qq share or login error.error code:"+arg0.errorCode+",msg:"+arg0.errorMessage+",detail:"+arg0.errorDetail);
	}
	@Override
	public Activity getActivity()
	{
		return this;
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		
//		if (mTencent!=null)
//		{
			if (requestCode == Constants.REQUEST_QQ_SHARE||requestCode==Constants.REQUEST_QZONE_SHARE) 
			{
				Tencent.onActivityResultData(requestCode, resultCode, data, this);
//	        	if (resultCode == Constants.ACTIVITY_OK) 
//	        	{
//	        		Tencent.handleResultData(data, this);
//	        	}
	        }/*else if(requestCode == Constants.REQUEST_API) 
	        {
		        if(resultCode == Constants.RESULT_LOGIN) 
		        {
		            Tencent.handleResultData(data, this);
		        }
		    }*/
//		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	/**
	 * 分享完成
	 * @param type				分享的平台，HHConstantParam.SHARE_TYPE_WX等值
	 * @param state				分享的状态，HHConstantParam.SHARE_RESULT_SUCCESS等值
	 */
	protected abstract void onShareFinishListener(int type,int state);
	/**
	 * 获取分享的位置，用来判断微信回调
	 * @return
	 */
	public static int getShareClickPosition()
	{
		return sharePosition;
	}

}
