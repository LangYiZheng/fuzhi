package com.guyu.android.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * 数据库管理和维护类
 **/
public class SdCardDBHelper extends SQLiteOpenHelper {

	public static final String TAG = "SdCardDBHelper";
	/**
	 * 数据库名称
	 **/
	public static String DATABASE_NAME = "iGisQuery.db";

	/**
	 * 数据库版本
	 **/
	public static int DATABASE_VERSION = 1;

	private static SdCardDBHelper mDbHelper = null;

	/**
	 * 构造函数
	 * 
	 * @param context
	 *            上下文环境
	 **/
	public SdCardDBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	public static SdCardDBHelper getInstance(Context paramContext) {
		if (mDbHelper == null) {
			DatabaseContext dbContext = new DatabaseContext(paramContext);
			mDbHelper = new SdCardDBHelper(dbContext);
		}
		return mDbHelper;
	}
	/**
	 * 置空单例
	 */
	public static void invalid()
	{
		mDbHelper = null;
	}
	/**
	 * 创建数据库时触发，创建离线存储所需要的数据库表
	 * 
	 * @param db
	 **/
	@Override
	public void onCreate(SQLiteDatabase db) {
		// Log.e(TAG, "开始创建数据库表");
		try {
			// 创建用户表(user)
			// db.execSQL("create table if not exists user"
			// +
			// "(_id integer primary key autoincrement,name varchar(20),password varchar(20),role varchar(10),updateTime varchar(20))");
			// Log.e(TAG, "创建离线所需数据库表成功");
		} catch (SQLException se) {
			se.printStackTrace();
			Log.e(TAG, "创建离线所需数据库表失败");
		}
	}
	/**
	 * 创建表
	 * @param sqlString
	 */
	public void createTable(String sqlString) {
		try {
			this.getWritableDatabase().execSQL(sqlString);
		} catch (SQLException localSQLException) {
			Log.e("SdCardDBHelper", "创建表错误:" + localSQLException.toString());
		}
	}
	/**
	 * 删除表
	 * @param sqlString
	 */
	public void deleteTable(String sqlString) {
		try {
			this.getWritableDatabase().execSQL(sqlString);
		} catch (SQLException localSQLException) {
			Log.e("SdCardDBHelper", "删除表错误:" + localSQLException.toString());
		}
	}
	/**
	 * 判断表是否存在
	 * @param tableName
	 * @return
	 */
	public boolean tableIsExist(String tableName) {
		if ((tableName == null) || (tableName.equals("")))
			return false;
		int i;
		try {
			Cursor localCursor = getReadableDatabase().rawQuery(
					"select count(*) as c from sqlite_master where type='table' and name='"
							+ tableName.trim() + "'", null);
			boolean bool = localCursor.moveToNext();
			i = 0;
			if (bool) {
				int j = localCursor.getInt(0);
				i = 0;
				if (j > 0)
					i = 1;
			}
			return i == 1;
		} catch (Exception localException) {
			i = 0;
			return false;
		}
	}
	/**
	 * 插入
	 * @param tableName
	 * @param values
	 * @return
	 */
	public long insert(String tableName, ContentValues  values) {
		return getWritableDatabase().insert(tableName, null,
				 values);
	}
	/**
	 * 删除
	 * @param tableName
	 * @param whereClause
	 * @param whereArgs
	 * @return
	 */
	public int delete(String tableName, String whereClause,
			String[] whereArgs) {
		return getWritableDatabase().delete(tableName, whereClause,
				whereArgs);
	}
	/**
	 * 查询
	 * @param tableName
	 * @return
	 */
	public Cursor query(String tableName) {
		return getReadableDatabase().query(tableName, null, null, null, null,
				null, null);
	}
	/**
	 * 多条件查询
	 * @param tableName
	 * @return
	 */
	public Cursor query(String tableName, String[] columns,
			String selection, String[] selectionArgs,
			String groupBy, String having, String orderBy) {
		return getReadableDatabase().query(tableName, columns,
				selection, selectionArgs, groupBy, having,
				orderBy);
	}
	/**
	 * 更新	
	 * @param tableName
	 * @param values
	 * @param whereClause
	 * @param whereArgs
	 * @return
	 */
	public int update(String tableName, ContentValues values,
			String whereClause, String[] whereArgs) {
		return getWritableDatabase().update(tableName, values,
				whereClause, whereArgs);
	}

	/**
	 * 更新数据库时触发，
	 * 
	 * @param db
	 * @param oldVersion
	 * @param newVersion
	 **/
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// db.execSQL("ALTER TABLE person ADD COLUMN other STRING");
	}
}