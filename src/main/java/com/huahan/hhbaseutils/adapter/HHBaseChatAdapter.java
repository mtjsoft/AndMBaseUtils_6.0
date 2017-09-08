package com.huahan.hhbaseutils.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huahan.hhbaseutils.HHDensityUtils;
import com.huahan.hhbaseutils.HHFormatUtils;
import com.huahan.hhbaseutils.HHImageSpecial;
import com.huahan.hhbaseutils.HHImageUtils.Builder;
import com.huahan.hhbaseutils.HHLog;
import com.huahan.hhbaseutils.HHViewHelper;
import com.huahan.hhbaseutils.R;
import com.huahan.hhbaseutils.constant.HHConstantParam;
import com.huahan.hhbaseutils.imp.HHBaseChatImp;
import com.huahan.hhbaseutils.imp.HHImageDecorator;
import com.huahan.hhbaseutils.model.HHBaseChatViewHolder;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 暂时支持的消息类型为文本，图片，语音，系统提示，不支持的消息5中消息，其他消息再后续添加。
 * 消息的类型引用HHConstantParam中的
 * @author chenyuan
 *
 */
public class HHBaseChatAdapter extends RecyclerView.Adapter<ViewHolder>
{

	
	private List<? extends HHBaseChatImp> mList;
	private Context mContext;
	/**
	 * 头像的默认图片
	 */
	private int mDefaultHeadImage=0;
	/**
	 * 图片的默认图片
	 */
	private int mDefaultImage=0;
	/**
	 * 是否是群聊
	 */
	private boolean mGroupChat=false;
	/**
	 * 构造方法
	 * @param context					上下文对象
	 * @param list						消息列表
	 * @param itemTypeCount				支持的消息的类型的总数，不小于5
	 */
	public HHBaseChatAdapter(Context context,List<? extends HHBaseChatImp> list,boolean groupChat,int defaultHeadImage,int defaultImage)
	{
		this.mContext=context;
		this.mList=list;
		this.mGroupChat=groupChat;
		this.mDefaultHeadImage=defaultHeadImage;
		this.mDefaultImage=defaultImage;
	}
	@Override
	public int getItemViewType(int position)
	{
		HHBaseChatImp imp = mList.get(position);
		//判断当前的消息类型是不是支持的消息类型
		if (imp.getMsgType()>HHConstantParam.CHAT_TYPE_LIMIT_RIGHT||imp.getMsgType()<HHConstantParam.CHAT_TYPE_LIMIT_LEFT)
		{
			return Math.abs(imp.getMsgType())%2==0?HHConstantParam.CHAT_TYPE_OTHER_SELF:HHConstantParam.CHAT_TYPE_OTHER_USER;
		}
		return imp.getMsgType();
	}
	
	@Override
	public int getItemCount()
	{
		return mList==null?0:mList.size();
	}

