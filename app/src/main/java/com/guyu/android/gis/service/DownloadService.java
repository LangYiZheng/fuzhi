package com.guyu.android.gis.service;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * Created by admin on 2017/9/30.
 */

public interface DownloadService {
    @Streaming
    @GET
    Observable<ResponseBody> downloadFile(@Url String   fileUrl);
    @Streaming
    @GET
    Observable<ResponseBody> download(@Header("Range") String start, @Url String url);
}
