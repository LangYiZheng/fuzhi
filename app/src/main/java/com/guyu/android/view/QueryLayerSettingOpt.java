package com.guyu.android.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.guyu.android.R;
import com.esri.android.map.FeatureLayer;
import com.esri.android.map.Layer;
import com.esri.android.map.MapView;
import com.esri.android.map.ags.ArcGISFeatureLayer;
import com.guyu.android.gis.app.GisQueryApplication;
import com.guyu.android.gis.config.LayerConfig;
import com.mobeta.android.dslv.DragSortListView;

public class QueryLayerSettingOpt implements View.OnClickListener {
	public static final String LAYER_ORDER = "layerOrder";
	private Activity activity;
	public boolean isOnlyShowVisibleLayer = false;
	public List<Boolean> lstSelected = new ArrayList();
	private List<Layer> mLayers = new ArrayList();
	private Dialog mDialog;
	private DragSortListView mListView = null;
	private ViewGroup mViewGroup = null;
	private MapView map;
	private MAdapter adapter;
	private Map<String,String> nameMap;

	public QueryLayerSettingOpt(MapView mMap, Activity mActivity) {
		this.activity = mActivity;
		this.map = mMap;
		this.mViewGroup = ((ViewGroup) ((LayoutInflater) this.activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
				R.layout.layout_layer_query_setting, null));
		if (this.mViewGroup == null)
			return;
		initData();
		initView();
		this.mDialog = new AlertDialog.Builder(this.activity)
				.setTitle("查询图层设置").setView(this.mViewGroup).create();
		this.mDialog.show();
	}

	private void initView() {
		initListView();
		initControlView();
	}

	private void initControlView() {
		this.mViewGroup.findViewById(R.id.btn_ok).setOnClickListener(this);
		this.mViewGroup.findViewById(R.id.btn_select_all).setOnClickListener(
				this);
		this.mViewGroup.findViewById(R.id.btn_un_select_all)
				.setOnClickListener(this);
		this.mViewGroup.findViewById(R.id.btn_select_changed)
				.setOnClickListener(this);
		this.mViewGroup.findViewById(R.id.btn_back).setOnClickListener(this);
	}

