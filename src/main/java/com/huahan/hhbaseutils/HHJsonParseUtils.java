package com.huahan.hhbaseutils;

import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

import com.huahan.hhbaseutils.HHEncryptUtils;

public class HHJsonParseUtils {
    /**
     * 获取服务器返回的结果码 网络错误时返回-1
     *
     * @return
     */
    public static int getResponceCode(String result) {
        return getResponceCode(result, "code");
    }

    /**
     * 获取服务器返回的结果码 网络错误时返回-1
     *
     * @return
     */
    public static int getResponceStatus(String result) {
        return getResponceCode(result, "status");
    }


    /**
     * 获取服务器返回的msg
     *
     * @return
     */
    public static String getResponceMsg(String result) {
        return getParamInfo(result, "msg");
    }

    /**
     * 获取服务器返回的结果码 网络错误时返回-1
     *
     * @param codeName 结果码的标识
     * @return
     */
    public static int getResponceCode(String result, String codeName) {
        int code = -1;
        if (!TextUtils.isEmpty(result)) {
            try {
                JSONObject jsonObject = new JSONObject(result);
                code = Integer.valueOf(jsonObject.getString(codeName));
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return code;
    }

    /**
     * 获取指定字段
     *
     * @param data       数据源
     * @param resultName 一级字段名
     * @param name       二级字段名   为空返回一级字段
     * @return
     */
    public static String getResult(String data, String resultName, String name) {
        JSONObject jsonObject;
        String content = "";
        try {
            jsonObject = new JSONObject(data);
            String result = jsonObject.getString(resultName);
            if (TextUtils.isEmpty(name)) {
                result = HHEncryptUtils.decodeBase64(result);
                return result;
            } else {
                JSONObject jObject = new JSONObject(result);
                content = jObject.getString(name);
                content = HHEncryptUtils.decodeBase64(content);
            }
            return content;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getParamInfo(String data, String paramName) {
        if (!TextUtils.isEmpty(data)) {
            try {
                JSONObject jsonObject = new JSONObject(data);
                String result = jsonObject.optString(paramName);
                if (TextUtils.isEmpty(result)) {
                    return "";
                }
                return result;
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return "";
    }
}
