package jp.isao.lost_property_prevention.map;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jp.isao.lost_property_prevention.R;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationListener;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class MyMapView extends MapView implements GestureDetector.OnGestureListener{

	//MapViewのイベントが発生
	private GestureDetector gesture = new GestureDetector(this);
	
	private double latitude_Location_Check;
	private double longitude_Location_Check;
	private boolean onLongPress_Location_Info_Flag = false;
	
	public boolean getOnLongPress_Location_Info_Flag() {
		return this.onLongPress_Location_Info_Flag;
	}

	public void setOnLongPress_Location_Info_Flag(
			boolean onLongPressLocationInfoFlag) {
		onLongPress_Location_Info_Flag = onLongPressLocationInfoFlag;
	}



	//List Overlayを宣言
	private List<Overlay> mOverLays;
	private Address addr;
	private SitesOverLay overLay;
	
	
	public double getLatitude_Location_Check() {
		return latitude_Location_Check;
	}

	public void setLatitude_Location_Check(double latitudeLocationCheck) {
		latitude_Location_Check = latitudeLocationCheck;
	}

	public double getLongitude_Location_Check() {
		return longitude_Location_Check;
	}

	public void setLongitude_Location_Check(double longitudeLocationCheck) {
		longitude_Location_Check = longitudeLocationCheck;
	}

	public MyMapView(Context context, String apiKey) {
		super(context, apiKey);
	}
	
	public MyMapView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public MyMapView(Context arg0, AttributeSet arg1) {
		super(arg0, arg1);
	}

	@Override
	public boolean onDown(MotionEvent arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onFling(MotionEvent arg0, MotionEvent arg1, float arg2,
			float arg3) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		
		mOverLays = getOverlays();
		
		//長押ししたときののx座標とy座標を取得する
		GeoPoint temp = this.getProjection().fromPixels((int)e.getX(), (int)e.getY());
		latitude_Location_Check = temp.getLatitudeE6() / 1E6;
		longitude_Location_Check = temp.getLongitudeE6() / 1E6;
		
		Geocoder geocoder = new Geocoder(getContext());

		try {
			
			ArrayList<Address> addrList = (ArrayList<Address>) geocoder.getFromLocation(latitude_Location_Check, longitude_Location_Check, 1);
			
			Address addr = addrList.get(0);
			//住所情報を画面表示
			showDialog("位置情報を取得しました", addr.getAddressLine(1));

			makePositionDrawable();
	        //長押しイベントで位置情報を取得したので、Flagをtrueにする			
			onLongPress_Location_Info_Flag =true;
			
			
		} catch (Exception e1) {
			showDialog("住所位置情報取得エラー", e1.getMessage());
		}
		

	}
	
	private void makePositionDrawable() {
	       if(mOverLays.size() > 0) {
	        	mOverLays.clear();
	        }
	       ArrayList<Address> addrList;
	        Geocoder geocoder = new Geocoder(getContext());
	        
	        try {
	        	//取得した緯度、経度のデータをaddrListにセットしている
	           	addrList = (ArrayList<Address>)geocoder.getFromLocation(latitude_Location_Check, longitude_Location_Check , 1);			
			
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
	           		//登録した画像データ（ピン）を一枚の層に複数item(ピン)を追加している
	           		overLay.addOverLay(item);
	           		//前にあるoverlayを消去する
	           		mOverLays.clear();
	           		//overLayに登録した一枚の層に、既に登録されている層に追加する
	           		//mOverLaysに登録することにより表示ができる
	           		mOverLays.add(overLay);
	                //マップの縮小、拡大セット
	                this.getController().setZoom(21);
	           		
	           		//取得した位置情報の場所に飛ぶアニメーション追加
	           		this.getController().animateTo(getPoint(addr.getLatitude(), addr.getLongitude()));
	           		
	           		
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
	

	@Override
	public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2,
			float arg3) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onShowPress(MotionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onSingleTapUp(MotionEvent arg0) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		gesture.onTouchEvent(ev);
		//親に渡す必要がある、親に渡さないと固まってしまう
		return super.onTouchEvent(ev);
	}
	
	//アラートダイアログの表示
    private void showDialog(String title, String message) {
    	AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
    	dialog.setTitle(title);
    	dialog.setMessage(message);
    	dialog.setPositiveButton("確認", null);
    	dialog.show();
    }
	

}
