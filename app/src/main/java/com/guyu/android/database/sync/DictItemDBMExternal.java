package com.guyu.android.database.sync;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.guyu.android.database.DBOptExternal;
import com.guyu.android.gis.common.DictItem;
import com.guyu.android.gis.common.DictItemTwo;

public class DictItemDBMExternal extends DBOptExternal {

	public DictItemDBMExternal(Context paramContext) {
		super(paramContext, "tbDictItem");
	}

	public DictItemDBMExternal(Context paramContext, String paramString) {
		super(paramContext, paramString);
	}

	public List<DictItem> getDictItemsByType(int dictId) {
		ArrayList<DictItem> localArrayList = new ArrayList<DictItem>();
		String[] arrayOfString = new String[1];
		arrayOfString[0] = "" + dictId;
		Cursor localCursor = query(null, "dictId = ? ", arrayOfString, null, null, "dispOrder asc");
		while (localCursor.moveToNext()) {
			DictItem dictItem = new DictItem();
			dictItem.setDictId(localCursor.getInt(localCursor.getColumnIndex("dictId")));
			dictItem.setItemId(localCursor.getInt(localCursor.getColumnIndex("itemId")));
			dictItem.setItemLabel(localCursor.getString(localCursor.getColumnIndex("itemLabel")));
			dictItem.setItemValue(localCursor.getString(localCursor.getColumnIndex("itemValue")));
			dictItem.setParentId(localCursor.getInt(localCursor.getColumnIndex("parentId")));
			localArrayList.add(dictItem);
			
			
		}
		localCursor.close();
		return localArrayList;
	}
	
	//启高  二级字典
	public List<DictItemTwo> getDictItemsByType2(int dicttwoId) {
		ArrayList<DictItemTwo> localArrayList = new ArrayList<DictItemTwo>();
		String[] arrayOfString = new String[1];
		arrayOfString[0] = "" + dicttwoId;
		Cursor localCursor = query(null, "dicttwoId = ? ", arrayOfString, null, null, "disptwoOrder asc");
		while (localCursor.moveToNext()) {
			DictItemTwo dictItem = new DictItemTwo();
			dictItem.setDicttwoId(localCursor.getInt(localCursor.getColumnIndex("dicttwoId")));
			dictItem.setItemtwoId(localCursor.getInt(localCursor.getColumnIndex("itemtwoId")));
			dictItem.setItemtwoLabel(localCursor.getString(localCursor.getColumnIndex("itemtwoLabel")));
			dictItem.setItemtwoValue(localCursor.getString(localCursor.getColumnIndex("itemtwoValue")));
			dictItem.setParenttwoId(localCursor.getInt(localCursor.getColumnIndex("parenttwoId")));
			localArrayList.add(dictItem);
			
			
		}
		localCursor.close();
		return localArrayList;
	}
	
	public boolean deleteAllData() {
		return delete(null, null);
	}

	public void updateData(List<DictItem> listCanton) {
		deleteAllData();
		for (DictItem dictItem : listCanton) {
			insertOneDictItem(dictItem);
		}
	}

	/**
	 * 插入字典项信息
	 * 
	 * @param dictItem
	 * @return
	 */
	public boolean insertOneDictItem(DictItem dictItem) {

		ContentValues localContentValues = new ContentValues();
		localContentValues.put("itemId", dictItem.getItemId());
		localContentValues.put("dictId", dictItem.getDictId());
		localContentValues.put("itemLabel", dictItem.getItemLabel());
		localContentValues.put("itemValue", dictItem.getItemValue());
		localContentValues.put("parentId", dictItem.getParentId());
		localContentValues.put("dispOrder", dictItem.getDispOrder());
		return insert(localContentValues);
	}
}
