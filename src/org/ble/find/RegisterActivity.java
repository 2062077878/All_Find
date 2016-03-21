package org.ble.find;

import org.ble.demo.R;

import android.app.Activity;
import android.os.Bundle;
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
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register_activity);
		
		registerbtn2 = (Button) findViewById(R.id.register_btn2);
		username_edit = (EditText) findViewById(R.id.username_edit);
		password_edit = (EditText) findViewById(R.id.password_edit);
		password_reedit = (EditText) findViewById(R.id.password_reedit);
		
		registerbtn2.setEnabled(true);
		registerbtn2.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				username=username_edit.getText().toString();   //获得用户名
				password=password_edit.getText().toString();
				repassword=password_reedit.getText().toString();
				if((password.compareTo(repassword)==0)){
					//开启网络访问子线程
					new Thread(new Runnable() {
						
						@Override
						public void run() {
							//TODO  地址待改
							Boolean postSussece=Get_PostUtil.sendPost("http://youfoundme.sinaapp.com/auth/resmob", 
									"phone="+username+"&password="+password);
						if(postSussece)							
							Log.d("密码", password);
						}
					}).start();
					
				}else{
					Toast.makeText(RegisterActivity.this, "输入密码不一致，请重新输入", Toast.LENGTH_LONG).show();
				}
				
			}
		});
	}
	
	protected void onResume() {	
		super.onResume();
	}
}
