package com.guyu.android.gis.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnticipateOvershootInterpolator;

import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;


import com.blankj.utilcode.util.ToastUtils;
import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polygon;
import com.guyu.android.database.sync.DictItemDBMExternal;
import com.guyu.android.database.sync.FieldDBMExternal;
import com.guyu.android.database.task.CaseImgDBMExternal;
import com.guyu.android.database.task.CaseSoundDBMExternal;
import com.guyu.android.database.task.CaseVideoDBMExternal;
import com.guyu.android.database.task.TaskDBMExternal;
import com.guyu.android.database.task.TaskJZDDBMExternal;
import com.guyu.android.gis.adapter.DictItemAdapter;
import com.guyu.android.gis.app.GisQueryApplication;
import com.guyu.android.gis.common.CaseImg;
import com.guyu.android.gis.common.CaseSound;
import com.guyu.android.gis.common.CaseVideo;
import com.guyu.android.gis.common.DictItem;
import com.guyu.android.gis.common.DictItemTwo;
import com.guyu.android.gis.common.Field;
import com.guyu.android.gis.common.Task;
import com.guyu.android.gis.common.TaskJZD;
import com.guyu.android.gis.opt.MapOperate;
import com.guyu.android.utils.UtilsTools;
import com.guyu.android.widget.wheel.StrericWheelAdapter;
import com.guyu.android.widget.wheel.WheelView;
import com.guyu.android.R;

public class TaskDetailsActivity extends Activity {
	private static String TAG = "TaskDetailsActivity";
	public static String DATA_TASK_OBJ = "TaskObj";

	private Task mTaskObj = null;
	private List<Field> m_lstFieldInfos = new ArrayList<Field>();
	private List<Field> mDefaultFieldList = new ArrayList<Field>();
	private List<String> m_FieldValueList = new ArrayList<String>();

	private String taskType;
	private LinearLayout mLayout;
	private LayoutInflater layoutInflater;
	
	private Spinner localSpinner = null;
	private Spinner localSpinner2 = null;

	private TextView mPhotoTextView = null;
	private TextView mSoundTextView = null;
	private TextView mVideoTextView = null;
	private List<EditText> areaTextList=new ArrayList<EditText>();//记录全部的面积字段

	private List<String> mImgList = new ArrayList<String>();
	private List<String> mSoundList = new ArrayList<String>();
	private List<String> mVideoList = new ArrayList<String>();
	private Button mUpButton = null;
	private Button mCaseImgsButton = null;
	private Button mCaseSoundsButton = null;
	private Button mCaseVideosButton = null;
	private MOnClickListener mClickListener;

	private WheelView yearWheel, monthWheel, dayWheel, hourWheel, minuteWheel, secondWheel;
	public static String[] yearContent = null;
	public static String[] monthContent = null;
	public static String[] dayContent = null;
	public static String[] hourContent = null;
	public static String[] minuteContent = null;
	public static String[] secondContent = null;

	protected void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		setContentView(R.layout.activity_task_details_h);
		this.layoutInflater = LayoutInflater.from(this);
		initTimePickerContent();
		initActivity();
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

