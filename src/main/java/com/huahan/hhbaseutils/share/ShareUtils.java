package com.huahan.hhbaseutils.share;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
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
import com.huahan.hhbaseutils.HHSystemUtils;
import com.huahan.hhbaseutils.HHViewHelper;
import com.huahan.hhbaseutils.adapter.HHShareAdapter;
import com.huahan.hhbaseutils.model.HHShareIDModel;
import com.huahan.hhbaseutils.model.HHShareModel;
import com.huahan.hhbaseutils.rippleview.MaterialRippleLayout;
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.WebpageObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.utils.Utility;
import com.tencent.connect.share.QQShare;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.tauth.Tencent;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
//import com.huahan.hhbaseutils.R;

/**
 * 分享的工具类<br/>
 * 使用说明：<br/>
 * 1:在assets目录下创建一个share.json的配置文件，该文件记录了各个平台分享的AppID和其他的信息<br/>
 * 2：share.json文件的内容：
 * <code>{"weixin": "","qq": "","sina": "","qq_name":""}</code>
 * ，分别代表了微信，qq，新浪,分享到qq时显示的应用的名称<br/>
 * 3：在项目下创建一个wxapi的包，例如xxx.xxxx.wxapi,在这个包下创建一个WXEntryActivity的类
 * 继承自HHWXEntryActivity,该类在清单文件中声明的时候，添加intent-filter,在intent-filter中添加一个action<br/>
 * <code>
 * android:name="android.intent.action.MAIN"
 * </code><br/>
 * 如果不添加一下代码可能造成无法监听分享状态。另外为了获得更好的显示效果，建议在清单文件中声明的时候给WXEntryActivity添加主题theme，并且
 * 设置主题为<code>android:theme="@android:style/Theme.Dialog"</code><br/>
 * 4:分享的时候设置的bitmap在分享的过程中会进行缩小处理以生成比较小的bitmap，原来的bitmap会调用recycle进行回收处理<br/>
 * 5:分享到QQ需要在清单文件中配置以下信息<br/>
 * <code>
 * 	&lt;activity android:name="com.tencent.tauth.AuthActivity"
 * 		android:launchMode="singleTask" 
 * 		android:noHistory="true" 
 * 		&gt; 
 * 		&lt;intent-filter&gt;
 * 			&lt;action android:name="android.intent.action.VIEW" /&gt;
 * 			&lt;category android:name="android.intent.category.DEFAULT" /&gt; 
 * 			&lt;category android:name="android.intent.category.BROWSABLE" /&gt;
 * 			&lt;data android:scheme="tencent自己的AppID" /&gt; &lt;/intent-filter&gt; 
 * 	&lt;/activity&gt;
 * 	&lt;activity android:name="com.tencent.connect.common.AssistActivity"
 * 				android:configChanges="orientation|keyboardHidden"
 * 				android:screenOrientation="portrait"
 * 				android:theme="@android:style/Theme.Translucent.NoTitleBar" /&gt;
 * </code> <br/>
 * 6：分享到新浪微博需要在清单文件中配置以下信息<br/>
 * <code>&lt;activity
 * 		android:name="com.sina.weibo.sdk.component.WeiboSdkBrowser"
 * android:configChanges="keyboardHidden|orientation"
 * android:windowSoftInputMode="adjustResize" android:exported="false" &gt;
 * &lt;/activity&gt;
 * 
 * &lt;!-- 手机短信注册页面 --&gt; &lt;activity
 * android:name="com.sina.weibo.sdk.register.mobile.MobileRegisterActivity"
 * android:configChanges="keyboardHidden|orientation"
 * android:screenOrientation="portrait"
 * android:windowSoftInputMode="adjustResize" android:exported="false" &gt;
 * &lt;/activity&gt;
 * 
 * &lt;!-- 注册选择国家页面 --&gt; &lt;activity
 * android:name="com.sina.weibo.sdk.register.mobile.SelectCountryActivity"
 * android:configChanges="keyboardHidden|orientation"
 * android:windowSoftInputMode="adjustResize" android:exported="false" &gt;
 * &lt;/activity&gt; &lt;service android:name="com.sina.weibo.sdk.net.DownloadService"
 * android:exported="false"&gt;&lt;/service&gt;</code><br/>
 * 7:分享需要的基本权限<br/>
 * <code>
 * &lt;uses-permission android:name="android.permission.INTERNET" /&gt;<br/>
 * &lt;uses-permission android:name="android.permission.MODIFY_AUDIO_SETTINGS" /&gt;<br/>
 * &lt;uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" /&gt;<br/>
 * &lt;uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" /&gt;<br/>
 * &lt;uses-permission android:name="android.permission.READ_PHONE_STATE" /&gt;<br/>
 * &lt;uses-permission android:name="android.permission.ACCESS_WIFI_STATE" /&gt;
 * </code>
 * 
 * @author yuan
 * 
 */
