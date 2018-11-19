package com.guyu.android.gis.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.blankj.utilcode.util.ToastUtils;
import com.esri.core.geometry.Point;
import com.guyu.android.gis.app.GisQueryApplication;
import com.guyu.android.gis.opt.MapOperate;
import com.guyu.android.utils.Point2D;
import com.guyu.android.utils.SorftKeyOpt;
import com.guyu.android.utils.Utility;
import com.guyu.android.utils.UtilsTools;
import com.guyu.android.R;

public class SetLocationActivity extends Activity {
	private int locationFormatType = 0;
	private View.OnClickListener mOnClick = null;
	private EditText mRightLat_duEditText = null;
	private EditText mRightLat_fenEditText = null;
	private EditText mRightLat_miaoEditText = null;
	private EditText mRightLattitudeEditText = null;
	private EditText mRightLon_duEditText = null;
	private EditText mRightLon_fenEditText = null;
	private EditText mRightLon_miaoEditText = null;
	private EditText mRightLontitudeEditText = null;
	private EditText mRightMeterXEditText = null;
	private EditText mRightMeterYEditText = null;
	private LinearLayout mVgDuFenMiaoLayout = null;
	private LinearLayout mVgDuLayout = null;
	private LinearLayout mVgMeterLayout = null;

	protected void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		setContentView(R.layout.activity_set_location);

