package com.guyu.android.gis.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.List;

import com.guyu.android.R;
import com.guyu.android.gis.common.DictItem;
import com.guyu.android.gis.common.DictItemTwo;

public class DictItemAdapter<T> extends BaseAdapter {
	private List<T> mDictItemLst = null;
	private LayoutInflater mInflater = null;

	public DictItemAdapter(Context paramContext, List<T> al_dictItem) {
		this.mInflater = ((LayoutInflater) paramContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE));
		this.mDictItemLst = al_dictItem;
	}

	public int getCount() {
		return this.mDictItemLst.size();
	}

	public T getItem(int paramInt) {
		return this.mDictItemLst.get(paramInt);
	}

	public long getItemId(int paramInt) {
		return paramInt;
	}

	public View getView(int paramInt, View paramView, ViewGroup paramViewGroup) {
		TextView localTextView1;
		if (paramInt < this.mDictItemLst.size()) {
			if (paramView == null)
				paramView = this.mInflater.inflate(
						R.layout.simple_spinner_item, paramViewGroup, false);
			localTextView1 = (TextView) paramView;
			if (mDictItemLst.get(paramInt) instanceof DictItem) {
				localTextView1.setText(((DictItem) this.mDictItemLst
						.get(paramInt)).getItemLabel());
			} else if (mDictItemLst.get(paramInt) instanceof DictItemTwo) {
				localTextView1.setText(((DictItemTwo) this.mDictItemLst
						.get(paramInt)).getItemtwoLabel());
			}
		}
		return paramView;
	}
}