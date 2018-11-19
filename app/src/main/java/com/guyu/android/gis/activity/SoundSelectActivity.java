package com.guyu.android.gis.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;

import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.blankj.utilcode.util.ToastUtils;
import com.guyu.android.database.task.CaseSoundDBMExternal;
import com.guyu.android.database.task.DocDefDBMExternal;
import com.guyu.android.gis.app.GisQueryApplication;
import com.guyu.android.gis.common.CallBack;
import com.guyu.android.gis.common.CaseSound;
import com.guyu.android.gis.common.DocDef;
import com.guyu.android.gis.opt.MapOperate;
import com.guyu.android.utils.FileUtil;
import com.guyu.android.utils.UtilsTools;
import com.guyu.android.R;

public class SoundSelectActivity extends Activity {
	private Handler mHandler = null;
	private View.OnClickListener onClickListener = new MOnClickListener();
	private List<Boolean> mSoundSelect = new ArrayList<Boolean>();
	private List<String> mSoundPaths = new ArrayList<String>();
	private List<String> mSoundNames = new ArrayList<String>();
	private List<String> mSoundDocDefIds = new ArrayList<String>();
	private List<CaseSound> mAllCaseSoundObjs = null;
	private String m_CurrentSound = "";
	private String m_CurrentSoundName = "";
	private List<String> mList_docDef = new ArrayList<String>();
	private ListView mSoundListView = null;
	private String mCaseId = null;
	private CaseSoundDBMExternal mCaseSoundManager = null;
	private MediaPlayer mPlayer = null;// 语音操作对象

	private String[] docDefIDS;
	private Map<String, DocDef> docDefMap;
	private Spinner docDefSpinner;
	private String bizId = "-1";
	private Button buttonUpdate;
	private static String TAG = "SoundSelectActivity";

	protected void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		setContentView(R.layout.activity_sound_select);

