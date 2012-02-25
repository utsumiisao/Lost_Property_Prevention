package jp.isao.lost_property_prevention.activity;

import com.google.ads.Ad;
import com.google.ads.AdListener;
import com.google.ads.AdRequest;
import com.google.ads.AdView;
import com.google.ads.AdRequest.ErrorCode;

import jp.isao.lost_property_prevention.GetAwayHomeBindService;
import jp.isao.lost_property_prevention.R;
import jp.isao.lost_property_prevention.GetAwayHomeBindService.Stub;
import jp.isao.lost_property_prevention.R.id;
import jp.isao.lost_property_prevention.R.layout;
import jp.isao.lost_property_prevention.data.Lost_Property_Prevention_Data;
import jp.isao.lost_property_prevention.db.Lost_Property_Prevention_Dao;
import jp.isao.lost_property_prevention.receiver.GetAwayHomeReceiver;
import jp.isao.lost_property_prevention.service.GetAwayHomeService;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

public class NotifyMessage extends Activity implements AdListener {
	//初期化処理
	private TextView mContent1;
	private TextView mContent2;
	private TextView mContent3;
	private TextView mContent4;
	private TextView mContent5;
	private TextView mContent6;
	private TextView mContent7;
	private TextView mContent8;
	private TextView mContent9;
	private TextView mContent10;
	
	private CheckBox mContent1_check_box;
	private CheckBox mContent2_check_box;
	private CheckBox mContent3_check_box;
	private CheckBox mContent4_check_box;
	private CheckBox mContent5_check_box;
	private CheckBox mContent6_check_box;
	private CheckBox mContent7_check_box;
	private CheckBox mContent8_check_box;
	private CheckBox mContent9_check_box;
	private CheckBox mContent10_check_box;
	
	private Lost_Property_Prevention_Data lppd;
	
	private Button mConfirmButton;
	
	//サービスの宣言
	private GetAwayHomeBindService service;
	
	private ServiceConnection conn;
	
	private Lost_Property_Prevention_Dao lppdao;
	private NotificationManager nfm;
	
	private Bundle extras;
	private int rowId=0;
	
