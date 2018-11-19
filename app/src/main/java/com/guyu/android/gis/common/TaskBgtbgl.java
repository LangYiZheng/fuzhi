package com.guyu.android.gis.common;

public class TaskBgtbgl extends Task {
	/**
	 * 变更图斑管理（年度变更调查）
	 */
	private static final long serialVersionUID = 1L;
	private String XH;// 序号
	private String TBBH;// 图斑编号
	private String TBLX;// 图斑类型
	private String XZQH;// 行政区划
	private double JCMJ;// 监测面积
	private String QSX;// 前时相
	private String HSX;// 后时相
	private String HCND;// 核查年度	

	public String getXH() {
		return XH;
	}

	public void setXH(String xH) {
		XH = xH;
	}

	public String getTBBH() {
		return TBBH;
	}

	public void setTBBH(String tBBH) {
		TBBH = tBBH;
	}

	public String getTBLX() {
		return TBLX;
	}

	public void setTBLX(String tBLX) {
		TBLX = tBLX;
	}

	public String getXZQH() {
		return XZQH;
	}

	public void setXZQH(String xZQH) {
		XZQH = xZQH;
	}

	public double getJCMJ() {
		return JCMJ;
	}

	public void setJCMJ(double jCMJ) {
		JCMJ = jCMJ;
	}

	public String getQSX() {
		return QSX;
	}

	public void setQSX(String qSX) {
		QSX = qSX;
	}

	public String getHSX() {
		return HSX;
	}

	public void setHSX(String hSX) {
		HSX = hSX;
	}

	public String getHCND() {
		return HCND;
	}

	public void setHCND(String hCND) {
		HCND = hCND;
	}

}
