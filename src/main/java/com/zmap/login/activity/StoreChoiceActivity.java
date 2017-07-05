package com.zmap.login.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.api.bean.DrugstoreInfo;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.base.BaseActivity;
import com.base.dao.impl.BasicSQliteDbDaoImpl;
import com.base.service.UploadLogInfoService;
import com.base.util.AndroidUtil;
import com.base.util.LocationUtil;
import com.base.util.MyLocationListener;
import com.zmap.datagather.activity1.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class StoreChoiceActivity extends BaseActivity implements
		OnItemClickListener, OnGetGeoCoderResultListener {
	private ProgressDialog mDialog = null;
	private final String positionFailure = "定位失败,正在重新定位…";
	private static final double RADIUS = 1000;

	private LocationClient mLocationClient;
	private MyLocationListener2 mMyLocationListener;
	private GeoCoder mSearch = null; // 搜索模块，也可去掉地图模块独立使用

	private BasicSQliteDbDaoImpl mImpl = null;
	private double mLatitude = 0;
	private double mLongitude = 0;
	private String mProvincesText = "";
	private String mCityText = "";
	private TextView mTvLng = null;// 经度
	private TextView mTvLat = null;// 纬度
	private TextView mStoreDateTv = null;// 时间
	private TextView mStoreAddressTv = null;// 地址

	private ListView mLvDisp = null;
	private DrugStoreAdapter mAdapter = null;
	private EditText mSearcher = null;
	private LocationUtil mLocationUtil = null;
	private List<DrugstoreInfo> mDrugstoreInfos = null;

	private final Handler mHandler = new Handler();
	private final Runnable mRunnable = new Runnable() {

		@Override
		public void run() {
			mHandler.removeCallbacks(mRunnable);
			mStoreDateTv.setText(AndroidUtil.dateFormat(new Date(),
					"yyyy-MM-dd HH:mm:ss"));
			mHandler.postDelayed(mRunnable, DateUtils.SECOND_IN_MILLIS);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mLocationClient = new LocationClient(this.getApplicationContext());
		mMyLocationListener = new MyLocationListener2();
		mLocationClient.registerLocationListener(mMyLocationListener);
		initLocationUtil();
		setContentView(R.layout.store_choice_main);
		initView();
		initLocation();
		setOnConnectivityListener(new onConnectivityListener() {

			@Override
			public void onAction() {
				if (TextUtils.isEmpty(mStoreAddressTv.getText())
						|| positionFailure.equals(mStoreAddressTv.getText())) {
					repositioning();
				}
			}
		});
		startService(new Intent(StoreChoiceActivity.this,
				UploadLogInfoService.class));
	}

	private void initView() {
		mTvLng = (TextView) findViewById(R.id.tvLng);
		mTvLat = (TextView) findViewById(R.id.tvLat);
		mStoreDateTv = (TextView) findViewById(R.id.store_date_text);
		mStoreAddressTv = (TextView) findViewById(R.id.store_address_text);
		mLvDisp = (ListView) findViewById(R.id.lvDisp);
		mLvDisp.setOnItemClickListener(this);
		mAdapter = new DrugStoreAdapter(mDrugstoreInfos);
		mLvDisp.setAdapter(mAdapter);
		mSearcher = (EditText) findViewById(R.id.search_edit);
		mSearcher.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				searchStroes(s);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
		mDialog = new ProgressDialog(this);
		mDialog.setMessage("正在定位…");
		mDialog.setCanceledOnTouchOutside(false);
		mDialog.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK
						&& event.getAction() == KeyEvent.ACTION_UP) {
					StoreChoiceActivity.this.finish();
				}
				return false;
			}
		});
	}

	/**
	 * 搜索药店
	 * 
	 * @param s
	 */
	private void searchStroes(CharSequence s) {
		if (TextUtils.isEmpty(s)) {
			mAdapter.setInfos(mDrugstoreInfos);
		} else {
			List<DrugstoreInfo> infos = new ArrayList<DrugstoreInfo>();
			for (DrugstoreInfo info : mDrugstoreInfos) {
				if (info.getCname().contains(s)) {
					infos.add(info);
				}
			}
			mAdapter.setInfos(infos);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mLatitude != 0 && mLongitude != 0) {
			mDrugstoreInfos = mImpl.getDrugstoreInfos(mLongitude, mLatitude,
					RADIUS);
			searchStroes(mSearcher.getText().toString());
		}
		mHandler.removeCallbacks(mRunnable);
		mHandler.post(mRunnable);
		// repositioning();
		// registerReceiver();
	}

	private void initLocationUtil() {
		mLocationUtil = new LocationUtil(this);
		mLocationUtil.setLocationListener(new MyLocationListener() {

			@Override
			public void updateLocation(double latitude, double longitude) {

				Log.d("Test", "updateLocation>>>" + latitude + ":" + longitude);
				if (latitude != 4.9E-234 && longitude != 4.9E-234) {
					mLatitude = latitude;
					mLongitude = longitude;
					mTvLng.setText(String.valueOf(longitude));
					mTvLat.setText(String.valueOf(latitude));
					getAddressbyGeoPoint(mLatitude, mLongitude);
					mDrugstoreInfos = mImpl.getDrugstoreInfos(longitude,
							latitude, RADIUS);
					mAdapter.setInfos(mDrugstoreInfos);
					if (mLocationClient.isStarted()) {
						mLocationClient.stop();
					}
					Log.d("Test", "infos =" + mDrugstoreInfos.size());
				} else {
					mTvLng.setText("定位失败");
					mTvLat.setText("定位失败");
					mStoreAddressTv.setText("");
				}
				if (mDialog.isShowing()) {
					mDialog.dismiss();
				}
				if (mLocationUtil != null) {
					mLocationUtil.removeUpdates();
				}
			}

			@Override
			public void updateLocation(Location location) {
				if (location == null) {
					mTvLng.setText("定位失败");
					mTvLat.setText("定位失败");
					mStoreAddressTv.setText("");
					if (mDialog.isShowing()) {
						mDialog.dismiss();
					}
					if (mLocationUtil != null) {
						mLocationUtil.removeUpdates();
					}
				}
			}
		});
		mLocationUtil.openGPSSettings();
	}

	private void initLocation() {
		// 初始化搜索模块，注册事件监听
		mSearch = GeoCoder.newInstance();
		mSearch.setOnGetGeoCodeResultListener(this);

		mImpl = BasicSQliteDbDaoImpl.getInstance(this);
		mHandler.post(mRunnable);

		repositioning();

	}

	public void gotoBaiduMap(View v) {
		BaiduMapLocation.actionIntent(this, mLatitude, mLongitude);
	}

	public void doGPSLocation(View v) {
		repositioning();
	}

	public void addStore(View v) {
		AddStroeActivity
				.actionIntent(this, mLatitude, mLongitude, mStoreAddressTv
						.getText().toString(), mProvincesText, mCityText);
	}

	private void getAddressbyGeoPoint(double latitude, double longitude) {

		// 自经纬度取得地址

		Geocoder gc = new Geocoder(getBaseContext(), Locale.getDefault());
		List lstAddr = null;

		try {
			lstAddr = gc.getFromLocation(latitude, longitude, 1);
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (lstAddr != null && lstAddr.size() > 0) {

			Address addr = (Address) lstAddr.get(0);
			if (addr != null) {
				mProvincesText = addr.getAdminArea();
				mCityText = addr.getLocality();
			}
			// 反Geo搜索
			if (mSearch != null) {
				LatLng ptCenter = new LatLng(latitude, longitude);
				mSearch.reverseGeoCode(new ReverseGeoCodeOption()
						.location(ptCenter));
			}
		} else {
			mTvLng.setText("定位失败");
			mTvLat.setText("定位失败");
			mStoreAddressTv.setText("");
		}
	}

	private String parseAddr(Address address) {

		return address.getAddressLine(0) + address.getAddressLine(1)
				+ address.getAddressLine(2) + address.getFeatureName();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.d("Test", "onActivityResult=" + data);
		if (resultCode == RESULT_OK) {
			if (requestCode == 0) {
				repositioning();
			}
		}
	}

	/**
	 * 重新定位
	 */
	private void repositioning() {
		// 百度地图定位
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(15000);
		option.setIsNeedAddress(true);
		if (mLocationClient.isStarted()) {
			mLocationClient.stop();
		}
		mLocationClient.start();
		mLocationClient.setLocOption(option);
		if (!mDialog.isShowing()) {
			mDialog.show();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		mHandler.removeCallbacks(mRunnable);
		// if (mLocationUtil != null) {
		// mLocationUtil.removeUpdates();
		// }
		// if (mLocationClient != null) {
		// mLocationClient.stop();
		// }
		// unRegisterReceiver();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mLocationUtil != null) {
			mLocationUtil.removeUpdates();
		}
		if (mLocationClient != null) {
			mLocationClient.stop();
		}
		if (mSearch != null) {
			mSearch.destroy();
			mSearch = null;
		}
	}

	private class DrugStoreAdapter extends BaseAdapter {
		private List<DrugstoreInfo> infos = null;
		private LayoutInflater mInflater = null;

		public DrugStoreAdapter(List<DrugstoreInfo> infos) {
			this.infos = infos;
			mInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		}

		public void setInfos(List<DrugstoreInfo> infos) {
			this.infos = infos;
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			if (infos != null) {
				return infos.size();
			}
			return 0;
		}

		@Override
		public Object getItem(int position) {
			if (infos != null) {
				return infos.get(position);
			}
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.store_choice_item,
						null);
				holder = new ViewHolder();
				holder.tvIdx = (TextView) convertView.findViewById(R.id.tvIdx);
				holder.tvDestFlag = (TextView) convertView
						.findViewById(R.id.tvDestFlag);
				holder.tvCname = (TextView) convertView
						.findViewById(R.id.tvCname);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			DrugstoreInfo info = (DrugstoreInfo) getItem(position);
			if (info != null) {
				holder.tvIdx.setText(String.valueOf(position + 1));
				holder.tvDestFlag.setText(info.getDest_flag());
				holder.tvCname.setText(info.getCname());
			}
			return convertView;
		}

	}

	private class ViewHolder {
		public TextView tvIdx;
		public TextView tvDestFlag;
		public TextView tvCname;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		DrugstoreInfo info = (DrugstoreInfo) mAdapter.getItem(position);
		InStroeEidtor.actionIntent(StoreChoiceActivity.this, info);
	}

	public class MyLocationListener2 implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			double latitude = location.getLatitude();
			double longitude = location.getLongitude();
			String address = location.getAddrStr();
			Log.d("Test", "MyLocationListener2=" + latitude + ":" + longitude);
			if (latitude != 4.9E-234 && longitude != 4.9E-234
					&& !TextUtils.isEmpty(address)) {
				mLatitude = latitude;
				mLongitude = longitude;
				if (TextUtils.isEmpty(mProvincesText)
						|| TextUtils.isEmpty(mCityText)) {
					mProvincesText = location.getProvince();
					mCityText = location.getCity();
				}
				mStoreAddressTv.setText(address);
				mTvLng.setText(String.valueOf(mLongitude));
				mTvLat.setText(String.valueOf(mLatitude));
				mDrugstoreInfos = mImpl.getDrugstoreInfos(mLongitude,
						mLatitude, RADIUS);
				searchStroes(mSearcher.getText().toString());
				mLocationClient.stop();
				if (mDialog.isShowing()) {
					mDialog.dismiss();
				}
			} else {
				if (mLocationUtil != null) {
					mLocationUtil.removeUpdates();
					mLocationUtil.getLocation();
				}
			}
		}
	}

	@Override
	public void onGetGeoCodeResult(GeoCodeResult arg0) {

	}

	@Override
	public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR
				|| result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
			Toast.makeText(StoreChoiceActivity.this, "服务器寻址失败",
					Toast.LENGTH_LONG).show();
			mStoreAddressTv.setText("");
			return;
		}
		Log.d("Test",
				result.getAddressDetail().city + ":"
						+ result.getAddressDetail().province);
		List<PoiInfo> infos = result.getPoiList();
		if (infos != null && infos.size() > 0) {
			String address = infos.get(0).address;
			mStoreAddressTv.setText(address);
			Toast.makeText(StoreChoiceActivity.this, "百度寻址成功",
					Toast.LENGTH_LONG).show();
		} else {
			Toast.makeText(StoreChoiceActivity.this, "服务器寻址失败",
					Toast.LENGTH_LONG).show();
			mStoreAddressTv.setText("");
		}

	}
}
