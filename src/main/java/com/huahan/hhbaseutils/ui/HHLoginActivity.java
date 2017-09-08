package com.huahan.hhbaseutils.ui;

import android.os.Message;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.view.View;
import android.view.View.OnClickListener;

import com.huahan.hhbaseutils.HHCommonUtils;
import com.huahan.hhbaseutils.HHLog;
import com.huahan.hhbaseutils.HHViewHelper;
import com.huahan.hhbaseutils.R;
import com.huahan.hhbaseutils.model.HHLoginResult;
import com.huahan.hhbaseutils.model.HHLoginResult.Result;
import com.huahan.hhbaseutils.view.processbutton.CircularProgressButton;
import com.huahan.hhbaseutils.view.processbutton.CircularProgressButton.OnCompleteAnimFinishListener;

public abstract class HHLoginActivity extends HHBaseActivity 
{
	private static final int USER_LOGIN=0;
	private AppCompatEditText mLoginNamEditText;
	private AppCompatEditText mLoginPwdEditText;
	private AppCompatImageView mNameImageView;
	private AppCompatImageView mPwdImageView;
	private CircularProgressButton mLoginButton;
	private HHLoginResult mLoginResult;
	
	
	
	@Override
	public View initView()
	{
		
		View view=View.inflate(getPageContext(), R.layout.hh_activity_login_material, null);
		mLoginNamEditText=HHViewHelper.getViewByID(view, R.id.hh_id_login_name);
		mLoginPwdEditText=HHViewHelper.getViewByID(view, R.id.hh_id_login_pwd);
		mNameImageView=HHViewHelper.getViewByID(view, R.id.hh_id_login_img_name);
		mPwdImageView=HHViewHelper.getViewByID(view, R.id.hh_id_login_img_pwd);
		mLoginButton=HHViewHelper.getViewByID(view, R.id.hh_id_login_login);
		return view;
	}

	@Override
	public void initValues()
	{
		HHCommonUtils.tintViewBackground(this, mNameImageView);
		HHCommonUtils.tintViewBackground(this, mPwdImageView);
		mLoginButton.setIndeterminateProgressMode(true);
		
	}
	@Override
	public void initListeners()
	{
		
		mLoginButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				//当前的状态是初始化或者登陆失败的状态，在这种情况下，需要用户去登陆
				HHLog.i("chenyuan", "process=="+mLoginButton.getProgress());
				if (mLoginButton.getProgress()==0||mLoginButton.getProgress()==-1)
				{
					userLoginThread();
					mLoginButton.setProgress(50);
				}
			}
		});
		mLoginButton.setOnCompleteAnimFinishListener(new OnCompleteAnimFinishListener()
		{
			
			@Override
			public void onCompleteAnimFinished()
			{
				onLoginSuccess();
			}
		});
	}
	private void userLoginThread()
	{
		final String userName=mLoginNamEditText.getText().toString();
		final String userPwd=mLoginPwdEditText.getText().toString();
		if (checkUserInput(userName, userPwd))
		{
			new Thread(new Runnable()
			{
				@Override
				public void run()
				{
					mLoginResult=userLogin(userName,userPwd);
					sendHandlerMessage(USER_LOGIN);
				}
			}).start();
		}
	}
	
	@Override
	public void processHandlerMsg(Message msg)
	{
		if (isActivityDestory())
		{
			return ;
		}
		switch (msg.what)
		{
		case USER_LOGIN:
			//如果使用者在userLogin方法中返回null或者在忘记设置result的值，再或者设置了result的值，但是result的值网络异常，则设置
			//mLoginButton的状态和现实的文字
			if (mLoginResult==null||mLoginResult.result==null||mLoginResult.result==Result.NETERROR)
			{
				mLoginButton.setErrorText(getString(R.string.hh_net_error));
				mLoginButton.setProgress(-1);
			}else if(mLoginResult.result==Result.FAILED)
			{
				String failedText=getLoginFailedText(mLoginResult.resultCode);
				mLoginButton.setErrorText(failedText);
				mLoginButton.setProgress(-1);
			}else {
				mLoginButton.setProgress(100);
			}
			break;

		default:
			break;
		}
		
	}
	/**
	 * 在这里调用用户登录的接口，并返回相应的结果数据,在这方法中必须返回一个HHLoginResult对象,该对象不允许为null。
	 * 如果登陆成功需要使用者在该方法内部保存获取到用户的数据，用户的数据不允许在handler中再去保存
	 * @return		用户登录的结果
	 */
	protected abstract HHLoginResult userLogin(String userName,String userPwd);
	
//	private HHLoginResult userLogin(String userName,String userPwd,String...args)
//	{
//		return null;
//	}
	/**
	 * 获取登录失败的时候显示的字符串，字符串的长度不宜过长
	 * @param resultCode		接口返回的登陆状态
	 * @return
	 */
	protected String getLoginFailedText(int resultCode)
	{
		return getString(R.string.hh_login_failed);
	}
	protected abstract void onLoginSuccess();
	
	protected abstract boolean checkUserInput(String userName,String userPwd);
	
	
	
}
