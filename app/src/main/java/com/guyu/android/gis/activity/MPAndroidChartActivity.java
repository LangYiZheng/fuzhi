package com.guyu.android.gis.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import com.guyu.android.R;
import com.guyu.android.gis.app.GisQueryApplication;
import com.guyu.android.gis.common.AnalysisIntersectionResultInfo;
import com.guyu.android.gis.common.AnalysisLayerResultInfo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import lecho.lib.hellocharts.view.ColumnChartView;

public class MPAndroidChartActivity extends Activity {

    private PieChart pieChart;
    private BarChart barChart;
    private float mCurPosY;
    private float mPosY;
    private boolean flag;
    private ArrayList<AnalysisIntersectionResultInfo> intersections;
    private TextView areaTextView, plateTextView;
    private float area;
    //    private XAxis xAxis; //X坐标轴
//    private YAxis yAxis; //Y
    private int[] colors = {
            Color.parseColor("#87481f"),
            Color.parseColor("#003a6c"),
            Color.parseColor("#7fb80e"),
            Color.parseColor("#deab8a"),
            Color.parseColor("#ef5b9c"),
            Color.parseColor("#45224a"),
            Color.parseColor("#d9d6c3"),
            Color.parseColor("#f58f98"),
            Color.parseColor("#a3cf62"),
            Color.parseColor("#d71345"),
            Color.parseColor("#f47920"),
            Color.parseColor("#fcaf17"),
            Color.parseColor("#ea66a6"),
            Color.parseColor("#2a5caa"),
            Color.parseColor("#f8aba6"),
            Color.parseColor("#f05b72"),
            Color.parseColor("#f391a9"),
            Color.parseColor("#afdfe4"),
            Color.parseColor("#33a3dc"),
            Color.parseColor("#00ae9d")
    };
    private AlertDialog dialog;
    private int count;
    private ArrayList<String> originalfields;
    private ArrayList<String> displayfields;
    private Map attributes;
    private ArrayList<AnalysisLayerResultInfo> analysisResultInfo;
    private AnalysisLayerResultInfo analysisLayerResultInfo;
    private String layerName;
    private HashMap<String, Float> hashMap;
    private ArrayList<Map.Entry<String, Float>> arrayList;
    private ColumnChartView mColumn;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_chart);
        areaTextView = findViewById(R.id.area);
        plateTextView = findViewById(R.id.plate);
        flag = getIntent().getExtras().getBoolean("flag");
        count = getIntent().getExtras().getInt("proCount");
        analysisResultInfo = GisQueryApplication.getApp().getAnalysisResultInfo();

        analysisLayerResultInfo = analysisResultInfo.get(count);

        intersections = analysisLayerResultInfo.getIntersections();
        area = (float) analysisLayerResultInfo.getArea();
        initData(analysisLayerResultInfo);
        showPieChartOrBarChart(flag);


    }

    private void initData(AnalysisLayerResultInfo analysisLayerResultInfo) {
        //key集合
        originalfields = analysisLayerResultInfo
                .getOriginalfields();
        //描述
        displayfields = analysisLayerResultInfo
                .getDisplayfields();
//        ArrayList<AnalysisIntersectionResultInfo> intersections = analysisLayerResultInfo
//                .getIntersections();
        float area = (float) analysisLayerResultInfo.getArea();
        areaTextView.setText("面 积：" + area + "平方米");
        layerName = analysisLayerResultInfo.getLayerConfig().getName();
        plateTextView.setText("图 层：" + layerName);
//        hashMap = null;
        if (analysisLayerResultInfo.getLayerName().equals("DLTB")) {
            hashMap = new HashMap<>();

            HashMap<String, HashMap<String, Float>> titleMap = new HashMap<>();
            // dlmc value
            HashMap<String, Float> dlmcMap;

            ArrayList<String> titleList = new ArrayList();

            arrayList = new ArrayList();
            for (AnalysisIntersectionResultInfo intersection : intersections) {
                String dlmc = (String) intersection.getAttributes().get("DLMC");

                if (hashMap.containsKey(dlmc)) {
                    Float area1 = intersection.getArea().floatValue();
                    Float area2 = hashMap.get(dlmc);
                    hashMap.put(dlmc, area1 + area2);
                } else {
                    Float area1 = intersection.getArea().floatValue();
                    hashMap.put(dlmc, area1);
                }

                String dlQSDWMC = (String) intersection.getAttributes().get("QSDWMC");
                if (!titleList.contains(dlQSDWMC)) {
                    titleList.add(dlQSDWMC);
                    titleMap.put(dlQSDWMC,new HashMap<>());
                }
            }

            for (String titleKey : titleList) {
                dlmcMap = new HashMap<>();
                for (AnalysisIntersectionResultInfo intersection : intersections) {
                    if (intersection.getAttributes().get("QSDWMC").toString().equals(titleKey)) {
                        String dlmc = (String) intersection.getAttributes().get("DLMC");

                        if (titleMap.get(titleKey).containsKey(dlmc)) {
                            HashMap<String, Float> map = titleMap.get(titleKey);
                            Float dlmc1Value = (Float) map.get(dlmc);
                            Float dlmc2Value = (Float) intersection.getArea().floatValue();
                            dlmcMap.put(dlmc, dlmc1Value + dlmc2Value);
                        } else {
                            dlmcMap.put(dlmc, intersection.getArea().floatValue());
                        }

                    }

                }
                titleMap.put(titleKey, dlmcMap);
            }

            for (Map.Entry<String, Float> stringFloatEntry : hashMap.entrySet()) {
                arrayList.add(stringFloatEntry);
            }

        }

    }

    private void showPieChartOrBarChart(boolean flag) {
        pieChart = findViewById(R.id.pieChart);
        barChart = findViewById(R.id.barChart);
        mColumn = findViewById(R.id.columnChart);
        if (flag) {
            pieChart.setVisibility(View.VISIBLE);
            barChart.setVisibility(View.GONE);
            setSlide(pieChart);
            initPieChart();
            setPieData();
        } else {
            pieChart.setVisibility(View.GONE);
            mColumn.setVisibility(View.VISIBLE);
//            barChart.setVisibility(View.VISIBLE);
//            setSlide(barChart);
//            initBarChart(barChart);
//            setBarData();


        }
    }

    private void setSlide(View view) {
        view.setOnTouchListener(new View.OnTouchListener() {

            private float mCurPosX;
            private float mPosX;

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        mPosY = event.getY();
                        mPosX = event.getX();
                        Log.e("mPosY", "" + mPosY);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        mCurPosY = event.getY();
                        mCurPosX = event.getX();
                        Log.e("mCurPosY", "" + mCurPosY);
                        break;
                    case MotionEvent.ACTION_UP:
                        Log.e("ACTION_UP", "1111111");
                        if (Math.abs(mCurPosX - mPosX) > 30) {
                            return true;
                        }
                        if (mCurPosY - mPosY > 0 && (mCurPosY - mPosY) > 80) {
                            Log.e("ACTION_UP", "向下移动");
//                            initPieChart();
                            //向下移动
                            if (count > 0) {
                                count--;
                                analysisLayerResultInfo = analysisResultInfo.get(count);
                                intersections = analysisLayerResultInfo.getIntersections();
                                if (flag) {
//                                    barChart.notifyDataSetChanged();
//                                    barChart.invalidate();
                                    initData(analysisLayerResultInfo);
                                    pieChart.notifyDataSetChanged();
                                    setPieData();

                                } else {
                                    initData(analysisLayerResultInfo);
                                    barChart.notifyDataSetChanged();
                                    setBarData();
                                }

                            }
//                            Toast.makeText(MPAndroidChartActivity.this, "下", Toast.LENGTH_SHORT).show();
                            return true;
                        } else if (mCurPosY - mPosY < 0 && (Math.abs(mCurPosY - mPosY) > 80)) {
//                            Log.e("ACTION_UP", "向上移动");
                            if (count < analysisResultInfo.size() - 1) {
                                count++;
                                analysisLayerResultInfo = analysisResultInfo.get(count);
                                intersections = analysisLayerResultInfo.getIntersections();
                                if (flag) {
                                    initData(analysisLayerResultInfo);
                                    pieChart.notifyDataSetChanged();
                                    setPieData();
                                } else {
                                    initData(analysisLayerResultInfo);
                                    barChart.notifyDataSetChanged();
                                    setBarData();
                                }
                            }
//                            Toast.makeText(MPAndroidChartActivity.this, "上", Toast.LENGTH_SHORT).show();
                            //向上移动
                            return true;
                        }
                        break;
                }
                return false;
            }
        });
    }

    private void initBarChart(BarChart mBarChart) {

        mBarChart.getDescription().setEnabled(false);
        mBarChart.setPinchZoom(false);

        float ratio = (float) intersections.size() / (float) 6;
        if (analysisLayerResultInfo.getLayerName().equals("DLTB")){
            ratio = (float) arrayList.size() / (float) 6;
        }
        mBarChart.zoom(ratio, 1f, 0, 0);

        mBarChart.animateY(1500);
        //设置是否可以缩放
        mBarChart.setScaleEnabled(false);
        //设置是否可以触摸
        mBarChart.setTouchEnabled(true);
        //设置是否可以拖拽
        mBarChart.setDragEnabled(true);
        //获取图例对象
        Legend legend = mBarChart.getLegend();
        //设置图例不显示
        legend.setEnabled(false);


        //设置不显示网格线，保留水平线
        XAxis xAxis = mBarChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        xAxis.setDrawGridLines(false);

        xAxis.setGridLineWidth(1);
        //设置为true当一个页面显示条目过多，X轴值隔一个显示一个
        xAxis.setGranularityEnabled(true);
        //设置最小间隔，防止当放大时，出现重复标签。
        xAxis.setGranularity(1f);
        //一个界面显示6个Lable。那么这里要设置7个
        xAxis.setLabelCount(6);
        if (analysisLayerResultInfo.getLayerName().equals("DLTB")) {
            xAxis.setValueFormatter((value, axis) -> {
                int idx = (int) value;
                return arrayList.get(idx).getKey();
            });
        }
        //将自定义的横坐标设置上去
        if (analysisLayerResultInfo.getLayerName().equals("CKQ")) {
            xAxis.setValueFormatter((value, axis) -> {
                int idx = (int) value;

                if (intersections.size() < 6) {

                    return idx < intersections.size() ? (String) intersections.get(idx).getAttributes().get("QLR") : "";
                }
                return (String) intersections.get(idx).getAttributes().get("QLR");

            });
        }
        //设置X轴字体显示角度
//        xAxis.setLabelRotationAngle(45f);

        xAxis.setTextSize(10f);

        //左边Y轴
        YAxis leftYAxis = mBarChart.getAxisLeft();
        leftYAxis.setLabelCount(6);
        //设置从Y轴左侧发出横线
        leftYAxis.setDrawGridLines(true);

        leftYAxis.setAxisMinimum(0f);
        int flag = (int) (area / 2);
        for (AnalysisIntersectionResultInfo intersection : intersections) {

//            BigDecimal bd = new BigDecimal(intersection.getArea());
//            bd = bd.setScale(2, RoundingMode.HALF_UP);
            if (flag < intersection.getArea()) {
                flag = (int) area;
            }
        }
        leftYAxis.setAxisMaximum(flag);
        //设置显示左边Y坐标
        leftYAxis.setEnabled(true);
        leftYAxis.setTextSize(12f);
//        leftYAxis.setSpaceBottom(13f);
//        leftYAxis.setSpaceTop(15f);
        leftYAxis.setTypeface(Typeface.DEFAULT_BOLD);
        //则将其设置为true隐藏Y轴
        leftYAxis.setDrawAxisLine(true);
        //右边Y轴
        YAxis rightYAxis = mBarChart.getAxisRight();
        //设置隐藏右边y坐标
        rightYAxis.setEnabled(false);

        barChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                int x = (int) e.getX();
                if (analysisLayerResultInfo.getLayerName().equals("DLTB")) {
                    return;
                }
                if (x >= intersections.size()) {
                    return;
                }
                attributes = intersections.get(x).getAttributes();
                dialog = new AlertDialog.Builder(MPAndroidChartActivity.this).create();
                View view = View.inflate(MPAndroidChartActivity.this, R.layout.dialog_chart, null);
                dialog.setView(view);
                LinearLayout ll = view.findViewById(R.id.dialog_chart_ll);
                TextView child = null;
                for (int i = 0; i < originalfields.size(); i++) {
                    child = new TextView(MPAndroidChartActivity.this);
                    Object o =  attributes.get(originalfields.get(i));
                    if (o == null){
                        o = "";
                    }
                    child.setText(displayfields.get(i) + "：" + o);
                    child.setPadding(10, 10, 10, 10);
                    child.setGravity(Gravity.CENTER);
                    ll.addView(child, i);
                }

                dialog.show();
            }

            @Override
            public void onNothingSelected() {

            }
        });

    }

    private void setBarData() {
        ArrayList<BarEntry> yValues = new ArrayList<>();
        for (int i = 0; i < intersections.size(); i++) {
            yValues.add(new BarEntry(i, intersections.get(i).getArea().floatValue()));
        }

        if (intersections.size() < 6) {
            for (int i = 0; i < (6 - intersections.size()); i++) {
                yValues.add(new BarEntry(intersections.size() + i, 0));
            }
        }

        if (analysisLayerResultInfo.getLayerName().equals("DLTB")) {
            yValues = null;
            yValues = new ArrayList<>();
            for (int i = 0; i < arrayList.size(); i++) {
                Map.Entry<String, Float> stringDoubleEntry = arrayList.get(i);
                yValues.add(new BarEntry(i, stringDoubleEntry.getValue()));
            }
        }
        BarDataSet set;
        if (barChart.getData() != null && barChart.getData().getDataSetCount() > 0) {
            set = (BarDataSet) barChart.getData().getDataSetByIndex(0);
            set.setValues(yValues);
            barChart.getData().notifyDataChanged();
            barChart.notifyDataSetChanged();
        } else {
            set = new BarDataSet(yValues, "");

            ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
            dataSets.add(set);

            BarData data = new BarData(dataSets);
            //设置顶部值是否显示
//            data.setDrawValues(false);

            //默认是0.85f
            data.setBarWidth(0.6f);


            barChart.setData(data);
            barChart.invalidate();
        }
    }


    private void initPieChart() {
        //饼状图
        pieChart.setClickable(true);
        pieChart.setUsePercentValues(true);//设置为TRUE的话，图标中的数据自动变为percent
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5, 10, 5, 5);//设置额外的偏移量(在图表视图周围)

        pieChart.setDragDecelerationFrictionCoef(0.95f);//设置滑动减速摩擦系数，在0~1之间
        //设置中间文件
        // pieChart.setCenterText(generateCenterSpannableText());
        pieChart.setDrawSliceText(false);//设置隐藏饼图上文字，只显示百分比
        pieChart.setDrawHoleEnabled(false);//设置为TRUE时，饼中心透明
        pieChart.setHoleColor(Color.WHITE);//设置饼中心颜色

        pieChart.setTransparentCircleColor(Color.WHITE);//透明的圆
        pieChart.setTransparentCircleAlpha(110);//透明度

        //pieChart.setHoleRadius(58f);//中间圆的半径占总半径的百分数
        pieChart.setHoleRadius(0);//实心圆
        //pieChart.setTransparentCircleRadius(61f);//// 半透明圈

        pieChart.setDrawCenterText(true);//绘制显示在饼图中心的文本

        pieChart.setRotationAngle(0);//设置一个抵消RadarChart的旋转度
        // 触摸旋转
        pieChart.setRotationEnabled(false);//通过触摸使图表旋转
        pieChart.setHighlightPerTapEnabled(true);//通过点击手势突出显示的值


        //变化监听
        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                int x = (int) h.getX();
                if (analysisLayerResultInfo.getLayerName().equals("DLTB")) {
                    return;
                }
                attributes = intersections.get(x).getAttributes();
                dialog = new AlertDialog.Builder(MPAndroidChartActivity.this).create();
                View view = View.inflate(MPAndroidChartActivity.this, R.layout.dialog_chart, null);
                dialog.setView(view);
                LinearLayout ll = view.findViewById(R.id.dialog_chart_ll);
                TextView child = null;
                for (int i = 0; i < originalfields.size(); i++) {
                    child = new TextView(MPAndroidChartActivity.this);
                    Object o = attributes.get(originalfields.get(i));
                    if (o == null){
                        o = "";
                    }
                    child.setText(displayfields.get(i) + "：" + o);
                    child.setPadding(10, 10, 10, 10);
                    child.setGravity(Gravity.CENTER);
                    ll.addView(child, i);
                }

                dialog.show();

            }

            @Override
            public void onNothingSelected() {

            }
        });
        //模拟数据


        pieChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);

        Legend l = pieChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(0f);
        l.setYOffset(0f);

        // 输入标签样式
        pieChart.setEntryLabelColor(Color.WHITE);
        pieChart.setEntryLabelTextSize(12f);
        /**
         * 设置比例图
         */
        Legend mLegend = pieChart.getLegend();
        mLegend.setPosition(Legend.LegendPosition.LEFT_OF_CHART_CENTER);  //在左边中间显示比例图
        mLegend.setFormSize(14f);//比例块字体大小 
        mLegend.setFormToTextSpace(2f);
        mLegend.setXEntrySpace(4f);//设置距离饼图的距离，防止与饼图重合
        mLegend.setYEntrySpace(4f);
        //设置比例块换行...
        mLegend.setWordWrapEnabled(true);
        mLegend.setDirection(Legend.LegendDirection.LEFT_TO_RIGHT);//设置字跟图表的左右顺序

        //mLegend.setTextColor(getResources().getColor(R.color.alpha_80));
        mLegend.setForm(Legend.LegendForm.SQUARE);//设置比例块形状，默认为方块
