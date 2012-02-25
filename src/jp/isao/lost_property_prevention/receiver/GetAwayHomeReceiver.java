package jp.isao.lost_property_prevention.receiver;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import jp.isao.lost_property_prevention.GetAwayHomeBindService;
import jp.isao.lost_property_prevention.R;
import jp.isao.lost_property_prevention.activity.Main_Activity;
import jp.isao.lost_property_prevention.adapter.Lost_Property_Prevention_ListAllAdapter;
import jp.isao.lost_property_prevention.data.Lost_Property_Prevention_Data;
import jp.isao.lost_property_prevention.db.Lost_Property_Prevention_Dao;
import jp.isao.lost_property_prevention.service.GetAwayHomeService;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

public class GetAwayHomeReceiver extends BroadcastReceiver {

	private ArrayList<Lost_Property_Prevention_Data> arrayListLppd;
	public static boolean run_service = false;
	private int now_hour;
	private int now_minute;
	private int today_day_of_week;
	private int starttime_hour_int;
	private int starttime_minute_int;
	private int endtime_hour_int;
	private Intent intent_service;
	public static int noti_id = 1000000000;
	
	private static int nowRunServiceRowId;
	
	private int runtime_hour_int;
	
	private Context mContext;
	private Lost_Property_Prevention_Data lppd_getRunService;
	private Lost_Property_Prevention_Data lppd_getRunServiceRunningServiceCheck;
	private NotificationManager nfm;
	
	//db Dao
	Lost_Property_Prevention_Dao lppdao;
	
	private boolean flag_Now_running_service=false;
	
	private int nowRuningServiceRowId;
	private int rowId=0;
	

	
	
	public int getNowRuningServiceRowId(Context context) {
		//プリファレンス取得
		SharedPreferences pref = context.getSharedPreferences("PreferenceTest", context.MODE_PRIVATE);
		//Editor取得
		SharedPreferences.Editor  edit = pref.edit();
		//前の通知のRowIdを取得する
		nowRuningServiceRowId = pref.getInt("rowId", rowId);
		
		return nowRuningServiceRowId;
	}

