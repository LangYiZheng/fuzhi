package com.guyu.android.utils;

import java.util.Timer;
import java.util.TimerTask;
import android.os.Handler;
import android.os.Message;

public class HnTimer {
	public interface OnCompletedListener {
		void onCompleted();
	}

	private Timer timer;
	private Long delay;
	private OnCompletedListener myListener;

	final Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			System.out.println("HnTimer.Handler");

			HnTimer.this.Completed();
			HnTimer.this.timer.cancel();

			super.handleMessage(msg);
		}
	};

	final TimerTask task = new TimerTask() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			// Message message = new Message();
			// message.what= 1;
			handler.sendEmptyMessage(0);
		}
	};

	public HnTimer(long d) {
		this.timer = new Timer();
		this.delay = d;
	}

	public void schedule() {
		timer.schedule(task, delay);
	}

	public void setCompletedListener(OnCompletedListener listener) {
		myListener = listener;
	}

	private void Completed() {
		myListener.onCompleted();
	}

	// 静态方法
	public static void setTimeout(long d, OnCompletedListener listener) {
		HnTimer myTimer = new HnTimer(d);
		myTimer.setCompletedListener(listener);
		myTimer.schedule();
	}
}