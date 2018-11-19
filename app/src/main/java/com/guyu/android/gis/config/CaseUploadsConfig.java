package com.guyu.android.gis.config;

import java.util.Map;

public class CaseUploadsConfig {
	private String uploadUrl;
	private Map<String,CaseUploadConfig> caseUploadConfigMap;
	public String getUploadUrl() {
		return uploadUrl;
	}
	public void setUploadUrl(String uploadUrl) {
		this.uploadUrl = uploadUrl;
	}
	public Map<String, CaseUploadConfig> getCaseUploadConfigMap() {
		return caseUploadConfigMap;
	}
	public void setCaseUploadConfigMap(
			Map<String, CaseUploadConfig> caseUploadConfigMap) {
		this.caseUploadConfigMap = caseUploadConfigMap;
	}
	
}
