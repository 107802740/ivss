package com.base.util;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

public class LocationUtil {
	private Context mContext;
	private MyLocationListener mLocationListener = null;
	private Location sCurrentLocation = null;
	private LocationManager sLocationManager = null;
	private GPSLocationListener gpsListener = null;
	private NetWorkLocationListener netWorkListener = null;

	public LocationUtil(Context context) {
		this.mContext = context;
		sLocationManager = (LocationManager) mContext
				.getSystemService(Context.LOCATION_SERVICE);
	}

	public void setLocationListener(MyLocationListener locationListener) {
		this.mLocationListener = locationListener;
	}

	public void openGPSSettings() {
		sLocationManager = (LocationManager) mContext
				.getSystemService(Context.LOCATION_SERVICE);
		if (sLocationManager
				.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
			Toast.makeText(mContext, "GPS模块正常", Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(mContext, "请开启GPS！", Toast.LENGTH_SHORT).show();
			Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			((Activity) mContext).startActivityForResult(intent, 0); // 此为设置完成后返回到获取界面
		}
		// getLocation();
	}

	public void getLocation() {
		netWorkListener = new NetWorkLocationListener();
		gpsListener = new GPSLocationListener();
		// 获取位置管理服务
		String serviceName = Context.LOCATION_SERVICE;
		// 查找到服务信息
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE); // 高精度
		criteria.setAltitudeRequired(false);
		criteria.setBearingRequired(false);
		criteria.setCostAllowed(true);
		criteria.setPowerRequirement(Criteria.POWER_LOW); // 低功耗

		// String provider = sLocationManager.getBestProvider(criteria, true);
		// // 获取GPS信息
		List<String> providers = sLocationManager.getProviders(true);// 获取可用定位方式
		Location location = null;
		Location location2 = null;
		for (String provider : providers) {
			if (LocationManager.GPS_PROVIDER.equals(provider)) {
				location = sLocationManager.getLastKnownLocation(provider); // 通过GPS获取位置
				// 设置监听器，自动更新的最小时间为间隔N秒(1秒为1*1000，这样写主要为了方便)或最小位移变化超过N米
				sLocationManager.requestLocationUpdates(provider, 180 * 1000,
						500, gpsListener);
			} else if (LocationManager.NETWORK_PROVIDER.equals(provider)) {
				location2 = sLocationManager.getLastKnownLocation(provider); // 通过NETWORK获取位置
				try {
					sLocationManager.requestLocationUpdates(provider,
							180 * 1000, 500, netWorkListener);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		boolean flag = isBetterLocation(location2, location);
		if (flag) {
			sCurrentLocation = location2;
		} else {
			sCurrentLocation = location;
		}
		updateToNewLocation(sCurrentLocation);
	}

	private void updateToNewLocation(Location location) {

		if (location != null) {
			double latitude = location.getLatitude();
			double longitude = location.getLongitude();
			Log.d("Test", "updateToNewLocation =" + "维度：" + latitude + "\n经度"
					+ longitude);
			if (mLocationListener != null) {
				mLocationListener.updateLocation(latitude, longitude);
				mLocationListener.updateLocation(location);
			}
		} else {
			Log.d("Test", "updateToNewLocation =location = null");
			if (mLocationListener != null) {
				mLocationListener.updateLocation(null);
			}
		}

	}

	private static final int CHECK_INTERVAL = 1000 * 30;

	protected boolean isBetterLocation(Location location,
			Location currentBestLocation) {
		if (currentBestLocation == null) { // A new location is always better
											// than no location
			return true;
		}
		if (location == null) {
			return false;
		}
		// Check whether the new location fix is newer or older
		long timeDelta = location.getTime() - currentBestLocation.getTime();

		boolean isSignificantlyNewer = timeDelta > CHECK_INTERVAL;
		boolean isSignificantlyOlder = timeDelta < -CHECK_INTERVAL;

		boolean isNewer = timeDelta > 0;
		// If it's been more than two minutes since the current location,
		// use the new location
		// because the user has likely moved
		if (isSignificantlyNewer) {
			return true;
			// If the new location is more than two minutes older, it must
			// be worse
		} else if (isSignificantlyOlder) {
			return false;
		}
		// Check whether the new location fix is more or less accurate
		int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation
				.getAccuracy());
		boolean isLessAccurate = accuracyDelta > 0;
		boolean isMoreAccurate = accuracyDelta < 0;
		boolean isSignificantlyLessAccurate = accuracyDelta > 200;
		// Check if the old and new location are from the same provider
		boolean isFromSameProvider = isSameProvider(location.getProvider(),
				currentBestLocation.getProvider());
		// Determine location quality using a combination of timeliness and
		// accuracy
		if (isMoreAccurate) {
			return true;
		} else if (isNewer && !isLessAccurate) {
			return true;
		} else if (isNewer && !isSignificantlyLessAccurate
				&& isFromSameProvider) {
			return true;
		}
		return false;
	}

	/** Checks whether two providers are the same */
	private boolean isSameProvider(String provider1, String provider2) {
		if (provider1 == null) {
			return provider2 == null;
		}
		return provider1.equals(provider2);
	}

	// GPS监听的回调函数
	private class GPSLocationListener implements LocationListener {

		private boolean isRemove = false;// 判断网络监听是否移除

		@Override
		public void onLocationChanged(Location location) {
			boolean flag = isBetterLocation(location, sCurrentLocation);

			if (flag) {
				sCurrentLocation = location;
				updateToNewLocation(sCurrentLocation);
			}
			// 获得GPS服务后，移除network监听
			if (location != null && !isRemove) {
				sLocationManager.removeUpdates(netWorkListener);
				isRemove = true;
			}
		}

		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generatedmethod stub
		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generatedmethod stub
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			if (LocationProvider.OUT_OF_SERVICE == status) {
				sLocationManager
						.requestLocationUpdates(
								LocationManager.NETWORK_PROVIDER, 0, 0,
								netWorkListener);
			}

		}
	}

	// GPS监听的回调函数
	private class NetWorkLocationListener implements LocationListener {

		@Override
		public void onLocationChanged(Location location) {
			boolean flag = isBetterLocation(location, sCurrentLocation);

			if (flag) {
				sCurrentLocation = location;
				updateToNewLocation(sCurrentLocation);
			}
		}

		@Override
		public void onProviderDisabled(String provider) {
		}

		@Override
		public void onProviderEnabled(String provider) {
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	}

	public void removeUpdates() {
		if (gpsListener != null) {
			sLocationManager.removeUpdates(gpsListener);
		}
		if (netWorkListener != null) {
			sLocationManager.removeUpdates(netWorkListener);
		}
	}

}
