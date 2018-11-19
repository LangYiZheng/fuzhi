package com.guyu.android.gis.presenter;

import com.blankj.utilcode.util.AppUtils;
import com.guyu.android.base.BaseEntity;
import com.guyu.android.base.CallBack;
import com.guyu.android.gis.app.GisQueryApplication;
import com.guyu.android.gis.common.MapVersion;
import com.guyu.android.gis.common.MapVersionList;
import com.guyu.android.gis.common.VersionName;
import com.guyu.android.gis.contract.UpdateContract;
import com.guyu.android.gis.model.UpdatesModel;
import com.guyu.android.network.DownloadFileUtil;
import com.guyu.android.utils.GsonUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by admin on 2017/9/28.
 */

public class UpdatesPresenter implements UpdateContract.Presenter {
    private UpdateContract.View view;
    private long breakPoints;
    private File file;
    private long totalBytes;
    private long contentLength;
    private VersionName versionName;
    private VersionName localVersionName;
    private CompositeDisposable cd;
    private DownloadFileUtil.CallBack downloadCallback;
    public UpdatesPresenter(UpdateContract.View view,DownloadFileUtil.CallBack callBack) {
        this.view = view;
        this.downloadCallback = callBack;
        view.setPresenter(this);
        File versionFile = new File(GisQueryApplication.currentProjectPath + "appVersion.txt");

        try (FileReader reader = new FileReader(versionFile)){

            localVersionName = GsonUtil.getGson().fromJson(reader, VersionName.class);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    @Override
    public void start() {
        checkForUpdatesOfMaps();
        checkForUpdates();
    }

    @Override
    public void checkForUpdatesOfMaps() {
        UpdatesModel.CheckForUpdatesForMaps(new CallBack<BaseEntity<MapVersionList>>() {
            @Override
            public void onSuccess(BaseEntity<MapVersionList> map) {
                List<MapVersion> newList = new ArrayList();
                List<MapVersion> l1 = map.getData().getList();
                if(GisQueryApplication.getApp().getProjectconfig().getMapVersion()==null){
                    return;
                }
                List<MapVersion> l2 = GisQueryApplication.getApp().getProjectconfig().getMapVersion().getData().getList();
                for (int i = 0; i < l1.size(); i++) {
                    if (Float.valueOf(l1.get(i).getMAPVER()).floatValue() > Float.valueOf(l2.get(i).getMAPVER()).floatValue()) {
                        if(!l1.get(i).getNAME().equals("LJK_YX")){
                            newList.add(l1.get(i));
                        }else {
                            if (!GisQueryApplication.getApp().isSpecialVirsion()){
                                newList.add(l1.get(i));
                            }
                        }

                    }
                }
                if (newList.size() > 0) {
                    String path = GisQueryApplication.currentProjectPath + "download/map/mapVersion.txt";
                    String str = GsonUtil.getGson().toJson(map);
                    writeStringToFile(path, str);
                    view.showUpdateDialogForMaps(newList);
                }
            }

            @Override
            public void onFail() {

            }

            @Override
            public void onError(String msg) {

            }
        });
    }

    @Override
    public void checkForUpdates() {
        UpdatesModel.CheckForUpdates(new CallBack<VersionName>() {
            @Override
            public void onSuccess(VersionName versionName) {
                UpdatesPresenter.this.versionName = versionName;
                String[] temp, temp1;
                String[] appVersion = AppUtils.getAppVersionName().split("\\.");
                String[] appVersion2 = versionName.getApk().split("\\.");

                temp = localVersionName.getXml().split("\\.");
                temp1 = temp[1].split("_");
                String[] xmlVersion = {temp[0], temp1[0], temp1[1]};

                temp = versionName.getXml().split("\\.");
                temp1 = temp[1].split("_");
                String[] xmlVersion2 = {temp[0], temp1[0], temp1[1]};

                temp = localVersionName.getDb().split("\\.");
                temp1 = temp[1].split("_");
                String[] dbVersion = {temp[0], temp1[0], temp1[1]};

                temp = versionName.getDb().split("\\.");
                temp1 = temp[1].split("_");
                String[] dbVersion2 = {temp[0], temp1[0], temp1[1]};

                boolean b1 = false, b2 = false, b3 = false;



                for (int i = 0; i < appVersion.length; i++) {
                    int v1 = Integer.valueOf(xmlVersion[i]);
                    int v2 = Integer.valueOf(xmlVersion2[i]);
                    if (v1 < v2) {
                        b2 = true;
                        downloadFile(2);
                        break;
                    }
                }

                for (int i = 0; i < appVersion.length; i++) {
                    int v1 = Integer.valueOf(dbVersion[i]);
                    int v2 = Integer.valueOf(dbVersion2[i]);
                    if (v1 < v2) {
                        b3 = true;
                        downloadFile(3);
                        break;
                    }
                }

                for (int i = 0; i < appVersion.length; i++) {
                    int v1 = Integer.valueOf(appVersion[i]);
                    int v2 = Integer.valueOf(appVersion2[i]);
                    if (v1 < v2) {
                        b1 = true;
                        view.showUpdateDialog(versionName.getApk());
                        break;
                    }
                }

                if (b1 || b2 || b3) {
                    String path = GisQueryApplication.currentProjectPath + "download/" + versionName.getApk() + "/appVersion.txt";
                    String str = GsonUtil.getGson().toJson(versionName);
                    writeStringToFile(path, str);
                }
            }

            @Override
            public void onFail() {

            }

            @Override
            public void onError(String msg) {

            }
        });
    }

    public static void writeStringToFile(String filePath, String str) {
        PrintStream ps = null;
        try {
            File file = new File(filePath);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            if (file.exists()) {
                file.delete();
            }
            ps = new PrintStream(new FileOutputStream(file));
            ps.println(str);// 往文件里写入字符串
            ps.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void downloadStart() {

        file = new File(GisQueryApplication.currentProjectPath + "download/" + versionName.getApk() + "/apk/", "GuguGis.apk");
        try {
            breakPoints = file.length();
        } catch (Exception e) {

        }

        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        if (file.exists())
            file.delete();
        cd = DownloadFileUtil.downloadFile(GisQueryApplication.getApp().getProjectconfig().getAppDownloadUrl() + "?type=apk", file, String.valueOf(breakPoints), downloadCallback);
    }

    @Override
    public void downloadPause() {
        DownloadFileUtil.unSubscribe(cd);
    }

    @Override
    public void downloadContinue() {
        try {
            breakPoints = file.length();
        } catch (Exception e) {

        }
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        cd = DownloadFileUtil.downloadFile(GisQueryApplication.getApp().getProjectconfig().getAppDownloadUrl() + "?type=apk", file, String.valueOf(breakPoints), downloadCallback);
    }

    @Override
    public void downloadStop() {
        DownloadFileUtil.unSubscribe(cd);
        file.delete();
    }

    @Override
    public void downloadFile(int type) {
        String filePath = GisQueryApplication.currentProjectPath + "download";
        String url = GisQueryApplication.getApp().getProjectconfig().getAppDownloadUrl() + "?type=";
        CallBack<String> callBack = null;
        String fileName = "";
        String[] temp;
        switch (type) {
            case 1:
                downloadStart();
                return;
            case 2:
                url += "xml";
                temp = versionName.getXml().split("_");
                filePath += "/" + temp[0] + "/xml/" + temp[1] + "/";
                fileName = "getProjectconfig().xml";
                break;
            case 3:
                url += "db";
                temp = versionName.getDb().split("_");
                filePath += "/" + temp[0] + "/db/" + temp[1] + "/";
                fileName = "iGisQuery.db";
                break;
        }
        DownloadFileUtil.downloadFile(url, filePath + fileName);
    }
}
