package com.huahan.hhbaseutils.task;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.huahan.hhbaseutils.HHLog;
import com.huahan.hhbaseutils.constant.HHConstantParam;
import com.huahan.hhbaseutils.model.HHShareModel;
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.WebpageObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.utils.Utility;

import java.lang.ref.WeakReference;
import java.net.URL;

/**
 * 分享到新浪微博的异步任务
 * @author yuan
 *
 */
public final class HHShareToSinaTask implements Runnable
{
	// 保存用户新浪微博认证的信息
	private static final String PREFERENCES_NAME = "com_weibo_sdk_android";
	private static final String KEY_UID = "uid";
	private static final String KEY_ACCESS_TOKEN = "access_token";
	private static final String KEY_EXPIRES_IN = "expires_in";
	private static final String KEY_REFRESH_TOKEN = "refresh_token";
	private static final String tag=HHShareToSinaTask.class.getSimpleName();
	private HHShareModel mShareModel;
	private String mSinaAppID;
	private IWeiboShareAPI mSinaApi;
	private WeakReference<Activity> mActivityReference;
	private Handler mHandler;
	
	
	public HHShareToSinaTask(HHShareModel shareModel,String sinaAppID,IWeiboShareAPI weiboApi,Activity activity)
	{
		this.mShareModel=shareModel;
		this.mSinaAppID=sinaAppID;
		HHLog.i(tag, "sina weibo app id is:"+sinaAppID);
		this.mSinaApi=weiboApi;
		this.mActivityReference=new WeakReference<Activity>(activity);
		mSinaApi.registerApp();
	}
	public HHShareToSinaTask(HHShareModel shareModel,String sinaAppID,IWeiboShareAPI weiboApi,Activity activity,Handler handler)
	{
		this(shareModel, sinaAppID, weiboApi, activity);
		this.mHandler=handler;
	}
	@Override
	public void run()
	{
		switch (mShareModel.getSinaShareType()) {
		case 0://分享网页
			shareWebpage();
			break;
		case 1://分享图片
			shareImage();
			break;

		default:
			break;
		}
	}
	/**
	 * 分享图片
	 */
	private void shareImage()
	{
		try
        {
            // 创建一个消息,这个消息就是新浪微博分享的时候发送的消息
            WeiboMultiMessage message = new WeiboMultiMessage();
            // 消息可以写到一个图片的信息
            ImageObject imageObject = new ImageObject();
            imageObject.actionUrl = mShareModel.getLinkUrl();
            imageObject.description = mShareModel.getDescription();
            imageObject.title = mShareModel.getTitle();
            imageObject.identify = Utility.generateGUID();

            if (mShareModel.getThumpBitmap() != null)
            {
                // 如果分享的内容中有Bitmap，则需要根据这个bitmap创建一个缩小的Bitmap
            	Bitmap mBitmap=mShareModel.getThumpBitmap();
            	Bitmap bitmap = null;
            	if (mBitmap.getWidth()==HHConstantParam.SHARE_THUMP_SIZE&&mBitmap.getHeight()==HHConstantParam.SHARE_THUMP_SIZE) {
					bitmap=mBitmap;
				}else {
					bitmap=Bitmap.createScaledBitmap(
	            			mShareModel.getThumpBitmap(),
	            			HHConstantParam.SHARE_THUMP_SIZE,
	            			HHConstantParam.SHARE_THUMP_SIZE, false);
					mBitmap.recycle();
				}
                mShareModel.setThumpBitmap(bitmap);
                // 设置图片对应显示的缩略图
                imageObject.setImageObject(bitmap);
            } else
            {
                // 如果分享的内容中没有Bitmap。则需要根据图片的地址来下载这个图片，并创建这个图片的缩略图
                Bitmap bitmap = BitmapFactory.decodeStream(new URL(mShareModel
                        .getImageUrl()).openStream());
                Bitmap thumbBitmap = null;
                if (bitmap.getWidth()==HHConstantParam.SHARE_THUMP_SIZE&&bitmap.getHeight()==HHConstantParam.SHARE_THUMP_SIZE) {
					thumbBitmap=bitmap;
				}else {
					thumbBitmap=Bitmap.createScaledBitmap(bitmap,
	                        HHConstantParam.SHARE_THUMP_SIZE,
	                        HHConstantParam.SHARE_THUMP_SIZE, false);
					bitmap.recycle();
				}
                // 设置图片对应显示的缩略图
                imageObject.setImageObject(thumbBitmap);
            }
            // 设置消息携带的图片信息
            message.imageObject = imageObject;
            // 创建一个请求
            SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
            // 用transaction唯一标识一个请求
            request.transaction = String.valueOf(System.currentTimeMillis());
            // 设置这个请求发送的消息
            request.multiMessage = message;
            if (mActivityReference.get() == null)
            {
                sendHandlerMessgae(HHConstantParam.SEND_SHARE_REQUEST_FAILED,
                        "activity is null");
                return;
            }
            AuthInfo authInfo = new AuthInfo(mActivityReference.get(),
                    mSinaAppID, "http://www.sina.com", "");
            Oauth2AccessToken accessToken = readAccessToken(mActivityReference
                    .get().getApplicationContext());
            String token = "";
            if (accessToken != null)
            {
                token = accessToken.getToken();
            }
            if (mSinaApi.isWeiboAppInstalled())
            {
                HHLog.i(tag, "sina weibo is installed");
                mSinaApi.sendRequest(mActivityReference.get(), request);
            } else
            {
                HHLog.i(tag, "sina weibo is uninstalled");
                mSinaApi.sendRequest(mActivityReference.get(), request,
                        authInfo, token, new WeiboAuthListener()
                        {
                            @Override
                            public void onWeiboException(WeiboException arg0)
                            {
                                HHLog.i(tag,
                                        "sina auth exception:"
                                                + arg0.getMessage());
                            }

                            @Override
                            public void onComplete(Bundle bundle)
                            {

                                String code = bundle.getString("code", "");
                                HHLog.i(tag,
                                        "sina auth complete is error and code is:"
                                                + code);
                                if (mActivityReference.get() == null)
                                {
                                    sendHandlerMessgae(
                                            HHConstantParam.SEND_SHARE_REQUEST_FAILED,
                                            "activity is null");
                                    return;
                                }
                                Oauth2AccessToken newToken = Oauth2AccessToken
                                        .parseAccessToken(bundle);
                                writeAccessToken(mActivityReference.get()
                                        .getApplicationContext(), newToken);
                            }

                            @Override
                            public void onCancel()
                            {
                                HHLog.i(tag, "sina auth canceled");
                            }
                        });
            }

            sendHandlerMessgae(HHConstantParam.SEND_SHARE_REQUEST_SUCCESS, null);
        } catch (Exception e)
        {
            e.printStackTrace();
            HHLog.i(tag, "分享到新浪微博error：" + e.getMessage());
            sendHandlerMessgae(HHConstantParam.SEND_SHARE_REQUEST_FAILED,
                    e.getMessage());
        }
	}
	/**
	 * 分享网页
	 */
	private void shareWebpage()
	{
		try
		{
			// 创建一个消息,这个消息就是新浪微博分享的时候发送的消息
			WeiboMultiMessage message = new WeiboMultiMessage();
			//消息可以写到一个图片的信息
			ImageObject imageObject = new ImageObject();
//			imageObject.actionUrl=mShareModel.getLinkUrl();
//			imageObject.description=mShareModel.getDescription();
//			imageObject.title=mShareModel.getTitle();
//			imageObject.identify=Utility.generateGUID();
			//消息携带的链接的信息
			WebpageObject mediaObject = new WebpageObject();
			//定义这个链接的表示信息
			mediaObject.identify = Utility.generateGUID();
			//链接的标题信息
			mediaObject.title = mShareModel.getTitle();
			//链接的描述信息
			mediaObject.description = mShareModel.getDescription();
			//链接指向的地址
			mediaObject.actionUrl = mShareModel.getLinkUrl();
			//链接默认显示的内容
			mediaObject.defaultText = mShareModel.getTitle();
			if (mShareModel.getThumpBitmap()!=null)
			{
				//如果分享的内容中有Bitmap，则需要根据这个bitmap创建一个缩小的Bitmap
				Bitmap mBitmap=mShareModel.getThumpBitmap();
				Bitmap bitmap = null;
				if (mBitmap.getWidth()==HHConstantParam.SHARE_THUMP_SIZE&&mBitmap.getHeight()==HHConstantParam.SHARE_THUMP_SIZE) {
					bitmap=mBitmap;
				}else {
					bitmap=Bitmap.createScaledBitmap(mBitmap, HHConstantParam.SHARE_THUMP_SIZE, HHConstantParam.SHARE_THUMP_SIZE, false);
					mBitmap.recycle();
				}
				mShareModel.setThumpBitmap(bitmap);
				//设置链接对应显示的缩略图
				mediaObject.setThumbImage(bitmap);
				//设置图片对应显示的缩略图
				imageObject.setImageObject(bitmap);
			}else {
				//如果分享的内容中没有Bitmap。则需要根据图片的地址来下载这个图片，并创建这个图片的缩略图
				Bitmap bitmap=BitmapFactory.decodeStream(new URL(mShareModel.getImageUrl()).openStream());
				Bitmap thumbBitmap=null;
				if (bitmap.getWidth()==HHConstantParam.SHARE_THUMP_SIZE&&bitmap.getHeight()==HHConstantParam.SHARE_THUMP_SIZE) {
					thumbBitmap=bitmap;
				}else {
					thumbBitmap=Bitmap.createScaledBitmap(bitmap, HHConstantParam.SHARE_THUMP_SIZE, HHConstantParam.SHARE_THUMP_SIZE, false);
					bitmap.recycle();
				}
				//设置链接对应显示的缩略图
				mediaObject.setThumbImage(thumbBitmap);
				//设置图片对应显示的缩略图
				imageObject.setImageObject(thumbBitmap);
			}
			//设置消息携带的多媒体信息
			message.mediaObject = mediaObject;
			//设置消息携带的图片信息
			message.imageObject=imageObject;
			// 创建一个请求
			SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
			// 用transaction唯一标识一个请求
			request.transaction = String.valueOf(System.currentTimeMillis());
			//设置这个请求发送的消息
			request.multiMessage = message;
			if (mActivityReference.get()==null)
			{
				sendHandlerMessgae(HHConstantParam.SEND_SHARE_REQUEST_FAILED, "activity is null");
				return ;
			}
			AuthInfo authInfo = new AuthInfo(mActivityReference.get(), mSinaAppID, "http://www.sina.com", "");
//			AuthInfo authInfo = new AuthInfo(mActivityReference.get(), mSinaAppID, "https://api.weibo.com/oauth2/default.html", "email,direct_messages_read,direct_messages_write,friendships_groups_read,friendships_groups_write,statuses_to_me_read,follow_app_official_microblog,invitation_write");
			Oauth2AccessToken accessToken = readAccessToken(mActivityReference.get().getApplicationContext());
			String token = "";
			if (accessToken != null)
			{
				token = accessToken.getToken();
			}
			if (mSinaApi.isWeiboAppInstalled())
			{
				HHLog.i(tag, "sina weibo is installed");
				mSinaApi.sendRequest(mActivityReference.get(), request);
			}else {
				HHLog.i(tag, "sina weibo is uninstalled");
				mSinaApi.sendRequest(mActivityReference.get(), request, authInfo, token, new WeiboAuthListener()
				{
					@Override
					public void onWeiboException(WeiboException arg0)
					{
						HHLog.i(tag, "sina auth exception:"+arg0.getMessage());
					}
					@Override
					public void onComplete(Bundle bundle)
					{
						
						String code = bundle.getString("code", "");
						HHLog.i(tag, "sina auth complete is error and code is:"+code);
						if (mActivityReference.get()==null)
						{
							sendHandlerMessgae(HHConstantParam.SEND_SHARE_REQUEST_FAILED, "activity is null");
							return ;
						}
						Oauth2AccessToken newToken = Oauth2AccessToken.parseAccessToken(bundle);
						writeAccessToken(mActivityReference.get().getApplicationContext(), newToken);
					}
					@Override
					public void onCancel()
					{
						HHLog.i(tag, "sina auth canceled");
					}
				});
			}
			
			sendHandlerMessgae(HHConstantParam.SEND_SHARE_REQUEST_SUCCESS, null);
		} catch (Exception e)
		{
			e.printStackTrace();
			HHLog.i(tag, "分享到新浪微博error："+e.getMessage());
			sendHandlerMessgae(HHConstantParam.SEND_SHARE_REQUEST_FAILED, e.getMessage());
		}
	}
	/**
	 * 在请求发送成功或者失败的时候发送消息
	 * @param state					发送的状态
	 * @param errorMsg				错误的消息
	 */
	private void sendHandlerMessgae(int state,String errorMsg)
	{
		if (mHandler==null)
		{
			return ;
		}
		Message msg = mHandler.obtainMessage();
		msg.what=state;
		msg.arg1=HHConstantParam.SHARE_TYPE_SINA;
		if (!TextUtils.isEmpty(errorMsg))
		{
			msg.obj=errorMsg;
		}
		mHandler.sendMessage(msg);
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
	/**
	 * 把用户新浪认证的信息保存到本地
	 * 
	 * @param applicationContext
	 * @param newToken
	 */
	protected void writeAccessToken(Context context, Oauth2AccessToken token)
	{
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

}
