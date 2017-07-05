package com.base.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AndroidUtil {
    public static String dateFormat(Date date) {
        String pattern = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat format = new SimpleDateFormat(pattern, Locale.CHINA);
        return format.format(date);
    }

    public static String dateFormat(Date date, String pattern) {
        SimpleDateFormat format = new SimpleDateFormat(pattern,Locale.CHINA);
        return format.format(date);
    }

    /**
     * 无网络提示并返回false，有网络返回true
     *
     * @param context
     * @return 是否有网络连接
     */
    public static boolean isNetWorkConnected(Context context) {
        if (context == null) {
            return false;
        }
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null && activeNetInfo.isConnected()) {
            return true;
        } else {
            return false;
        }
    }

    public static float versionToFloat(String version) {
        if (version != null && version.length() > 3) {
            StringBuffer sb = new StringBuffer();
            int dotIndex = version.indexOf(".");
            if (dotIndex == -1) {
                sb.append(version);
            } else {
                sb.append(version.substring(0, dotIndex + 2));
                String s1 = version.substring(dotIndex + 2, version.length());
                s1 = s1.replaceAll("\\.", "");
                sb.append(s1);
            }
            version = sb.toString();
        }
        float versionFloat = 0;
        try {
            versionFloat = Float.parseFloat(version);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return versionFloat;
    }

    /**
     * 获取应用的版本名称
     *
     * @param context
     * @param appPkgName
     * @return
     */
    public static String getAppVersion(Context context, String appPkgName) {
        PackageInfo pInfo = null;
        try {
            pInfo = context.getPackageManager().getPackageInfo(appPkgName, 0);
            if (pInfo != null) {
                return pInfo.versionName;
            }

        } catch (NameNotFoundException e) {
        }
        return "";
    }

    // 根据Unicode编码完美的判断中文汉字和符号
    public static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION) {
            return true;
        }
        return false;
    }

    /**
     * 判断密码格式
     *
     * @param psw
     * @return true 格式正确;false格式不正确
     */
    public static boolean judgePasswordFormat(String psw) {
        boolean flag = false;
        if (!TextUtils.isEmpty(psw)) {
            char[] chars = psw.toCharArray();
            for (char c : chars) {
                flag = isChinese(c);
                break;
            }
            if (!flag) {
                String regular = ".*[$' \"-].*";
                flag = psw.matches(regular);
            }
        }
        return !flag;
    }

    /**
     * 转换字节数组为16进制字串
     *
     * @param b 字节数组
     * @return 16进制字串
     */
    public static String byteArrayToHexString(byte[] b) {
        StringBuffer resultSb = new StringBuffer();
        for (int i = 0; i < b.length; i++) {
            resultSb.append(byteToHexString(b[i]));
        }
        return resultSb.toString().toUpperCase();
    }

    private final static String[] hexDigits = {"0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "A", "B", "C", "D", "E", "F"};

    private static String byteToHexString(byte b) {
        int n = b;
        if (n < 0)
            n = 256 + n;
        int d1 = n / 16;
        int d2 = n % 16;
        return hexDigits[d1] + hexDigits[d2];
    }

    private static Toast mToast = null;


    public static Handler uiHandler;

    public static Toast getmToast() {
        return mToast;
    }


    public static void showToastCenter(Context c, String message, int duration) {
        showToast(c, message, duration);
    }

    public static void showToastCenter(Context c, int msgId, int duration) {
        showToast(c, c.getString(msgId), duration);
    }

    public static void showLongToast(Context c, String message) {
        showToast(c, message, Toast.LENGTH_SHORT);
    }

    public static void showShortToast(Context c, String message) {
        showToast(c, message, Toast.LENGTH_SHORT);
    }

    public static void showToast(Toast mToast, Context c, String msg, int duration) {
        if (mToast != null) {
            mToast.setText(msg);
            mToast.setDuration(duration);
            mToast.setGravity(Gravity.CENTER, 0, 0);
        } else {
            mToast = Toast.makeText(c, msg, duration);
            mToast.setGravity(Gravity.CENTER, 0, 0);
        }
        mToast.show();
    }

    public static void showToast(Toast mToast, Context c, int stringId, int duration) {
        if (mToast != null) {
            mToast.setText(stringId);
            mToast.setDuration(duration);
            mToast.setGravity(Gravity.CENTER, 0, 0);
        } else {
            mToast = Toast.makeText(c, stringId, duration);
            mToast.setGravity(Gravity.CENTER, 0, 0);
        }
        mToast.show();
    }

    public static void showToast(final Context context, final String textMsg, final int duration) {
        if (Looper.myLooper() != Looper.getMainLooper()) {// 非UI主线程
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    showToast(context, textMsg, duration);
                }
            });
            return;
        }

        synchronized (AndroidUtil.class) {
            if (mToast != null) {
                mToast.setText(textMsg);
                mToast.setDuration(duration);
            } else {
                mToast = Toast.makeText(context, textMsg, duration);
            }
            mToast.show();
        }
    }
}
