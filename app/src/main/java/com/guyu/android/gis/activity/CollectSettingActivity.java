package com.guyu.android.gis.activity;

import com.blankj.utilcode.util.ToastUtils;
import com.guyu.android.gis.app.GisQueryApplication;
import com.guyu.android.utils.UtilsTools;
import com.guyu.android.R;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;



public class CollectSettingActivity extends Activity {
	private EditText mAutoCollectTimEditText = null;
	private int m_nAutoTime = 0;
	@Override
	protected void onCreate(Bundle paramBundle) {
		requestWindowFeature(1);
		super.onCreate(paramBundle);
		setContentView(R.layout.activity_collect_setting);
		initActivity();
	}

	private void initActivity() {
		initData();
		initWidget();
	}

	private void initData() {

	}

	private void initWidget() {
		this.mAutoCollectTimEditText = ((EditText) findViewById(R.id.et_autoCollectTime));
		int i = GisQueryApplication.getApp().GetIntConfigData("collect_auto_time", 1);
		if (i != 0) {
			this.mAutoCollectTimEditText.setText("" + i);
		}
		findViewById(R.id.button2).setOnClickListener(
				new View.OnClickListener() {
					public void onClick(View paramView) {
						CollectSettingActivity.this
								.saveAutoCollectTimeSetting();
						CollectSettingActivity.this.setResult(-1);
						CollectSettingActivity.this.finish();
					}
				});
		findViewById(R.id.button1).setOnClickListener(
				new View.OnClickListener() {
					public void onClick(View paramView) {
						CollectSettingActivity.this.finish();
					}
				});
	}

	private void saveAutoCollectTimeSetting() {
		String str = this.mAutoCollectTimEditText.getText().toString();
		if (str.equals("")) {
			ToastUtils.showLong(  "请输入采集时间");
		} else {
			this.m_nAutoTime = Integer.parseInt(str);
			GisQueryApplication.getApp().SetConfigData("collect_auto_time", this.m_nAutoTime);
		}

	}

}