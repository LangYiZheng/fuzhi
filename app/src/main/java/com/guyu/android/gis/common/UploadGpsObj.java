package com.guyu.android.gis.common;

import com.esri.core.geometry.Point;

public class UploadGpsObj {
	private Point gpsPoint;
	private String gpsTime;

	public Point getGpsPoint() {
		return gpsPoint;
	}

	public void setGpsPoint(Point gpsPoint) {
		this.gpsPoint = gpsPoint;
	}

	public String getGpsTime() {
		return gpsTime;
	}

	public void setGpsTime(String gpsTime) {
		this.gpsTime = gpsTime;
	}
	
}
