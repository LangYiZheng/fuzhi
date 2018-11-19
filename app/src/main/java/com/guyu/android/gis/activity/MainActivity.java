/**
 * 地图视图
 */
package com.guyu.android.gis.activity;

import java.io.File;
import java.io.FileNotFoundException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.PointF;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.AppCompatDelegate;
import android.text.format.Time;
import android.util.DisplayMetrics;
import android.util.FloatMath;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.bairuitech.callcenter.HallActivity;
import com.bairuitech.meeting.MeetingActivity;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.esri.android.map.FeatureLayer;
import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.Layer;
import com.esri.android.map.MapView;
import com.esri.android.map.TiledLayer;
import com.esri.android.map.ags.ArcGISDynamicMapServiceLayer;
import com.esri.android.map.ags.ArcGISFeatureLayer;
import com.esri.android.map.ags.ArcGISLocalTiledLayer;
import com.esri.android.map.ags.ArcGISTiledMapServiceLayer;
import com.esri.android.map.event.OnPinchListener;
import com.esri.android.map.event.OnStatusChangedListener;
import com.esri.android.map.event.OnZoomListener;
import com.esri.android.runtime.ArcGISRuntime;
import com.esri.core.ags.LayerServiceInfo;
import com.esri.core.geodatabase.Geodatabase;
import com.esri.core.geodatabase.GeodatabaseFeatureTable;
import com.esri.core.geometry.Envelope;
import com.esri.core.geometry.LinearUnit;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.SpatialReference;
import com.esri.core.geometry.Unit;
import com.esri.core.map.DrawingInfo;
import com.esri.core.map.FeatureSet;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.PictureMarkerSymbol;
import com.esri.core.symbol.SimpleFillSymbol;
import com.esri.core.symbol.SimpleLineSymbol;
import com.esri.core.symbol.SimpleMarkerSymbol;
import com.esri.core.symbol.SimpleMarkerSymbol.STYLE;
import com.guyu.android.bluetooth.BluetoothConnectThread;
import com.guyu.android.bluetooth.BluetoothSerialService;
import com.guyu.android.bluetooth.ByteQueue;
import com.guyu.android.database.task.CaseDBMExternal;
import com.guyu.android.database.task.TrackDBMExternal;
import com.guyu.android.gis.adapter.LayersAdapter;
import com.guyu.android.gis.app.GisQueryApplication;
import com.guyu.android.gis.app.NaviGPS;
import com.guyu.android.gis.common.Case;
import com.guyu.android.gis.config.LayerConfig;
import com.guyu.android.gis.config.MapBackgroundConfig;
import com.guyu.android.gis.config.QuickToggleLayerConfig;
import com.guyu.android.gis.localservice.GpsListener;
import com.guyu.android.gis.localservice.TaskUpdateService;
import com.guyu.android.gis.localservice.TrackingShowService;
import com.guyu.android.gis.maptools.CompassTool;
import com.guyu.android.gis.maptools.GeometryAnalysisTool;
import com.guyu.android.gis.maptools.GeometryCollectTool;
import com.guyu.android.gis.maptools.IdentifyTool;
import com.guyu.android.gis.maptools.MeasuringTool;
import com.guyu.android.gis.opt.MapOperate;
import com.guyu.android.gis.opt.TrackLayerOpt;
import com.guyu.android.utils.BitmapUtils;
import com.guyu.android.utils.CameraFilePathUtils;
import com.guyu.android.utils.HnTimer;
import com.guyu.android.utils.MDateUtils;
import com.guyu.android.utils.MapUtils;
import com.guyu.android.utils.MathUtils;
import com.guyu.android.utils.SysConfig;
import com.guyu.android.utils.Utility;
import com.guyu.android.utils.UtilsTools;
import com.guyu.android.view.ActionItem;
import com.guyu.android.view.BufferSeekbarOpt;
import com.guyu.android.view.QuickActions;
import com.guyu.android.R;

