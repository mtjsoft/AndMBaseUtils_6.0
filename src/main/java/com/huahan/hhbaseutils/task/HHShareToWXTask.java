package com.huahan.hhbaseutils.task;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.huahan.hhbaseutils.HHStreamUtils;
import com.huahan.hhbaseutils.constant.HHConstantParam;
import com.huahan.hhbaseutils.model.HHShareModel;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXImageObject;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;

import java.net.URL;

/**
 * 分享到微信的异步任务<br/>
 * 发送请求成功的时候，如果handler不是null，则会发送一个消息，；如果发送请求失败，同样如果handler不是null，也会发送一个消息，
 * 消息的what值分表是HHConstantParam
 * .SEND_SHARE_REQUEST_SUCCESS,和HHConstantParam.SEND_SHARE_REQUEST_FAILED;<br/>
 * 如果调用请求失败的情况下，可以根据msg.obj获取到失败的原因;如果需要获取请求的是那个平台，则可以通过消息的arg1值来获取，
 * 对应的值在HHConstantParam中定义，
 * 分别是SHARE_TYPE_WX,SHARE_TYPE_TIMELINE,SHARE_TYPE_QQ,SHARE_TYPE_SINA
 * 
 * @author yuan
 *
 */
public final class HHShareToWXTask implements Runnable {

	private static final String tag = HHShareToWXTask.class.getSimpleName();
	private HHShareModel mShareModel;
	private boolean mIsTimeLine;
	private IWXAPI mWXApi;
	private Handler mHandler;

	public HHShareToWXTask(HHShareModel mShareModel, boolean isTimeLine,
			IWXAPI api, Handler handler) {
		this.mShareModel = mShareModel;
		this.mIsTimeLine = isTimeLine;
		this.mWXApi = api;
		this.mHandler = handler;
	}

	public HHShareToWXTask(HHShareModel mShareModel, boolean isTimeLine,
			IWXAPI api) {
		this.mShareModel = mShareModel;
		this.mIsTimeLine = isTimeLine;
		this.mWXApi = api;
	}

