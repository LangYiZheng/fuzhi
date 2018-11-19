package com.guyu.android.gis.opt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.blankj.utilcode.util.ToastUtils;
import com.esri.android.map.FeatureLayer;
import com.esri.android.map.Layer;
import com.esri.android.map.ags.ArcGISFeatureLayer;
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polygon;
import com.esri.core.geometry.Polyline;
import com.esri.core.map.CallbackListener;
import com.esri.core.map.Feature;
import com.esri.core.map.FeatureResult;
import com.esri.core.map.FeatureSet;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.SimpleFillSymbol;
import com.esri.core.symbol.SimpleLineSymbol;
import com.esri.core.symbol.SimpleMarkerSymbol;
import com.esri.core.symbol.SimpleMarkerSymbol.STYLE;
import com.esri.core.table.FeatureTable;
import com.esri.core.tasks.SpatialRelationship;
import com.esri.core.tasks.ags.query.Query;
import com.esri.core.tasks.query.QueryParameters;
import com.guyu.android.database.SdCardDBHelper;
import com.guyu.android.gis.activity.MainActivity;
import com.guyu.android.gis.app.GisQueryApplication;
import com.guyu.android.gis.common.CallBack;
import com.guyu.android.gis.common.Record;
import com.guyu.android.gis.config.LayerConfig;
import com.guyu.android.utils.MapUtils;
import com.guyu.android.utils.SysConfig;
import com.guyu.android.utils.UtilsTools;
import com.guyu.android.view.QueryLayerSettingOpt;
import com.guyu.android.R;

public class FullTextQueryOpt {
	private  MainActivity mainActivity;
	private ProgressDialog dialog = null;
	private View mGroupView = null;
	private EditText queryEditText = null;
	private ImageButton mQuerySettingButton = null;
	private Button mQueryButton = null;
	private String[] queryLayerAry;
	private int callbacknum = 0;
	public ExpandableListView mListView = null;
	public QueryReusltAdapter mQueryResultAdapter;
	public static List<String> lstQueryResultLayerName = new ArrayList<String>();
	public static HashMap<String, List<Record>> lstQueryResultMap = new HashMap<String, List<Record>>();
	public Button mBackButton = null;
	public Button mNextButton = null;
	public Button mPreButton = null;
	public Button mInfoButton = null;
	public Button mAddToFavButton = null;
	public ListView mChildListView = null;
	public ViewGroup mGroupChildDetails = null;
	private List<String> lstFieldNames = new ArrayList<String>();
	private List<String> lstFieldValues = new ArrayList<String>();
	private boolean init = false;

	private EditText localEditText1;
	private EditText localEditText2;
	private String layerName;
	private String favgeometry;
	private String favattributes;
	private String favsymbol;
	private Context mContext;

	public FullTextQueryOpt(View view,  MainActivity mainActivity) {
		this.mainActivity = mainActivity;
		mGroupView = view;
		this.mContext = mGroupView.getContext();
	}

