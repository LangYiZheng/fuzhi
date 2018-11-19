package com.guyu.android.gis.common;

public class Canton {
	public static Integer TYPE_PROVINCE = 2;// 省级";
	public static Integer TYPE_CITY = 3;// 市级
	public static Integer TYPE_CONTY = 4;// 县级
	public static Integer TYPE_TOWN = 5;// "镇级";
	public static Integer TYPE_STREET = 6;// "街道";
	public static Integer TYPE_VILLAGE = 7;// 村级

	private int cantonId;
	private String cantonName;
	private int cantonLevel;
	private int parentId;
	private int dispOrder;

	public int getDispOrder() {
		return dispOrder;
	}

	public void setDispOrder(int dispOrder) {
		this.dispOrder = dispOrder;
	}

	public int getCantonId() {
		return cantonId;
	}

	public void setCantonId(int cantonId) {
		this.cantonId = cantonId;
	}

	public String getCantonName() {
		return cantonName;
	}

	public void setCantonName(String cantonName) {
		this.cantonName = cantonName;
	}

	public int getCantonLevel() {
		return cantonLevel;
	}

	public void setCantonLevel(int cantonLevel) {
		this.cantonLevel = cantonLevel;
	}

	public int getParentId() {
		return parentId;
	}

	public void setParentId(int parentId) {
		this.parentId = parentId;
	}

	public boolean isCity() {
        return this.cantonLevel == TYPE_CITY;
	}

	public boolean isCounty() {
        return this.cantonLevel == TYPE_CONTY;
	}

	public boolean isPro() {
        return this.cantonLevel == TYPE_PROVINCE;
	}

	public boolean isStreet() {
        return this.cantonLevel == TYPE_STREET || this.cantonLevel == TYPE_TOWN;
	}
}
