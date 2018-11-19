/**
 * 程序应用，最先加载
 */
package com.guyu.android.gis.app;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParser;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.os.Handler;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.blankj.utilcode.util.Utils;
import com.esri.android.map.FeatureLayer;
import com.esri.android.map.Layer;
import com.esri.android.map.TiledLayer;
import com.esri.android.map.ags.ArcGISDynamicMapServiceLayer;
import com.esri.android.map.ags.ArcGISFeatureLayer;
import com.esri.android.map.ags.ArcGISLocalTiledLayer;
import com.esri.android.map.ags.ArcGISTiledMapServiceLayer;
import com.esri.core.ags.LayerServiceInfo;
import com.esri.core.geodatabase.Geodatabase;
import com.esri.core.geodatabase.GeodatabaseFeatureTable;
import com.esri.core.geometry.Envelope;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.SpatialReference;
import com.esri.core.map.DrawingInfo;
import com.esri.core.map.FeatureSet;
import com.esri.core.map.Graphic;
import com.google.gson.reflect.TypeToken;
import com.guyu.android.base.BaseEntity;
import com.guyu.android.database.SdCardDBHelper;
import com.guyu.android.gis.activity.LoginActivity;
import com.guyu.android.gis.activity.MainActivity;
import com.guyu.android.gis.common.AnalysisLayerResultInfo;
import com.guyu.android.gis.common.GpsOffset;
import com.guyu.android.gis.common.MapVersionList;
import com.guyu.android.gis.config.AnyChatServerConfig;
import com.guyu.android.gis.config.CaseUploadConfig;
import com.guyu.android.gis.config.CaseUploadsConfig;
import com.guyu.android.gis.config.GpsProjectFix;
import com.guyu.android.gis.config.LayerConfig;
import com.guyu.android.gis.config.MapBackgroundConfig;
import com.guyu.android.gis.config.Project;
import com.guyu.android.gis.config.ProjectConfig;
import com.guyu.android.gis.config.ProjectsConfig;
import com.guyu.android.gis.config.QuickToggleLayerConfig;
import com.guyu.android.gis.config.TaskDownloadConfig;
import com.guyu.android.gis.config.TaskDownloadsConfig;
import com.guyu.android.gis.localservice.TaskUpdateService;
import com.guyu.android.gis.maptools.GeometryAnalysisTool;
import com.guyu.android.gis.opt.MapOperate;
import com.guyu.android.runtime.LicenseResult;
import com.guyu.android.utils.GsonUtil;
import com.guyu.android.utils.SdCardUtils;
import com.guyu.android.R;

/**
 */
public class GisQueryApplication extends MultiDexApplication {
    public static final String TAG = GisQueryApplication.class.getSimpleName();

    private static GisQueryApplication app;
    public static String MAP_STATE = "map_state";
    public static String MAP_STATE_SAVED = "map_state_saved";

    private static String APP_PREFERENCES = "igisquery_preferences";
    public static String MOCKUP_PROVIDER = "igisquery_provider";

    protected static String OFFLINE_FILE_EXTENSION = ".json";
    protected static final String BASEMAP_PREFIX = "file:";

    protected static final int REQUEST_CONNECT_BLUETOOTH_DEVICE = 1;
    public static final int REQUEST_ENABLE_BLUETOOTH = 2;

    public static final int BLUETOOTH_MESSAGE_STATE_CHANGE = 1;
    public static final int BLUETOOTH_MESSAGE_READ = 2;
    public static final int BLUETOOTH_MESSAGE_WRITE = 3;
    public static final int BLUETOOTH_MESSAGE_DEVICE_NAME = 4;
    public static final int BLUETOOTH_MESSAGE_TOAST = 5;
    public static String EXTRA_DEVICE_ADDRESS = "device_address";
    public static final String BLUETOOTH_DEVICE_NAME = "device_name";
    public static final String BLUETOOTH_TOAST = "toast";

    public static String LAYERS_VISIBILITY = "layers_visibility";

    public static String LINE_SEPARATOR = System.getProperty("line.separator");

    public static int NEAREST_TOLERANCE = 15;
    protected static int LOCATION_RADIUS_TOLERANCE2 = 40;
    protected static int ENLARGE_ZOOM = 1000;
    protected static int DEFAULT_ACCURACY = 10;
    private Handler mHand = null;
    private NaviGPS naviGps;
    private boolean isGpsEnable = false;// GPS是否可用
    private boolean isNetWorkEnable = false;// 网络是否可用
    public File sdCard;
    private File iGisQueryDataFile;
    public File internalDataFile;
    private String state = Environment.getExternalStorageState();
    private boolean externalStorageAvailable = false;
    private boolean externalStorageWriteable = false;

    private static String dataSDCardDirRootName;
    private static String offlineDataSDCardDirName;
    private SharedPreferences recentfile = null;
    private SharedPreferences savefile = null;

    protected static String basemapName;
    private static String basemapDataFrameName;
    protected static String basemapPath;
    public static String foundName;
    public static String currentProjectName;
    public static String currentProjectPath;
    private ConnectivityManager connectivityManager;
    private ProjectsConfig projectsconfig;
    private ProjectConfig projectconfig;
    // 授权结果
    private LicenseResult licenseResult;
    // 地块分析结果
    private ArrayList<AnalysisLayerResultInfo> analysisResultInfo;
    // 当前点击的图层详细分析结果
    private AnalysisLayerResultInfo analysisLayerResultInfo;
    // 当前地块分析工具
    private GeometryAnalysisTool geoAnalysisTool;
    private ArrayList<Layer> layers;
    private Geodatabase offlinegdb = null;
    private boolean layerLoaded;
    private boolean isSpecialVirsion;
    //-109.33是根据手持机修正的 -108.94f  大平板:-3.625 -105.315f  小平板:-9.125 -118.065f
    public static double MOVE_X = 0;
    //+7.6是根据手持机修正的  13.82f   大平板:-5.25  8.57f 小平板:-8.125 5.695f
    public static double MOVE_Y = 0;


    @Override
    public void onCreate() {
        super.onCreate();
        app = this;

        Utils.init(this);

    }

    public boolean isSpecialVirsion() {
        return isSpecialVirsion;
    }

