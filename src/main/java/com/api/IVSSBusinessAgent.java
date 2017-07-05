package com.api;

import android.text.TextUtils;

import com.api.bean.AddStoreInfo;
import com.api.bean.BaseResponse;
import com.api.bean.DisplayInfo;
import com.api.bean.DrugInfo;
import com.api.bean.DrugInfoList;
import com.api.bean.DrugNumbInfo;
import com.api.bean.DrugNumbInfoList;
import com.api.bean.DrugstoreInfo;
import com.api.bean.DrugstoreInfoList;
import com.api.bean.User;
import com.base.Constant;
import com.base.bean.ApkVersionInfo;
import com.zmap.datagather.activity1.MyApplication;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CancellationException;

/**
 * 服务器接口实现
 *
 * @author majx
 */
public class IVSSBusinessAgent extends APIAbstractAgent {
    private String server_action_url = "";
    private String mUriAction = "";
    private Map<String, String> mMap = null;

    public IVSSBusinessAgent() {
        server_action_url = Constant.SERVICE_RUL;
    }

    /**
     * 登录
     *
     * @param userId
     * @return
     * @throws CancellationException
     * @throws FPAPIException
     * @throws IOException
     */
    public User login(String username, String password)
            throws CancellationException, Exception {
        reset();
        mUriAction = "/login.do";
        mMap = new HashMap<String, String>();
        mMap.put("username", username);
        mMap.put("password", password);

        User accountInfo = doPost(mMap, server_action_url, mUriAction,
                User.class);
        if (accountInfo.result < 0) {
            throw new Exception(accountInfo.msg);
        } else {
            accountInfo.setPwd(password);
        }
        return accountInfo;
    }

    /**
     * 修改密码
     *
     * @param userId
     * @return
     * @throws CancellationException
     * @throws FPAPIException
     * @throws IOException
     */
    public boolean updataPsw(String username, String password,
                             String newPassword) throws CancellationException, Exception {
        reset();
        mUriAction = "/updataPsw.do";
        mMap = new HashMap<String, String>();
        mMap.put("username", username);
        mMap.put("password", password);
        mMap.put("newPassword", newPassword);

        BaseResponse response = doPost(mMap, server_action_url, mUriAction,
                BaseResponse.class);
        if (response.result < 0) {
            throw new Exception(response.msg);
        } else {
            return true;
        }
    }

    public List<DrugInfo> getDrugInfos(int pageSize, int pageNum)
            throws CancellationException, Exception {
        prepare();
        mUriAction = "/getDrugInfos.do";
        mMap = new HashMap<String, String>();
        mMap.put("username", MyApplication.sUser.getLoginid());
        mMap.put("password", MyApplication.sUser.getPwd());
        mMap.put("pageSize", String.valueOf(pageSize));
        mMap.put("pageNum", String.valueOf(pageNum));
        DrugInfoList druginfos = doPost(mMap, server_action_url, mUriAction,
                DrugInfoList.class);
        if (druginfos.result < 0) {
            throw new Exception(druginfos.msg);
        }
        return druginfos.getRoot();
    }

    public List<DrugNumbInfo> getDrugNumbInfos()
            throws CancellationException, Exception {
        prepare();
        mUriAction = "/getBatchNos.do";
        mMap = new HashMap<String, String>();
        mMap.put("username", MyApplication.sUser.getLoginid());
        mMap.put("password", MyApplication.sUser.getPwd());
        if(Constant.sLocation!=null){
            mMap.put("province", Constant.sLocation.getProvince());
        }
        mMap.put("province", "广东省");
        DrugNumbInfoList druginfos = doPost(mMap, server_action_url, mUriAction,
                DrugNumbInfoList.class);
        if (druginfos.result < 0) {
            throw new Exception(druginfos.msg);
        }
        return druginfos.getRoot();
    }

    public DrugstoreInfoList getDrugstoreInfo(int pageSize, int pageNum,
                                              String md5) throws CancellationException, Exception {
        prepare();
        mUriAction = "/getDrugStoreInfos.do";
        mMap = new HashMap<String, String>();
        mMap.put("username", MyApplication.sUser.getLoginid());
        mMap.put("password", MyApplication.sUser.getPwd());
        mMap.put("pageSize", String.valueOf(pageSize));
        mMap.put("pageNum", String.valueOf(pageNum));
        mMap.put("md5", md5);

        DrugstoreInfoList druginfos = doPost(mMap, server_action_url,
                mUriAction, DrugstoreInfoList.class);
        if (druginfos.result < 0) {
            throw new Exception(druginfos.msg);
        }
        return druginfos;
    }

    public AddStoreInfo addStore(DrugstoreInfo info)
            throws CancellationException, Exception {
        prepare();
        mUriAction = "/addStore.do";
        mMap = new HashMap<String, String>();

        mMap.put("cname", info.getCname());
        mMap.put("pointx", info.getPointx());
        mMap.put("pointy", info.getPointy());
        mMap.put("address", info.getAddress());
        mMap.put("levl", info.getLevl());
        mMap.put("month_amt", info.getMonth_amt());
        mMap.put("sales", info.getSales());
        mMap.put("sale_dbzh", info.getSale_dbzh());
        mMap.put("dest_flag", info.getDest_flag());
        mMap.put("area_code", info.getArea_code());
        mMap.put("use_flag", info.getUse_flag());
        mMap.put("city_name", info.getCity_name());
        mMap.put("province_name", info.getProvince_name());

        AddStoreInfo ds_codes = doPost(mMap, server_action_url, mUriAction,
                AddStoreInfo.class);
        if (ds_codes.result < 0) {
            throw new Exception(ds_codes.msg);
        }
        return ds_codes;
    }

    public ApkVersionInfo getLastVersion() throws CancellationException,
            Exception {
        reset();
        mUriAction = "/getLastVersoin.do";

        ApkVersionInfo info = doPost(mMap, server_action_url, mUriAction,
                ApkVersionInfo.class);
        if (info.result < 0) {
            throw new Exception(info.msg);
        }
        return info;
    }

    /**
     * 登录
     *
     * @param userId
     * @return
     * @throws CancellationException
     * @throws FPAPIException
     * @throws IOException
     */
    public DisplayInfo getStoreNum(String ds_code, String drug_code)
            throws CancellationException, Exception {
        reset();
        mUriAction = "/getStoreNum.do";
        mMap = new HashMap<String, String>();
        mMap.put("ds_code", ds_code);
        mMap.put("drug_code", drug_code);

        DisplayInfo info = doPost(mMap, server_action_url, mUriAction,
                DisplayInfo.class);
        if (info.result < 0) {
            throw new Exception(info.msg);
        }
        return info;
    }

    public boolean uploadLogInfo(double pointx, double pointy)
            throws CancellationException, Exception {
        prepare();
        boolean flag = false;
        mUriAction = "/upLoadLogInfo.do";
        mMap = new HashMap<String, String>();
        if (MyApplication.sUser != null
                && !TextUtils.isEmpty(MyApplication.sUser.getLoginid())) {
            mMap.put("loginId", MyApplication.sUser.getLoginid());
            mMap.put("pointx", String.valueOf(pointx));
            mMap.put("pointy", String.valueOf(pointy));
            BaseResponse result = doPost(mMap, server_action_url, mUriAction,
                    BaseResponse.class);
            if (result.result < 0) {
                flag = false;
            }
        }
        return flag;
    }
}
