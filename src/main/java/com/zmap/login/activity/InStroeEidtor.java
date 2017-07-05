package com.zmap.login.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.api.bean.DrugstoreInfo;
import com.api.bean.InstoreInfo;
import com.base.BaseActivity;
import com.base.dao.impl.UploadSQliteDbDaoImpl;
import com.base.util.AndroidUtil;
import com.base.util.BitmapScaleUtil;
import com.storage.StorageConfig;
import com.zmap.datagather.activity1.MyApplication;
import com.zmap.datagather.activity1.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class InStroeEidtor extends BaseActivity {
	private UploadSQliteDbDaoImpl mImpl = null;
	private EditText mTxt_ds_name = null;// 药店名称
	private EditText mTxt_ds_code = null;// 药店编号
	private Spinner spIfAddSVisitor;
	private EditText txtSVisitor;
	private TextView txtOtc;
	private ImageView photoImg;
	private DrugstoreInfo mInfo;

	private Bitmap mBitmap = null;

	private Uri imageFileUri = null;
	private Uri imageFileCacheUri = null;

	public static void actionIntent(Context context, DrugstoreInfo info) {
		Intent it = new Intent(context, InStroeEidtor.class);
		it.putExtra("storeInfo", info);
		context.startActivity(it);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.instore_info_edit);
		mImpl = UploadSQliteDbDaoImpl.getInstance(this);
		initView();
		initData();
	}

	private void initView() {
		mTxt_ds_code = (EditText) findViewById(R.id.txt_ds_code);
		mTxt_ds_name = (EditText) findViewById(R.id.txt_ds_name);
		spIfAddSVisitor = (Spinner) findViewById(R.id.spIfAddSVisitor);
		txtOtc = (TextView) findViewById(R.id.txtOtc);
		txtSVisitor = (EditText) findViewById(R.id.txtSVisitor);
		photoImg = (ImageView) findViewById(R.id.photoImg);
	}

	private void initData() {
		Intent it = getIntent();
		mInfo = (DrugstoreInfo) it.getSerializableExtra("storeInfo");
		mTxt_ds_name.setText(mInfo.getCname());
		mTxt_ds_code.setText(mInfo.getDs_code2());
		txtSVisitor.setText(MyApplication.visitor);
		txtOtc.setText(mInfo.getSales());
	}

	public void doInsert(View v) {
		MyApplication.visitor = txtSVisitor.getText().toString();
		InstoreInfo info = new InstoreInfo();
		info.setDs_code2(mInfo.getDs_code2());
		info.setArea_code(mInfo.getArea_code());
		info.setDs_name(mInfo.getCname());
		info.setDs_upload_tag("false");
		if (MyApplication.sUser != null) {
			info.setFloginId(MyApplication.sUser.getLoginid());
		} else {
			String loginid = getCookieValue("uid");
			if (loginid != null) {
				info.setFloginId(loginid);
			}
		}
		info.setIn_date(AndroidUtil.dateFormat(new Date()));
		String name = "";
		String fileName = "";
		if (mBitmap != null) {
			name = new DateFormat().format("yyyyMMdd_hhmmss",
					Calendar.getInstance(Locale.CHINA))
					+ ".jpg";
			File file = StorageConfig.getInStoreImageDir();
			if (!file.exists()) {
				file.mkdirs();// 创建文件夹
			}
			fileName = file.getAbsolutePath() + "/" + name;
			info.setImg_name(name);
			info.setImg_path(fileName);
		}
		mImpl.addInstoreInfo(info);
		if (mBitmap != null) {
			FileOutputStream b = null;
			try {
				b = new FileOutputStream(fileName);
				mBitmap.compress(Bitmap.CompressFormat.JPEG, 90, b);// 把数据写入文件
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} finally {
				try {
					b.flush();
					b.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		DisplayInfoActivity.actionIntent(this, mInfo);
		finish();
	}

	public void doExit(View v) {
		finish();
	}

	public void doPaizao(View v) {

		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		imageFileCacheUri = imageFileUri;
		File file = new File(StorageConfig.getInStoreImageDir()
				.getAbsolutePath(), "img_"
				+ AndroidUtil.dateFormat(new Date(), "yyyyMMdd_hhmmss")
				+ ".jpg");
		imageFileUri = Uri.fromFile(file);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, imageFileUri);// 指定系统相机拍照保存在imageFileUri所指的位置
		startActivityForResult(intent, 1);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			String sdStatus = Environment.getExternalStorageState();
			if (!sdStatus.equals(Environment.MEDIA_MOUNTED)
					|| !StorageConfig.isExistSDCard) { // 检测sd是否可用
				Toast.makeText(InStroeEidtor.this, "检测sd卡不可用",
						Toast.LENGTH_LONG).show();
				Log.i("TestFile",
						"SD card is not avaiable/writeable right now.");
				return;
			}
			String path = imageFileUri.getPath();
			mBitmap = new BitmapScaleUtil().getimage(path);
			if (mBitmap == null) {
				showMsg("拍照失败，请重试");
				return;
			}
			Log.d("Test",
					"mBitmap>>>" + mBitmap.getWidth() + ":"
							+ mBitmap.getHeight());
			photoImg.setVisibility(View.VISIBLE);
			photoImg.setImageBitmap(mBitmap);// 将图片显示在ImageView里
			clearCachePic();
			imageFileCacheUri = imageFileUri;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		clearCachePic();
	}

	private void clearCachePic() {
		if (imageFileCacheUri != null) {
			File file = new File(imageFileCacheUri.getPath());
			if (file.exists()) {
				file.delete();
			}
			imageFileCacheUri = null;
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putSerializable("storeInfo", mInfo);
		outState.putParcelable("bmp", mBitmap);
		if (imageFileUri != null) {
			outState.putString("imageFileUri", imageFileUri.toString());
		}
		if (imageFileCacheUri != null) {
			outState.putString("imageFileCacheUri",
					imageFileCacheUri.toString());
		}
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		mInfo = (DrugstoreInfo) savedInstanceState.getSerializable("storeInfo");
		mBitmap = savedInstanceState.getParcelable("bmp");
		String imageUri = savedInstanceState.getString("imageFileUri");
		if (!TextUtils.isEmpty(imageUri)) {
			imageFileUri = Uri.parse(imageUri);
		}
		String imageCacheUri = savedInstanceState
				.getString("imageFileCacheUri");
		if (!TextUtils.isEmpty(imageCacheUri)) {
			imageFileCacheUri = Uri.parse(imageCacheUri);
		}
		super.onRestoreInstanceState(savedInstanceState);
	}

}
