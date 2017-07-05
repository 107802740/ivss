package com.base.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.widget.Toast;

import com.api.UploadDataAgent;
import com.api.bean.DisplayInfo;
import com.api.bean.InstoreInfo;
import com.api.bean.PicPathInfo;
import com.base.dao.UploadSQLiteDbDao;
import com.base.dao.impl.UploadSQliteDbDaoImpl;
import com.zmap.datagather.activity1.MyApplication;
import com.zmap.login.activity.ManageActivity;

import java.util.List;
import java.util.concurrent.CancellationException;

public class UploadInstoreInfoTask extends AsyncTask<Void, Void, Boolean> {
	private UploadDataAgent mAgent = null;
	private ProgressDialog mDialog = null;
	private Context mContext;
	private Exception mException = null;
	private List<InstoreInfo> mInstoreInfos;
	private List<DisplayInfo> mDisplayInfos;

	public UploadInstoreInfoTask(Context mContext) {
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
				if ((mInstoreInfos == null || (mInstoreInfos != null && mInstoreInfos
						.size() <= 0))
						&& ((mDisplayInfos == null || (mDisplayInfos != null && mDisplayInfos
								.size() <= 0)))) {
					Toast.makeText(mContext, "暂无需要上传的信息", Toast.LENGTH_SHORT)
							.show();
				} else {
					Toast.makeText(mContext, "上传信息完成", Toast.LENGTH_SHORT)
							.show();
				}
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
			mDisplayInfos = impl.getDisplayInfos(MyApplication.sUser
					.getLoginid());
			mInstoreInfos = impl.getInstoreInfo(MyApplication.sUser
					.getLoginid());
			if (mInstoreInfos != null) {
				for (InstoreInfo info : mInstoreInfos) {
					resultInfo = mAgent.uploadInstoreInfo(info.getDs_code2(),
							info.getDs_name(), info.getFloginId(),
							info.getIn_date(), info.getArea_code(),
							info.getImg_name(), info.getImg_path());
					info.setDs_upload_tag("true");
					info.setDs_upload(resultInfo.getUploadTime());
					impl.addInstoreInfo(info);
				}
			}
			if (mDisplayInfos != null) {
				for (DisplayInfo info : mDisplayInfos) {
					resultInfo = mAgent.uploadDisplayInfo(info.getDs_code(),
							info.getDs_name(), info.getDrug_code(),
							info.getDrug_numb(), info.getDrug_bcode(),
							info.getDrug_name(), info.getDisp_surf(),
							info.getDrug_price(), info.getDisp_posi(),
							info.getStore_num(), info.getSeq_numb(),
							info.getLoginId(), info.getCreateTime(),
							info.getImg_name(), info.getImg_path(),info.getMonthly_sales());
					info.setUploadTag("true");
					info.setUploadtime(resultInfo.getUploadTime());
					impl.addDisplayInfo(info);
				}
			}
			return true;
		} catch (CancellationException e) {
			e.printStackTrace();
		} catch (Exception e) {
			mException = e;
			e.printStackTrace();
		} finally {
			ManageActivity.isUploadInfoIng = false;
		}

		return false;
	}

}
