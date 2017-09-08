package com.huahan.hhbaseutils.ui;

import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huahan.hhbaseutils.HHSystemUtils;
import com.huahan.hhbaseutils.HHViewHelper;
import com.huahan.hhbaseutils.R;
import com.huahan.hhbaseutils.adapter.HHBaseChatAdapter;
import com.huahan.hhbaseutils.anim.AnimationAdapter;
import com.huahan.hhbaseutils.imp.HHBaseChatImp;
import com.huahan.hhbaseutils.model.HHLoadState;
import com.huahan.hhbaseutils.model.HHTextWatcherAdapter;
import com.huahan.hhbaseutils.view.HHGridViewPager;
import com.huahan.hhbaseutils.view.HHRecordButton;

import java.util.ArrayList;
import java.util.List;

/**
 * 实现一个默认的聊天界面
 * @author yuan
 *
 */
public class HHBaseChatActivity extends HHBaseImageActivity
{

	//显示列表
	private RecyclerView mListView;
	//保存显示的数据
	private List<? extends HHBaseChatImp> mList;
	//Adapter
	private HHBaseChatAdapter mAdapter;
	/**
	 * 切换键盘和语音的按钮
	 */
	private ImageView mKeyVoiceImageView;
	/**
	 * “+”按钮
	 */
	private ImageView mMoreImageView;
	/**
	 * 发送的按钮
	 */
	private TextView mSendTextView;
	/**
	 * 输入文字的文本框
	 */
	private EditText mMsgEditText;
	/**
	 * 点击显示表情的按钮
	 */
	private ImageView mEmotionImageView;
	/**
	 * 输入框背景的图片控件
	 */
	private ImageView mEditBgImageView;
	/**
	 * 显示输入框的界面的Layout
	 */
	private FrameLayout mEditMsgLayout;
	/**
	 * 录制语音的按钮
	 */
	private HHRecordButton mRecordButton;
	/**
	 * 显示表情和更多
	 */
	private HHGridViewPager mViewPager;
	/**
	 * 包含更多内容的Layout
	 */
	private LinearLayout mMoreLayout;
	@Override
	public void onPageLoad()
	{
	}
	@Override
	public boolean initOnCreate()
	{
		changeLoadState(HHLoadState.SUCCESS);
		return true;
		
		
		
		
		
	}