@SuppressLint("HandlerLeak")
public class MainActivity extends FragmentActivity implements
        NaviGPS.MGpsListener {
    static String TAG = MainActivity.class.getSimpleName();
    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    private static final int DIALOG_LAYERS_VISIBILITY = 1;
    public static final int DIALOG_TURBINETYPES = 2;
    public static final int DIALOG_BLUETOOTH_DEVICES = 3;
    public static final int DIALOG_ENABLED_BLUETOOTH = 4;

    private static final int MAP_NORMAL_STATE = 0;
    private static final int MAP_COLLECT_STATE = 1;
    private static final int MAP_MASURE_STATE = 2;
    private static final int MAP_QUERY_STATE = 3;
    private int mCurrentMapState = 0;

    public static final String TOAST = "toast";

    private FrameLayout mainlayout;
    private RelativeLayout mTopTitle = null;
    private LinearLayout mTopCollect = null;
    // private RelativeLayout mGpsInfoLayout = null;
    // private RelativeLayout mGuideInfoLayout = null;
    // private RelativeLayout mHideStateLayout = null;

    private boolean isUseGps = true;

    public Animation animIn = null;
    public Animation animOut = null;

    private MapView map;
    private IdentifyTool identifyTool;
    private MeasuringTool measuringTool;
    private CompassTool compassTool;
    private GeometryCollectTool geoCollectTool;
    private GeometryAnalysisTool geoAnalysisTool;
    private BufferSeekbarOpt bufferSeekbarOpt;

    public GraphicsLayer gpsShowLayer;
    public GraphicsLayer trackShowLayer;
    private LinearLayout mVgTrackToolbar = null;
    private Envelope initialExtent;
    private Envelope fullExtent;
    private ArrayList<Layer> visibleLayers;

    // 蓝牙相关开始
    static volatile BluetoothSerialService bluetoothSerialService;
    private BluetoothAdapter bluetoothAdapter;

    private ArrayAdapter<String> newDevicesAdapter;
    private ByteQueue byteQueue;
    private String connectedDeviceName;
    private String connectedDeviceAddress;
    public static boolean blueToothConnected = false;
    // UUID协议
    public static final String SPP_UUID = "00001101-0000-1000-8000-00805F9B34FB";
    // 蓝牙连接
    public static BluetoothSocket btSocket;
    public BluetoothConnectThread bluetoothConnectThread;
    // 蓝牙相关结束
    private static List<GpsListener> mGpsListeners = new ArrayList<GpsListener>();

    private ToggleButton gpsToggleButton;
    private ViewGroup laserRangeLayout;
    private Animation fadeinAnimation;
    private Animation fadeoutAnimation;

    private QuickActions quickAction1 = null;
    private QuickActions quickAction4 = null;
    private QuickActions quickAciton_tools = null;
    private QuickActions quickAciton_landManager = null;
    private QuickActions quickAciton_illegalClues = null;
    private QuickActions quickAciton_sms = null;
    private LinearLayout mVgMesureToolbar = null;
    private ImageButton zoomfullButton;
    private ImageButton zoominButton;
    private ImageButton zoomoutButton;

    private ImageButton mLandDetailsBackBtn = null;
    private ImageButton mCameraButton = null;
    private ImageButton mLocationButton = null;
    private ImageButton imgScreen = null;
    // 巡查
    private ImageButton mTrackStartBtn = null;
    private ImageButton mTrackSettingBtn = null;
    private Button btnTrackSave;
    private Button btnTrackStop;
    // private ImageButton mTrackImportBtn = null;
    private boolean m_bEnableRecordTrack = true;
    private TextView mTrackInfoTextView = null;
    private TextView mGpsInfoTextView = null;

    private Handler mHandler = null;

    public static int nRecordCount = 0;
    private Matrix matrix = new Matrix();

    private ToggleButton compassButton;
    private ProgressBar progressBar;

    boolean firstLocation = true;
    private PictureMarkerSymbol gpsMarkerSymbol;
    private int gpsID = -1;
    private Graphic gpsGra;
    private Bitmap screenBitmap;
    private boolean getScreenBitmap;

    public Graphic getGpsGra() {
        return gpsGra;
    }

    private SensorManager sensorManager;
    // 4.3以上需要两个Sensor
    private Sensor aSensor;
    private Sensor mSensor;
    // 4.3以下
    private Sensor orientationSensor;
    float[] accelerometerValues = new float[3];
    float[] magneticFieldValues = new float[3];

    int loadedCount = 0;
    boolean maploaded;
    int selectedID = -1;
    private Dialog dialog;
    private String m_CurrentCameraPhoto;
    private int optCode = 0;// 操作码

    private int m_nScreenHeight = 0;
    private int m_nScreenWidth = 0;

    private int track_selected = 0;

    private Graphic locationGraphic;
    public GraphicsLayer locationLayer;
    private int locationID = -1;
    private PictureMarkerSymbol locationSymbol;

    private static final float[] zoom = {250000, 100000, 50000, 20000, 10000, 5000,
            2000, 1000, 500, 200, 100, 50, 25, 10, 1};
    private static int zoomIndex = 0;

    private static MainActivity mainActivity;

    public static MainActivity getInstance() {
        return mainActivity;
    }

    // 写一个广播的内部类，当收到动作时，结束activity
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            unregisterReceiver(this); // 这句话必须要写要不会报错，不写虽然能关闭，会报一堆错
            ((Activity) context).finish();
        }
    };

    /**
     * Method onAttachedToWindow.
     *
     * @see android . view. Window$Callback onAttachedToWindow()
     */
    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Window window = getWindow();
        window.setFormat(PixelFormat.RGBA_8888);
        window.addFlags(WindowManager.LayoutParams.FLAG_DITHER);
    }

    public Bitmap getScreenBitmap() {
        if (screenBitmap != null && !screenBitmap.isRecycled()) {
            screenBitmap.recycle();
        }
        return captureScreenWindow(map);
    }

    public Bitmap captureScreenWindow(MapView v) {
        v.clearFocus();
        v.setPressed(false);

        //能画缓存就返回false
        boolean willNotCache = v.willNotCacheDrawing();
        v.setWillNotCacheDrawing(false);
        int color = v.getDrawingCacheBackgroundColor();
        v.setDrawingCacheBackgroundColor(0);
        if (color != 0) {
            v.destroyDrawingCache();
        }
        v.buildDrawingCache();
        Bitmap cacheBitmap = null;
        while (cacheBitmap == null) {
            cacheBitmap = v.getDrawingMapCache(0, 0, v.getWidth(), v.getHeight());
        }
        Bitmap bitmap = Bitmap.createBitmap(cacheBitmap);
        // Restore the view
        v.destroyDrawingCache();
        v.setWillNotCacheDrawing(willNotCache);
        v.setDrawingCacheBackgroundColor(color);
        cacheBitmap.recycle();
        return bitmap;
    }

    /**
     * Method onCreate.
     *
     * @param savedInstanceState Bundle
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        ArcGISRuntime.setClientId("uK0DxqYT0om1UXa9");
        super.onCreate(savedInstanceState);
        IntentFilter filter = new IntentFilter();
        filter.addAction("finish");
        registerReceiver(broadcastReceiver, filter);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_main);
        Intent it = getIntent();

        if (it != null && it.getBooleanExtra("getScreenBitmap", false)) {
            getScreenBitmap = it.getBooleanExtra("getScreenBitmap", false);
            it.putExtra("getScreenBitmap", false);

        }
        mainActivity = this;
        // 启用蓝牙配置再初始化它
        if (GisQueryApplication.getApp().getProjectconfig().getUsebluetoothcollect()) {
            registerBluetoothReceiver();
        }
        mainlayout = (FrameLayout) findViewById(R.id.mainlayout);

        mTrackInfoTextView = ((TextView) findViewById(R.id.txtFold));
        mGpsInfoTextView = ((TextView) findViewById(R.id.tvGpsInfo));
        SpatialReference sr = GisQueryApplication.getApp().getProjectconfig().getSpatialreference();
        // 有坐标系设置并且使用初始化坐标系设置
        if (GisQueryApplication.getApp().getProjectconfig().getSpatialreference() != null
                && GisQueryApplication.getApp().getProjectconfig().getUseinitialspatialreference()) {
            map = new MapView(MainActivity.this, sr,
                    GisQueryApplication.getApp().getProjectconfig().getInitialextent());
        } else {
            map = new MapView(MainActivity.this);
        }

        MapBackgroundConfig mapbackgroundconfig = GisQueryApplication.getApp().getProjectconfig()
                .getMapbackgroundconfig();
        map.setMapBackground(
                Color.parseColor(mapbackgroundconfig.getBkColor()),
                Color.parseColor(mapbackgroundconfig.getGridColor()),
                mapbackgroundconfig.getGridSize(),
                mapbackgroundconfig.getGridLineSize());

        TextView appTitle = (TextView) findViewById(R.id.app_title);
        appTitle.setText(GisQueryApplication.getApp().getProjectconfig().getApptitle());

        this.mHandler = new MapHandler(Looper.myLooper());

        createLayers();
        mainlayout.addView(map, 0);


        findViewById(R.id.btnHuaTu).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                collectGeometry(null, "huatu");


            }
        });
    }

    private void registerBluetoothReceiver() {
        // Register for broadcasts when a device is discovered
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        initBluetoothSerialService();

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        filter.addAction(BluetoothDevice.EXTRA_BOND_STATE);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        this.registerReceiver(bluetoothReceiver, filter);
    }

    private void initBluetoothSerialService() {
        Log.v(TAG, "initBluetoothSerialService()");
        if (bluetoothSerialService != null)
            return;
        byteQueue = new ByteQueue(1024);
        bluetoothSerialService = new BluetoothSerialService(MainActivity.this,
                bluetoothHandler, byteQueue);
    }

    // BluetoothService信息的处理程序
    private final Handler bluetoothHandler = new Handler() {
        @SuppressWarnings("deprecation")
        @Override
        public void handleMessage(Message msg) {
            ImageButton imgbtnBlueToothCollect = (ImageButton) MainActivity.this
                    .findViewById(R.id.imgBlueToothCollect);
            switch (msg.what) {
                case GisQueryApplication.BLUETOOTH_MESSAGE_STATE_CHANGE:

                    switch (msg.arg1) {
                        case BluetoothSerialService.STATE_STARTED:
                            ToastUtils.showLong(getResources()
                                    .getString(R.string.connection_started));
                            break;
                        case BluetoothSerialService.STATE_CONNECTED:
                            if (progressBar != null)
                                progressBar.setVisibility(View.GONE);

                            if (dialog != null && dialog.isShowing())
                                dismissDialog(DIALOG_BLUETOOTH_DEVICES);
                            startConnectThread();
                            ToastUtils.showLong("已配对到设备: "
                                    + connectedDeviceName);

                            break;
                        case BluetoothSerialService.STATE_CONNECTING:
                            if (progressBar != null)
                                progressBar.setVisibility(View.VISIBLE);
                            ToastUtils.showLong(getResources()
                                    .getString(R.string.connecting_to_device));
                            break;
                        case BluetoothSerialService.STATE_STOP:
                            if (dialog != null) {
                                removeDialog(DIALOG_BLUETOOTH_DEVICES);
                            }
                            imgbtnBlueToothCollect.setSelected(false);
                            ToastUtils.showLong("已停止蓝牙设备！");
                            break;
                        case BluetoothSerialService.STATE_LISTEN:
                        case BluetoothSerialService.STATE_NONE:
                            Log.i(TAG, "bluetoothHandler --> STATE_NONE");
                            if (progressBar != null)
                                progressBar.setVisibility(View.GONE);

                            if (laserRangeLayout != null)
                                laserRangeLayout.setVisibility(View.GONE);

                            if (dialog != null)
                                removeDialog(DIALOG_BLUETOOTH_DEVICES);
                            break;
                    }
                    break;
                case GisQueryApplication.BLUETOOTH_MESSAGE_WRITE:
                    break;
                case GisQueryApplication.BLUETOOTH_MESSAGE_READ:
                    break;
                case GisQueryApplication.BLUETOOTH_MESSAGE_DEVICE_NAME:
                    connectedDeviceName = msg.getData().getString(
                            GisQueryApplication.BLUETOOTH_DEVICE_NAME);
                    ToastUtils.showLong("已连接到设备: "
                            + connectedDeviceName);
                    break;
                case GisQueryApplication.BLUETOOTH_MESSAGE_TOAST:
                    break;
            }
        }

        private void startConnectThread() {
            // TODO Auto-generated method stub
            bluetoothConnectThread = new BluetoothConnectThread(
                    MainActivity.this, MainActivity.this.bluetoothAdapter,
                    MainActivity.this.connectedDeviceAddress,
                    MainActivity.SPP_UUID);
            bluetoothConnectThread.start();
        }
    };

    private final BroadcastReceiver bluetoothReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            // Finds a device
            if (action.equals(BluetoothDevice.ACTION_FOUND)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent
                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // If it's already paired, skip it, because it's been listed
                // already
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    String deviceName = device.getName()
                            + GisQueryApplication.LINE_SEPARATOR
                            + device.getAddress();
                    boolean exists = false;
                    int count = newDevicesAdapter.getCount();
                    for (int i = 0; i < count; i++) {
                        if (newDevicesAdapter.getItem(i).equals(deviceName)) {
                            exists = true;
                            break;
                        }
                    }

                    if (!exists)
                        newDevicesAdapter.add(deviceName);
                }
            } else if (action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
                int bondState = intent
                        .getIntExtra(BluetoothDevice.EXTRA_BOND_STATE,
                                BluetoothDevice.ERROR);

                switch (bondState) {
                    case BluetoothDevice.BOND_NONE:
                        break;
                    case BluetoothDevice.BOND_BONDING:
                        break;
                    case BluetoothDevice.BOND_BONDED:
                        BluetoothDevice device = intent
                                .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                        bluetoothSerialService.connect(device);
                        break;
                }
            } else if (action
                    .equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
                setProgressBarIndeterminateVisibility(false);

                if (newDevicesAdapter.getCount() == 0) {
                    String noDevices = getResources().getText(
                            R.string.none_found).toString();
                    newDevicesAdapter.add(noDevices);
                }
            }
        }
    };

    @SuppressWarnings("deprecation")
    public void openOrCloseBlueToothDevices(Boolean isOpen) {
        if (isOpen) {
            if (bluetoothAdapter == null) {
                bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            } else {
                if (!bluetoothAdapter.isEnabled()) {
                    Intent enableIntent = new Intent(
                            BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableIntent,
                            GisQueryApplication.REQUEST_ENABLE_BLUETOOTH);
                } else {
                    if (!bluetoothAdapter.isDiscovering()) {
                        initBluetoothSerialService();

                        bluetoothSerialService.start();
                        showDialog(DIALOG_BLUETOOTH_DEVICES);
                    } else
                        bluetoothAdapter.startDiscovery();
                }
            }
        } else {
            if (bluetoothConnectThread != null) {
                bluetoothConnectThread.stopSelf();
            }
            if (bluetoothAdapter != null)
                bluetoothAdapter.cancelDiscovery();
            if (bluetoothSerialService != null)
                bluetoothSerialService.stop();
        }
    }

    // 处理蓝牙接受信息
    public void dealWithBlueToothReceiveData(String blueToothMsgData) {

        Log.i(TAG, "蓝牙接受的信息:" + blueToothMsgData);

        try {
            JSONObject jsonObject = new JSONObject(blueToothMsgData.toString());
            String datatype = jsonObject.getString("datatype");
            if ("bluetooth_state".equalsIgnoreCase(datatype)) {
                String databody = jsonObject.getString("databody");
                if ("connected".equals(databody)) {
                    MainActivity.this.mHandler.sendEmptyMessage(10000);
                } else if ("closed".equals(databody)) {
                    MainActivity.this.openOrCloseBlueToothDevices(false);
                    MainActivity.this.mHandler.sendEmptyMessage(10001);
                }
            } else if ("collect_polygon_points".equalsIgnoreCase(datatype)) {
                // 处理蓝牙传递过来的多边形点信息
                String databody = jsonObject.getString("databody");
                if (geoCollectTool.isRunning()) {
                    geoCollectTool.addPolygonFromBlueToothMsg(databody);
                } else if (geoAnalysisTool.isRunning()) {
                    geoAnalysisTool.addPolygonFromBlueToothMsg(databody);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void sendEmptyMessage(int msgCode) {
        MainActivity.this.mHandler.sendEmptyMessage(msgCode);
    }

    public void sendEmptyMessage(Message msg) {
        MainActivity.this.mHandler.sendMessage(msg);//
    }


    /**
     * 加载本地服务
     */
    private void startLocalServices() {
        int mTaskUpdateType = GisQueryApplication.getApp().GetIntConfigData(
                "TASK_UPDATE_TYPE", 0);
        if (mTaskUpdateType == 1) {
            // 启动任务自动更新服务
            GisQueryApplication.getApp().startTaskUpdateService();
        }

    }

    /**
     * 地图消息处理
     */
    private class MapHandler extends Handler {

        public MapHandler(Looper paramLooper) {
            super(paramLooper);
        }

        public void handleMessage(Message paramMessage) {
            switch (paramMessage.what) {
                case 0: {
                    if (geoCollectTool.isRunning()) {
                        geoCollectTool.collectInsertOpt();
                    } else if (geoAnalysisTool.isRunning()) {
                        geoAnalysisTool.collectInsertOpt();
                    }
                    break;
                }
                case 5001: {
                    fullTextQueryFragment.refresh();
                    break;
                }
                case 7777: {
                    geoCollectTool.clearAndDraw(true, false);
                    break;
                }
                case 8197: {
                    GisQueryApplication.getApp().startGPS(false);
                    locationOnMap();
                    break;
                }
                case 8888: {
                    geoAnalysisTool.clearAndDraw(true, true);
                    break;
                }
                case 8899: {
                    AlertDialog.Builder localBuilder = new AlertDialog.Builder(
                            MainActivity.this);
                    final String taskType = paramMessage.obj.toString();
                    localBuilder.setTitle("您有新的任务");
                    localBuilder.setPositiveButton("马上更新",
                            new DialogInterface.OnClickListener() {
                                public void onClick(
                                        DialogInterface paramDialogInterface,
                                        int paramInt) {

                                    Intent localIntent = new Intent(
                                            MainActivity.this, TaskManagerActivity.class);
                                    localIntent.putExtra("taskType", taskType);
                                    MainActivity.this.startActivity(localIntent);
                                }
                            });
                    localBuilder.setNegativeButton("暂时不更新", null);
                    localBuilder.setMessage("新的任务提醒");
                    localBuilder.show();
                    break;
                }
                case 9999: {
                    MapOperate.zoomToGeometry();
                    break;
                }
                case 10000: {
                    MainActivity.this.findViewById(R.id.imgBlueToothCollect)
                            .setSelected(true);
                    break;
                }
                case 10001: {
                    MainActivity.this.findViewById(R.id.imgBlueToothCollect)
                            .setSelected(false);
                    break;
                }
                case 12288: {
                    MainActivity.this.refreshRecordGpsTrack();
                    break;
                }
                case 12289: {
                    NaviGPS localNaviGPS2 = GisQueryApplication.getApp().getNaviGps();
                    if (UtilsTools.IsGpsDataValid(localNaviGPS2.dLongitude,
                            localNaviGPS2.dLatitude)) {
                        Point mapPointJWD = new Point(localNaviGPS2.dLongitude,
                                localNaviGPS2.dLatitude);
                        // 投影转换
                        Point localPoint2D3 = Utility.fromWgs84ToMap2(mapPointJWD,
                                map.getSpatialReference());
                        if (gpsID == -1) {
                            gpsMarkerSymbol = new PictureMarkerSymbol(ContextCompat
                                    .getDrawable(MainActivity.this,R.drawable.ic_gps_navigation));
                            gpsID = gpsShowLayer.addGraphic(gpsGra = new Graphic(
                                    localPoint2D3, gpsMarkerSymbol));
                        } else {
                            gpsShowLayer.updateGraphic(gpsID, localPoint2D3);
                        }
                        MainActivity.this.refreshGpsTextInfo(localPoint2D3.getX(),
                                localPoint2D3.getY());
                        // ==============================================================================
                        // 巡查轨迹采点
                        // ==============================================================================
                        if ((MapOperate.TrackIsRecording())
                                && (MainActivity.this.m_bEnableRecordTrack)) {
                            MainActivity.DispatchGpsInfo(localPoint2D3);
                            MapOperate.TrackRecord(localNaviGPS2.dLatitude,
                                    localNaviGPS2.dLongitude,
                                    localNaviGPS2.dAltitude, localNaviGPS2.nTime);
                            MainActivity.this.m_bEnableRecordTrack = false;
                            TextView localTextView = MainActivity.this.mTrackInfoTextView;
                            int l = 1 + MainActivity.nRecordCount;
                            MainActivity.nRecordCount = l;
                            localTextView.setText("" + l);
                            // 屏幕点
                            Point localPoint2 = MainActivity.this.map
                                    .toScreenPoint(localPoint2D3);
                            // 地图居中
                            if ((localPoint2 != null)
                                    && (((localPoint2.getX() < 10)
                                    || (localPoint2.getY() < 10)
                                    || (localPoint2.getX() > -10
                                    + MainActivity.this.m_nScreenWidth) || (localPoint2
                                    .getY() > -10
                                    + MainActivity.this.m_nScreenHeight)))) {
                                map.centerAt(localPoint2D3, true);
                            }
                            // =========================================================================
                            // 判断巡查轨迹状态修改
                            // =========================================================================
                            MainActivity.this.recordGpsTrack();
                        }
                    }
                }
                break;
            }
            super.handleMessage(paramMessage);
        }
    }

    public static void SetGPSChangedListener(GpsListener paramGpsListener) {
        mGpsListeners.clear();
        if (paramGpsListener != null) {
            mGpsListeners.add(paramGpsListener);
        }
    }

    public static void DispatchGpsInfo(Point paramPoint2D) {
        Iterator<GpsListener> localIterator = mGpsListeners.iterator();
        while (true) {
            if (!(localIterator.hasNext()))
                return;
            localIterator.next()
                    .GpsChangedListener(paramPoint2D);
        }
    }

    // ======================================================================================================================
    // ==============================================巡查轨迹代码开始=========================================================
    public void trackStartOrPauseOpt(View paramView) {
        if (paramView.isSelected()) {
            MapOperate.TrackPauseRecord();
            paramView.setSelected(false);
            pauseTrackShowing();
            ToastUtils.showLong("已停止记录!");
        } else {
            if (!(GisQueryApplication.getApp().isGpsEnable())) {
                IsOpenGPSDlg(R.string.is_open_gps);
            } else {
                if (!(MapOperate.IsStartTrackRecord())) {
                    new Time().setToNow();
                    SysConfig.TrackID = 1 + SysConfig.TrackID;
                    GisQueryApplication.getApp().SetConfigData("trackId", SysConfig.TrackID);



                    MapOperate
                            .TrackInit(
                                    SysConfig.mHumanInfo.getHumanName()
                                            + "_"
                                            + MDateUtils
                                            .GetCurrentFormatTime("yyyyMMdd_HHmmss"),
                                    SysConfig.TrackID,track_selected);
                    // 初始化时先保存轨迹对象
                    saveTrackToDb();
                }
                MapOperate.TrackStartRecord();
                startTrackShowing();
                paramView.setSelected(true);
                ToastUtils.showLong("开始记录中.....");
            }

        }

    }

    /**
     * 保存巡查轨迹到数据库
     */
    private void saveTrackToDb() {
        new TrackDBMExternal(this).insertOneTrack(MapOperate.GetTrackObj());
    }

    /**
     * 刷新巡查轨迹设置
     */
    private void refreshRecordGpsTrack() {
        recordGpsTrack();
    }

    /**
     * 根据设置判断是否该记录轨迹点
     */
    private void recordGpsTrack() {
        if (SysConfig.TrackType == 0) {
            recordGpsTrackByTime();
        } else {
            recordGpsTrackByDis();
        }
    }

    /**
     * 是否间距OK
     *
     * @return
     */
    private boolean isDistanceOk() {
        boolean bool1 = GisQueryApplication.getApp().isLocation();
        if (bool1) {
            Point localPoint2D1 = MapOperate.GetNewTrackPoint();
            if (UtilsTools.IsGpsDataValid(localPoint2D1.getX(),
                    localPoint2D1.getY())) {
                Point localPoint2D2 = new Point();
                localPoint2D2.setX(GisQueryApplication.getApp().getNaviGps().dLongitude);
                localPoint2D2.setY(GisQueryApplication.getApp().getNaviGps().dLatitude);
                boolean bool2 = MapOperate.GetLength(localPoint2D1,
                        localPoint2D2) >= SysConfig.TrackSetDis;
                return bool2;
            } else {
                return false;
            }
        } else {
            return false;
        }

    }

    /**
     * 距离判断线程
     *
     * @author dcx
     */
    class DistanceJudgeThread implements Runnable {
        public void run() {
            if (MainActivity.this.isDistanceOk()) {
                if (SysConfig.TrackType == 1) {
                    MainActivity.this.m_bEnableRecordTrack = true;
                }
            } else {
                if (SysConfig.TrackType == 1) {
                    DistanceJudgeThread disThread = new DistanceJudgeThread();
                    MainActivity.this.mHandler.postDelayed(disThread, 500L);
                }
            }
        }
    }

    /**
     * 按间隔距离记录轨迹
     */
    private void recordGpsTrackByDis() {
        DistanceJudgeThread disThread = new DistanceJudgeThread();
        MainActivity.this.mHandler.postDelayed(disThread, 500L);
    }

    /**
     * 按时间间隔记录轨迹
     */
    private void recordGpsTrackByTime() {
        this.mHandler.postDelayed(new Runnable() {
            public void run() {
                if (SysConfig.TrackType == 0) {
                    MainActivity.this.m_bEnableRecordTrack = true;
                }
            }
        }, 1000 * SysConfig.TrackSetTime);
    }

    /**
     * 开始巡查
     */
    private void startTrackShowing() {
        Intent localIntent = new Intent();
        localIntent.setAction("com.guyu.android.gis.localservice.TrackingShowService");
        localIntent.setPackage(getPackageName());
        TrackingShowService.state = TrackingShowService.TRACK_OPT_0_START;
        localIntent.putExtra("opt", TrackingShowService.TRACK_OPT_0_START);
        startService(localIntent);
    }

    /**
     * 暂停巡查
     */
    private void pauseTrackShowing() {
        Intent localIntent = new Intent();
        localIntent.setAction("com.guyu.android.gis.localservice.TrackingShowService");
        localIntent.setPackage(getPackageName());
        startService(localIntent);
    }

    /**
     * 停止巡查
     */
    private void stopTrackShowing() {
        Intent localIntent = new Intent();
        localIntent.setAction("com.guyu.android.gis.localservice.TrackingShowService");
        localIntent.setPackage(getPackageName());
        localIntent.putExtra("opt", TrackingShowService.TRACK_OPT_3_STOP);
        startService(localIntent);
    }

    // =============================================巡查轨迹结束代码=========================================================================
    // =====================================================================================================================================

    /**
     * 底部菜单动画初始化
     */
    private void menuAnimal() {
        this.animOut = AnimationUtils
                .loadAnimation(MainActivity.this, R.anim.push_right_out);
        this.animIn = AnimationUtils.loadAnimation(this, R.anim.push_right_in);
        this.animOut.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationEnd(Animation paramAnimation) {
                findViewById(R.id.vg_menu).setVisibility(View.INVISIBLE);
            }

            public void onAnimationRepeat(Animation paramAnimation) {
            }

            public void onAnimationStart(Animation paramAnimation) {
            }
        });
        this.animIn.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationEnd(Animation paramAnimation) {
                findViewById(R.id.vg_menu).setVisibility(View.VISIBLE);
            }

            public void onAnimationRepeat(Animation paramAnimation) {
            }

            public void onAnimationStart(Animation paramAnimation) {
            }
        });
    }

    public void saveLayerVisibility() {
        int i = 0;
        for (Layer layer : map.getLayers()) {
            if (layer instanceof ArcGISLocalTiledLayer
                    || layer instanceof ArcGISTiledMapServiceLayer
                    || layer instanceof ArcGISFeatureLayer
                    || layer instanceof FeatureLayer
                    || layer instanceof ArcGISDynamicMapServiceLayer) {
                GisQueryApplication.getApp().writeApplicationPreference(layer.getName(),
                        visibleLayers.get(i).isVisible());
                i++;
            }
        }
    }

    private void showOrHideLayer(final String[] togglelayer_labelAry, final boolean isShow) {

        for (int i = 0; i < togglelayer_labelAry.length; i++) {
            Layer togglelayer = MapUtils.getLayerByName(map,
                    togglelayer_labelAry[i]);
            togglelayer.setVisible(isShow);
        }
    }

    /**
     * 根据快捷图层配置创建触发按钮
     *
     * @param qtlc 快捷图层配置
     * @return
     */
    private ImageButton createOneQuickToggleLayerButton(
            QuickToggleLayerConfig qtlc) {
        String togglelayer_labels = qtlc.getLabel();
        final String[] togglelayer_labelAry = togglelayer_labels.split(",");// 分割字符串，返回子字符串数组

        ImageButton imgbtn = new ImageButton(MainActivity.this);// 弹出的按钮
        boolean togglelayerVisible = true;
        for (int i = 0; i < togglelayer_labelAry.length; i++) {
            Layer togglelayer = MapUtils.getLayerByName(map,
                    togglelayer_labelAry[i]);// 根据图层名找图层
            togglelayerVisible = togglelayerVisible && togglelayer.isVisible();
        }
        imgbtn.setSelected(togglelayerVisible);
        imgbtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View paramView) {

                if (paramView.isSelected()) {
                    showOrHideLayer(togglelayer_labelAry, false);
                    paramView.setSelected(false);
                } else {
                    showOrHideLayer(togglelayer_labelAry, true);
                    paramView.setSelected(true);
                }
                saveLayerVisibility();

            }
        });
        // 设置快捷图层图标
        setQuicklayerImageIcon(qtlc, imgbtn);

        return imgbtn;
    }

    // 设置快捷图层图标
    private void setQuicklayerImageIcon(QuickToggleLayerConfig qtlc,
                                        ImageButton imgbtn) {
        if ("yx".equals(qtlc.getIconflag())) {
            imgbtn.setImageDrawable(getResources().getDrawable(
                    R.drawable.menu_layer_icon_yx));
            imgbtn.setBackgroundDrawable(getResources().getDrawable(
                    R.drawable.menu_item_backgrounds));
            if (GisQueryApplication.getApp().isSpecialVirsion()) {
                imgbtn.setVisibility(View.GONE);// 特殊版本 无影像图
            }

        } else if ("lsyx".equals(qtlc.getIconflag())) {
            imgbtn.setImageDrawable(getResources().getDrawable(
                    R.drawable.menu_layer_icon_lsyx));
            imgbtn.setBackgroundDrawable(getResources().getDrawable(
                    R.drawable.menu_item_backgrounds));
        } else if ("zxyx".equals(qtlc.getIconflag())) {
            imgbtn.setImageDrawable(getResources().getDrawable(
                    R.drawable.menu_layer_icon_zxyx));
            imgbtn.setBackgroundDrawable(getResources().getDrawable(
                    R.drawable.menu_item_backgrounds));
        } else if ("bp".equals(qtlc.getIconflag())) {
            imgbtn.setImageDrawable(getResources().getDrawable(
                    R.drawable.menu_layer_icon_bp));
            imgbtn.setBackgroundDrawable(getResources().getDrawable(
                    R.drawable.menu_item_backgrounds));
        } else if ("gy".equals(qtlc.getIconflag())) {
            imgbtn.setImageDrawable(getResources().getDrawable(
                    R.drawable.menu_layer_icon_gy));
            imgbtn.setBackgroundDrawable(getResources().getDrawable(
                    R.drawable.menu_item_backgrounds));
        } else if ("zd".equals(qtlc.getIconflag())) {
            imgbtn.setImageDrawable(getResources().getDrawable(
                    R.drawable.menu_layer_icon_zd));
            imgbtn.setBackgroundDrawable(getResources().getDrawable(
                    R.drawable.menu_item_backgrounds));
        } else if ("gh".equals(qtlc.getIconflag())) {
            imgbtn.setImageDrawable(getResources().getDrawable(
                    R.drawable.menu_layer_icon_gh));
            imgbtn.setBackgroundDrawable(getResources().getDrawable(
                    R.drawable.menu_item_backgrounds));
        } else if ("xz".equals(qtlc.getIconflag())) {
            imgbtn.setImageDrawable(getResources().getDrawable(
                    R.drawable.menu_layer_icon_xz));
            imgbtn.setBackgroundDrawable(getResources().getDrawable(
                    R.drawable.menu_item_backgrounds));
        } else if ("xztb".equals(qtlc.getIconflag())) {
            imgbtn.setImageDrawable(getResources().getDrawable(
                    R.drawable.menu_layer_icon_xztb));
            imgbtn.setBackgroundDrawable(getResources().getDrawable(
                    R.drawable.menu_item_backgrounds));
        } else if ("ghtb".equals(qtlc.getIconflag())) {
            imgbtn.setImageDrawable(getResources().getDrawable(
                    R.drawable.menu_layer_icon_ghtb));
            imgbtn.setBackgroundDrawable(getResources().getDrawable(
                    R.drawable.menu_item_backgrounds));
        } else if ("dxt".equals(qtlc.getIconflag())) {// /////////启高
            imgbtn.setImageDrawable(getResources().getDrawable(
                    R.drawable.menu_layer_icon_dxt));
            imgbtn.setBackgroundDrawable(getResources().getDrawable(
                    R.drawable.menu_item_backgrounds));
        } else if ("tfh".equals(qtlc.getIconflag())) {
            imgbtn.setImageDrawable(getResources().getDrawable(
                    R.drawable.menu_layer_icon_tfh));
            imgbtn.setBackgroundDrawable(getResources().getDrawable(
                    R.drawable.menu_item_backgrounds));
        } else if ("dmdz".equals(qtlc.getIconflag())) {
            imgbtn.setImageDrawable(getResources().getDrawable(
                    R.drawable.menu_layer_icon_dmdz));
            imgbtn.setBackgroundDrawable(getResources().getDrawable(
                    R.drawable.menu_item_backgrounds));
        } else if ("xzqh".equals(qtlc.getIconflag())) {
            imgbtn.setImageDrawable(getResources().getDrawable(
                    R.drawable.menu_layer_icon_xzqh));
            imgbtn.setBackgroundDrawable(getResources().getDrawable(
                    R.drawable.menu_item_backgrounds));
        } else if ("lyxb".equals(qtlc.getIconflag())) {
            imgbtn.setImageDrawable(getResources().getDrawable(
                    R.drawable.menu_layer_icon_lyxb));
            imgbtn.setBackgroundDrawable(getResources().getDrawable(
                    R.drawable.menu_item_backgrounds));
        } else if ("lq".equals(qtlc.getIconflag())) {
            imgbtn.setImageDrawable(getResources().getDrawable(
                    R.drawable.menu_layer_icon_lq));
            imgbtn.setBackgroundDrawable(getResources().getDrawable(
                    R.drawable.menu_item_backgrounds));
        }
    }

    /**
     * 初始化底部菜单和左侧面板
     */
    private void menuControl() {
        initLeftPanel();

        this.mTopTitle = ((RelativeLayout) findViewById(R.id.vg_top_title));
        this.mTopCollect = ((LinearLayout) findViewById(R.id.vg_top_collect));
        this.mVgMesureToolbar = ((LinearLayout) findViewById(R.id.vg_measure_toolbar));

        intQuickToggleLayerMenu();
        menuAnimal();
        findViewById(R.id.mainMenuFoldControl).setOnClickListener(// 隐藏按钮点击事件
                new View.OnClickListener() {
                    public void onClick(View paramView) {
                        if (paramView.isSelected()) {
                            findViewById(R.id.vg_menu).startAnimation(animIn);
                            paramView.setSelected(false);
                            return;
                        }
                        findViewById(R.id.vg_menu).startAnimation(animOut);
                        paramView.setSelected(true);
                    }
                });
        // 返回键
        if (GisQueryApplication.getApp().getProjectconfig().exitFunctionCode("backkey")) {
            findViewById(R.id.mainMenuBack).setOnClickListener(
                    new View.OnClickListener() {
                        public void onClick(View paramView) {
                            onBackPressed();
                        }
                    });
            findViewById(R.id.mainMenuBack).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.mainMenuBack).setVisibility(View.GONE);
        }

        initTrackPopMenu();// 初始化巡查轨迹弹出菜单
        initLandAnalysisMenu();// 初始化分析菜单
        initCollectPopMenu();// 初始化采集菜单
        initLandManagerMenu();// 初始化巡查管理弹出菜单