	private AdView adGoogle;
    private AdRequest adr;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.notify);
		
		//Notificationのインスタンスを取得
		nfm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		//Intent取得
		extras = getIntent().getExtras();
		//Intentに付加してあるdataクラスを取得
		lppd = (Lost_Property_Prevention_Data)extras.getSerializable("lppd_from_GetAwayHomeService");

		

		
		
		//リスナー登録
		mContent1 = (TextView)findViewById(R.id.text_view_content1);
		mContent2 = (TextView)findViewById(R.id.text_view_content2);
		mContent3 = (TextView)findViewById(R.id.text_view_content3);
		mContent4 = (TextView)findViewById(R.id.text_view_content4);
		mContent5 = (TextView)findViewById(R.id.text_view_content5);
		mContent6 = (TextView)findViewById(R.id.text_view_content6);
		mContent7 = (TextView)findViewById(R.id.text_view_content7);
		mContent8 = (TextView)findViewById(R.id.text_view_content8);
		mContent9 = (TextView)findViewById(R.id.text_view_content9);
		mContent10 = (TextView)findViewById(R.id.text_view_content10);
		
		mContent1_check_box = (CheckBox)findViewById(R.id.content1_check_box);
		mContent2_check_box = (CheckBox)findViewById(R.id.content2_check_box);
		mContent3_check_box = (CheckBox)findViewById(R.id.content3_check_box);
		mContent4_check_box = (CheckBox)findViewById(R.id.content4_check_box);
		mContent5_check_box = (CheckBox)findViewById(R.id.content5_check_box);
		mContent6_check_box = (CheckBox)findViewById(R.id.content6_check_box);
		mContent7_check_box = (CheckBox)findViewById(R.id.content7_check_box);
		mContent8_check_box = (CheckBox)findViewById(R.id.content8_check_box);
		mContent9_check_box = (CheckBox)findViewById(R.id.content9_check_box);
		mContent10_check_box = (CheckBox)findViewById(R.id.content10_check_box);
		
		mConfirmButton = (Button)findViewById(R.id.confirm_notify);
		
		
		//チェック項目の内容をレイアウトに配置する
		setContent();
		
		//ServiceConnectionインタフェースを実装したクラスの作成
		conn = new ServiceConnection() {
			
			@Override
			public void onServiceConnected(ComponentName name, IBinder binder) {
				
				//binderからサービスを取得
				service =  GetAwayHomeBindService.Stub.asInterface(binder);
				
			}
			
			@Override
			public void onServiceDisconnected(ComponentName name) {

			}

		};
		
		//事前にチェックの時はGPSが動いているので、停止させる。
		//"jp.isao.MapView_test_service.GetAwayHomeBindService" == GetAwayHomeBindService.class.getName()
		Intent intentService = new Intent(GetAwayHomeBindService.class.getName());
		bindService(intentService, conn, BIND_AUTO_CREATE);
		
		
		mConfirmButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {

				//チェックボックスが全てチェックされていればtrueが返ってくる
				if (all_CheckBox_Condition()==true) {
					Log.d("-------------------通知　row Id-----------------", String.valueOf(lppd.getRowId()));
					//NotifycationManager取得
					NotificationManager mgr = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
					//通知削除 リストに登録されているrowId
					mgr.cancel(lppd.getRowId());

//					lppd.setNow_running_service("false");
//					lppdao = new Lost_Property_Prevention_Dao(NotifyMessage.this);
//					lppdao.updateLost_Property_Prevention_Data(lppd, lppd.getRowId());
					
					try {
						
						//サービスにGPSを停止させる
						service.stopGpsFunction();					
					} catch (RemoteException e) {
						Log.d("バインドサービス　エラー", "", e);
					}
					unbindService(conn);
					conn=null;

					//ユーザにチェックが完了したことを知らせる
					Toast.makeText(NotifyMessage.this, "持ち物チェックお疲れさまでした！", Toast.LENGTH_LONG).show();
					//Intentを生成してmain_Activityの方に飛ぶ
					Intent intent = new Intent(NotifyMessage.this,Main_Activity.class);
					startActivity(intent);
				} else {
					//全てチェックするようにダイアログを表示する
					dialog_CheckBox();
				}
				
			}
		});
		
	}
	
	
	private void dialog_CheckBox() {
		//AlertDialogのインスタンス取得
		final AlertDialog.Builder ad = new AlertDialog.Builder(this);
		//停止確認用のダイアログ
		ad.setTitle("項目を全てチェックして下さい");
		ad.setMessage("項目が全てチェックされていません、項目を全てチェックして下さい");
		ad.setPositiveButton("OK", null);
		ad.show();

	}
	
	
	private boolean all_CheckBox_Condition() {
		if(mContent1_check_box.isEnabled()==true) {
			if (mContent1_check_box.isChecked() == false) {
				return false;
			}
		}
		
		if(mContent2_check_box.isEnabled()==true) {
			if (mContent2_check_box.isChecked() == false) {
				return false;
			}
		}
		
		if(mContent3_check_box.isEnabled()==true) {
			if (mContent3_check_box.isChecked() == false) {
				return false;
			}
		}
		
		if(mContent4_check_box.isEnabled()==true) {
			if (mContent4_check_box.isChecked() == false) {
				return false;
			}
		}
		
		if(mContent5_check_box.isEnabled()==true) {
			if (mContent5_check_box.isChecked() == false) {
				return false;
			}
		}
		
		if(mContent6_check_box.isEnabled()==true) {
			if (mContent6_check_box.isChecked() == false) {
				return false;
			}
		}
		
		if(mContent7_check_box.isEnabled()==true) {
			if (mContent7_check_box.isChecked() == false) {
				return false;
			}
		}
		
		if(mContent8_check_box.isEnabled()==true) {
			if (mContent8_check_box.isChecked() == false) {
				return false;
			}
		}
		
		if(mContent9_check_box.isEnabled()==true) {
			if (mContent9_check_box.isChecked() == false) {
				return false;
			}
		}
		
		if(mContent10_check_box.isEnabled()==true) {
			if (mContent10_check_box.isChecked() == false) {
				return false;
			}
		}
		

		return true;


	}
	
	private void setContent() {

		
		if(lppd.getEdittext_content1().equals("")) {
			mContent1.setText("");
			mContent1_check_box.setEnabled(false);
		} else {
			mContent1.setText((String)lppd.getEdittext_content1());
		}
		
		if(lppd.getEdittext_content2().equals("")) {
			mContent2.setText("");
			mContent2_check_box.setEnabled(false);
		} else {
			mContent2.setText((String)lppd.getEdittext_content2());
		}
		
		if(lppd.getEdittext_content3().equals("")) {
			mContent3.setText("");
			mContent3_check_box.setEnabled(false);
		} else {
			mContent3.setText((String)lppd.getEdittext_content3());
		}
		
		if(lppd.getEdittext_content4().equals("")) {
			mContent4.setText("");
			mContent4_check_box.setEnabled(false);
		} else {
			mContent4.setText((String)lppd.getEdittext_content4());
		}
		
		if(lppd.getEdittext_content5().equals("")) {
			mContent5.setText("");
			mContent5_check_box.setEnabled(false);
		} else {
			mContent5.setText((String)lppd.getEdittext_content5());
		}
		
		if(lppd.getEdittext_content6().equals("")) {
			mContent6.setText("");
			mContent6_check_box.setEnabled(false);
		} else {
			mContent6.setText((String)lppd.getEdittext_content6());
		}
		
		if(lppd.getEdittext_content7().equals("")) {
			mContent7.setText("");
			mContent7_check_box.setEnabled(false);
		} else {
			mContent7.setText((String)lppd.getEdittext_content7());
		}
		
		if(lppd.getEdittext_content8().equals("")) {
			mContent8.setText("");
			mContent8_check_box.setEnabled(false);
		} else {
			mContent8.setText((String)lppd.getEdittext_content8());
		}
		
		if(lppd.getEdittext_content9().equals("")) {
			mContent9.setText("");
			mContent9_check_box.setEnabled(false);
		} else {
			mContent9.setText((String)lppd.getEdittext_content9());
		}
		
		if(lppd.getEdittext_content10().equals("")) {
			mContent10.setText("");
			mContent10_check_box.setEnabled(false);
		} else {
			mContent10.setText((String)lppd.getEdittext_content10());
		}
		
		
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		if(conn!=null) {
			unbindService(conn);
		}
		
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
