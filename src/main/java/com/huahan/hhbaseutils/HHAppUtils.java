package com.huahan.hhbaseutils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import com.huahan.hhbaseutils.model.HHApkInfo;

import java.io.File;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.content.Context.ACTIVITY_SERVICE;

/**
 * 和App相关的工具类
 *
 * @author yuan
 */
public class HHAppUtils {

    private static final String tag = HHAppUtils.class.getName();

    /**
     * 判断是否安装某应用程序
     *
     * @param context
     * @param packagename 应用的包名
     * @return true，已经安装该应用；false没有安装该应用
     */
    public static boolean isAppInstall(Context context, String packagename) {
        PackageInfo packageInfo = null;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(packagename, 0);
        } catch (NameNotFoundException e) {
            packageInfo = null;
        }
        if (packageInfo == null) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 跳转到其他app。如果该应用存在的话跳转到该应用，否则不执行任何操作
     *
     * @param context
     * @param packageName 其他应用的包名
     */
    public static void jumpToOtherApp(Context context, String packageName) {
        PackageManager packageManager = context.getPackageManager();
        Intent intent = new Intent();
        try {
            intent = packageManager.getLaunchIntentForPackage(packageName);
            context.startActivity(intent);
        } catch (Exception e) {
            HHLog.i(tag, "toOtherApp", e);
        }

    }

    /**
     * 安装应用程序.如果路径合法并且文件存在的时候回安装文件，否则不执行任何操作
     *
     * @param context
     * @param filePath 文件的路径
     */
    public static void installApp(Context context, String filePath) {
        File file = new File(filePath);
        if (!TextUtils.isEmpty(filePath) && file.exists()) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(new File(filePath)),
                    "application/vnd.android.package-archive");
            context.startActivity(intent);
        }
    }

    /**
     * 卸载应用程序
     *
     * @param context
     * @param packageName 应用的包名
     */
    public static void unInstallApp(Context context, String packageName) {
        Uri packageURI = Uri.parse("package:" + packageName);
        Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);
        context.startActivity(uninstallIntent);
    }

    /**
     * 判断App的快捷方式是否存在
     *
     * @param context
     * @param appName 应用的名称；或者创建的快捷方式显示的名字
     * @return
     */
    public static boolean isShortCutExist(Context context, String appName) {
        boolean isInstallShortcut = false;
        final String AUTHORITY = getAuthorityFromPermission(context);
        final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/favorites?notify=true");
        Cursor c = context.getContentResolver().query(CONTENT_URI, null, "title=?", new String[]{appName}, null);
        if (null != c && c.getCount() > 0) {
            isInstallShortcut = true;
        }
        return isInstallShortcut;
    }

    /**
     * 获取Launcher应用提供你的Provider的签名
     *
     * @param context
     * @return
     */
    private static String getAuthorityFromPermission(Context context) {

        List<PackageInfo> packs = context.getPackageManager().getInstalledPackages(PackageManager.GET_PROVIDERS);
        if (packs != null) {
            for (PackageInfo pack : packs) {
                ProviderInfo[] providers = pack.providers;
                if (providers != null) {
                    for (ProviderInfo provider : providers) {
                        if (isMatchPermission(provider.readPermission))
                            return provider.authority;
                        if (isMatchPermission(provider.writePermission))
                            return provider.authority;
                    }
                }
            }
        }
        return null;
    }

    /**
     * 判断当前的权限是否符合要求
     *
     * @param permission
     * @return
     */
    private static boolean isMatchPermission(String permission) {
        if (TextUtils.isEmpty(permission)) {
            return false;
        }
        String pattern = "com.android.launcher[1-3]?.permission.READ_SETTINGS";
        Pattern pat = Pattern.compile(pattern);
        Matcher matcher = pat.matcher(permission);
        return matcher.matches();
    }

    /**
     * 获取应用程序版本号,如果获取失败则返回-1
     *
     * @param context
     * @param packageName 软件的包名
     * @return
     */
    public static int getVerCode(Context context, String packageName) {
        int verCode = -1;
        try {
            if (TextUtils.isEmpty(packageName)) {
                packageName = context.getPackageName();
            }
            verCode = context.getPackageManager().getPackageInfo(packageName, 0).versionCode;
        } catch (NameNotFoundException e) {

        }
        return verCode;
    }

    /**
     * 获取应用的版本号。获取失败返回-1
     *
     * @param context
     * @return
     */
    public static int getVerCode(Context context) {
        return getVerCode(context, null);
    }

    /**
     * 获取应用程序版本名称，获取失败返回空字符串
     *
     * @param context
     * @param packageName 应用的包名
     * @return
     */
    public static String getVerName(Context context, String packageName) {
        String verName = "";
        try {
            if (TextUtils.isEmpty(packageName)) {
                packageName = context.getPackageName();
            }
            verName = context.getPackageManager().getPackageInfo(packageName, 0).versionName;
        } catch (NameNotFoundException e) {
        }
        return verName;
    }

    /**
     * 获取应用的版本名称，获取失败返回空字符串
     *
     * @param context
     * @return
     */
    public static String getVerName(Context context) {
        return getVerName(context, null);
    }

    /**
     * 获取一个apk文件的信息
     *
     * @param apkPath apk文件的本地路径
     * @return 获取失败返回null
     */
    public static HHApkInfo getApkInfo(Context context, String apkPath) {
        HHApkInfo info = null;
        if (!TextUtils.isEmpty(apkPath)) {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageArchiveInfo = packageManager.getPackageArchiveInfo(apkPath, PackageManager.GET_ACTIVITIES | PackageManager.GET_META_DATA);
            if (packageArchiveInfo != null) {

                info = new HHApkInfo();

                ApplicationInfo applicationInfo = packageArchiveInfo.applicationInfo;
                applicationInfo.sourceDir = apkPath;
                applicationInfo.publicSourceDir = apkPath;
                info.setVersionCode(packageArchiveInfo.versionCode);
                info.setVersionName(packageArchiveInfo.versionName);
                info.setLogo(packageManager.getApplicationIcon(applicationInfo));
                info.setAppName(packageManager.getApplicationLabel(applicationInfo).toString());
                info.setPackageName(packageArchiveInfo.packageName);

            }
        }
        return info;
    }

    /**
     * 获取App具体设置
     *
     * @param context 上下文
     */
    public static void getAppDetailsSettings(Context context, int requestCode) {
        getAppDetailsSettings(context, context.getPackageName(), requestCode);
    }

    /**
     * 获取App具体设置
     *
     * @param context     上下文
     * @param packageName 包名
     */
    public static void getAppDetailsSettings(Context context, String packageName, int requestCode) {
        ((AppCompatActivity) context).startActivityForResult(getAppDetailsSettingsIntent(packageName), requestCode);
    }

    /**
     * 获取App具体设置的意图
     *
     * @param packageName 包名
     * @return intent
     */
    public static Intent getAppDetailsSettingsIntent(String packageName) {
        Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
        intent.setData(Uri.parse("package:" + packageName));
        return intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    /**
     * 通过任务管理器杀死进程
     * 需添加权限 {@code <uses-permission android:name="android.permission.RESTART_PACKAGES">}<p> </p>
     *
     * @param context
     */
    public static void restart(Context context) {
        int currentVersion = android.os.Build.VERSION.SDK_INT;
        if (currentVersion > android.os.Build.VERSION_CODES.ECLAIR_MR1) {
            Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(startMain);
            System.exit(0);
        } else {// android2.1
            ActivityManager am = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
            am.restartPackage(context.getPackageName());
        }
    }
}
