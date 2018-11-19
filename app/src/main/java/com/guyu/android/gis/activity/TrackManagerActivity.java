package com.guyu.android.gis.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


import com.blankj.utilcode.util.ToastUtils;
import com.guyu.android.base.BaseEntity;
import com.guyu.android.base.BaseObserver;
import com.guyu.android.database.sync.DJQDBMExternal;
import com.guyu.android.database.sync.DJZQDBMExternal;
import com.guyu.android.database.task.CaseImgDBMExternal;
import com.guyu.android.database.task.CaseSoundDBMExternal;
import com.guyu.android.database.task.CaseVideoDBMExternal;
import com.guyu.android.database.task.TrackDBMExternal;
import com.guyu.android.gis.adapter.ExportCheckAdapter;
import com.guyu.android.gis.adapter.TrackManagerAdapter;
import com.guyu.android.gis.app.GisQueryApplication;
import com.guyu.android.gis.common.CaseImg;
import com.guyu.android.gis.common.CaseSound;
import com.guyu.android.gis.common.CaseVideo;
import com.guyu.android.gis.common.DJQ;
import com.guyu.android.gis.common.DJZQ;
import com.guyu.android.gis.common.TrackObj;
import com.guyu.android.gis.common.Tracklist;
import com.guyu.android.gis.entity.TrackManagerEntity;
import com.guyu.android.gis.opt.MapOperate;
import com.guyu.android.gis.service.TracklistService;
import com.guyu.android.network.DownloadFileUtil;
import com.guyu.android.network.Networks;
import com.guyu.android.utils.DebugUtils;
import com.guyu.android.utils.SysConfig;
import com.guyu.android.utils.UtilsTools;
import com.guyu.android.R;

import q.rorbin.badgeview.QBadgeView;

public class TrackManagerActivity extends Activity {
    private static final String TAG = "TrackManagerActivity";
    private static final String TAG_USER_ACTION = "用户操作";
    private List<TrackObj> mAllSelectTrackObjs = new ArrayList<TrackObj>();
    private List<TrackObj> mAllTrackObjs = new ArrayList<TrackObj>();
    private boolean[] mCheckList = null;
    List<Boolean> mIsUpLst = new ArrayList<Boolean>();
    List<String> mNameList = new ArrayList<String>();
    private View.OnClickListener mOnClickListener = null;
    private Button mSelectButton = null;
    private Button mMapViewButton = null;
    private TextView btnUpdateData = null;
    private QBadgeView qBadgeView;


    private Handler handler;

    private Tracklist tracklist;

    private TrackDBMExternal mTrackManager = null;
    private ListView mlistViewLyrs = null;

    private CheckBox mMultiOptCheckBox = null;
    private AdapterView.OnItemClickListener onItemClickListener1 = null;
    private AdapterView.OnItemClickListener onItemClickListener2 = null;
    private  ExportCheckAdapter exportCheckAdapter;

    private RecyclerView mRecyclerView;
    private List<DJQ> djqs;
    private List<DJZQ> djzqs;
    private Map<String,Map<String,List<TrackObj>>> djqinfo;
    private TrackManagerAdapter adapter;

    public void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        Log.i("TrackManagerActivity", "ExportTrackActivity -- onCreate");
        setContentView(R.layout.activity_track_manager);
        mRecyclerView = (RecyclerView)findViewById(R.id.list);
        btnUpdateData = (TextView) findViewById(R.id.btnUpdateData);


        initHandler();
        this.mTrackManager = new TrackDBMExternal(this);
        this.mOnClickListener = new MOnClickListener();
        findViewById(R.id.btn_cancel).setOnClickListener(this.mOnClickListener);

