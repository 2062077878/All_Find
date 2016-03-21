package org.ble.find;


import org.ble.demo.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class UserActivity extends Activity {

	private TextView forget;
	private Button loginbtn,registerbtn;
	private EditText phone,password;

	public static final int SHOW_RESPONSE=0;
	private ProgressDialog pd;
	private void showProgressDialog() {
		pd=new ProgressDialog(UserActivity.this);
		pd.setMessage("Progress");
		pd.setMessage("Loding");
		pd.show();		
	}
	private Handler handler=new Handler(){
		
		public void handleMessage(Message msg){
			switch(msg.what){
			case SHOW_RESPONSE:
				String response=(String)msg.obj;
				Log.e("返回json字符串", response);
			
				String arm_phone =Get_PostUtil.parseJSON(response, "phone");       //解析数据
				String arm_password =Get_PostUtil.parseJSON(response, "password");
				Log.e("解析字符串", arm_phone+"+"+arm_password);
				if((phone.getText().toString().equals(arm_phone))&&(password.getText().toString().equals(arm_password))){
					pd.dismiss();
					Intent intent=new Intent(UserActivity.this,UserSetActivity.class);
					//将账号和密码传过去
					 Bundle bundle = new Bundle();  
					 bundle.putString("phone",phone.getText().toString());
					 bundle.putString("password", password.getText().toString());
					 intent.putExtras(bundle);
					 startActivity(intent);
					finish();
				}
				else{
					 pd.dismiss();
					 AlertDialog.Builder dialog  = new Builder(UserActivity.this);
					 dialog.setTitle("提示" ) ;
					 dialog.setMessage("密码错误！"+"\r\n"+"(可通过手机验证寻回密码)" ) ;
					 dialog.setPositiveButton("确定" ,  null );
					 dialog.show(); 
				}
			}
		}
	};
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.userlogin_activity);
		forget = (TextView) findViewById(R.id.forget);
		registerbtn = (Button) findViewById(R.id.registerbtn);
		loginbtn = (Button) findViewById(R.id.loginbtn);
		phone = (EditText) findViewById(R.id.phone);
		password = (EditText) findViewById(R.id.password);
	/*找回密码*/	
		forget.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub  进入短信验证密码寻回页面
				return false;
			}
		});
		/*注册*/
		registerbtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(UserActivity.this,RegisterActivity.class);
				startActivity(intent);
			}
		});
		/*登录*/
		loginbtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showProgressDialog();
				if((!phone.getText().toString().equals(""))&&(!password.getText().toString().equals(""))){					
					new Thread(new Runnable() {
						
						@Override
						public void run() {							
							
							String respStr=Get_PostUtil.sendGet("http://youfoundme.sinaapp.com/auth/loginmob", 
									"phone="+phone.getText().toString()+"&password="+password.getText().toString());
							Log.e("登录", respStr);		
						//	String respStr=Get_PostUtil.sendGet("http://10.0.2.2/get_data.json");本地的试验
							Message message=new Message();							
							message.what=SHOW_RESPONSE;
							message.obj=respStr;
							handler.sendMessage(message);
							         
						}
						
					}).start();
				}
			}
		});

	}

	protected void onResume() {
		
		super.onResume();
	}
}
