package com.guyu.android.base;

import android.util.Log;

import com.blankj.utilcode.util.ToastUtils;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * BaseObserver
 * Created by shenyunhuan on 2017/6/23.
 */
public abstract class BaseObserver<T> implements Observer<BaseEntity<T>> {

    private static final String TAG = "BaseObserver";


    @Override
    public void onSubscribe(Disposable d) {

    }

    @Override
    public void onNext(BaseEntity<T> value) {
        if (value.isSuccess()) {
            T t = value.getData();
            onHandleSuccess(t);
        } else {
//            if (value.getCode() != 20002&&value.getCode() != 20001) {
//                onHandleError(MyMessage.getMessage(value.getCode()));
//                onHandleError(value);
//            }
            onHandleError(value);
        }
    }

    @Override
    public void onError(Throwable e) {

        Log.e(TAG, "onError:" + e.toString());
        if(e.toString().contains("SocketTimeoutException")){
//            BaseEntity b = new BaseEntity();
//            b.setCode(-1);
//            onHandleError(MyMessage.getMessage(b.getCode()));
//            onHandleError(b);
        }
    }

    @Override
    public void onComplete() {
        Log.d(TAG, "onComplete");
    }

    protected abstract void onHandleSuccess(T t);

//    protected void onHandleError(String msg) {
//
//        ToastUtils.showLong(msg);
//
//
//    }


    protected void onHandleError(BaseEntity<T> value) {
        ToastUtils.showLong(value.getMessage());
    }
}
