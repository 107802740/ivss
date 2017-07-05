package com.zmap.login.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.api.bean.DrugstoreInfo;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.base.BaseActivity;
import com.base.listener.onRuturnDataListener;
import com.base.task.UploadInfoTask;
import com.zmap.datagather.activity1.MyApplication;
import com.zmap.datagather.activity1.R;

public class AddStroeActivity extends BaseActivity {
	public static boolean isAddingStrore = false;
	private Spinner dest_flagStoreSp;
	private EditText provincesText;
	private EditText cityText;
	private EditText latitudeText;
	private EditText longitudeText;
	private String mProvinces;
	private String mCity;
	private Double spointx;
	private Double spointy;
	private Spinner storeLevelSp;
	private EditText txtAddress;
	private EditText txtDsName;
	private EditText txtMonthAmt;
	private EditText txtSaleDbzh;

	private LocationClient mLocationClient;
	//private MyLocationListener mMyLocationListener;

	public static void actionIntent(Context context, double latitude,
			double longitude, String location, String provinces, String city) {
		Intent intent = new Intent(context, AddStroeActivity.class);
		intent.putExtra("latitude", latitude);
		intent.putExtra("longitude", longitude);
		if (location != null && location.contains("正在获取中")) {
			location = "";
		}
		intent.putExtra("location", location);
		intent.putExtra("provinces", provinces);
		intent.putExtra("city", city);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_store);
		initView();
		initValues();
		initLocation();
		setOnConnectivityListener(new onConnectivityListener() {

			@Override
			public void onAction() {
				mLocationClient.stop();
				mLocationClient.start();
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		registerReceiver();
	}

	@Override
	protected void onPause() {
		super.onPause();
		unRegisterReceiver();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mLocationClient != null) {
			if (mLocationClient.isStarted()) {
				mLocationClient.stop();
			}
		}
	}

	private void initView() {
		this.txtSaleDbzh = ((EditText) findViewById(R.id.txtSaleDbzh));
		this.txtDsName = ((EditText) findViewById(R.id.txtDsName));
		this.txtMonthAmt = ((EditText) findViewById(R.id.txtMonthAmt));
		this.provincesText = ((EditText) findViewById(R.id.provincesText));
		this.cityText = ((EditText) findViewById(R.id.cityText));
		this.longitudeText = ((EditText) findViewById(R.id.longitudeText));
		this.latitudeText = ((EditText) findViewById(R.id.LatitudeText));
		this.txtAddress = ((EditText) findViewById(R.id.txtAddress));
		this.storeLevelSp = ((Spinner) findViewById(R.id.storelevelList));
		this.dest_flagStoreSp = ((Spinner) findViewById(R.id.istargetlList));
	}

	private void initValues() {
		Intent intent = getIntent();
		this.spointx = Double.valueOf(intent.getDoubleExtra("longitude", 0));
		this.spointy = Double.valueOf(intent.getDoubleExtra("latitude", 0));
		String slocation = intent.getStringExtra("location");
		this.mProvinces = intent.getStringExtra("provinces");
		this.mCity = intent.getStringExtra("city");
		this.longitudeText.setText(Double.toString(this.spointx));
		this.latitudeText.setText(Double.toString(this.spointy));
		this.provincesText.setText(this.mProvinces);
		this.cityText.setText(this.mCity);
		this.txtAddress.setText(slocation);
		this.txtSaleDbzh.setText(MyApplication.sUser.getName() + "("
				+ MyApplication.sUser.getLoginid() + ")");
		ArrayAdapter localArrayAdapter1 = new ArrayAdapter(this,
				android.R.layout.simple_spinner_item, new String[] { "A", "B",
						"C" });
		localArrayAdapter1
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		this.storeLevelSp.setAdapter(localArrayAdapter1);
		ArrayAdapter localArrayAdapter2 = new ArrayAdapter(this,
				android.R.layout.simple_spinner_item, new String[] { "是", "否" });
		localArrayAdapter2
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		this.dest_flagStoreSp.setAdapter(localArrayAdapter2);
	}

	private void initLocation() {
		// 百度地图定位
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(1000);
		option.setIsNeedAddress(true);
		mLocationClient = new LocationClient(this.getApplicationContext());
		mLocationClient.registerLocationListener(new MyLocationListener());
		mLocationClient.setLocOption(option);
	}


	public class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			double latitude = location.getLatitude();
			double longitude = location.getLongitude();
			if (latitude != 4.9E-234 && longitude != 4.9E-234) {
				if (TextUtils.isEmpty(mProvinces) || TextUtils.isEmpty(mCity)) {
					mProvinces = location.getProvince();
					mCity = location.getCity();
				}
				provincesText.setText(mProvinces);
				cityText.setText(mCity);
				mLocationClient.stop();
				String address = location.getAddrStr();
				if(!TextUtils.isEmpty(address)) {
					txtAddress.setText(address);
				}
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

	public void doCancel(View paramView) {
		finish();
	}

	public void doSave(View paramView) {
		String cname = this.txtDsName.getText().toString();
		String monthAmt = this.txtMonthAmt.getText().toString();
		String address = this.txtAddress.getText().toString();
		String provinceName = this.provincesText.getText().toString();
		String cityName = this.cityText.getText().toString();
		if (cname.equals("")) {
			showMsg("【药店名称】不能为空");
			this.txtDsName.setFocusable(true);
			this.txtDsName.setFocusableInTouchMode(true);
			this.txtDsName.requestFocus();
			return;
		}
		if (monthAmt.equals("")) {
			showMsg("【月营业额】不能为空");
			this.txtMonthAmt.setFocusable(true);
			this.txtMonthAmt.setFocusableInTouchMode(true);
			this.txtMonthAmt.requestFocus();
			return;
		}
		if (address.equals("")) {
			showMsg("【药店位置】不能为空");
			this.txtAddress.setFocusable(true);
			this.txtAddress.setFocusableInTouchMode(true);
			this.txtAddress.requestFocus();
			return;
		}
		if (provinceName.equals("")) {
			showMsg("【所在省份】不能为空");
			return;
		}
		if (cityName.equals("")) {
			showMsg("【所在城市】不能为空");
			return;
		}
		String levl = this.storeLevelSp.getSelectedItem().toString();
		String dest_flag = this.dest_flagStoreSp.getSelectedItem().toString();
		// String
		// Double localDouble1 = Double.valueOf(Double
		// .parseDouble(localDecimalFormat.format(this.spointx)));
		// Double localDouble2 = Double.valueOf(Double
		// .parseDouble(localDecimalFormat.format(this.spointy)));
		DrugstoreInfo info = new DrugstoreInfo();
		info.setCname(cname);
		info.setAddress(address);
		info.setArea_code(MyApplication.sUser.getAreaCode());
		info.setCity_name(cityName);
		info.setDest_flag(dest_flag);
		info.setLevl(levl);
		info.setMonth_amt(monthAmt);
		info.setPointx(String.valueOf(spointx));
		info.setPointy(String.valueOf(spointy));
		info.setProvince_name(provinceName);
		info.setSale_dbzh(MyApplication.sUser.getLoginid());
		info.setSales(MyApplication.sUser.getName());
		if (isAddingStrore) {
			showMsg("正在上传新建药店信息……");
			return;
		}
		isAddingStrore = true;
		new UploadInfoTask(AddStroeActivity.this, info,
				new onRuturnDataListener<Boolean>() {

					@Override
					public void onResult(Boolean t) {
						AddStroeActivity.this.finish();

					}
				}).execute();
	}

}
