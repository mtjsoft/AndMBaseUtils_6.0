package com.huahan.hhbaseutils.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.huahan.hhbaseutils.HHDensityUtils;
import com.huahan.hhbaseutils.HHImageUtils;
import com.huahan.hhbaseutils.HHLog;
import com.huahan.hhbaseutils.HHScreenUtils;
import com.huahan.hhbaseutils.R;
import com.huahan.hhbaseutils.imp.OnItemClickListener;
import com.huahan.hhbaseutils.model.HHSelectPhotoHolder;
import com.huahan.hhbaseutils.model.HHSystemPhotoModel;
import com.huahan.hhbaseutils.ui.HHSelectPhotoActivity;

import java.util.List;

/**
 * 选择图片的适配器
 * @author yuan
 *
 */
public class HHSelectPhotoAdapter extends RecyclerView.Adapter<HHSelectPhotoHolder>
{
	private static final String tag=HHSelectPhotoAdapter.class.getSimpleName();
	//上下文对象
	private Context mContext;
	//用于设置图片显示的大小
	private RelativeLayout.LayoutParams mLayoutParams;
	//显示的数据
	private List<HHSystemPhotoModel> mList;
	//加载图片
	private HHImageUtils mImageUtils;
	//当Item被点击的时候执行的监听器
	private OnItemClickListener mListener;
	//图片显示的宽度
	private int mWidth;
	//已经选择图片的数量
	private int mCount=0;
	//最大选择图片的数量
	private int mMaxSelectCount=1;
	
	public HHSelectPhotoAdapter(Context context,List<HHSystemPhotoModel> list,int selectCount)
	{
		//初始化数据
		this.mContext=context;
		this.mList=list;
		mImageUtils=HHImageUtils.getInstance(null);
		//计算图片显示的宽度
		mWidth = HHScreenUtils.getScreenWidth(context);
		mWidth=(mWidth-HHDensityUtils.dip2px(context, 4))/3;
		//初始化图片显示大小的LayoutParam
		mLayoutParams=new RelativeLayout.LayoutParams(mWidth, mWidth);
		this.mMaxSelectCount=selectCount;
	}
	@Override
	public int getItemCount()
	{
		return mList.size();
	}
	/**
	 * 返回当前Adapter显示的数据的集合
	 * @return
	 */
	public List<HHSystemPhotoModel> getDataList()
	{
		return mList;
	}
	/**
	 * 设置当一条点击是执行的监听器方法
	 */
	public void setOnItemClickListener(OnItemClickListener listener)
	{
		this.mListener=listener;
	}
	@Override
	public void onBindViewHolder(final HHSelectPhotoHolder viewHolder,final int arg1)
	{
		HHSystemPhotoModel model = mList.get(arg1);
		//如果显示的相机的数据，则设置相应的图片和隐藏选择框
		if (arg1==0&&model.getFilePath().equals(HHSelectPhotoActivity.DEFAULT_FILE_PATH))
		{
			viewHolder.photoImageView.setImageResource(R.drawable.hh_select_photo_camera);
			viewHolder.boxImageView.setVisibility(View.GONE);
		}else {
			//根据当前可以选择的最大的数量来设置是否显示选择框，以及选择框的大小
			if (mMaxSelectCount>1)
			{
				viewHolder.boxImageView.setVisibility(View.VISIBLE);
				if (model.isSelect())
				{
					viewHolder.boxImageView.setImageResource(R.drawable.hh_select_photo_se);
				}else {
					viewHolder.boxImageView.setImageResource(R.drawable.hh_select_photo);
				}
			}else {
				viewHolder.boxImageView.setVisibility(View.GONE);
			}
			//加载图片
			mImageUtils.loadImage(R.drawable.hh_select_photo_default, model.getFilePath(), viewHolder.photoImageView, mWidth/2, mWidth/2);
		}
		//设置当View点击的时候执行的监听器
		viewHolder.view.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				if (mListener!=null)
				{
					mListener.onItemClicked(viewHolder, arg1);
				}
			}
		});
		
	}

	@Override
	public HHSelectPhotoHolder onCreateViewHolder(ViewGroup arg0, int arg1)
	{
		//实例化ViewHolder
		HHLog.i(tag, "onCreateViewHolder:"+(mCount++));
		View view=View.inflate(mContext, R.layout.hh_item_select_photo, null);
		HHSelectPhotoHolder viewHolder=new HHSelectPhotoHolder(view);
		viewHolder.photoImageView.setLayoutParams(mLayoutParams);
		return viewHolder;
	}


}
