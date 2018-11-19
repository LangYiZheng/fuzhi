package com.guyu.android.gis.activity;


import com.guyu.android.gis.app.GisQueryApplication;
import com.guyu.android.utils.HnTimer;
import com.guyu.android.R;

import android.app.Activity;
import android.os.Bundle;



public class ExpiredActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		

		 this.setContentView(R.layout.activity_expired);

		 HnTimer.setTimeout(3000, new HnTimer.OnCompletedListener(){   
		        public void onCompleted() {		         
		            ExpiredActivity.this.finish();
		        }
		    });
		
	}

}
