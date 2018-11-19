package com.guyu.android.gis.activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import java.util.Iterator;
import java.util.List;

import com.guyu.android.gis.app.GisQueryApplication;

public class MiddleActivity extends Activity
{
  public static final String CONFIG_CODE = "CODE";
  public static final String CONFIG_DATA = "DATA";
  public static final int OPT_NONE = 0;
  public static final int OPT_SHOW = 3;

  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);

    Intent localIntent = getIntent();
    opt(localIntent.getIntExtra("CODE", 0), localIntent);
    finish();
  }

  public void opt(int paramInt, Intent paramIntent)
  {

  }
}