package com.guyu.android.database.task;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.util.Log;

import java.io.File;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.esri.core.geometry.Point;
import com.guyu.android.database.DBOptExternal2;
import com.guyu.android.gis.activity.MainActivity;
import com.guyu.android.gis.app.GisQueryApplication;
import com.guyu.android.gis.common.TrackObj;
import com.guyu.android.gis.opt.MapOperate;
import com.guyu.android.utils.FileUtil;
import com.guyu.android.utils.MathUtils;
import com.guyu.android.utils.SysConfig;
import com.guyu.android.utils.TimeUtil;
import com.guyu.android.utils.Utility;

public class TrackDBMExternal extends DBOptExternal2 {
    private static final String FILE_BMFZR = "BMFZR";
    private static final String FILE_IS_UP_INT = "isUp";
    private static final String FILE_PEO_ID_STRING = "humanId";
    private static final String FILE_TASK_ID_STRING = "taskId";
    private static final String FILE_TIME_LONG = "time";
    private static final String FILE_TRACK_ID_STRING = "trackId";
    private static final String FILE_TRACK_NAME_STRING = "trackName";
    private static final String FILE_TRACK_PATH_STRING = "trackPath";
    private static final String FILE_TRACK_REMARK_STRING = "remark";
    private static final String FILE_XCDD = "XCDD";
    private static final String FILE_XCLX = "XCLX";
    private static final String FILE_XCQK = "XCQK";
    private static final String FILE_XXFKJYJXCQQK = "XXFKJYJXCQQK";
    private static final String TABLE_NAME = "tbTrack";
    private static final String TAG = "TrackDBMExternal";

    public TrackDBMExternal(Context paramContext) {
        super(paramContext, "tbTrack");
    }

    public TrackDBMExternal(Context paramContext, String paramString) {
        super(paramContext, paramString);
    }

    public TrackDBMExternal(MainActivity mainActivity) {
        super(mainActivity, "tbTrack");
    }

