package com.guyu.android.gis.activity;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import org.json.JSONObject;

import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.esri.core.geometry.Point;
import com.guyu.android.database.task.CaseDBMExternal;
import com.guyu.android.database.task.CaseImgDBMExternal;
import com.guyu.android.database.task.CaseSoundDBMExternal;
import com.guyu.android.database.task.CaseVideoDBMExternal;
import com.guyu.android.database.task.TrackDBMExternal;
import com.guyu.android.gis.app.GisQueryApplication;
import com.guyu.android.gis.common.CaseImg;
import com.guyu.android.gis.common.CaseSound;
import com.guyu.android.gis.common.CaseVideo;
import com.guyu.android.gis.common.Field;
import com.guyu.android.gis.common.TrackObj;
import com.guyu.android.gis.config.CaseUploadsConfig;
import com.guyu.android.gis.opt.MapOperate;
import com.guyu.android.utils.DebugUtils;
import com.guyu.android.utils.MathUtils;
import com.guyu.android.utils.SecureUtil;
import com.guyu.android.utils.SysConfig;
import com.guyu.android.utils.UtilsTools;
import com.guyu.android.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;


public class TrackDetailsActivity extends Activity implements View.OnClickListener {

    public static String DATA_TRACK_OBJ = "TrackObj";
    private TrackObj mTrackObj = null;
    private Button mTrackUpButton = null;

    private EditText mXcddEditText = null;
    private EditText mXclxEditText = null;
    private EditText mXcqkEditText = null;
    private EditText mXcfkEditText = null;
    private EditText mBmfzrEditText = null;
    private TextView spinner ;
    private static long lastClickTime;

    private ProgressDialog dialog = null;
    private Handler mHandler = null;

    private List<TrackObj> mAllSelectTrackObjs = new ArrayList<TrackObj>();


    private RecyclerView rv = null;
    private List<List<String>> data = null;
    private MyAdapter adapter = null;
    private TrackObj localTrackObj;

    private TextView mPhotoTextView = null;
    private TextView mSoundTextView = null;
    private TextView mVideoTextView = null;

    private ImageButton photoButton;
    private ImageButton imageButton;
    private ImageButton videoButton;

    private List<String> mImgList = new ArrayList<String>();
    private List<String> mSoundList = new ArrayList<String>();
    private List<String> mVideoList = new ArrayList<String>();



    @Override
    public void onClick(View view) {
        Intent localIntent;
        switch (view.getId()) {
            case R.id.btn_case_imgs:
                localIntent = new Intent(this, ImgViewActivity.class);
                localIntent.putExtra("caseId", mTrackObj.getTrackId());
                startActivity(localIntent);
                break;
            case R.id.btn_case_sounds:
                localIntent = new Intent(this, SoundViewActivity.class);
                localIntent.putExtra("caseId", mTrackObj.getTrackId());
                startActivity(localIntent);
                break;
            case R.id.btn_case_videos:
                localIntent = new Intent(this, VideoViewActivity.class);
                localIntent.putExtra("caseId", mTrackObj.getTrackId());
                startActivity(localIntent);
                break;
            case R.id.btn_uploadOffline_track:
                if (isFastDoubleClick()) {
                    return;
                } else {
                    mTrackUpButton.setEnabled(false);
                    onPreExecute();
                    CaseImgDBMExternal mLandImgManager = new CaseImgDBMExternal(TrackDetailsActivity.this);
                    CaseSoundDBMExternal mLandSoundManager = new CaseSoundDBMExternal(TrackDetailsActivity.this);
                    CaseVideoDBMExternal mLandVideoManager = new CaseVideoDBMExternal(TrackDetailsActivity.this);
                    al_landImgObj = mLandImgManager.getTheCaseAllImg(mTrackObj.getTrackId());
                    al_landSoundObj = mLandSoundManager.getTheCaseAllSound(mTrackObj.getTrackId());
                    al_landVideoObj = mLandVideoManager.getTheCaseAllVideo(mTrackObj.getTrackId());
                    UploadThread uploadThread = new UploadThread();
                    new Thread(uploadThread).start();
                }
                break;
            case R.id.videoButton:
                TrackDetailsActivity.this.fileSelectOpt_video("2466");
                break;
            case R.id.imageButton:
                TrackDetailsActivity.this.fileSelectOpt_sound("2466");
                break;
            case R.id.photoButton:
                TrackDetailsActivity.this.fileSelectOpt_photo("2466");
                break;

        }

    }

    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(R.layout.activity_track_details);

