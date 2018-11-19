package com.guyu.android.gis.common;

public class Task extends Case {

	/**
	 * 所有下达任务父类
	 */
	private static final long serialVersionUID = 3309819582856345069L;

	private String bizId;// 业务标识
	private String xdsj;// 下达时间
	private String taskType;// 任务类型
	private int hczt;// 核查状态

	public String getBizId() {
		return bizId;
	}

	public void setBizId(String bizId) {
		this.bizId = bizId;
	}

	public String getXdsj() {
		return xdsj;
	}

	public void setXdsj(String xdsj) {
		this.xdsj = xdsj;
	}

	public String getTaskType() {
		return taskType;
	}

	public void setTaskType(String taskType) {
		this.taskType = taskType;
	}

	public int getHczt() {
		return hczt;
	}

	public void setHczt(int hczt) {
		this.hczt = hczt;
	}
}