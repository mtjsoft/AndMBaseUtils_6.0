package com.huahan.hhbaseutils.ui;

import android.app.Application;

import com.huahan.hhbaseutils.R;
import com.huahan.hhbaseutils.constant.HHConstantParam;
import com.huahan.hhbaseutils.model.HHApplicationInfo;
import com.huahan.hhbaseutils.model.HHLoadState;
import com.huahan.hhbaseutils.model.HHLoadViewInfo;

import java.util.Map;

public abstract class HHApplication extends Application {

    private HHApplicationInfo mApplicationInfo;

    @Override
    public void onCreate() {
        super.onCreate();
        mApplicationInfo = new HHApplicationInfo() {

            @Override
            public int getMainColor() {
                return getAppAcentColor();
            }

            @Override
            public void setAppLoadViewInfo() {
                Map<HHLoadState, HHLoadViewInfo> loadViewInfo = getLoadViewInfo();
                if (loadViewInfo != null && !loadViewInfo.isEmpty()) {
                    HHConstantParam.loadViewMap.putAll(loadViewInfo);
                }
            }
        };

    }

    public HHApplicationInfo getHHApplicationInfo() {
        return mApplicationInfo;
    }

    /**
     * 设置当前应用的主色调
     *
     * @return
     */
    protected abstract int getAppAcentColor();

    /**
     * 设置加载状态
     *
     * @return
     */
    protected abstract Map<HHLoadState, HHLoadViewInfo> getLoadViewInfo();

    /**
     * 获取默认图片的ID
     *
     * @return
     */
    protected int getDefaultDrawableID() {
        return R.drawable.hh_default_image;
    }
}
