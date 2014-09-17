package org.whut.database.entity.service.impl;

import org.whut.database.DBHelper;
import org.whut.database.entity.Task;
import org.whut.database.entity.service.TaskService;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class TaskServiceDao implements TaskService{

	private DBHelper mySQLite;
	private SQLiteDatabase db;

	public TaskServiceDao(Context context){
		mySQLite = DBHelper.getInstance(context);
		db = mySQLite.getWritableDatabase();
	}

	@Override
	public void addTask(Task task) {
		// TODO Auto-generated method stub
		db.beginTransaction();
		db.execSQL("insert into task(id,inspectPlanId,inspectTableId,inspectTableRecord,userId,deviceId,faultCount,inspectTime,createtime,status,taskDate,timeStart,timeEnd,appId,tableName,planName,deviceName,userName,startDay,endDay,localStatus) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
				new Object[]{task.getId(),task.getInspectPlanId(),task.getInspectTableId(),task.getInspectTableRecordId(),task.getUserId(),task.getDeviceId(),task.getFaultCount(),task.getInspectTime(),task.getCreatetime(),task.getStatus(),task.getTaskDate(),task.getTimeStart(),task.getTimeEnd(),task.getAppId(),task.getTableName(),task.getPlanName(),task.getDeviceName(),task.getUserName(),task.getStartDay(),task.getEndDay(),task.getLocalStatus()});
		Log.i("msg", "已完成任务添加.");
		db.setTransactionSuccessful();
		db.endTransaction();
	}

	//存在 返回id，不存在返回0
	@Override
	public int findTask(int id) {
		// TODO Auto-generated method stub
		Cursor cursor = db.rawQuery("select * from task where id=?", new String[]{id+""});
		while(cursor.moveToNext()){
			return cursor.getInt(cursor.getColumnIndex("id"));
		}
		return 0;
	}

	//
	@Override
	public void updateStatus(int id,int status) {
		// TODO Auto-generated method stub
		db.beginTransaction();
		db.execSQL("update task set status=? where id=?", new Object[]{status,id});
		db.setTransactionSuccessful();
		Log.i("msg", "更新任务状态成功！");
		db.endTransaction();
	}

	public void updateLocalStatus(int id) {
		// TODO Auto-generated method stub
		db.beginTransaction();
		db.execSQL("update task set localStatus=? where id=?",new Object[]{1,id});
		db.setTransactionSuccessful();
		Log.i("msg", "更新任务状态成功！");
		db.endTransaction();
	}

	//-1：该任务不存在，0：该任务未完成，1：该任务已完成
	public int checkLocalStatus(Integer id) {
		// TODO Auto-generated method stub
		Cursor cursor = db.rawQuery("select * from task where id = ?", new String[]{id+""});
		while(cursor.moveToNext()){
			return cursor.getInt(cursor.getColumnIndex("localStatus"));
		}
		return -1;
	}

	public int queryUnfinishedTask(int userId) {
		// TODO Auto-generated method stub
		int temp = 0;
		Cursor cursor = db.rawQuery("select * from task where userId=? and localStatus=?", new String[]{userId+"","0"});
		while(cursor.moveToNext()){
			temp = temp+1;
		}
		return temp;
	}






	
	
	
	
	
}
