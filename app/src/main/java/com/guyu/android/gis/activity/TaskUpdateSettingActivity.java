package com.guyu.android.gis.activity;

import com.guyu.android.gis.app.GisQueryApplication;
import com.guyu.android.gis.opt.MapOperate;
import com.guyu.android.utils.SysConfig;
import com.guyu.android.utils.UtilsTools;
import com.guyu.android.R;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Spinner;



public class TaskUpdateSettingActivity extends Activity {
	private int[] TimeSetArgs = { 1,5, 10, 20, 30, 60, 300, 1800 };
	private int lastTimeSet = 0;
	private View.OnClickListener mOnClickListener = null;
	private int mSelectIndex = 0;
	private int mTaskUpdateType = 0;
	private int mTaskUpdateTime = 0;
	private Spinner mSpinnerTimeSet = null;
	private CheckBox mAutoUpdateBox = null;

	protected void onCreate(Bundle paramBundle) {
		requestWindowFeature(1);
		super.onCreate(paramBundle);
		setContentView(R.layout.activity_taskupdate_setting);

		initActivity();
	}

	private void initActivity() {
		initData();
		initWidget();
	}

	private void initData() {
		this.mOnClickListener = new MOnClickListener();
		this.mTaskUpdateType = GisQueryApplication.getApp().GetIntConfigData("TASK_UPDATE_TYPE",
				this.mTaskUpdateType);
		this.mTaskUpdateTime = GisQueryApplication.getApp().GetIntConfigData("TASK_UPDATE_TIME",
				this.mTaskUpdateTime);
	}

	private class MOnClickListener implements View.OnClickListener {
		public void onClick(View paramView) {
			switch (paramView.getId()) {
			case R.id.ok:
				GisQueryApplication.getApp().SetConfigData(
						"TASK_UPDATE_TYPE",
						TaskUpdateSettingActivity.this.mTaskUpdateType);
				if (TaskUpdateSettingActivity.this.mTaskUpdateType == 1) {
					GisQueryApplication.getApp().SetConfigData(
									"TASK_UPDATE_TIME",
									TaskUpdateSettingActivity.this.TimeSetArgs[TaskUpdateSettingActivity.this.mSelectIndex]);
					SysConfig.TaskUpdateTime = TaskUpdateSettingActivity.this.TimeSetArgs[TaskUpdateSettingActivity.this.mSelectIndex];
					Log.i("TaskUpdateSetting",
							"保存时间间隔配置为："
									+ TaskUpdateSettingActivity.this.TimeSetArgs[TaskUpdateSettingActivity.this.mSelectIndex]
									+ "秒");
					//启动任务自动更新服务
					GisQueryApplication.getApp().startTaskUpdateService();
				}
				else
				{
					//停止任务自动更新服务
					GisQueryApplication.getApp().stopTaskUpdateService();
				}
				TaskUpdateSettingActivity.this.setResult(-1);
				TaskUpdateSettingActivity.this.finish();
				break;
			case R.id.cancel:
				TaskUpdateSettingActivity.this.finish();
			default:
				return;
			}

		}
	}

	private void initWidget() {

		int[] arrayOfInt1 = this.TimeSetArgs;

		int timeSetArgs_length = arrayOfInt1.length;
		this.mAutoUpdateBox = ((CheckBox) findViewById(R.id.cb_taskautoupdate));
		// 设置复选框状态
		mAutoUpdateBox.setChecked((mTaskUpdateType == 1));
		this.lastTimeSet = 0;
		for (int i = 0; i < TimeSetArgs.length; i++) {
			if (TimeSetArgs[i] == mTaskUpdateTime) {
				this.lastTimeSet = i;
				break;
			}
		}
		this.mSpinnerTimeSet = ((Spinner) findViewById(R.id.sp_update_time));

		this.mSpinnerTimeSet.setPrompt("选择时间间隔");
		this.mSpinnerTimeSet.setSelection(this.lastTimeSet);
		this.mSpinnerTimeSet
				.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
					public void onItemSelected(AdapterView<?> paramAdapterView,
							View paramView, int paramInt, long paramLong) {
						TaskUpdateSettingActivity.this.mSelectIndex = paramInt;
					}

					public void onNothingSelected(
							AdapterView<?> paramAdapterView) {
					}
				});
		this.mSpinnerTimeSet.setVisibility(View.VISIBLE);
		// 自动更新
		mAutoUpdateBox
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (mAutoUpdateBox.isChecked()) {
							TaskUpdateSettingActivity.this.mTaskUpdateType = 1;
						} else {
							TaskUpdateSettingActivity.this.mTaskUpdateType = 0;
						}
					}
				});
		findViewById(R.id.ok).setOnClickListener(this.mOnClickListener);
		findViewById(R.id.cancel).setOnClickListener(this.mOnClickListener);
	}
}