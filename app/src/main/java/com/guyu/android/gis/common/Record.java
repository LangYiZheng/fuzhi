package com.guyu.android.gis.common;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.esri.core.geometry.Geometry;

public class Record {
	private Geometry geometry = null;
	private String layerCaption;
	private String layerName;
	private List<String> lstFieldsName = null;
	private List<String> lstFieldsValue = null;
	private String queryKey;
	private String showValue;
	public int smid;

	public Record() {
	}

	public Record(String p_queryKey, String p_layerName,
			Geometry p_geometry, List<String> p_lstFieldsName,
			List<String> p_lstFieldsValue) {
		this.queryKey = p_queryKey;
		this.layerName = p_layerName;
		this.geometry = p_geometry;
		setLstFieldsName(p_lstFieldsName);
		setLstFieldsValue(p_lstFieldsValue);
	}

	public Record(String p_queryKey, String p_layerName,
			String p_layerCaption, Geometry p_geometry,
			List<String> p_lstFieldsName, List<String> p_lstFieldsValue) {
		this.queryKey = p_queryKey;
		this.layerName = p_layerName;
		this.layerCaption = p_layerCaption;
		this.geometry = p_geometry;
		setLstFieldsName(p_lstFieldsName);
		setLstFieldsValue(p_lstFieldsValue);
	}

	public Record(String p_queryKey, String p_layerName,
			String p_layerCaption, Geometry p_geometry,
			String[] p_fieldsName, List<String> p_lstFieldsValue) {
		this.queryKey = p_queryKey;
		this.layerName = p_layerName;
		this.layerCaption = p_layerCaption;
		this.geometry = p_geometry;
		setLstFieldsName(p_fieldsName);
		setLstFieldsValue(p_lstFieldsValue);
	}
	public String getQueryKey() {
		return queryKey;
	}

	public void setQueryKey(String queryKey) {
		this.queryKey = queryKey;
	}
	public Geometry getGeometry() {
		return this.geometry;
	}

	public String getLayerCaption() {
		return this.layerCaption;
	}

	public String getLayerName() {
		return this.layerName;
	}

	public List<String> getLstFieldsName() {
		return this.lstFieldsName;
	}

	public List<String> getLstFieldsValue() {
		return this.lstFieldsValue;
	}

	public String getShowValue() {
		Iterator<String> localIterator = this.lstFieldsValue.iterator();
		while (localIterator.hasNext()){
	      String str = localIterator.next();
	      if (str.contains(this.queryKey)){
	    	  this.showValue = str;
	    	  break;
	      }
	    }
		return showValue;
	}

	public void setGeometry(Geometry paramGeometry) {
		this.geometry = paramGeometry;
	}

	public void setLayerCaption(String paramString) {
		this.layerCaption = paramString;
	}

	public void setLayerName(String paramString) {
		this.layerName = paramString;
	}

	public void setLstFieldsName(List<String> paramList) {
		if (this.lstFieldsName == null)
			this.lstFieldsName = new ArrayList();
		this.lstFieldsName.addAll(paramList);
	}

	public void setLstFieldsName(String[] paramArrayOfString) {
		if (this.lstFieldsName == null)
			this.lstFieldsName = new ArrayList();
		int i = paramArrayOfString.length;
		for (int j = 0;j<i; j++) {
			this.lstFieldsName.add(paramArrayOfString[j]);
		}
	}

	public void setLstFieldsValue(List<String> paramList) {
		if (this.lstFieldsValue == null)
			this.lstFieldsValue = new ArrayList();
		this.lstFieldsValue.addAll(paramList);
	}

	public void setLstFieldsValue(String[] paramArrayOfString) {
		if (this.lstFieldsValue == null)
			this.lstFieldsValue = new ArrayList();
		int i = paramArrayOfString.length;
		for (int j = 0;j<i; j++) {
			this.lstFieldsValue.add( paramArrayOfString[j]);
		}
	}

	public void setShowValue(String paramString) {
		this.showValue = paramString;
	}
}