package com.guyu.android.gis.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.blankj.utilcode.util.ToastUtils;
import com.guyu.android.database.task.CaseImgDBMExternal;
import com.guyu.android.database.task.DocDefDBMExternal;
import com.guyu.android.gis.app.GisQueryApplication;
import com.guyu.android.gis.common.CallBack;
import com.guyu.android.gis.common.CaseImg;
import com.guyu.android.gis.common.DocDef;
import com.guyu.android.gis.opt.MapOperate;
import com.guyu.android.utils.CameraFilePathUtils;
import com.guyu.android.utils.FileUtil;
import com.guyu.android.utils.SysConfig;
import com.guyu.android.utils.UtilsTools;
import com.guyu.android.widget.selectimgs.ImgFileListActivity;
import com.guyu.android.R;

public class PhotoSelectActivity extends Activity {
	private Handler mHandler = null;
	private View.OnClickListener onClickListener = new MOnClickListener();
	private List<Boolean> mPhotoSelect = new ArrayList<Boolean>();
	private List<String> mPhotoPaths = new ArrayList<String>();
	private List<String> mPhotoNames = new ArrayList<String>();
	private List<String> mPhotoDocDefIds = new ArrayList<String>();
	private List<CaseImg> mAllCaseImgObjs = null;
	private String m_CurrentCamaraPhoto = "";
	private String m_CurrentCamaraPhotoName = "";
	private List<String> mList_docDef = new ArrayList<String>();
	private ListView mPhotoListView = null;
	private String mCaseId = null;
	private CaseImgDBMExternal mCaseImgManager = null;
	private String[] docDefIDS;
	private Map<String, DocDef> docDefMap;
	private Spinner docDefSpinner;
	private String bizId = "-1";
	private Button buttonUpdate;

	protected void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		setContentView(R.layout.activity_photo_select);


