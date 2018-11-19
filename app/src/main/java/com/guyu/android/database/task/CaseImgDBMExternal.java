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
import com.guyu.android.gis.common.CaseImg;
import com.guyu.android.gis.opt.MapOperate;
import com.guyu.android.utils.FileUtil;
import com.guyu.android.utils.SysConfig;

public class CaseImgDBMExternal extends DBOptExternal2 {

	public CaseImgDBMExternal(Context paramContext) {
		super(paramContext, "tbCaseImg");
	}

	public CaseImgDBMExternal(Context paramContext, String paramString) {
		super(paramContext, paramString);
	}

	public boolean clearTheCaseImgInfo(String paramString) {
		String[] arrayOfString = new String[2];
		arrayOfString[0] = "" + SysConfig.mHumanInfo.getHumanId();
		arrayOfString[1] = paramString;
		return delete("partId = ? and imgName = ?", arrayOfString);
	}

	public boolean clearTheCaseImgInfoByCaseID(String caseID) {	
		String[] arrayOfString = new String[2];
		arrayOfString[0] = "" + SysConfig.mHumanInfo.getHumanId();
		arrayOfString[1] = caseID;
		return delete("partId = ? and caseId = ?", arrayOfString);
	}
	public boolean deleteTheCaseImgInfoByCaseID(String caseID) {
		//先删除当前地块的图像文件
		List<CaseImg> caseImgList = getTheCaseAllImg(caseID);
		Iterator<CaseImg> localIterator = caseImgList.iterator();
		while(localIterator.hasNext())
		{
			CaseImg caseImgObj=localIterator.next();
			FileUtil.deleteFile(caseImgObj.getImgPath());
		}
		//清空表数据
		String[] arrayOfString = new String[2];
		arrayOfString[0] = "" + SysConfig.mHumanInfo.getHumanId();
		arrayOfString[1] = caseID;
		return delete("partId = ? and caseId = ?", arrayOfString);
	}
	public List<CaseImg> getTheCaseAllImg(String caseID) {
		ArrayList<CaseImg> localArrayList = new ArrayList<CaseImg>();
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
			CaseImg localCaseImgObj = new CaseImg();
			localCaseImgObj.setPartId(localCursor.getString(localCursor
					.getColumnIndex("partId")));
			localCaseImgObj.setCaseId(localCursor.getString(localCursor
					.getColumnIndex("caseId")));
			localCaseImgObj.setDocDefId(localCursor.getString(localCursor
					.getColumnIndex("docDefId")));
			localCaseImgObj.setImgName(localCursor.getString(localCursor
					.getColumnIndex("imgName")));
			localCaseImgObj.setImgPath(localCursor.getString(localCursor
					.getColumnIndex("imgPath")));
			localCaseImgObj.setTime(localCursor.getLong(localCursor
					.getColumnIndex("time")));
			localArrayList.add(localCaseImgObj);
		}
		localCursor.close();
		return localArrayList;
	}

	public void insertMultiCaseImgsInfo(List<CaseImg> paramList) {
		if (paramList != null) {
			Iterator<CaseImg> localIterator = paramList.iterator();
			while (localIterator.hasNext()) {
				insertOneCaseImgInfo(localIterator.next());
			}
		}

	}

	public boolean insertOneCaseImgInfo(CaseImg paramCaseImgObj) {
		ContentValues localContentValues = new ContentValues();
		localContentValues.put("partId", "" + SysConfig.mHumanInfo.getHumanId());
		localContentValues.put("imgName", paramCaseImgObj.getImgName());
		localContentValues.put("imgPath", paramCaseImgObj.getImgPath());
		localContentValues.put("caseId", paramCaseImgObj.getCaseId());
		localContentValues.put("docDefId", paramCaseImgObj.getDocDefId());
		localContentValues.put("time", Long.valueOf(paramCaseImgObj.getTime()));
		localContentValues.put("prjName", MapOperate.getPrjName());
		return insert(localContentValues);
	}

	public boolean isExsitTheImg(String paramString) {
		String[] arrayOfString = new String[2];
		arrayOfString[0] = "" + SysConfig.mHumanInfo.getHumanId();
		arrayOfString[1] = paramString;
		Cursor localCursor = query(null, "partId = ? and imgName = ?",
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
