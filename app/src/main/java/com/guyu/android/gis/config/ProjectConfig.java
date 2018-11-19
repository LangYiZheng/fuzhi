package com.guyu.android.gis.config;

import java.util.ArrayList;

import com.esri.core.geometry.Envelope;
import com.esri.core.geometry.SpatialReference;
import com.guyu.android.base.BaseEntity;
import com.guyu.android.gis.common.MapVersionList;

public class ProjectConfig {
    private String isysbaseurl;// isys基本路径
    private String apptitle;// 应用标题
    private String appVersionCheckUrl;// 应用版本监测路径
    private String tracklistUrl;// 应用版本监测路径
    private String downloadtrackUrl;// 巡查轨迹多媒体文件下载地址
    private AnyChatServerConfig anyChatServerConfig;// AnyChat服务器配置
    private CaseUploadsConfig caseUploadsConfig;// 案件上报配置
    private String trackUploadOnlineUrl;// 地块巡查上报地址
    private String trackUploadOfflineUrl;// 巡查轨迹离线上传地址
    private String uploadtrackurl;// 巡查轨迹多媒体文件上传地址



    private String cantonZoneDownloadUrl;// 区划定位下载地址
    private int cantonZoneRootCode;// 行政区划根节点代码
    private String bizDocDefDownloadUrl;// 业务要件定义类别下载路径
    private TaskDownloadsConfig taskDownloadsConfig;// 任务下载配置
    private SpatialReference spatialreference;// 地图坐标系
    private GpsProjectFix gpsProjectFix;
    private Envelope initialextent;// 初始视野
    private Envelope fullextent;// 全图视野

    private Boolean useofflinequery;// 使用离线查询
    private Boolean useinitialspatialreference;// 使用初始坐标系设置
    private Boolean usebluetoothcollect;// 使用蓝牙配对采集
    private MapBackgroundConfig mapbackgroundconfig;// 地图背景设置
    private ArrayList<LayerConfig> basemaps;// 底图图层
    private ArrayList<LayerConfig> operationallayers;// 业务图层
    private ArrayList<QuickToggleLayerConfig> quicktogglelayers;// 快捷图层
    private String[] functioncodes;// 授权功能代码
    private String licensekey;// 软件授权码

    private String appDownloadUrl;// app下载url
    private String maplisturl;// 地图list url
    private String mapdownloadurl;// 地图下载url 根据tpkId下载
    private BaseEntity<MapVersionList> mapVersion;// 地图版本配置

    public String getDownloadtrackUrl() {
        return downloadtrackUrl;
    }

    public void setDownloadtrackUrl(String downloadtrackUrl) {
        this.downloadtrackUrl = downloadtrackUrl;
    }

    public String getTracklistUrl() {
        return tracklistUrl;
    }

    public void setTracklistUrl(String tracklistUrl) {
        this.tracklistUrl = tracklistUrl;
    }

    public String getUploadtrackurl() {
        return uploadtrackurl;
    }

    public void setUploadtrackurl(String uploadtrackurl) {
        this.uploadtrackurl = uploadtrackurl;
    }

    public BaseEntity<MapVersionList> getMapVersion() {
        return mapVersion;
    }

    public void setMapVersion(BaseEntity<MapVersionList> mapVersion) {
        this.mapVersion = mapVersion;
    }

    public String getAppDownloadUrl() {
        return appDownloadUrl;
    }

    public String getMaplisturl() {
        return maplisturl;
    }

    public void setMaplisturl(String maplisturl) {
        this.maplisturl = maplisturl;
    }

    public String getMapdownloadurl() {
        return mapdownloadurl;
    }

    public void setMapdownloadurl(String mapdownloadurl) {
        this.mapdownloadurl = mapdownloadurl;
    }

    public void setAppDownloadUrl(String appDownloadUrl) {
        this.appDownloadUrl = appDownloadUrl;
    }

    public String getTaskDownloadUrl(String taskType) {
        return taskDownloadsConfig.getTaskDownloadUrl(taskType);
    }

    public String getApptitle() {
        return apptitle;
    }

    public void setApptitle(String apptitle) {
        this.apptitle = apptitle;
    }

    public String getIsysbaseurl() {
        return isysbaseurl;
    }

    public void setIsysbaseurl(String isysbaseurl) {
        this.isysbaseurl = isysbaseurl;
    }

    public String getAppVersionCheckUrl() {
        return appVersionCheckUrl;
    }

    public void setAppVersionCheckUrl(String appVersionCheckUrl) {
        this.appVersionCheckUrl = appVersionCheckUrl;
    }

    public AnyChatServerConfig getAnyChatServerConfig() {
        return anyChatServerConfig;
    }

    public void setAnyChatServerConfig(AnyChatServerConfig anyChatServerConfig) {
        this.anyChatServerConfig = anyChatServerConfig;
    }

    public SpatialReference getSpatialreference() {
        return spatialreference;
    }

    public void setSpatialreference(SpatialReference spatialreference) {
        this.spatialreference = spatialreference;
    }

