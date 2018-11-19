package com.guyu.android.gis.entity;

import com.chad.library.adapter.base.entity.MultiItemEntity;

/**
 * Created by YunHuan on 2017/11/6.
 */

public class UpdateMapsEntity implements MultiItemEntity {
    public final static int UPDATE_VIEW = 0;
//    public final static int MOVE_VIEW = 1;
    public final static int CANCEL_VIEW = 2;


    private int type;

    public UpdateMapsEntity(final int type) {
        this.type = type;
    }

    @Override
    public int getItemType() {

        return type;
    }
}
