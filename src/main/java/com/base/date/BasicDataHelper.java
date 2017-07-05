package com.base.date;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.base.bean.AreaInfoTable;
import com.base.bean.DrugInfoTable;
import com.base.bean.DrugstoreInfoTable;

public class BasicDataHelper extends SQLiteOpenHelper {
	private static final int REPORTEVENT_DATABASE_VERSION=5;
	private static BasicDataHelper sqlOpenDbHelperInstance;
	private static final String DATABASE_NAME = "IVSSDB";
	public static final String ID = "id";
	/**
	 * 药店资料
	 */
	private static final String CREATE_DRUGSTORE_TABLE = "CREATE TABLE "
			+ DrugstoreInfoTable.TABLE_NAME
			+ "( cid TEXT PRIMARY KEY NOT NULL , "
			+ DrugstoreInfoTable.DS_CODE2 + " TEXT ,"
			+ DrugstoreInfoTable.CNAME + " TEXT," + DrugstoreInfoTable.POINTX
			+ " TEXT," + DrugstoreInfoTable.POINTY + " TEXT,"
			+ DrugstoreInfoTable.ADDRESS + " TEXT," + DrugstoreInfoTable.LEVL
			+ " TEXT," + DrugstoreInfoTable.MONTH_AMT + " TEXT,"
			+ DrugstoreInfoTable.SALES + " TEXT,"
			+ DrugstoreInfoTable.SALE_DBZH + " TEXT,"
			+ DrugstoreInfoTable.DEST_FLAG + " TEXT,"
			+ DrugstoreInfoTable.FCREATETIME + " TEXT,"
			+ DrugstoreInfoTable.AREA_CODE + " TEXT,"
			+ DrugstoreInfoTable.USE_FLAG + " TEXT,"
			+ DrugstoreInfoTable.CITY_NAME + " TEXT,"
			+ DrugstoreInfoTable.PROVINCE_NAME + " TEXT" + ")";
	/**
	 * 药店资料
	 */
	private static final String CREATE_DRUGSTORE_TEMP_TABLE = "CREATE TABLE "
			+ DrugstoreInfoTable.TEMP_TABLE_NAME
			+ "( cid TEXT PRIMARY KEY NOT NULL , "
			+ DrugstoreInfoTable.DS_CODE2 + " TEXT ,"
			+ DrugstoreInfoTable.CNAME + " TEXT," + DrugstoreInfoTable.POINTX
			+ " TEXT," + DrugstoreInfoTable.POINTY + " TEXT,"
			+ DrugstoreInfoTable.ADDRESS + " TEXT," + DrugstoreInfoTable.LEVL
			+ " TEXT," + DrugstoreInfoTable.MONTH_AMT + " TEXT,"
			+ DrugstoreInfoTable.SALES + " TEXT,"
			+ DrugstoreInfoTable.SALE_DBZH + " TEXT,"
			+ DrugstoreInfoTable.DEST_FLAG + " TEXT,"
			+ DrugstoreInfoTable.FCREATETIME + " TEXT,"
			+ DrugstoreInfoTable.AREA_CODE + " TEXT,"
			+ DrugstoreInfoTable.USE_FLAG + " TEXT,"
			+ DrugstoreInfoTable.CITY_NAME + " TEXT,"
			+ DrugstoreInfoTable.PROVINCE_NAME + " TEXT" + ")";

	/**
	 * 产品资料
	 */
	private static final String CREATE_DRUG_TABLE = "CREATE TABLE "
			+ DrugInfoTable.TABLE_NAME + "(" + "cid"
			+ " integer PRIMARY KEY NOT NULL," + DrugInfoTable.CNAME + " TEXT,"
			+ DrugInfoTable.PRICE + " DOUBLE," + DrugInfoTable.DRUG_BCODE
			+ " TEXT," + DrugInfoTable.DRUG_CODE + " TEXT" + ")";
	/**
	 * 地区档案
	 */
	private static final String CREATE_AREA_TABLE = "CREATE TABLE "
			+ AreaInfoTable.TABLE_NAME + "(" + "cid"
			+ " integer PRIMARY KEY NOT NULL," + AreaInfoTable.AREA_CODE
			+ " TEXT," + AreaInfoTable.AREA_NAME + " TEXT,"
			+ AreaInfoTable.DQ_NAME + " TEXT," + AreaInfoTable.QUY_NAME
			+ " TEXT," + AreaInfoTable.CITY + " TEXT,"
			+ AreaInfoTable.CITY_FLAG + " TEXT," + AreaInfoTable.SQ_FLAG
			+ " TEXT," + AreaInfoTable.SQ_NAME + " TEXT" + ")";

	private static final String DROP_DRUG_TABLE = "DROP TABLE IF EXISTS "
			+ DrugInfoTable.TABLE_NAME;

	private static final String DROP_DRUGSTORE_TABLE = "DROP TABLE IF EXISTS "
			+ DrugstoreInfoTable.TABLE_NAME;
	private static final String DROP_DRUGSTORE_TEMP_TABLE = "DROP TABLE IF EXISTS "
			+ DrugstoreInfoTable.TEMP_TABLE_NAME;

	private static final String DROP_AREA_TABLE = "DROP TABLE IF EXISTS "
			+ AreaInfoTable.TABLE_NAME;

	public static BasicDataHelper SQLOpenDbHelperInstance(Context context) {
		if (sqlOpenDbHelperInstance == null) {
			return new BasicDataHelper(context);
		}
		return sqlOpenDbHelperInstance;
	}

	public BasicDataHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, null, version);
	}

	public BasicDataHelper(Context context) {
		this(context, DATABASE_NAME, null, REPORTEVENT_DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.e("Test", "资料表onCreate……");
		db.execSQL(CREATE_AREA_TABLE);
		db.execSQL(CREATE_DRUG_TABLE);
		db.execSQL(CREATE_DRUGSTORE_TABLE);
		db.execSQL(CREATE_DRUGSTORE_TEMP_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.e("Test", "资料表onUpgrade……"+oldVersion + ":-:" + newVersion);
		db.execSQL(DROP_AREA_TABLE);
		db.execSQL(DROP_DRUG_TABLE);
		db.execSQL(DROP_DRUGSTORE_TABLE);
		db.execSQL(DROP_DRUGSTORE_TEMP_TABLE);
		onCreate(db);
	}
}
