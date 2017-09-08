package com.huahan.hhbaseutils.task;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.huahan.hhbaseutils.HHLog;
import com.huahan.hhbaseutils.constant.HHConstantParam;
import com.huahan.hhbaseutils.imp.HHShareQQImp;
import com.huahan.hhbaseutils.model.HHShareModel;
import com.tencent.connect.share.QQShare;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.ref.WeakReference;

public final class HHShareToQqTask implements Runnable {

	private static final String tag = HHShareToQqTask.class.getSimpleName();
	private Handler mHandler;
	private String mShareAppName;
	private HHShareModel mShareModel;
	private Tencent mTencent;
	// 保存了Activity的一个弱引用，防止因为持有Activity的引用而导致的activity引起的内存泄漏问题
	private WeakReference<Activity> mAcivityReference;
	private IUiListener mListener;

	public HHShareToQqTask(HHShareModel mShareModel, String shareAppName,
			Tencent tencent, HHShareQQImp imp) {
		this.mShareModel = mShareModel;
		this.mShareAppName = shareAppName;
		this.mTencent = tencent;
		this.mAcivityReference = new WeakReference<Activity>(imp.getActivity());
		this.mListener = imp;
	}

	/**
	 * 构造一个分享到QQ的任务
	 * 
	 * @param mShareModel
	 *            分享的内容
	 * @param shareAppName
	 *            分享到QQ的时候显示的APP名字
	 * @param tencent
	 *            分享到QQ的api
	 * @param activity
	 *            分享的当前的页面
	 * @param handler
	 *            发送消息的handle对象，可以为null
	 */
	public HHShareToQqTask(HHShareModel mShareModel, String shareAppName,
			Tencent tencent, HHShareQQImp imp, Handler handler) {
		this(mShareModel, shareAppName, tencent, imp);
		this.mHandler = handler;
	}

	@Override
	public void run() {
		final Bundle params = new Bundle();
		// 设置分享的类型
		params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE,
				QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
		// 设置分享的标题
		params.putString(QQShare.SHARE_TO_QQ_TITLE, mShareModel.getTitle());
		// 设置分享的描述
		params.putString(QQShare.SHARE_TO_QQ_SUMMARY,
				mShareModel.getDescription());
		// 设置分享的链接的地址啊
		params.putString(QQShare.SHARE_TO_QQ_TARGET_URL,
				mShareModel.getLinkUrl());
		if (mShareModel.getThumpBitmap() != null) {
			// 如果bitmap不是null，则需要把bitmap保存到本地，然后重新设置分享的本地路径
			String path = writeBitmapToFile(mShareModel);
			if (TextUtils.isEmpty(path)) {
				sendHandlerMessage(HHConstantParam.SEND_SHARE_REQUEST_FAILED,
						"file path is empty or null");
				return;
			}
			params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, path);
		} else {
			// 如果bitmap为null，则需要设置分享的图片的url地址
			params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL,
					mShareModel.getImageUrl());
		}
		// 设置分享到QQ的时候显示的应用的名称
		params.putString(QQShare.SHARE_TO_QQ_APP_NAME, mShareAppName);
		// 分享的时候一定要先判断当前保存的activity时候还存在
		if (mAcivityReference.get() != null) {
			mAcivityReference.get().runOnUiThread(new Runnable() {

				@Override
				public void run() {
					mTencent.shareToQQ(mAcivityReference.get(), params,
							mListener);
				}
			});
			sendHandlerMessage(HHConstantParam.SEND_SHARE_REQUEST_SUCCESS, null);
		} else {
			sendHandlerMessage(HHConstantParam.SEND_SHARE_REQUEST_FAILED,
					"activity is null");
		}
	}

	private void sendHandlerMessage(int state, String errorMsg) {
		if (mHandler == null) {
			return;
		}
		Message msg = mHandler.obtainMessage();
		msg.what = state;
		msg.arg1 = HHConstantParam.SHARE_TYPE_QQ;
		if (!TextUtils.isEmpty(errorMsg)) {
			msg.obj = errorMsg;
		}
		mHandler.sendMessage(msg);
	}

	/**
	 * 把Bitmap文件写入到本地的路径当中
	 * 
	 * @param model
	 *            分享的Model
	 * @return 写入成功的时候返回这个文件的路径，否则的话返回null
	 */
	private String writeBitmapToFile(HHShareModel model) {
		Bitmap mBitmap=model.getThumpBitmap();
		if (mBitmap==null) {
			return null;
		}
		Bitmap bitmap = null;
		if (mBitmap.getWidth()==HHConstantParam.SHARE_THUMP_SIZE&&mBitmap.getHeight()==HHConstantParam.SHARE_THUMP_SIZE) {
			bitmap=mBitmap;
		}else {
			bitmap=Bitmap.createScaledBitmap(mBitmap,
					HHConstantParam.SHARE_THUMP_SIZE,
					HHConstantParam.SHARE_THUMP_SIZE, false);
			mBitmap.recycle();
		}
		model.setThumpBitmap(bitmap);
		File file = new File(HHConstantParam.DEFAULT_CACHE_SHARE);
		if (!file.exists()) {
			file.mkdirs();
		}
		String savePath = HHConstantParam.DEFAULT_CACHE_SHARE
				+ System.currentTimeMillis();
		file = new File(savePath);
		try {
			FileOutputStream fos = new FileOutputStream(file);
			bitmap.compress(CompressFormat.JPEG, 100, fos);
			fos.flush();
			fos.close();
			return savePath;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			HHLog.i(tag, "writeBitmapToFile error is:" + e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

}
