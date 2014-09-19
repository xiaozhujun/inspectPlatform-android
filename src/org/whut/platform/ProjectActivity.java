package org.whut.platform;

import java.util.ArrayList;
import java.util.List;

import org.whut.application.MyApplication;
import org.whut.entity.Location;
import org.whut.inspectplatform.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;




public class ProjectActivity extends Activity{

	private ListView listView;
	private ImageView left_back;
	private ArrayAdapter<String> adapter;
	private List<String> project_list;
	private Location locationData;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_project);
		
		MyApplication.getInstance().addActivity(this);
	
		listView = (ListView) findViewById(R.id.listView1);
		
		left_back = (ImageView) findViewById(R.id.iv_topbar_left_back);
	
		left_back.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		
		project_list = new ArrayList<String>();
		
		project_list = getIntent().getExtras().getStringArrayList("project_list");
		
		locationData = (Location)getIntent().getExtras().getSerializable("locationData");
		
		adapter = new ArrayAdapter<String>(ProjectActivity.this, R.layout.listitem_project, R.id.projectName, project_list);
		
		listView.setAdapter(adapter);
		
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Intent it = new Intent(ProjectActivity.this,InspectActivity.class);
				it.putExtra("locationData", locationData);
				it.putExtra("tableName", project_list.get(arg2));
				startActivity(it);
				finish();
			}
		});
	}
}
