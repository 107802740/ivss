package com.base.task;

import java.util.concurrent.CancellationException;

import android.os.AsyncTask;
import android.text.TextUtils;

import com.api.IVSSBusinessAgent;
import com.base.bean.ApkVersionInfo;
import com.base.listener.onRuturnDataListener;

public class GetLastVersionTask extends AsyncTask<Void, Void, ApkVersionInfo> {
	private onRuturnDataListener<ApkVersionInfo> mListener;

	public GetLastVersionTask(onRuturnDataListener<ApkVersionInfo> mListener) {
		this.mListener = mListener;
	}

	@Override
	protected ApkVersionInfo doInBackground(Void... params) {
		IVSSBusinessAgent agent = new IVSSBusinessAgent();
		ApkVersionInfo info = null;
		try {
			info = agent.getLastVersion();
		} catch (CancellationException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return info;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	@Override
	protected void onPostExecute(ApkVersionInfo result) {
		super.onPostExecute(result);
		if (result != null && !TextUtils.isEmpty(result.getApkVersion())) {
			if (mListener != null) {
				mListener.onResult(result);
			}
		}
	}

}
