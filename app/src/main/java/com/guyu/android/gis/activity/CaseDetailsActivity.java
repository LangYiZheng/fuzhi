package com.guyu.android.gis.activity;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Serializable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.io.File;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.print.PrintAttributes;
import android.print.PrintManager;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.ToastUtils;
import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.MapGeometry;
import com.esri.core.geometry.Polygon;
import com.guyu.android.database.sync.DictItemDBMExternal;
import com.guyu.android.database.sync.FieldDBMExternal;
import com.guyu.android.database.task.CaseDBMExternal;
import com.guyu.android.database.task.CaseImgDBMExternal;
import com.guyu.android.database.task.CaseSoundDBMExternal;
import com.guyu.android.database.task.CaseVideoDBMExternal;
import com.guyu.android.gis.adapter.DictItemAdapter;
import com.guyu.android.gis.adapter.MyPrintAdapter;
import com.guyu.android.gis.app.GisQueryApplication;
import com.guyu.android.gis.common.Case;
import com.guyu.android.gis.common.CaseImg;
import com.guyu.android.gis.common.CaseSound;
import com.guyu.android.gis.common.CaseVideo;
import com.guyu.android.gis.common.DictItem;
import com.guyu.android.gis.common.DictItemTwo;
import com.guyu.android.gis.common.Field;
import com.guyu.android.gis.opt.MapOperate;
import com.guyu.android.gis.wkt.PolygonObject;
import com.guyu.android.utils.GsonUtil;
import com.guyu.android.utils.MathUtils;
import com.guyu.android.utils.SdCardUtils;
import com.guyu.android.utils.UtilsTools;
import com.guyu.android.widget.wheel.StrericWheelAdapter;
import com.guyu.android.widget.wheel.WheelView;
import com.guyu.android.R;

public class CaseDetailsActivity extends Activity {
    public static String DATA_CASE_OBJ = "CaseObj";
    private LayoutInflater layoutInflater = null;

    private LinearLayout mLayout;
    private TextView mPhotoTextView = null;
    private TextView mSoundTextView = null;
    private TextView mVideoTextView = null;
    private EditText mwfmj2 = null;

    private LinearLayout include_table = null;

    private List<Field> mDefaultFieldList = new ArrayList<Field>();
    private List<String> m_FieldValueList = new ArrayList<String>();
    private List<Field> m_lstFieldInfos = new ArrayList<Field>();
    private List<String> mImgList = new ArrayList<String>();
    private List<String> mSoundList = new ArrayList<String>();
    private List<String> mVideoList = new ArrayList<String>();

    private Button mUpButton = null;
    private Button mCaseImgsButton = null;
    private Button mCaseSoundsButton = null;
    private Button mCaseVideosButton = null;
    private Case mCaseObj;
    private MOnClickListener mClickListener;
    private String mCaseId;
    private String mCaseType;
    private List<CaseImg> al_landImgObj;
    private List<CaseSound> al_landSoundObj;
    List<CaseVideo> al_landVideoObj;

    private Spinner localSpinner = null;
    private Spinner localSpinner2 = null;
    public int mPoint;

    private WheelView yearWheel, monthWheel, dayWheel, hourWheel, minuteWheel, secondWheel;
    public static String[] yearContent = null;
    public static String[] monthContent = null;
    public static String[] dayContent = null;
    public static String[] hourContent = null;
    public static String[] minuteContent = null;
    public static String[] secondContent = null;

    private final int WC = ViewGroup.LayoutParams.WRAP_CONTENT;
    private final int FP = ViewGroup.LayoutParams.MATCH_PARENT;
    private TableLayout tableLayout;
    private String table_data = "";// TableLayout的数据
    private EditText localEditText;
    private String tableData = "";

    private String[] keys = {"ZWM", "LDXW", "WGBHJB", "GYBHJB"};
    // 多选
    private boolean[] bools = new boolean[4];
    private RadioButton[] btns = new RadioButton[4];
    private Handler handler;


    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(R.layout.activity_case_details_h);

        initHandler();


        this.layoutInflater = LayoutInflater.from(this);

        // 启高
        include_table = (LinearLayout) findViewById(R.id.include_table);
        tableLayout = (TableLayout) findViewById(R.id.tl_table);

