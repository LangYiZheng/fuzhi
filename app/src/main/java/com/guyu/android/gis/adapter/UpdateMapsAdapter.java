package com.guyu.android.gis.adapter;

import android.app.DialogFragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.guyu.android.R;
import com.guyu.android.gis.activity.UpdateMapsDialogFragment;
import com.guyu.android.gis.app.GisQueryApplication;
import com.guyu.android.gis.common.MapVersion;
import com.guyu.android.gis.entity.UpdateMapsEntity;
import com.guyu.android.network.DownloadFileUtil;
import com.guyu.android.utils.FileUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;
import okio.Buffer;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;

/**
 * Created by YunHuan on 2017/11/6.
 */

public class UpdateMapsAdapter extends BaseMultiItemQuickAdapter<UpdateMapsEntity, BaseViewHolder> {

    private List<MapVersion> versions;
    private List<BaseViewHolder> helpers;
    private List<CompositeDisposable> compositeDisposables;
    private List<File> files;
    private Handler handler;
    private int moveSuccess;
    private DialogFragment dialogFragment;


    public UpdateMapsAdapter(List<UpdateMapsEntity> list, List<MapVersion> versions, UpdateMapsDialogFragment dialogFragment) {
        super(list);
        this.versions = versions;
        this.dialogFragment = dialogFragment;
        helpers = new ArrayList();
        compositeDisposables = new ArrayList();
        initHandler();
        files = new ArrayList<>();
        addItemType(UpdateMapsEntity.UPDATE_VIEW, R.layout.item_update_view);
//        addItemType(UpdateMapsEntity.MOVE_VIEW, R.layout.item_move_view);
        addItemType(UpdateMapsEntity.CANCEL_VIEW, R.layout.item_cancel_view);
    }

    private void initHandler() {
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                helpers.get(msg.what).setText(R.id.tv, msg.getData().getString("msg"));
                helpers.get(msg.what).setGone(R.id.btn,false);
                msg = null;
            }
        };
    }


    public void unSubscribe() {
        for (CompositeDisposable cd : compositeDisposables) {
            DownloadFileUtil.unSubscribe(cd);
        }
        for (File file : files) {

            file.delete();

        }
    }


    public void unSubscribe(int i) {

        DownloadFileUtil.unSubscribe(compositeDisposables.get(i));

//        for (File file : files) {
//
//            file.delete();
//
//        }
    }
    public void continueDownload(int i){
        CompositeDisposable cd = continueDown(i);
        List<CompositeDisposable> n = new ArrayList<>();

        for(int j = 0;j<compositeDisposables.size();j++){
            if(j!=i){
                n.add(compositeDisposables.get(j));
            }else{
                n.add(cd);
            }
        }
        compositeDisposables = null;
        compositeDisposables = n;
    }


    private CompositeDisposable continueDown(int i){

        CompositeDisposable cd = DownloadFileUtil.downloadFile(
                GisQueryApplication.getApp().getProjectconfig().getMapdownloadurl() + "?tpkId=" + versions.get(i).getID(),
                files.get(i),
                String.valueOf(files.get(i).length()), new DownloadFileUtil.CallBack(){
                    @Override
                    public void updateProgress(int progress) {
                        helpers.get(i).setProgress(R.id.pb,progress);
                    }

                    @Override
                    public void setMaxProgress(int max) {
                        helpers.get(i).setProgress(R.id.pb,0,max);
                    }

                    @Override
                    public void downloadSuccess(File file) {
                        moveFile(i);
                    }

                    @Override
                    public void downloadError() {

                    }
                }
        );
        return cd;
    }


    @Override
    protected void convert(BaseViewHolder helper, UpdateMapsEntity item) {
        helpers.add(helper);
        int position = helper.getAdapterPosition();
        switch (item.getItemType()) {
            case UpdateMapsEntity.UPDATE_VIEW:
                helper.addOnClickListener(R.id.btn);
                helper.setText(R.id.tv, "正在下载 " + versions.get(position).getNAMECN() + "_" + versions.get(position).getMAPVER());
                String fileType = ".tpk";
                if (versions.get(position).getNAME().equals("offlinedata")) {
                    fileType = ".geodatabase";
                }
                files.add(new File(GisQueryApplication.currentProjectPath + "download/map/" + versions.get(position).getNAME() + fileType));
                CompositeDisposable cd = continueDown(position);
                compositeDisposables.add(cd);

                break;
//            case UpdateMapsEntity.MOVE_VIEW:
//
//                helper.setText(R.id.tv, "正在移动 " + versions.get(position - versions.size()).getNAMECN() + "_" + versions.get(position - versions.size()).getMAPVER());
//
//                break;
            case UpdateMapsEntity.CANCEL_VIEW:
                helper.addOnClickListener(R.id.btn_cancel);
                break;
        }
    }

    private void moveFile(int i) {
        String name = versions.get(i).getNAME();
        File oldFile = files.get(i);
        File newFile = null;
        if (!name.equals("offlinedata")) {
            newFile = new File(GisQueryApplication.currentProjectPath + "basemap/" + name + ".tpk");
//            newFile.delete();

        } else {
            newFile = new File(GisQueryApplication.currentProjectPath + "OfflineData/" + name + ".geodatabase");
//            FileUtils.deleteAllInDir(GisQueryApplication.currentProjectPath + "OfflineData/");
        }

        moveFile(oldFile, newFile, i);
    }

    private void moveFile(final File oldFile, final File newFile, int i) {

        Message msg = new Message();
        msg.what = helpers.get(i).getAdapterPosition();
        Bundle b = new Bundle();
        b.putString("msg", "正在移动 " + versions.get(i).getNAMECN() + "_" + versions.get(i).getMAPVER());
        msg.setData(b);
        handler.sendMessage(msg);
        new Thread(new Runnable() {
            @Override
            public void run() {

                BufferedSink sink = null;
                BufferedSource source = null;
                try {
                    long sFileSize = 0;
                    if (sFileSize == 0) {
                        sFileSize = oldFile.length();
                    }
                    long fileSizeDownloaded = 0;
                    helpers.get(i).setProgress(R.id.pb, 0, (int) (sFileSize >> 4));

                    sink = Okio.buffer(Okio.sink(newFile));
                    Buffer buffer = sink.buffer();

                    long total = 0;
                    long len;
                    int bufferSize = 102400;

                    source = Okio.buffer(Okio.source(oldFile));

                    while ((len = source.read(buffer, bufferSize)) != -1) {
                        sink.emit();
                        total += len;
                        helpers.get(i).setProgress(R.id.pb, (int) (total >> 4));
                    }
                    sink.flush();

                    moveSuccess++;
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

                if (moveSuccess == versions.size()) {
                    FileUtil.moveFile(GisQueryApplication.currentProjectPath + "download/map/mapVersion.txt", GisQueryApplication.currentProjectPath + "mapVersion.txt");
                    GisQueryApplication.getApp().loadMapVersion();
                    GisQueryApplication.getApp().loadLayers();
                    FileUtils.deleteAllInDir(GisQueryApplication.currentProjectPath + "download/map/");
                    ToastUtils.showLong("离线地图更新成功");
                    dialogFragment.dismiss();
                }
            }
        }).start();
    }

}
