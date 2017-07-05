package com.base.dao;

import java.util.List;

import android.database.sqlite.SQLiteDatabase;

import com.api.bean.AreaInfo;
import com.api.bean.DrugInfo;
import com.api.bean.DrugstoreInfo;
import com.api.bean.DrugstoreInfoListAndMd5;

public interface BasicSQLiteDbDao {

	/**
	 * 打开Database;
	 * 
	 * @return
	 */
	public SQLiteDatabase openDatabase();

	public boolean addDrugInfo(DrugInfo info);

	public boolean isTableDataExist(String tableName);

	public boolean addDrugInfos(List<DrugInfo> infoList, boolean isDeleteTable);

	public DrugInfo getDrugInfoByBcode(String drug_bcode);

	public DrugInfo getDrugInfoByDrugCode(String drug_code);

	public boolean addDrugStoreInfo(DrugstoreInfo info);

	public boolean addDrugstoreInfos(String table,
			List<DrugstoreInfo> infoList, boolean isDeleteTable);

	public boolean copyData(String srcTable, String dstTable);

	public List<DrugstoreInfo> getDrugstoreInfos(double pointX, double pointY,
			double areaDir);

	public boolean addAreaInfo(AreaInfo info);

	public boolean addAreaInfos(List<AreaInfo> infoList);

	public DrugstoreInfoListAndMd5 getDrugstoreInfos2MD5(int pageSize,
			int pageNum);

	public String getDrugInfos2MD5(int pageSize, int pageNum);

}