		this.mHandler = new MyHandler(Looper.myLooper());
		docDefSpinner = (Spinner) findViewById(R.id.sp_value);
		initData();
		findViewById(R.id.buttonConfirm).setOnClickListener(this.onClickListener);
		findViewById(R.id.buttonCancle).setOnClickListener(this.onClickListener);
		buttonUpdate = (Button) findViewById(R.id.buttonUpdate);
		buttonUpdate.setOnClickListener(this.onClickListener);
		findViewById(R.id.toCamera).setOnClickListener(this.onClickListener);
		findViewById(R.id.toSelectPhoto).setOnClickListener(this.onClickListener);
		this.mPhotoListView = ((ListView) findViewById(R.id.list));
		this.mPhotoListView.setAdapter(new PhotoSelectAdapter(this, this.mPhotoNames, this.mPhotoPaths, this.mPhotoDocDefIds, this.mPhotoSelect));
		this.mPhotoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> paramAdapterView, View paramView, int paramInt, long paramLong) {
				List<Boolean> localList = PhotoSelectActivity.this.mPhotoSelect;
				boolean bool = PhotoSelectActivity.this.mPhotoSelect.get(paramInt);
				localList.set(paramInt, !bool);
				PhotoSelectActivity.this.mPhotoListView.invalidateViews();
			}
		});
	}

	private void initData() {
		this.bizId = getIntent().getStringExtra("bizId");
		this.mCaseId = getIntent().getStringExtra("caseId");
		refreshDocDefInfo();
		this.mCaseImgManager = new CaseImgDBMExternal(this);
		if (this.mCaseId != null)
			this.mAllCaseImgObjs = this.mCaseImgManager.getTheCaseAllImg(this.mCaseId);
		if ((this.mAllCaseImgObjs != null) && (this.mAllCaseImgObjs.size() > 0)) {
			Iterator<CaseImg> localIterator = this.mAllCaseImgObjs.iterator();
			while (localIterator.hasNext()) {
				CaseImg localCaseImgObj = localIterator.next();
				if (!(FileUtil.IsExist(localCaseImgObj.getImgPath())))
					continue;
				this.mPhotoPaths.add(localCaseImgObj.getImgPath());
				this.mPhotoNames.add(localCaseImgObj.getImgName());
				this.mPhotoDocDefIds.add(localCaseImgObj.getDocDefId());
				this.mPhotoSelect.add(Boolean.valueOf(true));
			}
		}

	}

	private void refreshDocDefInfo() {
		// 查询要件定义类别
		List<DocDef> al_docDef = new DocDefDBMExternal(PhotoSelectActivity.this).queryDocDefInfoByBizId(bizId);
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
		ArrayAdapter localArrayAdapter = new ArrayAdapter(this, R.layout.simple_spinner_item, this.mList_docDef);
		docDefSpinner.setAdapter(localArrayAdapter);
	}

	private class MOnClickListener implements View.OnClickListener {
		public void onClick(View paramView) {
			switch (paramView.getId()) {
			default:
			case R.id.buttonConfirm: {
				int mPhotoSelectSize = PhotoSelectActivity.this.mPhotoSelect.size();
				ArrayList<String> localArrayList1 = new ArrayList<String>();
				ArrayList<String> localArrayList2 = new ArrayList<String>();
				ArrayList<String> localArrayList3 = new ArrayList<String>();
				for (int j = 0; j < mPhotoSelectSize; j++) {

					if (!PhotoSelectActivity.this.mPhotoSelect.get(j))
						continue;
					localArrayList1.add(PhotoSelectActivity.this.mPhotoPaths.get(j));
					localArrayList2.add(PhotoSelectActivity.this.mPhotoNames.get(j));
					localArrayList3.add(PhotoSelectActivity.this.mPhotoDocDefIds.get(j));
				}
				PhotoSelectActivity.this.returnResult(localArrayList1, localArrayList2, localArrayList3);
				break;
			}
			case R.id.buttonCancle:
				PhotoSelectActivity.this.finish();
				break;
			case R.id.buttonUpdate:
				buttonUpdate.setEnabled(false);
				Toast.makeText(getBaseContext(), "要件类别更新中...", Toast.LENGTH_LONG).show();
				UpdateBizDocDefThread updateThread = new UpdateBizDocDefThread();
				new Thread(updateThread).start();
				break;
			case R.id.toCamera:
				PhotoSelectActivity.this.onPhoto();
				break;
			case R.id.toSelectPhoto:
				PhotoSelectActivity.this.onSelectPhoto();
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
				ToastUtils.showLong( "要件类别更新成功！");
				break;
			}
			case 2222: {
				buttonUpdate.setEnabled(true);
				ToastUtils.showLong( "要件类别更新失败，请稍后重试！");
				break;
			}
			}
		}
	}

	class UpdateBizDocDefThread implements Runnable {
		public void run() {
			PhotoSelectActivity.this.updateBizDocDef(new CallBack() {
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

	private void refreshCaseImgObjs(String p_caseId, List<String> p_photoPaths, List<String> p_photoNames, List<String> p_photoDocDefIds) {
		if ((p_photoPaths != null) && (p_photoNames != null) && (p_photoPaths.size() == p_photoNames.size()))
			this.mAllCaseImgObjs.clear();
		for (int i = 0; i < p_photoNames.size(); i++) {
			this.mAllCaseImgObjs.add(new CaseImg(p_caseId, p_photoDocDefIds.get(i), p_photoNames.get(i), p_photoPaths.get(i), UtilsTools.GetCurrentTime()));
		}
	}

	private void returnResult(List<String> p_photoPaths, List<String> p_photoNames, List<String> p_photoDocDefIds) {
		int length = p_photoPaths.size();
		String[] arrayOfString = new String[length];
		for (int j = 0; j < length; j++) {
			if ((this.mCaseImgManager != null) && (this.mCaseId != null)) {
				this.mCaseImgManager.clearTheCaseImgInfoByCaseID(this.mCaseId);
				refreshCaseImgObjs(this.mCaseId, p_photoPaths, p_photoNames, p_photoDocDefIds);
				this.mCaseImgManager.insertMultiCaseImgsInfo(this.mAllCaseImgObjs);
				arrayOfString[j] = (p_photoPaths.get(j));

			}

		}
		Intent localIntent = new Intent();
		localIntent.putExtra("imgs", arrayOfString);
		setResult(-1, localIntent);
		finish();
		return;
	}

	/**
	 * 照相后回掉
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 393217) {
			if (resultCode == -1) {
				this.mPhotoPaths.add(PhotoSelectActivity.this.m_CurrentCamaraPhoto);
				this.mPhotoNames.add(PhotoSelectActivity.this.m_CurrentCamaraPhotoName);
				this.mPhotoDocDefIds.add(docDefIDS[docDefSpinner.getSelectedItemPosition()]);
				Log.i("拍照2", "路径=" + PhotoSelectActivity.this.m_CurrentCamaraPhoto);
				Log.i("拍照2", "名字=" + PhotoSelectActivity.this.m_CurrentCamaraPhotoName);
				try {
					String path =GisQueryApplication.getApp().getProjectPath() + "CameraPhoto/";
					String fileName=PhotoSelectActivity.this.m_CurrentCamaraPhotoName;
					createBitMap(path,fileName);


				} catch (IOException e) {
					e.printStackTrace();
				}
				this.mPhotoSelect.add(Boolean.valueOf(true));
				PhotoSelectActivity.this.m_CurrentCamaraPhoto = "";
				PhotoSelectActivity.this.m_CurrentCamaraPhotoName = "";
				PhotoSelectActivity.this.mPhotoListView.invalidateViews();
			}
		} else if (requestCode == 393218) {
			if (resultCode == -1) {
				Bundle bundle = data.getExtras();
				if (bundle != null) {
					if (bundle.getStringArrayList("files") != null) {
						ArrayList<String> listfile = bundle.getStringArrayList("files");
						for (String filepath : listfile) {
							this.mPhotoPaths.add(filepath);
							String fName = filepath.trim();
							String temp[] = fName.split("/");
							String fileName = temp[temp.length - 1];
							this.mPhotoNames.add(fileName);
							this.mPhotoDocDefIds.add(docDefIDS[docDefSpinner.getSelectedItemPosition()]);
							this.mPhotoSelect.add(Boolean.valueOf(true));
						}
					}
				}
				PhotoSelectActivity.this.m_CurrentCamaraPhoto = "";
				PhotoSelectActivity.this.m_CurrentCamaraPhotoName = "";
				PhotoSelectActivity.this.mPhotoListView.invalidateViews();
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	public void createBitMap(String dir,String filename) throws IOException{
	  //得到当前登录人的姓名
	   String humanName=SysConfig.mHumanInfo.getHumanName();
	   //得到当前时间
	   SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	   Date date=new Date();
	   String time=format.format(date);
	   //得到经纬度
	   String part=SysConfig.GPSInfo;
	   String str=humanName+"      "+time;
	   String fileName= dir+filename;
	   BitmapFactory.Options options = new BitmapFactory.Options();
	   options.inSampleSize = 2;//图片宽高都为原来的二分之一，即图片为原来的四分之一
	   Bitmap photo = BitmapFactory.decodeFile(fileName, options);
       int width = photo.getWidth(), hight = photo.getHeight();
       Bitmap icon = Bitmap.createBitmap(width, hight, Bitmap.Config.ARGB_8888); //建立一个空的BItMap  
       Canvas canvas = new Canvas(icon);//初始化画布绘制的图像到icon上  
        
       Paint photoPaint = new Paint(); //建立画笔  
       photoPaint.setDither(true); //获取跟清晰的图像采样  
       photoPaint.setFilterBitmap(true);//过滤一些  
        
       Rect src = new Rect(0, 0, photo.getWidth(), photo.getHeight());//创建一个指定的新矩形的坐标  
       Rect dst = new Rect(0, 0, width, hight);//创建一个指定的新矩形的坐标  
       canvas.drawBitmap(photo, src, dst, photoPaint);//将photo 缩放或则扩大到 dst使用的填充区photoPaint  
        
       Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DEV_KERN_TEXT_FLAG);//设置画笔  
       textPaint.setTextSize(30.0f);//字体大小  
       textPaint.setTypeface(Typeface.DEFAULT_BOLD);//采用默认的宽度  
       textPaint.setColor(Color.RED);//采用的颜色  
       canvas.drawText(str, width-450, hight-60, textPaint);//绘制上去字，开始未知x,y采用那只笔绘制 
       if(part!=null){
    	   canvas.drawText(part, width-445, hight-20, textPaint);
       }
       canvas.save(Canvas.ALL_SAVE_FLAG); 
       canvas.restore(); 
       saveMyBitmap(fileName,icon);
	}
	/**
	 * 保存加了水印的图片
	 * @param filePath
	 * @param b
	 * @throws IOException
	 */
    public void saveMyBitmap(String filePath,Bitmap b) throws IOException {
        File f = new File(filePath);
        f.createNewFile();
        FileOutputStream fOut = null;
        try {
                fOut = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
                e.printStackTrace();
        }
        b.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
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
   /**
    * 拍照 
    */
	private void onPhoto() {
		if (Environment.getExternalStorageState().equals("mounted")) {
			String str1 = GisQueryApplication.getApp().getProjectPath() + "CameraPhoto/";
			String str2 = UtilsTools.GetCurrentTime() + ".jpg";
			File localFile = new File(str1);
			if (!(localFile.exists()))
				localFile.mkdirs();
			PhotoSelectActivity.this.m_CurrentCamaraPhoto = str1 + str2;
			PhotoSelectActivity.this.m_CurrentCamaraPhotoName = str2;
			Log.i("拍照1", "路径=" + PhotoSelectActivity.this.m_CurrentCamaraPhoto);
			Log.i("拍照1", "名字=" + PhotoSelectActivity.this.m_CurrentCamaraPhotoName);
//			Uri localUri = Uri.fromFile(new File(str1, str2));
//			Intent localIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//			localIntent.putExtra("output", localUri);
//			localIntent.putExtra("android.intent.extra.videoQuality", 0);
//			PhotoSelectActivity.this.startActivityForResult(localIntent, 393217);

			Intent mOpenCameraIntent = new Intent();
			mOpenCameraIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);


			Uri desUri = CameraFilePathUtils.getFileUri(this,m_CurrentCamaraPhoto);

			if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
				//已申请camera权限
				//mOpenCameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
			}

			mOpenCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,desUri);
			startActivityForResult(mOpenCameraIntent,CameraFilePathUtils.getCameraPermissionRequestCode());
		} else {
			Toast.makeText(this, "无SD卡", Toast.LENGTH_LONG).show();
		}

	}

	private void onSelectPhoto() {
		Intent intent = new Intent();
		intent.setClass(this, ImgFileListActivity.class);
		PhotoSelectActivity.this.startActivityForResult(intent, 393218);
	}

	public class PhotoSelectAdapter extends BaseAdapter {
		private LayoutInflater mInflater = null;
		private List<String> mPhotoNames = null;
		private List<String> mPhotoPaths = null;
		private List<String> mPhotoDocDefIds = null;
		private List<Boolean> mSelectList = null;

		public PhotoSelectAdapter(Activity activity, List<String> photoNameList, List<String> photoPathList, List<String> photoDocDefIds, List<Boolean> selectList) {
			this.mInflater = ((LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE));
			this.mPhotoNames = photoNameList;
			this.mPhotoPaths = photoPathList;
			this.mPhotoDocDefIds = photoDocDefIds;
			this.mSelectList = selectList;
		}

		public int getCount() {
			return this.mPhotoNames.size();
		}

		public Object getItem(int paramInt) {
			return this.mPhotoNames.get(paramInt);
		}

		public long getItemId(int paramInt) {
			return paramInt;
		}

		public View getView(int paramInt, View paramView, ViewGroup paramViewGroup) {
			if (paramInt < this.mPhotoNames.size()) {
				View localView = this.mInflater.inflate(R.layout.listview_photo_row, paramViewGroup, false);
				ImageView localImageView = (ImageView) localView.findViewById(R.id.icon);
				BitmapFactory.Options localOptions = new BitmapFactory.Options();
				localOptions.inJustDecodeBounds = true;
				BitmapFactory.decodeFile((String) this.mPhotoPaths.get(paramInt), localOptions);
				localOptions.inJustDecodeBounds = false;
				int i = (int) (localOptions.outHeight / 60.0F);
				if (i <= 0)
					i = 1;
				localOptions.inSampleSize = i;
				localImageView.setImageBitmap(BitmapFactory.decodeFile((String) this.mPhotoPaths.get(paramInt), localOptions));
				((TextView) localView.findViewById(R.id.fileName)).setText((CharSequence) this.mPhotoNames.get(paramInt));
				((TextView) localView.findViewById(R.id.docDefName)).setText(docDefMap.get(this.mPhotoDocDefIds.get(paramInt)).getDocDefName());
				if (paramInt < this.mSelectList.size())
					((CheckBox) localView.findViewById(R.id.checkBox01)).setChecked(this.mSelectList.get(paramInt).booleanValue());
				return localView;
			}
			return null;
		}
	}

}