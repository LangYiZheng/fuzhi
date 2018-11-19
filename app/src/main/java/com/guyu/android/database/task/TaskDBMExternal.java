package com.guyu.android.database.task;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.guyu.android.database.DBOptExternal2;
import com.guyu.android.gis.common.Task;
import com.guyu.android.gis.common.TaskAjcc;
import com.guyu.android.gis.common.TaskBgtbgl;
import com.guyu.android.gis.common.TaskTbgl;
import com.guyu.android.gis.common.TaskTdly;
import com.guyu.android.gis.common.TaskWfxsjb;
import com.guyu.android.gis.opt.MapOperate;
import com.guyu.android.utils.SysConfig;

public class TaskDBMExternal extends DBOptExternal2 {

	public TaskDBMExternal(Context paramContext) {
		super(paramContext, "tbTask");
	}

	public TaskDBMExternal(Context paramContext, String paramString) {
		super(paramContext, paramString);
	}

	public List<Task> getTaskInfos(String bizId, String taskType) {
		
		ArrayList<Task> localArrayList = new ArrayList<Task>();
		String[] arrayOfString = new String[2];
		arrayOfString[0] = bizId;
		arrayOfString[1] = "" + SysConfig.mHumanInfo.getHumanId();
		Cursor localCursor = query(null, "bizId = ? and partId = ? ", arrayOfString, null, null, "xdsj desc");
		if (localCursor != null) {
			if (localCursor.getCount() > 0) {
				while (true) {
					if (!(localCursor.moveToNext())) {
						localCursor.close();
						return localArrayList;
					}
					Task localTaskObj = null;
					JSONObject attrJsonObj = null;
					String attrs = localCursor.getString(localCursor.getColumnIndex("attrs"));
					try {
						attrJsonObj = new JSONObject(attrs);
						if ("ajcc".equals(taskType)) {
							localTaskObj = new TaskAjcc();
							if(attrJsonObj.has("AJLY")){
								((TaskAjcc) localTaskObj).setAJLY(attrJsonObj.getString("AJLY"));
							}
							if(!attrJsonObj.getString("HCZT").equals("null") && !attrJsonObj.getString("HCZT").equals("")){
								((TaskAjcc) localTaskObj).setHczt(attrJsonObj.getInt("HCZT"));
							}else{
								localTaskObj.setHczt(0);
							}
						} else if ("wfxsjb".equals(taskType)) {
							localTaskObj = new TaskWfxsjb();
                            if(attrJsonObj.has("XSLY")){
                            	((TaskWfxsjb) localTaskObj).setXSLY(attrJsonObj.getString("XSLY"));
							}
							if(!attrJsonObj.getString("HCZT").equals("null") && !attrJsonObj.getString("HCZT").equals("")){
								((TaskWfxsjb) localTaskObj).setHczt(attrJsonObj.getInt("HCZT"));
							}else{
								localTaskObj.setHczt(0);
							}
						} else if ("tbgl".equals(taskType)) {
							localTaskObj = new TaskTbgl();
                            if(attrJsonObj.has("TBBH")){
                            	((TaskTbgl) localTaskObj).setTBBH(attrJsonObj.getString("TBBH"));
							}
							if(!attrJsonObj.getString("HCZT").equals("null") && !attrJsonObj.getString("HCZT").equals("")){
								((TaskTbgl) localTaskObj).setHczt(attrJsonObj.getInt("HCZT"));
							}else{
								localTaskObj.setHczt(0);
							}
						} else if ("bgtbgl".equals(taskType)) {
							localTaskObj = new TaskBgtbgl();
                            if(attrJsonObj.has("TBBH")){
                            	((TaskBgtbgl) localTaskObj).setTBBH(attrJsonObj.getString("TBBH"));
							}
							if(!attrJsonObj.getString("HCZT").equals("null") && !attrJsonObj.getString("HCZT").equals("")){
								((TaskBgtbgl) localTaskObj).setHczt(attrJsonObj.getInt("HCZT"));
							}else{
								localTaskObj.setHczt(0);
							}
						} else if ("tdly".equals(taskType)) {
							localTaskObj = new TaskTdly();
                            if(attrJsonObj.has("XMMC")){
                            	((TaskTdly) localTaskObj).setXMMC(attrJsonObj.getString("XMMC"));
							}
                            if(attrJsonObj.has("XCJD")){
                            	((TaskTdly) localTaskObj).setXCJD(attrJsonObj.getString("XCJD"));
							}
							if(!attrJsonObj.getString("HCZT").equals("null") && !attrJsonObj.getString("HCZT").equals("")){
								((TaskTdly) localTaskObj).setHczt(attrJsonObj.getInt("HCZT"));
							}else{
								localTaskObj.setHczt(0);
							}
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}

					localTaskObj.setTaskType(taskType);
					localTaskObj.setCaseId(localCursor.getString(localCursor.getColumnIndex("caseId")));
					localTaskObj.setRecId(localCursor.getString(localCursor.getColumnIndex("recId")));
					localTaskObj.setBizId(localCursor.getString(localCursor.getColumnIndex("bizId")));
					localTaskObj.setAttrs(attrs);
					localTaskObj.setXdsj(localCursor.getString(localCursor.getColumnIndex("xdsj")));

					localArrayList.add(localTaskObj);
				}
			} else {
				localCursor.close();
			}
		}

		return localArrayList;
	}

	/**
	 * 插入多个任务信息
	 * 
	 * @param taskList
	 * @return
	 */
	public void insertMultiTaskInfo(List<Task> taskList) {
		System.out.println("插入--任务--信息----------:" + taskList.size());
		if (taskList != null) {
			Iterator<Task> localIterator = taskList.iterator();
			while (localIterator.hasNext()) {
				insertOneTaskInfo(localIterator.next());
			}
		}
	}

	/**
	 * 插入一个任务信息
	 * 
	 * @param paramTaskObj
	 * @return
	 */
	public boolean insertOneTaskInfo(Task paramTaskObj) {

		ContentValues localContentValues = new ContentValues();
		localContentValues.put("caseId", MapOperate.CreateNewCaseId());
		localContentValues.put("recId", paramTaskObj.getRecId());
		localContentValues.put("bizId", paramTaskObj.getBizId());
		localContentValues.put("partId", SysConfig.mHumanInfo.getHumanId());
		localContentValues.put("attrs", paramTaskObj.getAttrs());
		localContentValues.put("xdsj", paramTaskObj.getXdsj());

		return insert(localContentValues);
	}

	/**
	 * 删除一个任务信息
	 * 
	 * @param caseID
	 * @return
	 */
	public boolean deleteTaskInfoByRecId(String caseID) {
		String[] arrayOfString = new String[1];
		arrayOfString[0] = caseID;
		return delete("caseId = ? ", arrayOfString);
	}

	/**
	 * 删除多个任务信息
	 * 
	 * @param TaskList
	 * @return
	 */
	public void deleteTaskInfo(List<Task> TaskList) {
		System.out.println("删除--任务--信息-----:" + TaskList.size());
		if (TaskList != null) {
			Iterator<Task> localIterator = TaskList.iterator();
			while (localIterator.hasNext()) {
				deleteTaskInfoByRecId(localIterator.next().getRecId());
			}
		}

	}
	
	/**
	 * 删除多个任务信息
	 * 
	 * @param TaskList
	 * @return
	 */
	public boolean deleteTaskInfos(List<Task> TaskList) {
		System.out.println("删除--任务--信息-----:" + TaskList.size());
		if (TaskList != null) {
			Iterator<Task> localIterator = TaskList.iterator();
			while (localIterator.hasNext()) {
				deleteTaskInfoByRecId(localIterator.next().getCaseId());
			}
			return true;
		}
		return false;

	}

	/**
	 * 更新任务图形
	 * 
	 * @param caseId
	 * @param geos
	 * @return
	 */
	public boolean updateGeos(String caseId, String geos) {
		String[] arrayOfString = new String[2];
		arrayOfString[0] = "" + SysConfig.mHumanInfo.getHumanId();
		arrayOfString[1] = caseId;
		ContentValues localContentValues = new ContentValues();
		localContentValues.put("geos", geos);
		return update(localContentValues, "partId = ? and caseId = ?", arrayOfString);
	}

	/**
	 * 更新任务上传状态
	 * 
	 * @param caseId
	 * @param upState
	 * @return
	 */
	public boolean updateUpState(String caseId, int upState) {
		String[] arrayOfString = new String[2];
		arrayOfString[0] = "" + SysConfig.mHumanInfo.getHumanId();
		arrayOfString[1] = caseId;
		ContentValues localContentValues = new ContentValues();
		localContentValues.put("upState", upState);
		return update(localContentValues, "partId = ? and caseId = ?", arrayOfString);
	}

	/**
	 * 更新任务属性
	 * 
	 * @param caseID
	 * @param attrs
	 * @return
	 */
	public boolean updateTaskAttrs(String caseID, String attrs) {
		String[] arrayOfString = new String[2];
		arrayOfString[0] = "" + SysConfig.mHumanInfo.getHumanId();
		arrayOfString[1] = caseID;
		ContentValues localContentValues = new ContentValues();
		localContentValues.put("attrs", attrs);
		return update(localContentValues, "partId = ? and caseId = ?", arrayOfString);
	}
}