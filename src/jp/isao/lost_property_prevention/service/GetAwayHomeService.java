package jp.isao.lost_property_prevention.service;

import java.util.Calendar;

import jp.isao.lost_property_prevention.GetAwayHomeBindService;
import jp.isao.lost_property_prevention.R;
import jp.isao.lost_property_prevention.activity.NotifyMessage;
import jp.isao.lost_property_prevention.data.Lost_Property_Prevention_Data;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

public class GetAwayHomeService extends Service implements LocationListener {
	
	private double nowLatitude;
	private double nowLongitude;
	
	//自宅範囲外だったときの回数をカウントする変数
	private Integer count;	
	
	private double latitude;
	private double longitude;
	
	//LocationManagerの宣言
	LocationManager mLocationManager=null;
	
	
	Calendar calendar2 = Calendar.getInstance();
	
	public static final int NOTIFY_ID = 101;
	
	//Binder
	private final Binder binder = new LocalBinder();
	
	private Lost_Property_Prevention_Data lppd;

	NotificationManager nfm;
	
	//LocalBinder
	public class LocalBinder extends Binder {
		//サービス取得
		GetAwayHomeService getService() {
			return (GetAwayHomeService.this);
		}
	}
	
	
	@Override
	public IBinder onBind(Intent arg0) {
		return mGetAwayHomeBindService;
	}
	
