package com.guyu.android.gis.service;

import com.guyu.android.base.BaseEntity;
import com.guyu.android.gis.common.MapVersionList;
import com.guyu.android.gis.common.VersionName;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * Created by admin on 2017/9/28.
 */

public interface UpdatesService {
    @GET
    Observable<BaseEntity<VersionName>> checkForUpdates(
            @Url String url
    );

    @GET
    Observable<BaseEntity<MapVersionList>> checkForUpdatesForMap(
            @Url String url
    );
}
