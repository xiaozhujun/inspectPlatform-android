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
import org.whut.client.CasClient;
import org.whut.database.entity.History;
import org.whut.database.entity.Task;
import org.whut.database.entity.service.impl.HistoryServiceDao;
import org.whut.database.entity.service.impl.InspectImageServiceDao;
import org.whut.database.entity.service.impl.TaskServiceDao;
import org.whut.database.entity.service.impl.UserServiceDao;
import org.whut.entity.Listable;
import org.whut.entity.Location;
import org.whut.entity.Tag;
import org.whut.inspectplatform.R;
import org.whut.service.LocationService;
import org.whut.service.RFIDService;
import org.whut.strings.FileStrings;
import org.whut.strings.UrlStrings;
import org.whut.utils.FileUtils;
import org.whut.utils.JsonUtils;
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
	private List<List<Integer>> itemIds;
	private List<List<String[]>> resultList;
	public static MyExpandableListAdapter adapter;

	private List<Boolean> isInspected;
	//用户是否达成点检条件允许保存
	private boolean allow_save = false;
	
	
	//提示框
	private ProgressDialog dialog;
	private Timer timerDialog;


	//TopBar
	private ImageView topbar_btn_back;
	private TextView topbar_title;
	private RelativeLayout topbar_menu;


	//BottomView
	private Button startScan;


	//Handler处理消息，更新UI
	private Handler handler;
	private MyBroadcastReceiver receiver;

	//从上一Activity中传来的数据
	private Location locationData;
	private List<Task> taskData;
	private int userId;
	private int inspectType;
	private Task task;
	private String tableName;
	
	//点检表名字，不带路径的
	private String inspectTableName;
	private String inspectTime;
	private String deviceNum;

	private String fileDir;
	private String filePath;
	

	private HistoryServiceDao dao;
	private InspectImageServiceDao imageDao;

	//菜单相关
	private View menuView;
	private GridView menuGrid;
	private AlertDialog menuDialog;

	/** 菜单图片 **/
	int[] menu_image_array = {R.drawable.menu_filemanager,R.drawable.menu_refresh,
			R.drawable.menu_help,R.drawable.menu_quit};
	/** 菜单文字 **/
	String[] menu_name_array = {"保存结果", "重置数据", "帮助", "退出" };


	private void startLocation(){
		Intent locationIntent  = new Intent(InspectActivity.this,LocationService.class);
		locationIntent.putExtra("activity", "org.whut.platform.InspectActivity");
		startService(locationIntent);
	}
	
	private void stopLocation(){
		Intent locationIntent = new Intent(InspectActivity.this,LocationService.class);
		locationIntent.putExtra("activity", "org.whut.platform.InspectActivity");
		stopService(locationIntent);
	}
	
	

	@SuppressLint("HandlerLeak")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_inspect);

		MyApplication.getInstance().addActivity(this);

		FileUtils.setInspectDir(FileStrings.inspectDir);

		//开始定位
		startLocation();
		
		initData();

		//发送位置信息
		new Thread(new SendLocationBeforeInspectThread()).start();

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
					switch(inspectType){
					case 1:
						break;
					case 2://至少一卡
						break;
					case 3://全部卡
						allow_save = true;
						for(int i=0;i<isInspected.size();i++){
							if(!isInspected.get(i)){
								allow_save = false;
							}
						}
						break;
					}
					
					
					if(allow_save){
						try {
							XmlUtils.saveInspectResult(adapter.getCommentList(),adapter.getResultList(), filePath);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						menuDialog.dismiss();
						Toast.makeText(InspectActivity.this, "点检结果已保存在<"+filePath+">中", Toast.LENGTH_SHORT).show();
						//开始更新定位数据
						History history = new History();
						history.setFilePath(filePath);
						history.setUserId(locationData.getUserId());
						history.setUserName(locationData.getUserName());
						history.setInspectTableName(inspectTableName);
						history.setUploadFlag(0);
						history.setInspectTime(inspectTime);
						dao.addHistory(history);
						//若为日常任务，则更新数据库localStatus为1（已完成）
						if(taskData!=null){
							TaskServiceDao dao = new TaskServiceDao(InspectActivity.this);
							dao.updateLocalStatus(task.getId());
						}
						//更新主界面历史数据
						new Thread(new MainActivity.UpdateBadageViewThread()).start();
						//点检完毕，发送位置信息
						new Thread(new SendLocationAfterInspectThread()).start();
					}else{
						Toast.makeText(InspectActivity.this, "仍有区域尚未点检，请点检后再点击保存！", Toast.LENGTH_SHORT).show();
						menuDialog.dismiss();
					}
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
							adapter =  new MyExpandableListAdapter(InspectActivity.this, groupList, childList,itemIds,resultList,inspectTableName,userId);
							listView.setAdapter(adapter);
							//处理图片
							deleteImages();
							try {
								XmlUtils.saveInspectResult(adapter.getCommentList(),adapter.getResultList(), filePath);
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							Toast.makeText(InspectActivity.this, "点检结果已重置，请重新开始点检！", Toast.LENGTH_SHORT).show();
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
							//处理图片
							deleteImages();
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
				case 1://发送位置信息成功
					Toast.makeText(InspectActivity.this, "服务器端已同步点检信息，请扫卡开启点检！", Toast.LENGTH_SHORT).show();
					break;
				case 2:
					break;
				case 3:
					Intent it2 = new Intent(InspectActivity.this,UploadActivity.class);
					it2.putExtra("locationData", locationData);
					it2.putExtra("filePath", filePath);
					it2.putExtra("tableName", tableName);
					it2.putExtra("inspectTime", inspectTime);
					it2.putExtra("inspectTableName", inspectTableName);
					Log.i("tableName", inspectTableName+"----------InspectActivity跳转至UploadActivity");
					startActivity(it2);
					finish();
					break;
				}
			}
		};

		adapter = new MyExpandableListAdapter(InspectActivity.this, groupList, childList,itemIds,resultList,inspectTableName,userId);
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
						deleteImages();
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

			imageDao = new InspectImageServiceDao(InspectActivity.this);
			
			locationData = (Location) getIntent().getExtras().getSerializable("locationData");
			
			if(getIntent().getExtras().getSerializable("taskData")!=null){
				taskData = (List<Task>) getIntent().getExtras().getSerializable("taskData");
				task = (Task) getIntent().getExtras().getSerializable("task");
			}

			tableName = getIntent().getExtras().getString("tableName");

			userId = locationData.getUserId();
			
			inspectType = new UserServiceDao(InspectActivity.this).findInspectTypeByUserId(userId);
			
			locationData.setInspectTableName(tableName);
			
			fileDir = FileStrings.BASE_PATH;

			filePath = getFilePath(tableName);

			inspectTime = createFormatTime(filePath);

			//创建本次点检文件
			XmlUtils.createFile(filePath,locationData.getUserId(),locationData.getUserName(),inspectTime);

			//初始化数据
			groupList = XmlUtils.getInspectLocation(filePath);
			childList = XmlUtils.getInspectField(filePath);
			itemIds = XmlUtils.getInspectItemId(filePath);
			resultList = XmlUtils.getInspectResultListFromOriginal(FileStrings.BASE_PATH+"/"+(tableName.split("-")[0])+".xml");
			Log.i("ExpandableListView", FileStrings.BASE_PATH+"/"+tableName+".xml");
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
		//该点检表的名字，非路径
		inspectTableName = newFileName;
		Log.i("tableName", inspectTableName+"----InspectActivity");
		String tempFile = fileDir+"/"+tableName+".xml";
		if(FileUtils.prepareInspectFile(tempFile,newFileName)){
			String inspectDir = FileUtils.getInspectDir();
			fileFullName = inspectDir+"/"+newFileName;
		}
		return fileFullName;
	}


	@Override
	public boolean onChildClick(ExpandableListView parent, View v,
			int groupPosition, int childPosition, long id) {
		// TODO Auto-generated method stub
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

		Intent locationIntent = new Intent();
		locationIntent.putExtra("activity", "org.whut.platform.InspectActivity");
		stopService(locationIntent);
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
		
		isInspected = new ArrayList<Boolean>();
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
				deleteImages();
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
	
	private void deleteImages(){
		//重置点检的图片
		List<String> images_inspect = imageDao.getInspectImages(userId,tableName);
		//删除图片
		FileUtils.deleteImages(images_inspect);
		//删除本地数据库记录
		imageDao.deleteInspectImages(tableName);
	}


	class SendLocationBeforeInspectThread implements Runnable{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			HashMap<String,Object> params = new HashMap<String,Object>();
			params.put("lng", locationData.getLng());
			params.put("address", locationData.getAddress());
			params.put("userId", locationData.getUserId());
			params.put("lat", locationData.getLat());
			params.put("image", locationData.getImage());
			params.put("userName",locationData.getUserName());
			params.put("inspectTableName", locationData.getInspectTableName());
			HashMap<String,Object> map = new HashMap<String,Object>();
			map.put("jsonString",JsonUtils.HashToJson(params));
			Log.i("msg", JsonUtils.HashToJson(params));
			String message = CasClient.getInstance().doPost(UrlStrings.SEND_LOCATION,map);
			Message msg = Message.obtain();
			try {
				if(JsonUtils.Validate(message)){
					msg.what=1;
					handler.sendMessage(msg);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	class SendLocationWhileInspectThread implements Runnable{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			HashMap<String,Object> params = new HashMap<String,Object>();
			params.put("lng", locationData.getLng());
			params.put("address", locationData.getAddress());
			params.put("userId", locationData.getUserId());
			params.put("lat", locationData.getLat());
			params.put("userName",locationData.getUserName());
			//加入扫卡之后的deviceNum
			params.put("deviceNum", deviceNum);
			params.put("inspectTableName", locationData.getInspectTableName());
			HashMap<String,Object> map = new HashMap<String,Object>();
			map.put("jsonString",JsonUtils.HashToJson(params));
			Log.i("msg", JsonUtils.HashToJson(params));
			String message = CasClient.getInstance().doPost(UrlStrings.SEND_LOCATION,map);
			Message msg = Message.obtain();
			try {
				if(JsonUtils.Validate(message)){
					msg.what=2;
					handler.sendMessage(msg);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

	class SendLocationAfterInspectThread implements Runnable{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			HashMap<String,Object> params = new HashMap<String,Object>();
			params.put("lng", locationData.getLng());
			params.put("address", locationData.getAddress());
			params.put("userId", locationData.getUserId());
			params.put("lat", locationData.getLat());
			HashMap<String,Object> map = new HashMap<String,Object>();
			map.put("jsonString",JsonUtils.HashToJson(params));
			Log.i("msg", JsonUtils.HashToJson(params));
			String message = CasClient.getInstance().doPost(UrlStrings.SEND_LOCATION,map);
			Message msg = Message.obtain();
			try {
				if(JsonUtils.Validate(message)){
					msg.what=3;
					handler.sendMessage(msg);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}





	private class MyBroadcastReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			Log.i("Debug", "InspectActivity-------onReceive");
			if(intent.getBooleanExtra("locationService",false)){
				Location location = (Location) intent.getSerializableExtra("locationData");
				//更新locationData数据
				Log.i("MyLocation", "正在更新Location数据：---->lat = "+location.getLat()+";"+"--->lng="+location.getLng());
				locationData.setAddress(location.getAddress());
				locationData.setLat(location.getLat());
				locationData.setLng(location.getLng());
				stopLocation();
				return;
			}else{
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

				String area = tagData.getTagArea();
				deviceNum = tagData.getDeviceNum();
				

				Log.i("msg", " -------扫描区域---->"+area);
				Log.i("msg", "-------------->DeviceNum="+deviceNum);

				//判断扫描出来的数据属于哪个区域，展开对应点检项
				for(int i=0;i<groupList.size();i++){
					if(groupList.get(i).equals(area)){
						isInspected.set(i, true);
						Log.i("msg", "i"+":"+isInspected.get(i));
						allow_save = true;
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
						new Thread(new SendLocationWhileInspectThread()).start();
						return;
					}
				}
			}

		}

	}

}
