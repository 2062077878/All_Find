package org.ble.find;


import java.util.List;

import org.ble.database.*;
import org.ble.demo.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class UserActivity extends Activity {

//	private TextView forget;
	private CheckBox remenberPsw;
	private Button loginbtn,registerbtn;
	private EditText phone,password;
	
	//private SharedPreferences pref;
	//private SharedPreferences.Editor editor;
	
	public static final int SHOW_RESPONSE=0;
	//private ProgressDialog pd;
	private FindDBHandle findDBHandle;
	
/*	private void showProgressDialog() {
		pd=new ProgressDialog(UserActivity.this);
		pd.setMessage("Progress");
		pd.setMessage("Loding");
		pd.show();		
	}*/
	private Handler handler=new Handler(){
		
		public void handleMessage(Message msg){
			switch(msg.what){
			case SHOW_RESPONSE:
				String response=(String)msg.obj;
				Log.e("����json�ַ���", response);
			
				String arm_phone =Get_PostUtil.parseJSON(response, "phone");       //��������
				String arm_password =Get_PostUtil.parseJSON(response, "password");
				Log.e("�����ַ���", arm_phone+"+"+arm_password);
				if((phone.getText().toString().equals(arm_phone))&&(password.getText().toString().equals(arm_password))){
				//	pd.dismiss();
					//�����˺�����					
					/*if(remenberPsw.isChecked()){
						editor.putBoolean("remenber_password", true);
					}else{
						editor.clear();
						editor.putBoolean("remenber_password", false);
					}
					editor.putString("account", phone.getText().toString());
					editor.putString("psw", password.getText().toString());
					editor.commit();*/
					
					Intent intent=new Intent(UserActivity.this,UserSetActivity.class);
					//���˺ź����봫��ȥ
					 Bundle bundle = new Bundle();  
					 bundle.putString("phone",phone.getText().toString());
					 bundle.putString("password", password.getText().toString());
					 intent.putExtras(bundle);
					 startActivity(intent);
					finish();
				}
				else{
				//	 pd.dismiss();
					 AlertDialog.Builder dialog  = new Builder(UserActivity.this);
					 dialog.setTitle("��ʾ" ) ;
					 dialog.setMessage("�˺Ż��������!" ) ;
					 dialog.setPositiveButton("ȷ��" ,  null );
					 dialog.show(); 
				}
			}
		}
	};
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.userlogin_activity);
	//	forget = (TextView) findViewById(R.id.forget);
		remenberPsw=(CheckBox)findViewById(R.id.remenber_pass);
		registerbtn = (Button) findViewById(R.id.registerbtn);
		loginbtn = (Button) findViewById(R.id.loginbtn);
		phone = (EditText) findViewById(R.id.phone);
		password = (EditText) findViewById(R.id.password);
		
		findDBHandle=FindDBHandle.getInstance(this);
		
	/*��ס����*/	
		/*pref=PreferenceManager.getDefaultSharedPreferences(this);
		editor=pref.edit();
		boolean isRemenber=pref.getBoolean("remenber_password", false);
		if(isRemenber){
			String account=pref.getString("account", "");
			String psw=pref.getString("psw", "");
			phone.setText(account);
			password.setText(psw);
			remenberPsw.setChecked(true);
		}else{
			String account=pref.getString("account", "");
			phone.setText(account);
			remenberPsw.setChecked(false);
		}*/		
		/*��¼*/
		loginbtn.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
			//	showProgressDialog();
				String account=phone.getText().toString();
				String psw=password.getText().toString();
				if((!account.equals(""))&&(!psw.equals(""))){	
					
					if(remenberPsw.isChecked()){
						findDBHandle.updateCurrentUser(account, psw);   //�����¼�û���Ϣ
					}else{
						findDBHandle.deleteCurrentUser();
					}
					int i=0;
					List<UserMsg> userList = findDBHandle.queryUserMsg();
					for(i=0;i<userList.size();i++){
						String name=userList.get(i).getUserName();
						Log.e("�û���Ϣ��ѯ", name);
						if(name.equals(account)){  
							Intent intent=new Intent(UserActivity.this,UserSetActivity.class);
							//���˺ź����봫��ȥ
							 Bundle bundle = new Bundle();  
							 bundle.putString("phone",phone.getText().toString());
							 bundle.putString("password", password.getText().toString());
							 intent.putExtras(bundle);
							 startActivity(intent);
							finish();
							break;
						}
					}
									
					/*if(account.equals(pref.getString("account", ""))&&psw.equals(pref.getString("psw", ""))){
						pd.dismiss();
						if(remenberPsw.isChecked()){
							editor.putBoolean("remenber_password", true);
						}else{
							editor.putBoolean("remenber_password", false);
						}
						Intent intent=new Intent(UserActivity.this,UserSetActivity.class);
						//���˺ź����봫��ȥ
						 Bundle bundle = new Bundle();  
						 bundle.putString("phone",phone.getText().toString());
						 bundle.putString("password", password.getText().toString());
						 intent.putExtras(bundle);
						 startActivity(intent);
						finish();
					}*/					
					if(i==userList.size()){					
						//����������
							new Thread(new Runnable() {									
							@Override
							public void run() {														
								String respStr=Get_PostUtil.sendGet("http://youfoundme.sinaapp.com/auth/loginmob", 
										"phone="+phone.getText().toString()+"&password="+password.getText().toString());
								Log.e("��¼", respStr);		
							//	String respStr=Get_PostUtil.sendGet("http://10.0.2.2/get_data.json");���ص�����
								Message message=new Message();							
								message.what=SHOW_RESPONSE;
								message.obj=respStr;
								handler.sendMessage(message);
								         
							}
							
						}).start();
					}
				}else{
					Toast.makeText(UserActivity.this, "�˺Ż��������벻��Ϊ��", Toast.LENGTH_LONG).show();
				}
			}
		});
		/*ע��*/
		registerbtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(UserActivity.this,RegisterActivity.class);
				startActivity(intent);
			}
		});
	}

	protected void onResume() {
		
		super.onResume();
	}
	protected void onStart(){
		super.onStart();
		CurrentUser currentUser=findDBHandle.queryCurrentUser();
		if(currentUser!=null){
			phone.setText(currentUser.getCurrentUserName());
			password.setText(currentUser.getCurrentPassword());
		}	
		//Log.e("onStart","x"+ currentUser.getCurrentUserName()+currentUser.getCurrentPassword());
	}
}
