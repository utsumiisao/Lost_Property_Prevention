package jp.isao.lost_property_prevention.db;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

public class DBHelper extends SQLiteOpenHelper {
	//DB名
	public static final String DB_NAME = "lost_property.db";
	//DBのテーブル名
	public static final String DB_TABLE = "lost_property";
	//DBのVersion情報
	public static final int DB_VERSION = 1; 
	//RowID
	public static final String COLUMN_ID = "_id";
	//緯度
	public static final String LAITUDE = "laitude";
	//経度
	public static final String LONGITUDE = "longitude";
	//起動時刻
	public static final String STARTTIME = "starttime";
	//実行時間
	public static final String RUNTIME = "runtime";
	//確認内容1
	public static final String EDITETEXT1 = "edittext_content1";
	//確認内容2
	public static final String EDITETEXT2 = "edittext_content2";
	//確認内容3
	public static final String EDITETEXT3 = "edittext_content3";
	//確認内容4
	public static final String EDITETEXT4 = "edittext_content4";
	//確認内容5
	public static final String EDITETEXT5 = "edittext_content5";
	//確認内容6
	public static final String EDITETEXT6 = "edittext_content6";
	//確認内容7
	public static final String EDITETEXT7 = "edittext_content7";
	//確認内容8
	public static final String EDITETEXT8 = "edittext_content8";	
	//確認内容9
	public static final String EDITETEXT9 = "edittext_content9";
	//確認内容10
	public static final String EDITETEXT10 = "edittext_content10";
	//バイブレータ実行時間
	public static final String VIB_RUNTIME = "vib_runtime";
	
	
	//月曜
	public static final String MONDAY = "monday";	
	//火曜
	public static final String TUESDAY = "tuesday";	
	//水曜
	public static final String WEDNESDAY = "wednesday";	
	//木曜
	public static final String THURSDAY = "thursday";	
	//金曜
	public static final String FRIDAY = "friday";
	//土曜
	public static final String SATURDAY = "saturday";
	//日曜
	public static final String SUNDAY = "sunday";	
	//サービス実行
	public static final String RUN_SERVICE = "run_service";
	//サービス実行中フラグ
	public static final String NOW_RUNNING_SERVICE = "now_running_service";
	//GPS起動距離
	public static final String GPS_RUN_CIRCLE = "gps_run_circle";
	
	
	
	
	private Context mContext;
	

	public DBHelper(Context context) {

		super(context, DB_NAME, null, DB_VERSION);
		mContext = context;
		// TODO Auto-generated constructor stubj
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		try {
			StringBuilder createSql = new StringBuilder();
			createSql.append("CREATE TABLE " + DB_TABLE);
			createSql.append("(");
			//,space を空けること
			createSql.append(COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, ");
			createSql.append(LAITUDE + " REAL NOT NULL, ");
			createSql.append(LONGITUDE + " REAL NOT NULL, ");
			createSql.append(STARTTIME + " TEXT NOT NULL, ");
			createSql.append(RUNTIME + " TEXT NOT NULL, ");
			createSql.append(EDITETEXT1 + " TEXT, ");
			createSql.append(EDITETEXT2 + " TEXT, ");
			createSql.append(EDITETEXT3 + " TEXT, ");
			createSql.append(EDITETEXT4 + " TEXT, ");
			createSql.append(EDITETEXT5 + " TEXT, ");
			createSql.append(EDITETEXT6 + " TEXT, ");
			createSql.append(EDITETEXT7 + " TEXT, ");
			createSql.append(EDITETEXT8 + " TEXT, ");
			createSql.append(EDITETEXT9 + " TEXT, ");
			createSql.append(EDITETEXT10 + " TEXT, ");
			createSql.append(VIB_RUNTIME + " TEXT NOT NULL, ");
			createSql.append(MONDAY + " TEXT NOT NULL, ");
			createSql.append(TUESDAY + " TEXT NOT NULL, ");
			createSql.append(WEDNESDAY + " TEXT NOT NULL, ");
			createSql.append(THURSDAY + " TEXT NOT NULL, ");
			createSql.append(FRIDAY + " TEXT NOT NULL, ");
			createSql.append(SATURDAY + " TEXT NOT NULL, ");
			createSql.append(SUNDAY + " TEXT NOT NULL, ");
			createSql.append(RUN_SERVICE + " TEXT NOT NULL, ");
			createSql.append(NOW_RUNNING_SERVICE + " TEXT NOT NULL, ");
			createSql.append(GPS_RUN_CIRCLE + " TEXT NOT NULL ");
			createSql.append(")");
			db.execSQL(createSql.toString());

			
		} catch(SQLException e) {
			Log.e("DBHelper[38]:onCreate()", e.getMessage());
		}

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		try {
			StringBuilder dropSql = new StringBuilder();
			dropSql.append("DROP TABLE IF EXISTS " + DB_TABLE);
			db.execSQL(dropSql.toString());
			onCreate(db);
		} catch (SQLException e) {
			Log.e("DBHelper[54]:onCreate()", e.getMessage());
		}

	}

}