    /**
     * 读取轨迹文件返回以地图坐标系为准的点集合
     *
     * @param paramString
     * @return
     */
    private List<Point> readGpxFile(String paramString) {
        List<Point> rtnPoints = new ArrayList<Point>();
        File localFile = new File(paramString);
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory
                    .newInstance();
            Document document = null;
            DocumentBuilder builder = factory.newDocumentBuilder();
            document = builder.parse(localFile);
            Element root = document.getDocumentElement();
            NodeList nl_trkpts = root.getElementsByTagName("trkpt");
            for (int i = 0; i < nl_trkpts.getLength(); i++) {
                Element el_trkpt = (Element) nl_trkpts.item(i);
                double lon = MathUtils.GetAccurateNumber(
                        Double.parseDouble(el_trkpt.getAttribute("lon")), 6);
                double lat = MathUtils.GetAccurateNumber(
                        Double.parseDouble(el_trkpt.getAttribute("lat")), 6);
                Point mapPoint;
                if (MapOperate.IsPrjIsWGS84()) {
                    Point mapPointJWD = new Point(lon, lat);
                    mapPoint = Utility.fromWgs84ToMap2(mapPointJWD,
                            MapOperate.map.getSpatialReference());

                } else {
                    mapPoint = new Point(lon, lat);
                }

                rtnPoints.add(mapPoint);
            }
        } catch (Exception exception) {
        }
        return rtnPoints;
    }

    public void deleteTrackByName(String trackName) {
        FileUtil.deleteFile(MapOperate.mTrackPath + "/" + trackName + ".gpx");
        delete("trackName = ?", new String[]{trackName});
    }

    public List<TrackObj> getAllNoUpTrackInfos() {
        ArrayList<TrackObj> localArrayList = new ArrayList<TrackObj>();
        String[] arrayOfString = new String[1];
        if (SysConfig.mHumanInfo != null) {
            arrayOfString[0] = "" + SysConfig.mHumanInfo.getHumanId();
        } else {
            SharedPreferences sp_humanInfo = GisQueryApplication.getApp().getSharedPreferences("humanInfo",
                    Context.MODE_PRIVATE);
            arrayOfString[0] = "" + sp_humanInfo.getInt("HUMAN_ID", -1);
        }
        Cursor localCursor = query(null, "humanId = ? and isUp = 0",
                arrayOfString, null, null, "time desc");
        if (localCursor != null && localCursor.getCount() > 0) {
            while (localCursor.moveToNext()) {

                TrackObj localTrackObj = new TrackObj();
                localTrackObj.setHumanID(Integer.parseInt(localCursor
                        .getString(localCursor.getColumnIndex("humanId"))));
                localTrackObj.setTaskId(localCursor.getString(localCursor
                        .getColumnIndex("taskId")));
                localTrackObj.setTrackId(localCursor.getString(localCursor
                        .getColumnIndex("trackId")));
                localTrackObj.setTrackName(localCursor.getString(localCursor
                        .getColumnIndex("trackName")));
                localTrackObj.setTrackPath(localCursor.getString(localCursor
                        .getColumnIndex("trackPath")));
                localTrackObj.setXCDD(localCursor.getString(localCursor
                        .getColumnIndex("XCDD")));
                localTrackObj.setXCLX(localCursor.getString(localCursor
                        .getColumnIndex("XCLX")));
                localTrackObj.setXCQK(localCursor.getString(localCursor
                        .getColumnIndex("XCQK")));
                localTrackObj.setXXFKJYJXCQQK(localCursor.getString(localCursor
                        .getColumnIndex("XXFKJYJXCQQK")));
                localTrackObj.setBMFZR(localCursor.getString(localCursor
                        .getColumnIndex("BMFZR")));
                localTrackObj.setTime(localCursor.getLong(localCursor
                        .getColumnIndex("time")));
                localTrackObj.setIsUp(localCursor.getInt(localCursor
                        .getColumnIndex("isUp")));
                localTrackObj.setFirm(localCursor.getInt(localCursor
                        .getColumnIndex("firm")));
                localTrackObj.setDJQ(localCursor.getInt(localCursor
                        .getColumnIndex("DJQ")));
                localTrackObj.setDJZQ(localCursor.getInt(localCursor
                        .getColumnIndex("DJZQ")));
                localTrackObj
                        .setSetTrackPoints(readGpxFile(MapOperate
                                .GetTrackPath()
                                + localTrackObj.getTrackName()
                                + ".gpx"));
                if (!(localTrackObj.isValid()))
                    continue;
                localArrayList.add(localTrackObj);
            }
            localCursor.close();
            return localArrayList;
        } else {
            localCursor.close();
            Log.e("TrackDBMExternal", "轨迹记录为空");
            return localArrayList;
        }
    }

    public List<TrackObj> getAllTrackInfos() {
        ArrayList<TrackObj> localArrayList = new ArrayList<TrackObj>();
        String[] arrayOfString = new String[1];
        if (SysConfig.mHumanInfo != null) {
            arrayOfString[0] = "" + SysConfig.mHumanInfo.getHumanId();
        } else {
            SharedPreferences sp_humanInfo = GisQueryApplication.getApp().getSharedPreferences("humanInfo",
                    Context.MODE_PRIVATE);
            arrayOfString[0] = "" + sp_humanInfo.getInt("HUMAN_ID", -1);
        }
        Cursor localCursor = query(null, "humanId = ?", arrayOfString, null,
                null, "time desc");
        if (localCursor != null && localCursor.getCount() > 0) {
            while (localCursor.moveToNext()) {

                TrackObj localTrackObj = new TrackObj();
                localTrackObj.setHumanID(Integer.parseInt(localCursor
                        .getString(localCursor.getColumnIndex("humanId"))));
                localTrackObj.setTaskId(localCursor.getString(localCursor
                        .getColumnIndex("taskId")));
                localTrackObj.setTrackId(localCursor.getString(localCursor
                        .getColumnIndex("trackId")));
                localTrackObj.setTrackName(localCursor.getString(localCursor
                        .getColumnIndex("trackName")));
                localTrackObj.setTrackPath(localCursor.getString(localCursor
                        .getColumnIndex("trackPath")));
                localTrackObj.setXCDD(localCursor.getString(localCursor
                        .getColumnIndex("XCDD")));
                localTrackObj.setXCLX(localCursor.getString(localCursor
                        .getColumnIndex("XCLX")));
                localTrackObj.setXCQK(localCursor.getString(localCursor
                        .getColumnIndex("XCQK")));
                localTrackObj.setXXFKJYJXCQQK(localCursor.getString(localCursor
                        .getColumnIndex("XXFKJYJXCQQK")));
                localTrackObj.setBMFZR(localCursor.getString(localCursor
                        .getColumnIndex("BMFZR")));
                localTrackObj.setTime(localCursor.getLong(localCursor
                        .getColumnIndex("time")));
                localTrackObj.setIsUp(localCursor.getInt(localCursor
                        .getColumnIndex("isUp")));
                localTrackObj
                        .setSetTrackPoints(readGpxFile(MapOperate
                                .GetTrackPath()
                                + localTrackObj.getTrackName()
                                + ".gpx"));
                if (!(localTrackObj.isValid()))
                    continue;
                localArrayList.add(localTrackObj);
            }
            localCursor.close();
            return localArrayList;
        } else {
            localCursor.close();
            Log.e("TrackDBMExternal", "轨迹记录为空");
            return localArrayList;
        }
    }

    public List<TrackObj> getAllTrackInfos(boolean paramBoolean) {
        ArrayList<TrackObj> localArrayList = new ArrayList<TrackObj>();
        Cursor localCursor;
        if (paramBoolean) {
            localCursor = query(null, null, null, null, null,
                    "time desc");
        } else {
            String[] arrayOfString = new String[1];
            if (SysConfig.mHumanInfo != null) {
                arrayOfString[0] = "" + SysConfig.mHumanInfo.getHumanId();
            } else {
                SharedPreferences sp_humanInfo = GisQueryApplication.getApp().getSharedPreferences("humanInfo",
                        Context.MODE_PRIVATE);
                arrayOfString[0] = "" + sp_humanInfo.getInt("HUMAN_ID", -1);
            }
            localCursor = query(null, "humanId = ?", arrayOfString, null, null,
                    "time desc");
        }
        if (localCursor != null && localCursor.getCount() > 0) {
            while (localCursor.moveToNext()) {

                TrackObj localTrackObj = new TrackObj();
                localTrackObj.setHumanID(Integer.parseInt(localCursor
                        .getString(localCursor.getColumnIndex("humanId"))));
                localTrackObj.setTaskId(localCursor.getString(localCursor
                        .getColumnIndex("taskId")));
                localTrackObj.setTrackId(localCursor.getString(localCursor
                        .getColumnIndex("trackId")));
                localTrackObj.setTrackName(localCursor.getString(localCursor
                        .getColumnIndex("trackName")));
                localTrackObj.setTrackPath(localCursor.getString(localCursor
                        .getColumnIndex("trackPath")));
                localTrackObj.setXCDD(localCursor.getString(localCursor
                        .getColumnIndex("XCDD")));
                localTrackObj.setXCLX(localCursor.getString(localCursor
                        .getColumnIndex("XCLX")));
                localTrackObj.setXCQK(localCursor.getString(localCursor
                        .getColumnIndex("XCQK")));
                localTrackObj.setXXFKJYJXCQQK(localCursor.getString(localCursor
                        .getColumnIndex("XXFKJYJXCQQK")));
                localTrackObj.setBMFZR(localCursor.getString(localCursor
                        .getColumnIndex("BMFZR")));
                localTrackObj.setTime(localCursor.getLong(localCursor
                        .getColumnIndex("time")));
                localTrackObj.setIsUp(localCursor.getInt(localCursor
                        .getColumnIndex("isUp")));
                localTrackObj.setFirm(localCursor.getInt(localCursor
                        .getColumnIndex("firm")));
                localTrackObj.setDJQ(localCursor.getInt(localCursor
                        .getColumnIndex("DJQ")));
                localTrackObj.setDJZQ(localCursor.getInt(localCursor
                        .getColumnIndex("DJZQ")));
                localTrackObj
                        .setSetTrackPoints(readGpxFile(MapOperate
                                .GetTrackPath()
                                + localTrackObj.getTrackName()
                                + ".gpx"));
                if (!(localTrackObj.isValid()))
                    continue;
                localArrayList.add(localTrackObj);
            }
            localCursor.close();
            return localArrayList;
        } else {
            localCursor.close();
            Log.e("TrackDBMExternal", "轨迹记录为空");
            return localArrayList;
        }

    }

    public TrackObj getTrackInfo(String paramString) {
        String[] arrayOfString = new String[2];
        if (SysConfig.mHumanInfo != null) {
            arrayOfString[0] = "" + SysConfig.mHumanInfo.getHumanId();
        } else {
            SharedPreferences sp_humanInfo = GisQueryApplication.getApp().getSharedPreferences("humanInfo",
                    Context.MODE_PRIVATE);
            arrayOfString[0] = "" + sp_humanInfo.getInt("HUMAN_ID", -1);
        }
        arrayOfString[1] = paramString;
        try(Cursor localCursor = query(null, "humanId = ? and trackName = ?",
                arrayOfString, null, null, "time desc");){
            if (localCursor != null) {
                TrackObj localTrackObj;
                if (localCursor.getCount() == 1) {
                    localCursor.moveToFirst();
                    localTrackObj = new TrackObj();
                    localTrackObj.setHumanID(Integer.parseInt(localCursor
                            .getString(localCursor.getColumnIndex("humanId"))));
                    localTrackObj.setTaskId(localCursor.getString(localCursor
                            .getColumnIndex("taskId")));
                    localTrackObj.setTrackId(localCursor.getString(localCursor
                            .getColumnIndex("trackId")));
                    localTrackObj.setTrackName(localCursor.getString(localCursor
                            .getColumnIndex("trackName")));
                    localTrackObj.setTrackPath(localCursor.getString(localCursor
                            .getColumnIndex("trackPath")));
                    localTrackObj.setXCDD(localCursor.getString(localCursor
                            .getColumnIndex("XCDD")));
                    localTrackObj.setXCLX(localCursor.getString(localCursor
                            .getColumnIndex("XCLX")));
                    localTrackObj.setXCQK(localCursor.getString(localCursor
                            .getColumnIndex("XCQK")));
                    localTrackObj.setXXFKJYJXCQQK(localCursor.getString(localCursor
                            .getColumnIndex("XXFKJYJXCQQK")));
                    localTrackObj.setBMFZR(localCursor.getString(localCursor
                            .getColumnIndex("BMFZR")));
                    localTrackObj.setTime(localCursor.getLong(localCursor
                            .getColumnIndex("time")));
                    localTrackObj.setIsUp(localCursor.getInt(localCursor
                            .getColumnIndex("isUp")));
                    localTrackObj.setFirm(localCursor.getInt(localCursor
                            .getColumnIndex("firm")));
                    localTrackObj.setDJQ(localCursor.getInt(localCursor
                            .getColumnIndex("DJQ")));
                    localTrackObj.setDJZQ(localCursor.getInt(localCursor
                            .getColumnIndex("DJZQ")));
                    localTrackObj.setSetTrackPoints(readGpxFile(localTrackObj
                            .getTrackPath()));


                    localCursor.close();

                } else {
                    Log.e("TrackDBMExternal", "信息有误，查询到多个轨迹信息");
                    localTrackObj = null;
                }
                return localTrackObj;
            } else {
                Log.e("TrackDBMExternal", "getTrackInfo未查询到相关信息");
                return null;
            }
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }



    }

    public boolean insertOneTrack(TrackObj paramTrackObj) {
        boolean bool = false;
        TrackObj temp = getTrackInfo(paramTrackObj.getTrackName());
        if (temp == null) {
            if (paramTrackObj != null)
                bool = insertOneTrack(paramTrackObj.getTaskId(),
                        paramTrackObj.getTrackId(), paramTrackObj.getTrackName(),
                        paramTrackObj.getTrackPath(), paramTrackObj.getRemark(),
                        paramTrackObj.getXCDD(), paramTrackObj.getXCLX(), paramTrackObj.getXCQK(), paramTrackObj.getXXFKJYJXCQQK(), paramTrackObj.getBMFZR(), paramTrackObj.getTime(), paramTrackObj.getIsUp(), paramTrackObj.getFirm(),paramTrackObj.getDJQ(),paramTrackObj.getDJZQ());
        }
        return bool;
    }

    public boolean insertOneTrack(String paramString1, String paramString2,
                                  String paramString3, String paramString4, String paramString5,
                                  String paramString6, String paramString7, String paramString8,
                                  String paramString9, String paramString10, long paramLong, long isUp, int firm,int DJQ,int DJZQ) {
        ContentValues localContentValues = new ContentValues();
        localContentValues.put("humanId", SysConfig.mHumanInfo.getHumanId());
        localContentValues.put("taskId", paramString1);
        localContentValues.put("trackId", paramString2);
        localContentValues.put("trackName", paramString3);
        localContentValues.put("trackPath", paramString4);
        localContentValues.put("remark", paramString5);
        localContentValues.put("XCDD", paramString6);
        localContentValues.put("XCLX", paramString7);
        localContentValues.put("XCQK", paramString8);
        localContentValues.put("XXFKJYJXCQQK", paramString9);
        localContentValues.put("BMFZR", paramString10);
        localContentValues.put("time", Long.valueOf(paramLong));
        localContentValues.put("isUp", Integer.valueOf((int) isUp));
        localContentValues.put("firm", firm);
        localContentValues.put("DJQ", DJQ);
        localContentValues.put("DJZQ", DJZQ);
        return insert(localContentValues);
    }

    public void updataTrackState(List<TrackObj> paramList) {

        if (paramList != null) {
            Iterator<TrackObj> localIterator = paramList.iterator();
            while (localIterator.hasNext()) {
                updateTrackInfosReportState(
                        localIterator.next().getTrackName(), 1);
            }
        }
    }

    public boolean updateTrackAtrr(String paramString, TrackObj trackObj) {
        boolean bool = false;
        if (paramString != null) {
            ContentValues localContentValues = new ContentValues();
            localContentValues.put("XCDD", trackObj.getXCDD());
            localContentValues.put("XCLX", trackObj.getXCLX());
            localContentValues.put("XCQK", trackObj.getXCQK());
            localContentValues.put("XXFKJYJXCQQK", trackObj.getXXFKJYJXCQQK());
            localContentValues.put("BMFZR", trackObj.getBMFZR());
            localContentValues.put("firm", trackObj.getFirm());
            localContentValues.put("DJQ", trackObj.getDJQ());
            localContentValues.put("DJZQ", trackObj.getDJZQ());
            String[] arrayOfString = new String[2];
            arrayOfString[0] = "" + SysConfig.mHumanInfo.getHumanId();
            arrayOfString[1] = paramString;
            bool = update(localContentValues, "humanId = ? and trackName = ?",
                    arrayOfString);
        }
        return bool;
    }

    public boolean updateTrackInfosReportState(String paramString, int paramInt) {
        ContentValues localContentValues = new ContentValues();
        localContentValues.put("isUp", Integer.valueOf(paramInt));
        String[] arrayOfString = new String[2];
        arrayOfString[0] = "" + SysConfig.mHumanInfo.getHumanId();
        arrayOfString[1] = paramString;
        return update(localContentValues, "humanId = ? and trackName = ?",
                arrayOfString);
    }

    /**
     * 更新上传状态
     *
     * @return
     */
    public boolean updateTrackUpState(String trackID, int upState) {
        String[] arrayOfString = new String[2];
        arrayOfString[0] = "" + SysConfig.mHumanInfo.getHumanId();
        arrayOfString[1] = trackID;
        ContentValues localContentValues = new ContentValues();
        localContentValues.put("isUp", upState);
        return update(localContentValues, "humanId = ? and trackId = ?", arrayOfString);
    }

    public boolean addColumn(){
        String time = TimeUtil.getTime2();
        String AlterTable = "ALTER TABLE  "+this.TABLE_NAME+" RENAME TO "+this.TABLE_NAME+time;
        String CtreteNewTable ="CREATE TABLE "+this.TABLE_NAME+" (trackId      TEXT,_id          INTEGER,humanId      TEXT (16),taskId       TEXT (20),trackName    TEXT (50),trackPath    TEXT (300),remark       TEXT (300),XCDD         TEXT (300),XCLX         TEXT (300),XCQK         TEXT (300),XXFKJYJXCQQK TEXT (300),BMFZR        TEXT (300),time         LONG,isUp         INT,firm         INTEGER,DJQ          INTEGER    DEFAULT ( -1),DJZQ         INTEGER    DEFAULT ( -1),PRIMARY KEY (_id ASC))";
        String InsertData ="INSERT INTO "+this.TABLE_NAME+"(trackId,_id,humanId,taskId,trackName,trackPath,remark,XCDD,XCLX,XCQK,XXFKJYJXCQQK,BMFZR,time,isUp,firm) SELECT trackId,_id,humanId,taskId,trackName,trackPath,remark,XCDD,XCLX,XCQK,XXFKJYJXCQQK,BMFZR,time,isUp,firm" +" FROM "+this.TABLE_NAME+time;
        String DropOldTable = "DROP TABLE "+this.TABLE_NAME+time;
        try {
            this.getmDbHelper().getWritableDatabase().execSQL(AlterTable);
            this.getmDbHelper().getWritableDatabase().execSQL(CtreteNewTable);
            this.getmDbHelper().getWritableDatabase().execSQL(InsertData);
            this.getmDbHelper().getWritableDatabase().execSQL(DropOldTable);
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
       return true;

    }
}