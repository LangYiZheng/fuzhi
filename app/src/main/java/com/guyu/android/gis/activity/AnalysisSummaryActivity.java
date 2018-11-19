package com.guyu.android.gis.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.guyu.android.R;
import com.guyu.android.gis.app.GisQueryApplication;
import com.guyu.android.gis.common.AnalysisLayerResultInfo;
import com.guyu.android.gis.opt.MapOperate;
import com.guyu.android.utils.MathUtils;

import java.util.ArrayList;
import java.util.List;

public class AnalysisSummaryActivity extends Activity implements View.OnClickListener {
    private ListView mListView = null;
    private ResultAdapter mAdapter = null;
    private Class<Activity> m_strIntentAction = null;
    private View view;
    private Button pieChart;
    private Button barChart;
    private ArrayList<AnalysisLayerResultInfo> analysisResultInfo;
    private int proCount;

    @Override
    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        view = View.inflate(AnalysisSummaryActivity.this, R.layout.activity_land_tmp_analysis_h, null);
        setContentView(view);
        initView();
        Intent localIntent = getIntent();
        this.m_strIntentAction = (Class<Activity>) localIntent.getSerializableExtra("action");
        initWidget();

    }

    private void initView() {
        pieChart = view.findViewById(R.id.btn_pieChart);
        barChart = view.findViewById(R.id.btn_barChart);
        pieChart.setOnClickListener(this);
        barChart.setOnClickListener(this);
    }

    private void initWidget() {
        this.analysisResultInfo = new ArrayList<>();
        ArrayList<AnalysisLayerResultInfo> analysisResultInfo = GisQueryApplication.getApp().getAnalysisResultInfo();
        for (AnalysisLayerResultInfo analysisLayerResultInfo : analysisResultInfo) {
            if (analysisLayerResultInfo.getArea()>0){
                this.analysisResultInfo.add(analysisLayerResultInfo);
            }
        }

        this.mListView = ((ListView) findViewById(R.id.listView1));
        this.mAdapter = new ResultAdapter(this, this.analysisResultInfo);
        this.mListView.setAdapter(this.mAdapter);
        this.mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> paramAdapterView, View paramView, int paramInt, long paramLong) {
                AnalysisLayerResultInfo analysisLayerResultInfo = (AnalysisLayerResultInfo) AnalysisSummaryActivity.this.mAdapter.getItem(paramInt);
                if (analysisLayerResultInfo.getArea() > 0) {
                    AnalysisSummaryActivity.this.intoLandAnalysisRecordActivity(analysisLayerResultInfo);
                }
            }
        });
        this.mListView.invalidateViews();
    }

    private void intoLandAnalysisRecordActivity(AnalysisLayerResultInfo analysisLayerResultInfo) {
        // 利用Application 传递当前选择图层分析结果

        GisQueryApplication.getApp().setAnalysisLayerResultInfo(analysisLayerResultInfo);
        Intent localIntent = new Intent(this, LandAnalysisRecordDetailsActivity.class);
        localIntent.putExtra("action", AnalysisSummaryActivity.class);
        startActivity(localIntent);
    }

    /**
     * 返回
     *
     * @param paramView
     */
    public void backOnClick(View paramView) {
        if (CaseDetailsActivity.class.equals(m_strIntentAction)
                || TaskDetailsActivity.class.equals(m_strIntentAction)) {
            MapOperate.geoAnalysisTool.dispose();
        }
        Intent localIntent = new Intent(this, m_strIntentAction);
        localIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(localIntent);
        finish();
    }

    @Override
    public void onClick(View view) {
        boolean flag = true;
        Intent intent = new Intent(AnalysisSummaryActivity.this, MPAndroidChartActivity.class);
        switch (view.getId()) {
            case R.id.btn_pieChart:
                flag = true;
                break;
            case R.id.btn_barChart:
                flag = false;
                break;
        }

        if (analysisResultInfo!=null&&analysisResultInfo.get(0).getArea() <= 0) {
            return;
        }
        intent.putExtra("flag", flag);
        intent.putExtra("proCount", proCount);
        startActivity(intent);
    }

    class ResultAdapter extends BaseAdapter {
        LayoutInflater mInflater = null;
        ArrayList<AnalysisLayerResultInfo> mAnalysisResultInfo;

        public ResultAdapter(Context paramContext, ArrayList<AnalysisLayerResultInfo> analysisResultInfo) {
            this.mInflater = ((LayoutInflater) paramContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE));
            this.mAnalysisResultInfo = analysisResultInfo;
        }

        public int getCount() {
            return this.mAnalysisResultInfo.size();
        }

        public Object getItem(int paramInt) {
            proCount = paramInt ;
            return this.mAnalysisResultInfo.get(paramInt);
        }

        public long getItemId(int paramInt) {
            return paramInt;
        }

        public View getView(int paramInt, View paramView, ViewGroup paramViewGroup) {
            AnalysisLayerResultInfo analysisLayerResultInfo = mAnalysisResultInfo.get(paramInt);
            if (paramInt < mAnalysisResultInfo.size()) {

                if (paramView == null)
                    paramView = this.mInflater.inflate(R.layout.list_analysis_result_item_child, paramViewGroup, false);
                TextView localTextView1 = (TextView) paramView.findViewById(R.id.textView1);
                TextView localTextView2 = (TextView) paramView.findViewById(R.id.textView2);
                TextView localTextView3 = (TextView) paramView.findViewById(R.id.textView3);
                TextView localTextView4 = (TextView) paramView.findViewById(R.id.textView4);
                // TextView localTextView5 = (TextView) paramView
                // .findViewById(R.id.textView5);
                localTextView1.setText("" + (paramInt + 1));
                localTextView2.setText(analysisLayerResultInfo.getLayerName());
                localTextView2.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
                localTextView2.setTextColor(Color.BLUE);
                double d = MathUtils.GetAccurateNumber(analysisLayerResultInfo.getArea(), 2);
                localTextView3.setText(d + " 平方米");
                localTextView4.setText("查看详细");
                localTextView4.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View paramView) {
                    }
                });
                // localTextView5.setText(MathUtils.GetAccurateNumber(100.0D * d
                // / LandAnalysisOpt.getOriginalGeometry().GetArea(), 2) + "%");
            }
            return paramView;
        }

        public void refreshData(List<AnalysisLayerResultInfo> paramList) {
            if ((paramList == null) || (paramList.size() <= 0))
                return;
            this.mAnalysisResultInfo.clear();
            this.mAnalysisResultInfo.addAll(paramList);
        }
    }
}