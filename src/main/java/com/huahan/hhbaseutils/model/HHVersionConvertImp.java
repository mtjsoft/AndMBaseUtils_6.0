package com.huahan.hhbaseutils.model;

import com.huahan.hhbaseutils.imp.HHVersionImp;

/**
 * 定义了一个接口，改接口实现的功能获取数据，并且把数据转换成一个versionmodel
 * @author yuan
 *
 */
public interface HHVersionConvertImp
{
	HHVersionImp convertToVersionModel();
}
