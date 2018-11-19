package com.guyu.android.utils;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class SorftKeyOpt
{
  public static void hideEditeTextSorftKey(Context paramContext, View paramView)
  {
    ((InputMethodManager)paramContext.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(paramView.getWindowToken(), 0);
  }

  public static void showEditeTextSorftKey(Context paramContext, View paramView)
  {
    ((InputMethodManager)paramContext.getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(paramView, 2);
  }
}