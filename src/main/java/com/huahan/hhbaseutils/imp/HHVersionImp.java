package com.huahan.hhbaseutils.imp;

public interface HHVersionImp
{
	/**
	 * 获取新版本的名称
	 * @return
	 */
	String getVersionName();
	/**
	 * 获取新版本的版本号
	 * @return
	 */
	int getVersionCode();
	/**
	 * 获取新版本的下载地址
	 * @return
	 */
	String getUpdateAddress();
	/**
	 * 获取新版本的更新的内容
	 * @return
	 */
	String getUpdateContent();
	/**
	 * 获取新版本的发布的时间
	 * @return
	 */
	String getUpdateTime();
}
