package org.ble.find;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.ble.database.FindDBHandle;
import org.ble.demo.R;
import org.ble.demo.ScanningActivity;
import org.json.JSONArray;
import org.json.JSONObject;

import com.baidu.location.Address;
import com.baidu.location.BDLocation;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class UserSetActivity extends Activity {
	private String phone; 
	private String password;
	
	private String allmac="";
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
	/*ѡ���Ƕ�ʧ����Ѱ��*/
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
				"phone=" + phone);                   //�ַ�������   �����û��豸�б� ���ƺͱ�־
		String parseN=Get_PostUtil.parseJSONArray(allmac, "name");  //�����Ѱ��豸��
		if(parseN.length()>1)
			allName=parseN.substring(1).split(",");
		String parseA=Get_PostUtil.parseJSONArray(allmac, "address");  //�����Ѱ��豸��ַ
		if(parseA.length()>1)
			allAddress=parseA.substring(1).split(",");
		if(allName!=null&&allAddress!=null){
			selectName = allName[0];
			selectAddress=allAddress[0];
		}
	}
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
		
		    
		/*���豸*/		
		bindDervice.setOnClickListener(new OnClickListener() {		
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(UserSetActivity.this,ScanningActivity.class);
				intent.putExtra(ScanningActivity.EXTRAS_PHONE,phone);
				intent.putExtra(ScanningActivity.EXTRAS_NAME,"no");
				intent.putExtra(ScanningActivity.EXTRAS_PASSWORD,"no");
				startActivity(intent);
			}
		});
		/*��������*/	
		putWord.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						Thread thread4 = new Thread(new Runnable() {

							@Override
							public void run() {
								getAllAddress();
								Message message=new Message();														
									message.what=PUT_WORD;
									//message.obj=selectAddress;
									Bundle bundle = new Bundle();    
			                        bundle.putString("selAddress",selectAddress);  //��Bundle�д������        			                     
			                        message.setData(bundle);//mes����Bundle��������  
									handler2.sendMessage(message);							
							}
						});				
						thread4.start();
						//dialogWord();
						//setWord(selectAddress);	
					}
				});
		/*��ʧ���*/	
		loseFlag.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
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
				//dialogLose();   //ѡ���Ƕ�ʧ����Ѱ��
			}

		});
		/*�޸��豸��*/	
		changeName.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {				
				Intent intent=new Intent(UserSetActivity.this,ScanningActivity.class);
				intent.putExtra(ScanningActivity.EXTRAS_NAME,"changeName");
				intent.putExtra(ScanningActivity.EXTRAS_PHONE,phone);
				intent.putExtra(ScanningActivity.EXTRAS_PASSWORD,"no");
				startActivity(intent);
			}
		});
		/*�鿴��ʧ�б�*/	
		lostList.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//TODO���ʶ�ʧ��  ��ַ���޸�
				Intent intent=new Intent(UserSetActivity.this,WebActivity.class);
				intent.putExtra(WebActivity.EXTRA_WEB, "http://youfoundme.sinaapp.com/auth"); 
				startActivity(intent);
				
			}
		});
		/*�鿴��λ*/	
		findLocation.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Thread thread2 = new Thread(new Runnable() {
						public void run() {
						getAllAddress();				
						Log.e("�鿴��λ", "xxxxxx");
						Message message=new Message();
						message.what=GET_LOC;
						handler2.sendMessage(message);
					}
				});
				thread2.start();
				
			}
			
		});
	}
	
	private void dialogWord() {
		AlertDialog.Builder builder = new Builder(UserSetActivity.this);
		builder.setTitle("��ѡ�����Ե��豸");
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
		builder.setPositiveButton("ȷ��",new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
				dialog.dismiss();	//��dismiss		
				Log.e("ѡ����豸", "�豸��ʶ"+selectAddress);
				//��������  ������
				setWord(selectAddress);		
			}
		});
		builder.setNegativeButton("ȡ��",null);
		builder.create().show();
		
	}
	
	protected void setWord(final String seleAddress) {		
		 AlertDialog.Builder builder = new Builder(UserSetActivity.this);
		 builder.setTitle("�������豸���ԣ�");
		 final EditText editWord = new EditText(this);
		 builder.setView(editWord);		 
		 builder.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {			
			@Override
			public void onClick(DialogInterface dialog, int which) {				
				Thread thread5 = new Thread(new Runnable() {
					
					@Override
					public void run() {
						String word = editWord.getText().toString();	
						Log.e("��������", word);
						String param="";
						try {
							param = "address="+URLEncoder.encode((seleAddress),"utf-8")
									+"&content="+URLEncoder.encode((word),"utf-8");
						} catch (UnsupportedEncodingException e) {
							Log.e("����", "�쳣"+e.toString());
							e.printStackTrace();
						}
						boolean postSussece = Get_PostUtil.sendPost("http://youfoundme.sinaapp.com/auth/lostNote", 
								param);
					/*	String str = Get_PostUtil.sendPostret("http://youfoundme.sinaapp.com/auth/lostNote", 
								"address="+seleAddress+"&content="+word);
						Log.e("��������", str);*/
						if(postSussece)Log.e("��������", word+postSussece);
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
					}
				});
				thread5.start();
			}
		});
		 builder.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Log.e("����","ȡ��");
			}
		});
		 builder.show();
		
	}

	private void dialogLose() {
		AlertDialog.Builder builder = new Builder(UserSetActivity.this);
		builder.setTitle("��ʾ");
		builder.setTitle("���ѡ��");
		builder.setNegativeButton("�Ѷ�ʧ", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {			
				changeLost="1";
				dialog.dismiss();
				derviceDialog();
				
			}
		});
		builder.setPositiveButton("��Ѱ��", new DialogInterface.OnClickListener() {
			
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
		builder.setTitle("ѡ��Ҫ��ǵ��豸");
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
		
		builder.setPositiveButton("ȷ��",new DialogInterface.OnClickListener() {
			
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
		builder.setNegativeButton("ȡ��",null);
		builder.show();
		
	}
	private void getLocationDia(){
		AlertDialog.Builder builder = new Builder(UserSetActivity.this);
		builder.setTitle("��ѡ��Ҫ�鿴��λ���豸");
		builder.setSingleChoiceItems(allName, 0,new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				selectName = allName[which];
				selectAddress=allAddress[which];
			}
		}); 
		builder.setPositiveButton("ȷ��",new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
				dialog.dismiss();	//��dismiss		
				Log.e("ѡ����豸", "�豸��ʶ"+selectAddress);
				//�鿴��λ  ������
				new Thread(new Runnable() {

					@Override
					public void run() {
						//������
						String theLoc = Get_PostUtil.sendGet("http://youfoundme.sinaapp.com/location", 
           						"address="+selectAddress+"&location="+"getLocation");
						Log.e("���������ص�ַ", "loc"+theLoc);
						if(theLoc==null){
							handler.sendEmptyMessage(GET_WORD);
						}
						String location=Get_PostUtil.parseJSON(theLoc, "name"); 
						parseTheLocation(location);
						//������
						//Log.e("���뾭γ��", "");
						//location=new MyLocation();
					//	String return_Loc=location.getLocation("null");
						/*String return_Loc="23.057729,113.370236";
						Log.e("���ؾ�γ��", return_Loc);
						parseTheLocation(return_Loc);*/
					}

					private void parseTheLocation(String location) {
						try {					
							String[] loc = location.split(",");
							double lat = Double.parseDouble(loc[0]);
							double lng = Double.parseDouble(loc[1]);
							Geocoder geo=new Geocoder(getApplicationContext());
							List<android.location.Address> str=geo.getFromLocation(lat,lng,0);
							for(int i=0;i<str.size();i++){
								Log.e("ѭ��",str.get(i).toString());
							}
							String result=str.get(0).toString();						
							result=result.substring(result.indexOf("\""), result.indexOf(","));  //���ݽ�����ֵ�
							Message message = new Message();
							message.what = SHOW_LOCATION;
							message.obj = result;
							handler2.sendMessage(message);
						} catch (Exception e) {
							 Log.e("�쳣",e.toString());
							e.printStackTrace();
						}
						
					}
				}).start();
			}
		});
		builder.setNegativeButton("ȡ��",null);
		builder.create().show();
		
	}
	private Handler handler=new Handler(){
		public void handleMessage(Message msg){
			switch(msg.what){
				case SUSSECE:
					if(msg.arg1==1){
					Toast.makeText(UserSetActivity.this, "��ǳɹ�", Toast.LENGTH_SHORT).show();
				}else if(msg.arg1==2){
					Toast.makeText(UserSetActivity.this, "�ѷ�������", Toast.LENGTH_SHORT).show();
				};break;
				case FAILE:
					if(msg.arg1==1){
					Toast.makeText(UserSetActivity.this, "���ʧ��", Toast.LENGTH_SHORT).show();
				}else if(msg.arg1==2){
					Toast.makeText(UserSetActivity.this, "���Է���ʧ��", Toast.LENGTH_SHORT).show();
				}			
					break;	
				case GET_WORD:
					Toast.makeText(UserSetActivity.this, "�鿴����ʧ��", Toast.LENGTH_SHORT).show();
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
				Log.e("����λ��", "XXXXXXXXXX");
				String returnLoc=msg.obj.toString();
				 AlertDialog.Builder builder = new Builder(UserSetActivity.this);
				 builder.setTitle(selectName+"�����λ��Ϣ");
				 builder.setMessage(returnLoc);
				 builder.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {					
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
					   dialog.setTitle("�Ƿ��˳���¼");
					 dialog.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							finish();
						/*	Intent intent=new Intent(UserSetActivity.this,UserActivity.class);
							startActivity(intent);*/
						}
						
					}); 
						
					 dialog.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
							
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
