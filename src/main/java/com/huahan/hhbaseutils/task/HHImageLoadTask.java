package com.huahan.hhbaseutils.task;

import android.graphics.Bitmap;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

import com.huahan.hhbaseutils.HHImageUtils;
import com.huahan.hhbaseutils.HHLog;
import com.huahan.hhbaseutils.manager.HHAnimBuilder;
import com.huahan.hhbaseutils.model.HHImageParam;

import java.lang.ref.WeakReference;

public class HHImageLoadTask extends HHAsyncTask<Void, Integer, Bitmap>
{

	private static final String tag=HHImageLoadTask.class.getSimpleName();
	private HHImageUtils imageUtils;
	private WeakReference<ImageView> mReference;
	private HHImageParam param;

	public HHImageLoadTask(HHImageUtils imageUtils, ImageView imageView, HHImageParam params)
	{

		mReference = new WeakReference<ImageView>(imageView);
		this.param = params;
		this.imageUtils = imageUtils;
	}

	@Override
	protected Bitmap doInBackground(Void... params)
	{
		// TODO Auto-generated method stub
		Bitmap bitmap = null;
		// 如果当前的路径是一个HTTP地址
		if (param.widthDes < 1 || param.heightDes < 1)
		{
			ImageView imageView = mReference.get();
			if (imageView != null)
			{
				LayoutParams layoutParams = imageView.getLayoutParams();
				HHLog.i(tag, "image layout params is:"+layoutParams);
				if (layoutParams!=null&&layoutParams.width > 1&&layoutParams.height>1)
				{
					param.widthDes = layoutParams.width;
					param.heightDes = layoutParams.height;
				} 
			} else
			{
				return null;
			}
		}
		HHLog.i(tag, "widthDes:"+param.widthDes+",heightDes:"+param.heightDes);
		bitmap = imageUtils.getBitmap(param);
		return bitmap;
	}
	@Override
	protected void onCancelled()
	{
		// TODO Auto-generated method stub
		super.onCancelled();
	}
	@Override
	protected void onPostExecute(Bitmap bitmap)
	{
		// TODO Auto-generated method stub
		if (isCancelled())
		{

			bitmap = null;
		}
		if (bitmap!=null&&bitmap.isRecycled())
		{
			bitmap=null;
		}
		if (param.listener!=null)
		{
			param.listener.onGetBitmap(bitmap);
		}else {
			if (mReference != null && bitmap != null)
			{
				final ImageView imageView = mReference.get();
				final HHImageLoadTask bitmapWorkerTask = imageUtils.getBitmapWorkerTask(imageView);
				if (this == bitmapWorkerTask && imageView != null&&!bitmap.isRecycled())
				{
					imageView.setImageBitmap(bitmap);
					HHAnimBuilder.buildAlphAnimation(imageView).start();
				}
			}
		}

	}

	public String getFilePath()
	{
		return param.filePath;
	}

}
