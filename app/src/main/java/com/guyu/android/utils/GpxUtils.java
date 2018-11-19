package com.guyu.android.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;

import android.os.AsyncTask;
import android.text.format.Time;
import android.util.Xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.xmlpull.v1.XmlPullParser;
import com.esri.core.geometry.Point;
import com.guyu.android.gis.app.GisQueryApplication;
import com.guyu.android.gis.common.UploadGpsObj;
import com.guyu.android.gis.opt.MapOperate;

public class GpxUtils {
	public static String FormatGpxTime(long paramLong) {
		Time localTime = new Time();
		localTime.set(paramLong);
		return localTime.format("%y-%m-%dT%H:%M:%SZ");
	}

	public static List<Point> parseTrack(String paramString) {

		return null;
	}

	public static class GetTrackPointTask extends
			AsyncTask<String, Integer, String> {
		private ProgressDialog mProgressDialog;
		public List<Point> mTrack = null;

		public GetTrackPointTask(Context paramContext) {
			this.mProgressDialog = new ProgressDialog(paramContext);
			this.mProgressDialog.setTitle("读取轨迹点...");
			this.mProgressDialog.setButton("中断",
					new DialogInterface.OnClickListener() {
						public void onClick(
								DialogInterface paramDialogInterface,
								int paramInt) {
							paramDialogInterface.cancel();
						}
					});
			this.mProgressDialog
					.setOnCancelListener(new DialogInterface.OnCancelListener() {
						public void onCancel(
								DialogInterface paramDialogInterface) {
						}
					});
			this.mProgressDialog.show();
		}

		protected String doInBackground(String[] paramArrayOfString) {
			File localFile = new File(paramArrayOfString[0]);
			XmlPullParser localXmlPullParser;
			int i;
			Point localPoint2D1;
			try {
				FileInputStream localFileInputStream = new FileInputStream(
						localFile);
				localXmlPullParser = Xml.newPullParser();
				localXmlPullParser.setInput(localFileInputStream, "UTF-8");
				i = localXmlPullParser.getEventType();
				localPoint2D1 = null;

				label50: i = localXmlPullParser.next();

				this.mTrack = new ArrayList();
			} catch (Exception localException) {
				localException.printStackTrace();
				return null;
			}
			String str = localXmlPullParser.getName();
			if (str.equalsIgnoreCase("trkpt")) {
				localPoint2D1 = new Point();
				localPoint2D1.setX(Double.valueOf(
						localXmlPullParser.getAttributeValue(null, "lon"))
						.doubleValue());
				localPoint2D1.setY(Double.valueOf(
						localXmlPullParser.getAttributeValue(null, "lat"))
						.doubleValue());
			}
			while (true) {
				if ((localXmlPullParser.getName().equalsIgnoreCase("trkpt"))
						&& (localPoint2D1 != null))
					;
				Point localPoint2D2 = localPoint2D1;
				if (localPoint2D2 != null)
					this.mTrack.add(localPoint2D2);
				Integer[] arrayOfInteger = new Integer[1];
				arrayOfInteger[0] = Integer.valueOf(this.mTrack.size());
				publishProgress(arrayOfInteger);
				localPoint2D1 = null;

				if ((localPoint2D1 == null) || (str.equalsIgnoreCase("ele")))
					continue;
				str.equalsIgnoreCase("time");
			}

		}

		protected void onPostExecute(String paramString) {
			this.mProgressDialog.dismiss();
		}

		protected void onPreExecute() {
			super.onPreExecute();
		}

		protected void onProgressUpdate(Integer[] paramArrayOfInteger) {
			this.mProgressDialog.setProgress(paramArrayOfInteger[0].intValue());
			this.mProgressDialog.setMessage("已读取" + paramArrayOfInteger[0]
					+ "个轨迹点");
		}
	}

	public static class GpxFileWriter extends FileWriter {
		protected SimpleDateFormat formatter;
		private boolean startT;

		public GpxFileWriter(String paramString) throws IOException {
			this(paramString, true);
		}

