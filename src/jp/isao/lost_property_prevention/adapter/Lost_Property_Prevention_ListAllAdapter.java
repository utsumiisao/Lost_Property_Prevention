package jp.isao.lost_property_prevention.adapter;

import java.util.ArrayList;
import java.util.List;

import jp.isao.lost_property_prevention.R;
import jp.isao.lost_property_prevention.activity.Location_Check_Activity;
import jp.isao.lost_property_prevention.activity.Main_Activity;
import jp.isao.lost_property_prevention.data.Lost_Property_Prevention_Data;
import jp.isao.lost_property_prevention.data.Start_end_time_data;
import jp.isao.lost_property_prevention.db.Lost_Property_Prevention_Dao;
import jp.isao.lost_property_prevention.receiver.GetAwayHomeReceiver;
import jp.isao.lost_property_prevention.service.GetAwayHomeService;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

public class Lost_Property_Prevention_ListAllAdapter extends ArrayAdapter<Lost_Property_Prevention_Data> {
	//インフレートするため(LayoutInflater)
	private LayoutInflater mInflater;
	//Context
	private Context mContext;
	//レシーバ用のAlarmManagerを起動するFlag
	private boolean Flag_AlarmManager_Run = false;
	//db Dao
	Lost_Property_Prevention_Dao lppdao;
	//開始時間と終了時間保存用　ArrayList
	ArrayList<Start_end_time_data> getAllList_start_end_time = new ArrayList();
	//基準となる開始、終了時間保存用 ArrayList
	ArrayList<Start_end_time_data> getBasicList_start_end_time = new ArrayList();
	//時間が重複していないかチェックするフラグ
	boolean check_Gps_Schedule_Time_Overlap_Flag = false;
	
	//開始時間と終了時間保存用クラス
	Start_end_time_data setd;
	//基準となる開始、終了時間　保存クラス
	Start_end_time_data setd_basic;
	//対象となる開始、終了時間　保存クラス
	Start_end_time_data setd_target;
	
	
	
	private ArrayList<Lost_Property_Prevention_Data> arrayListLppd;
	//今タップしているリストのlppd
	Lost_Property_Prevention_Data mylppd;
	//dbの検索を行い、idが一致したときのlppd
	Lost_Property_Prevention_Data nowMylppd;
	//今実行されているサービスのrowId
	int nowMyRowId = 0;
	//
	boolean run_service_Flag;
	
	
	//コンストラクタ
	public Lost_Property_Prevention_ListAllAdapter(Context context, List<Lost_Property_Prevention_Data> objects) {
		this(context, 0, objects);
		mContext = context;
	}
	
	//コンストラクタ
	public Lost_Property_Prevention_ListAllAdapter(Context context, int resourceId, List<Lost_Property_Prevention_Data> objects) {
		super(context, resourceId, objects);
		mContext = context;
		mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		final Lost_Property_Prevention_Data lppd = getItem(position);
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.gps_schedule_list_item, null);
			
			holder = new ViewHolder();
			//ViewHolderにインスタンスを保持して置く。
			holder.list_set_start_time = (TextView)convertView.findViewById(R.id.list_set_start_time);
			holder.list_set_end_time = (TextView)convertView.findViewById(R.id.list_set_end_time);
			holder.list_run_check_box = (CheckBox)convertView.findViewById(R.id.list_run_check_box);
			holder.list_location_check_btn = (Button)convertView.findViewById(R.id.list_location_check_btn);
			holder.list_run_day_of_week_text = (TextView)convertView.findViewById(R.id.list_run_day_or_week_text);
			