	public void setNowRuningServiceRowId(int nowRuningServiceRowId) {
		this.nowRuningServiceRowId = nowRuningServiceRowId;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		
		mContext=context;
		//db dao クラス　インスタンス生成
		lppdao = new Lost_Property_Prevention_Dao(mContext);
		//レシーバが呼ばれたら毎回dbを読み込み、ArrayListに保存
		arrayListLppd = lppdao.selectLost_Property_Prevention_Data();
		
		nfm = (NotificationManager)context.getSystemService(context.NOTIFICATION_SERVICE);
		
		//データが更新されたらArrayListが送られてくるので、前のArrayListを上書きして更新する。
		//AlarmManagerで定期的に呼ばれるときはArrayListは付加されてなく、arryListLppdはnullなのでそのときは前のデータを
		//上書きしないようにしている

		
		Calendar calendar = Calendar.getInstance();
		now_hour = calendar.get(Calendar.HOUR_OF_DAY);
		now_minute = calendar.get(Calendar.MINUTE);
		today_day_of_week = calendar.get(Calendar.DAY_OF_WEEK);
		
		//dbのデータが取得できたかチェック
		if(arrayListLppd!=null) {
			//チェックボックスの状態と実行する曜日と、実行時間を調べ、実行する時間だったらServiceにインテントを発行する
			service_Check_Box();
		}
		
		//再起動したときに呼ばれる
		if(Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
			Log.d("************ ACTION_BOOT_COMPLETED *************", "レシーバー　　　　");
			boolean run_service_Flag = false;
			for (int i = 0; i < arrayListLppd.size(); i++) {
				lppd_getRunService = arrayListLppd.get(i);

				Log.d("************ setNow_running_service *************", String.valueOf(lppd_getRunService.getNow_running_service()));
				//再起動したときに実行中のサービスがあった場合は、初期化しfalseにする
				if(lppd_getRunService.getNow_running_service().equals("true")) {
					lppd_getRunService.setNow_running_service("false");
					Log.d("************ if文　setNow_running_service *************", String.valueOf(lppd_getRunService.getNow_running_service()));
					//DBを更新
					lppdao.updateLost_Property_Prevention_Data(lppd_getRunService, lppd_getRunService.getRowId());
				}
				
				if(lppd_getRunService.getRun_service().equals("true") && run_service_Flag==false) {
					//家から離れたことを教えるサービス
					Intent intent_receiver = new Intent(mContext, GetAwayHomeReceiver.class);
					
					//PendingIntent.FLAG_UPDATE_CURRENTは今実行中のServiceに新しいputExtraの情報を上書きするための定数
					PendingIntent sender = PendingIntent.getBroadcast(mContext, 0, intent_receiver, PendingIntent.FLAG_UPDATE_CURRENT);
					AlarmManager am = (AlarmManager) mContext.getSystemService(mContext.ALARM_SERVICE);
					
					//AlarmManagerを開始する。5分おきにPendingIntentが発行される
					am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, 1000, 1000 * 60 * 1, sender);
					//通知発行
					notification_receiver(mContext);
					//実行中のサービスを取得した場合、一度だけAlarmManagerと、レシーバー実行用の通知を発行する
					run_service_Flag=true;
					Log.d("************ ACTION_BOOT_COMPLETED *************", "レシーバー　サービス true　　　　");
				}
			}
			
		}

	}
	
	//リストのチェックボックスがtrueになっていれば実行時刻をチェックし、サービスを起動するか確認する	
	private boolean service_Check_Box() {
		boolean flag_AlarmManager_Run  = false;

		//取得したArrayList<Lost_Property_Prevention_Data>からgetRun_Service()を調べ、レシーバ(runService)が起動されているか確認する
		for (int i = 0; i < arrayListLppd.size(); i++) {
			lppd_getRunService = arrayListLppd.get(i);
			

			if(lppd_getRunService.getRun_service().equals("true")) {
				
				//GPS開始時刻 (時間:分) の時間だけ取り出し、int型で保存
				starttime_hour_int = Integer.parseInt(lppd_getRunService.getStarttime().substring(0, 2));
				//GPS開始時刻 (時間:分) の分だけ取り出し、int型で保存
				starttime_minute_int = Integer.parseInt(lppd_getRunService.getStarttime().substring(3, 5));
				
				//GPS実行時間 (時間) を取り出し、int型で保存
				runtime_hour_int = Integer.parseInt((String)lppd_getRunService.getRuntime()) + 1;
				//GPS終了時間を算出
				//runtime_hour_intのindex 0 のため 1足している
				endtime_hour_int = starttime_hour_int + runtime_hour_int;
				//実行する曜日と、時間を調べ、実行する時間だったらServiceにインテントを発行する
				runTimeCheck();	

				flag_AlarmManager_Run = true;
			}
			//実行中のサービスがあるか調べる
			if(lppd_getRunService.getNow_running_service().equals("true")) {
				flag_Now_running_service=true;
			}
		}
		return flag_AlarmManager_Run;
	}
	
	//ArrayListから実行する曜日を調べる
	private void runTimeCheck() {
		switch (today_day_of_week) {
		case Calendar.MONDAY:
			//Objectの型はequals()で比較する
			if(lppd_getRunService.getMonday().equals("true")) {
				if(runServiceTimeCheck() == true) {
					control_run_service(mContext);
				} else {
					stop_run_service(mContext);
				}

			}
			
			break;
		
		case Calendar.TUESDAY:
			if(lppd_getRunService.getTuesday().equals("true")) {

				if(runServiceTimeCheck() == true) {
					control_run_service(mContext);
				} else {
					stop_run_service(mContext);
				}

			}
			break;
			
		case Calendar.WEDNESDAY:
			if(lppd_getRunService.getWednesday().equals("true")) {
				if(runServiceTimeCheck() == true) {
					control_run_service(mContext);
				} else {
					stop_run_service(mContext);
				}

			}
			break;
			
		case Calendar.THURSDAY:
			if(lppd_getRunService.getThursday().equals("true")) {
				if(runServiceTimeCheck() == true) {
					control_run_service(mContext);
				} else {
					stop_run_service(mContext);
				}

			}
			break;
			
		case Calendar.FRIDAY:
			if(lppd_getRunService.getFriday().equals("true")) {
				if(runServiceTimeCheck() == true) {
					control_run_service(mContext);
				} else {
					stop_run_service(mContext);
				}

			}
			break;
			
		case Calendar.SATURDAY:
			if(lppd_getRunService.getSaturday().equals("true")) {
				if(runServiceTimeCheck() == true) {
					control_run_service(mContext);
				} else {
					stop_run_service(mContext);
				}

			}
			break;
			
		case Calendar.SUNDAY:
			if(lppd_getRunService.getSunday().equals("true")) {
				if(runServiceTimeCheck() == true) {
					control_run_service(mContext);
				} else {
					stop_run_service(mContext);
				}

			}
			break;

		default:

			break;
		}
	}
	
	
	
	private void control_run_service(Context context) {
		//設定時間を超えて、なおかつサービスが実行されていないとき
		//サービスは実行時間内に一度発行する
		Log.d("------------control_run_service----------", "RowId" + String.valueOf(lppd_getRunService.getRowId()) + " " +String.valueOf(lppd_getRunService.getNow_running_service()));
		if(lppd_getRunService.getNow_running_service().equals("false")) {
			rowId=0;
			//プリファレンス取得
			SharedPreferences pref = context.getSharedPreferences("PreferenceTest", context.MODE_PRIVATE);
			//Editor取得
			SharedPreferences.Editor  edit = pref.edit();
			//前の通知が残っている場合、プリファレンスで保存しておいたrowIdをもとに、前の通知を削除する
			//新しい通知を発行する時は、新しい通知のrowIdを保存する
			//前の通知のRowIdを取得する
			rowId = pref.getInt("rowId", rowId);
			if(rowId == 0) {
				//lppdから新しいrowIdを取得する
				//プリファレンスに保存
				Log.d("-----------rowId = 0------", String.valueOf(rowId));
				edit.putInt("rowId", lppd_getRunService.getRowId());
				Log.d("-----------lppd rowId -------", String.valueOf(lppd_getRunService.getRowId()));
		    	//必ず保存した後は最後にcommitメソッドを呼び出さないと更新されないので、注意
		    	edit.commit();
			} else {
				Log.d("--------前の通知を削除する--------", String.valueOf(rowId));
				//前の通知を削除する
				nfm.cancel(rowId);
				//lppdから新しいrowIdを取得する
				//プリファレンスに保存
				edit.putInt("rowId", lppd_getRunService.getRowId());
		    	//必ず保存した後は最後にcommitメソッドを呼び出さないと更新されないので、注意
		    	edit.commit();
			}

			//dbにsetNow_running_serviceの値をtrueにセットする
			lppd_getRunService.setNow_running_service("true");
			lppdao.updateLost_Property_Prevention_Data(lppd_getRunService, lppd_getRunService.getRowId());
			
			//Intent発行
			intent_service = new Intent(context, GetAwayHomeService.class);
			intent_service.putExtra("lppd_from_GetAwayHomeReceiver", lppd_getRunService);
			
			
			//サービスにIntent発行
			context.startService(intent_service);
		} else if(lppd_getRunService.getNow_running_service().equals("true")) {
			//一度インテントを発行するのみ
			
		}
	}
	
	private void stop_run_service(Context context) {
		if(lppd_getRunService.getNow_running_service().equals("true")) {
			//実行中のサービスが実行時間外になったとき初めて呼ばれる
			//実行時間外なのでsetNow_running_serviceをfalseにセットする
				lppd_getRunService.setNow_running_service("false");
				lppdao.updateLost_Property_Prevention_Data(lppd_getRunService, lppd_getRunService.getRowId());
				//現在実行中のサービスが無いことをDBを使用して調べる
				if(now_RunningServiceCheck()==false) {
					//GPSの実行時間をすぎてもGPSが立ち上がってる時は、サービスを停止しGPSを立ち下げる
					if(isServiceRunning(context, GetAwayHomeService.class)==true) {
						//Intent発行
						intent_service = new Intent(context, GetAwayHomeService.class);
						context.stopService(intent_service);
					}
				}
		}

			//現在実行中のサービスがあれば、GPSを停止しない
			Log.d("------------サービス　時間外------------ GetAwayHomeReceiver_325", String.valueOf(lppd_getRunService.getRowId()));
			Log.d("------------サービスが実行しているか-------- GetAwayHomeReceiver_326", String.valueOf(isServiceRunning(context, GetAwayHomeService.class)));
	}
	
	private boolean now_RunningServiceCheck() {
		//レシーバが呼ばれたら毎回dbを読み込み、ArrayListに保存
		ArrayList<Lost_Property_Prevention_Data> arrayListLppd_nowRunServiceCheck = lppdao.selectLost_Property_Prevention_Data();
		
		//取得したArrayList<Lost_Property_Prevention_Data>からgetNow_running_service()を調べ、レシーバ(runService)が起動されているか確認する
		for (int i = 0; i < arrayListLppd_nowRunServiceCheck.size(); i++) {
			lppd_getRunServiceRunningServiceCheck = arrayListLppd_nowRunServiceCheck.get(i);
			if(lppd_getRunServiceRunningServiceCheck.getNow_running_service().equals("true")) {
				return true;
			}
		}
		return false;
	}
	
	
	
	public static int getNowRunServiceRowId() {
		return nowRunServiceRowId;
	}
	
	public void setNowRunServiceRowId(int i) {
		this.nowRunServiceRowId = i;
	}
	
	public static boolean get_Run_Service() {
		return run_service;
	}
	
	public static void set_Run_Service(boolean b) {
		run_service = b;
	}
	
	
	//サービスの実行状態を取得するメソッド
	//第一引数サービスの取得に必要な、Context 第二引数調べたいサービスのクラス
	public boolean isServiceRunning(Context c, Class<?> cls) {
		ActivityManager am = (ActivityManager) c.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningServiceInfo> runningService = am.getRunningServices(100);
		//拡張for  取り出される要素を格納する変数　：　参照変数名（この要素分繰り返される）		
		for (RunningServiceInfo i : runningService) {
			Log.d("-----------実行中のサービス----------- GetAwayHomeReciver 357", i.service.getShortClassName());
			
			//もし動作中のサービスの中に、指定したサービスがあればtrueを返す
			//RunningServiceInfoのgetShortClassName()は先頭に"."が付くため　class<?> clsのgetSimpleName()で取得したクラス名の先頭に"."を付ける
			if((".service." + cls.getSimpleName()).equals(i.service.getShortClassName())) {
				return true;
			}
		}
		return false;
	}
	
