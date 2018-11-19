package com.guyu.android.gis.opt;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.ToastUtils;
import com.guyu.android.R;
import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.MapView;
import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.LinearUnit;
import com.esri.core.geometry.MapGeometry;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polygon;
import com.esri.core.geometry.Unit;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.SimpleFillSymbol;
import com.esri.core.symbol.SimpleLineSymbol;
import com.esri.core.symbol.SimpleMarkerSymbol;
import com.esri.core.symbol.SimpleMarkerSymbol.STYLE;
import com.esri.core.symbol.Symbol;
import com.esri.core.symbol.SymbolFactory;
import com.esri.core.symbol.SymbolHelper;
import com.esri.core.symbol.TextSymbol;
import com.guyu.android.database.SdCardDBHelper;
import com.guyu.android.gis.adapter.InfoAdapter;
import com.guyu.android.gis.app.GisQueryApplication;
import com.guyu.android.gis.common.FavEntity;
import com.guyu.android.gis.config.LayerConfig;
import com.guyu.android.gis.maptools.FavTool;
import com.guyu.android.utils.PolygonRecCenter;
import com.guyu.android.utils.SysConfig;
import com.guyu.android.utils.UtilsTools;

public class FavViewOpt implements OnClickListener, OnItemClickListener {

	private MapView mMap;
	private Activity mActivity;
	private GraphicsLayer mLayer;
	private ViewGroup mViewGroup = null;
	private ListView mListView = null;
	private Dialog mDialog;
	private Context mContext;
	private LayoutInflater mInflater;
	private List<FavEntity> mlstFavEntities = new ArrayList();
	public List<Boolean> lstSelected = new ArrayList();

	private int listState = 1;

	private Map<Integer, Boolean> isCheckMap = new HashMap<Integer, Boolean>();

	private MAdapter adapter;

	private List<String> strDel = new ArrayList<String>();

	private ViewGroup mInfoShowGroup = null;
	private ListView mInfoShowListView = null;
	private Unit[] mDefaultAreaUnits;
	private Unit[] mDefaultLinearUnits;

	private List<String> mInfoShowNames = new ArrayList();
	private List<String> mInfoShowValues = new ArrayList();
	
	private LinearLayout mVgFavToolbar = null;
	private FavTool favTool;
	
	public FavViewOpt(MapView map, Activity activity) {
		this.mMap = map;
		this.mActivity = activity;
		mContext = mMap.getContext();
		this.mInfoShowGroup = ((ViewGroup) mActivity
				.findViewById(R.id.vg_query_result));
		this.mInfoShowListView = ((ListView) mActivity
				.findViewById(R.id.lst_info_show));
		this.mVgFavToolbar = ((LinearLayout) mActivity.findViewById(R.id.vg_fav_toolbar));
		
		this.mActivity.findViewById(R.id.imgFav).setOnClickListener(
				new View.OnClickListener() {
					public void onClick(View paramView) {
						FavViewOpt.this.initDlgView();
					}
				});
	}

	protected void initDlgView() {
		// TODO Auto-generated method stub
		this.mViewGroup = ((ViewGroup) ((LayoutInflater) this.mActivity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
				R.layout.layout_layer_fav, null));
		initData();
		initControlView();
		this.mListView = ((ListView) this.mViewGroup.findViewById(R.id.lst_fav));
		adapter = new MAdapter(this.mContext);
		mListView.setAdapter(adapter);
		this.mListView.setOnItemClickListener(this);
		this.mDialog = new AlertDialog.Builder(this.mActivity)
				.setTitle(
						"收藏夹" + "(" + SysConfig.mHumanInfo.getHumanName() + ")")
				.setView(mViewGroup).create();
		this.mDialog.setCancelable(false);
		this.mDialog.show();
	}