		public GpxFileWriter(String paramString, boolean paramBoolean)
				throws IOException {
			super(paramString);
			this.startT = false;
			//old---yyyy-MM-dd'T'hh:mm:ss'Z'
			//this.formatter = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
			this.formatter = new SimpleDateFormat("yyyyMMddhhmmss");
			write("<?xml version=\"1.0\" encoding=\"UTF-8\"?><gpx version=\"1.1\" creator=\"Guyu\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"http://www.topografix.com/GPX/1/1\" xsi:schemaLocation=\"http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd\">");
			this.startT = paramBoolean;
			if (!(this.startT))
				return;
			startTrk("default");
		}

		public void close() throws IOException {
			if (this.startT)
				stopTrk();
			write("</gpx>");
			super.close();
		}

		public void startTrk(String paramString) throws IOException {
			//write("<trk><trackid>" + MapOperate.GetTrackObj().getTrackId() + "</trackid><name>" + paramString + "</name><humanid>"
			//write("<trk><trackid>" + MapOperate.GetTrackObj().getTrackId() + "</trackid><name>" + MapOperate.GetTrackObj().getTrackXCDD()+"_" +paramString+ "</name><humanid>"
			write("<trk><trackid>" + MapOperate.GetTrackObj().getTrackId() + "</trackid><name>" + paramString+ "</name><humanid>"
			+ MapOperate.GetTrackObj().getHumanID() + "</humanid> <trkseg>");
		}

		public void stopTrk() throws IOException {
			write("</trkseg></trk>");
		}

		public void write(double paramDouble1, double paramDouble2,
				double paramDouble3, long paramLong) throws IOException {
			Date localDate = new Date(paramLong);
			write(paramDouble1, paramDouble2, paramDouble3,
					this.formatter.format(localDate));
		}

		public void write(double paramDouble1, double paramDouble2,
				double paramDouble3, String gpsTime) throws IOException {
			if (MapOperate.IsPrjIsWGS84()) {
				double d1 = MathUtils.GetAccurateNumber(paramDouble1, 6);
				double d2 = MathUtils.GetAccurateNumber(paramDouble2, 6);
				write("<trkpt lat=\"" + d1 + "\" lon=\"" + d2 + "\"><pointid>"+UUID.randomUUID()+"</pointid><ele>"
						+ paramDouble3 + "</ele><time>" + gpsTime
						+ "</time></trkpt>\n");
				
				Point mapPoint = new Point(d2, d1);
				UploadGpsObj curUploadGpsObj = new UploadGpsObj();
				curUploadGpsObj.setGpsPoint(mapPoint);
				curUploadGpsObj.setGpsTime(gpsTime);
				MapOperate.curUploadGpsObj = curUploadGpsObj;
				sendGpsInfoToServer(mapPoint);
			} else {
				Point mapPointJWD = new Point(paramDouble2, paramDouble1);
				Point mapPoint = Utility.fromWgs84ToMap2(mapPointJWD,
						MapOperate.map.getSpatialReference());
				double dLon = MathUtils.GetAccurateNumber(mapPoint.getX(),3);
				double dLat = MathUtils.GetAccurateNumber(mapPoint.getY(),3);
				write("<trkpt lat=\"" + dLat + "\" lon=\"" + dLon + "\"><pointid>"+UUID.randomUUID()+"</pointid><ele>"
						+ paramDouble3 + "</ele><time>" + gpsTime
						+ "</time></trkpt>\n");
				UploadGpsObj curUploadGpsObj = new UploadGpsObj();
				curUploadGpsObj.setGpsPoint(mapPoint);
				curUploadGpsObj.setGpsTime(gpsTime);
				MapOperate.curUploadGpsObj = curUploadGpsObj;
				sendGpsInfoToServer(mapPoint);
			}

		}
	}

	/**
	 * 向服务器发送Gps信息
	 * 
	 * @param mapPoint
	 */
	public static void sendGpsInfoToServer(Point mapPoint) {
		UploadThread uploadThread = new UploadThread();
		new Thread(uploadThread).start();
	}