    public void init() {

        GisQueryApplication.this.savefile = getSharedPreferences("navi", 0);
        GisQueryApplication.this.recentfile = getSharedPreferences("navi_file", 0);
        dataSDCardDirRootName = GisQueryApplication.this.getResources().getString(
                R.string.config_data_sdcard_dir);

        //////////////////////////////////////offlinedata路径////////////////////////////////////////////
        offlineDataSDCardDirName = GisQueryApplication.this.getResources().getString(
                R.string.config_data_sdcard_offline_dir);

        basemapName = GisQueryApplication.this.getResources().getString(
                R.string.config_basemap_name);
        basemapDataFrameName = GisQueryApplication.this.getResources().getString(
                R.string.config_basemap_dataframe_name);
        foundName = GisQueryApplication.this.getResources().getString(R.string.config_found_name);
        naviGps = new NaviGPS();
        getDataStorage();
        // 解析有哪些工程
        loadProjectsConfig();
        // 解析当前工程
        loadProjectConfig();
        loadMapVersion();
        loadGpsOffset();
        loadLayers();
        File file = new File(currentProjectPath + "basemap/LJK_YX.tpk");
        if (!file.exists()) {
            isSpecialVirsion = true;
        }
        file = null;
    }

    private void loadGpsOffset() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                File versionFile = new File(GisQueryApplication.currentProjectPath + "GpsOffset.txt");

