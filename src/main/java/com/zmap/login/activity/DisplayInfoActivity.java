package com.zmap.login.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.api.bean.DisplayInfo;
import com.api.bean.DrugInfo;
import com.api.bean.DrugstoreInfo;
import com.base.BaseActivity;
import com.base.dao.impl.BasicSQliteDbDaoImpl;
import com.base.dao.impl.DrugNumbSQliteDbDaoImpl;
import com.base.dao.impl.UploadSQliteDbDaoImpl;
import com.base.listener.onRuturnDataListener;
import com.base.task.GetSotroeNumTask;
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

public class DisplayInfoActivity extends BaseActivity {
    private UploadSQliteDbDaoImpl mImpl = null;

    private EditText txtDsCode;// 药店编码
    private EditText txtDsName;// 药店名称
    private EditText textDrugBcode;// 药品条码
    private EditText txtDrugCode;// 药品编码
    private EditText txtDrugName;// 药品名称
    private EditText txtDrugNumb;// 批号
    private EditText txtDrugPrice;// 价格
    private EditText textStoreNum;// 库存
    private EditText textSalesVolume;//月销量
    private EditText textSeqNumb;// 监管码

    private Spinner spDispSurf;// 陈列面
    private Spinner spDispPosi;// 陈列位置
    private Button btnZingCatch;// 药品条码识别
    private Button btnSeqCatch;// 监管码识别
    private ImageView photoImg;

    private DrugstoreInfo mInfo;
    private Bitmap mBitmap = null;

    private Uri imageFileUri = null;
    private Uri imageFileCacheUri = null;

    private float mLowerLimitPrice = 0;


    public static void actionIntent(Context context, DrugstoreInfo info) {
        Intent it = new Intent(context, DisplayInfoActivity.class);
        it.putExtra("storeInfo", info);
        context.startActivity(it);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drug_info);
        mImpl = UploadSQliteDbDaoImpl.getInstance(this);
        initView();
        initData();
    }

    private void initView() {
        txtDsCode = (EditText) findViewById(R.id.txtDsCode);
        txtDsName = (EditText) findViewById(R.id.txtDsName);
        textDrugBcode = (EditText) findViewById(R.id.textDrugBcode);
        txtDrugCode = (EditText) findViewById(R.id.txtDrugCode);
        txtDrugName = (EditText) findViewById(R.id.txtDrugName);
        txtDrugNumb = (EditText) findViewById(R.id.txtDrugNumb);
        txtDrugPrice = (EditText) findViewById(R.id.txtDrugPrice);
        textStoreNum = (EditText) findViewById(R.id.textStoreNum);
        textSalesVolume = (EditText) findViewById(R.id.textSalesVolume);
        textSeqNumb = (EditText) findViewById(R.id.textSeqNumb);

        spDispSurf = (Spinner) findViewById(R.id.spDispSurf);
        spDispPosi = (Spinner) findViewById(R.id.spDispPosi);
        spDispPosi.setSelection(3);
        btnZingCatch = (Button) findViewById(R.id.btnZingCatch);
        btnSeqCatch = (Button) findViewById(R.id.btnSeqCatch);
        photoImg = (ImageView) findViewById(R.id.photoImg);

        btnZingCatch.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent openCameraIntent = new Intent(DisplayInfoActivity.this,
                        CaptureActivity.class);
                startActivityForResult(openCameraIntent, 2);

            }
        });
        btnSeqCatch.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent openCameraIntent = new Intent(DisplayInfoActivity.this,
                        CaptureActivity.class);
                startActivityForResult(openCameraIntent, 3);

            }
        });
        textDrugBcode.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    searchDrugInfoByBcode(textDrugBcode.getText().toString());
                }
            }
        });
        txtDrugCode.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    searchDrugInfoDrugCode(txtDrugCode.getText().toString());
                }
            }
        });

        txtDrugNumb.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    mDrugNumbStr = txtDrugNumb.getText().toString();
                    if (!TextUtils.isEmpty(mDrugNumbStr)) {
                        mHandler.removeCallbacks(mRun);
                        mHandler.postDelayed(mRun, 250);
                    }
                }
            }
        });
//        textStoreNum.setOnFocusChangeListener(new OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if (!hasFocus && TextUtils.isEmpty(textStoreNum.getText())) {
//                    AndroidUtil.showLongToast(getApplication(), "【陈列库存】不能为空");
//                }
//            }
//        });
        txtDrugPrice.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && !TextUtils.isEmpty(txtDrugPrice.getText())) {
                    float drugPrice = Float.valueOf(txtDrugPrice.getText().toString());
                    if (drugPrice < mLowerLimitPrice) {
                        AndroidUtil.showLongToast(getApplication(), "【药品价格】低于最低零售价");
                    }
                }
            }
        });
