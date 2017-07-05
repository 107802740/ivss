package com.base.date;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;

import com.base.bean.DisplayInfoTable;
import com.base.bean.InstoreInfoTable;

public class UploadDataBase extends SDSQLiteOpenHelper {
    private static final int REPORTEVENT_DATABASE_VERSION = 5;
    private static UploadDataBase sqlOpenDbHelperInstance;
    private static final String DATABASE_NAME = "IVSSDB";

    public static final String ID = "id";
    /**
     * 入店信息保存
     */
    private static final String CREATE_INSTORE_TABLE = "CREATE TABLE "
            + InstoreInfoTable.TABLE_NAME + "(" + InstoreInfoTable.DS_CODE2
            + " TEXT," + InstoreInfoTable.DS_NAME + " TEXT,"
            + InstoreInfoTable.FLOGINID + " TEXT," + InstoreInfoTable.IN_DATE
            + " TEXT," + InstoreInfoTable.IMG_NAME + " TEXT,"
            + InstoreInfoTable.IMG_PATH + " TEXT," + InstoreInfoTable.AREA_CODE
            + " TEXT," + InstoreInfoTable.DS_UPLOAD + " TEXT,"
            + InstoreInfoTable.DS_UPLOAD_TAG + " BOOLEAN ,"
            + " CONSTRAINT pk_t2 PRIMARY KEY (" + InstoreInfoTable.FLOGINID
            + "," + InstoreInfoTable.IN_DATE + ")" + ")";
    /**
     * 采集数据保存
     */
    private static final String CREATE_DATA_TABLE = "CREATE TABLE "
            + DisplayInfoTable.TABLE_NAME + "(" + DisplayInfoTable.DS_CODE
            + " TEXT," + DisplayInfoTable.DS_NAME + " TEXT,"
            + DisplayInfoTable.DRUG_CODE + " TEXT,"
            + DisplayInfoTable.DRUG_NUMB + " TEXT,"
            + DisplayInfoTable.DRUG_BCODE + " TEXT,"
            + DisplayInfoTable.DRUG_NAME + " TEXT,"
            + DisplayInfoTable.DISP_SURF + " TEXT,"
            + DisplayInfoTable.DRUG_PRICE + " TEXT,"
            + DisplayInfoTable.DISP_POSI + " TEXT,"
            + DisplayInfoTable.STORE_NUM + " TEXT,"
            + DisplayInfoTable.MONTHLY_SALES + " TEXT,"
            + DisplayInfoTable.SEQ_NUMB + " TEXT," + DisplayInfoTable.LOGINID
            + " TEXT," + DisplayInfoTable.CREATETIME + " TEXT,"
            + DisplayInfoTable.IMG_NAME + " TEXT," + DisplayInfoTable.IMG_PATH
            + " TEXT," + DisplayInfoTable.UPLOADTIME + " TEXT,"
            + DisplayInfoTable.UPLOADTAG + " BOOLEAN ,"
            + " CONSTRAINT pk_t2 PRIMARY KEY (" + DisplayInfoTable.DRUG_BCODE
            + "," + DisplayInfoTable.CREATETIME + ")" + ")";
    /**
     * 数据保存临时表
     */
    private static final String CREATE_DATA_TEMP_TABLE = "CREATE TABLE "
            + DisplayInfoTable.TABLE_NAME_TEMP + "(" + DisplayInfoTable.DS_CODE
            + " TEXT," + DisplayInfoTable.DS_NAME + " TEXT,"
            + DisplayInfoTable.DRUG_CODE + " TEXT,"
            + DisplayInfoTable.DRUG_NUMB + " TEXT,"
            + DisplayInfoTable.DRUG_BCODE + " TEXT,"
            + DisplayInfoTable.DRUG_NAME + " TEXT,"
            + DisplayInfoTable.DISP_SURF + " TEXT,"
            + DisplayInfoTable.DRUG_PRICE + " TEXT,"
            + DisplayInfoTable.DISP_POSI + " TEXT,"
            + DisplayInfoTable.STORE_NUM + " TEXT,"
            + DisplayInfoTable.MONTHLY_SALES + " TEXT,"
            + DisplayInfoTable.SEQ_NUMB + " TEXT," + DisplayInfoTable.LOGINID
            + " TEXT," + DisplayInfoTable.CREATETIME + " TEXT,"
            + DisplayInfoTable.IMG_NAME + " TEXT," + DisplayInfoTable.IMG_PATH
            + " TEXT," + DisplayInfoTable.UPLOADTIME + " TEXT,"
            + DisplayInfoTable.UPLOADTAG + " BOOLEAN ,"
            + " CONSTRAINT pk_t2 PRIMARY KEY (" + DisplayInfoTable.DRUG_BCODE
            + "," + DisplayInfoTable.CREATETIME + ")" + ")";

