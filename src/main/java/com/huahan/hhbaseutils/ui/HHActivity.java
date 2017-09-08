package com.huahan.hhbaseutils.ui;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SlidingPaneLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.huahan.hhbaseutils.HHActivityUtils;
import com.huahan.hhbaseutils.HHAppUtils;
import com.huahan.hhbaseutils.HHDialogUtils;
import com.huahan.hhbaseutils.HHViewHelper;
import com.huahan.hhbaseutils.R;
import com.huahan.hhbaseutils.imp.HHDialogClickListener;
import com.huahan.hhbaseutils.imp.HHPageBaseOper;
import com.huahan.hhbaseutils.imp.HHTopViewManagerImp;
import com.huahan.hhbaseutils.imp.PermissionsResultListener;
import com.huahan.hhbaseutils.manager.HHDefaultTopViewManager;
import com.huahan.hhbaseutils.manager.HHUiTopManager;
import com.huahan.hhbaseutils.manager.HHUiTopManager.TopMode;
import com.huahan.hhbaseutils.model.HHWeakHandler;

import java.lang.reflect.Field;
import java.util.ArrayList;

public abstract class HHActivity extends AppCompatActivity implements HHPageBaseOper {

    /**
     * 申请权限
     */
    public static final int CODE_REQUIRE_PERMISSION = 0;
    /**
     * 请求所有权限
     */
    public static final int CODE_REQUIRE_ALL_PERMISSION = 1;
    /**
     * 强制请求所有的权限，如果有一个没有通过，就一直申请
     */
    public static final int CODE_FORCE_REQUIRE_ALL_PERMISSION = 2;
    //定义的是整个页面的根布局
    private RelativeLayout mParentLayout;
    //定义的是头部和底部
    private LinearLayout mTopLayout, mBottomLayout;
    //定义的是中间显示的内容
    private FrameLayout mContainerLayout;
    //整个页面显示的视图
    private View mBaseView;
    //保存当前页面的上下文对象
    private Context mContext;
    //ui头部的管理类
    private HHUiTopManager mTopManager;
    private Bundle mSavedInstanceBundle;
    /**
     * 表示的是当前的页面是否执行了onDestory方法
     */
    private boolean mIsDestory = false;

