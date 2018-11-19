package com.guyu.android.gis.adapter;

import android.view.View;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.guyu.android.R;
import com.guyu.android.gis.activity.TrackManagerActivity;
import com.guyu.android.gis.common.TrackObj;
import com.guyu.android.gis.entity.TrackManagerEntity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TrackManagerAdapter  extends BaseMultiItemQuickAdapter<TrackManagerEntity, BaseViewHolder> {
    private List<TrackManagerEntity> list;
    private List<TrackManagerEntity> list2;
    private Map<String,Map<String,List<TrackObj>>> djqinfo;
    private List<String> info;
    private int state = 0;
    private String select;
    private TrackManagerActivity activity;
    public TrackManagerAdapter(List<TrackManagerEntity> list,Map<String,Map<String,List<TrackObj>>> djqinfo,TrackManagerActivity activity){
        super(list);
        this.list = list;
        this.djqinfo = djqinfo;
        this.activity = activity;
        addItemType(TrackManagerEntity.INFO_VIEW, R.layout.track_manager_list_text);
        getInfo();
        this.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener()
        {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                if(state == 0){
                    select = info.get(position);
                    getInfo(position);
                    setNewData(list2);
                    ++state;
                }else {
                    List<TrackObj> list1 = djqinfo.get(select).get(info.get(position));
                    activity.setmAllTrackObjs(list1);
                    state = 0;
                }

            }
        });
    }

    private void getInfo() {
        info = new ArrayList<>();
        Iterator<String> key=djqinfo.keySet().iterator();
        while (key.hasNext()){
            info.add(key.next());
        }

    }
    private void getInfo(int position) {
        Iterator<String> key=djqinfo.get(info.get(position)).keySet().iterator();
        info = new ArrayList<>();
        while (key.hasNext()){
            info.add(key.next());
        }
        list2 = getDataList(position);
    }

    private List<TrackManagerEntity> getDataList(int position) {
        List<TrackManagerEntity> list = new ArrayList<>();
        Set<String> key = djqinfo.get(select).keySet();
        for (int i = 0; i <key.size() ; i++) {
            list.add(new TrackManagerEntity(TrackManagerEntity.INFO_VIEW));
        }
        return  list;
    }

    @Override
    protected void convert(BaseViewHolder helper, TrackManagerEntity item) {
        int position = helper.getAdapterPosition();

        helper.setText(R.id.tv,info.get(position));
        helper.addOnClickListener(R.id.tv);
    }

}
