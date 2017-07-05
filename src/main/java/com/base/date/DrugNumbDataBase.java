package com.base.date;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.base.bean.DrugNumbInfoTable;

/**
 * 批号缓存表
 */
public class DrugNumbDataBase extends SQLiteOpenHelper {
    private static final int REPORTEVENT_DATABASE_VERSION=1;
    private static DrugNumbDataBase sqlOpenDbHelperInstance;
    private static final String DATABASE_NAME = "ivss2.db";
    /**
     * 入店信息保存
     */
    private static final String CREATE_INSTORE_TABLE = "CREATE TABLE "
            + DrugNumbInfoTable.TABLE_NAME + "(" + DrugNumbInfoTable.ID
            + " TEXT," + DrugNumbInfoTable.NUMBER + " TEXT,"
            + " CONSTRAINT pk_t2 PRIMARY KEY (" + DrugNumbInfoTable.ID + ")" + ")";

    private static final String DROP_DATA_TABLE = "DROP TABLE IF EXISTS "
            + DrugNumbInfoTable.TABLE_NAME;

    public static DrugNumbDataBase SQLOpenDbHelperInstance(Context context) {
        if (sqlOpenDbHelperInstance == null) {
            return new DrugNumbDataBase(context);
        }
        return sqlOpenDbHelperInstance;
    }

    public DrugNumbDataBase(Context context, String name, CursorFactory factory,
                            int version) {
        super(context, name, null, version);
    }

    public DrugNumbDataBase(Context context) {
        this(context, DATABASE_NAME, null, REPORTEVENT_DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.e("Test","批号表onCreate……");
        db.execSQL(CREATE_INSTORE_TABLE);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.e("Test","批号表onUpgrade……"+oldVersion + ":-:" + newVersion);
        db.execSQL(DROP_DATA_TABLE);

        onCreate(db);
    }


}
