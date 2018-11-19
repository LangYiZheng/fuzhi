package com.guyu.android.gis.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.blankj.utilcode.util.ToastUtils;
import com.guyu.android.database.task.TaskDBMExternal;
import com.guyu.android.gis.app.GisQueryApplication;
import com.guyu.android.gis.common.CallBack;
import com.guyu.android.gis.common.Case;
import com.guyu.android.gis.common.Task;
import com.guyu.android.gis.common.TaskAjcc;
import com.guyu.android.gis.common.TaskBgtbgl;
import com.guyu.android.gis.common.TaskTbgl;
import com.guyu.android.gis.common.TaskTdly;
import com.guyu.android.gis.common.TaskWfxsjb;
import com.guyu.android.gis.opt.MapOperate;
import com.guyu.android.gis.opt.TaskUpdateOpt;
import com.guyu.android.utils.SysConfig;
import com.guyu.android.utils.UtilsTools;
import com.guyu.android.R;

public class TaskManagerActivity extends Activity {
	private TaskDBMExternal mTaskDataManager = null;
	private TaskManagerActivity taskManagerActivity;
	
	public static String TASK_TYPE = "taskType";
	
	private ListView mWfxsListView = null;
	private ListView mWphcListView = null;
	private ListView mAjccListView = null;
	private ListView mNdbgdcListView = null;
	private ListView mTdlydtxcListView = null;

	private List<Task> mWfxsjbObjs = new ArrayList<Task>();
	private List<Task> mWphcObjs = new ArrayList<Task>();
	private List<Task> mAjccObjs = new ArrayList<Task>();
	private List<Task> mNdbgdcObjs = new ArrayList<Task>();
	private List<Task> mTdlydtxcObjs = new ArrayList<Task>();

	private List<Task> mWfxsjbObjsTemp = new ArrayList<Task>();
	private List<Task> mWphcObjsTemp = new ArrayList<Task>();
	private List<Task> mAjccObjsTemp = new ArrayList<Task>();
	private List<Task> mNdbgdcObjsTemp = new ArrayList<Task>();
	private List<Task> mTdlydtxcObjsTemp = new ArrayList<Task>();

	private ArrayList<View> pageViews;
	private ViewPager viewPager;

	private TextView mWfxsTextView = null;
	private TextView mWphcTextView = null;
	private TextView mAjccTextView = null;
	private TextView mNdbgdcTextView = null;
	private TextView mTdlydtxcTextView = null;

	private Button updateRightNowBtn = null;
	private Handler mHandler = null;
	// ---------------------

	private int offset = 0;// 动画图片偏移量
	private int currIndex = 0;// 当前页卡编号
	private int bmpW;// 动画图片宽度

	private Button btnSearch = null;
	private EditText etSearch = null;
	
	private CheckBox cb_more_opt_task;
	private Button btn_task_delete;
	private boolean isCheck;
	
	private boolean isSearch_ajcc;
	private boolean isSearch_ndbg;
	private boolean isSearch_wfxsjb;
	private boolean isSearch_wphc;
	private boolean isSearch_tdly;
	
	private List<Boolean> lstSelecteds_ajcc = null;
	private List<Boolean> lstSelecteds_ndbg = null;
	private List<Boolean> lstSelecteds_wfxsjb = null;
	private List<Boolean> lstSelecteds_wphc = null;
	private List<Boolean> lstSelecteds_tdly = null;

	// 已核查 的六种情况
	String[] TArr = { "已", "核", "查", "已核", "核查", "已核查" };
	// 未核查 的六种情况
	String[] FArr = { "未", "核", "查", "未核", "核查", "未核查" };

	protected void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		setContentView(R.layout.activity_task_manager);