	private void initControlView() {
		this.mViewGroup.findViewById(R.id.btn_draw).setOnClickListener(this);
		this.mViewGroup.findViewById(R.id.btn_edit).setOnClickListener(this);
		this.mViewGroup.findViewById(R.id.btn_delete).setOnClickListener(this);
		this.mViewGroup.findViewById(R.id.btn_select_all).setOnClickListener(
				this);
		this.mViewGroup.findViewById(R.id.btn_un_select_all)
				.setOnClickListener(this);
		this.mViewGroup.findViewById(R.id.btn_select_changed)
				.setOnClickListener(this);
		this.mViewGroup.findViewById(R.id.btn_okr).setOnClickListener(this);

	}

	@SuppressWarnings("unchecked")
	public void initData() {
		String[] columns = new String[] { "_id", "title", "layerName",
				"remark", "favgeometry", "favattributes", "time", "favsymbol" };
		String selection = "peoid=?";
		String[] selectionArgs = new String[] { ""
				+ SysConfig.mHumanInfo.getHumanId() + "" };
		Cursor localCursor = SdCardDBHelper.getInstance(mActivity).query(
				"tbFav", columns, selection, selectionArgs, null, null,
				"time desc");

		ArrayList localArrayList = new ArrayList();
		if ((localCursor != null) && (localCursor.getCount() > 0)) {
			ArrayList<LayerConfig> al_basemaps = new ArrayList<>();
			al_basemaps.addAll(GisQueryApplication.getApp().getProjectconfig()
					.getBasemaps());
			al_basemaps.addAll(GisQueryApplication.getApp().getProjectconfig()
					.getOperationallayers());
			Map<String, String> nameMap = new HashMap();
			for (LayerConfig l : al_basemaps) {
				nameMap.put(l.getLabel(), l.getName());
			}


			localCursor.moveToFirst();
			do
				localArrayList.add(new FavEntity(localCursor.getInt(localCursor
						.getColumnIndex("_id")), localCursor
						.getString(localCursor.getColumnIndex("title")),
						nameMap.get(localCursor.getString(localCursor.getColumnIndex("layerName")))
						,
						localCursor.getString(localCursor
								.getColumnIndex("remark")), localCursor
								.getString(localCursor
										.getColumnIndex("favgeometry")),
						localCursor.getString(localCursor
								.getColumnIndex("favattributes")), 
						localCursor.getLong(localCursor.getColumnIndex("time")),
						localCursor.getString(localCursor
										.getColumnIndex("favsymbol"))));
			while (localCursor.moveToNext());
			if ((localArrayList != null) && (localArrayList.size() > 0)) {
				if (this.mlstFavEntities != null) {
					this.mlstFavEntities.clear();
					this.lstSelected.clear();
				}
				this.mlstFavEntities.addAll(localArrayList);
			}
			for (int i = 0;; ++i) {
				if (i >= this.mlstFavEntities.size())
					return;
				this.lstSelected.add(Boolean.valueOf(false));
			}

		} else {
			this.mlstFavEntities.clear();
		}
	}

