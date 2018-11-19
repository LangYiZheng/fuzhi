package com.guyu.android.gis.localservice;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import com.blankj.utilcode.util.ToastUtils;
import com.guyu.android.R;
import com.esri.android.map.MapView;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polyline;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.SimpleLineSymbol;
import com.guyu.android.gis.activity.MainActivity;
import com.guyu.android.gis.activity.MiddleActivity;
import com.guyu.android.gis.opt.MapOperate;
import com.guyu.android.gis.opt.TrackLayerOpt;
import com.guyu.android.utils.DebugUtils;

public class TrackingShowService extends Service implements GpsListener {

    public static final int ONGOING_NOTIFICATION = 2;
    public static final int TRACK_OPT_0_START = 0; // 开始
    public static final int TRACK_OPT_1_PAUSE = 1; // 暂停
    public static final int TRACK_OPT_2_RESUME = 2; // 恢复
    public static final int TRACK_OPT_3_STOP = -1; // 停止
    public static final String TRACK_OPT = "TRACK_OPT";

    public Handler mHandler = null;
    private MapView mMapView = null; // 地图
    private boolean m_bEnable = false;
    private List<Point> mAllTrackPoint2ds = new ArrayList<Point>();
    //    private int oldSize = 0;
    private static SimpleLineSymbol lineSymbol = new SimpleLineSymbol(Color.YELLOW, 3);
    //    private int trackGraID = -1;

    public static int state = TRACK_OPT_3_STOP;

    private ArrayList<Integer> pauses = null;

    private Runnable runnable =
            new Runnable() {
                public void run() {
                    TrackingShowService.this.refresh();
                }
            };

    @Override
    public void onCreate() {
        this.mMapView = MapOperate.map;
        if (this.mMapView != null) {
            this.mAllTrackPoint2ds.clear();
            MainActivity.SetGPSChangedListener(this);
        }
        this.mHandler = new Handler(Looper.myLooper());
        super.onCreate();
    }

    private void initPoint() {
        this.mAllTrackPoint2ds.clear();
        pauses = null;
        //        oldSize = 0;
        //    trackGraID = -1;
    }

    /** GPS 位置发生改变 */
    @Override
    public void GpsChangedListener(Point paramPoint2D) {
        if (this.m_bEnable) {
            this.mAllTrackPoint2ds.add(paramPoint2D);
            MapOperate.map.post(this.runnable);
        }
    }

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int onStartCommand(Intent paramIntent, int paramInt1, int paramInt2) {

        if (state == 1) {
            initPoint();
        }

        if (paramIntent != null) {
            int i = paramIntent.getIntExtra("opt", 0);
            if (i == 0) {
                startDraw();
                startForeActivity("开始记录轨迹");
            } else if (i == 1) {
                pauseDraw();
                startForeActivity("停止记录轨迹");
                this.mHandler.postDelayed(
                        new Runnable() {
                            public void run() {
                                TrackingShowService.this.stopSelf();
                            }
                        },
                        2000L);
            } else {
                MapOperate.getTrackingLayer().removeAll();
            }
        }
        return super.onStartCommand(paramIntent, paramInt1, paramInt2);
    }

