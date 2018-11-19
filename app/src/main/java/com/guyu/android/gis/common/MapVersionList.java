package com.guyu.android.gis.common;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by YunHuan on 2017/11/3.
 */

public class MapVersionList {
    @SerializedName("list")
    private List<MapVersion> list;

    public List<MapVersion> getList() {
        return list;
    }
}
