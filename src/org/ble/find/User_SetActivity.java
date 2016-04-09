package org.ble.find;

import org.ble.demo.R;
import org.ble.find.CircleLayout;
import org.ble.find.CircleLayout.OnItemClickListener;
import org.ble.find.CircleLayout.OnItemSelectedListener;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


public class User_SetActivity extends Activity implements OnItemSelectedListener, OnItemClickListener{
	
	
	private TextView selectedTextView;

	@Override
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
		
	}

}
