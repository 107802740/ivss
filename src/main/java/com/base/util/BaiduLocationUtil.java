package com.base.util;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.base.Constant;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by marson on 2016/4/23.
 */
public class BaiduLocationUtil {

    private LocationClient mLocationClient;
    private MyLocationListener mMyLocationListener;
    private Context mContext;

    private LocationInfoListener mListener;

    public void getBaiduLocationInfo(Context context, LocationInfoListener listener) {
        mContext = context;
        mListener = listener;
        if (Constant.sLocation == null) {
            initLocation();
        } else {
            mListener.onReceiveLocation(Constant.sLocation);
        }
    }

    private void initLocation() {
        // 百度地图定位
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);
        option.setIsNeedAddress(true);
        if (mLocationClient == null) {
            mLocationClient = new LocationClient(mContext);
        }
        if (mMyLocationListener == null) {
            mMyLocationListener = new MyLocationListener();
        }
        mLocationClient.registerLocationListener(mMyLocationListener);
        mLocationClient.setLocOption(option);
        if (mLocationClient.isStarted()) {
            mLocationClient.stop();
        }
        mLocationClient.start();
    }

    private class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            if (latitude != 4.9E-234 && longitude != 4.9E-234) {
                mListener.onReceiveLocation(location);
                mLocationClient.stop();
                Log.e("Test", "onReceiveLocation=" + location.getProvince());
            }

        }

    }

    /**
     * 重新定位
     */
    private void repositioning() {
        mLocationClient.stop();
        mLocationClient.start();
    }


    private void getAddressbyGeoPoint(double latitude, double longitude) {

        // 自经纬度取得地址

        Geocoder gc = new Geocoder(mContext, Locale.getDefault());
        List lstAddr = null;

        try {
            lstAddr = gc.getFromLocation(latitude, longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (lstAddr != null && lstAddr.size() > 0) {

            Address addr = (Address) lstAddr.get(0);
            if (addr != null) {
//                mProvinces = addr.getAdminArea();
//                mCity = addr.getLocality();
            }
        }
    }
}
