package com.huahan.hhbaseutils.imp;

import com.huahan.hhbaseutils.model.HHChatMsgSendState;

public interface HHBaseChatImp
{
	/**
	 * 获取消息的类型，如果值大于HHConstantParam.CHAT_TYPE_OTHER_USER,则默认在
	 * 显示的时候会默认显示成不支持的消息。消息的值为HHConstantParam.CHAT_TYPE_XXX,同时
	 * 如果消息类型是偶数且不小于0，则代表是自己发送的消息，如果是奇数且不小于0，则代表是其他用户
	 * 发送的消息
	 * @return
	 */
	int getMsgType();
//	/**
//	 * 是否是自己发送的消息
//	 * @return
//	 */
//	boolean isSelfMsg();
	/**
	 * 获取消息的发送的时间
	 * @return
	 */
	String getMsgSendTime();
	/**
	 * 获取消息发送的时间
	 * @return
	 */
	String getMsgID();
	/**
	 * 获取消息的内容
	 * @return
	 */
	String getMsgContent();
	/**
	 * 获取媒体的大小，如果是图片的话返回的是图片的大小，例如"宽*高"；
	 * 如果是语音的话，返回语音的时长。如果不是语音或者图片，则可以返回null
	 * @return
	 */
	String getMediaSize();
	/**
	 * 返回发送者的名称，如果不需要显示的话返回null
	 * @return
	 */
	String getSendUserName();
	/**
	 * 获取发送者头像的地址
	 * @return
	 */
	String getSendUserHeadImageUrl();
	/**
	 * 获取发送状态
	 * @return
	 */
	HHChatMsgSendState getSendState();
	/**
	 * 获取读取状态，判断是否已经读取，true已读取
	 * @return
	 */
	boolean getReadState();
	/**
	 * 获取媒体的播放状态，判断是否已经播放，true，已播放
	 * @return
	 */
	boolean getMediaPlayState();
}
