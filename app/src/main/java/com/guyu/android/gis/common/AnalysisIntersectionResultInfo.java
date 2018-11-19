package com.guyu.android.gis.common;

import java.util.Map;

import com.esri.core.geometry.Geometry;

public class AnalysisIntersectionResultInfo {
	private Double area;
	private Geometry geometry;
	private Map attributes;

	public Double getArea() {
		return area;
	}

	public void setArea(Double area) {
		this.area = area;
	}

	public Geometry getGeometry() {
		return geometry;
	}

	public void setGeometry(Geometry geometry) {
		this.geometry = geometry;
	}

	public Map getAttributes() {
		return attributes;
	}

	public void setAttributes(Map attributes) {
		this.attributes = attributes;
	}
}
