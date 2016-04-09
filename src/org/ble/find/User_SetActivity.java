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

		//���TextView������Ϊ��ʾת�̰�ť�Ժ�ΪĬ�ϵ�ѡ���
		//Ĭ�ϵ���ײ�����һ����ѡ�У�Ȼ����ʾ����TextView�С�
		selectedTextView = (TextView)findViewById(R.id.text);
		selectedTextView.setText(((CircleImageView)circleMenu.getSelectedItem()).getName());
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
		
	}

}
