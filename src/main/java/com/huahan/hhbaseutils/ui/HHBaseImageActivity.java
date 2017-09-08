package com.huahan.hhbaseutils.ui;

import android.content.Intent;

import java.util.ArrayList;

/**
 * 
 * @author yuan
 *
 */
public abstract class HHBaseImageActivity extends HHBaseDataActivity
{
	public static final int SELECT_IMAGE_RESULT=10000;
	/**
	 * 获取图片
	 * @param photoCount
	 * @param sureColor 确定按钮颜色值设置，选中未选中两种状态
	 */
	protected void getImage(int photoCount,int sureColor)
	{
		getImage(photoCount,sureColor,HHSelectPhotoActivity.class);
	}
	/**
	 * 获取图片
	 * @param photoCount 选择图片的数量，不能小于1
	 * @param sureColor 确定按钮颜色值设置，选中未选中两种状态
	 * @param selectPhotoClass 选择图片的Activity的class对象
	 */
	protected void getImage(int photoCount,int sureColor,Class<? extends HHSelectPhotoActivity> selectPhotoClass)
	{
		Intent intent=new Intent(this,selectPhotoClass);
		intent.putExtra(HHSelectPhotoActivity.FLAG_SELECT_COUNT, photoCount);
		intent.putExtra(HHSelectPhotoActivity.SURE_COLOR, sureColor);
		startActivityForResult(intent, SELECT_IMAGE_RESULT);
	}
	/**
	 * 获取图片
	 * @param photoCount			选择图片的数量，不能小于1
	 */
	protected void getImage(int photoCount)
	{
		getImage(photoCount,HHSelectPhotoActivity.class);
	}
	/**
	 * 获取图片
	 * @param photoCount			选择图片的数量，不能小于1
	 * @param selectPhotoClass		选择图片的Activity的class对象
	 */
	protected void getImage(int photoCount,Class<? extends HHSelectPhotoActivity> selectPhotoClass)
	{
		Intent intent=new Intent(this,selectPhotoClass);
		intent.putExtra(HHSelectPhotoActivity.FLAG_SELECT_COUNT, photoCount);
		startActivityForResult(intent, SELECT_IMAGE_RESULT);
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode,data);
		if (resultCode==RESULT_OK&&requestCode==SELECT_IMAGE_RESULT)
		{
			ArrayList<String> list = data.getStringArrayListExtra(HHSelectPhotoActivity.FLAG_RESULT);
			if (list!=null)
			{
				onImageSelectFinish(list);
			}
		}
	}
	/**
	 * 选择图片成功的时候执行的代码
	 * @param photoList				选择的图片的集合
	 */
	protected abstract void onImageSelectFinish(ArrayList<String> photoList);
}
