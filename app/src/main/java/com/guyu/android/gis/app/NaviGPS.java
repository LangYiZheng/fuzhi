package com.guyu.android.gis.app;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.annotation.SuppressLint;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.guyu.android.gis.opt.MapOperate;
import com.guyu.android.utils.UtilsTools;

public class NaviGPS {
	private static final int CHECK_INTERVAL = 30000;
	private static final String TAG = "NaviGPS";
	private Handler GPSHand = null;
	public Iterator<GpsSatellite> GpsSatellites = null;
	private Handler SkyViewLocHandler = null;
	public double dAltitude;
	public double dBearing;
	public double dLatitude;
	public double dLongitude;
	public double dSpeed;
	public double dx;
	public double dy;
	public MGpsListener gpsListener = null;
	public List<Satellite> listStatellites = null;
	private LocationManager locMgr = null;
	public LocationClient mLocationClient = null;
	public Location location;
	public BDLocation mBDLocation;
	private GPSNaviListener mGPSListener = null;
	private BDLocationListener mCustomBDLocationListener = null;
	public int nFixMode;
	private int nNoGPsCount = 0;
	public int nSatellites;
	public long nTime;
	private long oldTime = 0L;

	public NaviGPS() {
		// 添加255颗卫星
		listStatellites = new ArrayList<Satellite>();
		for (int i = 0; i < 255; i++) {
			this.listStatellites.add(new Satellite());
		}
	}

	private boolean isSameProvider(String paramString1, String paramString2) {
		if (paramString1 == null)
			return (paramString2 == null);
		return paramString1.equals(paramString2);
	}

	private void updateLocation(Location paramLocation) {
		if (paramLocation != null) {
			this.location = paramLocation;
			this.dAltitude = paramLocation.getAltitude();
			this.dLongitude = paramLocation.getLongitude();
			this.dLatitude = paramLocation.getLatitude();
			this.dBearing = paramLocation.getBearing();
			this.dSpeed = paramLocation.getSpeed();
			this.nTime = paramLocation.getTime();
			this.dx = this.dLongitude;
			this.dy = this.dLatitude;
			// Point2D localPoint2D = MapOperate.GpsToGrid(new
			// Point2D(this.dLongitude, this.dLatitude));
			// this.dLongitude = localPoint2D.x;
			// this.dLatitude = localPoint2D.y;
			Log.d("GPS--", "转换为本地坐标：");
			Log.d("GPS--", "WGS84--Lon == " + paramLocation.getLongitude());
			Log.d("GPS--", "WGS84--Lat == " + paramLocation.getLatitude());
			 Log.d("GPS--", "本地--X == " + this.dLongitude);
			 Log.d("GPS--", "本地--Y == " + this.dLatitude);
			if (this.nTime > 0L) {
				this.nFixMode = 1;
				this.nNoGPsCount = 0;
				if (this.GPSHand != null)
					this.GPSHand.sendEmptyMessage(12289);
				if (this.SkyViewLocHandler != null) {
					this.SkyViewLocHandler.sendEmptyMessage(12289);
					Log.e(TAG, "GPSHand为空");
				}

			} else {
				Log.e(TAG, "gps时间不大于0：nTime=" + this.nTime);
			}

		}

	}

	/**
	 * 关闭GPS设备
	 */
	public void closeGpsDevice() {
		if (this.locMgr != null) {
			Log.i(TAG, "CloseGpsDevice-关闭gps监听器");
			this.locMgr.removeGpsStatusListener(this.mGPSListener);
			this.locMgr.removeUpdates(this.mGPSListener);
			this.locMgr = null;
		}
		if (this.mLocationClient != null) {
			this.mLocationClient.stop();
		}
	}

	/**
	 * 开启GPS设备
	 *
	 * @param paramLocationManager
	 * @return
	 */
	@SuppressLint("MissingPermission")
	public boolean openGpsDevice(LocationManager paramLocationManager) {
		//启用百度基站+WIFI定位
		if (this.mLocationClient == null) {
			this.mLocationClient = new LocationClient(GisQueryApplication.getApp());
			//this.mCustomBDLocationListener = new CustomBDLocationListener();
			// 百度位置监听器
			this.mCustomBDLocationListener = new BDLocationListener() {

				@Override
				public void onReceiveLocation(BDLocation arg0) {
					if(NaviGPS.this.location==null){
						if(UtilsTools.IsGpsDataValid(arg0.getLongitude(), arg0.getLatitude())){
							mBDLocation = arg0;
							NaviGPS.this.dLatitude = arg0.getLatitude();
							NaviGPS.this.dLongitude = arg0.getLongitude();
							Log.d("BDGPS--", "WGS84--Lon == " + NaviGPS.this.dLongitude);
							Log.d("BDGPS--", "WGS84--Lat == " + NaviGPS.this.dLatitude);
							if (NaviGPS.this.GPSHand != null){
								NaviGPS.this.GPSHand.sendEmptyMessage(12289);
							}else if (NaviGPS.this.SkyViewLocHandler != null) {
								NaviGPS.this.SkyViewLocHandler.sendEmptyMessage(12289);
								Log.e(TAG, "GPSHand为空");
							}
						}
					}
				}
				public void onConnectHotSpotMessage(String s, int i){
		        }
			};
			this.mLocationClient
					.registerLocationListener(this.mCustomBDLocationListener);
			LocationClientOption localLocationClientOption = new LocationClientOption();
			localLocationClientOption.setOpenGps(paramLocationManager!=null);
			localLocationClientOption.setAddrType("all");
			localLocationClientOption.setCoorType("gcj02");
			localLocationClientOption.setScanSpan(2000);
			localLocationClientOption.SetIgnoreCacheException(true);
			this.mLocationClient.setLocOption(localLocationClientOption);
		}
		if(this.mLocationClient.isStarted()==false){
			this.mLocationClient.start();
		}
		//启用GPS定位
		if (paramLocationManager == null) {
			return false;
		} else {
			this.mGPSListener = new GPSNaviListener();
			this.locMgr = paramLocationManager;
			this.locMgr.addGpsStatusListener(this.mGPSListener);
			this.locMgr.requestLocationUpdates("gps"
					, 0, 0,
					this.mGPSListener);
			return true;
		}
	}