        new GetAllTrackInfoTask().execute(new String[]{""});
        this.mlistViewLyrs = ((ListView) findViewById(R.id.lst_all_datas));
        mRecyclerView.setVisibility(View.VISIBLE);
        mlistViewLyrs.setVisibility(View.GONE);
//        getDJQINFO();
    }

    public void setmAllTrackObjs(List<TrackObj> mAllTrackObjs) {
        this.mAllTrackObjs = mAllTrackObjs;
        mNameList = new ArrayList<>();
        mCheckList = null;
        mIsUpLst = new ArrayList<>();
        getExportCheckAdapterData();
        mRecyclerView.setVisibility(View.GONE);
        mlistViewLyrs.setVisibility(View.VISIBLE);
        setExportCheckAdapter();
    }

    private void getDJQINFO(){
        DJQDBMExternal djqdb = new DJQDBMExternal(this);
        djqs = djqdb.getAll();
        DJZQDBMExternal djzqdb = new DJZQDBMExternal(this);
        djzqs = djzqdb.getAll();
        djqinfo = new HashMap<String,Map<String,List<TrackObj>>>();
        for (int i = 0; i < mAllTrackObjs.size(); i++) {
            TrackObj trackObj = mAllTrackObjs.get(i);
            if(trackObj.getDJQ()==-1){
                if(djqinfo.get("未分类")==null){
                    Map<String,List<TrackObj>> listMap = new HashMap<>();
                    List<TrackObj> list = new ArrayList<>();
                    list.add(trackObj);
                    listMap.put("未分类",list);
                    djqinfo.put("未分类",listMap);
                }else{
                    Map<String,List<TrackObj>> listMap = djqinfo.get("未分类");
                    List<TrackObj> list=listMap.get("未分类");
                    list.add(trackObj);
                    listMap.put("未分类",list);
                    djqinfo.put("未分类",listMap);
                }
            }else {
                String str_djq = djqs.get(trackObj.getDJQ()-1).getNAME();
                String str_djzq = djzqs.get(trackObj.getDJZQ()-1).getNAME();
                if(djqinfo.get(str_djq)==null){
                    Map<String,List<TrackObj>> listMap = new HashMap<>();
                    List<TrackObj> list = new ArrayList<>();
                    list.add(trackObj);
                    listMap.put(str_djzq,list);
                    djqinfo.put(str_djq,listMap);
                }else{
                    Map<String,List<TrackObj>> listMap = djqinfo.get(str_djq);
                    List<TrackObj> list=listMap.get(str_djzq);
                    if(list ==null){
                        list = new ArrayList<>();
                    }
                    list.add(trackObj);
                    listMap.put(str_djzq,list);
                    djqinfo.put(str_djq,listMap);
                }

            }

        }
        LinearLayoutManager lr=new LinearLayoutManager(this);
        lr.setOrientation(OrientationHelper.VERTICAL);
        mRecyclerView.setLayoutManager(lr);

        List<TrackManagerEntity> l = getDataList();
        adapter = new TrackManagerAdapter(l,djqinfo,this);
        adapter.openLoadAnimation();
        mRecyclerView.setAdapter(adapter);

    }

    private List<TrackManagerEntity> getDataList() {
        List<TrackManagerEntity> list = new ArrayList<>();
        Set<String> key = djqinfo.keySet();
        for (int i = 0; i <key.size() ; i++) {
            list.add(new TrackManagerEntity(TrackManagerEntity.INFO_VIEW));
        }
        return  list;
    }

    @Override
    protected void onStart() {
        super.onStart();
        int userId = SysConfig.mHumanInfo.getHumanId();
        String cur_userName = SysConfig.mHumanInfo.getHumanName();
        String userName = cur_userName;

        CaseImgDBMExternal mLandImgManager = new CaseImgDBMExternal(this);
        CaseSoundDBMExternal mLandSoundManager = new CaseSoundDBMExternal(this);
        CaseVideoDBMExternal mLandVideoManager = new CaseVideoDBMExternal(this);

        getTrackList(userId,userName);

    }

    private void initActivity() {
        initData();


        initWidget();
    }
    private void downloadFile(final String url ,final String path){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    DownloadFileUtil.downloadFile(url,path);
                }
            }).start();
    }

    private void initData() {

        this.mIsUpLst.clear();
        int userId = SysConfig.mHumanInfo.getHumanId();
        String cur_userName = SysConfig.mHumanInfo.getHumanName();
        String userName = cur_userName;

        CaseImgDBMExternal mLandImgManager = new CaseImgDBMExternal(this);
        CaseSoundDBMExternal mLandSoundManager = new CaseSoundDBMExternal(this);
        CaseVideoDBMExternal mLandVideoManager = new CaseVideoDBMExternal(this);

        btnUpdateData = (TextView) findViewById(R.id.btnUpdateData);


        if (userId >= 200) {


        } else {
            btnUpdateData.setVisibility(View.GONE);
        }

        btnUpdateData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tracklist != null) {
                    List<CaseImg> al_landImgObj = tracklist.getAl_landImgObj();
                    if (al_landImgObj != null) {

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                mLandImgManager.insertMultiCaseImgsInfo(al_landImgObj);
                            }
                        }).start();
                        for (int i = 0; i < al_landImgObj.size(); i++) {
                            String url = GisQueryApplication.getApp().getProjectconfig().getDownloadtrackUrl() + "?type=tbCaseImg" + "&partId=" + userId + "&partName=" + userName + "&caseid=" + al_landImgObj.get(i).getCaseId() + "&fileName=" + al_landImgObj.get(i).getImgName();

                            downloadFile(url,al_landImgObj.get(i).getImgPath());
                        }
                    }

                    List<CaseSound> al_landSoundObj = tracklist.getAl_landSoundObj();
                    if (al_landSoundObj != null) {

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                mLandSoundManager.insertMultiCaseSoundsInfo(al_landSoundObj);
                            }
                        }).start();
                        for (int i = 0; i < al_landSoundObj.size(); i++) {
                            String url = GisQueryApplication.getApp().getProjectconfig().getDownloadtrackUrl() + "?type=tbCaseSound" + "&partId=" + userId + "&partName=" + userName + "&caseid=" + al_landSoundObj.get(i).getCaseId() + "&fileName=" + al_landSoundObj.get(i).getSoundName();

                            downloadFile(url, al_landSoundObj.get(i).getSoundPath());
                        }
                    }
                    List<CaseVideo> al_landVideoObj = tracklist.getAl_landVideoObj();
                    if (al_landVideoObj != null) {

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                mLandVideoManager.insertMultiCaseVideosInfo(al_landVideoObj);
                            }
                        }).start();
                        for (int i = 0; i < al_landVideoObj.size(); i++) {
                            String url = GisQueryApplication.getApp().getProjectconfig().getDownloadtrackUrl() + "?type=tbCaseVideo" + "&partId=" + userId + "&partName=" + userName + "&caseid=" + al_landVideoObj.get(i).getCaseId() + "&fileName=" + al_landVideoObj.get(i).getVideoName();

                            downloadFile(url,al_landVideoObj.get(i).getVideoPath());

                        }
                    }
                    List<TrackObj> trackList = tracklist.getTrackList();

                    if (trackList != null&&trackList.size()>0) {
                        for (int i = 0; i < trackList.size(); i++) {
                            TrackObj mTrackObj = trackList.get(i);
                            String url = GisQueryApplication.getApp().getProjectconfig().getDownloadtrackUrl() + "?type=track" + "&partId=" + userId + "&partName=" + userName + "&caseid=" + mTrackObj.getTrackId() ;
                            downloadFile(url, mTrackObj.getTrackPath());

                            try {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        new TrackDBMExternal(TrackManagerActivity.this).insertOneTrack(mTrackObj);
                                    }
                                }).start();

                            } catch (Exception localException) {
                                DebugUtils.d("Tracklist", "轨迹表缺少字段，请更新至最新的数据库");
                            }
                        }

                        tracklist =  null;
                        handler.sendEmptyMessage(1);
                    }
                    new GetAllTrackInfoTask().execute(new String[]{""});
                }



            }
        });


        getExportCheckAdapterData();

    }

    private void getExportCheckAdapterData(){
        if ((this.mAllTrackObjs != null) && (this.mAllTrackObjs.size() > 0)) {
            this.mCheckList = new boolean[this.mAllTrackObjs.size()];
            int i = 0;
            Iterator localIterator = this.mAllTrackObjs.iterator();
            while (localIterator.hasNext()) {
                TrackObj localTrackObj = (TrackObj) localIterator.next();
                //	this.mNameList.add(localTrackObj.getTrackName());
                if (!("".equals(localTrackObj.getXCDD()))) {
                    this.mNameList.add(localTrackObj.getXCDD() + "_" + localTrackObj.getTrackName());
                } else {
                    this.mNameList.add(localTrackObj.getTrackName());
                }
                if (localTrackObj.isUp()) {
                    this.mIsUpLst.add(true);
                    //this.mCheckList[i] = true;
                } else {
                    this.mIsUpLst.add(false);
                }
                this.mCheckList[i] = false;
                ++i;
            }
        }
    }

    private void setExportCheckAdapter(){
        exportCheckAdapter= new ExportCheckAdapter(
                TrackManagerActivity.this,
                TrackManagerActivity.this.mNameList,
                TrackManagerActivity.this.mCheckList,
                TrackManagerActivity.this.mIsUpLst);
        this.mlistViewLyrs.setAdapter(exportCheckAdapter);
        this.mlistViewLyrs.invalidateViews();
    }

    private void initWidget() {
        this.mlistViewLyrs = ((ListView) findViewById(R.id.lst_all_datas));

        if (this.mNameList != null) {

            this.mlistViewLyrs
                    .setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        public void onItemClick(
                                AdapterView<?> paramAdapterView,
                                View paramView, int paramInt, long paramLong) {

                            Intent localIntent = new Intent(
                                    TrackManagerActivity.this, TrackDetailsActivity.class);
                            localIntent.putExtra(TrackDetailsActivity.DATA_TRACK_OBJ,
                                    (Serializable) TrackManagerActivity.this.mAllTrackObjs
                                            .get(paramInt));
                            TrackManagerActivity.this.startActivityForResult(localIntent, 403235);

                            boolean[] arrayOfBoolean = TrackManagerActivity.this.mCheckList;
                            arrayOfBoolean[paramInt] = !TrackManagerActivity.this.mCheckList[paramInt];
                            TrackManagerActivity.this.finish();
                        }
                    });
            setExportCheckAdapter();
        }
        findViewById(R.id.btn_delete).setOnClickListener(this.mOnClickListener);

        this.mSelectButton = ((Button) findViewById(R.id.btn_select_opt));
        this.mSelectButton.setOnClickListener(this.mOnClickListener);
        this.mMapViewButton = ((Button) findViewById(R.id.btn_mapview_opt));
        this.mMapViewButton.setOnClickListener(this.mOnClickListener);

        this.onItemClickListener1 = new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> paramAdapterView,
                                    View paramView, int paramInt, long paramLong) {
                Intent localIntent = new Intent(
                        TrackManagerActivity.this, TrackDetailsActivity.class);
                localIntent.putExtra(TrackDetailsActivity.DATA_TRACK_OBJ,
                        (Serializable) TrackManagerActivity.this.mAllTrackObjs
                                .get(paramInt));
                TrackManagerActivity.this.startActivityForResult(localIntent, 403235);
                TrackManagerActivity.this.finish();
            }
        };
        this.onItemClickListener2 = new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> paramAdapterView,
                                    View paramView, int paramInt, long paramLong) {

                boolean[] arrayOfBoolean = TrackManagerActivity.this.mCheckList;
                arrayOfBoolean[paramInt] = !TrackManagerActivity.this.mCheckList[paramInt];
                TrackManagerActivity.this.mlistViewLyrs.invalidateViews();
            }
        };

        this.mMultiOptCheckBox = ((CheckBox) findViewById(R.id.cb_more_track));
        this.mMultiOptCheckBox
                .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    public void onCheckedChanged(
                            CompoundButton paramCompoundButton,
                            boolean paramBoolean) {
                        if (paramBoolean) {
                            TrackManagerActivity.this.mlistViewLyrs.setOnItemClickListener(TrackManagerActivity.this.onItemClickListener2);
                            TrackManagerActivity.this.findViewById(R.id.btn_delete).setVisibility(View.VISIBLE);
                            TrackManagerActivity.this.mMapViewButton.setVisibility(View.VISIBLE);
                            TrackManagerActivity.this.mSelectButton.setVisibility(View.VISIBLE);
                        } else {
                            int ckLength = TrackManagerActivity.this.mCheckList.length;
                            for (int j = 0; j < ckLength; j++) {
                                TrackManagerActivity.this.mCheckList[j] = false;
                            }
                            TrackManagerActivity.this.mlistViewLyrs.invalidateViews();
                            TrackManagerActivity.this.mlistViewLyrs.setOnItemClickListener(TrackManagerActivity.this.onItemClickListener1);
                            TrackManagerActivity.this.findViewById(R.id.btn_delete).setVisibility(View.INVISIBLE);
                            TrackManagerActivity.this.mMapViewButton.setVisibility(View.INVISIBLE);
                            TrackManagerActivity.this.mSelectButton.setVisibility(View.INVISIBLE);
                        }
                    }
                });
        setSelectButton();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 403235) {
            List<TrackObj> allTrackObjs = TrackManagerActivity.this.mTrackManager
                    .getAllTrackInfos(true);
            TrackManagerActivity.this.mAllTrackObjs.addAll(allTrackObjs);
            setSelectButton();
            TrackManagerActivity.this.mlistViewLyrs.invalidateViews();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void deleteOpt() {
        if (TrackManagerActivity.this.mAllSelectTrackObjs.size() > 0) {
            new AlertDialog.Builder(this)
                    .setTitle("确认操作")
                    .setMessage("确认删除?")
                    .setPositiveButton("确定",
                            new DialogInterface.OnClickListener() {
                                public void onClick(
                                        DialogInterface paramDialogInterface,
                                        int paramInt) {
                                    TrackManagerActivity.this.mIsUpLst.clear();

                                    Iterator<TrackObj> localIterator1 = TrackManagerActivity.this.mAllSelectTrackObjs
                                            .iterator();
                                    while (localIterator1.hasNext()) {
                                        TrackObj localTrackObj1 = localIterator1
                                                .next();
                                        TrackManagerActivity.this.mTrackManager
                                                .deleteTrackByName(localTrackObj1
                                                        .getTrackName());
                                        TrackManagerActivity.this.mAllTrackObjs
                                                .remove(localTrackObj1);
                                    }
                                    TrackManagerActivity.this.mCheckList = new boolean[TrackManagerActivity.this.mAllTrackObjs
                                            .size()];
                                    TrackManagerActivity.this.mNameList.clear();
                                    Iterator<TrackObj> localIterator2 = TrackManagerActivity.this.mAllTrackObjs
                                            .iterator();

                                    int i = 0;
                                    while (localIterator2.hasNext()) {
                                        TrackObj localTrackObj2 = localIterator2
                                                .next();
                                        TrackManagerActivity.this.mNameList
                                                .add(localTrackObj2
                                                        .getTrackName());
                                        if (localTrackObj2.isUp()) {
                                            TrackManagerActivity.this.mIsUpLst
                                                    .add(true);
                                        } else {
                                            TrackManagerActivity.this.mIsUpLst
                                                    .add(false);
                                        }
                                        TrackManagerActivity.this.mCheckList[i] = false;
                                        ++i;
                                    }
                                    TrackManagerActivity.this.mSelectButton
                                            .setText("全选");
                                    setExportCheckAdapter();

                                }
                            }).setNegativeButton("取消", null).show();
        } else {
            ToastUtils.showLong("请选择要删除的轨迹信息！");
        }

    }

    /**
     * 地图上查看选中的轨迹
     */
    public void mapViewOpt() {
        MapOperate.mapShowTrackPaths(mAllSelectTrackObjs);
        Intent localIntent = new Intent(
                TrackManagerActivity.this, MainActivity.class);
        localIntent
                .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        localIntent.putExtra("opt", 4369);
        this.startActivity(localIntent);
    }

    private void exportDataOpt() {

    }

    private void initDataExchangeLayerNameList() {
        this.mAllSelectTrackObjs.clear();
        int i = 0;
        boolean[] arrayOfBoolean = this.mCheckList;
        int boolLen = arrayOfBoolean.length;
        for (int k = 0; k < boolLen; k++, i++) {
            if (arrayOfBoolean[k]) {
                this.mAllSelectTrackObjs.add(this.mAllTrackObjs
                        .get(i));
            }
        }
    }

    private void showMsgDlg(String paramString1, String paramString2) {

    }

    private void writeAllTrackXmlFile() {

    }

    private void initHandler(){
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case 0:
                        if(tracklist!=null&&tracklist.getTrackList()!=null&&tracklist.getTrackList().size()>0){
                            qBadgeView = new QBadgeView(TrackManagerActivity.this);
                            qBadgeView.bindTarget(btnUpdateData).setBadgeNumber(tracklist.getTrackList().size());
                        }else{
                            btnUpdateData.setVisibility(View.GONE);
                        }

                        break;
                    case 1:
                        if(qBadgeView!=null){
                            qBadgeView.hide(true);
                        }

                        btnUpdateData.setVisibility(View.GONE);
                        break;
                    case 2:
                        getDJQINFO();
                        break;
                }
                super.handleMessage(msg);
            }
        };
    }

    public void getTrackList(int userId,String userName) {
        String url = GisQueryApplication.getApp().getProjectconfig().getTracklistUrl() + "?type=track" + "&partId=" + userId + "&partName=" + userName;
        Networks.subscribeObserver(Networks.getRetrofit().create(TracklistService.class).tracklist(url),
                new BaseObserver<Tracklist>() {

                    @Override
                    protected void onHandleSuccess(Tracklist tracklist) {
                        TrackManagerActivity.this.tracklist = tracklist;
                        handler.sendEmptyMessage(0);
                    }

                    @Override
                    protected void onHandleError(BaseEntity<Tracklist> value) {
                        super.onHandleError(value);
                        handler.sendEmptyMessage(1);
                    }
                });


    }

    private class ExportDataTask extends AsyncTask<Void, String, Boolean> {
        private ProgressDialog dialog = null;

        protected Boolean doInBackground(Void[] paramArrayOfVoid) {
            TrackManagerActivity.this.writeAllTrackXmlFile();
            return Boolean.valueOf(true);
        }

        public void onPostExecute(Boolean paramBoolean) {
            if (paramBoolean.booleanValue())
                TrackManagerActivity.this.showMsgDlg("数据导出成功", "导出目录为：\n"
                        + SysConfig.AppWorkPath + "轨迹导出/");
            else {
                this.dialog.dismiss();
                ToastUtils.showLong("数据导出失败");
            }
        }

        protected void onPreExecute() {
            this.dialog = new ProgressDialog(TrackManagerActivity.this);
            this.dialog.setMessage("正在导出数据...");
            this.dialog.show();
        }
    }

    private class GetAllTrackInfoTask extends
            AsyncTask<String, String, Boolean> {
        List<TrackObj> allTrackObjs = null;
        private ProgressDialog dialog = null;

        public Boolean doInBackground(String[] paramArrayOfString) {
            if (paramArrayOfString.length > 0)
                this.allTrackObjs = TrackManagerActivity.this.mTrackManager
                        .getAllTrackInfos(true);

            return Boolean.valueOf(true);
        }

        public void onPostExecute(Boolean paramBoolean) {
            this.dialog.dismiss();
            if ((this.allTrackObjs != null) && (this.allTrackObjs.size() > 0)) {
                TrackManagerActivity.this.mAllTrackObjs.clear();
                TrackManagerActivity.this.mAllTrackObjs.addAll(this.allTrackObjs);
                initActivity();
            } else if (TrackManagerActivity.this.mAllTrackObjs.size() == 0) {
                TrackManagerActivity.this.showMsgDlg("数据导出", "轨迹数据为空");
                initActivity();
                return;
            }
            handler.sendEmptyMessage(2);

        }

        protected void onPreExecute() {
            this.dialog = new ProgressDialog(TrackManagerActivity.this);
            this.dialog.setMessage("正在读取轨迹数据...");
            this.dialog.show();
        }
    }

    private class MOnClickListener implements View.OnClickListener {
        public void onClick(View paramView) {
            switch (paramView.getId()) {
                case R.id.btn_delete:
                    Log.i("用户操作", "SetVisibleLayerActivity--点击【确定】按钮");
                    TrackManagerActivity.this.initDataExchangeLayerNameList();
                    TrackManagerActivity.this.deleteOpt();
                    break;
                case R.id.btn_cancel:
                    Log.i("用户操作", "SetVisibleLayerActivity--点击【取消】按钮");
                    MapOperate.clearTrackingLayer();
                    TrackManagerActivity.this.finish();
                    break;
                case R.id.btn_select_opt: {

                    if (TrackManagerActivity.this.mSelectButton.getText().equals(
                            "全选")) {
                        int ckLength = TrackManagerActivity.this.mCheckList.length;
                        for (int j = 0; j < ckLength; j++) {
                            TrackManagerActivity.this.mCheckList[j] = true;
                        }
                        TrackManagerActivity.this.mSelectButton.setText("全不选");
                    } else {
                        int ckLength = TrackManagerActivity.this.mCheckList.length;
                        for (int j = 0; j < ckLength; j++) {
                            TrackManagerActivity.this.mCheckList[j] = false;
                        }
                        TrackManagerActivity.this.mSelectButton.setText("全选");
                    }
                    TrackManagerActivity.this.mlistViewLyrs.invalidateViews();
                    break;
                }
                case R.id.btn_mapview_opt: {
                    TrackManagerActivity.this.initDataExchangeLayerNameList();
                    TrackManagerActivity.this.mapViewOpt();
                    break;
                }
                default:
                    return;
            }

        }
    }

    private void setSelectButton() {
        TrackManagerActivity.this.mSelectButton.setText("全选");
        setExportCheckAdapter();
    }

}