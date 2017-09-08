package com.huahan.hhbaseutils;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.huahan.hhbaseutils.dialog.Builder;
import com.huahan.hhbaseutils.imp.HHDialogClickListener;

/**
 * 对话框操作的工具类
 *
 * @author mtj
 */
public class HHDialogUtils extends Builder {
    private static Context thisContext;
    private static HHDialogUtils hhDialogUtils;
    private static Dialog dialog;
    private static TextView titleTextView;
    private static TextView msgTextView;
    private static TextView cancelTextView;
    private static TextView sureTextView;
    private static View lineView;

    public static HHDialogUtils builder(Context context) {
        thisContext = context;
        dialog = new Dialog(context, R.style.huahan_dialog);
        View view = View.inflate(context, R.layout.hh_dialog_capture_tip, null);
        titleTextView = HHViewHelper.getViewByID(view,
                R.id.tv_dialog_title);
        msgTextView = HHViewHelper.getViewByID(view,
                R.id.tv_dialog_msg);
        cancelTextView = HHViewHelper.getViewByID(view,
                R.id.tv_dialog_cancel);
        sureTextView = HHViewHelper.getViewByID(view,
                R.id.tv_dialog_sure);
        lineView = HHViewHelper.getViewByID(view, R.id.view);
        dialog.setContentView(view);
        android.view.WindowManager.LayoutParams attributes = dialog.getWindow()
                .getAttributes();
        attributes.width = HHScreenUtils.getScreenWidth(context)
                - HHDensityUtils.dip2px(context, 30);
        dialog.getWindow().setAttributes(attributes);
        if (hhDialogUtils == null) {
            hhDialogUtils = new HHDialogUtils();
        }
        return hhDialogUtils;
    }

    @Override
    public Builder buildTitle(String title) {
        titleTextView.setText(title);
        return hhDialogUtils;
    }

    @Override
    public Builder buildMsg(String msg) {
        msgTextView.setText(msg);
        return hhDialogUtils;
    }

    @Override
    public Builder buildSureClickListener(final HHDialogClickListener sureClickListener) {
        sureTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dialog.dismiss();
                if (sureClickListener != null) {
                    sureClickListener.onClick(dialog, v);
                }
            }
        });
        return hhDialogUtils;
    }

    @Override
    public Builder buildCancelClickListener(final HHDialogClickListener cancelClickListener) {
        cancelTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dialog.dismiss();
                if (cancelClickListener != null) {
                    cancelClickListener.onClick(dialog, v);
                }
            }
        });
        return hhDialogUtils;
    }

    @Override
    public Builder buildSureTextColor(int colorId) {
        sureTextView.setTextColor(thisContext.getResources().getColor(colorId));
        cancelTextView.setTextColor(thisContext.getResources().getColor(colorId));
        return hhDialogUtils;
    }

    @Override
    public Builder buildCanCancel(boolean canCancel) {
        dialog.setCancelable(canCancel);
        return hhDialogUtils;
    }

    @Override
    public Builder buildShowAll(boolean showAll) {
        if (!showAll) {
            lineView.setVisibility(View.GONE);
            cancelTextView.setVisibility(View.GONE);
        }
        return hhDialogUtils;
    }

    @Override
    public void showDialog() {
        dialog.show();
        thisContext = null;
    }
}
