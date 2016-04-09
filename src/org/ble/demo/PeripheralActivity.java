package org.ble.demo;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.ble.demo.R;
import org.ble.find.Get_PostUtil;
import org.ble.find.MainActivity;
import org.ble.find.MyLocation;

import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class PeripheralActivity extends Activity implements BleWrapperUiCallbacks, OnClickListener {	
    public static final String EXTRAS_DEVICE_NAME    = "BLE_DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "BLE_DEVICE_ADDRESS";
    public static final String EXTRAS_DEVICE_RSSI    = "BLE_DEVICE_RSSI";
	protected static final int GET_WORD = 1;
	protected static final int LOCATION=2;
    
    public enum ListType {
    	GATT_SERVICES,
    	GATT_CHARACTERISTICS,
    	GATT_CHARACTERISTIC_DETAILS
    }
    
    private ListType mListType = ListType.GATT_SERVICES;
    private String mDeviceName;
    private String mDeviceAddress;
    private String mDeviceRSSI;

    private BleWrapper mBleWrapper;
  //  private MyLocation location;
    
    private TextView mDeviceNameView;
    private TextView mDeviceAddressView;
    private TextView mDeviceRssiView;
    private TextView mDeviceStatus;
    private ListView mListView;
    private View     mListViewHeader;
    private TextView mHeaderTitle;
    private TextView mHeaderBackButton;
    private ServicesListAdapter mServicesListAdapter = null;
    private CharacteristicsListAdapter mCharacteristicsListAdapter = null; 
    private CharacteristicDetailsAdapter mCharDetailsAdapter = null;  
    
    private Button setDistance;
    private Button getWord;
    private Button openBear;
    private Button closeBear;
    
    private BluetoothGattCharacteristic mCharacteristic;
    //�趨Ϊ ������ͳһ��׼
    private UUID SV_UUID=UUID.fromString("0000fff0-0000-1000-8000-00805f9b34fb");
    private UUID CR_UUID=UUID.fromString("0000fff1-0000-1000-8000-00805f9b34fb");
    private int rssi; 
    private double bearFlag=2.0;
    private int bearCount=0;
    private String responseWord;
    
	private MyLocation location;
	public String return_Loc;

    /*�豸������*/
   public void uiDeviceConnected(final BluetoothGatt gatt,
			                      final BluetoothDevice device)
    {
	   Log.e("��־", "11");
	 	// mCharacteristic = gatt.getService(SV_UUID).getCharacteristic(CR_UUID);  //д�������������������
	     //setButtonView(true);
	   //  Log.e("��־", "yyy");
	   //Ҫ������̨���񣿣�
	   	 runOnUiThread(new Runnable() {
			@Override
			public void run() {
				 setButtonView(true);
				Log.e("��־", "aaa");
				mDeviceStatus.setText("������");				
				invalidateOptionsMenu();
			}
    	});
    }
   /*�豸δ����*/
    public void uiDeviceDisconnected(final BluetoothGatt gatt,
			                         final BluetoothDevice device)
    {  
		Log.e("��־", "22");
	  //����λ��Ϣ������������
    	
    	
    	runOnUiThread(new Runnable() {
			@Override
			public void run() {
				 setButtonView(false);
				mDeviceStatus.setText("δ����");
				mServicesListAdapter.clearList();
				mCharacteristicsListAdapter.clearList();
				mCharDetailsAdapter.clearCharacteristic();
				
				invalidateOptionsMenu();
				
			//	mHeaderTitle.setText("");
				mHeaderBackButton.setVisibility(View.INVISIBLE);
				mListType = ListType.GATT_SERVICES;
				mListView.setAdapter(mServicesListAdapter);
			}
    	});    	
    }
    
    public void uiNewRssiAvailable(final BluetoothGatt gatt,
    							   final BluetoothDevice device,
    							   final int rssi)
    {
    	Log.e("��־", "33");
    	runOnUiThread(new Runnable() {
	    	@Override
			public void run() {

				double a=40.0;
				double s=0.0000;
				s=Math.pow(10, (-rssi-66)/a);    //rssiתΪ����	
				int random = (int)(Math.random()*100);
				if(s>bearFlag){
					bearCount++;
				}
				if(bearCount>=3){
					bearCount=0;
					bear(1);
				}
			    mDeviceRSSI = (rssi == 0) ? "00.000��" : String.valueOf(s).substring(0, 3)+random + "��";  //ȡ3λС��
				mDeviceRssiView.setText(mDeviceRSSI);
			}
		});    	
    }
    
    public void uiAvailableServices(final BluetoothGatt gatt,
    						        final BluetoothDevice device,
    							    final List<BluetoothGattService> services)
    {
    	 Log.e("��־", "44");
    	runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mServicesListAdapter.clearList();
				mListType = ListType.GATT_SERVICES;
				mListView.setAdapter(mServicesListAdapter);
				mHeaderTitle.setText(mDeviceName + "\'s services:");
				mHeaderBackButton.setVisibility(View.INVISIBLE);
				
    			for(BluetoothGattService service : mBleWrapper.getCachedServices()) {
            		mServicesListAdapter.addService(service);
            	}				
    			mServicesListAdapter.notifyDataSetChanged();
			}    		
    	});
    }
   
    public void uiCharacteristicForService(final BluetoothGatt gatt,
    				 					   final BluetoothDevice device,
    									   final BluetoothGattService service,
    									   final List<BluetoothGattCharacteristic> chars)
    {
    	Log.e("��־", "55");
    //	mCharacteristic = gatt.getService(SV_UUID).getCharacteristic(CR_UUID);
    	runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mCharacteristicsListAdapter.clearList();
		    	mListType = ListType.GATT_CHARACTERISTICS;
		    	mListView.setAdapter(mCharacteristicsListAdapter);
		    	mHeaderTitle.setText(BleNamesResolver.resolveServiceName(service.getUuid().toString().toLowerCase(Locale.getDefault())) + "\'s characteristics:");
		    	mHeaderBackButton.setVisibility(View.VISIBLE);
		    	
		    	for(BluetoothGattCharacteristic ch : chars) {
		    		mCharacteristicsListAdapter.addCharacteristic(ch);
		    	}
		    	mCharacteristicsListAdapter.notifyDataSetChanged();
			}
    	});
    }
    
    public void uiCharacteristicsDetails(final BluetoothGatt gatt,
					 					 final BluetoothDevice device,
										 final BluetoothGattService service,
										 final BluetoothGattCharacteristic characteristic)
    {
    	Log.e("��־", "66");
    
    	runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mListType = ListType.GATT_CHARACTERISTIC_DETAILS;
				mListView.setAdapter(mCharDetailsAdapter);
		    	mHeaderTitle.setText(BleNamesResolver.resolveCharacteristicName(characteristic.getUuid().toString().toLowerCase(Locale.getDefault())) + "\'s details:");
		    	mHeaderBackButton.setVisibility(View.VISIBLE);
		    	
		    	mCharDetailsAdapter.setCharacteristic(characteristic);
		    	mCharDetailsAdapter.notifyDataSetChanged();
			}
    	});
    }

    public void uiNewValueForCharacteristic(final BluetoothGatt gatt,
											final BluetoothDevice device,
											final BluetoothGattService service,
											final BluetoothGattCharacteristic characteristic,
											final String strValue,
											final int intValue,
											final byte[] rawValue,
											final String timestamp)
    {
    	Log.e("��־", "77");
    	if(mCharDetailsAdapter == null || mCharDetailsAdapter.getCharacteristic(0) == null) return;
    	runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mCharDetailsAdapter.newValueForCharacteristic(characteristic, strValue, intValue, rawValue, timestamp);
				mCharDetailsAdapter.notifyDataSetChanged();
			}
    	});
    }
 
	public void uiSuccessfulWrite(final BluetoothGatt gatt,
            					  final BluetoothDevice device,
            					  final BluetoothGattService service,
            					  final BluetoothGattCharacteristic ch,
            					  final String description,
            					  final boolean flag
            					  )
	{
		
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				//Toast.makeText(getApplicationContext(), "Writing to " + description + " was finished successfully!", Toast.LENGTH_LONG).show();
				Toast.makeText(getApplicationContext(), "����ָ����ɹ�", Toast.LENGTH_SHORT).show();
					Log.e("��־", "88");
					
				}
		});
	}
	
	public void uiFailedWrite(final BluetoothGatt gatt,
							  final BluetoothDevice device,
							  final BluetoothGattService service,
							  final BluetoothGattCharacteristic ch,
							  final String description)
	{
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				//Toast.makeText(getApplicationContext(), "Writing to " + description + " FAILED!", Toast.LENGTH_LONG).show();
				Toast.makeText(getApplicationContext(), "����ָ��δ�ܷ���", Toast.LENGTH_SHORT).show();
			}
		});	
	}

	
	public void uiGotNotification(final BluetoothGatt gatt,
								  final BluetoothDevice device,
								  final BluetoothGattService service,
								  final BluetoothGattCharacteristic ch)
	{
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				// at this moment we only need to send this "signal" do characteristic's details view
				mCharDetailsAdapter.setNotificationEnabledForService(ch);
			}			
		});
	}

	@Override
	public void uiDeviceFound(BluetoothDevice device, int rssi, byte[] record) {
		// no need to handle that in this Activity (here, we are not scanning)
	}  	
	
    private AdapterView.OnItemClickListener listClickListener = new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			--position; // we have header so we need to handle this by decrementing by one
			if(position < 0) { // user have clicked on the header - action: BACK
				if(mListType.equals(ListType.GATT_SERVICES)) return;
				if(mListType.equals(ListType.GATT_CHARACTERISTICS)) {
					uiAvailableServices(mBleWrapper.getGatt(), mBleWrapper.getDevice(), mBleWrapper.getCachedServices());
					mCharacteristicsListAdapter.clearList();
					return;
				}
				if(mListType.equals(ListType.GATT_CHARACTERISTIC_DETAILS)) {
					mBleWrapper.getCharacteristicsForService(mBleWrapper.getCachedService());
					mCharDetailsAdapter.clearCharacteristic();
					return;
				}
			}
			else { // user is going deeper into the tree (device -> services -> characteristics -> characteristic's details) 
				if(mListType.equals(ListType.GATT_SERVICES)) {
					BluetoothGattService service = mServicesListAdapter.getService(position);
					mBleWrapper.getCharacteristicsForService(service);
				}
				else if(mListType.equals(ListType.GATT_CHARACTERISTICS)) {
					BluetoothGattCharacteristic ch = mCharacteristicsListAdapter.getCharacteristic(position);
					uiCharacteristicsDetails(mBleWrapper.getGatt(), mBleWrapper.getDevice(), mBleWrapper.getCachedService(), ch);
				} 
			}
		}     	
	};

	 
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_peripheral);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		mListViewHeader = (View) getLayoutInflater().inflate(R.layout.peripheral_list_services_header, null, false);
		//�ؼ���ʼ��
		connectViewsVariables();
		//����豸��Ϣ
        final Intent intent = getIntent();
        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);
        rssi = intent.getIntExtra(EXTRAS_DEVICE_RSSI, 0);  //TODOתΪ  ��
        double a=40.0;
		double s=0.0000;
		s=Math.pow(10, (-rssi-66)/a);    //rssiתΪ����	
		Log.e("RSSI",""+String.valueOf(s));
		int random = (int)(Math.random()*100);
        mDeviceRSSI = (rssi == 0) ? "00.000��" : String.valueOf(s).substring(0, 3) +random+ "��";  //ȡ3λС��String.valueOf(s).subString(0,5)�ᱨ����֪��Ϊʲô
        
        mDeviceNameView.setText(mDeviceName);
        mDeviceAddressView.setText(mDeviceAddress);
        mDeviceRssiView.setText(mDeviceRSSI);
        getActionBar().setTitle(mDeviceName);
        
        mListView.addHeaderView(mListViewHeader);
        mListView.setOnItemClickListener(listClickListener);
        
        setDistance.setOnClickListener(this);
        getWord.setOnClickListener(this);
        openBear.setOnClickListener(this);
        closeBear.setOnClickListener(this);        
      //��λ��ʼ��
        location=new MyLocation();
       boolean isCanLoc = location.initLocation(PeripheralActivity.this);
       if(!isCanLoc){
    	   Toast.makeText(PeripheralActivity.this, "û�ж�λȨ��", Toast.LENGTH_LONG).show();
       }
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if(mBleWrapper == null) mBleWrapper = new BleWrapper(this, this);
		
		if(mBleWrapper.initialize() == false) {
			finish();
		}
		
		if(mServicesListAdapter == null) mServicesListAdapter = new ServicesListAdapter(this);
		if(mCharacteristicsListAdapter == null) mCharacteristicsListAdapter = new CharacteristicsListAdapter(this);
		if(mCharDetailsAdapter == null) mCharDetailsAdapter = new CharacteristicDetailsAdapter(this, mBleWrapper);
		
		mListView.setAdapter(mServicesListAdapter);
		mListType = ListType.GATT_SERVICES;
		mHeaderBackButton.setVisibility(View.INVISIBLE);
	//	mHeaderTitle.setText("");
		
		// start automatically connecting to the device
    	//mDeviceStatus.setText("������ ...");
    	 mBleWrapper.connect(mDeviceAddress);
 
	};
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.peripheral, menu);
		if (mBleWrapper.isConnected()) {
	        menu.findItem(R.id.device_connect).setVisible(false);
	        menu.findItem(R.id.device_disconnect).setVisible(true);
	    } else {
	        menu.findItem(R.id.device_connect).setVisible(true);
	        menu.findItem(R.id.device_disconnect).setVisible(false);
	    }		
		return true;
	}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.device_connect:
            	mDeviceStatus.setText("������ ...");
            	mBleWrapper.connect(mDeviceAddress);
                return true;
            case R.id.device_disconnect:
            	mBleWrapper.diconnect();
                return true;
            case android.R.id.home:
            	mBleWrapper.diconnect();
            	mBleWrapper.close();
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }	

   /*��ʼ��*/ 
    private void connectViewsVariables() {
    	mDeviceNameView = (TextView) findViewById(R.id.peripheral_name);
		mDeviceAddressView = (TextView) findViewById(R.id.peripheral_address);
		mDeviceRssiView = (TextView) findViewById(R.id.peripheral_rssi);
		mDeviceStatus = (TextView) findViewById(R.id.peripheral_status);
		
		setDistance=(Button)findViewById(R.id.set_distance);
		getWord=(Button)findViewById(R.id.get_word);
		openBear=(Button)findViewById(R.id.open_bear);
		closeBear=(Button)findViewById(R.id.close_bear);
		
		setButtonView(false);  //��ʼ���ð�ť���ɵ��
		
		mListView = (ListView) findViewById(R.id.listView);
		mHeaderTitle = (TextView) mListViewHeader.findViewById(R.id.peripheral_service_list_title);
		mHeaderBackButton = (TextView) mListViewHeader.findViewById(R.id.peripheral_list_service_back);
    }
    
    private void setButtonView(boolean isable){    	
    	setDistance.setEnabled(isable);
    	getWord.setEnabled(isable);
    	openBear.setEnabled(isable);
    	closeBear.setEnabled(isable);
    }
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.set_distance:
			//TODO  ���þ���
			 AlertDialog.Builder builder = new Builder(PeripheralActivity.this);
			 builder.setTitle("�����뱨������(ֻ������)��");
			 final EditText editDistance = new EditText(this);
			 builder.setView(editDistance);
			 builder.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					String dis = editDistance.getText().toString();		
					bearFlag=Double.parseDouble(dis);				//TODO ������ǺϷ����ֻ�����쳣�˳���Ҫ��һ�´�������	
				}
			});
			 builder.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Log.e("�Ի���","ȡ��");
				}
			});
			 builder.show();
			break;
		case R.id.get_word:
			new Thread(new Runnable() {
				
				@Override
				public void run() {
			//		String word=Get_PostUtil.sendGet("http://10.0.2.2/get_word.json");  //�����豸��ǩ�������  TODO ��һ������mDeviceAddress
				//TODO ��ַ�ͽṹ����	
					String word=Get_PostUtil.sendGet("http://youfoundme.sinaapp.com/auth/lostNote", 
															"address="+mDeviceAddress);
					Log.e("��������", word);
					setResponseWord(Get_PostUtil.parseJSON(word, "name"));  //�������ݷ���"address:������Ϣ"
					Message message=new Message();
					message.what=GET_WORD;
					handler.sendMessage(message);
				}
			}).start();
			 
			break;
		case R.id.open_bear:
			bear(1);  //�� 0x01
			break;
		case R.id.close_bear:
			bear(0);  //��0x00
			break;
		default:
			break;
		}
		
	}

	private Handler handler=new Handler(){
			public void handleMessage(Message msg){			
				switch(msg.what){
					case GET_WORD:	
						 AlertDialog.Builder builder2 = new Builder(PeripheralActivity.this);
						 builder2.setTitle("��Ʒ�������ԣ�");
						 builder2.setMessage(getResponseWord());
						 builder2.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
					
							@Override
							public void onClick(DialogInterface dialog, int which) {
								Log.e("����",""+getResponseWord());
							}
						});	
						 builder2.show();
						 break;
					case LOCATION:
						String loc=msg.obj.toString();	
						Log.e("����","���Ͷ�λ��Ϣ"+loc);
						//Toast.makeText(PeripheralActivity.this, "���Ͷ�λ��Ϣ"+loc, Toast.LENGTH_SHORT).show();
				}
		}
	};
	public void bear(final int j){	
		BluetoothGatt gatt = mBleWrapper.getGatt();
		mCharacteristic = gatt.getService(SV_UUID).getCharacteristic(CR_UUID);
//		BluetoothGattCharacteristic ch = mCharacteristicsListAdapter.getCharacteristic(0);
//		uiCharacteristicsDetails(mBleWrapper.getGatt(), mBleWrapper.getDevice(), mBleWrapper.getCachedService(), ch);
		Thread thread=new Thread(new Runnable() {
			@Override
			public void run() {
				if(j==1){
					byte[] byt = hexStringToBytes("0x01");
					mBleWrapper.writeDataToCharacteristic(mCharacteristic, byt);
				}else{
					byte[] byt = hexStringToBytes("0x00");
					mBleWrapper.writeDataToCharacteristic(mCharacteristic, byt);
				}
				
				
			}
		});
		thread.start();
		
	}
	//�ַ���תbuty[]��ʽ
	private byte[] hexStringToBytes(final String hex) {
		String tmp = hex.substring(2).replaceAll("[^[0-9][a-f]]", "");
		byte[] bytes = new byte[tmp.length() / 2]; // every two letters in the string are one byte finally
		
		String part = "";
		
		for(int i = 0; i < bytes.length; ++i) {
			part = "0x" + tmp.substring(i*2, i*2+2);
			bytes[i] = Long.decode(part).byteValue();
		}
		
		return bytes;
	}
	public String getResponseWord() {
		return responseWord;
	}
	public void setResponseWord(String responseWord) {
		this.responseWord = responseWord;
	}
	
	@Override
	protected void onPause() {		
		super.onPause();
		Log.e("onPause", "pause");
	};

	protected void onStop(){
		super.onStop();
		Log.e("onStop", "stop");
	}
	protected void onDestroy(){	
		new Thread(new Runnable() {					
			@Override
			public void run() {
			   return_Loc=location.getLocation(mDeviceAddress);
			   parseTheLocation(return_Loc);				
			}
			private void parseTheLocation(String location) {
				try {					
					String[] loc = location.split(",");
					double lat = Double.parseDouble(loc[0]);
					double lng = Double.parseDouble(loc[1]);
					Geocoder geo=new Geocoder(getApplicationContext());
					List<android.location.Address> str=geo.getFromLocation(lat,lng,0);

					String result=str.get(0).toString();						
					result=result.substring(result.indexOf("\""), result.indexOf(","));  //���ݽ�����ֵ�
					 String param="";
					   try {
							param = "address="+URLEncoder.encode((mDeviceAddress),"utf-8")
									+"&location="+URLEncoder.encode((result),"utf-8");								
						} catch (UnsupportedEncodingException e) {
							Log.e("����", "�쳣"+e.toString());
							e.printStackTrace();
						}
					 boolean loction = Get_PostUtil.sendPost("http://youfoundme.sinaapp.com/location", 
							 param);	
					if(loction) Log.e("����λ��",result);
					
					Message message=new Message();
					message.what=LOCATION;
					message.obj=result;
					handler.sendMessage(message);
				} catch (Exception e) {
					 Log.e("�쳣",e.toString());
					e.printStackTrace();
				}
			}
		}).start();
		mServicesListAdapter.clearList();
		mCharacteristicsListAdapter.clearList();
		mCharDetailsAdapter.clearCharacteristic();		
		mBleWrapper.stopMonitoringRssiValue();
		mBleWrapper.diconnect();
		mBleWrapper.close();                 //ԭ����onpause�������Ͷ���
		Log.e("onDestroy", "destroy");		
		super.onDestroy();
	}
}
