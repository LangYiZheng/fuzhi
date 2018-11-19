package com.guyu.android.utils;

import android.text.format.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class MDateUtils {
	public static String DEF_DATE_FORMAT = "yyyy-MM-dd";

	public static long FormateStringTimeToLong(String paramString1,
			String paramString2) {
		long l = 0L;
		SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat(
				paramString2);
		java.util.Date localDate1;
		try {
			java.util.Date localDate2 = localSimpleDateFormat
					.parse(paramString1);
			localDate1 = localDate2;
			if (localDate1 != null)
				l = localDate1.getTime();
			return l;
		} catch (ParseException localParseException) {
			localParseException.printStackTrace();
			localDate1 = null;
		}
		return l;
	}

	public static long GetBeforeAfterDate(long paramLong, int paramInt) {
		GregorianCalendar localGregorianCalendar = new GregorianCalendar();
		localGregorianCalendar.setTimeInMillis(paramLong);
		int i = localGregorianCalendar.get(Calendar.YEAR);
		int j = localGregorianCalendar.get(Calendar.MONTH);
		int k = paramInt + localGregorianCalendar.get(Calendar.DATE);
		localGregorianCalendar.set(Calendar.YEAR, i);
		localGregorianCalendar.set(Calendar.MONTH, j);
		localGregorianCalendar.set(Calendar.DATE, k);
		return localGregorianCalendar.getTimeInMillis();
	}

	public static java.sql.Date GetBeforeAfterDate(String paramString,
			int paramInt) {
		SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat(
				"yyyy-MM-dd");
		try {
			localSimpleDateFormat.setLenient(false);
			java.sql.Date localDate = new java.sql.Date(localSimpleDateFormat
					.parse(paramString).getTime());
			GregorianCalendar localGregorianCalendar = new GregorianCalendar();
			localGregorianCalendar.setTime(localDate);
			int i = localGregorianCalendar.get(Calendar.YEAR);
			int j = localGregorianCalendar.get(Calendar.MONTH);
			int k = paramInt + localGregorianCalendar.get(Calendar.DATE);
			localGregorianCalendar.set(Calendar.YEAR, i);
			localGregorianCalendar.set(Calendar.MONTH, j);
			localGregorianCalendar.set(Calendar.DATE, k);
			return new java.sql.Date(localGregorianCalendar.getTimeInMillis());
		} catch (ParseException localParseException) {
			throw new RuntimeException("日期转换错误");
		}
	}

	public static String GetCurrentFormatTime(Long paramLong, String paramString) {
		return new SimpleDateFormat(paramString).format(paramLong);
	}

	public static String GetCurrentFormatTime(String paramString) {
		Time localTime = new Time();
		localTime.setToNow();
		return GetCurrentFormatTime(Long.valueOf(localTime.toMillis(false)),
				paramString);
	}
}

/*
 * Location: C:\Users\Administrator\Desktop\一张图查询\classes_dex2jar.jar Qualified
 * Name: com.mtkj.land.utils.MDateUtils JD-Core Version: 0.5.3
 */