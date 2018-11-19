package com.guyu.android.gis.common;

import java.io.Serializable;

public class Case implements Serializable {
	/**
	 * 案件
	 */
	private static final long serialVersionUID = 955164239052620741L;
	protected String caseId;// 案件ID      /原申请编号   / 项目名称 /申请编号 /编号  /立案文号  /承办人意见 / 申请单位
	protected String caseName;// 案件名称      /原林权证号   /  null   / null   /null/ null    / null     / null
	protected String caseType;// 案件类型      /
	protected String attrs;// 属性JSON     
	protected String geos;// 图形JSON

	protected String partId;// 人员标识            /
	protected int upState = 0;// 上报状态    
	
	protected String imgLst;// 拍照
	protected String soundLst;// 录音
	protected String videoLst;// 录像

	protected String createTime;// 创建时间
	private String recId;// 案卷标识

	public boolean isUp() {
		return (this.upState == 1);
	}

	public String getCaseId() {
		return caseId;
	}

	public void setCaseId(String caseId) {
		this.caseId = caseId;
	}

	public String getCaseName() {
		return caseName;
	}

	public void setCaseName(String caseName) {
		this.caseName = caseName;
	}

	public String getCaseType() {
		return caseType;
	}

	public void setCaseType(String caseType) {
		this.caseType = caseType;
	}

	public String getAttrs() {
		return attrs;
	}

	public void setAttrs(String attrs) {
		this.attrs = attrs;
	}

	public String getGeos() {
		return geos;
	}

	public void setGeos(String geos) {
		this.geos = geos;
	}

	public String getPartId() {
		return partId;
	}

	public void setPartId(String partId) {
		this.partId = partId;
	}

	public int getUpState() {
		return upState;
	}

	public void setUpState(int upState) {
		this.upState = upState;
	}

	public String getImgLst() {
		return imgLst;
	}

	public void setImgLst(String imgLst) {
		this.imgLst = imgLst;
	}

	public String getSoundLst() {
		return soundLst;
	}

	public void setSoundLst(String soundLst) {
		this.soundLst = soundLst;
	}

	public String getVideoLst() {
		return videoLst;
	}

	public void setVideoLst(String videoLst) {
		this.videoLst = videoLst;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getRecId() {
		return recId;
	}

	public void setRecId(String recId) {
		this.recId = recId;
	}
}
