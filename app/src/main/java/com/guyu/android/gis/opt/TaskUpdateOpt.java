package com.guyu.android.gis.opt;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.os.Message;
import android.util.Log;

import com.guyu.android.database.task.DocDefDBMExternal;
import com.guyu.android.database.task.TaskDBMExternal;
import com.guyu.android.database.task.TaskJZDDBMExternal;
import com.guyu.android.gis.app.GisQueryApplication;
import com.guyu.android.gis.common.CallBack;
import com.guyu.android.gis.common.DocDef;
import com.guyu.android.gis.common.Task;
import com.guyu.android.gis.common.TaskJZD;
import com.guyu.android.utils.HttpUtils;
import com.guyu.android.utils.SysConfig;
import com.guyu.android.utils.UtilsTools;

public class TaskUpdateOpt {
	/**
	 * 更新所有任务列表
	 * 
	 * @param paramContext
	 * @param callback
	 *            回调
	 */
	public static void updateAllTaskList(Context paramContext, CallBack callback) {
		TaskUpdateOpt.updateTaskList(paramContext,"ajcc");
		TaskUpdateOpt.updateTaskList(paramContext,"wfxsjb");
		TaskUpdateOpt.updateTaskList(paramContext,"tdly");
		TaskUpdateOpt.updateTaskList(paramContext,"tbgl");//图斑管理 卫片核查
		TaskUpdateOpt.updateTaskList(paramContext,"bgtbgl");//变更图斑管理 年度变更调查
		if (callback != null) {
			callback.execute();
		}
	}

	/**
	 * 向服务器发送接收到任务信息通知
	 * 
	 * @param humanid
	 * @param recids
	 */
	public static void sendReceivedTaskMsg(String humanid, String recids) {
		String postUrl = GisQueryApplication.getApp().getProjectconfig().getTaskDownloadsConfig()
				.getUpdateReceivedUrl();
		System.out.println("通知服务器已接受到任务，服务地址：" + postUrl + " 人员ID：" + humanid
				+ " 案卷ID：" + recids);
		Map<String, String> params = new HashMap<String, String>();
		params.put("partId", humanid);
		params.put("recIds", recids);
		HttpUtils.submitPostData(postUrl, params, "utf-8");
	}

