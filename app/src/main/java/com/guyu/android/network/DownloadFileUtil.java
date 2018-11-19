package com.guyu.android.network;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.chad.library.adapter.base.BaseViewHolder;
import com.guyu.android.R;
import com.guyu.android.base.BaseView;
import com.guyu.android.base.CallBack;
import com.guyu.android.gis.common.MapVersion;
import com.guyu.android.gis.contract.UpdateContract;
import com.guyu.android.gis.service.DownloadService;
import com.guyu.android.network.Networks;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;

/**
 * Created by admin on 2017/9/28.
 */

public class DownloadFileUtil {
    private static final String TAG = "DownloadFileUtil";
    private static boolean b = false;

    public interface CallBack {
        /**
         * 更新下载进度
         *
         * @param progress 进度
         */
        void updateProgress(int progress);

        /**
         * 设置最大下载进度值
         *
         * @param max 最大进度值
         */
        void setMaxProgress(int max);

        /**
         * 下载成功
         *
         * @param file 成功后的文件
         */
        void downloadSuccess(File file);

        /**
         * 下载失败
         */
        void downloadError();
    }

    /**
     * 小文件下载
     *
     * @param url
     * @param filePath 保存路径，包括后缀名
     * @return 下载是否成功
     */
    public static void downloadFile(final String url, final String filePath) {
        final CompositeDisposable cd = new CompositeDisposable();
        Networks.getRetrofit().create(DownloadService.class).downloadFile(url).map(new Function<ResponseBody, BufferedSource>() {
            @Override
            public BufferedSource apply(ResponseBody responseBody) throws Exception {
                return responseBody.source();
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new Observer<BufferedSource>() {
                    @Override
                    public void onSubscribe(Disposable disposable) {
                        cd.add(disposable);
                    }

                    @Override
                    public void onNext(BufferedSource bufferedSource) {
                        Log.d(TAG, "下载中");
                        try {
                            writeFile(bufferedSource, new File(filePath));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Log.e(TAG, "下载失败" + throwable.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "下载成功");
                        unSubscribe(cd);
                    }
                });
    }

    /**
     * 大文件带进度断点续传
     *
     * @param url       下载地址
     * @param file      文件
     * @param sartPoint 开始节点 0或file.length()
     * @param callBack  回掉更新进度信息 (KB需右移6位 >>6 MB长度需右移16位 >> 16)
     * @return {@link #unSubscribe} 解除订阅 暂停或取消下载
     */
    public static CompositeDisposable downloadFile(final String url, final File file, final String sartPoint, final CallBack callBack) {
        final CompositeDisposable cd = new CompositeDisposable();

        Networks.getDownloadRetrofit().create(DownloadService.class).download("bytes=" + sartPoint + "-", url).subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io()).subscribe(new Observer<ResponseBody>() {
            @Override
            public void onSubscribe(Disposable d) {
                cd.add(d);
            }

            @Override
            public void onNext(ResponseBody body) {
                Log.d(TAG, "下载中");
                BufferedSink sink = null;
                BufferedSource source = null;
                try {
                    long sFileSize = 0;
                    if (sFileSize == 0) {
                        sFileSize = body.contentLength();
                    }
                    long total = 0;
                    try {
                        total = file.length();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    callBack.setMaxProgress((int) ((total + sFileSize) >> 4));
                    sink = Okio.buffer(Okio.appendingSink(file));
                    Buffer buffer = sink.buffer();


                    long len;
                    int bufferSize = 1024;

                    source = body.source();

                    while ((len = source.read(buffer, bufferSize)) != -1) {
                        sink.emit();
                        total += len;
                        callBack.updateProgress((int) (total >> 4));
                    }
                    sink.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (source != null) {
                            source.close();
                        }
                        if (sink != null) {
                            sink.close();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }


            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "下载失败" + e.getMessage());
                callBack.downloadError();
                unSubscribe(cd);
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "下载成功");
                callBack.downloadSuccess(file);
                unSubscribe(cd);
            }
        });


        return cd;
    }

    /**
     * 写入文件
     */
    private static void writeFile(BufferedSource source, File file) throws IOException {
        if (!file.getParentFile().exists())
            file.getParentFile().mkdirs();

        if (file.exists())
            file.delete();

        BufferedSink bufferedSink = Okio.buffer(Okio.sink(file));
        bufferedSink.writeAll(source);

        bufferedSink.close();
        source.close();
    }


    /**
     * 解除订阅
     *
     * @param cd 订阅关系集合
     */
    public static void unSubscribe(CompositeDisposable cd) {
        if (cd != null && !cd.isDisposed()) {
            cd.dispose();
        }
    }

}