	@SuppressWarnings("unchecked")
	public void onItemClick(AdapterView<?> listView, View itemLayout,
			final int position, long id) {
		if (FavViewOpt.this.listState == 1) {
			TextView txt_id1 = (TextView) itemLayout.findViewById(R.id.txt_id);
			String[] columns1 = new String[] { "_id", "layerName",
					"favgeometry", "favattributes", "remark", "favsymbol" };
			String selection1 = "_id=?";
			String[] selectionArgs1 = new String[] { txt_id1.getText()
					.toString() };
			Cursor localCursor1 = SdCardDBHelper.getInstance(mActivity).query(
					"tbFav", columns1, selection1, selectionArgs1, null, null,
					null);
			ArrayList localArrayList = new ArrayList();

			if ((localCursor1 != null) && (localCursor1.getCount() > 0)) {

				ArrayList<LayerConfig> al_basemaps = new ArrayList<>();
				al_basemaps.addAll(GisQueryApplication.getApp().getProjectconfig()
						.getBasemaps());
				al_basemaps.addAll(GisQueryApplication.getApp().getProjectconfig()
						.getOperationallayers());
				Map<String, String> nameMap = new HashMap();
				for (LayerConfig l : al_basemaps) {
					nameMap.put(l.getLabel(), l.getName());
				}

				localCursor1.moveToFirst();
				do
					localArrayList.add(new FavEntity(
							nameMap.get(localCursor1.getString(localCursor1.getColumnIndex("layerName")))
							, localCursor1
									.getString(localCursor1
											.getColumnIndex("favgeometry")),
							localCursor1.getString(localCursor1
									.getColumnIndex("favattributes")),
							localCursor1.getString(localCursor1
									.getColumnIndex("remark")),
							localCursor1.getString(localCursor1
									.getColumnIndex("favsymbol"))));
				while (localCursor1.moveToNext());
			}
			localCursor1.close();
			if ((localArrayList != null) && (localArrayList.size() > 0)) {
				this.mlstFavEntities.clear();
				this.mlstFavEntities.addAll(localArrayList);
			}
			String favgeometry = String
					.valueOf(FavViewOpt.this.mlstFavEntities
							.get(0).getFavgeometry());
			String favattributes = String
					.valueOf(FavViewOpt.this.mlstFavEntities
							.get(0).getFavattributes());
			String layerName = String
					.valueOf(FavViewOpt.this.mlstFavEntities
							.get(0).getLayerName());
			String remark = String
					.valueOf(FavViewOpt.this.mlstFavEntities
							.get(0).getRemark());
			String favsymbol = String
					.valueOf(FavViewOpt.this.mlstFavEntities
							.get(0).getFavsymbol());
			this.mDialog.dismiss();
			identify(layerName, favgeometry, favattributes, remark, favsymbol);
		} else {
			List localList = FavViewOpt.this.lstSelected;
			boolean bool1 = FavViewOpt.this.lstSelected
					.get(position).booleanValue();
			boolean bool2 = false;
			if (bool1) {
				localList.set(position, Boolean.valueOf(bool2));
			} else {
				localList.set(position, Boolean.valueOf(true));
			}
			CheckBox localCheckBox = (CheckBox) itemLayout
					.findViewById(R.id.cb_selected);
			localCheckBox.setChecked(lstSelected.get(position));
			TextView txt_id = (TextView) itemLayout.findViewById(R.id.txt_id);
			strDel.clear();
			for (int i = 0; i < lstSelected.size(); i++) {
				if (lstSelected.get(i).booleanValue() == true) {
					strDel.add(String
							.valueOf(FavViewOpt.this.mlstFavEntities
									.get(i).get_id()));
				}
			}
		}

	}

	private void initFavTool() {
		SimpleFillSymbol fillSymbol;
		SimpleMarkerSymbol markerSymbol = new SimpleMarkerSymbol(Color.BLUE, 10, STYLE.DIAMOND);
		SimpleLineSymbol lineSymbol = new SimpleLineSymbol(Color.YELLOW, 3);
		fillSymbol = new SimpleFillSymbol(Color.argb(100, 0, 225, 255));
		fillSymbol.setOutline(new SimpleLineSymbol(Color.TRANSPARENT, 0));

		favTool = new FavTool(this.mMap, MapOperate.mActivity);
		Unit[] linearUnits = new Unit[] { Unit.create(LinearUnit.Code.METER) };
		favTool.setLinearUnits(linearUnits);
		favTool.setMarkerSymbol(markerSymbol);
		favTool.setLineSymbol(lineSymbol);
		favTool.setFillSymbol(fillSymbol);
	}
	
