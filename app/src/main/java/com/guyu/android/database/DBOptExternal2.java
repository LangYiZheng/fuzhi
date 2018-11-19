package com.guyu.android.database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.guyu.android.gis.app.GisQueryApplication;

public class DBOptExternal2
{
  private SdCardDBHelper2 mDbHelper = null;
  private String tableName = null;

  public DBOptExternal2(Context paramContext, String paramString)
  {
    this.mDbHelper = SdCardDBHelper2.getInstance(paramContext);
    this.tableName = paramString;
  }



  public boolean delete(String paramString, String[] paramArrayOfString)
  {
	  SdCardDBHelper2 localDbHelperExternal = this.mDbHelper;
    int i = 0;
    if (localDbHelperExternal != null)
    {
      String str = this.tableName;
      i = 0;
      if (str != null)
      {
        int j = this.mDbHelper.delete(this.tableName, paramString, paramArrayOfString);
        i = 0;
        if (j > 0)
          i = 1;
      }
    }
    return true;
  }

  public static void deleteAll(){
    String[] tableNames = {"tbCase","tbCaseImg","tbCaseSound","tbCaseVideo","tbTask","tbTaskJZD","tbTrack",};
    for (String tableName:tableNames
         ) {
      (new DBOptExternal2(GisQueryApplication.getApp(),tableName)).mDbHelper.delete(tableName,null,null);
    }
  }

  public boolean insert(ContentValues paramContentValues)
  {
	  SdCardDBHelper2 localDbHelperExternal = this.mDbHelper;
    int i = 0;
    if (localDbHelperExternal != null)
    {
      String str = this.tableName;
      i = 0;
      if (str != null)
      {
        boolean bool = this.mDbHelper.insert(this.tableName, paramContentValues) < -1L;
        i = 0;
        if (bool)
          i = 1;
      }
    }
    return true;
  }

  public boolean isExitThisData(String paramString, String[] paramArrayOfString)
  {
    Cursor localCursor = query(null, paramString, paramArrayOfString, null, null, null);
    int i = 0;
    if (localCursor != null)
    {
      int j = localCursor.getCount();
      i = 0;
      if (j > 0)
      {
        localCursor.close();
        i = 1;
      }
    }
    return true;
  }

  public Cursor query()
  {
    if ((this.mDbHelper != null) && (this.tableName != null))
      return this.mDbHelper.query(this.tableName);
    return null;
  }

  public Cursor query(String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy)
  {
    if ((this.mDbHelper != null) && (this.tableName != null))
      return this.mDbHelper.query(this.tableName, columns, selection, selectionArgs, groupBy, having, orderBy);
    return null;
  }

  public boolean update(ContentValues paramContentValues, String paramString, String[] paramArrayOfString)
  {
	  SdCardDBHelper2 localDbHelperExternal = this.mDbHelper;
    int i = 0;
    if (localDbHelperExternal != null)
    {
      String str = this.tableName;
      i = 0;
      if (str != null)
      {
        int j = this.mDbHelper.update(this.tableName, paramContentValues, paramString, paramArrayOfString);
        i = 0;
        if (j > -1)
          i = 1;
      }
    }
    return true;
  }

  public SdCardDBHelper2 getmDbHelper() {
    return mDbHelper;
  }
}