	private void initActivity() {
		this.mLayout = ((LinearLayout) findViewById(R.id.LinearLayout));
		this.mTaskObj = (Task) getIntent().getSerializableExtra(DATA_TASK_OBJ);
		taskType = mTaskObj.getTaskType();
		String title = "任务详情";
		if ("ajcc".equals(taskType)) {
			title = "案件查处详情";
		} else if ("wfxsjb".equals(taskType)) {
			title = "违法线索详情";
		} else if ("tbgl".equals(taskType)) {
			title = "卫片核查详情";
		} else if ("bgtbgl".equals(taskType)) {
			title = "年度变更调查详情";
		} else if ("tdly".equals(taskType)) {
			title = "土地利用动态巡查详情";
		}
		initData();
		initWidget();

		if (this.mTaskObj != null) {
			setTilte(title + "(" + this.mTaskObj.getRecId() + ")");
		} else {
			setTilte(title);
		}
		initDefaultLandField();
		initList();
		this.mClickListener = new MOnClickListener();		
		this.mCaseImgsButton = ((Button) findViewById(R.id.btn_case_imgs));
		this.mCaseSoundsButton = ((Button) findViewById(R.id.btn_case_sounds));
		this.mCaseVideosButton = ((Button) findViewById(R.id.btn_case_videos));
		this.mCaseImgsButton.setOnClickListener(this.mClickListener);
		this.mCaseSoundsButton.setOnClickListener(this.mClickListener);
		this.mCaseVideosButton.setOnClickListener(this.mClickListener);
		((Button) findViewById(R.id.task_details_rb_analysis)).setOnClickListener(this.mClickListener);
		mUpButton = ((Button) findViewById(R.id.task_details_rb_up_data));
		if (mTaskObj.getUpState() == 1) {
			mUpButton.setVisibility(View.VISIBLE);
			mUpButton.setText("已上报");
		} else {
			mUpButton.setVisibility(View.VISIBLE);
			mUpButton.setOnClickListener(new View.OnClickListener() {
				public void onClick(View paramView) {
					Intent localIntent = new Intent(TaskDetailsActivity.this,SubmitCaseActivity.class);
					localIntent.putExtra(SubmitCaseActivity.DATA_CASE_OBJ, (Serializable) TaskDetailsActivity.this.mTaskObj);
					TaskDetailsActivity.this.startActivityForResult(localIntent, 3236);
				}
			});
		}
	}

	private void setTilte(String paramString) {
		((TextView) findViewById(R.id.tv_title)).setText(paramString);
	}

	private void initData() {

	}

