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
import com.guyu.android.gis.common.CaseSound;
import com.guyu.android.gis.opt.MapOperate;
import com.guyu.android.utils.FileUtil;
import com.guyu.android.utils.SysConfig;

public class CaseSoundDBMExternal extends DBOptExternal2 {

	public CaseSoundDBMExternal(Context paramContext) {
		super(paramContext, "tbCaseSound");
	}

	public CaseSoundDBMExternal(Context paramContext, String paramString) {
		super(paramContext, paramString);
	}

	public boolean clearTheCaseSoundInfo(String paramString) {
		String[] arrayOfString = new String[2];
		arrayOfString[0] = "" + SysConfig.mHumanInfo.getHumanId();
		arrayOfString[1] = paramString;
		return delete("partId = ? and soundName = ?", arrayOfString);
	}

	public boolean clearTheCaseSoundInfoByCaseID(String caseID) {
		String[] arrayOfString = new String[2];
		arrayOfString[0] = "" + SysConfig.mHumanInfo.getHumanId();
		arrayOfString[1] = caseID;
		return delete("partId = ? and caseId = ?", arrayOfString);
	}

	public boolean deleteTheCaseSoundInfoByCaseID(String caseID) {
		// 先删除当前地块的图像文件
		List<CaseSound> caseSoundList = getTheCaseAllSound(caseID);
		Iterator<CaseSound> localIterator = caseSoundList.iterator();
		while (localIterator.hasNext()) {
			CaseSound caseSoundObj = localIterator.next();
			FileUtil.deleteFile(caseSoundObj.getSoundPath());
		}
		// 清空表数据
		String[] arrayOfString = new String[2];
		arrayOfString[0] = "" + SysConfig.mHumanInfo.getHumanId();
		arrayOfString[1] = caseID;
		return delete("partId = ? and caseId = ?", arrayOfString);
	}

	public List<CaseSound> getTheCaseAllSound(String caseID) {
		ArrayList<CaseSound> localArrayList = new ArrayList<CaseSound>();
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
			CaseSound localCaseSoundObj = new CaseSound();
			localCaseSoundObj.setPartId(localCursor.getString(localCursor
					.getColumnIndex("partId")));
			localCaseSoundObj.setCaseId(localCursor.getString(localCursor
					.getColumnIndex("caseId")));
			localCaseSoundObj.setDocDefId(localCursor.getString(localCursor
					.getColumnIndex("docDefId")));
			localCaseSoundObj.setSoundName(localCursor.getString(localCursor
					.getColumnIndex("soundName")));
			localCaseSoundObj.setSoundPath(localCursor.getString(localCursor
					.getColumnIndex("soundPath")));
			localCaseSoundObj.setTime(localCursor.getLong(localCursor
					.getColumnIndex("time")));

			localArrayList.add(localCaseSoundObj);
		}
		localCursor.close();
		return localArrayList;
	}

	public void insertMultiCaseSoundsInfo(List<CaseSound> paramList) {
		if (paramList != null) {
			Iterator<CaseSound> localIterator = paramList.iterator();
			while (localIterator.hasNext()) {
				insertOneCaseSoundInfo(localIterator.next());
			}
		}

	}

	public boolean insertOneCaseSoundInfo(CaseSound paramCaseSoundObj) {
		ContentValues localContentValues = new ContentValues();
		localContentValues.put("partId",
				"" + SysConfig.mHumanInfo.getHumanId());
		localContentValues.put("soundName", paramCaseSoundObj.getSoundName());
		localContentValues.put("soundPath", paramCaseSoundObj.getSoundPath());
		localContentValues.put("caseId", paramCaseSoundObj.getCaseId());
		localContentValues.put("docDefId", paramCaseSoundObj.getDocDefId());
		localContentValues.put("time", Long.valueOf(paramCaseSoundObj.getTime()));
		localContentValues.put("prjName", MapOperate.getPrjName());
		return insert(localContentValues);
	}

	public boolean isExsitTheSound(String paramString) {
		String[] arrayOfString = new String[2];
		arrayOfString[0] = "" + SysConfig.mHumanInfo.getHumanId();
		arrayOfString[1] = paramString;
		Cursor localCursor = query(null, "partId = ? and soundName = ?",
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
