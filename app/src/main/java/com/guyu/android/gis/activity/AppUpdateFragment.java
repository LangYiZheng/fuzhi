package com.guyu.android.gis.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.guyu.android.database.sync.CantonDBMExternal;
import com.guyu.android.database.sync.DictItemDBMExternal;
import com.guyu.android.database.sync.FieldDBMExternal;
import com.guyu.android.database.sync.HumanDBMExternal;
import com.guyu.android.database.sync.UnitDBMExternal;
import com.guyu.android.gis.app.GisQueryApplication;
import com.guyu.android.gis.common.CallBack;
import com.guyu.android.gis.common.Canton;
import com.guyu.android.gis.common.DictItem;
import com.guyu.android.gis.common.Field;
import com.guyu.android.gis.common.Human;
import com.guyu.android.gis.common.Unit;
import com.guyu.android.utils.UtilsTools;
import com.guyu.android.R;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

public class AppUpdateFragment extends Fragment {
	private LoginActivity fragmentActivity;
	private TextView txt_versionnum_app;
	private ProgressBar progressBar_app;
	private ProgressBar progressBar_db;
	private TextView txt_progress_app;
	private TextView txt_progress_db;
	private ImageButton btn_update_app;
	private ImageButton btn_update_db;

	private String url_canton = "/plugin/pdaSync/getCantonListByCantonCode.htm";
	private String url_unit = "/plugin/pdaSync/getUnitListByCantonCode.htm";
	private String url_human = "/plugin/pdaSync/getHumanListByCantonCode.htm";
	private String url_field = "/plugin/pdaSync/getFieldList.htm";
	private String url_dict = "/plugin/pdaSync/getDictItemList.htm";

	private int mFileLength = 0;
	private int mDownedFileLength = 0;
	private InputStream mInputStream;
	private URLConnection mConnection;
	private OutputStream mOutputStream;

	private String newVersionNum;
	private String newVersionDesc;
	private String downloadAddr;
	private ProgressDialog dialog = null;



	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		fragmentActivity = (LoginActivity) this.getActivity();
		return inflater.inflate(R.layout.layout_app_update, container, false);
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		txt_versionnum_app = (TextView) fragmentActivity.findViewById(R.id.txt_versionnum_app);
		progressBar_app = (ProgressBar) fragmentActivity.findViewById(R.id.progressBar_app);
		progressBar_db = (ProgressBar) fragmentActivity.findViewById(R.id.progressBar_db);
		txt_progress_app = (TextView) fragmentActivity.findViewById(R.id.txt_progress_app);
		txt_progress_db = (TextView) fragmentActivity.findViewById(R.id.txt_progress_db);
		btn_update_app = (ImageButton) fragmentActivity.findViewById(R.id.btn_updateApp);
		btn_update_db = (ImageButton) fragmentActivity.findViewById(R.id.btn_updateDb);

