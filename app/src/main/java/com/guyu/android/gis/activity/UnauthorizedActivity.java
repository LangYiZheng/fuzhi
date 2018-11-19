package com.guyu.android.gis.activity;

import com.guyu.android.gis.app.GisQueryApplication;
import com.guyu.android.utils.HnTimer;
import com.guyu.android.R;

import android.app.Activity;
import android.os.Bundle;


public class UnauthorizedActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		

		 this.setContentView(R.layout.activity_unauthorized);

		 HnTimer.setTimeout(3000, new HnTimer.OnCompletedListener(){   
		        public void onCompleted() {		         
		        	UnauthorizedActivity.this.finish();
		        }
		    });
		
	}

}
