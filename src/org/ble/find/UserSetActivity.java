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
		setContentView(R.layout.userset_activity);
		//Covers c = new Covers(this, "ab51d5a3cb4d45bbae9e8de9e43a432e"); 
		
		CircleLayout circleMenu = (CircleLayout)findViewById(R.id.main_circle_layout);
		circleMenu.setOnItemSelectedListener(this);
		circleMenu.setOnItemClickListener(this);

		//���TextView������Ϊ��ʾת�̰�ť�Ժ�ΪĬ�ϵ�ѡ���
		//Ĭ�ϵ���ײ�����һ����ѡ�У�Ȼ����ʾ����TextView�С�
		selectedTextView = (TextView)findViewById(R.id.text);
		selectedTextView.setText(((CircleImageView)circleMenu.getSelectedItem()).getName());
		
		Intent intent=this.getIntent();
		phone=intent.getStringExtra("phone");
		password=intent.getStringExtra("password");
		
		Log.d("the user message in bundle", phone+password);
		
	}
	//Բ��ת�����ײ�������Ϊ����Ŀ��ѡ��
		@Override
		public void onItemSelected(View view, int position, long id, String name) {		
			selectedTextView.setText(name);
		}

		//ѡ����ת���е�ĳһ����
		@Override
		public void onItemClick(View view, int position, long id, String name) {
			//�����ѡ�е�ͼ��
			Toast.makeText(getApplicationContext(), " " + name, Toast.LENGTH_SHORT).show();
			if(name.equals("���豸")){
				Intent intent=new Intent(UserSetActivity.this,ScanningActivity.class);
				intent.putExtra(ScanningActivity.EXTRAS_PHONE,phone);
				intent.putExtra(ScanningActivity.EXTRAS_NAME,"no");
				intent.putExtra(ScanningActivity.EXTRAS_PASSWORD,"no");
				startActivity(intent);
			}else if(name.equals("��������")){
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
			}else if(name.equals("��Ƕ�ʧ")){
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
			}else if(name.equals("�޸�����")){
				Intent intent=new Intent(UserSetActivity.this,ScanningActivity.class);
				intent.putExtra(ScanningActivity.EXTRAS_NAME,"changeName");
				intent.putExtra(ScanningActivity.EXTRAS_PHONE,phone);
				intent.putExtra(ScanningActivity.EXTRAS_PASSWORD,"no");
				startActivity(intent);
			}else if(name.equals("�鿴��ʧ�б�")){
				//TODO���ʶ�ʧ��  ��ַ���޸�
				Intent intent=new Intent(UserSetActivity.this,WebActivity.class);
				intent.putExtra(WebActivity.EXTRA_WEB, "http://youfoundme.sinaapp.com/auth/list?phone="+phone); 
				startActivity(intent);
			}else if(name.equals("�鿴��λ��Ϣ")){
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
						try {
							String param = "address="+URLEncoder.encode((seleAddress),"utf-8")
									+"&content="+URLEncoder.encode((word),"utf-8");
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
						} catch (UnsupportedEncodingException e) {
							Log.e("����", "�쳣"+e.toString());
							e.printStackTrace();
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
						//parseTheLocation(location);	
						Message message = new Message();
						message.what = SHOW_LOCATION;
						message.obj = location;
						handler2.sendMessage(message);
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
