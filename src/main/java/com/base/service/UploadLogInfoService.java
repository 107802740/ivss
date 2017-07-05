package com.base.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.api.IVSSBusinessAgent;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;

public class UploadLogInfoService extends Service {
	private boolean running = false;
	private final IVSSBusinessAgent mAgent = new IVSSBusinessAgent();
	private LocationClient mLocationClient;
	//private MyLocationListener mMyLocationListener;

	public void onCreate() {
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (!running) {
			Log.d("Test", "onStartCommand>>>>>>>>>");
			running = true;
			mLocationClient = new LocationClient(this.getApplicationContext());
			mLocationClient.registerLocationListener(new MyLocationListener());
			// 百度地图定位
			LocationClientOption option = new LocationClientOption();
			option.setLocationMode(LocationMode.Hight_Accuracy);
			option.setCoorType("bd09ll"); // 设置坐标类型
			option.setScanSpan(900000);
			option.setIsNeedAddress(true);
			mLocationClient.setLocOption(option);
			mLocationClient.start();
		}
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		running = false;
		if (mLocationClient != null) {
			mLocationClient.stop();
		}
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	public class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			final double mLatitude = location.getLatitude();
			final double mLongitude = location.getLongitude();
			if (mLatitude != 4.9E-234 && mLongitude != 4.9E-234) {
				new Thread() {
					public void run() {
						try {
							mAgent.uploadLogInfo(mLongitude, mLatitude);
						} catch (Exception e) {
							e.printStackTrace();
						}
					};
				}.start();
			}
		}

	}

}
