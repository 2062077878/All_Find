package org.ble.database;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class FindDBHandle {

	public static final String DB_NAME="All_Find";  //���ݿ���
	public static final int VERSION=5;    //�汾��
	
	private static FindDBHandle findDBHandle;
	private SQLiteDatabase db;
	
	/*���췽��˽�л�*/
	private FindDBHandle(Context context){
		//�������ݿ�
		FindOpenHelper dbHelper=new FindOpenHelper(context, DB_NAME, null, VERSION);
		db=dbHelper.getWritableDatabase();
	}
	/*��ȡFindDBHandler��ʵ��  -����ģʽ*/
	public synchronized static FindDBHandle getInstance(Context context){
		if(findDBHandle==null){
			findDBHandle=new FindDBHandle(context);			
		}
		return findDBHandle;
	}
	
	/*����û���Ϣ����*/
	public void addUserMsg(UserMsg userMsg){
		if(userMsg!=null){
			ContentValues values =new ContentValues();
			values.put("username", userMsg.getUserName());
			values.put("password", userMsg.getPassword());
			db.insert("User", null, values);
		}
	}	
	/*��ѯ�û���Ϣ����*/
	public List<UserMsg> queryUserMsg(){
		List<UserMsg> list=new ArrayList<UserMsg>();
		Cursor cursor=db.query("User", null, null, null, null, null, null);
		if(cursor.moveToFirst()){
			do{
				UserMsg userMsg=new UserMsg();
				userMsg.setId(cursor.getInt(cursor.getColumnIndex("id")));
				userMsg.setUserName(cursor.getString(cursor.getColumnIndex("username")));
				userMsg.setPassword(cursor.getString(cursor.getColumnIndex("password")));
				list.add(userMsg);
			}while(cursor.moveToNext());
		}
		return list;
		
	}
	
	/*����豸����(�ǵ���set)*/
	public void addDeviceMsg(DeviceMsg deviceMsg){
		if(deviceMsg!=null){
			ContentValues values =new ContentValues();
			values.put("user_id", deviceMsg.getUserId());
			values.put("device_address", deviceMsg.getDeviceAddress());
			values.put("device_name", deviceMsg.getDeviceName());
			db.insert("MyDevice", null, values);
			Log.e("�󱾵�", "x"+deviceMsg.getDeviceAddress());
		}
	}
	/*ɾ���豸�Ĳ���*/
	public void deleteDeviceMsg(String deviceAddress){
		db.delete("MyDevice", "device_address = ?", new String[]{deviceAddress});
		Log.e("ɾ����","x"+deviceAddress);
	}
	/*�޸��豸��*/
	public void updateDeviceMsg(String deviceAddress,String deviceName){
		ContentValues values =new ContentValues();
		values.put("device_name", deviceName);
		db.update("MyDevice", values, "device_address = ?", new String[]{deviceAddress});
		Log.e("�ı���", "x"+deviceAddress);
	}
	/*��ѯ�豸(ֻ�����豸��)*/
	public String queryDeviceAddress(String deviceAddress){
		String name="No Name";
		String address="";
	//	Cursor cursor=db.query("Device", null, "device_address = ?", new String[]{deviceAddress}, null, null, null);
		Cursor cursor=db.query("MyDevice", null,null,null, null,null,null);
		if(cursor.moveToFirst()){
			do{	
				Log.e("�鱾��ǰ", "x"+deviceAddress);
				address=cursor.getString(cursor.getColumnIndex("device_address"));
				if(address.equals(deviceAddress)){
					name=cursor.getString(cursor.getColumnIndex("device_name"));
					Log.e("�鱾�غ�", "x"+deviceAddress);
					break;
				}
			}while(cursor.moveToNext());
		}			
		return name;		
	}
	
	//TODO ����username���User���id
	public int getUserId(String name){
		int idNum=0;
		String strName;
		Cursor cursor=db.query("User", null, null, null, null, null, null);
	  //  idNum=cursor.getInt(cursor.getColumnIndex("id")); 
	    if(cursor.moveToFirst()){
			do{				
				strName=cursor.getString(cursor.getColumnIndex("username"));
				if(strName.equals(name)){
					idNum=cursor.getInt(cursor.getColumnIndex("id")); 
					break;
				}
			}while(cursor.moveToNext());
		}			
		return idNum;
	}
	
	/*�޸ı���������û���Ϣ*/
	public void updateCurrentUser(String currentUN,String currentPSW){
		ContentValues values =new ContentValues();
		values.put("current_username", currentUN);
		values.put("current_password", currentPSW);
		db.insert("CurrenUser", null, values);
	}
	/*��ѯ����������û���Ϣ*/
	public CurrentUser queryCurrentUser(){	
		Cursor cursor=db.query("CurrenUser", null, null, null, null, null, null);
		CurrentUser currentUser=new CurrentUser();
		if(cursor.moveToFirst()){
				currentUser.setId(cursor.getInt(cursor.getColumnIndex("id")));
				currentUser.setCurrentUserName(cursor.getString(cursor.getColumnIndex("current_username")));
				currentUser.setCurrentPassword(cursor.getString(cursor.getColumnIndex("current_password")));			
		}
		return currentUser;
		
	}
	public void deleteCurrentUser(){
		db.delete("CurrenUser", null, null);
	}
}