		txt_versionnum_app.setText(AppUtils.getAppVersionName());
		btn_update_app.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if (UtilsTools.isNetworkConnected(fragmentActivity)) {
					btn_update_app.setEnabled(false);
					doUpdateByCode(501);
				} else {
					ToastUtils.showLong( "您还没连上网络，请连网后再更新！");
				}

			}
		});
		btn_update_db.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if (UtilsTools.isNetworkConnected(fragmentActivity)) {
					ToastUtils.showLong( "开始更新数据库...");
					btn_update_db.setEnabled(false);
					doUpdateByCode(901);
				} else {
					ToastUtils.showLong( "您还没连上网络，请连网后再更新！");
				}
			}
		});
	}

	/**
	 * 显示加载信息
	 *
	 * @param loadingMessage
	 */
	protected void onPreExecute(String loadingMessage) {
		this.dialog = new ProgressDialog(this.fragmentActivity);
		this.dialog.setMessage(loadingMessage);
		this.dialog.setCancelable(false);
		this.dialog.show();
	}

	/**
	 * 数据库更新进度信息
	 */
	private void updateProcessStatus_db(int p_process) {
		progressBar_db.setProgress(p_process);
		txt_progress_db.setText("更新进度" + p_process + "%");
	}

	/**
	 * 应用更新下载进度信息
	 */
	private void updateProcessStatus_app(int p_process) {
		progressBar_app.setProgress(mDownedFileLength);
		txt_progress_app.setText("下载进度" + p_process + "%");
	}

	public void doUpdateByCode(int updateCode) {
		if (updateCode == 906) {
			updateProcessStatus_db(100);
			btn_update_db.setEnabled(true);
			ToastUtils.showLong( "数据库更新完毕！");
			return;
		}
		String url = "";
		switch (updateCode) {
		case 901: {
			url = GisQueryApplication.getApp().getProjectconfig().getIsysbaseurl() + url_canton + "?cantonCode=" + GisQueryApplication.getApp().getProjectconfig().getCantonZoneRootCode();
			break;
		}
		case 902: {
			updateProcessStatus_db(20);
			url = GisQueryApplication.getApp().getProjectconfig().getIsysbaseurl() + url_unit + "?cantonCode=" + GisQueryApplication.getApp().getProjectconfig().getCantonZoneRootCode();
			break;
		}
		case 903: {
			updateProcessStatus_db(40);
			url = GisQueryApplication.getApp().getProjectconfig().getIsysbaseurl() + url_human + "?cantonCode=" + GisQueryApplication.getApp().getProjectconfig().getCantonZoneRootCode();
			break;
		}
		case 904: {
			updateProcessStatus_db(60);
			url = GisQueryApplication.getApp().getProjectconfig().getIsysbaseurl() + url_field;
			break;
		}
		case 905: {
			updateProcessStatus_db(80);
			url = GisQueryApplication.getApp().getProjectconfig().getIsysbaseurl() + url_dict;
			break;
		}
		case 501: {// 软件版本检测
			onPreExecute("请稍等，正在检测是否有新版本...");
			url = GisQueryApplication.getApp().getProjectconfig().getAppVersionCheckUrl()
//					+ "?versionNum=" + txt_versionnum_app.getText()
			;
			break;
		}
		}
		UpdateThread updateThread = new UpdateThread(url, updateCode);
		new Thread(updateThread).start();

	}

	class SoftDownLoadThread implements Runnable {
		public void run() {
			AppUpdateFragment.this.downApp();
		}
	}

	class UpdateThread implements Runnable {
		private String url;
		private int updateCode;

		public UpdateThread(String p_url, int p_updateCode) {
			url = p_url;
			updateCode = p_updateCode;
		}

		public void run() {

			AppUpdateFragment.this.updateData(url, updateCode, new CallBack() {
				@Override
				public void execute() {
					fragmentActivity.mHandler.sendEmptyMessage(updateCode);
				}
			});
		}
	}

	private void updateData(String p_url, int updateCode, CallBack callBack) {
		try {
			URL url = new URL(p_url);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(5000);
			conn.setRequestMethod("GET");
			Log.i("CONN", conn.toString());
			if (conn.getResponseCode() == 200) {
				InputStream ins = conn.getInputStream();
				AppUpdateFragment.this.parseData(ins, updateCode, fragmentActivity);
			}
			if (callBack != null) {
				callBack.execute();
			}
		} catch (Exception e) {
			e.printStackTrace();
			fragmentActivity.mHandler.sendEmptyMessage(updateCode);
		}
	}

	private void parseData(InputStream ins, int updateCode, Context paramContext) {
		try {
			if (updateCode > 900) {
				parseData_db(ins, updateCode, paramContext);
			} else if (updateCode > 500) {
				parseData_app(ins, updateCode, paramContext);
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	private void parseData_db(InputStream ins, int updateCode, Context paramContext) {
		String resultData;
		try {
			resultData = UtilsTools.inputStream2String(ins);
			if ("".equals(resultData) == false) {
				JSONArray resultJsonArray = new JSONArray(resultData);
				if (updateCode == 901) {// 更新行政区
					List<Canton> listCanton = new ArrayList<Canton>();
					for (int i = 0; i < resultJsonArray.length(); i++) {
						JSONObject jsonObject = resultJsonArray.getJSONObject(i);
						int cantonId = jsonObject.getInt("CANTONID");
						String cantonName = jsonObject.getString("CANTONNAME");
						int cantonLevel = jsonObject.getInt("CANTONLEVEL");
						int dispOrder = jsonObject.getInt("DISPORDER");
						int parentId = jsonObject.getInt("PARENTID");
						Canton canton = new Canton();
						canton.setCantonId(cantonId);
						canton.setCantonName(cantonName);
						canton.setCantonLevel(cantonLevel);
						canton.setParentId(parentId);
						canton.setDispOrder(dispOrder);
						listCanton.add(canton);
					}
					if (listCanton.size() > 0) {
						new CantonDBMExternal(paramContext).updateData(listCanton);
					}
				} else if (updateCode == 902) {// 更新部门
					List<Unit> listUnit = new ArrayList<Unit>();
					for (int i = 0; i < resultJsonArray.length(); i++) {
						JSONObject jsonObject = resultJsonArray.getJSONObject(i);
						int unitId = jsonObject.getInt("UNITID");
						String unitName = jsonObject.getString("UNITNAME");
						int parentId = -1;
						if(jsonObject.getString("PARENTID") != null && !("null".equals(jsonObject.getString("PARENTID")))){
							parentId = jsonObject.getInt("PARENTID");
						}
						int dispOrder = jsonObject.getInt("DISPORDER");
						int cantonCode = jsonObject.getInt("CANTONCODE");
						Unit unit = new Unit();
						unit.setUnitId(unitId);
						unit.setUnitName(unitName);
						unit.setParentId(parentId);
						unit.setDispOrder(dispOrder);
						unit.setCantonCode(cantonCode);
						listUnit.add(unit);
					}
					if (listUnit.size() > 0) {
						new UnitDBMExternal(paramContext).updateData(listUnit);
					}
				} else if (updateCode == 903) {// 更新人员
					List<Human> listHuman = new ArrayList<Human>();
					for (int i = 0; i < resultJsonArray.length(); i++) {
						JSONObject jsonObject = resultJsonArray.getJSONObject(i);
						int humanId = jsonObject.getInt("HUMANID");
						String humanName = jsonObject.getString("HUMANNAME");
						String password = jsonObject.getString("PASSWORD");
						int gender = jsonObject.getInt("GENDER");
						int status = jsonObject.getInt("STATUS");
						String telephone = jsonObject.getString("TELEPHONE");
						String cellphone = jsonObject.getString("CELLPHONE");
						String homephone = jsonObject.getString("HOMEPHONE");
						int unitId = jsonObject.getInt("UNITID");
						String address = jsonObject.getString("ADDRESS");
						String email = jsonObject.getString("EMAIL");
						int dispOrder = jsonObject.getInt("DISPORDER");
						Human human = new Human();
						human.setHumanId(humanId);
						human.setHumanName(humanName);
						human.setPassword(password);
						human.setGender(gender);
						human.setStatus(status);
						human.setTelephone(telephone);
						human.setCellphone(cellphone);
						human.setHomephone(homephone);
						human.setUnitId(unitId);
						human.setAddress(address);
						human.setEmail(email);
						human.setDispOrder(dispOrder);
						listHuman.add(human);
					}
					if (listHuman.size() > 0) {
						new HumanDBMExternal(paramContext).updateData(listHuman);
					}
				} else if (updateCode == 904) {// 更新人员
					List<Field> listField = new ArrayList<Field>();
					for (int i = 0; i < resultJsonArray.length(); i++) {
						JSONObject jsonObject = resultJsonArray.getJSONObject(i);
						int fieldId = jsonObject.getInt("FIELDID");
						String fieldName = jsonObject.getString("FIELDNAME");
						String fieldCnname = jsonObject.getString("FIELDCNNAME");
						int fieldType = 0;
						if (jsonObject.getString("FIELDTYPE") != null && !("null".equals(jsonObject.getString("FIELDTYPE")))) {
							fieldType = jsonObject.getInt("FIELDTYPE");
						}
						int dataType = 0;
						if (jsonObject.getString("DATATYPE") != null && !("null".equals(jsonObject.getString("DATATYPE")))) {
							dataType = jsonObject.getInt("DATATYPE");
						}
						int dictType = 0;
						if (jsonObject.getString("DICTTYPE") != null && !("null".equals(jsonObject.getString("DICTTYPE")))) {
							dictType = jsonObject.getInt("DICTTYPE");
						}
						int dispOrder = jsonObject.getInt("DISPORDER");
						Field field = new Field();
						field.setFieldId(fieldId);
						field.setFieldName(fieldName);
						field.setFieldCnname(fieldCnname);
						field.setFieldType(fieldType);
						field.setDataType(dataType);
						field.setDictType(dictType);
						field.setDispOrder(dispOrder);
						listField.add(field);
					}
					if (listField.size() > 0) {
						new FieldDBMExternal(paramContext).updateData(listField);
					}
				} else if (updateCode == 905) {// 更新字典项
					List<DictItem> listDictItem = new ArrayList<DictItem>();
					for (int i = 0; i < resultJsonArray.length(); i++) {
						JSONObject jsonObject = resultJsonArray.getJSONObject(i);
						int itemId = jsonObject.getInt("ITEMID");
						int dictId = jsonObject.getInt("DICTID");
						String itemLabel = jsonObject.getString("ITEMLABEL");
						String itemValue = jsonObject.getString("ITEMVALUE");
						int parentId = jsonObject.getInt("PARENTID");
						int dispOrder = jsonObject.getInt("DISPORDER");
						DictItem dictItem = new DictItem();
						dictItem.setItemId(itemId);
						dictItem.setDictId(dictId);
						dictItem.setItemLabel(itemLabel);
						dictItem.setItemValue(itemValue);
						dictItem.setParentId(parentId);
						dictItem.setDispOrder(dispOrder);
						listDictItem.add(dictItem);
					}
					if (listDictItem.size() > 0) {
						new DictItemDBMExternal(paramContext).updateData(listDictItem);
					}
				}

			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	private void parseData_app(InputStream ins, int updateCode, Context paramContext) {
		String resultData;
		try {
			resultData = UtilsTools.inputStream2String(ins);
			if ("".equals(resultData) == false) {
				JSONObject resultJsonObject = new JSONObject(resultData);
				if (updateCode == 501) {// 版本检测结果
					newVersionNum = resultJsonObject.getString("versionNum");
					if (newVersionNum.equals(txt_versionnum_app.getText())) {
						fragmentActivity.mHandler.sendEmptyMessage(510);
					} else {

						downloadAddr = resultJsonObject.getString("downloadAddr");
						newVersionDesc = resultJsonObject.getString("versionDesc");
						fragmentActivity.mHandler.sendEmptyMessage(509);
					}
				}
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	/**
	 * 下载App
	 * downloadAddr  versionNum
	 * @param
	 * @param
	 */
	private void downApp() {
		mFileLength = 0;
		mDownedFileLength = 0;
		this.progressBar_app.setProgress(0);
		String downLoadUrl = GisQueryApplication.getApp().getProjectconfig().getIsysbaseurl() + downloadAddr + "?versionNum=" + newVersionNum;
		try {
			URL url = new URL(downLoadUrl);
			mConnection = url.openConnection();
			if (mConnection.getReadTimeout() == 5) {
				this.dialog.dismiss();
				btn_update_app.setEnabled(true);
				Log.i("---------->", "当前网络有问题");
			}
			mInputStream = mConnection.getInputStream();

		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		String softUpdatePath = GisQueryApplication.getApp().currentProjectPath + "/SoftUpdate/";
		File file1 = new File(softUpdatePath);
		if (!file1.exists()) {
			file1.mkdir();
		}
		String savePathString = softUpdatePath + newVersionNum + ".apk";
		File file = new File(savePathString);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			file.delete();
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			mOutputStream = new FileOutputStream(file);
			byte[] buffer = new byte[1024];
			mFileLength = mConnection.getContentLength();
			fragmentActivity.mHandler.sendEmptyMessage(511);
			int readLength = -1;
			while ((readLength = mInputStream.read(buffer)) != -1) {
				mDownedFileLength += readLength;
				mOutputStream.write(buffer, 0, readLength);
				fragmentActivity.mHandler.sendEmptyMessage(512);
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (mInputStream != null) {
				try {
					mInputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (mOutputStream != null) {
				try {
					mOutputStream.flush();
					mOutputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			fragmentActivity.mHandler.sendEmptyMessage(513);
		}
	}

	private void installApk() {
		String filePath = GisQueryApplication.getApp().currentProjectPath + "/SoftUpdate/" + newVersionNum + ".apk";
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setDataAndType(Uri.parse("file://" + filePath), "application/vnd.android.package-archive");
		startActivity(i);
	}

	/**
	 * 处理App下载消息
	 *
	 * @param msg
	 */
	public void handleAppDownMessage(Message msg) {
		switch (msg.what) {
		case 508: {
			String filePath = GisQueryApplication.getApp().currentProjectPath + "/SoftUpdate/" + newVersionNum + ".apk";
			File f = new File(filePath);
			if (f.exists()) {
				AlertDialog.Builder localBuilder = new AlertDialog.Builder(fragmentActivity);
				localBuilder.setTitle("软件更新");
				localBuilder.setPositiveButton("开始安装", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface paramDialogInterface, int paramInt) {
						AppUpdateFragment.this.installApk();
					}
				});
				localBuilder.setNegativeButton("重新下载", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface paramDialogInterface, int paramInt) {
						SoftDownLoadThread softUpdateThread = new SoftDownLoadThread();
						new Thread(softUpdateThread).start();
					}
				});
				localBuilder.setMessage("你已经下载了这个版本，是否用已下载的进行更新？");
				localBuilder.show();
			} else {
				SoftDownLoadThread softUpdateThread = new SoftDownLoadThread();
				new Thread(softUpdateThread).start();
			}
			break;
		}
		case 509: {
			this.dialog.dismiss();
			btn_update_app.setEnabled(true);
			AlertDialog.Builder localBuilder = new AlertDialog.Builder(fragmentActivity);
			localBuilder.setTitle("软件版本检测");
			localBuilder.setPositiveButton("马上更新软件", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface paramDialogInterface, int paramInt) {
					fragmentActivity.mHandler.sendEmptyMessage(508);
				}
			});
			localBuilder.setNegativeButton("暂时不更新", null);
			localBuilder.setMessage("软件最新版本：" + newVersionNum + "\n\r" + "新版描述：" + newVersionDesc);
			localBuilder.show();
			break;
		}
		case 510: {
			this.dialog.dismiss();
			btn_update_app.setEnabled(true);
			new AlertDialog.Builder(fragmentActivity).setTitle("软件版本检测").setMessage("您的软件已经是最新版本！").create().show();
			break;
		}
		case 511:
			progressBar_app.setMax(mFileLength);
			Log.i("文件长度----------->", progressBar_app.getMax() + "");
			break;
		case 512:
			int x = (int) (mDownedFileLength * 100D / mFileLength);
			Log.i("计算进度", "mDownedFileLength=" + mDownedFileLength + " mFileLength=" + mFileLength + " p=" + x);
			updateProcessStatus_app(x);
			break;
		case 513:
			ToastUtils.showLong( "软件下载完成！");
			installApk();
			break;
		default:
			break;
		}
	}
}