	private final GetAwayHomeBindService.Stub mGetAwayHomeBindService = new GetAwayHomeBindService.Stub() {
		
		@Override
		public void stopGpsFunction() throws RemoteException {
			if(!(mLocationManager==null)) {
				// GPS使用終了時にリソースを解放する
				mLocationManager.removeUpdates(GetAwayHomeService.this);
				//onDestory()でGPSを使用しているか判断するため
				mLocationManager = null;
				//範囲外だったときをカウントするカウンターを０にする
				count = 0;
				
			}
		}
	};
	
	
	private void startGpsFunction() {
	       //LocationManagerの取得
        mLocationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
       	// 位置情報更新の設定（更新時間0秒、更新距離0m）
		mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
    												0, 0, GetAwayHomeService.this); 
	}

	
	@Override
	public void onCreate() {

	}
	
	
	//開始時の[onStart]メソッド
	@Override
	public void onStart(Intent intent, int startId) {
		Log.d("-------------  onStart --------------", String.valueOf(count));		
		//自宅が起動距離で設定した範囲以上だったときをカウントするカウンターを０にする
		count = 0;
		lppd = (Lost_Property_Prevention_Data)intent.getSerializableExtra("lppd_from_GetAwayHomeReceiver");

		//GPS起動
		startGpsFunction();
		//事前チェック確認用通知
		notificationOnAhead();
	}


	@Override
	public void onLocationChanged(Location location) {
		//現在の緯度の取得
		nowLatitude = location.getLatitude();
		//現在の経度の取得
		nowLongitude = location.getLongitude();
		
		//設定した緯度の取得
		latitude = lppd.getLatitude();
		//設定した経度の取得
		longitude = lppd.getLongitude();
		
		//2点間の距離の取得
		if(nowLatitude!=0.0 && nowLongitude!=0.0) {
			float[] results = {0, 0, 0,};
			Location.distanceBetween(latitude, longitude, nowLatitude, nowLongitude, results);
			
			//自宅（目的地）までの距離が起動距離の設定範囲以上だったとき
			if(results[0]>=make_int_gps_run_circle()) {
				
				count++;
				Log.d("-------------  gpd Count --------------", String.valueOf(count));
				//自宅範囲外が5回以上だったら通知で知らせる
				if(count==5) {
					nfm.cancel(lppd.getRowId());
					Log.d("-------------  gpd Count --------------", String.valueOf(count));
//					statustext.setText("只今自宅から半径20m以上です！");
					notification();
					// GPS使用終了時解放する
					mLocationManager.removeUpdates(this);
					//onDestory()でGPSを使用しているか判断するため
					mLocationManager = null;
					//自分のサービスを停止
//					Intent intent = new Intent(this, GetAwayHomeService.class);
//					stopService(intent);
				}

				
			//自宅（目的地）までの距離が起動距離の設定範囲未満だったとき
			} else if(results[0]<make_int_gps_run_circle()) {

			}
			
		} else {
			Toast.makeText(this, "現在位置情報が取得できていません！", Toast.LENGTH_SHORT).show();
		}

		
	}
	
	private void notification() {
		//バイブレーションをONにする設定 (Normal mode に設定する）
		AudioManager audio = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        audio.setRingerMode(2);
        
		nfm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		
		Notification noti = new Notification();
		noti.icon = R.drawable.check_term_icon;
		noti.tickerText = "忘れ物はありませんか？";
		noti.when = System.currentTimeMillis();
		noti.defaults |= Notification.DEFAULT_SOUND;
		
		//バイブの実行時間を long[]配列を返すメソッド
		
		noti.vibrate = vibrate_run_time_factory(); //off on off on

		noti.ledARGB = 0xffff0000;
		noti.ledOnMS = 300;
		noti.ledOffMS = 1000;
		noti.flags |= Notification.FLAG_SHOW_LIGHTS;
		noti.defaults |= Notification.DEFAULT_LIGHTS;
		
		Intent intent = new Intent(this, NotifyMessage.class);
		intent.putExtra("lppd_from_GetAwayHomeService", lppd);
		//通知ボタンを押したときどこに行くか
		PendingIntent pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		
//    	//通知の一覧画面をセットするメソッド
    	noti.setLatestEventInfo(this, "持ち物チェック", "", pi);

		noti.contentIntent = pi;
		//lppd.getRowId()
		nfm.notify(lppd.getRowId(),noti);
	}
	
	
	private long[] vibrate_run_time_factory() {
		long[] vibrate_run_time;
		//vibrate実行時間をLost_Property_Prevention_Dataから取得
		int vib_runtime_minute = Integer.parseInt(lppd.getVib_runtime().substring(0, 2));
		int vib_runtime_second = Integer.parseInt(lppd.getVib_runtime().substring(3, 5));
		
		int vib_runtime_count=0;
		int vib_runtime_1s = vib_runtime_minute * 60 + vib_runtime_second;
		Log.d("-------------  vib_runtime_1s --------------", String.valueOf(vib_runtime_1s));
		
		vibrate_run_time = new long[vib_runtime_1s*2];
		
		//vibrateにセットするためのlong[]配列を生成する
		for (int i = 0; i < (int)vib_runtime_1s/2; i++) {
			
			vibrate_run_time[vib_runtime_count] = (long)500;
			Log.d("-------------  vibrate_run_time --------------",String.valueOf(vib_runtime_count) +" " +  String.valueOf(vibrate_run_time[vib_runtime_count]));
			vib_runtime_count++;
			vibrate_run_time[vib_runtime_count] = (long)1500;
			Log.d("-------------  vibrate_run_time --------------",String.valueOf(vib_runtime_count) +" " + String.valueOf(vibrate_run_time[vib_runtime_count]));
			vib_runtime_count++;
		}
		
		Log.d("-------------  vibrate_run_time　length --------------",String.valueOf(vib_runtime_count) +" " + String.valueOf(vibrate_run_time.length));
		
		return vibrate_run_time;
	}
	
	
	
	//実行時間になったら事前ににチェックしていれば、GPSはストップし家を出てもバイブが鳴らない
	//事前チェック用の通知
	private void notificationOnAhead() {
		//バイブレーションをONにする設定 (Normal mode に設定する）
		AudioManager audio = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        audio.setRingerMode(2);
		nfm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		
		Notification noti = new Notification();
		noti.icon = R.drawable.check_term_icon;
		noti.tickerText = "忘れ物はありませんか？";
		noti.when = System.currentTimeMillis();
		noti.defaults |= Notification.DEFAULT_SOUND;
		noti.ledARGB = 0xffff0000;
		noti.ledOnMS = 300;
		noti.ledOffMS = 1000;
		noti.flags |= Notification.FLAG_SHOW_LIGHTS;
		noti.defaults |= Notification.DEFAULT_LIGHTS;
		
		Intent intent = new Intent(this, NotifyMessage.class);
		intent.putExtra("lppd_from_GetAwayHomeService", lppd);
		//通知ボタンを押したときどこに行くか
		PendingIntent pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		
//    	//通知の一覧画面をセットするメソッド
    	noti.setLatestEventInfo(this, "持ち物チェック", "", pi);
		noti.contentIntent = pi;
		
		nfm.notify(lppd.getRowId(),noti);

	}
	
	private int make_int_gps_run_circle() {
		switch (Integer.valueOf(lppd.getGps_run_circle())) {
		case 0:
			return (int) 40;
		
		case 1:
			return (int) 60;
			
		case 2:
			return (int) 80;
			
		case 3:
			return (int) 100;
			
		case 4:
			return (int) 150;

		default:
			return (int) 0;
		}
		 
	}



	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}
	
	
	@Override
	public void onDestroy() {
	       //LocationManagerの取得
        if (!(mLocationManager==null)) {
		
			// GPS使用終了時解放する
			mLocationManager.removeUpdates(this);	
        }

	
		super.onDestroy();
	}
	
	
	

}
