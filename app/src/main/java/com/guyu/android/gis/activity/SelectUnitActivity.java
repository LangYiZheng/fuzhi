package com.guyu.android.gis.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

import com.blankj.utilcode.util.ToastUtils;
import com.guyu.android.database.sync.UnitDBMExternal;
import com.guyu.android.gis.app.GisQueryApplication;
import com.guyu.android.gis.common.Unit;
import com.guyu.android.utils.SysConfig;
import com.guyu.android.utils.UtilsTools;
import com.guyu.android.R;

public class SelectUnitActivity extends Activity {
	private List<Unit> mAllOfficeData = new ArrayList<Unit>();
	private UnitDBMExternal mUnitDBExOpt = null;
	private ListView mOfficeListView = null;
	private List<Unit> mOfficeLstData = new ArrayList<Unit>();

	private void filterOfficeDataByCanton() {
	}

	private void initActivity() {
		initData();
		initWidget();
		setTilte("单位选择");
		refresh();
	}

	private void initData() {
		int cantonID = 0;
		if (SysConfig.mCurrentCanton != null) {// 当前选中的区域对象
			cantonID = SysConfig.mCurrentCanton.getCantonId();
		} else {
			ToastUtils.showLong(  "请选择区域信息");
		}

		List localList = this.mUnitDBExOpt.getOfficeInfosByCantonCode(cantonID);// 根据区域编码获取单位
		if (localList == null) {
			return;
		} else {
			this.mAllOfficeData.addAll(localList);
		}
		initOfficeData();
	}

	private void initOfficeData() {
		if (this.mAllOfficeData != null) {
			this.mOfficeLstData.clear();
			this.mOfficeLstData.addAll(this.mAllOfficeData);
		}
		filterOfficeDataByCanton();
	}

	private void initWidget() {
		this.mOfficeListView = ((ListView) findViewById(R.id.lst_office_select));
		this.mOfficeListView.setAdapter(new OfficeAdapter(this,
				this.mOfficeLstData));
		this.mOfficeListView
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					public void onItemClick(AdapterView<?> paramAdapterView,
							View paramView, int paramInt, long paramLong) {
						SysConfig.mUnitInfo = SelectUnitActivity.this.mOfficeLstData
								.get(paramInt);
						SelectUnitActivity.this.reusltOk();
					}
				});
	}

	private void refresh() {
	}

	private void reusltOk() {
		setResult(0);
		finish();
	}

	private void setTilte(String paramString) {
		((TextView) findViewById(R.id.tv_title)).setText(paramString);
	}

	public void onBackBtnClick(View paramView) {
		// SysConfig.mUnit = null;
		SysConfig.mHumanInfo = null;
		setResult(-1);
		finish();
	}

	protected void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		setContentView(R.layout.activity_select_office);

		this.mUnitDBExOpt = new UnitDBMExternal(this);
		initActivity();
	}

	private class OfficeAdapter extends BaseAdapter {
		private LayoutInflater mInflater = null;
		private List<Unit> officeInfos = null;

		public OfficeAdapter(Context paramContext, List<Unit> paramList) {
			this.officeInfos = paramList;
			this.mInflater = ((LayoutInflater) paramContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE));
		}

		public int getCount() {
			return this.officeInfos.size();
		}

		public Object getItem(int paramInt) {
			return this.officeInfos.get(paramInt);
		}

		public long getItemId(int paramInt) {
			return paramInt;
		}

		public View getView(int paramInt, View paramView,
				ViewGroup paramViewGroup) {
			if ((this.officeInfos != null)
					&& (paramInt < this.officeInfos.size())) {
				if (paramView == null)
					paramView = this.mInflater.inflate(R.layout.spinner_office,
							paramViewGroup, false);
				paramView.findViewById(R.id.sp_value);
				((TextView) paramView.findViewById(R.id.tv_office_name))
						.setText(this.officeInfos.get(paramInt)
								.getUnitName());
			}
			return paramView;
		}
	}
}