	public void initQuery() {
		if (init)
			return;
		this.queryEditText = (EditText) mGroupView.findViewById(R.id.et_query);
		this.mQuerySettingButton = (ImageButton) mGroupView.findViewById(R.id.btn_query_setting);
		mQuerySettingButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View paramView) {
				showQuerySettingOpt();
			}
		});
		this.mQueryButton = (Button) mGroupView.findViewById(R.id.btn_start_query);
		mQueryButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View paramView) {
				doFullTextQuery("" + queryEditText.getText());
			}
		});
		this.mListView = ((ExpandableListView) this.mGroupView.findViewById(R.id.lst_datas_full_text));
		this.mQueryResultAdapter = new QueryReusltAdapter(this.mGroupView.getContext());
		this.mListView.setAdapter(this.mQueryResultAdapter);
		this.mListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
			public boolean onChildClick(ExpandableListView paramExpandableListView, View paramView, int paramInt1, int paramInt2, long paramLong) {
				FullTextQueryOpt.this.onChlickChildData(paramInt1, paramInt2);
				return true;
			}
		});
		this.mGroupChildDetails = ((ViewGroup) this.mGroupView.findViewById(R.id.vg_lst_show_details));
		this.mChildListView = ((ListView) this.mGroupChildDetails.findViewById(R.id.lst_datas_child_details));
		this.mChildListView.setAdapter(new ChildDetailsAdapter(this.mGroupView.getContext()));
		this.mPreButton = ((Button) this.mGroupChildDetails.findViewById(R.id.btn_pre));
		this.mInfoButton = ((Button) this.mGroupChildDetails.findViewById(R.id.btn_info));
		this.mNextButton = ((Button) this.mGroupChildDetails.findViewById(R.id.btn_next));
		this.mBackButton = ((Button) this.mGroupChildDetails.findViewById(R.id.btn_go_back_to_group));
		this.mAddToFavButton = ((Button) this.mGroupChildDetails.findViewById(R.id.btn_add_to_fav2));

		View.OnClickListener onClickListener = new View.OnClickListener() {
			@Override
			public void onClick(View paramView) {
				switch (paramView.getId()) {
				case R.id.btn_pre:
					preOpt();
					break;
				case R.id.btn_next:
					nextOpt();
					break;
				case R.id.btn_go_back_to_group:
					backToResultList();
					FullTextQueryOpt.this.mListView.invalidateViews();
					MapOperate.clearPublicGraphicsLayer();
					break;
				case R.id.btn_add_to_fav2:
					showAddFavDlg();
					break;
				default:
					break;
				}

			}
		};
		this.mPreButton.setOnClickListener(onClickListener);
		this.mInfoButton.setOnClickListener(onClickListener);
		this.mNextButton.setOnClickListener(onClickListener);
		this.mBackButton.setOnClickListener(onClickListener);
		this.mAddToFavButton.setOnClickListener(onClickListener);
		init = true;

	}

	protected void onPreExecute() {
		this.dialog = new ProgressDialog(mainActivity);
		this.dialog.setMessage("正在查询...");
		this.dialog.setCancelable(false);
		this.dialog.show();
	}

	private void preOpt() {
		String str = (String) this.mPreButton.getTag();
		int i = -1 + ((Integer) this.mNextButton.getTag()).intValue();
		Record localRecord = FullTextQueryOpt.lstQueryResultMap.get(str).get(i);
		refreshChildView(str, FullTextQueryOpt.lstQueryResultMap.get(str).size(), i, localRecord);
		this.mChildListView.invalidateViews();
		MapOperate.addAndZoomToGeometry(localRecord.getGeometry(), false);
	}

	private void nextOpt() {
		String str = (String) this.mPreButton.getTag();
		int i = 1 + ((Integer) this.mNextButton.getTag()).intValue();
		Record localRecord = FullTextQueryOpt.lstQueryResultMap.get(str).get(i);
		refreshChildView(str, FullTextQueryOpt.lstQueryResultMap.get(str).size(), i, localRecord);
		this.mChildListView.invalidateViews();
		MapOperate.addAndZoomToGeometry(localRecord.getGeometry(), false);
	}

	private void backToResultList() {
		if (this.mGroupChildDetails.getVisibility() == View.VISIBLE)
			this.mGroupChildDetails.setVisibility(View.GONE);
		if (this.mListView.getVisibility() == View.GONE)
			this.mListView.setVisibility(View.VISIBLE);
	}

	private void onChlickChildData(int paramInt1, int clickIndex) {
		if (FullTextQueryOpt.lstQueryResultLayerName.size() <= paramInt1)
			return;
		layerName = FullTextQueryOpt.lstQueryResultLayerName.get(paramInt1);
		Record localRecord = FullTextQueryOpt.lstQueryResultMap.get(layerName).get(clickIndex);
		int rsCount = FullTextQueryOpt.lstQueryResultMap.get(layerName).size();
		if (((localRecord != null) && (localRecord.getShowValue() != null) && (localRecord.getShowValue().equals("载入更多"))) || (localRecord.getGeometry() == null))
			return;
		refreshChildView(layerName, rsCount, clickIndex, localRecord);
		this.mChildListView.invalidateViews();
		favgeometry = "";
		favgeometry = GeometryEngine.geometryToJson(MapOperate.map.getSpatialReference(), localRecord.getGeometry());
		favsymbol = null;
		try {
			favsymbol = "";
			if (localRecord.getGeometry() instanceof Point) {
				favsymbol = new SimpleMarkerSymbol(Color.BLUE, 10, STYLE.DIAMOND).toJson();
			} else if (localRecord.getGeometry() instanceof Polyline) {
				favsymbol = new SimpleLineSymbol(Color.YELLOW, 3).toJson();
			} else if (localRecord.getGeometry() instanceof Polygon) {
				favsymbol = new SimpleFillSymbol(Color.argb(100, 0, 225, 255)).toJson();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		MapOperate.addAndZoomToGeometry(localRecord.getGeometry(), false);
	}

	private void refreshChildView(String layerName, int rsCount, int clickIndex, Record paramRecord) {
		if ((paramRecord != null) && (paramRecord.getLstFieldsName() != null) && (paramRecord.getLstFieldsName().size() > 0)) {
			if (rsCount <= 1) {
				this.mPreButton.setEnabled(false);
				this.mNextButton.setEnabled(false);
			} else {
				if (clickIndex == 0) {
					this.mPreButton.setEnabled(false);
					this.mNextButton.setEnabled(true);
				} else if (clickIndex == rsCount - 1) {
					this.mPreButton.setEnabled(true);
					this.mNextButton.setEnabled(false);
				} else {
					this.mPreButton.setEnabled(true);
					this.mNextButton.setEnabled(true);
				}
			}
		}
		this.mInfoButton.setText((clickIndex + 1) + "/" + rsCount);
		this.mPreButton.setTag(layerName);
		this.mNextButton.setTag(Integer.valueOf(clickIndex));
		this.lstFieldNames.clear();
		this.lstFieldValues.clear();
		this.lstFieldNames.addAll(paramRecord.getLstFieldsName());
		this.lstFieldValues.addAll(paramRecord.getLstFieldsValue());

		for (int i = 0; i < lstFieldValues.size(); i++) {
			if(lstFieldValues.get(i)==null||lstFieldValues.get(i).equals("null")){
				lstFieldValues.set(i,"");

			}
		}
		favattributes = "";
		for (int j = 0; j < paramRecord.getLstFieldsName().size(); j++) {
			favattributes += paramRecord.getLstFieldsName().get(j) + "=" + paramRecord.getLstFieldsValue().get(j) + ";";
			Log.d("图形属性", paramRecord.getLstFieldsName().get(j) + "--->" + paramRecord.getLstFieldsValue().get(j));
		}
		this.mAddToFavButton.setTag(paramRecord.smid + "@#@" + paramRecord.getLayerName() + "@#@" + layerName);
		switchToChildDetails();

	}

	private void switchToChildDetails() {
		if (this.mListView.getVisibility() == View.VISIBLE)
			this.mListView.setVisibility(View.GONE);
		if (this.mGroupChildDetails.getVisibility() == View.GONE)
			this.mGroupChildDetails.setVisibility(View.VISIBLE);
	}

	public void doFullTextQuery(String queryTxt) {
		if ("".equals(queryTxt.trim())) {
			ToastUtils.showLong( "请输入查询关键字！");
			return;
		}
		onPreExecute();
		backToResultList();
		callbacknum = 0;
		lstQueryResultLayerName = new ArrayList<String>();
		lstQueryResultMap = new HashMap<String, List<Record>>();
		lstFieldNames = new ArrayList<String>();
		lstFieldValues = new ArrayList<String>();
		String queryLayerNames = GisQueryApplication.getApp().GetStringConfigData("querylayers", "");
		if ("".equals(queryLayerNames)) {
			this.dialog.dismiss();
			showQuerySettingOpt();
		} else {
			queryLayerAry = queryLayerNames.split(";");
			for (int i = 0; i < queryLayerAry.length; i++) {
				Log.d("参与查询图层", "---->" + queryLayerAry[i]);
				Layer fealyr = MapUtils.getLayerByName(MapOperate.map, queryLayerAry[i]);

				queryOneFeatureLayer(fealyr, queryTxt);
			}
		}
	}

	private void showQuerySettingOpt() {
		new QueryLayerSettingOpt(MapOperate.map,mainActivity);
	}

	private void queryOneFeatureLayer(final Layer tmlyr, final String queryTxt) {
		final LayerConfig layerConfig = GisQueryApplication.getApp().getProjectconfig().getLayerConfigByLayerName(tmlyr.getName());
		String[] mOriginalfields = layerConfig.getOriginalfields();
		if (tmlyr instanceof ArcGISFeatureLayer) {
			ArcGISFeatureLayer fealyr = (ArcGISFeatureLayer) tmlyr;
			Query query = new Query();
			query.setReturnGeometry(true);
			query.setInSpatialReference(MapOperate.map.getSpatialReference());
			query.setSpatialRelationship(SpatialRelationship.INTERSECTS);
			query.setWhere(getQueryWhere(mOriginalfields, queryTxt));
			fealyr.queryFeatures(query, new CallbackListener<FeatureSet>() {

				@Override
				public void onCallback(FeatureSet fSet) {
					if (null != fSet) {
						doWithResultData(queryTxt, fSet, layerConfig, new CallBack() {
							@Override
							public void execute() {
								callbacknum++;
								if (callbacknum == queryLayerAry.length) {
									refresh();
								}

							}
						});
					}

				}

				@Override
				public void onError(Throwable e) {
					callbacknum++;
					Log.i("当前查询图层【" + tmlyr.getName() + "】", "查询失败"+e.getMessage());
					if (callbacknum == queryLayerAry.length) {
						refresh();
					}
				}
			}
			);
		} else if (tmlyr instanceof FeatureLayer) {
			FeatureLayer fealyr = (FeatureLayer) tmlyr;
			FeatureTable featbl = fealyr.getFeatureTable();
			QueryParameters query = new QueryParameters();
			query.setReturnGeometry(true);
			query.setInSpatialReference(MapOperate.map.getSpatialReference());
			query.setSpatialRelationship(SpatialRelationship.INTERSECTS);			
			query.setWhere(getQueryWhere(mOriginalfields, queryTxt));
			featbl.queryFeatures(query, new CallbackListener<FeatureResult>() {

				@Override
				public void onCallback(FeatureResult featureResult) {
					if (null != featureResult) {
						doWithResultData(queryTxt, featureResult, layerConfig, new CallBack() {
							@Override
							public void execute() {
								callbacknum++;
								if (callbacknum == queryLayerAry.length) {
									refresh();
								}

							}
						});
					}

				}

				@Override
				public void onError(Throwable e) {
					// TODO Auto-generated method stub
					callbacknum++;
					Log.i("当前查询图层【" + tmlyr.getName() + "】", "查询失败"+e.getMessage());
					if (callbacknum == queryLayerAry.length) {
						refresh();
					}
				}
			});
		}

	}

	public void refresh() {
		mainActivity.sendEmptyMessage(5001);
	}

	public void doRefresh() {
		this.dialog.dismiss();
		refreshListView();
		this.mListView.startLayoutAnimation();
	}

	private void refreshListView() {
		this.mQueryResultAdapter.notifyDataSetChanged();
		this.mListView.invalidateViews();
	}

	private void doWithResultData(String queryTxt, FeatureSet fSet, LayerConfig layerConfig, CallBack callBack) {
		String[] mOriginalfields = layerConfig.getOriginalfields();
		String[] mDisplayfields = layerConfig.getDisplayfields();
		Graphic gra[] = fSet.getGraphics();
		ArrayList<Record> al_record = new ArrayList<Record>();
		for (int i = 0; i < gra.length; i++) {
			Graphic graphic = gra[i];
			Map<String, Object> mapObj = graphic.getAttributes();
			ArrayList<String> displayfields = new ArrayList<String>();
			ArrayList<String> displayvalues = new ArrayList<String>();
			Object[] fields = mapObj.keySet().toArray();

			// 如果有配置字段显示顺序，按配置来
			if (mOriginalfields != null && mOriginalfields.length > 0 && mOriginalfields.length == mDisplayfields.length) {
				for (int j = 0; j < mOriginalfields.length; j++) {
					displayfields.add(mDisplayfields[j]);
					displayvalues.add("" + mapObj.get(mOriginalfields[j]));
				}
			} else {
				for (int j = 0; j < fields.length; j++) {
					displayfields.add("" + fields[j]);
					displayvalues.add("" + mapObj.get(fields[j]));
				}
			}
			Record record = new Record();
			record.setQueryKey(queryTxt);
			record.setGeometry(graphic.getGeometry());
			record.setLayerName(layerConfig.getLabel());
			record.setLstFieldsName(displayfields);
			record.setLstFieldsValue(displayvalues);
			al_record.add(record);

		}
		if (al_record.size() > 0) {
			lstQueryResultLayerName.add(layerConfig.getLabel());
			lstQueryResultMap.put(layerConfig.getLabel(), al_record);
		}
		callBack.execute();
	}

	public void doWithResultData(String queryTxt, FeatureResult featureResult, LayerConfig layerConfig, CallBack callBack) {
		String[] mOriginalfields = layerConfig.getOriginalfields();
		String[] mDisplayfields = layerConfig.getDisplayfields();
		ArrayList<Record> al_record = new ArrayList<Record>();
		Iterator<Object> iterator_feature = featureResult.iterator();
		while (iterator_feature.hasNext()) {
			Feature tmFeature = (Feature) iterator_feature.next();
			Map<String, Object> mapObj = tmFeature.getAttributes();
			ArrayList<String> displayfields = null;
			ArrayList<String> displayvalues = null;
			displayfields = new ArrayList<String>();
			displayvalues = new ArrayList<String>();
			Object[] fields = mapObj.keySet().toArray();
			// 如果有配置字段显示顺序，按配置来
			if (mOriginalfields != null && mOriginalfields.length > 0 && mOriginalfields.length == mDisplayfields.length) {
				for (int j = 0; j < mOriginalfields.length; j++) {
					displayfields.add(mDisplayfields[j]);
					displayvalues.add("" + mapObj.get(mOriginalfields[j]));
				}
			} else {
				for (int j = 0; j < fields.length; j++) {
					displayfields.add("" + fields[j]);
					displayvalues.add("" + fields[j]);
					displayvalues.add("" + mapObj.get(fields[j]));
				}
			}
			Record record = new Record();
			record.setQueryKey(queryTxt);
			record.setGeometry(tmFeature.getGeometry());
			record.setLayerName(layerConfig.getLabel());
			record.setLstFieldsName(displayfields);
			record.setLstFieldsValue(displayvalues);
			al_record.add(record);
		}
		if (al_record.size() > 0) {
			lstQueryResultLayerName.add(layerConfig.getLabel());
			lstQueryResultMap.put(layerConfig.getLabel(), al_record);
		}
		callBack.execute();
	}

	private String getQueryWhere(String[] mOriginalfields, String queryTxt) {
		if (mOriginalfields != null && mOriginalfields.length > 0) {
			String whereSql = "";
			for (String field : mOriginalfields) {
				whereSql += " " + field + " like '%" + queryTxt + "%' or";
			}
			String result = whereSql.substring(0, whereSql.length() - 2);
			System.out.println("------------查询条件："+result);
			return result;
		} else {
			System.out.println("------------不可查询-------------------");
			return "";
		}
	}

	class QueryReusltAdapter extends BaseExpandableListAdapter {
		private LayoutInflater mInflater = null;

		public QueryReusltAdapter(Context paramContext) {
			this.mInflater = ((LayoutInflater) paramContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE));
		}

		public Object getChild(int paramInt1, int paramInt2) {
			return FullTextQueryOpt.lstQueryResultMap.get(FullTextQueryOpt.lstQueryResultLayerName.get(paramInt1));
		}

		public long getChildId(int paramInt1, int paramInt2) {
			return paramInt2;
		}

		public View getChildView(int paramInt1, int paramInt2, boolean paramBoolean, View paramView, ViewGroup paramViewGroup) {
			if (paramInt1 < FullTextQueryOpt.lstQueryResultLayerName.size()) {
				if (paramView == null)
					paramView = this.mInflater.inflate(R.layout.list_query_result_child, paramViewGroup, false);
				((TextView) paramView.findViewById(R.id.textView1)).setText((FullTextQueryOpt.lstQueryResultMap.get(FullTextQueryOpt.lstQueryResultLayerName
						.get(paramInt1)).get(paramInt2)).getShowValue());
			}
			return paramView;
		}

		public int getChildrenCount(int paramInt) {
			return FullTextQueryOpt.lstQueryResultMap.get(FullTextQueryOpt.lstQueryResultLayerName.get(paramInt)).size();
		}

		public Object getGroup(int paramInt) {
			return FullTextQueryOpt.lstQueryResultLayerName.get(paramInt);
		}

		public int getGroupCount() {
			return FullTextQueryOpt.lstQueryResultLayerName.size();
		}

		public long getGroupId(int paramInt) {
			return paramInt;
		}

		public View getGroupView(int paramInt, boolean paramBoolean, View paramView, ViewGroup paramViewGroup) {
			if (paramInt < FullTextQueryOpt.lstQueryResultLayerName.size()) {
				if (paramView == null)
					paramView = this.mInflater.inflate(R.layout.list_query_result_group, paramViewGroup, false);
				((TextView) paramView.findViewById(R.id.textView1)).setText((CharSequence) FullTextQueryOpt.lstQueryResultLayerName.get(paramInt));
			}
			return paramView;
		}

		public boolean hasStableIds() {
			return false;
		}

		public boolean isChildSelectable(int paramInt1, int paramInt2) {
			return true;
		}
	}

	class ChildDetailsAdapter extends BaseAdapter {
		private LayoutInflater mInflater = null;

		public ChildDetailsAdapter(Context paramContext) {
			this.mInflater = ((LayoutInflater) paramContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE));
		}

		public int getCount() {
			return FullTextQueryOpt.this.lstFieldNames.size();
		}

		public Object getItem(int paramInt) {
			return FullTextQueryOpt.this.lstFieldNames.get(paramInt);
		}

		public long getItemId(int paramInt) {
			return paramInt;
		}

		public View getView(int paramInt, View paramView, ViewGroup paramViewGroup) {
			if (paramInt < FullTextQueryOpt.this.lstFieldNames.size()) {
				if (paramView == null)
					paramView = this.mInflater.inflate(R.layout.list_query_result_child_details, paramViewGroup, false);
				TextView localTextView1 = (TextView) paramView.findViewById(R.id.textView1);
				TextView localTextView2 = (TextView) paramView.findViewById(R.id.textView2);
				localTextView1.setText((CharSequence) FullTextQueryOpt.this.lstFieldNames.get(paramInt));
				localTextView2.setText((CharSequence) FullTextQueryOpt.this.lstFieldValues.get(paramInt));
			}
			return paramView;
		}
	}

	public void showAddFavDlg() {
		LayoutInflater inflater = (LayoutInflater) mainActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View localView = inflater.inflate(R.layout.layout_add_fav, null);
		localEditText1 = (EditText) localView.findViewById(R.id.et_title);
		localEditText1.setText(layerName);
		localEditText2 = (EditText) localView.findViewById(R.id.et_remark);
		new AlertDialog.Builder(mainActivity).setView(localView).setPositiveButton("确定", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface paramDialogInterface, int paramInt) {
				ContentValues localContentValues = new ContentValues();
				localContentValues.put("title", localEditText1.getText().toString());
				localContentValues.put("peoId", SysConfig.mHumanInfo.getHumanId());
				localContentValues.put("layerName", layerName);
				localContentValues.put("remark", localEditText2.getText().toString());
				localContentValues.put("favgeometry", favgeometry);
				localContentValues.put("favattributes", favattributes);
				localContentValues.put("favsymbol", favsymbol);
				localContentValues.put("time", Long.valueOf(System.currentTimeMillis()));
				if (SdCardDBHelper.getInstance(mainActivity).tableIsExist("tbFav")) {
					if (localEditText1.getText().toString().equals("")) {					
						ToastUtils.showLong( "请输入名称！");	
					} else {
						SdCardDBHelper.getInstance(mainActivity).insert("tbFav", localContentValues);
						ToastUtils.showLong( "已收藏！");	
					}
				} else {
					ToastUtils.showLong( "收藏失败！");				
				}

			}
		}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface paramDialogInterface, int paramInt) {
				paramDialogInterface.dismiss();
			}
		}).create().show();
	}

}