	/**
	 * 更新任务列表
	 * 
	 * @param paramContext
	 */
	public static boolean updateTaskList(Context paramContext,String taskType) {
		boolean isOK=false;
		String taskUpdateUrl =GisQueryApplication.getApp().getProjectconfig()
				.getTaskDownloadUrl(taskType)
				+ "&partId="
				+ SysConfig.mHumanInfo.getHumanId();		
		System.out.println("更新任务列表，服务地址：" + taskUpdateUrl);
		String bizId = getBizIdByTaskType(taskType);
		try {
			URL url = new URL(taskUpdateUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(5000);
			conn.setRequestMethod("GET");
			Log.i("CONN", conn.toString());
			if (conn.getResponseCode() == 200) {
				InputStream ins = conn.getInputStream();
				int rowNum = parseTaskData(ins, paramContext,bizId);
				if(rowNum > 0){
					System.out.println("更新的数目----------------------：rowNum：" + rowNum+";bizId:"+bizId+";type:"+taskType);
					String taskTypeVal=taskType;
					Message msg=new Message();
					msg.what=8899;
					msg.obj=taskTypeVal;
					msg.arg1=rowNum;
					MapOperate.mActivity.sendEmptyMessage(msg);
				}
			}
			isOK=true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return isOK;
	}

	/**
	 * 解析任务数据
	 * 
	 * @param ins
	 * @param paramContext
	 * @throws Exception
	 */
	private static int parseTaskData(InputStream ins, Context paramContext,String bizId)
			throws Exception {
		int rowNum=0;
		String taskinfo = UtilsTools.inputStream2String(ins);
		JSONObject jsonObject = new JSONObject(taskinfo);
		String xdsj = jsonObject.getString("XDSJ");
		JSONArray bizattrs = jsonObject.getJSONArray("BIZATTRS");
		JSONArray bizgeos = jsonObject.getJSONArray("BIZGEOS");
		JSONArray bizdocdefs = jsonObject.getJSONArray("BIZDOCDEFS");
		

		List<Task> listTask = new ArrayList<Task>();
		List<TaskJZD> listTaskJZD = new ArrayList<TaskJZD>();
		List<DocDef> listDocDef = new ArrayList<DocDef>();
		String recids="";
		//属性数据
		for(int i = 0;i < bizattrs.length();i++){
			JSONObject bizattr = bizattrs.getJSONObject(i);
			String recId = bizattr.getString("RECID");
			recids+=recId+",";
			Task task = new Task();
			task.setRecId(recId);
			task.setBizId(bizId);
			task.setAttrs(bizattr.toString());
			task.setXdsj(xdsj);
			listTask.add(task);
		}
		if(bizattrs.length()>0){
			recids = recids.substring(0, recids.length()-1);
		}		
		//图形数据
		for(int i = 0;i < bizgeos.length();i++){
			JSONObject bizjzd = bizgeos.getJSONObject(i);
			String recId = bizjzd.getString("RECID");
			String kh = bizjzd.getString("KH");
			String dh = bizjzd.getString("DH");
			String xzb = bizjzd.getString("XZB");
			String yzb = bizjzd.getString("YZB");
			String czf = bizjzd.getString("CZF");
			recids+=recId+",";
			TaskJZD taskJZD = new TaskJZD();
			taskJZD.setRecId(recId);
			taskJZD.setKh(kh);
			taskJZD.setDh(dh);
			taskJZD.setXzb(xzb);
			taskJZD.setYzb(yzb);
			taskJZD.setCzf(czf);
			listTaskJZD.add(taskJZD);
		}
		//要件定义数据
		for(int i = 0;i < bizdocdefs.length();i++){
			JSONObject bizdocdef = bizdocdefs.getJSONObject(i);
			String docDefID = bizdocdef.getString("DOCDEFID");
			String docDefName = bizdocdef.getString("DOCDEFNAME");
			String bizID = bizdocdef.getString("BIZID");
			int dispOrder = bizdocdef.getInt("DISPORDER");
			DocDef docDef = new DocDef();
			docDef.setDocDefID(docDefID);
			docDef.setDocDefName(docDefName);
			docDef.setBizID(bizID);
			docDef.setDispOrder(dispOrder);
			listDocDef.add(docDef);
		}
		
		if (listTask.size() > 0) {
			updateTaskInfo(listTask, paramContext);			
			sendReceivedTaskMsg("" + SysConfig.mHumanInfo.getHumanId(), recids);
			rowNum=rowNum+listTask.size();
		}
		
		if (listTaskJZD.size() > 0) {
			updateTaskJZDInfo(listTaskJZD, paramContext);
			rowNum=rowNum+listTaskJZD.size();
		}
		
		if (listDocDef.size() > 0) {
			updateDocDefInfo(listDocDef, bizId, paramContext);
			rowNum=rowNum+listDocDef.size();
		}
		return rowNum;
	}

	private static void updateTaskInfo(List<Task> taskList, Context paramContext) {
		new TaskDBMExternal(paramContext).deleteTaskInfo(taskList);
		new TaskDBMExternal(paramContext).insertMultiTaskInfo(taskList);
	}

	private static void updateTaskJZDInfo(List<TaskJZD> taskJZDList,
			Context paramContext) {
		new TaskJZDDBMExternal(paramContext).deleteTaskJZDInfo(taskJZDList);
		new TaskJZDDBMExternal(paramContext)
				.insertMultiTaskJZDInfo(taskJZDList);
	}
	
	private static void updateDocDefInfo(List<DocDef> docDefList, String bizId,	Context paramContext) {
		new DocDefDBMExternal(paramContext).deleteDocDefInfoByBizId(bizId);
		new DocDefDBMExternal(paramContext).insertMultiDocDefInfo(docDefList);
	}

	/**
	 * 根据任务类型获取业务ID
	 * @param taskType
	 * @return
	 */
	public static String getBizIdByTaskType(String taskType){
		return GisQueryApplication.getApp().getProjectconfig().getCaseUploadsConfig().getCaseUploadConfigMap().get(taskType).getBizId();
	}
}
