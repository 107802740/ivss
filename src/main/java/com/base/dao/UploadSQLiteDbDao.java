package com.base.dao;

import java.util.List;

import android.database.sqlite.SQLiteDatabase;

import com.api.bean.DisplayInfo;
import com.api.bean.InstoreInfo;

public interface UploadSQLiteDbDao {

	/**
	 * 打开Database;
	 * 
	 * @return
	 */
	public SQLiteDatabase openDatabase();

	public boolean addInstoreInfo(InstoreInfo info);

	public List<InstoreInfo> getInstoreInfo(String floginId);

	public boolean addDisplayInfo(DisplayInfo info);

	public List<DisplayInfo> getDisplayInfos(String floginId);

}
