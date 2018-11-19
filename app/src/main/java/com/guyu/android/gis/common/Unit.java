package com.guyu.android.gis.common;

public class Unit {
	private int unitId;
	private String unitName;
	private int parentId;
	private int dispOrder;
	private int cantonCode;
	public int getUnitId() {
		return unitId;
	}
	public void setUnitId(int unitId) {
		this.unitId = unitId;
	}
	public String getUnitName() {
		return unitName;
	}
	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}
	public int getParentId() {
		return parentId;
	}
	public void setParentId(int parentId) {
		this.parentId = parentId;
	}
	public int getDispOrder() {
		return dispOrder;
	}
	public void setDispOrder(int dispOrder) {
		this.dispOrder = dispOrder;
	}
	public int getCantonCode() {
		return cantonCode;
	}
	public void setCantonCode(int cantonCode) {
		this.cantonCode = cantonCode;
	}
	
}
