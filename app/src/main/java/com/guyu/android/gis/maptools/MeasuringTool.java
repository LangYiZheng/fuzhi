package com.guyu.android.gis.maptools;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.ActionMode;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.guyu.android.R;
import com.esri.android.map.CalloutPopupWindow;
import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.MapView;
import com.esri.android.map.event.OnSingleTapListener;
import com.esri.core.geometry.AreaUnit;
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.LinearUnit;
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

public class MeasuringTool implements OnSingleTapListener {

	private static final long serialVersionUID = 1L;

	public enum MeasureType {
		LINEAR, AREA;

		static public MeasureType getType(int i) {
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
	private OnSingleTapListener mOldOnSingleTapListener;
	private MarkerSymbol mMarkerSymbol;
	private LineSymbol mLineSymbol;
	private double mResult;
	private TextView mText;
	private MeasureType mMeasureMode = MeasureType.LINEAR;
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
	private CalloutPopupWindow mCallout;
	private ActionMode mMode;
	private Polyline mLine;
	private Polygon mPolygon;
	private ImageButton imgbtnMeasureLine;
	private ImageButton imgbtnMeasureArea;
	private ImageButton imgbtnUndo;
	private ImageButton imgbtnRedo;
	private ImageButton imgbtnDelete;
	private Boolean initOK = false;

	public MeasuringTool(MapView map, Activity activity) {
		this.mMap = map;
		mContext = mMap.getContext();
		mMarkerSymbol = new SimpleMarkerSymbol(Color.RED, 10, STYLE.CIRCLE);
		mLineSymbol = new SimpleLineSymbol(Color.BLACK, 3);
		mDefaultLinearUnits = new Unit[] { Unit.create(LinearUnit.Code.METER),
				Unit.create(LinearUnit.Code.KILOMETER),
				Unit.create(LinearUnit.Code.FOOT),
				Unit.create(LinearUnit.Code.MILE_STATUTE) };
		mDefaultAreaUnits = new Unit[] {
				Unit.create(AreaUnit.Code.SQUARE_METER),
				Unit.create(AreaUnit.Code.SQUARE_KILOMETER),
				Unit.create(AreaUnit.Code.SQUARE_FOOT),
				Unit.create(AreaUnit.Code.SQUARE_MILE_STATUTE) };
		mFillSymbol = new SimpleFillSymbol(Color.argb(100, 225, 225, 0));
		mFillSymbol.setOutline(new SimpleLineSymbol(Color.TRANSPARENT, 0));
		imgbtnMeasureLine = (ImageButton) activity
				.findViewById(R.id.imgMeasureLine);
		imgbtnMeasureArea = (ImageButton) activity
				.findViewById(R.id.imgMeasureArea);
		imgbtnUndo = (ImageButton) activity.findViewById(R.id.imgMeasureUndo);
		imgbtnRedo = (ImageButton) activity.findViewById(R.id.imgMeasureRedo);
		imgbtnDelete = (ImageButton) activity
				.findViewById(R.id.imgMeasureDelete);
		imgbtnMeasureLine.setOnClickListener(new View.OnClickListener() {
			public void onClick(View paramView) {
				ImageButton imgbtn = (ImageButton) paramView;
				if (imgbtn.isSelected()) {
					undoMeasure();
					imgbtn.setSelected(false);
				} else {
					doMeasureLine();
					imgbtn.setSelected(true);
					imgbtnMeasureArea.setSelected(false);
				}
			}
		});
		imgbtnMeasureArea.setOnClickListener(new View.OnClickListener() {
			public void onClick(View paramView) {
				ImageButton imgbtn = (ImageButton) paramView;
				if (imgbtn.isSelected()) {
					undoMeasure();
					imgbtn.setSelected(false);
				} else {
					doMeasureArea();
					imgbtn.setSelected(true);
					imgbtnMeasureLine.setSelected(false);
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
		imgbtnDelete.setOnClickListener(new View.OnClickListener() {
			public void onClick(View paramView) {
				deleteAll();
			}
		});
	}

	private void doMeasureLine() {
		mMeasureMode = MeasureType.LINEAR;
		if (mOldOnSingleTapListener == null) {
			mOldOnSingleTapListener = mMap.getOnSingleTapListener();
		}
		mMap.setOnSingleTapListener(this);
		mPoints = new ArrayList<Point>();
	}

	private void doMeasureArea() {
		mMeasureMode = MeasureType.AREA;
		if (mOldOnSingleTapListener == null) {
			mOldOnSingleTapListener = mMap.getOnSingleTapListener();
		}
		mMap.setOnSingleTapListener(this);
		mPoints = new ArrayList<Point>();
	}

	private void undoMeasure() {
		mMap.setOnSingleTapListener(mOldOnSingleTapListener);
		mPoints = null;
		mOldOnSingleTapListener = null;
	}

	public void init() {
		if (!initOK) {
			initOK = true;
			mLayer = new GraphicsLayer();
			mMap.addLayer(mLayer);
		}

	}

	public void dispose() {
		if (initOK) {
			initOK = false;
			deleteAll();
			mMap.removeLayer(mLayer);
			mLayer = null;
			mPoints = null;
		}
	}

	private void deleteAll() {
		if (mLayer != null) {
			mLayer.removeAll();
		}
		mResult = 0;
		if (mPoints != null && mPoints.size() > 0) {
			mPoints.clear();
			showResult();
			imgbtnMeasureLine.setSelected(false);
			imgbtnMeasureArea.setSelected(false);
			undoMeasure();
			updateMenu();
		}

	}

	@Override
	public void onSingleTap(float x, float y) {
		addPoint(x, y);
	}

	private void addPoint(float x, float y) {
		Point point = mMap.toMapPoint(x, y);
		mPoints.add(point);
		clearAndDraw();
	}

	private void undo() {
		if (mPoints_removed == null) {
			mPoints_removed = new ArrayList<Point>();
		}
		if (mPoints != null && mPoints.size() > 0) {
			Point tmpoint = mPoints.remove(mPoints.size() - 1);
			mPoints_removed.add(tmpoint);
			clearAndDraw();
		}

	}

	private void redo() {
		if (mPoints_removed != null && mPoints_removed.size() > 0) {
			Point tmpoint = mPoints_removed.remove(mPoints_removed.size() - 1);
			mPoints.add(tmpoint);
			clearAndDraw();
		}
	}

	private void clearAndDraw() {
		int[] oldGraphics = mLayer.getGraphicIDs();
		draw();
		mLayer.removeGraphics(oldGraphics);
		updateMenu();
	}

	private void draw() {
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
				if (mMeasureMode == MeasureType.AREA) {
					mPolygon.startPath(point);
				}
			} else {
				mLine.lineTo(point);
				if (mMeasureMode == MeasureType.AREA) {
					mPolygon.lineTo(point);
				}
			}
			mLayer.addGraphic(new Graphic(mLine, mLineSymbol));
			index++;
		}
		if (mMeasureMode == MeasureType.LINEAR) {
			mResult += GeometryEngine.geodesicLength(mLine,
					mMap.getSpatialReference(),
					(LinearUnit) getLinearUnit(mCurrentLinearUnit));
			Point screenPoint = mMap.toScreenPoint(mPoints.get(index - 1));
			showResult((float) screenPoint.getX(), (float) screenPoint.getY());
		} else if (mMeasureMode == MeasureType.AREA) {
			mLine.lineTo(mPoints.get(0));
			mLayer.addGraphic(new Graphic(mLine, mLineSymbol));
			mPolygon.lineTo(mPoints.get(0));
			mLayer.addGraphic(new Graphic(mPolygon, mFillSymbol));
			mResult = GeometryEngine.geodesicArea(mPolygon,
					mMap.getSpatialReference(),
					(AreaUnit) getAreaUnit(mCurrentAreaUnit));
			Point labelPointForPolygon = GeometryEngine
					.getLabelPointForPolygon(mPolygon,
							mMap.getSpatialReference());
			Point screenPoint = mMap.toScreenPoint(labelPointForPolygon);
			showResult((float) screenPoint.getX(), (float) screenPoint.getY());
		}
	}

	private void updateMenu() {
		// mMode.getMenu().findItem(MENU_DELETE).setVisible(mPoints.size() > 0);
		// mMode.getMenu().findItem(MENU_UNDO).setVisible(mPoints.size() > 0);
	}

	private void showResult(float x, float y) {
		if (mResult > 0) {
			if (mCallout == null) {
				mText = new TextView(mContext);
				mCallout = new CalloutPopupWindow(mText);
			}
			mText.setText(getResultString());
			mCallout.showCallout(mMap, mMap.toMapPoint(x, y), 0, 0);
		} else if (mCallout != null && mCallout.isShowing()) {
			mCallout.hide();
		}
	}

	private void showResult() {
		if (mResult > 0) {
			mText.setText(getResultString());
			mCallout.showCallout(mMap);
		} else if (mCallout != null && mCallout.isShowing()) {
			mCallout.hide();
		}
	}

	/**
	 * Customize linear units
	 * 
	 * @param linearUnits
	 *            Array of Unit for measurement of dimensions
	 */
	public void setLinearUnits(Unit[] linearUnits) {
		mLinearUnits = linearUnits;
	}

	Unit getLinearUnit(int position) {
		return mLinearUnits == null ? mDefaultLinearUnits[position]
				: mLinearUnits[position];
	}

	int getAreaUnitSize() {
		return mAreaUnits == null ? mDefaultAreaUnits.length
				: mAreaUnits.length;
	}

	/**
	 * Customize the area units
	 * 
	 * @param areaUnits
	 *            Array of Unit for measurement of dimensions
	 */
	public void setAreaUnits(Unit[] areaUnits) {
		mAreaUnits = areaUnits;
	}

	Unit getAreaUnit(int position) {
		return mAreaUnits == null ? mDefaultAreaUnits[position]
				: mAreaUnits[position];
	}

	int getLinearUnitSize() {
		return mLinearUnits == null ? mDefaultLinearUnits.length
				: mLinearUnits.length;
	}

	int getUnitSize() {
		return mMeasureMode == MeasureType.LINEAR ? getLinearUnitSize()
				: getAreaUnitSize();
	}

	Unit getUnit(int position) {
		return mMeasureMode == MeasureType.LINEAR ? getLinearUnit(position)
				: getAreaUnit(position);
	}

	Unit getCurrentUnit() {
		return getUnit(mMeasureMode == MeasureType.LINEAR ? mCurrentLinearUnit
				: mCurrentAreaUnit);
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
		return (mMeasureMode == MeasureType.LINEAR) ? mLine : mPolygon;
	}

	private String getResultString() {
		String resultStr = "";
		if (mMeasureMode == MeasureType.LINEAR) {
			if (mResult > 1000) {
				mResult = mResult / 1000.0;
				resultStr = mResult > 0 ? String.format("%.2f", mResult)
						+ " 公里" : "";
			} else {
				resultStr = mResult > 0 ? String.format("%.2f", mResult) + " 米"
						: "";
			}

		} else {
			if (mResult > 1000000) {
				mResult = mResult / 1000000.0;
				resultStr = mResult > 0 ? String.format("%.2f", mResult)
						+ " 平方公里" : "";
			} else {
				resultStr = mResult > 0 ? String.format("%.2f", mResult)
						+ " 平方米" : "";
			}
		}
		return resultStr;
	}

}
