package com.guyu.android.bluetooth;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import com.guyu.android.gis.activity.MainActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

public class BluetoothConnectThread extends Thread {
	private MainActivity mActivity;
	private BlueToothReceiver btr;
	private BluetoothAdapter mBluetoothAdapter;
	private String mAddress;
	private String mSppId;

	public BluetoothConnectThread(MainActivity mainActivity,
			BluetoothAdapter bluetoothAdapter, String address, String sppid) {
		super();
		mActivity = mainActivity;
		mBluetoothAdapter = bluetoothAdapter;
		mAddress = address;
		mSppId = sppid;
	}

	public void stopSelf() {
		btr.flag = false;
		MainActivity.blueToothConnected = false;
		btr.closeToothReceiver();
		if (MainActivity.btSocket != null) {
			try {
				MainActivity.btSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void run() {

		UUID uuid = UUID.fromString(mSppId);// 蓝牙普遍支持SPP协议
		// 实例蓝牙设备
		BluetoothDevice btDevice = mBluetoothAdapter.getRemoteDevice(mAddress);
		try {
			MainActivity.btSocket = btDevice
					.createRfcommSocketToServiceRecord(uuid);
			MainActivity.btSocket.connect();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		// 当正常连接配对其他蓝牙设备后启动线程，一直监听报文数据
		MainActivity.blueToothConnected = true;
		btr = new BlueToothReceiver(mActivity);
		btr.flag = true;
		btr.start();
	}
}

class BlueToothReceiver extends Thread {
	private MainActivity mActivity;
	private InputStream ips;
	boolean flag;

	public BlueToothReceiver(MainActivity mainActivity) {
		super();
		mActivity = mainActivity;
	}

	@Override
	public void run() {
		while (flag) {
			if (MainActivity.blueToothConnected) {
				try {
					// 没有接收到数据，这里一直处于阻塞状态
					ips = MainActivity.btSocket.getInputStream();
					String dataStr = inputStream2String(ips);
					mActivity.dealWithBlueToothReceiveData(dataStr);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void closeToothReceiver() {
		if (ips != null) {
			try {
				ips.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			ips = null;
		}
	}

	public String inputStream2String(InputStream in) throws IOException {
		int len = in.available();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buf = new byte[len];
		in.read(buf);
		baos.write(buf, 0, len);
		String data = baos.toString("UTF-8");
		return data;
	}
}
