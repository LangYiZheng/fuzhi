package com.guyu.android.gis.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.List;

import com.guyu.android.R;

public class InfoAdapter extends BaseAdapter {
	private List<String> lstName = null;
	private List<String> lstValue = null;
	private LayoutInflater mInflater = null;

	public InfoAdapter(Context paramContext, List<String> paramList1,
			List<String> paramList2) {
		this.mInflater = ((LayoutInflater) paramContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE));
		this.lstName = paramList1;
		this.lstValue = paramList2;
	}

	public int getCount() {
		return this.lstName.size();
	}

	public Object getItem(int paramInt) {
		return this.lstName.get(paramInt);
	}

	public long getItemId(int paramInt) {
		return paramInt;
	}

	public View getView(int paramInt, View paramView, ViewGroup paramViewGroup) {
		TextView localTextView1;
		TextView localTextView2;
		String str;
		if (paramInt < this.lstName.size()) {
			if (paramView == null)
				paramView = this.mInflater.inflate(
						R.layout.list_query_result_details, paramViewGroup,
						false);
			localTextView1 = (TextView) paramView.findViewById(R.id.textView1);
			localTextView2 = (TextView) paramView.findViewById(R.id.textView2);
			if (!(this.lstName.get(paramInt).equalsIgnoreCase("图片"))) {
				localTextView1.setText((CharSequence) this.lstName
						.get(paramInt));
				localTextView2.setText((CharSequence) this.lstValue
						.get(paramInt));
			}
			localTextView1.setText((CharSequence) this.lstName.get(paramInt));
			str = this.lstValue.get(paramInt);
			if ((str == null) || (str.length() <= 0)
					|| (!(str.contains(".jpg")))) {
				localTextView2.setText(str);
			} else {
				String[] arrayOfString = str.split(",");
				if ((arrayOfString == null) || (arrayOfString.length <= 0)) {
					localTextView2.setText(str);
				} else {
					localTextView2.setText(arrayOfString.length + "张");
				}
			}
		}
		return paramView;

	}
}