package com.guyu.android.gis.maptools;

import java.io.IOException;
import java.util.ArrayList;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.json.JSONArray;
import org.json.JSONException;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.view.ActionMode;

import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.ToastUtils;
import com.guyu.android.gis.activity.CollectSettingActivity;
import com.guyu.android.gis.activity.InitializeRecordActivity;
import com.guyu.android.R;

import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.MapView;

import com.esri.android.map.event.OnSingleTapListener;
import com.esri.core.geometry.AreaUnit;
import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.LinearUnit;
import com.esri.core.geometry.MapGeometry;
import com.esri.core.geometry.MultiPath;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polygon;
import com.esri.core.geometry.Polyline;
import com.esri.core.geometry.Unit;

import com.esri.core.map.Graphic;
import com.esri.core.symbol.FillSymbol;
import com.esri.core.symbol.LineSymbol;
import com.esri.core.symbol.MarkerSymbol;
import com.esri.core.symbol.SimpleFillSymbol;
import com.esri.core.symbol.SimpleLineSymbol;
import com.esri.core.symbol.SimpleMarkerSymbol;
import com.esri.core.symbol.SimpleMarkerSymbol.STYLE;
import com.guyu.android.database.task.CaseDBMExternal;
import com.guyu.android.database.task.TaskDBMExternal;
import com.guyu.android.gis.activity.MainActivity;
import com.guyu.android.gis.app.GisQueryApplication;
import com.guyu.android.gis.app.NaviGPS;
import com.guyu.android.gis.common.AnalysisIntersectionResultInfo;
import com.guyu.android.gis.common.AnalysisLayerResultInfo;
import com.guyu.android.gis.common.Case;
import com.guyu.android.gis.opt.MapOperate;
import com.guyu.android.utils.Utility;
import com.guyu.android.utils.UtilsTools;
import com.guyu.android.view.AnalysisLayerSetingOpt;
import com.guyu.android.view.BufferSeekbarOpt;

public class GeometryCollectTool implements OnSingleTapListener {

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

	private Case caseObj;
	private String caseType;
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
	private int callbacknum = 0;
	private String[] analysisLayerAry;
	private ArrayList<AnalysisLayerResultInfo> analysisResultInfo;
	private Boolean initOK = false;
	private Boolean m_bIsAutoState = false;// 是否自动采集
	private boolean m_bIsHandState = false;// 是否手动采集
	private Object polygon;

