package com.base.task;

import java.util.concurrent.CancellationException;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.widget.Toast;

import com.api.IVSSBusinessAgent;
import com.base.listener.onRuturnDataListener;

public class UpdataPswTask extends AsyncTask<Void, Void, Boolean> {
	private IVSSBusinessAgent mAgent = null;
	private ProgressDialog mDialog = null;
	private Context mContext;
	private Exception mException = null;
	private String mUsername;
	private String mPassword;
	private String mNewPassword;
	private onRuturnDataListener<Boolean> mListener;

	public UpdataPswTask(Context mContext, String username, String password,
			String newPassword, onRuturnDataListener<Boolean> listener) {
		super();
		this.mContext = mContext;
		this.mUsername = username;
		this.mPassword = password;
		this.mNewPassword = newPassword;
		this.mListener = listener;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		mAgent = new IVSSBusinessAgent();
		mDialog = new ProgressDialog(mContext);
		mDialog.setCanceledOnTouchOutside(false);
		mDialog.setMessage("正在修改密码……");
		mDialog.show();

	}

	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);
		if (result) {
			Toast.makeText(mContext, "修改密码成功", Toast.LENGTH_SHORT).show();
			if (mListener != null) {
				mListener.onResult(result);
			}
		} else {
			if (mException != null) {
				if (!TextUtils.isEmpty(mException.getMessage())) {

					Toast.makeText(mContext, mException.getMessage(),
							Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(mContext, "修改密码失败", Toast.LENGTH_SHORT)
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
			flag = mAgent.updataPsw(mUsername, mPassword, mNewPassword);
		} catch (CancellationException e) {
			e.printStackTrace();
		} catch (Exception e) {
			mException = e;
			e.printStackTrace();
		}
		return flag;
	}
}
