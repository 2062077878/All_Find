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
			// ��ȡ���п��õ�λ���ṩ��
			List<String> providerList = locationManager.getProviders(true);
			if (providerList.contains(LocationManager.GPS_PROVIDER)) {
				provider = LocationManager.GPS_PROVIDER;
			//	Log.e("GPS",provider+"loc_no");   //������gps
			} else if (providerList.contains(LocationManager.NETWORK_PROVIDER)) {
				provider = LocationManager.NETWORK_PROVIDER;
			} else {			
				
				return false;
			}

			locationManager.requestLocationUpdates(provider, 3000, 1,   //��������λ����Ϣ    
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
			//����λ��
		}
	};
	public String getLocation(String mDeviceAddress){
	 	/*new Thread(new Runnable() {					
			@Override
			public void run() {*/
				Log.e("loction","loc_inR");
				//TODO ��ȡ��λ					 
				Location myLocation = locationManager.getLastKnownLocation(provider);   //����ϴε���λ������
				if (myLocation != null) {
					Log.e("loction","loc_noNull");   //������������
					// ��ʾ��ǰ�豸��λ����Ϣ
					final StringBuilder loc = new StringBuilder();
					loc.append(myLocation.getLatitude()).append(",").append(myLocation.getLongitude());  //��γ��				
							//��ַ���ģ��Ѵ�ʱ��λ��Ϣ���������
							 boolean loction = Get_PostUtil.sendPost("http://youfoundme.sinaapp.com/location", 
	           						"address="+mDeviceAddress+"&location="+loc.toString());	
							if(loction) Log.e("loction",loc.toString());
							Log.e("loction","loc"+loc.toString());
							return loc.toString();
					
				}else {
					Location locatio = locationManager.getLastKnownLocation(provider);   //��ô�ʱ����λ������
					Log.e("loction",locatio+"locNull");
				}
				return "noLoc";
				/*locationManager.requestLocationUpdates(provider, 5000, 1,   //����λ����Ϣ    
						locationListener);*/
//			}
//		}).start();
	}
	
	

	
}