	private void initListView() {
		ArrayList<String> localArrayList = new ArrayList<String>();
		for (int i = 0; i < mLayers.size(); i++) {
			localArrayList.add(mLayers.get(i).getName());
		}

		ArrayList<LayerConfig> al_basemaps = new ArrayList<>();
		al_basemaps.addAll(GisQueryApplication.getApp().getProjectconfig()
				.getBasemaps());
		al_basemaps.addAll(GisQueryApplication.getApp().getProjectconfig()
				.getOperationallayers());
		nameMap = new HashMap();
		for(LayerConfig l:al_basemaps){
			nameMap.put(l.getLabel(),l.getName());
		}

		this.mListView = ((DragSortListView) this.mViewGroup
				.findViewById(R.id.drag_list_1));
		this.adapter = new MAdapter(this.activity,
				R.layout.list_item_checkable, R.id.text, localArrayList);
		this.mListView.setAdapter(this.adapter);
		DragSortListView localDragSortListView = getListView();
		// localDragSortListView.setDropListener(this.onDrop);
		// localDragSortListView.setRemoveListener(this.onRemove);
		localDragSortListView
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					public void onItemClick(AdapterView<?> paramAdapterView,
							View paramView, int paramInt, long paramLong) {
						QueryLayerSettingOpt.this.lstSelected
								.set(paramInt,
										!QueryLayerSettingOpt.this.lstSelected
												.get(paramInt));
						QueryLayerSettingOpt.this.getListView()
								.invalidateViews();
					}
				});
	}

	public void initData() {
		this.mLayers.clear();
		String analysisLayerNames = GisQueryApplication.getApp().GetStringConfigData("querylayers", "");
		String[] analysisLayerAry = analysisLayerNames.split(";");
		Layer[] alllayers = map.getLayers();
		for (int i = 0; i < alllayers.length; i++) {
			if (alllayers[i] instanceof ArcGISFeatureLayer) {
				ArcGISFeatureLayer fealyr = (ArcGISFeatureLayer) alllayers[i];
				// 只有面类型参与分析
				this.mLayers.add(fealyr);
				boolean tmSel = false;
				for (int j = 0; j < analysisLayerAry.length; j++) {
					if (fealyr.getName().equals(analysisLayerAry[j])) {
						tmSel = true;
						break;
					}
				}
				this.lstSelected.add(tmSel);

			} else if (alllayers[i] instanceof FeatureLayer) {
				FeatureLayer fealyr = (FeatureLayer) alllayers[i];
				this.mLayers.add(fealyr);
				boolean tmSel = false;
				for (int j = 0; j < analysisLayerAry.length; j++) {
					if (fealyr.getName().equals(analysisLayerAry[j])) {
						tmSel = true;
						break;
					}
				}
				this.lstSelected.add(tmSel);

			}
		}

	}

	public DragSortListView getListView() {
		if (this.mListView == null)
			this.mListView = ((DragSortListView) this.mViewGroup
					.findViewById(R.id.drag_list_1));
		return this.mListView;
	}

	public void saveLayerOrder() {
		String layersOrderInfo = "";
		Iterator<Layer> localIterator = this.mLayers.iterator();
		while (localIterator.hasNext()) {
			Layer localLayer = localIterator.next();
			layersOrderInfo = layersOrderInfo + localLayer.getName() + ";";
		}
		GisQueryApplication.getApp().SetConfigData(
				"layerOrder", layersOrderInfo);
	}

	public void saveAnalysisLayerInfo() {
		StringBuffer sb = new StringBuffer("");
		for (int i = 0; i < mLayers.size(); i++) {
			Layer tmLayer = mLayers.get(i);
			if (tmLayer instanceof ArcGISFeatureLayer) {
				ArcGISFeatureLayer fealyr = (ArcGISFeatureLayer) mLayers.get(i);
				if (this.lstSelected.get(i).booleanValue()) {
					sb.append(fealyr.getName() + ";");
				}
			} else if (tmLayer instanceof FeatureLayer) {
				FeatureLayer fealyr = (FeatureLayer) mLayers.get(i);
				if (this.lstSelected.get(i).booleanValue()) {
					sb.append(fealyr.getName() + ";");
				}
			}
		}
		if ("".equals(sb.toString())) {
			GisQueryApplication.getApp()
					.SetConfigData("querylayers", "");
		} else {
			GisQueryApplication.getApp()
					.SetConfigData("querylayers",
							sb.toString().substring(0, sb.length() - 1));
		}

	}

	@Override
	public void onClick(View paramView) {
		switch (paramView.getId()) {
		case R.id.btn_ok:
			saveAnalysisLayerInfo();
			this.mDialog.dismiss();
			break;
		case R.id.btn_select_all: {
			int length_lstSelected = this.lstSelected.size();
			this.lstSelected.clear();
			for (int i = 0; i < length_lstSelected; i++) {
				this.lstSelected.add(true);
			}
			getListView().invalidateViews();
			break;
		}
		case R.id.btn_un_select_all: {
			int length_lstSelected = this.lstSelected.size();
			this.lstSelected.clear();
			for (int i = 0; i < length_lstSelected; i++) {
				this.lstSelected.add(false);
			}
			getListView().invalidateViews();
			break;
		}
		case R.id.btn_select_changed: {
			int length_lstSelected = this.lstSelected.size();
			for (int i = 0; i < length_lstSelected; i++) {
				this.lstSelected
						.set(i, !this.lstSelected.get(i).booleanValue());
			}
			getListView().invalidateViews();
		}
		case R.id.btn_back:
		default:
		}

	}

	public class MAdapter<String> extends ArrayAdapter<String> {
		private LayoutInflater mInflater = null;

		public MAdapter(Activity mActivity, int paramInt1, int paramInt2,
				List<String> paramList) {
			super(mActivity, paramInt2, paramList);
			this.mInflater = ((LayoutInflater) mActivity
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE));
		}

		public View getView(int paramInt, View paramView,
				ViewGroup paramViewGroup) {
			if (paramView == null)
				paramView = this.mInflater.inflate(
						R.layout.list_item_checkable, paramViewGroup, false);
			TextView tv_text = ((TextView) paramView.findViewById(R.id.text));
			tv_text.setText(nameMap.get(getItem(paramInt).toString()).toString());
			CheckBox cbx = ((CheckBox) paramView.findViewById(R.id.check_box));
			cbx.setChecked(QueryLayerSettingOpt.this.lstSelected
					.get(paramInt).booleanValue());
			// paramView.findViewById(R.id.btn_field_setting).setOnClickListener(new
			// OnClickListener()
			// {
			// public void onClick(View paramView)
			// {
			// //AnalysisLayerSetingOpt.this.selectShowFieldDlg((Layer)AnalysisLayerSetingOpt.this.mLayers.get(this.val$position));
			// }
			// });
			ImageView localImageView = (ImageView) paramView
					.findViewById(R.id.drag_handle);
			Layer tmLayer = mLayers.get(paramInt);
			if (tmLayer instanceof ArcGISFeatureLayer) {
				ArcGISFeatureLayer fealyr = (ArcGISFeatureLayer) tmLayer;
				switch (fealyr.getGeometryType()) {
				case POINT:
				case MULTIPOINT:
					localImageView.setImageResource(R.drawable.icon_sgo_point);
					break;
				case LINE:
				case POLYLINE:
					localImageView
							.setImageResource(R.drawable.icon_sgo_polyline);
					break;
				case ENVELOPE:
				case POLYGON:
					localImageView
							.setImageResource(R.drawable.icon_sgo_polygon);
					break;
				default:
					localImageView
							.setImageResource(R.drawable.icon_sgo_unknown);
				}
			} else if (tmLayer instanceof FeatureLayer) {
				FeatureLayer fealyr = (FeatureLayer) tmLayer;
				switch (fealyr.getGeometryType()) {
				case POINT:
				case MULTIPOINT:
					localImageView.setImageResource(R.drawable.icon_sgo_point);
					break;
				case LINE:
				case POLYLINE:
					localImageView
							.setImageResource(R.drawable.icon_sgo_polyline);
					break;
				case ENVELOPE:
				case POLYGON:
					localImageView
							.setImageResource(R.drawable.icon_sgo_polygon);
					break;
				default:
					localImageView
							.setImageResource(R.drawable.icon_sgo_unknown);
				}
			}

			return paramView;
		}
	}
}