//        txtDrugNumb.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                mDrugNumbStr = s.toString();
//                if(!TextUtils.isEmpty(mDrugNumbStr)) {
//                    mHandler.removeCallbacks(mRun);
//                    mHandler.postDelayed(mRun, 500);
//                }
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//        });
    }

    private String mDrugNumbStr = "";
    private boolean isDrugNumbIllegal = true;//药品批号非法
    private DrugNumbSQliteDbDaoImpl mImpl2 = null;
    private final Handler mHandler = new Handler();
    private final Runnable mRun = new Runnable() {
        @Override
        public void run() {
            mImpl2 = DrugNumbSQliteDbDaoImpl.getInstance(getApplicationContext());
            isDrugNumbIllegal = !mImpl2.validationDrugNumb(mDrugNumbStr);
            mHandler.removeCallbacks(this);
            if (isDrugNumbIllegal) {
                AndroidUtil.showLongToast(getApplication(), "【药品批号】不存在，请正确输入");
            }
        }
    };

    private void initData() {
        Intent it = getIntent();
        mInfo = (DrugstoreInfo) it.getSerializableExtra("storeInfo");
        Log.e("Test", "province ==" + mInfo.getProvince_name());
        txtDsCode.setText(mInfo.getDs_code2());
        txtDsName.setText(mInfo.getCname());
    }

    private void refreshData() {
        textDrugBcode.setText("");
        txtDrugCode.setText("");
        txtDrugName.setText("");
        txtDrugNumb.setText("");
        txtDrugPrice.setText("");
        textStoreNum.setText("0");
        textSeqNumb.setText("");
        textSalesVolume.setText("");

        spDispSurf.setSelection(0);
        spDispPosi.setSelection(3);
        photoImg.setImageBitmap(null);
        photoImg.setVisibility(View.GONE);
        mBitmap = null;
    }

    private boolean showBlankToast() {
        if (TextUtils.isEmpty(textDrugBcode.getText())) {
            AndroidUtil.showLongToast(getApplication(), "【药品条码】不能为空");

            return true;
        }
        if (TextUtils.isEmpty(txtDrugCode.getText())) {
            AndroidUtil.showLongToast(getApplication(), "【药品编码】不能为空");
            return true;
        }
        if (TextUtils.isEmpty(txtDrugName.getText())) {
            AndroidUtil.showLongToast(getApplication(), "【药品名称】不能为空");
            return true;
        }
        if (TextUtils.isEmpty(txtDrugNumb.getText())) {
            AndroidUtil.showLongToast(getApplication(), "【药品批号】不能为空");
            return true;
        }
        if (TextUtils.isEmpty(txtDrugPrice.getText())) {
            AndroidUtil.showLongToast(getApplication(), "【药品价格】不能为空");
            return true;
        }
        if (TextUtils.isEmpty(textStoreNum.getText())) {
            AndroidUtil.showLongToast(getApplication(), "【陈列库存】不能为空");
            return true;
        }

        if (!TextUtils.isEmpty(txtDrugPrice.getText())) {
            float drugPrice = Float.valueOf(txtDrugPrice.getText().toString());
            if (drugPrice < mLowerLimitPrice) {
                AndroidUtil.showLongToast(getApplication(), "【药品价格】低于最低零售价");
            }
        }
//        if (isDrugNumbIllegal) {
//            AndroidUtil.showLongToast(getApplication(), "【药品批号】不存在，请正确输入");
//            return true;
//        }
        return false;
    }

    public void getStoreNum(View v) {
        String drugCode = textDrugBcode.getText().toString();
        String dsCode = txtDsCode.getText().toString();
        if (!TextUtils.isEmpty(drugCode) && !TextUtils.isEmpty(dsCode)) {
            new GetSotroeNumTask(DisplayInfoActivity.this, dsCode, drugCode,
                    new onRuturnDataListener<DisplayInfo>() {

                        @Override
                        public void onResult(DisplayInfo result) {
//							txtDrugNumb.setText(result.getDrug_numb());
                            textStoreNum.setText(result.getStore_num());
                        }
                    }).execute();
        }
    }

    public void doPaizao(View v) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        imageFileCacheUri = imageFileUri;
        File file = new File(StorageConfig.getDisplayDir().getAbsolutePath(),
                "img_" + AndroidUtil.dateFormat(new Date(), "yyyyMMdd_hhmmss")
                        + ".jpg");
        imageFileUri = Uri.fromFile(file);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageFileUri);// 指定系统相机拍照保存在imageFileUri所指的位置
        startActivityForResult(intent, 1);
    }

    public void doSubmit(View v) {
        if (!showBlankToast()) {
            save();
            refreshData();
        }
    }

    public void doSaveAndEixt(View v) {
        if (!showBlankToast()) {
            save();
            finish();
        }
    }

    private void save() {
        DisplayInfo info = new DisplayInfo();

        info.setDs_code(mInfo.getDs_code2());
        info.setDs_name(mInfo.getCname());
        info.setDrug_code(txtDrugCode.getText().toString());
        info.setDrug_numb(txtDrugNumb.getText().toString());
        info.setDrug_bcode(textDrugBcode.getText().toString());
        info.setDrug_name(txtDrugName.getText().toString());
        info.setDisp_surf(String.valueOf(spDispSurf.getSelectedItem()));
        info.setDrug_price(txtDrugPrice.getText().toString());
        info.setDisp_posi(String.valueOf(spDispPosi.getSelectedItem()));
        info.setStore_num(textStoreNum.getText().toString());
        info.setSeq_numb(textSeqNumb.getText().toString());
        info.setMonthly_sales(textSalesVolume.getText().toString());
        info.setLoginId(MyApplication.sUser.getLoginid());

        info.setUploadTag("false");
        info.setCreateTime(AndroidUtil.dateFormat(new Date()));
        String name = "";
        String fileName = "";
        if (mBitmap != null) {
            name = new DateFormat().format("yyyyMMdd_hhmmss",
                    Calendar.getInstance(Locale.CHINA))
                    + ".jpg";
            File file = StorageConfig.getDisplayDir();
            if (!file.exists()) {
                file.mkdirs();// 创建文件夹
            }
            fileName = file.getAbsolutePath() + "/" + name;
            info.setImg_name(name);
            info.setImg_path(fileName);
        }
        mImpl.addDisplayInfo(info);
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
        clearCachePic();
    }

    public void doIntoOther(View v) {
        Intent intent = new Intent(DisplayInfoActivity.this,
                StoreChoiceActivity.class);
        startActivity(intent);
        finish();
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case 1:

                    String sdStatus = Environment.getExternalStorageState();
                    if (!sdStatus.equals(Environment.MEDIA_MOUNTED)
                            || !StorageConfig.isExistSDCard) { // 检测sd是否可用
                        Log.i("TestFile",
                                "SD card is not avaiable/writeable right now.");
                        return;
                    }
                    mBitmap = new BitmapScaleUtil()
                            .getimage(imageFileUri.getPath());
                    if (mBitmap == null) {
                        showMsg("拍照失败，请重试");
                        return;
                    }
                    photoImg.setVisibility(View.VISIBLE);
                    photoImg.setImageBitmap(mBitmap);// 将图片显示在ImageView里
                    clearCachePic();
                    imageFileCacheUri = imageFileUri;
                    break;
                case 2:
                    Bundle bundle = data.getExtras();
                    String scanResult = bundle.getString("result");
                    textDrugBcode.setText(scanResult);
                    searchDrugInfoByBcode(scanResult);
                    break;
                case 3:
                    bundle = data.getExtras();

                    scanResult = bundle.getString("result");
                    textSeqNumb.setText(scanResult);
                    break;

                default:
                    break;
            }
        }
    }

    private void searchDrugInfoByBcode(String scanResult) {
        Log.d("Test", "searchDrugInfoByBcode>>>>>" + scanResult);
        if (!TextUtils.isEmpty(scanResult)) {
            BasicSQliteDbDaoImpl impl = BasicSQliteDbDaoImpl
                    .getInstance(getApplicationContext());
            DrugInfo info = impl.getDrugInfoByBcode(scanResult);
            if (info != null) {
                mLowerLimitPrice = Float.valueOf(info.getPrice());
                txtDrugCode.setText(info.getDrug_code());
                txtDrugName.setText(info.getCname());
//				txtDrugPrice.setText(String.valueOf(info.getPrice()));
                textStoreNum.setText("0");
            } else {
                mLowerLimitPrice = 0;
                txtDrugCode.setText("");
                txtDrugName.setText("");
//				txtDrugPrice.setText("");
                textStoreNum.setText("0");
                Toast.makeText(getApplicationContext(), "没有找到该条码对应的信息",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void searchDrugInfoDrugCode(String scanResult) {
        Log.d("Test", "searchDrugInfoDrugCode>>>>>" + scanResult);
        BasicSQliteDbDaoImpl impl = BasicSQliteDbDaoImpl
                .getInstance(getApplicationContext());
        if (!TextUtils.isEmpty(scanResult)) {
            DrugInfo info = impl.getDrugInfoByDrugCode(scanResult);
            if (info != null) {
                mLowerLimitPrice = Float.valueOf(info.getPrice());
                textDrugBcode.setText(info.getDrug_bcode());
                txtDrugName.setText(info.getCname());
//				txtDrugPrice.setText(String.valueOf(info.getPrice()));
                textStoreNum.setText("0");
            } else {
                mLowerLimitPrice = 0;
                textDrugBcode.setText("");
                txtDrugName.setText("");
//				txtDrugPrice.setText("");
                textStoreNum.setText("0");
                Toast.makeText(getApplicationContext(), "没有找到该药品编码对应的信息",
                        Toast.LENGTH_SHORT).show();
            }
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
