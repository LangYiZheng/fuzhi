package com.guyu.android.gis.adapter;

import java.util.List;

import com.guyu.android.R;
import com.guyu.android.gis.common.CantonZone;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * 自定义适配器类
 * @author guqiang
 *
 */
public class CantonZoneAdapter extends BaseAdapter {
	private List<CantonZone> mList;
	private Context mContext;

	public CantonZoneAdapter(Context Context, List<CantonZone> List) {
		this.mContext = Context;
		this.mList = List;
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public Object getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
    /**
     * 下面是重要代码
     */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater _LayoutInflater=LayoutInflater.from(mContext);
		convertView=_LayoutInflater.inflate(R.layout.canton_list_item, null);
		if(convertView!=null)
		{
			TextView _TextView1=(TextView)convertView.findViewById(R.id.textView1);
			TextView _TextView2=(TextView)convertView.findViewById(R.id.textView2);
			if(mList.get(position).getZoneid()==null){
				return null;
			}
			_TextView1.setText(mList.get(position).getZonename());
			_TextView2.setText(mList.get(position).getZonecode()+","+mList.get(position).getType());
			_TextView2.setVisibility(View.GONE); 
		}
		return convertView;
	}
}