package com.guyu.android.gis.common;

import com.google.gson.annotations.SerializedName;

public class GpsOffset {
    @SerializedName("MOVE_X")
    private double MOVE_X;

    @SerializedName("MOVE_Y")
    private double MOVE_Y;

    public double getMOVE_X() {
        return MOVE_X;
    }

    public double getMOVE_Y() {
        return MOVE_Y;
    }
}