	public void initDefaultLandField() {
		Map<String, Field> fmap = new FieldDBMExternal(this).getFieldMap();
		mDefaultFieldList.clear();
		JSONObject jsonObj;
		try {
			jsonObj = new JSONObject(mTaskObj.getAttrs());
			Iterator<String> it = jsonObj.keys();
			while (it.hasNext()) {
				String fieldEname = it.next();
				Field f = null;
				if ("RECID".equals(fieldEname)) {
					f = new Field("RECID", "案卷编号", null);
				} else if ("XCJD".equals(fieldEname)) {
					f = new Field("RECID", "巡查阶段", null);
				} else {
					f = fmap.get(fieldEname);
				}
				if (!("MPHOTO".equals(fieldEname)) && !("MSOUND".equals(fieldEname)) && !("MVIDEO".equals(fieldEname))) {
					String defValue = jsonObj.getString(fieldEname);
					if (defValue == null || "null".equals(defValue)) {
						mDefaultFieldList.add(f);
					} else {
						f.setDefaultValue(defValue);
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
		List<CaseImg> al_landImgObj = mLandImgManager.getTheCaseAllImg(mTaskObj.getCaseId());
		List<CaseSound> al_landSoundObj = mLandSoundManager.getTheCaseAllSound(mTaskObj.getCaseId());
		List<CaseVideo> al_landVideoObj = mLandVideoManager.getTheCaseAllVideo(mTaskObj.getCaseId());

		mDefaultFieldList.add(new Field("MPHOTO", "图片", "已选择" + al_landImgObj.size() + "张图片"));
		mDefaultFieldList.add(new Field("MSOUND", "音频", "已选择" + al_landSoundObj.size() + "个录音"));
		mDefaultFieldList.add(new Field("MVIDEO", "视频", "已选择" + al_landVideoObj.size() + "个录像"));
		m_lstFieldInfos.addAll(mDefaultFieldList);
		for (int i = 0; i < m_lstFieldInfos.size(); i++) {
			this.m_FieldValueList.add(this.m_lstFieldInfos.get(i).getDefaultValue());
		}

	}

	private void initList() {

		this.areaTextList.clear();
		for (int i = 0; i < this.m_lstFieldInfos.size(); i++) {
			Field localField = this.m_lstFieldInfos.get(i);
			int dataType = localField.getDataType();
			String str = localField.getFieldName();
			String fieldCnname=localField.getFieldCnname();
			LinearLayout localLinearLayout = (LinearLayout) this.layoutInflater.inflate(R.layout.record_field_style, null).findViewById(R.id.record_item);
			localLinearLayout.setTag(str);
			TextView localTextView1 = (TextView) localLinearLayout.findViewById(R.id.attribute_name);
			localTextView1.setTextSize(16.0F);
			localTextView1.setText(localField.getFieldCnname());
			// 时间类型
			if (dataType == 6) {
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
				localImageButton2.setOnClickListener(new View.OnClickListener() {
					public void onClick(View paramView) {
						View view = ((LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE)).inflate(R.layout.time_picker, null);
						Calendar calendar = Calendar.getInstance();
						int curYear = calendar.get(Calendar.YEAR);
						int curMonth = calendar.get(Calendar.MONTH) + 1;
						int curDay = calendar.get(Calendar.DAY_OF_MONTH);
						int curHour = calendar.get(Calendar.HOUR_OF_DAY);
						int curMinute = calendar.get(Calendar.MINUTE);
						int curSecond = calendar.get(Calendar.SECOND);						
						if ("".equals((""+localTextView4.getText()).trim())) {
							calendar.setTime(new Date());			
						}else{							
							Date date = UtilsTools.FormateStringToDate("" + localTextView4.getText(), "yyyy-MM-dd HH:mm:ss");
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

						AlertDialog.Builder builder = new AlertDialog.Builder(TaskDetailsActivity.this);
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
								sb.append(yearWheel.getCurrentItemValue()).append("-").append(monthWheel.getCurrentItemValue()).append("-").append(dayWheel.getCurrentItemValue());

								sb.append(" ");
								sb.append(hourWheel.getCurrentItemValue()).append(":").append(minuteWheel.getCurrentItemValue()).append(":")
										.append(secondWheel.getCurrentItemValue());
								localTextView4.setText(sb);
								dialog.cancel();
							}
						});

						builder.show();
					}
				});
			}
			// 图片
			else if (str.startsWith("MPHOTO")) {
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
				localImageButton1.setOnClickListener(new View.OnClickListener() {
					public void onClick(View paramView) {
						TaskDetailsActivity.this.mPhotoTextView = ((TextView) paramView.getTag());
						TaskDetailsActivity.this.mPhotoTextView.addTextChangedListener(new TaskDetailsActivity.TextChange(index2, 39321));
						TaskDetailsActivity.this.fileSelectOpt_photo();
					}
				});
			}
			// 录音
			else if (str.startsWith("MSOUND")) {
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
				localImageButton1.setOnClickListener(new View.OnClickListener() {
					public void onClick(View paramView) {
						TaskDetailsActivity.this.mSoundTextView = ((TextView) paramView.getTag());
						TaskDetailsActivity.this.mSoundTextView.addTextChangedListener(new TaskDetailsActivity.TextChange(index2, 39322));
						TaskDetailsActivity.this.fileSelectOpt_sound();
					}
				});
			}// 录像
			else if (str.startsWith("MVIDEO")) {
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
				localImageButton1.setOnClickListener(new View.OnClickListener() {
					public void onClick(View paramView) {
						TaskDetailsActivity.this.mVideoTextView = ((TextView) paramView.getTag());
						TaskDetailsActivity.this.mVideoTextView.addTextChangedListener(new TaskDetailsActivity.TextChange(index2, 39323));
						TaskDetailsActivity.this.fileSelectOpt_video();
					}
				});
			} else {
				EditText localEditText = (EditText) localLinearLayout.findViewById(R.id.attribute_value);
				EditText localEditText2 = (EditText) localLinearLayout.findViewById(R.id.attribute_value2);
				// 判断字段是否有字典定义
				int mDicType = localField.getDictType();
				if (mDicType != 0) {
					localSpinner = (Spinner) localLinearLayout
							.findViewById(R.id.sp_value);
					localSpinner2 = (Spinner) localLinearLayout
							.findViewById(R.id.city);
					if (mDicType != 123459) {// 是否是二级字典表
						localSpinner2.setVisibility(View.GONE);
					}
					
					final List<DictItem> al_dictItem = new DictItemDBMExternal(this).getDictItemsByType(mDicType);
					// 判断选择第几项
					int selIndex = 0;
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
//					Spinner localSpinner = (Spinner) localLinearLayout.findViewById(R.id.sp_value);
					DictItemAdapter localArrayAdapter = new DictItemAdapter(this, al_dictItem);
					localSpinner.setAdapter(localArrayAdapter);
					localSpinner.setSelection(selIndex);
					localSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
						public void onItemSelected(AdapterView<?> paramAdapterView, View paramView, int paramInt, long paramLong) {
							int mParentId = al_dictItem.get(paramInt)
									.getParentId();

							if (mParentId >= 1 && mParentId <= 4) {
								final List<DictItemTwo> al_dictItem2 = new DictItemDBMExternal(
										TaskDetailsActivity.this,
										"tbTwoDictItem")
										.getDictItemsByType2(mParentId);// 返回字典表集合
								DictItemAdapter localArrayAdapter = new DictItemAdapter(
										TaskDetailsActivity.this,
										al_dictItem2);
								localSpinner2
										.setAdapter(localArrayAdapter);

							} else {
								localTextView2.setText(al_dictItem.get(
										paramInt).getItemValue());
							}

						}

						public void onNothingSelected(AdapterView<?> paramAdapterView) {
						}
					});
				} else {
					localLinearLayout.findViewById(R.id.vg_record_style_01).setVisibility(View.VISIBLE);
					localLinearLayout.findViewById(R.id.vg_record_style_02).setVisibility(View.GONE);
					localLinearLayout.findViewById(R.id.vg_record_style_03).setVisibility(View.GONE);
					
                     if(fieldCnname.indexOf("面积")>0){
                    	 localEditText2.setId(i);
                    	 areaTextList.add(localEditText2);
						localEditText2.setText(localField.getDefaultValue());
					    localEditText2.setEnabled(false);
					    localEditText2.setVisibility(View.VISIBLE);
					    localEditText.addTextChangedListener(new TaskDetailsActivity.TextChange(i, 39325));
					}else{
						localEditText2.setVisibility(View.GONE);
                    }
					if ("null".equals(localField.getDefaultValue()) || "".equals(localField.getDefaultValue())) {
						localEditText.setHint("请输入" + localField.getFieldCnname());
					} else {
						localEditText.setText(localField.getDefaultValue());
					}
					TextChange localTextChange1 = new TextChange(i, dataType);
					localEditText.addTextChangedListener(localTextChange1);
					localEditText.setInputType(InputType.TYPE_CLASS_TEXT);
				}

			}

			LinearLayout.LayoutParams localLayoutParams = new LinearLayout.LayoutParams(-1, -2);
			this.mLayout.addView(localLinearLayout, localLayoutParams);
		}
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
				TaskDetailsActivity.this.m_FieldValueList.set(this.nIndex, paramEditable.toString().trim());
				return;
			}
			// 处理选择图片
			if (this.nType == 39321) {
				if (TaskDetailsActivity.this.mImgList != null) {
					String str1 = "";
					Iterator<String> localIterator = TaskDetailsActivity.this.mImgList.iterator();
					while (localIterator.hasNext()) {
						String str2 = localIterator.next();
						str1 = str1 + str2 + ",";
					}
					str1 = str1.substring(0, -1 + str1.length());
					TaskDetailsActivity.this.m_FieldValueList.set(this.nIndex, str1);
					Log.i(TAG, "字段索引：" + this.nIndex + "--图片信息:" + str1);
				}
			}// 处理选择录音
			else if (this.nType == 39322) {
				if (TaskDetailsActivity.this.mSoundList != null) {
					String str1 = "";
					Iterator<String> localIterator = TaskDetailsActivity.this.mSoundList.iterator();
					while (localIterator.hasNext()) {
						String str2 = localIterator.next();
						str1 = str1 + str2 + ",";
					}
					str1 = str1.substring(0, -1 + str1.length());
					TaskDetailsActivity.this.m_FieldValueList.set(this.nIndex, str1);
					Log.i(TAG, "字段索引：" + this.nIndex + "--音频信息:" + str1);
				}
			}
			// 处理选择录像
			else if (this.nType == 39323) {
				if (TaskDetailsActivity.this.mVideoList != null) {
					String str1 = "";
					Iterator<String> localIterator = TaskDetailsActivity.this.mVideoList.iterator();
					while (localIterator.hasNext()) {
						String str2 = localIterator.next();
						str1 = str1 + str2 + ",";
					}
					str1 = str1.substring(0, -1 + str1.length());
					TaskDetailsActivity.this.m_FieldValueList.set(this.nIndex, str1);
					Log.i(TAG, "字段索引：" + this.nIndex + "--视频信息:" + str1);
				}
			} else if(this.nType == 39325) {
					if (paramEditable.toString().trim().length()>=0) {
						String name=paramEditable.toString().trim();
						Pattern p = Pattern.compile("^ [ + ] ?(0\\.\\d+) |([1-9][0-9]*(\\.\\d+)?)$");
	                    if (p.matcher(name).matches()) {
	                    	Double mj=Double.parseDouble(paramEditable.toString().trim())/ 666.66666666699996D;
							String fieldValue=String.format("%.4f", mj)+"(亩)";
							for(int i=0;i<areaTextList.size();i++){
								if(areaTextList.get(i).getId()==nIndex){
									areaTextList.get(i).setText(fieldValue);
								}
							}
	                    }else{
							for(int i=0;i<areaTextList.size();i++){
								if(areaTextList.get(i).getId()==nIndex){
									areaTextList.get(i).setHint("请输入数字！");
									areaTextList.get(i).setText("");
								}
							}
						}
					}else{
						for(int i=0;i<areaTextList.size();i++){
							if(areaTextList.get(i).getId()==nIndex){
								areaTextList.get(i).setText(0+"(亩)");
							}
						}
					}
				}else{
					TaskDetailsActivity.this.m_FieldValueList.set(this.nIndex, paramEditable.toString().trim());
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
		if (requestCode == 3233) {
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
		} else if (requestCode == 3234) {
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
		} else if (requestCode == 3235) {
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
		}
		super.onActivityResult(requestCode, resultCode, data);

	}

