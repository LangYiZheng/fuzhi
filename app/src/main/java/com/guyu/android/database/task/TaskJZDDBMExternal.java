package com.guyu.android.database.task;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;


import com.guyu.android.database.DBOptExternal2;
import com.guyu.android.gis.common.TaskJZD;

public class TaskJZDDBMExternal extends DBOptExternal2 {

	public TaskJZDDBMExternal(Context paramContext) {
		super(paramContext, "tbTaskJZD");
	}

	public TaskJZDDBMExternal(Context paramContext, String paramString) {
		super(paramContext, paramString);
	}

	public List<TaskJZD> getAllTaskJZDInfos() {
		ArrayList<TaskJZD> localArrayList = new ArrayList<TaskJZD>();
		Cursor localCursor = query();
		if ((localCursor != null) && (localCursor.getCount() > 0))
			while (true) {
				if (!(localCursor.moveToNext())) {
					localCursor.close();
					return localArrayList;
				}
				TaskJZD localTaskObj = new TaskJZD();
				localTaskObj.setRecId(localCursor.getString(localCursor
						.getColumnIndex("recId")));
				localTaskObj.setKh(localCursor.getString(localCursor
						.getColumnIndex("kh")));
				localTaskObj.setDh(localCursor.getString(localCursor
						.getColumnIndex("dh")));
				localTaskObj.setXzb(localCursor.getString(localCursor
						.getColumnIndex("xzb")));
				localTaskObj.setYzb(localCursor.getString(localCursor
						.getColumnIndex("yzb")));
				localTaskObj.setCzf(localCursor.getString(localCursor
						.getColumnIndex("czf")));

				localArrayList.add(localTaskObj);
			}
		return localArrayList;
	}

	public List<TaskJZD> getTaskJZDInfosByRecid(String recid) {
		ArrayList<TaskJZD> localArrayList = new ArrayList<TaskJZD>();
		String[] arrayOfString = new String[1];
		arrayOfString[0] = recid;
		Cursor localCursor = query(null, "recId = ?", arrayOfString, null,
				null, null);
		if ((localCursor != null) && (localCursor.getCount() > 0))
			while (true) {
				if (!(localCursor.moveToNext())) {
					localCursor.close();
					return localArrayList;
				}
				TaskJZD localTaskObj = new TaskJZD();
				localTaskObj.setRecId(localCursor.getString(localCursor
						.getColumnIndex("recId")));
				localTaskObj.setKh(localCursor.getString(localCursor
						.getColumnIndex("kh")));
				localTaskObj.setDh(localCursor.getString(localCursor
						.getColumnIndex("dh")));
				localTaskObj.setXzb(localCursor.getString(localCursor
						.getColumnIndex("xzb")));
				localTaskObj.setYzb(localCursor.getString(localCursor
						.getColumnIndex("yzb")));
				localTaskObj.setCzf(localCursor.getString(localCursor
						.getColumnIndex("czf")));

				localArrayList.add(localTaskObj);
			}
		return localArrayList;
	}

	/**
	 * 插入一个任务信息
	 * 
	 * @param paramTaskObj
	 * @return
	 */
	public boolean insertOneTaskJZDInfo(TaskJZD paramTaskObj) {

		ContentValues localContentValues = new ContentValues();
		localContentValues.put("recId", paramTaskObj.getRecId());
		localContentValues.put("kh", paramTaskObj.getKh());
		localContentValues.put("dh", paramTaskObj.getDh());
		localContentValues.put("xzb", paramTaskObj.getXzb());
		localContentValues.put("yzb", paramTaskObj.getYzb());
		localContentValues.put("czf", paramTaskObj.getCzf());
		return insert(localContentValues);
	}

	/**
	 * 插入多个任务信息
	 * 
	 * @param taskList
	 * @return
	 */
	public void insertMultiTaskJZDInfo(List<TaskJZD> taskList) {
		if (taskList != null) {
			Iterator<TaskJZD> localIterator = taskList.iterator();
			while (localIterator.hasNext()) {
				insertOneTaskJZDInfo(localIterator.next());
			}
		}
	}

	/**
	 * 删除一个任务信息
	 * 
	 * @param recid
	 * @return
	 */
	public boolean deleteTaskJZDInfoByRecId(String recid) {
		String[] arrayOfString = new String[1];
		arrayOfString[0] = recid;
		return delete("recId = ? ", arrayOfString);
	}

	/**
	 * 删除多个任务信息
	 * 
	 * @param TaskList
	 * @return
	 */
	public void deleteTaskJZDInfo(List<TaskJZD> TaskList) {
		if (TaskList != null) {
			Iterator<TaskJZD> localIterator = TaskList.iterator();
			while (localIterator.hasNext()) {
				deleteTaskJZDInfoByRecId(localIterator.next().getRecId());
			}
		}

	}
}
