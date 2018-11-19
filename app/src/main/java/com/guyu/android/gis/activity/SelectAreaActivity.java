package com.guyu.android.gis.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.PagerTitleStrip;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Editable;
import android.text.TextWatcher;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;

import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import android.widget.TextView;

import java.util.ArrayList;

import java.util.List;


import com.guyu.android.database.sync.CantonDBMExternal;
import com.guyu.android.gis.app.GisQueryApplication;
import com.guyu.android.gis.common.Canton;
import com.guyu.android.utils.SysConfig;
import com.guyu.android.R;

public class SelectAreaActivity extends Activity {
	private TextView mAreaQueryTextView = null;
	private List<Canton> mCityAreaData = new ArrayList<Canton>();
	private ListView mCityLstView = null;
	private TextView mCityTextView = null;
	private List<Canton> mCountyAreaData = new ArrayList<Canton>();
	private ListView mCountyLstView = null;
	private TextView mCountyTextView = null;
	private View.OnClickListener mOnClickListener = null;
	private List<Canton> mProAreaData = new ArrayList<Canton>();
	private ListView mProLstView = null;
	private TextView mProTextView = null;
	private TextView et_area_query = null;

	private List<Canton> mQueryResultCantons = new ArrayList<Canton>();
	private ListView mQueryResultLstView = null;
	private List<Canton> mStreetAreaData = new ArrayList<Canton>();
	private ListView mStreetLstView = null;
	private LinearLayout mVgAreaQueryLayout = null;
	private LinearLayout mVgListSelectLayout = null;
	private int m_nCurrentPage = 0;
	private ViewGroup main;
	private ArrayList<View> pageViews;
	private ViewPager viewPager;
	private CantonDBMExternal mCantonDBExOpt = null;
	private PagerTitleStrip mPagerTitleStrip;
	private Canton pre_Canton = null;// 查询时上次选中的区域对象

