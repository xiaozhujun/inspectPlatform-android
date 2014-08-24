package org.whut.database.entity.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.whut.database.DBHelper;
import org.whut.database.entity.service.InspectImageService;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class InspectImageServiceDao implements InspectImageService{

	private DBHelper mySQLite;
	private SQLiteDatabase db;
	
	public InspectImageServiceDao(Context context){
		mySQLite = DBHelper.getInstance(context);
		db = mySQLite.getWritableDatabase();
	}
	
	@Override
	public void addInspectImage(int itemId, String filePath,String inspectTableName,String itemName,int uploadFlag) {
		// TODO Auto-generated method stub
		db.beginTransaction();
		db.execSQL("insert into inspectimage(itemId,filePath,inspectTableName,itemName,uploadFlag) values(?,?,?,?,?)",new Object[]{itemId,filePath,inspectTableName,itemName,uploadFlag});
		db.setTransactionSuccessful();
		db.endTransaction();
	}

	public void deleteInspectImages(String inspectTableName) {
		// TODO Auto-generated method stub
		db.beginTransaction();
		db.execSQL("delete from inspectimage where inspectTableName=?",new Object[]{inspectTableName});
		db.setTransactionSuccessful();
		db.endTransaction();
	}
	
	public void deleteInspectImagesByFilePath(String filePath){
		db.beginTransaction();
		db.execSQL("delete from inspectimage where filePath=?", new Object[]{filePath});
		db.setTransactionSuccessful();
		db.endTransaction();
	}

	//根据点检表名称，返回图片的地址集合
	public List<String> getInspectImages(String inspectTableName) {
		// TODO Auto-generated method stub
		List<String> list = new ArrayList<String>();
 		Cursor cursor = db.rawQuery("select * from inspectimage where inspectTableName=?", new String[]{inspectTableName});
 		while(cursor.moveToNext()){
 			String temp = cursor.getString(cursor.getColumnIndex("filePath"));
 			list.add(temp);
 		}
		return list;
	}

	//根据图片的地址，返回图片的信息集合
	public Map<String, String> getImageInfo(String filePath) {
		// TODO Auto-generated method stub
		Map<String,String> map = new HashMap<String, String>();
		Cursor cursor = db.rawQuery("select * from inspectimage where filePath=?", new String[]{filePath});
		while(cursor.moveToNext()){
			map.put("inspectTableName",cursor.getString(cursor.getColumnIndex("inspectTableName")));
			map.put("itemName", cursor.getString(cursor.getColumnIndex("itemName")));
			map.put("filePath", cursor.getString(cursor.getColumnIndex("filePath")));	
			map.put("uploadFlag", cursor.getInt(cursor.getColumnIndex("uploadFlag"))+"");
		}
		return map;
	}

	@Override
	public void updateInspectImage(String inspectTableName,String filePath,int itemId,int tableRecordId,int itemRecordId) {
		// TODO Auto-generated method stub
		db.beginTransaction();
		db.execSQL("update inspectimage set tableRecordId=?,itemRecordId=? where inspectTableName=? and itemId=? and filePath = ?",new Object[]{tableRecordId,itemRecordId,inspectTableName,itemId,filePath});
		Log.i("database", itemId+";"+tableRecordId+";"+itemRecordId+";"+filePath);
		db.setTransactionSuccessful();
		db.endTransaction();
		
	}

	public List<Map<String, String>> getInspectImagesInfo(
			String inspectTableName) {
		// TODO Auto-generated method stub
		List<Map<String,String>> list = new ArrayList<Map<String,String>>();
		Cursor cursor = db.rawQuery("select * from inspectimage where inspectTableName=?", new String[]{inspectTableName});
		while(cursor.moveToNext()){
			Map<String,String> map = new HashMap<String, String>();
			map.put("itemId", cursor.getInt(cursor.getColumnIndex("itemId"))+"");

			map.put("filePath", cursor.getString(cursor.getColumnIndex("filePath")));
			
			Log.i("database", cursor.getInt(cursor.getColumnIndex("itemId"))+"");
			Log.i("database", cursor.getString(cursor.getColumnIndex("filePath")));
			
			
			list.add(map);
		}
		return list;
	}

	public String getTableRecordId(String itemId) {
		// TODO Auto-generated method stub
		Cursor cursor = db.rawQuery("select * from inspectimage where itemId=?", new String[]{itemId});
		while(cursor.moveToNext()){
			return cursor.getInt(cursor.getColumnIndex("tableRecordId"))+"";
		}		
		return null;
	}

	public String getItemRecordId(String itemId) {
		// TODO Auto-generated method stub
		Cursor cursor = db.rawQuery("select * from inspectimage where itemId=?", new String[]{itemId});
		while(cursor.moveToNext()){
			return cursor.getInt(cursor.getColumnIndex("itemRecordId"))+"";
		}		
		return null;
	}

	public void updateUploadFlag(String filePath) {
		// TODO Auto-generated method stub
		db.beginTransaction();
		db.execSQL("update inspectimage set uploadFlag=? where filePath=?",new Object[]{1,filePath});
		db.setTransactionSuccessful();
		db.endTransaction();
	}

	public int validateInspectImage(String filePath) {
		// TODO Auto-generated method stub
		Cursor cursor = db.rawQuery("select * from inspectimage where filePath=?", new String[]{filePath});
		while(cursor.moveToNext()){
			return cursor.getInt(cursor.getColumnIndex("itemRecordId"));
		}
		return 0;
	}

	public int getItemId(String filePath) {
		// TODO Auto-generated method stub
		Cursor cursor = db.rawQuery("select * from inspectimage where filePath=?", new String[]{filePath});
		while(cursor.moveToNext()){
			return cursor.getInt(cursor.getColumnIndex("itemId"));
		}
		return 0;
	}

	
	
	
	
}
