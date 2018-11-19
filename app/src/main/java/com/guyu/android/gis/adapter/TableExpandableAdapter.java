package com.guyu.android.gis.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextPaint;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.guyu.android.gis.activity.MainActivity;
import com.guyu.android.R;
import com.esri.core.geometry.Geometry;
import com.guyu.android.gis.app.GisQueryApplication;
import com.guyu.android.gis.common.AnalysisIntersectionResultInfo;

public class TableExpandableAdapter extends BaseExpandableListAdapter {
	private List<List<TableRow>> allTableChild;
	private Activity mActivity;
	private int groupNum;
	private int mExpandedPos = -1;
	private ExpandableListView m_listView = null;
	private int m_nAllDataCount = 0;
	private List<Boolean> m_selectList = null;
	private List<TableRow> tableChild = new ArrayList();
	private List<TableRow> tableGroup;

	public TableExpandableAdapter(Activity activity,
			List<TableRow> level1List, List<List<TableRow>> level2List,
			ExpandableListView paramExpandableListView) {
		this.mActivity = activity;
		this.allTableChild = level2List;
		this.tableGroup = level1List;
		this.m_listView = paramExpandableListView;
		this.m_selectList = new ArrayList();
		if (this.m_selectList.size() > 0)
			this.m_selectList.clear();
		for (int i = 0; i < level1List.size(); i++) {
			this.m_selectList.add(false);
		}
		this.m_nAllDataCount = level1List.size() - 1;
	}

	private void refreshTableChild(int paramInt) {
		if (this.tableChild.size() > 0)
			this.tableChild.clear();
		if (paramInt == 0)
			return;
		this.tableChild.addAll((Collection) this.allTableChild
				.get(paramInt - 1));
	}

	public void expandeOpt(int paramInt) {
		if (paramInt < this.tableGroup.size()) {
			if (this.mExpandedPos != -1) {
				this.m_listView.collapseGroup(this.mExpandedPos);
				this.mExpandedPos = -1;
			} else {
				if (this.allTableChild.size() <= 0)
					return;
				refreshTableChild(paramInt);
				this.m_listView.expandGroup(paramInt);
				this.mExpandedPos = paramInt;
			}

		}
	}

	public Object getChild(int paramInt1, int paramInt2) {
		return this.tableChild.get(paramInt2);
	}

	public long getChildId(int paramInt1, int paramInt2) {
		return paramInt2;
	}

	public View getChildView(int paramInt1, int paramInt2,
			boolean paramBoolean, View paramView, ViewGroup paramViewGroup) {
		if ((this.tableChild != null) && (this.tableChild.size() > paramInt2))
			return new TableRowView(this.mActivity,
					(TableRow) this.tableChild.get(paramInt2), paramInt2, false);
		return paramView;
	}

	public int getChildrenCount(int paramInt) {
		return this.tableChild.size();
	}

	public Object getGroup(int paramInt) {
		return this.tableGroup.get(paramInt);
	}

	public int getGroupCount() {
		return this.tableGroup.size();
	}

	public long getGroupId(int paramInt) {
		return paramInt;
	}

	public View getGroupView(int paramInt, boolean paramBoolean,
			View paramView, ViewGroup paramViewGroup) {
		if ((this.tableGroup != null) && (this.tableGroup.size() > paramInt)) {
			Context localContext = this.mActivity;
			TableRow localTableRow = this.tableGroup.get(paramInt);
			paramView = new TableRowView(localContext, localTableRow, paramInt,
					true);
			paramView.setOnClickListener(new checkBoxClick(paramInt));

		}
		return paramView;
	}

	public void getSelectState(List<Boolean> paramList) {
		paramList.addAll(this.m_selectList);
	}

	public boolean hasStableIds() {
		return true;
	}

	/**
	 * 初始化选择状态
	 */
	public void initSelectState() {
		for (int i = 0; i < this.m_selectList.size(); i++) {
			this.m_selectList.set(i, false);
		}
	}

	public boolean isChildSelectable(int paramInt1, int paramInt2) {
		return true;
	}

	public static class TableCell {
		public static final int STRING = 0;// 文本
		public static final int IMAGE = 1;// 图片
		public static final int CHECK_BOX = 2;// 复选
		public static final int STRING_SUPER = 3;// 超链接
		public AnalysisIntersectionResultInfo analysisIntersectionResultInfo;// 图形
		public int height;
		public double lat;
		public double lon;
		private int type;
		public Object value;
		public int width;

		public TableCell(Object paramValue, int paramWidth, int paramHeight,
				int paramType) {
			this.value = paramValue;
			this.width = paramWidth;
			this.height = paramHeight;
			this.type = paramType;
		}
	}

	public static class TableRow {
		private TableExpandableAdapter.TableCell[] cells;

		public TableRow(TableExpandableAdapter.TableCell[] paramArrayOfTableCell) {
			this.cells = paramArrayOfTableCell;
		}

