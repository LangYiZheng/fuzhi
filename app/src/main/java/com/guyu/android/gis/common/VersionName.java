package com.guyu.android.gis.common;

import com.google.gson.annotations.SerializedName;

/**
 * Created by admin on 2017/9/28.
 */

public class VersionName {
    @SerializedName("apk")
    private String apk;

    @SerializedName("xml")
    private String xml;

    @SerializedName("db")
    private String db;

    public String getApk() {
        return apk;
    }

    public String getXml() {
        return xml;
    }

    public String getDb() {
        return db;
    }
}
