package com.guyu.android.gis.common;

import java.io.Serializable;

public class CaseSound implements Serializable {
	/**
	 * 案件音频
	 */
	private static final long serialVersionUID = -3560276057637520225L;
	private String soundName;
	private String soundPath;
	private String caseId;
	private String partId;
	private String prjName;
	private long time;
	private String docDefId;

	public CaseSound() {
	}

	public CaseSound(String p_caseId, String p_docDefId, String p_soundName,
			String p_soundPath, long p_time) {
		this.caseId = p_caseId;
		this.docDefId = p_docDefId;
		this.soundName = p_soundName;
		this.soundPath = p_soundPath;
		this.time = p_time;
	}

	public String getSoundName() {
		return soundName;
	}

	public void setSoundName(String soundName) {
		this.soundName = soundName;
	}

	public String getSoundPath() {
		return soundPath;
	}

	public void setSoundPath(String soundPath) {
		this.soundPath = soundPath;
	}

	public String getPrjName() {
		return this.prjName;
	}

	public long getTime() {
		return this.time;
	}

	public void setPrjName(String paramString) {
		this.prjName = paramString;
	}

	public void setTime(long paramLong) {
		this.time = paramLong;
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

}