package com.guyu.android.gis.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;

import com.guyu.android.R;
import com.guyu.android.gis.adapter.TableExpandableAdapter;
import com.guyu.android.gis.adapter.TableExpandableAdapter.TableRow;
import com.guyu.android.gis.app.GisQueryApplication;
import com.guyu.android.gis.common.AnalysisIntersectionResultInfo;
import com.guyu.android.gis.common.AnalysisLayerResultInfo;
import com.guyu.android.utils.MathUtils;

import java.util.ArrayList;
import java.util.List;

public class LandAnalysisRecordDetailsActivity extends Activity {
	private static int ROW_COUNT = 0;
	private static final String TAG = "LandAnalysisRecordDetailsActivity";
	private String mLandAtrribute;
	private LinearLayout mLandNameLayout = null;
	private int m_nIconWidth = 100;
	private int m_nRowHeight = 80;
	private int m_nScreenHeight;
	private int m_nScreenWidth;
	private Class<Activity> m_strIntentAction = null;

	protected void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		setContentView(R.layout.activity_land_analysis_record_details);

		initActivity();
	}

	private void initActivity() {
		DisplayMetrics localDisplayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(localDisplayMetrics);
		this.m_nScreenWidth = (localDisplayMetrics.widthPixels - (int) (0.5D + 50.0F * localDisplayMetrics.density));
		this.m_nScreenHeight = localDisplayMetrics.heightPixels;
		Intent localIntent = getIntent();
		this.m_strIntentAction = (Class<Activity>) localIntent.getSerializableExtra("action");
		initWidget();
	}

	private void initWidget() {

		AnalysisLayerResultInfo analysisLayerResultInfo = GisQueryApplication.getApp()
				.getAnalysisLayerResultInfo();
		ExpandableListView localExpandableListView = (ExpandableListView) findViewById(R.id.dataset_info_list1);
		localExpandableListView.setGroupIndicator(null);
		ArrayList<TableExpandableAdapter.TableRow> level1List = new ArrayList<TableExpandableAdapter.TableRow>();
		ArrayList<List<TableExpandableAdapter.TableRow>> level2List = new ArrayList<List<TableExpandableAdapter.TableRow>>();
		initData(level1List, level2List, analysisLayerResultInfo);
		localExpandableListView.setAdapter(new TableExpandableAdapter(this,
				level1List, level2List, localExpandableListView));
	}

	/**
	 * 初始化数据
	 * allTableChild rowTitleList
	 * @param
	 * @param
	 * @param analysisLayerResultInfo
	 */
	private void initData(List<TableExpandableAdapter.TableRow> level1List,
			List<List<TableExpandableAdapter.TableRow>> level2List,
			AnalysisLayerResultInfo analysisLayerResultInfo) {
		ArrayList<String> originalfields = analysisLayerResultInfo
				.getOriginalfields();
		ArrayList<String> displayfields = analysisLayerResultInfo
				.getDisplayfields();
		ArrayList<AnalysisIntersectionResultInfo> intersections = analysisLayerResultInfo
				.getIntersections();
		List<String> originalfields0_3 = null;
		List<String> originalfields4_ = null;
		List<String> displayfields0_3 = null;
		List<String> displayfields4_ = null;
		// subList 包括起始索引，不包括终止索引
		if (originalfields.size() >= 4) {
			originalfields0_3 = originalfields.subList(0, 4);
			displayfields0_3 = displayfields.subList(0, 4);
		} else {
			originalfields0_3 = originalfields
					.subList(0, originalfields.size());
			displayfields0_3 = displayfields.subList(0, displayfields.size());
		}
		if (originalfields.size() > 4) {
			originalfields4_ = originalfields.subList(4, originalfields.size());
			displayfields4_ = displayfields.subList(4, originalfields.size());
		} else {
			originalfields4_ = new ArrayList<String>();
			displayfields4_ = new ArrayList<String>();
		}

		// 获取一级标题行
		level1List.add(getOnTableRowTitle(displayfields0_3, true));
		// 获取一级数据行
		for (int i = 0; i < intersections.size(); i++) {
			AnalysisIntersectionResultInfo analysisIntersectionResultInfo = intersections
					.get(i);
			level1List.add(getOneTableRowValue(originalfields0_3,
					analysisIntersectionResultInfo, true));
		}
		// 获取二级数据行
		for (int i = 0; i < intersections.size(); i++) {
			AnalysisIntersectionResultInfo analysisIntersectionResultInfo = intersections
					.get(i);
			level2List.add(getOneTableChild(originalfields4_, displayfields4_,
					analysisIntersectionResultInfo, true));
		}
	}

	private List<TableRow> getOneTableChild(List<String> originalfields,
			List<String> displayfields,
			AnalysisIntersectionResultInfo analysisIntersectionResultInfo,
			boolean b) {
		ArrayList<TableExpandableAdapter.TableRow> keyValueTable = new ArrayList<TableExpandableAdapter.TableRow>();
		for (int k = 0; k < originalfields.size(); k++) {

			TableExpandableAdapter.TableCell[] arrayOfTableCell = new TableExpandableAdapter.TableCell[2];
			String curValue = ""
					+ analysisIntersectionResultInfo.getAttributes().get(
							originalfields.get(k));
			arrayOfTableCell[0] = new TableExpandableAdapter.TableCell(
					displayfields.get(k), this.m_nScreenWidth / 2,
					this.m_nRowHeight, 0);
			arrayOfTableCell[1] = new TableExpandableAdapter.TableCell(
					curValue, this.m_nScreenWidth / 2, this.m_nRowHeight, 0);
			TableExpandableAdapter.TableRow tr = new TableExpandableAdapter.TableRow(
					arrayOfTableCell);
			keyValueTable.add(tr);
		}
		return keyValueTable;
	}

	private TableExpandableAdapter.TableRow getOnTableRowTitle(
			List<String> displayfields, boolean paramBoolean) {
		int fieldCount = displayfields.size();
		int j = fieldCount + 3;
		TableExpandableAdapter.TableCell[] arrayOfTableCell = new TableExpandableAdapter.TableCell[j];
		if (paramBoolean) {
			arrayOfTableCell[0] = new TableExpandableAdapter.TableCell("扩展",
					this.m_nIconWidth, this.m_nRowHeight,
					TableExpandableAdapter.TableCell.STRING);
			arrayOfTableCell[1] = new TableExpandableAdapter.TableCell("图斑",
					this.m_nIconWidth, this.m_nRowHeight,
					TableExpandableAdapter.TableCell.STRING);
			arrayOfTableCell[2] = new TableExpandableAdapter.TableCell(
					"占用面积(㎡)", 3 * this.m_nIconWidth, this.m_nRowHeight,
					TableExpandableAdapter.TableCell.STRING);
			for (int k = 0; k < displayfields.size(); k++) {
				String str1 = displayfields.get(k);
				arrayOfTableCell[(k + 3)] = new TableExpandableAdapter.TableCell(
						str1,
						(this.m_nScreenWidth - (5 * this.m_nIconWidth)-15) / fieldCount,
						this.m_nRowHeight,
						TableExpandableAdapter.TableCell.STRING);
			}

		}
		return new TableExpandableAdapter.TableRow(arrayOfTableCell);
	}

	private TableExpandableAdapter.TableRow getOneTableRowValue(
			List<String> originalfields,
			AnalysisIntersectionResultInfo analysisIntersectionResultInfo,
			boolean paramBoolean) {

		TableExpandableAdapter.TableCell[] arrayOfTableCell = new TableExpandableAdapter.TableCell[7];
		if (paramBoolean) {
			arrayOfTableCell[0] = new TableExpandableAdapter.TableCell(
					Boolean.valueOf(false), this.m_nIconWidth,
					this.m_nRowHeight,
					TableExpandableAdapter.TableCell.CHECK_BOX);
			arrayOfTableCell[1] = new TableExpandableAdapter.TableCell("定位",
					this.m_nIconWidth, this.m_nRowHeight,
					TableExpandableAdapter.TableCell.STRING_SUPER);
			arrayOfTableCell[1].analysisIntersectionResultInfo = analysisIntersectionResultInfo;
			double d2 = analysisIntersectionResultInfo.getArea();
			d2 = MathUtils.GetAccurateNumber(d2, 2);
			if (d2 > 0.0D)
				arrayOfTableCell[2] = new TableExpandableAdapter.TableCell(d2,
						3 * this.m_nIconWidth, this.m_nRowHeight,
						TableExpandableAdapter.TableCell.STRING);
			for (int k = 0; k < originalfields.size(); k++) {
				String curValue = ""
						+ analysisIntersectionResultInfo.getAttributes().get(
								originalfields.get(k));
				curValue = "null".equalsIgnoreCase(curValue) ? "" : curValue;
				arrayOfTableCell[(k + 3)] = new TableExpandableAdapter.TableCell(
						curValue,
						(this.m_nScreenWidth - (5 * this.m_nIconWidth)-15) / originalfields.size(),
						this.m_nRowHeight,
						TableExpandableAdapter.TableCell.STRING);
			}
		}
		return new TableExpandableAdapter.TableRow(arrayOfTableCell);
	}

	public void backOnClick(View paramView) {
		Intent localIntent = new Intent(this,m_strIntentAction);
		localIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		startActivity(localIntent);
		finish();
	}

}
