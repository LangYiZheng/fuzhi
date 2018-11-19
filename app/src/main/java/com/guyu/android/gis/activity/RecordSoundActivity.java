
package com.guyu.android.gis.activity;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.czt.mp3recorder.MP3Recorder;
import com.guyu.android.utils.FileUtil;
import com.guyu.android.R;
public class RecordSoundActivity extends Activity {

	private MP3Recorder mRecorder;
	private String soundFilePath;
	Button startButton = null;
	Button stopButton = null;
	Button recordOKButton = null;
	Button recordBackButton = null;
	
	private TextView minText; // 分
	private TextView secText; // 秒
	private boolean isPaused = false;
	private String timeUsed;
	private int timeUsedInsec;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_record_sound);
		
		startButton = (Button) findViewById(R.id.StartButton);
		stopButton = (Button) findViewById(R.id.StopButton);
		recordOKButton = (Button) findViewById(R.id.RecordOKButton);
		recordBackButton = (Button) findViewById(R.id.RecordBackButton);
		
		stopButton.setEnabled(false);
		stopButton.setClickable(false);
		recordOKButton.setEnabled(false);
		recordOKButton.setClickable(false);
		
		minText = (TextView) findViewById(R.id.min);  
	    secText = (TextView) findViewById(R.id.sec);
	    
		initData();
	
		startButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					mRecorder.start();
					uiHandle.removeMessages(1);  
		            startTime();  
		            isPaused = false; 
		            RecordSoundActivity.this.stopButton.setEnabled(true);
		            RecordSoundActivity.this.stopButton.setClickable(true);
		            RecordSoundActivity.this.recordBackButton.setEnabled(false);
		            RecordSoundActivity.this.recordBackButton.setClickable(false);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		
		stopButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mRecorder.stop();
				isPaused = true;  
	            timeUsedInsec = 0; 
	            RecordSoundActivity.this.recordOKButton.setEnabled(true);
	            RecordSoundActivity.this.recordOKButton.setClickable(true);
	            RecordSoundActivity.this.recordBackButton.setEnabled(true);
	            RecordSoundActivity.this.recordBackButton.setClickable(true);
			}
		});
		
		recordOKButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				RecordSoundActivity.this.recordOK();//录音完成
			}
		});
		recordBackButton.setOnClickListener(new OnClickListener() {//返回
			@Override
			public void onClick(View v) {
				setResult(-1);
				RecordSoundActivity.this.finish();
			}
		});
	}

	private void initData() {
		Intent localIntent = getIntent();
		if (localIntent != null) {

			this.soundFilePath = localIntent.getStringExtra("soundFilePath");
			mRecorder = new MP3Recorder(new File(soundFilePath));
		}
	}

	private Handler uiHandle = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				if (!isPaused) {
					addTimeUsed();
					updateClockUI();
				}
				uiHandle.sendEmptyMessageDelayed(1, 1000);
				break;
			 default:
				break;
			}
		}
	};

	private void recordOK() {
		new AlertDialog.Builder(this)
		.setTitle("确认操作")
		.setMessage("确认完成?")
		.setPositiveButton("确定",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface paramDialogInterface, int paramInt) {
						System.out.println("你已经成功停止了录音!");
						setResult(0);
						RecordSoundActivity.this.finish();
					}
				}).setNegativeButton("放弃", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface paramDialogInterface, int paramInt) {
						FileUtil.deleteFile(RecordSoundActivity.this.soundFilePath);
						System.out.println("你已经成功删除了刚刚录音文件："+RecordSoundActivity.this.soundFilePath);
						setResult(-1);
						RecordSoundActivity.this.finish();
					}
				}).show();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mRecorder.stop();
	}

	@Override
	protected void onPause() {
		super.onPause();
		isPaused = true;
	}

	@Override
	protected void onResume() {
		super.onResume();
		isPaused = false;
	}

	private void startTime() {
		uiHandle.sendEmptyMessageDelayed(1, 1000);
	}

	/**
	 * 更新时间的显示
	 */
	private void updateClockUI() {
		minText.setText(getMin() + ":");
		secText.setText(getSec());
	}

	public void addTimeUsed() {
		timeUsedInsec = timeUsedInsec + 1;
		timeUsed = this.getMin() + ":" + this.getSec();
	}

	public CharSequence getMin() {
		return String.valueOf(timeUsedInsec / 60);
	}

	public CharSequence getSec() {
		int sec = timeUsedInsec % 60;
		return sec < 10 ? "0" + sec : String.valueOf(sec);
	}	      
}
