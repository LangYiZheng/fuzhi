package com.guyu.android.gis.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.esri.core.geometry.Point;

public class TrackObj
  implements Serializable
{
  private  String BMFZR="";
  private  final String TAG = "TrackObj";
  private  String XCDD = "";
  private  String XCLX = "";
  private  String XCQK = "";
  private  String XXFKJYJXCQQK = "";
  private final int SPLITE_NUM = 4;
  private int humanID;
  private List<Point> point2ds = null;
  private String remark;
  private String taskId;
  private long time;
//  private String trackBMFZR = "";
  private String trackId;
  private String trackName;
  private String trackPath;
  private int firm = 0;
  private int DJQ = -1;
  private int DJZQ = -1;
//  private String trackXCDD = "";
//  private String trackXCLX = "";
//  private String trackXCQK = "";
//  private String trackXXFK = "";


  private int isUp;

  public int getFirm() {
    return firm;
  }

  public void setFirm(int firm) {
    this.firm = firm;
  }

  public String[] getAllChildPoints()
  {
   
    return null;
  }

  public int getDJQ() {
    return DJQ;
  }

  public void setDJQ(int DJQ) {
    this.DJQ = DJQ;
  }

  public int getDJZQ() {
    return DJZQ;
  }

  public void setDJZQ(int DJZQ) {
    this.DJZQ = DJZQ;
  }

  public List<Point> getAllTrackPoints()
  {
    return this.point2ds;
  }

  public int getHumanID()
  {
    return this.humanID;
  }

  public String getRemark()
  {
    return this.remark;
  }

  public String getTaskId()
  {
    return this.taskId;
  }

  public long getTime()
  {
    return this.time;
  }

//  public String getTrackBMFZR()
//  {
//    return this.trackBMFZR;
//  }

  public String getTrackId()
  {
    return this.trackId;
  }

  public String getTrackName()
  {
    return this.trackName;
  }

  public String getTrackPath()
  {
    return this.trackPath;
  }

//  public String getTrackXCDD()
//  {
//    return this.trackXCDD;
//  }
//
//  public String getTrackXCLX()
//  {
//    return this.trackXCLX;
//  }
//
//  public String getTrackXCQK()
//  {
//    return this.trackXCQK;
//  }

//  public String getTrackXXFK()
//  {
//    return this.trackXXFK;
//  }

  public int getIsUp()
  {
    return this.isUp;
  }

  public boolean isUp()
  {
    return (this.isUp == 1);
  }

  public boolean isValid()
  {
    return ((this.point2ds != null) && (this.point2ds.size() > 0));
  }

  public void setHumanID(int humanID)
  {
    this.humanID = humanID;
  }

  public void setRemark(String paramString)
  {
    this.remark = paramString;
  }

  public void setSetTrackPoints(List<Point> paramList)
  {
    this.point2ds = paramList;
  }

  public void setTaskId(String paramString)
  {
    this.taskId = paramString;
  }

  public void setTime(long paramLong)
  {
    this.time = paramLong;
  }

//  public void setTrackBMFZR(String paramString)
//  {
//    this.trackBMFZR = paramString;
//  }

  public void setTrackId(String paramString)
  {
    this.trackId = paramString;
  }

  public void setTrackName(String paramString)
  {
    this.trackName = paramString;
  }

  public void setTrackPath(String paramString)
  {
    this.trackPath = paramString;
  }

  public  String getBMFZR() {
    return BMFZR;
  }

  public  void setBMFZR(String BMFZR) {
    this.BMFZR = BMFZR;
  }

  public  String getTAG() {
    return TAG;
  }

  public  String getXCDD() {
    return XCDD;
  }

  public  void setXCDD(String XCDD) {
    this.XCDD = XCDD;
  }

  public  String getXCLX() {
    return XCLX;
  }

  public  void setXCLX(String XCLX) {
    this.XCLX = XCLX;
  }

  public  String getXCQK() {
    return XCQK;
  }

  public  void setXCQK(String XCQK) {
    this.XCQK = XCQK;
  }

  public  String getXXFKJYJXCQQK() {
    return XXFKJYJXCQQK;
  }

  public  void setXXFKJYJXCQQK(String XXFKJYJXCQQK) {
    this.XXFKJYJXCQQK = XXFKJYJXCQQK;
  }

  public int getSPLITE_NUM() {
    return SPLITE_NUM;
  }

  public List<Point> getPoint2ds() {
    return point2ds;
  }

  public void setPoint2ds(List<Point> point2ds) {
    this.point2ds = point2ds;
  }
//  public void setTrackXCDD(String paramString)
//  {
//    this.trackXCDD = paramString;
//  }
//
//  public void setTrackXCLX(String paramString)
//  {
//    this.trackXCLX = paramString;
//  }
//
//  public void setTrackXCQK(String paramString)
//  {
//    this.trackXCQK = paramString;
//  }

//  public void setTrackXXFK(String paramString)
//  {
//    this.trackXXFK = paramString;
//  }

  public void setIsUp(int isUp)
  {
    this.isUp = isUp;
  }
}