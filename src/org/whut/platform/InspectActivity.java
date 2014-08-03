package org.whut.platform;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import android.app.AlertDialog;

import org.whut.adapter.MyExpandableListAdapter;
import org.whut.application.MyApplication;
import org.whut.database.entity.History;
import org.whut.database.entity.Task;
import org.whut.database.entity.service.impl.HistoryServiceDao;
import org.whut.database.entity.service.impl.TaskServiceDao;
import org.whut.entity.Listable;
import org.whut.entity.Location;
import org.whut.entity.Tag;
import org.whut.inspectplatform.R;
import org.whut.service.RFIDService;
import org.whut.strings.FileStrings;
import org.whut.utils.FileUtils;
import org.whut.utils.XmlUtils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class InspectActivity extends Activity implements ExpandableListView.OnGroupClickListener,ExpandableListView.OnChildClickListener{

	//RFIDService相关
	private static final String cardType = "0x02";
	private static final String activity = "org.whut.platform.InspectActivity";

	private ExpandableListView listView;
	private List<String> groupList;
	private List<List<String>> childList;
	private MyExpandableListAdapter adapter;

	private List<Boolean> isInspected;

	//提示框
	private ProgressDialog dialog;
	private Timer timerDialog;


	//TopBar
	private ImageView topbar_btn_back;
	private TextView topbar_title;
	private RelativeLayout topbar_menu;


	//BottomView
	private Button startScan;

	//记录背景颜色
	private List<List<Integer>> bg_color;
	//记录点检结果
	private List<List<Integer>> result;

	//Handler处理消息，更新UI
	private Handler handler;
	private MyBroadcastReceiver receiver;

	//从上一Activity中传来的数据
	private Location locationData;
	private List<Task> taskData;
	private Task task;
	private String tableName;
	
	private String inspectTime;
	private String deviceNum;
	
	private String fileDir;
	private String filePath;
	
	private HistoryServiceDao dao;

	//菜单相关
	private View menuView;
	private GridView menuGrid;
	private AlertDialog menuDialog;

	/** 菜单图片 **/
	int[] menu_image_array = {R.drawable.menu_filemanager,R.drawable.menu_refresh,
			R.drawable.menu_help,R.drawable.menu_quit};
	/** 菜单文字 **/
	String[] menu_name_array = {"保存结果", "重置数据", "帮助", "退出" };


	@SuppressLint("HandlerLeak")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_inspect);

		MyApplication.getInstance().addActivity(this);
		
		FileUtils.setInspectDir(FileStrings.inspectDir);

		initData();

		//初始化菜单

		menuView = View.inflate(this, R.layout.gridview_menu, null);

		menuDialog = new AlertDialog.Builder(this).create();

		menuDialog.setView(menuView);

		menuGrid = (GridView) menuView.findViewById(R.id.gridview);

		menuGrid.setAdapter(getMenuAdapter(menu_name_array,menu_image_array));

		menuGrid.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				switch(arg2){
				case 0://保存
					//此方法将点检结果保存在相应的点检表中，并返回点检表名
					try {
						XmlUtils.saveInspectResult(result, filePath);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					menuDialog.dismiss();
					Toast.makeText(InspectActivity.this, "点检结果已保存在<"+filePath+">中", Toast.LENGTH_SHORT).show();

					History history = new History();
					history.setFilePath(filePath);
					history.setUserId(locationData.getUserId());
					history.setUserName(locationData.getUserName());
					history.setTableName(tableName);
					history.setUploadFlag(0);
					history.setInspectTime(inspectTime);
					dao.addHistory(history);
					
					//若为日常任务，则更新数据库localStatus为1（已完成）
					if(taskData!=null){
						TaskServiceDao dao = new TaskServiceDao(InspectActivity.this);
						dao.updateLocalStatus(task.getId());
					}
					Intent it = new Intent(InspectActivity.this,UploadActivity.class);
					it.putExtra("locationData", locationData);
					it.putExtra("filePath", filePath);
					it.putExtra("tableName", tableName);
					it.putExtra("inspectTime", inspectTime);
					new Thread(new MainActivity.HandleHistoryThread()).start();
					startActivity(it);
					finish();
					break;
				case 1://重置数据
					menuDialog.dismiss();
					Builder alertDialog = new AlertDialog.Builder(InspectActivity.this);
					alertDialog.setTitle("提示").setMessage("是否重置本次点检结果？（警告：已点检项将全部消失）").setNegativeButton("取消", null)
					.setPositiveButton("确定", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							init();
							adapter =  new MyExpandableListAdapter(InspectActivity.this, groupList, childList, bg_color);
							listView.setAdapter(adapter);
							try {
								XmlUtils.saveInspectResult(result, filePath);
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							Toast.makeText(InspectActivity.this, "点检结果已重置，请重新输入点检结果！", Toast.LENGTH_SHORT).show();
						}
					}).show();

					break;
				case 2://帮助
					menuDialog.dismiss();
					Builder alertDialog1 = new AlertDialog.Builder(InspectActivity.this);
					alertDialog1.setTitle("帮助").setMessage("1.扫描标签之后可以点检相应的区域。\n 2、点检完毕之后，点击菜单-保存，可将点检结果存入对应的点检表中。\n 3.重置按钮可以重置本次点检结果。\n").setPositiveButton("确定", null).show();
					break;
				case 3://退出
					menuDialog.dismiss();
					Builder alertDialog2 = new AlertDialog.Builder(InspectActivity.this);
					alertDialog2.setTitle("提示").setMessage("是否放弃此次点检结果？").setNegativeButton("取消", null)
					.setPositiveButton("确定", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							FileUtils.deleteFile(filePath);
							if(taskData!=null){
								Intent it = new Intent(InspectActivity.this,TaskActivity.class);
								it.putExtra("taskData", (Serializable)taskData);
								it.putExtra("locationData", locationData);
								startActivity(it);
								finish();
							}else{
								finish();
							}
						}
					}).show();
					break;

				}
			}
		});



		handler = new Handler(){

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub

				switch(msg.what){
				case 0://计时器计时完成，没有扫描到标签卡
					Toast.makeText(InspectActivity.this, "未检测到标签卡，请重试！", Toast.LENGTH_SHORT).show();
					Intent it = new Intent();
					it.setAction("org.whut.service.RFIDService");
					it.putExtra("stopflag", true);
					sendBroadcast(it);
					break;
				}
			}
		};

		adapter = new MyExpandableListAdapter(InspectActivity.this, groupList, childList,bg_color);
		listView.setAdapter(adapter);
		listView.setOnGroupExpandListener(new OnGroupExpandListener() {

			@Override
			public void onGroupExpand(int groupPosition) {
				// TODO Auto-generated method stub
				for(int i=0;i<groupList.size();i++){
					if(groupPosition!=i){
						listView.collapseGroup(i);
					}
				}

				if(!isInspected.get(groupPosition)){
					listView.collapseGroup(groupPosition);
					Toast.makeText(InspectActivity.this, "请扫描相应区域标签开始点检！", Toast.LENGTH_SHORT).show();
				}
			}
		});

		listView.setOnChildClickListener(this);


		topbar_btn_back.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Builder alertDialog = new AlertDialog.Builder(InspectActivity.this);
				alertDialog.setTitle("提示").setMessage("是否放弃此次点检结果？").setNegativeButton("取消", null)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						FileUtils.deleteFile(filePath);
						if(taskData!=null){
							Intent it = new Intent(InspectActivity.this,TaskActivity.class);
							it.putExtra("taskData", (Serializable)taskData);
							it.putExtra("locationData", locationData);
							startActivity(it);
							finish();
						}else{
							finish();
						}
					}
				}).show();
			}
		});

		topbar_title.setText(getIntent().getExtras().getString("tableName"));


		topbar_menu.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				menuDialog.show();
			}
		});

		startScan.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.show();
				timerDialog = new Timer();
				//七秒后取消扫卡提示框
				timerDialog.schedule(new TimerTask() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						dialog.cancel();
						Message msg = Message.obtain();
						msg.what = 0;
						handler.sendMessage(msg);
					}
				}, 7000);

				//开启扫卡
				Intent sendToService = new Intent(InspectActivity.this,RFIDService.class);
				sendToService.putExtra("cardType", cardType);
				sendToService.putExtra("activity", activity);
				startService(sendToService);
			}
		});

	}


	@SuppressWarnings("unchecked")
	private void initData() {
		// TODO Auto-generated method stub
		try {
			//初始化组件
			listView = (ExpandableListView) findViewById(R.id.explistView);
			topbar_btn_back = (ImageView) findViewById(R.id.iv_topbar_left_back);
			topbar_title = (TextView) findViewById(R.id.tv_topbar_middle_detail);
			topbar_menu = (RelativeLayout) findViewById(R.id.tv_topbar_right_map_layout);
			startScan = (Button) findViewById(R.id.scanTag);

			//初始化对话提示框
			dialog = new ProgressDialog(this);
			dialog.setCancelable(true);
			dialog.setIndeterminate(false);
			dialog.setTitle("提示");
			dialog.setMessage("正在扫描标签卡，请稍候...");

			dao = new HistoryServiceDao(InspectActivity.this);

			locationData = (Location) getIntent().getExtras().getSerializable("locationData");
			
			if(getIntent().getExtras().getSerializable("taskData")!=null){
				taskData = (List<Task>) getIntent().getExtras().getSerializable("taskData");
				task = (Task) getIntent().getExtras().getSerializable("task");
			}
			
			tableName = getIntent().getExtras().getString("tableName");

			fileDir = FileStrings.BASE_PATH;

			filePath = getFilePath(tableName);
			
			inspectTime = createFormatTime(filePath);

			//创建本次点检文件
			XmlUtils.createFile(filePath,locationData.getUserId(),locationData.getUserName(),inspectTime);

			//初始化数据
			groupList = XmlUtils.getInspectLocation(filePath);
			childList = XmlUtils.getInspectField(filePath);

			init();




		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	//通过Intent中传递过来的tableName获取解析的xml文件地址
	@SuppressLint({ "SdCardPath", "SimpleDateFormat" })
	private String getFilePath(String tableName){
		String fileFullName = null;
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		String newFileName = tableName+"-"+locationData.getUserName().trim()+"-"+format.format(new Date())+".xml";
		String tempFile = fileDir+"/"+tableName+".xml";
		if(FileUtils.prepareInspectFile(tempFile,newFileName)){
			String inspectDir = FileUtils.getInspectDir();
			fileFullName = inspectDir+"/"+newFileName;
		}
		return fileFullName;
	}


	@SuppressLint("ResourceAsColor")
	@Override
	public boolean onChildClick(ExpandableListView parent, View v,
			final int groupPosition, final int childPosition, long id) {
		// TODO Auto-generated method stub
		Builder alertDialog = new AlertDialog.Builder(InspectActivity.this);
		alertDialog.setTitle(childList.get(groupPosition).get(childPosition)).setSingleChoiceItems(new String[]{"正常","异常","无"},result.get(groupPosition).get(childPosition),new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				//记录点检结果
				result.get(groupPosition).set(childPosition, which);
				Log.i("result", "groupPosition="+groupPosition+",childPosition="+childPosition+",which="+which+","+result.get(groupPosition).toString());
			}
		}).setNegativeButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				//记录点检结果
				switch (result.get(groupPosition).get(childPosition)) {
				case 0:
					bg_color.get(groupPosition).set(childPosition, Color.GREEN);
					break;
				case 1:
					bg_color.get(groupPosition).set(childPosition, Color.RED);
					break;
				case 2:
					bg_color.get(groupPosition).set(childPosition, Color.YELLOW);
					break;
				}
				adapter.setBg_color(bg_color);
				adapter.notifyDataSetChanged();
			}
		}).show();

		return false;
	}

	@Override
	public boolean onGroupClick(ExpandableListView parent, View v,
			int groupPosition, long id) {
		// TODO Auto-generated method stub

		return false;
	}



	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		Intent stopService = new Intent();
		stopService.setAction("org.whut.service.RFIDService");
		stopService.putExtra("stopflag", true);
		sendBroadcast(stopService);
		super.onDestroy();
	}


	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		unregisterReceiver(receiver);
		super.onPause();
	}


	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		//注册MyBroadcastReceiver
		receiver = new MyBroadcastReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("org.whut.platform.InspectActivity");
		registerReceiver(receiver, filter);
		super.onResume();
	}

	private SimpleAdapter getMenuAdapter(String[] menuNameArray,int[] imageResourceArray){
		ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; i < menuNameArray.length; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("itemImage", imageResourceArray[i]);
			map.put("itemText", menuNameArray[i]);
			data.add(map);
		}
		SimpleAdapter simperAdapter = new SimpleAdapter(this, data,
				R.layout.menu_item, new String[] { "itemImage", "itemText" },
				new int[] { R.id.item_image, R.id.item_text });
		return simperAdapter;
	}

	private void init(){
		result = new ArrayList<List<Integer>>();
		bg_color = new ArrayList<List<Integer>>();
		isInspected = new ArrayList<Boolean>();
		for(int i=0;i<childList.size();i++){
			List<Integer> list = new ArrayList<Integer>();
			List<Integer> list2 = new ArrayList<Integer>();
			for(int j=0;j<childList.get(i).size();j++){
				list.add(Color.GREEN);
				list2.add(0);
			}
			bg_color.add(list);
			result.add(list2);
		}
		for(int i=0;i<groupList.size();i++){
			isInspected.add(false);
		}
	}





	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		Builder alertDialog = new AlertDialog.Builder(InspectActivity.this);
		alertDialog.setTitle("提示").setMessage("是否放弃此次点检结果？").setNegativeButton("取消", null)
		.setPositiveButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				FileUtils.deleteFile(filePath);
				if(taskData!=null){
					Intent it = new Intent(InspectActivity.this,TaskActivity.class);
					it.putExtra("taskData", (Serializable)taskData);
					it.putExtra("locationData", locationData);
					startActivity(it);
					finish();
				}else{
					finish();
				}
			}
		}).show();
	}
	
	private String createFormatTime(String filePath){
		String inspectTime = filePath.split("-")[2].substring(0,14);
			
		String formatTime = inspectTime.substring(0,4)+"-"
				+inspectTime.substring(4,6)+"-"
				+inspectTime.substring(6,8)+" "
				+inspectTime.substring(8,10)+":"
				+inspectTime.substring(10,12)+":"
				+inspectTime.substring(12,14);
		Log.i("msg", formatTime);
		return formatTime;
	}





	private class MyBroadcastReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			Listable listable = intent.getParcelableExtra("listable");
			//扫描出的数据为空
			if(listable==null){
				Toast.makeText(InspectActivity.this, "读取标签卡数据为空，请重试！", Toast.LENGTH_SHORT).show();
				timerDialog.cancel();
				dialog.cancel();
				return;
			}else if(!(listable instanceof Tag)){ //读出来的数据为非设备数据
				Toast.makeText(InspectActivity.this, "标签类型错误，请读设备标签卡！", Toast.LENGTH_SHORT).show();
				timerDialog.cancel();
				dialog.cancel();
				return;
			}

			Tag tagData = (Tag) listable;


			String location = tagData.getTagArea();
			deviceNum = tagData.getDeviceNum();
			


			Log.i("msg", " -------扫描区域---->"+location);
			Log.i("msg", "-------------->DeviceNum="+deviceNum);
			
			//判断扫描出来的数据属于哪个区域，展开对应点检项
			for(int i=0;i<groupList.size();i++){
				if(groupList.get(i).equals(location)){
					isInspected.set(i, true);
					listView.expandGroup(i);
					//将deviceNum加入点检表
					try {
						XmlUtils.updateInspectTable(filePath,deviceNum);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					Toast.makeText(InspectActivity.this, "扫卡完成，可以开始点检...", Toast.LENGTH_SHORT).show();
					timerDialog.cancel();
					dialog.cancel();
					return;
				}
			}



		}

	}

}