    public GpsProjectFix getGpsProjectFix() {
        return gpsProjectFix;
    }

    public void setGpsProjectFix(GpsProjectFix gpsProjectFix) {
        this.gpsProjectFix = gpsProjectFix;
    }

    public Envelope getInitialextent() {
        return initialextent;
    }

    public void setInitialextent(Envelope initialextent) {
        this.initialextent = initialextent;
    }

    public Boolean getUsebluetoothcollect() {
        return usebluetoothcollect;
    }

    public void setUsebluetoothcollect(Boolean usebluetoothcollect) {
        this.usebluetoothcollect = usebluetoothcollect;
    }

    public Envelope getFullextent() {
        return fullextent;
    }

    public void setFullextent(Envelope fullextent) {
        this.fullextent = fullextent;
    }

    public CaseUploadsConfig getCaseUploadsConfig() {
        return caseUploadsConfig;
    }

    public void setCaseUploadsConfig(CaseUploadsConfig caseUploadsConfig) {
        this.caseUploadsConfig = caseUploadsConfig;
    }

    public String getTrackUploadOnlineUrl() {
        return trackUploadOnlineUrl;
    }

    public void setTrackUploadOnlineUrl(String trackUploadOnlineUrl) {
        this.trackUploadOnlineUrl = trackUploadOnlineUrl;
    }

    public String getTrackUploadOfflineUrl() {
        return trackUploadOfflineUrl;
    }

    public void setTrackUploadOfflineUrl(String trackUploadOfflineUrl) {
        this.trackUploadOfflineUrl = trackUploadOfflineUrl;
    }

    public TaskDownloadsConfig getTaskDownloadsConfig() {
        return taskDownloadsConfig;
    }

    public void setTaskDownloadsConfig(TaskDownloadsConfig taskDownloadsConfig) {
        this.taskDownloadsConfig = taskDownloadsConfig;
    }

    public String getCantonZoneDownloadUrl() {
        return cantonZoneDownloadUrl;
    }

    public void setCantonZoneDownloadUrl(String cantonZoneDownloadUrl) {
        this.cantonZoneDownloadUrl = cantonZoneDownloadUrl;
    }

    public int getCantonZoneRootCode() {
        return cantonZoneRootCode;
    }

    public void setCantonZoneRootCode(int cantonZoneRootCode) {
        this.cantonZoneRootCode = cantonZoneRootCode;
    }

    public String getBizDocDefDownloadUrl() {
        return bizDocDefDownloadUrl;
    }

    public void setBizDocDefDownloadUrl(String bizDocDefDownloadUrl) {
        this.bizDocDefDownloadUrl = bizDocDefDownloadUrl;
    }

    public Boolean getUseofflinequery() {
        return useofflinequery;
    }

    public void setUseofflinequery(Boolean useofflinequery) {
        this.useofflinequery = useofflinequery;
    }

    public Boolean getUseinitialspatialreference() {
        return useinitialspatialreference;
    }

    public void setUseinitialspatialreference(Boolean useinitialspatialreference) {
        this.useinitialspatialreference = useinitialspatialreference;
    }

    public MapBackgroundConfig getMapbackgroundconfig() {
        return mapbackgroundconfig;
    }

    public void setMapbackgroundconfig(MapBackgroundConfig mapbackgroundconfig) {
        this.mapbackgroundconfig = mapbackgroundconfig;
    }

    public ArrayList<LayerConfig> getBasemaps() {
        return basemaps;
    }

    public void setBasemaps(ArrayList<LayerConfig> basemaps) {
        this.basemaps = basemaps;
    }

    public ArrayList<LayerConfig> getOperationallayers() {
        return operationallayers;
    }

    public void setOperationallayers(ArrayList<LayerConfig> operationallayers) {
        this.operationallayers = operationallayers;
    }

    public ArrayList<QuickToggleLayerConfig> getQuicktogglelayers() {
        return quicktogglelayers;
    }

    public void setQuicktogglelayers(ArrayList<QuickToggleLayerConfig> quicktogglelayers) {
        this.quicktogglelayers = quicktogglelayers;
    }

    public String[] getFunctioncodes() {
        return functioncodes;
    }

    public void setFunctioncodes(String[] functioncodes) {
        this.functioncodes = functioncodes;
    }

    public boolean exitFunctionCode(String functioncode) {
        for (int i = 0; i < functioncodes.length; i++) {
            if (functioncode.equals(this.functioncodes[i])) {
                return true;
            }
        }
        return false;
    }

    public String getLicensekey() {
        return licensekey;
    }

    public void setLicensekey(String licensekey) {
        this.licensekey = licensekey;
    }

    public LayerConfig getLayerConfigByLayerName(String layerName) {
        for (LayerConfig lc : basemaps) {
            if (layerName.equals(lc.getLabel())) {
                return lc;
            }
        }
        for (LayerConfig lc : operationallayers) {
            if (layerName.equals(lc.getLabel())) {
                return lc;
            }
        }
        return null;
    }
}
