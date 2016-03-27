package org.ble.demo;


import org.ble.find.Get_PostUtil;
import org.ble.find.UserSetActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class ScanningActivity extends ListActivity {
	
	private static final long SCANNING_TIMEOUT = 5 * 1000; /* 5 seconds */
	private static final int ENABLE_BT_REQUEST_ID = 1;
	
	private static final int BUND=1;
	private static final int UNBUND=2;
	private static final int BUND_FAILE=3;
	private static final int UNBUND_FAILE=4;
	private static final int CHANGE=5;
	private static final int DISCHANGE=6;
	public static final int TRA_NAME=7;
	
	public static final String EXTRAS_PHONE    = "EXTRAS_PHONE";  //区分是寻宝还是绑定设备
	public static final String EXTRAS_NAME    = "EXTRAS_NAME";
	private String mPhone;
	private String name="UnKown";  //设备名
	private boolean mScanning = false;
	private Handler mHandler = new Handler();
	private DeviceListAdapter mDevicesListAdapter = null;
	private BleWrapper mBleWrapper = null;
	private String changeName;	//改名称的标志

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);   
        //setContentView(R.layout.activity_scanning_item);
        
        // create BleWrapper with empty callback object except uiDeficeFound function (we need only that here) 
        mBleWrapper = new BleWrapper(this, new BleWrapperUiCallbacks.Null() {
        	@Override
        	public void uiDeviceFound(final BluetoothDevice device, final int rssi, final byte[] record) {
        		/*new Thread(new Runnable() {
        			
        			@Override
        			public void run() {
        				//获得设备名
        				String deviceName=Get_PostUtil.sendGet("http://youfoundme.sinaapp.com/auth/addressList", 
        						"address="+device.getAddress());			
        				String get_name=Get_PostUtil.parseJSON(deviceName, "name");  //获得设备名
        				Message message=new Message();
        				message.what=TRA_NAME;
        				message.obj=get_name;
        				handler2.sendMessage(message);
        				Log.e("开始获取设备名", get_name+"oncreate");		     				
        				
        			//	handleFoundDevice(device, rssi, record);
        			}
        		}).start();*/
        		
        	//	handleFoundDevice(device, rssi, record,name);
        	}
        });
        
        final Intent intent = getIntent();
        mPhone = intent.getStringExtra(EXTRAS_PHONE);
        changeName=intent.getStringExtra(EXTRAS_NAME);     //扫描时没有传这个
        Log.e("电话", mPhone);
        Log.e("改名字", changeName);
        // check if we have BT and BLE on board
        if(mBleWrapper.checkBleHardwareAvailable() == false) {
        	bleMissing();
        }
    }

    @Override
    protected void onResume() {
    	super.onResume();
    	Log.e("onResume","resume");
    	   mBleWrapper = new BleWrapper(this, new BleWrapperUiCallbacks.Null() {
           	@Override
           	public void uiDeviceFound(final BluetoothDevice device, final int rssi, final byte[] record) {
           		new Thread(new Runnable() {

					@Override
           			public void run() {
           				//获得设备名          多个设备会有问题！！！！！！！！！
           				String deviceName=Get_PostUtil.sendGet("http://youfoundme.sinaapp.com/auth/userList", 
           						"address="+device.getAddress());	
           				Log.e("JSON数据", deviceName+"json");
           				
           				name=Get_PostUtil.parseJSON(deviceName, "name"); 
           				Log.e("解析","jiexi"+name);
           				Message message=new Message();
           				message.what=TRA_NAME;
           				message.obj=device;
           				message.arg1=rssi;
           				Bundle bundle = new Bundle();    
                        bundle.putString("Name",name);  //往Bundle中存放数据        
                        bundle.putByteArray("BY", record);
                        message.setData(bundle);//mes利用Bundle传递数据  
           				handler2.sendMessage(message);
           				Log.e("开始获取设备名", name+"onresume");			     				

           			}
           		}).start();
           		
           		//handleFoundDevice(device, rssi, record,name);
           	}
           });
    	
    	// on every Resume check if BT is enabled (user could turn it off while app was in background etc.)
    	if(mBleWrapper.isBtEnabled() == false) {
			// BT is not turned on - ask user to make it enabled
			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		    startActivityForResult(enableBtIntent, ENABLE_BT_REQUEST_ID);
		    // see onActivityResult to check what is the status of our request
		}
    	
    	// initialize BleWrapper object
        mBleWrapper.initialize();
    	
    	mDevicesListAdapter = new DeviceListAdapter(this);
        setListAdapter(mDevicesListAdapter);
    	
        // Automatically start scanning for devices
    	mScanning = true;
		// remember to add timeout for scanning to not run it forever and drain the battery
		addScanningTimeout();    	
		mBleWrapper.startScanning();		
        invalidateOptionsMenu();
    };
    
    @Override
    protected void onPause() {
    	super.onPause();
    	mScanning = false;    	
    	mBleWrapper.stopScanning();
    	invalidateOptionsMenu();  	
    	mDevicesListAdapter.clearList();
    };
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.scanning, menu);

        if (mScanning) {
            menu.findItem(R.id.scanning_start).setVisible(false);
            menu.findItem(R.id.scanning_stop).setVisible(true);
            menu.findItem(R.id.scanning_indicator)
                .setActionView(R.layout.progress_indicator);  //正在搜索
            menu.findItem(R.id.scanning).setVisible(true);
            menu.findItem(R.id.scanned).setVisible(false);

        } else {
            menu.findItem(R.id.scanning_start).setVisible(true);
            menu.findItem(R.id.scanning_stop).setVisible(false);
            menu.findItem(R.id.scanning_indicator).setActionView(null);
            menu.findItem(R.id.scanning).setVisible(false);
            menu.findItem(R.id.scanned).setVisible(true);

        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.scanning_start:
            	mScanning = true;
            	mBleWrapper.startScanning();
                break;
            case R.id.scanning_stop:
            	mScanning = false;
            	mBleWrapper.stopScanning();
                break;        
        }
        
        invalidateOptionsMenu();
        return true;
    }

    private Handler handler2=new Handler(){
    	public void handleMessage(Message msg){
    		switch(msg.what){
    		case TRA_NAME:
    			BluetoothDevice device1=(BluetoothDevice)msg.obj;
    			name = msg.getData().getString("Name");
    			byte[] record1=msg.getData().getByteArray("BY");
    			int rssi1=msg.arg1;  
    			
    			handleFoundDevice(device1,rssi1, record1,name);    //处理找到的设备
    			Log.e("终极", "zongji"+name);
    			break;
    		}
    	}
    };
    private Handler handler=new Handler(){
	public void handleMessage(Message msg){
		Intent intent=new Intent(ScanningActivity.this,UserSetActivity.class);
		switch(msg.what){
		case BUND:
			Toast.makeText(ScanningActivity.this, "绑定设备成功", Toast.LENGTH_SHORT).show();break;
		case UNBUND:
			Toast.makeText(ScanningActivity.this, "解绑设备成功", Toast.LENGTH_SHORT).show();break;
		case BUND_FAILE:
			Toast.makeText(ScanningActivity.this, "绑定设备失败", Toast.LENGTH_SHORT).show();break;
		case UNBUND_FAILE:
			Toast.makeText(ScanningActivity.this, "解绑设备失败", Toast.LENGTH_SHORT).show();break;
		case CHANGE:
			Toast.makeText(ScanningActivity.this, "修改名称成功", Toast.LENGTH_SHORT).show();break;
		case DISCHANGE:
			Toast.makeText(ScanningActivity.this, "修改名称失败", Toast.LENGTH_SHORT).show();break;
		default :break;
		}
		startActivity(intent);
	}
};
    /* user has selected one of the device 进入相应设备信息*/
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        final BluetoothDevice device = mDevicesListAdapter.getDevice(position);
        if (device == null) return;
        if(mPhone.compareTo("no")==0&&changeName.compareTo("no")==0){    //非登陆扫描
        	
	        final Intent intent = new Intent(ScanningActivity.this, PeripheralActivity.class);
	        //把设备名，标识，RSSI传过去
	        intent.putExtra(PeripheralActivity.EXTRAS_DEVICE_NAME, name);  //device.getName()
	        intent.putExtra(PeripheralActivity.EXTRAS_DEVICE_ADDRESS, device.getAddress());
	        intent.putExtra(PeripheralActivity.EXTRAS_DEVICE_RSSI, mDevicesListAdapter.getRssi(position));	        
	        if (mScanning) {
	            mScanning = false;
	            invalidateOptionsMenu();
	            mBleWrapper.stopScanning();
	        }	
	        startActivity(intent);
        }else if(changeName.compareTo("changeName")==0){ //改设备名
        	 AlertDialog.Builder builder = new Builder(ScanningActivity.this);
			 builder.setTitle("请输入要设定的设备名：");
			 final EditText newN = new EditText(this);
			 builder.setView(newN);
			 builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {	
				@Override
				public void onClick(DialogInterface dialog, int which) {
					name = newN.getText().toString();		
				       //TODO 把新的名字传给服务器	
					new Thread(new Runnable() {
						
						@Override
						public void run() {
							//修改设备名称
							Boolean postSussece=Get_PostUtil.sendPost("http://youfoundme.sinaapp.com/auth/userList", 
									"phone="+mPhone+"&address="+device.getAddress()+"&name="+name+"&flag=2");
							Message message=new Message();
						if(postSussece)	{							
							message.what=CHANGE;
							handler.sendMessage(message);
							Log.e("对话框","改名成功");
						}else{
							message.what=DISCHANGE;
							handler.sendMessage(message);
							Log.e("对话框","改名失败");
						}
						
						}
					}).start();
				}
			});
			 builder.show();
        }else{   
        	/*只要有号码传过来，就表明是要绑定或解绑*/
        	 AlertDialog.Builder builder = new Builder(ScanningActivity.this);
			 builder.setTitle("绑定或解绑设备：");
			 builder.setPositiveButton("绑定", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					new Thread(new Runnable() {					
						@Override
						public void run() {
							//请求绑定
							Boolean postSussece=Get_PostUtil.sendPost("http://youfoundme.sinaapp.com/auth/userList", 
									"phone="+mPhone+"&address="+device.getAddress()+"&name="+name+"&flag=1");
							Log.e("bangding", "请求绑定"+postSussece);
							Message message=new Message();
						if(postSussece)	{							
							message.what=BUND;
							handler.sendMessage(message);
						}else{
							message.what=BUND_FAILE;
							handler.sendMessage(message);				
						}
						
						}
					}).start();
					Log.e("对话框","绑定");
				}
			});
			 builder.setNegativeButton("解绑", new DialogInterface.OnClickListener() {
								@Override
				public void onClick(DialogInterface dialog, int which) {
					new Thread(new Runnable() {
										
						@Override
						public void run() {
						//请求解绑
							Boolean postSussece=Get_PostUtil.sendPost("http://youfoundme.sinaapp.com/auth/userList", 
											"phone="+mPhone+"&address="+device.getAddress()+"&name="+name+"&flag=0");
							Message message=new Message();
							if(postSussece){
									message.what=UNBUND;
									handler.sendMessage(message);
								}else{
									message.what=UNBUND_FAILE;
									handler.sendMessage(message);				
								}					
							}
						}).start();				
					Log.e("对话框","解绑");
				}
			});
			 builder.show();
        }
    }    
   
    /* check if user agreed to enable BT */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // user didn't want to turn on BT
        if (requestCode == ENABLE_BT_REQUEST_ID) {
        	if(resultCode == Activity.RESULT_CANCELED) {
		    	btDisabled();
		        return;
		    }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

	/* make sure that potential scanning will take no longer
	 * than <SCANNING_TIMEOUT> seconds from now on */
	private void addScanningTimeout() {
		Runnable timeout = new Runnable() {
            @Override
            public void run() {
            	if(mBleWrapper == null) return;
                mScanning = false;
                mBleWrapper.stopScanning();
                invalidateOptionsMenu();
            }
        };
        mHandler.postDelayed(timeout, SCANNING_TIMEOUT);
	}    

	/* add device to the current list of devices */
    private void handleFoundDevice(final BluetoothDevice device,
            final int rssi,
            final byte[] scanRecord,String name)      //name是自己加的
    {
    	Log.e("传入名称",name+"g");
		mDevicesListAdapter.addDevice(device, rssi, scanRecord, name);
		mDevicesListAdapter.notifyDataSetChanged();
	}	

    private void btDisabled() {
    	Toast.makeText(this, "Sorry, BT has to be turned ON for us to work!", Toast.LENGTH_LONG).show();
        finish();    	
    }
    
    private void bleMissing() {
    	Toast.makeText(this, "BLE Hardware is required but not available!", Toast.LENGTH_LONG).show();
        finish();    	
    }	
}