	protected boolean isBetterLocation(Location paramLocation1,
			Location paramLocation2) {
		return true;
	}

	public void setGpsListener(MGpsListener paramMGpsListener) {
		if (paramMGpsListener == null)
			return;
		this.gpsListener = paramMGpsListener;
	}

	public GPSNaviListener getGpsListener() {
		return this.mGPSListener;
	}

	void setMsgHand(Handler paramHandler) {
		this.GPSHand = paramHandler;
	}

	void setSkyViewLocHandler(Handler paramHandler) {
		this.SkyViewLocHandler = paramHandler;
	}



	// GPS位置监听器
	private class GPSNaviListener implements LocationListener,
			GpsStatus.Listener {
		private void UpdateSatellite() {

			if (NaviGPS.this.locMgr != null) {

				// 获取当前状态
				GpsStatus gpsStatus = NaviGPS.this.locMgr.getGpsStatus(null);
				// 获取卫星颗数的默认最大值
				int maxSatellites = gpsStatus.getMaxSatellites();
				maxSatellites = 255;
				// 创建一个迭代器保存所有卫星
				Iterator<GpsSatellite> iters = gpsStatus.getSatellites()
						.iterator();
				NaviGPS.this.GpsSatellites = iters;
				int count = 0;
				while (iters.hasNext() && count < maxSatellites) {
					GpsSatellite s = iters.next();
					List<Satellite> localList = NaviGPS.this.listStatellites;

					NaviGPS.Satellite localSatellite = localList
							.get(count);
					localSatellite.bFix = s.usedInFix();
					localSatellite.nSatelliteID = s.getPrn();
					localSatellite.nAzimuth = s.getAzimuth();
					localSatellite.nElevation = s.getElevation();
					localSatellite.nSignal = s.getSnr();
					count++;
				}
				NaviGPS.this.nSatellites = count;
				System.out.println("搜索到：" + count + "颗卫星");
			}

		}

		public void onGpsStatusChanged(int event) {
			switch (event) {
			// 第一次定位
			case GpsStatus.GPS_EVENT_FIRST_FIX:
				Log.i(TAG, "第一次定位");
				break;
			// 卫星状态改变
			case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
				Log.i(TAG, "卫星状态改变");
				UpdateSatellite();
				if (NaviGPS.this.nTime <= NaviGPS.this.oldTime) {
					NaviGPS localNaviGPS = NaviGPS.this;
					int i = localNaviGPS.nNoGPsCount;
					localNaviGPS.nNoGPsCount = (i + 1);
					if (i > 15 && mBDLocation == null) {
						NaviGPS.this.nFixMode = 0;
						NaviGPS.this.dAltitude = 0.0D;
						NaviGPS.this.dLongitude = 0.0D;
						NaviGPS.this.dLatitude = 0.0D;
						NaviGPS.this.dBearing = 0.0D;
						NaviGPS.this.dSpeed = 0.0D;
						NaviGPS.this.nTime = 0L;
						return;
					}
				}
				NaviGPS.this.oldTime = NaviGPS.this.nTime;
				break;
			// 定位启动
			case GpsStatus.GPS_EVENT_STARTED:
				Log.i(TAG, "定位启动");
				break;
			// 定位结束
			case GpsStatus.GPS_EVENT_STOPPED:
				Log.i(TAG, "定位结束");
				break;
			}
		}

		public void onLocationChanged(Location paramLocation) {
			NaviGPS.this.updateLocation(paramLocation);
			if (NaviGPS.this.gpsListener != null) {
				NaviGPS.this.gpsListener.location(paramLocation);
			}
		}

		public void onProviderDisabled(String paramString) {
		}

		public void onProviderEnabled(String paramString) {
		}

		public void onStatusChanged(String paramString, int paramInt,
				Bundle paramBundle) {
		}
	}

	public interface MGpsListener {
		void location(Location paramLocation);
	}

	public static class Satellite {
		public boolean bFix;
		public float nAzimuth;
		public float nElevation;
		public int nSatelliteID;
		public float nSignal;
	}
}