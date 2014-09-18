package org.whut.database.entity.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.whut.database.DBHelper;
import org.whut.database.entity.service.UserRoleService;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class UserRoleServiceDao implements UserRoleService{

	private DBHelper mySQLite;
	private SQLiteDatabase db;
	
	public UserRoleServiceDao(Context context){
		mySQLite = DBHelper.getInstance(context);
		db = mySQLite.getWritableDatabase();
	}
	
	
	
	@Override
	public void addUserRole(int user_id, String UserRole) {
		// TODO Auto-generated method stub
		db.beginTransaction();
		db.execSQL("insert into userrole(user_id,name) values(?,?)", new String[]{user_id+"",UserRole});
		db.setTransactionSuccessful();
		db.endTransaction();
	}



	@Override
	public List<String> getRoleById(int user_id) {
		// TODO Auto-generated method stub
		List<String> list = new ArrayList<String>();
		Cursor cursor = db.rawQuery("select * from userrole where user_id=?", new String[]{user_id+""});
		while(cursor.moveToNext()){
			String role = cursor.getString(cursor.getColumnIndex("name"));
			list.add(role);
		}
		
		return list;
	}

}
