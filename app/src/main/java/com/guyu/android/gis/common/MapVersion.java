package com.guyu.android.gis.common;

import com.google.gson.annotations.SerializedName;

/**
 * Created by YunHuan on 2017/11/3.
 */

public class MapVersion {
    @SerializedName("ID")
    private String ID;
    @SerializedName("NAMECN")
    private String NAMECN;
    @SerializedName("MAPTYPE")
    private String MAPTYPE;
    @SerializedName("MAPPATH")
    private String MAPPATH;
    @SerializedName("MAPVER")
    private String MAPVER;
    @SerializedName("NAME")
    private String NAME;
    @SerializedName("ADMINISTRATIVE")
    private String ADMINISTRATIVE;

    public String getID() {
        return ID;
    }

    public String getNAMECN() {
        return NAMECN;
    }

    public String getMAPTYPE() {
        return MAPTYPE;
    }

    public String getMAPPATH() {
        return MAPPATH;
    }

    public String getMAPVER() {
        return MAPVER;
    }

    public String getNAME() {
        return NAME;
    }

    public String getADMINISTRATIVE() {
        return ADMINISTRATIVE;
    }
}
