package com.guyu.android.gis.config;

import org.json.JSONObject;

public class LayerConfig {
	private String label;//图层名字
	private String name;//图层中文名字
	private String type;//图层类型 tiled 切片;dynamic 矢量;feature 要素
	private Boolean visible;//是否可见
	private Boolean useoffline;//是否使用离线
	private String offlinetype;//离线数据类型
	private String url;//图层在线服务路径
	private String offlinedata;//图层数据文件(FeatureLayer使用)
	private String definition;//图层定义文件(FeatureLayer使用)
	private String[] originalfields;//原始字段
	private String[] displayfields;//显示字段
	private JSONObject fieldDict; // 字段对应的字典

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Boolean getVisible() {
		return visible;
	}
	public void setVisible(Boolean visible) {
		this.visible = visible;
	}
	public Boolean getUseoffline() {
		return useoffline;
	}
	public void setUseoffline(Boolean useoffline) {
		this.useoffline = useoffline;
	}
	
	public String getOfflinetype() {
		return offlinetype;
	}
	public void setOfflinetype(String offlinetype) {
		this.offlinetype = offlinetype;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getOfflinedata() {
		return offlinedata;
	}
	public void setOfflinedata(String offlinedata) {
		this.offlinedata = offlinedata;
	}
	public String getDefinition() {
		return definition;
	}
	public void setDefinition(String definition) {
		this.definition = definition;
	}
	public String[] getOriginalfields() {
		return originalfields;
	}
	public void setOriginalfields(String[] originalfields) {
		this.originalfields = originalfields;
	}
	public String[] getDisplayfields() {
		return displayfields;
	}
	public void setDisplayfields(String[] displayfields) {
		this.displayfields = displayfields;
	}
	
	public void setFieldDict(JSONObject fieldDict) {
		this.fieldDict = fieldDict;
	}
	
	public JSONObject getFieldDict() {
		return fieldDict;
	}
	
}
