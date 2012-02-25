package jp.isao.lost_property_prevention.activity;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jp.isao.lost_property_prevention.R;
import jp.isao.lost_property_prevention.data.Lost_Property_Prevention_Data;
import jp.isao.lost_property_prevention.map.MyMapView;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class Location_Check_Activity extends MapActivity implements LocationListener  {
	private MyMapView mMap;
	private Button mMapSetButton;
	private Button mLoad_Gps;
	private Button mLoad_Network;
	
	private double latitude_Location_Check;
	private double longitude_Location_Check;
	
	private double defaultValueDouble = 0.0;
	private boolean defaultValueBoolean = false;
	
	private ArrayList<Address> addrList;
	
	private Address addr;
	
	private SitesOverLay overLay;
	
	private boolean key_Set_Add_Activity = false;
	private boolean key_Set_Edit_Activity = false;
	
	private boolean key_set=false;
	
	private LocationManager mLocationManager=null;
	//ダイアログ用インスタンス
	private ProgressDialog mDialog;
	
	//List Overlayを宣言
	private List<Overlay> mOverLays;
	
	//
	private Lost_Property_Prevention_Data lppd;
	
	private EditText mAddr;
	private Button mBtnFind;
	
	private boolean btnFindFlag = false;

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
	
	
	@Override
	protected void onCreate(Bundle icicle) {
		// TODO Auto-generated method stub
		super.onCreate(icicle);
		
		setContentView(R.layout.map_view_check);
		
		mMap = (MyMapView)findViewById(R.id.map_check);
//		mBackButton = (Button)findViewById(R.id.back);
		mMapSetButton = (Button)findViewById(R.id.mapset);
		mLoad_Gps = (Button)findViewById(R.id.load_gps);
		mLoad_Network = (Button)findViewById(R.id.load_netwrok);
		mBtnFind = (Button)findViewById(R.id.address_search_btn);
		mAddr = (EditText)findViewById(R.id.address_search_edt);

	
		//登録ボタンの処理
		mMapSetButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(key_Set_Add_Activity==true) {
					//Intentを生成して、Set_Add_Activityに戻る
					Intent intent = new Intent(Location_Check_Activity.this, Set_Add_Activity.class);
					//最後に、長押しで位置情報を取得した場合
					Log.d("------------getOnLongPress_Location_Info_Flag()------------", String.valueOf(mMap.getOnLongPress_Location_Info_Flag()));
					Log.d("------------getLatitude_Location_Check------------", String.valueOf(mMap.getLatitude_Location_Check()));
					Log.d("------------getLongitude_Location_Check------------", String.valueOf(mMap.getLongitude_Location_Check()));
					
					Log.d("------------latitude_Location_Check------------", String.valueOf(mMap.getLatitude_Location_Check()));
					Log.d("------------latitude_Location_Check------------", String.valueOf(mMap.getLongitude_Location_Check()));
					
					
					if(mMap.getOnLongPress_Location_Info_Flag()==true) {
						//取得した緯度、経度情報をlppdにセット
						lppd.setLatitude(mMap.getLatitude_Location_Check());
						lppd.setLongitude(mMap.getLongitude_Location_Check());
					//最後に、GPSまたはネットワークで位置情報を取得した場合
					} else if(mMap.getOnLongPress_Location_Info_Flag()==false) {
						//取得した緯度、経度情報をlppdにセット
						lppd.setLatitude(latitude_Location_Check);
						lppd.setLongitude(longitude_Location_Check);
					}

					intent.putExtra("lppd_from_Location_Check_Activity", lppd);
					Toast.makeText(Location_Check_Activity.this, "位置情報を登録しました", Toast.LENGTH_SHORT).show();
					//Set Activityに戻るので、デフォルト値のfalseに戻す
					key_Set_Add_Activity = false;
					startActivity(intent);
					
				} else if (key_Set_Edit_Activity==true) {
					//Intentを生成して、Set_Edit_Activityに戻る
					Intent intent = new Intent(Location_Check_Activity.this, Set_Edit_Activity.class);
					
					//最後に、長押しで位置情報を取得した場合
					Log.d("------------key_Set_Edit_Activity------------", String.valueOf(key_Set_Edit_Activity));
					Log.d("------------getOnLongPress_Location_Info_Flag()------------", String.valueOf(mMap.getOnLongPress_Location_Info_Flag()));
					Log.d("------------getLatitude_Location_Check------------", String.valueOf(mMap.getLatitude_Location_Check()));
					Log.d("------------getLongitude_Location_Check------------", String.valueOf(mMap.getLongitude_Location_Check()));
					
					Log.d("------------latitude_Location_Check------------", String.valueOf(latitude_Location_Check));
					Log.d("------------latitude_Location_Check------------", String.valueOf(longitude_Location_Check));
					
					
					if(mMap.getOnLongPress_Location_Info_Flag()==true) {
						//取得した緯度、経度情報をlppdにセット
						lppd.setLatitude(mMap.getLatitude_Location_Check());
						lppd.setLongitude(mMap.getLongitude_Location_Check());
					//最後に、GPSまたはネットワークで位置情報を取得した場合
					} else if(mMap.getOnLongPress_Location_Info_Flag()==false) {
						//取得した緯度、経度情報をlppdにセット
						lppd.setLatitude(latitude_Location_Check);
						lppd.setLongitude(longitude_Location_Check);
					}
					
					intent.putExtra("lppd_from_Location_Check_Activity", lppd);
					
					Toast.makeText(Location_Check_Activity.this, "位置情報を登録しました", Toast.LENGTH_SHORT).show();
					
					//Set Activityに戻るので、デフォルト値のfalseに戻す
					key_Set_Edit_Activity = false;
					startActivity(intent);
				}
				
			}
		});
		
		//GPSで位置情報更新
		mLoad_Gps.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mOverLays = mMap.getOverlays();
				
				//ダイアログを表示する
				mDialog = new ProgressDialog(Location_Check_Activity.this);
				mDialog.setTitle("位置情報を取得しています");
				mDialog.setMessage("位置情報を取得できない場合は、戻るボタンを押して下さい");
				mDialog.setCancelable(true);
				mDialog.show();
				
				//GPSから緯度、経度取得する処理を別スレッドで行う
				(new Thread(runnable_gps)).start();
				
			}
		});
		
		
		//Networkで位置情報更新
		mLoad_Network.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mOverLays = mMap.getOverlays();
				
				//ダイアログを表示する
				mDialog = new ProgressDialog(Location_Check_Activity.this);
				mDialog.setTitle("位置情報を取得しています");
				mDialog.setMessage("位置情報を取得できない場合は、戻るボタンを押して下さい");
				mDialog.setCancelable(true);
				mDialog.show();
				
				//GPSから緯度、経度取得する処理を別スレッドで行う
				(new Thread(runnable_network)).start();
				
			}
		});
		
		//検索ボタンで住所検索
		mBtnFind.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//検索ボタンが押されたのフラグ立たせる
				btnFindFlag = true;
				
				// マップにピンを立てて、住所から緯度、経度を取得する
				makePositionDrawable();
				
			}
		});
		
        

	}
	

	
	@Override
	protected void onRestart() {
		
	}
	
	@Override
	protected void onResume() {
		//Activityから来た緯度、経度の情報を取得し、MapViewに表示する
		Intent i = getIntent();
		lppd = new Lost_Property_Prevention_Data();

		//Set_Edit_Activityからの遷移
		//保存してある位置情報確認用
		latitude_Location_Check = i.getDoubleExtra("Latitude_check", defaultValueDouble);
		longitude_Location_Check = i.getDoubleExtra("Longitude_check", defaultValueDouble);
		Log.d("-------------latitude_Location_Check---------", String.valueOf(latitude_Location_Check));
		Log.d("-------------longitude_Location_Check---------", String.valueOf(longitude_Location_Check));
		
		
		//位置情報取得用
		key_Set_Edit_Activity = i.getBooleanExtra("key_Set_Edit_Activity", defaultValueBoolean);
		if(key_Set_Edit_Activity==true) {
			lppd = (Lost_Property_Prevention_Data)i.getSerializableExtra("lppd_from_Set_Edit_Activity");
			Log.d("-------------lppd.getRowId()---------", String.valueOf(lppd.getRowId()));
		}
		
		//Set_Add_Activityから遷移してきた事を示すkey
		key_Set_Add_Activity = i.getBooleanExtra("key_Set_Add_Activity", defaultValueBoolean);
		//データ保存用クラスを取得
		if(key_Set_Add_Activity==true) {
			lppd = (Lost_Property_Prevention_Data)i.getSerializableExtra("lppd_from_Set_Add_Activity");
		}
		
		
		//Set Activityから発行されたDBの位置情報確認用のIntent
		key_set = i.getBooleanExtra("key_set", defaultValueBoolean);
		
		if(latitude_Location_Check != 0.0 && longitude_Location_Check != 0.0) {			
			
			//位置情報を確認するだけなので、mMapSetButtonを無効化
			mMapSetButton.setEnabled(false);
			mLoad_Gps.setEnabled(false);
			mLoad_Network.setEnabled(false);
			
			mOverLays = mMap.getOverlays();
			//MapView表示とピン画像の表示
			makePositionDrawable();	
			
		} else {
			mOverLays = mMap.getOverlays();
				
			//ダイアログを表示する
			mDialog = new ProgressDialog(this);
			mDialog.setTitle("位置情報を取得しています");
			mDialog.setMessage("位置情報を取得できない場合は、戻るボタンを押して下さい");
			mDialog.setCancelable(true);
			mDialog.show();
			
			//GPSから緯度、経度取得する処理を別スレッドで行う
			(new Thread(runnable_gps)).start();
		} 
		
		super.onResume();
	}
	
	
	//GPSから位置情報を取得するためのスレッド
	private Runnable runnable_gps = new Runnable() {
		
		@Override
		public void run() {
		
			//位置情報取得スレッド
			handler_gps.sendMessage(handler_gps.obtainMessage());
			
		}
	};
	
	//Networkから位置情報を取得するためのスレッド
	private Runnable runnable_network = new Runnable() {
		
		@Override
		public void run() {
		
			//位置情報取得スレッド
			handler_network.sendMessage(handler_network.obtainMessage());
			
		}
	};	
	
	
	
	
	private void makePositionDrawable() {
	       if(mOverLays.size() > 0) {
	        	mOverLays.clear();
	        }
	        
	        Geocoder geocoder = new Geocoder(this);
	        
	        try {
	        	//検索ボタンを押した時の処理
	        	if (btnFindFlag==true) {
	        		//EditTextから住所を取得する
	        		addrList = (ArrayList<Address>)geocoder.getFromLocationName(mAddr.getText().toString(), 1);
	        		//住所を入力して緯度、経度を取得したので、Flagをfalseにする
	                mMap.setOnLongPress_Location_Info_Flag(false);
	        	} else {
	        		//確認用と、GPS,ネットワークで取得した緯度、経度を渡してピンを立てる場合
	        		//取得した緯度、経度のデータをaddrListにセットしている
		           	addrList = (ArrayList<Address>)geocoder.getFromLocation(latitude_Location_Check, longitude_Location_Check , 1);			
	        	}
			
	           	if(addrList != null && addrList.size() > 0) {
	           		//Address情報を取得
	           		addr = addrList.get(0);
	           		
	           		//マップに配置するピンの画像を取得する
	           		Drawable marker = getResources().getDrawable(R.drawable.marker);
	           		//ピンの画像の大きさを整える
	           		marker.setBounds(0, 0, marker.getIntrinsicWidth(), marker.getIntrinsicHeight());
	           		
	           		overLay = new SitesOverLay(marker);
	           		OverlayItem item = new OverlayItem(getPoint(addr.getLatitude(), addr.getLongitude()),
	           													addr.getCountryName(), addr.getAddressLine(1));
	           		//検索ボタンを押した時の処理
	           		if(btnFindFlag==true) {
	           			//住所検索から取得した場所の緯度、経度を登録する時にActivityに渡す変数に保存
	           			latitude_Location_Check = addr.getLatitude();
		           		longitude_Location_Check = addr.getLongitude();
		           		//flagをfalseに戻す
		           		btnFindFlag = false;
	           		}
	           		
	           		
	           		//一枚のoverLayに画像を登録する
	           		overLay.addOverLay(item);
	           		//前にあるoverlayを消去する
	           		mOverLays.clear();
	           		mOverLays.add(overLay);
	                //マップの縮小、拡大セット
	                mMap.getController().setZoom(21);
	           		
	           		//取得した位置情報の場所に飛ぶアニメーション追加
	           		mMap.getController().animateTo(getPoint(addr.getLatitude(), addr.getLongitude()));
	           		
	           		
	           	}
	        
	        } catch (IOException e) {
				showDialog("住所位置情報取得エラー", e.getMessage());
			}
	}
	
	
	 //緯度、経度からGeoPoint取得
    private GeoPoint getPoint(double latitude, double longitude) {
    	int lat = (int)(latitude * 1E6);
    	int lon = (int)(longitude * 1E6);
    	GeoPoint gp = new GeoPoint(lat, lon);
    	return gp;
    }
	
	
	private class SitesOverLay extends ItemizedOverlay<OverlayItem> {
		

		private ArrayList<OverlayItem> items = new ArrayList<OverlayItem>();

		
		public SitesOverLay(Drawable marker) {
			super(boundCenterBottom(marker));
			// TODO Auto-generated constructor stub
		}
		
		public void addOverLay(OverlayItem item) {
			items.add(item);
			
			populate();
		}
	

		@Override
		protected OverlayItem createItem(int i) {
			// TODO Auto-generated method stub
			return items.get(i);
		}
		
		
		

		@Override
		public int size() {
			// TODO Auto-generated method stub
			return items.size();
		}
		
		@Override
		public boolean onTap(int i) {
			// TODO Auto-generated method stub
			OverlayItem item = items.get(i);
			showDialog(item.getTitle(), item.getSnippet());
			
			return super.onTap(i);
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
	public void onLocationChanged(Location location) {
		//緯度の取得
		latitude_Location_Check = location.getLatitude();
		//経度の取得
		longitude_Location_Check = location.getLongitude();
		
		//MapView表示とピン画像の表示
		makePositionDrawable();	
		
		//GPSから位置情報が取得でき、MapViewにピンを配置できたら、処理中のダイアログを閉じる
		mDialog.dismiss();
		
		//ネットワークのリソースを解放する
        mLocationManager.removeUpdates(this);
        //最後に取得した位置情報を登録する
        //GPSまたはネットワークで位置情報を取得したので、Flagをfalseにする
        mMap.setOnLongPress_Location_Info_Flag(false);

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
	
	Handler handler_gps = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			//LocationManagerの取得
	        mLocationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
	        //位置情報更新の設定（更新時間0秒、更新距離0m）
	        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, Location_Check_Activity.this);		

		};
	};
	
	
	Handler handler_network = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			//LocationManagerの取得
	        mLocationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
	        //位置情報更新の設定（更新時間0秒、更新距離0m）
	        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, Location_Check_Activity.this);		

		};
	};	
	
	
	
	@Override
	protected void onPause() {
		//Set Activityに戻るので、デフォルト値のfalseに戻す
		key_Set_Add_Activity = false;
		//Set Activityに戻るので、デフォルト値のfalseに戻す
		key_set = false;
		//ネットワークのリソースを解放する
		if(!(mLocationManager==null)) {
	        mLocationManager.removeUpdates(this);			
		}
        super.onPause();
		
	}



}