			convertView.setTag(holder);
			
		} else {
			holder = (ViewHolder)convertView.getTag();
		}
		
		if (lppd != null) {
			
			Log.d("-----アダプタ--------", "-----------");
			//GPS開始時間セット
			holder.list_set_start_time.setText(lppd.getStarttime());
			//GPS終了時間セット
			//GPS開始時刻 (時間:分) の時間だけ取り出し、int型で保存
			int starttime_hour_int = Integer.parseInt(lppd.getStarttime().substring(0, 2));
			//GPS開始時刻 (時間:分) の分だけ取り出し、String型で保存
			String starttime_minute_String = (String)lppd.getStarttime().substring(3, 5);
			
			//GPS実行時間 (時間) を取り出し、int型で保存
			//index番号 0 (1時間 == 0)で保存されているので + 1する
			int runtime_hour_int = Integer.parseInt((String)lppd.getRuntime()) + 1;
			//GPS終了時間を算出
			int endtime_hour_int = starttime_hour_int + runtime_hour_int;
			String endtime_hour_minute_String =util_int_digit_two_to_String(get24hour(endtime_hour_int)) + ":" + starttime_minute_String;
			//GPS終了時間セット
			holder.list_set_end_time.setText(endtime_hour_minute_String);
			
			holder.list_run_check_box.setChecked(Boolean.valueOf(lppd.getRun_service()));
			
			//リストに実行する曜日をセットする
			holder.list_run_day_of_week_text.setText(make_string_run_day_of_week(lppd));	

			//位置ボタンのイベントリスナー登録
			holder.list_location_check_btn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(final View v) {
					
					//位置確認画面に遷移する (Location_Check_Activity)
					Intent intent = new Intent(mContext, Location_Check_Activity.class);
					intent.putExtra("Latitude_check", lppd.getLatitude());
					intent.putExtra("Longitude_check", lppd.getLongitude());
					mContext.startActivity(intent);
					
				}
			});

			

			//ArrayList<Lost_Property_Prevention_Data>に保存されているサービス実行中フラグを見て、checkboxをtrue or false　にする。
			if(lppd.getRun_service().equals("false")) {
				holder.list_run_check_box.setChecked(false);
			} else if (lppd.getRun_service().equals("true")) {
				holder.list_run_check_box.setChecked(true);
			}
			

			
			holder.list_run_check_box.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
		
					//db dao クラス　インスタンス生成
					lppdao = new Lost_Property_Prevention_Dao(mContext);
					//レシーバが呼ばれたら毎回dbを読み込み、ArrayListに保存
					arrayListLppd = lppdao.selectLost_Property_Prevention_Data();
					//今実行中のサービスを検索し、実行中のサービスのrowIdを取得
					for (int i = 0; i < arrayListLppd.size(); i++) {
						mylppd = arrayListLppd.get(i);
						Log.d("************ lppd length *************", String.valueOf( arrayListLppd.size()));
						Log.d("************ lppd getRowId *************", String.valueOf(lppd.getRowId()));
						Log.d("************ myLppd getRowId *************", String.valueOf(mylppd.getRowId()));
						Log.d("************ getMonday *************", String.valueOf(mylppd.getMonday()));
						Log.d("************ getTuesday *************", String.valueOf(mylppd.getTuesday()));
						Log.d("************ getWednesday *************", String.valueOf(mylppd.getWednesday()));
						Log.d("************ getThursday *************", String.valueOf(mylppd.getThursday()));
						Log.d("************ getFriday *************", String.valueOf(mylppd.getFriday()));
						Log.d("************ getSaturday *************", String.valueOf(mylppd.getSaturday()));
						Log.d("************ getSunday *************", String.valueOf(mylppd.getSunday()));
						Log.d("************ getVib_runtime *************", String.valueOf(mylppd.getVib_runtime()));
						Log.d("************ getRun_service *************", String.valueOf(mylppd.getRun_service()));
						Log.d("************ getNow_running_service *************", String.valueOf(mylppd.getNow_running_service()));

						Log.d("************ i *************", String.valueOf(i));
						
						//今実行中のサービス
						if(mylppd.getNow_running_service().equals("true")) {
							nowMyRowId = mylppd.getRowId();
						}
						
						if(mylppd.getRun_service().equals("true")) {
							//GPS開始時刻 (時間:分) の時間だけ取り出し、int型で保存
							int starttime_hour_int = Integer.parseInt(mylppd.getStarttime().substring(0, 2));
							//開始分のint型を取り出す
							int starttime_minute_int = Integer.parseInt(mylppd.getStarttime().substring(3,5));
							//index番号 0 (1時間 == 0)で保存されているので + 1する
							int runtime_hour_int = Integer.parseInt((String)mylppd.getRuntime()) + 1;
							//GPS終了時間を算出
							int endtime_hour_int = starttime_hour_int + runtime_hour_int;
							//実行曜日を取得
							if(mylppd.getSunday().equals("true")) {
								//開始時間と、終了時間の長さを取得し、setdに保存したのをArrayListにセットする
								//新しい値を入れるときは必ずインスタンスを生成してからセットする。　同じインスタンスだと新しく値をセットすると前にセットした値が上書きされる
								setd = new Start_end_time_data();
								setd.setStart_time(day_Of_Week_Time_Value(0, starttime_hour_int, starttime_minute_int));
								setd.setEnd_time(day_Of_Week_Time_Value(0, endtime_hour_int, starttime_minute_int));
								getAllList_start_end_time.add(setd);
							}
							if(mylppd.getMonday().equals("true")) {
								setd = new Start_end_time_data();
								setd.setStart_time(day_Of_Week_Time_Value(1, starttime_hour_int, starttime_minute_int));
								setd.setEnd_time(day_Of_Week_Time_Value(1, endtime_hour_int, starttime_minute_int));
								getAllList_start_end_time.add(setd);
							}
							if(mylppd.getTuesday().equals("true")) {
								setd = new Start_end_time_data();
								setd.setStart_time(day_Of_Week_Time_Value(2, starttime_hour_int, starttime_minute_int));
								setd.setEnd_time(day_Of_Week_Time_Value(2, endtime_hour_int, starttime_minute_int));
								getAllList_start_end_time.add(setd);
							}
							if(mylppd.getWednesday().equals("true")) {
								setd = new Start_end_time_data();
								setd.setStart_time(day_Of_Week_Time_Value(3, starttime_hour_int, starttime_minute_int));
								setd.setEnd_time(day_Of_Week_Time_Value(3, endtime_hour_int, starttime_minute_int));
								getAllList_start_end_time.add(setd);
							}
							if(mylppd.getThursday().equals("true")) {
								setd = new Start_end_time_data();
								setd.setStart_time(day_Of_Week_Time_Value(4, starttime_hour_int, starttime_minute_int));
								setd.setEnd_time(day_Of_Week_Time_Value(4, endtime_hour_int, starttime_minute_int));
								getAllList_start_end_time.add(setd);
							}
							if(mylppd.getFriday().equals("true")) {
								setd = new Start_end_time_data();
								setd.setStart_time(day_Of_Week_Time_Value(5, starttime_hour_int, starttime_minute_int));
								setd.setEnd_time(day_Of_Week_Time_Value(5, endtime_hour_int, starttime_minute_int));
								getAllList_start_end_time.add(setd);
							}
							if(mylppd.getSaturday().equals("true")) {
								setd = new Start_end_time_data();
								setd.setStart_time(day_Of_Week_Time_Value(6, starttime_hour_int, starttime_minute_int));
								setd.setEnd_time(day_Of_Week_Time_Value(6, endtime_hour_int, starttime_minute_int));
								getAllList_start_end_time.add(setd);
							}
						}
						
						//今リストで選択されているidを検索する
						//MainActivityを読み込んでいないので、lppdのデータが最新のデータでない（Reciver内でdbが更新されているのが現在反映されていない）
						if(mylppd.getRowId() == lppd.getRowId()) {
							//リスト選択した最新のdbのデータが入っている
							nowMylppd = mylppd;
						}
						
					}
					
					//CheckBoxの状態を取得する
					boolean checked = holder.list_run_check_box.isChecked();
					//家から離れたことを教えるサービス
					Intent intent = new Intent(mContext, GetAwayHomeReceiver.class);

					
					//PendingIntent.FLAG_UPDATE_CURRENTは今実行中のServiceに新しいputExtraの情報を上書きするための定数
					PendingIntent sender = PendingIntent.getBroadcast(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
					AlarmManager am = (AlarmManager) mContext.getSystemService(mContext.ALARM_SERVICE);
					
					lppdao = new Lost_Property_Prevention_Dao(mContext);
					
					//CheckBox == true
					if(checked==true) {
						//設定を実行できるかチェックする段階なので一度実行用チェックボックス false にする
						holder.list_run_check_box.setChecked(false);
						
						//GPS開始時刻 (時間:分) の時間だけ取り出し、int型で保存
						int starttime_hour_int = Integer.parseInt(nowMylppd.getStarttime().substring(0, 2));
						//開始分のint型を取り出す
						int starttime_minute_int = Integer.parseInt(nowMylppd.getStarttime().substring(3,5));
						//index番号 0 (1時間 == 0)で保存されているので + 1する
						int runtime_hour_int = Integer.parseInt((String)nowMylppd.getRuntime()) + 1;
						//GPS終了時間を算出
						int endtime_hour_int = starttime_hour_int + runtime_hour_int;
						Log.d("************ starttime_hour_int *************", String.valueOf(starttime_hour_int));
						Log.d("************ starttime_minute_int *************", String.valueOf(starttime_minute_int));
						Log.d("************ endtime_hour_int *************", String.valueOf(endtime_hour_int));
						if(nowMylppd.getSunday().equals("true")) {
							//開始時間と、終了時間の長さを取得し、setdに保存したのをArrayListにセットする
							setd = new Start_end_time_data();
							setd.setStart_time(day_Of_Week_Time_Value(0, starttime_hour_int, starttime_minute_int));
							setd.setEnd_time(day_Of_Week_Time_Value(0, endtime_hour_int, starttime_minute_int));
							
							getBasicList_start_end_time.add(setd);
						}
						if(nowMylppd.getMonday().equals("true")) {
							//開始時間と、終了時間の長さを取得し、setdに保存したのをArrayListにセットする
							setd = new Start_end_time_data();
							setd.setStart_time(day_Of_Week_Time_Value(1, starttime_hour_int, starttime_minute_int));
							setd.setEnd_time(day_Of_Week_Time_Value(1, endtime_hour_int, starttime_minute_int));
							getBasicList_start_end_time.add(setd);
						}
						if(nowMylppd.getTuesday().equals("true")) {
							//開始時間と、終了時間の長さを取得し、setdに保存したのをArrayListにセットする
							setd = new Start_end_time_data();
							setd.setStart_time(day_Of_Week_Time_Value(2, starttime_hour_int, starttime_minute_int));
							setd.setEnd_time(day_Of_Week_Time_Value(2, endtime_hour_int, starttime_minute_int));
							getBasicList_start_end_time.add(setd);
						}
						if(nowMylppd.getWednesday().equals("true")) {
							//開始時間と、終了時間の長さを取得し、setdに保存したのをArrayListにセットする
							setd = new Start_end_time_data();
							setd.setStart_time(day_Of_Week_Time_Value(3, starttime_hour_int, starttime_minute_int));
							setd.setEnd_time(day_Of_Week_Time_Value(3, endtime_hour_int, starttime_minute_int));
							getBasicList_start_end_time.add(setd);
						}
						if(nowMylppd.getThursday().equals("true")) {
							//開始時間と、終了時間の長さを取得し、setdに保存したのをArrayListにセットする
							setd = new Start_end_time_data();
							setd.setStart_time(day_Of_Week_Time_Value(4, starttime_hour_int, starttime_minute_int));
							setd.setEnd_time(day_Of_Week_Time_Value(4, endtime_hour_int, starttime_minute_int));
							getBasicList_start_end_time.add(setd);
						}
						if(nowMylppd.getFriday().equals("true")) {
							//開始時間と、終了時間の長さを取得し、setdに保存したのをArrayListにセットする
							setd = new Start_end_time_data();						
							setd.setStart_time(day_Of_Week_Time_Value(5, starttime_hour_int, starttime_minute_int));
							setd.setEnd_time(day_Of_Week_Time_Value(5, endtime_hour_int, starttime_minute_int));
							getBasicList_start_end_time.add(setd);
						}
						if(nowMylppd.getSaturday().equals("true")) {
							//開始時間と、終了時間の長さを取得し、setdに保存したのをArrayListにセットする
							setd = new Start_end_time_data();
							setd.setStart_time(day_Of_Week_Time_Value(6, starttime_hour_int, starttime_minute_int));
							setd.setEnd_time(day_Of_Week_Time_Value(6, endtime_hour_int, starttime_minute_int));
							getBasicList_start_end_time.add(setd);
						}
						
						for (int i = 0; i < getBasicList_start_end_time.size(); i++) {
							//保存された基準となる開始、終了時間　データ
							setd_basic = getBasicList_start_end_time.get(i);
							for (int j = 0; j < getAllList_start_end_time.size(); j++) {
								//保存された対象となる開始、終了時間　データ
								setd_target = getAllList_start_end_time.get(j);
								//基準となる開始時間と終了時間が対象となる開始、終了時間と重複していないか調べる。重複していれば trueがかえってくる
								if(check_Gps_Schedule_Time_Overlap(setd_basic.getStart_time(), setd_basic.getEnd_time(), setd_target.getStart_time(), setd_target.getEnd_time()) == true) {
									check_Gps_Schedule_Time_Overlap_Flag=true;
								}
								
							}
						}
						
						//選択したリストと他のリストの時間帯が重なっている場合
						if(check_Gps_Schedule_Time_Overlap_Flag==true) {
							showDialog("この設定を実行できません", "GPS実行時間が他の設定と重複しています。GPS実行時間をもう一度確認して下さい。");
							//フラグを初期値に戻す
							check_Gps_Schedule_Time_Overlap_Flag=false;
							
						} else {
							//チェックが済み実行できる段階なのでチェックボックスをtrueにする
							holder.list_run_check_box.setChecked(true);
							//もしどのリストの実行チェックボックスがtrueでなければ
							if(run_Service_Check()==false) {
								nowMylppd.setRun_service("true");
								//通知の確認ボタンを押した場合、setNow_running_service()がtrueのまま(実行チェックボックスがtrueのとき再び
								//サービスが起動しないようにするため)なので,実行時間内だったらサービスが起動するようにfalseにセットする
								//サービス実行中フラグを初期化する
								nowMylppd.setNow_running_service("false");
								lppdao.updateLost_Property_Prevention_Data(nowMylppd, nowMylppd.getRowId());
								
								//AlarmManagerを開始する。5分おきにPendingIntentが発行される
								am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, 1000, 1000 * 60 * 5, sender);
								
								//通知でバックグラウンドでアプリが実行されていることを知らせる
								notification_receiver(mContext);
								
								
								ArrayList<Lost_Property_Prevention_Data> reload_ArrayList_lppd = lppdao.selectLost_Property_Prevention_Data();
								clear();
								for (int count = 0; count < reload_ArrayList_lppd.size(); count++) {
									add(reload_ArrayList_lppd.get(count));
								}
								
//								//設定リストのチェックボックスの状態を最新の状態にするため、再びリストをアダプタにセットし初期化する
//								Intent intent_main_activity = new Intent(mContext, Main_Activity.class);
//								//Main_Activityにインテントを発行
//								mContext.startActivity(intent_main_activity);
							} else {
								nowMylppd.setRun_service("true");
								//サービス実行中フラグを初期化する
								nowMylppd.setNow_running_service("false");
								lppdao.updateLost_Property_Prevention_Data(nowMylppd, nowMylppd.getRowId());
								//通知でバックグラウンドでアプリが実行されていることを知らせる
								notification_receiver(mContext);
								mContext.sendBroadcast(intent);
								
								ArrayList<Lost_Property_Prevention_Data> reload_ArrayList_lppd = lppdao.selectLost_Property_Prevention_Data();
								clear();
								for (int count = 0; count < reload_ArrayList_lppd.size(); count++) {
									add(reload_ArrayList_lppd.get(count));
								}
								
								
//								//設定リストのチェックボックスの状態を最新の状態にするため、再びリストをアダプタにセットし初期化する
//								Intent intent_main_activity = new Intent(mContext, Main_Activity.class);
//								//Main_Activityにインテントを発行
//								mContext.startActivity(intent_main_activity);
							}
						}
	
						//CheckBox == false
						} else {
														
							//もし現在サービスが実行中であれば   nowMyRowIdは今実行中のRowId
							if(nowMyRowId==lppd.getRowId()) {
								nowMylppd.setNow_running_service("false");
								//レシーバにインテントが発行されても、run_serviceがfalseなので、サービスが実行されない
								nowMylppd.setRun_service("false");
								lppdao.updateLost_Property_Prevention_Data(nowMylppd, nowMylppd.getRowId());
								//NotifycationManager取得
								NotificationManager nowRunService_nm = (NotificationManager)mContext.getSystemService(mContext.NOTIFICATION_SERVICE);
								//通知削除
								nowRunService_nm.cancel(nowMylppd.getRowId());
								//実行中のサービスを停止する
								Intent i = new Intent(mContext, GetAwayHomeService.class);
								mContext.stopService(i);
								
								ArrayList<Lost_Property_Prevention_Data> reload_ArrayList_lppd = lppdao.selectLost_Property_Prevention_Data();
								clear();
								for (int count = 0; count < reload_ArrayList_lppd.size(); count++) {
									add(reload_ArrayList_lppd.get(count));
								}
								
//								//設定リストのチェックボックスの状態を最新の状態にするため、再びリストをアダプタにセットし初期化する
//								Intent intent_main_activity = new Intent(mContext, Main_Activity.class);
//								//Main_Activityにインテントを発行
//								mContext.startActivity(intent_main_activity);
								
							} else {
								nowMylppd.setRun_service("false");
								lppdao.updateLost_Property_Prevention_Data(nowMylppd, nowMylppd.getRowId());
								
								
								ArrayList<Lost_Property_Prevention_Data> reload_ArrayList_lppd = lppdao.selectLost_Property_Prevention_Data();
								clear();
								for (int count = 0; count < reload_ArrayList_lppd.size(); count++) {
									add(reload_ArrayList_lppd.get(count));
								}
								
//								//設定リストのチェックボックスの状態を最新の状態にするため、再びリストをアダプタにセットし初期化する
//								Intent intent_main_activity = new Intent(mContext, Main_Activity.class);
//								//Main_Activityにインテントを発行
//								mContext.startActivity(intent_main_activity);
							}
							//リストにある全てのチェックボックスがfalseだったら
							if(run_Service_Check()==false) {
								//AlarmManagerを停止する
								am.cancel(sender);
								//NotifycationManager取得
								NotificationManager mgr = (NotificationManager)mContext.getSystemService(mContext.NOTIFICATION_SERVICE);
								//通知削除
								mgr.cancel(GetAwayHomeReceiver.noti_id);
								
								
								ArrayList<Lost_Property_Prevention_Data> reload_ArrayList_lppd = lppdao.selectLost_Property_Prevention_Data();
								clear();
								for (int count = 0; count < reload_ArrayList_lppd.size(); count++) {
									add(reload_ArrayList_lppd.get(count));
								}
								
//								//設定リストのチェックボックスの状態を最新の状態にするため、再びリストをアダプタにセットし初期化する
//								Intent intent_main_activity = new Intent(mContext, Main_Activity.class);
//								//Main_Activityにインテントを発行
//								mContext.startActivity(intent_main_activity);
							}
	
						}
					
					//ArrayListをクリアしている
					getAllList_start_end_time.clear();
					getBasicList_start_end_time.clear();
					
				}
			});
			
			
		}
		
		return convertView;
	}

	
	//曜日、時間を取得し、一週間を数値化する
	//終了時刻のhourは 24以上でもOK
	private int day_Of_Week_Time_Value(int day_of_week, int hour, int minute) {
		Log.d("************ day_of_week *************", String.valueOf(day_of_week));
		Log.d("************ hour *************", String.valueOf(hour));
		Log.d("************ minute *************", String.valueOf(minute));
		
		//24時以上だったら
		if(hour>23) {
			hour = hour-24;
			//曜日 + 1
			day_of_week = day_of_week+1;
			//土曜を超えていたら,0に戻し日曜にする
			if(day_of_week>6) {
				day_of_week=0;
			}
		}
		Log.d("************ 数値化 *************", String.valueOf((24*60*day_of_week) + (hour*60 + minute)));
		//24時間 * 曜日（1~7)-1 + 時間(0~23) * 60分 + minute 分 
		return (24*60*day_of_week) + (hour*60 + minute);
	}
	
	
	//一週間の曜日と時間が他の設定値の曜日と時間にかぶっていないか調べる
	private boolean check_Gps_Schedule_Time_Overlap(int basic_start_time, int basic_end_time, int target_start_time, int target_end_time) {
		Log.d("************ basic_start_time *************", String.valueOf(basic_start_time));
		Log.d("************ basic_end_time *************", String.valueOf(basic_end_time));
		Log.d("************ target_start_time *************", String.valueOf(target_start_time));
		Log.d("************ target_end_time *************", String.valueOf(target_end_time));
		
		//土曜から日曜にまたがっている場合
		if(basic_start_time > basic_end_time) {
			//元となるスタート時間より、ターゲットとなるスタート時間、エンド時間の方が大きい
			// 
			//	basic_start	   target_end    target_start				max値
			//		|--------------|------------|-----------------------|
			if(basic_start_time <= target_start_time || basic_start_time <= target_end_time) {
				return true;
			}
			//　元となるエンド時間より、ターゲットなるスタート時間、エンド時間の方が小さい
			//
			//		start値		target_end		target_start			basic_end
			//		|----------------|-------------|-----------------------|
			if(basic_end_time >= target_start_time || basic_end_time >= target_end_time) {
				return true;
			}
			//	元となるスタート、エンド時間の幅より、ターゲットとなるスタート、エンド時間の方が大きい場合
			//
			//		basic_end										basic_start
			//		-----|											    |----
			//		min														max
			//      |-------------------------------------------------------|
			//				target_end						target_start
			//		------------|								|------------
			//
			//  (max値 - ターゲットのスタート時間)=頭の時間を取得     +  (ターゲットの終了時間)    = ターゲットの時間幅を取する
			//  それが3時間より小さい場合、もし、元となるスタート時間より、ターゲットの方が小さく、かつ元となるエンド時間が、ターゲットのエンド時間の方が大きかったら、時間が重なっているのでtrueを返す
			if(((10079 - target_start_time) + (target_end_time)) <= 3*60) {
				if(target_start_time <= basic_start_time && target_end_time >= basic_end_time) {
					return true;
				}
			}
			
			//土曜から日曜にまたがっていないとき
		} else {
			// ターゲットのスタート時間が、元となるスタート時間〜エンド時間の幅に入っている場合
			//
			//	basic_start				target_start        basic_end
			//     |-------------------------|-------------------|
			//
			if(basic_start_time <= target_start_time && basic_end_time >= target_start_time ) {
				return true;
			}
			// ターゲットのエンド時間が、元なるスタート時間〜エンド時間の幅に入っている場合
			//
			//	basic_start				target_end           basic_end
			//     |-------------------------|-------------------|
			if(basic_start_time <= target_end_time && basic_end_time >= target_end_time ) {
				return true;
			}
			//元となる、スタート時間、エンド時間の大きさより、ターゲットのスタート時間、エンド時間の方が大きい場合
			//
			//  target_start                   target_end
			//       |----------------------------|
			//		basic_start         basic_end
			//               |------------|
			if(target_start_time <= basic_start_time && target_end_time >= basic_start_time) {
				return true;
			}
		}
		Log.d("************ check_Gps_Schedule_Time_Overlap *************", String.valueOf(false));
		return false;
	}
	
    //アラートダイアログの表示
    //Settingクラスでも使用する
    public void showDialog(String title, String message) {
    	AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
    	dialog.setTitle(title);
    	dialog.setMessage(message);
    	dialog.setPositiveButton("確認", null);
    	dialog.show();
    	
    }
	
	//レシーバ用のAlarmManagerを起動するかチェックする	
	public boolean run_Service_Check() {
		Flag_AlarmManager_Run = false;

		ArrayList<Lost_Property_Prevention_Data> lppd_ArrayList = lppdao.selectLost_Property_Prevention_Data();
		//取得したArrayList<Lost_Property_Prevention_Data>からgetRun_Service()を調べ、レシーバ(runService)が起動されているか確認する
		for (int i = 0; i < lppd_ArrayList.size(); i++) {
			Lost_Property_Prevention_Data lppd_getRunService = lppd_ArrayList.get(i);
			if(lppd_getRunService.getRun_service().equals("true")) {
				//どれかレシーバが起動されていればフラグtrueにしてレシーバにIntentを発行しない
				return Flag_AlarmManager_Run = true;
			}
		}
		return Flag_AlarmManager_Run;
	}
	
	
	class ViewHolder {
		TextView list_set_start_time;
		TextView list_set_end_time;
		CheckBox list_run_check_box;
		Button list_location_check_btn;
		TextView list_run_day_of_week_text;
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
	
	public void notification_receiver(Context context) {
		
		NotificationManager nfm = (NotificationManager)context.getSystemService(context.NOTIFICATION_SERVICE);
		
		Notification noti = new Notification();
		noti.icon = R.drawable.run_time_icon;
		noti.tickerText = "忘れ物知らずを実行しました";
		noti.when = System.currentTimeMillis();
		//音無し
//		noti.defaults |= Notification.DEFAULT_SOUND;
		
		//通知のリストを押したらMainActivityに移動する
		Intent intentForeground = new Intent(context, Main_Activity.class);
		//通知ボタンを押したときどこに行くか
		PendingIntent pi = PendingIntent.getActivity(context, 0, intentForeground, 0);
    	//通知の一覧画面をセットするメソッド
		noti.setLatestEventInfo(context, "忘れ物知らず", "起動中", pi);
		
		//*ポイント 
		//フラグ設定しないと通知領域に表示されてしまう
		//通知領域に表示するのが目的なら flag の設定は不要
		noti.flags = noti.flags | noti.FLAG_NO_CLEAR | noti.FLAG_ONGOING_EVENT;
		noti.number = 0;
		noti.contentIntent = pi;
		
		//通知発行 通知のidは　dbの_id　と紐付け
		nfm.notify(GetAwayHomeReceiver.noti_id, noti);
		
	}
	
	private int get24hour(int i) {
		if(i>23) {
			return i-24;
		}
		return i;
	}
	
	private StringBuffer make_string_run_day_of_week(Lost_Property_Prevention_Data lppd) {
		StringBuffer sbf = new StringBuffer();
		
		if(lppd.getMonday().equals("true")) {
			sbf.append("月　");
		}
		if(lppd.getTuesday().equals("true")) {
			sbf.append("火　");
		}
		if(lppd.getWednesday().equals("true")) {
			sbf.append("水　");
		}
		if(lppd.getThursday().equals("true")) {
			sbf.append("木　");
		}
		if(lppd.getFriday().equals("true")) {
			sbf.append("金　");
		}
		if(lppd.getSaturday().equals("true")) {
			sbf.append("土　");
		}
		if(lppd.getSunday().equals("true")) {
			sbf.append("日　");
		}
		return sbf;
	}
	
	
	
	
}