        initActivity();
        rv.setLayoutManager(new LinearLayoutManager(this));
        initData();
        this.mHandler = new MyHandler(Looper.myLooper());
    }

    List<CaseImg> al_landImgObj;
    List<CaseSound> al_landSoundObj;
    List<CaseVideo> al_landVideoObj;

    private void initData() {
        data = new ArrayList();
        getData();


        initVideo();
        initSound();
        initPhoto();

        List<Point> points = mTrackObj.getAllTrackPoints();
        List<MyEntity> entities = new ArrayList<>();
        for (int i = 0; i < points.size(); i++) {
            Point p = points.get(i);
            List<String> l = new ArrayList();
            l.add(MathUtils.round(p.getX(), 3));
            l.add(MathUtils.round(p.getY(), 3));
            data.add(l);
            entities.add(new MyEntity(MyEntity.TYPE));
        }

        adapter = new MyAdapter(entities);
        adapter.openLoadAnimation();
        rv.setAdapter(adapter);
    }

    public void getData() {
        CaseImgDBMExternal mLandImgManager = new CaseImgDBMExternal(this);
        CaseSoundDBMExternal mLandSoundManager = new CaseSoundDBMExternal(this);
        CaseVideoDBMExternal mLandVideoManager = new CaseVideoDBMExternal(this);
        al_landImgObj = mLandImgManager.getTheCaseAllImg(mTrackObj.getTrackId());
        al_landSoundObj = mLandSoundManager.getTheCaseAllSound(mTrackObj.getTrackId());
        al_landVideoObj = mLandVideoManager.getTheCaseAllVideo(mTrackObj.getTrackId());

        this.mPhotoTextView.setText("已选择" + al_landImgObj.size() + "张图片");
        this.mSoundTextView.setText("已选择" + al_landSoundObj.size() + "个录音");
        this.mVideoTextView.setText("已选择" + al_landVideoObj.size() + "个录像");
    }

    private class MyEntity implements MultiItemEntity {
        public final static int TYPE = 0;


        private int type;

        public MyEntity(final int type) {
            this.type = type;
        }

        @Override
        public int getItemType() {

            return type;
        }
    }

    private class MyAdapter extends BaseMultiItemQuickAdapter<MyEntity, BaseViewHolder> {

        public MyAdapter(List<MyEntity> entities) {
            super(entities);
            addItemType(MyEntity.TYPE, R.layout.item_track_details);
        }

        @Override
        protected void convert(BaseViewHolder helper, MyEntity item) {
            int p = helper.getAdapterPosition();
            helper.setText(R.id.txt_x, data.get(p).get(0));
            helper.setText(R.id.txt_y, data.get(p).get(1));
        }
    }

    private void initActivity() {

        findViewById(R.id.btn_case_imgs).setOnClickListener(this);
        findViewById(R.id.btn_case_sounds).setOnClickListener(this);
        findViewById(R.id.btn_case_videos).setOnClickListener(this);


        mVideoTextView = (TextView) findViewById(R.id.mVideoTextView);
        mSoundTextView = (TextView) findViewById(R.id.mSoundTextView);
        mPhotoTextView = (TextView) findViewById(R.id.mPhotoTextView);

        localTrackObj = (TrackObj) getIntent().getSerializableExtra(
                DATA_TRACK_OBJ);
        mTrackUpButton = ((Button) findViewById(R.id.btn_uploadOffline_track));
        if (localTrackObj != null) {
            this.mTrackObj = localTrackObj;
            initWidget();
        }


        if (this.mTrackObj != null) {
            if (!("".equals(this.mTrackObj.getTrackName()))) {
                setTilte("巡查轨迹详情(" + this.mTrackObj.getTrackName() + ")");

            } else {
                setTilte("巡查轨迹详情(" + this.mTrackObj.getTrackId() + ")");
            }
        } else {
            setTilte("巡查轨迹详情");
        }
        if (localTrackObj.isUp()) {
			/*((Button) findViewById(R.id.btn_uploadOffline_track))
					.setVisibility(View.INVISIBLE);*/
            mTrackUpButton.setEnabled(false);
            mTrackUpButton.setText("已上传");
        } else {
            ((Button) findViewById(R.id.btn_uploadOffline_track))
                    .setVisibility(View.VISIBLE);

            mTrackUpButton.setOnClickListener(this);
        }
//		this.layoutInflater = LayoutInflater.from(this);
//		LinearLayout localLinearLayout = (LinearLayout) this.layoutInflater
//				.inflate(R.layout.record_field_style, null).findViewById(R.id.record_item);

//		"MPHOTO"
//		initMphoto("2466", 3, 0, 0, localLinearLayout);
//		initMphoto(bizID, i, localField, dataType, localLinearLayout);
//		"MSOUND"
//		initMsound("2466", 4, 0, 0, localLinearLayout);
//		initMsound(bizID, i, localField, dataType, localLinearLayout);
//		"MVIDEO"
//		initMvideo("2466", 5, 0, 0, localLinearLayout);
//		initMvideo(bizID, i, localField, dataType, localLinearLayout);


    }

    private void initWidget() {

        this.mXcddEditText = ((EditText) findViewById(R.id.et_xcdd_track_id));
        this.mXclxEditText = ((EditText) findViewById(R.id.et_xclx_track_id));
        this.mXcqkEditText = ((EditText) findViewById(R.id.et_xcqk_track_id));
        this.mXcfkEditText = ((EditText) findViewById(R.id.et_xxfk_track_id));

        this.mBmfzrEditText = ((EditText) findViewById(R.id.et_bmfzr_track_id));
        spinner = (TextView) findViewById(R.id.spinner);



        String[] data_list = this.getResources().getStringArray(R.array.business_type);


        spinner.setText(data_list[mTrackObj.getFirm()]);

        rv = (RecyclerView) findViewById(R.id.rv);


        this.mXcddEditText.setText(this.mTrackObj.getXCDD());
        this.mXclxEditText.setText(this.mTrackObj.getXCLX());
        this.mXcqkEditText.setText(this.mTrackObj.getXCQK());
        this.mXcfkEditText.setText(this.mTrackObj.getXXFKJYJXCQQK());
        this.mBmfzrEditText.setText(this.mTrackObj.getBMFZR());


        this.mXcddEditText.setEnabled(false);
        this.mXclxEditText.setEnabled(false);
        this.mXcqkEditText.setEnabled(false);
        this.mXcfkEditText.setEnabled(false);
        this.mBmfzrEditText.setEnabled(false);

    }

    class UploadThread implements Runnable {
        public void run() {


            String filePath = GisQueryApplication.getApp()
                    .getProjectPath() + "Track/";
            String fileName = TrackDetailsActivity.this.mTrackObj.getTrackName();
            String srcPath = filePath + fileName + ".gpx";

            String form = "";
            boolean isUploadSucess = true;

            if (al_landImgObj != null && al_landImgObj.size() > 0) {
                String key = null;
                for (int i = 0; i < al_landImgObj.size(); i++) {
                    form = "";
//                    try {
//                        key = SecureUtil.encodeBase64(SecureUtil.encryptOfRSA(SecureUtil.getRandomAESKey()));
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
                    CaseImg caseImg = al_landImgObj.get(i);
                    form += "&partId=" + caseImg.getPartId() + "&caseId=" + caseImg.getCaseId() + "&docDefId=" + caseImg.getDocDefId() + "&imgName=" + caseImg.getImgName() + "&imgPath=" + caseImg.getImgPath() + "&time=" + String.valueOf(caseImg.getTime()) + "&prjName=" + caseImg.getPrjName();
                    isUploadSucess = uploadFile2(caseImg.getImgPath(), form, key);
                }
            }

            if (al_landSoundObj != null && al_landSoundObj.size() > 0) {
                String key = null;
                for (int i = 0; i < al_landSoundObj.size(); i++) {
                    form = "";
//                    try {
//                        key = SecureUtil.encodeBase64(SecureUtil.encryptOfRSA(SecureUtil.getRandomAESKey()));
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
                    CaseSound caseSound = al_landSoundObj.get(i);
                    form += "&partId=" + caseSound.getPartId() + "&caseId=" + caseSound.getCaseId() + "&docDefId=" + caseSound.getDocDefId() + "&soundName=" + caseSound.getSoundName() + "&soundPath=" + caseSound.getSoundPath() + "&time=" + String.valueOf(caseSound.getTime()) + "&prjName=" + caseSound.getPrjName();
                    isUploadSucess = uploadFile2(caseSound.getSoundPath(), form, key);
                }
            }

            if (al_landVideoObj != null && al_landVideoObj.size() > 0) {
                String key = null;
                for (int i = 0; i < al_landVideoObj.size(); i++) {
                    form = "";
//                    try {
//                        key = SecureUtil.encodeBase64(SecureUtil.encryptOfRSA(SecureUtil.getRandomAESKey()));
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
                    CaseVideo caseVideo = al_landVideoObj.get(i);
                    form += "&partId=" + caseVideo.getPartId() + "&caseId=" + caseVideo.getCaseId() + "&docDefId=" + caseVideo.getDocDefId() + "&videoName=" + caseVideo.getVideoName() + "&videoPath=" + caseVideo.getVideoPath() + "&time=" + String.valueOf(caseVideo.getTime()) + "&prjName=" + caseVideo.getPrjName();
                    isUploadSucess = uploadFile2(caseVideo.getVideoPath(), form, key);
                }
            }


            if (isUploadSucess) {
                uploadFile(srcPath);//上传离线文件
            } else {
                mHandler.sendEmptyMessage(17001);
            }

        }
    }


    private String EncodedString(String str) {

        String s = SecureUtil.encryptString(str);
        return SecureUtil.toURLEncoded(s);
    }

    private boolean uploadFile2(String srcPath, String form, String key) {
        boolean isUploadSucess = false;
        //上传文件
        int userId = SysConfig.mHumanInfo.getHumanId();
        String cur_userName = SysConfig.mHumanInfo.getHumanName();
        String userName = cur_userName;
        try {
            userName = URLEncoder.encode(cur_userName, "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }

        try {
            System.out.println("key----" + key);
            String uploadUrl = GisQueryApplication.getApp().getProjectconfig().getUploadtrackurl()
//                    + "?partId=" + userId
                    + "?partName=" + userName
                    + "&key=" + "" + form;
            System.out.println("uploadUrl----" + uploadUrl);

            String end = "\r\n";
            String twoHyphens = "--";
            String boundary = "******";
            URL url = new URL(uploadUrl);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url
                    .openConnection();
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setUseCaches(false);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
            httpURLConnection.setRequestProperty("Charset", "UTF-8");
            httpURLConnection.setRequestProperty("Content-Type",
                    "multipart/form-data;boundary=" + boundary);
            httpURLConnection.connect();
            DataOutputStream dos = new DataOutputStream(
                    httpURLConnection.getOutputStream());
            dos.writeBytes(twoHyphens + boundary + end);
            dos.writeBytes("Content-Disposition: form-data; name=\"file\"; filename=\""
                    + srcPath.substring(srcPath.lastIndexOf("/") + 1)
                    + "\""
                    + end);
            dos.writeBytes(end);

            FileInputStream fis = new FileInputStream(srcPath);
            byte[] buffer = new byte[2048]; // 2k


            while (fis.read(buffer) != -1) {
//                dos.write(SecureUtil.encryptData(buffer));
                dos.write(buffer);
            }
            fis.close();

            dos.writeBytes(end);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + end);
            dos.flush();

            InputStream is = httpURLConnection.getInputStream();
            String resultJson = dealResponseResult(is);
            DebugUtils.d(this.getLocalClassName() + "resultJson: ", resultJson);
            JSONObject jsonObject = new JSONObject(resultJson);
            String resultMessage = jsonObject.getString("message");
            int resultCode = jsonObject.getInt("code");
            System.out.println("resultJson--------" + resultJson
                    + "resultMessage------" + resultMessage);
            if (resultCode == 0) {
                isUploadSucess = true;
            }
            dos.close();
            is.close();

        } catch (Exception e) {
            e.printStackTrace();

        }
        return isUploadSucess;
    }


    private void uploadFile(String srcPath) {
        int userId = SysConfig.mHumanInfo.getHumanId();
        String cur_userName = SysConfig.mHumanInfo.getHumanName();
        String userName = cur_userName;
        try {
            userName = URLEncoder.encode(cur_userName, "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }

        String uploadUrl = GisQueryApplication.getApp().getProjectconfig().getTrackUploadOfflineUrl()
                + "?trackId=" + mTrackObj.getTrackId() + "&humanId=" + mTrackObj.getHumanID() +
                "&taskId=" + mTrackObj.getTaskId() + "&trackName=" + mTrackObj.getTrackName() +
                "&trackPath=" + mTrackObj.getTrackPath() + "&remark=" + mTrackObj.getRemark() +
                "&XCDD=" + mTrackObj.getXCDD() + "&XCLX=" + mTrackObj.getXCLX() + "&XCQK=" + mTrackObj.getXCQK() +
                "&XXFKJYJXCQQK=" + mTrackObj.getXXFKJYJXCQQK() + "&BMFZR=" + mTrackObj.getBMFZR() + "&isUp=" + 1 + "&time=" + mTrackObj.getTime()+"&firm="+mTrackObj.getFirm()+"&DJQ="+mTrackObj.getDJQ()+"&DJZQ="+mTrackObj.getDJZQ();
        //	String uploadUrl = "http://192.168.0.110:8080/iSystem/map/trackOffline.htm";
        System.out.println("巡查轨迹上传路径是：----" + uploadUrl);
        String end = "\r\n";
        String twoHyphens = "--";
        String boundary = "******";
        try {
            URL url = new URL(uploadUrl);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url
                    .openConnection();
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setUseCaches(false);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
            httpURLConnection.setRequestProperty("Charset", "UTF-8");
            httpURLConnection.setRequestProperty("Content-Type",
                    "multipart/form-data;boundary=" + boundary);
            httpURLConnection.connect();
            DataOutputStream dos = new DataOutputStream(
                    httpURLConnection.getOutputStream());
            dos.writeBytes(twoHyphens + boundary + end);
            dos.writeBytes("Content-Disposition: form-data; name=\"file\"; filename=\""
                    + srcPath.substring(srcPath.lastIndexOf("/") + 1)
                    + "\""
                    + end);
            dos.writeBytes(end);

            FileInputStream fis = new FileInputStream(srcPath);
            byte[] buffer = new byte[2048]; // 2k
            int count = 0;
            while ((count = fis.read(buffer)) != -1) {
                dos.write(buffer, 0, count);
            }
            fis.close();

            dos.writeBytes(end);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + end);
            dos.flush();

            InputStream is = httpURLConnection.getInputStream();
            String resultJson = dealResponseResult(is);
            JSONObject jsonObject = new JSONObject(resultJson);
            String resultMessage = jsonObject.getString("message");
            int resultCode = jsonObject.getInt("code");
            System.out.println("resultJson--------" + resultJson
                    + "resultMessage------" + resultMessage);
            if (resultCode == 0) {
                mHandler.sendEmptyMessage(18001);
            } else {
                mHandler.sendEmptyMessage(16001);
            }
            dos.close();
            is.close();

        } catch (Exception e) {
            mHandler.sendEmptyMessage(17001);
            e.printStackTrace();
            setTitle(e.getMessage());
        }
    }

    /**
     * 处理服务器的响应结果（将输入流转化成字符串）
     *
     * @param inputStream 服务器的响应输入流
     * @return
     */
    public static String dealResponseResult(InputStream inputStream) {
        String resultData = null; // 存储处理结果
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int len = 0;
        try {
            while ((len = inputStream.read(data)) != -1) {
                byteArrayOutputStream.write(data, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        resultData = new String(byteArrayOutputStream.toByteArray());
        return resultData;
    }

    private class MyHandler extends Handler {

        public MyHandler(Looper paramLooper) {
            super(paramLooper);
        }

        public void handleMessage(Message paramMessage) {
            switch (paramMessage.what) {
                case 16001:
                    showFailureMsg();
                    break;
                case 17001:
                    showFailureMessage();
                    break;
                case 18001:
                    showSucessMessage();
            }
        }
    }

    private void showSucessMessage() {
        this.dialog.dismiss();
        new TrackDBMExternal(TrackDetailsActivity.this).updateTrackUpState(
                mTrackObj.getTrackId(), 1);
        ToastUtils.showLong("巡查轨迹上传成功！");
        //mTrackObj.setUpState(1);
        mTrackUpButton.setEnabled(false);
    }

    private void showFailureMsg() {
        this.dialog.dismiss();
        ToastUtils.showLong("上传失败！");
        mTrackUpButton.setEnabled(true);
        return;
    }

    private void showFailureMessage() {
        this.dialog.dismiss();
        ToastUtils.showLong("当前网络不可用，请稍后再试！");
        mTrackUpButton.setEnabled(true);
        return;
    }

    protected void onPreExecute() {
        this.dialog = new ProgressDialog(TrackDetailsActivity.this);
        this.dialog.setMessage("上传中...");
        this.dialog.setCancelable(false);
        this.dialog.show();
    }

    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (0 < timeD && timeD < 2000) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

    private void setTilte(String paramString) {
        ((TextView) findViewById(R.id.tv_title_track)).setText(paramString);
    }

    public void trackLocationOnClick(View paramView) {// 地图查看
        this.mAllSelectTrackObjs.add((mTrackObj));
        TrackDetailsActivity.this.mapViewOpt();
        //finish();
    }

    /**
     * 地图上查看该对象的轨迹
     */
    public void mapViewOpt() {
        MapOperate.mapShowTrackPaths(mAllSelectTrackObjs);
        Intent localIntent = new Intent(
                TrackDetailsActivity.this, MainActivity.class);
        localIntent
                .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        localIntent.putExtra("opt", 4372);
        this.startActivity(localIntent);
    }

    public void backOnClickTrack(View paramView) {//返回
        Intent localIntent = new Intent(
                TrackDetailsActivity.this, TrackManagerActivity.class);
        localIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(localIntent);
        finish();
    }


    private void fileSelectOpt_video(String bizID) {
        Intent localIntent = new Intent(this, VideoSelectActivity.class);
        localIntent.putExtra("bizId", bizID);
        localIntent.putExtra("caseId", mTrackObj.getTrackId());
        startActivityForResult(localIntent, 3235);
    }

    private void fileSelectOpt_sound(String bizID) {
        Intent localIntent = new Intent(this, SoundSelectActivity.class);
        localIntent.putExtra("bizId", bizID);
        localIntent.putExtra("caseId", mTrackObj.getTrackId());
        startActivityForResult(localIntent, 3234);
    }

    private void fileSelectOpt_photo(String bizID) {
        Intent localIntent = new Intent(this, PhotoSelectActivity.class);
        localIntent.putExtra("bizId", bizID);
        localIntent.putExtra("caseId", mTrackObj.getTrackId());
        startActivityForResult(localIntent, 3233);
    }

    private void initVideo() {

        videoButton = (ImageButton) findViewById(R.id.videoButton);
        mVideoTextView.addTextChangedListener(new TrackDetailsActivity.TextChange(39323));
        if (localTrackObj.isUp() == false) {
            videoButton.setOnClickListener(this);
        }
    }


    private void initSound() {

        imageButton = (ImageButton) findViewById(R.id.imageButton);
        mSoundTextView.addTextChangedListener(new TrackDetailsActivity.TextChange(39322));
        if (localTrackObj.isUp() == false) {
            imageButton.setOnClickListener(this);
        }
    }

    private void initPhoto() {

        photoButton = (ImageButton) findViewById(R.id.photoButton);
        mPhotoTextView.addTextChangedListener(new TrackDetailsActivity.TextChange(39321));
        if (localTrackObj.isUp() == false) {
            photoButton.setOnClickListener(this);
        }
    }


    private class TextChange implements TextWatcher {

        private int nType = 0;

        public TextChange(int fieldType) {

            this.nType = fieldType;
        }

        public void afterTextChanged(Editable paramEditable) {
//			if (this.nType == 1) {
//				TrackDetailsActivity.this.m_FieldValueList.set(this.nIndex, paramEditable.toString().trim());
//				return;
//			}
            // 处理选择图片
            if (this.nType == 39321) {
                if (TrackDetailsActivity.this.mImgList != null) {
                    String str1 = "";
                    Iterator<String> localIterator = TrackDetailsActivity.this.mImgList.iterator();
                    while (localIterator.hasNext()) {
                        String str2 = localIterator.next();
                        str1 = str1 + str2 + ",";
                    }
                    if(str1.length()>1){
                        str1 = str1.substring(0, -1 + str1.length());
                    }
//					TrackDetailsActivity.this.m_FieldValueList.set(this.nIndex, str1);

                }
            } // 处理选择录音
            else if (this.nType == 39322) {
                if (TrackDetailsActivity.this.mSoundList != null) {
                    String str1 = "";
                    Iterator<String> localIterator = TrackDetailsActivity.this.mSoundList.iterator();
                    while (localIterator.hasNext()) {
                        String str2 = localIterator.next();
                        str1 = str1 + str2 + ",";
                    }
                    if(str1.length()>1){
                        str1 = str1.substring(0, -1 + str1.length());
                    }

//					TrackDetailsActivity.this.m_FieldValueList.set(this.nIndex, str1);
                }
            }
            // 处理选择录像
            else if (this.nType == 39323) {
                if (TrackDetailsActivity.this.mVideoList != null) {
                    String str1 = "";
                    Iterator<String> localIterator = TrackDetailsActivity.this.mVideoList.iterator();
                    while (localIterator.hasNext()) {
                        String str2 = localIterator.next();
                        str1 = str1 + str2 + ",";
                    }
                    if(str1.length()>1){
                        str1 = str1.substring(0, -1 + str1.length());
                    }
//					TrackDetailsActivity.this.m_FieldValueList.set(this.nIndex, str1);
                }
            }

        }

        public void beforeTextChanged(CharSequence paramCharSequence, int paramInt1, int paramInt2, int paramInt3) {
        }

        public void onTextChanged(CharSequence paramCharSequence, int paramInt1, int paramInt2, int paramInt3) {
        }
    }

    /**
     * 选择图片后回掉
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {

            case 3233:
                if (resultCode == -1) {
                    String[] arrayOfString = data.getStringArrayExtra("imgs");
                    this.mImgList.clear();
                    for (int j = 0; j < arrayOfString.length; j++) {

                        String str = arrayOfString[j];
                        this.mImgList.add(str);
                    }
                    this.mPhotoTextView.setText("已选择" + mImgList.size() + "张图片");

                }

                break;
            case 3234:
                if (resultCode == -1) {
                    String[] arrayOfString = data.getStringArrayExtra("sounds");
                    this.mSoundList.clear();
                    for (int j = 0; j < arrayOfString.length; j++) {

                        String str = arrayOfString[j];
                        this.mSoundList.add(str);
                    }
                    this.mSoundTextView.setText("已选择" + mSoundList.size() + "个录音");

                }

                break;
            case 3235:
                if (resultCode == -1) {
                    String[] arrayOfString = data.getStringArrayExtra("videos");
                    this.mVideoList.clear();
                    for (int j = 0; j < arrayOfString.length; j++) {

                        String str = arrayOfString[j];
                        this.mVideoList.add(str);
                    }
                    this.mVideoTextView.setText("已选择" + mVideoList.size() + "个录像");
                }
                break;

        }


        super.onActivityResult(requestCode, resultCode, data);

    }

}