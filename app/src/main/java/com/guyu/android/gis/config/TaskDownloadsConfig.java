package com.guyu.android.gis.config;

import java.util.Map;

public class TaskDownloadsConfig {
private String downloadUrl;
private String updateReceivedUrl;
private Map<String,TaskDownloadConfig> taskDownLoadConfigMap;
public String getDownloadUrl() {
	return downloadUrl;
}
public void setDownloadUrl(String downloadUrl) {
	this.downloadUrl = downloadUrl;
}
public String getUpdateReceivedUrl() {
	return updateReceivedUrl;
}
public void setUpdateReceivedUrl(String updateReceivedUrl) {
	this.updateReceivedUrl = updateReceivedUrl;
}
public Map<String, TaskDownloadConfig> getTaskDownLoadConfigMap() {
	return taskDownLoadConfigMap;
}
public void setTaskDownLoadConfigMap(
		Map<String, TaskDownloadConfig> taskDownLoadConfigMap) {
	this.taskDownLoadConfigMap = taskDownLoadConfigMap;
}
public String getTaskDownloadUrl(String taskType){
	TaskDownloadConfig taskDownloadConfig=taskDownLoadConfigMap.get(taskType);
	return downloadUrl+"?attrsViewId="+taskDownloadConfig.getAttrsViewId()+"&geoViewId="+taskDownloadConfig.getGeoViewId();
}
}
