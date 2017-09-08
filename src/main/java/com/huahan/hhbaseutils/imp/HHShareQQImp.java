package com.huahan.hhbaseutils.imp;

import android.app.Activity;

import com.tencent.tauth.IUiListener;

/**
 * 分享到QQ的时候，当前的Activity应该实现的接口
 * @author yuan
 *
 */
public interface HHShareQQImp extends IUiListener
{
	Activity getActivity();
}
