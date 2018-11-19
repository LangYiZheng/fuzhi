package com.guyu.android.gis.config;

public class GpsProjectFix {
private boolean enabled;//是否启用投影修正
private double offsetx;//X偏移量
private double offsety;//Y偏移量
public boolean isEnabled() {
	return enabled;
}
public void setEnabled(boolean enabled) {
	this.enabled = enabled;
}
public double getOffsetx() {
	return offsetx;
}
public void setOffsetx(double offsetx) {
	this.offsetx = offsetx;
}
public double getOffsety() {
	return offsety;
}
public void setOffsety(double offsety) {
	this.offsety = offsety;
}

}
