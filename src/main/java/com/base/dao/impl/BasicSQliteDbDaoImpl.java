package com.base.dao.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import com.api.bean.AreaInfo;
import com.api.bean.DrugInfo;
import com.api.bean.DrugstoreInfo;
import com.api.bean.DrugstoreInfoListAndMd5;
import com.base.bean.AreaInfoTable;
import com.base.bean.DrugInfoTable;
import com.base.bean.DrugstoreInfoTable;
import com.base.dao.BasicSQLiteDbDao;
import com.base.date.BasicDataHelper;
import com.base.util.AndroidUtil;
import com.base.util.Distance;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

public class BasicSQliteDbDaoImpl implements BasicSQLiteDbDao {
	private static Object synObj = new Object();
	private int mOpenCounter;

	private static BasicSQliteDbDaoImpl instance;
	private static SQLiteOpenHelper mDatabaseHelper;
	private SQLiteDatabase mDatabase;

	private BasicSQliteDbDaoImpl() {
	}

	public static synchronized void initializeInstance(Context context) {
		if (instance == null) {
			instance = new BasicSQliteDbDaoImpl();
			mDatabaseHelper = new BasicDataHelper(context);
		}
	}

	public static synchronized BasicSQliteDbDaoImpl getInstance(Context context) {
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
	public boolean addDrugInfo(DrugInfo info) {
		synchronized (synObj) {
			if (info == null) {
				return false;
			}
			SQLiteDatabase db = null;
			boolean result = false;
			try {
				db = openDatabase();
				db.delete(DrugInfoTable.TABLE_NAME, DrugInfoTable.DRUG_BCODE
						+ " = ? ", new String[] { info.getDrug_bcode() });
				ContentValues values = new ContentValues();
				values.put(DrugInfoTable.CNAME, info.getCname());
				values.put(DrugInfoTable.DRUG_BCODE, info.getDrug_bcode());
				values.put(DrugInfoTable.DRUG_CODE, info.getDrug_code());
				values.put(DrugInfoTable.PRICE, info.getPrice());
				long row = db.replace(DrugInfoTable.TABLE_NAME, null, values);
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
	public boolean addDrugInfos(List<DrugInfo> infoList, boolean isDeleteTable) {
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
					db.delete(DrugInfoTable.TABLE_NAME, null, null);
				}
				for (DrugInfo info : infoList) {
					if (info == null) {
						continue;
					}
					ContentValues values = new ContentValues();
					values.put(DrugInfoTable.CNAME, info.getCname());
					values.put(DrugInfoTable.DRUG_BCODE, info.getDrug_bcode());
					values.put(DrugInfoTable.DRUG_CODE, info.getDrug_code());
					values.put(DrugInfoTable.PRICE, info.getPrice());
					db.replace(DrugInfoTable.TABLE_NAME, null, values);
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
	public DrugInfo getDrugInfoByBcode(String drug_bcode) {
		synchronized (synObj) {
			SQLiteDatabase db = null;
			Cursor cursor = null;
			DrugInfo info = null;
			try {
				db = openDatabase();
				cursor = db.query(DrugInfoTable.TABLE_NAME, null,
						DrugInfoTable.DRUG_BCODE + " = ?",
						new String[] { drug_bcode }, null, null, null);
				if (cursor != null && cursor.getCount() > 0) {
					cursor.moveToFirst();
					info = new DrugInfo();
					info.setPrice(cursor.getString(cursor
							.getColumnIndex(DrugInfoTable.PRICE)));
					info.setCname(cursor.getString(cursor
							.getColumnIndex(DrugInfoTable.CNAME)));
					info.setDrug_code(cursor.getString(cursor
							.getColumnIndex(DrugInfoTable.DRUG_CODE)));
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
			return info;
		}
	}

	@Override
	public DrugInfo getDrugInfoByDrugCode(String drug_code) {
		synchronized (synObj) {
			SQLiteDatabase db = null;
			Cursor cursor = null;
			DrugInfo info = null;
			try {
				db = openDatabase();
				cursor = db.query(DrugInfoTable.TABLE_NAME, null,
						DrugInfoTable.DRUG_CODE + " = ?",
						new String[] { drug_code }, null, null, null);
				if (cursor != null && cursor.getCount() > 0) {
					cursor.moveToFirst();
					info = new DrugInfo();
					info.setPrice(cursor.getString(cursor
							.getColumnIndex(DrugInfoTable.PRICE)));
					info.setCname(cursor.getString(cursor
							.getColumnIndex(DrugInfoTable.CNAME)));
					info.setDrug_code(cursor.getString(cursor
							.getColumnIndex(DrugInfoTable.DRUG_CODE)));
					info.setDrug_bcode(cursor.getString(cursor
							.getColumnIndex(DrugInfoTable.DRUG_BCODE)));
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
			return info;
		}
	}

	@Override
	public boolean addDrugStoreInfo(DrugstoreInfo info) {
		synchronized (synObj) {
			if (info == null) {
				return false;
			}
			SQLiteDatabase db = null;
			boolean result = false;
			try {
				db = openDatabase();
				ContentValues values = new ContentValues();
				values.put(DrugstoreInfoTable.CNAME, info.getCname());
				values.put("cid", info.getCid());
				values.put(DrugstoreInfoTable.DS_CODE2, info.getDs_code2());
				values.put(DrugstoreInfoTable.POINTX,
						Double.parseDouble(info.getPointx()));
				values.put(DrugstoreInfoTable.POINTY,
						Double.parseDouble(info.getPointy()));
				values.put(DrugstoreInfoTable.ADDRESS, info.getAddress());
				values.put(DrugstoreInfoTable.LEVL, info.getLevl());
				values.put(DrugstoreInfoTable.MONTH_AMT, info.getMonth_amt());
				values.put(DrugstoreInfoTable.SALES, info.getSales());
				values.put(DrugstoreInfoTable.SALE_DBZH, info.getSale_dbzh());
				values.put(DrugstoreInfoTable.DEST_FLAG, info.getDest_flag());
				values.put(DrugstoreInfoTable.FCREATETIME,
						info.getFcreatetime());
				values.put(DrugstoreInfoTable.AREA_CODE, info.getArea_code());
				values.put(DrugstoreInfoTable.USE_FLAG, info.getUse_flag());
				values.put(DrugstoreInfoTable.CITY_NAME, info.getCity_name());
				values.put(DrugstoreInfoTable.PROVINCE_NAME,
						info.getProvince_name());
				long row = db.replace(DrugstoreInfoTable.TABLE_NAME, null,
						values);
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
	public boolean addDrugstoreInfos(String tableName,
			List<DrugstoreInfo> infoList, boolean isDeleteTable) {
		synchronized (synObj) {
			if (infoList == null) {
				return false;
			}
			SQLiteDatabase db = null;
			boolean result = false;
			try {
				db = openDatabase();
				db.beginTransaction();
				if (DrugstoreInfoTable.TEMP_TABLE_NAME.equals(tableName)
						&& isDeleteTable) {
					db.delete(tableName, null, null);
				}
				for (DrugstoreInfo info : infoList) {
					if (info == null) {
						continue;
					}
					ContentValues values = new ContentValues();
					values.put("cid", info.getCid());
					values.put(DrugstoreInfoTable.CNAME, info.getCname());
					values.put(DrugstoreInfoTable.DS_CODE2, info.getDs_code2());
					values.put(DrugstoreInfoTable.POINTX, info.getPointx());
					values.put(DrugstoreInfoTable.POINTY, info.getPointy());
					values.put(DrugstoreInfoTable.ADDRESS, info.getAddress());
					values.put(DrugstoreInfoTable.LEVL, info.getLevl());
					values.put(DrugstoreInfoTable.MONTH_AMT,
							info.getMonth_amt());
					values.put(DrugstoreInfoTable.SALES, info.getSales());
					values.put(DrugstoreInfoTable.SALE_DBZH,
							info.getSale_dbzh());
					values.put(DrugstoreInfoTable.DEST_FLAG,
							info.getDest_flag());
					values.put(DrugstoreInfoTable.FCREATETIME,
							info.getFcreatetime());
					values.put(DrugstoreInfoTable.AREA_CODE,
							info.getArea_code());
					values.put(DrugstoreInfoTable.USE_FLAG, info.getUse_flag());
					values.put(DrugstoreInfoTable.CITY_NAME,
							info.getCity_name());
					values.put(DrugstoreInfoTable.PROVINCE_NAME,
							info.getProvince_name());
					long row = db.replace(tableName, null, values);
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
	public List<DrugstoreInfo> getDrugstoreInfos(double pointX, double pointY,
			double areaDir) {
		synchronized (synObj) {
			SQLiteDatabase db = null;
			Cursor cursor = null;
			List<DrugstoreInfo> infos = null;
			try {
				double longitude_left = pointX + 0.00001 * areaDir;
				double longitude_right = pointX - 0.00001 * areaDir;
				double latitude_top = pointY + 0.00001 * 1.1 * areaDir;
				double latitude_bottom = pointY - 0.00001 * 1.1 * areaDir;
				String sql = "select * from " + DrugstoreInfoTable.TABLE_NAME
						+ " where (" + DrugstoreInfoTable.POINTX
						+ " BETWEEN ? AND ? ) and ("
						+ DrugstoreInfoTable.POINTY + " BETWEEN ? AND ? )"
						+ " ;";
				db = openDatabase();
				cursor = db.rawQuery(
						sql,
						new String[] { String.valueOf(longitude_right),
								String.valueOf(longitude_left),
								String.valueOf(latitude_bottom),
								String.valueOf(latitude_top) });
				if (cursor != null) {
					cursor.moveToFirst();
					infos = new ArrayList<DrugstoreInfo>();
					DrugstoreInfo info = null;
					while (!cursor.isAfterLast()) {
						double pointx2 = 0;
						double pointy2 = 0;
						try {
							pointx2 = Double
									.parseDouble(cursor.getString(cursor
											.getColumnIndex(DrugstoreInfoTable.POINTX)));
							pointy2 = Double
									.parseDouble(cursor.getString(cursor
											.getColumnIndex(DrugstoreInfoTable.POINTY)));
						} catch (Exception e) {
						}
						double distance = Distance.getDistance(pointX, pointY,
								pointx2, pointy2);
						if (distance <= areaDir) {
							info = new DrugstoreInfo();
							info.setCid(cursor.getString(cursor
									.getColumnIndex("cid")));
							info.setDs_code2(cursor.getString(cursor
									.getColumnIndex(DrugstoreInfoTable.DS_CODE2)));
							info.setCname(cursor.getString(cursor
									.getColumnIndex(DrugstoreInfoTable.CNAME)));
							info.setPointx(String.valueOf(pointx2));
							info.setPointy(String.valueOf(pointy2));
							info.setAddress(cursor.getString(cursor
									.getColumnIndex(DrugstoreInfoTable.ADDRESS)));
							info.setLevl(cursor.getString(cursor
									.getColumnIndex(DrugstoreInfoTable.LEVL)));
							info.setMonth_amt(cursor.getString(cursor
									.getColumnIndex(DrugstoreInfoTable.MONTH_AMT)));
							info.setSales(cursor.getString(cursor
									.getColumnIndex(DrugstoreInfoTable.SALES)));
							info.setSale_dbzh(cursor.getString(cursor
									.getColumnIndex(DrugstoreInfoTable.SALE_DBZH)));
							info.setDest_flag(cursor.getString(cursor
									.getColumnIndex(DrugstoreInfoTable.DEST_FLAG)));
							info.setFcreatetime(cursor.getString(cursor
									.getColumnIndex(DrugstoreInfoTable.FCREATETIME)));
							info.setArea_code(cursor.getString(cursor
									.getColumnIndex(DrugstoreInfoTable.AREA_CODE)));
							info.setUse_flag(cursor.getString(cursor
									.getColumnIndex(DrugstoreInfoTable.USE_FLAG)));
							info.setCity_name(cursor.getString(cursor
									.getColumnIndex(DrugstoreInfoTable.CITY_NAME)));
							info.setProvince_name(cursor.getString(cursor
									.getColumnIndex(DrugstoreInfoTable.PROVINCE_NAME)));

							infos.add(info);
						}
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
	public boolean addAreaInfo(AreaInfo info) {
		synchronized (synObj) {
			if (info == null) {
				return false;
			}
			SQLiteDatabase db = null;
			boolean result = false;
			try {
				db = openDatabase();
				db.delete(AreaInfoTable.TABLE_NAME, AreaInfoTable.AREA_CODE
						+ " = ? ", new String[] { info.getArea_code() });
				ContentValues values = new ContentValues();
				values.put(AreaInfoTable.AREA_CODE, info.getArea_code());
				values.put(AreaInfoTable.AREA_NAME, info.getArea_name());
				values.put(AreaInfoTable.DQ_NAME, info.getDq_name());
				values.put(AreaInfoTable.SQ_NAME, info.getSq_name());
				values.put(AreaInfoTable.QUY_NAME, info.getQuy_name());
				values.put(AreaInfoTable.CITY, info.getCity());
				values.put(AreaInfoTable.CITY_FLAG, info.getCity_flag());
				values.put(AreaInfoTable.SQ_FLAG, info.getSq_flag());
				long row = db.replace(AreaInfoTable.TABLE_NAME, null, values);
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
	public boolean addAreaInfos(List<AreaInfo> infoList) {
		synchronized (synObj) {
			if (infoList == null) {
				return false;
			}
			SQLiteDatabase db = null;
			boolean result = false;
			try {
				db = openDatabase();

				db.beginTransaction();
				db.delete(AreaInfoTable.TABLE_NAME, null, null);
				for (AreaInfo info : infoList) {
					if (info == null) {
						continue;
					}

					ContentValues values = new ContentValues();
					values.put(AreaInfoTable.AREA_CODE, info.getArea_code());
					values.put(AreaInfoTable.AREA_NAME, info.getArea_name());
					values.put(AreaInfoTable.DQ_NAME, info.getDq_name());
					values.put(AreaInfoTable.SQ_NAME, info.getSq_name());
					values.put(AreaInfoTable.QUY_NAME, info.getQuy_name());
					values.put(AreaInfoTable.CITY, info.getCity());
					values.put(AreaInfoTable.CITY_FLAG, info.getCity_flag());
					values.put(AreaInfoTable.SQ_FLAG, info.getSq_flag());
					db.replace(AreaInfoTable.TABLE_NAME, null, values);
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
	public boolean isTableDataExist(String tableName) {
		synchronized (synObj) {
			SQLiteDatabase db = null;
			boolean isTableDataExist = false;
			try {
				db = openDatabase();
				String sql1 = "select count(*) from " + tableName;
				Cursor cursor = db.rawQuery(sql1, null);
				if (cursor != null) {
					cursor.moveToFirst();
					int size = cursor.getInt(0);
					if (size > 0) {
						isTableDataExist = true;
					}

					cursor.close();
				}
			} finally {
				closeDatabase();
			}
			return isTableDataExist;
		}
	}

	@Override
	public DrugstoreInfoListAndMd5 getDrugstoreInfos2MD5(int pageSize,
			int pageNum) {
		synchronized (synObj) {
			SQLiteDatabase db = null;
			Cursor cursor = null;
			String value = "";
			DrugstoreInfoListAndMd5 result = new DrugstoreInfoListAndMd5();
			try {
				String sql = "select cid," + DrugstoreInfoTable.DS_CODE2 + ","
						+ DrugstoreInfoTable.CNAME + ","
						+ DrugstoreInfoTable.ADDRESS + ","
						+ DrugstoreInfoTable.POINTX + ","
						+ DrugstoreInfoTable.POINTY + ","
						+ DrugstoreInfoTable.LEVL + ","
						+ DrugstoreInfoTable.MONTH_AMT + ","
						+ DrugstoreInfoTable.SALES + ","
						+ DrugstoreInfoTable.SALE_DBZH + ","
						+ DrugstoreInfoTable.DEST_FLAG + ","
						+ DrugstoreInfoTable.AREA_CODE + ","
						+ DrugstoreInfoTable.USE_FLAG + ","
						+ DrugstoreInfoTable.CITY_NAME + ","
						+ DrugstoreInfoTable.PROVINCE_NAME + " from "
						+ DrugstoreInfoTable.TABLE_NAME + " limit " + " ? "
						+ ", " + " ? ";
				db = openDatabase();
				cursor = db.rawQuery(sql,
						new String[] {
								String.valueOf(pageSize * (pageNum - 1)),
								String.valueOf(pageSize) });
				List<DrugstoreInfo> infos = null;
				if (cursor != null) {
					cursor.moveToFirst();
					MessageDigest md5 = MessageDigest.getInstance("MD5");
					// StringBuffer sb = new StringBuffer();
					DrugstoreInfo info = null;
					infos = new ArrayList<DrugstoreInfo>();
					while (!cursor.isAfterLast()) {

						info = new DrugstoreInfo();
						String str = cursor.getString(cursor
								.getColumnIndex("cid"));
						info.setCid(str);
						if (!TextUtils.isEmpty(str) && !str.equals("null")) {
							// sb.append(str);
							md5.update(str.getBytes("utf-8"));
						}
						str = cursor.getString(cursor
								.getColumnIndex(DrugstoreInfoTable.DS_CODE2));
						info.setDs_code2(str);
						if (!TextUtils.isEmpty(str) && !str.equals("null")) {
							// sb.append(str);
							md5.update(str.getBytes("utf-8"));
						}
						str = cursor.getString(cursor
								.getColumnIndex(DrugstoreInfoTable.CNAME));
						info.setCname(str);
						if (!TextUtils.isEmpty(str) && !str.equals("null")) {
							// sb.append(str);
							md5.update(str.getBytes("utf-8"));
						}
						str = cursor.getString(cursor
								.getColumnIndex(DrugstoreInfoTable.ADDRESS));
						info.setAddress(str);
						if (!TextUtils.isEmpty(str) && !str.equals("null")) {
							// sb.append(str);
							md5.update(str.getBytes("utf-8"));
						}
						str = cursor.getString(cursor
								.getColumnIndex(DrugstoreInfoTable.POINTX));
						info.setPointx(str);
						if (!TextUtils.isEmpty(str) && !str.equals("null")) {
							// sb.append(str);
							md5.update(str.getBytes("utf-8"));
						}
						str = cursor.getString(cursor
								.getColumnIndex(DrugstoreInfoTable.POINTY));
						info.setPointy(str);
						if (!TextUtils.isEmpty(str) && !str.equals("null")) {
							// sb.append(str);
							md5.update(str.getBytes("utf-8"));
						}
						str = cursor.getString(cursor
								.getColumnIndex(DrugstoreInfoTable.LEVL));
						info.setLevl(str);
						if (!TextUtils.isEmpty(str) && !str.equals("null")) {
							// sb.append(str);
							md5.update(str.getBytes("utf-8"));
						}
						str = cursor.getString(cursor
								.getColumnIndex(DrugstoreInfoTable.MONTH_AMT));
						info.setMonth_amt(str);
						if (!TextUtils.isEmpty(str) && !str.equals("null")) {
							// sb.append(str);
							md5.update(str.getBytes("utf-8"));
						}
						str = cursor.getString(cursor
								.getColumnIndex(DrugstoreInfoTable.SALES));
						info.setSales(str);
						if (!TextUtils.isEmpty(str) && !str.equals("null")) {
							// sb.append(str);
							md5.update(str.getBytes("utf-8"));
						}
						str = cursor.getString(cursor
								.getColumnIndex(DrugstoreInfoTable.SALE_DBZH));
						info.setSale_dbzh(str);
						if (!TextUtils.isEmpty(str) && !str.equals("null")) {
							// sb.append(str);
							md5.update(str.getBytes("utf-8"));
						}
						str = cursor.getString(cursor
								.getColumnIndex(DrugstoreInfoTable.DEST_FLAG));
						info.setDest_flag(str);
						if (!TextUtils.isEmpty(str) && !str.equals("null")) {
							// sb.append(str);
							md5.update(str.getBytes("utf-8"));
						}
						str = cursor.getString(cursor
								.getColumnIndex(DrugstoreInfoTable.AREA_CODE));
						info.setArea_code(str);
						if (!TextUtils.isEmpty(str) && !str.equals("null")) {
							// sb.append(str);
							md5.update(str.getBytes("utf-8"));
						}
						str = cursor.getString(cursor
								.getColumnIndex(DrugstoreInfoTable.USE_FLAG));
						info.setUse_flag(str);
						if (!TextUtils.isEmpty(str) && !str.equals("null")) {
							// sb.append(str);
							md5.update(str.getBytes("utf-8"));
						}
						str = cursor.getString(cursor
								.getColumnIndex(DrugstoreInfoTable.CITY_NAME));
						info.setCity_name(str);
						if (!TextUtils.isEmpty(str) && !str.equals("null")) {
							// sb.append(str);
							md5.update(str.getBytes("utf-8"));
						}
						str = cursor
								.getString(cursor
										.getColumnIndex(DrugstoreInfoTable.PROVINCE_NAME));
						info.setProvince_name(str);
						if (!TextUtils.isEmpty(str) && !str.equals("null")) {
							// sb.append(str);
							md5.update(str.getBytes("utf-8"));
						}

						infos.add(info);

						// sb.append("\n");
						cursor.moveToNext();
					}
					// sb.append("\n");
					// sb.append("\n");
					// sb.append("\n");
					// FileOutputStream os = new FileOutputStream(StorageConfig
					// .getDataDir().getAbsoluteFile() + "/logs.txt", true);
					// os.write(sb.toString().getBytes("utf-8"));
					// os.flush();
					// os.close();
					value = AndroidUtil.byteArrayToHexString(md5.digest());
					result.setMd5(value);
					result.setList(infos);
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
			return result;
		}
	}

	@Override
	public String getDrugInfos2MD5(int pageSize, int pageNum) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean copyData(String srcTable, String dstTable) {
		synchronized (synObj) {
			SQLiteDatabase db = null;
			boolean result = false;
			try {
				db = openDatabase();
				db.beginTransaction();
				db.delete(dstTable, null, null);
				String sql = "insert into " + dstTable + " select * from "
						+ srcTable + ";";
				db.execSQL(sql);
				db.delete(srcTable, null, null);
				db.setTransactionSuccessful();
				result = true;
			} catch(Exception e){
				e.printStackTrace();
			}finally {
				if (db != null) {
					db.endTransaction();
					closeDatabase();
				}

			}
			return result;
		}
	}

}