    @SuppressWarnings("deprecation")
    private void startForeActivity(String paramString) {
        Notification localNotification =
                new Notification(2130837646, paramString, System.currentTimeMillis());
        Intent localIntent = new Intent(this, MiddleActivity.class);
        localIntent.putExtra("CODE", 3);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            localNotification =
                    new Notification.Builder(this)
                            .setAutoCancel(true)
                            .setContentText(paramString)
                            .setContentIntent(PendingIntent.getActivity(this, 0, localIntent, 0))
                            .setSmallIcon(R.drawable.icon)
                            .setWhen(System.currentTimeMillis())
                            .setContentTitle("" + R.string.app_name)
                            .build();
        } else {
            localNotification = new Notification();
            localNotification.icon = R.drawable.icon;
            try {
                Method deprecatedMethod =
                        localNotification
                                .getClass()
                                .getMethod(
                                        "setLatestEventInfo",
                                        Context.class,
                                        CharSequence.class,
                                        CharSequence.class,
                                        PendingIntent.class);
                deprecatedMethod.invoke(
                        localNotification,
                        this,
                        "" + R.string.app_name,
                        paramString,
                        null,
                        PendingIntent.getActivity(this, 0, localIntent, 0));
            } catch (NoSuchMethodException
                    | IllegalAccessException
                    | IllegalArgumentException
                    | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        //		localNotification.setLatestEventInfo(this, "" + R.string.app_name, paramString,
        // PendingIntent.getActivity(this, 0, localIntent, 0));
        startForeground(2, localNotification);
    }

    /** 开始绘制 */
    public void startDraw() {
        this.m_bEnable = true;
    }

    /** 暂停绘制 */
    public void pauseDraw() {
        this.m_bEnable = false;
        if ((this.mAllTrackPoint2ds == null) || (this.mAllTrackPoint2ds.size() <= 1)) {
            return;
        } else {
            if (pauses == null) {
                pauses = new ArrayList<>();
            }
            pauses.add(mAllTrackPoint2ds.size() - 1);
        }

        //        TrackLayerOpt.DrawTrackToLayer(this.mAllTrackPoint2ds);
        //        Point localPoint2D = this.mAllTrackPoint2ds.get(-1 + this.mAllTrackPoint2ds.size());
        //        this.mAllTrackPoint2ds.clear();
        //        this.mAllTrackPoint2ds.add(localPoint2D);
    }

    public void onDestroy() {
        this.m_bEnable = false;
        MainActivity.SetGPSChangedListener(null);
        stopFore();
        initPoint();
        super.onDestroy();
    }

    private void stopFore() {
        stopForeground(true);
    }

    public void refreshTrackLayer() {

        if (mAllTrackPoint2ds.size() > 1) {
            if (pauses == null) {
                Polyline mLine = new Polyline();
                mLine.startPath(mAllTrackPoint2ds.get(0));
                for (int i = 1; i < mAllTrackPoint2ds.size(); i++) {
                    mLine.lineTo(mAllTrackPoint2ds.get(i));
                }

                Graphic graphic = new Graphic(mLine, lineSymbol);
                MapOperate.getTrackingLayer().removeAll();
                MapOperate.getTrackingLayer().addGraphic(graphic);

            } else {
                ArrayList<Polyline> lines = new ArrayList<>();
                //                Polyline mLine = new Polyline();

                for (int i = 0; i < pauses.size(); i++) {
                    int j = pauses.get(i);
                    if (i == 0) {
                        lines.add(getLine(0, j));
                    } else {
                        lines.add(getLine(pauses.get(i - 1) + 1,j ));
                    }

                    if (i == pauses.size() - 1) {
                        lines.add(getLine(j + 1, mAllTrackPoint2ds.size() - 1));
                    }
                }
                Graphic[] graphics = new Graphic[lines.size()];
                for (int i = 0; i < lines.size(); i++) {
                    graphics[i] = new Graphic(lines.get(i), lineSymbol);
                }
                MapOperate.getTrackingLayer().removeAll();
                MapOperate.getTrackingLayer().addGraphics(graphics);
            }
        }
    }

    private Polyline getLine(int start, int stop) {
        Polyline mLine = new Polyline();
        mLine.startPath(mAllTrackPoint2ds.get(start));
        if(start!=stop){
            for (int i = start; i <= stop; i++) {
                mLine.lineTo(mAllTrackPoint2ds.get(i));
            }
        }

        return mLine;
    }

    private void refresh() {
        //        if (this.mAllTrackPoint2ds.size() > this.oldSize) {
        Log.i("绘制轨迹", this.mAllTrackPoint2ds.size() + "个点");
        refreshTrackLayer();
        //            this.oldSize = this.mAllTrackPoint2ds.size();
        //            if (this.oldSize >= 100) {
        //                // TrackLayerOpt.DrawTrackToLayer(this.mAllTrackPoint2ds);
        //                Point localPoint2D = this.mAllTrackPoint2ds.get(this.mAllTrackPoint2ds.size()
        // - 1);
        //                this.mAllTrackPoint2ds.clear();
        //                this.mAllTrackPoint2ds.add(localPoint2D);
        //                this.oldSize = 1;
        //            }
        //        }
    }
}
