package org.ble.find;

import org.ble.demo.PeripheralActivity;
import org.ble.demo.R;
import org.ble.demo.ScanningActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private ImageView scanfind;
	private ImageView bbs;
	private ImageView introduction;
	private ImageView userlogin;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		scanfind = (ImageView) findViewById(R.id.left_top);
		bbs = (ImageView) findViewById(R.id.left_bottom);
		introduction = (ImageView) findViewById(R.id.right);
		userlogin = (ImageView) findViewById(R.id.bottom);
		/*
		 * 进入寻宝功能*/		
		scanfind.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction()==MotionEvent.ACTION_DOWN){
					bbs.setVisibility(View.INVISIBLE);
					introduction.setVisibility(View.INVISIBLE);
					userlogin.setVisibility(View.INVISIBLE);
					Toast.makeText(MainActivity.this, "go to find", Toast.LENGTH_SHORT).show();
					
					Intent intent=new Intent(MainActivity.this,ScanningActivity.class);
					intent.putExtra(ScanningActivity.EXTRAS_PHONE,"no");
					intent.putExtra(ScanningActivity.EXTRAS_NAME,"no");
					startActivity(intent);
				}else if (event.getAction()==MotionEvent.ACTION_UP){
					
				}
				return false;
			}
		});
		
		/*
		 * 进入论坛功能*/		
		bbs.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction()==MotionEvent.ACTION_DOWN){
					scanfind.setVisibility(View.INVISIBLE);
					introduction.setVisibility(View.INVISIBLE);
					userlogin.setVisibility(View.INVISIBLE);
					Toast.makeText(MainActivity.this, "go to bbs", Toast.LENGTH_SHORT).show();
					//TODO
				}else if (event.getAction()==MotionEvent.ACTION_UP){
					
				}
				return false;
			}
		});
		
		/*
		 * 进入用户介绍手册*/		
		introduction.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction()==MotionEvent.ACTION_DOWN){
					scanfind.setVisibility(View.INVISIBLE);
					bbs.setVisibility(View.INVISIBLE);
					userlogin.setVisibility(View.INVISIBLE);
					Toast.makeText(MainActivity.this, "go to user's introduction", Toast.LENGTH_SHORT).show();
					
					Intent intent=new Intent(MainActivity.this,IntroductionActivity.class);
					startActivity(intent);
				}else if (event.getAction()==MotionEvent.ACTION_UP){
					
				}
				return false;
			}
		});
		
		/*
		 * 进入用户登陆功能*/		
		userlogin.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction()==MotionEvent.ACTION_DOWN){
					scanfind.setVisibility(View.INVISIBLE);
					bbs.setVisibility(View.INVISIBLE);
					introduction.setVisibility(View.INVISIBLE);
					Toast.makeText(MainActivity.this, "go to userloginning", Toast.LENGTH_SHORT).show();
					//TODO
					Intent intent=new Intent(MainActivity.this,UserActivity.class);
					startActivity(intent);
				}else if (event.getAction()==MotionEvent.ACTION_UP){
					
				}
				return false;
			}
		});
	}
	
	protected void onResume() {
		scanfind.setVisibility(View.VISIBLE);
		bbs.setVisibility(View.VISIBLE);
		introduction.setVisibility(View.VISIBLE);
		userlogin.setVisibility(View.VISIBLE);
		super.onResume();
	}
	/*@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}*/

}