//        mLegend.setEnabled(false);//设置禁用比例块
    }

    @NonNull
    private ArrayList<PieEntry> getPieEntries() {
        ArrayList<PieEntry> entries = new ArrayList<PieEntry>();
        if (analysisLayerResultInfo.getLayerName().equals("DLTB")) {
            Set<Map.Entry<String, Float>> keySet = hashMap.entrySet();
            for (Map.Entry<String, Float> stringFloatEntry : keySet) {
//                BigDecimal bd = new BigDecimal(stringFloatEntry.getValue());
//                bd = bd.setScale(2, RoundingMode.HALF_UP);
                entries.add(new PieEntry(Float.valueOf(stringFloatEntry.getValue().toString()), stringFloatEntry.getKey() + "(" + stringFloatEntry.getValue() + "m²)"));
            }
            return entries;
        }


        for (AnalysisIntersectionResultInfo intersection : intersections) {
            Map attributes = intersection.getAttributes();
            String qlr = (String) attributes.get("QLR");
            if (qlr == null) {
                qlr = "";
            }
            //添加数据
//            BigDecimal bd = new BigDecimal(intersection.getArea());
//            bd = bd.setScale(2, RoundingMode.HALF_UP);

            entries.add(new PieEntry(intersection.getArea().floatValue(), qlr + "(" + intersection.getArea().floatValue() + "m²)"));
        }


        return entries;
    }

    //设置中间文字
