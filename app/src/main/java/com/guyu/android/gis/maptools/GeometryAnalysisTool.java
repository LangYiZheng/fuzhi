package com.guyu.android.gis.maptools;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.ActionMode;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.blankj.utilcode.util.ToastUtils;
import com.esri.android.map.CalloutPopupWindow;
import com.esri.android.map.FeatureLayer;
import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.Layer;
import com.esri.android.map.MapView;
import com.esri.android.map.ags.ArcGISFeatureLayer;
import com.esri.android.map.event.OnSingleTapListener;
import com.esri.core.geometry.AreaUnit;
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.LinearUnit;
import com.esri.core.geometry.MultiPath;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polygon;
import com.esri.core.geometry.Polyline;
import com.esri.core.geometry.Unit;
import com.esri.core.map.CallbackListener;
import com.esri.core.map.FeatureResult;
import com.esri.core.map.FeatureSet;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.FillSymbol;
import com.esri.core.symbol.LineSymbol;
import com.esri.core.symbol.MarkerSymbol;
import com.esri.core.symbol.SimpleFillSymbol;
import com.esri.core.symbol.SimpleLineSymbol;
import com.esri.core.symbol.SimpleMarkerSymbol;
import com.esri.core.symbol.SimpleMarkerSymbol.STYLE;
import com.esri.core.table.FeatureTable;
import com.esri.core.tasks.SpatialRelationship;
import com.esri.core.tasks.ags.query.Query;
import com.esri.core.tasks.query.QueryParameters;
import com.guyu.android.R;
import com.guyu.android.gis.activity.AnalysisSummaryActivity;
import com.guyu.android.gis.activity.CaseDetailsActivity;
import com.guyu.android.gis.activity.CollectSettingActivity;
import com.guyu.android.gis.activity.MainActivity;
import com.guyu.android.gis.activity.TaskDetailsActivity;
import com.guyu.android.gis.app.GisQueryApplication;
import com.guyu.android.gis.app.NaviGPS;
import com.guyu.android.gis.common.AnalysisIntersectionResultInfo;
import com.guyu.android.gis.common.AnalysisLayerResultInfo;
import com.guyu.android.gis.common.CallBack;
import com.guyu.android.gis.config.LayerConfig;
import com.guyu.android.gis.opt.MapOperate;
import com.guyu.android.utils.MapUtils;
import com.guyu.android.utils.Utility;
import com.guyu.android.utils.UtilsTools;
import com.guyu.android.view.AnalysisLayerSetingOpt;
import com.guyu.android.view.BufferSeekbarOpt;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class GeometryAnalysisTool implements OnSingleTapListener {

	private static final long serialVersionUID = 1L;

	public enum DrawType {
		LINEAR, AREA;

		static public DrawType getType(int i) {
			switch (i) {
			case 0:
				return LINEAR;
			case 1:
				return AREA;
			default:
				return LINEAR;
			}
		}
	}

	private static final int MENU_DELETE = 0;
	private static final int MENU_PREF = 1;
	private static final int MENU_UNDO = 2;
	private MapView mMap;
	private GraphicsLayer mLayer;
	private GraphicsLayer mAnalysisResultLayer;
	private OnSingleTapListener mOldOnSingleTapListener;
	private MarkerSymbol mMarkerSymbol;
	private LineSymbol mLineSymbol;
	private double mResult;
	private TextView mText;
	private DrawType mDrawMode = DrawType.LINEAR;
	private int mCurrentLinearUnit;
	private Unit[] mLinearUnits;
	private Unit[] mDefaultLinearUnits;
	private int mCurrentAreaUnit;
	private Unit[] mAreaUnits;
	private Unit[] mDefaultAreaUnits;
	private Context mContext;
	private ArrayList<Point> mPoints;
	private ArrayList<Point> mPoints_removed;
	private FillSymbol mFillSymbol;
	private FillSymbol mFillSymbol_analysisrs;
	private CalloutPopupWindow mCallout;
	private ActionMode mMode;
	private Polyline mLine;
	private Polygon mPolygon;
	private ImageButton imgbtnDrawArea;
	private ImageButton imgbtnUndo;
	private ImageButton imgbtnRedo;
	private ImageButton imgbtnOK;
	private ImageButton imgbtnAnalysisSetting;
	private ImageButton imgbtnAutoCollect;
	private ImageButton imgbtnInsertCollect;
	private ImageButton imgbtnBlueToothCollect;
	private BufferSeekbarOpt mBufferSeekbarOpt;
	private ProgressDialog dialog = null;
	private MainActivity mActivity;
	private GisQueryApplication mApp;
	private int callbacknum = 0;
	private String[] analysisLayerAry;
	private ArrayList<AnalysisLayerResultInfo> analysisResultInfo;
	private Boolean initOK = false;
	private Boolean m_bIsAutoState = false;// 是否自动采集
	private boolean m_bIsHandState = false;// 是否手动采集

	private Polygon mPolygon_landManager;// 从地块管理 地块详细传过来的分析地块
	private int polygonWhereFrom = 0;// 0 标绘打点 ;1 案件管理传递而来 ;2 任务管理传递而来
	public Handler mHandler = null;

	public GeometryAnalysisTool(MapView map, BufferSeekbarOpt bufferSeekbarOpt, MainActivity activity) {
		this.mMap = map;
		this.mBufferSeekbarOpt = bufferSeekbarOpt;
		this.mActivity = activity;
		this.mApp = ( GisQueryApplication.getApp());
		mContext = mMap.getContext();
		mMarkerSymbol = new SimpleMarkerSymbol(Color.RED, 10, STYLE.CIRCLE);
		mLineSymbol = new SimpleLineSymbol(Color.BLACK, 3);
		mDefaultLinearUnits = new Unit[] { Unit.create(LinearUnit.Code.METER), Unit.create(LinearUnit.Code.KILOMETER), Unit.create(LinearUnit.Code.FOOT),
				Unit.create(LinearUnit.Code.MILE_STATUTE) };
		mDefaultAreaUnits = new Unit[] { Unit.create(AreaUnit.Code.SQUARE_METER), Unit.create(AreaUnit.Code.SQUARE_KILOMETER), Unit.create(AreaUnit.Code.SQUARE_FOOT),
				Unit.create(AreaUnit.Code.SQUARE_MILE_STATUTE) };
		mFillSymbol = new SimpleFillSymbol(Color.argb(100, 225, 225, 0));
		mFillSymbol.setOutline(new SimpleLineSymbol(Color.TRANSPARENT, 0));
		mFillSymbol_analysisrs = new SimpleFillSymbol(Color.argb(100, 0, 225, 0));
		mFillSymbol_analysisrs.setOutline(new SimpleLineSymbol(Color.RED, 1));
		mPoints = new ArrayList<Point>();
		mDrawMode = DrawType.AREA;
		this.mHandler = new Handler(Looper.myLooper());
	}

	protected void onPreExecute() {
		this.dialog = new ProgressDialog(this.mActivity);
		this.dialog.setMessage("数据叠加分析中...");
		this.dialog.setCancelable(false);
		this.dialog.show();
	}

	private void doGeometryAnalysis() {
		onPreExecute();
		polygonWhereFrom = 0;
		callbacknum = 0;
		analysisResultInfo = new ArrayList<AnalysisLayerResultInfo>();
		final Polygon polygonForAnalysis = mBufferSeekbarOpt.getBufferResultGeometry();
		if (polygonForAnalysis != null) {
			String analysisLayerNames = mApp.GetStringConfigData("analysislayers", "");
			if ("".equals(analysisLayerNames)) {
				analysisSetOpt();
				this.dialog.dismiss();
			} else {
				analysisLayerAry = analysisLayerNames.split(";");
//				analysisLayerAry = "C;K;Q".split(";");
				for (int i = 0; i < analysisLayerAry.length; i++) {
					Log.d("参与分析图层", "---->" + analysisLayerAry[i]);
					Layer fealyr = MapUtils.getLayerByName(mMap, analysisLayerAry[i]);
					AnalysisLayerResultInfo analysisLayerResultInfo = new AnalysisLayerResultInfo();
					analysisLayerResultInfo.setFealyr(fealyr);
					analysisLayerResultInfo.setLayerName(fealyr.getName());

					LayerConfig lc = mApp.getProjectconfig().getLayerConfigByLayerName(fealyr.getName());
					analysisLayerResultInfo.setLayerConfig(lc);
					analysisLayerResultInfo.setPolygonForAnalysis(polygonForAnalysis);
					analysisLayerResultInfo.setAnalysisSR(mMap.getSpatialReference());
					analysisResultInfo.add(analysisLayerResultInfo);
					queryOneFeatureLayer(analysisLayerResultInfo);
				}
			}

		}
	}

	private void doGeometryAnalysis_landManager(int p_polygonWhereFrom) {
		onPreExecute();
		callbacknum = 0;
		polygonWhereFrom = p_polygonWhereFrom;
		analysisResultInfo = new ArrayList<AnalysisLayerResultInfo>();
		final Polygon polygonForAnalysis = mPolygon_landManager;

		if (polygonForAnalysis != null) {
			String analysisLayerNames = mApp.GetStringConfigData("analysislayers", "");
			if ("".equals(analysisLayerNames)) {
				analysisSetOpt();
				this.dialog.dismiss();
			} else {
				analysisLayerAry = analysisLayerNames.split(";");
				for (int i = 0; i < analysisLayerAry.length; i++) {
					Log.d("参与分析图层", "---->" + analysisLayerAry[i]);
					Layer fealyr = MapUtils.getLayerByName(mMap, analysisLayerAry[i]);
					AnalysisLayerResultInfo analysisLayerResultInfo = new AnalysisLayerResultInfo();
					analysisLayerResultInfo.setLayerName(fealyr.getName());
					LayerConfig lc = mApp.getProjectconfig().getLayerConfigByLayerName(fealyr.getName());
					analysisLayerResultInfo.setLayerConfig(lc);
					analysisLayerResultInfo.setFealyr(fealyr);
					analysisLayerResultInfo.setPolygonForAnalysis(polygonForAnalysis);
					analysisLayerResultInfo.setAnalysisSR(mMap.getSpatialReference());
					analysisResultInfo.add(analysisLayerResultInfo);
					queryOneFeatureLayer(analysisLayerResultInfo);
				}
			}

		}
	}

	/**
	 * 查询地块占压信息
	 * fealyr
	 * @param
	 * @param analysisLayerResultInfo
	 */
	private void queryOneFeatureLayer(final AnalysisLayerResultInfo analysisLayerResultInfo) {
		Layer tmlyr = analysisLayerResultInfo.getFealyr();
		if (tmlyr instanceof ArcGISFeatureLayer) {
			ArcGISFeatureLayer fealyr = (ArcGISFeatureLayer) tmlyr;
			CallbackListener<FeatureSet> callback = new CallbackListener<FeatureSet>() {

				@Override
				public void onCallback(FeatureSet fSet) {
					if (null != fSet) {
						analysisLayerResultInfo.setAnalysisFs(fSet);
						analysisLayerResultInfo.doResultAnalysis_Graphics(new CallBack() {
							@Override
							public void execute() {
								callbacknum++;
								if (callbacknum == analysisLayerAry.length) {
									GeometryAnalysisTool.this.mHandler.postDelayed(GeometryAnalysisTool.this.showRunnable, 500L);
								}

							}
						});
					}

				}

				@Override
				public void onError(Throwable e) {
					// TODO Auto-generated method stub
					callbacknum++;
					Log.i("当前分析图层【" + analysisLayerResultInfo.getLayerName() + "】", "查询失败，占用面积---->0");
					if (callbacknum == analysisLayerAry.length) {
						GeometryAnalysisTool.this.mHandler.postDelayed(GeometryAnalysisTool.this.showRunnable, 500L);
					}
				}
			};
			Query query = new Query();
			query.setReturnGeometry(true);
			query.setInSpatialReference(mMap.getSpatialReference());
			query.setSpatialRelationship(SpatialRelationship.INTERSECTS);
			query.setGeometry(analysisLayerResultInfo.getPolygonForAnalysis());
			fealyr.queryFeatures(query, callback);
		} else if (tmlyr instanceof FeatureLayer) {
			FeatureLayer fealyr = (FeatureLayer) tmlyr;
			FeatureTable featbl = fealyr.getFeatureTable();
			QueryParameters query = new QueryParameters();
			query.setReturnGeometry(true);
			query.setInSpatialReference(mMap.getSpatialReference());
			query.setSpatialRelationship(SpatialRelationship.INTERSECTS);
			query.setGeometry(analysisLayerResultInfo.getPolygonForAnalysis());

			CallbackListener<FeatureResult> callback = new CallbackListener<FeatureResult>() {

				@Override
				public void onCallback(FeatureResult featureResult) {
					if (null != featureResult) {
						analysisLayerResultInfo.setAnalysisFr(featureResult);
						analysisLayerResultInfo.doResultAnalysis_Features(new CallBack() {
							@Override
							public void execute() {
								callbacknum++;
								if (callbacknum == analysisLayerAry.length) {
									GeometryAnalysisTool.this.mHandler.postDelayed(GeometryAnalysisTool.this.showRunnable, 500L);
								}

							}
						});
					}

				}

				@Override
				public void onError(Throwable e) {
					// TODO Auto-generated method stub
					callbacknum++;
					Log.i("当前分析图层【" + analysisLayerResultInfo.getLayerName() + "】", "查询失败，占用面积---->0");
					if (callbacknum == analysisLayerAry.length) {
						GeometryAnalysisTool.this.mHandler.postDelayed(GeometryAnalysisTool.this.showRunnable, 500L);
					}
				}
			};
			featbl.queryFeatures(query, callback);
		}

	}

	private Runnable showRunnable = new Runnable() {
		public void run() {
			showAnalysisInfo();
		}
	};

	public void showAnalysisInfo() {
		// 利用Application 传递分析结果
		for (AnalysisLayerResultInfo analysisLayerResultInfo : analysisResultInfo) {
			Log.i("地块分析结果", "图层名：" + analysisLayerResultInfo.getLayerName() + "---面积：" + analysisLayerResultInfo.getArea());
		}
		mApp.setAnalysisResultInfo(analysisResultInfo);
		this.dialog.dismiss();
		Intent localIntent = new Intent(mContext, AnalysisSummaryActivity.class);
		if (polygonWhereFrom == 0) {
			localIntent.putExtra("action", MainActivity.class);
		} else if (polygonWhereFrom == 1) {
			localIntent.putExtra("action", CaseDetailsActivity.class);
		} else if (polygonWhereFrom == 2) {
			localIntent.putExtra("action", TaskDetailsActivity.class);
		}
		mActivity.startActivity(localIntent);
	}

	private void doDrawLine() {
		mPoints_removed = null;
		mDrawMode = DrawType.LINEAR;
		if (mOldOnSingleTapListener == null) {
			mOldOnSingleTapListener = mMap.getOnSingleTapListener();
		}
		mMap.setOnSingleTapListener(this);
		mPoints = new ArrayList<Point>();
	}

	private void doDrawArea() {
		mPoints_removed = null;
		mDrawMode = DrawType.AREA;
		if (mOldOnSingleTapListener == null) {
			mOldOnSingleTapListener = mMap.getOnSingleTapListener();
		}
		mMap.setOnSingleTapListener(this);
		if (mAnalysisResultLayer != null) {
			mAnalysisResultLayer.removeAll();
		}
	}

	private void undoDraw() {
		if (mMap.getOnSingleTapListener() == this) {
			mMap.setOnSingleTapListener(mOldOnSingleTapListener);
			mOldOnSingleTapListener = null;
		}
	}

	public Boolean isRunning() {
		return initOK;
	}

	public void init() {
		if (!initOK) {
			initOK = true;
			MapOperate.clearPublicGraphicsLayer();
			imgbtnDrawArea = (ImageButton) mActivity.findViewById(R.id.imgHandCollect);
			imgbtnUndo = (ImageButton) mActivity.findViewById(R.id.imgUndo);
			imgbtnRedo = (ImageButton) mActivity.findViewById(R.id.imgRedo);
			imgbtnOK = (ImageButton) mActivity.findViewById(R.id.imgOK);
			imgbtnAutoCollect = (ImageButton) mActivity.findViewById(R.id.imgAutoCollect);
			imgbtnInsertCollect = (ImageButton) mActivity.findViewById(R.id.imgInsertCollect);
			imgbtnBlueToothCollect = (ImageButton) mActivity.findViewById(R.id.imgBlueToothCollect);
			mActivity.findViewById(R.id.vg_seek_bar_control).setVisibility(View.VISIBLE);
			imgbtnAnalysisSetting = ((ImageButton) mActivity.findViewById(R.id.btn_analysis_setting));
			imgbtnAnalysisSetting.setOnClickListener(new View.OnClickListener() {
				public void onClick(View paramView) {
					analysisSetting();
				}
			});
			imgbtnAutoCollect.setOnClickListener(new View.OnClickListener() {
				public void onClick(View paramView) {
					collectAutoOpt();
				}
			});
			imgbtnInsertCollect.setOnClickListener(new View.OnClickListener() {
				public void onClick(View paramView) {
					collectInsertOpt();
				}
			});
			imgbtnBlueToothCollect.setOnClickListener(new View.OnClickListener() {
				public void onClick(View paramView) {
					if (imgbtnBlueToothCollect.isSelected()) {
						mActivity.openOrCloseBlueToothDevices(false);
						imgbtnBlueToothCollect.setSaveEnabled(false);
					} else {
						mActivity.openOrCloseBlueToothDevices(true);
					}

				}
			});
			imgbtnDrawArea.setOnClickListener(new View.OnClickListener() {
				public void onClick(View paramView) {
					ImageButton imgbtn = (ImageButton) paramView;
					if (imgbtn.isSelected()) {
						undoDraw();
						imgbtn.setSelected(false);
					} else {
						doDrawArea();
						imgbtn.setSelected(true);

					}
				}
			});
			imgbtnUndo.setOnClickListener(new View.OnClickListener() {
				public void onClick(View paramView) {
					undo();
				}
			});
			imgbtnRedo.setOnClickListener(new View.OnClickListener() {
				public void onClick(View paramView) {
					redo();
				}
			});
			imgbtnOK.setOnClickListener(new View.OnClickListener() {
				public void onClick(View paramView) {
					if (mPoints != null && mPoints.size() >= 3) {
						undoDraw();
						imgbtnDrawArea.setSelected(false);
						doGeometryAnalysis();
					} else {
						ToastUtils.showLong(  "请您绘制好地块！");
					}
				}
			});
			mBufferSeekbarOpt.init();
			mLayer = new GraphicsLayer();
			mAnalysisResultLayer = new GraphicsLayer();
			mMap.addLayer(mLayer);
			mMap.addLayer(mAnalysisResultLayer);
		}

	}

	public void analysisGeometry(Polygon mPolygon, int p_polygonWhereFrom) {
		if (!initOK) {
			initOK = true;
			mPolygon_landManager = mPolygon;
			mLayer = new GraphicsLayer();
			mAnalysisResultLayer = new GraphicsLayer();
			mMap.addLayer(mLayer);
			mMap.addLayer(mAnalysisResultLayer);

			Polyline mLine = new Polyline();
			for (int i = 0; i < mPolygon.getPointCount(); i++) {
				mLayer.addGraphic(new Graphic(mPolygon.getPoint(i), mMarkerSymbol, 100));
				if (i == 0) {
					mLine.startPath(mPolygon.getPoint(i));

				} else {
					mLine.lineTo(mPolygon.getPoint(i));
				}
			}
			mLayer.addGraphic(new Graphic(mLine, mLineSymbol));
			Graphic polygonGra = new Graphic(mPolygon, mFillSymbol);
			mLayer.addGraphic(polygonGra);
			mMap.setExtent(polygonGra.getGeometry(), 500);
			doGeometryAnalysis_landManager(p_polygonWhereFrom);
		}

	}

	public void dispose() {
		if (initOK) {
			initOK = false;
			mActivity.findViewById(R.id.imgBtnBackToLandInfos).setVisibility(View.INVISIBLE);
			mBufferSeekbarOpt.dispose();
			deleteAll();
			mMap.removeLayer(mLayer);
			mMap.removeLayer(mAnalysisResultLayer);
			mLayer = null;
			mAnalysisResultLayer = null;
			mPoints = null;
			mActivity.openOrCloseBlueToothDevices(false);
		}

	}

	/**
	 * 自动采集
	 */
	public void collectAutoOpt() {
		if (!(( GisQueryApplication.getApp()).isGpsEnable())) {
			this.mActivity.IsOpenGPSDlg(R.string.is_open_gps);
		} else {
			if (!(( GisQueryApplication.getApp()).isLocation())) {
				ToastUtils.showLong(  "GPS定位中....");
			} else {
				if (!(this.m_bIsAutoState)) {
					startAutoCollectMode();
				} else {
					cancelAutoCollectMode();
				}
			}

		}

	}

	/**
	 * 启动自动采集模式
	 */
	private void startAutoCollectMode() {
		this.m_bIsAutoState = true;
		this.imgbtnAutoCollect.setSelected(true);
		int i = GisQueryApplication.getApp().GetIntConfigData("collect_auto_time", 10);
		MapOperate.autoCollectionPause();
		MapOperate.autoCollectionStart(i);
	}

	/**
	 * 启动自动采集模式
	 */
	private void cancelAutoCollectMode() {
		this.imgbtnAutoCollect.setSelected(false);
		MapOperate.autoCollectionPause();
		this.m_bIsAutoState = false;
	}

	/**
	 * 手工插入采集
	 */
	public void collectInsertOpt() {
		if (!(( GisQueryApplication.getApp()).isGpsEnable())) {
			this.mActivity.IsOpenGPSDlg(R.string.is_open_gps);
		} else {
			if (!(( GisQueryApplication.getApp()).isLocation())) {
				ToastUtils.showLong(  "GPS定位中....");
			} else {
				NaviGPS localNaviGPS = ( GisQueryApplication.getApp()).getNaviGps();
				if (UtilsTools.IsGpsDataValid(localNaviGPS.dLongitude, localNaviGPS.dLatitude)) {
					Point localPoint2D1 = new Point(localNaviGPS.dLongitude, localNaviGPS.dLatitude);
					Point mapPoint = Utility.fromWgs84ToMap2(localPoint2D1, mMap.getSpatialReference());
					if (!this.m_bIsHandState) {
						if (mapPoint != null) {
							addMapPoint(mapPoint);
							mMap.centerAt(mapPoint, true);
						}

					}
				}
			}

		}

	}

	/**
	 * 定位分析结果
	 * 
	 * @param analysisIntersectionResultInfo
	 */
	public void showAnalysisIntersectionResultInfo(AnalysisIntersectionResultInfo analysisIntersectionResultInfo) {
		mAnalysisResultLayer.removeAll();
		Graphic mGra = new Graphic(analysisIntersectionResultInfo.getGeometry(), mFillSymbol_analysisrs);
		mAnalysisResultLayer.addGraphic(mGra);
		mMap.setExtent(analysisIntersectionResultInfo.getGeometry());
	}

	private void deleteAll() {
		mLayer.removeAll();
		mAnalysisResultLayer.removeAll();
		mResult = 0;
		if (mPoints != null && mPoints.size() > 0) {
			mPoints.clear();
			imgbtnDrawArea.setSelected(false);
			undoDraw();
		}

	}

	public void addPolygonFromBlueToothMsg(String databody) {
		if (mPoints == null) {
			mPoints = new ArrayList<Point>();
		}
		mPoints.clear();
		try {
			JSONArray jsonArray = new JSONArray(databody);
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONArray jsonPoint = jsonArray.getJSONArray(i);
				Float f_x = Float.parseFloat(jsonPoint.getString(0));
				Float f_y = Float.parseFloat(jsonPoint.getString(1));
				Point point = new Point(f_x, f_y);
				mPoints.add(point);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		MapOperate.mMapHandler.sendEmptyMessage(8888);
	}

	@Override
	public void onSingleTap(float x, float y) {
		addPoint(x, y);
	}

	private void addPoint(float x, float y) {
		if (mPoints == null) {
			mPoints = new ArrayList<Point>();
		}
		Point point = mMap.toMapPoint(x, y);
		if (mPoints_removed != null && mPoints_removed.size() > 0) {
			mPoints_removed.clear();
		}
		mPoints.add(point);
		clearAndDraw(false, true);
	}

	private void addMapPoint(Point point) {
		if (mPoints == null) {
			mPoints = new ArrayList<Point>();
		}
		mPoints.add(point);
		clearAndDraw(false, true);
	}

	private void undo() {
		if (mPoints_removed == null) {
			mPoints_removed = new ArrayList<Point>();
		}
		if (mPoints != null && mPoints.size() > 0) {
			Point tmpoint = mPoints.remove(mPoints.size() - 1);
			mPoints_removed.add(tmpoint);
			clearAndDraw(false, true);
		}

	}

	private void redo() {
		if (mPoints_removed != null && mPoints_removed.size() > 0) {
			Point tmpoint = mPoints_removed.remove(mPoints_removed.size() - 1);
			mPoints.add(tmpoint);
			clearAndDraw(false, true);
		}
	}

	public void clearAndDraw(boolean zoomTo, boolean hasBuffer) {
		if (mLayer == null)
			return;
		int[] oldGraphics = mLayer.getGraphicIDs();
		draw(zoomTo, hasBuffer);
		mLayer.removeGraphics(oldGraphics);
	}

	private void draw(boolean zoomTo, boolean hasBuffer) {
		if (mPoints.size() == 0) {
			return;
		}
		int index = 0;
		mResult = 0;
		mLine = new Polyline();
		mPolygon = new Polygon();
		for (Point point : mPoints) {
			mLayer.addGraphic(new Graphic(point, mMarkerSymbol, 100));
			if (index == 0) {
				mLine.startPath(point);
				if (mDrawMode == DrawType.AREA) {
					mPolygon.startPath(point);
				}
			} else {
				mLine.lineTo(point);
				if (mDrawMode == DrawType.AREA) {
					mPolygon.lineTo(point);
				}
			}
			mLayer.addGraphic(new Graphic(mLine, mLineSymbol));
			index++;
		}
		if (mDrawMode == DrawType.LINEAR) {
			mResult += GeometryEngine.geodesicLength(mLine, mMap.getSpatialReference(), (LinearUnit) getLinearUnit(mCurrentLinearUnit));
		} else if (mDrawMode == DrawType.AREA) {

			mLine.lineTo(mPoints.get(0));
			mLayer.addGraphic(new Graphic(mLine, mLineSymbol));
			mPolygon.lineTo(mPoints.get(0));
			mLayer.addGraphic(new Graphic(mPolygon, mFillSymbol));
			mResult = GeometryEngine.geodesicArea(mPolygon, mMap.getSpatialReference(), (AreaUnit) getAreaUnit(mCurrentAreaUnit));
			if (hasBuffer) {
				// 为缓冲操作做准备
				if (mPoints.size() >= 3) {
					// 先简化下图形，解决逆向绘制的图形分析不出来结果
					mPolygon = (Polygon) GeometryEngine.simplify(mPolygon, MapOperate.map.getSpatialReference());
					mBufferSeekbarOpt.setSeekBarEnabled(true);
					mBufferSeekbarOpt.setBufferGeometrys(mPolygon);
				} else {
					mBufferSeekbarOpt.setSeekBarEnabled(false);
				}
			}
			if (zoomTo) {
				MapOperate.setCurZoomToGeometry(mPolygon);
			}
		}
	}

	private void analysisSetting() {
		String[] arrayOfString = { "自动采集间隔设置", "叠加分析设置" };
		new AlertDialog.Builder(this.mActivity).setTitle("设置选项").setSingleChoiceItems(arrayOfString, -1, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface paramDialogInterface, int paramInt) {
				paramDialogInterface.dismiss();
				switch (paramInt) {

				case 0:
					GeometryAnalysisTool.this.collectSetOpt();
					return;
				case 1:
					GeometryAnalysisTool.this.analysisSetOpt();
				default:
					return;
				}

			}
		}).create().show();
	}

	public void collectSetOpt() {
		this.mActivity.startActivityForResult(new Intent(mContext, CollectSettingActivity.class), 3222);
	}

	public void analysisSetOpt() {
		new AnalysisLayerSetingOpt(mMap, this.mActivity);
	}

	/**
	 * Customize linear units
	 * 
	 * @param linearUnits
	 *            Array of Unit for Drawment of dimensions
	 */
	public void setLinearUnits(Unit[] linearUnits) {
		mLinearUnits = linearUnits;
	}

	Unit getLinearUnit(int position) {
		return mLinearUnits == null ? mDefaultLinearUnits[position] : mLinearUnits[position];
	}

	int getAreaUnitSize() {
		return mAreaUnits == null ? mDefaultAreaUnits.length : mAreaUnits.length;
	}

	/**
	 * Customize the area units
	 * 
	 * @param areaUnits
	 *            Array of Unit for Drawment of dimensions
	 */
	public void setAreaUnits(Unit[] areaUnits) {
		mAreaUnits = areaUnits;
	}

	Unit getAreaUnit(int position) {
		return mAreaUnits == null ? mDefaultAreaUnits[position] : mAreaUnits[position];
	}

	int getLinearUnitSize() {
		return mLinearUnits == null ? mDefaultLinearUnits.length : mLinearUnits.length;
	}

	int getUnitSize() {
		return mDrawMode == DrawType.LINEAR ? getLinearUnitSize() : getAreaUnitSize();
	}

	Unit getUnit(int position) {
		return mDrawMode == DrawType.LINEAR ? getLinearUnit(position) : getAreaUnit(position);
	}

	Unit getCurrentUnit() {
		return getUnit(mDrawMode == DrawType.LINEAR ? mCurrentLinearUnit : mCurrentAreaUnit);
	}

	/**
	 * Customize the line symbol
	 * 
	 * @param symbol
	 *            To draw lines on GraphicsLayer
	 */
	public void setLineSymbol(LineSymbol symbol) {
		mLineSymbol = symbol;
	}

	/**
	 * Customize the marker symbol
	 * 
	 * @param symbol
	 *            To draw marker symbol on GraphicsLayer
	 */
	public void setMarkerSymbol(MarkerSymbol symbol) {
		mMarkerSymbol = symbol;
	}

	/**
	 * Customize the fill symbol
	 * 
	 * @param symbol
	 *            To draw fill symbol on GraphicsLayer
	 */
	public void setFillSymbol(FillSymbol symbol) {
		mFillSymbol = symbol;
	}

	MultiPath getGeometry() {
		return (mDrawMode == DrawType.LINEAR) ? mLine : mPolygon;
	}

}