    // For Android 6.0
    private PermissionsResultListener mListener;
    //申请标记值
    public static final int REQUEST_CODE_ASK_PERMISSIONS = 100;
    //手动开启权限requestCode
    public static final int SETTINGS_REQUEST_CODE = 200;
    //拒绝权限后是否关闭界面或APP
    private boolean mNeedFinish = false;
    //界面传递过来的权限列表,用于二次申请
    private ArrayList<String> mPermissionsList = new ArrayList<>();
    //必要全选,如果这几个权限没通过的话,就无法使用APP
    public static ArrayList<String> FORCE_REQUIRE_PERMISSIONS = new ArrayList<String>() {
        {
            add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            add(Manifest.permission.ACCESS_WIFI_STATE);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceBundle) {
//        initSwipeBackFinish();
        super.onCreate(savedInstanceBundle);
        //设置Activity的动画
//        overridePendingTransition(R.anim.hh_window_in_right, R.anim.hh_window_out_left);
        //切换语言
//		Locale appLanguage = HHSystemUtils.getAppLanguage(this);
//		HHSystemUtils.changeLanguage(this, appLanguage);
        HHActivityUtils.getInstance().addActivity(this);
        mSavedInstanceBundle = savedInstanceBundle;
        mContext = this;
        setContentView(R.layout.hh_activity_main);
        initBaseInfo();
        mTopManager = new HHUiTopManager(this);
        initTopLayout();
        initOther();

    }

    /**
     * 获取Activity保存的bundle信息
     *
     * @return
     */
    protected Bundle getSavedInstanceBundle() {
        return mSavedInstanceBundle;
    }

    protected void initTopLayout() {
        mTopManager.showTopView(TopMode.DEFAULT);
    }

    /**
     * 在onCreate中调用，用于初始化整个页面的基本结构<br/>
     */
    protected void initOther() {

    }

    private void initBaseInfo() {
        //获取基本架构的基本控件
        mParentLayout = HHViewHelper.getViewByID(this, R.id.hh_rl_base_parent);
        mBottomLayout = HHViewHelper.getViewByID(this, R.id.hh_ll_base_bottom);
        mTopLayout = HHViewHelper.getViewByID(this, R.id.hh_ll_base_top);
        mContainerLayout = HHViewHelper.getViewByID(this, R.id.hh_fl_base_container);
    }

    /**
     * 返回头部管理器
     *
     * @return
     */
    public final HHUiTopManager getTopManager() {
        return mTopManager;
    }

    /**
     * 表示当前的页面是否执行了ondestory方法
     *
     * @return
     */
    protected boolean isActivityDestory() {
        return mIsDestory;
    }

    @Override
    protected void onDestroy() {
        HHActivityUtils.getInstance().removeActivity(this);
        super.onDestroy();
        mIsDestory = true;
    }

    /**
     * 把创建的View添加到显示内容的中间容器.<br/>
     *
     * @param index 插入的位置
     * @param view  插入的视图
     */
    protected void addViewToContainer(int index, View view) {
        mContainerLayout.addView(view, index, new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
    }

    /**
     * 返回当前页面的跟布局
     *
     * @return
     */
    protected RelativeLayout getBaseParentLayout() {
        return mParentLayout;
    }

    /**
     * 获取当前页面显示的上边的布局
     *
     * @return
     */
    public LinearLayout getBaseTopLayout() {
        return mTopLayout;
    }

    /**
     * 获取当前页面显示的下边的布局
     *
     * @return
     */
    protected LinearLayout getBaseBottomLayout() {
        return mBottomLayout;
    }

    @Override
    public void setBaseView(View view) {
        if (mBaseView != null) {
            mContainerLayout.removeView(mBaseView);
        }
        mBaseView = view;
        addViewToContainer(0, view);
    }

    @Override
    public Context getPageContext() {
        return mContext;
    }

    @Override
    public View getBaseView() {
        return mBaseView;
    }

    @Override
    public FrameLayout getBaseContainerLayout() {
        return mContainerLayout;
    }

    @Override
    public void addViewToContainer(View view) {
        addViewToContainer(-1, view);
    }

    /**
     * 返回处理消息的Handler对象
     *
     * @return
     */
    protected Handler getHandler() {
        return this.mHandler;
    }

    /**
     * 获取一个Message对象
     *
     * @return
     */
    protected Message getNewHandlerMessage() {
        return mHandler.obtainMessage();
    }

    /**
     * 发送消息
     *
     * @param msg
     */
    protected void sendHandlerMessage(Message msg) {
        mHandler.sendMessage(msg);
    }

    /**
     * 发送消息
     *
     * @param what
     */
    protected void sendHandlerMessage(int what) {
        Message msg = getNewHandlerMessage();
        msg.what = what;
        sendHandlerMessage(msg);
    }

    /**
     * 处理消息的handler
     */

    private HHWeakHandler<Activity> mHandler = new HHWeakHandler<Activity>(this) {
        @Override
        public void processHandlerMessage(Message msg) {
            processHandlerMsg(msg);
        }
    };

    /**
     * 根据View的ID，在parentView中查找ID为viewID的View
     *
     * @param parentView
     * @param viewID
     * @return
     */
    public <T> T getViewByID(View parentView, int viewID) {
        return HHViewHelper.getViewByID(parentView, viewID);
    }

    /**
     * 设置当前页面显示的标题（只有在当前页面使用的是HHDefaultTopViewManager的时候才有效果）
     *
     * @param pageTitle
     */
    public void setPageTitle(String pageTitle) {
        HHTopViewManagerImp avalibleManager = mTopManager.getAvalibleManager();
        if (avalibleManager instanceof HHDefaultTopViewManager) {
            HHDefaultTopViewManager defaultTopViewManager = (HHDefaultTopViewManager) avalibleManager;
            defaultTopViewManager.getTitleTextView().setText(pageTitle);
        }
    }

    /**
     * 设置当前页面的标题
     *
     * @param resID 标题的资源文件的ID
     */
    public void setPageTitle(int resID) {
        setPageTitle(getString(resID));
    }

    /**
     * 设置必要申请的权限
     *
     * @param permissionsList
     */
    public void setPermissionsList(ArrayList<String> permissionsList) {
        FORCE_REQUIRE_PERMISSIONS = permissionsList;
    }

    /**
     * 权限允许或拒绝对话框
     *
     * @param permissions 需要申请的权限
     * @param needFinish  如果必须的权限没有允许的话，是否需要finish当前 Activity
     * @param callback    回调对象
     */
    protected void requestPermission(final ArrayList<String> permissions, final boolean needFinish,
                                     final PermissionsResultListener callback) {
        if (permissions == null || permissions.size() == 0) {
            return;
        }
        mNeedFinish = needFinish;
        mListener = callback;
        mPermissionsList = permissions;
        if (Build.VERSION.SDK_INT >= 23) {
            //获取未通过的权限列表
            ArrayList<String> newPermissions = checkEachSelfPermission(permissions);
            if (newPermissions.size() > 0) {// 是否有未通过的权限
                requestEachPermissions(newPermissions.toArray(new String[newPermissions.size()]));
            } else {// 权限已经都申请通过了
                if (mListener != null) {
                    mListener.onPermissionGranted();
                }
            }
        } else {
            if (mListener != null) {
                mListener.onPermissionGranted();
            }
        }
    }

    /**
     * 检察每个权限是否申请
     *
     * @param permissions
     * @return newPermissions.size > 0 表示有权限需要申请
     */
    private ArrayList<String> checkEachSelfPermission(ArrayList<String> permissions) {
        ArrayList<String> newPermissions = new ArrayList<String>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                newPermissions.add(permission);
            }
        }
        return newPermissions;
    }

