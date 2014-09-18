package org.whut.database.entity.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.whut.database.DBHelper;
import org.whut.database.entity.service.UserService;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class UserServiceDao implements UserService{

	private DBHelper mySQLite;
	private SQLiteDatabase db;

	public UserServiceDao(Context context){
		mySQLite = DBHelper.getInstance(context);
		db = mySQLite.getWritableDatabase();
	}
	
	@Override
	public void addUser(HashMap<String, Object> map) {
		// TODO Auto-generated method stub
		db.beginTransaction();
		db.execSQL("insert into user(name,userName,id,image,sex,userRole,appName,inspectType) values(?,?,?,?,?,?,?,?)",
				new Object[]{map.get("name"),
				map.get("userName"),map.get("id"),map.get("image"),map.get("sex"),map.get("userRole"),map.get("appName"),map.get("inspectType")});
		Log.i("msg", "已完成用户添加.");
		db.setTransactionSuccessful();
		db.endTransaction();
	}

	@Override
	public boolean findUserById(int user_id) {
		// TODO Auto-generated method stub
		Cursor cursor = db.rawQuery("select * from user where id=?",  new String[]{user_id+""});
		while(cursor.moveToNext()){
			Log.i("database", cursor.getString(cursor.getColumnIndex("userName")));
			Log.i("database", cursor.getString(cursor.getColumnIndex("image")));	
			return true;
		}
		return false;
	}

	//存在用户返回用户的id，不存在返回0
	@Override
	public int findUserByUserName(String username) {
		// TODO Auto-generated method stub
		Cursor cursor = db.rawQuery("select * from user where username=?", new String[]{username});
		while(cursor.moveToNext()){
			return Integer.parseInt(cursor.getString(cursor.getColumnIndex("id")));
		}
		return 0;
	}

	@Override
	public List<String> getRoleById(int user_id) {
		// TODO Auto-generated method stub
		List<String> list = new ArrayList<String>();
		Cursor cursor = db.rawQuery("select * from userrole where id=?", new String[]{user_id+""});
		while(cursor.moveToNext()){
			String role = cursor.getString(cursor.getColumnIndex("name"));
			list.add(role);
		}
		
		return list;
	}

	public String findNameByUserName(String userName) {
		// TODO Auto-generated method stub
		Cursor cursor = db.rawQuery("select * from user where username=?", new String[]{userName});
		while(cursor.moveToNext()){
			return cursor.getString(cursor.getColumnIndex("name"));
		}
		return null;
	}

	public String findImageByUserName(String userName) {
		// TODO Auto-generated method stub
		Cursor cursor = db.rawQuery("select * from user where username=?", new String[]{userName});
		while(cursor.moveToNext()){
			return cursor.getString(cursor.getColumnIndex("image"));
		}
		return null;
	}

	public int findInspectTypeByUserId(int userId) {
		// TODO Auto-generated method stub
		Cursor cursor = db.rawQuery("select * from user where id=?", new String[]{userId+""});
		while(cursor.moveToNext()){
			return cursor.getInt(cursor.getColumnIndex("inspectType"));
		}
		return -1;
	}
	
	


}