	private void initWidget() {

	}

	private Polygon getGeometryByRecId() {
		// 获取此案卷的坐标拐点信息
		String mRecid = this.mTaskObj.getRecId();
		TaskJZDDBMExternal taskJZDDBMExternal = new TaskJZDDBMExternal(this);
		List<TaskJZD> list_Taskjzd = taskJZDDBMExternal.getTaskJZDInfosByRecid(mRecid);
		// 构造polygon
		if (list_Taskjzd != null && list_Taskjzd.size() > 0) {
			Polygon mPolygon = new Polygon();
			TaskJZD fTaskJZD = list_Taskjzd.get(0);
			Point mPoint = new Point(Double.parseDouble(fTaskJZD.getXzb()), Double.parseDouble(fTaskJZD.getYzb()));
			mPolygon.startPath(mPoint);
			for (int i = 1; i < list_Taskjzd.size(); i++) {
				TaskJZD mTaskJZD = list_Taskjzd.get(i);
				mPolygon.lineTo(Double.parseDouble(mTaskJZD.getXzb()), Double.parseDouble(mTaskJZD.getYzb()));
			}
			return mPolygon;
		} else {
			return null;
		}
	}

	public void taskLocationOnClick(View paramView) {
		Polygon mPolygon = getGeometryByRecId();
		if (mPolygon == null) {
			ToastUtils.showLong(  "很抱歉，无地图信息，无法定位！");
		} else {
			MapOperate.addAndZoomToPolygon(mPolygon, true);
			Intent localIntent = new Intent(TaskDetailsActivity.this,MainActivity.class);
			localIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			localIntent.putExtra("opt", 4371);
			this.startActivity(localIntent);
		}
	}