	public GeometryCollectTool(MapView map, BufferSeekbarOpt bufferSeekbarOpt, MainActivity activity) {
		this.mMap = map;
		this.mBufferSeekbarOpt = bufferSeekbarOpt;
		this.mActivity = activity;
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
		mFillSymbol_analysisrs.setOutline(new SimpleLineSymbol(Color.TRANSPARENT, 0));
		mPoints = new ArrayList<Point>();
		mDrawMode = DrawType.AREA;
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
			imgbtnDrawArea = (ImageButton) mActivity.findViewById(R.id.imgHandCollect);
			imgbtnUndo = (ImageButton) mActivity.findViewById(R.id.imgUndo);
			imgbtnRedo = (ImageButton) mActivity.findViewById(R.id.imgRedo);
			imgbtnOK = (ImageButton) mActivity.findViewById(R.id.imgOK);
			imgbtnAutoCollect = (ImageButton) mActivity.findViewById(R.id.imgAutoCollect);
			imgbtnInsertCollect = (ImageButton) mActivity.findViewById(R.id.imgInsertCollect);
			imgbtnBlueToothCollect = (ImageButton) mActivity.findViewById(R.id.imgBlueToothCollect);
			mActivity.findViewById(R.id.vg_seek_bar_control).setVisibility(View.INVISIBLE);

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
						m_bIsHandState = false;
						undoDraw();
						imgbtn.setSelected(false);
					} else {
						m_bIsHandState = true;
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
						MapOperate.curGeometry = mPolygon;
						if (caseObj == null) {
							dispose();
							((RelativeLayout) GeometryCollectTool.this.mActivity.findViewById(R.id.vg_top_title)).setVisibility(View.VISIBLE);
							((LinearLayout) GeometryCollectTool.this.mActivity.findViewById(R.id.vg_top_collect)).setVisibility(View.INVISIBLE);
							Intent localIntent = new Intent(mContext, InitializeRecordActivity.class);
							localIntent.putExtra("caseType", caseType);
							mActivity.startActivity(localIntent);
						} else {
							boolean boolRs = false;
							                                                             //启高：update采集地块的信息
							if ("dtxc".endsWith(caseType) || "wfxs".endsWith(caseType) || "change".endsWith(caseType)
									|| "linxia".endsWith(caseType)|| "linmu".endsWith(caseType)|| "mucai".endsWith(caseType)
									|| "weifa".endsWith(caseType) || "dongwu".endsWith(caseType)) {
								CaseDBMExternal caseDBMExternal = new CaseDBMExternal(mActivity);
								boolRs = caseDBMExternal.updateGeos(caseObj.getCaseId(), MapOperate.curGeometry);
							} else {
								TaskDBMExternal taskDBMExternal = new TaskDBMExternal(mActivity);
								boolRs = taskDBMExternal.updateGeos(caseObj.getCaseId(),
										GeometryEngine.geometryToJson(MapOperate.map.getSpatialReference(), MapOperate.curGeometry));
							}
							if (boolRs) {
								new AlertDialog.Builder(mActivity).setTitle("图形采集提示").setMessage("图形采集成功！").create().show();
							} else {
								new AlertDialog.Builder(mActivity).setTitle("图形采集提示").setMessage("图形采集失败！").create().show();
							}
						}

					} else {
						
						ToastUtils.showLong( "请您绘制好地块！");
					}

				}
			});
			mBufferSeekbarOpt.init();
			mLayer = new GraphicsLayer();
			mAnalysisResultLayer = new GraphicsLayer();
			mMap.addLayer(mLayer);
			mMap.addLayer(mAnalysisResultLayer);
			if (this.caseObj != null) {
				addGeometryFromCase();
			}
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
				ToastUtils.showLong( "GPS定位中....");
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
		int i =GisQueryApplication.getApp().GetIntConfigData("collect_auto_time", 10);
		MapOperate.autoCollectionPause();
		MapOperate.autoCollectionStart(i);
	}

	/**
	 * 停止自动采集模式
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
				ToastUtils.showLong( "GPS定位中....");
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

	public void addGeometryFromCase() {
		deleteAll();
		mPoints = new ArrayList<Point>();
		if (this.caseObj.getGeos() != null && !"".equals(this.caseObj.getGeos())) {
			JsonFactory jsonFactory = new JsonFactory();
			JsonParser jsonParser;
			try {
				jsonParser = jsonFactory.createJsonParser(this.caseObj.getGeos());
				MapGeometry mapGeo = GeometryEngine.jsonToGeometry(jsonParser);
				Geometry geo = mapGeo.getGeometry();
				if (geo instanceof Polygon) {
					MultiPath pg = (MultiPath) geo;
					int pathSize = pg.getPathCount();
					for (int j = 0; j < pathSize; j++) {
						int size = pg.getPathSize(j);
						for (int i = 0; i < size; i++) {
							Point pt = pg.getPoint(i);
							Point point = new Point(pt.getX(), pt.getY());
							if (i != size - 1) {
								mPoints.add(point);
							}
						}
					}
					MapOperate.mMapHandler.sendEmptyMessage(7777);
				}
			} catch (JsonParseException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			new AlertDialog.Builder(mActivity).setTitle("图形定位提示").setMessage("您还没有采集图形，请采集图形！").create().show();
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
		MapOperate.mMapHandler.sendEmptyMessage(7777);
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
		clearAndDraw(false, false);
	}

	private void addMapPoint(Point point) {
		if (mPoints == null) {
			mPoints = new ArrayList<Point>();
		}
		mPoints.add(point);
		clearAndDraw(false, false);
	}

	private void undo() {
		if (mPoints_removed == null) {
			mPoints_removed = new ArrayList<Point>();
		}
		if (mPoints != null && mPoints.size() > 0) {
			Point tmpoint = mPoints.remove(mPoints.size() - 1);
			mPoints_removed.add(tmpoint);
			clearAndDraw(false, false);
		}

	}

	private void redo() {
		if (mPoints_removed != null && mPoints_removed.size() > 0) {
			Point tmpoint = mPoints_removed.remove(mPoints_removed.size() - 1);
			mPoints.add(tmpoint);
			clearAndDraw(false, false);
		}
	}

	public void clearAndDraw(boolean zoomTo, boolean hasBuffer) {
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
		String[] arrayOfString = { "自动采集间隔设置", "添加地块设置" };
		new AlertDialog.Builder(this.mActivity).setTitle("设置选项").setSingleChoiceItems(arrayOfString, -1, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface paramDialogInterface, int paramInt) {
				paramDialogInterface.dismiss();
				switch (paramInt) {

				case 0:
					GeometryCollectTool.this.collectSetOpt();
					return;
				case 1:
					GeometryCollectTool.this.analysisSetOpt();
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

	public Case getCaseObj() {
		return caseObj;
	}

	public void setCaseObj(Case caseObj) {
		this.caseObj = caseObj;
	}

	public String getCaseType() {
		return caseType;
	}

	public void setCaseType(String caseType) {
		this.caseType = caseType;
	}

}
