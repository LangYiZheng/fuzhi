package com.guyu.android.utils;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Vibrator;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.format.Time;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class UtilsTools {
	private static final String ENCODING = "UTF-8";
	private static final String MAC_NAME = "HmacSHA1";
	public static String DEF_DATE_FORMAT = "yyyy-MM-dd";
	private static final char[] encodeTable = { 65, 66, 67, 68, 69, 70, 71, 72,
			73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89,
			90, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57 };

	public static void CallMobile(Context paramContext, String paramString) {
		try {
			paramContext.startActivity(new Intent("android.intent.action.CALL",
					Uri.parse("tel:" + paramString)));
			return;
		} catch (Exception localException) {
			Log.e("changchun", "启动打电话失败");
		}
	}

	/**
	 * 判断网络是否连接
	 * @param context
	 * @return
	 */
	public static boolean isNetworkConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
			if (mNetworkInfo != null) {
				return mNetworkInfo.isAvailable();
			}
		}
		return false;
	}
	public static boolean IsGpsDataValid(double dLongitude, double dLatitude) {
        return !((dLongitude < 1.0E-008D) && (dLongitude > -1.0E-008D)
                && (dLatitude < 1.0E-008D) && (dLatitude > -1.0E-008D));
	}

	public static String FormateDateToString(long paramLong) {
		return new SimpleDateFormat("yyyy/MM/dd").format(Long
				.valueOf(paramLong));
	}

	public static long FormateStringDateToLong(String paramString) {
		long l = 0L;
		SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat(
				"yyyy/MM/dd");
		Date localDate1;
		try {
			Date localDate2 = localSimpleDateFormat.parse(paramString);
			localDate1 = localDate2;
			if (localDate1 != null)
				l = localDate1.getTime();
			return l;
		} catch (ParseException localParseException) {
			localParseException.printStackTrace();
			return l;
		}
	}

	public static long FormateStringTimeToLong(String paramString) {
		long l = 0L;
		SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat(
				"HH:mm:ss");
		Date localDate1;
		try {
			Date localDate2 = localSimpleDateFormat.parse(paramString);
			localDate1 = localDate2;
			if (localDate1 != null)
				l = localDate1.getTime();
			return l;
		} catch (ParseException localParseException) {
			localParseException.printStackTrace();
			return l;
		}
	}

	public static Date FormateStringToDate(String paramString, String format) {
		// "yyyy/MM/dd HH:mm:ss"
		// "yyyy-MM-dd HH:mm:ss"
		SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat(format);
		try {
			Date localDate = localSimpleDateFormat.parse(paramString);
			return localDate;
		} catch (ParseException localParseException) {
			localParseException.printStackTrace();
		}
		return null;
	}

	public static String FormateTimeStempToString(long paramLong) {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Long
				.valueOf(paramLong));
	}

	public static String FormateTimeToString(long paramLong) {
		return new SimpleDateFormat("HH:mm:ss").format(Long.valueOf(paramLong));
	}

	public static String GetAppVersion(Context paramContext) {
		String str1 = paramContext.getPackageName();
		try {
			String str2 = paramContext.getPackageManager().getPackageInfo(str1,
					0).versionName;
			return str2;
		} catch (PackageManager.NameNotFoundException localNameNotFoundException) {
			localNameNotFoundException.printStackTrace();
		}
		return "";
	}

	public static String GetCurrentFormatTime(Long paramLong, String paramString) {
		return new SimpleDateFormat(paramString).format(paramLong);
	}

	public static String GetCurrentFormatTime() {
		Time localTime = new Time();
		localTime.setToNow();
		return localTime.format("%y%m%d%H%M%S");
	}

	public static String GetCurrentFormatTime1() {
		Time localTime = new Time();
		localTime.setToNow();
		return localTime.format("%y/%m/%d %H:%M:%S");
	}

	public static String GetCurrentFormatTime1(long paramLong) {
		Time localTime = new Time();
		localTime.set(localTime);
		return localTime.format("%y/%m/%d %H:%M:%S");
	}

	public static String GetCurrentFormatTime2() {
		Time localTime = new Time();
		localTime.setToNow();
		return localTime.format("%y/%m/%d_%H:%M");
	}

	public static String GetCurrentFormatTime3() {
		Time localTime = new Time();
		localTime.setToNow();
		return localTime.format("%y_%m_%d_%H.%M");
	}

	public static String GetCurrentFormatTime4() {
		Time localTime = new Time();
		localTime.setToNow();
		return localTime.format("%Y年%M月%d号_%H点%M分");
	}

	public static long GetCurrentTime() {
		Time localTime = new Time();
		localTime.setToNow();
		return localTime.toMillis(true);
	}

	public static String GetDate() {
		Time localTime = new Time();
		localTime.setToNow();
		return FormateDateToString(localTime.toMillis(false));
	}

	public static String GetFormatDateTime() {
		Time localTime = new Time();
		localTime.setToNow();
		return FormateTimeStempToString(localTime.toMillis(false));
	}

	@SuppressLint("MissingPermission")
	public static String GetIMEI(Context paramContext) {
		return ((TelephonyManager) paramContext.getSystemService(Context.TELEPHONY_SERVICE))
				.getDeviceId();
	}

	public static void GetIconSize(Context paramContext, int paramInt,
			int[] paramArrayOfInt) {
		Bitmap localBitmap = ((BitmapDrawable) paramContext.getResources()
				.getDrawable(paramInt)).getBitmap();
		paramArrayOfInt[0] = localBitmap.getHeight();
		paramArrayOfInt[1] = localBitmap.getWidth();
	}

	public static String GetTime() {
		Time localTime = new Time();
		localTime.setToNow();
		return FormateTimeToString(localTime.toMillis(false));
	}

	public static byte[] HmacSHA1Encrypt(String paramString1,
			String paramString2) throws Exception {
		SecretKeySpec localSecretKeySpec = new SecretKeySpec(
				paramString2.getBytes("UTF-8"), "HmacSHA1");
		Mac localMac = Mac.getInstance("HmacSHA1");
		localMac.init(localSecretKeySpec);
		return localMac.doFinal(paramString1.getBytes("UTF-8"));
	}



	public static int PlaySound(Context paramContext) {
		NotificationManager localNotificationManager = (NotificationManager) paramContext
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification localNotification = new Notification();
		localNotification.defaults = 1;
		int i = new Random(System.currentTimeMillis()).nextInt(2147483647);
		localNotificationManager.notify(i, localNotification);
		return i;
	}

	public static void SendSMS(Context paramContext, String paramString) {
		Intent localIntent = new Intent("android.intent.action.SENDTO",
				Uri.parse("sms:"));
		localIntent.putExtra("sms_body", paramString);
		paramContext.startActivity(localIntent);
	}

	public static void ShareText() {
	}

	@SuppressLint("MissingPermission")
	public static void Vibrate(Context paramContext, long paramLong) {
		((Vibrator) paramContext.getSystemService(Context.VIBRATOR_SERVICE))
				.vibrate(paramLong);
	}

	@SuppressLint("MissingPermission")
	public static void Vibrate(Context paramContext, long[] paramArrayOfLong,
							   boolean paramBoolean) {
		Vibrator localVibrator = (Vibrator) paramContext
				.getSystemService(Context.VIBRATOR_SERVICE);
		if (paramBoolean)
			;
		for (int i = 1;; i = -1) {
			localVibrator.vibrate(paramArrayOfLong, i);
			return;
		}
	}

	public static int argb2bgr(int paramInt) {
		int i = 0xFF & paramInt >> 16;
		int j = paramInt & 0xFF00;
		return ((paramInt & 0xFF) << 16 | i | j);
	}

	public static int bgr2argb(int paramInt) {
		int i = (paramInt & 0xFF) << 16;
		int j = paramInt & 0xFF00;
		return ((0xFF0000 & paramInt) >> 16 | j | 0xFF000000 | i);
	}

	public static boolean canDeleteSDFile(long paramLong, int paramInt) {
		Calendar localCalendar = Calendar.getInstance();
		localCalendar.add(Calendar.DATE, paramInt * -1);
		Date localDate = localCalendar.getTime();
		localCalendar.setTimeInMillis(paramLong);
		return localCalendar.getTime().before(localDate);
	}

	public static int[] fetchDrawsResId(Context paramContext, int paramInt) {
		Resources localResources = paramContext.getResources();
		String str = paramContext.getPackageName();
		String[] arrayOfString = localResources.getStringArray(paramInt);
		int i = arrayOfString.length;
		if (i > 0) {
			int[] arrayOfInt = new int[i];
			for (int j = 0;; ++j) {
				if (j >= i)
					return arrayOfInt;
				arrayOfInt[j] = localResources.getIdentifier(arrayOfString[j],
						"drawable", str);
			}
		}
		return null;
	}

	public static String[] getArrContent(Context paramContext, int paramInt) {
		return paramContext.getResources().getStringArray(paramInt);
	}

	public static int[] getArrContent_int(Context paramContext, int paramInt) {
		return paramContext.getResources().getIntArray(paramInt);
	}

	public static int getCurScreenHeight(Context paramContext) {
		return getDisplayMetrics(paramContext).heightPixels;
	}

	public static int getCurScreenWidth(Context paramContext) {
		return getDisplayMetrics(paramContext).widthPixels;
	}

	private static DisplayMetrics getDisplayMetrics(Context paramContext) {
		WindowManager localWindowManager = (WindowManager) paramContext
				.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics localDisplayMetrics = new DisplayMetrics();
		localWindowManager.getDefaultDisplay().getMetrics(localDisplayMetrics);
		return localDisplayMetrics;
	}

	public static Drawable getDrawableById(Context paramContext, int paramInt) {
		return paramContext.getResources().getDrawable(paramInt);
	}

	public static String getEditTextValue(EditText paramEditText) {
		Editable localEditable = paramEditText.getEditableText();
		return String.valueOf(localEditable.subSequence(0,
				localEditable.length()));
	}

	public static String getFieldType(int paramInt) {
		switch (paramInt) {
		default:
			return "未知类型";
		case 0:
			return "未知类型";
		case 1:
			return "布尔型";
		case 2:
			return "无符号单字节型";
		case 3:
			return "短整型";
		case 4:
			return "整数型";
		case 16:
			return "64位长整型";
		case 6:
			return "单精度浮点型";
		case 7:
			return "浮点型";
		case 8:
			return "日期型";
		case 9:
		case 11:
			return "二进制型";
		case 10:
		case 127:
			return "文本型";
		case 22:
			return "时间型";
		case 23:
			return "时间戳型";
		case 128:
			return "几何数据类型";
		case 129:
		}
		return "照片";
	}

	public static String getFieldTypeAuttribute(int paramInt) {
		switch (paramInt) {
		default:
			return "未知类型";
		case 0:
			return "未知类型";
		case 1:
			return "布尔型";
		case 2:
			return "无符号单字节型";
		case 3:
		case 4:
		case 16:
			return "整数";
		case 6:
		case 7:
			return "浮点数";
		case 8:
			return "日期类型";
		case 9:
		case 11:
			return "二进制型";
		case 12:
			return "数值";
		case 10:
		case 127:
			return "文本类型";
		case 22:
			return "时间型";
		case 23:
			return "时间戳型";
		case 128:
			return "几何数据类型";
		case 129:
		}
		return "照片";
	}

	public static Bitmap getLoacalBitmap(String paramString) {
		try {
			Bitmap localBitmap = BitmapFactory
					.decodeStream(new FileInputStream(paramString));
			return localBitmap;
		} catch (FileNotFoundException localFileNotFoundException) {
			localFileNotFoundException.printStackTrace();
		}
		return null;
	}

	public static String getRandomString(int paramInt) {
		char[] arrayOfChar = new char[paramInt];
		Random localRandom = new Random();
		for (int i = 0;; ++i) {
			if (i >= paramInt)
				return new String(arrayOfChar);
			arrayOfChar[i] = (char) (65 + localRandom.nextInt(9));
			arrayOfChar[i] = encodeTable[localRandom.nextInt(36)];
		}
	}

	public static String[] getStringsByResId(Context paramContext, int paramInt) {
		return paramContext.getResources().getStringArray(paramInt);
	}

	public static String getValue(Context paramContext, int paramInt) {
		return paramContext.getResources().getString(paramInt);
	}

	public static void hideSoftKeyBorad(Context paramContext, View paramView) {
		((InputMethodManager) paramContext.getSystemService(Context.INPUT_METHOD_SERVICE))
				.hideSoftInputFromWindow(paramView.getApplicationWindowToken(),
						0);
	}

	public static boolean isNumeric(String paramString) {
		return (Pattern.compile("[0-9]*").matcher(paramString).matches());
	}

	private static double max(double paramDouble1, double paramDouble2) {
		if (paramDouble1 > paramDouble2)
			return paramDouble1;
		return paramDouble2;
	}

	private static double min(double paramDouble1, double paramDouble2) {
		if (paramDouble1 < paramDouble2)
			return paramDouble1;
		return paramDouble2;
	}

	public static int rgba2argb(int paramInt) {
		return (0xFF000000 & paramInt << 24 | 0xFF & paramInt >> 8);
	}

	public static Bitmap rotateIcon(Resources paramResources, int paramInt,
			float paramFloat) {
		Bitmap localBitmap = BitmapFactory.decodeResource(paramResources,
				paramInt);
		Matrix localMatrix = new Matrix();
		localMatrix.postRotate(paramFloat);
		return Bitmap.createBitmap(localBitmap, 0, 0, localBitmap.getWidth(),
				localBitmap.getHeight(), localMatrix, false);
	}
	/**
	 * 输入流转字符串
	 * @param is
	 * @return
	 * @throws IOException
	 */
	public static String inputStream2String(InputStream is) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int i = -1;
		while ((i = is.read()) != -1) {
			baos.write(i);
		}
		return baos.toString();
	}
}