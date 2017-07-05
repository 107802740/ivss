package com.zmap.login.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.api.IVSSBusinessAgent;
import com.api.JsonUtil;
import com.api.bean.User;
import com.baidu.location.BDLocation;
import com.base.BaseActivity;
import com.base.Constant;
import com.base.bean.ApkVersionInfo;
import com.base.listener.onRuturnDataListener;
import com.base.service.DownloadService;
import com.base.service.DownloadService.DownLoadListener;
import com.base.task.GetLastVersionTask;
import com.base.util.AndroidUtil;
import com.base.util.BaiduLocationUtil;
import com.base.util.LocationInfoListener;
import com.storage.StorageConfig;
import com.zmap.datagather.activity1.MyApplication;
import com.zmap.datagather.activity1.R;
import com.zmap.login.bean.SpinnerData;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.concurrent.CancellationException;


public class LoginActivity extends BaseActivity {
    private String apk_msg = "正在下载";
    private String apk_path = "";
    private String apk_url = "";
    private String apk_version = "";

    private CheckBox chkPWD = null;
    private CheckBox chkUID = null;
    private EditText loginTxt = null;

    private EditText passwordTxt = null;
    private ProgressBar pbApk = null;
    private ProgressDialog pd = null;
    private int progress;
    private String sdk_path = "";
    private Spinner spXdMode;
    private TextView tvMsg = null;
    private EditText txtSVisitor = null;

