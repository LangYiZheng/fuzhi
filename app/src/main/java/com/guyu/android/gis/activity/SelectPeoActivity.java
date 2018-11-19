package com.guyu.android.gis.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.blankj.utilcode.util.ToastUtils;
import com.guyu.android.database.sync.HumanDBMExternal;
import com.guyu.android.gis.app.GisQueryApplication;
import com.guyu.android.gis.common.Human;
import com.guyu.android.utils.SysConfig;
import com.guyu.android.utils.UtilsTools;
import com.guyu.android.R;

public class SelectPeoActivity extends Activity
{
  private static final String TAG = "SelectPeoActivity";
  private HumanDBMExternal mPeoDBExOpt = null;
  private List<Human> mPeoLstData = new ArrayList();
  private ListView mPeopleListView = null;

  private List<Human> mAllPeoData = new ArrayList();
  private void initActivity()
  {
    initData();
    initWidget();
    setTilte("人员选择");
  }

  private void initData()
  {
    this.mPeoLstData.clear();
    refreshPeopleLstData();
  }

  private void initWidget()
  {
    this.mPeopleListView = ((ListView)findViewById(R.id.lst_peo_select));
    this.mPeopleListView.setAdapter(new PeoAdapter(this, this.mPeoLstData));
    this.mPeopleListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
    
    {
      public void onItemClick(AdapterView<?> paramAdapterView, View paramView, int paramInt, long paramLong)
      {
        SysConfig.mHumanInfo = SelectPeoActivity.this.mPeoLstData.get(paramInt);
        SelectPeoActivity.this.reusltOk();
      }
    });
    this.mPeopleListView.invalidateViews();
  }

  private void refreshPeopleLstData()
  {
    if (SysConfig.mUnitInfo == null){
    	ToastUtils.showLong(  "请选择单位信息!");
    	SysConfig.mHumanInfo = null;	
        setResult(0);
        finish();
        return;
    }
    this.mPeoLstData.clear();
    List localList = this.mPeoDBExOpt.getAllHumanByUnitId(SysConfig.mUnitInfo.getUnitId());
    if (localList != null){
    	this.mAllPeoData.addAll(localList);
    }else{
    	return;
    }
    if(mAllPeoData!=null){
    	this.mPeoLstData.addAll(mAllPeoData);
    }else{
    	return;
    }
  }

  private void reusltOk()
  {
    setResult(-1);
    finish();
  }

  private void setTilte(String paramString)
  {
    ((TextView)findViewById(R.id.tv_title)).setText(paramString);
  }

  public void onBackBtnClick(View paramView)
  {
	SysConfig.mHumanInfo = null;	
    setResult(0);
    finish();
  }

  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    setContentView(R.layout.activity_select_peo);

    this.mPeoDBExOpt = new HumanDBMExternal(this);
    initActivity();
  }

  private class PeoAdapter extends BaseAdapter
  {
    private LayoutInflater mInflater = null;
    private List<Human> peopleInfos = null;

    public PeoAdapter(Context paramContext,List<Human> paramList)
    {
      this.peopleInfos = paramList;
      this.mInflater = ((LayoutInflater)paramContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE));
    }

    public int getCount()
    {
        return this.peopleInfos.size();
    }

    public Object getItem(int paramInt)
    {
        return this.peopleInfos.get(paramInt);
    }

    public long getItemId(int paramInt)
    {
      return paramInt;
    }

    public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
    {
      if ((this.peopleInfos != null) && (paramInt < this.peopleInfos.size()))
      {
        if (paramView == null)
          paramView = this.mInflater.inflate(R.layout.spinner_peo, paramViewGroup, false);
        paramView.findViewById(R.id.record_item_head);
        ((TextView)paramView.findViewById(R.id.tv_peo_name)).setText(this.peopleInfos.get(paramInt).getHumanName());
      }
      return paramView;
    }
  }
}