	@Override
	public void onBindViewHolder(ViewHolder viewHolder, int position)
	{
		int viewType=getItemViewType(position);
		HHBaseChatImp chatInfo = mList.get(position);
		switch (viewType)
		{
		case HHConstantParam.CHAT_TYPE_TEXT_SELF:
		case HHConstantParam.CHAT_TYPE_TEXT_USER:
		case HHConstantParam.CHAT_TYPE_OTHER_SELF:
		case HHConstantParam.CHAT_TYPE_OTHER_USER:
			TextViewHolder textViewHolder=(TextViewHolder) viewHolder;
			bindTextViewHolder(textViewHolder, viewType, chatInfo);
			break;
		case HHConstantParam.CHAT_TYPE_IMAGE_SELF:
		case HHConstantParam.CHAT_TYPE_IMAGE_USER:
			ImageViewHolder imageViewHolder=(ImageViewHolder) viewHolder;
			bindImageViewHolder(imageViewHolder, viewType, chatInfo);
			break;
		case HHConstantParam.CHAT_TYPE_VOICE_SELF:
		case HHConstantParam.CHAT_TYPE_VOICE_USER:
			VoiceViewHolder voiceViewHolder=(VoiceViewHolder) viewHolder;
			bindVoiceViewHolder(voiceViewHolder, viewType, chatInfo);
			break;
		default:
			break;
		}
	}
	/**
	 * 展示语音消息
	 * @param viewHolder
	 * @param viewType
	 * @param chatInfo
	 */
	private void bindVoiceViewHolder(VoiceViewHolder viewHolder,int viewType,HHBaseChatImp chatInfo)
	{
		bindBaseViewHolder(viewHolder,viewType, chatInfo);
		viewHolder.voiceTimeTextView.setText(chatInfo.getMediaSize()+"\"");
		viewHolder.voiceStateImageView.setVisibility(chatInfo.getMediaPlayState()?View.GONE:View.VISIBLE);
		viewHolder.voiceImageView.setBackgroundResource(viewType==HHConstantParam.CHAT_TYPE_VOICE_SELF?R.drawable.hh_chat_voice_right:R.drawable.hh_chat_voice_left);
		if (viewType==HHConstantParam.CHAT_TYPE_VOICE_SELF)
		{
			viewHolder.voiceStateImageView.setVisibility(View.GONE);
			viewHolder.nameTextView.setVisibility(View.GONE);
		}
	}
	/**
	 * 展示图片消息
	 * @param viewHolder
	 * @param viewType
	 * @param chatInfo
	 */
	private void bindImageViewHolder(ImageViewHolder viewHolder,int viewType,HHBaseChatImp chatInfo)
	{
		bindBaseViewHolder(viewHolder,viewType, chatInfo);
		final int viewShape=viewType==HHConstantParam.CHAT_TYPE_IMAGE_SELF?R.drawable.hh_chat_right_image_shape:R.drawable.hh_chat_left_image_shape;
		int[] imageSize=getImageSize(chatInfo.getMediaSize());
		LayoutParams layoutParams = viewHolder.contentImageView.getLayoutParams();
		layoutParams.height=imageSize[1];
		layoutParams.width=imageSize[0];
		viewHolder.contentImageView.setLayoutParams(layoutParams);
		viewHolder.contentImageView.setScaleType(ScaleType.FIT_XY);
		Builder.getNewInstance(viewHolder.contentImageView, chatInfo.getMsgContent()).defaultImageID(mDefaultImage).imageDecorator(new HHImageDecorator()
		{
			
			@Override
			public Bitmap decorateImage(Bitmap bitmap)
			{
				return HHImageSpecial.getShapeBitmap(mContext, bitmap,viewShape);
			}
		}).load();
	}
	/**
	 * 获取图片的大小
	 * @param size		图片的大小
	 * @return
	 */
	private int[] getImageSize(String size)
	{
		int[] imageSize=null;
		if (!TextUtils.isEmpty(size))
		{
			String test="^(\\d+)\\*(\\d+)$";
			Pattern pattern=Pattern.compile(test);
			Matcher matcher = pattern.matcher(size);
			if (matcher.matches())
			{
				String width=matcher.group(1);
				String height=matcher.group(2);
				imageSize=new int[2];
				imageSize[0]=Integer.valueOf(width);
				imageSize[1]=Integer.valueOf(height);
			}
		}
		if (imageSize==null)
		{
			imageSize=new int[2];
			imageSize[0]=HHDensityUtils.dip2px(mContext, 90);
			imageSize[1]=HHDensityUtils.dip2px(mContext, 150);
			
		}
		return imageSize;
	}
	/**
	 * 展示文本消息
	 * @param viewHolder
	 * @param viewType
	 * @param chatInfo
	 */
	private void bindTextViewHolder(TextViewHolder viewHolder,int viewType,HHBaseChatImp chatInfo)
	{
		bindBaseViewHolder(viewHolder,viewType,chatInfo);
		if (viewType==HHConstantParam.CHAT_TYPE_OTHER_SELF||viewType==HHConstantParam.CHAT_TYPE_OTHER_USER)
		{
			viewHolder.contentTextView.setText(R.string.hh_chat_msg_not_support);
		}else {
			viewHolder.contentTextView.setText(chatInfo.getMsgContent());
		}
	}
	private void bindBaseViewHolder(HHBaseChatViewHolder viewHolder,int viewType,HHBaseChatImp chatInfo)
	{
		viewHolder.timeTextView.setText(getMsgSendTime(chatInfo.getMsgSendTime()));
		viewHolder.nameTextView.setText(chatInfo.getSendUserName());
		Builder.getNewInstance(viewHolder.headImageView, chatInfo.getSendUserHeadImageUrl()).defaultImageID(mDefaultHeadImage).load();
		switch (chatInfo.getSendState())
		{
		case FAILED:
			viewHolder.sendFailedImageView.setVisibility(View.VISIBLE);
			viewHolder.stateProgressBar.setVisibility(View.GONE);
			break;
		case SENDING:
			HHLog.i("test", "测试");
			viewHolder.sendFailedImageView.setVisibility(View.GONE);
			viewHolder.stateProgressBar.setVisibility(View.VISIBLE);
			break;
		case SUCCESS:
			viewHolder.stateProgressBar.setVisibility(View.GONE);
			viewHolder.sendFailedImageView.setVisibility(View.GONE);
			break;
			

		default:
			break;
		}
		if (viewType%2==0)//自己发送的消息
		{
			viewHolder.nameTextView.setVisibility(View.GONE);
		}else {
			viewHolder.nameTextView.setVisibility(mGroupChat?View.VISIBLE:View.GONE);
		}
	}
	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parentView, int viewType)
	{
		ViewHolder viewHolder=null;
		View view=null;
		switch (viewType)
		{
		case HHConstantParam.CHAT_TYPE_OTHER_SELF:
		case HHConstantParam.CHAT_TYPE_TEXT_SELF:
			view=View.inflate(mContext, R.layout.hh_item_chat_text_right, null);
			viewHolder=createTextViewHolder(view);
			break;
		case HHConstantParam.CHAT_TYPE_TEXT_USER:
		case HHConstantParam.CHAT_TYPE_OTHER_USER:
			view=View.inflate(mContext, R.layout.hh_item_chat_text_left, null);
			viewHolder=createTextViewHolder(view);
			break;
		case HHConstantParam.CHAT_TYPE_IMAGE_SELF:
			view=View.inflate(mContext, R.layout.hh_item_chat_image_right,null);
			viewHolder=new ImageViewHolder(view);
			break;
		case HHConstantParam.CHAT_TYPE_IMAGE_USER:
			view=View.inflate(mContext, R.layout.hh_item_chat_image_left,null);
			viewHolder=new ImageViewHolder(view);
			break;
		case HHConstantParam.CHAT_TYPE_VOICE_SELF:
			view=View.inflate(mContext, R.layout.hh_item_chat_voice_right, null);
			viewHolder=new VoiceViewHolder(view);
			break;
		case HHConstantParam.CHAT_TYPE_VOICE_USER:
			view=View.inflate(mContext, R.layout.hh_item_chat_voice_left, null);
			viewHolder=new VoiceViewHolder(view);
			break;
		case HHConstantParam.CHAT_TYPE_SYSTEM:
			
			break;
		default:
			break;
		}
		return viewHolder;
	}
	
	private ViewHolder createTextViewHolder(View view)
	{
		TextViewHolder viewHolder=new TextViewHolder(view);
		return viewHolder;
	}
	/**
	 * 显示文本的ViewHolder
	 * @author chen3
	 *
	 */
	private class TextViewHolder extends HHBaseChatViewHolder
	{
		TextView contentTextView;
		public TextViewHolder(View view)
		{
			super(view);
			contentTextView=HHViewHelper.getViewByID(view, R.id.hh_tv_chat_text_content);
		}
	}
	/**
	 * 显示图片的ViewHolder
	 * @author chen3
	 *
	 */
	private class ImageViewHolder extends HHBaseChatViewHolder
	{
		ImageView contentImageView;
		public ImageViewHolder(View view)
		{
			super(view);
			contentImageView=HHViewHelper.getViewByID(view, R.id.hh_img_chat_image_content);
		}
	}
	/**
	 * 显示语音的ViewHolder
	 * @author chen3
	 *
	 */
	private class VoiceViewHolder extends HHBaseChatViewHolder
	{
		LinearLayout voiceLaytout;
		ImageView voiceImageView;
		TextView voiceTimeTextView;
		ImageView voiceStateImageView;
		public VoiceViewHolder(View view)
		{
			super(view);
			voiceImageView=HHViewHelper.getViewByID(view, R.id.hh_img_chat_voice_content);
			voiceLaytout=HHViewHelper.getViewByID(view, R.id.hh_ll_chat_voice_layout);
			voiceTimeTextView=HHViewHelper.getViewByID(view, R.id.hh_tv_chat_voice_time);
			voiceStateImageView=HHViewHelper.getViewByID(view, R.id.hh_img_chat_voice_not_play);
		}
		
	}
	/**
	 * 把时间转换成特定的时间格式
	 * @param sendTime
	 * @return
	 */
	@SuppressWarnings("deprecation")
	private String getMsgSendTime(String sendTime)
	{
		Date convertToDate = HHFormatUtils.convertToDate(sendTime, HHConstantParam.DEFAULT_TIME_FORMAT);
		String formatTime=sendTime;
		if (convertToDate!=null)
		{
			long convertSecond=convertToDate.getTime()/1000;
			Date date=new Date(Calendar.getInstance().get(Calendar.YEAR)-1900,Calendar.getInstance().get(Calendar.MONTH),Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
			//判断是不是今天
			if (date.getYear()==convertToDate.getYear()&&date.getMonth()==convertToDate.getMonth()&&date.getDate()==convertToDate.getDate())
			{
				formatTime=HHFormatUtils.convertToString(convertToDate,"HH:mm");
			}else if (new Date().getYear()!=convertToDate.getYear())
			{
				//判断时间是否跨年，如果是跨年的话，就显示年月日信息
				int infoID=0;
				if (convertToDate.getHours()<12)
				{
					infoID=R.string.hh_chat_time_full_morning;
				}else if (convertToDate.getHours()<18)
				{
					infoID=R.string.hh_chat_time_full_afternoon;
				}else {
					infoID=R.string.hh_chat_time_full_night;
				}
				formatTime=HHFormatUtils.convertToString(convertToDate, mContext.getString(infoID));
			} else if(date.getTime()/1000-convertToDate.getTime()/1000<=HHConstantParam.DAY_SECOND)
			{
				//昨天
				formatTime=HHFormatUtils.convertToString(convertToDate, mContext.getString(R.string.hh_chat_time_yesterday));
			}else if (date.getTime()/1000-convertToDate.getTime()/1000<=HHConstantParam.DAY_SECOND*6) 
			{
				//周几
				int time=(int) ((date.getTime()/1000-convertToDate.getTime()/1000)/HHConstantParam.DAY_SECOND);
				int day = convertToDate.getDay();
				formatTime=HHFormatUtils.convertToString(convertToDate, "HH:mm");
				formatTime=mContext.getResources().getStringArray(R.array.hh_week)[day]+" "+formatTime;
			}else {
				int infoID=0;
				if (convertToDate.getHours()<12)
				{
					infoID=R.string.hh_chat_time_morning;
				}else if (convertToDate.getHours()<18)
				{
					infoID=R.string.hh_chat_time_afternoon;
				}else {
					infoID=R.string.hh_chat_time_night;
				}
				formatTime=HHFormatUtils.convertToString(convertToDate, mContext.getString(infoID));
			}
		}
		return formatTime;
	}


}
