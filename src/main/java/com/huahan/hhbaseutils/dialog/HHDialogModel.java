package com.huahan.hhbaseutils.dialog;

import com.huahan.hhbaseutils.imp.HHDialogClickListener;

/**
 * Created by Administrator on 2017/8/19.
 */

public class HHDialogModel {
    private String title;//标题
    private String msg;//消息
    private HHDialogClickListener sureClickListener;//确认监听
    private HHDialogClickListener cancelClickListener;//取消监听
    private boolean isCanCancel = true;//外边缘点击是否可以取消
    private boolean showAll = true;//是否显示取消和确认两个按钮

    public void setTitle(String title) {
        this.title = title;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setSureClickListener(HHDialogClickListener sureClickListener) {
        this.sureClickListener = sureClickListener;
    }

    public void setCancelClickListener(HHDialogClickListener cancelClickListener) {
        this.cancelClickListener = cancelClickListener;
    }

    public void setCanCancel(boolean canCancel) {
        isCanCancel = canCancel;
    }

    public void setShowAll(boolean showAll) {
        this.showAll = showAll;
    }
}