    /**
     * 申请权限前判断是否需要声明
     *
     * @param permissions
     */
    @TargetApi(Build.VERSION_CODES.M)
    private void requestEachPermissions(String[] permissions) {
        if (shouldShowRequestPermissionRationale(permissions)) {// 需要再次声明
            showRationaleDialog(permissions);
        } else {
            ActivityCompat.requestPermissions(this, permissions,
                    REQUEST_CODE_ASK_PERMISSIONS);
        }
    }

    /**
     * 再次申请权限时，是否需要声明
     *
     * @param permissions
     * @return
     */
    private boolean shouldShowRequestPermissionRationale(String[] permissions) {
        for (String permission : permissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 弹出声明的 Dialog
     *
     * @param permissions
     */
    private void showRationaleDialog(final String[] permissions) {
        HHDialogUtils.builder(this)
                .buildMsg(getString(R.string.permissions))
                .buildSureClickListener(new HHDialogClickListener() {
                    @Override
                    public void onClick(Dialog paramDialog, View paramView) {
                        ActivityCompat.requestPermissions(HHActivity.this, permissions,
                                REQUEST_CODE_ASK_PERMISSIONS);
                    }
                })
                .buildCancelClickListener(new HHDialogClickListener() {
                    @Override
                    public void onClick(Dialog paramDialog, View paramView) {
                        if (mNeedFinish) {
                            finish();
                        }
                    }
                })
                .buildShowAll(true)
                .buildCanCancel(false)
                .showDialog();
    }


    /**
     * 申请权限结果的回调
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_ASK_PERMISSIONS && permissions != null) {
            // 获取被拒绝的权限列表
            ArrayList<String> deniedPermissions = new ArrayList<>();
            for (String permission : permissions) {
                if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                    deniedPermissions.add(permission);
                }
            }
            //存在被拒绝的权限
            if (deniedPermissions != null && deniedPermissions.size() > 0) {
                mPermissionsList = deniedPermissions;
                if (mNeedFinish) {
                    showPermissionSettingDialog();
                } else {
                    if (mListener != null) {
                        mListener.onPermissionDenied();
                    }
                }
            } else {
                if (mListener != null) {
                    mListener.onPermissionGranted();
                }
            }
            // 判断被拒绝的权限中是否有包含必须具备的权限
//            ArrayList<String> forceRequirePermissionsDenied =
//                    checkForceRequirePermissionDenied(FORCE_REQUIRE_PERMISSIONS, deniedPermissions);
//            if (forceRequirePermissionsDenied != null && forceRequirePermissionsDenied.size() > 0) {
//                // 必备的权限被拒绝，
//                if (mNeedFinish) {
//                    showPermissionSettingDialog();
//                } else {
//                    if (mListener != null) {
//                        mListener.onPermissionDenied();
//                    }
//                }
//            } else {
//                // 不存在必备的权限被拒绝，可以进首页
//                if (mListener != null) {
//                    mListener.onPermissionGranted();
//                }
//            }
        }
    }

    /**
     * 判断被拒绝的权限中是否有包含必须具备的权限
     *
     * @param forceRequirePermissions
     * @param deniedPermissions
     * @return
     */
    private ArrayList<String> checkForceRequirePermissionDenied(
            ArrayList<String> forceRequirePermissions, ArrayList<String> deniedPermissions) {
        ArrayList<String> forceRequirePermissionsDenied = new ArrayList<>();
        if (forceRequirePermissions != null && forceRequirePermissions.size() > 0
                && deniedPermissions != null && deniedPermissions.size() > 0) {
            for (String forceRequire : forceRequirePermissions) {
                if (deniedPermissions.contains(forceRequire)) {
                    forceRequirePermissionsDenied.add(forceRequire);
                }
            }
        }
        return forceRequirePermissionsDenied;
    }

    /**
     * 手动开启权限弹窗
     */
    private void showPermissionSettingDialog() {
        HHDialogUtils.builder(this)
                .buildMsg(getString(R.string.permissions_must))
                .buildSureClickListener(new HHDialogClickListener() {
                    @Override
                    public void onClick(Dialog paramDialog, View paramView) {
                        paramDialog.dismiss();
                        startAppSettings();
                    }
                })
                .buildCancelClickListener(new HHDialogClickListener() {
                    @Override
                    public void onClick(Dialog paramDialog, View paramView) {
                        paramDialog.dismiss();
                        if (mNeedFinish) {
                            HHAppUtils.restart(HHActivity.this);
                        }
                    }
                }).buildShowAll(true)
                .buildCanCancel(false)
                .showDialog();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //如果需要跳转系统设置页后返回自动再次检查和执行业务 如果不需要则不需要重写onActivityResult
        if (requestCode == SETTINGS_REQUEST_CODE) {
            requestPermission(mPermissionsList, mNeedFinish, mListener);
        }
    }

    /**
     * 启动当前应用设置页面
     */
    private void startAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, SETTINGS_REQUEST_CODE);
    }

