package com.guyu.android.utils;

import android.text.format.Time;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class SDWriter
{
  private static SDWriter instance = null;
  private File file;
  public String logFilePath = null;
  private BufferedWriter output;
  private Time time = new Time();

  private SDWriter()
  {
    initLogPath();
    if (this.logFilePath != null)
      this.file = new File(this.logFilePath);
    try
    {
      this.file.createNewFile();
      this.output = new BufferedWriter(new FileWriter(this.file));
      return;
    }
    catch (IOException localIOException)
    {
      localIOException.printStackTrace();
    }
  }

  public static SDWriter GetInstance()
  {
    if (instance == null)
      instance = new SDWriter();
    return instance;
  }

  private void initLogPath()
  {
    this.time.setToNow();
    String str1 = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Long.valueOf(this.time.toMillis(false)));
    String str2 = SysConfig.SDCardPath + SysConfig.AppName + File.separator + "Log/";
    if (!(new File(str2).exists()))
      new File(str2).mkdirs();
    this.logFilePath = str2 + "MLog_" + str1 + ".txt";
  }

  public void CloseLog()
  {
    try
    {
      this.output.close();
      return;
    }
    catch (IOException localIOException)
    {
      localIOException.printStackTrace();
    }
  }

  public void WriteTxtLog(String paramString)
  {
    this.time.setToNow();
    String str1 = new SimpleDateFormat("yyyy-MM-dd__HH:mm:ss", Locale.getDefault()).format(Long.valueOf(this.time.toMillis(false)));
    String str2 = "时间:----" + str1 + "---" + "\r\n";
    try
    {
      if (this.output != null)
      {
        this.output.write(str2);
        this.output.write(paramString + "\r\n");
        this.output.flush();
      }
      return;
    }
    catch (IOException localIOException)
    {
      localIOException.printStackTrace();
    }
  }

  public String getLogFilePath()
  {
    return this.logFilePath;
  }

  public void setLogFilePath(String paramString)
  {
    this.logFilePath = paramString;
  }
}

/* Location:           C:\Users\Administrator\Desktop\一张图查询\classes_dex2jar.jar
 * Qualified Name:     com.utils.log.SDWriter
 * JD-Core Version:    0.5.3
 */