	public static class parseTrackTask extends
			AsyncTask<String, Integer, String> {
		private ProgressDialog mProgressDialog;
		public List<Point> mTrack = null;

		public parseTrackTask(Context paramContext) {
			this.mProgressDialog = new ProgressDialog(paramContext);
			this.mProgressDialog.setTitle("导入...");
			this.mProgressDialog.setButton("中断",
					new DialogInterface.OnClickListener() {
						public void onClick(
								DialogInterface paramDialogInterface,
								int paramInt) {
							paramDialogInterface.cancel();
						}
					});
			this.mProgressDialog
					.setOnCancelListener(new DialogInterface.OnCancelListener() {
						public void onCancel(
								DialogInterface paramDialogInterface) {
						}
					});
			this.mProgressDialog.show();
		}

		protected String doInBackground(String[] paramArrayOfString) {
			File localFile = new File(paramArrayOfString[0]);
			XmlPullParser localXmlPullParser;
			int i;
			Point localPoint2D1;
			try {
				FileInputStream localFileInputStream = new FileInputStream(
						localFile);
				localXmlPullParser = Xml.newPullParser();
				localXmlPullParser.setInput(localFileInputStream, "UTF-8");
				i = localXmlPullParser.getEventType();
				localPoint2D1 = null;

				label50: i = localXmlPullParser.next();

				this.mTrack = new ArrayList();
			} catch (Exception localException) {
				localException.printStackTrace();
				return null;
			}
			String str = localXmlPullParser.getName();
			if (str.equalsIgnoreCase("trkpt")) {
				localPoint2D1 = new Point();
				localPoint2D1.setX(Double.valueOf(
						localXmlPullParser.getAttributeValue(null, "lon"))
						.doubleValue());
				localPoint2D1.setY(Double.valueOf(
						localXmlPullParser.getAttributeValue(null, "lat"))
						.doubleValue());
			}
			while (true) {
				if ((localXmlPullParser.getName().equalsIgnoreCase("trkpt"))
						&& (localPoint2D1 != null))
					;
				Point localPoint2D2 = localPoint2D1;
				if (localPoint2D2 != null)
					this.mTrack.add(localPoint2D2);
				Integer[] arrayOfInteger = new Integer[1];
				arrayOfInteger[0] = Integer.valueOf(this.mTrack.size());
				publishProgress(arrayOfInteger);
				localPoint2D1 = null;

				if ((localPoint2D1 == null) || (str.equalsIgnoreCase("ele")))
					continue;
				str.equalsIgnoreCase("time");
			}

		}

		protected void onPostExecute(String paramString) {
			// MapOperate.DrawTrackToLayer(this.mTrack);
			// this.mProgressDialog.dismiss();
		}

		protected void onPreExecute() {
			super.onPreExecute();
		}

		protected void onProgressUpdate(Integer[] paramArrayOfInteger) {
			this.mProgressDialog.setProgress(paramArrayOfInteger[0].intValue());
			this.mProgressDialog.setMessage("已导入" + paramArrayOfInteger[0]
					+ "航迹点");
		}
	}

	public static class parseTrackThread extends Thread {
		private Context mContext;
		String mFileName;
		private ProgressDialog mProgressDialog;
		List<Point> mTrack;

		public parseTrackThread(Context paramContext, String paramString) {
			this.mContext = paramContext;
			this.mFileName = paramString;
		}

		public List<Point> getTrack() {
			return this.mTrack;
		}

		public void run() {

		}
	}

	static class UploadThread implements Runnable {
		public void run() {
			UploadGpsObj curUploadGpsObj = MapOperate.curUploadGpsObj;
			String gpsdata = "[{\"trackId\":\""
					+ MapOperate.GetTrackObj().getTrackId()
					+ "\",\"pointId\":\""
					+ UUID.randomUUID()
					+ "\",\"humanId\":"
					+ MapOperate.GetTrackObj().getHumanID()
					+ ",\"gpsTime\":\""
					+ curUploadGpsObj.getGpsTime()
					+ "\",\"x\":"
					+ MathUtils.round(MapOperate.curUploadGpsObj
							.getGpsPoint().getX(), 3)
					+ ",\"y\":"
					+ MathUtils.round(MapOperate.curUploadGpsObj
							.getGpsPoint().getY(), 3) + ",\"uploadId\":"
					+ MapOperate.curLandUploadId + "}]";
			MapOperate.curLandUploadId = "-1";
			String uploadUrl = GisQueryApplication.getApp().getProjectconfig()
					.getTrackUploadOnlineUrl();
			System.out.println("uploadUrl----" + uploadUrl);
			Map<String, String> params = new HashMap<String, String>();		
			params.put("trackInfo", gpsdata);
			HttpUtils.submitPostData(uploadUrl, params, "utf-8");
		}
	}
}