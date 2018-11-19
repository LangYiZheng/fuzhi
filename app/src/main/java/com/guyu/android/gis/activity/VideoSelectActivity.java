package com.guyu.android.gis.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.CamcorderProfile;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONObject;

import com.blankj.utilcode.util.ToastUtils;
import com.guyu.android.database.task.CaseVideoDBMExternal;
import com.guyu.android.database.task.DocDefDBMExternal;
import com.guyu.android.gis.app.GisQueryApplication;
import com.guyu.android.gis.common.CallBack;
import com.guyu.android.gis.common.CaseVideo;
import com.guyu.android.gis.common.DocDef;
import com.guyu.android.gis.opt.MapOperate;
import com.guyu.android.utils.CameraFilePathUtils;
import com.guyu.android.utils.FileUtil;
import com.guyu.android.utils.UtilsTools;
import com.guyu.android.R;

public class VideoSelectActivity extends Activity {
	private Handler mHandler = null;
	private View.OnClickListener onClickListener = new MOnClickListener();
	private List<Boolean> mVideoSelect = new ArrayList<Boolean>();
	private List<String> mVideoPath = new ArrayList<String>();
	private List<String> mThumbnailPath = new ArrayList<String>();
	private List<String> mVideoNames = new ArrayList<String>();
	private List<String> mVideoDocDefIds = new ArrayList<String>();
	private List<CaseVideo> mAllCaseVideoObjs = null;
	private String m_CurrentCamaraVideo = "";
	private String m_CurrentCamaraVideoName = "";
	private List<String> mList_docDef = new ArrayList<String>();
	private ListView mVideoListView = null;
	private String mCaseId = null;
	private CaseVideoDBMExternal mLandVideoManager = null;
	private String[] docDefIDS;
	private Map<String, DocDef> docDefMap;
	private Spinner docDefSpinner;
	private String bizId = "-1";
	private Button buttonUpdate;
	private static String TAG = "VideoSelectActivity";

	protected void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		setContentView(R.layout.activity_video_select);

