package jp.isao.lost_property_prevention.db;

import java.util.ArrayList;

import jp.isao.lost_property_prevention.data.Lost_Property_Prevention_Data;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;


public class Lost_Property_Prevention_Dao {
	
	private DBHelper mDBHelper;
	
	public Lost_Property_Prevention_Dao(Context context) {
		mDBHelper = new DBHelper(context);
	}
	

	
	//DBに保存されているスケジュール検索
	//dateに検索する日付を入れて、検索し結果をArrayListで返している
	//dateがNullだった場合は、全データをArrayListに返している
	public ArrayList<Lost_Property_Prevention_Data> selectLost_Property_Prevention_Data() {
		SQLiteDatabase db = null;
		Cursor cursor = null;
		//DBから取得したスケジュール情報保持用
		ArrayList<Lost_Property_Prevention_Data> sds = new ArrayList<Lost_Property_Prevention_Data>();
		try {
			//SQLiteDatabase取得
			db = mDBHelper.getReadableDatabase();
			StringBuilder sql = new StringBuilder();
			sql.append("SELECT * FROM ");
			sql.append(Lost_Property_Prevention.DB_TABLE);
			//DB検索処理
			cursor = db.rawQuery(sql.toString(), null);
			//sql文ログ出力
			Log.d("sql", sql.toString());
			//検索結果が0件じゃない場合
			if (cursor.getCount() != 0) {
				cursor.moveToFirst();
				while(!cursor.isAfterLast()) {
					//ArrayListに追加処理
					sds.add(getLost_Property_Prevention_Data(cursor));
					cursor.moveToNext();
				}
				//検索結果が0件の場合、ログ出力
			} else {
				Log.d("Lost_Property_Prevention_Dao", "GPSスケジュール情報がありませんでした！");
			}	
		} catch (Exception e) {
			Log.e("Lost_Property_Prevention_Dao 56", e.getMessage());
		} finally {
			if (cursor != null) {
				cursor.close();
			}
			if (db != null) {
				db.close();
			}
		}
		return sds;
	}
	
	
	//スケジュール情報追加
	public boolean addLost_Property_Prevention_Data(Lost_Property_Prevention_Data lppd) {
		boolean bError = false;
		SQLiteDatabase db = null;
		
		if (lppd == null) {
			bError = true;
			return bError;
		}
		
		try {
			db = mDBHelper.getWritableDatabase();
			ContentValues cv = new ContentValues();
			cv.put(Lost_Property_Prevention.LAITUDE, lppd.getLatitude());
			cv.put(Lost_Property_Prevention.LONGITUDE, lppd.getLongitude());
			cv.put(Lost_Property_Prevention.STARTTIME, lppd.getStarttime());
			cv.put(Lost_Property_Prevention.RUNTIME, lppd.getRuntime());
			cv.put(Lost_Property_Prevention.EDITETEXT1, lppd.getEdittext_content1());
			cv.put(Lost_Property_Prevention.EDITETEXT2, lppd.getEdittext_content2());
			cv.put(Lost_Property_Prevention.EDITETEXT3, lppd.getEdittext_content3());
			cv.put(Lost_Property_Prevention.EDITETEXT4, lppd.getEdittext_content4());
			cv.put(Lost_Property_Prevention.EDITETEXT5, lppd.getEdittext_content5());
			cv.put(Lost_Property_Prevention.EDITETEXT6, lppd.getEdittext_content6());
			cv.put(Lost_Property_Prevention.EDITETEXT7, lppd.getEdittext_content7());
			cv.put(Lost_Property_Prevention.EDITETEXT8, lppd.getEdittext_content8());
			cv.put(Lost_Property_Prevention.EDITETEXT9, lppd.getEdittext_content9());
			cv.put(Lost_Property_Prevention.EDITETEXT10, lppd.getEdittext_content10());
			cv.put(Lost_Property_Prevention.VIB_RUNTIME, lppd.getVib_runtime());
			cv.put(Lost_Property_Prevention.MONDAY, lppd.getMonday());
			cv.put(Lost_Property_Prevention.TUESDAY, lppd.getTuesday());
			cv.put(Lost_Property_Prevention.WEDNESDAY, lppd.getWednesday());
			cv.put(Lost_Property_Prevention.THURSDAY, lppd.getThursday());
			cv.put(Lost_Property_Prevention.FRIDAY, lppd.getFriday());
			cv.put(Lost_Property_Prevention.SATURDAY, lppd.getSaturday());
			cv.put(Lost_Property_Prevention.SUNDAY, lppd.getSunday());
			cv.put(Lost_Property_Prevention.RUN_SERVICE, lppd.getRun_service());
			cv.put(Lost_Property_Prevention.NOW_RUNNING_SERVICE, lppd.getNow_running_service());
			cv.put(Lost_Property_Prevention.GPS_RUN_CIRCLE, lppd.getGps_run_circle());
			
			//DBに追加
			int result = (int)db.insertOrThrow(Lost_Property_Prevention.DB_TABLE, null, cv);
			if (result == -1) {
				bError = true;
				Log.e("Lost_Property_Prevention_Dao", "GPSスケジュール情報が正しく追加されませんでした！");
			}
			
		} catch (SQLiteException e) {
			bError = true;
			Log.e("Lost_Property_Prevention_Dao 117", e.getMessage());
		} catch (SQLException e) {
			bError = true;
			Log.e("Lost_Property_Prevention_Dao 120", e.getMessage());
		} finally {
			if (db != null) {
				db.close();
			}
		}
		return bError;
	}
	