public class ShareUtils
{
	private static final String tag = ShareUtils.class.getName();
	// 保存用户新浪微博认证的信息
	private static final String PREFERENCES_NAME = "com_weibo_sdk_android";

	private static final String KEY_UID = "uid";
	private static final String KEY_ACCESS_TOKEN = "access_token";
	private static final String KEY_EXPIRES_IN = "expires_in";
	private static final String KEY_REFRESH_TOKEN = "refresh_token";
	// 微信支持朋友圈的版本
	private static final int TIMELINE_SUPPORTED_VERSION = 0x21020001;
	// 分享的时候图片的大小
	private static final int THUMB_SIZE = 100;
	private static ShareUtils shareUtils;
	private PopupWindow shareWindow;
	private View contentView;
	private HHShareIDModel shareIDModel;
	private IWXAPI wxApi;
	private Tencent tencent;
	private IWeiboShareAPI weiboApi;

	/**
	 * 私有化构造函数
	 */
	private ShareUtils()
	{
	};

	public static ShareUtils getInstance()
	{
		if (shareUtils == null)
		{
			shareUtils = new ShareUtils();
		}
		return shareUtils;
	}

	/**
	 * 显示分享的window对象
	 */
	@SuppressWarnings("deprecation")
	public void showShareWindow(final Activity activity, final HHShareModel shareModel)
	{

		if (shareWindow != null && shareWindow.isShowing())
		{
			return;
		}
		if (shareIDModel == null)
		{
			getShareID(activity.getApplicationContext());
			if (shareIDModel == null)
			{
				throw new RuntimeException("please check file at assets/share.json");
			}
			registerApp(activity.getApplicationContext());
		}

		// if (shareWindow == null)
		// {
		shareWindow = new PopupWindow(activity.getApplicationContext());
		contentView = View.inflate(activity, HHSystemUtils.getResourceID(activity, "hh_window_share", "layout"), null);
//		contentView = View.inflate(activity, R.layout.window_share, null);
		shareWindow.setContentView(contentView);
		shareWindow.setWidth(HHScreenUtils.getScreenWidth(activity));
		GridView gridView = (GridView) contentView.findViewById(HHSystemUtils.getResourceID(activity, "gv_share", "id"));
//		GridView gridView = (GridView) contentView.findViewById(R.id.gv_share);
		// GridView gridView = HHViewHelper.getViewByID(contentView,
		// R.id.gv_share);
		Log.i("chenyuan", "gridview is:" + gridView);
		MaterialRippleLayout cancelTextView = HHViewHelper.getViewByID(contentView, HHSystemUtils.getResourceID(activity, "rv_cancel", "id"));
//		MaterialRippleLayout cancelTextView = HHViewHelper.getViewByID(contentView, R.id.rv_cancel);
		gridView.setOnItemClickListener(new OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				shareWindow.dismiss();
				share(activity, position, shareModel);
			}
		});

		cancelTextView.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				shareWindow.dismiss();
			}
		});
		HHShareAdapter adapter = new HHShareAdapter(activity);
		gridView.setAdapter(adapter);

		shareWindow.setHeight(-2);
		shareWindow.setOutsideTouchable(true);
		shareWindow.setFocusable(true);
		shareWindow.setBackgroundDrawable(new BitmapDrawable());
		shareWindow.setAnimationStyle(HHSystemUtils.getResourceID(activity, "hh_window_share_anim", "style"));
