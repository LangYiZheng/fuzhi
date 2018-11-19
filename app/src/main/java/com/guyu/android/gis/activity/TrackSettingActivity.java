package com.guyu.android.gis.activity;

import com.guyu.android.gis.app.GisQueryApplication;
import com.guyu.android.utils.SysConfig;
import com.guyu.android.R;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import android.widget.AdapterView;
import android.widget.RadioGroup;
import android.widget.Spinner;



public class TrackSettingActivity extends Activity {
    private int[] DisSetArgs = {1,5, 10, 20, 50, 100, 500, 1000};
    private int[] TimeSetArgs = {1,5, 10, 20, 30, 60, 300, 1800};
    private int lastDisSet = 0;
    private int lastTimeSet = 0;
    private View.OnClickListener mOnClickListener = null;
    private int mSelectIndex = 0;
    private int mSelectType = 0;
    private Spinner mSpinnerDisSet = null;
    private Spinner mSpinnerTimeSet = null;
    private int mTrackSetTime = 5;
    private int mTrackSetDis = 5;

    protected void onCreate(Bundle paramBundle) {
        requestWindowFeature(1);
        super.onCreate(paramBundle);
        setContentView(R.layout.track_setting);

        initActivity();
    }

    private void initActivity() {
        initData();
        initWidget();
    }

    private void initData() {
        this.mOnClickListener = new MOnClickListener();
        this.mSelectType = GisQueryApplication.getApp().GetIntConfigData("TRACK_SET_TYPE", this.mSelectType);
        this.mTrackSetTime = GisQueryApplication.getApp().GetIntConfigData("TRACK_SET_TIME", 5);
        this.mTrackSetDis = GisQueryApplication.getApp().GetIntConfigData("TRACK_SET_DIS", 5);
    }

    private class MOnClickListener implements View.OnClickListener {
        public void onClick(View paramView) {
            switch (paramView.getId()) {
                case R.id.ok:
                    SysConfig.TrackType = TrackSettingActivity.this.mSelectType;
                    GisQueryApplication.getApp().SetConfigData("TRACK_SET_TYPE", TrackSettingActivity.this.mSelectType);
                    if (TrackSettingActivity.this.mSelectType == 0) {
                        SysConfig.TrackSetTime = TrackSettingActivity.this.TimeSetArgs[TrackSettingActivity.this.mSelectIndex];
                        GisQueryApplication.getApp().SetConfigData("TRACK_SET_TIME", TrackSettingActivity.this.TimeSetArgs[TrackSettingActivity.this.mSelectIndex]);
                        Log.i("TrackSettingActivity", "保存时间配置为：" + TrackSettingActivity.this.TimeSetArgs[TrackSettingActivity.this.mSelectIndex] + "秒");
                    } else {
                        SysConfig.TrackSetDis = TrackSettingActivity.this.DisSetArgs[TrackSettingActivity.this.mSelectIndex];
                        GisQueryApplication.getApp().SetConfigData("TRACK_SET_DIS", TrackSettingActivity.this.DisSetArgs[TrackSettingActivity.this.mSelectIndex]);
                        Log.i("TrackSettingActivity", "保存距离配置为：" + TrackSettingActivity.this.DisSetArgs[TrackSettingActivity.this.mSelectIndex] + "米");
                    }
                    TrackSettingActivity.this.setResult(-1);
                    TrackSettingActivity.this.finish();
                    break;
                case R.id.cancel:
                    TrackSettingActivity.this.finish();
                default:
                    return;
            }

        }
    }

    private void initWidget() {

        RadioGroup localRadioGroup = (RadioGroup) findViewById(R.id.rg_track_type);
        this.lastTimeSet = 0;
        this.mSpinnerTimeSet = ((Spinner) findViewById(R.id.sp_data_time));
        this.mSpinnerDisSet = ((Spinner) findViewById(R.id.sp_data_distance));
        this.mSpinnerTimeSet.setPrompt("选择时间间隔");
        this.mSpinnerTimeSet.setSelection(this.lastTimeSet);
        this.mSpinnerTimeSet.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> paramAdapterView, View paramView, int paramInt, long paramLong) {
                TrackSettingActivity.this.mSelectIndex = paramInt;
            }

            public void onNothingSelected(AdapterView<?> paramAdapterView) {
            }
        });
        localRadioGroup.check(R.id.type1);
        this.mSpinnerTimeSet.setVisibility(View.VISIBLE);
        this.mSpinnerDisSet.setVisibility(View.GONE);

        this.mSpinnerDisSet.setPrompt("选择距离间隔");
        this.mSpinnerDisSet.setVisibility(View.GONE);
        this.mSpinnerDisSet.setSelection(this.lastDisSet);
        this.mSpinnerDisSet.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> paramAdapterView, View paramView, int paramInt, long paramLong) {
                TrackSettingActivity.this.mSelectIndex = paramInt;
            }

            public void onNothingSelected(AdapterView<?> paramAdapterView) {
            }
        });
        this.mSpinnerTimeSet.setVisibility(View.GONE);
        this.mSpinnerDisSet.setVisibility(View.VISIBLE);

        localRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup paramRadioGroup, int paramInt) {
                switch (paramInt) {
                    case R.id.type1:
                        TrackSettingActivity.this.mSelectType = 0;
                        TrackSettingActivity.this.mSelectIndex = TrackSettingActivity.this.lastTimeSet;
                        TrackSettingActivity.this.mSpinnerDisSet.setVisibility(View.GONE);
                        TrackSettingActivity.this.mSpinnerTimeSet.setVisibility(View.VISIBLE);
                        TrackSettingActivity.this.mSpinnerTimeSet.setSelection(TrackSettingActivity.this.lastTimeSet);
                        break;
                    case R.id.type2:
                        TrackSettingActivity.this.mSelectType = 1;
                        TrackSettingActivity.this.mSelectIndex = TrackSettingActivity.this.lastDisSet;
                        TrackSettingActivity.this.mSpinnerTimeSet.setVisibility(View.GONE);
                        TrackSettingActivity.this.mSpinnerDisSet.setVisibility(View.VISIBLE);
                        TrackSettingActivity.this.mSpinnerDisSet.setSelection(TrackSettingActivity.this.lastDisSet);
                        break;
                    default:
                        return;
                }

            }
        });
        findViewById(R.id.ok).setOnClickListener(this.mOnClickListener);
        findViewById(R.id.cancel).setOnClickListener(this.mOnClickListener);
        if (this.mSelectType == 0) {
            localRadioGroup.check(R.id.type1);
            TrackSettingActivity.this.mSpinnerDisSet.setVisibility(View.GONE);
            TrackSettingActivity.this.mSpinnerTimeSet.setVisibility(View.VISIBLE);
            for (int i = 0; i < TimeSetArgs.length; i++) {
                if (TimeSetArgs[i] == this.mTrackSetTime) {
                    TrackSettingActivity.this.mSelectIndex = i;
                    break;
                }
            }
            this.mSpinnerTimeSet.setSelection(TrackSettingActivity.this.mSelectIndex);
        } else {
            localRadioGroup.check(R.id.type2);
            TrackSettingActivity.this.mSpinnerTimeSet.setVisibility(View.GONE);
            TrackSettingActivity.this.mSpinnerDisSet.setVisibility(View.VISIBLE);
            for (int i = 0; i < DisSetArgs.length; i++) {
                if (DisSetArgs[i] == this.mTrackSetDis) {
                    TrackSettingActivity.this.mSelectIndex = i;
                    break;
                }
            }
            this.mSpinnerDisSet.setSelection(TrackSettingActivity.this.mSelectIndex);
        }
    }
}