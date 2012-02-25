package jp.isao.lost_property_prevention.activity;

import java.util.ArrayList;
import java.util.Calendar;

import jp.isao.lost_property_prevention.R;
import jp.isao.lost_property_prevention.adapter.Lost_Property_Prevention_ListAllAdapter;
import jp.isao.lost_property_prevention.data.Lost_Property_Prevention_Data;
import jp.isao.lost_property_prevention.db.Lost_Property_Prevention_Dao;
import jp.isao.lost_property_prevention.receiver.GetAwayHomeReceiver;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.ads.Ad;
import com.google.ads.AdListener;
import com.google.ads.AdRequest;
import com.google.ads.AdView;
import com.google.ads.AdRequest.ErrorCode;


public class Main_Activity extends ListActivity implements Button.OnClickListener ,AdListener {
//	//メニューアイテムID（予定リスト）
//	public static final int SCHEDULE_LIST = 0;
//	//メニューアイテムID(カレンダー）
//	public static final int CALENDAR = 1;
//	//メニューアイテムID(予定追加）
//	public static final int SCHEDULE_ADD = 2;
	
	//メニューアイテムID(Delete)
	private static final int DELETE_ID = Menu.FIRST+3;
	
	//タイトル表示用
	private TextView mTxtTitle;
	//予定追加
	private Button mBtnScheAdd;
	//リストビュー
	private ListView mList;
	//Calendar
	private Calendar mCal = Calendar.getInstance();
	//db Dao
	Lost_Property_Prevention_Dao lppdao;
	//adapter
	Lost_Property_Prevention_ListAllAdapter adapter;
	
	
	private AdView adGoogle;
    private AdRequest adr;
    
    private boolean Flag_AlarmManager_Run=false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//画面レイアウト
		setContentView(R.layout.main);
		
		//インスタンス取得
		mBtnScheAdd = (Button)findViewById(R.id.btn_schedule_gps_add);
		mBtnScheAdd.setOnClickListener(this);
		
		mList = (ListView)findViewById(android.R.id.list);
		
		
		//フォーマット変更
		Lost_Property_Prevention_Dao dao = new Lost_Property_Prevention_Dao(this);
		//daoクラスのselect()から取得したArrayList(selectLost_Property_Prevention_Data)をList adapterにセットしている
		//adapterにArrayListのデータを渡しているので、getItem()でArrayListの一つのデータを取得できる
		adapter = new Lost_Property_Prevention_ListAllAdapter(this, dao.selectLost_Property_Prevention_Data());
		//アダプタ設定
		mList.setAdapter(adapter);
		
		//コンテキストメニューはこのメソッドに登録する必要がある
		registerForContextMenu(mList);
		//リストをスクロールしたときに、リストが黒くならない
		mList.setScrollingCacheEnabled(false);
		
		//リストの長押しイベント
		mList.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				return false;
			}

		});
		
		// TODO Auto-generated method stub
		//どれかリストの実行チェックボックスがtrueだったら
		//MainActivityが呼ばれたとき、チェックボックスをチェックする
		if(run_Service_Check()==true) {
			Log.d("---------Main_Activity-----109", "--------109-----");
			//家から離れたことを教えるサービス
			Intent intent = new Intent(this, GetAwayHomeReceiver.class);
			PendingIntent sender = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
			AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
			//AlarmManagerを開始する。5分おきにPendingIntentが発行される
			am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, 1000, 1000 * 60 * 5, sender);
			adapter.notification_receiver(this);
		}

	}
	
	@Override
	protected void onStart() {

        
		super.onStart();
	}

//	//オプションメニューを追加する
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		menu.add(0, SCHEDULE_LIST, 0, "予定リスト")
//			.setIcon(android.R.drawable.ic_menu_recent_history);
//		menu.add(0, CALENDAR, 0, "カレンダー")
//			.setIcon(android.R.drawable.ic_menu_my_calendar);
//		menu.add(0, SCHEDULE_ADD, 0, "予定追加")
//		.setIcon(android.R.drawable.ic_menu_add);
//		return super.onCreateOptionsMenu(menu);
//
//	}
	
