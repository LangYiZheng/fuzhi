package com.guyu.android.gis.service;

import com.guyu.android.base.BaseEntity;
import com.guyu.android.gis.common.Tracklist;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface TracklistService {
    @GET
    Observable<BaseEntity<Tracklist>> tracklist(
            @Url String url
    );
}