	//スケジュール情報更新処理
	public boolean updateLost_Property_Prevention_Data(Lost_Property_Prevention_Data lppd, int rowId) {
		boolean bError = false;
		SQLiteDatabase db = null;
		
		if (lppd == null) {
			bError = true;
			return bError;
		}
		
		
		try {
			db = mDBHelper.getWritableDatabase();
			ContentValues cv = new ContentValues();
			cv.put(Lost_Property_Prevention.LAITUDE, lppd.getLatitude());
			cv.put(Lost_Property_Prevention.LONGITUDE, lppd.getLongitude());
			cv.put(Lost_Property_Prevention.STARTTIME, lppd.getStarttime());
			cv.put(Lost_Property_Prevention.RUNTIME, lppd.getRuntime());
			cv.put(Lost_Property_Prevention.EDITETEXT1, lppd.getEdittext_content1());
			cv.put(Lost_Property_Prevention.EDITETEXT2, lppd.getEdittext_content2());
			cv.put(Lost_Property_Prevention.EDITETEXT3, lppd.getEdittext_content3());
			cv.put(Lost_Property_Prevention.EDITETEXT4, lppd.getEdittext_content4());
			cv.put(Lost_Property_Prevention.EDITETEXT5, lppd.getEdittext_content5());
			cv.put(Lost_Property_Prevention.EDITETEXT6, lppd.getEdittext_content6());
			cv.put(Lost_Property_Prevention.EDITETEXT7, lppd.getEdittext_content7());
			cv.put(Lost_Property_Prevention.EDITETEXT8, lppd.getEdittext_content8());
			cv.put(Lost_Property_Prevention.EDITETEXT9, lppd.getEdittext_content9());
			cv.put(Lost_Property_Prevention.EDITETEXT10, lppd.getEdittext_content10());
			cv.put(Lost_Property_Prevention.VIB_RUNTIME, lppd.getVib_runtime());
			cv.put(Lost_Property_Prevention.MONDAY, lppd.getMonday());
			cv.put(Lost_Property_Prevention.TUESDAY, lppd.getTuesday());
			cv.put(Lost_Property_Prevention.WEDNESDAY, lppd.getWednesday());
			cv.put(Lost_Property_Prevention.THURSDAY, lppd.getThursday());
			cv.put(Lost_Property_Prevention.FRIDAY, lppd.getFriday());
			cv.put(Lost_Property_Prevention.SATURDAY, lppd.getSaturday());
			cv.put(Lost_Property_Prevention.SUNDAY, lppd.getSunday());
			cv.put(Lost_Property_Prevention.RUN_SERVICE, lppd.getRun_service());
			cv.put(Lost_Property_Prevention.NOW_RUNNING_SERVICE, lppd.getNow_running_service());
			cv.put(Lost_Property_Prevention.GPS_RUN_CIRCLE, lppd.getGps_run_circle());
			
			//DBに更新
			int result = (int)db.update(Lost_Property_Prevention.DB_TABLE, cv, Lost_Property_Prevention.COLUMN_ID + "=?", new String[] {String.valueOf(rowId)});
			if (result != 1) {
				bError = true;
				Log.e("Lost_Property_Prevention_Dao", "GPSスケジュール情報が正しく更新されませんでした！");
			}
			
		} catch (SQLiteException e) {
			bError = true;
			Log.e("Lost_Property_Prevention_Dao 178", e.getMessage());
		} finally {
			if (db != null) {
				db.close();
			}
		}
		return bError;
	}
	
