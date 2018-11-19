package com.guyu.android.network;

import com.google.gson.GsonBuilder;
import com.guyu.android.gis.app.GisQueryApplication;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Administrator on 2016/11/3.
 */
public class RetrofitUtils {


    private static Retrofit mRetrofit;
    private static OkHttpClient mOkHttpClient;

    public static Retrofit getRetrofit(){
        if (mRetrofit==null){
            if (mOkHttpClient==null){
                mOkHttpClient = OkHttp3Utils.getOkHttpClient();
            }
            //Retrofit2使用build设计模式
            mRetrofit = new Retrofit.Builder()
                    //设置服务器的路径
                    .baseUrl(GisQueryApplication.getApp().getProjectconfig().getIsysbaseurl())
                    //添加回调库，默认是Gson
                    .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().serializeNulls().create()))
                    //添加回调库，默认是RxJava
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    //设置使用okhttp网络请求
                    .client(mOkHttpClient)
                    .build();
        }
        return mRetrofit;
    }

    public static Retrofit getDownloadRetrofit(){
        if (mRetrofit==null){
            if (mOkHttpClient==null){
                mOkHttpClient = OkHttp3Utils.getDownloadOkHttpClient();
            }
            //Retrofit2使用build设计模式
            mRetrofit = new Retrofit.Builder()
                    //设置服务器的路径
                    .baseUrl(GisQueryApplication.getApp().getProjectconfig().getIsysbaseurl())
                    //添加回调库，默认是Gson
                    .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().serializeNulls().create()))
                    //添加回调库，默认是RxJava
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    //设置使用okhttp网络请求
                    .client(mOkHttpClient)
                    .build();
        }
        return mRetrofit;
    }

//    public static Retrofit getRetrofitUsedToken(){
//        if (mRetrofit==null){
//            if (mOkHttpClient==null){
//                mOkHttpClient = OkHttp3Utils.getOkHttpClientUsedToken();
//            }
//            //Retrofit2使用build设计模式
//            mRetrofit = new Retrofit.Builder()
//                    //设置服务器的路径
//                    .baseUrl(API.BASE_URL)
//                    //添加回调库，默认是Gson
//                    .addConverterFactory(GsonConverterFactory.create())
//                    //添加回调库，默认是RxJava
//                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
//                    //设置使用okhttp网络请求
//                    .client(mOkHttpClient)
//                    .build();
//        }
//        return mRetrofit;
//    }


}
