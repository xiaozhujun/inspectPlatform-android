package org.whut.platform;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.whut.adapter.MyAdapter;
import org.whut.application.MyApplication;
import org.whut.database.entity.Task;
import org.whut.database.entity.service.impl.TaskServiceDao;
import org.whut.entity.Location;
import org.whut.inspectplatform.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
//import android.widget.TextView;

public class TaskActivity extends Activity{

	private ListView listView;
	private MyAdapter adapter;
	
	private ImageView left_back;
	
	private List<Map<String,Object>> data;
	private List<Task> taskData;
	private Location locationData;
	
	private TaskServiceDao dao;
	
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task);
		
		MyApplication.getInstance().addActivity(this);
		
		taskData  = (List<Task>) getIntent().getSerializableExtra("taskData");
		locationData = (Location) getIntent().getExtras().getSerializable("locationData");
		
		listView = (ListView) findViewById(R.id.listView1);
		left_back = (ImageView) findViewById(R.id.iv_topbar_left_back);
		
		left_back.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		
		dao = new TaskServiceDao(TaskActivity.this);
		
		data = new ArrayList<Map<String,Object>>();
		
		for(int i=0;i<taskData.size();i++){
			Map<String,Object> map = new HashMap<String, Object>();
			map.put("planName", taskData.get(i).getPlanName());
			//任务编号
			map.put("Id", taskData.get(i).getId());
			map.put("localStatus", dao.checkLocalStatus(taskData.get(i).getId()));
			data.add(map);
		}
		
		adapter = new MyAdapter(data, TaskActivity.this);
		
		listView.setAdapter(adapter);
		
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, final int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				switch(Integer.valueOf(data.get(arg2).get("localStatus").toString())){
					case 0:
						Intent it = new Intent(TaskActivity.this,InspectActivity.class);
						it.putExtra("tableName", taskData.get(arg2).getTableName());
						it.putExtra("locationData", locationData);
						it.putExtra("taskData", (Serializable)taskData);
						it.putExtra("task", taskData.get(arg2));
						startActivity(it);
						finish();
						break;
					case 1:
						Builder alertDialog = new AlertDialog.Builder(TaskActivity.this);
						alertDialog.setTitle("提示").setMessage("该任务已经完成，是否再次点检？").setNegativeButton("取消", null)
						.setPositiveButton("确定", new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								Intent it = new Intent(TaskActivity.this,InspectActivity.class);
								it.putExtra("tableName", taskData.get(arg2).getTableName());
								it.putExtra("locationData", locationData);
								it.putExtra("taskData", (Serializable)taskData);
								it.putExtra("task", taskData.get(arg2));
								startActivity(it);
								finish();
							}
						}).show();
						break;
				}
					
			}
		});

	}
}
