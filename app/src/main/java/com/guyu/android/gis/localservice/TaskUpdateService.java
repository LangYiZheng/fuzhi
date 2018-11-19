package com.guyu.android.gis.localservice;


import com.guyu.android.gis.app.GisQueryApplication;
import com.guyu.android.gis.opt.TaskUpdateOpt;
import com.guyu.android.utils.SysConfig;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;



public class TaskUpdateService extends Service {
	public static final int TASK_UPDATE_0_START = 0;// 开始
	public static final int TASK_UPDATE_1_STOP = 1;// 停止
	private Boolean taskAutoUpdateEnabled = false;
	public Handler mHandler = null;
	private static boolean serviceStart;

	public static boolean isServiceStart() {
		return serviceStart;
	}

	private Runnable taskUpdateRunnable = new Runnable() {
		public void run() {
			TaskUpdateService.this.refresh();
		}
	};

	@Override
	public void onCreate() {
		this.mHandler = new Handler(Looper.myLooper());
		super.onCreate();
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int onStartCommand(Intent paramIntent, int paramInt1, int paramInt2) {
		serviceStart = true;
		if (paramIntent != null) {
			int i = paramIntent.getIntExtra("opt", 0);
			if (i == 0) {
				if (taskAutoUpdateEnabled == false) {
					this.mHandler.postDelayed(this.taskUpdateRunnable,
							SysConfig.TaskUpdateTime);
				}
				taskAutoUpdateEnabled = true;
				System.out.println("任务更新服务已启动...");

			} else if (i == 1) {
				taskAutoUpdateEnabled = false;
				System.out.println("任务更新服务已停止...");
				this.mHandler.postDelayed(new Runnable() {
					public void run() {
						TaskUpdateService.this.stopSelf();
					}
				}, 2000L);
			}
		}
		return super.onStartCommand(paramIntent, paramInt1, paramInt2);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	private void refresh() {
		if (taskAutoUpdateEnabled) {
			UpdateThread updateThread = new UpdateThread();
			new Thread(updateThread).start();
			this.mHandler.postDelayed(this.taskUpdateRunnable,
					1000 *SysConfig.TaskUpdateTime);
		}

	}
	class UpdateThread implements Runnable {
		public void run() {
			TaskUpdateOpt.updateAllTaskList(TaskUpdateService.this,null);
		}
	}
}
