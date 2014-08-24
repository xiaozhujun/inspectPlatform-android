package org.whut.platform;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.whut.adapter.MyListAdapter;
import org.whut.application.MyApplication;
import org.whut.database.entity.service.impl.HistoryServiceDao;
import org.whut.entity.Location;
import org.whut.inspectplatform.R;
import org.whut.inspectplatform.R.color;
import org.whut.utils.FileUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class HistoryActivity extends Activity{


	private MyListAdapter adapter;
	private HistoryServiceDao dao;

	private List<Map<String,String>> list;

	//topBar
	private ImageView left_back;
	private RelativeLayout tv_topbar_right_map_layout;
	private TextView tv_topbar_right_edit;
	private boolean ButtonsOn =false;

	//Content
	private ListView listView;
	private RelativeLayout no_collection;
	private FrameLayout bottom_bar;
	private Button btn_select_all;
	private Button btn_cancel_all;
	private Button btn_upload;
	private Button btn_delete;

	private Location locationData;


	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if(ButtonsOn){
			bottom_bar.setVisibility(View.GONE);
			tv_topbar_right_edit.setText("编辑");
			adapter.setVisibility(View.GONE);
			adapter.notifyDataSetChanged();
			ButtonsOn = false;
		}else{
			new Thread(new MainActivity.HandleHistoryThread()).start();
			HistoryActivity.this.finish();
		}
	}



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		MyApplication.getInstance().addActivity(this);
		setContentView(R.layout.activity_history);
		//控件初始化
		left_back = (ImageView) findViewById(R.id.iv_topbar_left_back);
		tv_topbar_right_map_layout = (RelativeLayout) findViewById(R.id.tv_topbar_right_map_layout);
		tv_topbar_right_edit = (TextView) findViewById(R.id.tv_topbar_right_edit);
		listView = (ListView) findViewById(R.id.listView_history);
		no_collection = (RelativeLayout) findViewById(R.id.no_collection);
		bottom_bar = (FrameLayout) findViewById(R.id.bottom_bar);
		btn_select_all = (Button) findViewById(R.id.btnSelAll);
		btn_cancel_all = (Button) findViewById(R.id.btnCancelAll);
		btn_upload = (Button) findViewById(R.id.btnUpload);
		btn_delete = (Button) findViewById(R.id.btnDelAll);


		left_back.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new Thread(new MainActivity.HandleHistoryThread()).start();
				finish();
			}
		});


		dao = new HistoryServiceDao(HistoryActivity.this);


		locationData = (Location) getIntent().getSerializableExtra("locationData");

		list = dao.queryHistory(locationData.getUserId());

		if(list.size()!=0){
			no_collection.setVisibility(View.GONE);
			adapter = new MyListAdapter(list,HistoryActivity.this);
			listView.setAdapter(adapter);

			tv_topbar_right_map_layout.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					TextView view = (TextView)tv_topbar_right_map_layout.findViewById(R.id.tv_topbar_right_edit);

					if(view.getText().toString().equals("编辑")){
						bottom_bar.setVisibility(View.VISIBLE);
						view.setText("完成");
						adapter.setVisibility(View.VISIBLE);
						ButtonsOn = true;
						adapter.notifyDataSetChanged(); 
					}else{
						bottom_bar.setVisibility(View.GONE);
						view.setText("编辑");
						adapter.setVisibility(View.INVISIBLE);
						ButtonsOn = false;
						adapter.notifyDataSetChanged(); 
					}
				}
			});

			btn_select_all.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					for(int i=0;i<list.size();i++){
						MyListAdapter.getIsSelected().put(i, true);
					}
					adapter.notifyDataSetChanged();			
				}
			});


			btn_cancel_all.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					for(int i=0;i<list.size();i++){
						MyListAdapter.getIsSelected().put(i, false);
					}
					adapter.notifyDataSetChanged();
				}
			});

			btn_delete.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Builder alertDialog = new AlertDialog.Builder(HistoryActivity.this);
					alertDialog.setTitle("提示").setMessage("删除会同时删除点检记录和点检表且无法恢复，是否删除？").setPositiveButton("确定", new DialogInterface.OnClickListener(){

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							changeData();
							adapter.notifyDataSetChanged();
						}

					}).setNegativeButton("取消",null).show();



				}
			});

			btn_upload.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					//批量上传
					List<Integer> selected = new ArrayList<Integer>();
					int location=0;
					for(int i=0;i<list.size();i++){
						if(MyListAdapter.getIsSelected().get(i)){
							location = i;
							selected.add(1);
						}
					}

					if(selected.size()==0){
						Toast.makeText(HistoryActivity.this,"您尚未选中任何点检表！", Toast.LENGTH_SHORT).show();
					}else if(selected.size()==1){
						if(dao.isUploaded(list.get(location).get("filePath"))==1){
							Toast.makeText(HistoryActivity.this, "该点检表已上传！请选择其他未上传的点检表！", Toast.LENGTH_SHORT).show();
						}else if(dao.isUploaded(list.get(location).get("filePath"))==-1){
							Toast.makeText(HistoryActivity.this, "并无该项点检记录！", Toast.LENGTH_SHORT).show();
						}else{
							Intent it = new Intent(HistoryActivity.this,UploadActivity.class);
							it.putExtra("locationData", locationData);
							it.putExtra("filePath", list.get(location).get("filePath"));
							it.putExtra("inspectTableName", list.get(location).get("inspectTableName"));
							it.putExtra("inspectTime", list.get(location).get("inspectTime"));
							startActivity(it);
							finish();
						}
					}else if(selected.size()>1){
						Toast.makeText(HistoryActivity.this, "暂不支持批量上传功能，请选择一项上传！", Toast.LENGTH_SHORT).show();
					}
				}
			});


		}else{
			listView.setVisibility(View.GONE);
			no_collection.setVisibility(View.VISIBLE);
			tv_topbar_right_map_layout.setClickable(false);
			tv_topbar_right_edit.setClickable(false);
			tv_topbar_right_edit.setTextColor(color.gray);
		}
	}



	private void changeData(){
		List<Integer> selectedItem = new ArrayList<Integer>();
		for(int i=0;i<list.size();i++){
			if(MyListAdapter.getIsSelected().get(i)){
				selectedItem.add(i);
			}
		}

		for(int j=0;j<selectedItem.size();j++){
			//通过filePath来删除
			String filePath = list.get(j).get("filePath");
			dao.deleteHistory(filePath);
			FileUtils.deleteFile(filePath);
		}

		list = new ArrayList<Map<String,String>>();
		list = dao.queryHistory(locationData.getUserId());
		adapter = new MyListAdapter(list, HistoryActivity.this);
		adapter.setVisibility(View.VISIBLE);
		listView.setAdapter(adapter);

		if(list.size()==0){
			no_collection.setVisibility(View.VISIBLE);
			tv_topbar_right_map_layout.setClickable(false);
			tv_topbar_right_edit.setClickable(false);
			tv_topbar_right_edit.setText("编辑");
			tv_topbar_right_edit.setTextColor(color.gray);
			bottom_bar.setVisibility(View.GONE);
		}
	}
}
