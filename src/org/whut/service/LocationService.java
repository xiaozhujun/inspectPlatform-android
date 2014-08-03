package org.whut.service;


import org.whut.platform.MainActivity;
import org.whut.utils.LocationInit;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
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


import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

public class LocationService extends Service implements BDLocationListener{

	public LocationClient mLocationClient = null;
	public MKSearch mkSearch;

	private double latitude;
	private double longtitude;
	private GeoPoint point;
	
	private String info;

	
	@Override
	public void onReceiveLocation(BDLocation arg0) {
		// TODO Auto-generated method stub
		Log.i("MyService", "------> onReceiveLocation()");
		
		latitude = arg0.getLatitude();
		longtitude = arg0.getLongitude();
		
		
		point = new GeoPoint((int)(latitude * 1e6),(int)(longtitude * 1e6));
		
		//地址反解析
		mkSearch.reverseGeocode(point);
		
		Message message = Message.obtain();
		
		message.obj = latitude+";"+longtitude;
		
		//获取经纬度信息成功
		message.what = 3;
		
		MainActivity.handler.sendMessage(message);
		
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
	
		mLocationClient = new LocationClient(getApplicationContext());
		
		mkSearch = new MKSearch();
		mkSearch.init(MainActivity.mapManager, new MySearchListener());
		
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
				Message msg = Message.obtain();
				//定位失败
				msg.what = 4;
				MainActivity.handler.sendMessage(msg);
			} else {
				Log.i("MyService", "------>onGetAddrResult()");
				info = arg0.strAddr+";"+arg0.addressComponents.city;
				Message msg = Message.obtain();
				//定位成功
				msg.what = 5;
				//info=解析出来的地址+“;”+解析出的城市信息
				msg.obj = info;
				Log.i("msg", info);
				MainActivity.handler.sendMessage(msg);
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
