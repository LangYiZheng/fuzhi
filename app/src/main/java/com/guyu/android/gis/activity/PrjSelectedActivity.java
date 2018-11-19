package com.guyu.android.gis.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.blankj.utilcode.util.ToastUtils;
import com.guyu.android.gis.app.GisQueryApplication;
import com.guyu.android.gis.config.Project;
import com.guyu.android.utils.UtilsTools;
import com.guyu.android.R;

public class PrjSelectedActivity extends Activity {
	private static final String TAG = "PrjSelectedActivity";
	private static final String TAG_USER_ACTION = "Mucai-用户操作";
	private Handler mHandler = null;
	private View.OnClickListener mOnClickListener = null;
	List<String> mPrjList = null;
	private int mSelected = -1;
	private ListView mlistViewPrjs = null;

	public void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		Log.i("PrjSelectedActivity", "PrjSelectedActivity -- onCreate");
		setContentView(R.layout.activity_prj_select);
		
		initData();
		initWidget();
	}

	private void initData() {
		this.mPrjList = new ArrayList();
		ArrayList<Project> projects = GisQueryApplication.getApp().getProjectsconfig().getProjects();
		for (int i = 0; i < projects.size(); i++) {
			this.mPrjList.add(projects.get(i).getLabel());
		}

	}

	private void initWidget() {
		this.mlistViewPrjs = ((ListView) findViewById(R.id.listPrjs));
		this.mlistViewPrjs
				.setAdapter(new ArrayAdapter(this,
						android.R.layout.simple_list_item_single_choice,
						this.mPrjList) {
					@Override
					public View getView(int position, View convertView,
							ViewGroup parent) {
						CheckedTextView textView = (CheckedTextView) super.getView(position,
								convertView, parent);
						textView.setTextColor(0xFF000000);
						return textView;
					}
				});
		this.mlistViewPrjs.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		this.mlistViewPrjs.setItemsCanFocus(false);
		this.mlistViewPrjs.setCacheColorHint(0x00000000);
		this.mlistViewPrjs.setBackgroundColor(0x00000000);
		this.mlistViewPrjs.setAlpha(1);
		ArrayList<Project> projects = GisQueryApplication.getApp().getProjectsconfig().getProjects();
		for (int i = 0; i < projects.size(); i++) {
			if (projects.get(i).getName()
					.equals(GisQueryApplication.getApp().getProjectsconfig().getCurrentProjectName())) {
				this.mlistViewPrjs.setItemChecked(i, true);
				break;
			}
		}
		this.mlistViewPrjs
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					public void onItemClick(AdapterView<?> paramAdapterView,
							View paramView, int paramInt, long paramLong) {
						PrjSelectedActivity.this.mSelected = paramInt;
						PrjSelectedActivity.this.openPrjOpt();
					}
				});
		// findViewById(R.id.btnPrjOpen).setOnClickListener(this.mOnClickListener);
		// findViewById(2131165225).setOnClickListener(this.mOnClickListener);
		// findViewById(2131165463).setOnClickListener(this.mOnClickListener);
	}

	private boolean createPrjFolder(String paramString) {
		return true;
	}

	private void importPrj(File paramFile) {

	}

	private void importPrjOpt() {
		Intent localIntent = new Intent(
				"com.mtkj.land.fileSelect.ScanFolderActivity");
		localIntent.putExtra("expand_names", new String[] { ".sxwu", ".smwu" });
		startActivityForResult(localIntent, 8193);
	}

	private boolean isExistPrj(String paramString) {
		return true;
	}

	private void openPrjOpt() {
		if ((this.mSelected != -1)
				&& (this.mSelected < this.mlistViewPrjs.getCount())) {
			ArrayList<Project> projects = GisQueryApplication.getApp().getProjectsconfig().getProjects();
			String prjName = projects.get(this.mSelected).getName();
			GisQueryApplication.getApp().changeToProject(prjName);
			finish();
		} else {
			ToastUtils.showLong( "请选择工程");
		}

	}

	private void selectPrj(String paramString) {
		// ((App)getApplication()).SetConfigData("prjName", paramString);
		// setResult(-1);
		// finish();
	}

	private String trimExtension(String paramString) {
		if ((paramString != null) && (paramString.length() > 0)) {
			int i = paramString.lastIndexOf(46);
			if ((i > -1) && (i < paramString.length()))
				paramString = paramString.substring(0, i);
		}
		return paramString;
	}

	protected void onActivityResult(int paramInt1, int paramInt2,
			Intent paramIntent) {
		// if ((paramInt1 == 8193) && (paramInt2 == 4097) && (paramIntent !=
		// null))
		// {
		// File localFile =
		// (File)paramIntent.getSerializableExtra(ScanFolderActivity.File_Object);
		// if (localFile != null)
		// importPrj(localFile);
		// }
		// super.onActivityResult(paramInt1, paramInt2, paramIntent);
	}

	public void onBackPressed() {
		setResult(0);
		super.onBackPressed();
	}

	private class MHandler extends Handler {
		public MHandler(Looper paramLooper) {
			super(paramLooper);
		}

		public void handleMessage(Message paramMessage) {
			// super.handleMessage(paramMessage);
			// switch (paramMessage.what)
			// {
			// default:
			// return;
			// case 4097:
			// }
			// MapOperate.InitPrjList();
			// PrjSelectedActivity.this.mPrjList.clear();
			// MapOperate.GetPrjList(PrjSelectedActivity.this.mPrjList);
			// PrjSelectedActivity.this.mlistViewPrjs.invalidateViews();
			// UtilsTools.MsgBox(PrjSelectedActivity.this, "工程导入成功,请查看工程列表");
		}
	}

	private class MOnClickListener implements View.OnClickListener {
		public void onClick(View paramView) {
			switch (paramView.getId()) {
			default:
				return;
			case 2131165464:
				Log.i("Mucai-用户操作", "PrjManagerActivity--点击【打开工程】按钮");
				PrjSelectedActivity.this.openPrjOpt();
				return;
			case 2131165225:
				Log.i("Mucai-用户操作", "PrjManagerActivity--点击【取消】按钮");
				PrjSelectedActivity.this.finish();
				return;
			case 2131165463:
			}
			PrjSelectedActivity.this.importPrjOpt();
		}
	}

	private class SelectPrjTask extends AsyncTask<String, String, Boolean> {
		private ProgressDialog dialog = null;
		private String mPrjNameString = null;

		public SelectPrjTask(String paramString) {
			this.mPrjNameString = paramString;
		}

		protected Boolean doInBackground(String[] paramArrayOfString) {
			// if (MapOperate.IsOpenPrj())
			// MapOperate.close();
			// return Boolean.valueOf(true);
			return true;
		}

		public void onPostExecute(Boolean paramBoolean) {
			// this.dialog.dismiss();
			// if (paramBoolean.booleanValue())
			// {
			// Log.i("Mucai-PrjSelectedActivity", "工程：" + this.mPrjNameString +
			// "-打开成功");
			// PrjSelectedActivity.this.selectPrj(this.mPrjNameString);
			// return;
			// }
			// UtilsTools.MsgBox(PrjSelectedActivity.this, "请检查SD卡是否正常加载");
			// Log.e("Mucai-PrjSelectedActivity", "工程：" + this.mPrjNameString +
			// "-打开失败：无法打开工程");
		}

		protected void onPreExecute() {
			this.dialog = new ProgressDialog(PrjSelectedActivity.this);
			this.dialog.setCancelable(false);
			this.dialog.setMessage("正在打开工程...");
			this.dialog.show();
		}
	}
}