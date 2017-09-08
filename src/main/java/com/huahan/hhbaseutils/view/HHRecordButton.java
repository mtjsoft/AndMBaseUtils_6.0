package com.huahan.hhbaseutils.view;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huahan.hhbaseutils.HHLog;
import com.huahan.hhbaseutils.HHPathUtils;
import com.huahan.hhbaseutils.HHTipUtils;
import com.huahan.hhbaseutils.R;

import java.io.File;
/**
 * setCacheDirName（）：设置语音缓存文件名，不设置，默认为huahan
 * @author xiao
 *
 */
public class HHRecordButton extends TextView
{
	/**
	 * 打印日志的tag
	 */
	private static final String tag=HHRecordButton.class.getSimpleName();
	/**
	 * 文件的缓存文件名
	 */
	private String mCacheDirName = null;
	/**
	 * 文件的名字
	 */
	private String mFileName = null;
	/**
	 * 文件的路径
	 */
	private String mFilePath = null;
	/**
	 * 录音结束的监听器
	 */

	private OnFinishedRecordListener mListener;
	/**
	 * 最小的录制时间
	 */
	private static final int MIN_INTERVAL_TIME = 800;// 2s
	/**
	 * 录制开始的时间
	 */
	private long mStartTime;

	/**
	 * 取消语音发送
	 */
	private Dialog mRecordIdicatorDialog;
	/**
	 * 显示语气强度的图片
	 */
	private static int[] mResID = { R.drawable.hh_chat_voice_size_1, R.drawable.hh_chat_voice_size_2, R.drawable.hh_chat_voice_size_3, R.drawable.hh_chat_voice_size_4, R.drawable.hh_chat_voice_size_5 };
	/**
	 * 对话框展示的View
	 */
	private static View mView;
	/**
	 * 显示语音强度的View
	 */
	private static ImageView mVoiceImageView;
	/**
	 * 音频的录制器
	 */
	private MediaRecorder mRecorder;
	/**
	 * 根据当前的振幅，显示不同的图片
	 */
	private ObtainDecibelThread mRecordSizeThread;
	/**
	 * 展示对话框显示的顶部的位置
	 */
	private int mTop = 0;
	/**
	 * 展示对话框显示的底部的位置
	 */
	private int mBottom = 0;
	/**
	 * 语音强度的消息处理器
	 */
	private Handler mVolumeHandler;
	/**
	 * 最长的录制时间
	 */
	public final static int MAX_TIME = 60;// 一分钟
	/**
	 * 录制的Layout
	 */
	private LinearLayout mRecordLayout;
	/**
	 * 取消的Layout
	 */
	private LinearLayout mCancelLayout;
	
	private boolean cancel=false; 

	public HHRecordButton(Context context)
	{
		super(context);
		init();
	}

	public HHRecordButton(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		init();
	}

	public HHRecordButton(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init();
	}

	/**
	 * 设置语音的保存地址
	 * 
	 * @param path
	 */
	public void setSavePath(String path)
	{
		mFileName = path;
	}
	/**
	 * 设置语音保存文件名
	 * @param cache_dir_name
	 */
	public void setCacheDirName(String cache_dir_name)
	{
		mCacheDirName = cache_dir_name;
	}

	/**
	 * 设置语音录制结束的监听器
	 * 
	 * @param listener
	 */
	public void setOnFinishedRecordListener(OnFinishedRecordListener listener)
	{
		mListener = listener;
	}