	protected void onNewIntent(Intent paramIntent) {
		int optCode = paramIntent.getIntExtra("opt", 0);
		if (optCode == 5566) {
			MapOperate.clearPublicGraphicsLayer();
		}
		super.onNewIntent(paramIntent);
	}

	private void fileSelectOpt_photo() {
		Intent localIntent = new Intent(this,PhotoSelectActivity.class);
		localIntent.putExtra("bizId", mTaskObj.getBizId());
		localIntent.putExtra("caseId", mTaskObj.getCaseId());
		startActivityForResult(localIntent, 3233);
	}

	private void fileSelectOpt_sound() {
		Intent localIntent = new Intent(this,SoundSelectActivity.class);
		localIntent.putExtra("bizId", mTaskObj.getBizId());
		localIntent.putExtra("caseId", mTaskObj.getCaseId());
		startActivityForResult(localIntent, 3234);
	}

	private void fileSelectOpt_video() {
		Intent localIntent = new Intent(this,VideoSelectActivity.class);
		localIntent.putExtra("bizId", mTaskObj.getBizId());
		localIntent.putExtra("caseId", mTaskObj.getCaseId());
		startActivityForResult(localIntent, 3235);
	}

	/**
	 * 保存任务结果
	 * 
	 * @param paramView
	 */
	public void saveTaskResult(View paramView) {
		try {
			JSONObject jsonObj = new JSONObject(mTaskObj.getAttrs());
			for (int i = 0; i < m_lstFieldInfos.size(); i++) {
				jsonObj.put(this.m_lstFieldInfos.get(i).getFieldName(), this.m_FieldValueList.get(i));
			}
			TaskDetailsActivity.this.mTaskObj.setAttrs(jsonObj.toString());
			new TaskDBMExternal(this).updateTaskAttrs(mTaskObj.getCaseId(), jsonObj.toString());
			ToastUtils.showLong(  "保存成功！");
		} catch (JSONException e) {
			e.printStackTrace();
			ToastUtils.showLong(  "保存失败！");
		}
	}