		initActivity();
	}

	private void btnOk() {
		if (!(checkData()))
			return;
		Point2D localPoint2D = getEditeData();
		if (localPoint2D == null)
			return;
		Point localPoint = new Point(localPoint2D.x, localPoint2D.y);
		double x = 0;
		double y = 0;
		if (this.locationFormatType == 0||this.locationFormatType == 1) {
			if(localPoint2D.x>70&&localPoint2D.x<140&&localPoint2D.y>0&&localPoint2D.y<60){
				Point mapPoint = Utility.fromWgs84ToMap2(localPoint,
						MapOperate.map.getSpatialReference());
				x = mapPoint.getX();
				y = mapPoint.getY();
				backUp(x, y);
			} else{
				ToastUtils.showLong(  "输入数据非有效坐标值,请确认!");
			}
		} else {
			x = localPoint.getX();
			y = localPoint.getY();
			backUp(x, y);
		}
	}

	private boolean checkData() {
		boolean bool;
		if (this.locationFormatType == 0) {
			bool = checkData_dufenmiao();
			return bool;
		} else if (this.locationFormatType == 1) {
			bool = checkData_du();
			return bool;
		} else {
			bool = checkData_meter();
			return bool;
		}
	}

	private boolean checkData_du() {
		if ((!(this.mRightLattitudeEditText.getText().toString()
				.equalsIgnoreCase("")))
				&& (!(this.mRightLontitudeEditText.getText().toString()
						.equalsIgnoreCase(""))))
			return true;
		ToastUtils.showLong(  "数据不全,请检查");
		return false;
	}

	private boolean checkData_dufenmiao() {
		if ((!(this.mRightLon_duEditText.getText().toString()
				.equalsIgnoreCase("")))
				&& (!(this.mRightLon_fenEditText.getText().toString()
						.equalsIgnoreCase("")))
				&& (!(this.mRightLon_miaoEditText.getText().toString()
						.equalsIgnoreCase("")))
				&& (!(this.mRightLat_duEditText.getText().toString()
						.equalsIgnoreCase("")))
				&& (!(this.mRightLat_fenEditText.getText().toString()
						.equalsIgnoreCase("")))
				&& (!(this.mRightLat_miaoEditText.getText().toString()
						.equalsIgnoreCase(""))))
			return true;
		ToastUtils.showLong(  "数据不全,请检查");
		return false;
	}

	private boolean checkData_meter() {
		if ((!(this.mRightMeterXEditText.getText().toString()
				.equalsIgnoreCase("")))
				&& (!(this.mRightMeterYEditText.getText().toString()
						.equalsIgnoreCase(""))))
			return true;
		ToastUtils.showLong(  "数据不全,请检查");
		return false;
	}

	private double getD(int paramInt1, int paramInt2, double paramDouble) {
		return (paramInt1 + paramInt2 / 60.0D + paramDouble / 3600.0D);
	}

	private float[] getDFM(double paramDouble) {
		double d = Math.abs(paramDouble);
		int i = (int) Math.floor(d);
		int j = (int) Math.floor(60.0D * (d - i));
		float f = (int) (100.0F * (float) (60.0D * (60.0D * (d - i) - j))) / 100;
		float[] arrayOfFloat = new float[3];
		arrayOfFloat[0] = i;
		arrayOfFloat[1] = j;
		arrayOfFloat[2] = f;
		return arrayOfFloat;
	}

	private Point2D getEditeData() {
		Point2D localPoint2D;
		if (this.locationFormatType == 0) {
			localPoint2D = getEditeData_dufenmiao();
			return localPoint2D;
		} else if (this.locationFormatType == 1) {
			localPoint2D = getEditeData_du();
			return localPoint2D;
		} else {
			localPoint2D = getEditeData_meter();
			return localPoint2D;
		}
	}

	private Point2D getEditeData_du() {
		Point2D localPoint2D = new Point2D();
		String str1 = this.mRightLontitudeEditText.getText().toString();
		String str2 = this.mRightLattitudeEditText.getText().toString();
		try {
			double d1 = Double.parseDouble(str1);
			double d2 = Double.parseDouble(str2);
			localPoint2D.x = d1;
			localPoint2D.y = d2;
			return localPoint2D;
		} catch (Exception localException) {
		}
		return null;
	}

	private Point2D getEditeData_dufenmiao() {
		Point2D localPoint2D = new Point2D();
		String str1 = this.mRightLon_duEditText.getText().toString();
		String str2 = this.mRightLon_fenEditText.getText().toString();
		String str3 = this.mRightLon_miaoEditText.getText().toString();
		String str4 = this.mRightLat_duEditText.getText().toString();
		String str5 = this.mRightLat_fenEditText.getText().toString();
		String str6 = this.mRightLat_miaoEditText.getText().toString();
		try {
			int i = Integer.parseInt(str1);
			int j = Integer.parseInt(str2);
			double d1 = Double.parseDouble(str3);
			int k = Integer.parseInt(str4);
			int l = Integer.parseInt(str5);
			double d2 = Double.parseDouble(str6);
			double d3 = getD(i, j, d1);
			double d4 = getD(k, l, d2);
			localPoint2D.x = d3;
			localPoint2D.y = d4;
			return localPoint2D;
		} catch (Exception localException) {
		}
		return null;
	}

	private Point2D getEditeData_meter() {
		Point2D localPoint2D = new Point2D();
		String str1 = this.mRightMeterXEditText.getText().toString();
		String str2 = this.mRightMeterYEditText.getText().toString();
		try {
			double d1 = Double.parseDouble(str1);
			double d2 = Double.parseDouble(str2);
			localPoint2D.x = d1;
			localPoint2D.y = d2;
			return localPoint2D;
		} catch (Exception localException) {
		}
		return null;
	}

	private void initActivity() {
		if (MapOperate.IsPrjIsWGS84())
			;
		initData();
		initWidget();
	}

	private void initData() {
		this.mOnClick = new MOnClickListener();
	}

	private void initWidget() {
		((Button) findViewById(R.id.btn_get_location))
				.setOnClickListener(this.mOnClick);
		findViewById(R.id.btn_cancel).setOnClickListener(this.mOnClick);
		this.mRightLon_duEditText = ((EditText) findViewById(R.id.et_lon_right_value_du));
		this.mRightLon_fenEditText = ((EditText) findViewById(R.id.et_lon_right_value_fen));
		this.mRightLon_miaoEditText = ((EditText) findViewById(R.id.et_lon_right_value_miao));
		this.mRightLat_duEditText = ((EditText) findViewById(R.id.et_lat_right_value_du));
		this.mRightLat_fenEditText = ((EditText) findViewById(R.id.et_lat_right_value_fen));
		this.mRightLat_miaoEditText = ((EditText) findViewById(R.id.et_lat_right_value_miao));
		this.mRightMeterXEditText = ((EditText) findViewById(R.id.et_meter_x));
		this.mRightMeterYEditText = ((EditText) findViewById(R.id.et_meter_y));
		this.mRightMeterXEditText
				.setOnEditorActionListener(new NextGetFocusListener(
						this.mRightMeterYEditText));
		this.mRightLon_duEditText
				.setOnEditorActionListener(new NextGetFocusListener(
						this.mRightLon_fenEditText));
		this.mRightLon_fenEditText
				.setOnEditorActionListener(new NextGetFocusListener(
						this.mRightLon_miaoEditText));
		this.mRightLon_miaoEditText
				.setOnEditorActionListener(new NextGetFocusListener(
						this.mRightLat_duEditText));
		this.mRightLat_duEditText
				.setOnEditorActionListener(new NextGetFocusListener(
						this.mRightLat_fenEditText));
		this.mRightLat_fenEditText
				.setOnEditorActionListener(new NextGetFocusListener(
						this.mRightLat_miaoEditText));
		this.mRightLat_miaoEditText
				.setOnEditorActionListener(new HideSoftKeyListener(
						this.mRightLat_miaoEditText));
		this.mVgDuLayout = ((LinearLayout) findViewById(R.id.ll_du));
		this.mVgMeterLayout = ((LinearLayout) findViewById(R.id.ll_meter));
		this.mVgDuFenMiaoLayout = ((LinearLayout) findViewById(R.id.ll_dufenmiao));
		this.mRightLontitudeEditText = ((EditText) findViewById(R.id.et_lon_right_value));
		this.mRightLattitudeEditText = ((EditText) findViewById(R.id.et_lat_right_value));
		findViewById(R.id.rb_dufenmiao).setVisibility(View.GONE);
		findViewById(R.id.rb_du).setVisibility(View.GONE);
		this.mRightLattitudeEditText
				.setOnEditorActionListener(new HideSoftKeyListener(
						this.mRightLattitudeEditText));
		((RadioGroup) findViewById(R.id.rb_select_type))
				.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
					public void onCheckedChanged(RadioGroup paramRadioGroup,
							int paramInt) {
						switch (paramInt) {
						default:
							return;
						case R.id.rb_dufenmiao:
							SetLocationActivity.this.locationFormatType = 0;
							SetLocationActivity.this.mVgDuLayout
									.setVisibility(View.GONE);
							SetLocationActivity.this.mVgDuFenMiaoLayout
									.setVisibility(View.VISIBLE);
							SetLocationActivity.this.mVgMeterLayout
									.setVisibility(View.GONE);
							return;
						case R.id.rb_du:
							SetLocationActivity.this.locationFormatType = 1;
							SetLocationActivity.this.mVgDuLayout
									.setVisibility(View.VISIBLE);
							SetLocationActivity.this.mVgDuFenMiaoLayout
									.setVisibility(View.GONE);
							SetLocationActivity.this.mVgMeterLayout
									.setVisibility(View.GONE);
							return;
						case R.id.rb_meter:
						}
						SetLocationActivity.this.locationFormatType = 2;
						SetLocationActivity.this.mVgMeterLayout
								.setVisibility(View.VISIBLE);
						SetLocationActivity.this.mVgDuFenMiaoLayout
								.setVisibility(View.GONE);
						SetLocationActivity.this.mVgDuLayout.setVisibility(View.GONE);
					}
				});
		if (MapOperate.IsPrjIsWGS84()) {
			findViewById(R.id.rb_meter).setEnabled(false);
			findViewById(R.id.rb_meter).setVisibility(View.INVISIBLE);
			((RadioButton) findViewById(R.id.rb_dufenmiao)).setChecked(true);
			if (this.locationFormatType != 0)
				return;
			// break label463;
			this.mVgDuLayout.setVisibility(View.GONE);
			this.mVgDuFenMiaoLayout.setVisibility(View.VISIBLE);
			this.mVgMeterLayout.setVisibility(View.GONE);
		}
		do {
			((RadioButton) findViewById(R.id.rb_meter)).setChecked(true);
			// break label412;
			if (this.locationFormatType != 1)
				continue;
			this.mVgDuLayout.setVisibility(View.VISIBLE);
			this.mVgDuFenMiaoLayout.setVisibility(View.GONE);
			this.mVgMeterLayout.setVisibility(View.GONE);
			return;
		} while (this.locationFormatType != 2);
		this.mVgMeterLayout.setVisibility(View.VISIBLE);
		this.mVgDuFenMiaoLayout.setVisibility(View.GONE);
		this.mVgDuLayout.setVisibility(View.GONE);
		return;
	}

	class MOnClickListener implements View.OnClickListener {
		public void onClick(View paramView) {
			switch (paramView.getId()) {
			default:
				return;
			case R.id.btn_get_location:
				SetLocationActivity.this.btnOk();
				return;
			case R.id.btn_cancel:
				Intent localIntent = new Intent();
				setResult(0, localIntent);
				finish();
				return;
			}
		}
	}

	private void backUp(double paramDouble1, double paramDouble2) {
		Intent localIntent = new Intent();
		localIntent.putExtra("dLon", paramDouble1);
		localIntent.putExtra("dLat", paramDouble2);
		setResult(-1, localIntent);
		finish();
	}

	private class HideSoftKeyListener implements
			TextView.OnEditorActionListener {
		private View view = null;

		public HideSoftKeyListener(EditText paramEditText) {
			this.view = paramEditText;
		}

		public boolean onEditorAction(TextView paramTextView, int paramInt,
				KeyEvent paramKeyEvent) {
			SorftKeyOpt.hideEditeTextSorftKey(SetLocationActivity.this,
					this.view);
			return true;
		}
	}

	private class NextGetFocusListener implements
			TextView.OnEditorActionListener {

		private View view = null;

		public NextGetFocusListener(View paramView) {
			this.view = paramView;
		}

		public boolean onEditorAction(TextView paramTextView, int paramInt,
				KeyEvent paramKeyEvent) {
			if (this.view != null)
				this.view.requestFocus();
			return true;
		}
	}
}
