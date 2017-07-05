package com.base.task;

import java.util.concurrent.CancellationException;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.widget.Toast;

import com.api.IVSSBusinessAgent;
import com.api.bean.DisplayInfo;
import com.base.listener.onRuturnDataListener;

public class GetSotroeNumTask extends AsyncTask<Void, Void, DisplayInfo> {
	private IVSSBusinessAgent mAgent = null;
	private Context mContext;
	private Exception mException = null;
	private String mDsCode;
	private String mDrugCode;
	private onRuturnDataListener<DisplayInfo> mListener;

	public GetSotroeNumTask(Context mContext, String ds_code, String drug_code,
			onRuturnDataListener<DisplayInfo> listener) {
		super();
		this.mContext = mContext;
		this.mDsCode = ds_code;
		this.mDrugCode = drug_code;
		this.mListener = listener;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		mAgent = new IVSSBusinessAgent();

	}

	@Override
	protected void onPostExecute(DisplayInfo result) {
		super.onPostExecute(result);
		if (result != null) {
			if (mListener != null) {
				mListener.onResult(result);
			}
		} else {
			if (mException != null) {
				if (!TextUtils.isEmpty(mException.getMessage())) {
					Toast.makeText(mContext, mException.getMessage(),
							Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(mContext, "获取药品库存信息失败，请稍后重试",
							Toast.LENGTH_SHORT).show();
				}
			}
		}
	}

	@Override
	protected DisplayInfo doInBackground(Void... params) {
		DisplayInfo info = null;
		try {
			info = mAgent.getStoreNum(mDsCode, mDrugCode);
		} catch (CancellationException e) {
			e.printStackTrace();
		} catch (Exception e) {
			mException = e;
			e.printStackTrace();
		}

		return info;
	}

}