    private static final String DROP_DATA_TABLE = "DROP TABLE IF EXISTS "
            + DisplayInfoTable.TABLE_NAME;
    private static final String DROP_DATA_TEMP_TABLE = "DROP TABLE IF EXISTS "
            + DisplayInfoTable.TABLE_NAME_TEMP;

    private static final String DROP_INSTORE_TABLE = "DROP TABLE IF EXISTS "
            + InstoreInfoTable.TABLE_NAME;

    public static UploadDataBase SQLOpenDbHelperInstance(Context context) {
        if (sqlOpenDbHelperInstance == null) {
            sqlOpenDbHelperInstance= new UploadDataBase(context.getApplicationContext());
        }
        return sqlOpenDbHelperInstance;
    }

    public UploadDataBase(Context context, String name, CursorFactory factory,
                          int version) {
        super(context, name, null, version);
    }

    public UploadDataBase(Context context) {
        this(context, DATABASE_NAME, null, REPORTEVENT_DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.e("Test", "onCreate>>>>>>>>>>table");
        db.execSQL(CREATE_DATA_TABLE);
        db.execSQL(CREATE_INSTORE_TABLE);
    }

    private boolean isReCreateDB = false;

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.e("Test", "onUpgrade>>>>>>>>>>table:" + oldVersion + ":-:" + newVersion);
        if (oldVersion < 5 && newVersion == 5) {
            if (!checkColumnExist1(db, DisplayInfoTable.TABLE_NAME, DisplayInfoTable.MONTHLY_SALES)) {
                //创建临时表，备份数据
                try {
                    db.beginTransaction();
                    db.execSQL(DROP_DATA_TEMP_TABLE);
                    db.execSQL(CREATE_DATA_TEMP_TABLE);
                    copyData(db);
                    //删除原表，创建新表，copy数据，删除临时表
                    db.execSQL(DROP_DATA_TABLE);
                    db.execSQL(CREATE_DATA_TABLE);
                    copyData(db, DisplayInfoTable.TABLE_NAME_TEMP, DisplayInfoTable.TABLE_NAME);
                    db.execSQL(DROP_DATA_TEMP_TABLE);
                    db.setTransactionSuccessful();
                } finally {
                    if (db != null) {
                        db.endTransaction();
                    }

                }

            }
        } else {
            db.execSQL(DROP_DATA_TABLE);
            db.execSQL(DROP_INSTORE_TABLE);
            onCreate(db);

        }
    }

    public void copyData(SQLiteDatabase db) {


        String sql = "insert into " + DisplayInfoTable.TABLE_NAME_TEMP + "(" + DisplayInfoTable.DS_CODE
                + " ," + DisplayInfoTable.DS_NAME + " ,"
                + DisplayInfoTable.DRUG_CODE + " ,"
                + DisplayInfoTable.DRUG_NUMB + " ,"
                + DisplayInfoTable.DRUG_BCODE + " ,"
                + DisplayInfoTable.DRUG_NAME + " ,"
                + DisplayInfoTable.DISP_SURF + " ,"
                + DisplayInfoTable.DRUG_PRICE + " ,"
                + DisplayInfoTable.DISP_POSI + " ,"
                + DisplayInfoTable.STORE_NUM + " ,"
                + DisplayInfoTable.SEQ_NUMB + " ," + DisplayInfoTable.LOGINID
                + " ," + DisplayInfoTable.CREATETIME + " ,"
                + DisplayInfoTable.IMG_NAME + " ," + DisplayInfoTable.IMG_PATH
                + " ," + DisplayInfoTable.UPLOADTIME + " ,"
                + DisplayInfoTable.UPLOADTAG + ")" + " select * from "
                + DisplayInfoTable.TABLE_NAME + ";";
        db.execSQL(sql);
    }

    public void copyData(SQLiteDatabase db, String srcTable, String dstTable) {


        String sql = "insert into " + dstTable + " select * from "
                + srcTable + ";";
        db.execSQL(sql);
    }


    /**
     * 检查某表列是否存在
     *
     * @param db
     * @param tableName  表名
     * @param columnName 列名
     * @return
     */
    private boolean checkColumnExist1(SQLiteDatabase db, String tableName
            , String columnName) {
        boolean result = false;
        Cursor cursor = null;
        try {
            //查询一行
            cursor = db.rawQuery("SELECT * FROM " + tableName + " LIMIT 0"
                    , null);
            result = cursor != null && cursor.getColumnIndex(columnName) != -1;
        } catch (Exception e) {
            Log.e("Test", "checkColumnExists1..." + e.getMessage());
        } finally {
            if (null != cursor && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return result;
    }
}