		this.mHandler = new MyHandler(Looper.myLooper());
		docDefSpinner = (Spinner) findViewById(R.id.sp_value);
		initData();
		findViewById(R.id.buttonConfirm).setOnClickListener(
				this.onClickListener);
		findViewById(R.id.buttonCancle)
				.setOnClickListener(this.onClickListener);
		buttonUpdate = (Button) findViewById(R.id.buttonUpdate);
		buttonUpdate.setOnClickListener(this.onClickListener);
		findViewById(R.id.toRecordSound).setOnClickListener(
				this.onClickListener);
		this.mSoundListView = ((ListView) findViewById(R.id.list));
		this.mSoundListView.setAdapter(new SoundSelectAdapter(this,
				this.mSoundNames, this.mSoundPaths, this.mSoundDocDefIds,
				this.mSoundSelect));
		this.mSoundListView
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					public void onItemClick(AdapterView<?> paramAdapterView,
							View paramView, int paramInt, long paramLong) {
						List<Boolean> localList = SoundSelectActivity.this.mSoundSelect;
						boolean bool = SoundSelectActivity.this.mSoundSelect
								.get(paramInt);
						localList.set(paramInt, !bool);
						SoundSelectActivity.this.mSoundListView
								.invalidateViews();
					}
				});
	}

	private void initData() {
		this.bizId = getIntent().getStringExtra("bizId");
		this.mCaseId = getIntent().getStringExtra("caseId");
		refreshDocDefInfo();		
		this.mCaseSoundManager = new CaseSoundDBMExternal(this);
		if (this.mCaseId != null)
			this.mAllCaseSoundObjs = this.mCaseSoundManager
					.getTheCaseAllSound(this.mCaseId);
		if ((this.mAllCaseSoundObjs != null)
				&& (this.mAllCaseSoundObjs.size() > 0)) {
			Iterator<CaseSound> localIterator = this.mAllCaseSoundObjs
					.iterator();
			while (localIterator.hasNext()) {
				CaseSound localLandSoundObj = localIterator.next();
				if (!(FileUtil.IsExist(localLandSoundObj.getSoundPath())))
					continue;
				this.mSoundPaths.add(localLandSoundObj.getSoundPath());
				this.mSoundNames.add(localLandSoundObj.getSoundName());
				this.mSoundDocDefIds.add(localLandSoundObj.getDocDefId());
				this.mSoundSelect.add(Boolean.valueOf(true));
			}
		}

	}
	private void refreshDocDefInfo() {
		// 查询要件定义类别
		List<DocDef> al_docDef = new DocDefDBMExternal(SoundSelectActivity.this)
				.queryDocDefInfoByBizId(bizId);
		docDefIDS = new String[al_docDef.size() + 1];
		mList_docDef.clear();
		mList_docDef.add("默认");
		docDefIDS[0] = "0";
		docDefMap = new HashMap<String, DocDef>();
		DocDef docDef0 = new DocDef();
		docDef0.setDispOrder(0);
		docDef0.setDocDefID("0");
		docDef0.setDocDefName("默认");
		docDefMap.put("0", docDef0);
		int i = 1;
		for (DocDef docDef : al_docDef) {
			docDefMap.put(docDef.getDocDefID(), docDef);
			docDefIDS[i] = docDef.getDocDefID();
			mList_docDef.add(docDef.getDocDefName());
			i++;
		}
		ArrayAdapter localArrayAdapter = new ArrayAdapter(this,
				R.layout.simple_spinner_item, this.mList_docDef);
		docDefSpinner.setAdapter(localArrayAdapter);
	}
	private class MOnClickListener implements View.OnClickListener {
		public void onClick(View paramView) {
			switch (paramView.getId()) {
			default:
			case R.id.buttonConfirm: {
				int mSoundSelectSize = SoundSelectActivity.this.mSoundSelect
						.size();
				ArrayList<String> localArrayList1 = new ArrayList<String>();
				ArrayList<String> localArrayList2 = new ArrayList<String>();
				ArrayList<String> localArrayList3 = new ArrayList<String>();
				for (int j = 0; j < mSoundSelectSize; j++) {

					if (!SoundSelectActivity.this.mSoundSelect.get(j))
						continue;
					localArrayList1
							.add(SoundSelectActivity.this.mSoundPaths
									.get(j));
					localArrayList2
							.add(SoundSelectActivity.this.mSoundNames
									.get(j));
					localArrayList3
							.add(SoundSelectActivity.this.mSoundDocDefIds
									.get(j));
				}
				SoundSelectActivity.this.returnResult(localArrayList1,
						localArrayList2, localArrayList3);
				break;
			}
			case R.id.buttonCancle:
				SoundSelectActivity.this.finish();
				break;
			case R.id.buttonUpdate:
				buttonUpdate.setEnabled(false);
				Toast.makeText(getBaseContext(), "要件类别更新中...", Toast.LENGTH_LONG).show();
				UpdateBizDocDefThread updateThread = new UpdateBizDocDefThread();
				new Thread(updateThread).start();
				break;
			case R.id.toRecordSound:
				SoundSelectActivity.this.onRecordSound();
				break;
			}

		}
	}

	private class MyHandler extends Handler {
		public MyHandler(Looper paramLooper) {
			super(paramLooper);
		}

		public void handleMessage(Message paramMessage) {
			switch (paramMessage.what) {
			case 1111: {
				refreshDocDefInfo();
				buttonUpdate.setEnabled(true);
				ToastUtils.showLong(  "要件类别更新成功！");
				break;
			}
			case 2222: {				
				buttonUpdate.setEnabled(true);
				ToastUtils.showLong(  "要件类别更新失败，请稍后重试！");
				break;
			}
			}
		}
	}
	class UpdateBizDocDefThread implements Runnable {
		public void run() {
			SoundSelectActivity.this.updateBizDocDef(new CallBack() {
						@Override
						public void execute() {
							mHandler.sendEmptyMessage(1111);
						}
					});
		}
	}
	/**
	 * 更新要件定义
	 */
	private void updateBizDocDef(CallBack callBack) {
		try {
			URL url = new URL(
					GisQueryApplication.getApp().getProjectconfig().getBizDocDefDownloadUrl()
							+ "?bizId=" + bizId);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(5000);
			conn.setRequestMethod("GET");
			Log.i("CONN", conn.toString());
			if (conn.getResponseCode() == 200) {
				InputStream ins = conn.getInputStream();
				parseBizDocDefData(ins, this, bizId);
			}
			if(callBack!=null){
				callBack.execute();
			}			
		} catch (Exception e) {
			e.printStackTrace();
			mHandler.sendEmptyMessage(2222);
		}
	}

	private void parseBizDocDefData(InputStream ins, Context paramContext,
			String bizID) {
		String bizDocDefInfo;
		try {
			bizDocDefInfo = UtilsTools.inputStream2String(ins);
			if("".equals(bizDocDefInfo)==false){
				JSONArray bizdocdefs = new JSONArray(bizDocDefInfo);
				List<DocDef> listDocDef = new ArrayList<DocDef>();
				// 要件定义数据
				for (int i = 0; i < bizdocdefs.length(); i++) {
					JSONObject bizdocdef = bizdocdefs.getJSONObject(i);
					String lDocDefID = bizdocdef.getString("DOCDEFID");
					String lDocDefName = bizdocdef.getString("DOCDEFNAME");
					String lBizID = bizdocdef.getString("BIZID");
					int lDispOrder = bizdocdef.getInt("DISPORDER");
					DocDef docDef = new DocDef();
					docDef.setDocDefID(lDocDefID);
					docDef.setDocDefName(lDocDefName);
					docDef.setBizID(lBizID);
					docDef.setDispOrder(lDispOrder);
					listDocDef.add(docDef);
				}
				if (listDocDef.size() > 0) {
					updateDocDefInfo(listDocDef, "" + bizID, paramContext);
				}
			}		
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	private void updateDocDefInfo(List<DocDef> docDefList, String bizId,
			Context paramContext) {
		new DocDefDBMExternal(paramContext).deleteDocDefInfoByBizId(bizId);
		new DocDefDBMExternal(paramContext).insertMultiDocDefInfo(docDefList);
	}


	private void refreshLandSoundObjs(String p_caseId,
			List<String> p_soundPaths, List<String> p_soundNames,
			List<String> p_soundDocDefIds) {
		if ((p_soundPaths != null) && (p_soundNames != null)
				&& (p_soundPaths.size() == p_soundNames.size()))
			this.mAllCaseSoundObjs.clear();
		for (int i = 0; i < p_soundNames.size(); i++) {
			this.mAllCaseSoundObjs.add(new CaseSound(p_caseId, p_soundDocDefIds
					.get(i), p_soundNames.get(i), p_soundPaths.get(i),
					UtilsTools.GetCurrentTime()));
		}
	}

	private void returnResult(List<String> p_soundPaths,
			List<String> p_soundNames, List<String> p_soundDocDefIds) {
		int length = p_soundPaths.size();
		String[] arrayOfString = new String[length];
		for (int j = 0; j < length; j++) {
			if ((this.mCaseSoundManager != null) && (this.mCaseId != null)) {
				this.mCaseSoundManager
						.clearTheCaseSoundInfoByCaseID(this.mCaseId);
				refreshLandSoundObjs(this.mCaseId, p_soundPaths, p_soundNames,
						p_soundDocDefIds);
				this.mCaseSoundManager
						.insertMultiCaseSoundsInfo(this.mAllCaseSoundObjs);
				arrayOfString[j] = p_soundPaths.get(j);

			}

		}
		Intent localIntent = new Intent();
		localIntent.putExtra("sounds", arrayOfString);
		setResult(-1, localIntent);
		finish();
		return;
	}

	/**
	 * 录音后回掉
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 393218) {
			if (resultCode == 0) {// 录音完成
				// Uri uri = data.getData();
				// FileUtil.copyFile(uri,
				// SoundSelectActivity.this.m_CurrentSound, this);
				this.mSoundPaths.add(SoundSelectActivity.this.m_CurrentSound);
				this.mSoundNames
						.add(SoundSelectActivity.this.m_CurrentSoundName);
				this.mSoundDocDefIds.add(docDefIDS[docDefSpinner
						.getSelectedItemPosition()]);
				Log.i("录音2", "路径=" + SoundSelectActivity.this.m_CurrentSound);
				Log.i("录音2", "名字="
						+ SoundSelectActivity.this.m_CurrentSoundName);
				this.mSoundSelect.add(Boolean.valueOf(true));
				SoundSelectActivity.this.m_CurrentSound = "";
				SoundSelectActivity.this.m_CurrentSoundName = "";
				SoundSelectActivity.this.mSoundListView.invalidateViews();
			} else {// 放弃录音
				SoundSelectActivity.this.m_CurrentSound = "";
				SoundSelectActivity.this.m_CurrentSoundName = "";
				SoundSelectActivity.this.mSoundListView.invalidateViews();
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void onRecordSound() {
		if (Environment.getExternalStorageState().equals("mounted")) {
			String str1 = GisQueryApplication.getApp()
					.getProjectPath() + "Sounds/";
			String str2 = UtilsTools.GetCurrentTime() + ".mp3";
			File localFile = new File(str1);
			if (!(localFile.exists()))
				localFile.mkdirs();
			SoundSelectActivity.this.m_CurrentSound = str1 + str2;
			SoundSelectActivity.this.m_CurrentSoundName = str2;
			Log.i("录音1", "路径=" + SoundSelectActivity.this.m_CurrentSound);
			Log.i("录音1", "名字=" + SoundSelectActivity.this.m_CurrentSoundName);

			Intent localIntent = new Intent(
					SoundSelectActivity.this,RecordSoundActivity.class);


			localIntent.putExtra("soundFilePath",
					SoundSelectActivity.this.m_CurrentSound);
			SoundSelectActivity.this
					.startActivityForResult(localIntent, 393218);
		} else {
			Toast.makeText(this, "无SD卡", Toast.LENGTH_LONG).show();
		}

	}

	public class SoundSelectAdapter extends BaseAdapter {
		private LayoutInflater mInflater = null;
		private List<String> mSoundNames = null;
		private List<String> mSoundPaths = null;
		private List<String> mSoundDocDefIds = null;
		private List<Boolean> mSelectList = null;

		public SoundSelectAdapter(Activity activity,
				List<String> soundNameList, List<String> soundPathList,
				List<String> soundDocDefIds, List<Boolean> selectList) {
			this.mInflater = ((LayoutInflater) activity
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE));
			this.mSoundNames = soundNameList;
			this.mSoundPaths = soundPathList;
			this.mSoundDocDefIds = soundDocDefIds;
			this.mSelectList = selectList;
		}

		public int getCount() {
			return this.mSoundNames.size();
		}

		public Object getItem(int paramInt) {
			return this.mSoundNames.get(paramInt);
		}

		public long getItemId(int paramInt) {
			return paramInt;
		}

		public View getView(int paramInt, View paramView,
				ViewGroup paramViewGroup) {
			if (paramInt < this.mSoundNames.size()) {
				View localView = this.mInflater.inflate(
						R.layout.listview_sound_row, paramViewGroup, false);
				BitmapFactory.Options localOptions = new BitmapFactory.Options();
				localOptions.inJustDecodeBounds = true;
				BitmapFactory.decodeFile(
						(String) this.mSoundPaths.get(paramInt), localOptions);
				localOptions.inJustDecodeBounds = false;
				int i = (int) (localOptions.outHeight / 60.0F);
				if (i <= 0)
					i = 1;
				localOptions.inSampleSize = i;
				String filePath = this.mSoundPaths.get(paramInt);
				((TextView) localView.findViewById(R.id.fileName))
						.setText((CharSequence) this.mSoundNames.get(paramInt));
				((TextView) localView.findViewById(R.id.docDefName))
						.setText(docDefMap.get(
								this.mSoundDocDefIds.get(paramInt))
								.getDocDefName());
				if (paramInt < this.mSelectList.size())
					((CheckBox) localView.findViewById(R.id.checkBox01))
							.setChecked(this.mSelectList
									.get(paramInt).booleanValue());
				ImageView btn_play = (ImageView) localView
						.findViewById(R.id.btn_soundPlay);

				btn_play.setOnClickListener(new PlayButtonOnClickListener(
						filePath));
				return localView;
			}
			return null;
		}

		/**
		 * 音频播放按钮处理
		 */
		class PlayButtonOnClickListener implements OnClickListener {
			private String mFilePath;

			public PlayButtonOnClickListener(String filePath) {
				mFilePath = filePath;
			}

			@Override
			public void onClick(final View view) {
				if (view.isSelected()) {
					SoundSelectActivity.this.mPlayer.release();
					SoundSelectActivity.this.mPlayer = null;
					view.setSelected(!view.isSelected());
				} else {
					if (SoundSelectActivity.this.mPlayer == null) {
						SoundSelectActivity.this.mPlayer = new MediaPlayer();
						// 设置播放完成监听
						OnCompletionListener completListener = new OnCompletionListener() {

							@Override
							public void onCompletion(MediaPlayer arg0) {
								SoundSelectActivity.this.mPlayer.release();
								SoundSelectActivity.this.mPlayer = null;
								view.setSelected(!view.isSelected());
							}
						};
						SoundSelectActivity.this.mPlayer
								.setOnCompletionListener(completListener);
					}
					try {
						if (!SoundSelectActivity.this.mPlayer.isPlaying()) {
							SoundSelectActivity.this.mPlayer
									.setDataSource(mFilePath);
							SoundSelectActivity.this.mPlayer.prepare();
							SoundSelectActivity.this.mPlayer.start();
							view.setSelected(!view.isSelected());
						}

					} catch (IOException e) {
						Log.e(TAG, "播放失败");
					}
				}

			}
		}
	}

}