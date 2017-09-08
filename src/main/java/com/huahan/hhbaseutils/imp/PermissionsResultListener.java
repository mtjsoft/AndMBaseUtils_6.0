package com.huahan.hhbaseutils.imp;

/**
 * 权限申请接口
 * Created by Administrator on 2017/8/23.
 */

public interface PermissionsResultListener {
    void onPermissionGranted();

    void onPermissionDenied();
}
