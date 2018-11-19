package com.guyu.android.database.task;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.GeometryEngine;
import com.guyu.android.database.DBOptExternal2;
import com.guyu.android.gis.common.Case;
import com.guyu.android.gis.common.IllegalChange;
import com.guyu.android.gis.common.IllegalClues;
import com.guyu.android.gis.common.IllegalDongWu;
import com.guyu.android.gis.common.IllegalLand;
import com.guyu.android.gis.common.IllegalLinMu;
import com.guyu.android.gis.common.IllegalLinXia;
import com.guyu.android.gis.common.IllegalMuCai;
import com.guyu.android.gis.common.IllegalWeiFaOne;
import com.guyu.android.gis.opt.MapOperate;
import com.guyu.android.utils.SysConfig;

public class CaseDBMExternal extends DBOptExternal2 {
	private Context mContext;

	public CaseDBMExternal(Context paramContext) {
		super(paramContext, "tbCase");
		mContext = paramContext;

	}

	public CaseDBMExternal(Context paramContext, String paramString) {
		super(paramContext, paramString);
	}

	/**
	 * 查询案件信息
	 * 
	 * @param caseType
	 *            案件类型
	 * @return
	 */
	public List<Case> getCasesByType(String caseType) {
		ArrayList<Case> localArrayList = new ArrayList<Case>();
		String[] arrayOfString = new String[2];
		arrayOfString[0] = "" + SysConfig.mHumanInfo.getHumanId();
		arrayOfString[1] = caseType;
		Cursor localCursor = query(null, "partId = ? and caseType = ? ", arrayOfString, null, null, "createTime desc");
		while (localCursor.moveToNext()) {
			Case caseObj = null;
			if ("dtxc".equals(caseType)) {
				caseObj = new IllegalLand();
			} 
			else if ("wfxs".equals(caseType)) {
				caseObj = new IllegalClues();
			} 
			else if ("change".equals(caseType)) {///启高10.17
				caseObj = new IllegalChange();
			} 
			else if ("linxia".equals(caseType)) {///启高10.17
				caseObj = new IllegalLinXia();
			} 
			else if ("linmu".equals(caseType)) {///启高10.17
				caseObj = new IllegalLinMu();
			} 
			else if ("mucai".equals(caseType)) {///启高10.17
				caseObj = new IllegalMuCai();
			} 
			else if ("weifa".equals(caseType)) {///启高10.17
				caseObj = new IllegalWeiFaOne();
			} 
			else if ("dongwu".equals(caseType)) {///启高10.17
				caseObj = new IllegalDongWu();
			} 
			else {
				caseObj = new Case();
			}
			caseObj.setPartId(localCursor.getString(localCursor.getColumnIndex("partId")));
			caseObj.setCaseId(localCursor.getString(localCursor.getColumnIndex("caseId")));
			caseObj.setCaseName(localCursor.getString(localCursor.getColumnIndex("caseName")));
			caseObj.setCaseType(localCursor.getString(localCursor.getColumnIndex("caseType")));
			caseObj.setAttrs(localCursor.getString(localCursor.getColumnIndex("attrs")));
			caseObj.setGeos(localCursor.getString(localCursor.getColumnIndex("geos")));
			caseObj.setUpState(localCursor.getInt(localCursor.getColumnIndex("upState")));
			caseObj.setCreateTime(localCursor.getString(localCursor.getColumnIndex("createTime")));
			caseObj.setRecId(localCursor.getString(localCursor.getColumnIndex("recId")));
			localArrayList.add(caseObj);
		}
		localCursor.close();
		return localArrayList;
	}

