package com.huahan.hhbaseutils.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.huahan.hhbaseutils.HHCommonUtils;
import com.huahan.hhbaseutils.HHImageUtils;
import com.huahan.hhbaseutils.HHImageUtils.Builder;
import com.huahan.hhbaseutils.HHViewHelper;
import com.huahan.hhbaseutils.R;
import com.huahan.hhbaseutils.model.HHSelectPhotoTypeModel;
import com.huahan.hhbaseutils.model.HHSystemPhotoModel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class HHSelectPhotoTypeAdapter extends BaseAdapter
{



	//显示的目录的列表
	private List<HHSelectPhotoTypeModel> mList;
	private Activity mContext;
	//纪录了选中的位置
	private int mCheckPosition=0;
	public HHSelectPhotoTypeAdapter(Activity activity, Map<String, List<HHSystemPhotoModel>> typeMap,List<HHSystemPhotoModel> photoList)
	{
		this.mContext=activity;
		mList=new ArrayList<HHSelectPhotoTypeModel>();
		//添加默认数据，该条数据相当于是全部图片
		HHSelectPhotoTypeModel model = new HHSelectPhotoTypeModel();
		model.setChecked(true);
		model.setPhotoList(photoList);
		mList.add(model);
		//把Map中的数据添加到List中
		if (typeMap!=null)
		{
			Set<String> keySet = typeMap.keySet();
			Iterator<String> iterator = keySet.iterator();
			while (iterator.hasNext())
			{
				String key = iterator.next();
				mList.add(new HHSelectPhotoTypeModel(key, typeMap.get(key)));
			}
		}
	}
	/**
	 * 返回当前Adapter显示的图片
	 * @return
	 */
	public List<HHSelectPhotoTypeModel> getDataList()
	{
		return mList;
	}
	

	@Override
	public int getCount()
	{
		return mList.size();
	}

	@Override
	public Object getItem(int position)
	{
		return mList.get(position);
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		ViewHolder viewHolder=null;
		if (convertView==null)
		{
			viewHolder=new ViewHolder();
			convertView=View.inflate(mContext, R.layout.hh_item_select_photo_type, null);
			viewHolder.nameTextView=HHViewHelper.getViewByID(convertView, R.id.hh_tv_item_photo_type_name);
			viewHolder.countTextView=HHViewHelper.getViewByID(convertView, R.id.hh_tv_item_photo_type_count);
			viewHolder.photoImageView=HHViewHelper.getViewByID(convertView,R.id.hh_img_item_photo_type);
			viewHolder.checkImageView=HHViewHelper.getViewByID(convertView, R.id.hh_img_item_photo_type_ischeck);
			HHCommonUtils.tintViewBackground(mContext, viewHolder.checkImageView);
			convertView.setTag(viewHolder);
		}else {
			viewHolder=(ViewHolder) convertView.getTag();
		}
		HHSelectPhotoTypeModel model = mList.get(position);
		if (position!=0)
		{
			//设置显示的数量，同时显示图片
			viewHolder.countTextView.setVisibility(View.VISIBLE);
			viewHolder.nameTextView.setText(model.getDirName());
			viewHolder.countTextView.setText(String.format(mContext.getString(R.string.hh_picture_count), model.getPhotoList().size()));
			Builder builder=new Builder(viewHolder.photoImageView, model.getPhotoList().get(0).getFilePath());
			builder.load();
		}else {
			//如果是第一条数据，显示的全部这一项
			viewHolder.nameTextView.setText(mContext.getString(R.string.hh_picture));
			viewHolder.countTextView.setText("");
			viewHolder.countTextView.setVisibility(View.GONE);
			//判断时候有图片，如果有图片的话就显示图片，如果没有图片的话就显示默认的图片，默认的图片在上边已经设置
			//同时全部里边不显示图片的数量
			if (model.getPhotoList()!=null&&model.getPhotoList().size()>1)
			{
				HHImageUtils.Builder builder=new Builder(viewHolder.photoImageView, model.getPhotoList().get(1).getFilePath());
				builder.load();
			}else {
				viewHolder.photoImageView.setImageResource(R.drawable.hh_select_photo_type_default);
			}
		}
		//设置选中的图标
		if (model.isChecked())
		{
			viewHolder.checkImageView.setVisibility(View.VISIBLE);
		}else {
			viewHolder.checkImageView.setVisibility(View.INVISIBLE);
		}
		return convertView;
	}
	private class ViewHolder
	{
		TextView nameTextView;
		TextView countTextView;
		ImageView photoImageView;
		ImageView checkImageView;
	}
	/**
	 * 设置新的选中的位置
	 * @param position
	 */
	public void setNewCheckPosition(int position)
	{
		if (position==mCheckPosition)
		{
			return ;
		}
		mList.get(mCheckPosition).setChecked(false);
		mList.get(position).setChecked(true);
		mCheckPosition=position;
		notifyDataSetChanged();
	}
	

}
