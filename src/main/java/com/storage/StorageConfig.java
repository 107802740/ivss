package com.storage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;
import android.os.storage.StorageManager;

import java.io.File;
import java.io.IOException;

public final class StorageConfig {
	public static boolean isExistSDCard = false;
	private static File mRootDir = null;

	private static String TAG = "StorageConfig";

	@SuppressLint("NewApi")
	static public boolean init(Context context) {

		String path = null;
		String state = null;
		int currentapiVersion = android.os.Build.VERSION.SDK_INT;
		do {
			if (currentapiVersion >= 14
					&& currentapiVersion < 19) {
				StorageManager storageMan = (StorageManager) context
						.getSystemService(Context.STORAGE_SERVICE);
				if (storageMan == null) {
					break;
				}
				Object[] r = (Object[]) ReflectionHelper.invokeMethod(
						storageMan, "getVolumeList", null);

				int count = r.length;
				for (int i = 0; i < count; i++) {
					StorageVolumeWrapper storageVolumeWrapper = new StorageVolumeWrapper(
							r[i]);
					if (storageVolumeWrapper != null) {
						path = storageVolumeWrapper.getPath();
						Object[] arg = new Object[] { path };
						state = (String) ReflectionHelper.invokeMethod(
								storageMan, "getVolumeState", arg);
						if (path != null && state != null
								&& state.equals(Environment.MEDIA_MOUNTED)) {
							mRootDir = new File(path);
							boolean flag = mRootDir.canWrite();
							if (flag) {
								isExistSDCard = true;
							}
							if (storageVolumeWrapper.isRemovable() && flag) {
								// 第一个外置SDCard
								return true;
							} else {
								// 最后一个内置
								continue;
							}
						}
					}
				}

			}
		} while (false);
		if (mRootDir == null) {
			if (android.os.Environment.getExternalStorageState().equals(
					android.os.Environment.MEDIA_MOUNTED)) {
				isExistSDCard = true;
			} else {
				isExistSDCard = false;
			}

			mRootDir = Environment.getExternalStorageDirectory();
		}
		return true;
	}

	static public File getAvailableExternalStorage() {
		return mRootDir;
	}

	static public File getAppExternalStorage() {
		return new File(mRootDir, "IVSS");
	}

	static public File getUploadeDir() {
		File dir = new File(getAppExternalStorage(), "updload");
		if (!dir.exists()) {
			dir.mkdirs();
		}
		createNomediaFile(dir);
		return dir;
	}

	static public File getDataDir() {
		File dir = new File(getAppExternalStorage(), "data");
		if (!dir.exists()) {
			dir.mkdirs();
		}
		createNomediaFile(dir);
		return dir;
	}

	/**
	 * file目录
	 * 
	 * @return
	 */
	static public File getDisplayDir() {
		File dir = new File(getDataDir(), "displayPic");
		if (!dir.exists()) {
			dir.mkdirs();
		}
		createNomediaFile(dir);
		return dir;
	}
	/**
	 * file目录
	 * 
	 * @return
	 */
	static public File getInStoreImageDir() {
		File dir = new File(getDataDir(), "instorePic");
		if (!dir.exists()) {
			dir.mkdirs();
		}
		createNomediaFile(dir);
		return dir;
	}

	/**
	 * file目录
	 * 
	 * @return
	 */
	static public File getDataBaseDir() {
		File dir = new File(getDataDir(), "db");
		if (!dir.exists()) {
			dir.mkdirs();
		}
		createNomediaFile(dir);
		return dir;
	}

	private static void createNomediaFile(File dir) {
		File file = new File(dir, ".nomedia");
		try {
			if (!file.exists()) {
				file.createNewFile();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
