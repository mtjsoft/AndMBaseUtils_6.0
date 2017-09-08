package com.huahan.hhbaseutils.model;

import com.huahan.hhbaseutils.imp.HHNameValueList;

import java.util.ArrayList;

public abstract class HHAbsNameValueModel implements HHNameValueList
{
	/**
	 * 保存键值对信息
	 */
	protected ArrayList<HHBasicNameValuePair> nameValueListIgnore;
}

