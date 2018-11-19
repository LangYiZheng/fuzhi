package com.guyu.android.gis.activity;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.introspect.AnnotatedClass;
import org.codehaus.jackson.map.introspect.JacksonAnnotationIntrospector;
import org.codehaus.jackson.map.ser.FilterProvider;
import org.codehaus.jackson.map.ser.impl.SimpleBeanPropertyFilter;
import org.codehaus.jackson.map.ser.impl.SimpleFilterProvider;
import org.json.JSONObject;

import com.blankj.utilcode.util.ToastUtils;
import com.guyu.android.database.task.CaseDBMExternal;
import com.guyu.android.database.task.CaseImgDBMExternal;
import com.guyu.android.database.task.CaseSoundDBMExternal;
import com.guyu.android.database.task.CaseVideoDBMExternal;
import com.guyu.android.database.task.TaskDBMExternal;
import com.guyu.android.gis.app.GisQueryApplication;
import com.guyu.android.gis.common.Case;
import com.guyu.android.gis.common.CaseImg;
import com.guyu.android.gis.common.CaseSound;
import com.guyu.android.gis.common.CaseVideo;
import com.guyu.android.gis.common.IllegalChange;
import com.guyu.android.gis.common.IllegalClues;
import com.guyu.android.gis.common.IllegalDongWu;
import com.guyu.android.gis.common.IllegalLand;
import com.guyu.android.gis.common.IllegalLinMu;
import com.guyu.android.gis.common.IllegalLinXia;
import com.guyu.android.gis.common.IllegalMuCai;
import com.guyu.android.gis.common.IllegalWeiFaOne;
import com.guyu.android.gis.common.Task;
import com.guyu.android.gis.config.CaseUploadsConfig;
import com.guyu.android.gis.opt.MapOperate;
import com.guyu.android.utils.FileUtil;
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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

public class SubmitCaseActivity extends Activity {
    public static String DATA_CASE_OBJ = "caseObj";
    private CheckBox mIandInfoBox = null;
    private CheckBox mPicBox = null;
    private CheckBox mSoundBox = null;
    private CheckBox mVideoBox = null;
    private Case mUploadCaseObj = null;
    private Button mUploadButton = null;
    private TextView mTVPic = null;
    private TextView mTVSound = null;
    private TextView mTVVideo = null;

    private ProgressDialog dialog = null;
    private static long lastClickTime;
    private Handler mHandler = null;

