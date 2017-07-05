package com.zmap.datagather.activity1;

import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.util.Log;

import com.api.bean.User;
import com.baidu.mapapi.SDKInitializer;
import com.base.BaseActivity;
import com.base.service.DownloadService;
import com.storage.StorageConfig;

public class MyApplication extends Application {
	public static User sUser;
	public static String visitor;
	public static MyApplication instance = null;
	private List<Activity> activityList = new LinkedList();

	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
		SDKInitializer.initialize(this);
		StorageConfig.init(getApplicationContext());
	}

	public void addActivity(Activity paramActivity) {
		this.activityList.add(paramActivity);
	}

	public void remove(Activity paramActivity) {
		this.activityList.remove(paramActivity);
		if (this.activityList.size() <= 0) {
			quit(paramActivity);
		}
	}

	private void quit(Activity activity) {
		Log.d("Tast", "quit app>>>>>>>");
//		sUser = null;
		visitor = null;
		((BaseActivity) activity).setCookieValue("upgrade", "");
		stopService(new Intent(activity, DownloadService.class));
	}

	// /**
	// * 获取应用的主要运行线程池(主要供Activiy处理后台业务逻辑，如AsyncFramework)
	// *
	// * @return
	// */
	// public synchronized Executor getMajorExecutor() {
	// if (mMajorExecutor == null) {
	// mMajorExecutor = Executors.newFixedThreadPool(1);
	// }
	// return mMajorExecutor;
	// }
	//
	// /**
	// * 获取额外的运行线程池(主要供Activity动态下载图片等资源，避免阻塞主要运行线程)
	// *
	// * @return
	// */
	// public synchronized Executor getExtraExecutor() {
	// if (mExtraExecutor == null) {
	// mExtraExecutor = Executors.newFixedThreadPool(3);
	// }
	// return mExtraExecutor;
	// }
	//
	// /**
	// * 获取空闲任务类的运行线程池（主要用于运行时间不敏感的运行时间可能较长的如清理无用缓存文件、流量上报等任务）
	// *
	// * @return
	// */
	// public synchronized Executor getIdleExecutor() {
	// if (mIdleExcutor == null) {
	// mIdleExcutor = Executors.newFixedThreadPool(1);
	// }
	// return mIdleExcutor;
	// }
	//
	// private Executor mMajorExecutor;
	// private Executor mExtraExecutor;
	// private Executor mIdleExcutor;
}