        initTimePickerContent();
        initActivity();
    }

    private void onPrintPdf(String path) {
        PrintManager printManager = (PrintManager) getSystemService(Context.PRINT_SERVICE);
        PrintAttributes.Builder builder = new PrintAttributes.Builder();
        builder.setColorMode(PrintAttributes.COLOR_MODE_COLOR);
        printManager.print("test pdf print", new MyPrintAdapter(this, path), builder.build());
    }


    private void initHandler() {
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 0:
                        break;
                }
            }
        };
    }

    public void initTimePickerContent() {
        yearContent = new String[10];
        for (int i = 0; i < 10; i++)
            yearContent[i] = String.valueOf(i + 2013);

        monthContent = new String[12];
        for (int i = 0; i < 12; i++) {
            monthContent[i] = String.valueOf(i + 1);
            if (monthContent[i].length() < 2) {
                monthContent[i] = "0" + monthContent[i];
            }
        }

        dayContent = new String[31];
        for (int i = 0; i < 31; i++) {
            dayContent[i] = String.valueOf(i + 1);
            if (dayContent[i].length() < 2) {
                dayContent[i] = "0" + dayContent[i];
            }
        }
        hourContent = new String[24];
        for (int i = 0; i < 24; i++) {
            hourContent[i] = String.valueOf(i);
            if (hourContent[i].length() < 2) {
                hourContent[i] = "0" + hourContent[i];
            }
        }

        minuteContent = new String[60];
        for (int i = 0; i < 60; i++) {
            minuteContent[i] = String.valueOf(i);
            if (minuteContent[i].length() < 2) {
                minuteContent[i] = "0" + minuteContent[i];
            }
        }
        secondContent = new String[60];
        for (int i = 0; i < 60; i++) {
            secondContent[i] = String.valueOf(i);
            if (secondContent[i].length() < 2) {
                secondContent[i] = "0" + secondContent[i];
            }
        }
    }

    // 启高。点击去getCaseByIdAndType需要修改
    // GeometryCollectTool修改。否则上传不了
    private void initActivity() {
        this.mCaseId = this.getIntent().getStringExtra("caseId");
        this.mCaseType = this.getIntent().getStringExtra("caseType");
        this.mCaseObj = new CaseDBMExternal(this).getCaseByIdAndType(mCaseId, // 启高。点击去getCaseByIdAndType需要修改
                mCaseType);
        if (mCaseObj != null) {
            this.mLayout = ((LinearLayout) findViewById(R.id.ll_layout));
            initDefaultLandField();
            initiList();
            this.mClickListener = new MOnClickListener();
            ((Button) findViewById(R.id.case_details_rb_analysis)).setOnClickListener(this.mClickListener);

            ((Button) findViewById(R.id.btn_insert)).setOnClickListener(this.mClickListener);
            ((Button) findViewById(R.id.btn_keep)).setOnClickListener(this.mClickListener);

            if ("dtxc".equals(this.mCaseObj.getCaseType())) {
                setTilte("动态巡查详情(" + this.mCaseObj.getCaseName() + ")");

            } else if ("wfxs".equals(this.mCaseObj.getCaseType())) {
                setTilte("违法线索详情(" + this.mCaseObj.getCaseName() + ")");
            }
            // 启高
            else if ("change".equals(this.mCaseObj.getCaseType())) {
                setTilte("林权流转变更详情(" + this.mCaseObj.getCaseName() + ")");
            } else if ("linxia".equals(this.mCaseObj.getCaseType())) {
                setTilte("林下经济详情(" + this.mCaseObj.getCaseName() + ")");
            } else if ("linmu".equals(this.mCaseObj.getCaseType())) {
                setTilte("林木采伐详情(" + this.mCaseObj.getCaseName() + ")");
            } else if ("mucai".equals(this.mCaseObj.getCaseType())) {
                setTilte("木材检查详情(" + this.mCaseObj.getCaseName() + ")");
            } else if ("weifa".equals(this.mCaseObj.getCaseType())) {
                setTilte("林业行政处罚立案详情(" + this.mCaseObj.getCaseName() + ")");
            } else if ("dongwu".equals(this.mCaseObj.getCaseType())) {
                include_table.setVisibility(View.VISIBLE);
                setTilte("野生动物养殖详情(" + this.mCaseObj.getCaseName() + ")");
            }
            this.mCaseImgsButton = ((Button) findViewById(R.id.btn_case_imgs));
            this.mCaseSoundsButton = ((Button) findViewById(R.id.btn_case_sounds));
            this.mCaseVideosButton = ((Button) findViewById(R.id.btn_case_videos));
            this.mCaseImgsButton.setOnClickListener(this.mClickListener);
            this.mCaseSoundsButton.setOnClickListener(this.mClickListener);
            this.mCaseVideosButton.setOnClickListener(this.mClickListener);
            findViewById(R.id.btnPrint).setOnClickListener((v) -> {
                onPrintPdf(GisQueryApplication.currentProjectPath + "z.pdf");
            });
            findViewById(R.id.btnExport).setOnClickListener((v) -> {
                onExportFile();
            });

            // 上报按钮
            mUpButton = ((Button) findViewById(R.id.case_details_rb_up_data));
            if (mCaseObj.isUp()) {
                mUpButton.setVisibility(View.VISIBLE);
                mUpButton.setEnabled(false);
                mUpButton.setClickable(false);
                findViewById(R.id.case_details_save_data).setEnabled(false);
                mUpButton.setText("已上报");
            } else {
                ((Button) findViewById(R.id.case_details_rb_up_data)).setVisibility(View.VISIBLE);
                mUpButton = ((Button) findViewById(R.id.case_details_rb_up_data));
                mUpButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View paramView) {
                        Intent localIntent = new Intent(CaseDetailsActivity.this, SubmitCaseActivity.class);// 启高
                        localIntent.putExtra(SubmitCaseActivity.DATA_CASE_OBJ,
                                (Serializable) CaseDetailsActivity.this.mCaseObj);
                        CaseDetailsActivity.this.startActivityForResult(localIntent, 3236);
                    }
                });
            }
        } else {
            setTilte("案件详情");
        }

    }

    private void onExportFile() {

        String json = mCaseObj.getGeos();
        PolygonObject po = GsonUtil.getGson().fromJson(json, PolygonObject.class);
        List<List<Double[]>> rings = po.getRings();
        List<List<String[]>> strRings = new ArrayList<>();
        for (int i = 0; i < rings.size(); i++) {
            List<String[]> l = new ArrayList<>();
            List<Double[]> r = rings.get(i);
            for (int j = 0; j < r.size(); j++) {
                Double[] d = r.get(j);
                String[] s = new String[d.length];

                s[0] = MathUtils.round(d[0], 3);
                s[1] = MathUtils.round(d[1], 3);
                l.add(s);
            }
            strRings.add(l);
        }
        Rings r = new Rings(strRings);
        String str = GsonUtil.getGson().toJson(r);
        writeStringToFile(SdCardUtils.getFirstExterPath()+"/动态巡查数据/"+mCaseObj.getCaseId() + ".txt",str);

    }

    public static void writeStringToFile(String filePath, String str) {
        PrintStream ps = null;
        try {
            File file = new File(filePath);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            if (file.exists()) {
                file.delete();
            }
            ps = new PrintStream(new FileOutputStream(file));
            ps.println(str);// 往文件里写入字符串
            ps.flush();
            ToastUtils.showLong("数据导出成功\n在 动态巡查 文件夹中");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class Rings {
        List<List<String[]>> strRings;

        public Rings(List<List<String[]>> strRings) {
            this.strRings = strRings;
        }
    }

    private void setTilte(String paramString) {
        ((TextView) findViewById(R.id.tv_title)).setText(paramString);
    }

    public void initDefaultLandField() {
        // 从数据库加载当前业务的字段配置
        Map<String, Field> fmap = new FieldDBMExternal(this).getFieldMap(" casetype = ? ",
                new String[]{this.mCaseType}, " disporder asc ");// 查询所有字段信息
        // 清空默认的字段列表
        mDefaultFieldList.clear();
        // 获取配置文件中需要上传的字段列表
        String casefields = GisQueryApplication.getApp().getProjectconfig().getCaseUploadsConfig().getCaseUploadConfigMap()
                .get(this.mCaseObj.getCaseType()).getFields();
        // 获取
        String attrs = mCaseObj.getAttrs();// 属性JSON
        System.out.println("------从数据库加载保存的数据内容：" + attrs);
        try {
            JSONObject jsonObject = new JSONObject(attrs);
            String[] fieldNameAry = casefields.split(",");
            // tableData = jsonObject.getString("DONGWU");//启高

            for (String fieldName : fieldNameAry) {
                Field f = fmap.get(fieldName);
                if (f != null) {
                    if (fieldName.equals("FZMS")) {
                        String ss = jsonObject.getString(fieldName);
                        String[] mFzms = ss.split("&");
                        f.setDefaultValue(mFzms[0]);
                        mPoint = Integer.parseInt(mFzms[1]);
                        mDefaultFieldList.add(f);
                    } else {
                        f.setDefaultValue(jsonObject.getString(fieldName));
                        mDefaultFieldList.add(f);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // 查询选择有多少个图片、多少个音频、多少个视频
        CaseImgDBMExternal mLandImgManager = new CaseImgDBMExternal(this);
        CaseSoundDBMExternal mLandSoundManager = new CaseSoundDBMExternal(this);
        CaseVideoDBMExternal mLandVideoManager = new CaseVideoDBMExternal(this);
        al_landImgObj = mLandImgManager.getTheCaseAllImg(mCaseObj.getCaseId());
        al_landSoundObj = mLandSoundManager.getTheCaseAllSound(mCaseObj.getCaseId());
        al_landVideoObj = mLandVideoManager.getTheCaseAllVideo(mCaseObj.getCaseId());

        mDefaultFieldList.add(new Field("MPHOTO", "图片", "已选择" + al_landImgObj.size() + "张图片"));
        mDefaultFieldList.add(new Field("MSOUND", "音频", "已选择" + al_landSoundObj.size() + "个录音"));
        mDefaultFieldList.add(new Field("MVIDEO", "视频", "已选择" + al_landVideoObj.size() + "个录像"));
        if (this.mCaseObj.getCaseType().equals("dongwu")) { // 启高 11.14
            mDefaultFieldList.add(new Field("DONGWU", "涉及物种", this.mCaseObj.getAttrs()));
        }
        m_lstFieldInfos.addAll(mDefaultFieldList);
        for (int i = 0; i < m_lstFieldInfos.size(); i++) {
            Field f = this.m_lstFieldInfos.get(i);
            m_FieldValueList.add(f.getDefaultValue());

            if (f.getFieldCnname().equals("涉及物种")) {
                Map<String, String> json = GsonUtil.parseData(f.getDefaultValue());
                String data_table = json.get("DONGWU");
                List<Map<String, String>> list = GsonUtil.jsonToListMaps(data_table);
                tableLayout.setStretchAllColumns(true);
                for (int row = 0; row < list.size(); row++) {
                    TableRow tableRow = new TableRow(CaseDetailsActivity.this);
                    tableRow.setBackgroundColor(Color.rgb(222, 220, 210));
                    json = list.get(row);
                    for (int col = 0; col < 5; col++) {
                        if (col < 4) {

                            EditText et_write = new EditText(CaseDetailsActivity.this);
                            et_write.setBackgroundResource(R.drawable.shappe);

                            et_write.setText(json.get(keys[col]));

                            tableRow.addView(et_write);

                        } else {
                            // 换成删除按钮
                            Button btnDel = new Button(CaseDetailsActivity.this);
                            btnDel.setText("删除");
                            btnDel.setOnClickListener(new View.OnClickListener() {

                                @Override
                                public void onClick(View view) {
                                    TableRow tableRow = (TableRow) view.getParent();
                                    tableLayout.removeView(tableRow);
                                }

                            });
                            tableRow.addView(btnDel);
                        }
                    }
                    tableLayout.addView(tableRow, new TableLayout.LayoutParams(FP, WC));
                }
            }
        }
    }

    private void initiList() {
        this.mLayout.removeAllViewsInLayout();
        final String bizID = GisQueryApplication.getApp().getProjectconfig().getCaseUploadsConfig().getCaseUploadConfigMap()
                .get(mCaseType).getBizId();
        for (int i = 0; i < this.m_lstFieldInfos.size(); i++) {
            Field localField = this.m_lstFieldInfos.get(i);
            String str = localField.getFieldName();
            String fieldCnname = localField.getFieldCnname();
            final int dataType = localField.getDataType();
            LinearLayout localLinearLayout = (LinearLayout) this.layoutInflater
                    .inflate(R.layout.record_field_style, null).findViewById(R.id.record_item);
            localLinearLayout.setTag(str);
            TextView localTextView1 = (TextView) localLinearLayout.findViewById(R.id.attribute_name);
            localTextView1.setTextSize(16.0F);
            localTextView1.setText(localField.getFieldCnname());
            if (dataType == 12) {
                initButton(i, localLinearLayout);
            }
            // 多选
            else if (dataType == 11) {
                String s = localField.getDefaultValue();
                char[] c = s.toCharArray();
                for (int j = 0; j < c.length; j++) {
                    int k = c[j] - 49;
                    bools[k] = true;
                }
                initRadioButton(i, localLinearLayout);
            }
            // 时间类型
            else if (dataType == 6) {
                initDataType6_time(i, localField, dataType, localLinearLayout);
            } else if (str.startsWith("MPHOTO")) {// 图片
                initMphoto(bizID, i, localField, dataType, localLinearLayout);
            } else if (str.startsWith("MSOUND")) {// 录音
                initMsound(bizID, i, localField, dataType, localLinearLayout);
            } else if (str.startsWith("MVIDEO")) {// 录像
                initMvideo(bizID, i, localField, dataType, localLinearLayout);
            } else {
                localEditText = (EditText) localLinearLayout.findViewById(R.id.attribute_value);
                EditText localEditText2 = (EditText) localLinearLayout.findViewById(R.id.attribute_value2);
                // 判断字段是否有字典定义
                int mDicType = localField.getDictType();
                if (mDicType != 0) {
                    initDictItem(i, localField, dataType, localLinearLayout, mDicType);

                } else {
                    initFieldHintAndText(i, localField, str, fieldCnname, dataType, localLinearLayout, localTextView1,
                            localEditText2);
                }

            }

            LinearLayout.LayoutParams localLayoutParams = new LinearLayout.LayoutParams(-1, -2);
            this.mLayout.addView(localLinearLayout, localLayoutParams);
        }
    }

    private void initButton(int i, LinearLayout localLinearLayout) {
        localLinearLayout.findViewById(R.id.vg_record_style_01).setVisibility(View.GONE);
        localLinearLayout.findViewById(R.id.vg_record_style_02).setVisibility(View.GONE);
        localLinearLayout.findViewById(R.id.vg_record_style_03).setVisibility(View.GONE);
        localLinearLayout.findViewById(R.id.rbtn).setVisibility(View.GONE);
        localLinearLayout.findViewById(R.id.rlBtn).setVisibility(View.VISIBLE);
        OnClickListener l = new OnClickListener(i);
        Button btn1 = (Button) localLinearLayout.findViewById(R.id.btnStartSignature);
        Button btn2 = (Button) localLinearLayout.findViewById(R.id.btnShowSignature);

        btn1.setOnClickListener(l);
        btn2.setOnClickListener(l);
    }

    private void initRadioButton(int i, LinearLayout localLinearLayout) {
        localLinearLayout.findViewById(R.id.vg_record_style_01).setVisibility(View.GONE);
        localLinearLayout.findViewById(R.id.vg_record_style_02).setVisibility(View.GONE);
        localLinearLayout.findViewById(R.id.vg_record_style_03).setVisibility(View.GONE);
        localLinearLayout.findViewById(R.id.rbtn).setVisibility(View.VISIBLE);
        btns[0] = (RadioButton) localLinearLayout.findViewById(R.id.btn1);
        btns[1] = (RadioButton) localLinearLayout.findViewById(R.id.btn2);
        btns[2] = (RadioButton) localLinearLayout.findViewById(R.id.btn3);
        btns[3] = (RadioButton) localLinearLayout.findViewById(R.id.btn4);
        for (int j = 0; j < bools.length; j++) {
            if (bools[j]) {
                btns[j].setChecked(true);
            }
        }
        OnClickListener l = new OnClickListener(i);
        for (int j = 0; j < btns.length; j++) {
            btns[j].setOnClickListener(l);
        }

    }

    private class OnClickListener implements View.OnClickListener {
        private int index;

        public OnClickListener(int index) {
            this.index = index;
        }

        @Override
        public void onClick(View v) {
            Intent intent;
            switch (v.getId()) {
                case R.id.btn1:
                    if (bools[0]) {
                        btns[0].setChecked(!btns[0].isChecked());
                    }

                    bools[0] = btns[0].isChecked();
                    break;
                case R.id.btn2:
                    if (bools[1]) {
                        btns[1].setChecked(!btns[1].isChecked());
                    }
                    bools[1] = btns[1].isChecked();
                    break;
                case R.id.btn3:
                    if (bools[2]) {
                        btns[2].setChecked(!btns[2].isChecked());
                    }
                    bools[2] = btns[2].isChecked();
                    break;
                case R.id.btn4:
                    if (bools[3]) {
                        btns[3].setChecked(!btns[3].isChecked());
                    }
                    bools[3] = btns[3].isChecked();
                    break;
                case R.id.btnStartSignature:
                    intent = new Intent(CaseDetailsActivity.this, SignaturePadActivity.class);

                    intent.putExtra("index", index);
                    CaseDetailsActivity.this.startActivityForResult(intent, 3238);

                    break;
                case R.id.btnShowSignature:
                    String photo = new String(CaseDetailsActivity.this.m_FieldValueList.get(index));
                    if (photo != null && photo.length() > 0) {
                        intent = new Intent(CaseDetailsActivity.this, ShowSignatureActivity.class);
                        intent.putExtra("index", index);
                        intent.putExtra("signatureString", photo);
                        CaseDetailsActivity.this.startActivityForResult(intent, 3237);
                    } else {
                        Toast.makeText(CaseDetailsActivity.this, "暂无签名图片", Toast.LENGTH_LONG).show();
                    }

                    break;
            }
            String str = "";
            for (int i = 0; i < bools.length; i++) {
                if (bools[i]) {
                    str += (i + 1);
                }
            }
            CaseDetailsActivity.this.m_FieldValueList.set(this.index, str);
        }

    }

    private void initDictItem(int i, Field localField, final int dataType, LinearLayout localLinearLayout,
                              int mDicType) {

        localSpinner = (Spinner) localLinearLayout.findViewById(R.id.sp_value);
        localSpinner2 = (Spinner) localLinearLayout.findViewById(R.id.city);

        if (mDicType != 123459) {// 是否是二级字典表
            localSpinner2.setVisibility(View.GONE);
        }

        final List<DictItem> al_dictItem = new DictItemDBMExternal(this).getDictItemsByType(mDicType);
        // 判断选择第几项
        int selIndex = 0;
        // 审核状态
        if (localField.getFieldId() == 4053 && mCaseObj.isUp()) {
            localField.setDefaultValue("审核中");
            al_dictItem.get(0).setItemValue("审核中");
            al_dictItem.get(0).setItemLabel("审核中");
        }
        for (int sI = 0; sI < al_dictItem.size(); sI++) {
            if (al_dictItem.get(sI).getItemValue().equals(localField.getDefaultValue())) {
                selIndex = sI;
                break;
            }
        }
        localLinearLayout.findViewById(R.id.vg_record_style_01).setVisibility(View.GONE);
        localLinearLayout.findViewById(R.id.vg_record_style_02).setVisibility(View.GONE);
        localLinearLayout.findViewById(R.id.vg_record_style_03).setVisibility(View.VISIBLE);

        final TextView localTextView2 = (TextView) localLinearLayout.findViewById(R.id.field_sp_value);
        TextChange localTextChange1 = new TextChange(i, dataType);
        localTextView2.addTextChangedListener(localTextChange1);
        DictItemAdapter localArrayAdapter = new DictItemAdapter(this, al_dictItem);
        localSpinner.setAdapter(localArrayAdapter);
        localSpinner.setSelection(selIndex);
        if (mCaseObj.isUp() == false) {
            localSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                public void onItemSelected(AdapterView<?> paramAdapterView, View paramView, int paramInt,
                                           long paramLong) {
                    int mParentId = al_dictItem.get(paramInt).getParentId();

                    if (mParentId >= 1 && mParentId <= 4) {
                        final List<DictItemTwo> al_dictItem2 = new DictItemDBMExternal(CaseDetailsActivity.this,
                                "tbTwoDictItem").getDictItemsByType2(mParentId);// 返回字典表集合
                        DictItemAdapter localArrayAdapter = new DictItemAdapter(CaseDetailsActivity.this, al_dictItem2);
                        localSpinner2.setAdapter(localArrayAdapter);

                        localSpinner2.setSelection(mPoint, true);// 设置spinner的默认值

                        mPoint = -2;// 还原默认值

                        localTextView2.setText(al_dictItem.get(paramInt).getItemValue());

                        final String text2 = al_dictItem.get(paramInt).getItemValue();
                        localSpinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                String localTextView3 = text2 + "&" + position;
                                localTextView2.setText(localTextView3);
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                            }
                        });

                    } else {
                        localTextView2.setText(al_dictItem.get(paramInt).getItemValue());
                    }

                }

                public void onNothingSelected(AdapterView<?> paramAdapterView) {
                }
            });
        } else {
            localSpinner.setEnabled(false);
        }
    }

    private void initFieldHintAndText(int i, Field localField, String str, String fieldCnname, final int dataType,
                                      LinearLayout localLinearLayout, TextView localTextView1, EditText localEditText2) {
        localLinearLayout.findViewById(R.id.vg_record_style_01).setVisibility(View.VISIBLE);
        localLinearLayout.findViewById(R.id.vg_record_style_02).setVisibility(View.GONE);
        localLinearLayout.findViewById(R.id.vg_record_style_03).setVisibility(View.GONE);

        if (this.mCaseObj.getCaseType().equals("dongwu")) {
            if (localField.getFieldCnname().equals("涉及物种")) {
                localEditText.setVisibility(View.GONE);
            }
            localEditText.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);

        }

        if (fieldCnname.indexOf("面积") > 0 && (!(fieldCnname.indexOf("面积") == 2))) {// 启高
            mwfmj2 = localEditText2;
            localEditText2.setText(localField.getDefaultValue());
            localEditText2.setEnabled(false);
            localEditText2.setVisibility(View.VISIBLE);
            localEditText.addTextChangedListener(new CaseDetailsActivity.TextChange(i, 39327));
        } else {
            localEditText2.setVisibility(View.GONE);
        }
        if ("null".equals(localField.getDefaultValue()) || "".equals(localField.getDefaultValue())) {
            localEditText.setHint("请输入" + localField.getFieldCnname());
        } else {
            localEditText.setText(localField.getDefaultValue());
        }
        localEditText.setInputType(InputType.TYPE_CLASS_TEXT);
        TextChange localTextChange1 = new TextChange(i, dataType);
        if (mCaseObj.isUp() == false) {
            localEditText.addTextChangedListener(localTextChange1);
        } else {
            localEditText.setEnabled(false);
        }
        if (str.equals("CASENAME")) {
            localEditText.setHint("请输入案件名称");
        } else if (str.equals("WFMJ")) {
            localTextView1.setText(localField.getFieldCnname() + "（㎡）");
            localEditText.setInputType(InputType.TYPE_CLASS_NUMBER + InputType.TYPE_NUMBER_FLAG_DECIMAL);
            localEditText.setEnabled(true);
        } else if (str.equals("MJ")) {
            localTextView1.setText(localField.getFieldCnname() + "（亩）");
            localEditText.setInputType(InputType.TYPE_CLASS_NUMBER + InputType.TYPE_NUMBER_FLAG_DECIMAL);
            localEditText.setEnabled(true);
        } else if (str.equals("ITEMXMMJ")) { // 启高 布局问题
            double d1 = MapOperate.GetArea() / 666.66666666699996D;
            localEditText.setText(String.format("%.4f", d1));
            localEditText.setInputType(InputType.TYPE_CLASS_NUMBER + InputType.TYPE_NUMBER_FLAG_DECIMAL);
            localEditText.setEnabled(true);
        } else if (str.equals("CFMJ_M")) { // 启高 布局问题
            double d1 = MapOperate.GetArea() / 666.66666666699996D;
            localEditText.setText(String.format("%.4f", d1));
            localEditText.setInputType(InputType.TYPE_CLASS_NUMBER + InputType.TYPE_NUMBER_FLAG_DECIMAL);
            localEditText.setEnabled(true);
        } else if (str.equals("CASEID")) {
            localEditText.setText(this.mCaseId);
            localEditText.setEnabled(false);
        } else if (str.equals("PARTID")) {
            localEditText.setEnabled(false);
        }
    }

    private void initMvideo(final String bizID, int i, Field localField, final int dataType,
                            LinearLayout localLinearLayout) {
        localLinearLayout.findViewById(R.id.vg_record_style_01).setVisibility(View.GONE);
        localLinearLayout.findViewById(R.id.vg_record_style_02).setVisibility(View.VISIBLE);
        localLinearLayout.findViewById(R.id.vg_record_style_03).setVisibility(View.GONE);
        ImageButton localImageButton1 = (ImageButton) localLinearLayout.findViewById(R.id.btn_value_setting);
        localImageButton1.setBackgroundResource(R.drawable.video_icon);
        TextView localTextView3 = (TextView) localLinearLayout.findViewById(R.id.field_value);
        localTextView3.setHint("请选择录像");
        localTextView3.setText(localField.getDefaultValue());
        TextChange localTextChange3 = new TextChange(i, dataType);
        localTextView3.addTextChangedListener(localTextChange3);
        localImageButton1.setTag(localTextView3);
        final int index2 = i;
        if (mCaseObj.isUp() == false) {
            localImageButton1.setOnClickListener(new View.OnClickListener() {
                public void onClick(View paramView) {
                    CaseDetailsActivity.this.mVideoTextView = ((TextView) paramView.getTag());
                    CaseDetailsActivity.this.mVideoTextView
                            .addTextChangedListener(new CaseDetailsActivity.TextChange(index2, 39323));
                    CaseDetailsActivity.this.fileSelectOpt_video(bizID);
                }
            });
        }
    }

    private void initMsound(final String bizID, int i, Field localField, final int dataType,
                            LinearLayout localLinearLayout) {
        localLinearLayout.findViewById(R.id.vg_record_style_01).setVisibility(View.GONE);
        localLinearLayout.findViewById(R.id.vg_record_style_02).setVisibility(View.VISIBLE);
        localLinearLayout.findViewById(R.id.vg_record_style_03).setVisibility(View.GONE);
        ImageButton localImageButton1 = (ImageButton) localLinearLayout.findViewById(R.id.btn_value_setting);
        localImageButton1.setBackgroundResource(R.drawable.sound_icon);
        TextView localTextView3 = (TextView) localLinearLayout.findViewById(R.id.field_value);
        localTextView3.setHint("请选择录音");
        localTextView3.setText(localField.getDefaultValue());
        TextChange localTextChange3 = new TextChange(i, dataType);
        localTextView3.addTextChangedListener(localTextChange3);
        localImageButton1.setTag(localTextView3);
        final int index2 = i;
        if (mCaseObj.isUp() == false) {
            localImageButton1.setOnClickListener(new View.OnClickListener() {
                public void onClick(View paramView) {
                    CaseDetailsActivity.this.mSoundTextView = ((TextView) paramView.getTag());
                    CaseDetailsActivity.this.mSoundTextView
                            .addTextChangedListener(new CaseDetailsActivity.TextChange(index2, 39322));
                    CaseDetailsActivity.this.fileSelectOpt_sound(bizID);
                }
            });
        }
    }

    private void initMphoto(final String bizID, int i, Field localField, final int dataType,
                            LinearLayout localLinearLayout) {
        localLinearLayout.findViewById(R.id.vg_record_style_01).setVisibility(View.GONE);
        localLinearLayout.findViewById(R.id.vg_record_style_02).setVisibility(View.VISIBLE);
        localLinearLayout.findViewById(R.id.vg_record_style_03).setVisibility(View.GONE);
        ImageButton localImageButton1 = (ImageButton) localLinearLayout.findViewById(R.id.btn_value_setting);
        localImageButton1.setBackgroundResource(R.drawable.camera_icon);
        TextView localTextView3 = (TextView) localLinearLayout.findViewById(R.id.field_value);
        localTextView3.setHint("请选择图片");
        localTextView3.setText(localField.getDefaultValue());
        TextChange localTextChange3 = new TextChange(i, dataType);
        localTextView3.addTextChangedListener(localTextChange3);
        localImageButton1.setTag(localTextView3);
        final int index2 = i;
        if (mCaseObj.isUp() == false) {
            localImageButton1.setOnClickListener(new View.OnClickListener() {
                public void onClick(View paramView) {
                    CaseDetailsActivity.this.mPhotoTextView = ((TextView) paramView.getTag());
                    CaseDetailsActivity.this.mPhotoTextView
                            .addTextChangedListener(new CaseDetailsActivity.TextChange(index2, 39321));
                    CaseDetailsActivity.this.fileSelectOpt_photo(bizID);
                }
            });
        }
    }

    private void initDataType6_time(int i, Field localField, final int dataType, LinearLayout localLinearLayout) {
        localLinearLayout.findViewById(R.id.vg_record_style_01).setVisibility(View.GONE);
        localLinearLayout.findViewById(R.id.vg_record_style_02).setVisibility(View.VISIBLE);
        localLinearLayout.findViewById(R.id.vg_record_style_03).setVisibility(View.GONE);
        ImageButton localImageButton2 = (ImageButton) localLinearLayout.findViewById(R.id.btn_value_setting);
        localImageButton2.setBackgroundResource(R.drawable.time);
        final TextView localTextView4 = (TextView) localLinearLayout.findViewById(R.id.field_value);
        TextChange localTextChange4 = new TextChange(i, dataType);
        localTextView4.addTextChangedListener(localTextChange4);
        localTextView4.setText(localField.getDefaultValue());
        localImageButton2.setTag(localTextView4);
        if (mCaseObj.isUp() == false) {
            localImageButton2.setOnClickListener(new View.OnClickListener() {
                public void onClick(View paramView) {
                    View view = ((LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE))
                            .inflate(R.layout.time_picker, null);
                    Calendar calendar = Calendar.getInstance();
                    int curYear = calendar.get(Calendar.YEAR);
                    int curMonth = calendar.get(Calendar.MONTH) + 1;
                    int curDay = calendar.get(Calendar.DAY_OF_MONTH);
                    int curHour = calendar.get(Calendar.HOUR_OF_DAY);
                    int curMinute = calendar.get(Calendar.MINUTE);
                    int curSecond = calendar.get(Calendar.SECOND);
                    if ("".equals(("" + localTextView4.getText()).trim())) {
                        calendar.setTime(new Date());
                    } else {
                        Date date = UtilsTools.FormateStringToDate("" + localTextView4.getText(),
                                "yyyy-MM-dd HH:mm:ss");
                        calendar.setTime(date);
                    }
                    curYear = calendar.get(Calendar.YEAR);
                    curMonth = calendar.get(Calendar.MONTH) + 1;
                    curDay = calendar.get(Calendar.DAY_OF_MONTH);
                    curHour = calendar.get(Calendar.HOUR_OF_DAY);
                    curMinute = calendar.get(Calendar.MINUTE);
                    curSecond = calendar.get(Calendar.SECOND);

                    yearWheel = (WheelView) view.findViewById(R.id.yearwheel);
                    monthWheel = (WheelView) view.findViewById(R.id.monthwheel);
                    dayWheel = (WheelView) view.findViewById(R.id.daywheel);
                    hourWheel = (WheelView) view.findViewById(R.id.hourwheel);
                    minuteWheel = (WheelView) view.findViewById(R.id.minutewheel);
                    secondWheel = (WheelView) view.findViewById(R.id.secondwheel);

                    AlertDialog.Builder builder = new AlertDialog.Builder(CaseDetailsActivity.this);
                    builder.setView(view);

                    yearWheel.setAdapter(new StrericWheelAdapter(yearContent));
                    yearWheel.setCurrentItem(curYear - 2013);
                    yearWheel.setCyclic(true);
                    yearWheel.setInterpolator(new AnticipateOvershootInterpolator());

                    monthWheel.setAdapter(new StrericWheelAdapter(monthContent));

                    monthWheel.setCurrentItem(curMonth - 1);

                    monthWheel.setCyclic(true);
                    monthWheel.setInterpolator(new AnticipateOvershootInterpolator());

                    dayWheel.setAdapter(new StrericWheelAdapter(dayContent));
                    dayWheel.setCurrentItem(curDay - 1);
                    dayWheel.setCyclic(true);
                    dayWheel.setInterpolator(new AnticipateOvershootInterpolator());

                    hourWheel.setAdapter(new StrericWheelAdapter(hourContent));
                    hourWheel.setCurrentItem(curHour);
                    hourWheel.setCyclic(true);
                    hourWheel.setInterpolator(new AnticipateOvershootInterpolator());

                    minuteWheel.setAdapter(new StrericWheelAdapter(minuteContent));
                    minuteWheel.setCurrentItem(curMinute);
                    minuteWheel.setCyclic(true);
                    minuteWheel.setInterpolator(new AnticipateOvershootInterpolator());

                    secondWheel.setAdapter(new StrericWheelAdapter(secondContent));
                    secondWheel.setCurrentItem(curSecond);
                    secondWheel.setCyclic(true);
                    secondWheel.setInterpolator(new AnticipateOvershootInterpolator());

                    builder.setTitle("选取时间");
                    builder.setPositiveButton("确  定", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            StringBuffer sb = new StringBuffer();
                            sb.append(yearWheel.getCurrentItemValue()).append("-")
                                    .append(monthWheel.getCurrentItemValue()).append("-")
                                    .append(dayWheel.getCurrentItemValue());

                            sb.append(" ");
                            sb.append(hourWheel.getCurrentItemValue()).append(":")
                                    .append(minuteWheel.getCurrentItemValue()).append(":")
                                    .append(secondWheel.getCurrentItemValue());
                            localTextView4.setText(sb);
                            dialog.cancel();
                        }
                    });

                    builder.show();
                }
            });
        }
    }

    private void fileSelectOpt_photo(String bizID) {
        Intent localIntent = new Intent(this, PhotoSelectActivity.class);
        localIntent.putExtra("bizId", bizID);
        localIntent.putExtra("caseId", this.mCaseId);
        startActivityForResult(localIntent, 3233);
    }

    private void fileSelectOpt_sound(String bizID) {
        Intent localIntent = new Intent(this, SoundSelectActivity.class);
        localIntent.putExtra("bizId", bizID);
        localIntent.putExtra("caseId", this.mCaseId);
        startActivityForResult(localIntent, 3234);
    }

    private void fileSelectOpt_video(String bizID) {
        Intent localIntent = new Intent(this, VideoSelectActivity.class);
        localIntent.putExtra("bizId", bizID);
        localIntent.putExtra("caseId", this.mCaseId);
        startActivityForResult(localIntent, 3235);
    }

    private class TextChange implements TextWatcher {
        private int nIndex = -1;
        private int nType = 0;

        public TextChange(int paramInt1, int fieldType) {
            this.nIndex = paramInt1;
            this.nType = fieldType;
        }

        public void afterTextChanged(Editable paramEditable) {
            if (this.nType == 1) {
                CaseDetailsActivity.this.m_FieldValueList.set(this.nIndex, paramEditable.toString().trim());
                return;
            }
            // 处理选择图片
            if (this.nType == 39321) {
                if (CaseDetailsActivity.this.mImgList != null) {
                    String str1 = "";
                    Iterator<String> localIterator = CaseDetailsActivity.this.mImgList.iterator();
                    while (localIterator.hasNext()) {
                        String str2 = localIterator.next();
                        str1 = str1 + str2 + ",";
                    }
                    str1 = str1.substring(0, -1 + str1.length());
                    CaseDetailsActivity.this.m_FieldValueList.set(this.nIndex, str1);

                }
            } // 处理选择录音
            else if (this.nType == 39322) {
                if (CaseDetailsActivity.this.mSoundList != null) {
                    String str1 = "";
                    Iterator<String> localIterator = CaseDetailsActivity.this.mSoundList.iterator();
                    while (localIterator.hasNext()) {
                        String str2 = localIterator.next();
                        str1 = str1 + str2 + ",";
                    }
                    str1 = str1.substring(0, -1 + str1.length());
                    CaseDetailsActivity.this.m_FieldValueList.set(this.nIndex, str1);
                }
            }
            // 处理选择录像
            else if (this.nType == 39323) {
                if (CaseDetailsActivity.this.mVideoList != null) {
                    String str1 = "";
                    Iterator<String> localIterator = CaseDetailsActivity.this.mVideoList.iterator();
                    while (localIterator.hasNext()) {
                        String str2 = localIterator.next();
                        str1 = str1 + str2 + ",";
                    }
                    str1 = str1.substring(0, -1 + str1.length());
                    CaseDetailsActivity.this.m_FieldValueList.set(this.nIndex, str1);
                }
            } else if (this.nType == 39327) {
                if (paramEditable.toString().trim().length() >= 1) {
                    String name = paramEditable.toString().trim();
                    Pattern p = Pattern.compile("^ [ + ] ?(0\\.\\d+) |([1-9][0-9]*(\\.\\d+)?)$");
                    if (p.matcher(name).matches()) {
                        Double mj = Double.parseDouble(paramEditable.toString().trim()) / 666.66666666699996D;
                        mwfmj2.setText(String.format("%.4f", mj) + "(亩)");
                    } else {
                        mwfmj2.setHint("请输入数字！");
                        mwfmj2.setText("");
                    }
                } else {
                    mwfmj2.setText(0 + "(亩)");
                }
            } else {
                CaseDetailsActivity.this.m_FieldValueList.set(this.nIndex, paramEditable.toString().trim());

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
            case 3236:
                if (resultCode == 1) {// 上传成功后
                    mUpButton.setEnabled(false);
                    findViewById(R.id.case_details_save_data).setEnabled(false);
                    mUpButton.setText("已上报");
                }

                break;
            case 3233:
                if (resultCode == -1) {
                    String[] arrayOfString = data.getStringArrayExtra("imgs");
                    this.mImgList.clear();
                    for (int j = 0; j < arrayOfString.length; j++) {

                        String str = arrayOfString[j];
                        this.mImgList.add(str);
                    }
                    if ((this.mImgList.size() > 0) && (this.mPhotoTextView != null)) {

                        this.mPhotoTextView.setText("已选择" + this.mImgList.size() + "张图片");
                    }
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
                    if ((this.mSoundList.size() > 0) && (this.mSoundTextView != null)) {

                        this.mSoundTextView.setText("已选择" + this.mSoundList.size() + "个录音");
                    }
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
                    if ((this.mVideoList.size() > 0) && (this.mVideoTextView != null)) {

                        this.mVideoTextView.setText("已选择" + this.mVideoList.size() + "个录像");
                    }
                }
                break;
            case 3238:
                int index = data.getIntExtra("index", 0);
                CaseDetailsActivity.this.m_FieldValueList.set(index, data.getStringExtra("signatureString"));
                saveData();
                break;
            case 3237:
                int index2 = data.getIntExtra("index", 0);
                CaseDetailsActivity.this.m_FieldValueList.set(index2, data.getStringExtra("signatureString"));

                break;
//            case 3233:
//                break;
//            case 3233:
//                break;
        }


        super.onActivityResult(requestCode, resultCode, data);

    }

    private class MOnClickListener implements View.OnClickListener {
        public void onClick(View paramView) {
            if (paramView.getId() == R.id.btn_case_imgs) {
                Intent localIntent = new Intent(CaseDetailsActivity.this, ImgViewActivity.class);
                localIntent.putExtra("caseId", CaseDetailsActivity.this.mCaseObj.getCaseId());
                CaseDetailsActivity.this.startActivity(localIntent);
            } else if (paramView.getId() == R.id.btn_case_sounds) {
                Intent localIntent = new Intent(CaseDetailsActivity.this, SoundViewActivity.class);
                localIntent.putExtra("caseId", CaseDetailsActivity.this.mCaseObj.getCaseId());
                CaseDetailsActivity.this.startActivity(localIntent);
            } else if (paramView.getId() == R.id.btn_case_videos) {
                Intent localIntent = new Intent(CaseDetailsActivity.this, VideoViewActivity.class);
                localIntent.putExtra("caseId", CaseDetailsActivity.this.mCaseObj.getCaseId());
                CaseDetailsActivity.this.startActivity(localIntent);
            } else if (paramView.getId() == R.id.case_details_rb_analysis) {
                // 占地分析
                if (CaseDetailsActivity.this.mCaseObj.getGeos() == null) {
                    ToastUtils.showLong("无地块信息");
                } else {
                    Intent localIntent = new Intent(CaseDetailsActivity.this, MainActivity.class);
                    localIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    CaseDetailsActivity.this.startActivity(localIntent);
                    JsonFactory jsonFactory = new JsonFactory();
                    JsonParser jsonParser;
                    try {
                        jsonParser = jsonFactory.createJsonParser(CaseDetailsActivity.this.mCaseObj.getGeos());
                        MapGeometry mapGeo = GeometryEngine.jsonToGeometry(jsonParser);
                        Geometry geo = mapGeo.getGeometry();
                        // 先简化下图形，解决逆向绘制的图形分析不出来结果
                        geo = GeometryEngine.simplify(geo, MapOperate.map.getSpatialReference());
                        MapOperate.geoAnalysisTool.analysisGeometry((Polygon) geo, 1);
                    } catch (JsonParseException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else if (paramView.getId() == R.id.btn_insert) {
                insertWuZhong();
            } else if (paramView.getId() == R.id.btn_keep) {
                saveData();
            }
        }
    }

    /**
     * 地图定位
     *
     * @param paramView
     */
    public void caseLocationOnClick(View paramView) {
        MapOperate.editCaseGeometry(this.mCaseObj);
        Intent localIntent = new Intent(this, MainActivity.class);
        localIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        localIntent.putExtra("opt", 4367);
        this.startActivity(localIntent);
    }

    public void insertWuZhong() {

        tableLayout.setStretchAllColumns(true);
        for (int row = 0; row < 1; row++) {
            TableRow tableRow = new TableRow(CaseDetailsActivity.this);
            tableRow.setBackgroundColor(Color.rgb(222, 220, 210));
            for (int col = 0; col < 5; col++) {
                if (col < 4) {
                    EditText et_write = new EditText(CaseDetailsActivity.this);
                    et_write.setBackgroundResource(R.drawable.shappe);
                    tableRow.addView(et_write);
                } else {
                    // 换成删除按钮
                    Button btnDel = new Button(CaseDetailsActivity.this);
                    btnDel.setText("删除");
                    btnDel.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {
                            TableRow tableRow = (TableRow) view.getParent();
                            tableLayout.removeView(tableRow);
                        }

                    });
                    tableRow.addView(btnDel);
                }
            }
            tableLayout.addView(tableRow, new TableLayout.LayoutParams(FP, WC));
        }

    }

    public void updateCaseObj() {
        this.mCaseObj = new CaseDBMExternal(this).getCaseByIdAndType(mCaseObj.getCaseId(), mCaseObj.getCaseType());
    }

    /**
     * 保存任务结果
     *
     * @param paramView
     */
    public void saveCaseEditInfo(View paramView) {
        saveData();
    }

    private void saveData() {
        try {
            if ("dongwu".equals(this.mCaseObj.getCaseType())) {
                keepdongwuData();// 保存涉及物种的数据
            }

            JSONObject jsonObj = new JSONObject(mCaseObj.getAttrs());
            for (int i = 0; i < m_lstFieldInfos.size(); i++) {
                jsonObj.put(this.m_lstFieldInfos.get(i).getFieldName(), this.m_FieldValueList.get(i));
            }
            System.out.println("------------保存新的属性：" + jsonObj.toString());
            CaseDetailsActivity.this.mCaseObj.setAttrs(jsonObj.toString());
            new CaseDBMExternal(this).updateCaseAttrs(mCaseObj.getCaseId(), m_FieldValueList.get(2),
                    jsonObj.toString());
            ToastUtils.showLong("保存成功！");
        } catch (JSONException e) {
            e.printStackTrace();
            ToastUtils.showLong("保存失败！");
        }
    }

    private void keepdongwuData() {

        if (!(table_data.isEmpty())) {
            table_data = "";
        }
        TableRow tableRow3 = new TableRow(CaseDetailsActivity.this);
        EditText et_write3 = new EditText(CaseDetailsActivity.this);
        // tableLayout.getChildCount();//获取TableLayout的行数
        /*
         * if (tableLayout.getChildCount() > 0) { for (int sum = 0; sum <
         * tableLayout.getChildCount(); sum++) { tableRow3 = (TableRow)
         * tableLayout.getChildAt(sum); // tableRow3.getChildCount();获取列总数 for (int line
         * = 0; line < 4; line++) { et_write3 = (EditText) tableRow3.getChildAt(line);
         * if (line == 3) { table_data += et_write3.getText().toString(); } else {
         * table_data += et_write3.getText().toString() + "&"; } } // table_data += "&";
         * } }
         */
        List<Map<String, String>> list = new ArrayList();
        Map<String, String> map;
        if (tableLayout.getChildCount() > 0) {
            for (int sum = 0; sum < tableLayout.getChildCount(); sum++) {
                tableRow3 = (TableRow) tableLayout.getChildAt(sum);
                // tableRow3.getChildCount();获取列总数
                map = new HashMap();
                for (int line = 0; line < keys.length; line++) {
                    et_write3 = (EditText) tableRow3.getChildAt(line);
                    map.put(keys[line], et_write3.getText().toString());
                }
                list.add(map);
            }
        }
        table_data = GsonUtil.listToJson(list);
        localEditText.setText(table_data);

    }

    /**
     * 返回键
     *
     * @param paramView
     */
    public void backOnClick(View paramView) {
        // 启高
        Intent localIntent = new Intent(this, CaseManagerActivity.class);
        localIntent.putExtra("caseType", this.mCaseObj.getCaseType());
        localIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(localIntent);
        finish();
    }
}