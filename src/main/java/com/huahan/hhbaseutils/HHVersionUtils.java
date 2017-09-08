package com.huahan.hhbaseutils;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.huahan.hhbaseutils.imp.HHVersionImp;
import com.huahan.hhbaseutils.imp.OnVersionItemClickedListener;
import com.huahan.hhbaseutils.model.HHVersionConvertImp;
import com.huahan.hhbaseutils.model.HHVersionParam;

import java.lang.ref.WeakReference;
import java.util.HashMap;

public class HHVersionUtils
{
	public static final int GET_NEW_VERSION = 2000;
	public static final int NOTIFY_ID = 10000;
	public static final int APP_DOWNLOAD_COMPLETE = 2001;
	public static final int APP_DOWNLOAD_ING = 2002;
	private static final int APP_DOWNLOAD_FAILED = 2003;
	
	private static HHVersionParam mVersionParam;
	/**
	 * 私有化构造函数
	 */
	private HHVersionUtils(){}
	/**
	 * 构造器，用来获取一个HHVersionUtils
	 * @author yuan
	 *
	 */
	public static class Builder
	{
		private HHVersionParam param=new HHVersionParam();
		/**
		 * 接口地址
		 * 
		 * @param address
		 */
		private Builder(Context context)
		{
			param.context=new WeakReference<Context>(context);
		}
		/**
		 * 构造一个Builder对象，该对象可用来显示版本更新的对话框
		 * @param address
		 * @param clazz
		 * @return
		 */
		public static Builder getNewInstance(Context context)
		{
			return new Builder(context);
		}
		/**
		 * 设置当版本更新的Item被点击的时候执行的监听器
		 * @param listener
		 * @return
		 */
		public Builder listener(OnVersionItemClickedListener listener)
		{
			param.listener=listener;
			return this;
		}
		/**
		 * 设置一个转换器，自己获取数据，并且解析，如果不设置的话，就使用默认的获取和解析方式
		 * @param convertImp
		 * @return
		 */
		public Builder convertToModel(HHVersionConvertImp convertImp)
		{
			param.convertFactory=convertImp;
			return this;
		}
		/**
		 * 设置加载数据的时候的参数
		 * @param paramMap
		 * @return
		 */
		public Builder paramMap(HashMap<String, String> paramMap)
		{
			param.paramMap.putAll(paramMap);
			return this;
		}
		/**
		 * 显示版本更新
		 */
		public void show()
		{
			loadVersionInfo(param);
		}
		/**
		 * 显示加载的对话框等信息，默认是显示的
		 * @param showLoadding
		 * @return
		 */
		public Builder showLoadding(boolean showLoadding)
		{
			param.showLoadding=showLoadding;
			return this;
		}
	}
	/**
	 * 加载版本信息
	 */
	private static void loadVersionInfo(HHVersionParam param)
	{
		mVersionParam=param;
		if (param.showLoadding)
		{
			HHTipUtils.getInstance().showProgressDialog(mVersionParam.context.get(), R.string.hh_version_load_info);
		}
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				HHVersionImp model=null;
				if (mVersionParam.convertFactory!=null)
				{
					model=mVersionParam.convertFactory.convertToVersionModel();
				}
			}
		}).start();
	}
	private static Handler mHandler=new Handler()
	{

		@Override
		public void handleMessage(Message msg)
		{
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case APP_DOWNLOAD_ING:
//				RemoteViews contentViewing = notification.contentView;
//				contentViewing.setTextViewText(processTextID,
//						getProgressString((Integer) msg.obj));
//				contentViewing.setProgressBar(processBarID, 100,
//						(Integer) msg.obj, false);
//				manager.notify(NOTIFY_ID, notification);
				break;
			case GET_NEW_VERSION:
				HHTipUtils.getInstance().dismissProgressDialog();
				if (msg.obj == null) {
					if (mVersionParam.showLoadding) {
						int errorID = HHSystemUtils.getResourceID(mVersionParam.context.get(),
								"net_error", "string");
						HHTipUtils.getInstance().showToast(mVersionParam.context.get(), errorID);
					}
				} else {
					HHVersionImp versionModel = (HHVersionImp) msg.obj;
					int code = versionModel.getVersionCode();
					if (TextUtils.isEmpty(code+"")) {
						if (mVersionParam.showLoadding) {
							int versionNullID = HHSystemUtils.getResourceID(
									mVersionParam.context.get(), "version_null", "string");
							HHTipUtils.getInstance().showToast(mVersionParam.context.get(),
									versionNullID);
						}
					} else {
						int oldCode = HHAppUtils.getVerCode(mVersionParam.context.get(), null);
						int newCode = versionModel.getVersionCode();
						if (newCode > oldCode) {
//							getVersionUpdateDialog(versionModel);
						} else if (mVersionParam.showLoadding) {
							int newVersionID = HHSystemUtils.getResourceID(
									mVersionParam.context.get(), "new_last_version", "string");
							HHTipUtils.getInstance().showToast(mVersionParam.context.get(),
									newVersionID);
						}
					}
				}
				break;
			case APP_DOWNLOAD_COMPLETE:
//				manager.cancel(NOTIFY_ID);
//				int downFinishID = HHSystemUtils.getResourceID(context,
//						"down_finish", "string");
//				int clickID = HHSystemUtils.getResourceID(context,
//						"click_install", "string");
//				Intent intent = new Intent(Intent.ACTION_VIEW);
//				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//				intent.setDataAndType(Uri.parse("file://" + savePath),
//						"application/vnd.android.package-archive");
//				PendingIntent pendingIntent = PendingIntent.getActivity(
//						context.getApplicationContext(), 0, intent,
//						PendingIntent.FLAG_UPDATE_CURRENT);
//				Notification notification = new Notification.Builder(context)
//						.setAutoCancel(true)
//						.setContentTitle(context.getString(downFinishID))
//						.setContentText(context.getString(clickID))
//						.setContentIntent(pendingIntent)
//						.setSmallIcon(R.drawable.logo)
//						.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.logo))
//						.setWhen(System.currentTimeMillis()).getNotification();
//				manager.notify(NOTIFY_ID, notification);
				break;
			case APP_DOWNLOAD_FAILED:
//				manager.cancel(NOTIFY_ID);
//				HHTipUtils.getInstance().showToast(VersionUtils.this.context,R.string.down_faild);
				break;
			default:
				break;
			}
		}
		
	};
	
	/**
	 * 获取一个新版本更新的提示的dialog
	 * 
	 * @param model
	 * 
	 */
	private void getVersionUpdateDialog(HHVersionImp model) {
//		DialogUtils.showOptionDialog(context,
//				context.getString(R.string.version_update),
//				model.getUpdate_content(), new OnOptionDialogClickListener() {
//
//					@Override
//					public void onClick(Dialog dialog, View view) {
//						dialog.dismiss();
//						showNotification();
//					}
//				}, new OnOptionDialogClickListener() {
//
//					@Override
//					public void onClick(Dialog dialog, View view) {
//						dialog.dismiss();
//					}
//				}, true);
	}
	
}
