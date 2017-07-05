package com.base.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.widget.Toast;

import com.api.IVSSBusinessAgent;
import com.api.bean.DrugInfo;
import com.api.bean.DrugNumbInfo;
import com.base.dao.BasicSQLiteDbDao;
import com.base.dao.impl.BasicSQliteDbDaoImpl;
import com.base.dao.impl.DrugNumbSQliteDbDaoImpl;
import com.zmap.login.activity.ManageActivity;

import java.util.List;
import java.util.concurrent.CancellationException;

public class GetDrugInfoTask extends AsyncTask<Void, Void, List<DrugInfo>> {
    private IVSSBusinessAgent mAgent = null;
    private ProgressDialog mDialog = null;
    private Context mContext;
    private Exception mException = null;
    private final int pageSize = 3000;
    private int pageNum = 1;

    public GetDrugInfoTask(Context mContext) {
        super();
        this.mContext = mContext;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mAgent = new IVSSBusinessAgent();
        mDialog = new ProgressDialog(mContext);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setMessage("下载产品信息…");
        mDialog.show();

    }

    @Override
    protected void onPostExecute(List<DrugInfo> result) {
        super.onPostExecute(result);
        if (result != null) {
            SharedPreferences sp = mContext.getSharedPreferences("ivss_sps",
                    Context.MODE_PRIVATE);
            Editor ed = sp.edit();
            ed.putBoolean("druginfo", true);
            ed.putBoolean("drug_numb_info", true);
            ed.commit();
            ed = null;
            sp = null;
            Toast.makeText(mContext, "下载产品信息完成", Toast.LENGTH_SHORT).show();
        } else {
            if (mException != null) {
                if (!TextUtils.isEmpty(mException.getMessage())) {

                    Toast.makeText(mContext, mException.getMessage(),
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mContext, "下载产品信息失败，请稍后重试",
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
        mDialog.dismiss();
    }

    @Override
    protected List<DrugInfo> doInBackground(Void... params) {
        List<DrugInfo> infos = null;
        boolean isLoop = true;
        boolean isDeleteTalbe = true;
        try {
            while (isLoop) {
                infos = mAgent.getDrugInfos(pageSize, pageNum);
                if (infos != null) {
                    BasicSQLiteDbDao impl = BasicSQliteDbDaoImpl
                            .getInstance(mContext);
                    impl.addDrugInfos(infos, isDeleteTalbe);
                    if (infos.size() < pageSize) {
                        isLoop = false;
                    }
                    isDeleteTalbe = false;
                    pageNum++;
                } else {
                    isLoop = false;
                }
            }
            List<DrugNumbInfo> mDrugNumbInfos = mAgent.getDrugNumbInfos();
            DrugNumbSQliteDbDaoImpl impl2 = DrugNumbSQliteDbDaoImpl
                    .getInstance(mContext);
            impl2.addDrugNumbInfos(mDrugNumbInfos, true);
            mDrugNumbInfos = null;
        } catch (CancellationException e) {
            e.printStackTrace();
        } catch (Exception e) {
            mException = e;
            e.printStackTrace();
        } finally {
            ManageActivity.isDownLoadDrugInfoIng = false;
        }

        return infos;
    }

}
