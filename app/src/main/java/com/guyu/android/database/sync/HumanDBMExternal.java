package com.guyu.android.database.sync;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.guyu.android.database.DBOptExternal;
import com.guyu.android.gis.common.Human;
import com.guyu.android.utils.MLog;

public class HumanDBMExternal extends DBOptExternal {

	private static final String FILE_CALL_PHONE_STRING = "telephone";
	private static final String FILE_CODE_STRING = "humanId";
	private static final String FILE_NAME_STRING = "humanName";
	private static final String FILE_OFFICE_CODE_STRING = "unitId";
	private static final String FILE_PWD_STRING = "password";
	private static final String TABLE_NAME = "tbhuman";
	private static final String TAG = "HumanDBM";

	public HumanDBMExternal(Context paramContext) {
		super(paramContext, "tbHuman");
	}

	public HumanDBMExternal(Context paramContext, String paramString) {
		super(paramContext, paramString);
	}

	public List<Human> getAllHuman() {
		Cursor localCursor = query();
		if ((localCursor != null) && (localCursor.getCount() > 0)) {
			ArrayList<Human> localArrayList = new ArrayList<Human>();
			while (true) {
				if (!(localCursor.moveToNext())) {
					localCursor.close();
					return localArrayList;
				}
				Human human = new Human();
				human.setHumanName(localCursor.getString(localCursor
						.getColumnIndex("humanName")));
				human.setHumanId(localCursor.getInt(localCursor
						.getColumnIndex("humanId")));
				human.setUnitId(localCursor.getInt(localCursor
						.getColumnIndex("unitId")));
				human.setGender(localCursor.getInt(localCursor
						.getColumnIndex("gender")));
				human.setStatus(localCursor.getInt(localCursor
						.getColumnIndex("status")));
				human.setTelephone(localCursor.getString(localCursor
						.getColumnIndex("telephone")));
				human.setCellphone(localCursor.getString(localCursor
						.getColumnIndex("cellphone")));
				human.setHomephone(localCursor.getString(localCursor
						.getColumnIndex("homephone")));
				human.setEmail(localCursor.getString(localCursor
						.getColumnIndex("email")));
				human.setAddress(localCursor.getString(localCursor
						.getColumnIndex("address")));
				if (localCursor.getColumnIndex("password") >= 0)
					human.setPassword(localCursor.getString(localCursor
							.getColumnIndex("password")));
				localArrayList.add(human);
			}
		}
		MLog.e("HumanDBMExternal", "human记录为空");
		return null;
	}

	public List<Human> getAllHumanByUnitId(int unitID) {
		Cursor localCursor = query(null, "unitId = ?", new String[] { ""
				+ unitID }, null, null, null);
		if (localCursor != null) {
			ArrayList<Human> localArrayList = new ArrayList<Human>();
			while (true) {
				if (!(localCursor.moveToNext())) {
					localCursor.close();
					return localArrayList;
				}
				Human human = new Human();
				human.setHumanName(localCursor.getString(localCursor
						.getColumnIndex("humanName")));
				human.setHumanId(localCursor.getInt(localCursor
						.getColumnIndex("humanId")));
				human.setUnitId(localCursor.getInt(localCursor
						.getColumnIndex("unitId")));
				human.setGender(localCursor.getInt(localCursor
						.getColumnIndex("gender")));
				human.setStatus(localCursor.getInt(localCursor
						.getColumnIndex("status")));
				human.setTelephone(localCursor.getString(localCursor
						.getColumnIndex("telephone")));
				human.setCellphone(localCursor.getString(localCursor
						.getColumnIndex("cellphone")));
				human.setHomephone(localCursor.getString(localCursor
						.getColumnIndex("homephone")));
				human.setEmail(localCursor.getString(localCursor
						.getColumnIndex("email")));
				human.setAddress(localCursor.getString(localCursor
						.getColumnIndex("address")));

				if (localCursor.getColumnIndex("password") >= 0)
					human.setPassword(localCursor.getString(localCursor
							.getColumnIndex("password")));
				localArrayList.add(human);
			}
		}
		MLog.e("HumanDBMExternal", "getAllHumanByUnitId未查询到相关信息");
		return null;
	}

	public Human getHumanByHumanId(String humanID) {
		Cursor localCursor = query(null, "humanID = ?",
				new String[] { humanID }, null, null, null);
		if (localCursor != null) {
			Human human = null;
			if (localCursor.getCount() == 1) {
				localCursor.moveToFirst();
				human = new Human();
				human.setHumanName(localCursor.getString(localCursor
						.getColumnIndex("humanName")));
				human.setHumanId(localCursor.getInt(localCursor
						.getColumnIndex("humanId")));
				human.setUnitId(localCursor.getInt(localCursor
						.getColumnIndex("unitId")));
				human.setGender(localCursor.getInt(localCursor
						.getColumnIndex("gender")));
				human.setStatus(localCursor.getInt(localCursor
						.getColumnIndex("status")));
				human.setTelephone(localCursor.getString(localCursor
						.getColumnIndex("telephone")));
				human.setCellphone(localCursor.getString(localCursor
						.getColumnIndex("cellphone")));
				human.setHomephone(localCursor.getString(localCursor
						.getColumnIndex("homephone")));
				human.setEmail(localCursor.getString(localCursor
						.getColumnIndex("email")));
				human.setAddress(localCursor.getString(localCursor
						.getColumnIndex("address")));
				if (localCursor.getColumnIndex("password") >= 0)
					human.setPassword(localCursor.getString(localCursor
							.getColumnIndex("password")));
			} else {
				localCursor.close();
				human = null;
				return human;
			}
		}
		return null;
	}

	public boolean isExistPeo(String paramString) {
		return (getHumanByHumanId(paramString) != null);
	}

	public boolean deleteAllData() {
		return delete(null, null);
	}

	public void updateData(List<Human> listHuman) {
		deleteAllData();
		for (Human human : listHuman) {
			insertOneHuman(human);
		}
	}

	/**
	 * 插入人员信息
	 * 
	 * @param human
	 * @return
	 */
	public boolean insertOneHuman(Human human) {

		ContentValues localContentValues = new ContentValues();
		localContentValues.put("humanId", human.getHumanId());
		localContentValues.put("humanName", human.getHumanName());
		localContentValues.put("password", human.getPassword());
		localContentValues.put("gender", human.getGender());
		localContentValues.put("status", human.getStatus());
		localContentValues.put("unitId", human.getUnitId());
		localContentValues.put("telephone", human.getTelephone());
		localContentValues.put("cellphone", human.getCellphone());
		localContentValues.put("homephone", human.getHomephone());
		localContentValues.put("address", human.getAddress());
		localContentValues.put("email", human.getEmail());
		localContentValues.put("employeeId", human.getEmployeeId());
		localContentValues.put("dispOrder", human.getDispOrder());
		return insert(localContentValues);
	}
}