	private class MOnClickListener implements View.OnClickListener {
		public void onClick(View paramView) {
			if (paramView.getId() == R.id.btn_case_imgs) {
				Intent localIntent = new Intent(TaskDetailsActivity.this,ImgViewActivity.class);
				localIntent.putExtra("caseId", TaskDetailsActivity.this.mTaskObj.getCaseId());
				TaskDetailsActivity.this.startActivity(localIntent);
			} else if (paramView.getId() == R.id.btn_case_sounds) {
				Intent localIntent = new Intent(TaskDetailsActivity.this,SoundViewActivity.class);
				localIntent.putExtra("caseId", TaskDetailsActivity.this.mTaskObj.getCaseId());
				TaskDetailsActivity.this.startActivity(localIntent);
			} else if (paramView.getId() == R.id.btn_case_videos) {
				Intent localIntent = new Intent(TaskDetailsActivity.this,VideoViewActivity.class);
				localIntent.putExtra("caseId", TaskDetailsActivity.this.mTaskObj.getCaseId());
				TaskDetailsActivity.this.startActivity(localIntent);
			} else if (paramView.getId() == R.id.task_details_rb_analysis) {
				// 地块分析
				Polygon mPolygon = getGeometryByRecId();
				if (mPolygon == null) {
					ToastUtils.showLong(  "很抱歉，无地图信息，无法进行占地分析！");
				} else {
					Intent localIntent = new Intent(TaskDetailsActivity.this,MainActivity.class);
					localIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
					TaskDetailsActivity.this.startActivity(localIntent);
					Geometry geo = mPolygon;
					// 先简化下图形，解决逆向绘制的图形分析不出来结果
					geo = GeometryEngine.simplify(mPolygon, MapOperate.map.getSpatialReference());
					MapOperate.geoAnalysisTool.analysisGeometry((Polygon) geo, 2);
				}
			}
		}
	}

	public void backOnClick(View paramView) {
		Intent localIntent = new Intent(this,TaskManagerActivity.class);
		localIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		localIntent.putExtra("taskType", "wfxs");
		startActivity(localIntent);
		finish();
	}

}