    public void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(R.layout.login_main);
        initViews();
        initValues();
        initEvents();
        MyApplication.instance.addActivity(this);
        checkUpdateAPK();
    }

    private void initEvents() {
        this.spXdMode.setOnItemSelectedListener(new SpItemSelectedListener());
    }

    private void initValues() {
        String uid = getCookieValue("uid");
        String pwd = getCookieValue("pwd");
        String chkUID_value = getCookieValue("chkUID");
        String chkPWD_value = getCookieValue("chkPWD");
        if ("true".equals(chkUID_value)) {
            loginTxt.setText(uid);
            chkUID.setChecked(true);
        } else {
            loginTxt.setText("");
            chkUID.setChecked(false);
        }
        if ("true".equals(chkPWD_value)) {
            passwordTxt.setText(pwd);
            chkPWD.setChecked(true);
        } else {
            passwordTxt.setText("");
            chkPWD.setChecked(false);
        }
        // loginTxt.setText("test1");
        // passwordTxt.setText("123456");
        initSpinnerXdMode();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MyApplication.visitor = txtSVisitor.getText().toString();
    }

    private void initViews() {
        this.loginTxt = ((EditText) findViewById(R.id.login_username_etxt));
        this.passwordTxt = ((EditText) findViewById(R.id.login_password_etxt));
        this.txtSVisitor = ((EditText) findViewById(R.id.txtSVisitor));
        this.chkUID = ((CheckBox) findViewById(R.id.chkUID));
        this.chkPWD = ((CheckBox) findViewById(R.id.chkPWD));
        this.tvMsg = ((TextView) findViewById(R.id.tvMsg));
        this.pbApk = ((ProgressBar) findViewById(R.id.pbApk));
        this.spXdMode = ((Spinner) findViewById(R.id.spXdMode));
    }

    private void checkUpdateAPK() {
        String apkUpgradeStr = getStringCookieValue("upgrade");
        if (TextUtils.isEmpty(apkUpgradeStr)) {
            new GetLastVersionTask(new onRuturnDataListener<ApkVersionInfo>() {

                @Override
                public void onResult(ApkVersionInfo result) {
                    setCookieValue("upgrade", JsonUtil.toJsonString(result));
                    apk_url = Constant.HOST + result.getApkUrl();
                    apk_version = result.getApkVersion();
                    showUpgradeDialog();
                }
            }).execute();
        } else {
            ApkVersionInfo info = JsonUtil.fromJsonString(apkUpgradeStr,
                    ApkVersionInfo.class);
            apk_url = Constant.HOST + info.getApkUrl();
            apk_version = info.getApkVersion();
            showUpgradeDialog();
        }
    }

    public void showUpgradeDialog() {
        if (mContext == null) {
            return;
        }
        String version = AndroidUtil.getAppVersion(this, getPackageName());
        float versionFloat = AndroidUtil.versionToFloat(version);
        float apkVersionFloat = AndroidUtil.versionToFloat(apk_version);
        if (versionFloat >= apkVersionFloat) {
            setCookieValue("upgrade", "");
            if (isDoAutoUpdate) {
                showMsg("当前版本已是最新版本");
            }
            isDoAutoUpdate = false;
            return;
        }
        isDoAutoUpdate = false;
        setTitle("智能巡店-版本号【当前" + version + "/最新" + this.apk_version + "】");
        AlertDialog.Builder localBuilder = new AlertDialog.Builder(this);
        localBuilder.setTitle("软件版本升级");
        localBuilder.setMessage("检测到新版本【" + this.apk_version + "】");
        localBuilder.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface paramDialogInterface,
                                        int paramInt) {
                        paramDialogInterface.dismiss();
                        LoginActivity.this.loginTxt.setFocusable(false);
                        LoginActivity.this.passwordTxt.setFocusable(false);
                        LoginActivity.this.txtSVisitor.setFocusable(false);
                        LoginActivity.this.doDownApk();
                    }
                });
        localBuilder.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface paramDialogInterface,
                                        int paramInt) {
                        paramDialogInterface.dismiss();
                    }
                });
        localBuilder.create().show();
    }

    public void doCancel(View paramView) {
        this.loginTxt.setText("");
        this.passwordTxt.setText("");
    }

    public void doLogin(View paramView) {
        final String username;
        final String password;
        SpinnerData localSpinnerData;
        try {
            username = this.loginTxt.getText().toString();
            password = this.passwordTxt.getText().toString();
            localSpinnerData = (SpinnerData) this.spXdMode.getSelectedItem();
            if (username.equals("")) {
                showMsg("用户名不能为空");
                this.loginTxt.setFocusable(true);
                this.loginTxt.setFocusableInTouchMode(true);
                this.loginTxt.requestFocus();
                return;
            }
            if (password.equals("")) {
                showMsg("密码不能为空");
                this.passwordTxt.setFocusable(true);
                this.passwordTxt.setFocusableInTouchMode(true);
                this.passwordTxt.requestFocus();
                return;
            }
        } catch (Exception localException) {
            showMsg("登录异常：" + localException.getMessage());
            return;
        }
        setCookieValue("xdms", localSpinnerData.getValue());
        setCookieValue("uid", username);
        setCookieValue("pwd", password);
        setCookieValue("chkUID", this.chkUID.isChecked());
        setCookieValue("chkPWD", this.chkPWD.isChecked());
        new LoginTask(loginTxt.getText().toString(), passwordTxt.getText().toString()).execute();
//		mDialog = new ProgressDialog(LoginActivity.this);
//		mDialog.setMessage("正在登录…");
//		mDialog.setCanceledOnTouchOutside(false);
//		mDialog.show();
    }

    public void doLogout(View paramView) {
        finish();
    }

    private void doDownApk() {
        if (DownloadService.downloadListener != null
                && DownloadService.downloadListener.containsKey(apk_url)) {
            if (this.pd != null) {
                this.pd.show();
            }
            showMsg("正在更新应用程序…");
            return;
        }
        String appPath = StorageConfig.getAppExternalStorage()
                .getAbsolutePath();
        String name = new StringBuilder().append(getPackageName())
                .append(".apk").toString();
        File file = new File(appPath, name);
        if (file.exists()) {
            file.delete();
        }
        DownloadService.downloadListener.put(apk_url, new MyDownloadListener());
        Intent intent = new Intent(this, DownloadService.class);
        intent.putExtra("url", apk_url);
        intent.putExtra("path", appPath);
        intent.putExtra("name", name);
        startService(intent);
    }

    public void doUpdatePwd(View paramView) {
        try {
            startActivity(new Intent(this, UpdatePwdActivity.class));
            return;
        } catch (Exception localException) {
            showMsg("异常：" + localException.getMessage());
        }
    }

    private boolean isDoAutoUpdate = false;

    public void doAutoUpdate(View v) {
        if (DownloadService.downloadListener != null
                && DownloadService.downloadListener.containsKey(apk_url)) {
            if (this.pd != null) {
                this.pd.show();
            }
            showMsg("正在更新应用程序…");
            return;
        }
        isDoAutoUpdate = true;
        checkUpdateAPK();
    }

    public void initSpinnerXdMode() {
        try {
            ArrayList localArrayList = new ArrayList();
            localArrayList.add(new SpinnerData("1", "定位巡店"));
            localArrayList.add(new SpinnerData("2", "计划巡店"));
            ArrayAdapter localArrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item,
                    localArrayList);
            localArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            this.spXdMode.setAdapter(localArrayAdapter);
            String str = getCookieValue("xdms");
            if (("定位巡店".equals(str)) || ("1".equals(str))) {
                this.spXdMode.setSelection(0);
                return;
            }
            if (("计划巡店".equals(str)) || ("2".equals(str))) {
                this.spXdMode.setSelection(1);
                return;
            }
        } catch (Exception localException) {
            showMsg("initSpinnerXdMode.异常【" + localException.getMessage() + "】");
        }
    }

    private class SpItemSelectedListener implements
            AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> arg0, View arg1,
                                   int position, long id) {
            SpinnerData item = (SpinnerData) spXdMode.getSelectedItem();
            setCookieValue("xdms", item.getValue());
        }

        public void onNothingSelected(AdapterView<?> arg0) {
        }
    }

    @Override
    protected void onResume() {
        Log.d("Tast", "onResume>>>>>");
//		MyApplication.sUser = null;
//		stopService(new Intent(LoginActivity.this, UploadLogInfoService.class));
        super.onResume();
        if (Constant.sLocation == null) {
            new BaiduLocationUtil().getBaiduLocationInfo(LoginActivity.this, new LocationInfoListener() {
                @Override
                public void onReceiveLocation(BDLocation location) {
                    Constant.sLocation = location;
                }
            });
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("Tast", "onDestroy>>>>>>>>>>>>>>>");
    }

    private void initProgressDialog() {
        if (this.pd == null) {
            this.pd = new ProgressDialog(this);
            this.pd.setProgressStyle(1);
            this.pd.setTitle("下载巡店系统【" + this.apk_version + "】");
            this.pd.setMessage(this.apk_msg);
            this.pd.setCancelable(true);
            this.pd.setButton("取消", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface paramDialogInterface,
                                    int paramInt) {
                    stopService(new Intent(LoginActivity.this,
                            DownloadService.class));
                    paramDialogInterface.dismiss();
                    LoginActivity.this.pd = null;
                }
            });
            // this.pd.show();
        }
    }

    class MyDownloadListener implements DownLoadListener {

        @Override
        public void startDownload() {
            initProgressDialog();
            LoginActivity.this.pd.show();
        }

        @Override
        public void downloading(long progress, long apkSize) {
            if (apkSize > 0) {
                float mb = 1024 * 1024;
                float downed = progress / mb;
                float total = apkSize / mb;
                BigDecimal bd = new BigDecimal(downed);
                BigDecimal bd2 = new BigDecimal(total);
                int pro = (int) (progress * 100 / apkSize);
                initProgressDialog();
                LoginActivity.this.pd.setMessage(new StringBuilder()
                        .append(bd.setScale(2, BigDecimal.ROUND_HALF_UP))
                        .append("M/")
                        .append(bd2.setScale(2, BigDecimal.ROUND_HALF_UP))
                        .append("M"));
                LoginActivity.this.pd.setProgress(pro);
            }
        }

        @Override
        public void downloaded(Uri uri) {
            // 通过Intent安装APK文件
            LoginActivity.this.pd.dismiss();
            LoginActivity.this.pd = null;
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.setData(uri);
            i.setDataAndType(uri, "application/vnd.android.package-archive");
            startActivity(i);
            setCookieValue("upgrade", "");
        }

        @Override
        public void downloadFail() {
            LoginActivity.this.pd.dismiss();
            LoginActivity.this.pd = null;
        }

    }

    private ProgressDialog mDialog = null;

    private class LoginTask extends AsyncTask<Void, Void, User> {
        private ProgressDialog mDialog = null;
        private Exception exception = null;
        private String username;
        private String password;

        public LoginTask(String username, String password) {
            this.username = username;
            this.password = password;
        }

        @Override
        protected User doInBackground(Void... params) {
            User user = null;
            try {
                IVSSBusinessAgent agent = new IVSSBusinessAgent();

                user = agent.login(username, password);
            } catch (CancellationException e) {
                e.printStackTrace();
            } catch (Exception e) {
                exception = e;
                e.printStackTrace();
            }
            return user;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDialog = new ProgressDialog(LoginActivity.this);
            mDialog.setMessage("正在登录…");
            mDialog.setCanceledOnTouchOutside(false);
            mDialog.show();
        }

        @Override
        protected void onPostExecute(final User result) {
            super.onPostExecute(result);

            if (result != null) {
//                BaiduLocationUtil.getBaiduLocationInfo(LoginActivity.this, new LocationInfoListener() {
//                    @Override
//                    public void onReceiveLocation(BDLocation location) {
//
//                        Constant.sLocation = location;

                MyApplication.sUser = result;
                Toast.makeText(LoginActivity.this, "欢迎光临\n" + result.getName(),
                        Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginActivity.this,
                        ManageActivity.class);
                intent.putExtra("type", "login");
                startActivity(intent);
                mDialog.dismiss();
//                    }
//                });

            } else {
                mDialog.dismiss();
                if (exception != null
                        && !TextUtils.isEmpty(exception.getMessage())) {
                    Toast.makeText(LoginActivity.this, exception.getMessage(),
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(LoginActivity.this, "登录失败…",
                            Toast.LENGTH_SHORT).show();
                }

            }

        }

    }
}