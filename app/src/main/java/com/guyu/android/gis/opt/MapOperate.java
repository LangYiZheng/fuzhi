package com.guyu.android.gis.opt;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;

import android.graphics.Color;
import android.os.Handler;
import android.util.Log;

import com.guyu.android.R;
import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.MapView;
import com.esri.core.geometry.AreaUnit;
import com.esri.core.geometry.Envelope;
import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.LinearUnit;
import com.esri.core.geometry.MapGeometry;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polygon;
import com.esri.core.geometry.Polyline;
import com.esri.core.geometry.SpatialReference;
import com.esri.core.geometry.Unit;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.PictureMarkerSymbol;
import com.esri.core.symbol.SimpleFillSymbol;
import com.esri.core.symbol.SimpleLineSymbol;
import com.esri.core.symbol.SimpleMarkerSymbol;
import com.esri.core.symbol.SimpleMarkerSymbol.STYLE;
import com.guyu.android.gis.activity.MainActivity;
import com.guyu.android.gis.common.Case;
import com.guyu.android.gis.common.TrackObj;
import com.guyu.android.gis.common.UploadGpsObj;
import com.guyu.android.gis.maptools.GeometryAnalysisTool;
import com.guyu.android.gis.maptools.GeometryCollectTool;
import com.guyu.android.utils.GenerateSequenceUtil;
import com.guyu.android.utils.GpxUtils;
import com.guyu.android.utils.MDateUtils;
import com.guyu.android.utils.SysConfig;
import com.guyu.android.utils.UtilsTools;

public class MapOperate {
    private static String TAG = "MapOperate";
    public static int SET_GPSDATA = 0;
    private static int m_nAtuoTimes;
    public static MapView map;
    private static String prjName;
    public static Geometry curGeometry;
    public static UploadGpsObj curUploadGpsObj;// 当前上传GPS点信息
    public static String curLandUploadId = "-1";// 当前地块上传ID，用于生成案卷后 匹配更新RecID
    private static boolean mTrackIsRecording = false;
    private static GraphicsLayer grasLayer;
    private static String mCurrentTrackTaskId;
    private static boolean isRegistTrackId = false;
    private static String mTrackName;
    private static TrackObj mTrackObj;
    public static String mTrackPath;
    private static int mTrackUpIndex = 0;
    private static Point mNewGpsTrack = new Point();
    private static String mTrackNumber;
    private static int mCoordFormat = 0;
    public static Handler mMapHandler;
    private static AutoRunnable autoRunnable = new AutoRunnable();
    private static GpxUtils.GpxFileWriter mGpxFileWriter;// 记录GPS信息
    private static List<Point> mTrackRecordList = new ArrayList<Point>();
    private static GraphicsLayer mTrackShowLayer;// 显示巡查轨迹图层
    private static GraphicsLayer mGpsShowLayer;
    public static MainActivity mActivity;
    public static GeometryCollectTool geoCollectTool;
    public static GeometryAnalysisTool geoAnalysisTool;
    private static GraphicsLayer mLocationShowLayer;// 显示坐标定位图层
    private static GraphicsLayer mFavShowLayer;// 显示收藏图层
    private static Geometry curZoomToGeometry;// 当前需要缩放到的图形

    /**
     * 初始化参数
     *
     * @param paramMapViewEx
     * @param mapHandler
     */
    public static void initParams(MainActivity mainActivity, MapView paramMapViewEx, Handler mapHandler) {
        Log.i(TAG, "MapOperate初始化");

        mActivity = mainActivity;
        map = paramMapViewEx;
        mMapHandler = mapHandler;
        mTrackShowLayer = MapOperate.getTrackingLayer();
        mGpsShowLayer = MapOperate.getGpsShowLayer();
        mLocationShowLayer = MapOperate.getLocationShowLayer();
        mFavShowLayer = MapOperate.getFavShowLayer();
    }

    public static boolean IsPrjIsWGS84() {
        return map.getSpatialReference().getID() == SpatialReference.WKID_WGS84;
    }

    public static boolean IsStartTrackRecord() {
        return ((mCurrentTrackTaskId != null) && (mCurrentTrackTaskId.length() > 0));
    }

