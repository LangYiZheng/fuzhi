package com.guyu.android.runtime;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;
import android.content.Context;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;

import com.guyu.android.utils.EncryptUtils;
/**
 * 解密
 * @author 陈启高
 *
 */
public class GuyuRunTime {

	public static LicenseResult checkLicenseKey(Context paramContext,
			String licensekey) {
		if (licensekey == null) {
			return new LicenseResult(LicenseResult.INVALID, 0);
		}
		String decryptKey = EncryptUtils.decryptByDES(licensekey);// 1.解密licensekey
		
		if (decryptKey == null) {
			return new LicenseResult(LicenseResult.INVALID, 0);
		}
		String[] licenseInfo = decryptKey.split(";"); // 2
		if (licenseInfo.length >= 3) {
			String key_imei = licenseInfo[0];
			@SuppressLint("MissingPermission") String local_imei = ((TelephonyManager) paramContext
					.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
			if (local_imei == null || "".endsWith(local_imei.trim())) {
				local_imei = Secure.getString(
						paramContext.getContentResolver(), Secure.ANDROID_ID);
			}
			/*if (key_imei.equalsIgnoreCase(local_imei)) {
				String startDate = licenseInfo[1];
				String endDate = licenseInfo[2];
				String[] functioncodes = null; // 3
				if (licenseInfo.length > 3) {
					functioncodes = licenseInfo[3].split(","); // 4
				}
				java.util.Calendar c = java.util.Calendar.getInstance();
				java.text.SimpleDateFormat f = new java.text.SimpleDateFormat(
						"yyyy-MM-dd");
				String curDate = f.format(c.getTime());
				int bStart = compareDate(curDate, startDate);//用于比较两个 DateTime 。
				int bEnd = compareDate(curDate, endDate);
				if ((bStart == 1 || bStart == 0) && (bEnd == -1 || bEnd == 0)) {
					int restDays = daysOfTwo(curDate, endDate);
					LicenseResult lr = new LicenseResult(LicenseResult.VALID,
							restDays);// 5
					lr.setFunctioncodes(functioncodes); // 6
					return lr;
				} else {
					return new LicenseResult(LicenseResult.EXPIRED, 0);
				}
			} else {
				return new LicenseResult(LicenseResult.INVALID, 0);
			}*/
			
			String startDate = licenseInfo[1];
			String endDate = licenseInfo[2];
			String[] functioncodes = null; // 3
			if (licenseInfo.length > 3) {
				functioncodes = licenseInfo[3].split(","); // 4
			}
			java.util.Calendar c = java.util.Calendar.getInstance();
			java.text.SimpleDateFormat f = new java.text.SimpleDateFormat(
					"yyyy-MM-dd");
			String curDate = f.format(c.getTime());
			int bStart = compareDate(curDate, startDate);//用于比较两个 DateTime 。
			int bEnd = compareDate(curDate, endDate);
			if ((bStart == 1 || bStart == 0) && (bEnd == -1 || bEnd == 0)) {
				int restDays = daysOfTwo(curDate, endDate);
				LicenseResult lr = new LicenseResult(LicenseResult.VALID,
						restDays);// 5
				lr.setFunctioncodes(functioncodes); // 6
				return lr;
			} else {
				return new LicenseResult(LicenseResult.EXPIRED, 0);
			}
			
		} else {
			return new LicenseResult(LicenseResult.INVALID, 0);
		}

	}

	public static int compareDate(String DATE1, String DATE2) {

		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date dt1 = df.parse(DATE1);
			Date dt2 = df.parse(DATE2);
			if (dt1.getTime() > dt2.getTime()) {
				return 1;
			} else if (dt1.getTime() < dt2.getTime()) {
				return -1;
			} else {
				return 0;
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return 0;
	}

	public static int daysOfTwo(String DATE1, String DATE2) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date dt1 = df.parse(DATE1);
			Date dt2 = df.parse(DATE2);
			Calendar aCalendar = Calendar.getInstance();

			aCalendar.setTime(dt1);

			int day1 = aCalendar.get(Calendar.DAY_OF_YEAR);

			aCalendar.setTime(dt2);

			int day2 = aCalendar.get(Calendar.DAY_OF_YEAR);
			return day2 - day1;
		} catch (ParseException e) {
			e.printStackTrace();
			return -1;
		}
	}
}
