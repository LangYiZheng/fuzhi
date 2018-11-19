package com.guyu.android.database.sync;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.guyu.android.database.DBOptExternal;
import com.guyu.android.gis.common.Field;

public class FieldDBMExternal extends DBOptExternal {

	public FieldDBMExternal(Context paramContext) {
		super(paramContext, "tbField");
	}

	public FieldDBMExternal(Context paramContext, String paramString) {
		super(paramContext, paramString);
	}

	/**
	 * 查询所有字段信息
	 * 
	 * @return
	 */
	public Map<String, Field> getFieldMap() {
		Map<String, Field> map = new HashMap<String, Field>();
		Cursor localCursor = query(null, null, null, null, null, null);
		while (localCursor.moveToNext()) {
			Field f = new Field();
			f.setFieldId(localCursor.getInt(localCursor
					.getColumnIndex("fieldId")));
			f.setFieldName(localCursor.getString(localCursor
					.getColumnIndex("fieldName")));
			f.setFieldCnname(localCursor.getString(localCursor
					.getColumnIndex("fieldCnname")));
			f.setFieldType(localCursor.getInt(localCursor
					.getColumnIndex("fieldType")));
			f.setDataType(localCursor.getInt(localCursor
					.getColumnIndex("dataType")));
			f.setDictType(localCursor.getInt(localCursor
					.getColumnIndex("dictType")));
			f.setDispOrder(localCursor.getInt(localCursor
					.getColumnIndex("dispOrder")));
			map.put(f.getFieldName(), f);
		}
		localCursor.close();
		return map;
	}
	
	/**
	 * 查询所有字段信息
	 * 
	 * @return
	 */
	public Map<String, Field> getFieldMap(String selection,String[] selectionArr,String disporder) {
		Map<String, Field> map = new HashMap<String, Field>();
		Cursor localCursor = query(null, selection, selectionArr, null,null, disporder );
		while (localCursor.moveToNext()) {
			Field f = new Field();
			f.setFieldId(localCursor.getInt(localCursor
					.getColumnIndex("fieldId")));
			f.setFieldName(localCursor.getString(localCursor
					.getColumnIndex("fieldName")));
			f.setFieldCnname(localCursor.getString(localCursor
					.getColumnIndex("fieldCnname")));
			f.setFieldType(localCursor.getInt(localCursor
					.getColumnIndex("fieldType")));
			f.setDataType(localCursor.getInt(localCursor
					.getColumnIndex("dataType")));
			f.setDictType(localCursor.getInt(localCursor
					.getColumnIndex("dictType")));
			f.setDispOrder(localCursor.getInt(localCursor
					.getColumnIndex("dispOrder")));
			map.put(f.getFieldName(), f);
		}
		localCursor.close();
		return map;
	}
	
	/**
	 * 查询所有字段信息
	 * 
	 * @return
	 */
	public List<Field> getFieldList(String selection,String[] selectionArr,String disporder) {
		List<Field>  list = new ArrayList<Field>();
		Cursor localCursor = query(null, selection, selectionArr, null,null, disporder );
		while (localCursor.moveToNext()) {
			Field f = new Field();
			f.setFieldId(localCursor.getInt(localCursor
					.getColumnIndex("fieldId")));
			f.setFieldName(localCursor.getString(localCursor
					.getColumnIndex("fieldName")));
			f.setFieldCnname(localCursor.getString(localCursor
					.getColumnIndex("fieldCnname")));
			f.setFieldType(localCursor.getInt(localCursor
					.getColumnIndex("fieldType")));
			f.setDataType(localCursor.getInt(localCursor
					.getColumnIndex("dataType")));
			f.setDictType(localCursor.getInt(localCursor
					.getColumnIndex("dictType")));
			f.setDispOrder(localCursor.getInt(localCursor
					.getColumnIndex("dispOrder")));
			list.add(f);
		}
		localCursor.close();
		return list;
	}


	public boolean deleteAllData() {
		return delete(null, null);
	}

	public void updateData(List<Field> listField) {
		deleteAllData();
		for (Field field : listField) {
			insertOneField(field);
		}
	}

	/**
	 * 插入字段信息
	 * 
	 * @param field
	 * @return
	 */
	public boolean insertOneField(Field field) {

		ContentValues localContentValues = new ContentValues();
		localContentValues.put("fieldId", field.getFieldId());
		localContentValues.put("fieldName", field.getFieldName());
		localContentValues.put("fieldCnname", field.getFieldCnname());
		localContentValues.put("fieldType", field.getFieldType());
		localContentValues.put("dataType", field.getDataType());
		localContentValues.put("dictType", field.getDictType());
		localContentValues.put("dispOrder", field.getDispOrder());
		return insert(localContentValues);
	}
}
