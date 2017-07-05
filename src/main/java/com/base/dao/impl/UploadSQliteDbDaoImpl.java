package com.base.dao.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.api.bean.DisplayInfo;
import com.api.bean.InstoreInfo;
import com.base.bean.DisplayInfoTable;
import com.base.bean.InstoreInfoTable;
import com.base.dao.UploadSQLiteDbDao;
import com.base.date.UploadDataBase;

import java.util.ArrayList;
import java.util.List;

public class UploadSQliteDbDaoImpl implements UploadSQLiteDbDao {
    private static Object synObj = new Object();

    private int mOpenCounter = 0;

    private static UploadSQliteDbDaoImpl instance;
    private static UploadDataBase mDatabaseHelper;
    private SQLiteDatabase mDatabase;

    private UploadSQliteDbDaoImpl() {
    }

    public static synchronized void initializeInstance(Context context) {
        if (instance == null) {
            instance = new UploadSQliteDbDaoImpl();
            mDatabaseHelper = UploadDataBase.SQLOpenDbHelperInstance(context.getApplicationContext());
        }
    }

    public static synchronized UploadSQliteDbDaoImpl getInstance(Context context) {
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
    public boolean addInstoreInfo(InstoreInfo info) {
        synchronized (synObj) {
            if (info == null) {
                return false;
            }
            SQLiteDatabase db = null;
            boolean result = false;
            try {
                db = openDatabase();

                ContentValues values = new ContentValues();
                values.put(InstoreInfoTable.DS_CODE2, info.getDs_code2());
                values.put(InstoreInfoTable.DS_NAME, info.getDs_name());
                values.put(InstoreInfoTable.FLOGINID, info.getFloginId());
                values.put(InstoreInfoTable.IN_DATE, info.getIn_date());
                values.put(InstoreInfoTable.IMG_NAME, info.getImg_name());
                values.put(InstoreInfoTable.IMG_PATH, info.getImg_path());
                values.put(InstoreInfoTable.AREA_CODE, info.getArea_code());
                values.put(InstoreInfoTable.DS_UPLOAD, info.getDs_upload());
                values.put(InstoreInfoTable.DS_UPLOAD_TAG,
                        info.isDs_upload_tag());
                long row = db
                        .replace(InstoreInfoTable.TABLE_NAME, null, values);
                if (row > 0) {
                    result = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (db != null) {
                    closeDatabase();
                }
            }
            return result;
        }
    }

    @Override
    public List<InstoreInfo> getInstoreInfo(String floginId) {
        synchronized (synObj) {
            SQLiteDatabase db = null;
            Cursor cursor = null;
            List<InstoreInfo> infos = null;
            try {

                db = openDatabase();
                cursor = db.query(InstoreInfoTable.TABLE_NAME, null,
                        InstoreInfoTable.FLOGINID + " = ? and "
                                + InstoreInfoTable.DS_UPLOAD_TAG + " = ? ",
                        new String[]{floginId, String.valueOf(false)}, null,
                        null, null);
                if (cursor != null) {
                    cursor.moveToFirst();
                    infos = new ArrayList<InstoreInfo>();
                    InstoreInfo info = null;
                    while (!cursor.isAfterLast()) {
                        info = new InstoreInfo();
                        info.setArea_code(cursor.getString(cursor
                                .getColumnIndex(InstoreInfoTable.DS_CODE2)));
                        info.setDs_code2(cursor.getString(cursor
                                .getColumnIndex(InstoreInfoTable.DS_CODE2)));
                        info.setDs_name(cursor.getString(cursor
                                .getColumnIndex(InstoreInfoTable.DS_NAME)));
                        info.setDs_upload(cursor.getString(cursor
                                .getColumnIndex(InstoreInfoTable.DS_UPLOAD)));
                        info.setDs_upload_tag(cursor.getString(cursor
                                .getColumnIndex(InstoreInfoTable.DS_UPLOAD_TAG)));
                        info.setFloginId(cursor.getString(cursor
                                .getColumnIndex(InstoreInfoTable.FLOGINID)));
                        info.setImg_name(cursor.getString(cursor
                                .getColumnIndex(InstoreInfoTable.IMG_NAME)));
                        info.setImg_path(cursor.getString(cursor
                                .getColumnIndex(InstoreInfoTable.IMG_PATH)));
                        info.setIn_date(cursor.getString(cursor
                                .getColumnIndex(InstoreInfoTable.IN_DATE)));

                        infos.add(info);
                        cursor.moveToNext();
                    }
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
                if (db != null) {
                    // db.close();
                    closeDatabase();
                }
            }
            return infos;
        }
    }

    @Override
    public boolean addDisplayInfo(DisplayInfo info) {
        synchronized (synObj) {
            if (info == null) {
                return false;
            }
            SQLiteDatabase db = null;
            boolean result = false;
            try {
                db = openDatabase();

                ContentValues values = new ContentValues();
                values.put(DisplayInfoTable.CREATETIME, info.getCreateTime());
                values.put(DisplayInfoTable.DISP_POSI, info.getDisp_posi());
                values.put(DisplayInfoTable.DISP_SURF, info.getDisp_surf());
                values.put(DisplayInfoTable.DRUG_BCODE, info.getDrug_bcode());
                values.put(DisplayInfoTable.DRUG_CODE, info.getDrug_code());
                values.put(DisplayInfoTable.DRUG_NAME, info.getDrug_name());
                values.put(DisplayInfoTable.DRUG_NUMB, info.getDrug_numb());
                values.put(DisplayInfoTable.DRUG_PRICE, info.getDrug_price());
                values.put(DisplayInfoTable.DS_CODE, info.getDs_code());
                values.put(DisplayInfoTable.DS_NAME, info.getDs_name());
                values.put(DisplayInfoTable.IMG_NAME, info.getImg_name());
                values.put(DisplayInfoTable.IMG_PATH, info.getImg_path());
                values.put(DisplayInfoTable.LOGINID, info.getLoginId());
                values.put(DisplayInfoTable.STORE_NUM, info.getStore_num());
                values.put(DisplayInfoTable.MONTHLY_SALES, info.getMonthly_sales());
                values.put(DisplayInfoTable.SEQ_NUMB, info.getSeq_numb());
                values.put(DisplayInfoTable.UPLOADTAG, info.getUploadTag());
                values.put(DisplayInfoTable.UPLOADTIME, info.getUploadtime());

                long row = db
                        .replace(DisplayInfoTable.TABLE_NAME, null, values);
                if (row > 0) {
                    result = true;
                }
            } finally {
                if (db != null) {
                    closeDatabase();
                }
            }
            return result;
        }
    }

    @Override
    public List<DisplayInfo> getDisplayInfos(String floginId) {
        synchronized (synObj) {
            SQLiteDatabase db = null;
            Cursor cursor = null;
            List<DisplayInfo> infos = null;
            try {

                db = openDatabase();
                cursor = db.query(DisplayInfoTable.TABLE_NAME, null,
                        DisplayInfoTable.LOGINID + " = ? and "
                                + DisplayInfoTable.UPLOADTAG + " = ? ",
                        new String[]{floginId, String.valueOf(false)}, null,
                        null, null);
                if (cursor != null) {
                    cursor.moveToFirst();
                    infos = new ArrayList<DisplayInfo>();
                    DisplayInfo info = null;
                    while (!cursor.isAfterLast()) {
                        info = new DisplayInfo();
                        info.setCreateTime(cursor.getString(cursor
                                .getColumnIndex(DisplayInfoTable.CREATETIME)));
                        info.setDisp_posi(cursor.getString(cursor
                                .getColumnIndex(DisplayInfoTable.DISP_POSI)));
                        info.setDisp_surf(cursor.getString(cursor
                                .getColumnIndex(DisplayInfoTable.DISP_SURF)));
                        info.setDrug_bcode(cursor.getString(cursor
                                .getColumnIndex(DisplayInfoTable.DRUG_BCODE)));
                        info.setDrug_code(cursor.getString(cursor
                                .getColumnIndex(DisplayInfoTable.DRUG_CODE)));
                        info.setDrug_name(cursor.getString(cursor
                                .getColumnIndex(DisplayInfoTable.DRUG_NAME)));
                        info.setDrug_numb(cursor.getString(cursor
                                .getColumnIndex(DisplayInfoTable.DRUG_NUMB)));
                        info.setDrug_price(cursor.getString(cursor
                                .getColumnIndex(DisplayInfoTable.DRUG_PRICE)));
                        info.setDs_code(cursor.getString(cursor
                                .getColumnIndex(DisplayInfoTable.DS_CODE)));
                        info.setDs_name(cursor.getString(cursor
                                .getColumnIndex(DisplayInfoTable.DS_NAME)));
                        info.setImg_name(cursor.getString(cursor
                                .getColumnIndex(DisplayInfoTable.IMG_NAME)));
                        info.setImg_path(cursor.getString(cursor
                                .getColumnIndex(DisplayInfoTable.IMG_PATH)));
                        info.setLoginId(cursor.getString(cursor
                                .getColumnIndex(DisplayInfoTable.LOGINID)));
                        info.setStore_num(cursor.getString(cursor
                                .getColumnIndex(DisplayInfoTable.STORE_NUM)));
                        info.setSeq_numb(cursor.getString(cursor
                                .getColumnIndex(DisplayInfoTable.SEQ_NUMB)));
                        info.setUploadTag(cursor.getString(cursor
                                .getColumnIndex(DisplayInfoTable.UPLOADTAG)));
                        info.setUploadtime(cursor.getString(cursor
                                .getColumnIndex(DisplayInfoTable.UPLOADTIME)));
                        info.setMonthly_sales(cursor.getString(cursor
                                .getColumnIndex(DisplayInfoTable.MONTHLY_SALES)));

                        infos.add(info);
                        cursor.moveToNext();
                    }
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
                if (db != null) {
                    // db.close();
                    closeDatabase();
                }
            }
            return infos;
        }
    }
}