//        initIllegalcluesMenu();
//        initSmsMenu();
        initMyTaskMenu();
        initToolsPopMenu();// 初始化工具弹出菜单

        initLQ_Change();
    }

    private ViewPager pager = null;
    private ArrayList<Fragment> fragmentList;
    private CantonZoneNaviFragment cantonZoneNaviFragment;
    private FullTextQueryFragment fullTextQueryFragment;

    public CantonZoneNaviFragment getCantonZoneNaviFragment() {
        return cantonZoneNaviFragment;
    }

    public FullTextQueryFragment getFullTextQueryFragment() {
        return fullTextQueryFragment;
    }

    private OnCheckedChangeListener onCheckedChangeListener = new OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(RadioGroup arg0, int checkedId) {
            switch (checkedId) {
                case R.id.radio_zonenavi:
                    pager.setCurrentItem(0);
                    break;
                case R.id.radio_fulltextquery:
                    pager.setCurrentItem(1);
                    break;
            }
        }

    };

    /**
     * 初始化左侧面板
     */
    private void initLeftPanel() {
        if (GisQueryApplication.getApp().getProjectconfig().exitFunctionCode("zonenavi")
                || GisQueryApplication.getApp().getProjectconfig()
                .exitFunctionCode("fulltextquery")) {
            findViewById(R.id.leftpanel).setVisibility(View.VISIBLE);
            final RadioGroup lefttabs_radiogroup = ((RadioGroup) findViewById(R.id.tab_radiogroup));
            lefttabs_radiogroup
                    .setOnCheckedChangeListener(onCheckedChangeListener);
            pager = (ViewPager) findViewById(R.id.viewpager);

            pager.setOnPageChangeListener(new OnPageChangeListener() {
                @Override
                public void onPageSelected(int position) {
                    if (position == 0) {
                        lefttabs_radiogroup.check(R.id.radio_zonenavi);
                    } else {
                        lefttabs_radiogroup.check(R.id.radio_fulltextquery);
                    }

                }

                @Override
                public void onPageScrolled(int arg0, float arg1, int arg2) {
                }

                @Override
                public void onPageScrollStateChanged(int arg0) {
                }
            });
            fragmentList = new ArrayList<Fragment>();
            // 初始化区划导航
            if (GisQueryApplication.getApp().getProjectconfig().exitFunctionCode("zonenavi")) {
                fragmentList
                        .add(cantonZoneNaviFragment = new CantonZoneNaviFragment());
            }
            // 初始化全文检索
            if (GisQueryApplication.getApp().getProjectconfig()
                    .exitFunctionCode("fulltextquery")) {
                fragmentList
                        .add(fullTextQueryFragment = new FullTextQueryFragment());
                fullTextQueryFragment.setMainActivity(this);
            }
            pager.setAdapter(new MyPageAdapter(
                    this.getSupportFragmentManager(), fragmentList));
            pager.setCurrentItem(0);

        } else {
            findViewById(R.id.leftpanel).setVisibility(View.GONE);
        }
    }

    private class MyPageAdapter extends FragmentPagerAdapter {
        ArrayList<Fragment> list;

        public MyPageAdapter(FragmentManager fm, ArrayList<Fragment> list) {
            super(fm);
            this.list = list;
        }

        @Override
        public Fragment getItem(int arg0) {
            return this.list.get(arg0);
        }

        @Override
        public int getCount() {
            return this.list.size();
        }

    }

    /**
     * 初始化快捷图层菜单
     */
    private void intQuickToggleLayerMenu() {
        LinearLayout vg_menu = (LinearLayout) findViewById(R.id.vg_menu);
        // LinearLayout vg_menu = (LinearLayout) findViewById(R.id.vg_menu_two);
        ArrayList<QuickToggleLayerConfig> al_quicklayerconfig = GisQueryApplication.getApp().getProjectconfig()
                .getQuicktogglelayers();
        for (int i = 0; i < al_quicklayerconfig.size(); i++) {
            QuickToggleLayerConfig qtlc = al_quicklayerconfig.get(i);
//            ImageButton imgbtn = createOneQuickToggleLayerButton(qtlc);

            // LinearLayout.LayoutParams linearParams =
            // (LinearLayout.LayoutParams) imgbtn.getLayoutParams();
            // linearParams.height = 5; // 当控件的高强制设成365象素
            // imgbtn.setLayoutParams(linearParams); // 使设置好的布局参数应用到控件aaa

//            vg_menu.addView(imgbtn, i);

        }
    }

    /**
     * 初始化巡查轨迹弹出菜单
     */
    private void initTrackPopMenu() {
        // 巡查轨迹
//        final ActionItem itemCreateTrackSelected = new ActionItem();
//        itemCreateTrackSelected.setIcon(getResources().getDrawable(
//                R.drawable.menu_track_toolbar_selected));
//        itemCreateTrackSelected.setVisible(false);
//        itemCreateTrackSelected.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View paramView) {
//                if (quickAction4 == null)
//                    return;
//                quickAction4.popupWindowDismiss();
//                quickAction4.setActionItemVisible(0, false);
//                quickAction4.setActionItemVisible(1, true);
//                mVgTrackToolbar.setVisibility(View.INVISIBLE);
//            }
//        });


//        final ActionItem itemCreateTrackUnSelected = new ActionItem();
//        itemCreateTrackUnSelected.setIcon(getResources().getDrawable(
//                R.drawable.menu_track_toolbar));
//        itemCreateTrackUnSelected
//                .setOnClickListener(new View.OnClickListener() {
//                    public void onClick(View paramView) {
//                        if (quickAction4 == null)
//                            return;
//                        quickAction4.popupWindowDismiss();
//                        quickAction4.setActionItemVisible(0, true);
//                        quickAction4.setActionItemVisible(1, false);
//                        mVgTrackToolbar.setVisibility(View.VISIBLE);
//                    }
//                });

        final ActionItem itemCreateTrackStart4 = new ActionItem();
        itemCreateTrackStart4.setIcon(getResources().getDrawable(
                R.drawable.menu_track_start1));
        itemCreateTrackStart4.setVisible(false);
        itemCreateTrackStart4
                .setOnClickListener(new View.OnClickListener() {
                    public void onClick(View paramView) {
                        if (quickAction4 == null)
                            return;
                        quickAction4.popupWindowDismiss();
                        quickAction4.setActionItemVisible(0, false);
                        quickAction4.setActionItemVisible(1, true);
                        mVgTrackToolbar.setVisibility(View.VISIBLE);
                        track_selected = 0;
                        trackStartOrPauseOpt(mTrackStartBtn);
                    }
                });

        final ActionItem itemCreateTrackStart1 = new ActionItem();
        itemCreateTrackStart1.setIcon(getResources().getDrawable(
                R.drawable.menu_track_start1));
        itemCreateTrackStart1
                .setOnClickListener(new View.OnClickListener() {
                    public void onClick(View paramView) {
                        if (quickAction4 == null)
                            return;
                        quickAction4.popupWindowDismiss();
                        quickAction4.setActionItemVisible(0, true);
                        quickAction4.setActionItemVisible(1, false);
                        mVgTrackToolbar.setVisibility(View.VISIBLE);
                        track_selected = 0;
                        trackStartOrPauseOpt(mTrackStartBtn);
                    }
                });



        final ActionItem itemCreateTrackStart2 = new ActionItem();
        itemCreateTrackStart2.setIcon(getResources().getDrawable(
                R.drawable.menu_track_start2));
        itemCreateTrackStart2
                .setOnClickListener(new View.OnClickListener() {
                    public void onClick(View paramView) {
                        if (quickAction4 == null)
                            return;
                        quickAction4.popupWindowDismiss();
                        quickAction4.setActionItemVisible(0, true);
                        quickAction4.setActionItemVisible(1, false);
                        mVgTrackToolbar.setVisibility(View.VISIBLE);
                        track_selected = 1;
                        trackStartOrPauseOpt(mTrackStartBtn);
                    }
                });


        final ActionItem itemCreateTrackStart3 = new ActionItem();
        itemCreateTrackStart3.setIcon(getResources().getDrawable(
                R.drawable.menu_track_start3));
        itemCreateTrackStart3
                .setOnClickListener(new View.OnClickListener() {
                    public void onClick(View paramView) {
                        if (quickAction4 == null)
                            return;
                        quickAction4.popupWindowDismiss();
                        quickAction4.setActionItemVisible(0, true);
                        quickAction4.setActionItemVisible(1, false);
                        mVgTrackToolbar.setVisibility(View.VISIBLE);
                        track_selected = 2;
                        trackStartOrPauseOpt(mTrackStartBtn);
                    }
                });



        final ActionItem itemRefrenceTrack = new ActionItem();
        itemRefrenceTrack.setIcon(getResources().getDrawable(
                R.drawable.menu_track_openref));
        itemRefrenceTrack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View paramView) {
                MainActivity.this.quickAction4.popupWindowDismiss();
                MainActivity.this
                        .startActivity(new Intent(
                                MainActivity.this, TrackManagerActivity.class));
            }
        });
        if (GisQueryApplication.getApp().getProjectconfig().exitFunctionCode("trackmgr")) {
            findViewById(R.id.mainMenuBtn04).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.mainMenuBtn04).setVisibility(View.GONE);
        }
        findViewById(R.id.mainMenuBtn04).setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View paramView) {
                        quickAction4 = new QuickActions(paramView);
//                        quickAction4.addActionItem(itemCreateTrackSelected);
//                        quickAction4.addActionItem(itemCreateTrackUnSelected);

                        quickAction4.addActionItem(itemCreateTrackStart4);
                        quickAction4.addActionItem(itemCreateTrackStart1);
                        quickAction4.addActionItem(itemCreateTrackStart2);
                        quickAction4.addActionItem(itemCreateTrackStart3);

                        quickAction4.addActionItem(itemRefrenceTrack);
                        quickAction4.setAnimStyle(QuickActions.ANIM_AUTO);
                        quickAction4.show();
                    }
                });
    }

    /**
     * 初始化巡查管理弹出菜单
     */
    private void initLandManagerMenu() {
        final ActionItem landAnalysis = new ActionItem();
        landAnalysis.setIcon(getResources().getDrawable(
                R.drawable.menu_land_analysis));
        landAnalysis.setOnClickListener(new View.OnClickListener() {
            public void onClick(View paramView) {
                //MainActivity.this.analysisOpt();
            }
        });
        final ActionItem landManager = new ActionItem();
        landManager
                .setIcon(getResources().getDrawable(R.drawable.menu_land_mg));
        landManager.setOnClickListener(new View.OnClickListener() {
            public void onClick(View paramView) {
                MainActivity.this.quickAciton_landManager.popupWindowDismiss();
                normalStateStart();
                Intent localIntent = new Intent(
                        MainActivity.this, CaseManagerActivity.class);
//                localIntent.putExtra("caseType", "dtxc");
                localIntent.putExtra("caseType", "huatu");
                MainActivity.this.startActivity(localIntent);
            }
        });
        final ActionItem itemAddLand = new ActionItem();
        itemAddLand.setIcon(getResources()
                .getDrawable(R.drawable.menu_land_add));
        itemAddLand.setOnClickListener(new View.OnClickListener() {
            public void onClick(View paramView) {
                MainActivity.this.quickAciton_landManager.popupWindowDismiss();
                collectGeometry(null, "huatu");
            }
        });
        if (GisQueryApplication.getApp().getProjectconfig().exitFunctionCode("landmgr")) {
            findViewById(R.id.mainMenuLandManger).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.mainMenuLandManger).setVisibility(View.GONE);
        }
        findViewById(R.id.mainMenuLandManger).setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View paramView) {
                        MainActivity.this.quickAciton_landManager = new QuickActions(
                                paramView);
                        MainActivity.this.quickAciton_landManager
                                .addActionItem(landManager);
                        MainActivity.this.quickAciton_landManager
                                .addActionItem(itemAddLand);
                        //MainActivity.this.quickAciton_landManager
                        // .addActionItem(landAnalysis);
                        MainActivity.this.quickAciton_landManager
                                .setAnimStyle(QuickActions.ANIM_AUTO);
                        if (MainActivity.this.mLandDetailsBackBtn.isShown()) {
                            MainActivity.this.finishActivity(3234);
                            MainActivity.this.mLandDetailsBackBtn
                                    .setVisibility(View.GONE);
                        }
                        MainActivity.this.quickAciton_landManager.show();
                    }
                });
    }

    /**
     * 违法线索
     */
    private void initIllegalcluesMenu() {
        final ActionItem illegalcluesMg = new ActionItem();
        illegalcluesMg.setIcon(getResources().getDrawable(
                R.drawable.menu_illegalclues_mg));
        illegalcluesMg.setOnClickListener(new View.OnClickListener() {
            public void onClick(View paramView) {
                MainActivity.this.quickAciton_illegalClues.popupWindowDismiss();
                normalStateStart();
                Intent localIntent = new Intent(
                        MainActivity.this, CaseManagerActivity.class);
                localIntent.putExtra("caseType", "wfxs");
                MainActivity.this.startActivity(localIntent);
            }
        });
        final ActionItem illegalcluesAdd = new ActionItem();
        illegalcluesAdd.setIcon(getResources().getDrawable(
                R.drawable.menu_illegalclues_add));
        illegalcluesAdd.setOnClickListener(new View.OnClickListener() {
            public void onClick(View paramView) {
                MainActivity.this.quickAciton_illegalClues.popupWindowDismiss();
                Intent localIntent = new Intent(
                        MainActivity.this, InitializeRecordActivity.class);
                localIntent.putExtra("caseType", "wfxs");
                MainActivity.this.startActivity(localIntent);
            }
        });
        if (GisQueryApplication.getApp().getProjectconfig().exitFunctionCode("illegalclues")) {
            findViewById(R.id.mainMenuIllegalClues).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.mainMenuIllegalClues).setVisibility(View.GONE);
        }
        findViewById(R.id.mainMenuIllegalClues).setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View paramView) {
                        MainActivity.this.quickAciton_illegalClues = new QuickActions(
                                paramView);
                        MainActivity.this.quickAciton_illegalClues
                                .addActionItem(illegalcluesMg);
                        MainActivity.this.quickAciton_illegalClues
                                .addActionItem(illegalcluesAdd);
                        MainActivity.this.quickAciton_illegalClues
                                .setAnimStyle(QuickActions.ANIM_AUTO);
                        if (MainActivity.this.mLandDetailsBackBtn.isShown()) {
                            MainActivity.this.finishActivity(3234);
                            MainActivity.this.mLandDetailsBackBtn
                                    .setVisibility(View.GONE);
                        }
                        MainActivity.this.quickAciton_illegalClues.show();
                    }
                });

    }

    /**
     * 林权变更，流转管理;林下经济；
     */
    private void initLQ_Change() {

        // 林权变更,线索管理
        final ActionItem illegalcluesMg = new ActionItem();
        illegalcluesMg.setIcon(getResources().getDrawable(
                R.drawable.menu_illegalclues_mg4));
        illegalcluesMg.setOnClickListener(new View.OnClickListener() {
            public void onClick(View paramView) {
                MainActivity.this.quickAciton_illegalClues.popupWindowDismiss();
                normalStateStart();
                Intent localIntent = new Intent(
                        MainActivity.this, CaseManagerActivity.class);
                localIntent.putExtra("caseType", "change");
                MainActivity.this.startActivity(localIntent);
            }
        });
        // 林权变更,线索登记
        final ActionItem illegalcluesAdd = new ActionItem();
        illegalcluesAdd.setIcon(getResources().getDrawable(
                R.drawable.menu_illegalclues_add6));
        illegalcluesAdd.setOnClickListener(new View.OnClickListener() {
            public void onClick(View paramView) {
                MainActivity.this.quickAciton_illegalClues.popupWindowDismiss();
                Intent localIntent = new Intent(
                        MainActivity.this, InitializeRecordActivity.class);
                localIntent.putExtra("caseType", "change");
                MainActivity.this.startActivity(localIntent);
            }
        });

        // 林下经济,线索管理
        final ActionItem illegalcluesMg2 = new ActionItem();
        illegalcluesMg2.setIcon(getResources().getDrawable(
                R.drawable.menu_illegalclues_mg6));
        illegalcluesMg2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View paramView) {
                MainActivity.this.quickAciton_illegalClues.popupWindowDismiss();
                normalStateStart();
                Intent localIntent = new Intent(
                        MainActivity.this, CaseManagerActivity.class);
                localIntent.putExtra("caseType", "linxia");
                MainActivity.this.startActivity(localIntent);
            }
        });
        // 林下经济,线索登记
        final ActionItem illegalcluesAdd2 = new ActionItem();
        illegalcluesAdd2.setIcon(getResources().getDrawable(
                R.drawable.menu_illegalclues_add5));
        illegalcluesAdd2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View paramView) {
                MainActivity.this.quickAciton_illegalClues.popupWindowDismiss();
                Intent localIntent = new Intent(
                        MainActivity.this, InitializeRecordActivity.class);
                localIntent.putExtra("caseType", "linxia");
                MainActivity.this.startActivity(localIntent);
            }
        });

        // 林木采伐,线索管理
        final ActionItem illegalcluesMg3 = new ActionItem();
        illegalcluesMg3.setIcon(getResources().getDrawable(
                R.drawable.menu_illegalclues_mg5));
        illegalcluesMg3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View paramView) {
                MainActivity.this.quickAciton_illegalClues.popupWindowDismiss();
                normalStateStart();
                Intent localIntent = new Intent(
                        MainActivity.this, CaseManagerActivity.class);
                localIntent.putExtra("caseType", "linmu");
                MainActivity.this.startActivity(localIntent);
            }
        });
        // 林木采伐,线索登记
        final ActionItem illegalcluesAdd3 = new ActionItem();
        illegalcluesAdd3.setIcon(getResources().getDrawable(
                R.drawable.menu_illegalclues_add4));
        illegalcluesAdd3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View paramView) {
                MainActivity.this.quickAciton_illegalClues.popupWindowDismiss();
                Intent localIntent = new Intent(
                        MainActivity.this, InitializeRecordActivity.class);
                localIntent.putExtra("caseType", "linmu");
                MainActivity.this.startActivity(localIntent);
            }
        });

        // 木材检查站,线索管理
        final ActionItem illegalcluesMg4 = new ActionItem();
        illegalcluesMg4.setIcon(getResources().getDrawable(
                R.drawable.menu_illegalclues_mg3));
        illegalcluesMg4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View paramView) {
                MainActivity.this.quickAciton_illegalClues.popupWindowDismiss();
                normalStateStart();
                Intent localIntent = new Intent(
                        MainActivity.this, CaseManagerActivity.class);
                localIntent.putExtra("caseType", "mucai");
                MainActivity.this.startActivity(localIntent);
            }
        });
        // 木材检查站,线索登记
        final ActionItem illegalcluesAdd4 = new ActionItem();
        illegalcluesAdd4.setIcon(getResources().getDrawable(
                R.drawable.menu_illegalclues_add3));
        illegalcluesAdd4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View paramView) {
                MainActivity.this.quickAciton_illegalClues.popupWindowDismiss();
                Intent localIntent = new Intent(
                        MainActivity.this, InitializeRecordActivity.class);
                localIntent.putExtra("caseType", "mucai");
                MainActivity.this.startActivity(localIntent);
            }
        });

        // 违法立案,线索管理
        final ActionItem illegalcluesMg5 = new ActionItem();
        illegalcluesMg5.setIcon(getResources().getDrawable(
                R.drawable.menu_illegalclues_mg2));
        illegalcluesMg5.setOnClickListener(new View.OnClickListener() {
            public void onClick(View paramView) {
                MainActivity.this.quickAciton_illegalClues.popupWindowDismiss();
                normalStateStart();
                Intent localIntent = new Intent(
                        MainActivity.this, CaseManagerActivity.class);
                localIntent.putExtra("caseType", "weifa");
                MainActivity.this.startActivity(localIntent);
            }
        });
        // 违法立案,线索登记
        final ActionItem illegalcluesAdd5 = new ActionItem();
        illegalcluesAdd5.setIcon(getResources().getDrawable(
                R.drawable.menu_illegalclues_add2));
        illegalcluesAdd5.setOnClickListener(new View.OnClickListener() {
            public void onClick(View paramView) {
                MainActivity.this.quickAciton_illegalClues.popupWindowDismiss();
                Intent localIntent = new Intent(
                        MainActivity.this, InitializeRecordActivity.class);
                localIntent.putExtra("caseType", "weifa");
                MainActivity.this.startActivity(localIntent);
            }
        });

        // 野生动物,线索管理
        final ActionItem illegalcluesMg6 = new ActionItem();
        illegalcluesMg6.setIcon(getResources().getDrawable(
                R.drawable.menu_illegalclues_mg1));
        illegalcluesMg6.setOnClickListener(new View.OnClickListener() {
            public void onClick(View paramView) {
                MainActivity.this.quickAciton_illegalClues.popupWindowDismiss();
                normalStateStart();
                Intent localIntent = new Intent(
                        MainActivity.this, CaseManagerActivity.class);
                localIntent.putExtra("caseType", "dongwu");
                MainActivity.this.startActivity(localIntent);
            }
        });
        // 野生动物,线索登记
        final ActionItem illegalcluesAdd6 = new ActionItem();
        illegalcluesAdd6.setIcon(getResources().getDrawable(
                R.drawable.menu_illegalclues_add1));
        illegalcluesAdd6.setOnClickListener(new View.OnClickListener() {
            public void onClick(View paramView) {
                MainActivity.this.quickAciton_illegalClues.popupWindowDismiss();
                Intent localIntent = new Intent(
                        MainActivity.this, InitializeRecordActivity.class);
                localIntent.putExtra("caseType", "dongwu");
                MainActivity.this.startActivity(localIntent);
            }
        });

        if (GisQueryApplication.getApp().getProjectconfig().exitFunctionCode("linquanchange")) {// 启高自己配的
            findViewById(R.id.mainMenuLQ_Change).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.mainMenuLQ_Change).setVisibility(View.GONE);
        }
        findViewById(R.id.mainMenuLQ_Change).setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View paramView) {
                        MainActivity.this.quickAciton_illegalClues = new QuickActions(
                                paramView);
                        MainActivity.this.quickAciton_illegalClues
                                .addActionItem(illegalcluesMg);
                        MainActivity.this.quickAciton_illegalClues
                                .addActionItem(illegalcluesAdd);
                        // 2启高
                        MainActivity.this.quickAciton_illegalClues
                                .addActionItem(illegalcluesMg2);
                        MainActivity.this.quickAciton_illegalClues
                                .addActionItem(illegalcluesAdd2);
                        MainActivity.this.quickAciton_illegalClues
                                .addActionItem(illegalcluesMg3);
                        MainActivity.this.quickAciton_illegalClues
                                .addActionItem(illegalcluesAdd3);
                        MainActivity.this.quickAciton_illegalClues
                                .addActionItem(illegalcluesMg4);
                        MainActivity.this.quickAciton_illegalClues
                                .addActionItem(illegalcluesAdd4);
                        MainActivity.this.quickAciton_illegalClues
                                .addActionItem(illegalcluesMg5);
                        MainActivity.this.quickAciton_illegalClues
                                .addActionItem(illegalcluesAdd5);
                        MainActivity.this.quickAciton_illegalClues
                                .addActionItem(illegalcluesMg6);
                        MainActivity.this.quickAciton_illegalClues
                                .addActionItem(illegalcluesAdd6);

                        MainActivity.this.quickAciton_illegalClues
                                .setAnimStyle(QuickActions.ANIM_AUTO);
                        if (MainActivity.this.mLandDetailsBackBtn.isShown()) {
                            MainActivity.this.finishActivity(3234);
                            MainActivity.this.mLandDetailsBackBtn
                                    .setVisibility(View.GONE);
                        }
                        MainActivity.this.quickAciton_illegalClues.show();
                    }
                });
    }

    /**
     * 初始化短信上报菜单
     */
    private void initSmsMenu() {
        // 短信上报
        final ActionItem smsUpStart = new ActionItem();
        smsUpStart.setIcon(getResources()
                .getDrawable(R.drawable.menu_sms_start));
        smsUpStart.setOnClickListener(new View.OnClickListener() {
            public void onClick(View paramView) {
                //MainActivity.this.quickAciton_sms.popupWindowDismiss();
                //MainActivity.this.openSms();
            }
        });
        final ActionItem smsUpStop = new ActionItem();
        smsUpStop.setIcon(getResources().getDrawable(
                R.drawable.menu_sms_shutdown));
        smsUpStop.setOnClickListener(new View.OnClickListener() {
            public void onClick(View paramView) {
                //MainActivity.this.quickAciton_sms.popupWindowDismiss();
                // if (MapOperate.IsStartTrackRecord())
                // MapOperate.SendTrackStopRecording();
                // MapOperate.SendLogoutInfo();
                // SysConfig.IsEnableSms = false;
                // ToastUtils.showLong(MainActivity.this, "短信关闭");
            }
        });
        final ActionItem smsSetting = new ActionItem();
        smsSetting.setIcon(getResources().getDrawable(
                R.drawable.menu_sms_setting));
        smsSetting.setOnClickListener(new View.OnClickListener() {
            public void onClick(View paramView) {
                //MainActivity.this.quickAciton_sms.popupWindowDismiss();
                //MainActivity.this.startActivity(new
                // Intent("com.mtkj.land.activity.SettingActivity.class));
            }
        });
        final ActionItem smsUpLands = new ActionItem();
        smsUpLands.setIcon(getResources().getDrawable(
                R.drawable.menu_sms_up_land));
        smsUpLands.setOnClickListener(new View.OnClickListener() {
            public void onClick(View paramView) {
                //MainActivity.this.quickAciton_sms.popupWindowDismiss();
                // if (SysConfig.IsEnableSms)
                // {
                //MainActivity.this.startActivity(new
                // Intent("com.mtkj.land.activity.ExportLandBySmsActivity.class));
                // return;
                // }
                //MainActivity.this.isOpenSmsAndSendLand();
            }
        });
        if (GisQueryApplication.getApp().getProjectconfig().exitFunctionCode("sms")) {
            findViewById(R.id.mainMenuSms).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.mainMenuSms).setVisibility(View.GONE);
        }
        findViewById(R.id.mainMenuSms).setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View paramView) {

                        // if (new
                        // SimCardManager().isSIMCardOk(MainActivity.this))
                        {
                            MainActivity.this.quickAciton_sms = new QuickActions(
                                    paramView);
                            MainActivity.this.quickAciton_sms
                                    .addActionItem(smsUpStop);
                            MainActivity.this.quickAciton_sms
                                    .addActionItem(smsUpStart);
                            MainActivity.this.quickAciton_sms
                                    .addActionItem(smsSetting);
                            MainActivity.this.quickAciton_sms
                                    .addActionItem(smsUpLands);
                            MainActivity.this.quickAciton_sms.setAnimStyle(4);
                            MainActivity.this.quickAciton_sms.show();
                        }
                        //MainActivity.this.showMsgDlg2("打开失败提示", new
                        // SimCardManager().readSIMCard(MainActivity.this));
                    }
                });
    }

    /**
     * 初始化我的任务菜单
     */
    private void initMyTaskMenu() {
        // 任务管理
        if (GisQueryApplication.getApp().getProjectconfig().exitFunctionCode("mytask")) {// mytask：在平板的配置文件里设置
            findViewById(R.id.mainMenuMyTask).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.mainMenuMyTask).setVisibility(View.GONE);
        }
        findViewById(R.id.mainMenuMyTask).setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View paramView) {

                        Intent localIntent = new Intent(
                                MainActivity.this, MyTaskActivity.class);
//                        localIntent.putExtra("taskType", "wfxs");
                        MainActivity.this.startActivity(localIntent);
                    }
                });
    }

    /**
     * 初始化地块分析菜单
     */
    private void initLandAnalysisMenu() {
        if (GisQueryApplication.getApp().getProjectconfig().exitFunctionCode("geoanalysis")) {
            findViewById(R.id.mainMenuLandManger_analysis).setVisibility(
                    View.VISIBLE);
        } else {
            findViewById(R.id.mainMenuLandManger_analysis).setVisibility(
                    View.GONE);
        }

        findViewById(R.id.mainMenuLandManger_analysis).setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View paramView) {
                        ImageButton btn_analysis = (ImageButton) paramView;
                        if (btn_analysis.isSelected()) {
                            normalStateStart();
                            geoAnalysisTool.dispose();
                            btn_analysis.setSelected(false);
                        } else {
                            normalStateStart();
                            collectStateStart();
                            geoAnalysisTool.init();
                            btn_analysis.setSelected(true);
                        }
                    }
                });
    }

    /**
     * 初始化采集菜单
     */
    private void initCollectPopMenu() {
        // 采集
        final ActionItem itemCreateLayer = new ActionItem();
        itemCreateLayer.setIcon(getResources().getDrawable(
                R.drawable.menu_new_edit_layer));
        itemCreateLayer.setOnClickListener(new View.OnClickListener() {
            public void onClick(View paramView) {
                // if (MainActivity.this.mCurrentMapState == 1)
                // {
                //MainActivity.this.isExitEditModeDlg();
                // return;
                // }
                //MainActivity.this.itemCreateLayerOnClick();
            }
        });
        final ActionItem itemEditLayer = new ActionItem();
        itemEditLayer.setIcon(getResources().getDrawable(
                R.drawable.menu_select_edit_layer));
        itemEditLayer.setOnClickListener(new View.OnClickListener() {
            public void onClick(View paramView) {
                // if (MainActivity.this.mCurrentMapState != 1)
                // {
                // if (MainActivity.this.mCurrentMapState != 2)
                // break label37;
                //MainActivity.this.measureStateStop();
                //MainActivity.this.itemEditLayerOnClick();
                // }
                // return;
                // if (MainActivity.this.mCurrentMapState == 3)
                // {
                // label37:MainActivity.this.queryStateStop();
                //MainActivity.this.itemEditLayerOnClick();
                // return;
                // }
                //MainActivity.this.itemEditLayerOnClick();
            }
        });
        if (GisQueryApplication.getApp().getProjectconfig().exitFunctionCode("collect")) {
            findViewById(R.id.mainMenuBtn01).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.mainMenuBtn01).setVisibility(View.GONE);
        }
        findViewById(R.id.mainMenuBtn01).setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View paramView) {
                        MainActivity.this.quickAction1 = new QuickActions(
                                paramView);
                        MainActivity.this.quickAction1
                                .addActionItem(itemCreateLayer);
                        MainActivity.this.quickAction1
                                .addActionItem(itemEditLayer);
                        MainActivity.this.quickAction1
                                .setAnimStyle(QuickActions.ANIM_AUTO);
                        MainActivity.this.quickAction1.show();
                    }
                });
    }

    /**
     * 初始化工具弹出菜单
     */
    private void initToolsPopMenu() {
        // 打开工程
//        final ActionItem toolsOpenPrj = new ActionItem();
//        toolsOpenPrj.setIcon(getResources().getDrawable(
//                R.drawable.menu_open_prj));
//        toolsOpenPrj.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View paramView) {
//                MainActivity.this.quickAciton_tools.popupWindowDismiss();
//                MainActivity.this
//                        .startActivity(new Intent(
//                                MainActivity.this, PrjSelectedActivity.class));
//            }
//        });
        // 坐标定位
        final ActionItem toolsLocationTo = new ActionItem();
        toolsLocationTo.setIcon(getResources().getDrawable(
                R.drawable.menu_tools_location_to));
        toolsLocationTo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View paramView) {
                MainActivity.this.quickAciton_tools.popupWindowDismiss();
                MainActivity.this.locationQuery();
            }
        });

        // 测量
        final ActionItem toolsMeasure = new ActionItem();
        toolsMeasure.setIcon(getResources().getDrawable(
                R.drawable.menu_tools_measure));
        toolsMeasure.setOnClickListener(new View.OnClickListener() {
            public void onClick(View paramView) {

                if (mCurrentMapState == MainActivity.MAP_MASURE_STATE) {
                    measuringTool.dispose();
                    mVgMesureToolbar.setVisibility(View.GONE);
                    mCurrentMapState = MainActivity.MAP_NORMAL_STATE;
                    quickAciton_tools.popupWindowDismiss();
                } else {
                    normalStateStart();
                    measuringTool.init();
                    mVgMesureToolbar.setVisibility(View.VISIBLE);
                    mCurrentMapState = MainActivity.MAP_MASURE_STATE;
                    quickAciton_tools.popupWindowDismiss();
                }
            }
        });
        // 指北针
        final ActionItem toolsCompass = new ActionItem();
        toolsCompass.setIcon(getResources().getDrawable(
                R.drawable.menu_tools_compass));
        toolsCompass.setOnClickListener(new View.OnClickListener() {
            public void onClick(View paramView) {

                if (MainActivity.this.compassTool.getState() == 0) {
                    MainActivity.this.compassTool.init();
                } else {
                    MainActivity.this.compassTool.dispose();
                }
            }
        });