//		shareWindow.setAnimationStyle(R.style.window_share_anim);
		shareWindow.setOnDismissListener(new OnDismissListener()
		{
			@Override
			public void onDismiss()
			{
				HHScreenUtils.setWindowDim(activity, 1.0f);
			}
		});
		// }
		HHScreenUtils.setWindowDim(activity, 0.7f);
		shareWindow.showAtLocation(activity.getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);
	}


	/**
	 * 获取分享的AppID
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
			shareIDModel = new HHShareIDModel();
			shareIDModel.setQq(jsonObject.optString("qq"));
			shareIDModel.setSina(jsonObject.optString("sina"));
			shareIDModel.setWeixin(jsonObject.optString("weixin"));
			shareIDModel.setQqName(jsonObject.optString("qq_name"));
		} catch (Exception e)
		{
			HHLog.i(tag, "getShareID", e);
			shareIDModel = null;
		}
	}

	/**
	 * 注册app
	 * 
	 * @param context
	 */
	private void registerApp(Context context)
	{
		wxApi = WXAPIFactory.createWXAPI(context, shareIDModel.getWeixin(), false);
		wxApi.registerApp(shareIDModel.getWeixin());
		tencent = Tencent.createInstance(shareIDModel.getQq(), context);
		weiboApi = WeiboShareSDK.createWeiboAPI(context, shareIDModel.getSina());
		weiboApi.registerApp();
	}

	/**
	 * 检查是否支持分享到朋友圈
	 * 
	 * @return
	 */
	@SuppressWarnings("unused")
	private boolean checkTimelineSupport()
	{
		int wxSdkVersion = wxApi.getWXAppSupportAPI();
		if (wxSdkVersion >= TIMELINE_SUPPORTED_VERSION)
		{
			return true;
		} else
		{
			return false;
		}
	}

	/**
	 * 分享
	 * 
	 * @param position
	 *            用户点击的时候那个平台
	 * @param model
	 *            分享的数据
	 */
	private void share(Activity activity, int position, HHShareModel model)
	{
		switch (position)
		{
		case 0:// 微信
			shareToWX(model, false);
			break;
		case 1:// 朋友圈
			shareToWX(model, true);
			break;
		case 2:// QQ
			shareToQQ(activity, model);
			break;
		case 3:// 新浪
			shareToSina(activity, model);
			break;

		default:
			break;
		}
	}

	private void shareToSina(final Activity activity,final HHShareModel model)
	{
		Log.i("doc", "shareToSina");
		new Thread(new Runnable()
		{
			
			@Override
			public void run()
			{
				try
				{
					// 创建一个消息
					WeiboMultiMessage message = new WeiboMultiMessage();
					
					ImageObject imageObject = new ImageObject();
					imageObject.actionUrl=model.getLinkUrl();
					imageObject.description=model.getDescription();
					imageObject.title=model.getTitle();
					imageObject.identify=Utility.generateGUID();
					
					WebpageObject mediaObject = new WebpageObject();
					mediaObject.identify = Utility.generateGUID();
					mediaObject.title = model.getTitle();
					mediaObject.description = model.getDescription();
					mediaObject.actionUrl = model.getLinkUrl();
					mediaObject.defaultText = model.getTitle();
					

					if (model.getThumpBitmap()!=null)
					{
						
						Bitmap bitmap = Bitmap.createScaledBitmap(model.getThumpBitmap(), THUMB_SIZE, THUMB_SIZE, false);
						model.getThumpBitmap().recycle();
						model.setThumpBitmap(bitmap);
						mediaObject.setThumbImage(bitmap);
						imageObject.setImageObject(bitmap);
					}else {
						Bitmap bitmap=BitmapFactory.decodeStream(new URL(model.getImageUrl()).openStream());
						Bitmap thumbBitmap=Bitmap.createScaledBitmap(bitmap, THUMB_SIZE, THUMB_SIZE, false);
						bitmap.recycle();
						mediaObject.setThumbImage(thumbBitmap);
//						imageObject.imagePath=model.getImageUrl();
						imageObject.setImageObject(thumbBitmap);
						Log.i("chenyuan",thumbBitmap+"***");
					}
					
					message.mediaObject = mediaObject;
					message.imageObject=imageObject;
					
					// 创建一个请求
					SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
					// 用transaction唯一标识一个请求
					request.transaction = String.valueOf(System.currentTimeMillis());
					request.multiMessage = message;
					AuthInfo authInfo = new AuthInfo(activity, shareIDModel.getSina(), "https://api.weibo.com/oauth2/default.html", "email,direct_messages_read,direct_messages_write,friendships_groups_read,friendships_groups_write,statuses_to_me_read,follow_app_official_microblog,invitation_write");
					Oauth2AccessToken accessToken = readAccessToken(activity.getApplicationContext());
					String token = "";
					if (accessToken != null)
					{
						token = accessToken.getToken();
					}
					Log.i("chenyuan", "新浪发送消息=====" + token + "," + weiboApi.isWeiboAppInstalled()+","+shareIDModel.getSina());
						weiboApi.sendRequest(activity, request, authInfo, token, new WeiboAuthListener()
						{
	
							@Override
							public void onWeiboException(WeiboException arg0)
							{
							}
	
							@Override
							public void onComplete(Bundle bundle)
							{
								// TODO Auto-generated method stub
								Log.i("chenyuan", "分享姐过");
								Oauth2AccessToken newToken = Oauth2AccessToken.parseAccessToken(bundle);
								writeAccessToken(activity.getApplicationContext(), newToken);
								// Toast.makeText(getApplicationContext(),
								// "onAuthorizeComplete token = " + newToken.getToken(),
								// 0).show();
							}
	
							@Override
							public void onCancel()
							{
								Log.i("chenyuan", "分享取消===");
							}
						});
//					 }
				} catch (Exception e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
					Log.i("chenyuan", "分享失败=="+e.getMessage());
				}
				
			}
		}).start();
		
		 

	}

	/**
	 * 把用户新浪认证的信息保存到本地
	 * 
	 * @param applicationContext
	 * @param newToken
	 */
	protected void writeAccessToken(Context context, Oauth2AccessToken token)
	{
		// TODO Auto-generated method stub
		if (null == context || null == token)
		{
			return;
		}

		SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_APPEND);
		Editor editor = pref.edit();
		editor.putString(KEY_UID, token.getUid());
		editor.putString(KEY_ACCESS_TOKEN, token.getToken());
		editor.putString(KEY_REFRESH_TOKEN, token.getRefreshToken());
		editor.putLong(KEY_EXPIRES_IN, token.getExpiresTime());
		editor.commit();
	}

	private void shareToQQ(Activity activity, HHShareModel model)
	{

		// 暂时分享的时候本地的图片，使用的是本地的URL。以后会开放使用URL地址的形式分享图片，到时候再使用分享Image_URL
	
		final Bundle params = new Bundle();
		params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
		params.putString(QQShare.SHARE_TO_QQ_TITLE, model.getTitle());
		params.putString(QQShare.SHARE_TO_QQ_SUMMARY, model.getDescription());
		params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, model.getLinkUrl());
		if (model.getThumpBitmap()!=null)
		{
			String path = writeBitmapToFile(model);
			params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, path);
		}else {
			params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL,model.getImageUrl());
		}
		params.putString(QQShare.SHARE_TO_QQ_APP_NAME, shareIDModel.getQqName());
		// params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, "其他附加功能");
		tencent.shareToQQ(activity, params, null);
	}

	/**
	 * 分享到微信
	 * 
	 * @param model
	 */
	private void shareToWX(final HHShareModel model, final boolean isTimeLine)
	{
		new Thread(new Runnable()
		{
			
			@Override
			public void run()
			{
				// TODO Auto-generated method stub
				try
				{
					WXWebpageObject webpage = new WXWebpageObject();
					webpage.webpageUrl = model.getLinkUrl();
//					WXImageObject imageObject=new WXImageObject();
//					imageObject.imageUrl=model.getImageUrl();
					WXMediaMessage msg = new WXMediaMessage(webpage);
					msg.title = model.getTitle();
					msg.description = model.getDescription();
					if (model.getThumpBitmap()!=null)
					{
						
						Bitmap thumbBmp = Bitmap.createScaledBitmap(model.getThumpBitmap(), THUMB_SIZE, THUMB_SIZE, true);
						model.getThumpBitmap().recycle();
						model.setThumpBitmap(thumbBmp);
						msg.setThumbImage(model.getThumpBitmap());
					}else {
						Bitmap bmp = BitmapFactory.decodeStream(new URL(model.getImageUrl()).openStream());
						Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, THUMB_SIZE, THUMB_SIZE, true);
						bmp.recycle();
						msg.setThumbImage(thumbBmp);
					}
					// msg.thumbData = Util.bmpToByteArray(thumb, true);
					SendMessageToWX.Req req = new SendMessageToWX.Req();
					req.transaction = buildTransaction("webpage");
					req.message = msg;
					req.scene = isTimeLine ? SendMessageToWX.Req.WXSceneTimeline : SendMessageToWX.Req.WXSceneSession;
					wxApi.sendReq(req);
				} catch (Exception e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
					Log.i("chenyuan", "分享错误"+e.getMessage());
				}
			}
		}).start();
		
	}

	/**
	 * 分享到微信的一个类似签名的标签
	 * 
	 * @param type
	 *            表示的是分享的类型
	 * @return
	 */
	private String buildTransaction(final String type)
	{
		return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
	}

	/**
	 * 获取分享的时候暂时保存的图片的路径
	 * 
	 * @return
	 */
	private String getTencentImageCacheFile()
	{
		String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/share/";
		return path;
	}

	/**
	 * 把Bitmap文件压缩到文件中，并返回文件的路径。压缩失败的时候返回null
	 * 
	 * @param model
	 * @return
	 */
	private String writeBitmapToFile(HHShareModel model)
	{
		if (model.getThumpBitmap() != null)
		{
			Bitmap bitmap = Bitmap.createScaledBitmap(model.getThumpBitmap(), THUMB_SIZE, THUMB_SIZE, false);
			model.getThumpBitmap().recycle();
			model.setThumpBitmap(bitmap);
			File file = new File(getTencentImageCacheFile());
			if (!file.exists())
			{
				file.mkdirs();
			}
			String savePath = getTencentImageCacheFile() + System.currentTimeMillis();
			file = new File(savePath);
			try
			{
				FileOutputStream fos = new FileOutputStream(file);
				bitmap.compress(CompressFormat.JPEG, 100, fos);
				fos.flush();
				fos.close();
				return savePath;
			} catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * 读取用户新浪微博认证的信息
	 * 
	 * @param context
	 * @return
	 */
	private Oauth2AccessToken readAccessToken(Context context)
	{
		if (null == context)
		{
			return null;
		}
		Oauth2AccessToken token = new Oauth2AccessToken();
		SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_APPEND);
		token.setUid(pref.getString(KEY_UID, ""));
		token.setToken(pref.getString(KEY_ACCESS_TOKEN, ""));
		token.setRefreshToken(pref.getString(KEY_REFRESH_TOKEN, ""));
		token.setExpiresTime(pref.getLong(KEY_EXPIRES_IN, 0));

		return token;
	}

}
