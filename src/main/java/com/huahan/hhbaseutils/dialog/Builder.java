package com.huahan.hhbaseutils.dialog;

import com.huahan.hhbaseutils.imp.HHDialogClickListener;

/**
 * Created by Administrator on 2017/8/19.
 */

public abstract class Builder {
    public abstract Builder buildTitle(String title);

    public abstract Builder buildMsg(String msg);

    public abstract Builder buildSureClickListener(HHDialogClickListener sureClickListener);

    public abstract Builder buildCancelClickListener(HHDialogClickListener cancelClickListener);

    public abstract Builder buildSureTextColor(int colorId);

    public abstract Builder buildCanCancel(boolean canCancel);

    public abstract Builder buildShowAll(boolean showAll);

    public abstract void showDialog();
}
