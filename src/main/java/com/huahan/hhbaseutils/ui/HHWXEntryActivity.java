package com.huahan.hhbaseutils.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.huahan.hhbaseutils.HHLog;
import com.huahan.hhbaseutils.HHStreamUtils;
import com.huahan.hhbaseutils.constant.HHConstantParam;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.json.JSONObject;

import java.io.InputStream;

public class HHWXEntryActivity extends Activity implements IWXAPIEventHandler
{

	private static final String tag = HHWXEntryActivity.class.getName();
	private IWXAPI wxApi;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		String appID=getWXAppID();
		if (TextUtils.isEmpty(appID))
		{
			throw new RuntimeException("please set weixin appid.edit file assets/share.json");
		}
		wxApi=WXAPIFactory.createWXAPI(this, appID, false);
		wxApi.handleIntent(getIntent(), this);
	}
	@Override
	protected void onNewIntent(Intent intent)
	{
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
		setIntent(intent);
		wxApi.handleIntent(getIntent(), this);
	}
	/**
	 * 获取微信的AppID
	 * 
	 * @return
	 */
	private String getWXAppID()
	{

		String appID = null;
		AssetManager manager = getAssets();
		try
		{
			InputStream open = manager.open("share.json");
			String streamToString = HHStreamUtils.convertStreamToString(open);
			JSONObject jsonObject = new JSONObject(streamToString);
			appID = jsonObject.optString("weixin");
			Log.i("chenyuan", "app id:"+appID);
		} catch (Exception e)
		{
			HHLog.i(tag, "getWXAppID", e);
		}
		return appID;

	}
	@Override
	public void onReq(BaseReq arg0)
	{
		
	}
	@Override
	public void onResp(BaseResp arg0)
	{
		HHLog.i(tag, "app receive weixin responce:"+arg0);
		if (arg0 instanceof SendMessageToWX.Resp&&arg0.getType()==ConstantsAPI.COMMAND_SENDMESSAGE_TO_WX)
		{
			Intent intent;
			switch (arg0.errCode)
			{
			case BaseResp.ErrCode.ERR_OK://代表的是成功
				HHLog.i(tag, "weixin share success");
				intent = new Intent(HHConstantParam.ACTION_SHARE_SUCCESS);
				break;
			case BaseResp.ErrCode.ERR_USER_CANCEL://代表的是取消分享
				HHLog.i(tag, "weixin share cenceled");
				intent = new Intent(HHConstantParam.ACTION_SHARE_CANCEL);
				break;
			default:
				HHLog.i(tag, "weixin share failed");
				intent = new Intent(HHConstantParam.ACTION_SHARE_FAILED);
				break;
			}
			sendBroadcast(intent);
		}
		finish();
	}


}
