package com.huahan.hhbaseutils.model;

public class HHLoginResult
{
	public enum Result
	{
		/**
		 * 登陆成功
		 */
		SUCCESS,
		/**
		 * 登陆失败
		 */
		FAILED,
		/**
		 * 网络异常
		 */
		NETERROR
	}
	/**
	 * 接口返回的结果码
	 */
	public int resultCode;
	/**
	 * 用户登陆状态
	 */
	public Result result;
	
}
