package org.whut.database.entity.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.whut.database.DBHelper;
import org.whut.database.entity.History;
import org.whut.database.entity.service.HistoryService;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class HistoryServiceDao implements HistoryService{
	private DBHelper mySQLite;
	private SQLiteDatabase db;

	public HistoryServiceDao(Context context){
		mySQLite = DBHelper.getInstance(context);
		db = mySQLite.getWritableDatabase();
	}

	@Override
	public void addHistory(History history) {
		// TODO Auto-generated method stub
		db.beginTransaction();
		db.execSQL("insert into history(userId,userName,filePath,inspectTableName,uploadFlag,inspectTime) values(?,?,?,?,?,?)",
				new Object[]{history.getUserId(),history.getUserName(),history.getFilePath(),history.getInspectTableName(),history.getUploadFlag(),history.getInspectTime()});
		Log.i("msg", "已完成点检历史添加"+history.getUserId()+","+history.getInspectTableName()+","+history.getUploadFlag()+","+history.getInspectTime()+","+history.getUserName()+","+history.getFilePath());
		
		db.setTransactionSuccessful();
		db.endTransaction();
	}

	
	//返回历史数量
	@Override
	public int findHistory(int userId) {
		// TODO Auto-generated method stub
		Cursor cursor = db.rawQuery("select * from history where userId=?", new String[]{userId+""});
		while(cursor.moveToNext()){
			return cursor.getCount();
		}
		return 0;
	}

	public List<Map<String,String>> queryHistory(int userId) {
		// TODO Auto-generated method stub
		Cursor cursor =  db.rawQuery("select * from history where userId = ?", new String[]{userId+""});
		List<Map<String,String>> list = new ArrayList<Map<String,String>>();
		while(cursor.moveToNext()){
			Map<String,String> map = new HashMap<String,String>();
			map.put("userId", userId+"");
			map.put("filePath", cursor.getString(cursor.getColumnIndex("filePath")));
			map.put("userName", cursor.getString(cursor.getColumnIndex("userName")));
			map.put("inspectTime",cursor.getString(cursor.getColumnIndex("inspectTime")));
			map.put("inspectTableName",cursor.getString(cursor.getColumnIndex("inspectTableName")));
			map.put("uploadFlag",cursor.getInt(cursor.getColumnIndex("uploadFlag"))+"");
			list.add(map);
		}
		return list;
	}

	public void deleteHistory(String filePath) {
		// TODO Auto-generated method stub
		db.beginTransaction();
		db.execSQL("delete from history where filePath=?", new String[]{filePath});
		Log.i("msg", filePath+"已删除");
		db.setTransactionSuccessful();
		db.endTransaction();
	}

	@Override
	public void updateUploadFlag(String filePath) {
		// TODO Auto-generated method stub
		db.beginTransaction();
		db.execSQL("update history set uploadFlag=? where filePath=?", new Object[]{1,filePath});
		db.setTransactionSuccessful();
		db.endTransaction();
	}

	@Override
	public int isUploaded(String filePath) {
		// TODO Auto-generated method stub
		Cursor cursor = db.rawQuery("select * from history where filePath=?" ,new String[]{filePath});
		while(cursor.moveToNext()){
			return cursor.getInt(cursor.getColumnIndex("uploadFlag"));
		}
		return -1;
	}
	
	
	
}