	//スケジュール情報削除処理
	public boolean deleteLost_Property_Prevention_Data(int rowId) {
		boolean bError = false;
		SQLiteDatabase db = null;
		
		try {
			db = mDBHelper.getWritableDatabase();
			//DB削除
			int result = (int)db.delete(Lost_Property_Prevention.DB_TABLE, Lost_Property_Prevention.COLUMN_ID + "=?", new String[] {String.valueOf(rowId)});
			if (result != 1) {
				bError = true;
				Log.e("Lost_Property_Prevention_Dao 198", "GPSスケジュール情報が正しく削除されませんでした！");
			}			
		} catch (SQLiteException e) {
			bError = true;
			Log.e("Lost_Property_Prevention_Dao 202", e.getMessage());
		} finally {
			if (db != null) {
				db.close();
			}
		}
		return bError;

	}
	
	//スケジュール情報の値を設定し、取得
	private Lost_Property_Prevention_Data getLost_Property_Prevention_Data(Cursor cursor) {
		Lost_Property_Prevention_Data lppd = new Lost_Property_Prevention_Data();
		lppd.setRowId(cursor.getInt(0));
		lppd.setLatitude(cursor.getDouble(1));
		lppd.setLongitude(cursor.getDouble(2));
		lppd.setStarttime(cursor.getString(3));
		lppd.setRuntime(cursor.getString(4));
		lppd.setEdittext_content1(cursor.getString(5));
		lppd.setEdittext_content2(cursor.getString(6));
		lppd.setEdittext_content3(cursor.getString(7));
		lppd.setEdittext_content4(cursor.getString(8));
		lppd.setEdittext_content5(cursor.getString(9));
		lppd.setEdittext_content6(cursor.getString(10));
		lppd.setEdittext_content7(cursor.getString(11));
		lppd.setEdittext_content8(cursor.getString(12));
		lppd.setEdittext_content9(cursor.getString(13));
		lppd.setEdittext_content10(cursor.getString(14));
		lppd.setVib_runtime(cursor.getString(15));
		lppd.setMonday(cursor.getString(16));
		lppd.setTuesday(cursor.getString(17));
		lppd.setWednesday(cursor.getString(18));
		lppd.setThursday(cursor.getString(19));
		lppd.setFriday(cursor.getString(20));
		lppd.setSaturday(cursor.getString(21));
		lppd.setSunday(cursor.getString(22));
		lppd.setRun_service(cursor.getString(23));
		lppd.setNow_running_service(cursor.getString(24));
		lppd.setGps_run_circle(cursor.getString(25));
		
		return lppd;
	}
	
	

}
