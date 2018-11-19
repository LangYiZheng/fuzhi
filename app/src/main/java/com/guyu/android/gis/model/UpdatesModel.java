package com.guyu.android.gis.model;

import com.blankj.utilcode.util.ToastUtils;
import com.guyu.android.base.BaseEntity;
import com.guyu.android.base.BaseObserver;
import com.guyu.android.base.CallBack;
import com.guyu.android.gis.app.GisQueryApplication;
import com.guyu.android.gis.common.MapVersionList;
import com.guyu.android.gis.common.VersionName;
import com.guyu.android.gis.config.ProjectConfig;
import com.guyu.android.gis.service.UpdatesService;
import com.guyu.android.network.Networks;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by admin on 2017/9/28.
 */

public class UpdatesModel {
    public static void CheckForUpdates(final CallBack<VersionName> callBack) {
        String url = "";
        try {
            url = GisQueryApplication.getApp().getProjectconfig().getAppVersionCheckUrl();
        } catch (Exception e) {
            e.printStackTrace();

            callBack.onError(e.getMessage());
            return;
        }

        Networks.subscribeObserver(Networks.getRetrofit().create(UpdatesService.class).checkForUpdates(url),
                new BaseObserver<VersionName>() {
                    @Override
                    protected void onHandleSuccess(VersionName versionName) {

                        callBack.onSuccess(versionName);
                    }
                });
    }

    public static void CheckForUpdatesForMaps(final CallBack<BaseEntity<MapVersionList>> callBack) {
        String url = "";
        try {
            while (GisQueryApplication.getApp().getProjectconfig().getMapVersion() ==null){
                Thread.sleep(100);
            }
            url = GisQueryApplication.getApp().getProjectconfig().getMaplisturl() + "?administrative=" + GisQueryApplication.getApp().getProjectconfig().getMapVersion().getMessage();
        } catch (Exception e) {
            e.printStackTrace();

           callBack.onError(e.getMessage());
            return;
        }

        Networks.subscribeObserver(Networks.getRetrofit().create(UpdatesService.class).checkForUpdatesForMap(url), new Observer<BaseEntity<MapVersionList>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(BaseEntity<MapVersionList> mapVersionListBaseEntity) {
                callBack.onSuccess(mapVersionListBaseEntity);
            }

            @Override
            public void onError(Throwable e) {
                callBack.onError(e.getMessage());
            }

            @Override
            public void onComplete() {

            }
        });

    }
}
