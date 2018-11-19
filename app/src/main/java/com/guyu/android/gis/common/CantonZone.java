package com.guyu.android.gis.common;

public class CantonZone {
	private String zoneid;
	private String parentid;
	private String zonename;
	private String zonedata;
	private int disporder;
	private int zonecode;
	private int type;
	
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getZoneid() {
		return zoneid;
	}
	public void setZoneid(String zoneid) {
		this.zoneid = zoneid;
	}
	public String getParentid() {
		return parentid;
	}
	public void setParentid(String parentid) {
		this.parentid = parentid;
	}
	public String getZonename() {
		return zonename;
	}
	public CantonZone(String zoneid, String parentid, String zonename,
			String zonedata, int disporder, int zonecode) {
		super();
		this.zoneid = zoneid;
		this.parentid = parentid;
		this.zonename = zonename;
		this.zonedata = zonedata;
		this.disporder = disporder;
		this.zonecode = zonecode;
	}
	public CantonZone() {
		super();
	}
	public CantonZone(String zonedata) {
		super();
		this.zonedata = zonedata;
	}
	public void setZonename(String zonename) {
		this.zonename = zonename;
	}
	public String getZonedata() {
		return zonedata;
	}
	public void setZonedata(String zonedata) {
		this.zonedata = zonedata;
	}
	public int getDisporder() {
		return disporder;
	}
	public void setDisporder(int disporder) {
		this.disporder = disporder;
	}
	public int getZonecode() {
		return zonecode;
	}
	public void setZonecode(int zonecode) {
		this.zonecode = zonecode;
	}
}
