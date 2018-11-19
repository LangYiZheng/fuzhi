package com.guyu.android.gis.maptools;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.esri.android.map.CalloutPopupWindow;
import com.esri.android.map.FeatureLayer;
import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.Layer;
import com.esri.android.map.MapView;
import com.esri.android.map.ags.ArcGISFeatureLayer;
import com.esri.android.map.event.OnSingleTapListener;
import com.esri.core.geometry.AreaUnit;
import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.LinearUnit;
import com.esri.core.geometry.MultiPath;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polygon;
import com.esri.core.geometry.Polyline;
import com.esri.core.geometry.Unit;
import com.esri.core.map.Feature;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.FillSymbol;
import com.esri.core.symbol.LineSymbol;
import com.esri.core.symbol.MarkerSymbol;
import com.esri.core.symbol.SimpleFillSymbol;
import com.esri.core.symbol.SimpleLineSymbol;
import com.esri.core.symbol.SimpleMarkerSymbol;
import com.esri.core.symbol.SimpleMarkerSymbol.STYLE;
import com.guyu.android.database.SdCardDBHelper;
import com.guyu.android.gis.adapter.InfoAdapter;
import com.guyu.android.gis.app.GisQueryApplication;
import com.guyu.android.gis.config.LayerConfig;
import com.guyu.android.gis.opt.FavViewOpt;
import com.guyu.android.gis.opt.MapOperate;
import com.guyu.android.utils.SysConfig;
import com.guyu.android.R;

public class IdentifyTool implements OnSingleTapListener {

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

    private MapView mMap;
    private Activity mActivity;
    private GisQueryApplication mApp;
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
    private ViewGroup mInfoShowGroup = null;
    private ListView mInfoShowListView = null;
    private List<String> mInfoShowNames = new ArrayList();
    private TextView mInfoShowTextView = null;
    private List<String> mInfoShowValues = new ArrayList();
    private EditText localEditText1;
    private EditText localEditText2;
    private String layerName;
    public List<Boolean> lstSelected = new ArrayList();
    private ViewGroup mViewGroup = null;
    private ListView mListView = null;
    private List<Integer> m_id = new ArrayList();
    private List<String> m_title = new ArrayList();
    private List<String> m_remark = new ArrayList();
    private Dialog mDialog;
    private String favgeometry;
    private String favattributes;
    private String favsymbol;
    private Layer curSelectedLayer;

    private List<Integer> indexs;
    private List<String> names;

