package com.guyu.android.database.sync;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.guyu.android.database.DBOptExternal;
import com.guyu.android.gis.common.Unit;

public class UnitDBMExternal extends DBOptExternal {

	public UnitDBMExternal(Context paramContext) {
		super(paramContext, "tbUnit");
	}

	public UnitDBMExternal(Context paramContext, String paramString) {
		super(paramContext, paramString);
	}

	public List<Unit> getOfficeInfosByCantonCode(int cantonCode) {
		String[] arrayOfString = new String[1];
		arrayOfString[0] = "" + cantonCode;
		Cursor localCursor = query(null, "cantonCode = ?", arrayOfString, null,
				null, null);
		if ((localCursor != null) && (localCursor.getCount() > 0)) {
			ArrayList<Unit> localArrayList = new ArrayList<Unit>();
			while (true) {
				if (!(localCursor.moveToNext())) {
					localCursor.close();
					return localArrayList;
				}
				Unit unit = new Unit();
				unit.setUnitName(localCursor.getString(localCursor
						.getColumnIndex("unitName")));
				unit.setUnitId(localCursor.getInt(localCursor
						.getColumnIndex("unitId")));
				unit.setCantonCode(cantonCode);
				localArrayList.add(unit);
			}
		}
		return null;
	}

	public Unit getOfficeInfo(String paramString) {
		Cursor localCursor = query(null, "unitId = ?",
				new String[] { paramString }, null, null, null);
		if (localCursor != null) {
			Unit unit;
			if (localCursor.getCount() == 1) {
				localCursor.moveToFirst();
				unit = new Unit();
				unit.setUnitName(localCursor.getString(localCursor
						.getColumnIndex("unitName")));
				unit.setUnitId(localCursor.getInt(localCursor
						.getColumnIndex("unitId")));
			} else {
				localCursor.close();
				unit = null;
				return unit;
			}
		}
		return null;
	}

	public boolean deleteAllData() {
		return delete(null, null);
	}

	public void updateData(List<Unit> listUnit) {
		deleteAllData();
		for (Unit unit : listUnit) {
			insertOneUnit(unit);
		}
	}
	/**
	 * 插入部门信息
	 * 
	 * @param unit
	 * @return
	 */
	public boolean insertOneUnit(Unit unit) {

		ContentValues localContentValues = new ContentValues();
		localContentValues.put("unitId", unit.getUnitId());
		localContentValues.put("unitName", unit.getUnitName());
		localContentValues.put("cantonCode", unit.getCantonCode());
		localContentValues.put("parentId", unit.getParentId());
		localContentValues.put("dispOrder", unit.getDispOrder());
		return insert(localContentValues);
	}
}