		public TableExpandableAdapter.TableCell getCellValue(int paramInt) {
			if (paramInt >= this.cells.length)
				return null;
			return this.cells[paramInt];
		}

		public int getSize() {
			return this.cells.length;
		}
	}

	class TableRowView extends LinearLayout {
		public TableRowView(final Context paramContext,
				TableExpandableAdapter.TableRow paramTableRow, int paramInt,
				boolean paramBoolean) {
			super(paramContext);
			int bgColor = 0;
			if (paramTableRow.getSize() == 2) {
				bgColor = (paramInt % 2 == 0) ? Color.parseColor("#CCCCCC")
						: Color.parseColor("#FFFFFF");
			} else {
				bgColor = (paramInt % 2 == 0) ? Color.parseColor("#CCEEEE")
						: Color.parseColor("#CCDDEE");
			}

			int j = 0;
			while (j < paramTableRow.getSize()) {
				final TableExpandableAdapter.TableCell localTableCell = paramTableRow
						.getCellValue(j);
				if (localTableCell == null)
					return;
				if (localTableCell.type == TableCell.STRING) {
					LinearLayout.LayoutParams localLayoutParams1 = new LinearLayout.LayoutParams(
							localTableCell.width, -1);
					localLayoutParams1.setMargins(0, 0, 2, 2);
					TextView localTextView1 = new TextView(paramContext);
					localTextView1.setTextColor(Color.BLACK);
					localTextView1.setGravity(17);
					localTextView1.setMinHeight(localTableCell.height);
					localTextView1.setBackgroundColor(bgColor);
					localTextView1
							.setText(String.valueOf(localTableCell.value));
					addView(localTextView1, localLayoutParams1);
				} else if (localTableCell.type == TableCell.STRING_SUPER) {
					LinearLayout.LayoutParams localLayoutParams2 = new LinearLayout.LayoutParams(
							localTableCell.width, -1);
					localLayoutParams2.setMargins(0, 0, 2, 2);
					TextView localTextView2 = new TextView(paramContext);
					localTextView2.setTextColor(Color.BLUE);
					localTextView2.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
					localTextView2.setGravity(17);
					localTextView2.setMinHeight(localTableCell.height);
					localTextView2.setBackgroundColor(bgColor);
					localTextView2
							.setText(String.valueOf(localTableCell.value));
					localTextView2
							.setOnClickListener(new View.OnClickListener() {
								public void onClick(View paramView) {

									GisQueryApplication.getApp().getGeoAnalysisTool().showAnalysisIntersectionResultInfo(localTableCell.analysisIntersectionResultInfo);
									Intent localIntent = new Intent(
											mActivity, MainActivity.class);
									localIntent
											.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
									localIntent.putExtra("opt", 4368);
									paramContext.startActivity(localIntent);
								}
							});
					addView(localTextView2, localLayoutParams2);
				} else if (localTableCell.type == TableCell.CHECK_BOX) {

					CheckBox localCheckBox = new CheckBox(paramContext);
					localCheckBox.setFocusable(false);
					localCheckBox.setClickable(false);
					localCheckBox.setButtonDrawable(R.drawable.check_box_bk2);
					if (TableExpandableAdapter.this.m_selectList.size() > 0) {
						localCheckBox
								.setChecked((TableExpandableAdapter.this.m_selectList
										.get(paramInt - 1)).booleanValue());
					}

					localCheckBox.setGravity(17);

					localCheckBox.setTag("check_box");
					LinearLayout.LayoutParams localLayoutParams3 = new LinearLayout.LayoutParams(
							-2, -2);
					localLayoutParams3.gravity = Gravity.CENTER;
					localLayoutParams3.setMargins(6, 0, 0, 0);

					LinearLayout localLinearLayout = new LinearLayout(
							paramContext);
					localLinearLayout.setBackgroundColor(bgColor);
					localLinearLayout
							.addView(localCheckBox, localLayoutParams3);

					LinearLayout.LayoutParams localLayoutParams4 = new LinearLayout.LayoutParams(
							localTableCell.width, localTableCell.height);
					localLayoutParams4.setMargins(0, 0, 2, 2);
					localLayoutParams4.gravity = Gravity.CENTER;
					addView(localLinearLayout, localLayoutParams4);

				}
				j++;
			}

		}
	}

	private class checkBoxClick implements View.OnClickListener {
		int position = -1;

		public checkBoxClick(int paramInt) {
			this.position = paramInt;
		}

		public void onClick(View paramView) {
			CheckBox localCheckBox = (CheckBox) paramView
					.findViewWithTag("check_box");
			List<Boolean> localList = TableExpandableAdapter.this.m_selectList;
			if (TableExpandableAdapter.this.m_selectList.size() > 0) {
				if (localCheckBox != null) {
					boolean bl_sel = TableExpandableAdapter.this.m_selectList
							.get(this.position - 1).booleanValue();
					localList.set(this.position - 1, !bl_sel);
					localCheckBox.setChecked(!bl_sel);
				}
			}
			TableExpandableAdapter.this.expandeOpt(this.position);
		}
	}
}