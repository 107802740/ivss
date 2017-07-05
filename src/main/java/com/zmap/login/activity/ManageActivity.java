package com.zmap.login.activity;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.base.BaseActivity;
import com.base.Constant;
import com.base.bean.DrugInfoTable;
import com.base.bean.DrugstoreInfoTable;
import com.base.dao.BasicSQLiteDbDao;
import com.base.dao.impl.BasicSQliteDbDaoImpl;
import com.base.task.GetDrugInfoTask;
import com.base.task.GetDrugStoreInfoTask;
import com.base.task.UploadInstoreInfoTask;
import com.base.util.BaiduLocationUtil;
import com.base.util.LocationInfoListener;
import com.storage.StorageConfig;
import com.zmap.datagather.activity1.R;

import java.io.File;
import java.lang.ref.WeakReference;

public class ManageActivity extends BaseActivity implements OnClickListener {
    public static boolean isDownLoadStoreInfoIng = false;
    public static boolean isDownLoadDrugInfoIng = false;
    public static boolean isUploadInfoIng = false;
    public static boolean isDeleting = false;

    private BasicSQLiteDbDao impl = null;
    private Button mGotoStoreChoice = null;
    private ProgressDialog mDialog = null;

    private final MyHandler mHandler = new MyHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manage_layout);
        impl = BasicSQliteDbDaoImpl.getInstance(ManageActivity.this);
        mGotoStoreChoice = (Button) findViewById(R.id.goto_store_choice);

        mDialog = new ProgressDialog(this);
        mDialog.setCanceledOnTouchOutside(false);
        mGotoStoreChoice.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.goto_store_choice:
                boolean flag1 = impl
                        .isTableDataExist(DrugstoreInfoTable.TABLE_NAME);
                boolean flag2 = impl.isTableDataExist(DrugInfoTable.TABLE_NAME);
                SharedPreferences sp = getSharedPreferences("ivss_sps",
                        Context.MODE_PRIVATE);
                boolean flag_store = sp.getBoolean("storeinfo", false);
                boolean flag_drug = sp.getBoolean("druginfo", false);
                boolean flag_drug_numb = sp.getBoolean("drug_numb_info", false);
                sp = null;
                if ((!flag1 && !flag_store) || (!flag2 && !flag_drug)
                        || isDownLoadDrugInfoIng || isDownLoadStoreInfoIng || !flag_drug_numb) {
                    String str = "您还没有下载基础数据，跳过可能导致部分操作出错，是否确认进入巡店界面？";
                    if (!flag1 && !flag_store) {
                        str = "您还没有下载药店资料，跳过可能导致部分操作出错，是否确认进入巡店界面？";
                    } else if (!flag2 && !flag_drug) {
                        str = "您还没有下载产品信息，跳过可能导致部分操作出错，是否确认进入巡店界面？";
                    } else if (isDownLoadStoreInfoIng) {
                        str = "您目前正在下载药店资料，下载完成前进入巡店界面可能影响您接下来的操作，是否确认进入巡店界面？";
                    } else if (isDownLoadDrugInfoIng) {
                        str = "您目前正在下载产品信息，下载完成前进入巡店界面可能影响您接下来的操作，是否确认进入巡店界面？";
                    } else {
                        if (!flag_drug_numb) {
                            str = "产品信息有所更新，跳过可能导致部分操作出错，是否确认进入巡店界面？";
                        }
                    }

                    AlertDialog dialog = new AlertDialog.Builder(
                            ManageActivity.this).create();
                    dialog.setTitle("警告");
                    dialog.setMessage(str);
                    dialog.setButton(DialogInterface.BUTTON_POSITIVE, "确定",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    Intent intent = new Intent(ManageActivity.this,
                                            StoreChoiceActivity.class);
                                    startActivity(intent);
                                }
                            });
                    dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    dialog.dismiss();
                                }
                            });
                    dialog.show();
                } else {
                    Intent intent = new Intent(ManageActivity.this,
                            StoreChoiceActivity.class);
                    startActivity(intent);
                }

                break;

            default:
                break;
        }
    }

    public void downloadDrugStoreInfo(View v) {
        if (isDownLoadStoreInfoIng) {
            showMsg("正在下载药店资料……");
            return;
        }
        Builder dialog = new AlertDialog.Builder(ManageActivity.this)
                .setTitle("下载提示")
                .setMessage(
                        "下载基础数据将消耗您一定的时间与流量，为节省您的时间与流量，建议在WLAN环境下进行下载，是否确认下载药店资料？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        isDownLoadStoreInfoIng = true;
                        File file = new File(StorageConfig.getDataDir()
                                .getAbsoluteFile(), "logs.txt");
                        if (file.exists()) {
                            file.delete();
                        }
                        new GetDrugStoreInfoTask(ManageActivity.this).execute();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        dialog.create().show();

    }

    public void downloadDrugInfo(View v) {
        if (isDownLoadDrugInfoIng) {
            showMsg("正在下载产品信息……");
            return;
        }
        if (Constant.sLocation == null) {
            new BaiduLocationUtil().getBaiduLocationInfo(ManageActivity.this, new LocationInfoListener() {
                @Override
                public void onReceiveLocation(BDLocation location) {
                    Constant.sLocation = location;

                }
            });
        }
        Builder dialog = new AlertDialog.Builder(ManageActivity.this)
                .setTitle("下载提示")
                .setMessage(
                        "下载基础数据将消耗您一定的时间与流量，为节省您的时间与流量，建议在WLAN环境下进行下载，是否确认下载产品信息？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        isDownLoadDrugInfoIng = true;
                        new GetDrugInfoTask(ManageActivity.this).execute();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        dialog.create().show();

    }

    public void cleanCache(View v) {
        if (isDeleting) {
            showMsg("正在清除缓存……");
            return;
        }
        Builder dialog = new AlertDialog.Builder(ManageActivity.this)
                .setTitle("删除警告")
                .setMessage(
                        "警告：接下来操作将清除本地缓存数据，如您还有数据未上传到服务器，将丢失数据并无法还原，是否确认删除？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        isDeleting = true;
                        File file = StorageConfig.getAppExternalStorage();
                        cleanFile(file);
                        mDialog.setMessage("正在清除缓存…");
                        mDialog.show();
                        new Thread() {
                            public void run() {
                                Message msg = new Message();
                                msg.what = 5;
                                mHandler.sendMessageDelayed(msg, 2000);
                            }

                            ;
                        }.start();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        dialog.create().show();
    }

    private void cleanFile(File file) {
        if (file.isFile()) {
            file.delete();
        } else {
            File[] filelist = file.listFiles();
            for (File f : filelist) {
                if (f.isFile()) {
                    f.delete();
                } else {
                    cleanFile(f);
                }
            }
        }
    }

    public void uploadInfo(View v) {
        if (isUploadInfoIng) {
            showMsg("正在上传巡店数据……");
            return;
        }
        Builder dialog = new AlertDialog.Builder(ManageActivity.this)
                .setTitle("上传提示")
                .setMessage(
                        "上传巡店信息将消耗您一定的时间与流量，为节省您的时间与流量，建议在WLAN环境下进行上传，是否确认上传信息？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        isUploadInfoIng = true;
                        new UploadInstoreInfoTask(ManageActivity.this)
                                .execute();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        dialog.create().show();

    }

    // public void uploadInfoTest(View v) {
    // long time = System.currentTimeMillis();
    // // new UploadInstoreInfoTestTask(this).execute();
    // SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    // String createTime = format.format(new Date());
    // final DrugstoreInfo info = new DrugstoreInfo();
    // info.setAddress("广东省广州市");
    // info.setArea_code("133");
    // info.setCity_name("广州市");
    // info.setCname("测试&");
    // info.setDest_flag("否");
    // info.setFcreatetime(createTime);
    // info.setLevl("B");
    // info.setMonth_amt("15000");
    // info.setPointx("113.564645");
    // info.setPointy("23.98478395");
    // info.setProvince_name("广东省");
    // info.setSale_dbzh("123");
    // info.setSales("james");
    // info.setUse_flag("是");
    // for (int i = 0; i < 300; i++) {
    // new Thread() {
    // public void run() {
    // IVSSBusinessAgent agent = new IVSSBusinessAgent();
    // try {
    // agent.addStore(info);
    // } catch (CancellationException e) {
    // e.printStackTrace();
    // } catch (Exception e) {
    // e.printStackTrace();
    // }
    // };
    // }.start();
    // }
    // Log.d("Test", "time>>>>>" + (System.currentTimeMillis() - time));
    // }

    public void downloadAreaInfo(View v) {
        Toast.makeText(this, "暂不支持，敬请期待", Toast.LENGTH_SHORT).show();
    }

    private static class MyHandler extends Handler {
        private final WeakReference<ManageActivity> mActivity;

        public MyHandler(ManageActivity activity) {
            this.mActivity = new WeakReference<ManageActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {

            super.handleMessage(msg);
            ManageActivity activity = mActivity.get();
            if (activity != null) {
                if (msg != null) {
                    int what = msg.what;
                    if (what == 3) {
                        // 下载地区资料
                        activity.mDialog.dismiss();
                    } else if (what == 5) {
                        // 消除缓存
                        isDeleting = false;
                        activity.mDialog.dismiss();
                        Toast.makeText(activity, "消除缓存完成",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }
}