		this.mTaskDataManager = new TaskDBMExternal(this);
		this.mHandler = new MyHandler(Looper.myLooper());
		if ((SysConfig.mHumanInfo != null)) {
			if (SysConfig.mHumanInfo.getHumanId() == -1) {
				ToastUtils.showLong(  "执法人员编号为空");
				return;
			} else {
				initActivity();
			}
		}
	}

	private void initActivity() {
		String taskType ="";
		if(getIntent().getStringExtra(TASK_TYPE) != ""){
			taskType = getIntent().getStringExtra(TASK_TYPE).toString();
		}
		/*if(taskType != ""){
			ToastUtils.showLong(  "taskType--------:"+taskType);
		}*/
		initNormalWidget();
		initFlipperWidget(taskType);
	}

	/**
	 * 搜索
	 * 
	 * @param paramView
	 */
	public void onClickSearch(View paramView) {
		String etStr = etSearch.getText().toString().trim().toLowerCase();
		if (etStr.equals("")) {
			showTask();
		} else {
			if (currIndex == 0) {// 违法线索
				this.mWfxsjbObjsTemp.clear();
				List<Task> curWfxsjbListTemp = new ArrayList<Task>();

				for (int i = 0; i < mWfxsjbObjs.size(); i++) {
					String attrStr = mWfxsjbObjs.get(i).getAttrs().toString();
					String[] strArr = attrStr.split(",");
					Task taskTemp = mWfxsjbObjs.get(i);

					if (taskTemp.getXdsj().toLowerCase().contains(etStr)) {
						curWfxsjbListTemp.add(taskTemp);
						continue;
					}

					for (int j = 0; j < strArr.length; j++) {
						String strTemp = strArr[j].substring(strArr[j].indexOf(":") + 1).toLowerCase();
						if (strTemp.contains(etStr)) {
							curWfxsjbListTemp.add(taskTemp);
							break;
						}
					}

					if (isHave(TArr, etStr) && taskTemp.getHczt() == 1) {
						curWfxsjbListTemp.add(taskTemp);
					}

					if (isHave(FArr, etStr) && taskTemp.getHczt() == 0) {
						curWfxsjbListTemp.add(taskTemp);
					}
				}
				if (curWfxsjbListTemp.size() > 0) {
					this.mWfxsjbObjsTemp.addAll(curWfxsjbListTemp);
					this.lstSelecteds_wfxsjb = new ArrayList<Boolean>();
				}
				for (int i = 0; i < this.mWfxsjbObjsTemp.size(); i++) {
					this.lstSelecteds_wfxsjb.add(Boolean.valueOf(false));
				}
				this.mWfxsListView.setAdapter(new TaskAdapter(this, this.mWfxsjbObjsTemp, 1));
				this.mWfxsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					public void onItemClick(AdapterView<?> paramAdapterView, View paramView, int paramInt, long paramLong) {
						if(isCheck){
							List<Boolean> localList = TaskManagerActivity.this.lstSelecteds_wfxsjb;
							if (paramInt < TaskManagerActivity.this.lstSelecteds_wfxsjb.size()) {								
								localList.set(paramInt, !(TaskManagerActivity.this.lstSelecteds_wfxsjb.get(paramInt).booleanValue()));}
							TaskManagerActivity.this.mWfxsListView.invalidateViews();							
						}else{
							goToDetailPage(mWfxsjbObjsTemp, paramInt);
						}
					}
				});
				isSearch_wfxsjb = true;
			} else if (currIndex == 1) {// 卫片核查
				this.mWphcObjsTemp.clear();
				List<Task> curWphcListTemp = new ArrayList<Task>();

				for (int i = 0; i < mWphcObjs.size(); i++) {
					String attrStr = mWphcObjs.get(i).getAttrs().toString();
					String[] strArr = attrStr.split(",");
					Task taskTemp = mWphcObjs.get(i);

					if (taskTemp.getXdsj().toLowerCase().contains(etStr)) {
						curWphcListTemp.add(taskTemp);
						continue;
					}

					for (int j = 0; j < strArr.length; j++) {
						String strTemp = strArr[j].substring(strArr[j].indexOf(":") + 1).toLowerCase();
						if (strTemp.contains(etStr)) {
							curWphcListTemp.add(taskTemp);
							break;
						}
					}

					if (isHave(TArr, etStr) && taskTemp.getHczt() == 1) {
						curWphcListTemp.add(taskTemp);
					}

					if (isHave(FArr, etStr) && taskTemp.getHczt() == 0) {
						curWphcListTemp.add(taskTemp);
					}
				}

				if (curWphcListTemp.size() > 0) {
					this.mWphcObjsTemp.addAll(curWphcListTemp);
					this.lstSelecteds_wphc = new ArrayList<Boolean>();
				}
				for (int i = 0; i < this.mWphcObjsTemp.size(); i++) {
					this.lstSelecteds_wphc.add(Boolean.valueOf(false));
				}
				this.mWphcListView.setAdapter(new TaskAdapter(this, this.mWphcObjsTemp, 2));
				this.mWphcListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					public void onItemClick(AdapterView<?> paramAdapterView, View paramView, int paramInt, long paramLong) {
						if(isCheck){
							List<Boolean> localList = TaskManagerActivity.this.lstSelecteds_wphc;
							if (paramInt < TaskManagerActivity.this.lstSelecteds_wphc.size()) {								
								localList.set(paramInt, !(TaskManagerActivity.this.lstSelecteds_wphc.get(paramInt).booleanValue()));}
							TaskManagerActivity.this.mWphcListView.invalidateViews();							
						}else{
							goToDetailPage(mWphcObjsTemp, paramInt);
						}
					}
				});
				isSearch_wphc = true;
			} else if (currIndex == 2) {// 案卷查处
				this.mAjccObjsTemp.clear();
				List<Task> curAjccListTemp = new ArrayList<Task>();

				for (int i = 0; i < mAjccObjs.size(); i++) {
					String attrStr = mAjccObjs.get(i).getAttrs().toString();
					String[] strArr = attrStr.split(",");
					Task taskTemp = mAjccObjs.get(i);

					if (taskTemp.getRecId().toLowerCase().contains(etStr)) {
						curAjccListTemp.add(taskTemp);
						continue;
					}

					if (taskTemp.getXdsj().toLowerCase().contains(etStr)) {
						curAjccListTemp.add(taskTemp);
						continue;
					}

					for (int j = 0; j < strArr.length; j++) {
						String strTemp = strArr[j].substring(strArr[j].indexOf(":") + 1).toLowerCase();
						if (strTemp.contains(etStr)) {
							curAjccListTemp.add(taskTemp);
							break;
						}
					}

					if (isHave(TArr, etStr) && taskTemp.getHczt() == 1) {
						curAjccListTemp.add(taskTemp);
					}

					if (isHave(FArr, etStr) && taskTemp.getHczt() == 0) {
						curAjccListTemp.add(taskTemp);
					}
				}

				if (curAjccListTemp.size() > 0) {
					this.mAjccObjsTemp.addAll(curAjccListTemp);
					this.lstSelecteds_ajcc = new ArrayList<Boolean>();
				}
				for (int i = 0; i < this.mAjccObjsTemp.size(); i++) {
					this.lstSelecteds_ajcc.add(Boolean.valueOf(false));
				}
				this.mAjccListView.setAdapter(new TaskAdapter(this, this.mAjccObjsTemp, 3));
				this.mAjccListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					public void onItemClick(AdapterView<?> paramAdapterView, View paramView, int paramInt, long paramLong) {
						if(isCheck){
							List<Boolean> localList = TaskManagerActivity.this.lstSelecteds_ajcc;
							if (paramInt < TaskManagerActivity.this.lstSelecteds_ajcc.size()) {								
								localList.set(paramInt, !(TaskManagerActivity.this.lstSelecteds_ajcc.get(paramInt).booleanValue()));}
							TaskManagerActivity.this.mAjccListView.invalidateViews();							
						}else{
							goToDetailPage(mAjccObjsTemp, paramInt);
						}
					}
				});
				isSearch_ajcc = true;
			} else if (currIndex == 3) {// 年度变更调查

				this.mNdbgdcObjsTemp.clear();
				List<Task> curNdbgdcListTemp = new ArrayList<Task>();

				for (int i = 0; i < mNdbgdcObjs.size(); i++) {
					String attrStr = mNdbgdcObjs.get(i).getAttrs().toString();
					String[] strArr = attrStr.split(",");
					Task taskTemp = mNdbgdcObjs.get(i);

					if (taskTemp.getXdsj().toLowerCase().contains(etStr)) {
						curNdbgdcListTemp.add(taskTemp);
						continue;
					}

					for (int j = 0; j < strArr.length; j++) {
						String strTemp = strArr[j].substring(strArr[j].indexOf(":") + 1).toLowerCase();
						if (strTemp.contains(etStr)) {
							curNdbgdcListTemp.add(taskTemp);
							break;
						}
					}

					if (isHave(TArr, etStr) && taskTemp.getHczt() == 1) {
						curNdbgdcListTemp.add(taskTemp);
					}

					if (isHave(FArr, etStr) && taskTemp.getHczt() == 0) {
						curNdbgdcListTemp.add(taskTemp);
					}
				}
				if (curNdbgdcListTemp.size() > 0) {
					this.mNdbgdcObjsTemp.addAll(curNdbgdcListTemp);
					this.lstSelecteds_ndbg = new ArrayList<Boolean>();
				}
				for (int i = 0; i < this.mNdbgdcObjsTemp.size(); i++) {
					this.lstSelecteds_ndbg.add(Boolean.valueOf(false));
				}
				this.mNdbgdcListView.setAdapter(new TaskAdapter(this, this.mNdbgdcObjsTemp, 4));
				this.mNdbgdcListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					public void onItemClick(AdapterView<?> paramAdapterView, View paramView, int paramInt, long paramLong) {
						if(isCheck){
							List<Boolean> localList = TaskManagerActivity.this.lstSelecteds_ndbg;
							if (paramInt < TaskManagerActivity.this.lstSelecteds_ndbg.size()) {								
								localList.set(paramInt, !(TaskManagerActivity.this.lstSelecteds_ndbg.get(paramInt).booleanValue()));}
							TaskManagerActivity.this.mNdbgdcListView.invalidateViews();							
						}else{
							goToDetailPage(mNdbgdcObjsTemp, paramInt);
						}
					}
				});
				isSearch_ndbg = true;
			} else if (currIndex == 4) {// 土地利用动态巡查
				this.mTdlydtxcObjsTemp.clear();
				List<Task> curTdlydtxcListTemp = new ArrayList<Task>();

				for (int i = 0; i < mTdlydtxcObjs.size(); i++) {
					String attrStr = mTdlydtxcObjs.get(i).getAttrs().toString();
					String[] strArr = attrStr.split(",");
					Task taskTemp = mTdlydtxcObjs.get(i);

					if (taskTemp.getRecId().toLowerCase().contains(etStr)) {
						curTdlydtxcListTemp.add(taskTemp);
						continue;
					}

					if (taskTemp.getXdsj().toLowerCase().contains(etStr)) {
						curTdlydtxcListTemp.add(taskTemp);
						continue;
					}

					for (int j = 0; j < strArr.length; j++) {
						String strTemp = strArr[j].substring(strArr[j].indexOf(":") + 1).toLowerCase();
						if (strTemp.contains(etStr)) {
							curTdlydtxcListTemp.add(taskTemp);
							break;
						}
					}

					if (isHave(TArr, etStr) && taskTemp.getHczt() == 1) {
						curTdlydtxcListTemp.add(taskTemp);
					}

					if (isHave(FArr, etStr) && taskTemp.getHczt() == 0) {
						curTdlydtxcListTemp.add(taskTemp);
					}

				}
				
				
				if (curTdlydtxcListTemp.size() > 0) {
					this.mTdlydtxcObjsTemp.addAll(curTdlydtxcListTemp);
					this.lstSelecteds_tdly = new ArrayList<Boolean>();
				}
				for (int i = 0; i < this.mTdlydtxcObjsTemp.size(); i++) {
					this.lstSelecteds_tdly.add(Boolean.valueOf(false));
				}
				this.mTdlydtxcListView.setAdapter(new TaskAdapter(this, this.mTdlydtxcObjsTemp, 5));
				this.mTdlydtxcListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					public void onItemClick(AdapterView<?> paramAdapterView, View paramView, int paramInt, long paramLong) {
						if(isCheck){
							List<Boolean> localList = TaskManagerActivity.this.lstSelecteds_tdly;
							if (paramInt < TaskManagerActivity.this.lstSelecteds_tdly.size()) {								
								localList.set(paramInt, !(TaskManagerActivity.this.lstSelecteds_tdly.get(paramInt).booleanValue()));}
							TaskManagerActivity.this.mTdlydtxcListView.invalidateViews();							
						}else{
							goToDetailPage(mTdlydtxcObjsTemp, paramInt);
						}
					}
				});
				isSearch_tdly = true;
			} else {
				Toast.makeText(TaskManagerActivity.this, "输入有误,无法查询,请核实!", Toast.LENGTH_SHORT).show();
			}
		}

	}

	private void initNormalWidget() {
		this.mWfxsTextView = ((TextView) findViewById(R.id.tv_wfxs));
		this.mWphcTextView = ((TextView) findViewById(R.id.tv_wphc));
		this.mAjccTextView = ((TextView) findViewById(R.id.tv_ajcc));
		this.mNdbgdcTextView = ((TextView) findViewById(R.id.tv_ndbgdc));
		this.mTdlydtxcTextView = ((TextView) findViewById(R.id.tv_tdlydtxc));
		this.updateRightNowBtn = ((Button) findViewById(R.id.updateRightNowBtn));
		this.updateRightNowBtn.setVisibility(View.VISIBLE);
		this.etSearch = (EditText) findViewById(R.id.etSearch);
		this.btnSearch = ((Button) findViewById(R.id.btnSearch));

		mWfxsTextView.setOnClickListener(new MyOnClickListener(0));
		mWphcTextView.setOnClickListener(new MyOnClickListener(1));
		mAjccTextView.setOnClickListener(new MyOnClickListener(2));
		mNdbgdcTextView.setOnClickListener(new MyOnClickListener(3));
		mTdlydtxcTextView.setOnClickListener(new MyOnClickListener(4));
	}

	private void initFlipperWidget(String taskType) {

		initPageViews();
		
		int curItem=0;
		currIndex=0;
		if(taskType != ""){
			if(taskType.equals("wfxsjb")){
				curItem=0;
				currIndex=0;
			}else if(taskType.equals("tbgl")){
				curItem=1;
				currIndex=1;
			}else if(taskType.equals("ajcc")){
				curItem=2;
				currIndex=2;
			}else if(taskType.equals("bgtbgl")){
				curItem=3;
				currIndex=3;
			}else if(taskType.equals("tdly")){
				curItem=4;
				currIndex=4;
			}
		}
		this.viewPager = ((ViewPager) findViewById(R.id.taskPage));
		viewPager.setAdapter(new GuidePageAdapter(pageViews));
		viewPager.setCurrentItem(curItem);
		viewPager.setOnPageChangeListener(new MyOnPageChangeListener());

		setSelectedTextView(currIndex);
		
		isSearch_ajcc = false;
		isSearch_ndbg = false;
		isSearch_wfxsjb = false;
		isSearch_wphc = false;
		isSearch_tdly = false;
		
		this.cb_more_opt_task = ((CheckBox) findViewById(R.id.cb_more_opt_task));
		this.btn_task_delete = ((Button) findViewById(R.id.btn_task_delete));
		cb_more_opt_task.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton paramCompoundButton, boolean paramBoolean) {
				if (paramBoolean) {
					btn_task_delete.setVisibility(View.VISIBLE);
					isCheck = true;
				} else {
					btn_task_delete.setVisibility(View.INVISIBLE);
					isCheck = false;
				}
			}
		});
	}

	public class MyOnClickListener implements View.OnClickListener {
		private int index = 0;

		public MyOnClickListener(int i) {
			index = i;
		}

		public void onClick(View v) {
			viewPager.setCurrentItem(index);
			setSelectedTextView(index);
			viewPager.setVisibility(View.VISIBLE);

		}
	}

	public void setSelectedTextView(int index){
		if (index == 0) {
			mWfxsTextView.setSelected(true);
			mWphcTextView.setSelected(false);
			mAjccTextView.setSelected(false);
			mNdbgdcTextView.setSelected(false);
			mTdlydtxcTextView.setSelected(false);
		} else if (index == 1) {
			mWfxsTextView.setSelected(false);
			mWphcTextView.setSelected(true);
			mAjccTextView.setSelected(false);
			mNdbgdcTextView.setSelected(false);
			mTdlydtxcTextView.setSelected(false);
		} else if (index == 2) {
			mWfxsTextView.setSelected(false);
			mWphcTextView.setSelected(false);
			mAjccTextView.setSelected(true);
			mNdbgdcTextView.setSelected(false);
			mTdlydtxcTextView.setSelected(false);
		} else if (index == 3) {
			mWfxsTextView.setSelected(false);
			mWphcTextView.setSelected(false);
			mAjccTextView.setSelected(false);
			mNdbgdcTextView.setSelected(true);
			mTdlydtxcTextView.setSelected(false);
		} else {
			mWfxsTextView.setSelected(false);
			mWphcTextView.setSelected(false);
			mAjccTextView.setSelected(false);
			mNdbgdcTextView.setSelected(false);
			mTdlydtxcTextView.setSelected(true);
		}
	}
	public class MyOnPageChangeListener implements OnPageChangeListener {

		int one = offset * 2 + bmpW;// 页卡1 -> 页卡2 偏移量
		int two = one * 2;// 页卡1 -> 页卡3 偏移量
		int three = one * 3;
		int four = one * 4;

		@Override
		public void onPageSelected(int arg0) {
			Animation animation = null;
			switch (arg0) {
			case 0:
				if (currIndex == 1) {
					animation = new TranslateAnimation(one, 0, 0, 0);
				} else if (currIndex == 2) {
					animation = new TranslateAnimation(two, 0, 0, 0);
				} else if (currIndex == 3) {
					animation = new TranslateAnimation(three, 0, 0, 0);
				} else if (currIndex == 4) {
					animation = new TranslateAnimation(four, 0, 0, 0);
				}
				break;
			case 1:
				if (currIndex == 0) {
					animation = new TranslateAnimation(offset, one, 0, 0);
				} else if (currIndex == 2) {
					animation = new TranslateAnimation(two, one, 0, 0);
				} else if (currIndex == 3) {
					animation = new TranslateAnimation(three, one, 0, 0);
				} else if (currIndex == 4) {
					animation = new TranslateAnimation(four, one, 0, 0);
				}
				break;
			case 2:
				if (currIndex == 0) {
					animation = new TranslateAnimation(offset, two, 0, 0);
				} else if (currIndex == 1) {
					animation = new TranslateAnimation(one, two, 0, 0);
				} else if (currIndex == 3) {
					animation = new TranslateAnimation(three, two, 0, 0);
				} else if (currIndex == 4) {
					animation = new TranslateAnimation(four, two, 0, 0);
				}
				break;
			case 3:
				if (currIndex == 0) {
					animation = new TranslateAnimation(offset, three, 0, 0);
				} else if (currIndex == 1) {
					animation = new TranslateAnimation(one, three, 0, 0);
				} else if (currIndex == 2) {
					animation = new TranslateAnimation(two, three, 0, 0);
				} else if (currIndex == 4) {
					animation = new TranslateAnimation(four, three, 0, 0);
				}
				break;
			case 4:
				if (currIndex == 0) {
					animation = new TranslateAnimation(offset, four, 0, 0);
				} else if (currIndex == 1) {
					animation = new TranslateAnimation(one, four, 0, 0);
				} else if (currIndex == 2) {
					animation = new TranslateAnimation(two, four, 0, 0);
				} else if (currIndex == 3) {
					animation = new TranslateAnimation(three, four, 0, 0);
				}
				break;
			}
			currIndex = arg0;
			animation.setFillAfter(true);
			animation.setDuration(300);
			setSelectedTextView(arg0);
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}

	}

	public class GuidePageAdapter extends PagerAdapter {

		public List<View> mListViews;

		GuidePageAdapter(List<View> mListViews) {
			this.mListViews = mListViews;
		}

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			((ViewPager) arg0).removeView(mListViews.get(arg1));
		}

		@Override
		public void finishUpdate(View arg0) {
		}

		@Override
		public int getCount() {
			return mListViews.size();
		}

		@Override
		public Object instantiateItem(View arg0, int arg1) {
			((ViewPager) arg0).addView(mListViews.get(arg1), 0);
			return mListViews.get(arg1);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == (arg1);
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {
		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {
		}

	}

	private void initPageViews() {

		if (this.pageViews == null) {
			this.pageViews = new ArrayList<View>();
		} else {
			this.pageViews.clear();
		}

		LayoutInflater mInflater = getLayoutInflater();

		ViewGroup localViewGroup1 = (ViewGroup) mInflater.inflate(R.layout.item_wfxsjb, this.viewPager);
		ViewGroup localViewGroup2 = (ViewGroup) mInflater.inflate(R.layout.item_wphc, this.viewPager);
		ViewGroup localViewGroup3 = (ViewGroup) mInflater.inflate(R.layout.item_ajcc, this.viewPager);
		ViewGroup localViewGroup4 = (ViewGroup) mInflater.inflate(R.layout.item_ndbg, this.viewPager);
		ViewGroup localViewGroup5 = (ViewGroup) mInflater.inflate(R.layout.item_tdly, this.viewPager);

		// 违法线索交办
		this.mWfxsjbObjs.clear();
		List<Task> curWfxsjbList = this.mTaskDataManager.getTaskInfos(getBizIdByTaskType("wfxsjb"), "wfxsjb");
		if (curWfxsjbList.size() > 0) {
			this.mWfxsjbObjs.addAll(curWfxsjbList);
			this.lstSelecteds_wfxsjb = new ArrayList<Boolean>();
		}
		for (int i = 0; i < this.mWfxsjbObjs.size(); i++) {
			this.lstSelecteds_wfxsjb.add(Boolean.valueOf(false));
		}
		this.mWfxsListView = ((ListView) localViewGroup1.findViewById(R.id.list_wfxs));
		this.mWfxsListView.setAdapter(new TaskAdapter(this, this.mWfxsjbObjs, 1));
		this.mWfxsListView
		.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> paramAdapterView,
					View paramView, int paramInt, long paramLong) {
				if(isCheck){
					List<Boolean> localList = TaskManagerActivity.this.lstSelecteds_wfxsjb;
					if (paramInt < TaskManagerActivity.this.lstSelecteds_wfxsjb.size()) {								
						localList.set(paramInt, !(TaskManagerActivity.this.lstSelecteds_wfxsjb.get(paramInt).booleanValue()));}
					TaskManagerActivity.this.mWfxsListView.invalidateViews();							
				}else{
					goToDetailPage(mWfxsjbObjs, paramInt);
				}
			}
		});
		
		// 卫片核查
		this.mWphcObjs.clear();
		List<Task> curWphcList = this.mTaskDataManager.getTaskInfos(getBizIdByTaskType("tbgl"), "tbgl");
		if (curWphcList.size() > 0) {
			this.mWphcObjs.addAll(curWphcList);
			this.lstSelecteds_wphc = new ArrayList<Boolean>();
		}
		for (int i = 0; i < this.mWphcObjs.size(); i++) {
			this.lstSelecteds_wphc.add(Boolean.valueOf(false));
		}
		this.mWphcListView = ((ListView) localViewGroup2.findViewById(R.id.list_wphc));
		this.mWphcListView.setAdapter(new TaskAdapter(this, this.mWphcObjs, 2));		
		this.mWphcListView
		.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> paramAdapterView,
					View paramView, int paramInt, long paramLong) {
				if(isCheck){
					List<Boolean> localList = TaskManagerActivity.this.lstSelecteds_wphc;
					if (paramInt < TaskManagerActivity.this.lstSelecteds_wphc.size()) {								
						localList.set(paramInt, !(TaskManagerActivity.this.lstSelecteds_wphc.get(paramInt).booleanValue()));}
					TaskManagerActivity.this.mWphcListView.invalidateViews();							
				}else{
					goToDetailPage(mWphcObjs, paramInt);
				}
			}
		});

		// 案件查处
		this.mAjccObjs.clear();
		List<Task> curAjccList = this.mTaskDataManager.getTaskInfos(getBizIdByTaskType("ajcc"), "ajcc");
		if (curAjccList.size() > 0) {
			this.mAjccObjs.addAll(curAjccList);
			this.lstSelecteds_ajcc = new ArrayList<Boolean>();
		}
		for (int i = 0; i < this.mAjccObjs.size(); i++) {
			this.lstSelecteds_ajcc.add(Boolean.valueOf(false));
		}
		this.mAjccListView = ((ListView) localViewGroup3.findViewById(R.id.list_ajcc));
		this.mAjccListView.setAdapter(new TaskAdapter(this, this.mAjccObjs, 3));
		this.mAjccListView
		.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> paramAdapterView,
					View paramView, int paramInt, long paramLong) {
				if(isCheck){
					List<Boolean> localList = TaskManagerActivity.this.lstSelecteds_ajcc;
					if (paramInt < TaskManagerActivity.this.lstSelecteds_ajcc.size()) {								
						localList.set(paramInt, !(TaskManagerActivity.this.lstSelecteds_ajcc.get(paramInt).booleanValue()));}
					TaskManagerActivity.this.mAjccListView.invalidateViews();							
				}else{
					goToDetailPage(mAjccObjs, paramInt);
				}						
			}
		});

		// 年度变更调查
		this.mNdbgdcObjs.clear();
		List<Task> curNdbgdcList = this.mTaskDataManager.getTaskInfos(getBizIdByTaskType("bgtbgl"), "bgtbgl");
		if (curNdbgdcList.size() > 0) {
			this.mNdbgdcObjs.addAll(curNdbgdcList);
			this.lstSelecteds_ndbg = new ArrayList<Boolean>();
		}
		for (int i = 0; i < this.mNdbgdcObjs.size(); i++) {
			this.lstSelecteds_ndbg.add(Boolean.valueOf(false));
		}
		this.mNdbgdcListView = ((ListView) localViewGroup4.findViewById(R.id.list_ndbgdc));
		this.mNdbgdcListView.setAdapter(new TaskAdapter(this, this.mNdbgdcObjs, 4));
		this.mNdbgdcListView
		.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> paramAdapterView,
					View paramView, int paramInt, long paramLong) {
				if(isCheck){
					List<Boolean> localList = TaskManagerActivity.this.lstSelecteds_ndbg;
					if (paramInt < TaskManagerActivity.this.lstSelecteds_ndbg.size()) {								
						localList.set(paramInt, !(TaskManagerActivity.this.lstSelecteds_ndbg.get(paramInt).booleanValue()));}
					TaskManagerActivity.this.mNdbgdcListView.invalidateViews();							
				}else{
					goToDetailPage(mNdbgdcObjs, paramInt);
				}
			}
		});

		// 土地利用动态巡查
		this.mTdlydtxcObjs.clear();
		List<Task> curTdlydtxcList = this.mTaskDataManager.getTaskInfos(getBizIdByTaskType("tdly"), "tdly");
		if (curTdlydtxcList.size() > 0) {
			this.mTdlydtxcObjs.addAll(curTdlydtxcList);
			this.lstSelecteds_tdly = new ArrayList<Boolean>();
		}
		for (int i = 0; i < this.mTdlydtxcObjs.size(); i++) {
			this.lstSelecteds_tdly.add(Boolean.valueOf(false));
		}
		this.mTdlydtxcListView = ((ListView) localViewGroup5.findViewById(R.id.list_tdlydtxc));
		this.mTdlydtxcListView.setAdapter(new TaskAdapter(this, this.mTdlydtxcObjs, 5));
		this.mTdlydtxcListView
		.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> paramAdapterView,
					View paramView, int paramInt, long paramLong) {
				if(isCheck){
					List<Boolean> localList = TaskManagerActivity.this.lstSelecteds_tdly;
					if (paramInt < TaskManagerActivity.this.lstSelecteds_tdly.size()) {								
						localList.set(paramInt, !(TaskManagerActivity.this.lstSelecteds_tdly.get(paramInt).booleanValue()));}
					TaskManagerActivity.this.mTdlydtxcListView.invalidateViews();							
				}else{
					goToDetailPage(mTdlydtxcObjs, paramInt);
				}
			}
		});
		
		pageViews.add(localViewGroup1);
		pageViews.add(localViewGroup2);
		pageViews.add(localViewGroup3);
		pageViews.add(localViewGroup4);
		pageViews.add(localViewGroup5);
	}

	/**
	 * 进入详细页面
	 * 
	 * @param curTaskList
	 * @param paramInt
	 */
	private void goToDetailPage(List<Task> curTaskList, int paramInt) {
		Intent localIntent = new Intent(this,TaskDetailsActivity.class);
		localIntent.putExtra(TaskDetailsActivity.DATA_TASK_OBJ, (Serializable) curTaskList.get(paramInt));
		TaskManagerActivity.this.startActivityForResult(localIntent, 6236);
	}

	private boolean deleteTask(List<Task> curTaskList, int paramInt) {
		return this.mTaskDataManager.deleteTaskInfoByRecId(curTaskList.get(paramInt).getCaseId().toString());
	}

	public void refreshData() {

		List<Task> curWfxsList = this.mTaskDataManager.getTaskInfos(getBizIdByTaskType("wfxsjb"), "wfxsjb");
		this.mWfxsjbObjs.clear();
		this.mWfxsjbObjs.addAll(curWfxsList);
		this.lstSelecteds_wfxsjb = new ArrayList<Boolean>();
		mWfxsListView.invalidateViews();
		for (int i = 0; i < this.mWfxsjbObjs.size(); i++) {
			this.lstSelecteds_wfxsjb.add(Boolean.valueOf(false));
		}
		
		List<Task> curWphcList = this.mTaskDataManager.getTaskInfos(getBizIdByTaskType("tbgl"), "tbgl");
		this.mWphcObjs.clear();
		this.mWphcObjs.addAll(curWphcList);
		this.lstSelecteds_wphc = new ArrayList<Boolean>();
		mWphcListView.invalidateViews();
		for (int i = 0; i < this.mWphcObjs.size(); i++) {
			this.lstSelecteds_wphc.add(Boolean.valueOf(false));
		}
		
		List<Task> curAjccList = this.mTaskDataManager.getTaskInfos(getBizIdByTaskType("ajcc"), "ajcc");
		this.mAjccObjs.clear();
		this.mAjccObjs.addAll(curAjccList);
		this.lstSelecteds_ajcc = new ArrayList<Boolean>();
		mAjccListView.invalidateViews();
		for (int i = 0; i < this.mAjccObjs.size(); i++) {
			this.lstSelecteds_ajcc.add(Boolean.valueOf(false));
		}
		
		List<Task> curNdbgList = this.mTaskDataManager.getTaskInfos(getBizIdByTaskType("bgtbgl"), "bgtbgl");
		this.mNdbgdcObjs.clear();
		this.mNdbgdcObjs.addAll(curNdbgList);
		this.lstSelecteds_ndbg = new ArrayList<Boolean>();
		mNdbgdcListView.invalidateViews();
		for (int i = 0; i < this.mNdbgdcObjs.size(); i++) {
			this.lstSelecteds_ndbg.add(Boolean.valueOf(false));
		}
		
		List<Task> curTdlyList = this.mTaskDataManager.getTaskInfos(getBizIdByTaskType("tdly"), "tdly");
		this.mTdlydtxcObjs.clear();
		this.mTdlydtxcObjs.addAll(curTdlyList);
		this.lstSelecteds_tdly = new ArrayList<Boolean>();
		mTdlydtxcListView.invalidateViews();
		for (int i = 0; i < this.mTdlydtxcObjs.size(); i++) {
			this.lstSelecteds_tdly.add(Boolean.valueOf(false));
		}
		
		updateRightNowBtn.setEnabled(true);
	}

	private class MyHandler extends Handler {
		public MyHandler(Looper paramLooper) {
			super(paramLooper);
		}

		public void handleMessage(Message paramMessage) {
			switch (paramMessage.what) {
			case 13000:
				refreshData();
				break;
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case 6236: {
			refreshData();
			break;
		}
		}
	}

	public class TaskAdapter extends BaseAdapter implements OnItemLongClickListener {

		private List<Task> taskInfos = null;
		private LayoutInflater mInflater = null;
		private int curPage = 1;

		public TaskAdapter(Context paramContext, List<Task> paramList, int pageNum) {
			curPage = pageNum;
			this.taskInfos = paramList;
			this.mInflater = ((LayoutInflater) paramContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE));
		}

		public int getCount() {
			return this.taskInfos.size();
		}

		public Object getItem(int paramInt) {
			return this.taskInfos.get(paramInt);
		}

		public long getItemId(int paramInt) {
			return paramInt;
		}

		@Override
		public View getView(int paramInt, View paramView, ViewGroup paramViewGroup) {
			if ((this.taskInfos != null) && (paramInt < this.taskInfos.size())) {

				String recid = this.taskInfos.get(paramInt).getRecId();
				int hczt = this.taskInfos.get(paramInt).getHczt();
				String xdsj = this.taskInfos.get(paramInt).getXdsj();
				String hcztStr = (hczt == 1) ? "已核查" : "未核查";

				if (curPage == 1) {// 违法线索
					if (paramView == null)
						paramView = this.mInflater.inflate(R.layout.record_item_wfxsjb, paramViewGroup, false);
					String xsly = ((TaskWfxsjb) this.taskInfos.get(paramInt)).getXSLY();
					if(hczt != 1){
						paramView.setBackgroundColor(Color.argb(127,205,0,50));
					}else{
						paramView.setBackgroundColor(Color.WHITE);
					}
					((TextView) paramView.findViewById(R.id.textView01)).setText(recid);
					((TextView) paramView.findViewById(R.id.textView02)).setText(xsly);
					((TextView) paramView.findViewById(R.id.textView03)).setText(hcztStr);
					((TextView) paramView.findViewById(R.id.textView04)).setText(xdsj);
					CheckBox localCheckBox_wfxsjb = (CheckBox) paramView.findViewById(R.id.cb_check_wfxsjb);
				    if ((TaskManagerActivity.this.lstSelecteds_wfxsjb != null) && (TaskManagerActivity.this.lstSelecteds_wfxsjb.size() > paramInt)) {
				    	localCheckBox_wfxsjb.setChecked(TaskManagerActivity.this.lstSelecteds_wfxsjb.get(paramInt).booleanValue());
					}
				} else if (curPage == 2) {// 卫片核查
					if (paramView == null)
						paramView = this.mInflater.inflate(R.layout.record_item_wphc, paramViewGroup, false);

					String tbbh = ((TaskTbgl) this.taskInfos.get(paramInt)).getTBBH();
					if(hczt != 1){
						paramView.setBackgroundColor(Color.argb(127,205,0,50));
					}else{
						paramView.setBackgroundColor(Color.WHITE);
					}
					((TextView) paramView.findViewById(R.id.textView01)).setText(recid);
					((TextView) paramView.findViewById(R.id.textView02)).setText(tbbh);
					((TextView) paramView.findViewById(R.id.textView03)).setText(hcztStr);
					((TextView) paramView.findViewById(R.id.textView04)).setText(xdsj);
					CheckBox localCheckBox_wphc = (CheckBox) paramView.findViewById(R.id.cb_check_wphc);
				    if ((TaskManagerActivity.this.lstSelecteds_wphc != null) && (TaskManagerActivity.this.lstSelecteds_wphc.size() > paramInt)) {
				    	localCheckBox_wphc.setChecked(TaskManagerActivity.this.lstSelecteds_wphc.get(paramInt).booleanValue());
					}
				} else if (curPage == 3) {// 案卷查处
					if (paramView == null)
						paramView = this.mInflater.inflate(R.layout.record_item_ajcc, paramViewGroup, false);
					String ajly = ((TaskAjcc) this.taskInfos.get(paramInt)).getAJLY();
					if ("null".equals(ajly)) {
						ajly = "";
					}
					if(hczt != 1){
						paramView.setBackgroundColor(Color.argb(127,205,0,50));
					}else{
						paramView.setBackgroundColor(Color.WHITE);
					}
					((TextView) paramView.findViewById(R.id.textView01)).setText(recid);
					((TextView) paramView.findViewById(R.id.textView02)).setText(ajly);
					((TextView) paramView.findViewById(R.id.textView03)).setText(hcztStr);
					((TextView) paramView.findViewById(R.id.textView04)).setText(xdsj);
					CheckBox localCheckBox_ajcc = (CheckBox) paramView.findViewById(R.id.cb_check_ajcc);
				    if ((TaskManagerActivity.this.lstSelecteds_ajcc != null) && (TaskManagerActivity.this.lstSelecteds_ajcc.size() > paramInt)) {
				    	localCheckBox_ajcc.setChecked(TaskManagerActivity.this.lstSelecteds_ajcc.get(paramInt).booleanValue());
					}				   
				} else if (curPage == 4) {// 年度变更调查
					if (paramView == null)
						paramView = this.mInflater.inflate(R.layout.record_item_ndbg, paramViewGroup, false);

					String tbbh = ((TaskBgtbgl) this.taskInfos.get(paramInt)).getTBBH();
					if(hczt != 1){
						paramView.setBackgroundColor(Color.argb(127,205,0,50));
					}else{
						paramView.setBackgroundColor(Color.WHITE);
					}
					((TextView) paramView.findViewById(R.id.textView01)).setText(recid);
					((TextView) paramView.findViewById(R.id.textView02)).setText(tbbh);
					((TextView) paramView.findViewById(R.id.textView03)).setText(hcztStr);
					((TextView) paramView.findViewById(R.id.textView04)).setText(xdsj);
					 CheckBox localCheckBox_ndbg = (CheckBox) paramView.findViewById(R.id.cb_check_ndbg);
					    if ((TaskManagerActivity.this.lstSelecteds_ndbg != null) && (TaskManagerActivity.this.lstSelecteds_ndbg.size() > paramInt)) {
					    	localCheckBox_ndbg.setChecked(TaskManagerActivity.this.lstSelecteds_ndbg.get(paramInt).booleanValue());
						}
				} else {// 土地利用动态巡查
					if (paramView == null)
						paramView = this.mInflater.inflate(R.layout.record_item_tdly, paramViewGroup, false);

					String xmmc = ((TaskTdly) this.taskInfos.get(paramInt)).getXMMC();
					String xcjd = ((TaskTdly) this.taskInfos.get(paramInt)).getXCJD();
					if(hczt != 1){
						paramView.setBackgroundColor(Color.argb(127,205,0,50));
					}else{
						paramView.setBackgroundColor(Color.WHITE);
					}
					((TextView) paramView.findViewById(R.id.textView01)).setText(recid);
					((TextView) paramView.findViewById(R.id.textView02)).setText(xmmc);
					((TextView) paramView.findViewById(R.id.textView03)).setText(xcjd);
					((TextView) paramView.findViewById(R.id.textView04)).setText(hcztStr);
					((TextView) paramView.findViewById(R.id.textView05)).setText(xdsj);
					CheckBox localCheckBox_tdly = (CheckBox) paramView.findViewById(R.id.cb_check_tdly);
				    if ((TaskManagerActivity.this.lstSelecteds_tdly != null) && (TaskManagerActivity.this.lstSelecteds_tdly.size() > paramInt)) {
				    	localCheckBox_tdly.setChecked(TaskManagerActivity.this.lstSelecteds_tdly.get(paramInt).booleanValue());
					}
				}
			}
			return paramView;
		}

		public void refreshData(List<Task> paramList) {
			if ((paramList == null) || (paramList.size() <= 0))
				return;
			this.taskInfos.clear();
			this.taskInfos.addAll(paramList);
		}

		@Override
		public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			// TODO Auto-generated method stub
			return false;
		}
	}
	
	public void onDeleteTaskClick(View paramView){
		List<Task> localArrayList = new ArrayList<Task>();
		if (currIndex == 0) {
			if ((this.lstSelecteds_wfxsjb != null) && (this.lstSelecteds_wfxsjb.size() > 0)) {
				for (int i = 0; i < this.lstSelecteds_wfxsjb.size(); i++) {
					if (this.lstSelecteds_wfxsjb.get(i)) {
						if(isSearch_wfxsjb){
							localArrayList.add(this.mWfxsjbObjsTemp.get(i));
						} else {
							localArrayList.add(this.mWfxsjbObjs.get(i));
						}
					}
				}			
			} else {
				ToastUtils.showLong(  "操作有误!");
			}
		} else if (currIndex == 1) {
			if ((this.lstSelecteds_wphc != null) && (this.lstSelecteds_wphc.size() > 0)) {
				for (int i = 0; i < this.lstSelecteds_wphc.size(); i++) {
					if (this.lstSelecteds_wphc.get(i)) {
						if(isSearch_wphc){
							localArrayList.add(this.mWphcObjsTemp.get(i));
						} else {
							localArrayList.add(this.mWphcObjs.get(i));
						}			
					}
				}
			} else {
				ToastUtils.showLong(  "操作有误!");
			}			
		} else if (currIndex == 2) {
			if ((this.lstSelecteds_ajcc != null) && (this.lstSelecteds_ajcc.size() > 0)) {
				for (int i = 0; i < this.lstSelecteds_ajcc.size(); i++) {
					if (this.lstSelecteds_ajcc.get(i)) {
						if(isSearch_ajcc){
							localArrayList.add(this.mAjccObjsTemp.get(i));
						} else {
							localArrayList.add(this.mAjccObjs.get(i));
						}
					}
				}
			} else {
				ToastUtils.showLong(  "操作有误!");
			}
		} else if (currIndex == 3) {
			if ((this.lstSelecteds_ndbg != null) && (this.lstSelecteds_ndbg.size() > 0)) {
				for (int i = 0; i < this.lstSelecteds_ndbg.size(); i++) {
					if (this.lstSelecteds_ndbg.get(i)) {
						if(isSearch_ndbg){
							localArrayList.add(this.mNdbgdcObjsTemp.get(i));
						} else {
							localArrayList.add(this.mNdbgdcObjs.get(i));
						}						
					}
				}
			} else {
				ToastUtils.showLong(  "操作有误!");
			}
		} else if (currIndex == 4) {
			if ((this.lstSelecteds_tdly != null) && (this.lstSelecteds_tdly.size() > 0)) {
				for (int i = 0; i < this.lstSelecteds_tdly.size(); i++) {
					if (this.lstSelecteds_tdly.get(i)) {
						if(isSearch_tdly){
							localArrayList.add(this.mTdlydtxcObjsTemp.get(i));
						} else {
							localArrayList.add(this.mTdlydtxcObjs.get(i));
						}						
					}
				}
			} else {
				ToastUtils.showLong(  "操作有误!");
			}
		} else {
			ToastUtils.showLong(  "操作有误!");
		}
		if(this.mTaskDataManager.deleteTaskInfos(localArrayList)){
			this.cb_more_opt_task.setChecked(false);
			btn_task_delete.setVisibility(View.INVISIBLE);
			isCheck = false;
			refreshData();
			showTask();
		}
	}

	/**
	 * 更新设置
	 * 
	 * @param paramView
	 */
	public void updateSettingOnClick(View paramView) {
		startActivityForResult(new Intent(this,TaskUpdateSettingActivity.class), 8300);
	}

	/**
	 * 马上更新数据
	 * 
	 * @param paramView
	 */
	public void updateOnClick(View paramView) {
		updateRightNowBtn.setEnabled(false);
		Toast.makeText(getBaseContext(), "正在更新任务...", Toast.LENGTH_LONG).show();
		UpdateThread updateThread = new UpdateThread();
		new Thread(updateThread).start();
	}

	class UpdateThread implements Runnable {
		public void run() {
			TaskUpdateOpt.updateAllTaskList(TaskManagerActivity.this, new CallBack() {

				@Override
				public void execute() {
					mHandler.sendEmptyMessage(13000);
				}
			});
		}
	}

	/**
	 * 返回按钮
	 * 
	 * @param paramView
	 */
	public void onBackOnClick(View paramView) {
		Intent localIntent = new Intent(this,MainActivity.class);
		localIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		startActivity(localIntent);
		finish();
	}

	/**
	 * 根据任务类型获取业务ID
	 * 
	 * @param taskType
	 * @return
	 */
	public static String getBizIdByTaskType(String taskType) {
		return GisQueryApplication.getApp().getProjectconfig().getCaseUploadsConfig().getCaseUploadConfigMap().get(taskType).getBizId();
	}

	public static boolean isHave(String[] strs, String s) {
		for (int i = 0; i < strs.length; i++) {
			if (strs[i].indexOf(s) != -1) {
				return true;
			}
		}
		return false;

	}

	public void showTask() {
		if (currIndex == 0) {// 违法线索
			this.mWfxsjbObjsTemp.clear();
			List<Task> curWfxsjbListTemp = mWfxsjbObjs;
			if (curWfxsjbListTemp.size() > 0) {
				this.mWfxsjbObjsTemp.addAll(curWfxsjbListTemp);
			}
			this.mWfxsListView.setAdapter(new TaskAdapter(this, this.mWfxsjbObjsTemp, 1));
			this.mWfxsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				public void onItemClick(AdapterView<?> paramAdapterView, View paramView, int paramInt, long paramLong) {
					if(isCheck){
						List<Boolean> localList = TaskManagerActivity.this.lstSelecteds_wfxsjb;
						if (paramInt < TaskManagerActivity.this.lstSelecteds_wfxsjb.size()) {								
							localList.set(paramInt, !(TaskManagerActivity.this.lstSelecteds_wfxsjb.get(paramInt).booleanValue()));}
						TaskManagerActivity.this.mWfxsListView.invalidateViews();							
					}else{
						goToDetailPage(mWfxsjbObjsTemp, paramInt);
					}
				}
			});
			isSearch_wfxsjb = true;
		} else if (currIndex == 1) {// 卫片核查
			this.mWphcObjsTemp.clear();
			List<Task> curWphcListTemp = mWphcObjs;

			if (curWphcListTemp.size() > 0) {
				this.mWphcObjsTemp.addAll(curWphcListTemp);
			}
			this.mWphcListView.setAdapter(new TaskAdapter(this, this.mWphcObjsTemp, 2));
			this.mWphcListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				public void onItemClick(AdapterView<?> paramAdapterView, View paramView, int paramInt, long paramLong) {
					if(isCheck){
						List<Boolean> localList = TaskManagerActivity.this.lstSelecteds_wphc;
						if (paramInt < TaskManagerActivity.this.lstSelecteds_wphc.size()) {								
							localList.set(paramInt, !(TaskManagerActivity.this.lstSelecteds_wphc.get(paramInt).booleanValue()));}
						TaskManagerActivity.this.mWphcListView.invalidateViews();							
					}else{
						goToDetailPage(mWphcObjsTemp, paramInt);
					}					
				}
			});
			isSearch_wphc = true;
		} else if (currIndex == 2) {// 案卷查处
			this.mAjccObjsTemp.clear();
			List<Task> curAjccListTemp = mAjccObjs;
			if (curAjccListTemp.size() > 0) {
				this.mAjccObjsTemp.addAll(curAjccListTemp);
			}
			this.mAjccListView.setAdapter(new TaskAdapter(this, this.mAjccObjsTemp, 3));
			this.mAjccListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				public void onItemClick(AdapterView<?> paramAdapterView, View paramView, int paramInt, long paramLong) {
					if(isCheck){
						List<Boolean> localList = TaskManagerActivity.this.lstSelecteds_ajcc;
						if (paramInt < TaskManagerActivity.this.lstSelecteds_ajcc.size()) {								
							localList.set(paramInt, !(TaskManagerActivity.this.lstSelecteds_ajcc.get(paramInt).booleanValue()));}
						TaskManagerActivity.this.mAjccListView.invalidateViews();							
					}else{
						goToDetailPage(mAjccObjsTemp, paramInt);
					}
				}
			});
			isSearch_ajcc = true;
		} else if (currIndex == 3) {// 年度变更调查
			this.mNdbgdcObjsTemp.clear();
			List<Task> curNdbgdcListTemp = mNdbgdcObjs;

			if (curNdbgdcListTemp.size() > 0) {
				this.mNdbgdcObjsTemp.addAll(curNdbgdcListTemp);
			}
			this.mNdbgdcListView.setAdapter(new TaskAdapter(this, this.mNdbgdcObjsTemp, 4));
			this.mNdbgdcListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				public void onItemClick(AdapterView<?> paramAdapterView, View paramView, int paramInt, long paramLong) {
					if(isCheck){
						List<Boolean> localList = TaskManagerActivity.this.lstSelecteds_ndbg;
						if (paramInt < TaskManagerActivity.this.lstSelecteds_ndbg.size()) {								
							localList.set(paramInt, !(TaskManagerActivity.this.lstSelecteds_ndbg.get(paramInt).booleanValue()));}
						TaskManagerActivity.this.mNdbgdcListView.invalidateViews();							
					}else{
						goToDetailPage(mNdbgdcObjsTemp, paramInt);
					}
				}
			});
			isSearch_ndbg = true;
		} else if (currIndex == 4) {// 土地利用动态巡查
			this.mTdlydtxcObjsTemp.clear();
			List<Task> curTdlydtxcListTemp = mTdlydtxcObjs;

			if (curTdlydtxcListTemp.size() > 0) {
				this.mTdlydtxcObjsTemp.addAll(curTdlydtxcListTemp);
			}
			this.mTdlydtxcListView.setAdapter(new TaskAdapter(this, this.mTdlydtxcObjsTemp, 5));
			this.mTdlydtxcListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				public void onItemClick(AdapterView<?> paramAdapterView, View paramView, int paramInt, long paramLong) {
					if(isCheck){
						List<Boolean> localList = TaskManagerActivity.this.lstSelecteds_tdly;
						if (paramInt < TaskManagerActivity.this.lstSelecteds_tdly.size()) {								
							localList.set(paramInt, !(TaskManagerActivity.this.lstSelecteds_tdly.get(paramInt).booleanValue()));}
						TaskManagerActivity.this.mTdlydtxcListView.invalidateViews();							
					}else{
						goToDetailPage(mTdlydtxcObjsTemp, paramInt);
					}
					
				}
			});
			isSearch_tdly = true;
		} else {
			Toast.makeText(TaskManagerActivity.this, "查询有误!", Toast.LENGTH_SHORT).show();
		}
	}

}