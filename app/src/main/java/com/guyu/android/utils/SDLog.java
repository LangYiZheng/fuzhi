package com.guyu.android.utils;

public class SDLog
{
  private static SDWriter writer = SDWriter.GetInstance();

  public static void d(String paramString1, String paramString2)
  {
    writer.WriteTxtLog(paramString1 + "," + paramString2);
  }

  public static void e(String paramString1, String paramString2)
  {
    writer.WriteTxtLog(paramString1 + "," + paramString2);
  }

  public static void i(String paramString1, String paramString2)
  {
    writer.WriteTxtLog(paramString1 + "," + paramString2);
  }

  public static void v(String paramString1, String paramString2)
  {
    writer.WriteTxtLog(paramString1 + "," + paramString2);
  }

  public static void w(String paramString1, String paramString2)
  {
    writer.WriteTxtLog(paramString1 + "," + paramString2);
  }
}

/* Location:           C:\Users\Administrator\Desktop\一张图查询\classes_dex2jar.jar
 * Qualified Name:     com.utils.log.SDLog
 * JD-Core Version:    0.5.3
 */