package com.base.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.widget.Toast;

import com.api.IVSSBusinessAgent;
import com.api.bean.DrugstoreInfo;
import com.api.bean.DrugstoreInfoList;
import com.api.bean.DrugstoreInfoListAndMd5;
import com.base.bean.DrugstoreInfoTable;
import com.base.dao.BasicSQLiteDbDao;
import com.base.dao.impl.BasicSQliteDbDaoImpl;
import com.zmap.login.activity.ManageActivity;

import java.util.List;
import java.util.concurrent.CancellationException;

public class GetDrugStoreInfoTask extends
        AsyncTask<Void, Void, DrugstoreInfoList> {
    private IVSSBusinessAgent mAgent = null;
    private ProgressDialog mDialog = null;
    private Context mContext;
    private Exception mException = null;
    private final int pageSize = 3000;
    private int totlePageNum = 1;
    private int pageNum = 1;

    public GetDrugStoreInfoTask(Context mContext) {
        super();
        this.mContext = mContext;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mAgent = new IVSSBusinessAgent();
        mDialog = new ProgressDialog(mContext);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setMessage("下载药店资料…");
        mDialog.show();

    }

    @Override
    protected void onPostExecute(DrugstoreInfoList result) {
        super.onPostExecute(result);
        ManageActivity.isDownLoadStoreInfoIng = false;
        if (result != null) {
            if (pageNum >= totlePageNum) {
                SharedPreferences sp = mContext.getSharedPreferences("ivss_sps",
                        Context.MODE_PRIVATE);
                Editor ed = sp.edit();
                ed.putBoolean("storeinfo", true);
                ed.commit();
                ed = null;
                sp = null;
                Toast.makeText(mContext, "下载药店资料完成", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(mContext, "下载药店资料中途出错，未能完全下载", Toast.LENGTH_SHORT).show();
            }
        } else {
            if (mException != null) {
                if (!TextUtils.isEmpty(mException.getMessage())) {

                    Toast.makeText(mContext, mException.getMessage(),
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mContext, "下载药店资料失败，请稍后重试",
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
        mDialog.dismiss();
    }

    @Override
    protected DrugstoreInfoList doInBackground(Void... params) {
        DrugstoreInfoList result = null;
        boolean isLoop = true;
        boolean isDeleteTalbe = true;
        BasicSQLiteDbDao impl = null;

        try {
            impl = BasicSQliteDbDaoImpl.getInstance(mContext);
            while (isLoop) {
                DrugstoreInfoListAndMd5 md5list = impl.getDrugstoreInfos2MD5(
                        pageSize, pageNum);
                result = mAgent.getDrugstoreInfo(pageSize, pageNum,
                        md5list.getMd5());
                if (result != null) {
                    List<DrugstoreInfo> infos = result.getRoot();
                    if (infos != null && infos.size() > 0) {
                        impl.addDrugstoreInfos(
                                DrugstoreInfoTable.TEMP_TABLE_NAME, infos,
                                isDeleteTalbe);
                    } else if (md5list.getList() != null
                            && md5list.getList().size() > 0) {
                        impl.addDrugstoreInfos(
                                DrugstoreInfoTable.TEMP_TABLE_NAME,
                                md5list.getList(), isDeleteTalbe);
                    }
                    totlePageNum = (int) Math.ceil(result.getTotleSize() * 1.0f / pageSize);
                    if (totlePageNum <= pageNum) {
                        isLoop = false;
                        break;
                    }
                    isDeleteTalbe = false;
                    pageNum++;
                } else {
                    isLoop = false;
                }
            }
        } catch (CancellationException e) {
            mException = e;
            e.printStackTrace();
        } catch (Exception e) {
            mException = e;
            e.printStackTrace();
        } finally {
            impl = BasicSQliteDbDaoImpl.getInstance(mContext);
            impl.copyData(DrugstoreInfoTable.TEMP_TABLE_NAME,
                    DrugstoreInfoTable.TABLE_NAME);
            ManageActivity.isDownLoadStoreInfoIng = false;
        }
        return result;
    }
}
