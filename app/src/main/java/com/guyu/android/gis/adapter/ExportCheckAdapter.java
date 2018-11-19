package com.guyu.android.gis.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.guyu.android.R;

public class ExportCheckAdapter extends BaseAdapter {
	private List<Boolean> isUplst = null;
	private boolean[] mCheckList = null;
	private LayoutInflater mInflater = null;
	private List<String> mNameList = null;

	public ExportCheckAdapter(Context paramContext, List<String> paramList,
			boolean[] paramArrayOfBoolean, List<Boolean> paramList1) {
		this.mNameList = paramList;
		this.mInflater = ((LayoutInflater) paramContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE));
		this.mCheckList = paramArrayOfBoolean;
		this.isUplst = paramList1;
		if(mNameList.size()>isUplst.size()){
			int size = mNameList.size()/2;
			List<String> nameList = new ArrayList<>();
			for (int i = 0; i < size; i++) {
				nameList.add(mNameList.get(i));
			}
			mNameList = nameList;
		}
	}

	public int getCount() {
		return this.mNameList.size();
	}

	public Object getItem(int paramInt) {
		return this.mNameList.get(paramInt);
	}

	public long getItemId(int paramInt) {
		return paramInt;
	}

	public View getView(int paramInt, View paramView, ViewGroup paramViewGroup) {
		if (paramInt < this.mNameList.size()) {
			if (paramView == null)
				paramView = this.mInflater.inflate(
						R.layout.list_row_icon_text_check, paramViewGroup,
						false);
			((ImageView) paramView.findViewById(R.id.icon)).setVisibility(8);
			TextView localTextView1 = (TextView) paramView
					.findViewById(R.id.title);
			TextView localTextView2 = (TextView) paramView
					.findViewById(R.id.txt_state);
			CheckBox localCheckBox = (CheckBox) paramView
					.findViewById(R.id.check);
			localTextView1.setText((CharSequence) this.mNameList.get(paramInt));
			localCheckBox.setChecked(this.mCheckList[paramInt]);
			if (this.isUplst.get(paramInt)) {
				localTextView2.setText("已上传");
			} else {
				localTextView2.setText("未上传");
			}
			localCheckBox.setFocusable(false);
			localCheckBox.setClickable(false);
		}
		else
		{
			Log.e("LayerAdapter", "getView失败");
		}		
		return paramView;
	}
}