	/**
	 * 根据案件ID 和案件类型
	 * 
	 * @param caseId
	 * @param caseType
	 * @return
	 */
	public Case getCaseByIdAndType(String caseId, String caseType) {
		String[] arrayOfString = new String[1];
		arrayOfString[0] = caseId;
		Cursor localCursor = query(null, " caseId = ? ", arrayOfString, null, null, null);
		Case caseObj = null;
		while (localCursor.moveToNext()) {
			if ("dtxc".equals(caseType)) {
				caseObj = new IllegalLand();
			} else if ("wfxs".equals(caseType)) {
				caseObj = new IllegalClues();
			} 
			//启高
			else if ("change".equals(caseType)) {
				caseObj = new IllegalChange();
			}
			//启高
			else if ("linxia".equals(caseType)) {
				caseObj = new IllegalLinXia();
			}
			//启高
			else if ("linmu".equals(caseType)) {
				caseObj = new IllegalLinMu();
			}
			//启高
			else if ("mucai".equals(caseType)) {
				caseObj = new IllegalMuCai();
			}
			//启高
			else if ("weifa".equals(caseType)) {
				caseObj = new IllegalWeiFaOne();
			}
			//启高
			else if ("dongwu".equals(caseType)) {
				caseObj = new IllegalDongWu();
			}
			
			else {
				caseObj = new Case();
			}
			caseObj.setPartId(localCursor.getString(localCursor.getColumnIndex("partId")));
			caseObj.setCaseId(localCursor.getString(localCursor.getColumnIndex("caseId")));
			caseObj.setCaseName(localCursor.getString(localCursor.getColumnIndex("caseName")));
			caseObj.setCaseType(localCursor.getString(localCursor.getColumnIndex("caseType")));
			caseObj.setAttrs(localCursor.getString(localCursor.getColumnIndex("attrs")));
			caseObj.setGeos(localCursor.getString(localCursor.getColumnIndex("geos")));
			caseObj.setUpState(Integer.parseInt(localCursor.getString(localCursor.getColumnIndex("upState"))));
			caseObj.setCreateTime(localCursor.getString(localCursor.getColumnIndex("createTime")));
			caseObj.setRecId(localCursor.getString(localCursor.getColumnIndex("recId")));
		}
		localCursor.close();
		return caseObj;
	}
	
	
	
	/**
	 * 根据案件ID
	 * 
	 * @param caseId
	 * @return
	 */
	public Case getCaseById(String caseId) {
		String[] arrayOfString = new String[1];
		arrayOfString[0] = caseId;
		Cursor localCursor = query(null, " caseId = ? ", arrayOfString, null, null, null);
		Case caseObj = null;
		while (localCursor.moveToNext()) {
			caseObj = new Case();
			caseObj.setPartId(localCursor.getString(localCursor.getColumnIndex("partId")));
			caseObj.setCaseId(localCursor.getString(localCursor.getColumnIndex("caseId")));
			caseObj.setCaseName(localCursor.getString(localCursor.getColumnIndex("caseName")));
			caseObj.setCaseType(localCursor.getString(localCursor.getColumnIndex("caseType")));
			caseObj.setAttrs(localCursor.getString(localCursor.getColumnIndex("attrs")));
			caseObj.setGeos(localCursor.getString(localCursor.getColumnIndex("geos")));
			caseObj.setUpState(Integer.parseInt(localCursor.getString(localCursor.getColumnIndex("upState"))));
			caseObj.setCreateTime(localCursor.getString(localCursor.getColumnIndex("createTime")));
			caseObj.setRecId(localCursor.getString(localCursor.getColumnIndex("recId")));
		}
		localCursor.close();
		return caseObj;
	}
	/**
	 * 插入多个案件信息
	 * 
	 * @param caseList
	 * @return
	 */
	public void insertMultiCase(List<Case> caseList) {
		if (caseList != null) {
			Iterator<Case> localIterator = caseList.iterator();
			while (localIterator.hasNext()) {
				insertOneCase(localIterator.next());
			}
		}

	}

