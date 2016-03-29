package org.ble.database;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class FindDBHandle {

	public static final String DB_NAME="All_Find";  //数据库名
	public static final int VERSION=5;    //版本号
	
	private static FindDBHandle findDBHandle;
	private SQLiteDatabase db;
	
	/*构造方法私有化*/
	private FindDBHandle(Context context){
		//创建数据库
		FindOpenHelper dbHelper=new FindOpenHelper(context, DB_NAME, null, VERSION);
		db=dbHelper.getWritableDatabase();
	}
	/*获取FindDBHandler的实例  -单例模式*/
	public synchronized static FindDBHandle getInstance(Context context){
		if(findDBHandle==null){
			findDBHandle=new FindDBHandle(context);			
		}
		return findDBHandle;
	}
	
	/*添加用户信息操作*/
	public void addUserMsg(UserMsg userMsg){
		if(userMsg!=null){
			ContentValues values =new ContentValues();
			values.put("username", userMsg.getUserName());
			values.put("password", userMsg.getPassword());
			db.insert("User", null, values);
		}
	}	
	/*查询用户信息操作*/
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
	
	/*添加设备操作(记得先set)*/
	public void addDeviceMsg(DeviceMsg deviceMsg){
		if(deviceMsg!=null){
			ContentValues values =new ContentValues();
			values.put("user_id", deviceMsg.getUserId());
			values.put("device_address", deviceMsg.getDeviceAddress());
			values.put("device_name", deviceMsg.getDeviceName());
			db.insert("MyDevice", null, values);
			Log.e("绑本地", "x"+deviceMsg.getDeviceAddress());
		}
	}
	/*删除设备的操作*/
	public void deleteDeviceMsg(String deviceAddress){
		db.delete("MyDevice", "device_address = ?", new String[]{deviceAddress});
		Log.e("删本地","x"+deviceAddress);
	}
	/*修改设备名*/
	public void updateDeviceMsg(String deviceAddress,String deviceName){
		ContentValues values =new ContentValues();
		values.put("device_name", deviceName);
		db.update("MyDevice", values, "device_address = ?", new String[]{deviceAddress});
		Log.e("改本地", "x"+deviceAddress);
	}
	/*查询设备(只需获得设备名)*/
	public String queryDeviceAddress(String deviceAddress){
		String name="No Name";
		String address="";
	//	Cursor cursor=db.query("Device", null, "device_address = ?", new String[]{deviceAddress}, null, null, null);
		Cursor cursor=db.query("MyDevice", null,null,null, null,null,null);
		if(cursor.moveToFirst()){
			do{	
				Log.e("查本地前", "x"+deviceAddress);
				address=cursor.getString(cursor.getColumnIndex("device_address"));
				if(address.equals(deviceAddress)){
					name=cursor.getString(cursor.getColumnIndex("device_name"));
					Log.e("查本地后", "x"+deviceAddress);
					break;
				}
			}while(cursor.moveToNext());
		}			
		return name;		
	}
	
	//TODO 根据username获得User表的id
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
	
	/*修改保存密码的用户信息*/
	public void updateCurrentUser(String currentUN,String currentPSW){
		ContentValues values =new ContentValues();
		values.put("current_username", currentUN);
		values.put("current_password", currentPSW);
		db.insert("CurrenUser", null, values);
	}
	/*查询保存密码的用户信息*/
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

