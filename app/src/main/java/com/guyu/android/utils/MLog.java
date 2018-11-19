package com.guyu.android.utils;

import android.util.Log;

public class MLog
{
  public static final int FLAG_LOGCAT_AND_SD = 3;
  public static final int FLAG_LOG_CAT = 1;
  public static final int FLAG_NO_LOG = 0;
  public static final int FLAG_WRITE_SD = 2;
  public static int currentFlag = 0;

  public static void d(String paramString1, String paramString2)
  {
    switch (currentFlag)
    {
    case 0:
    default:
      return;
    case 1:
      Log.d(paramString1, paramString2);
      return;
    case 2:
      SDLog.d(paramString1, paramString2);
      return;
    case 3:
    }
    Log.d(paramString1, paramString2);
    SDLog.d(paramString1, paramString2);
  }

  public static void e(String paramString1, String paramString2)
  {
    switch (currentFlag)
    {
    default:
      return;
    case 0:
    case 1:
    case 2:
    case 3:
    }
    Log.e(paramString1, paramString2);
    SDLog.e(paramString1, paramString2);
  }

  public static int getCurrentFlag()
  {
    return currentFlag;
  }

  public static void i(String paramString1, String paramString2)
  {
    switch (currentFlag)
    {
    case 0:
    default:
      return;
    case 1:
      Log.i(paramString1, paramString2);
      return;
    case 2:
      SDLog.i(paramString1, paramString2);
      return;
    case 3:
    }
    Log.i(paramString1, paramString2);
    SDLog.i(paramString1, paramString2);
  }

  public static void setCurrentFlag(int paramInt)
  {
    currentFlag = paramInt;
  }

  public static void v(String paramString1, String paramString2)
  {
    switch (currentFlag)
    {
    case 0:
    default:
      return;
    case 1:
      Log.v(paramString1, paramString2);
      return;
    case 2:
      SDLog.v(paramString1, paramString2);
      return;
    case 3:
    }
    Log.v(paramString1, paramString2);
    SDLog.v(paramString1, paramString2);
  }

  public static void w(String paramString1, String paramString2)
  {
    switch (currentFlag)
    {
    case 0:
    default:
      return;
    case 1:
      Log.w(paramString1, paramString2);
      return;
    case 2:
      SDLog.w(paramString1, paramString2);
      return;
    case 3:
    }
    Log.w(paramString1, paramString2);
    SDLog.w(paramString1, paramString2);
  }
}

/* Location:           C:\Users\Administrator\Desktop\一张图查询\classes_dex2jar.jar
 * Qualified Name:     com.utils.log.MLog
 * JD-Core Version:    0.5.3
 */