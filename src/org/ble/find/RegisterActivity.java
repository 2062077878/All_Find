package org.ble.find;

import java.util.List;

import org.ble.demo.R;
import org.ble.database.*;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
public class RegisterActivity extends Activity {

	private EditText username_edit,password_edit,password_reedit;
	private String username="",password="",repassword="";
	private Button registerbtn2;
	
	private FindDBHandle findDBHandle;
	private static final int FAIL=1;
	private static final int SUCCESE=2;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register_activity);
		
		registerbtn2 = (Button) findViewById(R.id.register_btn2);
		username_edit = (EditText) findViewById(R.id.username_edit);
		password_edit = (EditText) findViewById(R.id.password_edit);
		password_reedit = (EditText) findViewById(R.id.password_reedit);		
		registerbtn2.setEnabled(true);
		findDBHandle=FindDBHandle.getInstance(this);
		
		registerbtn2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				username=username_edit.getText().toString();   //获得用户名
				password=password_edit.getText().toString();
				repassword=password_reedit.getText().toString();
				if((password.compareTo(repassword)==0)){
					//开启网络访问子线程
					new Thread(new Runnable() {
						int i;
						@Override
						public void run() {
							List<UserMsg> userList = findDBHandle.queryUserMsg();
							Message msg=new Message();
							for(i=0;i<userList.size();i++){
								String name=userList.get(i).getUserName();
								if(username.equals(name)){
									msg.what=FAIL;
									handler.sendMessage(msg);
									break;
								}								
							}
							if(i==userList.size()){
								UserMsg userMsg=new UserMsg();
								userMsg.setUserName(username);
								userMsg.setPassword(password);
								findDBHandle.addUserMsg(userMsg);  //添加进本地数据库
								//网络注册							
								  Boolean postSussece=Get_PostUtil.sendPost("http://youfoundme.sinaapp.com/auth/resmob", 
											"phone="+username+"&password="+password);
								  if(postSussece)							
									Log.d("密码", password);
								msg.what=SUCCESE;
								handler.sendMessage(msg);
							}
						
						}
					}).start();
					
				}else{
					Toast.makeText(RegisterActivity.this, "输入密码不一致，请重新输入", Toast.LENGTH_LONG).show();
				}
				
			}
		});
	}
	
	private Handler handler=new Handler(){
		public void handleMessage(Message msg){
			switch(msg.what){
			case FAIL:
				Toast.makeText(RegisterActivity.this, "该账号已被注册", Toast.LENGTH_LONG).show();
			case SUCCESE:
				Toast.makeText(RegisterActivity.this, "账号注册成功", Toast.LENGTH_LONG).show();
			}
		}
	};
	protected void onResume() {	
		super.onResume();
	}
}
