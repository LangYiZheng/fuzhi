package com.guyu.android.runtime;

public class LicenseResult {
	public static int INVALID = -1;
	public static int EXPIRED = 0;
	public static int VALID = 1;
	public int restDay = 0;
	public int result = -1;
	private String[] functioncodes;// 授权功能代码 
	LicenseResult(int p_result,int p_restDay){
		result = p_result;
		restDay = p_restDay;
	}
	public String[] getFunctioncodes() {
		return functioncodes;
	}
	public void setFunctioncodes(String[] functioncodes) {
		this.functioncodes = functioncodes;
	}
}