	@Override
	public void onClick(View paramView) {
		// TODO Auto-generated method stub
		switch (paramView.getId()) {
		default:
			this.listState = 1;
			return;
		case R.id.btn_draw:
			this.listState = 1;
			if(listState==1){
				this.mDialog.dismiss();				
				initFavTool();
				this.favTool.dispose();
				MapOperate.curGeometry = null;
				favTool.init();
				mVgFavToolbar.setVisibility(View.VISIBLE);
			}
			return;
		case R.id.btn_edit:
			if (this.mlstFavEntities.size() > 0) {
				this.listState = 0;
				this.mViewGroup.findViewById(R.id.btn_edit).setVisibility(
						View.GONE);
				this.mViewGroup.findViewById(R.id.btn_delete).setVisibility(
						View.VISIBLE);
				LayoutInflater inflater = (LayoutInflater) this.mActivity
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				View localView = inflater.inflate(R.layout.list_fav_item, null);
				CheckBox localCheckBox = (CheckBox) localView
						.findViewById(R.id.cb_selected);
				localCheckBox.setVisibility(View.VISIBLE);
				this.mListView.invalidateViews();
			}
			return;
		case R.id.btn_delete:
			if (strDel.size() > 0) {
				String[] toBeStored = strDel.toArray(new String[strDel.size()]);
				for (int i = 0; i < toBeStored.length; i++) {
					String strA = toBeStored[i].toString();
					String[] a = { "0" };
					a[0] = strA;
					int s = SdCardDBHelper.getInstance(mActivity).delete(
							"tbfav", "_id=?", a);
				}
				this.listState = 1;
				this.mDialog.dismiss();
				initDlgView();
			} else {
				this.listState = 1;
				this.mDialog.dismiss();
				initDlgView();
			}
			return;
		case R.id.btn_select_all:
			return;
		case R.id.btn_un_select_all:
			return;
		case R.id.btn_select_changed:
			return;
		case R.id.btn_okr:
			this.listState = 1;
			this.mDialog.dismiss();
		}

	}

	public static void MsgBox(Context paramContext, String paramString) {
		ToastUtils.showLong(paramString);
	}

	public class MAdapter extends BaseAdapter {
		private LayoutInflater mInflater = null;

		public MAdapter(Context paramContext) {
			configCheckMap(false);
			this.mInflater = ((LayoutInflater) paramContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE));
		}

		public void configCheckMap(boolean bool) {

			for (int i = 0; i < mlstFavEntities.size(); i++) {
				isCheckMap.put(mlstFavEntities.get(i).get_id(), bool);
			}

		}

		public int getCount() {
			return FavViewOpt.this.mlstFavEntities.size();
		}

		public Object getItem(int paramInt) {
			return FavViewOpt.this.mlstFavEntities.get(paramInt);
		}

		public long getItemId(int paramInt) {
			return paramInt;
		}

