package org.ble.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class FindOpenHelper extends SQLiteOpenHelper {
	
	public static final String CREATE_USER_MSG="create table User ("
					+"id integer primary key autoincrement, "
					+"username text, "
					+"password text)";
	public static final String CREATE_DEVICE_MSG="create table MyDevice ("
			+"id integer primary key autoincrement, "
			+"user_id integer, "
			+"device_address text, "
			+"device_name text)";
	
	 public static final String CURRENT_USER_MSG="create table CurrenUser ("
			 +"id integer primary key autoincrement, "
			 +"current_username text, "
			 +"current_password text)";
	public FindOpenHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {		
		db.execSQL(CREATE_USER_MSG);  //创建用户信息表
		db.execSQL(CREATE_DEVICE_MSG); //创建设备表
		db.execSQL(CURRENT_USER_MSG); //创建保存目前用户表
		Log.e("创建表", "成功");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		switch (oldVersion) {
		case 1:					
		case 2:					
		case 3:
			/*db.execSQL("drop table if exists User");
			db.execSQL("drop table if exists Device");
			db.execSQL("drop table if exists CurrenUser");
			onCreate(db);*/
		case 4:db.execSQL(CREATE_DEVICE_MSG); 
		default:
			break;
		}
	/*	db.execSQL("drop table if exists User");
		db.execSQL("drop table if exists Device");
		onCreate(db);*/
	}

}