    /**
     * 初始化滑动返回
     */
    private void initSwipeBackFinish() {
        if (isSupportSwipeBack()) {
            SlidingPaneLayout slidingPaneLayout = new SlidingPaneLayout(this);
            //通过反射改变mOverhangSize的值为0，这个mOverhangSize值为菜单到右边屏幕的最短距离，
            //默认是32dp
            try {
                //更改属性
                Field field = SlidingPaneLayout.class.getDeclaredField("mOverhangSize");
                field.setAccessible(true);
                field.set(slidingPaneLayout, 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
            //设置监听事件
            slidingPaneLayout.setPanelSlideListener(new slideListener());
            slidingPaneLayout.setSliderFadeColor(getResources().getColor(R.color.transparent));
            // 左侧的透明视图
            View leftView = new View(this);
            leftView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            slidingPaneLayout.addView(leftView, 0);
            ViewGroup decorView = (ViewGroup) getWindow().getDecorView();
            // 右侧的内容视图
            ViewGroup decorChild = (ViewGroup) decorView.getChildAt(0);
            decorChild.setBackgroundColor(getResources()
                    .getColor(android.R.color.white));
            decorView.removeView(decorChild);
            decorView.addView(slidingPaneLayout);
            // 为 SlidingPaneLayout 添加内容视图
            slidingPaneLayout.addView(decorChild, 1);
        }
    }

    /**
     * 是否支持滑动退出
     */
    public boolean isSupportSwipeBack() {
        return true;
    }

    /**
     * 侧滑退出监听
     */
    private class slideListener implements SlidingPaneLayout.PanelSlideListener {

        @Override
        public void onPanelSlide(View panel, float slideOffset) {

        }

        @Override
        public void onPanelOpened(View panel) {
            finish();
        }

        @Override
        public void onPanelClosed(View panel) {

        }
    }
}
