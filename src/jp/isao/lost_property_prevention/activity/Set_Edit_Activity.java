package jp.isao.lost_property_prevention.activity;


import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.google.ads.Ad;
import com.google.ads.AdListener;
import com.google.ads.AdRequest;
import com.google.ads.AdView;
import com.google.ads.AdRequest.ErrorCode;

import jp.isao.lost_property_prevention.R;
import jp.isao.lost_property_prevention.data.Lost_Property_Prevention_Data;
import jp.isao.lost_property_prevention.db.Lost_Property_Prevention_Dao;
import jp.isao.lost_property_prevention.receiver.GetAwayHomeReceiver;
import jp.isao.lost_property_prevention.service.GetAwayHomeService;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class Set_Edit_Activity extends Activity implements AdListener {
	
	Calendar dateAndTime = Calendar.getInstance();
	Calendar timeMinuteSecond = Calendar.getInstance();
	
	private Button mPlaceCheck;
	private Button mPlaceUpdate;
	private Button mStarttime;
	private Spinner spinner_runtime;
	private Spinner spinner_gps_circle;
	private Button mSet;
	private Button mCancel;
	
	private CheckBox mCheck_Monday;
	private CheckBox mCheck_Tuesday;
	private CheckBox mCheck_Wednesday;
	private CheckBox mCheck_Thursday;
	private CheckBox mCheck_Friday;
	private CheckBox mCheck_Saturday;
	private CheckBox mCheck_Sunday;
	
	private EditText mContent1;
	private EditText mContent2;
	private EditText mContent3;
	private EditText mContent4;
	private EditText mContent5;
	private EditText mContent6;
	private EditText mContent7;
	private EditText mContent8;
	private EditText mContent9;
	private EditText mContent10;
	
	private Button mVibrate_time_btn;
	
	private Button mVibrate_btn_plus_minute;
	private TextView mVibrate_text_minute;
	private Button mVibrate_btn_minus_minute;
	
	private Button mVibrate_btn_plus_second;
	private TextView mVibrate_text_second;
	private Button mVibrate_btn_minus_second;
	
	private Lost_Property_Prevention_Data lppd;
	private Lost_Property_Prevention_Data lppdget;
	
	private LayoutInflater mInflater;
	
	private TimePickerDialog tpd_vibrate_time;
	
	
	private String starttime_String;
	private String vibruntime_String;
	
	private SQLiteDatabase db = null;
	
	private double latitude_Set=0.0;
	private double longitude_Set=0.0;
	
	private double defaultValueDouble = 0.0;
	
	private int rowId_Setting;
	

	
	private boolean starttimeFlag = false;
	private boolean vibtimeFlag = false;
	private boolean dbGetDataFlag = false;
	private boolean cursorFlag = false;
	
	private Cursor cursor = null;
	
	
	private int hourofday;
	private int minute;
	private int second;
	
	private AdView adGoogle;
    private AdRequest adr;
	
	//Set　Activityから発行された位置情報追加のkey
	private boolean key_Set_Edit_Activity=true;
	
	
	//setボタンを押したときのコールバックメソッド
	//開始時刻のTimePickerDialog
	TimePickerDialog.OnTimeSetListener t_start_time = new OnTimeSetListener() {
		
		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			//hourOfDayの時間表示が,24時であるが、TimePickerDialogに表示する時は24時にセットできないので、24時を　＞　0時に変更する

			dateAndTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
			dateAndTime.set(Calendar.MINUTE, minute);
			//SimpleDateFormat生成
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
			//時間のフォーマット設定し、スタート時間ボタン画面に表示
			mStarttime.setText(sdf.format(dateAndTime.getTime()));
			
		}
	};
	
	//setボタンを押したときのコールバックメソッド
	//ヴァイブ実行時間のTimePickerDialog
	TimePickerDialog.OnTimeSetListener t_vib = new OnTimeSetListener() {

		@Override
		public void onTimeSet(TimePicker view, int h, int m) {
			//SimpleDateFormat生成
			mVibrate_time_btn.setText(util_int_digit_two_to_String(minute) + ":" + util_int_digit_two_to_String(second));
			
		}
		
	};
	
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.set_update);
		//リスナー登録
		mPlaceCheck = (Button)findViewById(R.id.btn_place_check_update);
		mPlaceUpdate = (Button)findViewById(R.id.btn_place_update);
		mStarttime = (Button)findViewById(R.id.btn_starttime_update);
		spinner_gps_circle = (Spinner)findViewById(R.id.spinner_circle);
		spinner_runtime = (Spinner)findViewById(R.id.spinner_update);
		mCheck_Monday = (CheckBox)findViewById(R.id.text_monday_checkbox_update);
		mCheck_Tuesday = (CheckBox)findViewById(R.id.text_tuesday_checkbox_update);
		mCheck_Wednesday = (CheckBox)findViewById(R.id.text_wednesday_checkbox_update);
		mCheck_Thursday = (CheckBox)findViewById(R.id.text_thursday_checkbox_update);
		mCheck_Friday = (CheckBox)findViewById(R.id.text_friday_checkbox_update);
		mCheck_Saturday = (CheckBox)findViewById(R.id.text_saturday_checkbox_update);
		mCheck_Sunday = (CheckBox)findViewById(R.id.text_sunday_checkbox_update);
		
		mContent1 = (EditText)findViewById(R.id.edit_text_content_update1);
		mContent2 = (EditText)findViewById(R.id.edit_text_content_update2);
		mContent3 = (EditText)findViewById(R.id.edit_text_content_update3);
		mContent4 = (EditText)findViewById(R.id.edit_text_content_update4);
		mContent5 = (EditText)findViewById(R.id.edit_text_content_update5);
		mContent6 = (EditText)findViewById(R.id.edit_text_content_update6);
		mContent7 = (EditText)findViewById(R.id.edit_text_content_update7);
		mContent8 = (EditText)findViewById(R.id.edit_text_content_update8);
		mContent9 = (EditText)findViewById(R.id.edit_text_content_update9);
		mContent10 = (EditText)findViewById(R.id.edit_text_content_update10);
		
		mVibrate_time_btn = (Button)findViewById(R.id.vibrate_time_update);
		
		mSet = (Button)findViewById(R.id.registry_btn_update);
		mCancel = (Button)findViewById(R.id.cancel_btn_update);
		

		
		
		//位置情報確認用
		mPlaceCheck.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Set_Edit_Activity.this, Location_Check_Activity.class);
				//位置情報確認するために、ArrayListの緯度、経度情報をLocation_Check_Activityに渡す
				intent.putExtra("Latitude_check", lppdget.getLatitude());
				intent.putExtra("Longitude_check", lppdget.getLongitude());
				startActivity(intent);
			}
		});
		
		//位置情報の更新Activityに飛ぶ
		mPlaceUpdate.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Set_Edit_Activity.this, Location_Check_Activity.class);
				//起動時間をボタンのテキストから取得
				starttime_String = mStarttime.getText().toString();
				
				//ヴァイブ実行時間をボタンのテキストから取得
				vibruntime_String = mVibrate_time_btn.getText().toString();
				//位置情報を更新し、緯度、経度を取得するためのkey
				intent.putExtra("lppd_from_Set_Edit_Activity", getLppd());
				intent.putExtra("key_Set_Edit_Activity", key_Set_Edit_Activity);
				startActivity(intent);
			}
		});
		

		
		
		//登録ボタン用のリスナー登録
		mSet.setOnClickListener(new OnClickListener() {
	
			
			@Override
			public void onClick(View v) {
				
				//SpinnerからgetSelectedItemId()を使用して値を取得する
				
				if(latitude_Set==0.0 || longitude_Set==0.0) {
					showDialog("位置情報が設定されていません","位置情報を設定してください");					
				} else if(mStarttime.getText().toString().equals("追加")) {
					showDialog("起動時刻が設定されていません","開始時刻を設定してください");
				} else if(mCheck_Monday.isChecked()==false && mCheck_Tuesday.isChecked()==false && mCheck_Wednesday.isChecked()==false && mCheck_Thursday.isChecked()==false &&
						mCheck_Friday.isChecked()==false && mCheck_Saturday.isChecked()==false && mCheck_Sunday.isChecked()==false) {
					showDialog("曜日が指定されていません", "曜日をチェックしてください");
				} else if(mContent1.getText().toString().equals("") && mContent2.getText().toString().equals("") && mContent3.getText().toString().equals("") && mContent4.getText().toString().equals("") &&
						mContent5.getText().toString().equals("") && mContent6.getText().toString().equals("") && mContent7.getText().toString().equals("") && mContent8.getText().toString().equals("") &&
						mContent9.getText().toString().equals("") && mContent10.getText().toString().equals("") ) {
					showDialog("確認内容が書かれていません", "確認内容を書いてください");
				} else if(mVibrate_time_btn.getText().toString().equals("追加")) {
					showDialog("ヴァイブ実行時間が設定されていません","ヴァイブ実行時間を設定してください");
				}
				else {
					final AlertDialog.Builder ad = new AlertDialog.Builder(Set_Edit_Activity.this);
					
					//起動時間をボタンのテキストから取得
					starttime_String = mStarttime.getText().toString();
					
					//ヴァイブ実行時間をボタンのテキストから取得
					vibruntime_String = mVibrate_time_btn.getText().toString();
					//上書き確認用のダイアログ

					ad.setTitle("確認");
					
					ad.setMessage( 	"起動時刻：　" + starttime_String + "\n" +
									"実行時間：　" + spinner_runtime.getSelectedItem().toString() + "\n" +
									"起動距離：　" + spinner_gps_circle.getSelectedItem().toString() + "\n" +
									run_a_day_of_the_week_toString() +
									"確認内容 1：　" + mContent1.getText().toString() + "\n" +
									"確認内容 2：　" + mContent2.getText().toString() + "\n" +
									"確認内容 3：　" + mContent3.getText().toString() + "\n" +
									"確認内容 4：　" + mContent4.getText().toString() + "\n" +
									"確認内容 5：　" + mContent5.getText().toString() + "\n" +
									"確認内容 6：　" + mContent6.getText().toString() + "\n" +
									"確認内容 7：　" + mContent7.getText().toString() + "\n" +
									"確認内容 8：　" + mContent8.getText().toString() + "\n" +
									"確認内容 9：　" + mContent9.getText().toString() + "\n" +
									"確認内容 10：　" + mContent10.getText().toString() + "\n" +
									"バイブレータの実行時間:　" + vibruntime_String.replace("\n", "　")
									
					);
					
					ad.setPositiveButton("上書き登録", new DialogInterface.OnClickListener() {
						//DBに登録内容を上書き保存
						@Override
						public void onClick(DialogInterface dialog, int which) {
									Lost_Property_Prevention_Dao lppdao = new Lost_Property_Prevention_Dao(Set_Edit_Activity.this);
									
									//lppdget リストから取得したrowid
									lppdao.updateLost_Property_Prevention_Data(getLppd(), lppdget.getRowId());
									Log.d("------------lppdget.getRowId()-----------", String.valueOf(lppdget.getRowId()));
									GetAwayHomeReceiver gahr = new GetAwayHomeReceiver();
									//更新したデータが既にサビースを起動していれば停止する
									//実行中のサービスと更新したrowIdを比較する
									if(lppdget.getRowId()==gahr.getNowRuningServiceRowId(Set_Edit_Activity.this)) {
										Intent intentService = new Intent(Set_Edit_Activity.this, GetAwayHomeService.class);
										stopService(intentService);
										//NotifycationManager取得
										NotificationManager mgr = (NotificationManager)Set_Edit_Activity.this.getSystemService(Set_Edit_Activity.this.NOTIFICATION_SERVICE);
										//通知削除
										mgr.cancel(lppdget.getRowId());
									}

									Toast.makeText(Set_Edit_Activity.this, "リストを更新しました", Toast.LENGTH_LONG).show();
									Intent intent = new Intent(Set_Edit_Activity.this, Main_Activity.class);
									startActivity(intent);
									
							
						}
													
					});
					
					
					ad.setNegativeButton("キャンセル", null);
					ad.show();
				}
				
			}
		});
		

		
		
		
		//キャンセル用のボタン
		mCancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//Cursor使われていたら閉じる
				if (cursor != null) {
					cursor.close();
				}
				//dbが使われていたら閉じる
				if (db != null) {
					db.close();
				}
				Intent intent = new Intent(Set_Edit_Activity.this, Main_Activity.class);
				startActivity(intent);
				
			}
		});
		
		

	}
	
	
	private String run_a_day_of_the_week_toString() {
		StringBuffer run_a_day_of_the_week = new StringBuffer();
		run_a_day_of_the_week.append("実行曜日：　");
		if (mCheck_Monday.isChecked()==true) {
			run_a_day_of_the_week.append("月曜　");
		}
		if (mCheck_Tuesday.isChecked()==true) {
			run_a_day_of_the_week.append("火曜　");
		}
		if (mCheck_Wednesday.isChecked()==true) {
			run_a_day_of_the_week.append("水曜　");
		}
		if (mCheck_Thursday.isChecked()==true) {
			run_a_day_of_the_week.append("木曜　");
		}
		if (mCheck_Friday.isChecked()==true) {
			run_a_day_of_the_week.append("金曜　");
		} 
		if (mCheck_Saturday.isChecked()==true) {
			run_a_day_of_the_week.append("土曜　");
		} 
		if (mCheck_Sunday.isChecked()==true) {
			run_a_day_of_the_week.append("日曜　");
		}
		run_a_day_of_the_week.append("\n");
	
		return run_a_day_of_the_week.toString();
	}

	
	//現在のレイアウトに表示されているデータを　データ保存用クラス Lost_Property_Prevention_Data　に保存するためのメソッド
	private Lost_Property_Prevention_Data getLppd() {

		lppd = new Lost_Property_Prevention_Data();
		//保存用クラスにデータをセットする
		lppd.setRowId(lppdget.getRowId());
		lppd.setLatitude(latitude_Set);
		lppd.setLongitude(longitude_Set);
		lppd.setStarttime(starttime_String);
		//実行時間セット　index番号 0　（時間）
		lppd.setGps_run_circle(String.valueOf(spinner_gps_circle.getSelectedItemPosition()));
		lppd.setRuntime(String.valueOf(spinner_runtime.getSelectedItemPosition()));
		lppd.setMonday(String.valueOf(mCheck_Monday.isChecked()));
		lppd.setTuesday(String.valueOf(mCheck_Tuesday.isChecked()));
		lppd.setWednesday(String.valueOf(mCheck_Wednesday.isChecked()));
		lppd.setThursday(String.valueOf(mCheck_Thursday.isChecked()));
		lppd.setFriday(String.valueOf(mCheck_Friday.isChecked()));
		lppd.setSaturday(String.valueOf(mCheck_Saturday.isChecked()));
		lppd.setSunday(String.valueOf(mCheck_Sunday.isChecked()));
		lppd.setEdittext_content1(mContent1.getText().toString());
		lppd.setEdittext_content2(mContent2.getText().toString());
		lppd.setEdittext_content3(mContent3.getText().toString());
		lppd.setEdittext_content4(mContent4.getText().toString());
		lppd.setEdittext_content5(mContent5.getText().toString());
		lppd.setEdittext_content6(mContent6.getText().toString());
		lppd.setEdittext_content7(mContent7.getText().toString());
		lppd.setEdittext_content8(mContent8.getText().toString());
		lppd.setEdittext_content9(mContent9.getText().toString());
		lppd.setEdittext_content10(mContent10.getText().toString());
		lppd.setVib_runtime(vibruntime_String);
		lppd.setRun_service("false");
		lppd.setNow_running_service("false");
		
		Log.d("------------latitude_Set-----------", String.valueOf(latitude_Set));
		Log.d("------------longitude_Set-----------", String.valueOf(longitude_Set));
		
		Log.d("------------lppd latitude_Set-----------", String.valueOf(lppd.getLatitude()));
		Log.d("------------lppd longitude_Set-----------", String.valueOf(lppd.getLongitude()));
		
		return lppd;
	}
	
	@Override
	protected void onResume() {
		
		
		lppdget = new Lost_Property_Prevention_Data();
		
		//Map_View_Check Activityから来た緯度、経度の情報を取得し、DBに保存する
		Intent i = getIntent();
		
		lppdget = (Lost_Property_Prevention_Data)i.getSerializableExtra("Schedule_ListItem_Click");
//		Log.d("------------Schedule_ListItem_Click()-----------", String.valueOf(lppdget.getRowId()));
		if(lppdget==null) {
			lppdget = (Lost_Property_Prevention_Data)i.getSerializableExtra("lppd_from_Location_Check_Activity");
			Log.d("------------lppd_from_Location_Check_Activity-----------", String.valueOf(lppdget));
		}
		
		if(lppdget != null) {
			//ArrayListのデータをインスタンスにセットする
			mStarttime.setText(lppdget.getStarttime());
			//int型に変換する
			spinner_gps_circle.setSelection(Integer.parseInt(lppdget.getGps_run_circle()));
			spinner_runtime.setSelection(Integer.parseInt((lppdget.getRuntime())));
			mCheck_Monday.setChecked(Boolean.parseBoolean(lppdget.getMonday()));
			mCheck_Tuesday.setChecked(Boolean.parseBoolean(lppdget.getTuesday()));
			mCheck_Wednesday.setChecked(Boolean.parseBoolean(lppdget.getWednesday()));
			mCheck_Thursday.setChecked(Boolean.parseBoolean(lppdget.getThursday()));
			mCheck_Friday.setChecked(Boolean.parseBoolean(lppdget.getFriday()));
			mCheck_Saturday.setChecked(Boolean.parseBoolean(lppdget.getSaturday()));
			mCheck_Sunday.setChecked(Boolean.parseBoolean(lppdget.getSunday()));
			mContent1.setText(lppdget.getEdittext_content1());
			mContent2.setText(lppdget.getEdittext_content2());
			mContent3.setText(lppdget.getEdittext_content3());
			mContent4.setText(lppdget.getEdittext_content4());
			mContent5.setText(lppdget.getEdittext_content5());
			mContent6.setText(lppdget.getEdittext_content6());
			mContent7.setText(lppdget.getEdittext_content7());
			mContent8.setText(lppdget.getEdittext_content8());
			mContent9.setText(lppdget.getEdittext_content9());
			mContent10.setText(lppdget.getEdittext_content10());
			mVibrate_time_btn.setText(lppdget.getVib_runtime());
			
			latitude_Set = lppdget.getLatitude();
			longitude_Set = lppdget.getLongitude();
			Log.d("------------lppdget.getRowId()-----------", String.valueOf(lppdget.getRowId()));
			
		}
		
		
		
		//Location_Check_Activityから発行されたIntent
		//もしDBに緯度、経度の情報が有った場合、上書き保存する
		//そうでない場合は、DBの緯度、経度を保存する
//		latitude_Set = i.getDoubleExtra("latitude_LocationCheck", latitude_Set);
		//Set　Activityから発行され位置情報更新用のIntent
//		longitude_Set = i.getDoubleExtra("longitude_LocationCheck", longitude_Set);
		
		//TimePickerDialog用のリスナー登録
		//開始時刻のTimePickerDialog
		mStarttime.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				if(mStarttime.getText().toString().equals("追加")) {
					hourofday = Calendar.HOUR_OF_DAY;
					minute = Calendar.MINUTE;
				} else {
					hourofday = Integer.valueOf(mStarttime.getText().toString().substring(0, 2));
					minute = Integer.valueOf(mStarttime.getText().toString().substring(3,5));
				}
				
				
				TimePickerDialog tpd_start_time = new TimePickerDialog(Set_Edit_Activity.this, 
										t_start_time,
										//DBに保存されている起動時間をTimePickerDialogにセットしている
//										Integer.valueOf(starttime_Setting.substring(0, 2)),
										hourofday,
										//Calendar or 起動時刻ボタンから分を取得
										minute,
										true);
				
				
				tpd_start_time.show();
				
				//ヴァイブ実行時間を設定したので、starttimeFlagをtrueに設定する
				starttimeFlag = true;
			}

		});
		
		//TimePickerDialog用のリスナー登録
		mVibrate_time_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(mVibrate_time_btn.getText().toString().equals("追加")) {
					minute = 0;
					second = 0;
				} else {
					minute = Integer.valueOf(mVibrate_time_btn.getText().toString().substring(0, 2));
					second = Integer.valueOf(mVibrate_time_btn.getText().toString().substring(3,5));
				}
				
				customPickerDialog();
				
				//スタート時間を設定したので、timeFlagをtrueに設定する
				vibtimeFlag = true;
			}

		});
		
		
	
		
		super.onResume();
	}
	
	//分：秒　を設定するためのCustomPickerDialog
	private void customPickerDialog() {

		
		tpd_vibrate_time = new TimePickerDialog(Set_Edit_Activity.this, 
								t_vib,
								0,
								0,
								true);
		
		//インフレータのインスタンス生成
		mInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View convertView = mInflater.inflate(R.layout.vibrate_time_layout, null);
		//リスナー登録
		mVibrate_btn_plus_minute = (Button)convertView.findViewById(R.id.vib_btn_plus_minute);
		mVibrate_text_minute = (TextView)convertView.findViewById(R.id.vib_text_minute);
		mVibrate_btn_minus_minute = (Button)convertView.findViewById(R.id.vib_btn_minus_minute);
		mVibrate_btn_plus_second = (Button)convertView.findViewById(R.id.vib_btn_plus_second);
		mVibrate_text_second = (TextView)convertView.findViewById(R.id.vib_text_second);
		mVibrate_btn_minus_second = (Button)convertView.findViewById(R.id.vib_btn_minus_second);
		
		
		
		//分、秒をレイアウトのテキストにセットする
		mVibrate_text_minute.setText(util_int_digit_two_to_String(minute));
		mVibrate_text_second.setText(util_int_digit_two_to_String(second));
		
		//pickerDialogのタイトルに　分:秒 の形式でセットする
		tpd_vibrate_time.setTitle(util_int_digit_two_to_String(minute) + ":" + util_int_digit_two_to_String(second));
		//セットしたオリジナルのViewをTimePickerDialogにセットする
		tpd_vibrate_time.setView(convertView);
		
		//表示する
		tpd_vibrate_time.show();
		

		
		
		
		//分のプラスボタンを押したときのイベント
		mVibrate_btn_plus_minute.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) { 
				if(minute>=59) {
					minute=0;
				} else {
					minute++;
				}
				//TimePickerDialogの分のTextViewに変更したminuteを表示し、タイトルの　分：秒を更新する
				mVibrate_text_minute.setText(util_int_digit_two_to_String(minute));
				tpd_vibrate_time.setTitle(util_int_digit_two_to_String(minute) + ":" + util_int_digit_two_to_String(second));
				
			}
		});
		
		//分のマイナスボタンを押したときのイベント
		mVibrate_btn_minus_minute.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(minute<=0) {
					minute=59;
				} else {
					minute--;
				}
				//TimePickerDialogの分のTextViewに変更したminuteを表示し、タイトルの　分：秒を更新する
				mVibrate_text_minute.setText(util_int_digit_two_to_String(minute));
				tpd_vibrate_time.setTitle(util_int_digit_two_to_String(minute) + ":" + util_int_digit_two_to_String(second));
			}
		});
		
		//秒のプラスボタンを押したときのイベント
		mVibrate_btn_plus_second.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(second>=59) {
					second=0;
				} else {
					second++;
				}
				//TimePickerDialogの分のTextViewに変更したsecondを表示し、タイトルの　分：秒を更新する
				mVibrate_text_second.setText(util_int_digit_two_to_String(second));
				tpd_vibrate_time.setTitle(util_int_digit_two_to_String(minute) + ":" + util_int_digit_two_to_String(second));
			}
		});
		
		//秒のマイナスボタンを押したときのイベント
		mVibrate_btn_minus_second.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(second<=0) {
					second=59;
				} else {
					second--;
				}
				//TimePickerDialogの分のTextViewに変更したsecondを表示し、タイトルの　分：秒を更新する
				mVibrate_text_second.setText(util_int_digit_two_to_String(second));
				tpd_vibrate_time.setTitle(util_int_digit_two_to_String(minute) + ":" + util_int_digit_two_to_String(second));
			}
		});
		

		

	}
	
	//二桁までのInteger型を引数に渡し、一桁だったら二桁に変更しString型で返し、二桁だったらそのままString型で返す
	private String util_int_digit_two_to_String(Integer digit_two) {
		String digit_two_String = String.valueOf(digit_two);
		if(digit_two_String.length()==1) {
			return "0" + digit_two_String;
		} else {
			return digit_two_String;
		}
		
	}
	
	
	
    //アラートダイアログの表示
    //Settingクラスでも使用する
    public void showDialog(String title, String message) {
    	AlertDialog.Builder dialog = new AlertDialog.Builder(this);
    	dialog.setTitle(title);
    	dialog.setMessage(message);
    	dialog.setPositiveButton("確認", null);
    	dialog.show();
    	
    }
    
    @Override
    protected void onPause() {
    	super.onPause();
    }
	
	@Override
	public void onDismissScreen(Ad arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onFailedToReceiveAd(Ad arg0, ErrorCode arg1) {
		// TODO Auto-generated method stub
		Log.d("AdMob", "失敗");
	    arg0.stopLoading();
	    arg0.loadAd(new AdRequest());
	}

	@Override
	public void onLeaveApplication(Ad arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPresentScreen(Ad arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onReceiveAd(Ad arg0) {
		// TODO Auto-generated method stub
		Log.d("AdMob", "成功");
	}

}