	@Override
	public View initView()
	{
		View view=View.inflate(this, R.layout.hh_activity_base_chat, null);
		//列表
		mListView=HHViewHelper.getViewByID(view, R.id.hh_rv_chat_list);
		//切换键盘和语音
		mKeyVoiceImageView=HHViewHelper.getViewByID(view, R.id.hh_img_chat_voice_key);
		//“+”按钮
		mMoreImageView=HHViewHelper.getViewByID(view, R.id.hh_img_chat_more);
		//发送
		mSendTextView=HHViewHelper.getViewByID(view, R.id.hh_tv_chat_msg_send);
		//表情
		mEmotionImageView=HHViewHelper.getViewByID(view, R.id.hh_img_chat_msg_emotion);
		//消息的输入框
		mMsgEditText=HHViewHelper.getViewByID(view, R.id.hh_et_chat_msg_edit);
		//输入框的背景
		mEditBgImageView=HHViewHelper.getViewByID(view, R.id.hh_img_chat_msg_edit_bg);
		//录制语音的按钮
		mRecordButton=HHViewHelper.getViewByID(view, R.id.hh_btn_chat_voice_record);
		//输入框的Layout
		mEditMsgLayout=HHViewHelper.getViewByID(view, R.id.hh_fl_chat_msg_edit);
		//显示表情
		mViewPager=HHViewHelper.getViewByID(view, R.id.hh_vp_chat_more);
		//包含更多内容
		mMoreLayout=HHViewHelper.getViewByID(view, R.id.hh_ll_chat_msg_more);
		return view;
	}
	@Override
	public void initValues()
	{
		mList=getChatMsgList(0, getPageSize());
		mAdapter=new HHBaseChatAdapter(this, mList, false, R.drawable.hh_default_image, R.drawable.hh_default_image);
		mListView.setLayoutManager(new LinearLayoutManager(this));
		mListView.setAdapter(mAdapter);
		mRecordButton.setSavePath("/sdcard/123.amr");
	}
	@Override
	public void initListeners()
	{
		//设置当焦点变化的时候执行的监听器
		mMsgEditText.setOnFocusChangeListener(new OnFocusChangeListener()
		{
			
			@Override
			public void onFocusChange(View v, boolean hasFocus)
			{
				int backgroundResID=hasFocus?R.drawable.hh_chat_msg_edit_bg_focused:R.drawable.hh_chat_msg_edit_bg;
				mEditBgImageView.setBackgroundResource(backgroundResID);
			}
		});
		mMsgEditText.addTextChangedListener(new HHTextWatcherAdapter(){

			@Override
			public void afterTextChanged(Editable s)
			{
				super.afterTextChanged(s);
				if (s!=null&&s.toString().length()>0)
				{
					//当前有文本
					//1：切换显示发送的按钮
					if (mSendTextView.getVisibility()==View.INVISIBLE);
					{
						changeSendAndMore(mSendTextView, mMoreImageView);
					}
				}else {
					if (mMoreImageView.getVisibility()==View.INVISIBLE);
					{
						changeSendAndMore(mMoreImageView, mSendTextView);
					}
				}
			}
			
		});
		/**
		 * 当键盘和语音切换的按钮点击的时候执行的事件
		 */
		mKeyVoiceImageView.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				//当前显示的是录制语音的界面，切换到输入框的界面
				if (mRecordButton.getVisibility()==View.VISIBLE)
				{
					mRecordButton.setVisibility(View.GONE);
					mEditMsgLayout.setVisibility(View.VISIBLE);
					mKeyVoiceImageView.setImageResource(R.drawable.hh_selector_img_chat_voice);
					//设置输入框获取焦点
					requestMsgFocus();
					//弹起软键盘
					HHSystemUtils.toogleKeyboard(getPageContext());
				}else {
					mRecordButton.setVisibility(View.VISIBLE);
					mEditMsgLayout.setVisibility(View.GONE);
					
					mKeyVoiceImageView.setImageResource(R.drawable.hh_selector_img_chat_key);
					//隐藏软键盘
					HHSystemUtils.hideSystemKeyBoard(getPageContext(), mMsgEditText);
				}
			}
		});
		mMoreImageView.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				if (mMoreLayout.getVisibility()==View.GONE)
				{
					mMoreLayout.setVisibility(View.VISIBLE);
					//隐藏软键盘
					HHSystemUtils.hideSystemKeyBoard(getPageContext(), mMsgEditText);
					//使输入框失去焦点
					mMsgEditText.setVisibility(View.INVISIBLE);
					mMsgEditText.setVisibility(View.VISIBLE);
				}else {
					mMoreLayout.setVisibility(View.GONE);
					mRecordButton.setVisibility(View.GONE);
					mEditMsgLayout.setVisibility(View.VISIBLE);
					mKeyVoiceImageView.setImageResource(R.drawable.hh_selector_img_chat_voice);
					requestMsgFocus();
					HHSystemUtils.toogleKeyboard(getPageContext());
				}
			}
		});
	}
	/**
	 * 使输入框获取焦点
	 */
	private void requestMsgFocus()
	{
		mMsgEditText.setFocusable(true);
		mMsgEditText.setFocusableInTouchMode(true);
		mMsgEditText.requestFocus();
	}
	/**
	 * 切换发送的按钮和更过的按钮
	 * @param visibleView			将要显示的View
	 * @param hideView				将要隐藏的按钮
	 */
	private void changeSendAndMore(View visibleView,final View hideView)
	{
		visibleView.setVisibility(View.VISIBLE);
		AlphaAnimation outAnimation=new AlphaAnimation(1.0f, 0.1f);
		AlphaAnimation inAnimation=new AlphaAnimation(0.1f, 1.0f);
		inAnimation.setDuration(300);
		outAnimation.setDuration(300);
		visibleView.setAnimation(inAnimation);
		hideView.setAnimation(outAnimation);
		outAnimation.setAnimationListener(new AnimationAdapter(){

			@Override
			public void onAnimationEnd(Animation animation)
			{
				super.onAnimationEnd(animation);
				hideView.setVisibility(View.INVISIBLE);
			}
			
		});
		outAnimation.start();
		inAnimation.start();
	}
	@Override
	public void processHandlerMsg(Message msg)
	{
	}
	@Override
	protected void onImageSelectFinish(ArrayList<String> photoList)
	{
	}
	/**
	 * 获取消息列表
	 * @param lastMsgID			最近一条消息的ID
	 * @param pageSize			每页获取的数据的大小
	 */
	protected List<? extends HHBaseChatImp> getChatMsgList(int lastMsgID,int pageSize)
	{
		return null;
	}
	/**
	 * 每页获取数据的条数
	 * @return
	 */
	protected int getPageSize()
	{
		return 20;
	}
	
}
