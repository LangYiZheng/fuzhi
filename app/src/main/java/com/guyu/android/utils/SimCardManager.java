package com.guyu.android.utils;

import android.content.Context;
import android.telephony.TelephonyManager;

public class SimCardManager {
	public boolean isSIMCardOk(Context paramContext) {
		return (((TelephonyManager) paramContext.getSystemService(Context.TELEPHONY_SERVICE))
				.getSimState() == 5);
	}

	public String readErrorInfo(Context paramContext) {
		TelephonyManager localTelephonyManager = (TelephonyManager) paramContext
				.getSystemService(Context.TELEPHONY_SERVICE);
		StringBuffer localStringBuffer = new StringBuffer();
		if (localTelephonyManager.getSimSerialNumber() == null) {
			localStringBuffer.append("@无法取得SIM卡号");
		} else {
			localStringBuffer.append("@"
					+ localTelephonyManager.getSimOperator().toString());
		}

		if (localTelephonyManager.getSimOperator().equals("")) {
			localStringBuffer.append("@无法取得供货商代码");
		} else {
			localStringBuffer.append("@"
					+ localTelephonyManager.getSimOperatorName().toString());
		}
		if (localTelephonyManager.getSimOperatorName().equals("")) {
			localStringBuffer.append("@无法取得供货商");
		} else {
			localStringBuffer.append("@"
					+ localTelephonyManager.getSimOperatorName());
		}

		if (localTelephonyManager.getSimCountryIso().equals("")) {
			localStringBuffer.append("@无法取得国籍");
		} else {
			localStringBuffer.append("@"
					+ localTelephonyManager.getSimCountryIso().toString());
		}
		if (localTelephonyManager.getNetworkOperator().equals("")) {
			localStringBuffer.append("@无法取得网络运营商");
		} else {
			localStringBuffer.append("@"
					+ localTelephonyManager.getNetworkOperator());
		}
		if (localTelephonyManager.getNetworkOperatorName().equals("")) {
			localStringBuffer.append("@无法取得网络运营商名称");
		} else {
			localStringBuffer.append("@"
					+ localTelephonyManager.getNetworkOperatorName());
		}
		if (localTelephonyManager.getNetworkType() == 0) {
			localStringBuffer.append("@无法取得网络类型");
		} else {
			localStringBuffer.append("@"
					+ localTelephonyManager.getNetworkType());
		}
		return localStringBuffer.toString();
	}

	public String readSIMCard(Context paramContext) {
		TelephonyManager localTelephonyManager = (TelephonyManager) paramContext
				.getSystemService(Context.TELEPHONY_SERVICE);
		StringBuffer localStringBuffer = new StringBuffer();
		switch (localTelephonyManager.getSimState()) {
		default:
		case 0:
			localStringBuffer.append("未知状态");
			break;
		case 1:
			localStringBuffer.append("未发现SIM卡，请检查是否已插入SIM卡");
			break;
		case 2:
			localStringBuffer.append("需要PIN解锁");
			break;
		case 3:
			localStringBuffer.append("需要PUK解锁");
			break;
		case 4:
			localStringBuffer.append("需要NetworkPIN解锁");
			break;
		case 5:
			localStringBuffer.append("良好");
			break;

		}
		return localStringBuffer.toString();
	}
}