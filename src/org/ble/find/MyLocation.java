package org.ble.find;

import java.util.List;

import org.ble.demo.PeripheralActivity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

public class MyLocation {

    private LocationManager locationManager;
	private String provider;
	
	public boolean initLocation(PeripheralActivity peripheralActivity){

	  locationManager = (LocationManager) peripheralActivity.getSystemService(Context.LOCATION_SERVICE);
			// 获取所有可用的位置提供器
			List<String> providerList = locationManager.getProviders(true);
			if (providerList.contains(LocationManager.GPS_PROVIDER)) {
				provider = LocationManager.GPS_PROVIDER;
			//	Log.e("GPS",provider+"loc_no");   //调用了gps
			} else if (providerList.contains(LocationManager.NETWORK_PROVIDER)) {
				provider = LocationManager.NETWORK_PROVIDER;
			} else {			
				
				return false;
			}

			locationManager.requestLocationUpdates(provider, 3000, 1,   //监听更新位置信息    
			locationListener);
			return true;
			
	}
	LocationListener locationListener=new LocationListener() {
		
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onLocationChanged(Location location) {
			// TODO Auto-generated method stub
			//更新位置
		}
	};
	public String getLocation(String mDeviceAddress){
	 	/*new Thread(new Runnable() {					
			@Override
			public void run() {*/
				Log.e("loction","loc_inR");
				//TODO 获取定位					 
				Location myLocation = locationManager.getLastKnownLocation(provider);   //获得上次地理位置坐标
				if (myLocation != null) {
					Log.e("loction","loc_noNull");   //进不来？？？
					// 显示当前设备的位置信息
					final StringBuilder loc = new StringBuilder();
					loc.append(myLocation.getLatitude()).append(",").append(myLocation.getLongitude());  //经纬度				
							//地址待改，把此时地位信息发至服务端
							 boolean loction = Get_PostUtil.sendPost("http://youfoundme.sinaapp.com/location", 
	           						"address="+mDeviceAddress+"&location="+loc.toString());	
							if(loction) Log.e("loction",loc.toString());
							Log.e("loction","loc"+loc.toString());
							return loc.toString();
					
				}else {
					Location locatio = locationManager.getLastKnownLocation(provider);   //获得此时地理位置坐标
					Log.e("loction",locatio+"locNull");
				}
				return "noLoc";
				/*locationManager.requestLocationUpdates(provider, 5000, 1,   //更新位置信息    
						locationListener);*/
//			}
//		}).start();
	}
	
	

	
}