package com.guyu.android.utils;

import com.guyu.android.gis.common.Canton;
import com.guyu.android.gis.common.Human;
import com.guyu.android.gis.common.Unit;

import java.util.ArrayList;
import java.util.List;


public class SysConfig
{
  public static int AUTOSetDis = 0;
  public static int AUTOSetTime = 0;
  public static int AUTOType = 0;
  public static String AppName;
  public static String AppWorkPath;
  public static String BaseMapFolderName;
  public static List<String> CenterPhoneLst;
  public static String DBName;
  public static String DBNameJournal;
  public static String DBPath;
  public static String DatasourceExpandName;
  public static String ExportDataFolderName;
  public static String GpsNumber;
  public static String GuidPointsFolderName;
  public static String ImgFolderName;
  public static boolean IsBaseFuction = false;
  public static boolean IsEnableSms = false;
  public static boolean IsLoginOk = false;
  public static String LicFilePath;
  public static String MapFolderName;
  public static String PrjWorkPath;
  public static String SDCardPath;
  public static int ScreenWidth = 0;
  public static int ScrrenHeight = 0;
  public static String SymbolPath;
  public static String TrackFolderName;
  public static int TrackID = 0;
  public static int TrackSetDis = 0;
  public static int TrackSetTime = 0;
  public static int TrackType = 0;
  public static String TrackUpTip;
  
  public static int TaskUpdateTime = 5;
  public static final int VerTypeHYLD = 3;
  public static final int VerTypeNormal = 1;
  public static final int VerTypeTaiYuan = 2;
  public static String WebAdd;
  public static String WebService_Namespace;
  public static String WorkspaceExpandNameM;
  public static String WorkspaceExpandNameX;
  public static String[] allDeviceIds;
  public static String[] allLandNames;
  public static Canton mCityCanton;
  public static Canton mCountyCanton;
  public static Unit mUnitInfo;
  public static Human mHumanInfo;
  public static Canton mProCanton;
  public static Canton mStreetCanton;
  public static String strCenterPhoneLst;
  public static int versionType = 3;

  public static String GPSInfo;
  public static Canton mCurrentCanton;
  
  static
  {
    AppName = "LandMapper";
    SDCardPath = "/mnt/sdcard/";
    AppWorkPath = "/mnt/sdcard/" + AppName + "/";
    LicFilePath = "/mnt/sdcard/" + AppName + "/";
    PrjWorkPath = "/mnt/sdcard/" + AppName + "/project/";
    SymbolPath = "/mnt/sdcard/" + AppName + "/symbol/";
    DBPath = "/mnt/sdcard/" + AppName + "/db/";
    MapFolderName = "Map";
    BaseMapFolderName = "BaseMap";
    ImgFolderName = "ImgFolder";
    ExportDataFolderName = "mucai_export_data";
    TrackFolderName = "Track";
    GuidPointsFolderName = "GuidePoints";
    WorkspaceExpandNameX = ".sxwu";
    WorkspaceExpandNameM = ".smwu";
    DatasourceExpandName = ".udb";
    DBName = "landmapper.db";
    DBNameJournal = "landmapper.db-journal";
    TrackType = 0;
    TrackSetTime = 5;
    TrackSetDis = 5;
    AUTOType = 0;
    AUTOSetTime = 5;
    AUTOSetDis = 10;
    ScreenWidth = 1000;
    ScrrenHeight = 1000;
    mProCanton = null;
    mCurrentCanton=null;
    mCityCanton = null;
    mCountyCanton = null;
    mStreetCanton = null;
    mUnitInfo = null;
    mHumanInfo = null;
    strCenterPhoneLst = "";
    CenterPhoneLst = new ArrayList();
    TrackUpTip = "10";
    WebAdd = "http://10.88.146.51:8099/Service.asmx";
    WebService_Namespace = "http://tempuri.org/";
    IsEnableSms = false;
    GpsNumber = "GPS001";
    TrackID = 0;
    IsLoginOk = false;
    GPSInfo=null;
  }
}