	/**
	 * 插入一个案件信息
	 * 
	 * @param caseObj
	 * @return
	 */
	public boolean insertOneCase(Case caseObj) {

		ContentValues localContentValues = new ContentValues();
		localContentValues.put("partId", "" + SysConfig.mHumanInfo.getHumanId());
		localContentValues.put("caseId", caseObj.getCaseId());
		localContentValues.put("caseName", caseObj.getCaseName());
		localContentValues.put("caseType", caseObj.getCaseType());
		localContentValues.put("attrs", caseObj.getAttrs());
		localContentValues.put("geos", caseObj.getGeos());
		localContentValues.put("upState", 0);
		localContentValues.put("createTime", caseObj.getCreateTime());
		return insert(localContentValues);
	}

	/**
	 * 删除一个案件信息
	 * 
	 * @param caseID
	 * @return
	 */
	public boolean deleteCaseByCaseID(String caseID) {
		String[] arrayOfString = new String[2];
		arrayOfString[0] = "" + SysConfig.mHumanInfo.getHumanId();
		arrayOfString[1] = caseID;
		new CaseImgDBMExternal(this.mContext).deleteTheCaseImgInfoByCaseID(caseID);
		return delete("partId = ? and caseId = ?", arrayOfString);
	}

	/**
	 * 删除多个案件信息
	 * 
	 * @param caseList
	 * @return
	 */
	public void deleteMultiCase(List<Case> caseList) {
		if (caseList != null) {
			Iterator<Case> localIterator = caseList.iterator();
			while (localIterator.hasNext()) {
				deleteCaseByCaseID(localIterator.next().getCaseId());
			}
		}

	}

	/**
	 * 更新案件图形并更新属性中面积的属性
	 * 
	 * @param caseId
	 * @param geometry
	 * @return
	 */
	public boolean updateGeos(String caseId, Geometry geometry) {
		Case caseObj = getCaseById(caseId);
		String attrs = caseObj.getAttrs();
		JSONObject jo;
		try {
			jo = new JSONObject(attrs);
			double pfm = MapOperate.GetArea(geometry);
			if(jo.has("WFMJ")){
				jo.put("WFMJ", String.format("%.2f", pfm));
			}else if(jo.has("MJ")){
				double mm = pfm/ 666.66666666699996D;
				jo.put("MJ", String.format("%.2f", mm));
			}
			attrs = jo.toString();
		} catch (JSONException e) {		
			e.printStackTrace();
		}
		String geos = GeometryEngine.geometryToJson(MapOperate.map.getSpatialReference(), MapOperate.curGeometry);
		String[] arrayOfString = new String[2];
		arrayOfString[0] = "" + SysConfig.mHumanInfo.getHumanId();
		arrayOfString[1] = caseId;
		ContentValues localContentValues = new ContentValues();
		localContentValues.put("attrs", attrs);
		localContentValues.put("geos", geos);
		return update(localContentValues, "partId = ? and caseId = ?", arrayOfString);
		
	}

	/**
	 * 更新案件上传状态
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
	 * 更新案件属性
	 * 
	 * @param caseId
	 * @param attrs
	 * @return
	 */
	public boolean updateCaseAttrs(String caseId, String caseName,String attrs) {
		String[] arrayOfString = new String[2];
		arrayOfString[0] = "" + SysConfig.mHumanInfo.getHumanId();
		arrayOfString[1] = caseId;
		ContentValues localContentValues = new ContentValues();
		localContentValues.put("attrs", attrs);
		localContentValues.put("casename", caseName);
		return update(localContentValues, "partId = ? and caseId = ?", arrayOfString);
	}

	/**
	 * 更新案件RecId
	 * 
	 * @param caseId
	 * @param recId
	 * @return
	 */
	public boolean updateCaseRecId(String caseId, int recId) {
		String[] arrayOfString = new String[1];
		arrayOfString[0] = caseId;
		ContentValues localContentValues = new ContentValues();
		localContentValues.put("recId", recId);
		return update(localContentValues, "caseId = ?", arrayOfString);
	}

}