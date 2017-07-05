package com.base.task;

import java.util.concurrent.CancellationException;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.widget.Toast;

import com.api.IVSSBusinessAgent;
import com.api.bean.AddStoreInfo;
import com.api.bean.DrugstoreInfo;
import com.base.dao.BasicSQLiteDbDao;
import com.base.dao.impl.BasicSQliteDbDaoImpl;
import com.base.listener.onRuturnDataListener;
import com.zmap.login.activity.AddStroeActivity;

public class UploadInfoTask extends AsyncTask<Void, Void, Boolean> {
	private IVSSBusinessAgent mAgent = null;
	private ProgressDialog mDialog = null;
	private Context mContext;
	private Exception mException = null;
	private DrugstoreInfo mInfo = null;
	private onRuturnDataListener<Boolean> mListener;

	public UploadInfoTask(Context mContext, DrugstoreInfo info,
			onRuturnDataListener<Boolean> listener) {
		super();
		this.mContext = mContext;
		this.mInfo = info;
		this.mListener = listener;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		mAgent = new IVSSBusinessAgent();
		mDialog = new ProgressDialog(mContext);
		mDialog.setCanceledOnTouchOutside(false);
		mDialog.setMessage("正在上传药店数据……");
		mDialog.show();

	}

	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);
		if (result) {
			Toast.makeText(mContext, "新增药店完成", Toast.LENGTH_SHORT).show();
			if (mListener != null) {
				mListener.onResult(result);
			}
		} else {
			if (mException != null) {
				if (!TextUtils.isEmpty(mException.getMessage())) {

					Toast.makeText(mContext, mException.getMessage(),
							Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(mContext, "新增药店失败，请稍后重试", Toast.LENGTH_SHORT)
							.show();
				}
			}
		}
		mDialog.dismiss();
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		boolean flag = false;
		try {
			AddStoreInfo info = mAgent.addStore(mInfo);
			mInfo.setDs_code2(info.getDs_code2());
			mInfo.setCid(info.getCid());
			BasicSQLiteDbDao dao = BasicSQliteDbDaoImpl.getInstance(mContext);
			dao.addDrugStoreInfo(mInfo);
			flag = true;
		} catch (CancellationException e) {
			e.printStackTrace();
		} catch (Exception e) {
			mException = e;
			e.printStackTrace();
		} finally {
			AddStroeActivity.isAddingStrore = false;
		}
		return flag;
	}
}
