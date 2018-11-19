package com.bairuitech.meeting;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.bairuitech.anychat.AnyChatBaseEvent;
import com.bairuitech.anychat.AnyChatCoreSDK;
import com.bairuitech.anychat.AnyChatDefine;
import com.bairuitech.meeting.config.ConfigEntity;
import com.bairuitech.meeting.config.ConfigService;
import com.guyu.android.R;
import com.guyu.android.gis.app.GisQueryApplication;
import com.guyu.android.gis.opt.MapOperate;
import com.guyu.android.utils.SysConfig;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MeetingActivity extends Activity implements AnyChatBaseEvent {
	// 视频配置界面标识
	public static final int ACTIVITY_ID_VIDEOCONFIG = 1;

	private ListView mRoleList;
	private EditText mEditRoomID;
	private TextView mBottomConnMsg;
	private TextView mRoom1;
	private TextView mRoom2;
	private TextView mRoom3;
	private TextView mRoom4;
	//private TextView mBottomBuildMsg;
	private Button mBtnStart;
	private Button mBtnLogout;
	private Button mBtnWaiting;
	private LinearLayout mWaitingLayout;
	private LinearLayout mProgressLayout;
	private String mStrIP = "192.168.0.29";
	private String mStrName = "name";
	private int mSPort = 8906;
	private int mSRoomID = 1;

	private final int SHOWLOGINSTATEFLAG = 1; // 显示的按钮是登陆状态的标识
	private final int SHOWWAITINGSTATEFLAG = 2; // 显示的按钮是等待状态的标识
	private final int SHOWLOGOUTSTATEFLAG = 3; // 显示的按钮是登出状态的标识
	private final int LOCALVIDEOAUTOROTATION = 1; // 本地视频自动旋转控制


	private List<RoleInfo> mRoleInfoList = new ArrayList<RoleInfo>();
	private RoleListAdapter mAdapter;
	private int UserselfID;

	public AnyChatCoreSDK anyChatSDK;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_meeting);
		InitSDK();
		InitLayout();
		initWaitingTips();
		ApplyVideoConfig();
		// 注册广播
		registerBoradcastReceiver();
	}

	private void InitSDK() {
		if (anyChatSDK == null) {
			anyChatSDK = AnyChatCoreSDK.getInstance(this);
			anyChatSDK.SetBaseEvent(this);
			anyChatSDK.InitSDK(android.os.Build.VERSION.SDK_INT, 0);
			AnyChatCoreSDK.SetSDKOptionInt(
					AnyChatDefine.BRAC_SO_LOCALVIDEO_AUTOROTATION,
					LOCALVIDEOAUTOROTATION);
		}
	}
	private void login() {
		mStrIP= GisQueryApplication.getApp().getProjectconfig().getAnyChatServerConfig().getIp();
		mSPort=Integer.parseInt(GisQueryApplication.getApp().getProjectconfig().getAnyChatServerConfig().getPort());
		mStrName=String.valueOf(SysConfig.mHumanInfo.getHumanName())+"("+SysConfig.mHumanInfo.getHumanId()+")";
		anyChatSDK.Connect(mStrIP, mSPort);
		anyChatSDK.Login(mStrName, "");
	}

	private void InitLayout() {
		mRoleList = (ListView) this.findViewById(R.id.roleListView);
		mEditRoomID = (EditText) this.findViewById(R.id.mainUIEditRoomID);
		mBottomConnMsg = (TextView) this.findViewById(R.id.mainUIbottomConnMsg);
		mRoom1 = (TextView) this.findViewById(R.id.room1);
		mRoom2 = (TextView) this.findViewById(R.id.room2);
		mRoom3 = (TextView) this.findViewById(R.id.room3);
		mRoom4 = (TextView) this.findViewById(R.id.room4);
//		mBottomBuildMsg = (TextView) this
//				.findViewById(R.id.mainUIbottomBuildMsg);
		mBtnStart = (Button) this.findViewById(R.id.mainUIStartBtn);
		mBtnLogout = (Button) this.findViewById(R.id.mainUILogoutBtn);
		mBtnWaiting = (Button) this.findViewById(R.id.mainUIWaitingBtn);
		mWaitingLayout = (LinearLayout) this.findViewById(R.id.waitingLayout);

		mRoleList.setDivider(null);
		mBottomConnMsg.setText(R.string.str_noconnect2server);
		// 初始化bottom_tips信息
//		mBottomBuildMsg.setText(" V" + anyChatSDK.GetSDKMainVersion() + "."
//				+ anyChatSDK.GetSDKSubVersion() + "  Build time: "
//				+ anyChatSDK.GetSDKBuildTime());
//		mBottomBuildMsg.setGravity(Gravity.CENTER_HORIZONTAL);
		mRoom1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mRoom1.setBackgroundColor(Color.YELLOW);
				mSRoomID = 1;
				mRoom2.setClickable(false);
				mRoom3.setClickable(false);
				mRoom4.setClickable(false);
				login();
			}
		});
		mRoom2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mRoom2.setBackgroundColor(Color.YELLOW);
				mSRoomID = 2;
				mRoom1.setClickable(false);
				mRoom3.setClickable(false);
				mRoom4.setClickable(false);
				login();
			}
		});
		mRoom3.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mRoom3.setBackgroundColor(Color.YELLOW);
				mSRoomID = 3;
				mRoom1.setClickable(false);
				mRoom2.setClickable(false);
				mRoom4.setClickable(false);
				login();
			}
		});
		mRoom4.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mRoom4.setBackgroundColor(Color.YELLOW);
				mSRoomID = 4;
				mRoom1.setClickable(false);
				mRoom2.setClickable(false);
				mRoom3.setClickable(false);
				login();
			}
		});
		mBtnStart.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (checkInputData()) {
					setBtnVisible(SHOWWAITINGSTATEFLAG);
					mSRoomID = Integer.parseInt(mEditRoomID.getText()
							.toString().trim());
					 login();
				}
			}
		});

		mBtnLogout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setBtnVisible(SHOWLOGINSTATEFLAG);
				anyChatSDK.LeaveRoom(-1);
				anyChatSDK.Logout();
				mRoleList.setAdapter(null);
				mRoom1.setBackgroundColor(Color.WHITE);
				mRoom2.setBackgroundColor(Color.WHITE);
				mRoom3.setBackgroundColor(Color.WHITE);
				mRoom4.setBackgroundColor(Color.WHITE);
				mRoom1.setClickable(true);
				mRoom2.setClickable(true);
				mRoom3.setClickable(true);
				mRoom4.setClickable(true);
				mBottomConnMsg.setText(R.string.str_noconnect2server);
			}
		});
	}

	private boolean checkInputData() {
		String roomID = mEditRoomID.getText().toString().trim();

		if (ValueUtils.isStrEmpty(roomID)) {
			mBottomConnMsg.setText("请输入房间号");
			return false;
		} else {
			return true;
		}
	}

	// 控制登陆，等待和登出按钮状态
	private void setBtnVisible(int index) {
		if (index == SHOWLOGINSTATEFLAG) {
			mBtnStart.setVisibility(View.VISIBLE);
			mBtnLogout.setVisibility(View.GONE);
			mBtnWaiting.setVisibility(View.GONE);

			mProgressLayout.setVisibility(View.GONE);
		} else if (index == SHOWWAITINGSTATEFLAG) {
			mBtnStart.setVisibility(View.GONE);
			mBtnLogout.setVisibility(View.GONE);
			mBtnWaiting.setVisibility(View.VISIBLE);

			mProgressLayout.setVisibility(View.VISIBLE);
		} else if (index == SHOWLOGOUTSTATEFLAG) {
			mBtnStart.setVisibility(View.GONE);
			mBtnLogout.setVisibility(View.VISIBLE);
			mBtnWaiting.setVisibility(View.GONE);

			mProgressLayout.setVisibility(View.GONE);
		}
	}

	// init登陆等待状态UI
	private void initWaitingTips() {
		if (mProgressLayout == null) {
			mProgressLayout = new LinearLayout(this);
			mProgressLayout.setOrientation(LinearLayout.HORIZONTAL);
			mProgressLayout.setGravity(Gravity.CENTER_VERTICAL);
			mProgressLayout.setPadding(1, 1, 1, 1);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			params.setMargins(5, 5, 5, 5);
			ProgressBar progressBar = new ProgressBar(this, null,
					android.R.attr.progressBarStyleLarge);
			mProgressLayout.addView(progressBar, params);
			mProgressLayout.setVisibility(View.GONE);
			mWaitingLayout.addView(mProgressLayout, new LayoutParams(params));
		}
	}

	private void hideKeyboard() {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm.isActive()) {
			imm.hideSoftInputFromWindow(getCurrentFocus()
					.getApplicationWindowToken(),
					InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	protected void onDestroy() {
		anyChatSDK.LeaveRoom(-1);
		anyChatSDK.Logout();
		anyChatSDK.Release();
		unregisterReceiver(mBroadcastReceiver);
		super.onDestroy();
	}

	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		anyChatSDK.SetBaseEvent(this);

		// 一种简便的方法，当断网的时候，返回到登录界面，不去刷新用户列表，下面广播已经清空了列表
		if (mBtnStart.getVisibility() != View.VISIBLE)
			updateUserList();
	}

	@Override
	public void OnAnyChatConnectMessage(boolean bSuccess) {
		if (!bSuccess) {
			setBtnVisible(SHOWLOGINSTATEFLAG);
			mBottomConnMsg.setText("连接服务器失败，自动重连，请稍后...");
		}
	}

	@Override
	public void OnAnyChatLoginMessage(int dwUserId, int dwErrorCode) {
		if (dwErrorCode == 0) {
			setBtnVisible(SHOWLOGOUTSTATEFLAG);
			hideKeyboard();

			mBottomConnMsg.setText(R.string.str_connect2serversuccess);
			int sHourseID = mSRoomID;
			anyChatSDK.EnterRoom(sHourseID, "");

			UserselfID = dwUserId;
			// finish();
		} else {
			setBtnVisible(SHOWLOGINSTATEFLAG);
			mBottomConnMsg.setText("登录失败，errorCode：" + dwErrorCode);
		}
	}

	@Override
	public void OnAnyChatEnterRoomMessage(int dwRoomId, int dwErrorCode) {
		System.out.println("OnAnyChatEnterRoomMessage" + dwRoomId + "err:"
				+ dwErrorCode);
	}

	@Override
	public void OnAnyChatOnlineUserMessage(int dwUserNum, int dwRoomId) {
		mBottomConnMsg.setText("进入房间成功！");
		updateUserList();
	}

	private void updateUserList() {
		mRoleInfoList.clear();
		final int[] userID = anyChatSDK.GetOnlineUser();
		RoleInfo userselfInfo = new RoleInfo();
		userselfInfo.setName(anyChatSDK.GetUserName(UserselfID)
				+ "(自己) 【点击可设置】");
		userselfInfo.setUserID(String.valueOf(UserselfID));
		userselfInfo.setRoleIconID(getRoleRandomIconID());
		mRoleInfoList.add(userselfInfo);

		for (int index = 0; index < userID.length; ++index) {
			RoleInfo info = new RoleInfo();
			info.setName(anyChatSDK.GetUserName(userID[index]));
			info.setUserID(String.valueOf(userID[index]));
			info.setRoleIconID(getRoleRandomIconID());
			mRoleInfoList.add(info);
		}

		mAdapter = new RoleListAdapter(MeetingActivity.this, mRoleInfoList);
		mRoleList.setAdapter(mAdapter);
		mRoleList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if (arg2 == 0) {
					Intent intent = new Intent();
					intent.setClass(MeetingActivity.this, VideoConfig.class);
					startActivityForResult(intent, ACTIVITY_ID_VIDEOCONFIG);
					return;
				}

				onSelectItem(arg2,userID);
			}
		});
	}

	private void onSelectItem(int postion,int[] userID) {
		Intent intent = new Intent();
		intent.setClass(this, VideoActivity.class);
		startActivity(intent);
	}

	private int getRoleRandomIconID() {
		int number = new Random().nextInt(5) + 1;
		if (number == 1) {
			return R.drawable.role_1;
		} else if (number == 2) {
			return R.drawable.role_2;
		} else if (number == 3) {
			return R.drawable.role_3;
		} else if (number == 4) {
			return R.drawable.role_4;
		} else if (number == 5) {
			return R.drawable.role_5;
		}

		return R.drawable.role_1;
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK && requestCode == ACTIVITY_ID_VIDEOCONFIG) {
			ApplyVideoConfig();
		}
	}

	// 根据配置文件配置视频参数
	private void ApplyVideoConfig() {
		ConfigEntity configEntity = ConfigService.LoadConfig(this);
		if (configEntity.mConfigMode == 1) // 自定义视频参数配置
		{
			// 设置本地视频编码的码率（如果码率为0，则表示使用质量优先模式）
			AnyChatCoreSDK.SetSDKOptionInt(
					AnyChatDefine.BRAC_SO_LOCALVIDEO_BITRATECTRL,
					configEntity.mVideoBitrate);
//			if (configEntity.mVideoBitrate == 0) {
				// 设置本地视频编码的质量
				AnyChatCoreSDK.SetSDKOptionInt(
						AnyChatDefine.BRAC_SO_LOCALVIDEO_QUALITYCTRL,
						configEntity.mVideoQuality);
//			}
			// 设置本地视频编码的帧率
			AnyChatCoreSDK.SetSDKOptionInt(
					AnyChatDefine.BRAC_SO_LOCALVIDEO_FPSCTRL,
					configEntity.mVideoFps);
			// 设置本地视频编码的关键帧间隔
			AnyChatCoreSDK.SetSDKOptionInt(
					AnyChatDefine.BRAC_SO_LOCALVIDEO_GOPCTRL,
					configEntity.mVideoFps * 4);
			// 设置本地视频采集分辨率
			AnyChatCoreSDK.SetSDKOptionInt(
					AnyChatDefine.BRAC_SO_LOCALVIDEO_WIDTHCTRL,
					configEntity.mResolutionWidth);
			AnyChatCoreSDK.SetSDKOptionInt(
					AnyChatDefine.BRAC_SO_LOCALVIDEO_HEIGHTCTRL,
					configEntity.mResolutionHeight);
			// 设置视频编码预设参数（值越大，编码质量越高，占用CPU资源也会越高）
			AnyChatCoreSDK.SetSDKOptionInt(
					AnyChatDefine.BRAC_SO_LOCALVIDEO_PRESETCTRL,
					configEntity.mVideoPreset);
		}
		// 让视频参数生效
		AnyChatCoreSDK.SetSDKOptionInt(
				AnyChatDefine.BRAC_SO_LOCALVIDEO_APPLYPARAM,
				configEntity.mConfigMode);
		// P2P设置
		AnyChatCoreSDK.SetSDKOptionInt(
				AnyChatDefine.BRAC_SO_NETWORK_P2PPOLITIC,
				configEntity.mEnableP2P);
		// 本地视频Overlay模式设置
		AnyChatCoreSDK.SetSDKOptionInt(
				AnyChatDefine.BRAC_SO_LOCALVIDEO_OVERLAY,
				configEntity.mVideoOverlay);
		// 回音消除设置
		AnyChatCoreSDK.SetSDKOptionInt(AnyChatDefine.BRAC_SO_AUDIO_ECHOCTRL,
				configEntity.mEnableAEC);
		// 平台硬件编码设置
		AnyChatCoreSDK.SetSDKOptionInt(
				AnyChatDefine.BRAC_SO_CORESDK_USEHWCODEC,
				configEntity.mUseHWCodec);
		// 视频旋转模式设置
		AnyChatCoreSDK.SetSDKOptionInt(
				AnyChatDefine.BRAC_SO_LOCALVIDEO_ROTATECTRL,
				configEntity.mVideoRotateMode);
		// 本地视频采集偏色修正设置
		AnyChatCoreSDK.SetSDKOptionInt(
				AnyChatDefine.BRAC_SO_LOCALVIDEO_FIXCOLORDEVIA,
				configEntity.mFixColorDeviation);
		// 视频GPU渲染设置
		AnyChatCoreSDK.SetSDKOptionInt(
				AnyChatDefine.BRAC_SO_VIDEOSHOW_GPUDIRECTRENDER,
				configEntity.mVideoShowGPURender);
		// 本地视频自动旋转设置
		AnyChatCoreSDK.SetSDKOptionInt(
				AnyChatDefine.BRAC_SO_LOCALVIDEO_AUTOROTATION,
				configEntity.mVideoAutoRotation);
	}

	@Override
	public void OnAnyChatUserAtRoomMessage(int dwUserId, boolean bEnter) {
		if (bEnter) {
			RoleInfo info = new RoleInfo();
			info.setUserID(String.valueOf(dwUserId));
			info.setName(anyChatSDK.GetUserName(dwUserId));
			info.setRoleIconID(getRoleRandomIconID());
			mRoleInfoList.add(info);
			mAdapter.notifyDataSetChanged();
		} else {

			for (int i = 0; i < mRoleInfoList.size(); i++) {
				if (mRoleInfoList.get(i).getUserID().equals("" + dwUserId)) {
					mRoleInfoList.remove(i);
					mAdapter.notifyDataSetChanged();
				}
			}
		}
	}

	@Override
	public void OnAnyChatLinkCloseMessage(int dwErrorCode) {
		setBtnVisible(SHOWLOGINSTATEFLAG);
		mRoleList.setAdapter(null);
		anyChatSDK.LeaveRoom(-1);
		anyChatSDK.Logout();
		mBottomConnMsg.setText("连接关闭，errorCode：" + dwErrorCode);
	}

	// 广播
	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals("VideoActivity")) {
				Toast.makeText(MeetingActivity.this, "网络已断开！", Toast.LENGTH_SHORT)
						.show();
				mRoom1.setClickable(true);
				mRoom2.setClickable(true);
				mRoom3.setClickable(true);
				mRoom4.setClickable(true);
				setBtnVisible(SHOWLOGINSTATEFLAG);
				mRoleList.setAdapter(null);
				mBottomConnMsg.setText(R.string.str_noconnect2server);
				anyChatSDK.LeaveRoom(-1);
				anyChatSDK.Logout();
			}
		}
	};

	public void registerBoradcastReceiver() {
		IntentFilter myIntentFilter = new IntentFilter();
		myIntentFilter.addAction("VideoActivity");
		// 注册广播
		registerReceiver(mBroadcastReceiver, myIntentFilter);
	}
}
