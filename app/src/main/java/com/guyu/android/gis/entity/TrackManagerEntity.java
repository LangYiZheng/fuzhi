package com.guyu.android.gis.entity;

import com.chad.library.adapter.base.entity.MultiItemEntity;

public class TrackManagerEntity implements MultiItemEntity {
    public final static int INFO_VIEW = 0;
    private int type = 0;
    public TrackManagerEntity(int type){
        this.type = type;
    }
    @Override
    public int getItemType() {
        return type;
    }
}
