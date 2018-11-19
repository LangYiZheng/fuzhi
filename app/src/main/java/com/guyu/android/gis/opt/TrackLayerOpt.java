package com.guyu.android.gis.opt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.blankj.utilcode.util.ToastUtils;
import com.guyu.android.R;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polyline;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.SimpleLineSymbol;
import com.guyu.android.database.sync.DJQDBMExternal;
import com.guyu.android.database.sync.DJZQDBMExternal;
import com.guyu.android.database.task.TrackDBMExternal;
import com.guyu.android.gis.common.DJQ;
import com.guyu.android.gis.common.DJZQ;
import com.guyu.android.gis.common.TrackObj;
import com.guyu.android.utils.UtilsTools;

import cn.qqtheme.framework.picker.DoublePicker;
import cn.qqtheme.framework.picker.LinkagePicker;

public class TrackLayerOpt {
	private static final String TAG = "TrackLayerOpt";
	private static List<Point> allTrackPoints = new ArrayList<Point>();

	public static void addOnePoint(Point paramPoint2D) {
		allTrackPoints.add(paramPoint2D);
	}

	private static int select;

	private static int s1;
	private static int s2;
	private static TextView localEditText1;
	public static void showRecordAtrrDlg(final Activity paramContext,final int track_selected) {
		select = track_selected;
		LayoutInflater mInflater = ((LayoutInflater) paramContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE));
		View localView = mInflater.inflate(R.layout.dialog_track_atrr, null);
		  localEditText1 = (TextView) localView
				.findViewById(R.id.et_add);

		final EditText localEditText2 = (EditText) localView
				.findViewById(R.id.et_line);
		final EditText localEditText3 = (EditText) localView
				.findViewById(R.id.et_case);
		final EditText localEditText4 = (EditText) localView
				.findViewById(R.id.et_more_info);
		final EditText localEditText5 = (EditText) localView
				.findViewById(R.id.et_men);


		localEditText1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				onPickerShow(paramContext);
			}


		});

		final Spinner spinner = (Spinner) localView.findViewById(R.id.spinner);

		localEditText1.setText("");
		localEditText2.setText("");
		localEditText3.setText("");
		localEditText4.setText("");
		localEditText5.setText("");


		String[] data_list = paramContext.getResources().getStringArray(R.array.business_type);



		//适配器
		ArrayAdapter arr_adapter= new ArrayAdapter<String>(paramContext, android.R.layout.simple_spinner_item, data_list);
		//设置样式
		arr_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		//加载适配器
		spinner.setAdapter(arr_adapter);
		spinner.setSelection(track_selected);


		spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {//选择item的选择点击监听事件
			public void onItemSelected(AdapterView<?> arg0, View arg1,
									   int arg2, long arg3) {
				select = arg2;
			}

			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});


		localEditText1.setHint("请输入巡查地点");
		localEditText2.setHint("请输入巡查路线");
		localEditText3.setHint("请输入巡查情况");
		localEditText4.setHint("请输入信息反馈及一级巡查区情况");
		localEditText5.setHint("请输入部门负责人");
		AlertDialog.Builder localBuilder = new AlertDialog.Builder(paramContext);
		localBuilder.setView(localView);
		localBuilder.setCancelable(false);
		localBuilder.setTitle("轨迹属性");
		localBuilder.setPositiveButton("保存",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface paramDialogInterface,
							int paramInt) {
						TrackObj trackObj = new TrackObj();
						trackObj.setXCDD (localEditText1.getText().toString()
								.trim());
						trackObj.setXCLX (localEditText2.getText().toString()
								.trim());
						trackObj.setXCQK ( localEditText3.getText().toString()
								.trim());
						trackObj.setXXFKJYJXCQQK (localEditText4.getText()
								.toString().trim());
						trackObj.setBMFZR ( localEditText5.getText().toString()
								.trim());
						trackObj.setFirm(select);
						trackObj.setDJQ(s1);
						trackObj.setDJZQ(s2);
						try {
							new TrackDBMExternal(paramContext)
									.updateTrackAtrr(MapOperate.GetTrackName(),trackObj);
							return;
						} catch (Exception localException) {
							ToastUtils.showLong(
									"轨迹表缺少字段，请更新至最新的数据库");
						}
					}
				});
		localBuilder.create().show();
		onPickerShow(paramContext);
	}

	private static void onPickerShow(Activity paramContext) {
		List<DJQ> djqs;
		List<DJZQ> djzqs;
		DJQDBMExternal djqdb = new DJQDBMExternal(paramContext);
		djqs = djqdb.getAll();
		DJZQDBMExternal djzqdb = new DJZQDBMExternal(paramContext);
		djzqs = djzqdb.getAll();
		final List<String> l1 = new ArrayList<>();
		Map<Integer,String> map = new HashMap<>();
		Map<String,Integer> map2 = new HashMap<>();
		for (DJQ d:djqs){
			map.put(d.get_id(),d.getNAME());
			map2.put(d.getNAME(),d.get_id());
		}
		for (int i = 0; i < djqs.size(); i++) {
			l1.add(map.get(i+1));
		}
		Map<Integer,List<String>> mapdjzq = new HashMap<>();
		Map<String,Integer> map4 = new HashMap<>();
		for (int i = 0; i < djzqs.size(); i++) {
			DJZQ d = djzqs.get(i);
			List<String> list = mapdjzq.get(d.get_pid());
			if(list==null){
				list = new ArrayList<>();
			}
			list.add(d.getNAME());
			mapdjzq.put(d.get_pid(),list);


			map4.put(d.getNAME(),d.get_id());
		}



		LinkagePicker.DataProvider provider = new LinkagePicker.DataProvider() {

			@Override
			public boolean isOnlyTwo() {
				return true;
			}


			@Override
			public List<String> provideFirstData() {

				return l1;
			}


			@Override
			public List<String> provideSecondData(int firstIndex) {

				return mapdjzq.get(firstIndex+1);
			}


			@Override
			public List<String> provideThirdData(int firstIndex, int secondIndex) {
				return null;
			}

		};
		LinkagePicker picker = new LinkagePicker(paramContext, provider);
		picker.setCycleDisable(true);
		picker.setUseWeight(true);
		picker.setSelectedIndex(0, 0);
		//picker.setSelectedItem("12", "9");
		picker.setContentPadding(10, 0);
		picker.setOnStringPickListener(new LinkagePicker.OnStringPickListener() {
			@Override
			public void onPicked(String first, String second, String third) {
				localEditText1.setText(second);
				s1 = map2.get(first);
				s2 = map4.get(second);
//				ToastUtils.showLong(first + "-" + second + "-" + third);
			}

		});
		picker.show();
	}
	public static void DrawTrackToLayer(List<Point> mAllTrackPoint2ds) {
		// TODO Auto-generated method stub
		
	}
}
