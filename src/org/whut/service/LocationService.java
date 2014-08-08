package org.whut.service;

import org.whut.entity.Location;
import org.whut.utils.LocationInit;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKGeneralListener;
import com.baidu.mapapi.search.MKAddrInfo;
import com.baidu.mapapi.search.MKBusLineResult;
import com.baidu.mapapi.search.MKDrivingRouteResult;
import com.baidu.mapapi.search.MKPoiResult;
import com.baidu.mapapi.search.MKSearch;
import com.baidu.mapapi.search.MKSearchListener;
import com.baidu.mapapi.search.MKShareUrlResult;
import com.baidu.mapapi.search.MKSuggestionResult;
import com.baidu.mapapi.search.MKTransitRouteResult;
import com.baidu.mapapi.search.MKWalkingRouteResult;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import android.annotation.SuppressLint;
import android.app.Service;

import android.content.Intent;

import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class LocationService extends Service implements BDLocationListener{

	public BMapManager mapManager = null;
	public LocationClient mLocationClient = null;
	public MKSearch mkSearch;
	
	private double latitude;
	private double longtitude;
	private GeoPoint point;
	
	private String address;
	private String city;
	private Location locationData;
	
	//发送定位请求的Activity
	private String activity = null;
	
	
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			Log.i("MyService", "----------handleMessage()");
			Intent serviceIntent = new Intent();
			Log.i("msg",activity);
			serviceIntent.setAction(activity);
			serviceIntent.putExtra("locationData", locationData);
			serviceIntent.putExtra("city", city);
			serviceIntent.putExtra("locationService", true);
			sendBroadcast(serviceIntent);
		};
	};
	
	
	@Override
	public void onReceiveLocation(BDLocation arg0) {
		// TODO Auto-generated method stub
		Log.i("MyService", "------> onReceiveLocation()");
		
		latitude = arg0.getLatitude();
		longtitude = arg0.getLongitude();
		
		point = new GeoPoint((int)(latitude * 1e6),(int)(longtitude * 1e6));
		
		//地址反解析
		mkSearch.reverseGeocode(point);
		
		locationData.setLat(latitude+"");
		
		locationData.setLng(longtitude+"");
	
		mLocationClient.stop();
	}

	@Override
	public void onReceivePoi(BDLocation arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		Log.i("MyService", "------> onBind()");
		
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		Log.i("MyService", "------> onCreate()");
	
		mapManager = new BMapManager(getApplicationContext());
		
		mapManager.init(new MKGeneralListener() {

			@Override
			public void onGetPermissionState(int arg0) {

				Toast.makeText(getApplicationContext(),
						"密钥错误", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onGetNetworkState(int arg0) {

				Toast.makeText(getApplicationContext(),

						"网络错误", Toast.LENGTH_SHORT).show();
			}
		});
		
		
		locationData = new Location();
		
		mLocationClient = new LocationClient(getApplicationContext());
		
		mkSearch = new MKSearch();
		
		mkSearch.init(mapManager, new MySearchListener());
		
		//为LocationClient设置监听器，一旦获取了结果，会回调listener中的onReceiveLocation方法
		mLocationClient.registerLocationListener(this);
		//设置定位的参数，包括坐标编码方式，定位方式等等
		mLocationClient.setLocOption(new LocationInit().getOption());
		//启动定位
		mLocationClient.start();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.i("MyService", "------> onDestroy()");
		if(mLocationClient.isStarted()){
			mLocationClient.stop();
		}		
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		Log.i("MyService", "------> onStartCommand()");
		
		activity = intent.getStringExtra("activity");
		
		Log.i("MyService", "------->"+activity+"正在调用LocationService");
		
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public boolean onUnbind(Intent intent) {
		// TODO Auto-generated method stub
		Log.i("MyService", "------> onUnbind()");
		return super.onUnbind(intent);
	}

	
	public class MySearchListener implements MKSearchListener{

		@Override
		public void onGetAddrResult(MKAddrInfo arg0, int arg1) {
			// TODO Auto-generated method stub
			if (arg1!= 0 || arg0 == null) {
				Log.i("MyService", "----->onGetAddrResult 参数空");
			} else {
				Log.i("MyService", "------>onGetAddrResult()");
				city = arg0.addressComponents.city;
				address = arg0.strAddr;				
				locationData.setAddress(address);
				handler.sendEmptyMessage(0);
			}
		}

		@Override
		public void onGetBusDetailResult(MKBusLineResult arg0, int arg1) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onGetDrivingRouteResult(MKDrivingRouteResult arg0, int arg1) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onGetPoiDetailSearchResult(int arg0, int arg1) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onGetPoiResult(MKPoiResult arg0, int arg1, int arg2) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onGetShareUrlResult(MKShareUrlResult arg0, int arg1,
				int arg2) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onGetSuggestionResult(MKSuggestionResult arg0, int arg1) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onGetTransitRouteResult(MKTransitRouteResult arg0, int arg1) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onGetWalkingRouteResult(MKWalkingRouteResult arg0, int arg1) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	
}
