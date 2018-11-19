package com.guyu.android.base;


/**
 * Created by shenyunhuan on 2017/7/7.
 */

public interface CallBack<T> {
    void onSuccess(T t);
    void onFail();
    void onError(String msg);
}
