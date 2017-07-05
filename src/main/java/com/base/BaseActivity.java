package com.base;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.api.bean.User;
import com.base.util.AndroidUtil;
import com.umeng.analytics.MobclickAgent;
import com.zmap.datagather.activity1.MyApplication;
import com.zmap.datagather.activity1.R;

public class BaseActivity extends Activity {
    protected Context mContext = null;
    public String spsName = "ivss_sps";
    private ConnectivityBrocastReceiver mReceiver = null;
    private onConnectivityListener mListener = null;
    private TextView titleView = null;
    private String titleTxt = null;

    public void setOnConnectivityListener(onConnectivityListener mListener) {
        this.mListener = mListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        MyApplication.instance.addActivity(this);
        Log.d("Tast", this.getClass().getSimpleName() + ":onCreate:"
                + MyApplication.sUser);
        mContext = this;

        String version = AndroidUtil.getAppVersion(this, getPackageName());
        if (MyApplication.sUser != null) {
            titleTxt = getString(R.string.app_name) + "V" + version + "-欢迎光临-"
                    + MyApplication.sUser.getName();
        } else {
            titleTxt = getString(R.string.app_name) + "V" + version;
        }
//		setTitleColor(0xFF6EB609);
        MobclickAgent.onError(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        if (titleView == null) {
            titleView = (TextView) findViewById(R.id.titleView);
        }
        titleView.setText(titleTxt);

    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (MyApplication.instance != null) {
            MyApplication.instance.remove(this);
        }
        mContext = null;
    }

    public String getCookieValue(String paramString) {
        return getSharedPreferences(this.spsName, Context.MODE_PRIVATE)
                .getString(paramString, "");
    }

    public float getFloatCookieValue(String paramString) {
        return getSharedPreferences(this.spsName, Context.MODE_PRIVATE)
                .getFloat(paramString, 0.0F);
    }

    public int getIntCookieValue(String paramString) {
        return getSharedPreferences(this.spsName, Context.MODE_PRIVATE).getInt(
                paramString, 0);
    }

    public long getLongCookieValue(String paramString) {
        return getSharedPreferences(this.spsName, Context.MODE_PRIVATE)
                .getLong(paramString, 0L);
    }

    public String getStringCookieValue(String paramString) {
        return getSharedPreferences(this.spsName, Context.MODE_PRIVATE)
                .getString(paramString, "");
    }

    public void pln(Object paramObject) {
        System.out.println(paramObject);
    }

    public void setCookieValue(String key, Object value) {
        SharedPreferences.Editor localEditor;
        if ((key != null) && (value != null)) {
            localEditor = getSharedPreferences(this.spsName,
                    Context.MODE_PRIVATE).edit();
            if (value instanceof Integer) {
                localEditor
                        .putInt(key, Integer.parseInt(String.valueOf(value)));
            } else if (value instanceof Float) {
                localEditor.putFloat(key,
                        Float.parseFloat(String.valueOf(value)));
            } else if (value instanceof Long) {
                localEditor.putLong(key, Long.parseLong(String.valueOf(value)));
            } else {
                localEditor.putString(key, value.toString());
            }
            localEditor.commit();
        }
    }

    public void showMsg(Object paramObject) {
        Toast.makeText(this, paramObject.toString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("user", MyApplication.sUser);
        Log.d("Tast", "onSaveInstanceState =" + MyApplication.sUser);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.d("Tast", "onRestoreInstanceState1 =" + MyApplication.sUser);
        MyApplication.sUser = (User) savedInstanceState.getSerializable("user");
        Log.d("Tast", "onRestoreInstanceState2 =" + MyApplication.sUser);
    }

    public void registerReceiver() {
        mReceiver = new ConnectivityBrocastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mReceiver, filter);
    }

    public void unRegisterReceiver() {
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }
    }

    class ConnectivityBrocastReceiver extends BroadcastReceiver {
        private int lastType = -1;

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
                ConnectivityManager connectMgr = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectMgr.getActiveNetworkInfo();
                if (networkInfo == null
                        || !connectMgr.getBackgroundDataSetting()) {
                } else {
                    // 联网操作
                    if (mListener != null) {
                        mListener.onAction();
                    }
                }
            }
        }
    }

    public interface onConnectivityListener {
        public void onAction();
    }
}