//	private void notification_receiver(Context context) {
//		
//		NotificationManager nfm = (NotificationManager)context.getSystemService(context.NOTIFICATION_SERVICE);
//		
//		Notification noti = new Notification();
//		noti.icon = R.drawable.run_time_icon;
//		noti.tickerText = "忘れ物知らずを実行しました";
//		noti.when = System.currentTimeMillis();
//		noti.defaults |= Notification.DEFAULT_SOUND;
//		
//		//通知のリストを押したらMainActivityに移動する
//		Intent intentForeground = new Intent(context, Main_Activity.class);
//		//通知ボタンを押したときどこに行くか
//		PendingIntent pi = PendingIntent.getActivity(context, 0, intentForeground, 0);
//    	//通知の一覧画面をセットするメソッド
//		noti.setLatestEventInfo(context, "忘れ物知らず", "起動中", pi);
//		
//		//*ポイント 
//		//フラグ設定しないと通知領域に表示されてしまう
//		//通知領域に表示するのが目的なら flag の設定は不要
//		noti.flags = noti.flags | noti.FLAG_NO_CLEAR | noti.FLAG_ONGOING_EVENT;
//		noti.number = 0;
//		noti.contentIntent = pi;
//		
//		//通知発行 通知のidは　dbの_id　と紐付け
//		nfm.notify(noti_id, noti);
//		firstLowId = lppd.getRowId();
//	}
	
	private boolean runServiceTimeCheck() {
		
		switch (runtime_hour_int) {
		//実行時間1時間
		case 1:
			//起動時刻〜起動時刻+1まで
			if(((now_hour == starttime_hour_int) && (now_minute >= starttime_minute_int)) ||
				((now_hour == get24hour(starttime_hour_int + 1) ) && (now_minute <= starttime_minute_int)) 
				) {
				Log.d("************ hour 1 true *************", "******************");
				Log.d("************ starttime_hour_int *************", String.valueOf(starttime_hour_int));
				Log.d("************ now_hour *************", String.valueOf(now_hour));
				Log.d("************ now_minute *************", String.valueOf(now_minute));
				
				return true;
			//それ以外の時間
			} else {
				Log.d("************ hour 1 false *************", "*****************");
				Log.d("************ starttime_hour_int *************", String.valueOf(starttime_hour_int));
				Log.d("************ now_hour *************", String.valueOf(now_hour));
				Log.d("************ now_minute *************", String.valueOf(now_minute));
				return false;
			}

			//実行時間2時間
		case 2:
			//起動時刻〜起動時刻+2まで
			if(((now_hour == starttime_hour_int) && (now_minute >= starttime_minute_int)) ||
				(now_hour == get24hour(starttime_hour_int + 1)) || 	
				((now_hour == get24hour(starttime_hour_int + 2)) && (now_minute <= starttime_minute_int)) 
				) {
				Log.d("************ hour 2 true *************", "*******************");
				Log.d("************ starttime_hour_int *************", String.valueOf(starttime_hour_int));
				Log.d("************ now_hour *************", String.valueOf(now_hour));
				Log.d("************ now_minute *************", String.valueOf(now_minute));
				return true;
	
			//それ以外の時間
			} else {
				Log.d("************ hour 2 false *************", "*****************");
				Log.d("************ starttime_hour_int *************", String.valueOf(starttime_hour_int));
				Log.d("************ now_hour *************", String.valueOf(now_hour));
				Log.d("************ now_minute *************", String.valueOf(now_minute));
				return false;
			}
			
			
			//実行時間3時間
		case 3:
			//起動時刻〜起動時刻+3まで
			if(((now_hour == starttime_hour_int) && (now_minute >= starttime_minute_int)) ||
				(now_hour == get24hour(starttime_hour_int + 1)) ||
				(now_hour == get24hour(starttime_hour_int + 2)) ||
				((now_hour == get24hour(starttime_hour_int + 3)) && (now_minute <= starttime_minute_int)) 
				) {
				Log.d("************ hour 3 true *************", "****************");
				Log.d("************ starttime_hour_int *************", String.valueOf(starttime_hour_int));
				Log.d("************ starttime_minute *************", String.valueOf(starttime_minute_int));
				Log.d("************ now_hour *************", String.valueOf(now_hour));
				Log.d("************ now_minute *************", String.valueOf(now_minute));

				return true;
			//それ以外の時間
			} else {
				Log.d("************ hour 3 false *************", "***************");
				Log.d("************ starttime_hour_int *************", String.valueOf(starttime_hour_int));
				Log.d("************ starttime_minute *************", String.valueOf(starttime_minute_int));
				Log.d("************ now_hour *************", String.valueOf(now_hour));
				Log.d("************ now_minute *************", String.valueOf(now_minute));
				return false;
			}

		default:
			return false;
		}
	}
	
	private int get24hour(int i) {
		if(i>23) {
			return i-24;
		}
		return i;
	}
	public void notification_receiver(Context context) {
		
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
	
	
	
	

}
	
