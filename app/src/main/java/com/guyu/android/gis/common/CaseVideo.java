package com.guyu.android.gis.common;

import java.io.Serializable;

public class CaseVideo implements Serializable {

	/**
	 * 案件视频
	 */
	private static final long serialVersionUID = -8025609023749902281L;
	private String videoName;
	private String videoPath;
	private String thumbnailPath;
	private String caseId;
	private String partId;
	private String prjName;
	private long time;
	private String docDefId;

	public CaseVideo() {
	}

	public CaseVideo(String p_caseId, String p_docDefId, String p_videoName,
			String p_videoPath, String p_thumbnailPath, long paramLong) {
		this.caseId = p_caseId;
		this.docDefId = p_docDefId;
		this.videoName = p_videoName;
		this.videoPath = p_videoPath;
		this.thumbnailPath = p_thumbnailPath;
		this.time = paramLong;
	}

	public String getVideoName() {
		return videoName;
	}

	public void setVideoName(String videoName) {
		this.videoName = videoName;
	}

	public String getVideoPath() {
		return videoPath;
	}

	public void setVideoPath(String videoPath) {
		this.videoPath = videoPath;
	}

	public String getThumbnailPath() {
		return thumbnailPath;
	}

	public void setThumbnailPath(String thumbnailPath) {
		this.thumbnailPath = thumbnailPath;
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

	public void setPrjName(String paramString) {
		this.prjName = paramString;
	}

	public void setTime(long paramLong) {
		this.time = paramLong;
	}

}