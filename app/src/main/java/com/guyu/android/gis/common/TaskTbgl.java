package com.guyu.android.gis.common;

public class TaskTbgl extends Task {
	/**
	 * 图斑管理（卫片核查）
	 */
	private static final long serialVersionUID = 8337712996256587635L;
	private String QXMC;// 区县名称
	private String TBBH;// 图斑编号
	private String DLMC;// 地类名称
	private String TFH;// 图幅号
	private String ZLDWMC;// 坐落单位名称
	private String QSDWMC;// 权属单位名称
	private String TBMJ;// 图斑面积
	private String TBLX;// 图斑类型

	public String getQXMC() {
		return QXMC;
	}

	public void setQXMC(String qXMC) {
		QXMC = qXMC;
	}

	public String getTBBH() {
		return TBBH;
	}

	public void setTBBH(String tBBH) {
		TBBH = tBBH;
	}

	public String getDLMC() {
		return DLMC;
	}

	public void setDLMC(String dLMC) {
		DLMC = dLMC;
	}

	public String getTFH() {
		return TFH;
	}

	public void setTFH(String tFH) {
		TFH = tFH;
	}

	public String getZLDWMC() {
		return ZLDWMC;
	}

	public void setZLDWMC(String zLDWMC) {
		ZLDWMC = zLDWMC;
	}

	public String getQSDWMC() {
		return QSDWMC;
	}

	public void setQSDWMC(String qSDWMC) {
		QSDWMC = qSDWMC;
	}

	public String getTBMJ() {
		return TBMJ;
	}

	public void setTBMJ(String tBMJ) {
		TBMJ = tBMJ;
	}

	public String getTBLX() {
		return TBLX;
	}

	public void setTBLX(String tBLX) {
		TBLX = tBLX;
	}

}
