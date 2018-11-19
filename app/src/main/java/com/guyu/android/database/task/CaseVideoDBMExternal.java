package com.guyu.android.database.task;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;

import com.guyu.android.database.DBOptExternal2;
import com.guyu.android.gis.app.GisQueryApplication;
import com.guyu.android.gis.common.CaseVideo;
import com.guyu.android.gis.opt.MapOperate;
import com.guyu.android.utils.FileUtil;
import com.guyu.android.utils.SysConfig;

public class CaseVideoDBMExternal extends DBOptExternal2 {

	public CaseVideoDBMExternal(Context paramContext) {
		super(paramContext, "tbCaseVideo");
	}

	public CaseVideoDBMExternal(Context paramContext, String paramString) {
		super(paramContext, paramString);
	}

	public boolean clearTheCaseVideoInfo(String paramString) {
		String[] arrayOfString = new String[2];
		arrayOfString[0] = "" + SysConfig.mHumanInfo.getHumanId();
		arrayOfString[1] = paramString;
		return delete("partId = ? and videoName = ?", arrayOfString);
	}

	public boolean clearTheCaseVideoInfoByCaseID(String caseID) {
		String[] arrayOfString = new String[2];
		arrayOfString[0] = "" + SysConfig.mHumanInfo.getHumanId();
		arrayOfString[1] = caseID;
		return delete("partId = ? and caseId = ?", arrayOfString);
	}

	public boolean deleteTheCaseVideoInfoByCaseID(String caseID) {
		// 先删除当前地块的图像文件
		List<CaseVideo> caseVideoList = getTheCaseAllVideo(caseID);
		Iterator<CaseVideo> localIterator = caseVideoList.iterator();
		while (localIterator.hasNext()) {
			CaseVideo caseVideoObj = localIterator.next();
			FileUtil.deleteFile(caseVideoObj.getVideoPath());
		}
		// 清空表数据
		String[] arrayOfString = new String[2];
		arrayOfString[0] = "" + SysConfig.mHumanInfo.getHumanId();
		arrayOfString[1] = caseID;
		return delete("partId = ? and caseId = ?", arrayOfString);
	}

	public List<CaseVideo> getTheCaseAllVideo(String caseID) {
		ArrayList<CaseVideo> localArrayList = new ArrayList<CaseVideo>();
		String[] arrayOfString = new String[2];
		if(SysConfig.mHumanInfo!=null){
			arrayOfString[0] = "" + SysConfig.mHumanInfo.getHumanId();
		}else{
			SharedPreferences sp_humanInfo = GisQueryApplication.getApp().getSharedPreferences("humanInfo",
					Context.MODE_PRIVATE);
			arrayOfString[0] = "" + sp_humanInfo.getInt("HUMAN_ID", -1);
		}
		arrayOfString[1] = caseID;
		Cursor localCursor = query(null, "partId = ? and caseId = ?",
				arrayOfString, null, null, null);
		while (localCursor.moveToNext()) {
			CaseVideo localCaseVideoObj = new CaseVideo();
			localCaseVideoObj.setPartId(localCursor.getString(localCursor
					.getColumnIndex("partId")));
			localCaseVideoObj.setCaseId(localCursor.getString(localCursor
					.getColumnIndex("caseId")));
			localCaseVideoObj.setDocDefId(localCursor.getString(localCursor
					.getColumnIndex("docDefId")));
			localCaseVideoObj.setVideoName(localCursor.getString(localCursor
					.getColumnIndex("videoName")));
			localCaseVideoObj.setVideoPath(localCursor.getString(localCursor
					.getColumnIndex("videoPath")));
			localCaseVideoObj.setThumbnailPath(localCursor.getString(localCursor
					.getColumnIndex("thumbnailPath")));
			localCaseVideoObj.setTime(localCursor.getLong(localCursor
					.getColumnIndex("time")));

			localArrayList.add(localCaseVideoObj);
		}
		localCursor.close();
		return localArrayList;
	}

	public void insertMultiCaseVideosInfo(List<CaseVideo> paramList) {
		if (paramList != null) {
			Iterator<CaseVideo> localIterator = paramList.iterator();
			while (localIterator.hasNext()) {
				insertOneCaseVideoInfo(localIterator.next());
			}
		}

	}

	public boolean insertOneCaseVideoInfo(CaseVideo paramCaseVideoObj) {
		ContentValues localContentValues = new ContentValues();
		localContentValues.put("partId",
				"" + SysConfig.mHumanInfo.getHumanId());
		localContentValues.put("videoName", paramCaseVideoObj.getVideoName());
		localContentValues.put("videoPath", paramCaseVideoObj.getVideoPath());
		localContentValues.put("thumbnailPath", paramCaseVideoObj.getThumbnailPath());
		localContentValues.put("caseId", paramCaseVideoObj.getCaseId());
		localContentValues.put("docDefId", paramCaseVideoObj.getDocDefId());
		localContentValues.put("time", Long.valueOf(paramCaseVideoObj.getTime()));
		localContentValues.put("prjName", MapOperate.getPrjName());
		return insert(localContentValues);
	}

	public boolean isExsitTheVideo(String paramString) {
		String[] arrayOfString = new String[2];
		arrayOfString[0] = "" + SysConfig.mHumanInfo.getHumanId();
		arrayOfString[1] = paramString;
		Cursor localCursor = query(null, "partId = ? and videoName = ?",
				arrayOfString, null, null, null);
		int i = 0;
		if (localCursor != null) {
			int j = localCursor.getCount();
			i = 0;
			if (j > 0) {
				i = 1;
				localCursor.close();
			}
		}
		return i == 1;
	}

}
