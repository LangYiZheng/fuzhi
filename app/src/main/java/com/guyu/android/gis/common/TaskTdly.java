package com.guyu.android.gis.common;

public class TaskTdly extends Task {
	/**
	 * 土地利用动态巡查
	 */
	private static final long serialVersionUID = 1L;
	private String XMMC;// 项目名称
	private String DZJGH;// 电子监管号
	private String TDYT;// 土地用途
	private String GYFS;// 供应方式
	private String SRR;// 受让人
	private double GYMJ;// 供应面积
	private String TDZL;// 土地坐落
	private String XCJD;//巡查阶段
	public String getXMMC() {
		return XMMC;
	}

	public void setXMMC(String xMMC) {
		XMMC = xMMC;
	}

	public String getDZJGH() {
		return DZJGH;
	}

	public void setDZJGH(String dZJGH) {
		DZJGH = dZJGH;
	}

	public String getTDYT() {
		return TDYT;
	}

	public void setTDYT(String tDYT) {
		TDYT = tDYT;
	}

	public String getGYFS() {
		return GYFS;
	}

	public void setGYFS(String gYFS) {
		GYFS = gYFS;
	}

	public String getSRR() {
		return SRR;
	}

	public void setSRR(String sRR) {
		SRR = sRR;
	}

	public double getGYMJ() {
		return GYMJ;
	}

	public void setGYMJ(double gYMJ) {
		GYMJ = gYMJ;
	}

	public String getTDZL() {
		return TDZL;
	}

	public void setTDZL(String tDZL) {
		TDZL = tDZL;
	}

	public String getXCJD() {
		return XCJD;
	}

	public void setXCJD(String xCJD) {
		XCJD = xCJD;
	}
	
}
