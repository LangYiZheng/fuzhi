package com.guyu.android.database.sync;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.guyu.android.database.DBOptExternal;
import com.guyu.android.gis.common.Canton;
import com.guyu.android.utils.MLog;

public class CantonDBMExternal extends DBOptExternal {

	public CantonDBMExternal(Context paramContext) {
		super(paramContext, "tbCanton");
	}

	public CantonDBMExternal(Context paramContext, String paramString) {
		super(paramContext, paramString);
	}

	public List<Canton> getAllProviceInfos()// 获取所有的省级
	{
		String[] arrayOfString = new String[1];
		arrayOfString[0] = Canton.TYPE_PROVINCE.toString();
		Cursor localCursor = query(null, "cantonlevel = ?", arrayOfString,
				null, null, null);
		if ((localCursor != null) && (localCursor.getCount() > 0)) {
			ArrayList<Canton> localArrayList = new ArrayList<Canton>();
			while (true) {
				if (!(localCursor.moveToNext())) {
					localCursor.close();
					return localArrayList;
				}
				Canton canton = new Canton();
				canton.setCantonId(localCursor.getInt(localCursor
						.getColumnIndex("cantonId")));
				canton.setCantonName(localCursor.getString(localCursor
						.getColumnIndex("cantonName")));
				canton.setParentId(localCursor.getInt(localCursor
						.getColumnIndex("parentId")));
				canton.setCantonLevel(localCursor.getInt(localCursor
						.getColumnIndex("cantonLevel")));
				localArrayList.add(canton);
			}
		}
		MLog.e("CantonDBM", "");
		return null;
	}

	public List<Canton> getCityInfosByProID(int cantonID) {
		String[] arrayOfString = new String[2];
		arrayOfString[0] = Canton.TYPE_CITY.toString();
		arrayOfString[1] = ""+cantonID;
		Cursor localCursor = query(null, " cantonlevel = ? and parentId = ? ",
				arrayOfString, null, null, null);
		if ((localCursor != null) && (localCursor.getCount() > 0)) {
			ArrayList<Canton> localArrayList = new ArrayList<Canton>();
			while (true) {
				if (!(localCursor.moveToNext())) {
					localCursor.close();
					return localArrayList;
				}
				Canton canton = new Canton();
				canton.setCantonId(localCursor.getInt(localCursor
						.getColumnIndex("cantonId")));
				canton.setCantonName(localCursor.getString(localCursor
						.getColumnIndex("cantonName")));
				canton.setParentId(localCursor.getInt(localCursor
						.getColumnIndex("parentId")));
				canton.setCantonLevel(localCursor.getInt(localCursor
						.getColumnIndex("cantonLevel")));
				localArrayList.add(canton);
			}
		}
		MLog.e("CantonDBM", "");
		return null;
	}

	public List<Canton> getCountyInfosByCantonID(int cantonID) {
		String[] arrayOfString = new String[2];
		arrayOfString[0] = Canton.TYPE_CONTY.toString();
		arrayOfString[1] = ""+cantonID;
		Cursor localCursor = query(null, " cantonlevel = ? and parentId = ? ",
				arrayOfString, null, null, null);
		if ((localCursor != null) && (localCursor.getCount() > 0)) {
			ArrayList<Canton> localArrayList = new ArrayList<Canton>();
			while (true) {
				if (!(localCursor.moveToNext())) {
					localCursor.close();
					return localArrayList;
				}
				Canton canton = new Canton();
				canton.setCantonId(localCursor.getInt(localCursor
						.getColumnIndex("cantonId")));
				canton.setCantonName(localCursor.getString(localCursor
						.getColumnIndex("cantonName")));
				canton.setParentId(localCursor.getInt(localCursor
						.getColumnIndex("parentId")));
				canton.setCantonLevel(localCursor.getInt(localCursor
						.getColumnIndex("cantonLevel")));
				localArrayList.add(canton);
			}
		}
		MLog.e("CantonDBM", "");
		return null;
	}

	public List<Canton> getAllCantonsByKeyWord(String paramString)// 查询
	{
		String[] arrayOfString = new String[1];
		arrayOfString[0] = "%" + paramString + "%";
		Cursor localCursor = query(null, "cantonName like ?", arrayOfString,
				null, null, null);
		if ((localCursor != null) && (localCursor.getCount() > 0)) {
			ArrayList<Canton> localArrayList = new ArrayList<Canton>();
			while (true) {
				if (!(localCursor.moveToNext())) {
					localCursor.close();
					return localArrayList;
				}
				Canton canton = new Canton();
				canton.setCantonId(localCursor.getInt(localCursor
						.getColumnIndex("cantonId")));
				canton.setCantonName(localCursor.getString(localCursor
						.getColumnIndex("cantonName")));
				canton.setParentId(localCursor.getInt(localCursor
						.getColumnIndex("parentId")));
				canton.setCantonLevel(localCursor.getInt(localCursor
						.getColumnIndex("cantonLevel")));
				localArrayList.add(canton);
			}
		}
		return null;
	}

	public Canton getFatherCantonByCode(int cantonId) {// 根据父节点ID获取父节点信息
		Cursor localCursor = query(null, "cantonID = ?",
				new String[] { ""+cantonId }, null, null, null);
		if (localCursor != null) {
			Canton canton = null;
			if (localCursor.getCount() == 1) {
				localCursor.moveToFirst();
				canton = new Canton();
				canton.setCantonId(localCursor.getInt(localCursor
						.getColumnIndex("cantonId")));
				canton.setCantonName(localCursor.getString(localCursor
						.getColumnIndex("cantonName")));
				canton.setParentId(localCursor.getInt(localCursor
						.getColumnIndex("parentId")));
				canton.setCantonLevel(localCursor.getInt(localCursor
						.getColumnIndex("cantonLevel")));

			}
			while (true) {
				localCursor.close();
				return canton;
			}
		}
		MLog.e("CantonDBM", "getFatherCantonByCode未查询到相关信息");
		return null;
	}

	public boolean deleteAllData() {
		return delete(null, null);
	}

	public void updateData(List<Canton> listCanton) {
		deleteAllData();
		for (Canton canton : listCanton) {
			insertOneCanton(canton);
		}

	}

	/**
	 * 插入行政区信息
	 * 
	 * @param canton
	 * @return
	 */
	public boolean insertOneCanton(Canton canton) {

		ContentValues localContentValues = new ContentValues();
		localContentValues.put("cantonId", canton.getCantonId());
		localContentValues.put("cantonName", canton.getCantonName());
		localContentValues.put("cantonLevel", canton.getCantonLevel());
		localContentValues.put("parentId", canton.getParentId());
		localContentValues.put("dispOrder", canton.getDispOrder());
		return insert(localContentValues);
	}
}