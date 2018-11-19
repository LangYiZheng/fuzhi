package com.guyu.android.gis.activity;

import com.guyu.android.gis.opt.FullTextQueryOpt;
import com.guyu.android.R;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;



public class FullTextQueryFragment extends Fragment {
	private FullTextQueryOpt fullTextQueryOpt;
	private MainActivity mainActivity;
	public MainActivity getMainActivity() {
		return mainActivity;
	}

	public void setMainActivity( MainActivity mainActivity) {
		this.mainActivity = mainActivity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		return inflater
				.inflate(R.layout.layout_fulltextquery, container, false);
	}
	
	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		fullTextQueryOpt = new FullTextQueryOpt(view,mainActivity);
		fullTextQueryOpt.initQuery();
		
	}
	public void refresh(){
		fullTextQueryOpt.doRefresh();
	}	
}
