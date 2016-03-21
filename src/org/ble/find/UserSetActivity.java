package org.ble.find;

import org.ble.demo.R;
import org.ble.demo.ScanningActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class UserSetActivity extends Activity {
	private String phone; 
	private String password;
	
	private String allmac;
//	private String xString;
	String[] allName;
	String[] allAddress;
	private String selectName ;
	private String selectAddress;
	
	private Button bindDervice;
	private Button putWord;
	private Button loseFlag;
	private Button changeName;
	private Button lostList;
	private Button findLocation;
	/*选择标记丢失还是寻回*/
	private String changeLost="0";	
	private static final int SUSSECE=1;
	private static final int FAILE=0;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_set_activity);
		Intent intent=this.getIntent();
		phone=intent.getStringExtra("phone");
		password=intent.getStringExtra("password");
		
		Log.d("the user message in bundle", phone+password);
		
		bindDervice=(Button)findViewById(R.id.bind_dervice);
		putWord=(Button)findViewById(R.id.put_word);
		loseFlag=(Button)findViewById(R.id.lose_flag);
		changeName=(Button)findViewById(R.id.change_name);
		lostList=(Button)findViewById(R.id.lose_list);
		findLocation=(Button)findViewById(R.id.find_location);
		/*绑定设备*/		
		bindDervice.setOnClickListener(new OnClickListener() {		
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(UserSetActivity.this,ScanningActivity.class);
				intent.putExtra(ScanningActivity.EXTRAS_PHONE,phone);
				intent.putExtra(ScanningActivity.EXTRAS_NAME,"no");
				startActivity(intent);
			}
		});
		/*发布留言*/	
		putWord.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						Thread thread4 = new Thread(new Runnable() {
							
							@Override
							public void run() {
								allmac = Get_PostUtil.sendGet("http://youfoundme.sinaapp.com/auth/userList",
										"phone=" + phone);                   //字符串数组   返回用户设备列表 名称和标志
								allName=Get_PostUtil.parseJSONArray(allmac, "name");  //所有已绑定设备名
								allAddress=Get_PostUtil.parseJSONArray(allmac, "address");  //所有已绑定设备地址
								//dialogWord();
							}
						});				
						thread4.start();
						dialogWord();
					}
				});
		/*丢失标记*/	
		loseFlag.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Thread thread = new Thread(new Runnable() {
					
					@Override
					public void run() {
						allmac = Get_PostUtil.sendGet("http://youfoundme.sinaapp.com/auth/userList",
								"phone=" + phone);                   //字符串数组   返回用户设备列表 名称和标志
						allName=Get_PostUtil.parseJSONArray(allmac, "name");  //所有已绑定设备名
						allAddress=Get_PostUtil.parseJSONArray(allmac, "address");  //所有已绑定设备地址
					}
				});				
				thread.start();
				dialogLose();   //选择标记丢失还是寻回
			}

		});
		/*修改设备名*/	
		changeName.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {				
				Intent intent=new Intent(UserSetActivity.this,ScanningActivity.class);
				intent.putExtra(ScanningActivity.EXTRAS_NAME,"changeName");
				intent.putExtra(ScanningActivity.EXTRAS_PHONE,phone);
				startActivity(intent);
			}
		});
		/*查看丢失列表*/	
		lostList.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub  webView
				
			}
		});
		/*查看定位*/	
		findLocation.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	private void dialogWord() {
		AlertDialog.Builder builder = new Builder(UserSetActivity.this);
		builder.setTitle("请选择留言的设备");
		selectName = allName[0];
		selectAddress=allAddress[0];
		builder.setSingleChoiceItems(allName, 0,new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				selectName = allName[which];
				selectAddress=allAddress[which];
			}
		}); 
		builder.setPositiveButton("确认",new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//发布留言
				dialog.dismiss();	//先dismiss			
			}
		});
		builder.setNegativeButton("取消",null);
		builder.create().show();
		
	}
	
	private void dialogLose() {
		AlertDialog.Builder builder = new Builder(UserSetActivity.this);
		builder.setTitle("提示");
		builder.setTitle("标记选择");
		builder.setNegativeButton("已丢失", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Thread thread1 =new Thread(new Runnable() {
					
					@Override
					public void run() {					
						changeLost="1";
					}
				});
				thread1.start();
				dialog.dismiss();
				derviceDialog();
				
			}
		});
		builder.setPositiveButton("已寻回", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Thread thread2 = new Thread(new Runnable() {
					
					@Override
					public void run() {
						changeLost="0";
					}
				});
				thread2.start();
				dialog.dismiss();
				derviceDialog();
				
			}
		});
		builder.create().show();
		
	}
	
	private void derviceDialog() {
		AlertDialog.Builder builder = new Builder(UserSetActivity.this);
		builder.setTitle("选择要标记的设备");
		selectName = allName[0];
		selectAddress=allAddress[0];
		builder.setSingleChoiceItems(allName, 0,new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				selectName = allName[which];
				selectAddress=allAddress[which];
			}
		}); 
		builder.setPositiveButton("确认",new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Thread thread3 = new Thread(new Runnable() {
					
					@Override
					public void run() {
						//TODO
						boolean postSussece = Get_PostUtil.sendPost("http://youfoundme.sinaapp.com/auth/addressList", 
								"phone="+phone+"&address="+selectAddress+"&name="+selectName+"&lostFlag="+changeLost);
						Message message=new Message();
						if(postSussece)	{							
							message.what=SUSSECE;
							message.arg1=1;
							handler.sendMessage(message);
						}else{
							message.what=FAILE;
							message.arg1=1;
							handler.sendMessage(message);				
						}
						Log.e("selectname",selectName+"___"+changeLost+"___"+phone);
					}
				});
				thread3.start();

			}
		});
		builder.setNegativeButton("取消",null);
		builder.show();
		
	}
	
	
	private Handler handler=new Handler(){
		public void handleMessage(Message msg){
			switch(msg.what){
				case SUSSECE:
					if(msg.arg1==1){
					Toast.makeText(UserSetActivity.this, "标记成功", Toast.LENGTH_SHORT).show();
				}else if(msg.arg1==2){
					Toast.makeText(UserSetActivity.this, "已发布留言", Toast.LENGTH_SHORT).show();
				}
				case FAILE:
					if(msg.arg1==1){
					Toast.makeText(UserSetActivity.this, "标记失败", Toast.LENGTH_SHORT).show();
				}else if(msg.arg1==2){
					Toast.makeText(UserSetActivity.this, "留言发布失败", Toast.LENGTH_SHORT).show();
				}
			}
		}
	};
	
	
	
	
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) 
    {
			     if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN)
			     {
			             
			    	 AlertDialog.Builder dialog = new Builder(UserSetActivity.this);
					   dialog.setTitle("是否退出登录");
					 dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							finish();						    
						}
						
					}); 
						
					 dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								   dialog.cancel();
							}
						}); 
					 dialog.show();								
			     }
			   return super.onKeyDown(keyCode, event);
    }

}
