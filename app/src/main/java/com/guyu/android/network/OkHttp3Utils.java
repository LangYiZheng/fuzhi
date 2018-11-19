package com.guyu.android.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.blankj.utilcode.util.AppUtils;
import com.guyu.android.gis.app.GisQueryApplication;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * 类描述：封装一个OkHttp3的获取类
 */
public class OkHttp3Utils {

    private static String TAG = "OkHttp";

    private static OkHttpClient mOkHttpClient;
//    private static OkHttpClient mUsedTokenOkHttpClient;

    //设置缓存目录
    private static File cacheDirectory = new File(GisQueryApplication.getApp().getCacheDir().getAbsolutePath(), "MyCache");
    private static Cache cache = new Cache(cacheDirectory, 30 * 1024 * 1024);


    /**
     * 获取OkHttpClient对象
     *
     * @return
     */
    public static OkHttpClient getOkHttpClient() {
        if (null == mOkHttpClient) {

            //同样okhttp3后也使用build设计模式
            mOkHttpClient = new OkHttpClient.Builder()
                    //设置一个自动管理cookies的管理器
//                     .cookieJar(new CookiesManager())
                    //添加拦截器
                    .addInterceptor(interceptor)
//                    .addInterceptor(getParamsInterceptor())
                    //添加网络连接器
                    .addNetworkInterceptor(interceptor)
                    .addInterceptor(new LogInterceptor())
                    //添加图片上传的拦截器
//                    .addInterceptor(UploadInterceptor) 会影响无网缓存
                    //日志拦截器
//                    .addInterceptor(loggingInterceptor)
                    //设置请求读写的超时时间
                    .connectTimeout(15, TimeUnit.SECONDS)
                    .writeTimeout(15, TimeUnit.SECONDS)
                    .readTimeout(15, TimeUnit.SECONDS)
                    .cache(cache)
                    .build();


        }

        return mOkHttpClient;
    }

    public static OkHttpClient getDownloadOkHttpClient() {
        if (null == mOkHttpClient) {

            //同样okhttp3后也使用build设计模式
            mOkHttpClient = new OkHttpClient.Builder()
                    //设置一个自动管理cookies的管理器
//                     .cookieJar(new CookiesManager())
                    //添加拦截器
//                    .addInterceptor(interceptor)
//                    .addInterceptor(getParamsInterceptor())
                    //添加网络连接器
//                    .addNetworkInterceptor(interceptor)
                    .addInterceptor(new LogInterceptor())
                    //添加图片上传的拦截器
//                    .addInterceptor(UploadInterceptor) 会影响无网缓存
                    //日志拦截器
//                    .addInterceptor(loggingInterceptor)
                    //设置请求读写的超时时间
//                    .connectTimeout(15, TimeUnit.SECONDS)
//                    .writeTimeout(15, TimeUnit.SECONDS)
//                    .readTimeout(15, TimeUnit.SECONDS)
//                    .cache(cache)
                    .build();


        }

        return mOkHttpClient;
    }

//    public static OkHttpClient getOkHttpClientUsedToken() {
//
//        if (null == mUsedTokenOkHttpClient) {
//
//            //同样okhttp3后也使用build设计模式
//            mUsedTokenOkHttpClient = new OkHttpClient.Builder()
//                    //设置一个自动管理cookies的管理器
////                     .cookieJar(new CookiesManager())
//                    //添加拦截器
//                    .addInterceptor(interceptor)
//                    .addInterceptor(getParamsInterceptor())
//                    //添加网络连接器
//                    .addNetworkInterceptor(interceptor)
//                    .addInterceptor(getHeaderInterceptor())
//                    .addInterceptor(new LogInterceptor())
//                    //添加图片上传的拦截器
////                    .addInterceptor(UploadInterceptor) 会影响无网缓存
//                    //日志拦截器
////                        .addInterceptor(loggingInterceptor)
//                    //设置请求读写的超时时间
//                    .connectTimeout(12, TimeUnit.SECONDS)
//                    .writeTimeout(20, TimeUnit.SECONDS)
//                    .readTimeout(20, TimeUnit.SECONDS)
//                    .cache(cache)
//                    .build();
//
//
//        }
//
//        return mUsedTokenOkHttpClient;
//    }

    public static class LogInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            //这个chain里面包含了request和response，所以你要什么都可以从这里拿
            Request request = chain.request();

            long t1 = System.nanoTime();
            Response response = chain.proceed(request);
            long t2 = System.nanoTime();

            double time = (t2 - t1) / 1e6d;

