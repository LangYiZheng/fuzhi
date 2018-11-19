package com.guyu.android.database.zonenavi;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.guyu.android.database.DBOptExternal;
import com.guyu.android.gis.common.CantonZone;

/**
 * 区划导航
 * @author Administrator
 *
 */
public class CantonZoneDBMExternal extends DBOptExternal {


	public CantonZoneDBMExternal(Context paramContext) {
		super(paramContext, "tbgCantonZone");
	}

	public CantonZoneDBMExternal(Context paramContext, String paramString) {
		super(paramContext, paramString);
	}
	
	public CantonZone getAreaInfoByZoneCode(int zoneCode) {
		CantonZone localCantonZone = null;
		Cursor localCursor = query(new String[] { "zoneData", "zoneName" },
				"zoneCode = ?", new String[] { ""+zoneCode }, null, null, "dispOrder");
		if (localCursor != null) {
			if (localCursor.getCount() == 1) {
				localCursor.moveToFirst();
				localCantonZone = new CantonZone();
				localCantonZone.setZonedata(localCursor.getString(localCursor
						.getColumnIndex("zoneData")));
				localCantonZone.setZonename(localCursor.getString(localCursor
						.getColumnIndex("zoneName")));
				localCursor.close();
			}
		}
		return localCantonZone;
	}
	
	public CantonZone getCantonZoneByZoneCode(int zoneCode){
		Cursor localCursor = query(new String[] { "zoneData", "zoneName",
				"zoneId","zoneCode"}, "zoneCode = ?", new String[] { ""+zoneCode }, null,
		null, "dispOrder");
		if ((localCursor != null) && (localCursor.getCount() > 0)) {
			CantonZone localCantonZone=null;
			while (true) {
				if (!(localCursor.moveToNext())) {
					localCursor.close();
					return localCantonZone;
				}
				localCantonZone = new CantonZone();
				localCantonZone.setZonename(localCursor.getString(localCursor
						.getColumnIndex("zoneName")));
				localCantonZone.setZoneid(localCursor.getString(localCursor
						.getColumnIndex("zoneId")));
				localCantonZone.setZonecode(localCursor.getInt(localCursor
						.getColumnIndex("zoneCode")));
			}
		}
		return null;
	}
	
	public List<CantonZone> getZoneDataByParentid(String zoneid,String type) {
		Cursor localCursor = query(new String[] { "zoneData", "zoneName",
				"zoneId","zoneCode" }, "parentId = ?", new String[] { zoneid }, null,
				null, "dispOrder");
		List<CantonZone> localArrayList = new ArrayList<CantonZone>();
		if ((localCursor != null) && (localCursor.getCount() > 0)) {
			while (true) {
				if (!(localCursor.moveToNext())) {
					localCursor.close();
					return localArrayList;
				}
				CantonZone localCantonZone = new CantonZone();
				localCantonZone.setType(Integer.parseInt(type));
				localCantonZone.setZonename(localCursor.getString(localCursor
						.getColumnIndex("zoneName")));
				localCantonZone.setZoneid(localCursor.getString(localCursor
						.getColumnIndex("zoneId")));
				localCantonZone.setZonecode(localCursor.getInt(localCursor
						.getColumnIndex("zoneCode")));
				localArrayList.add(localCantonZone);
			}
		}
		return localArrayList;
	}

	/**
	 * 删除全部数据
	 * @param 
	 * @return 
	 * @return
	 */
	public boolean deleteAllCantonZone() {
		return delete(null, null);
	}
	/**
	 * 删除多个区划导航信息
	 * @param cantonZoneList
	 * @return
	 */
	public void deleteCantonZoneInfo(List<CantonZone> cantonZoneList) {
		System.out.println("删除cantonZoneList-----:"+cantonZoneList.size());
		if (cantonZoneList != null) {
			Iterator<CantonZone> localIterator = cantonZoneList.iterator();
			while (localIterator.hasNext()) {
				deleteCantonZoneInfoByZoneId(localIterator.next().getZoneid());
			}
		}
	}
	
	/**
	 * 删除一个区划导航
	 * @param zondeid
	 * @return
	 */
	public boolean deleteCantonZoneInfoByZoneId(String zondeid) {
		String[] arrayOfString = new String[1];
		arrayOfString[0] = zondeid;
		return delete("zoneId = ? ", arrayOfString);
	}
	
	/**
	 * 插入多个区划导航信息
	 * @param cantonZoneList
	 * @return
	 */
	public void insertMultiCantonZoneInfo(List<CantonZone> cantonZoneList) {
		System.out.println("插入cantonZoneList-----:"+cantonZoneList.size());
		if (cantonZoneList != null) {
			Iterator<CantonZone> localIterator = cantonZoneList.iterator();
			while (localIterator.hasNext()) {
				insertOneCantonZoneInfo(localIterator.next());
			}
		}
	}
	/**
	 * 插入一个区划导航
	 * @param paramCantonZoneObj
	 * @return
	 */
	public boolean insertOneCantonZoneInfo(CantonZone paramCantonZoneObj) {

		ContentValues localContentValues = new ContentValues();
		localContentValues.put("zoneId", paramCantonZoneObj.getZoneid());
		localContentValues.put("zoneName", paramCantonZoneObj.getZonename());
		localContentValues.put("zoneCode", paramCantonZoneObj.getZonecode());
		localContentValues.put("zoneData", paramCantonZoneObj.getZonedata());
		localContentValues.put("parentId", paramCantonZoneObj.getParentid());
		localContentValues.put("dispOrder", paramCantonZoneObj.getDisporder());
		return insert(localContentValues);
	}
}
