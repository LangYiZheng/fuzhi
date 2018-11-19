package com.guyu.android.gis.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
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

import com.esri.core.geometry.GeometryEngine;
import com.guyu.android.R;
import com.guyu.android.database.sync.DictItemDBMExternal;
import com.guyu.android.database.sync.FieldDBMExternal;
import com.guyu.android.database.task.CaseDBMExternal;
import com.guyu.android.gis.adapter.DictItemAdapter;
import com.guyu.android.gis.app.GisQueryApplication;
import com.guyu.android.gis.common.DictItem;
import com.guyu.android.gis.common.DictItemTwo;
import com.guyu.android.gis.common.Field;
import com.guyu.android.gis.common.IllegalChange;
import com.guyu.android.gis.common.IllegalClues;
import com.guyu.android.gis.common.IllegalDongWu;
import com.guyu.android.gis.common.IllegalLand;
import com.guyu.android.gis.common.IllegalLinMu;
import com.guyu.android.gis.common.IllegalLinXia;
import com.guyu.android.gis.common.IllegalMuCai;
import com.guyu.android.gis.common.IllegalWeiFaOne;
import com.guyu.android.gis.opt.MapOperate;
import com.guyu.android.utils.GsonUtil;
import com.guyu.android.utils.SysConfig;
import com.guyu.android.utils.UtilsTools;
import com.guyu.android.widget.wheel.StrericWheelAdapter;
import com.guyu.android.widget.wheel.WheelView;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class InitializeRecordActivity extends Activity {
    private static String TAG = "InitializeRecordActivity";
    static final int DATE_DIALOG_ID = 1;
    private int mOptType = 0;
    private String mCaseId = null;
    public LayoutInflater layoutInflater = null;
    private LinearLayout mLayout;
    private TextView mPhotoTextView = null;
    private TextView mSoundTextView = null;
    private TextView mVideoTextView = null;

    private RadioButton btn_level, btn_approval;

    private Spinner localSpinner = null;
    private Spinner localSpinner2 = null;
    private LinearLayout ll_btn = null;
    private LinearLayout include_table = null;
    private final int WC = ViewGroup.LayoutParams.WRAP_CONTENT;
    private final int FP = ViewGroup.LayoutParams.MATCH_PARENT;
    private TableLayout tableLayout;
    private String table_data = "";// TableLayout的数据
    private EditText localEditText;

    private List<Field> mDefaultFieldList = new ArrayList<Field>();
    private List<String> m_FieldValueList = new ArrayList<String>();
    private List<Field> m_lstFieldInfos = new ArrayList<Field>();
    private List<String> mImgList = new ArrayList<String>();
    private List<String> mSoundList = new ArrayList<String>();
    private List<String> mVideoList = new ArrayList<String>();
    private View.OnClickListener btnOnClickListener = null;
    private Map<String, String> caseMap = null;
    // 案件类型
    private String caseType;

    private WheelView yearWheel, monthWheel, dayWheel, hourWheel, minuteWheel, secondWheel;
    public static String[] yearContent = null;
    public static String[] monthContent = null;
    public static String[] dayContent = null;
    public static String[] hourContent = null;
    public static String[] minuteContent = null;
    public static String[] secondContent = null;

    private TextView text_title;

    private String attrs = "";

    private String[] keys = {"ZWM", "LDXW", "WGBHJB", "GYBHJB"};
    //多选
    private boolean[] bools = new boolean[4];
    private RadioButton[] btns = new RadioButton[4];

    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        Intent localIntent = getIntent();
        this.caseType = localIntent.getStringExtra("caseType");
        init();

    }

    private void init() {
        setContentView(R.layout.activity_initialize_record);


        // 加载布局管理器
        layoutInflater = LayoutInflater.from(this);

        // 启高
        ll_btn = (LinearLayout) findViewById(R.id.ll_btn);
        include_table = (LinearLayout) findViewById(R.id.include_table);

        tableLayout = (TableLayout) findViewById(R.id.tl_table);

        text_title = (TextView) findViewById(R.id.text_title);
        text_title.setVisibility(View.VISIBLE);

        this.mLayout = ((LinearLayout) findViewById(R.id.ll_layout));
        // 设置表单头部信息
        setCaseTypeTitle();

        this.caseMap = new HashMap<String, String>();
        // 获取业务所有字段
        setBizDefaultFieldList();

        for (int i = 0; i < m_lstFieldInfos.size(); i++) {
            Field f = InitializeRecordActivity.this.m_lstFieldInfos.get(i);
            m_FieldValueList.add(f.getDefaultValue() == null ? "" : f.getDefaultValue());// 布局编辑框默认值
        }

        // 初始化时间选择器
        initTimePickerContent();
        // 初始化字段的默认值
        initActivity();
        // 绑定按钮事件
        this.btnOnClickListener = new BtnOnClickListener();
        findViewById(R.id.btn_ok).setOnClickListener(this.btnOnClickListener);
        findViewById(R.id.btn_cancel).setOnClickListener(this.btnOnClickListener);

        btn_level = (RadioButton) findViewById(R.id.btn_level);
        btn_approval = (RadioButton) findViewById(R.id.btn_approval);
        btn_level.setOnClickListener(this.btnOnClickListener);
        btn_approval.setOnClickListener(this.btnOnClickListener);
        findViewById(R.id.btn_insert).setOnClickListener(this.btnOnClickListener);
        findViewById(R.id.btn_keep).setOnClickListener(this.btnOnClickListener);

    }

    // 设置业务表单表头信息
    private void setCaseTypeTitle() {
        if (caseType.equals("change")) {
            text_title.setText("林权、林木和林地使用权流转、变更登记申请表");
        } else if (caseType.equals("linxia")) {
            text_title.setText("广东省林下经济项目基本信息");
        } else if (caseType.equals("linmu")) {
            text_title.setText("林木采伐基本信息");
        } else if (caseType.equals("mucai")) {
            text_title.setText("木材检查基本信息");
        } else if (caseType.equals("weifa")) {
            ll_btn.setVisibility(View.GONE);
            text_title.setText("林业行政处罚立案登记表");
        } else if (caseType.equals("weifa_approval")) {
            ll_btn.setVisibility(View.VISIBLE);
            text_title.setText("林业行政处罚立案登记表");
        } else if (caseType.equals("dongwu")) {
            include_table.setVisibility(View.VISIBLE);
            text_title.setText("野生动植物养殖申请表");
        } else if (caseType.equals("huatu")) {
            include_table.setVisibility(View.VISIBLE);
            text_title.setText("任务采集表");
        }
    }


    // 获取所有业务字段
    public void setBizDefaultFieldList() {
        // 从数据库中查询出当前表单配置的页面字段
        List<Field> fList = new FieldDBMExternal(this).getFieldList(" casetype = ? ", new String[]{this.caseType},
                " disporder asc ");
        mDefaultFieldList.clear();

        // 加载所有的字段
        mDefaultFieldList.addAll(fList);

        // XSLY
        mDefaultFieldList.add(new Field("MPHOTO", "图片", "已选择0张图片"));// 设置布局中的“图片”字段
        mDefaultFieldList.add(new Field("MSOUND", "音频", "已选择0个录音"));// 设置布局中的“音频”字段
        mDefaultFieldList.add(new Field("MVIDEO", "视频", "已选择0个录像"));// 设置布局中的“视频”字段
        // 特殊字段
        if (caseType.equals("dongwu")) {
            mDefaultFieldList.add(new Field("DONGWU", "涉及物种", "涉及物种数据"));
        }
        m_lstFieldInfos.clear();
        m_lstFieldInfos.addAll(mDefaultFieldList);

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

    private void argsInit() {
        Intent localIntent = getIntent();
        this.mOptType = localIntent.getIntExtra("optType", 0);

    }

    // 设置字段默认值
    private void initActivity() {
        argsInit();// 设置 activity的类型 直接点击进入可编辑的状态
        if (this.mOptType == 0) {
            initiList();
        } else {
            initEidtList();
        }
    }

    private void initEidtList() {
    }

    // 初始化字段显示样式与设置默认值
    private void initiList() {
        final String bizID = GisQueryApplication.getApp().getProjectconfig().getCaseUploadsConfig().getCaseUploadConfigMap()
                .get(this.caseType).getBizId();
        for (int i = 0; i < this.m_lstFieldInfos.size(); i++) {
            final Field localField = this.m_lstFieldInfos.get(i);
            String str = localField.getFieldName();
            String fieldCnname = localField.getFieldCnname();
            final int dataType = localField.getDataType();
            LinearLayout localLinearLayout = (LinearLayout) layoutInflater.inflate(R.layout.record_field_style, null)
                    .findViewById(R.id.record_item);// //record_item_edit
            localLinearLayout.setTag(str);
            TextView localTextView1 = (TextView) localLinearLayout.findViewById(R.id.attribute_name);
            localTextView1.setTextSize(16.0F);

            localTextView1.setText(localField.getFieldCnname());
            if (dataType == 12) {
                initButton(i, localLinearLayout);
            }
            // 多选
            else if (dataType == 11) {
                initRadioButton(i, localLinearLayout);
            }
            // 时间类型
            else if (dataType == 6) {
                initDataType6_time(i, dataType, localLinearLayout);
            } else if (str.startsWith("MPHOTO")) {// 图片
                initMphoto(bizID, i, dataType, localLinearLayout);
            } else if (str.startsWith("MSOUND")) {// 录音
                initMsound(bizID, i, dataType, localLinearLayout);
            } else if (str.startsWith("MVIDEO")) {// 录像
                initMvideo(bizID, i, dataType, localLinearLayout);
            } else {// 其他类型
                // 加载下拉菜单 步骤：
                // 1.需改dataType的值为合适的值，修改tbField(布局的左边字段)表的dicType不为0，使之把下拉框显示出来，把其他布局隐藏
                // 2.把tbDictItem添加数据。其中dictId为上一步dicType的值

                localEditText = (EditText) localLinearLayout.findViewById(R.id.attribute_value);
                EditText localEditText2 = (EditText) localLinearLayout.findViewById(R.id.attribute_value2);
                // 判断字段是否是字典
                int mDicType = localField.getDictType();// dictType;//判断是否为字典
                if (mDicType != 0) {
                    initDictItem(i, dataType, localLinearLayout, mDicType);
                } else {// 非字段 包括文本 数字的处理
                    initFieldHintAndText(i, localField, str, fieldCnname, dataType, localLinearLayout, localTextView1,
                            localEditText2);
                }
            }
            LinearLayout.LayoutParams localLayoutParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
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
                    intent = new Intent(InitializeRecordActivity.this, SignaturePadActivity.class);

                    intent.putExtra("index", index);
                    InitializeRecordActivity.this.startActivityForResult(intent, 3236);

                    break;
                case R.id.btnShowSignature:
                    String photo = new String(InitializeRecordActivity.this.m_FieldValueList.get(index));
                    if (photo != null && photo.length() > 0) {
                        intent = new Intent(InitializeRecordActivity.this, ShowSignatureActivity.class);
                        intent.putExtra("index", index);
                        intent.putExtra("signatureString", photo);
                        InitializeRecordActivity.this.startActivityForResult(intent, 3237);
                    } else {
                        Toast.makeText(InitializeRecordActivity.this, "暂无签名图片", Toast.LENGTH_LONG).show();
                    }

                    break;
            }
            String str = "";
            for (int i = 0; i < bools.length; i++) {
                if (bools[i]) {
                    str += (i + 1);
                }
            }
            InitializeRecordActivity.this.m_FieldValueList.set(this.index, str);
        }

    }

    // 处理文本与数字字段
    private void initFieldHintAndText(int i, final Field localField, String str, String fieldCnname, final int dataType,
                                      LinearLayout localLinearLayout, TextView localTextView1, EditText localEditText2) {
        localLinearLayout.findViewById(R.id.vg_record_style_01).setVisibility(View.VISIBLE);
        localLinearLayout.findViewById(R.id.vg_record_style_02).setVisibility(View.GONE);
        localLinearLayout.findViewById(R.id.vg_record_style_03).setVisibility(View.GONE);

        // 特殊处理字典 开始
        if (caseType.equals("dongwu") // 启高
                ) {
            if (localField.getFieldCnname().equals("涉及物种")) {
                localEditText.setVisibility(View.GONE);
            }
            localEditText.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);

        }
        if (fieldCnname.indexOf("面积") > 0 && (!(fieldCnname.indexOf("面积") == 2))) {// 启高
            if ("null".equals(String.valueOf(MapOperate.GetArea()))
                    || "".equals(String.valueOf(MapOperate.GetArea()))) {
                localEditText2.setText(0.00 + "(亩)");
                localEditText2.setEnabled(false);
            } else {
                double d1 = MapOperate.GetArea() / 666.66666666699996D;
                localEditText2.setText(String.format("%.4f", d1) + "(亩)");
                localEditText2.setEnabled(false);
            }
            localEditText2.setVisibility(View.VISIBLE);
        } else {
            localEditText2.setVisibility(View.GONE);
        }

        // 设置hint 没有默认值的时候就设置成hint的值
        if (StringUtils.isEmpty(localField.getDefaultValue())) {
            localEditText.setText(localField.getDefaultValue());
        } else {
            localEditText.setHint(localField.getHintValue());
        }

        TextChange localTextChange1 = new TextChange(i, dataType);
        localEditText.addTextChangedListener(localTextChange1);
        localEditText.setInputType(InputType.TYPE_CLASS_TEXT);

        if (str.equals("CASENAME")) {
            localEditText.setHint("请输入案件名称");
            localEditText.requestFocus();
        } else if (str.equals("WFMJ")) {
            double d2 = MapOperate.GetArea();
            localEditText.setText(String.format("%.4f", d2));
            localEditText.setInputType(InputType.TYPE_CLASS_NUMBER + InputType.TYPE_NUMBER_FLAG_DECIMAL);
            localEditText.setEnabled(true);
            localTextView1.setText("违法面积（㎡）");
        } else if (str.equals("MJ")) {
            double d1 = MapOperate.GetArea() / 666.66666666699996D;
            localEditText.setText(String.format("%.4f", d1));
            localEditText.setInputType(InputType.TYPE_CLASS_NUMBER + InputType.TYPE_NUMBER_FLAG_DECIMAL);
            localEditText.setEnabled(true);
            localTextView1.setText("面积（亩）");
        } else if (str.equals("ITEMXMMJ")) { // 启高 布局问题
            double d1 = MapOperate.GetArea() / 666.66666666699996D;
            localEditText.setText(String.format("%.4f", d1));
            localEditText.setEnabled(true);
            localEditText.setInputType(InputType.TYPE_CLASS_NUMBER + InputType.TYPE_NUMBER_FLAG_DECIMAL);
        } else if (str.equals("CFMJ_M")) { // 启高 布局问题
            double d1 = MapOperate.GetArea() / 666.66666666699996D;
            localEditText.setText(String.format("%.4f", d1));
            localEditText.setEnabled(true);
            localEditText.setInputType(InputType.TYPE_CLASS_NUMBER + InputType.TYPE_NUMBER_FLAG_DECIMAL);
        } else if (str.equals("CASEID")) {
            this.mCaseId = MapOperate.CreateNewCaseId();
            localEditText.setText(this.mCaseId);
            localEditText.setEnabled(false);
        } // 启高 重点 YSQBH：原申请编号 。hint属性就是设置text默认为空时的提示信息
        else if (str.equals("YSQBH")) {
            this.mCaseId = MapOperate.CreateNewCaseId();
            localEditText.setText(this.mCaseId);
            localEditText.setEnabled(true); // 设置是否可编辑
        } else if (str.equals("XMMC")) {
            this.mCaseId = MapOperate.CreateNewCaseId();
            localEditText.setText(this.mCaseId);
            localEditText.setEnabled(true); // 设置是否可编辑
        } else if (str.equals("SQBH_M")) {
            this.mCaseId = MapOperate.CreateNewCaseId();
            localEditText.setText(this.mCaseId);
            localEditText.setEnabled(true); // 设置是否可编辑
        } else if (str.equals("BH_MC")) {
            this.mCaseId = MapOperate.CreateNewCaseId();
            localEditText.setText(this.mCaseId);
            localEditText.setEnabled(true); // 设置是否可编辑
        } else if (str.equals("LAWH_WF")) {
            this.mCaseId = MapOperate.CreateNewCaseId();
            localEditText.setText(this.mCaseId);
            localEditText.setEnabled(true); // 设置是否可编辑
        } else if (str.equals("SQDW_DW")) {
            this.mCaseId = MapOperate.CreateNewCaseId();
            localEditText.setText(this.mCaseId);
            localEditText.setEnabled(true); // 设置是否可编辑
        } else if (str.equals("PARTID")) {
            localEditText.setText("" + SysConfig.mHumanInfo.getHumanId());
            localEditText.setEnabled(false);
        }
    }

    private void initDictItem(int i, final int dataType, LinearLayout localLinearLayout, int mDicType) {
        localSpinner = (Spinner) localLinearLayout.findViewById(R.id.sp_value);
        localSpinner2 = (Spinner) localLinearLayout.findViewById(R.id.city);

        final List<DictItem> al_dictItem = new DictItemDBMExternal(this).getDictItemsByType(mDicType);// 返回字典表集合

        if (mDicType != 123459) {// 是否是二级字典表
            localSpinner2.setVisibility(View.GONE);
        }

        // 布局是否可见
        localLinearLayout.findViewById(R.id.vg_record_style_01).setVisibility(View.GONE);
        localLinearLayout.findViewById(R.id.vg_record_style_02).setVisibility(View.GONE);
        localLinearLayout.findViewById(R.id.vg_record_style_03).setVisibility(View.VISIBLE);

        final TextView localTextView2 = (TextView) localLinearLayout // 设置spinner数据
                .findViewById(R.id.field_sp_value);
        TextChange localTextChange1 = new TextChange(i, dataType);
        localTextView2.addTextChangedListener(localTextChange1);

        DictItemAdapter localArrayAdapter = new DictItemAdapter(this, al_dictItem);
        localSpinner.setAdapter(localArrayAdapter);

        localSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> paramAdapterView, View paramView, int paramInt, long paramLong) {
                int mParentId = al_dictItem.get(paramInt).getParentId();

                if (mParentId >= 1 && mParentId <= 4) {
                    final List<DictItemTwo> al_dictItem2 = new DictItemDBMExternal(InitializeRecordActivity.this,
                            "tbTwoDictItem").getDictItemsByType2(mParentId);// 返回字典表集合
                    DictItemAdapter localArrayAdapter = new DictItemAdapter(InitializeRecordActivity.this,
                            al_dictItem2);
                    localSpinner2.setAdapter(localArrayAdapter);

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
                            // TODO Auto-generated
                            // method stub
                        }
                    });

                } else {
                    localTextView2.setText(al_dictItem.get(paramInt).getItemValue());
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }

        });
    }

    private void initMvideo(final String bizID, int i, final int dataType, LinearLayout localLinearLayout) {
        localLinearLayout.findViewById(R.id.vg_record_style_01).setVisibility(View.GONE);
        localLinearLayout.findViewById(R.id.vg_record_style_02).setVisibility(View.VISIBLE);
        localLinearLayout.findViewById(R.id.vg_record_style_03).setVisibility(View.GONE);
        ImageButton localImageButton1 = (ImageButton) localLinearLayout.findViewById(R.id.btn_value_setting);
        localImageButton1.setBackgroundResource(R.drawable.video_icon);
        TextView localTextView3 = (TextView) localLinearLayout.findViewById(R.id.field_value);
        localTextView3.setHint("请选择录像");
        TextChange localTextChange3 = new TextChange(i, dataType);
        localTextView3.addTextChangedListener(localTextChange3);
        localImageButton1.setTag(localTextView3);
        final int index2 = i;
        localImageButton1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View paramView) {
                InitializeRecordActivity.this.mVideoTextView = ((TextView) paramView.getTag());
                InitializeRecordActivity.this.mVideoTextView
                        .addTextChangedListener(new InitializeRecordActivity.TextChange(index2, 39323));
                InitializeRecordActivity.this.fileSelectOpt_video(bizID);
            }
        });
    }

    private void initMsound(final String bizID, int i, final int dataType, LinearLayout localLinearLayout) {
        localLinearLayout.findViewById(R.id.vg_record_style_01).setVisibility(View.GONE);
        localLinearLayout.findViewById(R.id.vg_record_style_02).setVisibility(View.VISIBLE);
        localLinearLayout.findViewById(R.id.vg_record_style_03).setVisibility(View.GONE);
        ImageButton localImageButton1 = (ImageButton) localLinearLayout.findViewById(R.id.btn_value_setting);
        localImageButton1.setBackgroundResource(R.drawable.sound_icon);
        TextView localTextView3 = (TextView) localLinearLayout.findViewById(R.id.field_value);
        localTextView3.setHint("请选择录音");
        TextChange localTextChange3 = new TextChange(i, dataType);
        localTextView3.addTextChangedListener(localTextChange3);
        localImageButton1.setTag(localTextView3);
        final int index2 = i;
        localImageButton1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View paramView) {
                InitializeRecordActivity.this.mSoundTextView = ((TextView) paramView.getTag());
                InitializeRecordActivity.this.mSoundTextView
                        .addTextChangedListener(new InitializeRecordActivity.TextChange(index2, 39322));
                InitializeRecordActivity.this.fileSelectOpt_sound(bizID);
            }
        });
    }

    private void initMphoto(final String bizID, int i, final int dataType, LinearLayout localLinearLayout) {
        localLinearLayout.findViewById(R.id.vg_record_style_01).setVisibility(View.GONE);
        localLinearLayout.findViewById(R.id.vg_record_style_02).setVisibility(View.VISIBLE);
        localLinearLayout.findViewById(R.id.vg_record_style_03).setVisibility(View.GONE);
        ImageButton localImageButton1 = (ImageButton) localLinearLayout.findViewById(R.id.btn_value_setting);
        localImageButton1.setBackgroundResource(R.drawable.camera_icon);
        TextView localTextView3 = (TextView) localLinearLayout.findViewById(R.id.field_value);
        localTextView3.setHint("请选择图片");
        TextChange localTextChange3 = new TextChange(i, dataType);
        localTextView3.addTextChangedListener(localTextChange3);
        localImageButton1.setTag(localTextView3);
        final int index2 = i;
        localImageButton1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View paramView) {
                InitializeRecordActivity.this.mPhotoTextView = ((TextView) paramView.getTag());
                InitializeRecordActivity.this.mPhotoTextView
                        .addTextChangedListener(new InitializeRecordActivity.TextChange(index2, 39321));
                InitializeRecordActivity.this.fileSelectOpt_photo(bizID);
            }
        });
    }

    private void initDataType6_time(int i, final int dataType, LinearLayout localLinearLayout) {
        localLinearLayout.findViewById(R.id.vg_record_style_01).setVisibility(View.GONE);
        localLinearLayout.findViewById(R.id.vg_record_style_02).setVisibility(View.VISIBLE);
        localLinearLayout.findViewById(R.id.vg_record_style_03).setVisibility(View.GONE);
        ImageButton localImageButton2 = (ImageButton) localLinearLayout.findViewById(R.id.btn_value_setting);
        localImageButton2.setBackgroundResource(R.drawable.time);
        final TextView localTextView4 = (TextView) localLinearLayout.findViewById(R.id.field_value);
        TextChange localTextChange4 = new TextChange(i, dataType);
        localTextView4.addTextChangedListener(localTextChange4);
        localTextView4.setText(UtilsTools.GetFormatDateTime());
        localImageButton2.setTag(localTextView4);
        localImageButton2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View paramView) {
                View view = ((LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE)).inflate(R.layout.time_picker,
                        null);
                Calendar calendar = Calendar.getInstance();
                int curYear = calendar.get(Calendar.YEAR);
                int curMonth = calendar.get(Calendar.MONTH) + 1;
                int curDay = calendar.get(Calendar.DAY_OF_MONTH);
                int curHour = calendar.get(Calendar.HOUR_OF_DAY);
                int curMinute = calendar.get(Calendar.MINUTE);
                int curSecond = calendar.get(Calendar.SECOND);
                if (!("".equals(localTextView4.getText()))) {
                    Date date = UtilsTools.FormateStringToDate("" + localTextView4.getText(), "yyyy-MM-dd HH:mm:ss");
                    calendar.setTime(date);
                    curYear = calendar.get(Calendar.YEAR);
                    curMonth = calendar.get(Calendar.MONTH) + 1;
                    curDay = calendar.get(Calendar.DAY_OF_MONTH);
                    curHour = calendar.get(Calendar.HOUR_OF_DAY);
                    curMinute = calendar.get(Calendar.MINUTE);
                    curSecond = calendar.get(Calendar.SECOND);
                }

                yearWheel = (WheelView) view.findViewById(R.id.yearwheel);
                monthWheel = (WheelView) view.findViewById(R.id.monthwheel);
                dayWheel = (WheelView) view.findViewById(R.id.daywheel);
                hourWheel = (WheelView) view.findViewById(R.id.hourwheel);
                minuteWheel = (WheelView) view.findViewById(R.id.minutewheel);
                secondWheel = (WheelView) view.findViewById(R.id.secondwheel);

                AlertDialog.Builder builder = new AlertDialog.Builder(InitializeRecordActivity.this);
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
                        sb.append(yearWheel.getCurrentItemValue()).append("-").append(monthWheel.getCurrentItemValue())
                                .append("-").append(dayWheel.getCurrentItemValue());

                        sb.append(" ");
                        sb.append(hourWheel.getCurrentItemValue()).append(":").append(minuteWheel.getCurrentItemValue())
                                .append(":").append(secondWheel.getCurrentItemValue());
                        localTextView4.setText(sb);
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });
    }

    private class BtnOnClickListener implements View.OnClickListener {

        public void onClick(View paramView) {
            System.out.println("--------------------" + paramView.getId() + "被点击了");
            switch (paramView.getId()) {
                case R.id.btn_level:// 启高
                    System.out.println("level被点击了");
                    InitializeRecordActivity.this.caseType = "weifa";
                    InitializeRecordActivity.this.caseMap.clear();
                    InitializeRecordActivity.this.caseMap = new HashMap<String, String>();
                    InitializeRecordActivity.this.m_lstFieldInfos.clear();
                    init();
                    btn_level.setChecked(true);
                    btn_approval.setChecked(false);
                    break;
                case R.id.btn_approval:// 启高
                    System.out.println("approval被点击了");
                    InitializeRecordActivity.this.caseType = "weifa_approval";
                    InitializeRecordActivity.this.caseMap.clear();
                    InitializeRecordActivity.this.caseMap = new HashMap<String, String>();
                    InitializeRecordActivity.this.m_lstFieldInfos.clear();
                    init();
                    btn_level.setChecked(false);
                    btn_approval.setChecked(true);
                    break;
                case R.id.btn_ok:
                    // 启动 保存数据异步
                    System.out.println("btn_ok 被点击了");
                    if ("dongwu".equals(InitializeRecordActivity.this.caseType)) {
                        keepdongwuData();// 保存涉及物种的数据
                    }

                    new InitializeRecordActivity.SaveCaseTask().execute(new Integer[0]);
                    break;
                case R.id.btn_cancel:
                    System.out.println("btn_cancel 被点击了");
                    InitializeRecordActivity.this.isExitSys();
                    break;
                case R.id.btn_insert:// 添加物种
                    System.out.println("btn_insert 被点击了");
                    insertWuZhong();

                    break;
                case R.id.btn_keep:
                    System.out.println("btn_keep 被点击了");
                    if ("dongwu".equals(InitializeRecordActivity.this.caseType)) {
                        keepdongwuData();// 保存涉及物种的数据
                    }
                    new InitializeRecordActivity.SaveCaseTask().execute(new Integer[0]);
                    break;
                default:
                    break;
            }

        }

        private void insertWuZhong() {
            tableLayout.setStretchAllColumns(true);
            for (int row = 0; row < 1; row++) {
                TableRow tableRow = new TableRow(InitializeRecordActivity.this);
                tableRow.setBackgroundColor(Color.rgb(222, 220, 210));
                for (int col = 0; col < 5; col++) {
                    if (col < 4) {
                        EditText et_write = new EditText(InitializeRecordActivity.this);
                        et_write.setBackgroundResource(R.drawable.shappe);
                        tableRow.addView(et_write);
                    } else {
                        // 换成删除按钮
                        Button btnDel = new Button(InitializeRecordActivity.this);
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

        private void keepdongwuData() {
            if (!(table_data.isEmpty())) {
                table_data = "";
            }
            TableRow tableRow3 = new TableRow(InitializeRecordActivity.this);
            EditText et_write3 = new EditText(InitializeRecordActivity.this);
            // tableLayout.getChildCount();//获取TableLayout的行数
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
            // m_lstFieldInfos.add(new Field("DONGWU", "涉及物种", this.mCaseObj.getPartId()));
        }
    }

    class SaveCaseTask extends AsyncTask<Integer, Void, Boolean> {
        private ProgressDialog dialog = null;

        protected Boolean doInBackground(Integer[] paramArrayOfInteger) {

            for (int i = 0; i < m_lstFieldInfos.size(); i++) {
                Field f = m_lstFieldInfos.get(i);
                caseMap.put(f.getFieldName(), m_FieldValueList.get(i));
            }
            // ObjectMapper类是Jackson库的主要类。它提供一些功能将转换成Java对象匹配JSON结构，反之亦然。
            // 它使用JsonParser和JsonGenerator的实例实现JSON实际的读/写。
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                attrs = objectMapper.writeValueAsString(caseMap);// 属性json
            } catch (JsonGenerationException e) {
                e.printStackTrace();
            } catch (JsonMappingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if ("dtxc".equals(InitializeRecordActivity.this.caseType)) {
                // 保存地块信息
                IllegalLand illegalLand = new IllegalLand();
                illegalLand.setCaseId(mCaseId);// IllegalClues的父类属性
                illegalLand.setCaseName(m_FieldValueList.get(2));
                illegalLand.setCaseType(caseType);
                illegalLand.setAttrs(attrs);
                if (MapOperate.curGeometry != null) {
                    illegalLand.setGeos(GeometryEngine.geometryToJson(MapOperate.map.getSpatialReference(),
                            MapOperate.curGeometry));
                }
                illegalLand.setCreateTime(UtilsTools.GetFormatDateTime());
                new CaseDBMExternal(InitializeRecordActivity.this).insertOneCase(illegalLand);
                return true;
            } else if ("wfxs".equals(InitializeRecordActivity.this.caseType)) {
                // 保存违法信息
                IllegalClues illegalCluesObj = new IllegalClues();
                illegalCluesObj.setCaseId(mCaseId);// IllegalClues的父类属性
                illegalCluesObj.setCaseName(m_FieldValueList.get(2));// IllegalClues的父类属性
                illegalCluesObj.setCaseType(caseType);// IllegalClues的父类属性
                illegalCluesObj.setAttrs(attrs);// IllegalClues的父类属性

                if (MapOperate.curGeometry != null) {
                    illegalCluesObj.setGeos(GeometryEngine.geometryToJson(
                            // IllegalClues的父类属性
                            MapOperate.map.getSpatialReference(), MapOperate.curGeometry));
                }
                illegalCluesObj.setCreateTime(UtilsTools.GetFormatDateTime());// IllegalClues的父类属性
                new CaseDBMExternal(InitializeRecordActivity.this).insertOneCase(illegalCluesObj);// 插入一个案件信息
                return true;
            }
            // 启高
            else if ("change".equals(InitializeRecordActivity.this.caseType)) {
                // 保存林权流转变更信息
                IllegalChange illegalChangeObj = new IllegalChange();
                illegalChangeObj.setCaseId(mCaseId);// IllegalClues的父类属性
                // List<String> m_FieldValueList
                illegalChangeObj.setCaseName(m_FieldValueList.get(2));
                illegalChangeObj.setCaseType(caseType);
                illegalChangeObj.setAttrs(attrs);

                if (MapOperate.curGeometry != null) {
                    illegalChangeObj.setGeos(GeometryEngine.geometryToJson(MapOperate.map.getSpatialReference(),
                            MapOperate.curGeometry));
                }
                illegalChangeObj.setCreateTime(UtilsTools.GetFormatDateTime());
                new CaseDBMExternal(InitializeRecordActivity.this).insertOneCase(illegalChangeObj);
                return true;
            } // 启高
            else if ("linxia".equals(InitializeRecordActivity.this.caseType)) {
                // 保存林下经济信息
                IllegalLinXia illegalLinXiaObj = new IllegalLinXia();
                illegalLinXiaObj.setCaseId(mCaseId);// IllegalClues的父类属性
                // List<String> m_FieldValueList
                illegalLinXiaObj.setCaseName(m_FieldValueList.get(2));
                illegalLinXiaObj.setCaseType(caseType);
                illegalLinXiaObj.setAttrs(attrs);

                if (MapOperate.curGeometry != null) {
                    illegalLinXiaObj.setGeos(GeometryEngine.geometryToJson(MapOperate.map.getSpatialReference(),
                            MapOperate.curGeometry));
                }
                illegalLinXiaObj.setCreateTime(UtilsTools.GetFormatDateTime());
                new CaseDBMExternal(InitializeRecordActivity.this).insertOneCase(illegalLinXiaObj);
                return true;
            } else if ("linmu".equals(InitializeRecordActivity.this.caseType)) {
                // 保存林木采伐信息
                IllegalLinMu illegalLinMuObj = new IllegalLinMu();
                illegalLinMuObj.setCaseId(mCaseId);// IllegalClues的父类属性
                // List<String> m_FieldValueList
                illegalLinMuObj.setCaseName(m_FieldValueList.get(2));
                illegalLinMuObj.setCaseType(caseType);
                illegalLinMuObj.setAttrs(attrs);

                if (MapOperate.curGeometry != null) {
                    illegalLinMuObj.setGeos(GeometryEngine.geometryToJson(MapOperate.map.getSpatialReference(),
                            MapOperate.curGeometry));
                }
                illegalLinMuObj.setCreateTime(UtilsTools.GetFormatDateTime());
                new CaseDBMExternal(InitializeRecordActivity.this).insertOneCase(illegalLinMuObj);
                return true;
            } else if ("mucai".equals(InitializeRecordActivity.this.caseType)) {
                // 保存木材检查信息
                IllegalMuCai illegalMuCaiObj = new IllegalMuCai();
                illegalMuCaiObj.setCaseId(mCaseId);// IllegalClues的父类属性
                // List<String> m_FieldValueList
                illegalMuCaiObj.setCaseName(m_FieldValueList.get(2));
                illegalMuCaiObj.setCaseType(caseType);
                illegalMuCaiObj.setAttrs(attrs);

                if (MapOperate.curGeometry != null) {
                    illegalMuCaiObj.setGeos(GeometryEngine.geometryToJson(MapOperate.map.getSpatialReference(),
                            MapOperate.curGeometry));
                }
                illegalMuCaiObj.setCreateTime(UtilsTools.GetFormatDateTime());
                new CaseDBMExternal(InitializeRecordActivity.this).insertOneCase(illegalMuCaiObj);
                return true;
            } else if ("weifa".equals(InitializeRecordActivity.this.caseType)) {
                // 保存违法案件1信息
                IllegalWeiFaOne illegalWeiFaObj = new IllegalWeiFaOne();
                illegalWeiFaObj.setCaseId(mCaseId);// IllegalClues的父类属性
                // List<String> m_FieldValueList
                illegalWeiFaObj.setCaseName(m_FieldValueList.get(2));
                illegalWeiFaObj.setCaseType(caseType);
                illegalWeiFaObj.setAttrs(attrs);

                if (MapOperate.curGeometry != null) {
                    illegalWeiFaObj.setGeos(GeometryEngine.geometryToJson(MapOperate.map.getSpatialReference(),
                            MapOperate.curGeometry));
                }
                illegalWeiFaObj.setCreateTime(UtilsTools.GetFormatDateTime());
                new CaseDBMExternal(InitializeRecordActivity.this).insertOneCase(illegalWeiFaObj);
                return true;
            } else if ("weifa_approval".equals(InitializeRecordActivity.this.caseType)) {
                // 保存违法案件1信息
                IllegalWeiFaOne illegalWeiFaObj = new IllegalWeiFaOne();
                illegalWeiFaObj.setCaseId(mCaseId);// IllegalClues的父类属性
                // List<String> m_FieldValueList
                illegalWeiFaObj.setCaseName(m_FieldValueList.get(2));
                illegalWeiFaObj.setCaseType(caseType);
                illegalWeiFaObj.setAttrs(attrs);

                if (MapOperate.curGeometry != null) {
                    illegalWeiFaObj.setGeos(GeometryEngine.geometryToJson(MapOperate.map.getSpatialReference(),
                            MapOperate.curGeometry));
                }
                illegalWeiFaObj.setCreateTime(UtilsTools.GetFormatDateTime());
                new CaseDBMExternal(InitializeRecordActivity.this).insertOneCase(illegalWeiFaObj);
                return true;
            } else if ("dongwu".equals(InitializeRecordActivity.this.caseType)) {
                // 保存野生动物养殖信息
                IllegalDongWu illegalDongWuObj = new IllegalDongWu();
                illegalDongWuObj.setCaseId(mCaseId);// IllegalClues的父类属性
                // List<String> m_FieldValueList
                illegalDongWuObj.setCaseName(m_FieldValueList.get(2));
                illegalDongWuObj.setCaseType(caseType);
                illegalDongWuObj.setAttrs(attrs);

                if (MapOperate.curGeometry != null) {
                    illegalDongWuObj.setGeos(GeometryEngine.geometryToJson(MapOperate.map.getSpatialReference(),
                            MapOperate.curGeometry));
                }
                illegalDongWuObj.setCreateTime(UtilsTools.GetFormatDateTime());
                new CaseDBMExternal(InitializeRecordActivity.this).insertOneCase(illegalDongWuObj);
                return true;
            } else if ("huatu".equals(InitializeRecordActivity.this.caseType)) {
                // 保存画图
                IllegalWeiFaOne illegalWeiFaObj = new IllegalWeiFaOne();
                illegalWeiFaObj.setCaseId(mCaseId);// IllegalClues的父类属性
                // List<String> m_FieldValueList
                illegalWeiFaObj.setCaseName(m_FieldValueList.get(2));
                illegalWeiFaObj.setCaseType(caseType);
                illegalWeiFaObj.setAttrs(attrs);

                if (MapOperate.curGeometry != null) {
                    illegalWeiFaObj.setGeos(GeometryEngine.geometryToJson(MapOperate.map.getSpatialReference(),
                            MapOperate.curGeometry));
                }
                illegalWeiFaObj.setCreateTime(UtilsTools.GetFormatDateTime());
                new CaseDBMExternal(InitializeRecordActivity.this).insertOneCase(illegalWeiFaObj);
                return true;
            } else {
                return true;
            }

        }

        protected void onPostExecute(Boolean paramBoolean) {
            // 跳转到案件管理
            this.dialog.dismiss();
            Intent localIntent = new Intent(InitializeRecordActivity.this, CaseManagerActivity.class);// 启高
            localIntent.putExtra("caseType", caseType);
            InitializeRecordActivity.this.startActivity(localIntent);
            InitializeRecordActivity.this.finish();
        }

        protected void onPreExecute() {
            this.dialog = new ProgressDialog(InitializeRecordActivity.this);
            this.dialog.setMessage("正在保存数据...");
            this.dialog.show();
            super.onPreExecute();
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
                String str3 = paramEditable.toString().trim().toUpperCase();
                if ((str3.equals("TRUE")) || (str3.endsWith("FALSE")))
                    InitializeRecordActivity.this.m_FieldValueList.set(this.nIndex, paramEditable.toString().trim());
                return;
            }
            // 处理选择图片
            if (this.nType == 39321) {
                if (InitializeRecordActivity.this.mImgList != null) {
                    String str1 = "";
                    Iterator<String> localIterator = InitializeRecordActivity.this.mImgList.iterator();
                    while (localIterator.hasNext()) {
                        String str2 = localIterator.next();
                        str1 = str1 + str2 + ",";
                    }
                    str1 = str1.substring(0, -1 + str1.length());
                    InitializeRecordActivity.this.m_FieldValueList.set(this.nIndex, str1);
                    Log.i(TAG, "字段索引：" + this.nIndex + "--图片信息:" + str1);
                }
            } // 处理选择录音
            else if (this.nType == 39322) {
                if (InitializeRecordActivity.this.mSoundList != null) {
                    String str1 = "";
                    Iterator<String> localIterator = InitializeRecordActivity.this.mSoundList.iterator();
                    while (localIterator.hasNext()) {
                        String str2 = localIterator.next();
                        str1 = str1 + str2 + ",";
                    }
                    str1 = str1.substring(0, -1 + str1.length());
                    InitializeRecordActivity.this.m_FieldValueList.set(this.nIndex, str1);
                    Log.i(TAG, "字段索引：" + this.nIndex + "--音频信息:" + str1);
                }
            }
            // 处理选择录像
            else if (this.nType == 39323) {
                if (InitializeRecordActivity.this.mVideoList != null) {
                    String str1 = "";
                    Iterator<String> localIterator = InitializeRecordActivity.this.mVideoList.iterator();
                    while (localIterator.hasNext()) {
                        String str2 = localIterator.next();
                        str1 = str1 + str2 + ",";
                    }
                    str1 = str1.substring(0, -1 + str1.length());
                    InitializeRecordActivity.this.m_FieldValueList.set(this.nIndex, str1);
                    Log.i(TAG, "字段索引：" + this.nIndex + "--视频信息:" + str1);
                }
            } else {
                if (InitializeRecordActivity.this.m_FieldValueList.size() > 0) {
                    InitializeRecordActivity.this.m_FieldValueList.set(this.nIndex, paramEditable.toString().trim());
                }
                Log.i(TAG, "字段索引为：" + this.nIndex + ":" + paramEditable.toString().trim());
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
            case 3236:
                int index = data.getIntExtra("index", 0);
                InitializeRecordActivity.this.m_FieldValueList.set(index, data.getStringExtra("signatureString"));
                break;
            case 3237:
                int index2 = data.getIntExtra("index", 0);
                InitializeRecordActivity.this.m_FieldValueList.set(index2, data.getStringExtra("signatureString"));
                break;
//            case 3233:
//                break;
//            case 3233:
//                break;
        }

        super.onActivityResult(requestCode, resultCode, data);

    }

    /**
     * 按回退按钮
     */
    @Override
    public void onBackPressed() {
        isExitSys();
    }

    private void isExitSys() {
        AlertDialog.Builder localBuilder = new AlertDialog.Builder(this);
        localBuilder.setTitle("提示");
        localBuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                MapOperate.curGeometry = null;
                InitializeRecordActivity.this.finish();
            }
        });
        localBuilder.setNegativeButton("取消", null);
        localBuilder.setMessage("数据未保存，是否放弃编辑");
        localBuilder.show();
    }

}
