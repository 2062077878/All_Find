package org.ble.demo;

import java.util.ArrayList;

import org.ble.demo.R;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class DeviceListAdapter extends BaseAdapter {
	
	private ArrayList<BluetoothDevice> mDevices;
	private ArrayList<byte[]> mRecords;
	private ArrayList<Integer> mRSSIs;
	private LayoutInflater mInflater;    //Instantiates a layout XML file into its corresponding View objects.
	private ArrayList<String> mNames;
	public DeviceListAdapter(Activity par) {
		super();
		mDevices  = new ArrayList<BluetoothDevice>();
		mRecords = new ArrayList<byte[]>();
		mRSSIs = new ArrayList<Integer>();
		mNames=new ArrayList<String>();
		
		mInflater = par.getLayoutInflater();
	}
	public DeviceListAdapter(){
		
	}
	
	public void addDevice(BluetoothDevice device, int rssi, byte[] scanRecord,String name) {
		if(mDevices.contains(device) == false) {
			mDevices.add(device);
			mRSSIs.add(rssi);
			mNames.add(name);
			mRecords.add(scanRecord);
		}
		 Log.e("显示设备名", "deviceListAdapter"+name);	
	}
	public BluetoothDevice getDevice(int index) {
		return mDevices.get(index);
	}
	
	public int getRssi(int index) {
		return mRSSIs.get(index);
	}
	public String getName(int index) {
		 Log.e("显示设备名", "c");	
		return mNames.get(index);
		
	}
	
	public void clearList() {
		mDevices.clear();
		mRSSIs.clear();
		mNames.clear();
		mRecords.clear();
	}
	
	@Override
	public int getCount() {
		return mDevices.size();
	}

	@Override
	public Object getItem(int position) {
		 Log.e("显示设备名", "d");	
		return getDevice(position);
	}

	@Override
	public long getItemId(int position) {
		 Log.e("显示设备名", "e");	
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// get already available view or create new if necessary
		FieldReferences fields;
        if (convertView == null) {
        	 Log.e("显示设备名", "f");	
        	convertView = mInflater.inflate(R.layout.activity_scanning_item, null);
        	fields = new FieldReferences();
        	fields.deviceAddress = (TextView)convertView.findViewById(R.id.deviceAddress);
        	fields.deviceName    = (TextView)convertView.findViewById(R.id.deviceName);
        	fields.deviceRssi    = (TextView)convertView.findViewById(R.id.deviceRssi);
            convertView.setTag(fields);
        } else {
            fields = (FieldReferences) convertView.getTag();
        }			
        Log.e("显示设备名", "x");
        // set proper values into the view
        BluetoothDevice device = mDevices.get(position);
        int rssi = mRSSIs.get(position);   //获得RSSI值
        
        double a=40.0;
		double s=0.0000;
		s=Math.pow(10, (-rssi-66)/a);    //rssi转为距离
		int random = (int)(Math.random()*100);
        String rssiString = (rssi == 0) ? "00.000米" : String.valueOf(s).substring(0, 3)+random + "米";  //取3位小数
      
      //  String name = device.getName();
        String Name =  mNames.get(position);
        if(Name == null || Name.length() <= 0) {
            Log.e("显示设备名", "kong"+Name);
            Name = "NO Device";
        }
          else if(Name.equals("UnKown")) 
        {
        	  Name ="Unknown Device";
        }
        
        String address = device.getAddress();
        
        fields.deviceName.setText(Name);
        fields.deviceAddress.setText(address);
        fields.deviceRssi.setText(rssiString);

		return convertView;
	}

	public class FieldReferences {
		TextView deviceName;
		TextView deviceAddress;
		TextView deviceRssi;
	}
}
