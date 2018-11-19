package com.guyu.android.gis.config;

public class MapBackgroundConfig {

	private String bkColor;//地图背景颜色
	private String gridColor;//网格颜色
	private float gridSize;//网格大小
	private float gridLineSize;//网格线粗细

	public String getBkColor() {
		return bkColor;
	}

	public void setBkColor(String bkColor) {
		this.bkColor = bkColor;
	}

	public String getGridColor() {
		return gridColor;
	}

	public void setGridColor(String gridColor) {
		this.gridColor = gridColor;
	}

	public float getGridSize() {
		return gridSize;
	}

	public void setGridSize(float gridSize) {
		this.gridSize = gridSize;
	}

	public float getGridLineSize() {
		return gridLineSize;
	}

	public void setGridLineSize(float gridLineSize) {
		this.gridLineSize = gridLineSize;
	}
}
