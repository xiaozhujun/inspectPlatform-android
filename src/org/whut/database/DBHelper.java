package org.whut.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper{

	private static DBHelper instance;
	
	public DBHelper(Context context) {
		super(context,"db",null,1);
	}
	
	
	public static DBHelper getInstance(Context context){
		if(instance==null){
			synchronized(DBHelper.class){
				if(instance==null){
					instance = new DBHelper(context);
				}
			}
		}
		return instance;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		Log.i("msg", "DB正在创建...");
//		db.execSQL("CREATE TABLE IF NOT EXISTS USER(" +
//				"user_id integer primary key autoincrement," +
//				"username varchar(255),tablename varchar(255)," +
//				"taskname varchar(255),devicename varchar(255)," +
//				"date varchar(255),timeslot varchar(255)," +
//				"finishtime varchar(255),finishflag varchar(255)," +
//				"uploadflag varchar(255),tableflag varchar(255)," +
//				"filesavepath varchar(255))");
		
		db.execSQL("CREATE TABLE IF NOT EXISTS USER(id integer primary key,name varchar(255),userName varchar(255),image varchar(255),sex varchar(255),userRole varchar(255),appName varchar(255),inspectType varchar(255))");
		db.execSQL("CREATE TABLE IF NOT EXISTS USERROLE(id integer primary key, user_id integer , name varchar(255))");
		db.execSQL("CREATE TABLE IF NOT EXISTS TASK(taskId integer primary key autoincrement,id integer,inspectPlanId integer,inspectTableId integer,inspectTableRecord integer,userId integer,deviceId integer,faultCount integer,inspectTime varchar(255),createtime varchar(255),status integer,taskDate varchar(255),timeStart integer,timeEnd integer,appId integer,tableName varchar(255),planName varchar(255),deviceName varchar(255),userName varchar(255),startDay varchar(255),endDay varchar(255),localStatus integer)");
		db.execSQL("CREATE TABLE IF NOT EXISTS HISTORY(historyId integer primary key autoincrement, userId integer, userName varchar(255),filePath varchar(255),inspectTableName varchar(255),deviceName varchar(255),uploadFlag integer, inspectTime varchar(255))");
		db.execSQL("CREATE TABLE IF NOT EXISTS INSPECTIMAGE(id integer primary key autoincrement,itemId integer,filePath varchar(255),inspectTableName varchar(255),itemName varchar(255),userId integer,uploadFlag interger,tableRecordId integer,itemRecordId integer,appId integer)");
		Log.i("msg", "DB创建完成...");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
      	db.execSQL("DROP TABLE IF EXISTS USER");  
      	db.execSQL("DROP TABLE IF EXISTS TASK"); 
      	db.execSQL("DROP TABLE IF EXISTS HISTORY"); 
      	db.execSQL("DROP TABLE IF EXISTS INSPECTIMAGE");
        onCreate(db); 
	}

}
