package com.huahan.hhbaseutils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.support.v4.app.ActivityCompat;

import com.huahan.hhbaseutils.ui.HHActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * 权限相关的工具类
 *
 * @author chen3
 */
public class HHPermissionUtils {
    private static final String tag = HHPermissionUtils.class.getSimpleName();

    /**
     * 判断是否授予了权限
     *
     * @param context    上下文对象
     * @param permission 授予的权限android.Manifest.permission.xxx
     * @return
     */

    public static boolean isPermissionGrant(Context context, String permission) {
        return ActivityCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * 请求在AndroidManifest中生命的所有的权限
     *
     * @param context 上下文对象
     */
    @TargetApi(23)
    public static void requireAllPermission(Activity context) {
        if (Build.VERSION.SDK_INT > 22) {
            PackageManager manager = context.getPackageManager();
            PackageInfo packageInfo = null;
            try {
                HHLog.i(tag, context.getPackageName());
                packageInfo = manager.getPackageInfo(context.getPackageName(), PackageManager.GET_PERMISSIONS);
            } catch (NameNotFoundException e) {
            }
            if (packageInfo != null) {
                String[] permissions = packageInfo.requestedPermissions;
                for (String permissionInfo : permissions) {
                    HHLog.i(tag, permissionInfo);
                }
                context.requestPermissions(permissions, HHActivity.CODE_REQUIRE_ALL_PERMISSION);
            }
        }
    }

    /**
     * 强制请求所有权限
     *
     * @param context 上下文对象
     */
    @TargetApi(23)
    public static void forceRequireAllPermission(Activity context) {
        if (Build.VERSION.SDK_INT > 22) {
            PackageManager manager = context.getPackageManager();
            PackageInfo packageInfo = null;
            try {
                HHLog.i(tag, context.getPackageName());
                packageInfo = manager.getPackageInfo(context.getPackageName(), PackageManager.GET_PERMISSIONS);
            } catch (NameNotFoundException e) {
            }
            if (packageInfo != null) {
                String[] permissions = packageInfo.requestedPermissions;
                List<String> permissionList = new ArrayList<String>();
                for (String permissionInfo : permissions) {
                    HHLog.i(tag, permissionInfo);
                    if (context.checkSelfPermission(permissionInfo) == PackageManager.PERMISSION_DENIED) {
                        permissionList.add(permissionInfo);
                    }
                }
                if (permissionList.size() > 0) {
                    String[] array = new String[permissionList.size()];
                    for (int i = 0; i < array.length; i++) {
                        array[i] = permissionList.get(i);
                    }
                    context.requestPermissions(array, HHActivity.CODE_FORCE_REQUIRE_ALL_PERMISSION);
                }

            }
        }
    }
}
