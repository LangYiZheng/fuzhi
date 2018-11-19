package com.guyu.android.gis.activity;

import com.guyu.android.gis.opt.CantonZoneOpt;
import com.guyu.android.R;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class CantonZoneNaviFragment extends Fragment {
	private CantonZoneOpt cantonZoneOpt;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		return inflater.inflate(R.layout.layout_area_select, container, false);
	}
	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);
		cantonZoneOpt = new CantonZoneOpt(view);
		cantonZoneOpt.initCantonZoneList();
	}

}
