package com.huahan.hhbaseutils.adapter;

import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.huahan.hhbaseutils.HHImageUtils.Builder;
import com.huahan.hhbaseutils.HHLog;
import com.huahan.hhbaseutils.HHScreenUtils;
import com.huahan.hhbaseutils.HHViewHelper;
import com.huahan.hhbaseutils.R;
import com.huahan.hhbaseutils.imp.HHSmallBigImageImp;
import com.huahan.hhbaseutils.imp.LoadImageListener;
import com.huahan.hhbaseutils.ui.HHImageBrowerActivity;
import com.huahan.utils.view.scaleimage.PhotoViewAttacher.OnViewTapListener;
import com.huahan.utils.view.scaleimage.ScaleImageView;

import java.util.List;

public class HHImageBrowerAdapter extends PagerAdapter {

	private static final String tag = HHImageBrowerAdapter.class
			.getSimpleName();
	private HHImageBrowerActivity mActivity;
	private List<? extends HHSmallBigImageImp> mList;
	private int mDefaultImageID = 0;
	private boolean mLoadImageNotWifi=false;

	public HHImageBrowerAdapter(HHImageBrowerActivity activity,
			int defaultImageID, boolean loadImageNotWifi) {
		this.mActivity = activity;
		this.mList = mActivity.getImageList();
		this.mDefaultImageID = defaultImageID;
		this.mLoadImageNotWifi = loadImageNotWifi;
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((View) object);
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		HHSmallBigImageImp imageImp = mList.get(position);
		View view = View
				.inflate(mActivity, R.layout.hh_item_image_brower, null);
		final ScaleImageView imageView = HHViewHelper.getViewByID(view,
				R.id.hh_img_image_brower);
		ProgressBar progressBar = HHViewHelper.getViewByID(view,
				R.id.hh_pb_circle);
		imageView.setOnViewTapListener(new OnViewTapListener() {

			@Override
			public void onViewTap(View view, float x, float y) {
				if (mActivity.isClickFinish()) {
					mActivity.finish();
				} else {
					mActivity.onClick(view);
				}
			}
		});
		container.addView(view);
		setSmallImage(imageView, imageImp.getDefaultImage(),
				imageImp.getBigImage(), progressBar);
		return view;
	}

	private void setSmallImage(final ImageView imageView,
			final String smallPath, final String bigPath, final ProgressBar bar) {
		Builder.getNewInstance(imageView, smallPath)
				.defaultImageID(mDefaultImageID)
				.loadImageNotWifi(mLoadImageNotWifi)
				.listener(new LoadImageListener() {

					@Override
					public void onSizeChangedListener(int progress,
							int downloadSize) {

					}

					@Override
					public void onGetBitmap(Bitmap bitmap) {
						if (bitmap != null) {
							imageView.setImageBitmap(bitmap);
							HHLog.i(tag, "small width:" + bitmap.getWidth()
									+ ",height:" + bitmap.getHeight());
						}
						if (TextUtils.isEmpty(bigPath)
								|| bigPath.equals(smallPath)) {
							bar.setVisibility(View.GONE);
						} else {
							setBigImage(imageView, bigPath, bar);
						}
					}
				}).load();
	}

	private void setBigImage(final ImageView imageView, String bigPath,
			final ProgressBar bar) {
		Builder.getNewInstance(imageView, bigPath)
				.width(HHScreenUtils.getScreenWidth(mActivity))
				.height(HHScreenUtils.getScreenHeight(mActivity))
				.loadImageNotWifi(mLoadImageNotWifi)
				.listener(new LoadImageListener() {

					@Override
					public void onSizeChangedListener(int progress,
							int downloadSize) {

					}

					@Override
					public void onGetBitmap(Bitmap bitmap) {
						if (bitmap != null) {
							HHLog.i(tag, "big width:" + bitmap.getWidth()
									+ ",height:" + bitmap.getHeight());
							imageView.setImageBitmap(bitmap);
						}
						bar.setVisibility(View.GONE);
					}
				}).load();
	}

}