                try (FileReader reader = new FileReader(versionFile)) {

                    GpsOffset gpsOffset = GsonUtil.getGson().fromJson(reader, GpsOffset.class);
                    MOVE_X = gpsOffset.getMOVE_X();
                    MOVE_Y = gpsOffset.getMOVE_Y();
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        }).start();

    }

    public void loadMapVersion() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                File file = new File(GisQueryApplication.currentProjectPath + "mapVersion.txt");
                try (FileReader reader = new FileReader(file)) {

                    projectconfig.setMapVersion(GsonUtil.getGson().fromJson(reader, new TypeToken<BaseEntity<MapVersionList>>() {
                    }.getType()));
//                    MapVersionList list = (MapVersionList)projectconfig.getMapVersion().getData();
//                    Log.d("mapVersion",GsonUtil.getGson().toJson(list));
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("mapVersion", e.getMessage());
                }


            }
        }).start();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public ProjectsConfig getProjectsconfig() {

        return projectsconfig;
    }

    public ProjectConfig getProjectconfig() {


        return projectconfig;
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        System.gc();
    }

    public GeometryAnalysisTool getGeoAnalysisTool() {
        return geoAnalysisTool;
    }

    public void setGeoAnalysisTool(GeometryAnalysisTool geoAnalysisTool) {
        this.geoAnalysisTool = geoAnalysisTool;
    }

    public ArrayList<AnalysisLayerResultInfo> getAnalysisResultInfo() {
        return analysisResultInfo;
    }

    public void setAnalysisResultInfo(
            ArrayList<AnalysisLayerResultInfo> analysisResultInfo) {
        this.analysisResultInfo = analysisResultInfo;
    }

    public AnalysisLayerResultInfo getAnalysisLayerResultInfo() {
        return analysisLayerResultInfo;
    }

    public void setAnalysisLayerResultInfo(
            AnalysisLayerResultInfo analysisLayerResultInfo) {
        this.analysisLayerResultInfo = analysisLayerResultInfo;
    }

    public LicenseResult getLicenseResult() {
        return licenseResult;
    }

    public void setLicenseResult(LicenseResult licenseResult) {
        this.licenseResult = licenseResult;
        // 启高
        String[] functioncodeArray_Config = projectconfig.getFunctioncodes();

        String[] functioncodeArray_License = licenseResult.getFunctioncodes();

        String functioncodeStr = "";
        if (functioncodeArray_License != null) {
            for (String temcode : functioncodeArray_License) {
                if (isExitInArray(functioncodeArray_Config, temcode)) {
                    functioncodeStr = functioncodeStr + temcode + ",";
                }
            }


            functioncodeStr = functioncodeStr.substring(0,
                    functioncodeStr.length() - 1);
            functioncodeStr = functioncodeStr;
//          functioncodeStr = functioncodeStr + ",linquanchange";
            String[] functioncodes_last = functioncodeStr.split(",");

            projectconfig.setFunctioncodes(functioncodes_last);
        } else {
            projectconfig.setFunctioncodes(new String[]{});
        }

    }

    public boolean isExitInArray(String[] array, String funcode) {
        for (String temcode : array) {
            if (funcode.equals(temcode)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取导航对象
     *
     * @return
     */
    public NaviGPS getNaviGps() {
        return this.naviGps;
    }

    public boolean isGpsEnable() {
        this.isGpsEnable = ((LocationManager) getSystemService(Context.LOCATION_SERVICE))
                .isProviderEnabled("gps");
        if (this.isGpsEnable) {
            if (naviGps.getGpsListener() == null) {
                startGPS(true);
            }
        }
        return this.isGpsEnable;

    }

    public boolean startGPS(boolean useGPS) {
        if (useGPS) {
            LocationManager localLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            this.isGpsEnable = localLocationManager.isProviderEnabled("gps");
            this.isNetWorkEnable = localLocationManager
                    .isProviderEnabled("network");
            this.naviGps.openGpsDevice(localLocationManager);
        } else {
            this.naviGps.openGpsDevice(null);
        }
        return this.isGpsEnable;
    }

    public void setHand(Handler paramHandler) {
        this.mHand = paramHandler;
        this.naviGps.setMsgHand(paramHandler);
    }

    public boolean isLocation() {
        return (this.naviGps.nFixMode == 1);
    }

    public static GisQueryApplication getApp() {
        return app;
    }


    public void loadLayers() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                layers = null;
                layerLoaded = false;
                getLayers();
            }
        }).start();
    }

    public void loadProjectsConfig() {
        projectsconfig = new ProjectsConfig();
        File f_projectsconfig = getFile("projects.xml", "", "");
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        Document document = null;
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            document = builder.parse(f_projectsconfig);
            Element root = document.getDocumentElement();
            projectsconfig.setCurrentProjectName(root.getAttribute("current"));
            GisQueryApplication.currentProjectName = root
                    .getAttribute("current");
            NodeList nl_projects = root.getElementsByTagName("project");
            ArrayList<Project> al_project = new ArrayList<Project>();
            for (int i = 0; i < nl_projects.getLength(); i++) {
                Element el_project = (Element) nl_projects.item(i);
                Project prj = new Project();
                prj.setLabel(el_project.getAttribute("label"));
                prj.setName(el_project.getAttribute("name"));
                al_project.add(prj);
            }
            projectsconfig.setProjects(al_project);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadProjectConfig() {
        MapOperate.setPrjName(currentProjectName);
        currentProjectPath = this.getProjectPath();
        projectconfig = new ProjectConfig();

        // 设置轨迹数据存放路径
        MapOperate.mTrackPath = this.getProjectPath() + "Track/";
        File dirTrack = new File(MapOperate.mTrackPath);
        if (!dirTrack.exists()) {
            dirTrack.mkdir();
        }
        File f_projectconfig = getFile("projectconfig.xml",
                projectsconfig.getCurrentProjectName(), "");
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        Document document = null;
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            document = builder.parse(f_projectconfig);
            Element root = document.getDocumentElement();

            // =============================================================
            // 解析isys基本路径
            // =============================================================
            NodeList nl_isysbaseurl = root.getElementsByTagName("isysbaseurl");
            Element el_isysbaseurl = (Element) nl_isysbaseurl.item(0);
            projectconfig.setIsysbaseurl(el_isysbaseurl.getTextContent());
            // =============================================================
            // 解析app相关设置
            // =============================================================
            // 设置应用标题
            NodeList nl_apptitle = root.getElementsByTagName("apptitle");
            Element el_apptitle = (Element) nl_apptitle.item(0);
            projectconfig.setApptitle(el_apptitle.getTextContent());
            // 设置应用版本和版本检测路径
            NodeList nl_appversion = root.getElementsByTagName("appversion");
            Element el_appversion = (Element) nl_appversion.item(0);
            projectconfig.setAppVersionCheckUrl(el_appversion.getAttribute("checkUrl"));

            NodeList nl_tracklist = root.getElementsByTagName("tracklist");
            Element el_tracklist = (Element) nl_tracklist.item(0);
            projectconfig.setTracklistUrl(el_tracklist.getAttribute("tracklistUrl"));

            NodeList nl_downloadtrackUrl = root.getElementsByTagName("downloadtrackurl");
            Element el_downloadtrackUrl = (Element) nl_downloadtrackUrl.item(0);
            projectconfig.setDownloadtrackUrl(el_downloadtrackUrl.getAttribute("downloadtrackUrl"));

            NodeList nl_appdownloadurl = root.getElementsByTagName("appdownloadurl");
            Element el_appdownloadurl = (Element) nl_appdownloadurl.item(0);
            projectconfig.setAppDownloadUrl(el_appdownloadurl.getAttribute("downloadUrl"));

            NodeList nl_maplisturl = root.getElementsByTagName("maplisturl");
            Element el_maplisturl = (Element) nl_maplisturl.item(0);
            projectconfig.setMaplisturl(el_maplisturl.getAttribute("downloadUrl"));

            NodeList nl_mapdownloadurl = root.getElementsByTagName("mapdownloadurl");
            Element el_mapdownloadurl = (Element) nl_mapdownloadurl.item(0);
            projectconfig.setMapdownloadurl(el_mapdownloadurl.getAttribute("downloadUrl"));

            // =============================================================
            // 解析AnyChat 服务器配置
            // =============================================================
            AnyChatServerConfig anyChatServerConfig = new AnyChatServerConfig();
            NodeList nl_anychatserverconfig = root
                    .getElementsByTagName("anychatserver");
            Element el_anychatserverconfig = (Element) nl_anychatserverconfig
                    .item(0);
            anyChatServerConfig
                    .setIp(el_anychatserverconfig.getAttribute("ip"));
            anyChatServerConfig.setPort(el_anychatserverconfig
                    .getAttribute("port"));
            projectconfig.setAnyChatServerConfig(anyChatServerConfig);
            // =============================================================
            // 解析设置所属区划相关配置
            // =============================================================
            NodeList nl_cantonzonerootcode = root
                    .getElementsByTagName("cantonzonerootcode");
            Element el_cantonzonerootcode = (Element) nl_cantonzonerootcode
                    .item(0);
            projectconfig.setCantonZoneRootCode(Integer
                    .parseInt(el_cantonzonerootcode.getTextContent()));
            // =============================================================
            // 解析设置区划导航相关配置
            // =============================================================
            NodeList nl_cantonzonenavi = root
                    .getElementsByTagName("cantonzonenavi");
            Element el_cantonzonenavi = (Element) nl_cantonzonenavi.item(0);
            projectconfig.setCantonZoneDownloadUrl((projectconfig
                    .getIsysbaseurl()
                    + el_cantonzonenavi.getAttribute("downloadUrl")
                    + "?cantonZoneRootCode=" + projectconfig
                    .getCantonZoneRootCode()));
            // ============================================================
            // 解析业务要件定义相关配置
            // ============================================================
            NodeList nl_bizdocdef = root.getElementsByTagName("bizdocdef");
            Element el_bizdocdef = (Element) nl_bizdocdef.item(0);
            projectconfig.setBizDocDefDownloadUrl((projectconfig
                    .getIsysbaseurl() + el_bizdocdef
                    .getAttribute("downloadUrl")));
            // ============================================================
            // 解析所有案件上传配置信息
            // ============================================================
            NodeList nl_caseuploads = root.getElementsByTagName("caseuploads");
            Element el_caseuploads = (Element) nl_caseuploads.item(0);
            CaseUploadsConfig caseUploadsConfig = new CaseUploadsConfig();
            caseUploadsConfig.setUploadUrl(projectconfig.getIsysbaseurl()
                    + el_caseuploads.getAttribute("uploadUrl"));
            NodeList nl_caseupload = el_caseuploads
                    .getElementsByTagName("caseupload");
            Map<String, CaseUploadConfig> caseUploadConfigMap = new HashMap<String, CaseUploadConfig>();
            for (int i = 0; i < nl_caseupload.getLength(); i++) {
                Element el_caseupload = (Element) nl_caseupload.item(i);
                CaseUploadConfig caseUploadConfig = new CaseUploadConfig();
                caseUploadConfig.setCaseType(el_caseupload
                        .getAttribute("caseType"));
                caseUploadConfig.setBizId(el_caseupload.getAttribute("bizId"));
                if (el_caseupload.hasAttribute("fields")) {
                    caseUploadConfig.setFields(el_caseupload
                            .getAttribute("fields"));
                }
                caseUploadConfigMap.put(caseUploadConfig.getCaseType(),
                        caseUploadConfig);
            }
            caseUploadsConfig.setCaseUploadConfigMap(caseUploadConfigMap);
            projectconfig.setCaseUploadsConfig(caseUploadsConfig);
            // =====================================================================
            // 解析所有任务下载配置信息
            // =====================================================================
            NodeList nl_taskdownloads = root
                    .getElementsByTagName("taskdownloads");
            Element el_taskdownloads = (Element) nl_taskdownloads.item(0);
            NodeList nl_taskdownload = el_taskdownloads
                    .getElementsByTagName("taskdownload");
            TaskDownloadsConfig taskDownloadsConfig = new TaskDownloadsConfig();
            taskDownloadsConfig.setDownloadUrl(projectconfig.getIsysbaseurl()
                    + el_taskdownloads.getAttribute("downloadUrl"));
            taskDownloadsConfig.setUpdateReceivedUrl(projectconfig
                    .getIsysbaseurl()
                    + el_taskdownloads.getAttribute("updateReceivedUrl"));
            Map<String, TaskDownloadConfig> taskDownLoadConfigMap = new HashMap<String, TaskDownloadConfig>();
            for (int i = 0; i < nl_taskdownload.getLength(); i++) {
                Element el_taskdownload = (Element) nl_taskdownload.item(i);
                TaskDownloadConfig taskDownloadConfig = new TaskDownloadConfig();
                taskDownloadConfig.setCaseType(el_taskdownload
                        .getAttribute("caseType"));
                taskDownloadConfig.setAttrsViewId(el_taskdownload
                        .getAttribute("attrsViewId"));
                taskDownloadConfig.setGeoViewId(el_taskdownload
                        .getAttribute("geoViewId"));
                taskDownLoadConfigMap.put(taskDownloadConfig.getCaseType(),
                        taskDownloadConfig);
            }
            taskDownloadsConfig.setTaskDownLoadConfigMap(taskDownLoadConfigMap);
            projectconfig.setTaskDownloadsConfig(taskDownloadsConfig);

            // 设置轨迹巡查上传地址
            NodeList nl_trackupload = root.getElementsByTagName("trackupload");
            Element el_trackupload = (Element) nl_trackupload.item(0);
            // 在线的地址
            NodeList nl_trackuploadonlineurl = el_trackupload
                    .getElementsByTagName("onlineurl");
            Element el_trackuploadonlineurl = (Element) nl_trackuploadonlineurl
                    .item(0);
            projectconfig.setTrackUploadOnlineUrl(projectconfig
                    .getIsysbaseurl()
                    + el_trackuploadonlineurl.getTextContent());
            // 离线的地址
            NodeList nl_trackuploadofflineurl = el_trackupload
                    .getElementsByTagName("offlineurl");
            Element el_trackuploadofflineurl = (Element) nl_trackuploadofflineurl
                    .item(0);
            projectconfig.setTrackUploadOfflineUrl(projectconfig
                    .getIsysbaseurl()
                    + el_trackuploadofflineurl.getTextContent());


            NodeList nl_uploadtrackurl = el_trackupload
                    .getElementsByTagName("uploadtrackurl");
            Element el_uploadtrackurl = (Element) nl_uploadtrackurl
                    .item(0);
            projectconfig.setUploadtrackurl(projectconfig
                    .getIsysbaseurl()
                    + el_uploadtrackurl.getTextContent());
            // =================================================================================================
            // 地图相关设置
            // =================================================================================================
            NodeList nl_map = root.getElementsByTagName("map");
            Element el_map = (Element) nl_map.item(0);
            // 使用离线查询？使用初始坐标系设置？
            Boolean useofflinequery = "true".endsWith(el_map
                    .getAttribute("useofflinequery"));
            Boolean useinitialspatialreference = "true".endsWith(el_map
                    .getAttribute("useinitialspatialreference"));
            projectconfig.setUseofflinequery(useofflinequery);
            projectconfig
                    .setUseinitialspatialreference(useinitialspatialreference);
            // 解析坐标系设置
            NodeList nl_spatialreference = el_map
                    .getElementsByTagName("spatialreference");
            if (nl_spatialreference != null
                    && nl_spatialreference.getLength() > 0) {
                Element el_spatialreference = (Element) nl_spatialreference
                        .item(0);
                NodeList nl_wkid = el_spatialreference
                        .getElementsByTagName("wkid");
                if (nl_wkid != null && nl_wkid.getLength() > 0) {
                    Element el_wkid = (Element) nl_wkid.item(0);
                    projectconfig.setSpatialreference(SpatialReference
                            .create(Integer.parseInt(el_wkid.getTextContent()
                                    .trim())));
                } else {
                    NodeList nl_wkt = el_spatialreference
                            .getElementsByTagName("wkt");
                    if (nl_wkt != null && nl_wkt.getLength() > 0) {
                        Element el_wkt = (Element) nl_wkt.item(0);
                        projectconfig.setSpatialreference(SpatialReference
                                .create(el_wkt.getTextContent().trim()));
                    } else {
                        projectconfig.setSpatialreference(null);
                    }
                }
            } else {
                projectconfig.setSpatialreference(null);
            }
            // 设置Gps投影修正参数
            NodeList nl_gpsprojectfix = el_map
                    .getElementsByTagName("gpsprojectfix");
            Element el_gpsprojectfix = (Element) nl_gpsprojectfix.item(0);
            GpsProjectFix gpsProjectFix = new GpsProjectFix();
            gpsProjectFix.setEnabled("true".equals(el_gpsprojectfix
                    .getAttribute("enabled")));
            gpsProjectFix.setOffsetx(Double.parseDouble(el_gpsprojectfix
                    .getAttribute("offsetx")));
            gpsProjectFix.setOffsety(Double.parseDouble(el_gpsprojectfix
                    .getAttribute("offsety")));
            projectconfig.setGpsProjectFix(gpsProjectFix);
            // 设置采集相关配置
            NodeList nl_collect = el_map.getElementsByTagName("collect");
            if (nl_collect != null && nl_collect.getLength() > 0) {
                Element el_collect = (Element) nl_collect.item(0);
                projectconfig.setUsebluetoothcollect("true".equals(el_collect
                        .getAttribute("usebluetooth")));
            } else {
                projectconfig.setUsebluetoothcollect(false);
            }

            // 设置初始视野和全图视野
            NodeList nl_initialextent = el_map
                    .getElementsByTagName("initialextent");
            Element el_initialextent = (Element) nl_initialextent.item(0);
            NodeList nl_fullextent = el_map.getElementsByTagName("fullextent");
            Element el_fullextent = (Element) nl_fullextent.item(0);
            projectconfig
                    .setInitialextent(getExtentObjectByString(el_initialextent
                            .getTextContent()));
            projectconfig.setFullextent(getExtentObjectByString(el_fullextent
                    .getTextContent()));
            // 解析地图背景设置
            NodeList nl_mapBackgroundConfig = el_map
                    .getElementsByTagName("background");
            Element el_mapBackgroundConfig = (Element) nl_mapBackgroundConfig
                    .item(0);
            MapBackgroundConfig mapBackgroundConfig = new MapBackgroundConfig();
            mapBackgroundConfig.setBkColor(el_mapBackgroundConfig
                    .getAttribute("bkcolor"));
            mapBackgroundConfig.setGridColor(el_mapBackgroundConfig
                    .getAttribute("gridcolor"));
            mapBackgroundConfig
                    .setGridSize(Float.parseFloat(el_mapBackgroundConfig
                            .getAttribute("gridsize")));
            mapBackgroundConfig.setGridLineSize(Float
                    .parseFloat(el_mapBackgroundConfig
                            .getAttribute("gridlinesize")));
            projectconfig.setMapbackgroundconfig(mapBackgroundConfig);
            // 解析底图图层
            NodeList nl_basemaps = root.getElementsByTagName("basemaps");
            Element el_basemaps = (Element) nl_basemaps.item(0);
            NodeList nl_basemaps_layers = el_basemaps
                    .getElementsByTagName("layer");
            ArrayList<LayerConfig> al_basemaps_layers = new ArrayList<LayerConfig>();
            for (int i = 0; i < nl_basemaps_layers.getLength(); i++) {
                Element el_basemap_layer = (Element) nl_basemaps_layers.item(i);
                LayerConfig lc_basemap = new LayerConfig();
                lc_basemap.setLabel(el_basemap_layer.getAttribute("label"));
                lc_basemap.setName(el_basemap_layer.getAttribute("name"));
                lc_basemap.setType(el_basemap_layer.getAttribute("type"));
                lc_basemap.setUrl(el_basemap_layer.getAttribute("url"));
                lc_basemap.setVisible("true".equals(el_basemap_layer
                        .getAttribute("visible")));
                lc_basemap.setUseoffline("true".equals(el_basemap_layer
                        .getAttribute("useoffline")));
                lc_basemap.setOfflinetype(el_basemap_layer
                        .getAttribute("offlinetype"));
                //////////////////////////////////////////////222//////////////////////////////////////////////////////
                lc_basemap.setOfflinedata(el_basemap_layer
                        .getAttribute("offlinedata"));


                if ("feature".equals(lc_basemap.getType())) {
                    lc_basemap.setDefinition(el_basemap_layer
                            .getAttribute("definition"));
                }
                String contrastfields = el_basemap_layer
                        .getAttribute("contrastfields");
                if (contrastfields != null && !"".equals(contrastfields)) {
                    String[] temAry_field = contrastfields.split(";");
                    if (temAry_field.length == 2) {
                        lc_basemap
                                .setOriginalfields(temAry_field[0].split(","));
                        lc_basemap.setDisplayfields(temAry_field[1].split(","));
                    }
                }
                String fielddict = el_basemap_layer
                        .getAttribute("fielddict");
                if (fielddict != null && !"".equals(fielddict)) {
                    JSONObject jsonObject = new JSONObject(fielddict);
                    lc_basemap.setFieldDict(jsonObject);
                }

                al_basemaps_layers.add(lc_basemap);

            }
            projectconfig.setBasemaps(al_basemaps_layers);

            // 解析业务图层
            NodeList nl_operationallayers = root
                    .getElementsByTagName("operationallayers");
            Element el_operationallayers = (Element) nl_operationallayers
                    .item(0);
            NodeList nl_operationallayers_layers = el_operationallayers
                    .getElementsByTagName("layer");
            ArrayList<LayerConfig> al_operationallayers_layers = new ArrayList<LayerConfig>();
            for (int i = 0; i < nl_operationallayers_layers.getLength(); i++) {
                Element el_operationallayers_layer = (Element) nl_operationallayers_layers
                        .item(i);
                LayerConfig lc_operationallayer = new LayerConfig();
                lc_operationallayer.setLabel(el_operationallayers_layer
                        .getAttribute("label"));
                lc_operationallayer.setName(el_operationallayers_layer
                        .getAttribute("name"));
                lc_operationallayer.setType(el_operationallayers_layer
                        .getAttribute("type"));
                lc_operationallayer.setUrl(el_operationallayers_layer
                        .getAttribute("url"));
                lc_operationallayer.setVisible("true"
                        .equals(el_operationallayers_layer
                                .getAttribute("visible")));
                lc_operationallayer.setUseoffline("true"
                        .equals(el_operationallayers_layer
                                .getAttribute("useoffline")));
                lc_operationallayer.setOfflinetype(el_operationallayers_layer
                        .getAttribute("offlinetype"));
                ///////////////////////////////////////////////////////////////////////////////////////////////////////////////
                lc_operationallayer.setOfflinedata(el_operationallayers_layer
                        .getAttribute("offlinedata"));


                if ("feature".equals(lc_operationallayer.getType())) {
                    lc_operationallayer
                            .setDefinition(el_operationallayers_layer
                                    .getAttribute("definition"));
                }

                String contrastfields = el_operationallayers_layer
                        .getAttribute("contrastfields");
                if (contrastfields != null && !"".equals(contrastfields)) {
                    String[] temAry_field = contrastfields.split(";");
                    if (temAry_field.length == 2) {
                        lc_operationallayer.setOriginalfields(temAry_field[0]
                                .split(","));
                        lc_operationallayer.setDisplayfields(temAry_field[1]
                                .split(","));
                    }
                }

                String fielddict = el_operationallayers_layer.getAttribute("fielddict");
                if (fielddict != null && !"".equals(fielddict)) {
                    JSONObject jsonObject = new JSONObject(fielddict);
                    lc_operationallayer.setFieldDict(jsonObject);
                }

                al_operationallayers_layers.add(lc_operationallayer);
            }
            projectconfig.setOperationallayers(al_operationallayers_layers);
            // 解析快捷图层
            NodeList nl_quicktogglelayers = root
                    .getElementsByTagName("quicktogglelayer");
            ArrayList<QuickToggleLayerConfig> al_quicktogglelayers = new ArrayList<QuickToggleLayerConfig>();
            for (int i = 0; i < nl_quicktogglelayers.getLength(); i++) {
                Element el_quicktogglelayer = (Element) nl_quicktogglelayers
                        .item(i);
                QuickToggleLayerConfig lc_quicktogglelayer = new QuickToggleLayerConfig();
                lc_quicktogglelayer.setLabel(el_quicktogglelayer
                        .getAttribute("label"));
                lc_quicktogglelayer.setIconflag((el_quicktogglelayer
                        .getAttribute("iconflag")));
                al_quicktogglelayers.add(lc_quicktogglelayer);
            }
            projectconfig.setQuicktogglelayers(al_quicktogglelayers);
            // 解析授权功能
            NodeList nl_functioncodes = root
                    .getElementsByTagName("functioncodes");
            Element el_functioncodes = (Element) nl_functioncodes.item(0);
            projectconfig.setFunctioncodes(el_functioncodes.getTextContent()
                    .split(","));
            // 解析软件授权码
            NodeList nl_licensekey = root.getElementsByTagName("licensekey");
            Element el_licensekey = (Element) nl_licensekey.item(0);

            //启高
            String key = el_licensekey.getTextContent().trim();//projectconfig.xml中的licensekey
            projectconfig.setLicensekey(key);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public SharedPreferences getConfig() {
        if (this.savefile == null)
            this.savefile = getSharedPreferences("navi", 0);
        return this.savefile;
    }

    public SharedPreferences getRencentFile() {
        if (this.recentfile == null)
            this.recentfile = getSharedPreferences("navi_file", 0);
        return this.recentfile;
    }

    public int GetIntConfigData(String paramString, int paramInt) {
        return this.savefile.getInt(paramString, paramInt);
    }

    public String GetStringConfigData(String paramString1, String paramString2) {
        return this.savefile.getString(paramString1, paramString2);
    }

    public void SetConfigData(String paramString, double paramDefault) {
        this.savefile.edit().putFloat(paramString, (float) paramDefault)
                .commit();
    }

    public void SetConfigData(String paramString, float paramDefault) {
        this.savefile.edit().putFloat(paramString, paramDefault).commit();
    }

    public void SetConfigData(String paramString, int paramDefault) {
        this.savefile.edit().putInt(paramString, paramDefault).commit();
    }

    public void SetConfigData(String paramString, long paramDefault) {
        this.savefile.edit().putLong(paramString, paramDefault).commit();
    }

    public void SetConfigData(String paramString1, String paramDefault) {
        this.savefile.edit().putString(paramString1, paramDefault).commit();
    }

    public void SetConfigData(String paramString, boolean paramDefault) {
        this.savefile.edit().putBoolean(paramString, paramDefault).commit();
    }

    public Envelope getExtentObjectByString(String extentStr) {
        String[] ary_initialextent = extentStr.split(" ");
        Point p1 = new Point(Float.parseFloat(ary_initialextent[0]),
                Float.parseFloat(ary_initialextent[1]));
        Point p2 = new Point(Float.parseFloat(ary_initialextent[2]),
                Float.parseFloat(ary_initialextent[3]));
        return new Envelope(p1.getX(), p1.getY(), p2.getX(), p2.getY());
    }

    public String getFileString(String fileName, String projectName) {
        String res = "";
        try {
            File file = this.getFile(fileName, projectName, "OfflineData");
            FileInputStream fin = new FileInputStream(file);
            int length = fin.available();
            byte[] buffer = new byte[length];
            fin.read(buffer);
//            res = EncodingUtils.getString(buffer, "UTF-8");
            res = new String(buffer, "UTF-8");
            fin.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;

    }

    public FeatureSet getFeatureSet(String fileName, String projectName) {
        FeatureSet featureSet = null;
        File jsonFile = this.getFile(fileName, projectName, "OfflineData");
        JsonFactory jsonFactory = new JsonFactory();
        JsonParser jsonParser;
        try {
            jsonParser = jsonFactory.createJsonParser(jsonFile);
            featureSet = FeatureSet.fromJson(jsonParser, true);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return featureSet;

    }

    /**
     * Method writeApplicationPreference.
     *
     * @param key   String
     * @param value Object
     */
    public void writeApplicationPreference(String key, Object value) {
        SharedPreferences sharedPreferences = getSharedPreferences(
                APP_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences
                .edit();

        if (value instanceof String)
            sharedPreferencesEditor.putString(key, value.toString());
        else if (value instanceof Boolean)
            sharedPreferencesEditor.putBoolean(key, (Boolean) value);
        else if (value instanceof Integer)
            sharedPreferencesEditor.putInt(key, (Integer) value);

        sharedPreferencesEditor.commit();
    }

    /**
     * Method readBooleanApplicationPrefecence.
     *
     * @param key String
     * @return boolean
     */
    public boolean readBooleanApplicationPrefecence(String key) {
        SharedPreferences sharedPreferences = getSharedPreferences(
                APP_PREFERENCES, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(key, false);
    }

    /**
     * Method readMapState.
     *
     * @return String
     */
    public String readMapState() {
        SharedPreferences sharedPreferences = getSharedPreferences(
                APP_PREFERENCES, Context.MODE_PRIVATE);
        return sharedPreferences.getString(MAP_STATE, null);
    }

    /**
     * Method isConnected.
     *
     * @return boolean
     */
    public boolean isConnected() {
        connectivityManager = (ConnectivityManager) this
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        boolean hasConnection = false;
        NetworkInfo[] netInfo = connectivityManager.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    hasConnection = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    hasConnection = true;

        }

        return hasConnection;
    }

    public String getExterPath() {
        String sdcard_path = "";
        // 得到路径
        try {
            Runtime runtime = Runtime.getRuntime();
            Process proc = runtime.exec("mount");
            InputStream is = proc.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            String line;
            BufferedReader br = new BufferedReader(isr);
            while ((line = br.readLine()) != null) {
                if (line.contains("secure"))
                    continue;
                if (line.contains("asec"))
                    continue;

                if (line.contains("fat")) {
                    String columns[] = line.split(" ");
                    if (columns != null && columns.length > 1) {
                        sdcard_path = sdcard_path.concat("" + columns[1]);
                    }
                } else if (line.contains("fuse")) {
                    String columns[] = line.split(" ");
                    if (columns != null && columns.length > 1) {
                        sdcard_path = sdcard_path.concat(columns[1]);
                    }
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return sdcard_path;
    }

    /**
     * Method getDataStorage.
     *
     * @param
     */
    public void getDataStorage() {
        boolean removableStorage = SdCardUtils.isSecondSDcardMounted();

        StringBuilder sb = new StringBuilder();

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            externalStorageAvailable = externalStorageWriteable = true;

            sdCard = Environment.getExternalStorageDirectory();
            sb.append(sdCard.getAbsolutePath());
            sb.append(File.separator);
            sb.append(dataSDCardDirRootName);
            iGisQueryDataFile = new File(sb.toString());

            if (iGisQueryDataFile.exists() == false) {
                if (removableStorage) {
                    sb = new StringBuilder();
                    sb.append(SdCardUtils.getSecondExterPath());
                    sb.append(File.separator);
                    sb.append(dataSDCardDirRootName);
                    iGisQueryDataFile = new File(sb.toString());
                }
            }

        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            externalStorageAvailable = true;
            externalStorageWriteable = false;
        } else {
            externalStorageAvailable = externalStorageWriteable = false;
        }
    }

    /**
     * Method isExternalSDCardPresent.
     *
     * @return boolean
     */
    public boolean isExternalSDCardPresent() {
        if (sdCard == null)
            return false;

        return sdCard.exists();
    }

    /**
     * Method isExternalStorageAvailable.
     *
     * @return boolean
     */
    public boolean isExternalStorageAvailable() {
        return externalStorageAvailable;
    }

    /**
     * Method isExternalStorageWriteable.
     *
     * @return boolean
     */
    public boolean isExternalStorageWriteable() {
        return externalStorageWriteable;
    }

    /**
     * Method getStorageBaseMap.
     *
     * @return String
     */
    public String getStorageBaseMap() {
        if (!externalStorageAvailable || iGisQueryDataFile == null)
            return null;

        File[] listFiles = iGisQueryDataFile.listFiles();

        int count = listFiles.length;

        if (count == 0)
            return null;

        StringBuilder sb = new StringBuilder();
        for (File file : listFiles) {
            if (file.getName().equals(basemapName)) {
                sb.append(BASEMAP_PREFIX);
                sb.append(File.separator);
                sb.append(File.separator);
                sb.append(file.getAbsolutePath());
                sb.append(File.separator);
                sb.append(basemapDataFrameName);
                break;
            }
        }

        return basemapPath = sb.toString();
    }

    public String getProjectPath() {
        if (!externalStorageAvailable || iGisQueryDataFile == null)
            return null;

        StringBuilder sb = new StringBuilder();
        sb.append(iGisQueryDataFile.getAbsolutePath());
        sb.append(File.separator);
        sb.append(currentProjectName);
        sb.append(File.separator);
        return sb.toString();
    }

    public File getFile(String fileName, String projectName, String dirName) {
        if (!externalStorageAvailable || iGisQueryDataFile == null)
            return null;

        StringBuilder sb = new StringBuilder();
        sb.append(iGisQueryDataFile.getAbsolutePath());
        sb.append(File.separator);
        sb.append(projectName);
        sb.append(File.separator);
        sb.append(dirName);
        File offlineFile = new File(sb.toString());

        File[] listFiles = offlineFile.listFiles();

        int count = listFiles.length;

        if (count == 0)
            return null;

        for (File file : listFiles) {
            if (file.isDirectory())
                continue;

            if (file.getName().equals(fileName)) {
                try {
                    return file;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * Method getJSONFile.
     *
     * @param fileName String
     * @return File
     */
    public File getJSONFile(String fileName) {
        if (!externalStorageAvailable || iGisQueryDataFile == null)
            return null;

        StringBuilder sb = new StringBuilder();
        sb.append(iGisQueryDataFile.getAbsolutePath());
        sb.append(File.separator);
        sb.append(this.projectsconfig.getCurrentProjectName());
        sb.append(File.separator);
        sb.append(offlineDataSDCardDirName);
        File offlineFile = new File(sb.toString());

        File[] listFiles = offlineFile.listFiles();

        int count = listFiles.length;

        if (count == 0)
            return null;

        for (File file : listFiles) {
            if (file.isDirectory())
                continue;

            if (file.getName().equals(fileName + OFFLINE_FILE_EXTENSION)) {
                try {
                    return file;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * Method getBasemapPath.
     *
     * @return String
     */
    public static String getBasemapPath() {
        return basemapPath;
    }

    // 清理离线数据库
    public void disposeOfflineDB() {

        if (offlinegdb != null) {
            offlinegdb.dispose();
            offlinegdb = null;
//            layers.clear();
//            layers = null;
//            layerLoaded = false;
        }
    }

    public void closeApp() {
        disposeOfflineDB();
        layers = null;
        if (this.naviGps != null) {
            this.naviGps.closeGpsDevice();
            this.naviGps.setGpsListener(null);
        }
        externalStorageAvailable = false;
        sdCard = null;
        iGisQueryDataFile = null;
        System.exit(0);
    }

    /**
     * 切换到某个工程
     *
     * @param prjName
     */
    public void changeToProject(String prjName) {
        // 清空上个工程保存的分析图层设置
        SetConfigData("analysislayers", "");
        File f_projectsconfig = getFile("projects.xml", "", "");
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        Document document = null;
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            document = builder.parse(f_projectsconfig);
            Element root = document.getDocumentElement();
            root.setAttribute("current", prjName);
            projectsconfig.setCurrentProjectName(prjName);
            GisQueryApplication.currentProjectName = prjName;
            TransformerFactory tFactory = TransformerFactory.newInstance();
            Transformer transformer = null;

            try {
                transformer = tFactory.newTransformer();
            } catch (TransformerConfigurationException e) {
                e.printStackTrace();
            }

            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(f_projectsconfig);

            try {
                transformer.transform(source, result);
            } catch (TransformerException e) {
                e.printStackTrace();
            }
            rebootSystem();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 重启当前系统
     */
    public void rebootSystem() {
//        final Intent intent = getPackageManager().getLaunchIntentForPackage(getPackageName());
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        startActivity(intent);
        // 解析当前工程
        loadProjectConfig();
        loadMapVersion();
        SdCardDBHelper.invalid();
        Intent intent = new Intent(
                this.getApplicationContext(), LoginActivity.class);
        intent.setFlags(
                Intent.FLAG_ACTIVITY_CLEAR_TASK |
                        Intent.FLAG_ACTIVITY_NEW_TASK);
        this.startActivity(intent);
    }

    /**
     * 开始任务自动更新
     */
    public void startTaskUpdateService() {
        Intent localIntent = new Intent();
        localIntent.setAction("com.guyu.android.gis.localservice.TaskUpdateService");
        localIntent.setPackage(getPackageName());
        localIntent.putExtra("opt", TaskUpdateService.TASK_UPDATE_0_START);
        startService(localIntent);
    }

    /**
     * 停止任务自动更新
     */
    public void stopTaskUpdateService() {
        Intent localIntent = new Intent();
        localIntent.setAction("com.guyu.android.gis.localservice.TaskUpdateService");
        localIntent.setPackage(getPackageName());
        localIntent.putExtra("opt", TaskUpdateService.TASK_UPDATE_1_STOP);
        startService(localIntent);
    }

    public boolean isLayerLoaded() {
        return layerLoaded;
    }

    public ArrayList<Layer> getLayers() {
        if (layers == null) {
            layers = new ArrayList<>();
            ArrayList<LayerConfig> al_basemaps = new ArrayList<>();
            al_basemaps.addAll(projectconfig
                    .getBasemaps());
            al_basemaps.addAll(projectconfig
                    .getOperationallayers());

//            for (LayerConfig lc : al_basemaps) {
//                loadOneLayer(lc);
//            }

            LinkedHashSet<String> nameSet = new LinkedHashSet<>();
            Map<String, LayerConfig> configMap = new HashMap<>();
            for (LayerConfig lc : al_basemaps) {
                nameSet.add(lc.getLabel());
                configMap.put(lc.getLabel(), lc);
            }
            Iterator<String> iter = nameSet.iterator();

            while (iter.hasNext()) {
                String key = iter.next();
                LayerConfig lc = configMap.get(key);
                loadOneLayer(lc);
            }
            al_basemaps = null;
            nameSet = null;
            configMap = null;

            layerLoaded = true;
        }
        return layers;
    }

    private void loadOneLayer(final LayerConfig layerConfig) {


        String layerType = layerConfig.getType();
        if (layerConfig.getUseoffline()) {
            // 使用离线数据
            if ("feature".equals(layerType)) {
                String offlineType = layerConfig.getOfflinetype();
                if (offlineType != null) {
                    if ("gdb".equals(offlineType)) {
                        // 创建FeatureLayer
                        FeatureLayer temLayer = getFeatureLayerByName(layerConfig
                                .getLabel());
                        temLayer.setVisible(layerConfig.getVisible());
                        layers.add(temLayer);
                        Log.d(TAG, "加载图层2---" + layerConfig.getLabel()
                                + " 可见---" + temLayer.isVisible());
                    } else if ("json".equals(offlineType)) {
                        String layerDefinition = getFileString(layerConfig.getDefinition(),
                                projectsconfig
                                        .getCurrentProjectName());
                        JsonFactory jsonFactory = new JsonFactory();
                        JsonParser jsonParser;
                        try {

                            // 创建ArcGISFeatureLayer
                            ArcGISFeatureLayer.Options layerOptions = new ArcGISFeatureLayer.Options();
                            layerOptions.mode = ArcGISFeatureLayer.MODE.SNAPSHOT;
                            ArcGISFeatureLayer temLayer = new ArcGISFeatureLayer(
                                    layerDefinition, null, layerOptions);
                            // 设置渲染器
                            jsonParser = jsonFactory
                                    .createJsonParser(layerDefinition);
                            LayerServiceInfo layerServiceInfo = LayerServiceInfo
                                    .fromJson(jsonParser);
                            DrawingInfo drawingInfo = layerServiceInfo
                                    .getDrawingInfo();
                            temLayer.setRenderer(drawingInfo.getRenderer());
                            // eg:temLayer.setRenderer(new SimpleRenderer(new
                            // SimpleFillSymbol(0xFFFF00)));
                            temLayer.setName(layerConfig.getLabel());
                            temLayer.setVisible(false);
                            Log.d(TAG, "加载图层3---" + layerConfig.getLabel()
                                    + " 可见---" + temLayer.isVisible());
                            loadFeatureLayerData((ArcGISFeatureLayer) temLayer, layerConfig);
                            layers.add(temLayer);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                }

            }

            else if ("tiled".equals(layerType)) {
                TiledLayer temLayer = new ArcGISLocalTiledLayer(
                        GisQueryApplication.currentProjectPath
                                + layerConfig.getOfflinedata());
                temLayer.setName(layerConfig.getLabel());
                temLayer.setVisible(layerConfig.getVisible());
                layers.add(temLayer);
//                        map.addLayer(temLayer);
                Log.d(TAG, "加载图层1---" + layerConfig.getLabel() + " 可见---"
                        + temLayer.isVisible());
            }
        } else {
            // 使用在线数据
            if ("tiled".equals(layerType)) {
                ArcGISTiledMapServiceLayer temLayer = new ArcGISTiledMapServiceLayer(
                        layerConfig.getUrl());
                temLayer.setName(layerConfig.getLabel());
                temLayer.setVisible(layerConfig.getVisible());
                Log.d(TAG, "加载图层4---" + layerConfig.getLabel() + " 可见---"
                        + temLayer.isVisible());
                layers.add(temLayer);
            } else if ("feature".equals(layerType)) {
                // 创建在线FeatureLayer
                ArcGISFeatureLayer.Options layerOptions = new ArcGISFeatureLayer.Options();
                layerOptions.mode = ArcGISFeatureLayer.MODE.ONDEMAND;
                ArcGISFeatureLayer temLayer = new ArcGISFeatureLayer(
                        layerConfig.getUrl(), layerOptions);

                temLayer.setName(layerConfig.getLabel());
                temLayer.setVisible(false);
                layers.add(temLayer);
                Log.d(TAG, "加载图层5---" + layerConfig.getLabel() + " 可见---"
                        + temLayer.isVisible());
            } else if ("dynamic".equals(layerType)) {
                // 创建在线矢量图层
                ArcGISDynamicMapServiceLayer temLayer = new ArcGISDynamicMapServiceLayer(
                        layerConfig.getUrl());
                temLayer.setName(layerConfig.getLabel());
                temLayer.setVisible(false);
                layers.add(temLayer);
                Log.d(TAG, "加载图层6---" + layerConfig.getLabel() + " 可见---"
                        + temLayer.isVisible());
            }
        }


    }

    private void loadFeatureLayerData(final ArcGISFeatureLayer featureLayer,
                                      final LayerConfig layerConfig) {
        new Thread() {
            @Override
            public void run() {
                FeatureSet fs = getFeatureSet(
                        layerConfig.getOfflinedata(),
                        projectsconfig
                                .getCurrentProjectName());
                if (fs != null) {
                    Graphic[] grasAry = fs.getGraphics();
                    int batchCount = 2000;
                    int loadBatch = (int) Math.ceil(grasAry.length
                            / (float) batchCount);
                    for (int i = 1; i <= loadBatch; i++) {
                        Graphic[] curGrasAry = getCurrentBatchGraphics(grasAry,
                                batchCount, i);
                        featureLayer.addGraphics(curGrasAry);
                        try {
                            sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                } else {
                    Log.e("用户操作", "加载" + featureLayer.getName() + "图层数据失败!");
                }

            }
        }.start();
    }

    /**
     * 根据名称获取离线数据库中的图层对象
     *
     * @param layerName 图层名称
     * @return
     */
    private FeatureLayer getFeatureLayerByName(String layerName) {
        long s = System.currentTimeMillis();
        Geodatabase offlinegdb = getOfflineGDB();
        FeatureLayer temFeatureLayer = null;
        for (GeodatabaseFeatureTable gdbFeatureTable : offlinegdb
                .getGeodatabaseTables()) {
            temFeatureLayer = new FeatureLayer(gdbFeatureTable);
            if (layerName.equals(temFeatureLayer.getName())) {
                break;
            }
        }

        // offlinegdb.dispose();
        return temFeatureLayer;
    }

    /**
     * 获取离线地理数据库
     */
    private Geodatabase getOfflineGDB() {
        if (offlinegdb == null) {
            String gdbpath = GisQueryApplication.currentProjectPath
                    + "OfflineData/offlinedata.geodatabase";
            try {
                offlinegdb = new Geodatabase(gdbpath);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return offlinegdb;

    }

    /**
     * 获取当前批次加载图形数组
     *
     * @param grasAry    整个数组
     * @param batchCount 每批次数量
     * @param batchNum   批次编号
     * @return 当前批次的图形数组
     */
    private Graphic[] getCurrentBatchGraphics(Graphic[] grasAry,
                                              int batchCount, int batchNum) {
        if (grasAry.length < batchCount) {
            return grasAry;
        }
        Graphic[] rtnAry = new Graphic[batchCount];
        int startIndex = (batchNum - 1) * batchCount;
        int endIndex = startIndex + batchCount;
        if (endIndex > grasAry.length) {
            endIndex = grasAry.length;
        }
        for (int i = startIndex, j = 0; i < endIndex; i++, j++) {
            rtnAry[j] = grasAry[i];
        }
        return rtnAry;
    }
}
