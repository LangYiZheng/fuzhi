package com.guyu.android.gis.activity;

import com.blankj.utilcode.util.ToastUtils;
import com.guyu.android.gis.app.GisQueryApplication;
import com.guyu.android.gis.common.Canton;
import com.guyu.android.gis.common.Human;
import com.guyu.android.gis.common.Unit;
import com.guyu.android.utils.MD5;
import com.guyu.android.utils.SysConfig;
import com.guyu.android.utils.UtilsTools;
import com.guyu.android.R;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;

public class LoginFragment extends Fragment {

	private TextView mAreaTextView = null;
	private TextView mOfficeTextView = null;
	private TextView mPeoTextView = null;
	private TextView mPwdTextView = null;
	private TextView mCantonIDTextView = null;
	private TextView mUnitIDTextView = null;
	private CheckBox mRecordPwdBox = null;
	private EditText mTextPassWord = null;
	private SharedPreferences sp_humanInfo;
	private SharedPreferences sp_pwdInfo;

	private int cantonID;
	private int unitID;
	private FragmentActivity fragmentActivity;



	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		fragmentActivity = this.getActivity();
		return inflater.inflate(R.layout.layout_login_simple, container, false);
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		sp_pwdInfo = fragmentActivity.getSharedPreferences("pwdInfo",
				Context.MODE_PRIVATE);
		sp_humanInfo = fragmentActivity.getSharedPreferences("humanInfo",
				Context.MODE_PRIVATE);
		initWidget();
		Button btn_login = (Button) fragmentActivity
				.findViewById(R.id.btn_login);
		btn_login.setOnClickListener(new OnClickListener() {// 登陆
					@Override
					public void onClick(View view) {
						if (loginOnClick()) {
							System.out.println("ID:"
									+ SysConfig.mHumanInfo.getHumanId()
									+ ",name:"
									+ SysConfig.mHumanInfo.getHumanName()
									+ ",unitid:"
									+ SysConfig.mHumanInfo.getUnitId());
							fragmentActivity
									.startActivity(new Intent(LoginFragment.this.getActivity(),
											MainActivity.class));
							fragmentActivity.overridePendingTransition(
									R.anim.fade, R.anim.hold);
							fragmentActivity.finish();
						}
					}
				});
		Button btn_cancel = (Button) fragmentActivity
				.findViewById(R.id.btn_quite);
		btn_cancel.setOnClickListener(new OnClickListener() {// 取消
					@Override
					public void onClick(View view) {

						if (sp_humanInfo.getString("PROJECT_NAME", "").equals(
								GisQueryApplication.getApp().getProjectsconfig()
										.getCurrentProjectName())) {
							fragmentActivity.finish();
						} else {
							fragmentActivity.getApplicationContext()
									.sendBroadcast(new Intent("finish"));
							fragmentActivity.finish();
						}

					}
				});
		mAreaTextView.setOnClickListener(new OnClickListener() {// 选择区域
					@Override
					public void onClick(View view) {
						fragmentActivity
								.startActivityForResult(
										new Intent(
												LoginFragment.this.getActivity(),SelectAreaActivity.class),
										0);
					}
				});
		mOfficeTextView.setOnClickListener(new OnClickListener() {// 选择单位
					@Override
					public void onClick(View view) {
						if (LoginFragment.this.mAreaTextView.getText()
								.toString().equals("")) {
							showWaringDialog("请先选择行政区！");
							return;
						} else {
							fragmentActivity
									.startActivityForResult(
											new Intent(
													LoginFragment.this.getActivity(),SelectUnitActivity.class),
											1);
						}
					}
				});
		mPeoTextView.setOnClickListener(new OnClickListener() {// 选择人员
					@Override
					public void onClick(View view) {
						if (LoginFragment.this.mOfficeTextView.getText()
								.toString().equals("")) {
							System.out
									.println(LoginFragment.this.mOfficeTextView
											.getText());
							showWaringDialog("请先选择单位！");
							return;
						} else {
							fragmentActivity
									.startActivityForResult(
											new Intent(
													LoginFragment.this.getActivity(),SelectPeoActivity.class),
											2);
						}
					}
				});
		// 监听记住密码多选框按钮事件
		mRecordPwdBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (mRecordPwdBox.isChecked()) {
					System.out.println("记住密码已选中");
					sp_pwdInfo.edit().putBoolean("ISCHECK", true).commit();
				} else {
					System.out.println("记住密码没有选中");
					sp_pwdInfo.edit().putBoolean("ISCHECK", false).commit();

				}
			}
		});
	}

	private void initWidget() {
		this.mAreaTextView = ((TextView) fragmentActivity
				.findViewById(R.id.tv_select_area));
		this.mOfficeTextView = ((TextView) fragmentActivity
				.findViewById(R.id.tv_select_office));
		this.mPeoTextView = ((TextView) fragmentActivity
				.findViewById(R.id.tv_select_peo));
		this.mPwdTextView = ((TextView) fragmentActivity.findViewById(R.id.pwd));
		this.mCantonIDTextView = ((TextView) fragmentActivity
				.findViewById(R.id.cantonID));
		this.mUnitIDTextView = ((TextView) fragmentActivity
				.findViewById(R.id.unitID));
		this.mTextPassWord = ((EditText) fragmentActivity
				.findViewById(R.id.et_pwd));
		this.mRecordPwdBox = ((CheckBox) fragmentActivity
				.findViewById(R.id.cb_01));

		SysConfig.mCurrentCanton = new Canton();
		SysConfig.mUnitInfo = new Unit();
		SysConfig.mHumanInfo = new Human();
		if (sp_humanInfo.getString("PROJECT_NAME", "").equals(
				GisQueryApplication.getApp().getProjectsconfig().getCurrentProjectName())) {
			this.mAreaTextView.setText(sp_humanInfo.getString("AREA_NAME", ""));
			this.mOfficeTextView.setText(sp_humanInfo
					.getString("UNIT_NAME", ""));
			this.mPeoTextView.setText(sp_humanInfo.getString("USER_NAME", ""));
			this.mPwdTextView.setText(sp_humanInfo.getString("PWD", ""));
			this.mCantonIDTextView.setText(sp_humanInfo.getString("CANTON_ID",
					""));
			this.mUnitIDTextView.setText(sp_humanInfo.getString("UNITID", ""));

			String curCantonID = this.mCantonIDTextView.getText().toString();
			String curUnitID = this.mUnitIDTextView.getText().toString();
			SysConfig.mCurrentCanton.setCantonId(Integer.parseInt(curCantonID));
			SysConfig.mUnitInfo.setUnitId(Integer.parseInt(curUnitID));
			SysConfig.mHumanInfo
					.setHumanId(sp_humanInfo.getInt("HUMAN_ID", -1));
			SysConfig.mHumanInfo.setHumanName(sp_humanInfo.getString(
					"USER_NAME", ""));
			SysConfig.mHumanInfo.setUnitId(Integer.parseInt(curUnitID));

			if (sp_pwdInfo.getBoolean("ISCHECK", false)) // 如果上次是记住密码
			{
				this.mTextPassWord
						.setText(sp_pwdInfo.getString("PASSWORD", ""));
				this.mRecordPwdBox.setChecked(true);
			}
		}

	}

	private void showWaringDialog(String waringInfo) {

		ToastUtils.showLong( waringInfo);
	}

	private boolean loginOnClick() {
		if (checkLoginData()) {
			saveAreaData();
			login();
			return true;
		} else {
			return false;
		}
	}

	private void saveAreaData() {
		Editor editor = sp_humanInfo.edit();

		editor.putString("PROJECT_NAME",
				GisQueryApplication.getApp().getProjectsconfig().getCurrentProjectName());
		editor.putString("AREA_NAME", mAreaTextView.getText().toString());
		editor.putString("UNIT_NAME", mOfficeTextView.getText().toString());
		editor.putString("USER_NAME", mPeoTextView.getText().toString());
		editor.putString("PWD", mPwdTextView.getText().toString());
		editor.putString("CANTON_ID", mCantonIDTextView.getText().toString());
		editor.putString("UNITID", mUnitIDTextView.getText().toString());
		if (SysConfig.mHumanInfo != null) {
			editor.putInt("HUMAN_ID", SysConfig.mHumanInfo.getHumanId());
		}
		editor.commit();
	}

	private void login() {
		if (this.mRecordPwdBox.isChecked()) {
			// 记住密码、
			Editor editor = sp_pwdInfo.edit();
			editor.putString("PASSWORD", mTextPassWord.getText().toString());
			editor.commit();
		}
	}

	public void onActivityResult(int requestCode, int resultCode,
			Intent paramIntent) {

		if (requestCode == 0) {
			String areaName = "";

			if (SysConfig.mProCanton != null) {// 省
				areaName = areaName + SysConfig.mProCanton.getCantonName()
						+ "-";
			}
			if (SysConfig.mCityCanton != null) {// 市
				areaName = areaName + SysConfig.mCityCanton.getCantonName()
						+ "-";
			}
			if (SysConfig.mCountyCanton != null) {// 县
				areaName = areaName + SysConfig.mCountyCanton.getCantonName();
			}
			if (areaName.length() > 0) {// 行政区名
				areaName = areaName.substring(0, -1 + areaName.length());
				this.mAreaTextView.setText(areaName);
				mOfficeTextView.setText("");// 行政区改变时，单位、人员、密码设置为空
				mPeoTextView.setText("");
				mPwdTextView.setText("");
				this.mTextPassWord.setText("");

				if (SysConfig.mCityCanton != null) {
					SysConfig.mCurrentCanton = SysConfig.mCountyCanton == null ? SysConfig.mCityCanton
							: SysConfig.mCountyCanton;
					cantonID = SysConfig.mCountyCanton == null ? SysConfig.mCityCanton
							.getCantonId() : SysConfig.mCountyCanton
							.getCantonId();
				} else {
					SysConfig.mCurrentCanton = SysConfig.mProCanton;
					cantonID = SysConfig.mProCanton.getCantonId();
				}
				this.mCantonIDTextView.setText("" + cantonID);
			}
			if (SysConfig.mUnitInfo != null) {// 单位
				if (resultCode == -1) {

					mOfficeTextView.setText("");
					mUnitIDTextView.setText("");
					SysConfig.mUnitInfo = null;
				} else {
					mOfficeTextView.setText(SysConfig.mUnitInfo.getUnitName());
					unitID = SysConfig.mUnitInfo.getUnitId();
					mPeoTextView.setText("");
					mPwdTextView.setText("");
					this.mTextPassWord.setText("");
					mUnitIDTextView.setText(""+unitID);
				}

			} else {
				mOfficeTextView.setText("");
			}
			if (SysConfig.mProCanton != null) {// 人员
				if (SysConfig.mHumanInfo != null) {
					mPeoTextView.setText(SysConfig.mHumanInfo.getHumanName());
					mPwdTextView.setText(SysConfig.mHumanInfo.getPassword());
				} else {
					mPeoTextView.setText("");
					mPwdTextView.setText("");
				}
				this.mTextPassWord.setText("");// 当切换人员时，将密码框设置为空
			} else {
				this.mPeoTextView.setText("");
			}
		} else if (requestCode == 1) {// 单位
			if (SysConfig.mUnitInfo != null) {
				mOfficeTextView.setText(SysConfig.mUnitInfo.getUnitName());
				unitID = SysConfig.mUnitInfo.getUnitId();
				mUnitIDTextView.setText(""+unitID);
				mPeoTextView.setText("");
				mPwdTextView.setText("");
				this.mTextPassWord.setText("");
			}
		} else if (requestCode == 2) {// 人员
			if (SysConfig.mHumanInfo != null) {
				mPeoTextView.setText(SysConfig.mHumanInfo.getHumanName());
				mPwdTextView.setText(SysConfig.mHumanInfo.getPassword());
				this.mTextPassWord.setText("");
			}
		}
	}

	private boolean checkLoginData() {
		if (this.mAreaTextView.getText().toString().equals("")) {
			ToastUtils.showLong( "请选择区域信息");
			return false;
		}
		if (this.mOfficeTextView.getText().toString().equals("")) {
			ToastUtils.showLong( "请选择单位信息");
			return false;
		}
		if (this.mPeoTextView.getText().toString().equals("")) {
			ToastUtils.showLong( "请选择人员信息");
			return false;
		}
		if (this.mTextPassWord.getText().toString().equals("")) {
			ToastUtils.showLong( "请输入密码");
			return false;
		}
		if (mPeoTextView.getText().toString() != null) {// 用户名存在
			String md5Pwd = MD5.GetMD5Code(this.mTextPassWord.getText().toString());
			if (md5Pwd.equalsIgnoreCase(mPwdTextView.getText().toString())) {
				return true;
			} else {
				ToastUtils.showLong( "密码不正确");
				return false;
			}
		} else {
			ToastUtils.showLong( "登陆失败！");
			return false;
		}
	}

}
