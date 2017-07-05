package com.base.service;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.StatFs;
import android.text.TextUtils;

import com.storage.StorageConfig;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

public class DownloadService extends Service {
    private boolean running = false;
    public static Map<String, DownLoadListener> downloadListener = new HashMap<String, DownLoadListener>();
    public static Map<String, Integer> downloadProgress = new HashMap<String, Integer>();
    private Holder holder;
    int flag = 0;

    public void onCreate() {
        super.onCreate();
        // mManager = (NotificationManager)
        // getSystemService(NOTIFICATION_SERVICE);
        running = true;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            // System.out.println("Get intent:" + intent);
            final String url = intent.getStringExtra("url");
            if (!TextUtils.isEmpty(url)) {
                final String path = intent.getStringExtra("path");
                String name = intent.getStringExtra("name");
                if (!TextUtils.isEmpty(name)) {
                    if (!name.endsWith(".apk")) {
                        name += ".apk";
                    }
                }
                final String fileName = name;
                startDownload(url);

                new Thread() {
                    private Exception e = null;

                    @Override
                    public void run() {
                        try {
                            loadFile(url, fileName, path, ++flag);
                        } catch (Exception e) {
                            this.e = e;
                        } finally {
                            if (e != null) {
                                String errorMsg = e.getMessage();
                                if (errorMsg != null
                                        && errorMsg.contains("timed out")) {
                                    errorMsg = "连接服务器超时";
                                }
                                sendMsg(-1, 0, url, fileName, 0L, path,
                                        errorMsg);
                            }
                        }
                    }
                }.start();
            }
        }
        return START_STICKY;
    }

    private void startDownload(String url) {
        long number = 100;
        downloadProgress.put(url, 0);
        DownLoadListener listener = downloadListener.get(url);
        if (listener != null) {
            listener.startDownload();
        }
    }

    @Override
    public void onDestroy() {
        downloadListener.clear();
        holder = null;
        running = false;
        super.onDestroy();
    }

    // 状态栏视图更新
    private void displayNotificationMessage(Notification notification,
                                            Holder data) {
        boolean remove = false;
        if (data.count >= 100) {
            remove = true;
        }
        setDownlogFinsh(notification, data, remove);
    }

    private void setDownlogFinsh(Notification notification, Holder data,
                                 boolean remove) {
        if (remove) {
            String key = data.url;
            downloadProgress.remove(key);
            DownLoadListener listener = downloadListener.get(key);
            if (listener != null) {
                listener.downloaded(Uri
                        .fromFile(new File(data.path, data.name)));
            }
            downloadListener.remove(key);
            if (downloadListener.size() <= 0) {
                stopSelf();
            }
        }
    }

    private void setDownlogFail(Holder data) {
        // NotificationManager manager = managers.get(url);
        String error = data.errorMsg;
        if (data != null) {
            String key = data.url;
            File file = new File(data.path, data.name);
            if (file.exists()) {
                file.delete();
            }
            downloadProgress.remove(key);
            DownLoadListener listener = downloadListener.get(key);
            if (listener != null) {
                listener.downloadFail();
            }
            downloadListener.remove(key);
            // managers.remove(url);
            if (downloadListener.size() <= 0) {
                stopSelf();
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    public void loadFile(String url, String name, String path, int flag)
            throws Exception {
        if (!StorageConfig.isExistSDCard) {
            throw new Exception("SD卡已卸载或不存在");
        }
        float progressStep = 0.0f;
        HttpClient client = new DefaultHttpClient();
        HttpGet get = new HttpGet(url);
        HttpParams params = new BasicHttpParams();
        params.setParameter(CoreConnectionPNames.SO_TIMEOUT, 30000);// 从socket读数据时发生阻塞的超时时间
        params.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 45000);// 连接的超时时间
        get.setParams(params);
        HttpResponse response;
        response = client.execute(get);
        HttpEntity entity = response.getEntity();
        long length = entity.getContentLength();
        StatFs stat = new StatFs(StorageConfig.getAvailableExternalStorage()
                .getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        if (length >= availableBlocks * blockSize) {
            throw new Exception("手机存储空间不足");
        }
        InputStream is = entity.getContent();
        FileOutputStream fileOutputStream = null;

        File file = null;
        if (is != null) {
            file = new File(path, name);
            if (!file.exists()) {
                file.createNewFile();
            }
            fileOutputStream = new FileOutputStream(file);
            byte[] buf = new byte[8192];
            int ch = -1;
            long count = 0;
            sendMsg(1, 0, url, name, length, path, "");
            // ch中存放从buf字节数组中读取到的字节个数
            while ((ch = is.read(buf)) != -1 && running) {
                fileOutputStream.write(buf, 0, ch);
                count += ch;
                sendMsg(1, count, url, name, length, path, "");
            }
            file.renameTo(new File(path, name));

            sendMsg(2, length, url, name, length, path, "");
            if (fileOutputStream != null) {
                fileOutputStream.flush();
                fileOutputStream.close();
            }
        }
    }

    private void sendMsg(int what, long c, String url, String name,
                         long appSize, String path, String errorMsg) {
        Message msg = new Message();
        msg.what = what;// 用来识别发送消息的类型
        msg.arg1 = 0;
        holder = new Holder();
        holder.count = c;
        holder.url = url;
        holder.name = name;
        holder.path = path;
        holder.apkSize = appSize;
        holder.errorMsg = errorMsg;
        msg.obj = holder;// 消息传递的自定义对象信息
        handler.sendMessage(msg);
    }

    // 定义一个Handler，用于处理下载线程与主线程间通讯
    private final MyHandler handler = new MyHandler(this);

    public void openfile(Uri url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(url, "application/vnd.android.package-archive");
        startActivity(intent);
    }

    public class Holder {
        String url;
        long count;
        String name;
        String path;
        long apkSize;
        String errorMsg;
    }

    public interface DownLoadListener {
        public void startDownload();

        public void downloading(long progress, long apkSize);

        public void downloaded(Uri uri);

        public void downloadFail();
    }

    private static class MyHandler extends Handler {
        private final WeakReference<DownloadService> mService;

        public MyHandler(DownloadService service) {
            mService = new WeakReference<DownloadService>(service);
        }

        @Override
        public void handleMessage(Message msg) {
            DownloadService service = mService.get();
            if (service != null) {
                final Holder data = (Holder) msg.obj;
                if (!Thread.currentThread().isInterrupted()) {
                    // 判断下载线程是否中断
                    DownLoadListener listener = downloadListener.get(data.url);
                    switch (msg.what) {
                        case 1:
                            if (listener != null) {
                                listener.downloading(data.count, data.apkSize);
                            }
                            break;
                        case 2:
                            String key = data.url;
                            downloadProgress.remove(key);
                            if (listener != null) {
                                listener.downloaded(Uri.fromFile(new File(data.path,
                                        data.name)));
                            }
                            downloadListener.remove(key);
                            if (downloadListener.size() <= 0) {
                                service.stopSelf();
                            }
                            break;

                        case -1:
                            service.setDownlogFail(data);
                            break;
                    }
                }
            }
            super.handleMessage(msg);
        }
    }

}
