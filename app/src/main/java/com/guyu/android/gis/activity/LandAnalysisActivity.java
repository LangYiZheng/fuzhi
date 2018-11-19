package com.guyu.android.gis.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;

import com.guyu.android.gis.app.GisQueryApplication;
import com.guyu.android.R;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class LandAnalysisActivity extends Activity {
	private static int ROW_COUNT = 0;
	private static final String TAG = "LandAnalysisActivity";
	private LinearLayout mLandNameLayout = null;
	private int m_nIconWidth = 60;
	private int m_nRowHeight = 60;
	private int m_nScreenHeight;
	private int m_nScreenWidth;
	private List<View> pageViews = new ArrayList<View>();

	protected void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		setContentView(R.layout.activity_land_analysis);

		//initActivity();
	}


	private void initTopLandNameView() {
//		this.mLandNameLayout = ((LinearLayout) findViewById(2131165275));
	}




}