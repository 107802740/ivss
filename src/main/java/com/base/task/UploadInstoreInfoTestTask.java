package com.base.task;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.CancellationException;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.api.UploadDataAgent;
import com.api.bean.InstoreInfo;
import com.api.bean.PicPathInfo;
import com.base.dao.UploadSQLiteDbDao;
import com.base.dao.impl.UploadSQliteDbDaoImpl;
import com.base.util.AndroidUtil;
import com.storage.StorageConfig;
import com.zmap.datagather.activity1.MyApplication;

public class UploadInstoreInfoTestTask extends AsyncTask<Void, Void, Boolean> {
	private UploadDataAgent mAgent = null;
	private ProgressDialog mDialog = null;
	private Context mContext;
	private Exception mException = null;

	public UploadInstoreInfoTestTask(Context mContext) {
		super();
		this.mContext = mContext;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		mAgent = new UploadDataAgent();
		mDialog = new ProgressDialog(mContext);
		mDialog.setCanceledOnTouchOutside(false);
		mDialog.setMessage("上传信息…");
		mDialog.show();

	}

	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);
		if (mException != null) {
			if (!TextUtils.isEmpty(mException.getMessage())) {

				Toast.makeText(mContext, mException.getMessage(),
						Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(mContext, "上传信息失败", Toast.LENGTH_SHORT).show();
			}
		} else {
			if (result) {
				Toast.makeText(mContext, "上传信息完成", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(mContext, "上传信息失败", Toast.LENGTH_SHORT).show();

			}
		}
		mDialog.dismiss();
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		try {
			UploadSQLiteDbDao impl = UploadSQliteDbDaoImpl
					.getInstance(mContext);
			PicPathInfo resultInfo = null;
			InstoreInfo info = null;
			String path = StorageConfig.getInStoreImageDir().getAbsolutePath()
					+ "/20140805_014729.jpg";
			long time = System.currentTimeMillis();
			for (int i = 0; i < 100; i++) {
				info = new InstoreInfo();
				UUID uuid = UUID.randomUUID();
				int txt = uuid.hashCode();
				info.setArea_code("test_" + txt);
				info.setDs_code2("test_" + txt);
				info.setDs_name("test店名_" + txt);
				info.setFloginId(MyApplication.sUser.getLoginid());
				info.setImg_name("test_img_" + txt + ".jpg");
				info.setImg_path(path);
				info.setIn_date(AndroidUtil.dateFormat(new Date()));
				resultInfo = mAgent.uploadInstoreInfo(info.getDs_code2(),
						info.getDs_name(), info.getFloginId(),
						info.getIn_date(), info.getArea_code(),
						info.getImg_name(), info.getImg_path());
				info.setDs_upload_tag("true");
				info.setDs_upload(resultInfo.getUploadTime());
				impl.addInstoreInfo(info);
			}
			Log.d("Test", "time =" + (System.currentTimeMillis() - time));
			// DisplayInfo dInfo = null;
			// Random rd = new Random();
			// for (int i = 0; i < 1; i++) {
			// dInfo = new DisplayInfo();
			// UUID uuid = UUID.randomUUID();
			// int txt = uuid.hashCode();
			// dInfo.setDs_code("test_d_" + txt);
			// dInfo.setDrug_name("test_d_name_" + txt);
			// dInfo.setDrug_code(String.valueOf(rd.nextInt(500) + 100));
			// dInfo.setDrug_numb(String.valueOf(rd.nextInt(500) + 100));
			// dInfo.setDrug_bcode(String.valueOf(txt));
			// dInfo.setDrug_name("test_drug_name" + txt);
			// dInfo.setDisp_surf(String.valueOf(rd.nextInt(3) + 1));
			// dInfo.setDrug_price(String.valueOf(rd.nextFloat() * 20 + 6));
			// dInfo.setDisp_posi("差");
			// dInfo.setLoginId(MyApplication.sUser.getLoginid());
			// dInfo.setCreateTime(AndroidUtil.dateFormat(new Date()));
			// dInfo.setImg_name("test_img_" + txt + ".jpg");
			// dInfo.setImg_path(path);
			// resultInfo = mAgent.uploadDisplayInfo(dInfo.getDs_code(),
			// dInfo.getDs_name(), dInfo.getDrug_code(),
			// dInfo.getDrug_numb(), dInfo.getDrug_bcode(),
			// dInfo.getDrug_name(), dInfo.getDisp_surf(),
			// dInfo.getDrug_price(), dInfo.getDisp_posi(),
			// dInfo.getStore_num(), dInfo.getSeq_numb(),dInfo.getLoginId(),
			// dInfo.getCreateTime(), dInfo.getImg_name(),
			// dInfo.getImg_path());
			// dInfo.setUploadTag("true");
			// dInfo.setUploadtime(resultInfo.getUploadTime());
			// impl.addDisplayInfo(dInfo);
			// }

			return true;
		} catch (CancellationException e) {
			e.printStackTrace();
		} catch (Exception e) {
			mException = e;
			e.printStackTrace();
		}

		return false;
	}

}
