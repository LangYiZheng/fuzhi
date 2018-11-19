package com.guyu.android.network;


import com.guyu.android.base.BaseObserver;

import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.Observer;
import retrofit2.Retrofit;

/**
 * Created by Administrator on 2017/6/28.
 */

public class Networks {
    private static Retrofit retrofit = RetrofitUtils.getRetrofit();

    public static Retrofit getRetrofit() {
        return retrofit;
    }

    public static Retrofit getDownloadRetrofit() {
        return RetrofitUtils.getDownloadRetrofit();
    }

    public static<T> void subscribeObserver(Observable observable, BaseObserver<T> observer){//带有基类BaseEntity<T>切换主线程
        observable.compose(RxSchedulers.<T>io_main()).subscribe(observer);
    }

    public static<T> void subscribeObserver(Observable observable, Observer<T> observer){//切换主线程
        observable.compose(RxSchedulers.<T>io_main()).subscribe(observer);
    }
}