    public IdentifyTool(MapView map, Activity activity) {
        this.mMap = map;
        this.mActivity = activity;
        this.mApp =  GisQueryApplication.getApp();
        this.mInfoShowGroup = ((ViewGroup) mActivity
                .findViewById(R.id.vg_query_result));
        this.mInfoShowListView = ((ListView) mActivity
                .findViewById(R.id.lst_info_show));
        mContext = mMap.getContext();
        mMarkerSymbol = new SimpleMarkerSymbol(Color.RED, 10, STYLE.CIRCLE);
        mLineSymbol = new SimpleLineSymbol(Color.BLACK, 3);
        mDefaultLinearUnits = new Unit[]{Unit.create(LinearUnit.Code.METER),
                Unit.create(LinearUnit.Code.KILOMETER),
                Unit.create(LinearUnit.Code.FOOT),
                Unit.create(LinearUnit.Code.MILE_STATUTE)};
        mDefaultAreaUnits = new Unit[]{
                Unit.create(AreaUnit.Code.SQUARE_METER),
                Unit.create(AreaUnit.Code.SQUARE_KILOMETER),
                Unit.create(AreaUnit.Code.SQUARE_FOOT),
                Unit.create(AreaUnit.Code.SQUARE_MILE_STATUTE)};
        mFillSymbol = new SimpleFillSymbol(Color.argb(100, 225, 225, 0));
        mFillSymbol.setOutline(new SimpleLineSymbol(Color.TRANSPARENT, 0));
        this.mActivity.findViewById(R.id.btn_back_to_normal)
                .setOnClickListener(new View.OnClickListener() {
                    public void onClick(View paramView) {
                        MapOperate.clearFavShowLayer();
                        IdentifyTool.this.clearSelection();
                        mInfoShowGroup.setVisibility(View.INVISIBLE);
                        if (GisQueryApplication.getApp().getProjectconfig()
                                .exitFunctionCode("zonenavi") || GisQueryApplication.getApp().getProjectconfig()
                                .exitFunctionCode("fulltextquery")) {
                            mActivity.findViewById(R.id.leftpanel)
                                    .setVisibility(View.VISIBLE);
                        } else {

                            mActivity.findViewById(R.id.leftpanel)
                                    .setVisibility(View.GONE);
                        }
                    }
                });
        this.mActivity.findViewById(R.id.btn_add_to_fav).setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View paramView) {
                        showAddFavDlg();
                    }
                });
        new FavViewOpt(mMap, mActivity);
    }

    private void doIdentify() {
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

    private void undoIdentify() {
        mMap.setOnSingleTapListener(mOldOnSingleTapListener);
        mPoints = null;
        mOldOnSingleTapListener = null;
    }

    public void init() {

        mLayer = new GraphicsLayer();
        mMap.addLayer(mLayer);
        doIdentify();

    }

    public void dispose() {
        deleteAll();
        mMap.removeLayer(mLayer);
        mLayer = null;
        mPoints = null;
    }

    private void deleteAll() {
        if (mLayer != null) {
            mLayer.removeAll();
        }
        mResult = 0;
        if (mPoints != null && mPoints.size() > 0) {
            mPoints.clear();
            showResult();
            undoIdentify();
            updateMenu();
        }

    }

    @Override
    public void onSingleTap(final float x, final float y) {
//		identify(x, y, this.mMap.getLayers().length - 1);
        Layer[] alllayers = this.mMap.getLayers();
        indexs = new ArrayList<>();
        names = new ArrayList<>();
        for (int i = 0; i < alllayers.length; i++) {
            if (alllayers[i].isVisible() && alllayers[i].getName() != null&&!alllayers[i].getName().equals("dx")&&!alllayers[i].getName().equals("lj_clip_ProjectRaster")) {
                indexs.add(i);
                names.add(alllayers[i].getName());
            }
        }
        if (names.size() != 0) {
            if (names.size() != 1) {
//                String[] items = new String[names.size()];
//                for (int i = 0; i < names.size(); i++) {
//                    items[i] = names.get(i);
//                }
//
//                AlertDialog dialog = new AlertDialog.Builder(mActivity).setTitle("请选择图层")
//                        .setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
//
//                            @Override
//                            public void onClick(DialogInterface dialog, int i) {
//                                dialog.dismiss();
//                                identify(x, y, indexs.get(i).intValue());
//                            }
//                        }).create();
//                dialog.show();
                identify(x, y, indexs.get(indexs.size()-1).intValue());
            } else {
                identify(x, y, indexs.get(0).intValue());
            }
        } else {
            Toast.makeText(mActivity, "对不起，暂未有任何图层信息", Toast.LENGTH_LONG).show();
        }


    }

    /**
     * 信息查询方法
     *
     * @param
     */
    public void identify(float x, float y, int layerIndex) {

//
//        Layer[] alllayers = this.mMap.getLayers();
////        if(alllayers[layerIndex].getName().equals("dx")){
////            Toast.makeText(mActivity, "对不起，地形图无任何信息", Toast.LENGTH_LONG).show();
////        }
//        Point pt = mMap.toMapPoint(new Point(x, y));
//
//        if (alllayers[layerIndex] instanceof ArcGISFeatureLayer) {
//            ArcGISFeatureLayer flyr = (ArcGISFeatureLayer) alllayers[layerIndex];
//            int[] graIds = flyr.getGraphicIDs(x, y,
//                    GisQueryApplication.NEAREST_TOLERANCE);
//            if (graIds.length > 0) {
//                showIdentifyInfo_Graphic(flyr, graIds);
//                return;
//            } else {
//                identify(x, y, layerIndex - 1);
//                return;
//            }
//        } else if (alllayers[layerIndex] instanceof FeatureLayer) {
//            FeatureLayer flyr = (FeatureLayer) alllayers[layerIndex];
//            long[] feaIds = flyr.getFeatureIDs(x, y,
//                    GisQueryApplication.NEAREST_TOLERANCE);
//            if (feaIds.length > 0) {
//                showIdentifyInfo_Feature(flyr, feaIds);
//                return;
//            } else {
//                identify(x, y, layerIndex - 1);
//                return;
//            }
//        }




        Layer[] alllayers = this.mMap.getLayers();
        Point pt = mMap.toMapPoint(new Point(x, y));
        for (int i = layerIndex; i >= 0; i--) {
            if (alllayers[i] instanceof ArcGISFeatureLayer) {
                ArcGISFeatureLayer flyr = (ArcGISFeatureLayer) alllayers[i];
                int[] graIds = flyr.getGraphicIDs(x, y,
                        GisQueryApplication.NEAREST_TOLERANCE);
                if (graIds.length > 0) {
                    showIdentifyInfo_Graphic(flyr, graIds);
                    break;
                } else {
                    identify(x, y, i - 1);
                    break;
                }
            } else if (alllayers[i] instanceof FeatureLayer) {
                FeatureLayer flyr = (FeatureLayer) alllayers[i];
                long[] feaIds = flyr.getFeatureIDs(x, y,
                        GisQueryApplication.NEAREST_TOLERANCE);
                if (feaIds.length > 0) {
                    showIdentifyInfo_Feature(flyr, feaIds);
                    break;
                } else {
                    identify(x, y, i - 1);
                    break;
                }
            }
        }

    }

    private void clearSelection() {
        if (mLayer != null) {
            mLayer.removeAll();
        }
        if (curSelectedLayer != null) {
            if (curSelectedLayer instanceof ArcGISFeatureLayer) {
                ((ArcGISFeatureLayer) curSelectedLayer).clearSelection();
            } else if (curSelectedLayer instanceof FeatureLayer) {
                ((FeatureLayer) curSelectedLayer).clearSelection();
            }
        }
    }

    private void showIdentifyInfo_Graphic(ArcGISFeatureLayer flyr, int[] graIds) {
        Graphic firstGra = flyr.getGraphic(graIds[0]);
        layerName = flyr.getName();
        clearSelection();
        curSelectedLayer = flyr;
        // 高亮选中图形
        Geometry curGeo = firstGra.getGeometry();
        favgeometry = "";
        favgeometry = GeometryEngine.geometryToJson(
                MapOperate.map.getSpatialReference(), curGeo);
        favsymbol = null;
        try {
            favsymbol = firstGra.getSymbol().toJson();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (curGeo instanceof Polygon) {
            mLayer.addGraphic(new Graphic(firstGra.getGeometry(), mFillSymbol));
        } else if (curGeo instanceof Polyline) {
            mLayer.addGraphic(new Graphic(firstGra.getGeometry(), mLineSymbol));
        } else {
            mLayer.addGraphic(new Graphic(firstGra.getGeometry(), mMarkerSymbol));
        }
        // 展示图形属性
        this.mInfoShowNames.clear();
        this.mInfoShowValues.clear();
        Map<String, Object> attr = firstGra.getAttributes();
        String[] attrnames = firstGra.getAttributeNames();
        favattributes = "";
        LayerConfig lc = mApp.getProjectconfig()
                .getLayerConfigByLayerName(layerName);
        String[] mOriginalfields = lc.getOriginalfields();
        String[] mDisplayfields = lc.getDisplayfields();
        if (mOriginalfields != null && mOriginalfields.length > 0
                && mOriginalfields.length == mDisplayfields.length) {
            JSONObject jsonObject = lc.getFieldDict();
            for (int j = 0; j < mOriginalfields.length; j++) {
                this.mInfoShowNames.add(mDisplayfields[j]);
                if (null == jsonObject) {
                    favattributes += mDisplayfields[j] + "="
                            + attr.get(mOriginalfields[j]) + ";";
                    this.mInfoShowValues.add("" + attr.get(mOriginalfields[j]));
                    Log.d("图形属性",
                            mDisplayfields[j] + "--->"
                                    + attr.get(mOriginalfields[j]));
                } else {
                    Object value = attr.get(mOriginalfields[j]);
                    String fieldName = mOriginalfields[j];
                    try {
                        JSONObject itemJson = jsonObject.getJSONObject(fieldName);
                        String dictValue = itemJson.getString("" + value);
                        if (StringUtils.isEmpty(dictValue)) {
                            favattributes += mDisplayfields[j] + "="
                                    + attr.get(mOriginalfields[j]) + ";";
                            this.mInfoShowValues.add("" + attr.get(mOriginalfields[j]));
                            Log.d("图形属性",
                                    mDisplayfields[j] + "--->"
                                            + attr.get(mOriginalfields[j]));
                        } else {
                            favattributes += mDisplayfields[j] + "="
                                    + dictValue + ";";
                            this.mInfoShowValues.add("" + dictValue);
                            Log.d("图形属性",
                                    mDisplayfields[j] + "--->"
                                            + attr.get(mOriginalfields[j]) + "对应字典：" + dictValue);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        favattributes += mDisplayfields[j] + "="
                                + attr.get(mOriginalfields[j]) + ";";
                        this.mInfoShowValues.add("" + attr.get(mOriginalfields[j]));
                        Log.d("图形属性",
                                mDisplayfields[j] + "--->"
                                        + attr.get(mOriginalfields[j]));
                    }
                }


            }
        } else {
            for (int j = 0; j < attrnames.length; j++) {
                if (attrnames[j].indexOf("OBJECTID") != -1) {
                    this.mInfoShowNames.add("对象标识");
                } else {
                    this.mInfoShowNames.add(attrnames[j]);
                }
                this.mInfoShowValues.add("" + attr.get(attrnames[j]));
                favattributes += attrnames[j] + "=" + attr.get(attrnames[j])
                        + ";";
                Log.d("图形属性", attrnames[j] + "--->" + attr.get(attrnames[j]));
            }
        }

        this.mActivity.findViewById(R.id.leftpanel).setVisibility(
                View.INVISIBLE);
        this.mActivity.findViewById(R.id.vg_result_control_bar).setVisibility(
                View.GONE);
        this.mActivity.findViewById(R.id.btn_add_to_fav).setVisibility(
                View.VISIBLE);
        mInfoShowGroup.setVisibility(View.VISIBLE);
        this.mActivity.findViewById(R.id.txt_title).setVisibility(View.VISIBLE);

        ArrayList<LayerConfig> al_basemaps = new ArrayList<>();
        al_basemaps.addAll(GisQueryApplication.getApp().getProjectconfig()
                .getBasemaps());
        al_basemaps.addAll(GisQueryApplication.getApp().getProjectconfig()
                .getOperationallayers());
        Map<String, String> nameMap = new HashMap();
        for (LayerConfig l : al_basemaps) {
            nameMap.put(l.getLabel(), l.getName());
        }

        ((TextView) this.mActivity.findViewById(R.id.txt_title)).setText(nameMap.get(flyr
                .getName()));
        for (int i = 0; i < mInfoShowValues.size(); i++) {
            if(mInfoShowValues.get(i)==null||mInfoShowValues.get(i).equals("null")){
                mInfoShowValues.set(i,"");

            }
        }


        mInfoShowListView.setAdapter(new InfoAdapter(this.mActivity,
                this.mInfoShowNames, this.mInfoShowValues));
    }

    private void showIdentifyInfo_Feature(FeatureLayer flyr, long[] curfeaIds) {
        Feature firstFea = flyr.getFeature(curfeaIds[0]);

        layerName = flyr.getName();
        clearSelection();
        flyr.selectFeature(curfeaIds[0]);
        curSelectedLayer = flyr;

        // 展示图形属性
        mInfoShowNames.clear();
        mInfoShowValues.clear();
        Map<String, Object> attr = firstFea.getAttributes();
        // String[] attrnames =
        // firstFea.getAttributeNames();
        favattributes = "";
        LayerConfig lc = mApp.getProjectconfig()
                .getLayerConfigByLayerName(layerName);
        String[] mOriginalfields = lc.getOriginalfields();
        String[] mDisplayfields = lc.getDisplayfields();

        if (mOriginalfields != null && mOriginalfields.length > 0
                && mOriginalfields.length == mDisplayfields.length) {

            JSONObject jsonObject = lc.getFieldDict();

            for (int j = 0; j < mOriginalfields.length; j++) {
                /*mInfoShowNames.add(mDisplayfields[j]);
				mInfoShowValues.add("" + attr.get(mOriginalfields[j]));
				favattributes += mDisplayfields[j] + "="
						+ attr.get(mOriginalfields[j]) + ";";
				Log.d("图形属性",
						mDisplayfields[j] + "--->"
								+ attr.get(mOriginalfields[j]));*/


                this.mInfoShowNames.add(mDisplayfields[j]);
                if (null == jsonObject) {
                    favattributes += mDisplayfields[j] + "="
                            + attr.get(mOriginalfields[j]) + ";";
                    this.mInfoShowValues.add("" + attr.get(mOriginalfields[j]));
                    Log.d("图形属性",
                            mDisplayfields[j] + "--->"
                                    + attr.get(mOriginalfields[j]));
                } else {
                    Object value = attr.get(mOriginalfields[j]);
                    String fieldName = mOriginalfields[j];
                    try {
                        JSONObject itemJson = jsonObject.getJSONObject(fieldName);
                        String dictValue = itemJson.getString("" + value);
                        if (StringUtils.isEmpty(dictValue)) {
                            favattributes += mDisplayfields[j] + "="
                                    + attr.get(mOriginalfields[j]) + ";";
                            this.mInfoShowValues.add("" + attr.get(mOriginalfields[j]));
                            Log.d("图形属性",
                                    mDisplayfields[j] + "--->"
                                            + attr.get(mOriginalfields[j]));
                        } else {
                            favattributes += mDisplayfields[j] + "="
                                    + dictValue + ";";
                            this.mInfoShowValues.add("" + dictValue);
                            Log.d("图形属性",
                                    mDisplayfields[j] + "--->"
                                            + attr.get(mOriginalfields[j]) + "对应字典：" + dictValue);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        favattributes += mDisplayfields[j] + "="
                                + attr.get(mOriginalfields[j]) + ";";
                        this.mInfoShowValues.add("" + attr.get(mOriginalfields[j]));
                        Log.d("图形属性",
                                mDisplayfields[j] + "--->"
                                        + attr.get(mOriginalfields[j]));
                    }
                }


            }
        } else {
            Iterator<String> it = attr.keySet().iterator();
            while (it.hasNext()) {
                String key;
                String value;
                key = it.next().toString();
                value = "" + attr.get(key);
                if (key.indexOf("OBJECTID") != -1) {
                    mInfoShowNames.add("对象标识");
                } else {
                    mInfoShowNames.add(key);
                }
                mInfoShowValues.add(value);
                favattributes += key + "=" + value + ";";
                Log.d("图形属性", key + "--->" + value);
            }

        }

        mActivity.findViewById(R.id.leftpanel).setVisibility(View.INVISIBLE);
        mActivity.findViewById(R.id.vg_result_control_bar).setVisibility(
                View.GONE);
        mActivity.findViewById(R.id.btn_add_to_fav).setVisibility(View.VISIBLE);
        mInfoShowGroup.setVisibility(View.VISIBLE);
        mActivity.findViewById(R.id.txt_title).setVisibility(View.VISIBLE);
        ArrayList<LayerConfig> al_basemaps = new ArrayList<>();
        al_basemaps.addAll(GisQueryApplication.getApp().getProjectconfig()
                .getBasemaps());
        al_basemaps.addAll(GisQueryApplication.getApp().getProjectconfig()
                .getOperationallayers());
        Map<String, String> nameMap = new HashMap();
        for (LayerConfig l : al_basemaps) {
            nameMap.put(l.getLabel(), l.getName());
        }

        ((TextView) mActivity.findViewById(R.id.txt_title)).setText(nameMap.get(flyr
                .getName()));
        for (int i = 0; i < mInfoShowValues.size(); i++) {
            if(mInfoShowValues.get(i)==null||mInfoShowValues.get(i).equals("null")){
                mInfoShowValues.set(i,"");

            }
        }
        mInfoShowListView.setAdapter(new InfoAdapter(mActivity, mInfoShowNames,
                mInfoShowValues));

        Geometry curGeo = firstFea.getGeometry();
        favgeometry = "";
        favgeometry = GeometryEngine.geometryToJson(
                MapOperate.map.getSpatialReference(), curGeo);
        favsymbol = null;
        try {
            favsymbol = firstFea.getSymbol().toJson();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
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
     * @param linearUnits Array of Unit for measurement of dimensions
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
     * @param areaUnits Array of Unit for measurement of dimensions
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
     * @param symbol To draw lines on GraphicsLayer
     */
    public void setLineSymbol(LineSymbol symbol) {
        mLineSymbol = symbol;
    }

    /**
     * Customize the marker symbol
     *
     * @param symbol To draw marker symbol on GraphicsLayer
     */
    public void setMarkerSymbol(MarkerSymbol symbol) {
        mMarkerSymbol = symbol;
    }

    /**
     * Customize the fill symbol
     *
     * @param symbol To draw fill symbol on GraphicsLayer
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

    public void showAddFavDlg() {
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
                    }
                }).create().show();
    }

    public static void MsgBox(Context paramContext, String paramString) {
        Toast.makeText(paramContext, paramString, Toast.LENGTH_LONG).show();
    }

}
