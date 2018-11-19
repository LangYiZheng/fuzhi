package com.guyu.android.gis.common;

import java.io.Serializable;

public class CaseImg implements Serializable {
	/**
	 * 案件图片
	 */
	private static final long serialVersionUID = -87993899297474286L;
	private String imgName;
	private String imgPath;
	private String caseId;
	private String partId;
	private String prjName;
	private long time;
	private String docDefId;

	public CaseImg() {
	}

	public CaseImg(String p_caseId, String p_docDefId, String p_imgName,
			String p_imgPath, long p_time) {
		this.caseId = p_caseId;
		this.docDefId = p_docDefId;
		this.imgName = p_imgName;
		this.imgPath = p_imgPath;
		this.time = p_time;
	}

	public String getImgName() {
		return this.imgName;
	}

	public String getImgPath() {
		return this.imgPath;
	}

	public String getCaseId() {
		return caseId;
	}

	public void setCaseId(String caseId) {
		this.caseId = caseId;
	}

	public String getPartId() {
		return partId;
	}

	public void setPartId(String partId) {
		this.partId = partId;
	}

	public String getDocDefId() {
		return docDefId;
	}

	public void setDocDefId(String docDefId) {
		this.docDefId = docDefId;
	}

	public String getPrjName() {
		return this.prjName;
	}

	public long getTime() {
		return this.time;
	}

	public void setImgName(String paramString) {
		this.imgName = paramString;
	}

	public void setImgPath(String paramString) {
		this.imgPath = paramString;
	}

	public void setPrjName(String paramString) {
		this.prjName = paramString;
	}

	public void setTime(long paramLong) {
		this.time = paramLong;
	}

}