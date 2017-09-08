package com.huahan.hhbaseutils.imp;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.huahan.hhbaseutils.HHLog;
import com.lzy.okgo.callback.AbsCallback;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * 自定义JsonCallBack
 * Created by matengjiao on 2017/8/13.
 */

public abstract class HHJsonCallBack<T> extends AbsCallback {

    @Override
    public T convertResponse(Response response) throws Throwable {
        ResponseBody body = response.body();
        if (body == null) {
            return null;
        }
        T data = null;
        Gson gson = new Gson();
        JsonReader jsonReader = new JsonReader(body.charStream());
        Type genType = getClass().getGenericSuperclass();
        Type type = ((ParameterizedType) genType).getActualTypeArguments()[0];
        data = gson.fromJson(jsonReader, type);
        return data;
    }
}