            if (request.method().equals("GET")) {

                Log.v(TAG, String.format("GET %s%n%.1fms%n%s%n%s%n%s%n%s%n", request.url(), time, request.headers(), response.code(), response.headers(), response.body().charStream()));
            } else if (request.method().equals("POST")) {
                StringBuilder sb = new StringBuilder();
                if (request.body() instanceof FormBody) {
                    FormBody body = (FormBody) request.body();
                    for (int i = 0; i < body.size(); i++) {
                        sb.append(body.encodedName(i) + "=" + body.encodedValue(i) + "&");
                    }
                    sb.delete(sb.length() - 1, sb.length());
                }
                Log.v(TAG, String.format("POST %s%n%.1fms%n%s%n%s%n%s%n%s%n%s%n" , request.url(), time, request.headers(), sb.toString(), response.code(), response.headers(), response.body().charStream()));

            } else if (request.method().equals("PUT")) {
                Log.v(TAG, String.format("PUT %s%n%.1fms%n%s%n%s%n%s%n%s%n%s%n", request.url(), time, request.headers(), request.body().toString(), response.code(), response.headers(), response.body().charStream()));
            } else if (request.method().equals("DELETE")) {
                Log.v(TAG, String.format("DELETE %s%n%.1fms%n%s%n%s%n%s%n", request.url(), time, request.headers(), response.code(), response.headers()));
            }
            //这里不能直接使用response.body().string()的方式输出日志
            //因为response.body().string()之后，response中的流会被关闭，程序会报错，我们需要创建出一
            //个新的response给应用层处理
            ResponseBody responseBody = response.peekBody(1024 * 1024);
            Log.v(TAG, String.format("接收响应: [%s] %n返回json:【%s】 %.1fms%n%s",
                    response.request().url(),
                    responseBody.string(),
                    time,
                    response.headers()));

            return response;
        }
    }


    private static Interceptor getParamsInterceptor() {
        HashMap<String, String> map = new HashMap<String, String>();
        // 公共参数 当前版本类型 2 代表安卓
        // 当前app版本名称
        map.put("client", "2");
        map.put("version", AppUtils.getAppVersionName());
        return new BasicParamsInterceptor.Builder().addParamsMap(map).build();
    }



    /**
     * 判断网络是否可用
     *
     * @param context Context对象
     */
    public static boolean isNetworkReachable(Context context) {
        try {
            ConnectivityManager connectivity = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity != null) {
                // 获取网络连接管理的对像
                NetworkInfo info = connectivity.getActiveNetworkInfo();
                if (info == null || !info.isAvailable()) {
                    return false;
                } else {
                    return true;
                }
            }
        } catch (Exception e) {
            Log.d(TAG, "----isNetworkReachable---" + e.getLocalizedMessage());
            e.printStackTrace();
        }
        return false;
    }


    static Interceptor interceptor = new Interceptor() {

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            if (!isNetworkReachable(GisQueryApplication.getApp())) {//没网强制从缓存读取(必须得写，不然断网状态下，退出应用，或者等待一分钟后，就获取不到缓存）
                request = request.newBuilder()
                        .cacheControl(CacheControl.FORCE_CACHE)
                        .build();
            }
            Response response = chain.proceed(request);
            if (isNetworkReachable(GisQueryApplication.getApp())) {

                int maxAge = 0 * 60; // 在线缓存在6秒内可读取
                // 有网络时 设置缓存超时时间0个小时
                return response.newBuilder()
                        .header("Cache-Control", "public, max-age=" + maxAge)
                        .removeHeader("Pragma")
                        .removeHeader("Cache-Control")
//                        .header("Cache-Control",cacheControl)
                        .build();
            } else {
                // 无网络时，设置超时为4周
                int maxStale = 60 * 60 * 24 * 28; // tolerate 4-weeks stale
                return response.newBuilder()
                        .removeHeader("Pragma")
                        .removeHeader("Cache-Control")
                        .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)/*2419200*/
                        .build();
            }
        }
    };


    static Interceptor UploadInterceptor = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            int maxAge = 0 * 60; // tolerate 4-weeks stale
            Request request = chain.request().newBuilder()
                    .removeHeader("Pragma")
                    .addHeader("Content-Type", "application/json;charset=UTF-8")
                    .addHeader("Connection", "keep-alive")
                    .addHeader("Accept", "*/*")
                    .header("Cache-Control", String.format("public,max-age=%d", maxAge))
                    .build();
            return chain.proceed(request);
        }
    };

    static HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
        @Override
        public void log(String message) {
            Log.d("loggingInterceptor", "OkHttp====message " + message);
        }
    });



}
