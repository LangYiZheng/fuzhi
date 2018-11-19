package com.guyu.android.gis.maptools;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.Toast;

import com.guyu.android.gis.activity.MainActivity;
import com.guyu.android.R;



public class CompassTool {

	private MainActivity mainActivity;
	private View mGroupView = null;
	private float currentDegree = 0.0F;
	private ImageView image = null;
	private SensorManager mSensorManager = null;
	private SensorEventListener seListener = null;

	private int state = 0;

	public CompassTool(MainActivity mainActivity) {
		this.mainActivity = mainActivity;

	}

	public int getState() {
		return state;
	}

	public void init() {
		mGroupView = mainActivity.findViewById(R.id.vg_compass);
		mGroupView.setVisibility(View.VISIBLE);
		this.image = ((ImageView)mGroupView.findViewById(R.id.compassImage));
		this.mSensorManager = ((SensorManager) mainActivity
				.getSystemService( Context.SENSOR_SERVICE));
		if (this.mSensorManager == null) {
			Toast.makeText(mainActivity, "手机不具备传感器,无法使用此功能", Toast.LENGTH_LONG).show();
		} else {
			this.seListener = new SensorEventListener() {
				@Override
				public void onAccuracyChanged(Sensor arg0, int arg1) {
					// TODO Auto-generated method stub

				}
				@Override
				public void onSensorChanged(SensorEvent paramSensorEvent) {
					switch (paramSensorEvent.sensor.getType()) {
					default:
						return;
					case 3:
						if(CompassTool.this.image!=null){
							float f = paramSensorEvent.values[0];
							RotateAnimation localRotateAnimation = new RotateAnimation(
									CompassTool.this.currentDegree, -90-f, 1, 0.5F, 1,
									0.5F);
							localRotateAnimation.setDuration(100L);
							CompassTool.this.image
									.startAnimation(localRotateAnimation);
							CompassTool.this.currentDegree = (-90-f);
						}				
					}
				}
			};
			this.mSensorManager.registerListener(this.seListener, this.mSensorManager.getDefaultSensor(3), 1);
			state = 1;
		}
	}

	public void dispose() {
		if (mGroupView != null) {
			mGroupView.setVisibility(View.GONE);
		}
		if (this.mSensorManager != null){
			this.mSensorManager.unregisterListener(this.seListener);
			seListener = null;
			mSensorManager = null;			
		}	
		this.currentDegree = 0.0F;
		this.image = null;
		state = 0;
	}
}
