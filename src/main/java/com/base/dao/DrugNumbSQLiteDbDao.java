package com.base.dao;

import android.database.sqlite.SQLiteDatabase;

import com.api.bean.DrugNumbInfo;

import java.util.List;

public interface DrugNumbSQLiteDbDao {

	/**
	 * 打开Database;
	 * 
	 * @return
	 */
	public SQLiteDatabase openDatabase();



	public boolean addDrugNumbInfos(List<DrugNumbInfo> infoList, boolean isDeleteTable);

	public boolean validationDrugNumb(String drug_numb);



}