//	//オプションメニューのアイコンが選択されたときのイベント
//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//		
//		Intent intent = null;
//		
//		
//		switch (item.getItemId()) {
//		case SCHEDULE_LIST:
//			//画面遷移：予定リスト画面
//			intent = new Intent(this, ScheduleListAll.class);
//			startActivity(intent);
//			
//			break;
//		case CALENDAR:
//			//画面遷移：予定カレンダー画面
//			intent = new Intent(this, ScheduleMain.class);
//			startActivity(intent);
//			break;
//		
//		case SCHEDULE_ADD:
//			//画面遷移：予定追加画面
//			intent = new Intent(this, ScheduleNew.class);
//			intent.putExtra("Schedule_AddButton_Click", mDate);
//			startActivity(intent);
//			break;
//		}
//
//		return super.onOptionsItemSelected(item);
//	}
	
	
	//リストアイテムクリックイベント処理
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Lost_Property_Prevention_Data lppd = (Lost_Property_Prevention_Data)mList.getAdapter().getItem(position);
		//画面遷移：予定編集画面
		Intent intent = new Intent(this, Set_Edit_Activity.class);
		intent.putExtra("Schedule_ListItem_Click", lppd);
		startActivity(intent);
		super.onListItemClick(l, v, position, id);
	}
	

	 //コンテキストメニュー生成
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, final ContextMenu.ContextMenuInfo menuInfo) {
    	final AlertDialog.Builder ad = new AlertDialog.Builder(this);

		ad.setTitle("削除しますか？");
		
		ad.setMessage("このリストを削除しますか？");
		
		ad.setPositiveButton("削除", new DialogInterface.OnClickListener() {
			//DBに登録内容を保存
			@Override
			public void onClick(DialogInterface dialog, int which) {
	    		//db dao クラス　インスタンス生成
				lppdao = new Lost_Property_Prevention_Dao(Main_Activity.this);
				//長押しした時の、セットしたArrayListのpositionを取得する
				int delete_Position = ((AdapterView.AdapterContextMenuInfo)menuInfo).position;
				Log.d("------------ position ------------", String.valueOf(delete_Position));
				//adpterから削除するリストのデータを取得する
				Lost_Property_Prevention_Data lppd_delete_rowId = adapter.getItem(delete_Position);				
				//取得したリストのデータのrowIdを削除する
				lppdao.deleteLost_Property_Prevention_Data(lppd_delete_rowId.getRowId());

				//リストビューから削除
				adapter.remove(lppd_delete_rowId);
				//リストビュー更新
				adapter.notifyDataSetChanged();
				
			}
		});
		
		//ダイアログを閉じる
		ad.setNegativeButton("キャンセル", null);	
		//ダイアログ表示
		ad.show();
			
    }
    
    //コンテキストメニューアイテムクリック時
    @Override
    public boolean onContextItemSelected(MenuItem item) {
    	return (super.onOptionsItemSelected(item));
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
    
    
    //MainActivityがkillされるとき、AlarmManagerが起動されていれば、プリファレンスにtrueを保存する
    //再びMainActivityがonCreateされたとき、保存したプリファレンスがtrueだったら、AlarmManagerを再起動する
    //プリファレンスをfalseにして、AlarmManagerを毎回再起動しないようにする
	@Override
	protected void onDestroy() {
		

		super.onDestroy();
	}

	
	//予定追加ボタンのイベント処理
	@Override
	public void onClick(View v) {
		Intent intent = new Intent(this, Set_Add_Activity.class);
		startActivity(intent);
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
	
	//レシーバ用のAlarmManagerを起動するかチェックする	
	public boolean run_Service_Check() {
		Flag_AlarmManager_Run = false;

		//db dao クラス　インスタンス生成
		lppdao = new Lost_Property_Prevention_Dao(Main_Activity.this);
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
	

}