	// ---------------------
	private ImageView cursor;// 动画图片
	private TextView t1, t2, t3;// 页卡头标
	private int offset = 0;// 动画图片偏移量
	private int currIndex = 0;// 当前页卡编号
	private int bmpW;// 动画图片宽度

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_area);

		this.mCantonDBExOpt = new CantonDBMExternal(this);
		initNormalWidget();
		initFlipperWidget();
		initQueryAreaWidget();

		mAreaQueryTextView.setOnClickListener(new OnClickListener() {// 查询
					@Override
					public void onClick(View view) {
						SelectAreaActivity.this.mProTextView.setSelected(false);
						SelectAreaActivity.this.mCityTextView
								.setSelected(false);
						SelectAreaActivity.this.mCountyTextView
								.setSelected(false);
						SelectAreaActivity.this.mAreaQueryTextView
								.setSelected(true);
						SelectAreaActivity.this.mVgListSelectLayout
								.setVisibility(View.GONE);
						SelectAreaActivity.this.mVgAreaQueryLayout
								.setVisibility(View.VISIBLE);
						((InputMethodManager) SelectAreaActivity.this
								.getSystemService(Context.INPUT_METHOD_SERVICE))
								.showSoftInput(SelectAreaActivity.this
										.findViewById(R.id.et_area_query), 0);
					}
				});
	}

	private void initNormalWidget() {
		this.mVgListSelectLayout = ((LinearLayout) findViewById(R.id.ll_area_lst_select));
		SelectAreaActivity.this.mVgListSelectLayout.setVisibility(View.VISIBLE);
		this.mVgAreaQueryLayout = ((LinearLayout) findViewById(R.id.ll_area_query));
		this.mVgAreaQueryLayout.setVisibility(View.GONE);
		this.mAreaQueryTextView = ((TextView) findViewById(R.id.txt_area_query));

		this.mProTextView = ((TextView) findViewById(R.id.txt_area_province));
		this.mCityTextView = ((TextView) findViewById(R.id.txt_area_city));
		this.mCountyTextView = ((TextView) findViewById(R.id.txt_area_county));
		mProTextView.setOnClickListener(new MyOnClickListener(0));
		mCityTextView.setOnClickListener(new MyOnClickListener(1));
		mCountyTextView.setOnClickListener(new MyOnClickListener(2));
		this.et_area_query = ((TextView) findViewById(R.id.et_area_query));
	}

	private void initQueryAreaWidget() {
		((EditText) findViewById(R.id.et_area_query))
				.addTextChangedListener(new TextWatcher() {
					public void afterTextChanged(Editable paramEditable) {
						String str = paramEditable.toString();
						List localList = SelectAreaActivity.this.mCantonDBExOpt
								.getAllCantonsByKeyWord(str);
						if (localList == null)
							return;
						SelectAreaActivity.this.mQueryResultCantons.clear();
						SelectAreaActivity.this.mQueryResultCantons
								.addAll(localList);
						SelectAreaActivity.this.mQueryResultLstView
								.invalidateViews();
						SelectAreaActivity.this.mCityLstView.invalidateViews();
						SelectAreaActivity.this.mCountyLstView
								.invalidateViews();
					}

					public void beforeTextChanged(
							CharSequence paramCharSequence, int paramInt1,
							int paramInt2, int paramInt3) {
					}

					public void onTextChanged(CharSequence paramCharSequence,
							int paramInt1, int paramInt2, int paramInt3) {
					}
				});

		this.mQueryResultLstView = ((ListView) findViewById(R.id.lst_query_result));
		this.mQueryResultLstView.setAdapter(new AreaAdapter(this,
				this.mQueryResultCantons));
		this.mQueryResultLstView
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					public void onItemClick(AdapterView<?> paramAdapterView,
							View paramView, int paramInt, long paramLong) {
						Canton cur_Canton = null;
						if (paramInt < SelectAreaActivity.this.mQueryResultCantons
								.size()) {
							cur_Canton = SelectAreaActivity.this.mQueryResultCantons
									.get(paramInt);

							SysConfig.mProCanton = null;
							SysConfig.mCityCanton = null;
							SysConfig.mCountyCanton = null;
							SysConfig.mUnitInfo = null;
							SysConfig.mHumanInfo = null;
							((InputMethodManager) SelectAreaActivity.this
									.getSystemService(Context.INPUT_METHOD_SERVICE))
									.hideSoftInputFromWindow(
											SelectAreaActivity.this
													.getCurrentFocus()
													.getWindowToken(), 2);

							if ((cur_Canton.isPro())) {// 省
								SysConfig.mProCanton = cur_Canton;
								SelectAreaActivity.this.mProAreaData.clear();
								SelectAreaActivity.this.mCityAreaData.clear();
								SelectAreaActivity.this.mCountyAreaData.clear();
								SelectAreaActivity.this.mProAreaData
										.add(cur_Canton);
								mProTextView.setSelected(false);
								mCityTextView.setSelected(true);
								mCountyTextView.setSelected(false);
								mAreaQueryTextView.setSelected(false);
								SelectAreaActivity.this.mProLstView
										.invalidateViews();
								selectProOpt(0);// 切换到省
							}

							if ((cur_Canton.isCity())) {// 市
								Canton pro_Canton = SelectAreaActivity.this.mCantonDBExOpt
										.getFatherCantonByCode(cur_Canton
												.getParentId());
								if (pro_Canton != null) {
									SysConfig.mProCanton = pro_Canton;
									SysConfig.mCityCanton = cur_Canton;
									SelectAreaActivity.this.mProAreaData
											.clear();
									SelectAreaActivity.this.mCityAreaData
											.clear();
									SelectAreaActivity.this.mCountyAreaData
											.clear();
									SelectAreaActivity.this.mProAreaData
											.add(pro_Canton);
									SelectAreaActivity.this.mCityAreaData
											.add(cur_Canton);
									mCityTextView.setSelected(false);
									mProTextView.setSelected(false);
									mCountyTextView.setSelected(true);
									mAreaQueryTextView.setSelected(false);
									SelectAreaActivity.this.mCityLstView
											.invalidateViews();
									selectCityOpt(0);// 切换到市
								}
							}
							if ((cur_Canton.isCounty())) {// 区县
								Canton city_Canton = SelectAreaActivity.this.mCantonDBExOpt
										.getFatherCantonByCode(cur_Canton
												.getParentId());
								if (city_Canton != null) {
									Canton pro_Canton = SelectAreaActivity.this.mCantonDBExOpt
											.getFatherCantonByCode(city_Canton
													.getParentId());
									if (pro_Canton != null) {
										SysConfig.mProCanton = pro_Canton;
										SysConfig.mCityCanton = city_Canton;
										SysConfig.mCountyCanton = cur_Canton;
										SelectAreaActivity.this.mProAreaData
												.clear();
										SelectAreaActivity.this.mCityAreaData
												.clear();
										SelectAreaActivity.this.mCountyAreaData
												.clear();
										SelectAreaActivity.this.mProAreaData
												.add(pro_Canton);
										SelectAreaActivity.this.mCityAreaData
												.add(city_Canton);
										SelectAreaActivity.this.mCountyAreaData
												.add(cur_Canton);
										SysConfig.mCurrentCanton
												.setCantonId(SysConfig.mCountyCanton
														.getCantonId());
										mProTextView
												.setText(SysConfig.mProCanton
														.getCantonName());
										mCityTextView
												.setText(SysConfig.mCityCanton
														.getCantonName());
										mCountyTextView
												.setText(SysConfig.mCountyCanton
														.getCantonName());
										SelectAreaActivity.this.mCountyLstView
												.invalidateViews();
										SelectAreaActivity.this
												.startActivityForResult(
														new Intent(
																SelectAreaActivity.this,SelectUnitActivity.class),
														0);// 切换到县
									}
								}
							}
						}
						SelectAreaActivity.this.mVgAreaQueryLayout
								.setVisibility(View.GONE);
						SelectAreaActivity.this.mVgListSelectLayout
								.setVisibility(View.VISIBLE);
					}
				});
	}

	private void initFlipperWidget() {
		initPageViews();
		this.viewPager = ((ViewPager) findViewById(R.id.guidePages));
		viewPager.setAdapter(new GuidePageAdapter(pageViews));
		viewPager.setCurrentItem(0);
		viewPager.setOnPageChangeListener(new MyOnPageChangeListener());
		this.mProLstView.invalidateViews();
		this.mCityLstView.invalidateViews();
		this.mCountyLstView.invalidateViews();
	}

	public class MyOnClickListener implements View.OnClickListener {
		private int index = 0;

		public MyOnClickListener(int i) {
			index = i;
		}

		public void onClick(View v) {
			viewPager.setCurrentItem(index);
			if (index == 0) {
				mProTextView.setSelected(true);
				mCityTextView.setSelected(false);
				mCountyTextView.setSelected(false);
				SelectAreaActivity.this.setCurrentFlipperPage(0);
			} else if (index == 1) {
				mCityTextView.setSelected(true);
				mProTextView.setSelected(false);
				mCountyTextView.setSelected(false);
				SelectAreaActivity.this.setCurrentFlipperPage(1);
			} else {
				mCountyTextView.setSelected(true);
				mProTextView.setSelected(false);
				mCityTextView.setSelected(false);
				SelectAreaActivity.this.setCurrentFlipperPage(2);
			}
			viewPager.setVisibility(View.VISIBLE);
			mAreaQueryTextView.setSelected(false);
			SelectAreaActivity.this.mVgAreaQueryLayout.setVisibility(View.GONE);
			SelectAreaActivity.this.mVgListSelectLayout
					.setVisibility(View.VISIBLE);
			System.out.println("当前页为-------------------------：" + index);
		}
	}

	private void initPageViews() {
		this.pageViews = new ArrayList();
		LayoutInflater mInflater = getLayoutInflater();
		ViewGroup localViewGroup1 = (ViewGroup) mInflater.inflate(
				R.layout.item01, null);
		ViewGroup localViewGroup2 = (ViewGroup) mInflater.inflate(
				R.layout.item01, null);
		ViewGroup localViewGroup3 = (ViewGroup) mInflater.inflate(
				R.layout.item01, null);

		List<Canton> allProInfo = getAllProviceInfos();// 获取所有的省级区域--行政区域
		if (allProInfo != null) {
			mProAreaData.addAll(allProInfo);
		}
		this.mProLstView = ((ListView) localViewGroup1
				.findViewById(R.id.lst_area));
		this.mProLstView.setAdapter(new AreaAdapter(this, this.mProAreaData));// 行政区
		this.mProLstView
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					public void onItemClick(AdapterView<?> paramAdapterView,
							View paramView, int paramInt, long paramLong) {
						if (SelectAreaActivity.this.mProAreaData.size() <= paramInt) {
							return;
						}
						selectProOpt(paramInt);// 点击省，到下一个面板选择市
					}
				});

		this.mCityLstView = ((ListView) localViewGroup2
				.findViewById(R.id.lst_area));
		this.mCityLstView.setAdapter(new AreaAdapter(this, this.mCityAreaData));
		this.mCityLstView
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					public void onItemClick(AdapterView<?> paramAdapterView,
							View paramView, int paramInt, long paramLong) {
						selectCityOpt(paramInt);// 点击市，到下一个面板选择县
					}
				});
		this.mCountyLstView = ((ListView) localViewGroup3
				.findViewById(R.id.lst_area));
		this.mCountyLstView.setAdapter(new AreaAdapter(this,
				this.mCountyAreaData));
		this.mCountyLstView
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					public void onItemClick(AdapterView<?> paramAdapterView,
							View paramView, int paramInt, long paramLong) {
						SysConfig.mCountyCanton = SelectAreaActivity.this.mCountyAreaData
								.get(paramInt);
						SysConfig.mCurrentCanton
								.setCantonId(SysConfig.mCountyCanton
										.getCantonId());
						SelectAreaActivity.this
								.startActivityForResult(
										new Intent(
												SelectAreaActivity.this,SelectUnitActivity.class),
										0);
					}
				});
		pageViews.add(localViewGroup1);
		pageViews.add(localViewGroup2);
		pageViews.add(localViewGroup3);

	}

	public class AreaAdapter extends BaseAdapter {
		private List<Canton> areaInfos = null;
		private LayoutInflater mInflater = null;

		public AreaAdapter(Context paramContext, List<Canton> paramList) {
			this.areaInfos = paramList;
			this.mInflater = ((LayoutInflater) paramContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE));
		}

		public int getCount() {
			return this.areaInfos.size();
		}

		public Object getItem(int paramInt) {
			return this.areaInfos.get(paramInt);
		}

		public long getItemId(int paramInt) {
			return paramInt;
		}

		@Override
		public View getView(int paramInt, View paramView,
				ViewGroup paramViewGroup) {
			if ((this.areaInfos != null) && (paramInt < this.areaInfos.size())) {
				paramView = this.mInflater.inflate(R.layout.list_area,
						paramViewGroup, false);
				paramView.findViewById(R.id.iv_area_type_show);
				((TextView) paramView.findViewById(R.id.tv_area_name))
						.setText(this.areaInfos.get(paramInt)
								.getCantonName());
			}
			return paramView;
		}
	}

	protected void onActivityResult(int requestCode, int resultCode,
			Intent paramIntent) {
		super.onActivityResult(requestCode, resultCode, paramIntent);
		if (requestCode == 0) {// 在行政区划里选择单位
			if (resultCode == 0) {// 正常结束
				SelectAreaActivity.this
						.startActivityForResult(
								new Intent(
										SelectAreaActivity.this,SelectPeoActivity.class),
								1);
			} else {// 返回按钮
				setResult(-1);
				SelectAreaActivity.this.finish();
			}
		} else {// 在行政区划里的单位中选择人员
			setResult(0);
			SelectAreaActivity.this.finish();
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

	/**
	 * 页卡切换监听
	 */
	public class MyOnPageChangeListener implements OnPageChangeListener {

		int one = offset * 2 + bmpW;// 页卡1 -> 页卡2 偏移量
		int two = one * 2;// 页卡1 -> 页卡3 偏移量

		@Override
		public void onPageSelected(int arg0) {
			Animation animation = null;
			switch (arg0) {
			case 0:
				if (currIndex == 1) {
					animation = new TranslateAnimation(one, 0, 0, 0);
				} else if (currIndex == 2) {
					animation = new TranslateAnimation(two, 0, 0, 0);
				}
				break;
			case 1:
				if (currIndex == 0) {
					animation = new TranslateAnimation(offset, one, 0, 0);
				} else if (currIndex == 2) {
					animation = new TranslateAnimation(two, one, 0, 0);
				}
				break;
			case 2:
				if (currIndex == 0) {
					animation = new TranslateAnimation(offset, two, 0, 0);
				} else if (currIndex == 1) {
					animation = new TranslateAnimation(one, two, 0, 0);
				}
				break;
			}
			currIndex = arg0;
			animation.setFillAfter(true);// True:图片停在动画结束位置
			animation.setDuration(300);
			if (arg0 == 0) {
				mProTextView.setSelected(true);
				mCityTextView.setSelected(false);
				mCountyTextView.setSelected(false);
			} else if (arg0 == 1) {
				mCityTextView.setSelected(true);
				mProTextView.setSelected(false);
				mCountyTextView.setSelected(false);
			} else {
				mCountyTextView.setSelected(true);
				mProTextView.setSelected(false);
				mCityTextView.setSelected(false);
			}
			mAreaQueryTextView.setSelected(false);
			SelectAreaActivity.this.mVgAreaQueryLayout.setVisibility(View.GONE);
			SelectAreaActivity.this.mVgListSelectLayout
					.setVisibility(View.VISIBLE);
			Log.d("当前页:-----", String.valueOf(arg0));
			// cursor.startAnimation(animation);
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}
	}

	private List<Canton> getAllProviceInfos() {
		return SelectAreaActivity.this.mCantonDBExOpt.getAllProviceInfos();
	}

	private List<Canton> getCityInfosByProID(int cantonID) {
		return SelectAreaActivity.this.mCantonDBExOpt
				.getCityInfosByProID(cantonID);
	}

	private List<Canton> getCountyInfosByCantonID(int cantonID) {
		return SelectAreaActivity.this.mCantonDBExOpt
				.getCountyInfosByCantonID(cantonID);
	}

	private void setCurrentFlipperPage(int paramInt) {
		if (this.viewPager == null) {
			return;
		}
		this.m_nCurrentPage = paramInt;
		this.viewPager.setCurrentItem(this.m_nCurrentPage);
	}

	private void selectProOpt(int paramInt) {// 选择省
		if (this.mProAreaData.size() <= paramInt) {
			return;
		}
		Canton localCanton1 = this.mProAreaData.get(paramInt);
		if ((SysConfig.mProCanton == null)
				|| (localCanton1.getCantonId() != SysConfig.mProCanton
						.getCantonId())) {
			SysConfig.mProCanton = localCanton1;
			SysConfig.mCityCanton = null;
			SysConfig.mCountyCanton = null;
			refreshArea();
		}
		this.mCityAreaData.clear();
		Canton localCanton2 = new Canton();
		localCanton2.setCantonId(SysConfig.mProCanton.getCantonId());
		localCanton2.setCantonName("省级");
		this.mCityAreaData.add(localCanton2);
		if (getCityInfosByProID(localCanton1.getCantonId()) != null) {
			this.mCityAreaData.addAll(getCityInfosByProID(SysConfig.mProCanton
					.getCantonId()));
		} else {
			return;
		}
		mProTextView.setText(SysConfig.mProCanton.getCantonName());
		mCityTextView.setText("市级");
		this.mCityLstView.invalidateViews();
		setCurrentFlipperPage(1);
	}

	private void selectCityOpt(int paramInt)// 市
	{
		if (this.mCityAreaData.size() <= paramInt) {
			return;
		}
		Canton localCanton1 = this.mCityAreaData.get(paramInt);
		if ((SysConfig.mCityCanton == null)
				|| (localCanton1.getCantonId() != SysConfig.mCityCanton
						.getCantonId())) {
			SysConfig.mCityCanton = localCanton1;
			SysConfig.mCountyCanton = null;
			refreshAreaText();
		}
		this.mCountyAreaData.clear();
		if (localCanton1.getCantonName() == "省级") {// 跳向单位
			SysConfig.mCurrentCanton.setCantonId(localCanton1.getCantonId());
			SelectAreaActivity.this
					.startActivityForResult(
							new Intent(
									SelectAreaActivity.this,SelectUnitActivity.class),
							0);
		} else {// 跳向县
			Canton localCanton2 = new Canton();
			localCanton2.setCantonId(SysConfig.mCityCanton.getCantonId());
			localCanton2.setCantonName("市级");
			this.mCountyAreaData.add(localCanton2);
			if (getCountyInfosByCantonID(localCanton1.getCantonId()) != null) {// 当该市下边有县时，显示县
				this.mCountyAreaData
						.addAll(getCountyInfosByCantonID(SysConfig.mCityCanton
								.getCantonId()));
			} else {
				return;
			}
		}
		mProTextView.setText(SysConfig.mProCanton.getCantonName());
		mCityTextView.setText(SysConfig.mCityCanton.getCantonName());
		this.mCountyLstView.invalidateViews();
		setCurrentFlipperPage(2);
	}

	public void onBackBtnClick(View paramView) {
		SysConfig.mUnitInfo = null;
		SysConfig.mHumanInfo = null;
		setResult(0);
		finish();
	}

	private void refreshArea() {
		refreshAreaText();
		if (this.mProLstView != null)
			this.mProLstView.invalidateViews();
		if (this.mCityLstView != null)
			this.mCityLstView.invalidateViews();
		if (this.mCountyLstView != null)
			this.mCountyLstView.invalidateViews();
	}

	private void refreshAreaText() {
		if (SysConfig.mProCanton != null) {
			this.mProTextView.setText(SysConfig.mProCanton.getCantonName());
			if (SysConfig.mCityCanton != null) {
				this.mCityTextView.setText(SysConfig.mCityCanton
						.getCantonName());
			}
		}
		if (SysConfig.mCityCanton != null) {
			this.mCityTextView.setText(SysConfig.mCityCanton.getCantonName());
		}

	}

}