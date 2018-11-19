package com.guyu.android.gis.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;

import com.blankj.utilcode.util.ToastUtils;
import com.guyu.android.database.task.CaseDBMExternal;
import com.guyu.android.gis.app.GisQueryApplication;
import com.guyu.android.gis.common.Case;
import com.guyu.android.utils.UtilsTools;
import com.guyu.android.R;

public class CaseManagerActivity extends Activity {
	private List<Boolean> lstSelecteds = null;
	private Button mCaseDeleteButton = null;
	private ListView mIllegalCluesListView = null;
	private List<Case> mCaseObjs = new ArrayList<Case>();
	private EditText mCaseQueryEditText = null;
	private CheckBox mMultiOptBox = null;
	private LinearLayout mQueryLayout = null;
	private String caseType;
	private AdapterView.OnItemClickListener onItemClickListener1 = null;
	private AdapterView.OnItemClickListener onItemClickListener2 = null;
	private TextView mTitle;
	protected void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		setContentView(R.layout.activity_case_manager_h);
		initActivity();
	}

	private void initActivity() {
		initData();
		initWidget();
	}

	private void initData() {
		this.caseType = this.getIntent().getStringExtra("caseType");
		this.mCaseObjs.clear();
		List<Case> localList = new CaseDBMExternal(this)
				.getCasesByType(caseType);
		if (localList != null) {
			this.mCaseObjs.addAll(localList);
			this.lstSelecteds = new ArrayList<Boolean>();
		}
		for (int i = 0; i < this.mCaseObjs.size(); i++) {
			this.lstSelecteds.add(Boolean.valueOf(false));
		}
	}

	private void initWidget() {
		this.mQueryLayout = ((LinearLayout) findViewById(R.id.ll_query));
		this.mQueryLayout.setVisibility(View.GONE);
		this.mCaseQueryEditText = ((EditText) findViewById(R.id.et_case_query));
		this.mTitle = (TextView) findViewById(R.id.tv_title);
		if ("dtxc".equals(caseType)) {
			this.mTitle.setText("巡查管理");
		} else if ("wfxs".equals(caseType)) {
			this.mTitle.setText("线索管理");
		}

		// 启高
		else if ("change".equals(caseType)) {
			this.mTitle.setText("林权变更流转管理");
		}
		// 启高
		else if ("linxia".equals(caseType)) {
			this.mTitle.setText("林下经济管理");
		}
		// 启高
		else if ("linmu".equals(caseType)) {
			this.mTitle.setText("林木采伐管理");
		}
		// 启高
		else if ("mucai".equals(caseType)) {
			this.mTitle.setText("木材检查管理");
		}
		// 启高
		else if ("weifa".equals(caseType)) {
			this.mTitle.setText("林业行政处罚立案管理");
		}
		// 启高
		else if ("dongwu".equals(caseType)) {
			this.mTitle.setText("野生动物养殖管理");
		}

		this.mIllegalCluesListView = ((ListView) findViewById(R.id.lst_case));

		this.mIllegalCluesListView.setAdapter(new IllegalCluesSelectAdapter(
				this));
		this.mIllegalCluesListView.invalidateViews();// invalidate()是用来刷新View的
		this.onItemClickListener1 = new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> paramAdapterView,
					View paramView, int paramInt, long paramLong) {
				Intent localIntent = new Intent(
						CaseManagerActivity.this,CaseDetailsActivity.class);// 启高
				String id = CaseManagerActivity.this.mCaseObjs.get(paramInt)
						.getCaseId();
				String type = CaseManagerActivity.this.mCaseObjs.get(paramInt)
						.getCaseType();
				localIntent.putExtra("caseId", id);
				localIntent.putExtra("caseType", type);
				CaseManagerActivity.this.startActivityForResult(localIntent,
						4236);
			}
		};
		this.onItemClickListener2 = new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> paramAdapterView,
					View paramView, int paramInt, long paramLong) {
				List<Boolean> localList = CaseManagerActivity.this.lstSelecteds;
				if (paramInt < CaseManagerActivity.this.lstSelecteds.size()) {
					localList.set(paramInt,
							!(CaseManagerActivity.this.lstSelecteds
									.get(paramInt).booleanValue()));
				}
				CaseManagerActivity.this.mIllegalCluesListView
						.invalidateViews();
			}
		};
		this.mIllegalCluesListView
				.setOnItemClickListener(this.onItemClickListener1);// 林权管理点击事件
		this.mCaseDeleteButton = ((Button) findViewById(R.id.btn_case_delete));
		this.mCaseDeleteButton.setVisibility(View.GONE);
		this.mCaseDeleteButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View paramView) {
				CaseManagerActivity.this.deleteOpt();
			}
		});
		this.mMultiOptBox = ((CheckBox) findViewById(R.id.cb_more_opt));
		this.mMultiOptBox
				.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
					public void onCheckedChanged(
							CompoundButton paramCompoundButton,
							boolean paramBoolean) {
						if (paramBoolean) {
							CaseManagerActivity.this.mIllegalCluesListView
									.setOnItemClickListener(CaseManagerActivity.this.onItemClickListener2);
							CaseManagerActivity.this.mCaseDeleteButton
									.setVisibility(View.VISIBLE);
						} else {
							CaseManagerActivity.this.mIllegalCluesListView
									.invalidateViews();
							CaseManagerActivity.this.mIllegalCluesListView
									.setOnItemClickListener(CaseManagerActivity.this.onItemClickListener1);
							CaseManagerActivity.this.mCaseDeleteButton
									.setVisibility(View.INVISIBLE);
						}
					}
				});
	}

	/**
	 * 删除地块操作
	 */
	private void deleteOpt() {
		ArrayList<Case> localArrayList = new ArrayList<Case>();
		if ((this.lstSelecteds != null) && (this.lstSelecteds.size() > 0)) {
			for (int i = 0; i < this.lstSelecteds.size(); i++) {
				if (this.lstSelecteds.get(i)) {
					localArrayList.add(this.mCaseObjs.get(i));
				}
			}
			showDelDlg(localArrayList);
		} else {
			ToastUtils.showLong(  "请选择要删除的地块");
		}
	}

	private void showDelDlg(final List<Case> paramList) {
		new AlertDialog.Builder(this).setTitle("确认操作").setMessage("确认删除?")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface paramDialogInterface,
							int paramInt) {
						CaseDBMExternal caseDBMExternal = new CaseDBMExternal(
								CaseManagerActivity.this);
						caseDBMExternal.deleteMultiCase(paramList);
						initData();
						CaseManagerActivity.this.mIllegalCluesListView
								.invalidateViews();
					}
				}).setNegativeButton("取消", null).show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 4236) {
			initData();
			CaseManagerActivity.this.mIllegalCluesListView.invalidateViews();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	public void onBackOnClick(View paramView) {
		Intent localIntent = new Intent(
				CaseManagerActivity.this,MainActivity.class);
		localIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		startActivity(localIntent);
		finish();
	}

	private class IllegalCluesSelectAdapter extends BaseAdapter {
		private LayoutInflater mInflater = null;

		public IllegalCluesSelectAdapter(Context paramContext) {
			this.mInflater = ((LayoutInflater) paramContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE));
		}

		public int getCount() {
			return CaseManagerActivity.this.mCaseObjs.size();
		}

		public Object getItem(int paramInt) {
			return CaseManagerActivity.this.mCaseObjs.get(paramInt);
		}

		public long getItemId(int paramInt) {
			return paramInt;
		}

		public View getView(int paramInt, View paramView,
				ViewGroup paramViewGroup) {
			String caseName = "";
			if (paramView == null) {
				paramView = this.mInflater.inflate(R.layout.record_item_case,
						paramViewGroup, false);
			}
			TextView localTextView1 = (TextView) paramView
					.findViewById(R.id.txt_case_name);
			TextView localTextView2 = (TextView) paramView
					.findViewById(R.id.txt_case_createtime);
			TextView localTextView3 = (TextView) paramView
					.findViewById(R.id.txt_case_upstate);
			CheckBox localCheckBox = (CheckBox) paramView
					.findViewById(R.id.cb_check);
			String dateTime = "";
			if ((CaseManagerActivity.this.mCaseObjs != null)
					&& (paramInt < CaseManagerActivity.this.mCaseObjs.size())) {

				caseName = CaseManagerActivity.this.mCaseObjs.get(paramInt)
						.getCaseName();
				if ("".equals(caseName)) {
					localTextView1.setText("未命名案件");
				} else {
					localTextView1.setText(caseName);
				}

				dateTime = CaseManagerActivity.this.mCaseObjs.get(paramInt)
						.getCreateTime();
				localTextView2.setText(dateTime);

				if (CaseManagerActivity.this.mCaseObjs.get(paramInt)
						.getUpState() != 1) {
					localTextView3.setText("未上报");
					localCheckBox.setVisibility(View.VISIBLE);
					if ((CaseManagerActivity.this.lstSelecteds != null)
							&& (CaseManagerActivity.this.lstSelecteds.size() > paramInt)) {
						localCheckBox
								.setChecked(CaseManagerActivity.this.lstSelecteds
										.get(paramInt).booleanValue());
					}

				} else {
					localTextView3.setText("已上报（案卷编号："
							+ CaseManagerActivity.this.mCaseObjs.get(paramInt)
									.getRecId() + "）");
					localCheckBox.setVisibility(View.INVISIBLE);
				}

			}
			return paramView;
		}
	}
}