	@Override
	public void run() {
		switch (mShareModel.getWxShareType()) {
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
	private void shareImage() {
		try {

			// 设置分享的时候显示的图片
			if (TextUtils.isEmpty(mShareModel.getImageUrl())) {
				// 如果mShareModel中包含了一个缩略图，则使用这个缩略图创建一个指定了狂傲的缩略图
				Bitmap mBitmap=mShareModel.getThumpBitmap();
				if (mBitmap==null) {
					return;
				}
				Bitmap thumbBmp = null;
				if (mBitmap.getWidth()==HHConstantParam.SHARE_THUMP_SIZE&&mBitmap.getHeight()==HHConstantParam.SHARE_THUMP_SIZE) {
					thumbBmp=mBitmap;
				}else {
					thumbBmp=Bitmap.createScaledBitmap(mBitmap,
							HHConstantParam.SHARE_THUMP_SIZE,
							HHConstantParam.SHARE_THUMP_SIZE, true);
					mBitmap.recycle();
				}
				// 如果这个指定大小的缩略图创建成功了，为了节省内存，会回收mShareModel中这个比较大的缩略图
				// 重新设置mShareModel中的缩略图
				mShareModel.setThumpBitmap(thumbBmp);
			} else {
				// 如果mShareModel中没有缩略图，只有一个缩略图的地址，则需要根据这个地址了下载这个缩略图，因此使用的这个缩略图的地址对应的这个
				// 图片不应该适用过大的图片
				Bitmap bmp = BitmapFactory.decodeStream(new URL(mShareModel
						.getImageUrl()).openStream());
				if (bmp == null) {
					bmp = mShareModel.getThumpBitmap();
				}
				// 根据从网上获取的图片创建一个指定大小的缩略图
				Bitmap thumbBmp = null;
				if (bmp.getWidth()==HHConstantParam.SHARE_THUMP_SIZE&&bmp.getHeight()==HHConstantParam.SHARE_THUMP_SIZE) {
					thumbBmp=bmp;
				}else {
					//分享图片大小不超过32k,字节32*1028
					thumbBmp=Bitmap.createScaledBitmap(bmp,
							HHConstantParam.SHARE_THUMP_SIZE,
							HHConstantParam.SHARE_THUMP_SIZE, true);
					// 回收这个比较大的图片
					bmp.recycle();
				}
				// 设置消息得到缩略图
				mShareModel.setThumpBitmap(thumbBmp);
			}
			WXImageObject imgObj = new WXImageObject(
					mShareModel.getThumpBitmap());

			WXMediaMessage msg = new WXMediaMessage();
			msg.mediaObject = imgObj;
			msg.thumbData = HHStreamUtils.convertBitmapToByteArray(mShareModel.getThumpBitmap(), true);
			// 实例化一个发送消息的请求
			SendMessageToWX.Req req = new SendMessageToWX.Req();
			// 设置这个请求的一个唯一的标识信息
			req.transaction = buildTransaction("img");
			// 设置这个请求发送的消息
			req.message = msg;
			// 设置这个请求应用的场景，这个场景确定了分享到微信朋友圈还是微信的好友
			req.scene = mIsTimeLine ? SendMessageToWX.Req.WXSceneTimeline
					: SendMessageToWX.Req.WXSceneSession;
			// 调用微信的api发送这个请求
			mWXApi.sendReq(req);
			sendHandlerMessage(HHConstantParam.SEND_SHARE_REQUEST_SUCCESS, null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.i(tag, "shareToWX分享错误" + e.getMessage());
			sendHandlerMessage(HHConstantParam.SEND_SHARE_REQUEST_FAILED,
					e.getMessage());
		}
	}

	/**
	 * 分享网页
	 */
	private void shareWebpage() {
		try {
			// 分享的是一个网页
			WXWebpageObject webpage = new WXWebpageObject();
			// 设置网页分享的地址
			webpage.webpageUrl = mShareModel.getLinkUrl();
			// 分享的消息
			WXMediaMessage msg = new WXMediaMessage(webpage);
			// 分享的标题
			msg.title = mShareModel.getTitle();
			// 分享的描述信息
			msg.description = mShareModel.getDescription();
			// 设置分享的时候显示的图片
			if (TextUtils.isEmpty(mShareModel.getImageUrl())) {
				// 如果mShareModel中包含了一个缩略图，则使用这个缩略图创建一个指定了狂傲的缩略图
				Bitmap mBitmap=mShareModel.getThumpBitmap();
				if (mBitmap==null) {
					return;
				}
				Bitmap thumbBmp = null;
				if (mBitmap.getWidth()==HHConstantParam.SHARE_THUMP_SIZE&&mBitmap.getHeight()==HHConstantParam.SHARE_THUMP_SIZE) {
					thumbBmp=mBitmap;
				}else {
					thumbBmp=Bitmap.createScaledBitmap(mBitmap,
							HHConstantParam.SHARE_THUMP_SIZE,
							HHConstantParam.SHARE_THUMP_SIZE, true);
					// 如果这个指定大小的缩略图创建成功了，为了节省内存，会回收mShareModel中这个比较大的缩略图
					mBitmap.recycle();
				}
				// 重新设置mShareModel中的缩略图
				mShareModel.setThumpBitmap(thumbBmp);
				// 设置分享的消息使用的缩略图
				msg.setThumbImage(mShareModel.getThumpBitmap());
			} else {
				// 如果mShareModel中没有缩略图，只有一个缩略图的地址，则需要根据这个地址了下载这个缩略图，因此使用的这个缩略图的地址对应的这个
				// 图片不应该适用过大的图片
				Bitmap bmp = BitmapFactory.decodeStream(new URL(mShareModel
						.getImageUrl()).openStream());
				if (bmp == null) {
					bmp = mShareModel.getThumpBitmap();
				}
				// 根据从网上获取的图片创建一个指定大小的缩略图
				Bitmap thumbBmp = null;
				if (bmp.getWidth()==HHConstantParam.SHARE_THUMP_SIZE&&bmp.getHeight()==HHConstantParam.SHARE_THUMP_SIZE) {
					thumbBmp=bmp;
				}else {
					thumbBmp=Bitmap.createScaledBitmap(bmp,
							HHConstantParam.SHARE_THUMP_SIZE,
							HHConstantParam.SHARE_THUMP_SIZE, true);
					// 回收这个比较大的图片
					bmp.recycle();
				}
				// 设置消息得到缩略图
				msg.setThumbImage(thumbBmp);
			}
			// 实例化一个发送消息的请求
			SendMessageToWX.Req req = new SendMessageToWX.Req();
			// 设置这个请求的一个唯一的标识信息
			req.transaction = buildTransaction("webpage");
			// 设置这个请求发送的消息
			req.message = msg;
			// 设置这个请求应用的场景，这个场景确定了分享到微信朋友圈还是微信的好友
			req.scene = mIsTimeLine ? SendMessageToWX.Req.WXSceneTimeline
					: SendMessageToWX.Req.WXSceneSession;
			// 调用微信的api发送这个请求
			mWXApi.sendReq(req);
			sendHandlerMessage(HHConstantParam.SEND_SHARE_REQUEST_SUCCESS, null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.i(tag, "shareToWX分享错误" + e.getMessage());
			sendHandlerMessage(HHConstantParam.SEND_SHARE_REQUEST_FAILED,
					e.getMessage());
		}
	}

	/**
	 * 发送消息
	 * 
	 * @param state
	 *            发送求情的状态
	 * @param errorMsg
	 *            发送失败的时候的错误消息；发送成功的时候这个值传null即可
	 */
	private void sendHandlerMessage(int state, String errorMsg) {
		if (mHandler == null) {
			return;
		}
		Message msg = mHandler.obtainMessage();
		msg.what = state;
		msg.arg1 = mIsTimeLine ? HHConstantParam.SHARE_TYPE_TIMELINE
				: HHConstantParam.SHARE_TYPE_WX;
		if (!TextUtils.isEmpty(errorMsg)) {
			msg.obj = errorMsg;
		}
		mHandler.sendMessage(msg);
	}

	/**
	 * 分享到微信的一个类似签名的标签
	 * 
	 * @param type
	 *            表示的是分享的类型
	 * @return
	 */
	private String buildTransaction(final String type) {
		return (type == null) ? String.valueOf(System.currentTimeMillis())
				: type + System.currentTimeMillis();
	}

}
