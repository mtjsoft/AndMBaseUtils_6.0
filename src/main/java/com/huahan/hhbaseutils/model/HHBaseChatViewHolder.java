package com.huahan.hhbaseutils.model;

import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.huahan.hhbaseutils.HHViewHelper;
import com.huahan.hhbaseutils.R;

/**
 * 聊天界面Adapter的ViewHolder，提取公共部分，适用于除系统消息以外的其他消息
 * @author chenyuan
 *
 */
public class HHBaseChatViewHolder extends ViewHolder
{
	/**
	 * 显示用户姓名
	 */
	public TextView nameTextView;
	/**
	 * 显示用户头像
	 */
	public ImageView headImageView;
	/**
	 * 消息的发送的时间
	 */
	public TextView timeTextView;
	/**
	 *	发送失败的时候显示的图标
	 */
	public ImageView sendFailedImageView;
	/**
	 * 显示的发送的状态
	 */
	public ProgressBar stateProgressBar;
	public HHBaseChatViewHolder(View view)
	{
		super(view);
		nameTextView=HHViewHelper.getViewByID(view, R.id.hh_id_chat_user_name);
		headImageView=HHViewHelper.getViewByID(view, R.id.hh_id_chat_user_head);
		timeTextView=HHViewHelper.getViewByID(view, R.id.hh_id_chat_msg_time);
		sendFailedImageView=HHViewHelper.getViewByID(view, R.id.hh_img_chat_type_sendfailed);
		stateProgressBar=HHViewHelper.getViewByID(view, R.id.hh_pb_chat_msg);
	}
	
}
