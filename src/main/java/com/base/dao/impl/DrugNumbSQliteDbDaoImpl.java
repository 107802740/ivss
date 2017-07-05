package com.base.dao.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import com.api.bean.DrugNumbInfo;
import com.base.bean.DrugNumbInfoTable;
import com.base.dao.DrugNumbSQLiteDbDao;
import com.base.date.DrugNumbDataBase;

import java.util.List;

public class DrugNumbSQliteDbDaoImpl implements DrugNumbSQLiteDbDao {
    private static Object synObj = new Object();
    private int mOpenCounter;

    private static DrugNumbSQliteDbDaoImpl instance;
    private static SQLiteOpenHelper mDatabaseHelper;
    private SQLiteDatabase mDatabase;

    private DrugNumbSQliteDbDaoImpl() {
    }

    public static synchronized void initializeInstance(Context context) {
        if (instance == null) {
            instance = new DrugNumbSQliteDbDaoImpl();
            mDatabaseHelper = new DrugNumbDataBase(context);
        }
    }

    public static synchronized DrugNumbSQliteDbDaoImpl getInstance(Context context) {
        if (instance == null) {
            initializeInstance(context);
        }

        return instance;
    }

    public synchronized SQLiteDatabase openDatabase() {
        synchronized (synObj) {
            mOpenCounter++;
            if (mOpenCounter == 1) {
                // Opening new database
                mDatabase = mDatabaseHelper.getReadableDatabase();
            }
            return mDatabase;
        }
    }

    public synchronized void closeDatabase() {
        synchronized (synObj) {
            mOpenCounter--;
            if (mOpenCounter == 0) {
                // Closing database
                mDatabase.close();
            }
        }
    }


    @Override
    public boolean addDrugNumbInfos(List<DrugNumbInfo> infoList, boolean isDeleteTable) {
        synchronized (synObj) {
            if (infoList == null) {
                return false;
            }
            SQLiteDatabase db = null;
            boolean result = false;
            try {
                db = openDatabase();

                db.beginTransaction();
                if (isDeleteTable) {
                    db.delete(DrugNumbInfoTable.TABLE_NAME, null, null);
                }
                for (DrugNumbInfo info : infoList) {
                    if (info == null) {
                        continue;
                    }
                    ContentValues values = new ContentValues();
                    values.put(DrugNumbInfoTable.ID, info.getId());
                    values.put(DrugNumbInfoTable.NUMBER, info.getDrugNumb());
                    db.replace(DrugNumbInfoTable.TABLE_NAME, null, values);
                }
                db.setTransactionSuccessful();
                result = true;
            } finally {
                if (db != null) {
                    db.endTransaction();
                    closeDatabase();
                }

            }
            return result;
        }
    }

    @Override
    public boolean validationDrugNumb(String drub_numb) {
        synchronized (synObj) {
            SQLiteDatabase db = null;
            Cursor cursor = null;
            boolean flag = false;
            if (TextUtils.isEmpty(drub_numb)) {
                return flag;
            }
            try {
                db = openDatabase();
                String sql = "select * from " + DrugNumbInfoTable.TABLE_NAME + " where " + DrugNumbInfoTable.NUMBER + "  = ? COLLATE NOCASE;";
                cursor = db.rawQuery(sql,new String[]{drub_numb});
                if (cursor != null && cursor.getCount() > 0) {
//                    cursor.moveToFirst();
//                    info = new DrugInfo();
//                    info.setPrice(cursor.getString(cursor
//                            .getColumnIndex(DrugInfoTable.PRICE)));
//                    info.setCname(cursor.getString(cursor
//                            .getColumnIndex(DrugInfoTable.CNAME)));
//                    info.setDrug_code(cursor.getString(cursor
//                            .getColumnIndex(DrugInfoTable.DRUG_CODE)));
                    flag = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
                if (db != null) {
                    // db.close();
                    closeDatabase();
                }

            }
            return flag;
        }
    }


}