    private List<CaseImg> al_caseimg = null;
    private List<CaseSound> al_casesound = null;
    private List<CaseVideo> al_casevideo = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(1);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_case_submit);

        this.mHandler = new MyHandler(Looper.myLooper());
        initActivity();
    }

    private void initActivity() {
        Case locaCaseObj = (Case) getIntent().getSerializableExtra(
                DATA_CASE_OBJ);
        if (locaCaseObj != null) {
            this.mUploadCaseObj = locaCaseObj;
        }
        this.mIandInfoBox = ((CheckBox) findViewById(R.id.cb_landinfo));
        this.mPicBox = ((CheckBox) findViewById(R.id.cb_pic));
        this.mSoundBox = ((CheckBox) findViewById(R.id.cb_sound));
        this.mVideoBox = ((CheckBox) findViewById(R.id.cb_video));

        this.mTVPic = ((TextView) findViewById(R.id.tv_pic));
        this.mTVSound = ((TextView) findViewById(R.id.tv_sound));
        this.mTVVideo = ((TextView) findViewById(R.id.tv_video));

        this.mTVPic.setEnabled(false);
        this.mTVSound.setEnabled(false);
        this.mTVVideo.setEnabled(false);

        this.mIandInfoBox.setEnabled(false);// 默认勾选地块信息、图片信息，即地块信息、图片信息是必须上传的
        this.mIandInfoBox.setChecked(true);

        this.mUploadButton = ((Button) findViewById(R.id.select_land_upload));//上传按钮
        if (mUploadCaseObj != null) {
            al_caseimg = new CaseImgDBMExternal(SubmitCaseActivity.this)
                    .getTheCaseAllImg(mUploadCaseObj.getCaseId());
            al_casesound = new CaseSoundDBMExternal(SubmitCaseActivity.this)
                    .getTheCaseAllSound(mUploadCaseObj.getCaseId());
            al_casevideo = new CaseVideoDBMExternal(SubmitCaseActivity.this)
                    .getTheCaseAllVideo(mUploadCaseObj.getCaseId());

            if (al_caseimg == null || al_caseimg.size() == 0) {
                this.mPicBox.setVisibility(View.GONE);
                this.mTVPic.setVisibility(View.VISIBLE);
            } else {
                this.mPicBox.setVisibility(View.VISIBLE);
                this.mTVPic.setVisibility(View.GONE);
                this.mPicBox.setChecked(true);
                this.mPicBox.setVisibility(View.VISIBLE);
            }

            if (al_casesound == null || al_casesound.size() == 0) {
                this.mSoundBox.setVisibility(View.GONE);
                this.mTVSound.setVisibility(View.VISIBLE);
            } else {
                this.mSoundBox.setVisibility(View.VISIBLE);
                this.mTVSound.setVisibility(View.GONE);
            }

            if (al_casevideo == null || al_casevideo.size() == 0) {
                this.mVideoBox.setVisibility(View.GONE);
                this.mTVVideo.setVisibility(View.VISIBLE);
            } else {
                this.mVideoBox.setVisibility(View.VISIBLE);
                this.mTVVideo.setVisibility(View.GONE);
            }
        }

    }

    public void landUpdOnClick(View paramView) {// 上传  按钮
        if (isFastDoubleClick()) {
            return;
        } else {// 启高
            if (mUploadCaseObj instanceof IllegalClues
                    || mUploadCaseObj instanceof IllegalChange
                    || mUploadCaseObj instanceof IllegalLinXia
                    || mUploadCaseObj instanceof IllegalLinMu
                    || mUploadCaseObj instanceof IllegalMuCai
                    || mUploadCaseObj instanceof IllegalWeiFaOne
                    || mUploadCaseObj instanceof IllegalDongWu) {
                if (mUploadCaseObj.getGeos() != null) {
                    mUploadButton.setEnabled(false);//按钮变灰色，不可点击
                    onPreExecute();
                    UploadThread uploadThread = new UploadThread();
                    new Thread(uploadThread).start();
                } else {
                   ToastUtils.showLong( "请采集地块信息后再上报！");
                }
            } else {
                mUploadButton.setEnabled(false);
                onPreExecute();
                UploadThread uploadThread = new UploadThread();
                new Thread(uploadThread).start();
            }
        }
    }

    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        System.out.println("--------------timeD+----" + timeD);
        if (0 < timeD && timeD < 2000) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

    protected void onPreExecute() {
        this.dialog = new ProgressDialog(SubmitCaseActivity.this);
        this.dialog.setMessage("地块上传中...");
        this.dialog.setCancelable(false);
        this.dialog.show();
    }

    private class MyHandler extends Handler {

        public MyHandler(Looper paramLooper) {
            super(paramLooper);
        }

        public void handleMessage(Message paramMessage) {
            switch (paramMessage.what) {
                case 14000:
                    showFailureMessage();
                    break;
                case 15000:
                    showSucessMessage();
                    break;
                case 16000:
                    mUploadButton.setEnabled(true);
                    break;
            }
        }
    }

    private void showSucessMessage() {
        this.dialog.dismiss();
        if (mUploadCaseObj instanceof IllegalLand) {
            new CaseDBMExternal(SubmitCaseActivity.this).updateUpState(
                    mUploadCaseObj.getCaseId(), 1);
           ToastUtils.showLong( "地块信息上传成功！");
            mUploadButton.setEnabled(false);
            Intent localIntent = new Intent(
                    SubmitCaseActivity.this, CaseDetailsActivity.class);
            localIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(localIntent);
            setResult(1);
            finish();
        } else if (mUploadCaseObj instanceof IllegalClues) {
            new CaseDBMExternal(SubmitCaseActivity.this).updateUpState(
                    mUploadCaseObj.getCaseId(), 1);
           ToastUtils.showLong( "违法线索信息上传成功！");
            mUploadButton.setEnabled(false);
            Intent localIntent = new Intent(
                    SubmitCaseActivity.this, CaseDetailsActivity.class);
            localIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(localIntent);
            setResult(1);
            finish();
        }
        // 启高
        else if (mUploadCaseObj instanceof IllegalChange) {
            new CaseDBMExternal(SubmitCaseActivity.this).updateUpState(
                    mUploadCaseObj.getCaseId(), 1);
           ToastUtils.showLong( "林权流转变更上传成功！");
            mUploadButton.setEnabled(false);
            Intent localIntent = new Intent(
                    SubmitCaseActivity.this, CaseDetailsActivity.class);
            localIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(localIntent);
            setResult(1);
            finish();
        }
        // 启高
        else if (mUploadCaseObj instanceof IllegalLinXia) {
            new CaseDBMExternal(SubmitCaseActivity.this).updateUpState(
                    mUploadCaseObj.getCaseId(), 1);
           ToastUtils.showLong( "林下经济上传成功！");
            mUploadButton.setEnabled(false);
            Intent localIntent = new Intent(
                    SubmitCaseActivity.this, CaseDetailsActivity.class);
            localIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(localIntent);
            setResult(1);
            finish();
        }
        // 启高
        else if (mUploadCaseObj instanceof IllegalLinMu) {
            new CaseDBMExternal(SubmitCaseActivity.this).updateUpState(
                    mUploadCaseObj.getCaseId(), 1);
           ToastUtils.showLong( "林木采伐上传成功！");
            mUploadButton.setEnabled(false);
            Intent localIntent = new Intent(
                    SubmitCaseActivity.this, CaseDetailsActivity.class);
            localIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(localIntent);
            setResult(1);
            finish();
        }
        // 启高
        else if (mUploadCaseObj instanceof IllegalMuCai) {
            new CaseDBMExternal(SubmitCaseActivity.this).updateUpState(
                    mUploadCaseObj.getCaseId(), 1);
           ToastUtils.showLong( "木材检查上传成功！");
            mUploadButton.setEnabled(false);
            Intent localIntent = new Intent(
                    SubmitCaseActivity.this, CaseDetailsActivity.class);
            localIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(localIntent);
            setResult(1);
            finish();
        }
        // 启高
        else if (mUploadCaseObj instanceof IllegalWeiFaOne) {
            new CaseDBMExternal(SubmitCaseActivity.this).updateUpState(
                    mUploadCaseObj.getCaseId(), 1);
           ToastUtils.showLong( "林业行政处罚立案上传成功！");
            mUploadButton.setEnabled(false);
            Intent localIntent = new Intent(
                    SubmitCaseActivity.this, CaseDetailsActivity.class);
            localIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(localIntent);
            setResult(1);
            finish();
        }
        // 启高
        else if (mUploadCaseObj instanceof IllegalDongWu) {
            new CaseDBMExternal(SubmitCaseActivity.this).updateUpState(
                    mUploadCaseObj.getCaseId(), 1);
           ToastUtils.showLong( "野生动物养殖申请上传成功！");
            mUploadButton.setEnabled(false);
            Intent localIntent = new Intent(
                    SubmitCaseActivity.this, CaseDetailsActivity.class);
            localIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(localIntent);
            setResult(1);
            finish();
        } else if (mUploadCaseObj instanceof Task) {
            new TaskDBMExternal(SubmitCaseActivity.this).updateUpState(
                    mUploadCaseObj.getRecId(), 1);
           ToastUtils.showLong( "任务成果信息上传成功！");
            mUploadButton.setEnabled(false);
            Intent localIntent = new Intent(
                    SubmitCaseActivity.this, TaskDetailsActivity.class);
            localIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(localIntent);
            setResult(1);
            finish();
        } else if (mUploadCaseObj instanceof Case) {
            new CaseDBMExternal(SubmitCaseActivity.this).updateUpState(
                    mUploadCaseObj.getCaseId(), 1);
           ToastUtils.showLong( "任务成果信息上传成功！");
            mUploadButton.setEnabled(false);
            Intent localIntent = new Intent(
                    SubmitCaseActivity.this, CaseDetailsActivity.class);
            localIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(localIntent);
            setResult(1);
            finish();
        }

    }

    private void showFailureMessage() {
        this.dialog.dismiss();
        ToastUtils.showLong(  "当前网络不可用，请稍后再试！");
        mUploadButton.setEnabled(true);
        return;
    }

    class UploadThread implements Runnable {
        public void run() {

            String fileName = UtilsTools.GetCurrentTime() + "";
            String sourceCaseFilePath = GisQueryApplication.getApp()
                    .getProjectPath() + "TaskUpload/caseFiles/" + fileName;// json路径
            File caseDir = new File(sourceCaseFilePath);
            if (!(caseDir.exists()))
                caseDir.mkdirs();
            String zipFilePath = GisQueryApplication.getApp()
                    .getProjectPath() + "TaskUpload/zipFiles/";// 压缩包路径
            File zipDir = new File(zipFilePath);
            if (!(zipDir.exists()))
                zipDir.mkdirs();

            ObjectMapper mapper = new ObjectMapper();
            Set<String> filterFields = new HashSet<String>();
            filterFields.add("landGeometry");
            filterFields.add("IMGLST");
            filterFields.add("SOUNDLST");
            filterFields.add("VIDEOLST");
            filterFields.add("allPicPath");
            filterFields.add("allSoundPath");
            filterFields.add("allVideoPath");
            Set<String> filterFields_empty = new HashSet<String>();

            FilterProvider filters = new SimpleFilterProvider()
                    .addFilter(
                            mUploadCaseObj.getClass().getName(),
                            SimpleBeanPropertyFilter
                                    .serializeAllExcept(filterFields))
                    .addFilter(
                            CaseImg.class.getName(),
                            SimpleBeanPropertyFilter
                                    .serializeAllExcept(filterFields_empty))
                    .addFilter(
                            CaseSound.class.getName(),
                            SimpleBeanPropertyFilter
                                    .serializeAllExcept(filterFields_empty))
                    .addFilter(
                            CaseVideo.class.getName(),
                            SimpleBeanPropertyFilter
                                    .serializeAllExcept(filterFields_empty));
            mapper.setFilters(filters);
            mapper.setAnnotationIntrospector(new JacksonAnnotationIntrospector() {
                @Override
                public Object findFilterId(AnnotatedClass ac) {
                    return ac.getName();
                }
            });
            try {
                //重点类
                CaseUploadsConfig caseUploadsConfig = GisQueryApplication.getApp().getProjectconfig()
                        .getCaseUploadsConfig();

                Map<String, Object> caseMap = new HashMap<String, Object>();

                if (mUploadCaseObj instanceof IllegalLand) {
                    // 动态巡查
                    caseMap.put("RECID", -1);
                    caseMap.put("BIZID", caseUploadsConfig
                            .getCaseUploadConfigMap().get("dtxc").getBizId());
                    caseMap.put("BIZATTRS", mapper.readValue(
                            mUploadCaseObj.getAttrs(), Object.class));
                    caseMap.put("BIZGEOS", mapper.readValue(
                            mUploadCaseObj.getGeos(), Object.class));
                } else if (mUploadCaseObj instanceof IllegalClues) {
                    // 违法线索
                    caseMap.put("RECID", -1);
                    caseMap.put("BIZID", caseUploadsConfig
                            .getCaseUploadConfigMap().get("wfxs").getBizId());
                    caseMap.put("BIZATTRS", mapper.readValue(
                            mUploadCaseObj.getAttrs(), Object.class));
                    caseMap.put("BIZGEOS", mapper.readValue(
                            mUploadCaseObj.getGeos(), Object.class));
                }
                // 启高
                else if (mUploadCaseObj instanceof IllegalChange) {
                    // 林权流转变更
                    caseMap.put("RECID", -1);
                    caseMap.put("BIZID", caseUploadsConfig
                            .getCaseUploadConfigMap().get("change").getBizId());
                    caseMap.put("BIZATTRS", mapper.readValue(
                            mUploadCaseObj.getAttrs(), Object.class));
                    // String st = mUploadCaseObj.getGeos();
                    caseMap.put("BIZGEOS", mapper.readValue(
                            mUploadCaseObj.getGeos(), Object.class));

                    int ls = 0;
                }
                // 启高
                else if (mUploadCaseObj instanceof IllegalLinXia) {
                    // 林下经济
                    caseMap.put("RECID", -1);
                    caseMap.put("BIZID", caseUploadsConfig
                            .getCaseUploadConfigMap().get("linxia").getBizId());
                    caseMap.put("BIZATTRS", mapper.readValue(
                            mUploadCaseObj.getAttrs(), Object.class));
                    // String st = mUploadCaseObj.getGeos();
                    caseMap.put("BIZGEOS", mapper.readValue(
                            mUploadCaseObj.getGeos(), Object.class));
                }
                // 启高
                else if (mUploadCaseObj instanceof IllegalLinMu) {
                    // 林木采伐
                    caseMap.put("RECID", -1);
                    caseMap.put("BIZID", caseUploadsConfig
                            .getCaseUploadConfigMap().get("linmu").getBizId());
                    caseMap.put("BIZATTRS", mapper.readValue(
                            mUploadCaseObj.getAttrs(), Object.class));
                    // String st = mUploadCaseObj.getGeos();
                    caseMap.put("BIZGEOS", mapper.readValue(
                            mUploadCaseObj.getGeos(), Object.class));
                }
                // 启高
                else if (mUploadCaseObj instanceof IllegalMuCai) {
                    // 木材检查
                    caseMap.put("RECID", -1);
                    caseMap.put("BIZID", caseUploadsConfig
                            .getCaseUploadConfigMap().get("mucai").getBizId());
                    caseMap.put("BIZATTRS", mapper.readValue(
                            mUploadCaseObj.getAttrs(), Object.class));
                    // String st = mUploadCaseObj.getGeos();
                    caseMap.put("BIZGEOS", mapper.readValue(
                            mUploadCaseObj.getGeos(), Object.class));
                }
                // 启高
                else if (mUploadCaseObj instanceof IllegalWeiFaOne) {
                    // 林业行政处罚
                    caseMap.put("RECID", -1);
                    caseMap.put("BIZID", caseUploadsConfig
                            .getCaseUploadConfigMap().get("weifa").getBizId());
                    caseMap.put("BIZATTRS", mapper.readValue(
                            mUploadCaseObj.getAttrs(), Object.class));
                    // String st = mUploadCaseObj.getGeos();
                    caseMap.put("BIZGEOS", mapper.readValue(
                            mUploadCaseObj.getGeos(), Object.class));
                }
                // 启高
                else if (mUploadCaseObj instanceof IllegalDongWu) {
                    // 林业行政处罚
                    caseMap.put("RECID", -1);
                    caseMap.put("BIZID", caseUploadsConfig
                            .getCaseUploadConfigMap().get("dongwu").getBizId());
                    caseMap.put("BIZATTRS", mapper.readValue(
                            mUploadCaseObj.getAttrs(), Object.class));
                    // String st = mUploadCaseObj.getGeos();
                    caseMap.put("BIZGEOS", mapper.readValue(
                            mUploadCaseObj.getGeos(), Object.class));
                } else if (mUploadCaseObj instanceof Task) {
                    // 我的任务
                    caseMap.put("RECID", mUploadCaseObj.getRecId());
                    caseMap.put("BIZID", ((Task) mUploadCaseObj).getBizId());
                    caseMap.put("BIZATTRS", mapper.readValue(
                            mUploadCaseObj.getAttrs(), Object.class));
                } else {
                    // 普通案件
                    caseMap.put("RECID", mUploadCaseObj.getRecId());
                    if (mUploadCaseObj instanceof Task) {
                        caseMap.put("BIZID", ((Task) mUploadCaseObj).getBizId());
                    } else {
                        caseMap.put("BIZID", "" + 2466);
                    }

                    caseMap.put("BIZATTRS", mapper.readValue(
                            mUploadCaseObj.getAttrs(), Object.class));
                }
                if (SubmitCaseActivity.this.mPicBox.isChecked()) {
                    caseMap.put("BIZIMGS", al_caseimg);
                }
                if (SubmitCaseActivity.this.mSoundBox.isChecked()) {
                    caseMap.put("BIZSOUNDS", al_casesound);
                }
                if (SubmitCaseActivity.this.mVideoBox.isChecked()) {
                    caseMap.put("BIZVIDEOS", al_casevideo);
                }
                MapOperate.curLandUploadId = UtilsTools.GetCurrentFormatTime();
                caseMap.put("UPLOADID", MapOperate.curLandUploadId);
                String caseinfo = mapper.writeValueAsString(caseMap);
                saveFile(caseinfo, fileName + ".json", sourceCaseFilePath);// 生成JSON文件并保存到sd卡中
            } catch (JsonGenerationException e) {
                e.printStackTrace();
            } catch (JsonMappingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            // 将图片、音频、视频copy到json同一个文件夹下
            if (SubmitCaseActivity.this.mPicBox.isChecked()) {
                for (CaseImg caseImg : al_caseimg) {
                    String fileRealPath = caseImg.getImgPath();
                    String imgfileName = fileRealPath.substring(
                            fileRealPath.lastIndexOf("/") + 1,
                            fileRealPath.length());
                    String sourceImgPath = GisQueryApplication.getApp().getProjectPath()
                            + "TaskUpload/caseFiles/"
                            + fileName
                            + "/"
                            + imgfileName;
                    FileUtil.copyFile(caseImg.getImgPath(), sourceImgPath);
                }
            }
            if (SubmitCaseActivity.this.mSoundBox.isChecked()) {
                for (CaseSound caseSound : al_casesound) {
                    String sourceSoundPath = caseSound.getSoundPath().replace(
                            "Sounds", "TaskUpload/caseFiles/" + fileName);
                    FileUtil.copyFile(caseSound.getSoundPath(), sourceSoundPath);
                }
            }
            if (SubmitCaseActivity.this.mVideoBox.isChecked()) {
                for (CaseVideo caseVideo : al_casevideo) {
                    String sourceVideoPath = caseVideo.getVideoPath().replace(
                            "CameraVideo", "TaskUpload/caseFiles/" + fileName);
                    FileUtil.copyFile(caseVideo.getVideoPath(), sourceVideoPath);
                }
            }
            boolean flag = fileToZip(sourceCaseFilePath, zipFilePath, fileName);// 文件打包
            if (flag) {
                System.out.println(">>>>>> 文件打包成功. <<<<<<");
                String srcPath = zipFilePath + fileName + ".zip";
                uploadFile(srcPath);
                System.out.println("上传地址：" + srcPath);
            } else {
                System.out.println(">>>>>> 文件打包失败. <<<<<<");
            }
        }
    }



    private void uploadFile(String srcPath) {                  //上传文件
        int userId = SysConfig.mHumanInfo.getHumanId();
        String cur_userName = SysConfig.mHumanInfo.getHumanName();
        String userName = cur_userName;
        try {
            userName = URLEncoder.encode(cur_userName, "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        CaseUploadsConfig caseUploadsConfig = GisQueryApplication.getApp().getProjectconfig().getCaseUploadsConfig();


        try {

//            String id = String.valueOf(userId);
//            String  key = SecureUtil.encodeBase64(SecureUtil.encryptOfRSA(SecureUtil.getRandomAESKey()));
//            id = SecureUtil.encryptString(id);
//            userName =  SecureUtil.encryptString(userName);
//            System.out.println("partId----" + id);
//            System.out.println("userName----" + userName);
//            System.out.println("key----" + key);
//            String uploadUrl = caseUploadsConfig.getUploadUrl() + "?partId="
//                    + SecureUtil.toURLEncoded(id )+ "&partName=" + SecureUtil.toURLEncoded(userName)
//                    +"&key="+ SecureUtil.toURLEncoded(key);

            String uploadUrl = caseUploadsConfig.getUploadUrl() + "?partId="
                    + userId+ "&partName=" + userName;
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


            while ( fis.read(buffer) != -1) {
//                dos.write(SecureUtil.encryptData(buffer));
                dos.write(buffer);
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
                if (jsonObject.has("data") && jsonObject.getInt("data") != -1) {
                    int recId = jsonObject.getInt("data");
                    new CaseDBMExternal(this).updateCaseRecId(
                            mUploadCaseObj.getCaseId(), recId);
                }
                mHandler.sendEmptyMessage(15000);
            } else {
                mHandler.sendEmptyMessage(14000);
                mHandler.sendEmptyMessage(16000);
            }
            dos.close();
            is.close();

        } catch (Exception e) {
            mHandler.sendEmptyMessage(14000);
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

    // 生成文件并保存到sd卡上
    public boolean saveFile(String contentStr, String fileName, String filePath) {
        OutputStream out = null;
        try {
            File f = new File(filePath + "/" + fileName);
            f.createNewFile();
            System.out.println(filePath + "/" + fileName);
            out = new FileOutputStream(f);

        } catch (Exception e) {
            System.out.println("Error" + e.getMessage());
            return false;
        }
        try {
            OutputStreamWriter outw = new OutputStreamWriter(out, "utf-8");
            outw.write(contentStr);
            outw.close();
            out.close();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * 将存放在sourceFilePath目录下的源文件,打包成fileName名称的ZIP文件,并存放到zipFilePath。
     *
     * @param sourceFilePath
     * @param zipFilePath    压缩后存放路径
     * @param fileName       压缩后文件的名称
     * @return flag
     */

    public boolean fileToZip(String sourceFilePath, String zipFilePath,
                             String fileName) {

        boolean flag = false;
        File sourceFile = new File(sourceFilePath);
        BufferedInputStream bis = null;
        ZipOutputStream zos = null;
        if (sourceFile.exists() == false) {
            System.out.println(">>>>>> 待压缩的文件目录：" + sourceFilePath
                    + " 不存在. <<<<<<");
        } else {
            try {
                File zipFile = new File(zipFilePath + "/" + fileName + ".zip");
                if (zipFile.exists()) {
                    System.out.println(">>>>>> " + zipFilePath + " 目录下存在名字为："
                            + fileName + ".zip" + " 打包文件. <<<<<<");
                } else {
                    File[] sourceFiles = sourceFile.listFiles();
                    if (null == sourceFiles || sourceFiles.length < 1) {
                        System.out.println(">>>>>> 待压缩的文件目录：" + sourceFilePath
                                + " 里面不存在文件,无需压缩. <<<<<<");
                    } else {
                        zos = new ZipOutputStream(new BufferedOutputStream(
                                new FileOutputStream(zipFile)));
                        byte[] bufs = new byte[1024 * 10];
                        for (int i = 0; i < sourceFiles.length; i++) {
                            // 创建ZIP实体,并添加文件内容
                            ZipEntry zipEntry = new ZipEntry(
                                    sourceFiles[i].getName());
                            zos.putNextEntry(zipEntry);
                            // 读取待压缩的文件并写进压缩包里
                            bis = new BufferedInputStream(new FileInputStream(
                                    sourceFiles[i]), 1024 * 10);
                            int read = 0;
                            while ((read = bis.read(bufs, 0, 1024 * 10)) != -1) {
                                zos.write(bufs, 0, read);
                            }
                        }
                        flag = true;
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            } finally {
                // 关闭流
                try {
                    if (null != bis)
                        bis.close();
                    if (null != zos)
                        zos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }
        }
        return flag;
    }

    public void backOnClick(View paramView) {

        if (mUploadCaseObj instanceof IllegalLand
                || mUploadCaseObj instanceof IllegalClues) {
            Intent localIntent = new Intent(
                    SubmitCaseActivity.this, CaseDetailsActivity.class);
            localIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(localIntent);
            finish();
        }

        // 启高
        if (mUploadCaseObj instanceof IllegalChange
                || mUploadCaseObj instanceof IllegalMuCai
                || mUploadCaseObj instanceof IllegalLinXia
                || mUploadCaseObj instanceof IllegalLinMu
                || mUploadCaseObj instanceof IllegalWeiFaOne
                || mUploadCaseObj instanceof IllegalDongWu) {
            Intent localIntent = new Intent(
                    SubmitCaseActivity.this, CaseDetailsActivity.class);
            localIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(localIntent);
            finish();
        } else if (mUploadCaseObj instanceof Task) {
            Intent localIntent = new Intent(
                    SubmitCaseActivity.this, TaskDetailsActivity.class);
            localIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(localIntent);
            finish();
        }
    }
}