	/**
	 * 执行初始化操作
	 */
	private void init()
	{
		// 获取屏幕的宽度
		int screenHeight = getResources().getDisplayMetrics().heightPixels;
		mVolumeHandler = new ShowVolumeHandler();
		mRecordIdicatorDialog = new Dialog(getContext(), R.style.hh_dialog_select_photo);
		mView = View.inflate(getContext(), R.layout.hh_chat_voice_recording, null);
		// 计算dialog显示的时候，上下左右的位置
		mView.measure(0, 0);
		// 获取测量的高度
		int height = mView.getMeasuredHeight();
		// 对话框显示的顶部的位置
		mTop = (screenHeight - height) / 2;
		// 对话框显示的底部的位置
		mBottom = mTop + height;
		// 显示语音强度的ImageView
		mVoiceImageView = (ImageView) mView.findViewById(R.id.img_voice_size);
		mRecordLayout = (LinearLayout) mView.findViewById(R.id.hh_ll_chat_voice_record);
		mCancelLayout = (LinearLayout) mView.findViewById(R.id.hh_ll_chat_voice_cancel);
		// 给显示音量大小的控件设置相应的默认值
		mVoiceImageView.setImageResource(R.drawable.hh_chat_voice_size_3);
		// 把视图添加到dialog中
		mRecordIdicatorDialog.setContentView(mView, new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		// 给dialog设置消失时的事件监听器
		mRecordIdicatorDialog.setOnDismissListener(onDismiss);
		// 设置dialog显示的位置为显示在整个屏幕的中间位置
		LayoutParams lp = mRecordIdicatorDialog.getWindow().getAttributes();
		lp.gravity = Gravity.CENTER;
		setGravity(Gravity.CENTER);
		// 设置显示的文本是按住说话
		this.setText(R.string.hh_chat_voice_press_say);
		// 设置当前的背景
		setBackgroundResource(R.drawable.hh_shape_chat_voice_send_press);

	}

	@SuppressLint("ClickableViewAccessibility") 
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{

		int action = event.getAction();
		switch (action)
		{
		case MotionEvent.ACTION_DOWN:
			// 设置显示的文本是松开发送
			setText(R.string.hh_chat_voice_realse_send);
			// 改变当前的背景
			setBackgroundResource(R.drawable.hh_shape_chat_voice_send_normal);
			// 初始化对话框
			initDialogAndStartRecord();
			break;

		case MotionEvent.ACTION_UP:
			// 设置显示的文本是按住说话
			this.setText(R.string.hh_chat_voice_press_say);
			// 设置当前的背景
			setBackgroundResource(R.drawable.hh_shape_chat_voice_send_press);
			//
			if (mCancelLayout.getVisibility() == View.VISIBLE)// 取消发送
			{
				cancelRecord();
			} else
			{
				if (cancel)
				{
					Log.i("cyb", "cancelRecord");
					cancelRecord();
					cancel=false;
				}else {
					finishRecord();
				}
				
			}
			break;
		case MotionEvent.ACTION_MOVE:// 当手指移动到view外面，会cancel
			//
			if (isOver(event))// 如果当前的位置不在
			{
				if (mRecordLayout.getVisibility() == View.GONE)
				{
					mRecordLayout.setVisibility(View.VISIBLE);
					mCancelLayout.setVisibility(View.GONE);
					setText("松开 发送");
				}

			} else
			{
				if (mCancelLayout.getVisibility() == View.GONE)
				{
					mRecordLayout.setVisibility(View.GONE);
					mCancelLayout.setVisibility(View.VISIBLE);
					setText(getResources().getString(R.string.hh_chat_voice_up_cancel));
				}
			}
			break;
		case MotionEvent.ACTION_CANCEL:
			this.setText(R.string.hh_chat_voice_press_say);
			setBackgroundResource(R.drawable.hh_shape_chat_voice_send_press);
			cancelRecord();
			break;
		}

		return true;
	}

	/**
	 * 判断当前的坐标是不是在指定的范围内
	 * 
	 * @param event
	 *            滑动的事件
	 * @return
	 */
	private boolean isOver(MotionEvent event)
	{
		float y = event.getRawY();
		if (y < mBottom)// 判断当前y坐标的位置是不是在对话框底部的上方
		{
			return false;
		}
		return true;
	}
	/**
	 * 初始化对话框并且开始录制
	 */
	private void initDialogAndStartRecord()
	{

		//设置开始时间
		mStartTime = System.currentTimeMillis();
		//开始录制
		startRecording();
		//显示录制的对话框
		mRecordIdicatorDialog.show();
	}
	/**
	 * 结束录制
	 */
	private void finishRecord()
	{
		//隐藏对话框
		mRecordIdicatorDialog.dismiss();
		//获取录制的时间
		long intervalTime = System.currentTimeMillis() - mStartTime;
		HHLog.i(tag, "voice time:"+intervalTime);
		//停止录制
		stopRecording();
		//判断录制的时间是否小于最小的录制时间
		if (intervalTime < MIN_INTERVAL_TIME)
		{
			HHTipUtils.getInstance().showToast(getContext(), R.string.hh_chat_voice_time_less);
			//删除文件
			File file = new File(mFilePath);
			file.delete();
			return;
		}
		if (mListener != null)
		{
			int voiceTime = (int) (intervalTime / 1000);
			mListener.onFinishedRecord(mFilePath, voiceTime == 0 ? 1 : voiceTime);
		}
	}
	/**
	 * 取消录制
	 */
	private void cancelRecord()
	{
		stopRecording();
		//隐藏对话框
		mRecordIdicatorDialog.dismiss();
		//提示取消录音
		HHTipUtils.getInstance().showToast(getContext(), R.string.hh_chat_voice_cancel_record);
		//删除已录制的文件
		File file = new File(mFilePath);
		file.delete();
	}
	/**
	 * 开始录制
	 */
	private void startRecording()
	{
		//实例化录制器
		mRecorder = new MediaRecorder();
		//设置音频的来源是麦克风
		mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		//设置输出的格式是amr
		mRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
		//设置音频的编码格式是amr
		mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		mFileName=System.currentTimeMillis()+".amr";
		mFilePath = HHPathUtils.getInstance().getVoicePath(getContext(),mCacheDirName)+"/" + mFileName;
		//设置输出的文件
		mRecorder.setOutputFile(mFilePath);
		try
		{
			mRecorder.prepare();
			mRecorder.start();
			mRecordSizeThread = new ObtainDecibelThread();
			mRecordSizeThread.start();
		} catch (Exception e)
		{
			cancel=true;
			e.printStackTrace();
			HHTipUtils.getInstance().showToast(getContext(), R.string.hh_no_jurisdiction_to_record);
			HHLog.i(tag, "startRecording",e);
		}
		

	}
	/**
	 * 停止录制
	 */
	private void stopRecording()
	{

		try
		{

			//取消线程
			if (mRecordSizeThread != null)
			{
				mRecordSizeThread.exit();
				mRecordSizeThread = null;
			}
			//回收录制器
			if (mRecorder != null)
			{
				mRecorder.stop();
				mRecorder.release();
				mRecorder = null;
			}
			
		} catch (Exception e)
		{
			// TODO: handle exception
		}
		
	}

	private class ObtainDecibelThread extends Thread
	{

		private volatile boolean running = true;

		public void exit()
		{
			running = false;
		}

		@Override
		public void run()
		{
			while (running)
			{
				try
				{
					Thread.sleep(200);
				} catch (InterruptedException e)
				{
					e.printStackTrace();
				}
				//如果录制器为null，或者当前的状态不是录制的状态的时候，就直接退出
				if (mRecorder == null || !running)
				{
					break;
				}
				//获取录制器最大的振幅
				int x = mRecorder.getMaxAmplitude();
				//计算当前的振幅，并且发送不同的消息，显示不同的图片
				if (x != 0)
				{
					int f = (int) (10 * Math.log(x) / Math.log(10));
					if (f < 16)
						mVolumeHandler.sendEmptyMessage(0);
					else if (f < 26)
						mVolumeHandler.sendEmptyMessage(1);
					else if (f < 32)
						mVolumeHandler.sendEmptyMessage(2);
					else if (f < 38)
						mVolumeHandler.sendEmptyMessage(3);
					else
						mVolumeHandler.sendEmptyMessage(4);

				}

			}
		}

	}

	private OnDismissListener onDismiss = new OnDismissListener()
	{

		@Override
		public void onDismiss(DialogInterface dialog)
		{
			
			stopRecording();
		}
	};


	/**
	 * 显示语音的强度
	 * 
	 * @author chen3
	 * 
	 */
	static class ShowVolumeHandler extends Handler
	{
		@Override
		public void handleMessage(Message msg)
		{
			mVoiceImageView.setImageResource(mResID[msg.what]);
		}
	}
	/**
	 * 当录制结束的时候执行的监听器
	 * @author chen3
	 *
	 */
	public interface OnFinishedRecordListener
	{
		public void onFinishedRecord(String audioPath, int time);
	}

}
