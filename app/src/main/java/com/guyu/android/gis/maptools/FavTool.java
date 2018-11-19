package com.guyu.android.gis.maptools;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.guyu.android.database.SdCardDBHelper;
import com.guyu.android.gis.opt.MapOperate;
import com.guyu.android.utils.SysConfig;

public class FavTool implements OnSingleTapListener {

	private static final long serialVersionUID = 1L;

	public enum FavType {
		LINEAR, AREA;

		static public FavType getType(int i) {
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

	private MapView mMap;
	private Activity mActivity;
	private GraphicsLayer mLayer;
	private Context mContext;
	private MarkerSymbol mMarkerSymbol;
	private LineSymbol mLineSymbol;
	private Unit[] mDefaultLinearUnits;
	private int mCurrentAreaUnit;
	private Unit[] mAreaUnits;
	private Unit[] mDefaultAreaUnits;
	private FillSymbol mFillSymbol;

	private ImageButton imgbtnFavLine;
	private ImageButton imgbtnFavArea;
	private ImageButton imgbtnUndo;
	private ImageButton imgbtnRedo;
	private ImageButton imgbtnDelete;
	private ImageButton imgbtnOK;
	private ImageButton imgMenuDelete;

	private Boolean initOK = false;

	private FavType mFavMode = FavType.LINEAR;

	private ArrayList<Point> mPoints;
	private ArrayList<Point> mPoints_removed;
	private double mResult;
	private OnSingleTapListener mOldOnSingleTapListener;
	private TextView mText;
	private CalloutPopupWindow mCallout;
	private Polyline mLine;
	private Polygon mPolygon;
	private int mCurrentLinearUnit;
	private Unit[] mLinearUnits;
	private EditText localEditText1;
	private EditText localEditText2;

	private String layerName = "手动绘制";

	private String favgeometry;
	private String favattributes;
	private String favsymbol;
	private LinearLayout mVgFavToolbar = null;

	public FavTool(MapView map, Activity activity) {
		this.mMap = map;
		mContext = mMap.getContext();
		this.mActivity = activity;
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

		mVgFavToolbar = ((LinearLayout) activity.findViewById(R.id.vg_fav_toolbar));
		
		imgbtnFavArea = (ImageButton) activity.findViewById(R.id.imgFavArea);
		//imgbtnFavLine = (ImageButton) activity.findViewById(R.id.imgFavLine);

		imgMenuDelete = (ImageButton) activity.findViewById(R.id.imgMenuDelete);
		imgbtnUndo = (ImageButton) activity.findViewById(R.id.imgFavUndo);
		imgbtnRedo = (ImageButton) activity.findViewById(R.id.imgFavRedo);
		imgbtnDelete = (ImageButton) activity.findViewById(R.id.imgFavDelete);
		imgbtnOK = (ImageButton) activity.findViewById(R.id.imgFavOK);

		imgMenuDelete.setOnClickListener(new View.OnClickListener() {
			public void onClick(View paramView) {
				try {
					deleteAll();
					mPolygon = new Polygon();
					mVgFavToolbar.setVisibility(View.GONE);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		
		imgbtnOK.setOnClickListener(new View.OnClickListener() {
			public void onClick(View paramView) {
				try {
					showAddFavDlg();
					dispose();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		/*imgbtnFavLine.setOnClickListener(new View.OnClickListener() {
			public void onClick(View paramView) {
				ImageButton imgbtn = (ImageButton) paramView;
				if (imgbtn.isSelected()) {
					undoFav();
					imgbtn.setSelected(false);
				} else {
					doFavLine();
					imgbtn.setSelected(true);
					imgbtnFavArea.setSelected(false);
				}
			}
		});*/

		imgbtnFavArea.setOnClickListener(new View.OnClickListener() {
			public void onClick(View paramView) {
				ImageButton imgbtn = (ImageButton) paramView;
				if (imgbtn.isSelected()) {
					undoFav();
					imgbtn.setSelected(false);
				} else {
					doFavArea();
					imgbtn.setSelected(true);
					//imgbtnFavLine.setSelected(false);
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

	private void doFavLine() {
		mFavMode = FavType.LINEAR;
		if (mOldOnSingleTapListener == null) {
			mOldOnSingleTapListener = mMap.getOnSingleTapListener();
		}
		mMap.setOnSingleTapListener(this);
		mPoints = new ArrayList<Point>();
	}

	private void doFavArea() {
		mFavMode = FavType.AREA;
		if (mOldOnSingleTapListener == null) {
			mOldOnSingleTapListener = mMap.getOnSingleTapListener();
		}
		mMap.setOnSingleTapListener(this);
		mPoints = new ArrayList<Point>();
	}

	private void undoFav() {
		mMap.setOnSingleTapListener(mOldOnSingleTapListener);
		mPoints = null;
		mOldOnSingleTapListener = null;
	}

	private void deleteAll() {
		if (mLayer != null) {
			mLayer.removeAll();
		}
		mResult = 0;
		if (mPoints != null && mPoints.size() > 0) {
			mPoints.clear();
			// showResult();
			//imgbtnFavLine.setSelected(false);
			imgbtnFavArea.setSelected(false);
			undoFav();
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
		if (mLayer != null) {
			int[] oldGraphics = mLayer.getGraphicIDs();
			draw();
			mLayer.removeGraphics(oldGraphics);
			updateMenu();
		}
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
				if (mFavMode == FavType.AREA) {
					mPolygon.startPath(point);
				}
			} else {
				mLine.lineTo(point);
				if (mFavMode == FavType.AREA) {
					mPolygon.lineTo(point);
				}
			}
			mLayer.addGraphic(new Graphic(mLine, mLineSymbol));
			index++;
		}
		if (mFavMode == FavType.LINEAR) {
			mResult += GeometryEngine.geodesicLength(mLine,
					mMap.getSpatialReference(),
					(LinearUnit) getLinearUnit(mCurrentLinearUnit));
			Point screenPoint = mMap.toScreenPoint(mPoints.get(index - 1));
			// showResult((float) screenPoint.getX(), (float)
			// screenPoint.getY());
		} else if (mFavMode == FavType.AREA) {
			mLine.lineTo(mPoints.get(0));
			mLayer.addGraphic(new Graphic(mLine, mLineSymbol));
			mPolygon.lineTo(mPoints.get(0));
			mLayer.addGraphic(new Graphic(mPolygon, null));
			mResult = GeometryEngine.geodesicArea(mPolygon,
					mMap.getSpatialReference(),
					(AreaUnit) getAreaUnit(mCurrentAreaUnit));
			Point labelPointForPolygon = GeometryEngine
					.getLabelPointForPolygon(mPolygon,
							mMap.getSpatialReference());
			Point screenPoint = mMap.toScreenPoint(labelPointForPolygon);
			// showResult((float) screenPoint.getX(), (float)
			// screenPoint.getY());
		}
	}

	private void updateMenu() {

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
		return mFavMode == FavType.LINEAR ? getLinearUnitSize()
				: getAreaUnitSize();
	}

	Unit getUnit(int position) {
		return mFavMode == FavType.LINEAR ? getLinearUnit(position)
				: getAreaUnit(position);
	}

	Unit getCurrentUnit() {
		return getUnit(mFavMode == FavType.LINEAR ? mCurrentLinearUnit
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
		return (mFavMode == FavType.LINEAR) ? mLine : mPolygon;
	}

	private String getResultString() {
		String resultStr = "";
		if (mFavMode == FavType.LINEAR) {
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

	public void showAddFavDlg() throws Exception {
		favgeometry = GeometryEngine.geometryToJson(
				MapOperate.map.getSpatialReference(), mPolygon);
		favattributes = "";
		favsymbol = mLineSymbol.toJson();
		LayoutInflater inflater = (LayoutInflater) this.mActivity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View localView = inflater.inflate(R.layout.layout_add_fav, null);
		localEditText1 = (EditText) localView.findViewById(R.id.et_title);
		localEditText1.setText(layerName);
		localEditText2 = (EditText) localView.findViewById(R.id.et_remark);
		new AlertDialog.Builder(this.mActivity)
				.setView(localView)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface paramDialogInterface,
							int paramInt) {
						ContentValues localContentValues = new ContentValues();
						localContentValues.put("title", localEditText1
								.getText().toString());
						localContentValues.put("peoId",
								SysConfig.mHumanInfo.getHumanId());
						localContentValues.put("layerName", layerName);
						localContentValues.put("remark", localEditText2
								.getText().toString());
						localContentValues.put("favgeometry", favgeometry);
						localContentValues.put("favattributes", favattributes);
						localContentValues.put("favsymbol", favsymbol);
						localContentValues.put("time",
								Long.valueOf(System.currentTimeMillis()));
						if (SdCardDBHelper.getInstance(mActivity).tableIsExist(
								"tbFav")) {
							if (localEditText1.getText().toString().equals("")) {
								MsgBox(mContext, "请输入名称");
							} else {
								SdCardDBHelper.getInstance(mActivity).insert(
										"tbFav", localContentValues);
								deleteAll();
								mPolygon = new Polygon();
								mVgFavToolbar.setVisibility(View.GONE);
								MsgBox(mContext, "已收藏");
							}
						} else {
							MsgBox(mContext, "插入失败");
						}

					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface paramDialogInterface,
							int paramInt) {
						paramDialogInterface.dismiss();
						init();
						mPolygon = new Polygon();
						mVgFavToolbar.setVisibility(View.GONE);
					}
				}).create().show();
	}

	public static void MsgBox(Context paramContext, String paramString) {
		Toast.makeText(paramContext, paramString, 0).show();
	}

}
