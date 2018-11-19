package com.guyu.android.gis.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.guyu.android.database.task.CaseVideoDBMExternal;
import com.guyu.android.gis.app.GisQueryApplication;
import com.guyu.android.gis.common.CaseVideo;
import com.guyu.android.utils.FileUtil;
import com.guyu.android.R;

public class VideoViewActivity extends Activity {
	private View.OnClickListener onClickListener = new MOnClickListener();
	private List<Boolean> mVideoSelect = new ArrayList<Boolean>();
	private List<String> mVideoPath = new ArrayList<String>();
	private List<String> mThumbnailPath = new ArrayList<String>();
	private List<String> mVideoNames = new ArrayList<String>();
	private List<CaseVideo> mAllCaseVideoObjs = null;
	private ListView mVideoListView = null;
	private String mCaseID = null;
	private CaseVideoDBMExternal mCaseVideoManager = null;

	protected void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		setContentView(R.layout.activity_video_show);


		initData();
		findViewById(R.id.buttonCancle).setOnClickListener(this.onClickListener);
		this.mVideoListView = ((ListView) findViewById(R.id.list));
		this.mVideoListView.setAdapter(new VideoSelectAdapter(this, this.mVideoNames, this.mVideoPath, this.mThumbnailPath, this.mVideoSelect));
		this.mVideoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> paramAdapterView, View paramView, int paramInt, long paramLong) {
				List<Boolean> localList = VideoViewActivity.this.mVideoSelect;
				boolean bool = VideoViewActivity.this.mVideoSelect.get(paramInt);
				localList.set(paramInt, !bool);
				VideoViewActivity.this.mVideoListView.invalidateViews();
			}
		});
	}

	private void initData() {
		Intent localIntent = getIntent();
		if (localIntent != null)
			this.mCaseID = localIntent.getStringExtra("caseId");
		this.mCaseVideoManager = new CaseVideoDBMExternal(this);
		if (this.mCaseID != null)
			this.mAllCaseVideoObjs = this.mCaseVideoManager.getTheCaseAllVideo(this.mCaseID);
		if ((this.mAllCaseVideoObjs != null) && (this.mAllCaseVideoObjs.size() > 0)) {
			Iterator<CaseVideo> localIterator = this.mAllCaseVideoObjs.iterator();
			while (localIterator.hasNext()) {
				CaseVideo localLandVideoObj = localIterator.next();
				if (!(FileUtil.IsExist(localLandVideoObj.getVideoPath())))
					continue;
				this.mVideoPath.add(localLandVideoObj.getVideoPath());
				this.mThumbnailPath.add(localLandVideoObj.getThumbnailPath());
				this.mVideoNames.add(localLandVideoObj.getVideoName());
				this.mVideoSelect.add(Boolean.valueOf(true));
			}
		}

	}

	private class MOnClickListener implements View.OnClickListener {
		public void onClick(View paramView) {
			switch (paramView.getId()) {
			default:
			case R.id.buttonCancle:
				VideoViewActivity.this.finish();
				break;
			}

		}
	}

	public class VideoSelectAdapter extends BaseAdapter {
		private LayoutInflater mInflater = null;
		private List<String> mVideoNames = null;
		private List<String> mVideoPaths = null;
		private List<String> mThumbnailPaths = null;
		private List<Boolean> mSelectList = null;

		public VideoSelectAdapter(Activity activity, List<String> videoNameList, List<String> videoPathList, List<String> thumbnailPathList, List<Boolean> selectList) {
			this.mInflater = ((LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE));
			this.mVideoNames = videoNameList;
			this.mVideoPaths = videoPathList;
			this.mThumbnailPaths = thumbnailPathList;
			this.mSelectList = selectList;
		}

		public int getCount() {
			return this.mVideoNames.size();
		}

		public Object getItem(int paramInt) {
			return this.mVideoNames.get(paramInt);
		}

		public long getItemId(int paramInt) {
			return paramInt;
		}

		public View getView(int paramInt, View paramView, ViewGroup paramViewGroup) {
			if (paramInt < this.mVideoNames.size()) {
				View localView = this.mInflater.inflate(R.layout.listview_video_row_show, paramViewGroup, false);
				ImageView localImageView = (ImageView) localView.findViewById(R.id.icon);
				BitmapFactory.Options localOptions = new BitmapFactory.Options();
				localOptions.inJustDecodeBounds = true;
				BitmapFactory.decodeFile((String) this.mVideoPaths.get(paramInt), localOptions);
				localOptions.inJustDecodeBounds = false;
				int i = (int) (localOptions.outHeight / 60.0F);
				if (i <= 0)
					i = 1;
				localOptions.inSampleSize = i;
				String thumbnailfilePath = this.mThumbnailPaths.get(paramInt);
				String videofilePath = this.mVideoPaths.get(paramInt);
				if(thumbnailfilePath!=null){
					Drawable d = Drawable.createFromPath(new File(thumbnailfilePath).getAbsolutePath());
					localImageView.setBackgroundDrawable(d);
				}

				// 点击播放视频
				localImageView.setOnClickListener(new PlayButtonOnClickListener(videofilePath));
				((TextView) localView.findViewById(R.id.fileName)).setText((CharSequence) this.mVideoNames.get(paramInt));
				if (paramInt < this.mSelectList.size())
					((CheckBox) localView.findViewById(R.id.checkBox01)).setChecked(this.mSelectList.get(paramInt).booleanValue());
				return localView;
			}
			return null;
		}
	}

	/**
	 * 视频播放按钮处理
	 */
	class PlayButtonOnClickListener implements OnClickListener {
		private String mFilePath;

		public PlayButtonOnClickListener(String filePath) {
			mFilePath = filePath;
		}

		@Override
		public void onClick(final View view) {
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_VIEW);
			Uri data = Uri.parse(mFilePath);
			intent.setDataAndType(data, "video/mp4");
			startActivity(intent);
		}
	}

}