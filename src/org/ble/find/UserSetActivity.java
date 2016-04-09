package org.ble.find;


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import org.ble.find.CircleLayout;
import org.ble.find.CircleLayout.OnItemClickListener;
import org.ble.find.CircleLayout.OnItemSelectedListener;

public class UserSetActivity extends Activity implements OnItemSelectedListener, OnItemClickListener {
	private String phone; 
	private String password;
	
	private String allmac="";
//	private String xString;
	 String[] allName;
	 String[] allAddress;
	private String selectName ;
	private String selectAddress;
		
	private TextView selectedTextView;
	/*选择标记丢失还是寻回*/
	private String changeLost="0";	
	private static final int SUSSECE=1;
	private static final int FAILE=0;
	
	private static final int PUT_WORD=2;
	private static final int IF_LOST=3;
	private static final int GET_LOC=4;
	private static final int SHOW_LOCATION=5;
	private static final int GET_WORD=6;
	  
	private void getAllAddress(){
		allmac = Get_PostUtil.sendGet("http://youfoundme.sinaapp.com/auth/addressList",
				"phone=" + phone);                   //字符串数组   返回用户设备列表 名称和标志
		String parseN=Get_PostUtil.parseJSONArray(allmac, "name");  //所有已绑定设备名
		if(parseN.length()>1)
			allName=parseN.substring(1).split(",");
		String parseA=Get_PostUtil.parseJSONArray(allmac, "address");  //所有已绑定设备地址
		if(parseA.length()>1)
			allAddress=parseA.substring(1).split(",");
		if(allName!=null&&allAddress!=null){
			selectName = allName[0];
			selectAddress=allAddress[0];
		}
	}
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.userset_activity);
		//Covers c = new Covers(this, "ab51d5a3cb4d45bbae9e8de9e43a432e"); 
		
		CircleLayout circleMenu = (CircleLayout)findViewById(R.id.main_circle_layout);
		circleMenu.setOnItemSelectedListener(this);
		circleMenu.setOnItemClickListener(this);

		//这个TextView仅仅作为演示转盘按钮以何为默认的选中项，
		//默认的最底部的那一条被选中，然后显示到该TextView中。
		selectedTextView = (TextView)findViewById(R.id.text);
		selectedTextView.setText(((CircleImageView)circleMenu.getSelectedItem()).getName());
		
		Intent intent=this.getIntent();
		phone=intent.getStringExtra("phone");
		password=intent.getStringExtra("password");
		
		Log.d("the user message in bundle", phone+password);
		
	}
	//圆盘转动到底部，则认为该条目被选中
		@Override
		public void onItemSelected(View view, int position, long id, String name) {		
			selectedTextView.setText(name);
		}

		//选择了转盘中的某一条。
		@Override
		public void onItemClick(View view, int position, long id, String name) {
			//点击被选中的图标
			Toast.makeText(getApplicationContext(), " " + name, Toast.LENGTH_SHORT).show();
			if(name.equals("绑定设备")){
				Intent intent=new Intent(UserSetActivity.this,ScanningActivity.class);
				intent.putExtra(ScanningActivity.EXTRAS_PHONE,phone);
				intent.putExtra(ScanningActivity.EXTRAS_NAME,"no");
				intent.putExtra(ScanningActivity.EXTRAS_PASSWORD,"no");
				startActivity(intent);
			}else if(name.equals("发布留言")){
				Thread thread4 = new Thread(new Runnable() {

					@Override
					public void run() {
						getAllAddress();
						Message message=new Message();														
							message.what=PUT_WORD;
							//message.obj=selectAddress;
							Bundle bundle = new Bundle();    
	                        bundle.putString("selAddress",selectAddress);  //往Bundle中存放数据        			                     
	                        message.setData(bundle);//mes利用Bundle传递数据  
							handler2.sendMessage(message);							
					}
				});				
				thread4.start();
			}else if(name.equals("标记丢失")){
				Thread thread = new Thread(new Runnable() {
					
					@Override
					public void run() {
						getAllAddress();
						Message message=new Message();														
							message.what=IF_LOST;
							handler2.sendMessage(message);			
					}
				});				
				thread.start();
			}else if(name.equals("修改名称")){
				Intent intent=new Intent(UserSetActivity.this,ScanningActivity.class);
				intent.putExtra(ScanningActivity.EXTRAS_NAME,"changeName");
				intent.putExtra(ScanningActivity.EXTRAS_PHONE,phone);
				intent.putExtra(ScanningActivity.EXTRAS_PASSWORD,"no");
				startActivity(intent);
			}else if(name.equals("查看丢失列表")){
				//TODO访问丢失表  地址待修改
				Intent intent=new Intent(UserSetActivity.this,WebActivity.class);
				intent.putExtra(WebActivity.EXTRA_WEB, "http://youfoundme.sinaapp.com/auth/list?phone="+phone); 
				startActivity(intent);
			}else if(name.equals("查看定位信息")){
				Thread thread2 = new Thread(new Runnable() {
					public void run() {
					getAllAddress();				
					Log.e("查看定位", "xxxxxx");
					Message message=new Message();
					message.what=GET_LOC;
					handler2.sendMessage(message);
				}
			});
			thread2.start();
			}
		}

	private void dialogWord() {
		AlertDialog.Builder builder = new Builder(UserSetActivity.this);
		builder.setTitle("请选择留言的设备");
		//selectName = allName[0];
		//selectAddress=allAddress[0];
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
				
				dialog.dismiss();	//先dismiss		
				Log.e("选择的设备", "设备标识"+selectAddress);
				//发布留言  待测试
				setWord(selectAddress);		
			}
		});
		builder.setNegativeButton("取消",null);
		builder.create().show();		
	}
	
	protected void setWord(final String seleAddress) {		
		 AlertDialog.Builder builder = new Builder(UserSetActivity.this);
		 builder.setTitle("请输入设备留言：");
		 final EditText editWord = new EditText(this);
		 builder.setView(editWord);		 
		 builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {			
			@Override
			public void onClick(DialogInterface dialog, int which) {				
				Thread thread5 = new Thread(new Runnable() {
					
					@Override
					public void run() {
						String word = editWord.getText().toString();	
						Log.e("发布留言", word);						
						try {
							String param = "address="+URLEncoder.encode((seleAddress),"utf-8")
									+"&content="+URLEncoder.encode((word),"utf-8");
							boolean postSussece = Get_PostUtil.sendPost("http://youfoundme.sinaapp.com/auth/lostNote", 
									param);
						/*	String str = Get_PostUtil.sendPostret("http://youfoundme.sinaapp.com/auth/lostNote", 
									"address="+seleAddress+"&content="+word);
							Log.e("发布留言", str);*/
							if(postSussece)Log.e("发布留言", word+postSussece);
							Message message=new Message();
							if(postSussece)	{							
								message.what=SUSSECE;
								message.arg1=2;
								handler.sendMessage(message);
							}else{
								message.what=FAILE;
								message.arg1=2;
								handler.sendMessage(message);				
							}
						} catch (UnsupportedEncodingException e) {
							Log.e("参数", "异常"+e.toString());
							e.printStackTrace();
						}					
					}
				});
				thread5.start();
			}
		});
		 builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Log.e("留言","取消");
			}
		});
		 builder.show();
		
	}

	private void dialogLose() {
		AlertDialog.Builder builder = new Builder(UserSetActivity.this);
		builder.setTitle("提示");
		builder.setTitle("标记选择");
		builder.setNegativeButton("已丢失", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {			
				changeLost="1";
				dialog.dismiss();
				derviceDialog();
				
			}
		});
		builder.setPositiveButton("已寻回", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
				changeLost="0";
				dialog.dismiss();
				derviceDialog();
				
			}
		});
		builder.create().show();
		
	}
	
	private void derviceDialog() {
		AlertDialog.Builder builder = new Builder(UserSetActivity.this);
		builder.setTitle("选择要标记的设备");

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
						
						try {
							String param = "phone="+URLEncoder.encode((phone),"utf-8")
									+"&address="+URLEncoder.encode((selectAddress),"utf-8")
									+"&name="+URLEncoder.encode((selectName),"utf-8");
							boolean postSussece = Get_PostUtil.sendPost("http://youfoundme.sinaapp.com/auth/addressList", 
									param+"&lostFlag="+changeLost);
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
						} catch (UnsupportedEncodingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}						
					}
				});
				thread3.start();

			}
		});
		builder.setNegativeButton("取消",null);
		builder.show();
		
	}
	private void getLocationDia(){
		AlertDialog.Builder builder = new Builder(UserSetActivity.this);
		builder.setTitle("请选择要查看定位的设备");
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
				
				dialog.dismiss();	//先dismiss		
				Log.e("选择的设备", "设备标识"+selectAddress);
				//查看定位  待测试
				new Thread(new Runnable() {

					@Override
					public void run() {
						//待测试
						String theLoc = Get_PostUtil.sendGet("http://youfoundme.sinaapp.com/location", 
           						"address="+selectAddress+"&location="+"getLocation");
						Log.e("服务器返回地址", "loc"+theLoc);
						if(theLoc==null){
							handler.sendEmptyMessage(GET_WORD);
						}
						String location=Get_PostUtil.parseJSON(theLoc, "name"); 
						//parseTheLocation(location);	
						Message message = new Message();
						message.what = SHOW_LOCATION;
						message.obj = location;
						handler2.sendMessage(message);
					}
				
				}).start();
			}
		});
		builder.setNegativeButton("取消",null);
		builder.create().show();
		
	}
	private Handler handler=new Handler(){
		public void handleMessage(Message msg){
			switch(msg.what){
				case SUSSECE:
					if(msg.arg1==1){
					Toast.makeText(UserSetActivity.this, "标记成功", Toast.LENGTH_SHORT).show();
				}else if(msg.arg1==2){
					Toast.makeText(UserSetActivity.this, "已发布留言", Toast.LENGTH_SHORT).show();
				};break;
				case FAILE:
					if(msg.arg1==1){
					Toast.makeText(UserSetActivity.this, "标记失败", Toast.LENGTH_SHORT).show();
				}else if(msg.arg1==2){
					Toast.makeText(UserSetActivity.this, "留言发布失败", Toast.LENGTH_SHORT).show();
				}			
					break;	
				case GET_WORD:
					Toast.makeText(UserSetActivity.this, "查看留言失败", Toast.LENGTH_SHORT).show();
			}
		}
	};
	private Handler handler2=new Handler(){
		public void handleMessage(Message msg){
			switch(msg.what){
			case PUT_WORD:				
				dialogWord(); break;
			case IF_LOST: dialogLose();  break;
			case GET_LOC:getLocationDia(); break;
			
			case SHOW_LOCATION:				
				Log.e("解析位置", "XXXXXXXXXX");
				String returnLoc=msg.obj.toString();
				 AlertDialog.Builder builder = new Builder(UserSetActivity.this);
				 builder.setTitle(selectName+"的最后定位信息");
				 builder.setMessage(returnLoc);
				 builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub						
					}
				});
				 builder.show();
				 break;
			default :break;
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
						/*	Intent intent=new Intent(UserSetActivity.this,UserActivity.class);
							startActivity(intent);*/
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
