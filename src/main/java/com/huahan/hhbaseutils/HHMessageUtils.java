package com.huahan.hhbaseutils;

import android.content.Context;
import android.os.Message;

/**
 * 消息管理器
 * Created by Administrator on 2017/8/15.
 */

public class HHMessageUtils {

    public static Message getMessage(Context context, int what, String result) {
        Message message = new Message();
        message.what = what;
        if (result == null) {
            message.arg1 = -1;
            message.obj = context.getString(R.string.net_error);
        } else {
            message.arg1 = HHJsonParseUtils.getResponceStatus(result);
            message.obj = HHJsonParseUtils.getResponceMsg(result);
        }
        return message;
    }
}