		this.mHandler = new MyHandler(Looper.myLooper());
		docDefSpinner = (Spinner) findViewById(R.id.sp_value);
		initData();
		findViewById(R.id.buttonConfirm).setOnClickListener(this.onClickListener);
		findViewById(R.id.buttonCancle).setOnClickListener(this.onClickListener);
		buttonUpdate = (Button) findViewById(R.id.buttonUpdate);
		buttonUpdate.setOnClickListener(this.onClickListener);
		findViewById(R.id.toRecordVideo).setOnClickListener(this.onClickListener);
		this.mVideoListView = ((ListView) findViewById(R.id.list));
		this.mVideoListView.setAdapter(new VideoSelectAdapter(this, this.mVideoNames, this.mVideoPath, this.mThumbnailPath, this.mVideoDocDefIds, this.mVideoSelect));
		this.mVideoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> paramAdapterView, View paramView, int paramInt, long paramLong) {
				List<Boolean> localList = VideoSelectActivity.this.mVideoSelect;
				boolean bool = VideoSelectActivity.this.mVideoSelect.get(paramInt);
				localList.set(paramInt, !bool);
				VideoSelectActivity.this.mVideoListView.invalidateViews();
			}
		});
	}

	private void initData() {
		this.bizId = getIntent().getStringExtra("bizId");
		this.mCaseId = getIntent().getStringExtra("caseId");
		refreshDocDefInfo();
		this.mLandVideoManager = new CaseVideoDBMExternal(this);
		if (this.mCaseId != null)
			this.mAllCaseVideoObjs = this.mLandVideoManager.getTheCaseAllVideo(this.mCaseId);
		if ((this.mAllCaseVideoObjs != null) && (this.mAllCaseVideoObjs.size() > 0)) {
			Iterator<CaseVideo> localIterator = this.mAllCaseVideoObjs.iterator();
			while (localIterator.hasNext()) {
				CaseVideo localLandVideoObj = localIterator.next();
				if (!(FileUtil.IsExist(localLandVideoObj.getVideoPath())))
					continue;
				this.mVideoPath.add(localLandVideoObj.getVideoPath());
				this.mThumbnailPath.add(localLandVideoObj.getThumbnailPath());
				this.mVideoNames.add(localLandVideoObj.getVideoName());
				this.mVideoDocDefIds.add(localLandVideoObj.getDocDefId());
				this.mVideoSelect.add(Boolean.valueOf(true));
			}
		}

	}

	private void refreshDocDefInfo() {
		// 查询要件定义类别
		List<DocDef> al_docDef = new DocDefDBMExternal(VideoSelectActivity.this).queryDocDefInfoByBizId(bizId);
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
		ArrayAdapter<String> localArrayAdapter = new ArrayAdapter<String>(this, R.layout.simple_spinner_item, this.mList_docDef);
		docDefSpinner.setAdapter(localArrayAdapter);
	}

	private class MOnClickListener implements View.OnClickListener {
		public void onClick(View paramView) {
			switch (paramView.getId()) {
			default:
			case R.id.buttonConfirm: {
				int mVideoSelectSize = VideoSelectActivity.this.mVideoSelect.size();
				ArrayList<String> al_videoPath = new ArrayList<String>();
				ArrayList<String> al_videoName = new ArrayList<String>();
				ArrayList<String> al_thumbnailPath = new ArrayList<String>();
				ArrayList<String> al_docDefIds = new ArrayList<String>();
				for (int j = 0; j < mVideoSelectSize; j++) {

					if (!VideoSelectActivity.this.mVideoSelect.get(j))
						continue;
					al_videoPath.add(VideoSelectActivity.this.mVideoPath.get(j));
					al_thumbnailPath.add(VideoSelectActivity.this.mThumbnailPath.get(j));
					al_videoName.add(VideoSelectActivity.this.mVideoNames.get(j));
					al_docDefIds.add(VideoSelectActivity.this.mVideoDocDefIds.get(j));
				}
				VideoSelectActivity.this.returnResult(al_videoPath, al_thumbnailPath, al_videoName, al_docDefIds);
				break;
			}
			case R.id.buttonCancle:
				VideoSelectActivity.this.finish();
				break;
			case R.id.buttonUpdate:
				buttonUpdate.setEnabled(false);
				ToastUtils.showLong(  "要件类别更新中...");
				UpdateBizDocDefThread updateThread = new UpdateBizDocDefThread();
				new Thread(updateThread).start();
				break;
			case R.id.toRecordVideo:
				VideoSelectActivity.this.onRecordVideo();
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
			VideoSelectActivity.this.updateBizDocDef(new CallBack() {
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
			URL url = new URL(GisQueryApplication.getApp().getProjectconfig().getBizDocDefDownloadUrl() + "?bizId=" + bizId);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(5000);
			conn.setRequestMethod("GET");
			Log.i("CONN", conn.toString());
			if (conn.getResponseCode() == 200) {
				InputStream ins = conn.getInputStream();
				parseBizDocDefData(ins, this, bizId);
			}
			if (callBack != null) {
				callBack.execute();
			}
		} catch (Exception e) {
			e.printStackTrace();
			mHandler.sendEmptyMessage(2222);
		}
	}

	private void parseBizDocDefData(InputStream ins, Context paramContext, String bizID) {
		String bizDocDefInfo;
		try {
			bizDocDefInfo = UtilsTools.inputStream2String(ins);
			if ("".equals(bizDocDefInfo) == false) {
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

	private void updateDocDefInfo(List<DocDef> docDefList, String bizId, Context paramContext) {
		new DocDefDBMExternal(paramContext).deleteDocDefInfoByBizId(bizId);
		new DocDefDBMExternal(paramContext).insertMultiDocDefInfo(docDefList);
	}

	private void refreshLandVideoObjs(String pCaseID, List<String> al_videoPath, List<String> al_thumbnailPath, List<String> al_videoName, List<String> al_docDefIds) {
		if ((al_videoPath != null) && (al_videoName != null) && (al_videoPath.size() == al_videoName.size()))
			this.mAllCaseVideoObjs.clear();
		for (int i = 0; i < al_videoName.size(); i++) {
			this.mAllCaseVideoObjs.add(new CaseVideo(pCaseID, al_docDefIds.get(i), al_videoName.get(i), al_videoPath.get(i), al_thumbnailPath.get(i), UtilsTools.GetCurrentTime()));
		}
	}

	private void returnResult(List<String> al_videoPath, List<String> al_thumbnailPath, List<String> al_videoName, List<String> al_docDefIds) {
		int length = al_videoPath.size();
		String[] arrayOfString = new String[length];
		for (int j = 0; j < length; j++) {
			if ((this.mLandVideoManager != null) && (this.mCaseId != null)) {
				this.mLandVideoManager.clearTheCaseVideoInfoByCaseID(this.mCaseId);
				refreshLandVideoObjs(this.mCaseId, al_videoPath, al_thumbnailPath, al_videoName, al_docDefIds);
				this.mLandVideoManager.insertMultiCaseVideosInfo(this.mAllCaseVideoObjs);
				arrayOfString[j] = al_videoPath.get(j);

			}

		}
		Intent localIntent = new Intent();
		localIntent.putExtra("videos", arrayOfString);
		setResult(-1, localIntent);
		finish();
		return;
	}

	/**
	 * 录像后回掉
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 393217) {
			if (resultCode == -1) {
				Bitmap bitmap = null;
				bitmap = ThumbnailUtils.createVideoThumbnail(VideoSelectActivity.this.m_CurrentCamaraVideo, Images.Thumbnails.MICRO_KIND);
				bitmap = ThumbnailUtils.extractThumbnail(bitmap, 180, 100, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
				String thumbnailPath = VideoSelectActivity.this.m_CurrentCamaraVideo.replace(".mp4", ".png");
				saveBitmap(bitmap, thumbnailPath);
				this.mVideoPath.add(VideoSelectActivity.this.m_CurrentCamaraVideo);
				this.mThumbnailPath.add(thumbnailPath);
				this.mVideoNames.add(VideoSelectActivity.this.m_CurrentCamaraVideoName);
				this.mVideoDocDefIds.add(docDefIDS[docDefSpinner.getSelectedItemPosition()]);
				Log.i("录像2", "路径=" + VideoSelectActivity.this.m_CurrentCamaraVideo);
				Log.i("录像2", "名字=" + VideoSelectActivity.this.m_CurrentCamaraVideoName);
				this.mVideoSelect.add(Boolean.valueOf(true));
				VideoSelectActivity.this.m_CurrentCamaraVideo = "";
				VideoSelectActivity.this.m_CurrentCamaraVideoName = "";
				VideoSelectActivity.this.mVideoListView.invalidateViews();
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	public void saveBitmap(Bitmap mBitmap, String filePath) {
		File f = new File(filePath);
		try {
			f.createNewFile();
		} catch (IOException e) {
			Log.e(TAG, "在保存图片时出错：" + e.toString());
		}
		FileOutputStream fOut = null;
		try {
			fOut = new FileOutputStream(f);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
		try {
			fOut.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			fOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void onRecordVideo() {
		if (Environment.getExternalStorageState().equals("mounted")) {
			String str1 = GisQueryApplication.getApp().getProjectPath() + "CameraVideo/";
			String str2 = UtilsTools.GetCurrentTime() + ".mp4";
			File localFile = new File(str1);
			if (!(localFile.exists()))
				localFile.mkdirs();
			VideoSelectActivity.this.m_CurrentCamaraVideo = str1 + str2;
			VideoSelectActivity.this.m_CurrentCamaraVideoName = str2;
			Log.i("录像1", "路径=" + VideoSelectActivity.this.m_CurrentCamaraVideo);
			Log.i("录像1", "名字=" + VideoSelectActivity.this.m_CurrentCamaraVideoName);
//			Uri localUri = Uri.fromFile(new File(str1, str2));
//			Intent openVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
//			openVideoIntent.putExtra("output", localUri);
//			openVideoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
//			VideoSelectActivity.this.startActivityForResult(openVideoIntent, 393217);

			Intent mOpenCameraIntent = new Intent();
			mOpenCameraIntent.setAction(MediaStore.ACTION_VIDEO_CAPTURE);

			mOpenCameraIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, CamcorderProfile.QUALITY_480P);

			Uri desUri = CameraFilePathUtils.getFileUri(this,m_CurrentCamaraVideo);

			if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
				//已申请camera权限
				//mOpenCameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
			}

			mOpenCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,desUri);
			startActivityForResult(mOpenCameraIntent,CameraFilePathUtils.getCameraPermissionRequestCode());

		} else {
			ToastUtils.showLong( "无SD卡");
		}

	}

	public class VideoSelectAdapter extends BaseAdapter {
		private LayoutInflater mInflater = null;
		private List<String> mVideoNames = null;
		private List<String> mVideoPaths = null;
		private List<String> mThumbnailPaths = null;
		private List<String> mVideoDocDefIds = null;
		private List<Boolean> mSelectList = null;

		public VideoSelectAdapter(Activity activity, List<String> videoNameList, List<String> videoPathList, List<String> thumbnailPathList, List<String> videoDocDefIds,
				List<Boolean> selectList) {
			this.mInflater = ((LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE));
			this.mVideoNames = videoNameList;
			this.mVideoPaths = videoPathList;
			this.mThumbnailPaths = thumbnailPathList;
			this.mVideoDocDefIds = videoDocDefIds;
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
				View localView = this.mInflater.inflate(R.layout.listview_video_row, paramViewGroup, false);
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
				((TextView) localView.findViewById(R.id.docDefName)).setText(docDefMap.get(this.mVideoDocDefIds.get(paramInt)).getDocDefName());
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