//        // 视频通话
//        final ActionItem toolsVideoCall = new ActionItem();
//        toolsVideoCall.setIcon(getResources().getDrawable(
//                R.drawable.menu_tools_videocall));
//        toolsVideoCall.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View paramView) {
//                MainActivity.this.quickAciton_tools.popupWindowDismiss();
//                videoCallOptionsSelect();
//            }
//        });
        // 系统管理
        final ActionItem toolsSetting = new ActionItem();
        toolsSetting.setIcon(getResources().getDrawable(
                R.drawable.menu_sms_setting2));
        toolsSetting.setOnClickListener(new View.OnClickListener() {
            public void onClick(View paramView) {
                dismissAll();
                MainActivity.this
                        .startActivity(new Intent(
                                MainActivity.this, PrjSettingActivity.class)
                        );

            }
        });

        // 图层设置
        final ActionItem toolsLayerManager = new ActionItem();
        toolsLayerManager.setIcon(getResources().getDrawable(
                R.drawable.menu_tools_layer_setting));
        toolsLayerManager.setOnClickListener(new View.OnClickListener() {
            @SuppressWarnings("deprecation")
            public void onClick(View paramView) {
                showDialog(DIALOG_LAYERS_VISIBILITY);
            }
        });
        if (GisQueryApplication.getApp().getProjectconfig().exitFunctionCode("tools")) {
            findViewById(R.id.mainMenuTools).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.mainMenuTools).setVisibility(View.GONE);
        }

        findViewById(R.id.mainMenuTools).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View paramView) {

                        quickAciton_tools = new QuickActions(paramView);
//                        if (GisQueryApplication.getApp().getProjectconfig()
//                                .exitFunctionCode("openprj")) {
//                            quickAciton_tools.addActionItem(toolsOpenPrj);
//                        }
                        if (GisQueryApplication.getApp().getProjectconfig()
                                .exitFunctionCode("coordposition")) {
                            quickAciton_tools.addActionItem(toolsLocationTo);
                        }
                        if (GisQueryApplication.getApp().getProjectconfig()
                                .exitFunctionCode("measure")) {
                            quickAciton_tools.addActionItem(toolsMeasure);
                        }
                        if (GisQueryApplication.getApp().getProjectconfig()
                                .exitFunctionCode("compass")) {
                            quickAciton_tools.addActionItem(toolsCompass);
                        }
//                        if (GisQueryApplication.getApp().getProjectconfig()
//                                .exitFunctionCode("videocall")) {
//                            quickAciton_tools.addActionItem(toolsVideoCall);
//                        }
                        if (GisQueryApplication.getApp().getProjectconfig()
                                .exitFunctionCode("layermgr")) {
                            quickAciton_tools.addActionItem(toolsLayerManager);
                        }
                        if (GisQueryApplication.getApp().getProjectconfig()
                                .exitFunctionCode("sysmgr")) {
                            quickAciton_tools.addActionItem(toolsSetting);
                        }
                        quickAciton_tools.setAnimStyle(QuickActions.ANIM_AUTO);
                        if (!MainActivity.this.isDestroyed() && !quickAciton_tools.isShowing()) {
                            quickAciton_tools.show();
                        }

                    }
                });
    }

    /**
     * 视频通话选项
     */
    private void videoCallOptionsSelect() {
        String[] arrayOfString = {"一对一通话", "多人会议"};
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("视频通话选项")
                .setSingleChoiceItems(arrayOfString, -1,
                        new DialogInterface.OnClickListener() {
                            public void onClick(
                                    DialogInterface paramDialogInterface,
                                    int paramInt) {
                                paramDialogInterface.dismiss();
                                switch (paramInt) {
                                    case 0:
                                        MainActivity.this
                                                .startActivity(new Intent(
                                                        MainActivity.this, HallActivity.class));
                                        break;
                                    case 1:
                                        MainActivity.this
                                                .startActivity(new Intent(
                                                        MainActivity.this, MeetingActivity.class));
                                        break;
                                }
                            }
                        }).create().show();
    }

    /**
     * 采集地块
     */
    public void collectGeometry(Case caseObj, String caseType) {

        if (this.quickAciton_landManager != null)
            this.quickAciton_landManager.popupWindowDismiss();
        normalStateStart();
        collectStateStart();
        geoCollectTool.setCaseObj(caseObj);
        geoCollectTool.setCaseType(caseType);
        geoCollectTool.init();
    }

    public void camaraOpt() {
        onPhoto();
    }

    /**
     * 拍照功能
     */
    private void onPhoto() {
        if (Environment.getExternalStorageState().equals("mounted")) {
            String str1 = GisQueryApplication.getApp().getProjectPath() + "CameraPhoto/";
            String str2 = UtilsTools.GetCurrentFormatTime() + ".jpg";
            File localFile = new File(str1);
            if (!(localFile.exists()))
                localFile.mkdirs();
            this.m_CurrentCameraPhoto = str1 + str2;
//            Uri localUri = Uri.fromFile(new File(str1, str2));
//            Intent localIntent = new Intent(
//                    "android.media.action.IMAGE_CAPTURE");
//            localIntent.putExtra("output", localUri);
//            localIntent.putExtra("android.intent.extra.videoQuality", 0);
//            startActivityForResult(localIntent, 393217);

            Intent mOpenCameraIntent = new Intent();
            mOpenCameraIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);


            Uri desUri = CameraFilePathUtils.getFileUri(this,m_CurrentCameraPhoto);

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                //已申请camera权限
                //mOpenCameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }

            mOpenCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,desUri);
            startActivityForResult(mOpenCameraIntent,CameraFilePathUtils.getCameraPermissionRequestCode());

        } else {
            ToastUtils.showLong("无SD卡！");
        }

    }

    public String getM_CurrentCameraPhoto() {
        return m_CurrentCameraPhoto;
    }

    /**
     * 刷新GPS信息
     *
     * @param paramX
     * @param paramY
     */
    private void refreshGpsTextInfo(double paramX, double paramY) {
        String str = "";
        if (MapOperate.IsPrjIsWGS84()) {
            str = "经度:"
                    + MapOperate.FormatCoord(MathUtils.GetAccurateNumber(
                    paramX, 6))
                    + "  纬度:"
                    + MapOperate.FormatCoord(MathUtils.GetAccurateNumber(
                    paramY, 6));
        } else {

            str = "X = "
                    + MathUtils.round(paramX, 3)

                    + "  Y = "
                    + MathUtils.round(paramY, 3);
        }
        SysConfig.GPSInfo = str;
        this.mGpsInfoTextView.setText(str);
    }

    private ImageView mMapLegendImageView;

    /**
     * 初始化地图图例
     */
    private void initMapLegend() {
        this.findViewById(R.id.imgLegend).setOnClickListener(
                new OnClickListener() {

                    @SuppressWarnings("deprecation")
                    @Override
                    public void onClick(View arg0) {
                        if (findViewById(R.id.vg_maplegend).getVisibility() == View.GONE) {
                            mMapLegendImageView = (ImageView) findViewById(R.id.maplegendImage);
                            Display localDisplay = getWindowManager()
                                    .getDefaultDisplay();
                            Bitmap mBitmap = null;
                            try {
                                mBitmap = BitmapUtils.getBitmapByPath(new File(
                                                GisQueryApplication.currentProjectPath,
                                                "maplegend.png").getAbsolutePath(),
                                        localDisplay.getWidth(), localDisplay
                                                .getHeight());
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                            mMapLegendImageView
                                    .setOnTouchListener(new MTouchListener());
                            mMapLegendImageView.setImageBitmap(mBitmap);
                            matrix = new Matrix();
                            findViewById(R.id.vg_maplegend).setVisibility(
                                    View.VISIBLE);
                            findViewById(R.id.imgLegend).setSelected(true);
                        } else {
                            findViewById(R.id.vg_maplegend).setVisibility(
                                    View.GONE);
                            findViewById(R.id.imgLegend).setSelected(false);
                        }

                    }
                });
    }

    /**
     * 初始化信息查询工具
     */
    private void initIdentifyTool() {
        SimpleFillSymbol fillSymbol;
        SimpleMarkerSymbol markerSymbol = new SimpleMarkerSymbol(Color.BLUE,
                10, STYLE.DIAMOND);
        SimpleLineSymbol lineSymbol = new SimpleLineSymbol(Color.YELLOW, 3);
        fillSymbol = new SimpleFillSymbol(Color.argb(100, 0, 225, 255));
        fillSymbol.setOutline(new SimpleLineSymbol(Color.TRANSPARENT, 0));

        identifyTool = new IdentifyTool(map, MainActivity.this);
        Unit[] linearUnits = new Unit[]{Unit.create(LinearUnit.Code.METER)};
        identifyTool.setLinearUnits(linearUnits);
        identifyTool.setMarkerSymbol(markerSymbol);
        identifyTool.setLineSymbol(lineSymbol);
        identifyTool.setFillSymbol(fillSymbol);
        identifyTool.init();
    }

    /**
     * 初始化测量工具
     */
    private void initMeasureTool() {
        SimpleFillSymbol fillSymbol;
        SimpleMarkerSymbol markerSymbol = new SimpleMarkerSymbol(Color.BLUE,
                10, STYLE.DIAMOND);
        SimpleLineSymbol lineSymbol = new SimpleLineSymbol(Color.YELLOW, 3);
        fillSymbol = new SimpleFillSymbol(Color.argb(100, 0, 225, 255));
        fillSymbol.setOutline(new SimpleLineSymbol(Color.TRANSPARENT, 0));

        measuringTool = new MeasuringTool(map, MainActivity.this);
        Unit[] linearUnits = new Unit[]{Unit.create(LinearUnit.Code.METER)};
        measuringTool.setLinearUnits(linearUnits);
        measuringTool.setMarkerSymbol(markerSymbol);
        measuringTool.setLineSymbol(lineSymbol);
        measuringTool.setFillSymbol(fillSymbol);
    }

    /**
     * 初始化指北针工具
     */
    private void initCompassTool() {
        this.compassTool = new CompassTool(this);
    }

    /**
     * 初始化图形采集工具
     */
    private void initGeometryCollectTool() {
        if (bufferSeekbarOpt == null)
            bufferSeekbarOpt = new BufferSeekbarOpt(map, this);
        SimpleMarkerSymbol markerSymbol = new SimpleMarkerSymbol(Color.BLUE,
                10, STYLE.DIAMOND);
        SimpleLineSymbol lineSymbol = new SimpleLineSymbol(Color.YELLOW, 3);
        SimpleFillSymbol fillSymbol = new SimpleFillSymbol(Color.argb(100, 0,
                225, 255));
        fillSymbol.setOutline(new SimpleLineSymbol(Color.TRANSPARENT, 0));

        geoCollectTool = new GeometryCollectTool(map, bufferSeekbarOpt,
                MainActivity.this);
        geoCollectTool.setMarkerSymbol(markerSymbol);
        geoCollectTool.setLineSymbol(lineSymbol);
        geoCollectTool.setFillSymbol(fillSymbol);
        MapOperate.geoCollectTool = geoCollectTool;

    }

    /**
     * 初始化图形分析工具
     */
    private void initGeometryAnalysisTool() {
        if (bufferSeekbarOpt == null)
            bufferSeekbarOpt = new BufferSeekbarOpt(map, this);
        SimpleMarkerSymbol markerSymbol = new SimpleMarkerSymbol(Color.BLUE,
                10, STYLE.DIAMOND);
        SimpleLineSymbol lineSymbol = new SimpleLineSymbol(Color.YELLOW, 3);
        SimpleFillSymbol fillSymbol = new SimpleFillSymbol(Color.argb(100, 0,
                225, 255));
        fillSymbol.setOutline(new SimpleLineSymbol(Color.TRANSPARENT, 0));

        geoAnalysisTool = new GeometryAnalysisTool(map, bufferSeekbarOpt,
                MainActivity.this);
        geoAnalysisTool.setMarkerSymbol(markerSymbol);
        geoAnalysisTool.setLineSymbol(lineSymbol);
        geoAnalysisTool.setFillSymbol(fillSymbol);
        MapOperate.geoAnalysisTool = geoAnalysisTool;
        GisQueryApplication.getApp().setGeoAnalysisTool(geoAnalysisTool);
    }

    private void isStopTracking() {
        AlertDialog.Builder localBuilder = new AlertDialog.Builder(MainActivity.this);
        localBuilder.setTitle("提示");
        localBuilder.setPositiveButton("是",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface paramDialogInterface,
                                        int paramInt) {
                        TrackLayerOpt.showRecordAtrrDlg(MainActivity.this,track_selected);
                        TrackingShowService.state = TrackingShowService.TRACK_OPT_3_STOP;
                        Intent localIntent = new Intent();
                        localIntent.setAction("com.guyu.android.gis.localservice.TrackingShowService");
                        localIntent.setPackage(getPackageName());
                        TrackingShowService.state = TrackingShowService.TRACK_OPT_3_STOP;
                        localIntent.putExtra("opt", TrackingShowService.TRACK_OPT_3_STOP);


                        stopService(localIntent);
                        MainActivity.this.trackStopOpt();
                    }
                });
        localBuilder.setNegativeButton("否", null);
        localBuilder.setMessage("停止轨迹记录?");
        localBuilder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 8196:
                GisQueryApplication.getApp().startGPS(true);
                break;

            case 8200:
                this.mHandler.sendEmptyMessage(12288);
                break;

            case 3237:
                if (resultCode == -1) {
                    float d1 = (float) data.getDoubleExtra("dLon", 0.0D);
                    float d2 = (float) data.getDoubleExtra("dLat", 0.0D);
                    if (UtilsTools.IsGpsDataValid(d1, d2)) {
                        Point point = new Point(d1, d2);
                        locationLayer = MapOperate.getLocationShowLayer();
                        if (locationID == -1) {
                            locationSymbol = new PictureMarkerSymbol(this
                                    .getResources().getDrawable(
                                            R.drawable.icon_location));
                            locationID = locationLayer
                                    .addGraphic(locationGraphic = new Graphic(
                                            point, locationSymbol));
                        } else {
                            locationLayer.updateGraphic(locationID, point);
                        }
                        zoomToCurrentLocation(point, 10000);
                    } else {
                        Log.e("坐标定位", "坐标无效");
                    }
                } else {
                    MainActivity.this.finishActivity(3237);
                }
                break;
            case 9999:
                screenBitmap = ClipActivity.getInstance().getBitmap();
                setResult(RESULT_OK);
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);

    }

    private void registerSensor() {
        if (sensorManager == null || sensorListener == null)
            setSensor();
        if (Build.VERSION.SDK_INT >= 18) {
            sensorManager.registerListener(sensorListener, aSensor,
                    SensorManager.SENSOR_DELAY_NORMAL);
            sensorManager.registerListener(sensorListener, mSensor,
                    SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            sensorManager.registerListener(sensorListener, orientationSensor,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }

    }

    @Override
    protected void onStart() {
        Log.d(TAG, "onStart");
        super.onStart();
        if (GisQueryApplication.getApp().getProjectconfig().getUsebluetoothcollect()) {
            registerBluetoothReceiver();
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterBluetooth();
        unregisteredSensor();
        if (this.compassTool != null && this.compassTool.getState() == 1) {
            this.compassTool.dispose();
        }
    }

    @Override
    protected synchronized void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();

        // if (map == null)
        // return;
        //
        // String mapState = GisQueryApplication.getApp().readMapState();
        //
        // if (mapState == null)
        // return;
        //
        // map.restoreState(mapState);

        registerSensor();
        if (this.compassTool != null && this.compassTool.getState() == 1) {
            this.compassTool.init();
        }
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause");
        super.onPause();

        if (map == null || !map.isLoaded())
            return;

        if (GisQueryApplication.getApp().readBooleanApplicationPrefecence(GisQueryApplication.MAP_STATE_SAVED))
            return;

        GisQueryApplication.getApp().writeApplicationPreference(GisQueryApplication.MAP_STATE,
                map.retainState());
        GisQueryApplication.getApp().writeApplicationPreference(
                GisQueryApplication.MAP_STATE_SAVED, true);
    }

    private void unregisteredSensor() {
        if (sensorManager == null || sensorListener == null)
            return;

        sensorManager.unregisterListener(sensorListener);
    }

    private void unregisterBluetooth() {
        if (bluetoothSerialService != null) {
            bluetoothSerialService.stop();
            bluetoothSerialService = null;
        }

        if (bluetoothAdapter != null) {
            bluetoothAdapter.cancelDiscovery();
            bluetoothAdapter = null;
        }

        if (bluetoothReceiver == null)
            return;

        unregisterReceiver(bluetoothReceiver);
    }

    /**
     * 开启采集状态
     */
    private void collectStateStart() {
        // 重置测量
        mVgMesureToolbar.setVisibility(View.GONE);
        this.measuringTool.dispose();
        mCurrentMapState = MainActivity.MAP_COLLECT_STATE;
        this.mTopCollect.setVisibility(View.VISIBLE);
        this.mTopTitle.setVisibility(View.INVISIBLE);
    }

    /**
     * 回归正常状态
     */
    private void normalStateStart() {
        mCurrentMapState = MainActivity.MAP_NORMAL_STATE;
        // 重置添加地块和分析
        findViewById(R.id.mainMenuLandManger_analysis).setSelected(false);
        this.mTopTitle.setVisibility(View.VISIBLE);
        this.mTopCollect.setVisibility(View.INVISIBLE);
        this.geoAnalysisTool.dispose();
        this.geoCollectTool.dispose();
        // 重置测量
        mVgMesureToolbar.setVisibility(View.GONE);
        this.measuringTool.dispose();
        MapOperate.curGeometry = null;
    }

    public void dismissAll() {
        if (quickAction1 != null) {
            quickAction1.popupWindowDismiss();
            quickAction1 = null;
        }
        if (quickAction4 != null) {
            quickAction4.popupWindowDismiss();
            quickAction4 = null;
        }
        if (quickAciton_tools != null) {
            quickAciton_tools.popupWindowDismiss();
            quickAciton_tools = null;
        }
        if (quickAciton_landManager != null) {
            quickAciton_landManager.popupWindowDismiss();
            quickAciton_landManager = null;
        }
        if (quickAciton_illegalClues != null) {
            quickAciton_illegalClues.popupWindowDismiss();
            quickAciton_illegalClues = null;
        }
        if (quickAciton_sms != null) {
            quickAciton_sms.popupWindowDismiss();
            quickAciton_sms = null;
        }
        System.gc();
    }

    @Override
    protected void onDestroy() {
        mainActivity = null;
        if (screenBitmap != null && !screenBitmap.isRecycled()) {
            screenBitmap.recycle();
            screenBitmap = null;
        }
        super.onDestroy();
        if (broadcastReceiver != null) {
            unregisterReceiver(broadcastReceiver);
        }
        dismissAll();
        Log.d(TAG, "onDestroy");
        // 销毁前先关闭蓝牙
        openOrCloseBlueToothDevices(false);
        if (map != null) {
            map.removeAll();
        }
        GisQueryApplication.getApp().disposeOfflineDB();


    }

    /**
     * 按回退按钮
     */
    @Override
    public void onBackPressed() {
        if (mCurrentMapState == MainActivity.MAP_NORMAL_STATE) {
            // 检查是否正在巡查状态
//            if (MapOperate.IsTrackWorking()) {
//                isStopTracking();
//            } else {
                isExitSys();
//            }
        } else {
            switch (mCurrentMapState) {
                case MainActivity.MAP_COLLECT_STATE: {
                    normalStateStart();
                    break;
                }
                case MainActivity.MAP_MASURE_STATE: {
                    normalStateStart();
                    break;
                }
                case MainActivity.MAP_QUERY_STATE: {
                    normalStateStart();
                    break;
                }
            }
        }

    }

    /**
     * 判断是否退出系统
     */
    private void isExitSys() {
        AlertDialog.Builder localBuilder = new AlertDialog.Builder(MainActivity.this);
        localBuilder.setTitle("提示");
        localBuilder.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface paramDialogInterface,
                                        int paramInt) {
                        Log.i("Mucai-用户操作", "退出系统");
                        MainActivity.this.moveTaskToBack(true);
                        new MainActivity.ExitTask().execute(new Void[0]);
                    }
                });
        localBuilder.setNegativeButton("取消", null);
        localBuilder.setMessage("确定退出系统？");
        localBuilder.show();
    }

    private class ExitTask extends AsyncTask<Void, String, Boolean> {
        private ProgressDialog dialog = null;

        @Override
        protected Boolean doInBackground(Void... arg0) {
            MainActivity.this.exitSys();
            return Boolean.valueOf(true);
        }

        @Override
        public void onPostExecute(Boolean paramBoolean) {
            MainActivity.this.finishActivity(3234);
            MainActivity.this.finishActivity(3236);
            MainActivity.this.finishActivity(3238);
            MainActivity.this.finishActivity(3239);
            MainActivity.this.finishActivity(3240);
            this.dialog.dismiss();
            MainActivity.this.finish();
        }

        @Override
        protected void onPreExecute() {
            this.dialog = new ProgressDialog(MainActivity.this);
            this.dialog.setMessage("正在保存数据...");
            this.dialog.setCancelable(false);
            this.dialog.show();
        }

    }


    public void exitSys() {
        if (map != null) {
            map.removeAll();
        }

        GisQueryApplication.getApp().closeApp();
    }

    private void createLayers() {
        final int layerConfigNum = GisQueryApplication.getApp().getProjectconfig().getBasemaps()
                .size()
                + GisQueryApplication.getApp().getProjectconfig().getOperationallayers().size();
        map.setOnStatusChangedListener(new OnStatusChangedListener() {
            private static final long serialVersionUID = 1L;

            @Override
            public void onStatusChanged(Object source, STATUS status) {
                Log.d(TAG, "onStatusChanged --> " + source.getClass().getName()
                        + ", " + status);
                if (source == map && status == STATUS.INITIALIZED) {
                    setDefaultMapExtent();
                    GisQueryApplication.getApp().setHand(MainActivity.this.mHandler);
                    MapOperate.initParams(
                            MainActivity.this, map, mHandler);
                    trackShowLayer = MapOperate.getTrackingLayer();
                    gpsShowLayer = MapOperate.getGpsShowLayer();
                    initWidgets();
                    // 恢复上一次图层的勾选设置
                    layersVisibility();
                    initMapLegend();
                    initIdentifyTool();
                    initMeasureTool();
                    initCompassTool();
                    initGeometryCollectTool();
                    initGeometryAnalysisTool();
                    menuControl();
                    maploaded = true;
                    if (TaskUpdateService.isServiceStart()) {
                        startLocalServices();
                    }

                } else if ((source instanceof ArcGISLocalTiledLayer
                        || source instanceof ArcGISTiledMapServiceLayer
                        || source instanceof ArcGISFeatureLayer
                        || source instanceof FeatureLayer || source instanceof ArcGISDynamicMapServiceLayer)
                        && status == STATUS.INITIALIZATION_FAILED) {

                    System.out.println("启高-图层初始化失败：status"
                            + status);
                    Log.d("图层初始化失败-->", ((Layer) source).getName() + "---"
                            + ((Layer) source).isVisible() + ".status=" + status + "---失败信息:"
                            + status.getError().getDescription());
                    loadedCount++;
                } else if ((source instanceof ArcGISLocalTiledLayer
                        || source instanceof ArcGISTiledMapServiceLayer
                        || source instanceof ArcGISFeatureLayer
                        || source instanceof FeatureLayer || source instanceof ArcGISDynamicMapServiceLayer)
                        && status == STATUS.LAYER_LOADED) {

                    System.out.println("启高-图层加载成功：status"
                            + status);
                    loadedCount++;
                } else if ((source instanceof ArcGISLocalTiledLayer
                        || source instanceof ArcGISTiledMapServiceLayer
                        || source instanceof ArcGISFeatureLayer
                        || source instanceof FeatureLayer || source instanceof ArcGISDynamicMapServiceLayer)
                        && status == STATUS.LAYER_LOADING_FAILED) {
                    System.out.println("启高-图层加载失败：status"
                            + status);
                    Log.d("图层加载失败-->", ((Layer) source).getName() + "---"
                            + ((Layer) source).isVisible() + ".status=" + status + "---失败信息:"
                            + status.getError().getDescription());
                    loadedCount++;
                }

                if (
                        status == STATUS.LAYER_LOADED &&
                                loadedCount == layerConfigNum && maploaded == false) {
                    GisQueryApplication.getApp().setHand(MainActivity.this.mHandler);
                    MapOperate.initParams(
                            MainActivity.this, map, mHandler);
                    trackShowLayer = MapOperate.getTrackingLayer();
                    gpsShowLayer = MapOperate.getGpsShowLayer();
                    initWidgets();
                    // 恢复上一次图层的勾选设置
                    layersVisibility();
                    initMapLegend();
                    initIdentifyTool();
                    initMeasureTool();
                    initCompassTool();
                    initGeometryCollectTool();
                    initGeometryAnalysisTool();
                    menuControl();
                    maploaded = true;
                    if (TaskUpdateService.isServiceStart()) {
                        startLocalServices();
                    }
                }


            }
        });

        map.setOnPinchListener(new OnPinchListener() {

            private static final long serialVersionUID = 1L;

            @Override
            public void postPointersDown(float x1, float y1, float x2,
                                         float y2, double factor) {
                // TODO Auto-generated method stub

            }

            @Override
            public void postPointersMove(float x1, float y1, float x2,
                                         float y2, double factor) {
                // TODO Auto-generated method stub
                updateScaleText();
            }

            @Override
            public void postPointersUp(float x1, float y1, float x2, float y2,
                                       double factor) {
                // TODO Auto-generated method stub

            }

            @Override
            public void prePointersDown(float x1, float y1, float x2, float y2,
                                        double factor) {
                // TODO Auto-generated method stub

            }

            @Override
            public void prePointersMove(float x1, float y1, float x2, float y2,
                                        double factor) {
                // TODO Auto-generated method stub

            }

            @Override
            public void prePointersUp(float x1, float y1, float x2, float y2,
                                      double factor) {
                // TODO Auto-generated method stub

            }
        });
        map.setOnZoomListener(new OnZoomListener() {
            private static final long serialVersionUID = 1L;

            @Override
            public void postAction(float arg0, float arg1, double arg2) {
                // TODO Auto-generated method stub
                updateScaleText();
            }

            @Override
            public void preAction(float arg0, float arg1, double arg2) {
                // TODO Auto-generated method stub

            }

        });


        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (!GisQueryApplication.getApp().isLayerLoaded()) {
                        Thread.sleep(10);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                ArrayList<Layer> layers = GisQueryApplication.getApp().getLayers();
                Layer t = null;
                for (Layer layer : layers) {
//                    if(layer.getName().equals("lj_clip_ProjectRaster")){
//                        t=layer;
//                        continue;
//                    }
                    map.addLayer(layer);
                }
//                if(t!=null){
//                    map.addLayer(t);
//                }

            }
        }).start();


    }


    @SuppressWarnings("deprecation")
    void setSensor() {
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (Build.VERSION.SDK_INT >= 18) {
            aSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            mSensor = sensorManager
                    .getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        } else {
            orientationSensor = sensorManager
                    .getDefaultSensor(Sensor.TYPE_ORIENTATION);
        }

    }

    private void setDefaultMapExtent() {
        initialExtent = GisQueryApplication.getApp().getProjectconfig().getInitialextent();// 初始化视野
        fullExtent = GisQueryApplication.getApp().getProjectconfig().getFullextent();// 全图视野
        map.setExtent(initialExtent);
        updateScaleText();
    }

    public void trackStopOpt() {
        mTrackStartBtn.setSelected(false);
        itemCreateTrackSelectedOnClick();
        MapOperate.TrackFinishRecord();
        stopTrackShowing();
        nRecordCount = 0;
        mTrackInfoTextView.setText("" + nRecordCount);
        ToastUtils.showLong("已停止记录!");
    }



    protected void itemCreateTrackSelectedOnClick() {
        Log.i("Mucai-用户操作", "创建轨迹--子菜单点击事件");
        if (this.quickAction4 == null)
            return;
        quickAction4.popupWindowDismiss();
        quickAction4.setActionItemVisible(0, false);
        quickAction4.setActionItemVisible(1, true);
        mVgTrackToolbar.setVisibility(View.INVISIBLE);
    }

    private void initWidgets() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        this.m_nScreenHeight = dm.heightPixels;
        this.m_nScreenWidth = dm.widthPixels;

        this.mVgTrackToolbar = ((LinearLayout) findViewById(R.id.vg_track_toolbar));
        this.mTrackStartBtn = ((ImageButton) findViewById(R.id.imgTrackStart));
        btnTrackSave = (Button) findViewById(R.id.btnTrackSave);
        btnTrackStop = (Button) findViewById(R.id.btnTrackStop);
        this.mTrackSettingBtn = ((ImageButton) findViewById(R.id.imgTrackSetting));
        // this.mTrackImportBtn = ((ImageButton)
        // findViewById(R.id.imgTrackImport));
        SysConfig.TrackType = GisQueryApplication.getApp().GetIntConfigData(
                "TRACK_SET_TYPE", 0);
        SysConfig.TrackSetTime = GisQueryApplication.getApp().GetIntConfigData(
                "TRACK_SET_TIME", 5);
        SysConfig.TrackSetDis = GisQueryApplication.getApp().GetIntConfigData(
                "TRACK_SET_DIS", 5);
        btnTrackSave.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                TrackLayerOpt.showRecordAtrrDlg(MainActivity.this,track_selected);
                TrackingShowService.state = TrackingShowService.TRACK_OPT_3_STOP;
                Intent localIntent = new Intent();
                localIntent.setAction("com.guyu.android.gis.localservice.TrackingShowService");
                localIntent.setPackage(getPackageName());
                TrackingShowService.state = TrackingShowService.TRACK_OPT_3_STOP;
                localIntent.putExtra("opt", TrackingShowService.TRACK_OPT_3_STOP);


                stopService(localIntent);
                MainActivity.this.trackStopOpt();
            }
        });
        btnTrackStop.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                TrackingShowService.state = TrackingShowService.TRACK_OPT_3_STOP;
                Intent localIntent = new Intent();
                localIntent.setAction("com.guyu.android.gis.localservice.TrackingShowService");
                localIntent.setPackage(getPackageName());
                TrackingShowService.state = TrackingShowService.TRACK_OPT_3_STOP;
                localIntent.putExtra("opt", TrackingShowService.TRACK_OPT_3_STOP);


                stopService(localIntent);
                MainActivity.this.trackStopOpt();
            }
        });
        this.mTrackStartBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                trackStartOrPauseOpt(v);
            }
        });
        this.mTrackSettingBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(
                        new Intent(
                                MainActivity.this, TrackSettingActivity.class),
                        8200);
            }
        });

        fadeinAnimation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.appear);
        fadeoutAnimation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.disappear);
        mLandDetailsBackBtn = ((ImageButton) findViewById(R.id.imgBtnBackToLandInfos));
        mLandDetailsBackBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View paramView) {
                // 返回案件详细信息
                if (optCode == 4367) {
                    MainActivity.this.normalStateStart();
                    Intent localIntent = new Intent(
                            MainActivity.this, CaseDetailsActivity.class);// 启高
                    Case caseObj = MainActivity.this.geoCollectTool
                            .getCaseObj();
                    localIntent.putExtra("caseId", caseObj.getCaseId());
                    localIntent.putExtra("caseType", caseObj.getCaseType());
                    MainActivity.this.startActivity(localIntent);
                    MainActivity.this.mLandDetailsBackBtn
                            .setVisibility(View.INVISIBLE);
                } else if (optCode == 4368) {
                    // 返回地块分析详细界面
                    Intent localIntent = new Intent(
                            MainActivity.this, LandAnalysisRecordDetailsActivity.class);
                    localIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    MainActivity.this.startActivity(localIntent);
                    MainActivity.this.mLandDetailsBackBtn
                            .setVisibility(View.INVISIBLE);
                } else if (optCode == 4369) {
                    // 返回轨迹管理界面
                    Intent localIntent = new Intent(
                            MainActivity.this, TrackManagerActivity.class);
                    localIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    MainActivity.this.startActivityForResult(localIntent, 3238);
                    MainActivity.this.mLandDetailsBackBtn
                            .setVisibility(View.INVISIBLE);
                } else if (optCode == 4371) {
                    // 返回任务详细界面
                    Intent localIntent = new Intent(
                            MainActivity.this, TaskDetailsActivity.class);
                    localIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    localIntent.putExtra("opt", 5566);
                    MainActivity.this.startActivityForResult(localIntent, 3240);
                    MainActivity.this.mLandDetailsBackBtn
                            .setVisibility(View.INVISIBLE);
                } else if (optCode == 4372) {
                    // 返回轨迹详细信息界面
                    Intent localIntent = new Intent(
                            MainActivity.this, TrackDetailsActivity.class);
                    localIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    MainActivity.this.startActivityForResult(localIntent, 3238);
                    MainActivity.this.mLandDetailsBackBtn
                            .setVisibility(View.INVISIBLE);
                } else if (optCode == 4374) {
                    MainActivity.this.normalStateStart();
                    Intent localIntent = new Intent(
                            MainActivity.this, CaseDetailsActivity.class);
                    MainActivity.this.startActivity(localIntent);
                    MainActivity.this.mLandDetailsBackBtn
                            .setVisibility(View.INVISIBLE);
                }

            }
        });
        mCameraButton = ((ImageButton) findViewById(R.id.imgCamara));
        mCameraButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View paramView) {
                MainActivity.this.camaraOpt();
            }
        });
        mLocationButton = (ImageButton) findViewById(R.id.imgLocation);
        mLocationButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View paramView) {
                MainActivity.this.locationOpt();
            }
        });

        imgScreen = (ImageButton) findViewById(R.id.imgScreen);
        if (getScreenBitmap) {
            imgScreen.setVisibility(View.VISIBLE);
        }
        imgScreen.setOnClickListener(new View.OnClickListener() {
            public void onClick(View paramView) {
                getScreenBitmap();
                Intent intent = new Intent(mainActivity, ClipActivity.class);
                startActivityForResult(intent, 9999);
            }
        });
        zoomfullButton = (ImageButton) findViewById(R.id.img_full_extend);
        zoomfullButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                map.setExtent(fullExtent);
                updateScaleText();
            }
        });
        zoominButton = (ImageButton) findViewById(R.id.img_zoom_in);
        zoominButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//				map.zoomin(true);
                if (zoomIndex != zoom.length - 1) {
                    map.zoomToScale(map.getCenter(), zoom[++zoomIndex]);
                    updateScaleText();
                }

            }
        });
        zoomoutButton = (ImageButton) findViewById(R.id.img_zoom_out);
        zoomoutButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//				map.zoomout(true);
                if (zoomIndex != 0) {
                    map.zoomToScale(map.getCenter(), zoom[--zoomIndex]);
                    updateScaleText();
                }
            }
        });

        makeWidgetsVisible();
    }

    public void locationOpt() {
        if (!(GisQueryApplication.getApp().isGpsEnable())) {
            IsOpenGPSDlg(R.string.is_open_gps);
        } else {
            locationOnMap();
        }

    }

    public void locationOnMap() {
        ToastUtils.showLong("GPS定位中...");
        NaviGPS localNaviGPS = GisQueryApplication.getApp().getNaviGps();


        if (UtilsTools.IsGpsDataValid(localNaviGPS.dLongitude,
                localNaviGPS.dLatitude)) {
            Point mapPointJWD = new Point(localNaviGPS.dLongitude,
                    localNaviGPS.dLatitude);
            Point mapPoint = Utility.fromWgs84ToMap2(mapPointJWD,
                    map.getSpatialReference());
            gpsShowLayer.setVisible(true);
            if (gpsID == -1) {
                // icon_landing_arrow
                gpsMarkerSymbol = new PictureMarkerSymbol(ContextCompat
                        .getDrawable(MainActivity.this,R.drawable.ic_gps_navigation));
                gpsID = gpsShowLayer.addGraphic(gpsGra = new Graphic(mapPoint,
                        gpsMarkerSymbol));
            } else {
                gpsShowLayer.updateGraphic(gpsID, mapPoint);
            }

            zoomToCurrentLocation(mapPoint, map.getScale());
        }
    }

    public void IsOpenGPSDlg(int paramInt) {
        AlertDialog.Builder localBuilder = new AlertDialog.Builder(MainActivity.this);
        localBuilder.setTitle(R.string.gps_closed);
        localBuilder.setPositiveButton(R.string.setting,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface paramDialogInterface,
                                        int paramInt) {
                        Intent localIntent = new Intent();
                        localIntent
                                .setAction("android.settings.LOCATION_SOURCE_SETTINGS");
                        MainActivity.this.startActivityForResult(localIntent,
                                8196);
                    }
                });
        localBuilder.setNegativeButton(R.string.cancel,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface paramDialogInterface,
                                        int paramInt) {
                        MainActivity.this.setUseGps(false);
                        MainActivity.this.mHandler.sendEmptyMessage(8197);
                    }
                });
        localBuilder.setMessage(paramInt);
        localBuilder.show();
    }

    // private void gpsShowOpt() {
    // this.mGpsInfoLayout.setVisibility(View.VISIBLE);
    // this.mGuideInfoLayout.setVisibility(View.VISIBLE);
    // this.mHideStateLayout.setVisibility(View.VISIBLE);
    // }

    // private void guideHideOpt() {
    // this.mGuideInfoLayout.setVisibility(8);
    // this.mGpsInfoLayout.setVisibility(0);
    // }
    //
    // private void guideShowOpt() {
    // this.mGuideInfoLayout.setVisibility(8);
    // this.mHideStateLayout.setVisibility(0);
    // }
    //
    // private void hideShowOpt() {
    // this.mHideStateLayout.setVisibility(8);
    // this.mGpsInfoLayout.setVisibility(0);
    // }

    private void updateScaleText() {
        HnTimer.setTimeout(500, new HnTimer.OnCompletedListener() {
            public void onCompleted() {
                TextView tvScale = (TextView) findViewById(R.id.tvScale);
                tvScale.setText("1:" + (int) (map.getScale()));
            }
        });

    }

    /**
     * 新打开触发
     */
    protected void onNewIntent(Intent paramIntent) {
        optCode = paramIntent.getIntExtra("opt", 0);
        switch (optCode) {
            case 4367:
            case 4368:
            case 4369:
            case 4371:
            case 4372:
            case 4373:
                this.mLandDetailsBackBtn.setVisibility(View.VISIBLE);
                break;

        }
        super.onNewIntent(paramIntent);
    }

    private void makeWidgetsVisible() {
        if (GisQueryApplication.getApp().getProjectconfig().getUsebluetoothcollect()) {
            findViewById(R.id.imgBlueToothCollect).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.imgBlueToothCollect).setVisibility(View.GONE);
        }
        zoominButton.setAnimation(fadeinAnimation);
        zoominButton.setVisibility(View.VISIBLE);
        zoomoutButton.setAnimation(fadeinAnimation);
        zoomoutButton.setVisibility(View.VISIBLE);
        zoomfullButton.setAnimation(fadeinAnimation);
        zoomfullButton.setVisibility(View.VISIBLE);
    }

    /**
     * Method mapWidgetsVisibility.
     *
     * @param visible boolean
     */
    void mapWidgetsVisibility(boolean visible) {
        Animation animation = (visible) ? fadeinAnimation : fadeoutAnimation;
        int visibility = (visible) ? View.VISIBLE : View.GONE;

        zoomfullButton.setAnimation(animation);
        zoomfullButton.setVisibility(visibility);
        compassButton.setAnimation(animation);
        compassButton.setVisibility(visibility);
        gpsToggleButton.setAnimation(animation);
        gpsToggleButton.setVisibility(visibility);
    }

    protected void layersVisibility() {
        boolean layersDialog = GisQueryApplication.getApp().readBooleanApplicationPrefecence(GisQueryApplication.LAYERS_VISIBILITY);
        if (layersDialog) {
            boolean isVisible;
            for (Layer layer : map.getLayers()) {
                if (layer instanceof ArcGISLocalTiledLayer
                        || layer instanceof ArcGISTiledMapServiceLayer
                        || layer instanceof ArcGISFeatureLayer
                        || layer instanceof FeatureLayer
                        || layer instanceof ArcGISDynamicMapServiceLayer) {
                    isVisible = GisQueryApplication.getApp().readBooleanApplicationPrefecence(layer.getName());
                    layer.setVisible(isVisible);
                }
            }
        }

        getVisibleLayers();
    }

    private void getVisibleLayers() {
        visibleLayers = new ArrayList<Layer>();
        for (Layer layer : map.getLayers()) {
            if (layer instanceof ArcGISLocalTiledLayer
                    || layer instanceof ArcGISTiledMapServiceLayer
                    || layer instanceof ArcGISFeatureLayer
                    || layer instanceof FeatureLayer
                    || layer instanceof ArcGISDynamicMapServiceLayer)
                visibleLayers.add(layer);
        }
    }

    // **********************************************
    // Main Menu
    // **********************************************

    /**
     * Method onCreateOptionsMenu.
     *
     * @param menu Menu
     * @return boolean
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mapview_menu, menu);

        return true;
    }

    /**
     * Method onOptionsItemSelected.
     *
     * @param item MenuItem
     * @return boolean
     */
    @SuppressWarnings("deprecation")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (!map.isLoaded())
            return false;

        switch (item.getItemId()) {
            case R.id.maplayers_menuitem:
                showDialog(DIALOG_LAYERS_VISIBILITY);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Method onCreateDialog.
     *
     * @param id int
     * @return Dialog
     */
    @Override
    protected Dialog onCreateDialog(int id) {
        dialog = null;

        switch (id) {
            case DIALOG_LAYERS_VISIBILITY:
                dialog = createLayersVisibilityDialog();
                break;
            case DIALOG_BLUETOOTH_DEVICES:
                dialog = createBluetoothDevicesDialog();
                break;
            default:
                dialog = null;
                break;
        }

        return dialog;
    }

    /**
     * Method onPrepareDialog.
     *
     * @param id          int
     * @param localDialog Dialog
     */
    @Override
    protected void onPrepareDialog(int id, Dialog localDialog) {

    }

    /**
     * Method 创建图层可见对话框
     *
     * @return Dialog
     */
    @SuppressLint("InflateParams")
    private Dialog createLayersVisibilityDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        final View view = getLayoutInflater().inflate(R.layout.maplayersview,
                null);

        builder.setTitle(R.string.maplayers);
        builder.setIcon(R.drawable.maplayers);
        builder.setView(view);
        Dialog localDialog = builder.create();
        ArrayList<LayerConfig> al_basemaps = new ArrayList<>();
        al_basemaps.addAll(GisQueryApplication.getApp().getProjectconfig()
                .getBasemaps());
        al_basemaps.addAll(GisQueryApplication.getApp().getProjectconfig()
                .getOperationallayers());
        Map<String, String> nameMap = new HashMap();
        for (LayerConfig l : al_basemaps) {
            nameMap.put(l.getLabel(), l.getName());
        }

        LayersAdapter layersAdapter = new LayersAdapter(this, visibleLayers, nameMap,
                R.layout.maplayer_item);
        ListView list = (ListView) view.findViewById(R.id.maplayerslistview);
        list.setAdapter(layersAdapter);

        localDialog
                .setOnDismissListener(new android.content.DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        int i = 0;

                        for (Layer layer : map.getLayers()) {
                            if (layer instanceof ArcGISLocalTiledLayer
                                    || layer instanceof ArcGISTiledMapServiceLayer
                                    || layer instanceof ArcGISFeatureLayer
                                    || layer instanceof FeatureLayer
                                    || layer instanceof ArcGISDynamicMapServiceLayer) {
                                GisQueryApplication.getApp().writeApplicationPreference(layer
                                        .getName(), visibleLayers.get(i)
                                        .isVisible());
                                i++;
                            }
                        }
                        GisQueryApplication.getApp().writeApplicationPreference(
                                GisQueryApplication.LAYERS_VISIBILITY, true);
                    }
                });

        return localDialog;
    }

    /**
     * Method createBluetoothDevicesDialog.
     *
     * @return Dialog
     */
    @SuppressLint("InflateParams")
    private Dialog createBluetoothDevicesDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        final View view = getLayoutInflater().inflate(
                R.layout.bluetoothdeviceslist, null);

        builder.setTitle(R.string.bluetooth_devices);
        builder.setIcon(R.drawable.bluetooth);
        builder.setView(view);
        Dialog localDialog = builder.create();

        TextView pairedDevicesText = (TextView) view
                .findViewById(R.id.paired_devices_text);

        if (bluetoothAdapter == null) {
            pairedDevicesText.setText(R.string.alert_dialog_no_bt);
            return localDialog;
        }

        progressBar = (ProgressBar) view.findViewById(R.id.progressbar);

        ArrayAdapter<String> pairedDevicesAdapter = new ArrayAdapter<String>(
                MainActivity.this, R.layout.listitem);
        ListView pairedListView = (ListView) view
                .findViewById(R.id.paired_devices_list);
        pairedListView.setAdapter(pairedDevicesAdapter);
        pairedListView.setOnItemClickListener(deviceOnClickListener);

        // Get a set of currently paired devices
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter
                .getBondedDevices();

        if (pairedDevices.size() > 0) {
            pairedDevicesText.setVisibility(View.VISIBLE);
            for (BluetoothDevice device : pairedDevices) {
                pairedDevicesAdapter.add(device.getName() + "\n"
                        + device.getAddress());
            }
        } else {
            String noDevices = getResources().getText(R.string.none_paired)
                    .toString();
            pairedDevicesAdapter.add(noDevices);
        }

        newDevicesAdapter = new ArrayAdapter<String>(MainActivity.this, R.layout.listitem);
        ListView newListView = (ListView) view.findViewById(R.id.new_devices);
        newListView.setAdapter(newDevicesAdapter);
        newListView.setOnItemClickListener(deviceOnClickListener);

        Button scanDevicesButton = (Button) view.findViewById(R.id.button_scan);
        scanDevicesButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                doDiscovery(view);
            }
        });

        localDialog
                .setOnDismissListener(new android.content.DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (bluetoothSerialService == null)
                                    return;

                                if (bluetoothSerialService.isConnecting())
                                    return;
                            }
                        });
                    }
                });

        return localDialog;
    }

    /**
     * Method doDiscovery.
     *
     * @param view View
     */
    void doDiscovery(View view) {
        // Indicate scanning in the title
        setProgressBarIndeterminateVisibility(true);

        // Turn on sub-title for new devices
        view.findViewById(R.id.title_new_devices).setVisibility(View.VISIBLE);

        // If we're already discovering, stop it
        if (bluetoothAdapter.isDiscovering())
            bluetoothAdapter.cancelDiscovery();

        // Request discover from BluetoothAdapter
        bluetoothAdapter.startDiscovery();
    }

    private OnItemClickListener deviceOnClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            bluetoothAdapter.cancelDiscovery();
            // Get the device MAC address, which is the last 17 chars in the
            // View
            String info = ((TextView) view).getText().toString();
            String address = info.substring(info.length() - 17);
            connectedDeviceAddress = address;
            Log.v(TAG, "address --> " + address);
            BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);

            // Attempt to connect to the device
            bluetoothSerialService.connect(device);
        }
    };

    /**
     * Method zoomToCurrentLocation.
     *
     * @param mapPoint Point
     * @param scale    float
     */
    public void zoomToCurrentLocation(Point mapPoint, double scale) {
        // if (isNumeric("" +new BigDecimal(mapPoint.getX()).toPlainString() )
        // && isNumeric("" + new BigDecimal(mapPoint.getY()).toPlainString()))
        {
            Log.d("mapPoint", "x=" + mapPoint.getX() + "  y=" + mapPoint.getY());
            map.zoomToScale(mapPoint, scale);

        }

    }

    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
    }

    SensorEventListener sensorListener = new SensorEventListener() {
        @SuppressWarnings("deprecation")
        @Override
        public void onSensorChanged(SensorEvent event) {

            // 方向传感器
            // Android 4.3 以上版本
            if (Build.VERSION.SDK_INT >= 18) {
                if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
                    magneticFieldValues = event.values;
                if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
                    accelerometerValues = event.values;
                float[] values = new float[3];
                float[] R = new float[9];
                SensorManager.getRotationMatrix(R, null, accelerometerValues,
                        magneticFieldValues);
                SensorManager.getOrientation(R, values);
                // 要经过一次数据格式的转换，转换为度
                float z = (float) Math.toDegrees(values[0]);
                // float x = (float) Math.toDegrees(values[1]);
                // float y = (float) Math.toDegrees(values[2]);

                // Log.i(TAG, "4.3以上版本---x:" + x + " y:" + y + " z:" + z);
                if (gpsMarkerSymbol != null) {
                    z = z + 90;// 横向手机指向
                    gpsMarkerSymbol.setAngle(z);
                    gpsShowLayer.updateGraphic(gpsID, gpsMarkerSymbol);
                }
            } else {
                if (event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
                    // x表示手机指向的方位，0表示北90表示东，180表示南，270表示西
                    float x = event.values[SensorManager.DATA_X];
                    // float y = event.values[SensorManager.DATA_Y];
                    // float z = event.values[SensorManager.DATA_Z];
                    // Log.i(TAG, "4.3以下版本---x:" + x + " y:" + y + " z:" + z);
                    if (gpsMarkerSymbol != null) {
                        x = x + 90;
                        gpsMarkerSymbol.setAngle(x);
                        gpsShowLayer.updateGraphic(gpsID, gpsMarkerSymbol);
                    }
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    @Override
    public void location(Location paramLocation) {
        Log.d("位置改变：",
                paramLocation.getLatitude() + "----"
                        + paramLocation.getLongitude());
        if (!(MapOperate.TrackIsRecording()))
            return;
    }

    // 坐标定位方法
    private void locationQuery() {
        startActivityForResult(new Intent(
                MainActivity.this, SetLocationActivity.class), 3237);
    }

    public boolean isUseGps() {
        return isUseGps;
    }

    public void setUseGps(boolean isUseGps) {
        this.isUseGps = isUseGps;
    }

    public Graphic getLocationGraphic() {
        return locationGraphic;
    }

    public class MTouchListener implements View.OnTouchListener {
        static final int DRAG = 1;
        static final int NONE = 0;
        static final int ZOOM = 2;
        PointF mid = new PointF();
        int mode = 0;
        float oldDist = 1.0F;
        Matrix savedMatrix = new Matrix();
        PointF start = new PointF();

        // 计算移动距离
        @SuppressLint("FloatMath")
        private float spacing(MotionEvent event) {
            float x = event.getX(0) - event.getX(1);
            float y = event.getY(0) - event.getY(1);
            return (float) Math.sqrt(x * x + y * y);
        }

        // 计算中点位置
        @SuppressLint("ClickableViewAccessibility")
        private void midPoint(PointF point, MotionEvent event) {
            float x = event.getX(0) + event.getX(1);
            float y = event.getY(0) + event.getY(1);
            point.set(x / 2, y / 2);
        }

        @SuppressLint("ClickableViewAccessibility")
        public boolean onTouch(View paramView, MotionEvent event) {
            ImageView view = (ImageView) paramView;

            // Handle touch events here...
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                // 设置拖拉模式
                case MotionEvent.ACTION_DOWN:
                    savedMatrix.set(matrix);
                    start.set(event.getX(), event.getY());
                    mode = DRAG;
                    break;

                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_POINTER_UP:
                    mode = NONE;
                    break;
                // 设置多点触摸模式
                case MotionEvent.ACTION_POINTER_DOWN:
                    oldDist = spacing(event);
                    if (oldDist > 10f) {
                        savedMatrix.set(matrix);
                        midPoint(mid, event);
                        mode = ZOOM;
                    }
                    break;
                // 若为DRAG模式，则点击移动图片
                case MotionEvent.ACTION_MOVE:
                    if (mode == DRAG) {
                        matrix.set(savedMatrix);
                        // 设置位移
                        matrix.postTranslate(event.getX() - start.x, event.getY()
                                - start.y);
                    }
                    // 若为ZOOM模式，则多点触摸缩放
                    else if (mode == ZOOM) {
                        float newDist = spacing(event);
                        if (newDist > 10f) {
                            matrix.set(savedMatrix);
                            float scale = newDist / oldDist;
                            // 设置缩放比例和图片中点位置
                            matrix.postScale(scale, scale, mid.x, mid.y);
                        }
                    }
                    break;
            }

            // Perform the transformation
            view.setImageMatrix(matrix);
            return true;

        }
    }
}