package com.huahan.hhbaseutils.imp;

/**
 * Created by matengjiao on 2017/8/13.
 */

public interface HHCallBackImp<T> {
    void onSuccess(T data);

    void onError(T data);
}
