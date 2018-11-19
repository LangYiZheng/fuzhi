package com.guyu.android.gis.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;

import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.guyu.android.database.task.CaseSoundDBMExternal;
import com.guyu.android.gis.app.GisQueryApplication;
import com.guyu.android.gis.common.CaseSound;
import com.guyu.android.utils.FileUtil;
import com.guyu.android.R;

public class SoundViewActivity extends Activity {
	private View.OnClickListener onClickListener = new MOnClickListener();
	private List<Boolean> mSoundSelect = new ArrayList<Boolean>();
	private List<String> mSoundPath = new ArrayList<String>();
	private List<String> mSoundNames = new ArrayList<String>();
	private List<CaseSound> mAllCaseSoundObjs = null;
	private ListView mSoundListView = null;
	private String mCaseID = null;
	private CaseSoundDBMExternal mLandSoundManager = null;
	private MediaPlayer mPlayer = null;// 语音操作对象
	private static String TAG = "SoundViewActivity";

	protected void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		setContentView(R.layout.activity_sound_show);

		initData();
		findViewById(R.id.buttonCancle)
				.setOnClickListener(this.onClickListener);
		this.mSoundListView = ((ListView) findViewById(R.id.list));
		this.mSoundListView.setAdapter(new SoundSelectAdapter(this,
				this.mSoundNames, this.mSoundPath, this.mSoundSelect));
	}

	private void initData() {
		this.mCaseID = getIntent().getStringExtra("caseId");
		this.mLandSoundManager = new CaseSoundDBMExternal(this);
		if (this.mCaseID != null)
			this.mAllCaseSoundObjs = this.mLandSoundManager
					.getTheCaseAllSound(this.mCaseID);
		if ((this.mAllCaseSoundObjs != null)
				&& (this.mAllCaseSoundObjs.size() > 0)) {
			Iterator<CaseSound> localIterator = this.mAllCaseSoundObjs
					.iterator();
			while (localIterator.hasNext()) {
				CaseSound localLandSoundObj = localIterator
						.next();
				if (!(FileUtil.IsExist(localLandSoundObj.getSoundPath())))
					continue;
				this.mSoundPath.add(localLandSoundObj.getSoundPath());
				this.mSoundNames.add(localLandSoundObj.getSoundName());
				this.mSoundSelect.add(Boolean.valueOf(true));
			}
		}

	}

	private class MOnClickListener implements View.OnClickListener {
		public void onClick(View paramView) {
			switch (paramView.getId()) {
			default:
			case R.id.buttonCancle:
				SoundViewActivity.this.finish();
				break;
			}

		}
	}

	public class SoundSelectAdapter extends BaseAdapter {
		private LayoutInflater mInflater = null;
		private List<String> mSoundNames = null;
		private List<String> mSoundPaths = null;
		private List<Boolean> mSelectList = null;

		public SoundSelectAdapter(Activity activity,
				List<String> soundNameList, List<String> soundPathList,
				List<Boolean> selectList) {
			this.mInflater = ((LayoutInflater) activity
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE));
			this.mSoundNames = soundNameList;
			this.mSoundPaths = soundPathList;
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
				View localView = this.mInflater
						.inflate(R.layout.listview_sound_row_show,
								paramViewGroup, false);
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
					SoundViewActivity.this.mPlayer.release();
					SoundViewActivity.this.mPlayer = null;
					view.setSelected(!view.isSelected());
				} else {
					if (SoundViewActivity.this.mPlayer == null) {
						SoundViewActivity.this.mPlayer = new MediaPlayer();
						// 设置播放完成监听
						OnCompletionListener completListener = new OnCompletionListener() {

							@Override
							public void onCompletion(MediaPlayer arg0) {
								SoundViewActivity.this.mPlayer.release();
								SoundViewActivity.this.mPlayer = null;
								view.setSelected(!view.isSelected());
							}
						};
						SoundViewActivity.this.mPlayer
								.setOnCompletionListener(completListener);
					}
					try {
						if (!SoundViewActivity.this.mPlayer.isPlaying()) {
							SoundViewActivity.this.mPlayer
									.setDataSource(mFilePath);
							SoundViewActivity.this.mPlayer.prepare();
							SoundViewActivity.this.mPlayer.start();
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