		public View getView(final int paramInt, View paramView,
				ViewGroup paramViewGroup) {
			if (paramView == null)
				paramView = this.mInflater.inflate(R.layout.list_fav_item,
						paramViewGroup, false);
			TextView localTextView1 = (TextView) paramView
					.findViewById(R.id.txt_left_top);
			TextView localTextView2 = (TextView) paramView
					.findViewById(R.id.txt_left_bottom_1);
			TextView localTextView3 = (TextView) paramView
					.findViewById(R.id.txt_left_bottom_3);
			TextView txt_id = (TextView) paramView.findViewById(R.id.txt_id);

			localTextView1.setText(FavViewOpt.this.mlstFavEntities
					.get(paramInt).getTitle());
			localTextView2.setText(FavViewOpt.this.mlstFavEntities
					.get(paramInt).getRemark());
			localTextView3.setText(UtilsTools.GetCurrentFormatTime(Long
					.valueOf(FavViewOpt.this.mlstFavEntities
							.get(paramInt).getTime()),
					UtilsTools.DEF_DATE_FORMAT));
			txt_id.setText(String
					.valueOf(FavViewOpt.this.mlstFavEntities
							.get(paramInt).get_id()));
			CheckBox localCheckBox = (CheckBox) paramView
					.findViewById(R.id.cb_selected);
			if (FavViewOpt.this.listState == 0) {
				if (localCheckBox.getVisibility() != View.VISIBLE)
					localCheckBox.setVisibility(View.VISIBLE);
				return paramView;
			}
			if (localCheckBox.getVisibility() != View.GONE) {
				localCheckBox.setVisibility(View.GONE);
				localCheckBox.setChecked(FavViewOpt.this.lstSelected
						.get(paramInt).booleanValue());
			}
			return paramView;

		}
	}

	private void identify(String layerName, String favgeometry,
			String favattributes, String remark, String favsymbol) {
		JsonFactory jsonFactory = new JsonFactory();
		JsonParser jsonParser;
		try {
			jsonParser = jsonFactory.createJsonParser(favgeometry);
			MapGeometry mapGeo = GeometryEngine.jsonToGeometry(jsonParser);
			Geometry curGeo = mapGeo.getGeometry();
			if (mapGeo.getSpatialReference().getID()==mMap.getSpatialReference().getID()) {
				mLayer = MapOperate.getFavShowLayer();
				mLayer.removeAll();
				SymbolFactory symbolFactory = null;
				JsonParser jsonParserSysmbol = jsonFactory
						.createJsonParser(favsymbol);
				Symbol symbol = SymbolHelper.createSymbol(jsonParserSysmbol);
				mLayer.addGraphic(new Graphic(curGeo, symbol));
				mMap.addLayer(mLayer);
				Polygon polygon = GeometryEngine.buffer(curGeo, mMap.getSpatialReference(), 0, null);
				Point point = PolygonRecCenter.getPoint(polygon);				
				TextSymbol ts = new TextSymbol (18,remark,Color.RED);
				ts.setFontFamily("DroidSansFallback.ttf");
				Graphic gp = new Graphic(point,ts);
				mLayer.addGraphic(gp);
				mMap.setExtent(mapGeo.getGeometry());
				this.mInfoShowNames.clear();
				this.mInfoShowValues.clear();
				String[] favattributesArray = favattributes.split(";");
				this.mActivity.findViewById(R.id.leftpanel).setVisibility(
						View.GONE);
				this.mActivity.findViewById(R.id.vg_result_control_bar)
				.setVisibility(View.GONE);
				this.mActivity.findViewById(R.id.btn_add_to_fav).setVisibility(
				View.GONE);
				mInfoShowGroup.setVisibility(View.GONE);
				this.mActivity.findViewById(R.id.txt_title).setVisibility(View.GONE);
				if(favattributesArray.length>1){
					for (int i = 0; i < favattributesArray.length; i++) {
						String[] favatt = null;
						favatt = favattributesArray[i].split("=");
						if (favatt[0].indexOf("OBJECTID") != -1) {
							this.mInfoShowNames.add("对象标识");
						} else {
							this.mInfoShowNames.add(favatt[0].toString());
						}
						this.mInfoShowValues.add(favatt[1].toString());
					}
					this.mActivity.findViewById(R.id.leftpanel).setVisibility(
							View.INVISIBLE);
					this.mActivity.findViewById(R.id.vg_result_control_bar)
							.setVisibility(View.GONE);
					this.mActivity.findViewById(R.id.btn_add_to_fav).setVisibility(
							View.GONE);
					mInfoShowGroup.setVisibility(View.VISIBLE);
					this.mActivity.findViewById(R.id.txt_title).setVisibility(
							View.VISIBLE);
					((TextView) this.mActivity.findViewById(R.id.txt_title))
							.setText(layerName);
					for (int i = 0; i < mInfoShowValues.size(); i++) {
						if(mInfoShowValues.get(i)==null||mInfoShowValues.get(i).equals("null")){
							mInfoShowValues.set(i,"");

						}
					}

					mInfoShowListView.setAdapter(new InfoAdapter(this.mActivity,
							this.mInfoShowNames, this.mInfoShowValues));					
				}				
			} else {
				MsgBox(this.mContext, "空间坐标系不一致");
			}
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}