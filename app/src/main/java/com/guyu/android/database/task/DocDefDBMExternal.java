package com.guyu.android.database.task;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;


import com.guyu.android.database.DBOptExternal;
import com.guyu.android.gis.common.DocDef;

public class DocDefDBMExternal extends DBOptExternal{//要件定义

	public DocDefDBMExternal(Context paramContext) {
		super(paramContext, "tbDocDef");
	}

	public DocDefDBMExternal(Context paramContext, String paramString) {
		super(paramContext, paramString);
	}
	

	/**
	 * 插入多个要件定义信息
	 * @param docDefList
	 * @return
	 */
	public void insertMultiDocDefInfo(List<DocDef> docDefList) {		
		if (docDefList != null) {
			Iterator<DocDef> localIterator = docDefList.iterator();
			while (localIterator.hasNext()) {
				insertOneDocDefInfo(localIterator.next());
			}
		}
	}
	/**
	 * 插入一个要件定义信息
	 * @param paramDocDefObj
	 * @return
	 */
	public boolean insertOneDocDefInfo(DocDef paramDocDefObj) {

		ContentValues localContentValues = new ContentValues();
		localContentValues.put("docDefId", paramDocDefObj.getDocDefID());		
		localContentValues.put("docDefName", paramDocDefObj.getDocDefName());
		localContentValues.put("bizId", paramDocDefObj.getBizID());
		localContentValues.put("dispOrder", paramDocDefObj.getDispOrder());		
		return insert(localContentValues);
	}
	
	/**
	 * 删除一个要件定义信息
	 * @param bizId
	 * @return
	 */
	public boolean deleteDocDefInfoByBizId(String bizId) {
		String[] arrayOfString = new String[1];
		arrayOfString[0] = bizId;
		return delete("bizId = ? ", arrayOfString);
	}
	/**
	 * 删除多个要件定义信息
	 * @param docDefList
	 * @return
	 */
	public void deleteWphcInfo(List<DocDef> docDefList) {	
		if (docDefList != null) {
			Iterator<DocDef> localIterator = docDefList.iterator();
			while (localIterator.hasNext()) {
				deleteDocDefInfoByBizId(localIterator.next().getBizID());
			}
		}

	}

	
	/**
	 * 根据业务ID查找要件定义信息
	 * @param bizId
	 * @return
	 */
	public List<DocDef> queryDocDefInfoByBizId(String bizId) {
		ArrayList<DocDef> localArrayList = new ArrayList<DocDef>();
		String[] arrayOfString = new String[1];
		arrayOfString[0] = bizId;
		Cursor localCursor = query(null, "bizId = ?",
				arrayOfString, null, null, null);		
		if ((localCursor != null) && (localCursor.getCount() > 0)) {
			while (true) {
				if (!(localCursor.moveToNext())) {
					localCursor.close();
					return localArrayList;
				}
				DocDef localLandObj = new DocDef();
				localLandObj.setBizID(localCursor.getString(localCursor.getColumnIndex("bizId")));
				localLandObj.setDocDefID(localCursor.getString(localCursor.getColumnIndex("docDefId")));
				localLandObj.setDocDefName(localCursor.getString(localCursor.getColumnIndex("docDefName")));
				localLandObj.setDispOrder(localCursor.getInt(localCursor.getColumnIndex("dispOrder")));
				localArrayList.add(localLandObj);
			}
		}
		return localArrayList;		
	}	
	
}