    /**
     * 自动GPS打点停止
     */
    public static void autoCollectionPause() {
        mMapHandler.removeCallbacks(autoRunnable);
    }

    /**
     * 自动GPS打点开始
     *
     * @param paramInt
     */
    public static void autoCollectionStart(int paramInt) {
        m_nAtuoTimes = paramInt;
        if (mMapHandler == null)
            return;
        mMapHandler.postDelayed(autoRunnable, 10L);
    }

    /**
     * 是否在记录巡查轨迹
     *
     * @return
     */
    public static boolean TrackIsRecording() {
        return mTrackIsRecording;
    }

    /**
     * 开始记录巡查轨迹
     *
     * @return
     */
    public static void TrackStartRecord() {
        mTrackIsRecording = true;
    }

    /**
     * 暂停记录巡查轨迹
     *
     * @return
     */
    public static void TrackPauseRecord() {
        mTrackIsRecording = false;
    }

    /**
     * 初始化巡查
     *
     * @param paramString
     * @param trackID
     */
    public static void TrackInit(String paramString, int trackID,int track_selected) {
        isRegistTrackId = false;
        int len_trackID = ("" + trackID).length();
        String strTrackID = "" + trackID;
        if (len_trackID < 5) {
            int addV = 5 - len_trackID;
            for (int i = 0; i < addV; i++) {
                strTrackID = "0" + strTrackID;
            }
        }



        mTrackIsRecording = true;
        mTrackName = paramString;
        mGpxFileWriter = null;
        mTrackUpIndex = 1;
        mTrackNumber = SysConfig.GpsNumber + MDateUtils.GetCurrentFormatTime("yyyyMMddHHmmss");
        mCurrentTrackTaskId = "RW" + SysConfig.GpsNumber + MDateUtils.GetCurrentFormatTime("yyyyMMddHHmmss");
        mTrackObj = new TrackObj();
        mTrackObj.setHumanID(SysConfig.mHumanInfo.getHumanId());
        mTrackObj.setTaskId(mCurrentTrackTaskId);
        mTrackObj.setTrackId(mTrackNumber);
        mTrackObj.setTime(UtilsTools.GetCurrentTime());
        mTrackObj.setTrackName(mTrackName);
        mTrackObj.setTrackPath(mTrackPath + mTrackName + ".gpx");
        mTrackObj.setIsUp(0);

        mTrackObj.setFirm(track_selected);

        RegistTrackTaskId(mCurrentTrackTaskId);
        try {
            mGpxFileWriter = new GpxUtils.GpxFileWriter(mTrackPath + mTrackName + ".gpx", false);
            mGpxFileWriter.startTrk(paramString);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 记录轨迹
     *
     * @param dLatitude
     * @param dLongitude
     * @param dAltitude
     * @param nTime
     */
    public static void TrackRecord(double dLatitude, double dLongitude, double dAltitude, long nTime) {
        mNewGpsTrack.setX(dLongitude);
        mNewGpsTrack.setY(dLatitude);
        if (mGpxFileWriter == null)
            return;
        try {
            mGpxFileWriter.write(dLatitude, dLongitude, dAltitude, nTime);
            mTrackRecordList.add(new Point(dLongitude, dLatitude));
            if (mTrackRecordList.size() < 4)
                return;
            if (!(isRegistTrackId)) {
                RegistTrackTaskId(mCurrentTrackTaskId);
                return;
            }
            SendTrackInRecording();
            return;
        } catch (IOException localIOException) {
            localIOException.printStackTrace();
        }
    }

    public static void TrackFinishRecord() {
        mTrackIsRecording = false;
        try {
            mGpxFileWriter.stopTrk();
            mGpxFileWriter.close();
            mGpxFileWriter = null;
            if (mTrackRecordList.size() > 0)
                SendTrackInRecording();
            SendTrackStopRecording();
            mCurrentTrackTaskId = null;
            mTrackNumber = null;
            // InitTrackList();
            return;
        } catch (Exception localException) {
            localException.printStackTrace();
        }
    }

    /**
     * 向服务器发送轨迹停止
     */
    public static void SendTrackStopRecording() {

    }

    public static void SendTrackInRecording() {
        // if ((smsManager == null) || (!(SysConfig.IsEnableSms)) ||
        // (mTrackRecordList.size() <= 0))
        // return;
        // new AsyncTask()
        // {
        // protected Void doInBackground(Void[] paramArrayOfVoid)
        // {
        // MapOperate.smsManager.sendTrackMsg(MapOperate.mCurrentTrackTaskId,
        // MapOperate.mTrackNumber, MapOperate.mTrackRecordList,
        // MapOperate.mTrackUpIndex);
        // MapOperate.mTrackUpIndex += MapOperate.mTrackRecordList.size();
        // MapOperate.mTrackRecordList.clear();
        // return null;
        // }
        // }
        // .execute(new Void[0]);
    }

    public static String GetTrackPath() {
        return mTrackPath;
    }

    public static String GetTrackName() {
        return mTrackName;
    }

    public static boolean IsTrackWorking() {
        return (mGpxFileWriter != null);
    }

    public static Geometry getCurZoomToGeometry() {
        return curZoomToGeometry;
    }

    public static void setCurZoomToGeometry(Geometry curZoomToGeometry) {
        MapOperate.curZoomToGeometry = curZoomToGeometry;
        mMapHandler.sendEmptyMessage(9999);
    }

    public static void zoomToGeometry() {
        if (MapOperate.curZoomToGeometry != null) {
            map.setExtent(MapOperate.curZoomToGeometry, 500, true);
        }
    }

    /**
     * 格式化坐标
     *
     * @param coord
     * @return
     */
    public static String FormatCoord(double coord) {
        switch (mCoordFormat) {
            case 0:
                return toDeg(coord);
            case 1:
                return toDegMin(coord);
            default:
                return toDegMinSec(coord);
        }

    }

    /**
     * 坐标值转换到度
     *
     * @param coord
     * @return
     */
    public static String toDeg(double coord) {
        double d = Math.abs(coord);
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append(d);
        if ((((localStringBuilder.length() <= 0) || (localStringBuilder.charAt(-1 + localStringBuilder.length()) != '0'))) && (localStringBuilder.length() > 11))
            localStringBuilder.setLength(11);
        localStringBuilder.append("°");
        return localStringBuilder.toString();
    }

    /**
     * 坐标值转换到度分
     *
     * @param coord
     * @return
     */
    public static String toDegMin(double coord) {
        double d = Math.abs(coord);
        StringBuilder localStringBuilder = new StringBuilder();
        int i = (int) Math.floor(d);
        float f = 1000000.0F * 60.0F * (float) (d - i) / 1000000.0F;
        localStringBuilder.append(i).append("°").append(f);
        if (localStringBuilder.length() > 11)
            localStringBuilder.setLength(11);
        localStringBuilder.append("'");
        return localStringBuilder.toString();
    }

    /**
     * 坐标值转换到度分秒
     *
     * @param coord
     * @return
     */
    public static String toDegMinSec(double coord) {
        double d = Math.abs(coord);
        StringBuilder localStringBuilder = new StringBuilder();
        int i = (int) Math.floor(d);
        int j = (int) Math.floor(60.0D * (d - i));
        float f = (int) (100.0F * (float) (60.0D * (60.0D * (d - i) - j))) / 100.0F;
        localStringBuilder.append(i).append("°").append(j).append("'").append(f).append("\"");
        return localStringBuilder.toString();
    }

    public static void RegistTrackTaskId(String paramString) {
        // if ((smsManager != null) && (SysConfig.IsEnableSms))
        // {
        // if ((SysConfig.GpsNumber == null) ||
        // (SysConfig.mPeopleInfo.getPeoId() == null))
        // break label48;
        // smsManager.sendRegistTrackTaskId(SysConfig.GpsNumber,
        // SysConfig.mPeopleInfo.getPeoId(), paramString);
        isRegistTrackId = true;
        // }
        // return;
        // label48: MLog.e(TAG, "注册轨迹任务iD错误===信息不全--");
    }

    public static String getPrjName() {
        return prjName;
    }

    public static void setPrjName(String prjName) {
        MapOperate.prjName = prjName;
    }

    /**
     * 创建地块标识
     *
     * @return
     */
    public static String CreateNewLandId() {
        return SysConfig.GpsNumber + MDateUtils.GetCurrentFormatTime("yyyyMMddHHmmss");
    }

    /**
     * 创建案件标识
     *
     * @return
     */
    public static String CreateNewCaseId() {
        return "CASE" + GenerateSequenceUtil.generateSequenceNo();
    }

    /**
     * 获取当前绘制地块的周长（添加地块）
     *
     * @return
     */
    public static double GetLength() {
        return GeometryEngine.geodesicLength(curGeometry.copy(), map.getSpatialReference(), (LinearUnit) Unit.create(LinearUnit.Code.METER));
    }

    /**
     * 获取当前绘制地块的面积（添加地块）
     *
     * @return
     */
    public static double GetArea() {
        if (curGeometry != null) {
            return GeometryEngine.geodesicArea(curGeometry.copy(), map.getSpatialReference(), (AreaUnit) Unit.create(AreaUnit.Code.SQUARE_METER));
        } else {
            return 0D;
        }

    }

    /**
     * 获取传递过来的地块面积
     *
     * @param p_geo
     * @return
     */
    public static double GetArea(Geometry p_geo) {
        if (p_geo != null) {
            return GeometryEngine.geodesicArea(p_geo.copy(), map.getSpatialReference(), (AreaUnit) Unit.create(AreaUnit.Code.SQUARE_METER));
        } else {
            return 0D;
        }

    }

    /**
     * 清空公共GraphicsLayer
     */
    public static void clearPublicGraphicsLayer() {
        if (grasLayer != null) {
            grasLayer.removeAll();
        }
    }

    public static GraphicsLayer getPublicGraphicsLayer() {
        if (grasLayer == null) {
            grasLayer = new GraphicsLayer();
            map.addLayer(grasLayer);
        }
        return grasLayer;
    }

    /**
     * 添加并定位到Graphic
     *
     * @param gra
     */
    public static void addAndZoomToGraphic(Graphic gra) {
        GraphicsLayer mGraphicsLayer = getPublicGraphicsLayer();
        mGraphicsLayer.removeAll();
        mGraphicsLayer.addGraphic(gra);
        map.setExtent(gra.getGeometry());
    }

    /**
     * 添加并定位到Geometry
     *
     * @param mGeometry
     */
    public static void addAndZoomToGeometry(Geometry mGeometry, boolean showPoint) {
        if (mGeometry instanceof Point) {
            addAndZoomToPoint((Point) mGeometry);
        } else if (mGeometry instanceof Polyline) {
            addAndZoomToPolyline((Polyline) mGeometry, showPoint);
        } else if (mGeometry instanceof Polygon) {
            addAndZoomToPolygon((Polygon) mGeometry, showPoint);
        }

    }

    /**
     * 添加并定位到Point
     *
     * @param mPoint
     */
    public static void addAndZoomToPoint(Point mPoint) {

        GraphicsLayer mGraphicsLayer = getPublicGraphicsLayer();
        mGraphicsLayer.removeAll();
        SimpleMarkerSymbol mMarkerSymbol = new SimpleMarkerSymbol(Color.BLUE, 10, STYLE.DIAMOND);
        mGraphicsLayer.addGraphic(new Graphic(mPoint, mMarkerSymbol, 100));
        map.centerAt(mPoint, false);
    }

    /**
     * 添加并定位到Polyline
     *
     * @param mPolyline
     */
    public static void addAndZoomToPolyline(Polyline mPolyline, boolean showPoint) {
        GraphicsLayer mGraphicsLayer = getPublicGraphicsLayer();
        mGraphicsLayer.removeAll();
        SimpleMarkerSymbol mMarkerSymbol = new SimpleMarkerSymbol(Color.BLUE, 10, STYLE.DIAMOND);
        SimpleLineSymbol mLineSymbol = new SimpleLineSymbol(Color.YELLOW, 3);
        Graphic polylineGra = null;
        if (showPoint) {
            Polyline mLine = new Polyline();
            for (int i = 0; i < mPolyline.getPointCount(); i++) {
                mGraphicsLayer.addGraphic(new Graphic(mPolyline.getPoint(i), mMarkerSymbol, 100));
                if (i == 0) {
                    mLine.startPath(mPolyline.getPoint(i));

                } else {
                    mLine.lineTo(mPolyline.getPoint(i));
                }
            }
            polylineGra = new Graphic(mLine, mLineSymbol);
        } else {
            polylineGra = new Graphic(mPolyline, mLineSymbol);
        }
        mGraphicsLayer.addGraphic(polylineGra);
        map.setExtent(polylineGra.getGeometry(), 500);

    }

    /**
     * 添加并定位到Polygon
     *
     * @param mPolygon
     */
    public static void addAndZoomToPolygon(Polygon mPolygon, boolean showPoint) {

        GraphicsLayer mGraphicsLayer = getPublicGraphicsLayer();
        mGraphicsLayer.removeAll();
        SimpleFillSymbol mFillSymbol = new SimpleFillSymbol(Color.TRANSPARENT);
        mFillSymbol.setOutline(new SimpleLineSymbol(Color.CYAN, 2));
        Graphic polygonGra = new Graphic(mPolygon, mFillSymbol);
        mGraphicsLayer.addGraphic(polygonGra);
        map.setExtent(polygonGra.getGeometry(), 500);
    }

    /**
     * 添加并定位到Geometry(Json)
     *
     * @param geometryJson
     */
    public static void addAndZoomToGeometryJson(String geometryJson) {

        JsonFactory jsonFactory = new JsonFactory();
        JsonParser jsonParser;
        try {
            jsonParser = jsonFactory.createJsonParser(geometryJson);
            MapGeometry mapGeo = GeometryEngine.jsonToGeometry(jsonParser);
            Geometry geo = mapGeo.getGeometry();
            GraphicsLayer mGraphicsLayer = getPublicGraphicsLayer();
            mGraphicsLayer.removeAll();
            SimpleMarkerSymbol mMarkerSymbol = new SimpleMarkerSymbol(Color.BLUE, 10, STYLE.DIAMOND);
            SimpleLineSymbol mLineSymbol = new SimpleLineSymbol(Color.CYAN, 3);
            SimpleFillSymbol mFillSymbol = new SimpleFillSymbol(Color.TRANSPARENT);
            mFillSymbol.setOutline(new SimpleLineSymbol(Color.TRANSPARENT, 0));
            Polygon mPolygon = ((Polygon) geo);
            Polyline mLine = new Polyline();
            for (int i = 0; i < mPolygon.getPointCount(); i++) {
                mGraphicsLayer.addGraphic(new Graphic(mPolygon.getPoint(i), mMarkerSymbol, 100));
                if (i == 0) {
                    mLine.startPath(mPolygon.getPoint(i));

                } else {
                    mLine.lineTo(mPolygon.getPoint(i));
                }
            }
            mGraphicsLayer.addGraphic(new Graphic(mLine, mLineSymbol));
            Graphic polygonGra = new Graphic(mPolygon, mFillSymbol);
            mGraphicsLayer.addGraphic(polygonGra);
            map.setExtent(polygonGra.getGeometry(), 500);
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 编辑案件图形
     *
     * @param caseObj
     */
    public static void editCaseGeometry(Case caseObj) {
        MapOperate.mActivity.collectGeometry(caseObj, caseObj.getCaseType());
    }

    public static TrackObj GetTrackObj() {
        return mTrackObj;
    }

    public static Point GetNewTrackPoint() {
        return mNewGpsTrack;
    }

    /**
     * 获取巡查路线显示图层
     *
     * @return
     */
    public static GraphicsLayer getTrackingLayer() {
        if (mTrackShowLayer == null) {
            mTrackShowLayer = new GraphicsLayer();
            map.addLayer(mTrackShowLayer);
        }
        return mTrackShowLayer;
    }

    /**
     * 清空公共GraphicsLayer
     */
    public static void clearTrackingLayer() {
        if (mTrackShowLayer != null) {
            mTrackShowLayer.removeAll();
        }
    }

    /**
     * 获取GPS显示图层
     *
     * @return
     */
    public static GraphicsLayer getGpsShowLayer() {
        if (mGpsShowLayer == null) {
            mGpsShowLayer = new GraphicsLayer();
            map.addLayer(mGpsShowLayer);
        }
        return mGpsShowLayer;
    }

    public static GraphicsLayer getLocationShowLayer() {
        if (mLocationShowLayer == null) {
            mLocationShowLayer = new GraphicsLayer();
            map.addLayer(mLocationShowLayer);
        }
        return mLocationShowLayer;
    }

    public static GraphicsLayer getFavShowLayer() {
        if (mFavShowLayer == null) {
            mFavShowLayer = new GraphicsLayer();
            map.addLayer(mFavShowLayer);
        }
        return mFavShowLayer;
    }

    /**
     * 清空收藏GraphicsLayer
     */
    public static void clearFavShowLayer() {
        if (mFavShowLayer != null) {
            mFavShowLayer.removeAll();
        }
    }

    public static void mapShowTrackPaths(List<TrackObj> trackObjs) {
        PictureMarkerSymbol trackStartSymbol = new PictureMarkerSymbol(map.getResources().getDrawable(R.drawable.track_start));
        trackStartSymbol.setOffsetX(8f);
        trackStartSymbol.setOffsetY(17f);
        PictureMarkerSymbol trackEndSymbol = new PictureMarkerSymbol(map.getResources().getDrawable(R.drawable.track_end));
        trackEndSymbol.setOffsetX(8f);
        trackEndSymbol.setOffsetY(17f);
        SimpleMarkerSymbol mMarkerSymbol = new SimpleMarkerSymbol(Color.BLUE, 10, STYLE.DIAMOND);
        SimpleLineSymbol mLineSymbol = new SimpleLineSymbol(Color.YELLOW, 3);
        Iterator<TrackObj> localIterator1 = trackObjs.iterator();
        GraphicsLayer mGraphicsLayer = getTrackingLayer();
        mGraphicsLayer.removeAll();
        List<Point> allPoints = new ArrayList<Point>();
        while (localIterator1.hasNext()) {
            TrackObj curTrackObj = localIterator1.next();
            List<Point> trackPoints = curTrackObj.getAllTrackPoints();
            allPoints.addAll(trackPoints);
            Polyline mLine = new Polyline();
            for (int i = 0; i < trackPoints.size(); i++) {

                if (i == 0) {
                    mGraphicsLayer.addGraphic(new Graphic(trackPoints.get(i), trackStartSymbol, 100));
                    mLine.startPath(trackPoints.get(i));

                } else if (i == trackPoints.size() - 1) {
                    mGraphicsLayer.addGraphic(new Graphic(trackPoints.get(i), trackEndSymbol, 100));
                    mLine.lineTo(trackPoints.get(i));
                } else {
                    mGraphicsLayer.addGraphic(new Graphic(trackPoints.get(i), mMarkerSymbol, 100));
                    mLine.lineTo(trackPoints.get(i));
                }
            }
            mGraphicsLayer.addGraphic(new Graphic(mLine, mLineSymbol));
        }
        // 缩放到合适视野
        Envelope envelope = getEnvelopeByPoints(allPoints);
        map.setExtent(envelope);
    }

    public static Envelope getEnvelopeByPoints(List<Point> points) {
        double xmin = -1;
        double ymin = -1;
        double xmax = -1;
        double ymax = -1;
        Iterator<Point> localIterator = points.iterator();
        while (localIterator.hasNext()) {
            Point localPoint = localIterator.next();
            if (xmin == -1) {
                xmin = xmax = localPoint.getX();
                ymin = ymax = localPoint.getY();
            }
            if (localPoint.getX() > xmax) {
                xmax = localPoint.getX();
            }
            if (localPoint.getX() < xmin) {
                xmin = localPoint.getX();
            }
            if (localPoint.getY() > ymax) {
                ymax = localPoint.getY();
            }
            if (localPoint.getY() < ymin) {
                ymin = localPoint.getY();
            }
        }
        return new Envelope(xmin, ymin, xmax, ymax);
    }

    public static double GetLength(Point p1, Point p2) {
        double d = 0.0D;
        d = GeometryEngine.geodesicDistance(p1, p2, SpatialReference.create(SpatialReference.WKID_WGS84), (LinearUnit) Unit.create(LinearUnit.Code.METER));
        return d;

    }

    private static class AutoRunnable implements Runnable {
        public void run() {
            MapOperate.mMapHandler.sendEmptyMessage(MapOperate.SET_GPSDATA);
            MapOperate.mMapHandler.postDelayed(MapOperate.autoRunnable, 1000 * MapOperate.m_nAtuoTimes);
        }
    }
}