//    private SpannableString generateCenterSpannableText() {
//        //原文：MPAndroidChart\ndeveloped by Philipp Jahoda
//        SpannableString s = new SpannableString("项目");
//        //s.setSpan(new RelativeSizeSpan(1.7f), 0, 14, 0);
//        //s.setSpan(new StyleSpan(Typeface.NORMAL), 14, s.length() - 15, 0);
//        // s.setSpan(new ForegroundColorSpan(Color.GRAY), 14, s.length() - 15, 0);
//        //s.setSpan(new RelativeSizeSpan(.8f), 14, s.length() - 15, 0);
//        // s.setSpan(new StyleSpan(Typeface.ITALIC), s.length() - 14, s.length(), 0);
//        // s.setSpan(new ForegroundColorSpan(ColorTemplate.getHoloBlue()), s.length() - 14, s.length(), 0);
//        return s;
//    }

    //设置数据
    private void setPieData() {
        ArrayList<PieEntry> entries = getPieEntries();
        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setSliceSpace(2f);//饼图区块之间的距离
        dataSet.setSelectionShift(5f);//

        //数据和颜色
//        Integer[] colors=new Integer[]{Color.parseColor("#d87a80"), Color.parseColor("#2ec7c9"), Color.parseColor("#b6a2de"),
//                Color.parseColor("#5ab1ef"), Color.parseColor("#ffb980"), Color.parseColor("#8d98b3")};
        ArrayList<Integer> ints = new ArrayList();
        Random random = new Random();

        if (entries.size() <= colors.length) {
            for (int i = 0; i < entries.size(); i++) {

                ints.add(colors[i]);

            }
        } else {
            for (int color : colors) {
                ints.add(color);
            }
            for (int i = 0; i < entries.size() - colors.length; i++) {
                int color = 0xff000000 | random.nextInt(0x00ffffff);
                ints.add(color);
            }
        }


        //添加对应的颜色值
        List<Integer> colorSum = new ArrayList<>();
        for (Integer c : ints) {
            colorSum.add(c);
        }
        dataSet.setColors(colorSum);
        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.BLACK);
        pieChart.highlightValues(null);//在给定的数据集中突出显示给定索引的值
        pieChart.setData(data);

        //刷新
        pieChart.invalidate();

    }


}
