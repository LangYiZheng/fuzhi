package com.guyu.android.gis.common;

import com.google.gson.annotations.SerializedName;
import com.guyu.android.base.BaseEntity;

import java.util.List;

public class Tracklist extends BaseEntity{
    @SerializedName("tbCaseImg")
    List<CaseImg> al_landImgObj;
    @SerializedName("tbCaseSound")
    List<CaseSound> al_landSoundObj;
    @SerializedName("tbCaseVideo")
    List<CaseVideo> al_landVideoObj;
    @SerializedName("track")
    List< TrackObj> trackList;

    public List<CaseImg> getAl_landImgObj() {
        return al_landImgObj;
    }

    public List<CaseSound> getAl_landSoundObj() {
        return al_landSoundObj;
    }

    public List<CaseVideo> getAl_landVideoObj() {
        return al_landVideoObj;
    }

    public List<TrackObj> getTrackList() {
        return trackList;
    }
}
