package org.whut.platform;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.whut.application.MyApplication;
import org.whut.client.CasClient;
import org.whut.database.entity.Task;
import org.whut.database.entity.service.impl.HistoryServiceDao;
import org.whut.database.entity.service.impl.InspectImageServiceDao;
import org.whut.database.entity.service.impl.TaskServiceDao;
import org.whut.database.entity.service.impl.UserRoleServiceDao;
import org.whut.database.entity.service.impl.UserServiceDao;
import org.whut.entity.Location;
import org.whut.inspectplatform.R;
import org.whut.service.InitDataService;
import org.whut.service.LocationService;
import org.whut.strings.UrlStrings;
import org.whut.utils.JsonUtils;
import org.whut.utils.XmlUtils;

import com.readystatesoftware.viewbadger.BadgeView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity{

	public static Handler handler ;

	private ProgressDialog dialog;
	private ProgressDialog updateDialog;

	private Intent serviceIntent;
	private Intent locationIntent;

	private MyBroadcastReciever reciever;

	private static UserServiceDao userDao;
	private static TaskServiceDao taskDao;
	private static HistoryServiceDao hisDao;
	private static InspectImageServiceDao imgDao;
	private static UserRoleServiceDao roleDao;

	private static int userId;
	private String userName;
	private String image;

	private static Location locationData;
	private static List<Task> taskData;
	private String city_info;
	private List<String> project_list;

	private List<HashMap<String,String>> list_roles;
	
	private TextView city;

	private static int projectNum;
	private static int taskNum;
	private static int hisNum;
	private static int imgNum;
	private static int versionNum;


	private static int versionCodeOnline;
	private static int versionCodeLocal;
	private GridView gridView;
	private BadgeView bv_project;
	private BadgeView bv_task;
	private BadgeView bv_history;
	private BadgeView bv_image;
	private BadgeView bv_version;

	private RelativeLayout account;
	
	private int inspectType;


	int[] images = {R.drawable.img_project,R.drawable.img_task,R.drawable.img_history,R.drawable.img_upload,R.drawable.img_update};
	String[] functions ={"点检项目","待做任务","点检记录","图片上传","版本信息"};


	/** 
	 * 菜单、返回键响应 
	 */  
	@Override  
	public boolean onKeyDown(int keyCode, KeyEvent event) {  
		if(keyCode == KeyEvent.KEYCODE_BACK)  {   
			exitBy2Click();
		}  
		return false;  
	}  
	/** 
	 * 双击退出函数 
	 */  
	private static Boolean isExit = false;  

	private void exitBy2Click() {  
		Timer tExit = null;  
		if (isExit == false) {  
			isExit = true; // 准备退出  
			Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();  
			tExit = new Timer();  
			tExit.schedule(new TimerTask() {  
				@Override  
				public void run() {  
					isExit = false; // 取消退出  
				}  
			}, 2000); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务  

		} else {  
			MyApplication.getInstance().exit();
		}  
	}


	@SuppressLint("HandlerLeak")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		MyApplication.getInstance().addActivity(this);
		setContentView(R.layout.activity_main);

		serviceIntent = new Intent(MainActivity.this,InitDataService.class);

		locationIntent = new Intent(MainActivity.this,LocationService.class);
		locationIntent.putExtra("activity", "org.whut.platform.MainActivity");

		//zhaowei
		userName = getIntent().getExtras().getString("userName");

		//扫卡方式配置：
		if(userName=="zhaowei"){//至少一卡
			inspectType = 2;
		}else if(userName=="manager"){//全部卡
			inspectType = 3;
		}
		
		userDao = new UserServiceDao(this);
		taskDao = new TaskServiceDao(this);
		hisDao = new HistoryServiceDao(this);
		imgDao = new InspectImageServiceDao(this);
		roleDao = new UserRoleServiceDao(this);

		locationData = new Location();
		taskData = new ArrayList<Task>();
		initDialog();

		city = (TextView) findViewById(R.id.iv_topbar_location);
		gridView = (GridView) findViewById(R.id.function_choose);
		account = (RelativeLayout) findViewById(R.id.tv_topbar_right_map_layout);

		account.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
				alertDialog.setTitle(userName).setItems(new String[]{"切换账户","退出"}, new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						switch(which){
						case 0:
							Builder alertDialog_switch = new AlertDialog.Builder(MainActivity.this);
							alertDialog_switch.setTitle("提示").setMessage("是否切换账户？").setPositiveButton("确定", new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog, int which) {
									// TODO Auto-generated method stub
									CasClient.getInstance().reset();
									final Intent it = getPackageManager().getLaunchIntentForPackage(getPackageName());
									it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
									startActivity(it);
								}
							}).setNegativeButton("取消", null).show();
							break;
						case 1:
							Builder alertDialog_exit = new AlertDialog.Builder(MainActivity.this);
							alertDialog_exit.setTitle("提示").setMessage("是否退出？").setPositiveButton("确定", new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog, int which) {
									// TODO Auto-generated method stub
									MyApplication.getInstance().exit();
								}
							}).setNegativeButton("取消", null).show();
							break;
						}
					}

				}).show();
			}
		});


		handler = new Handler(){

			@SuppressWarnings("unchecked")
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				switch(msg.what){
				case 0://未获取到登录的User信息，登录异常，重新登录
					Toast.makeText(getApplicationContext(), "登录异常，请重新登录！", Toast.LENGTH_SHORT).show();
					Intent it = new Intent(MainActivity.this,LoginActivity.class);
					startActivity(it);
					finish();
					break;
				case 1://获取到登录的User信息，取得用户的Id，存入本地SQLite中保存
					HashMap<String,Object> params = (HashMap<String, Object>) msg.obj;
					params.put("inspectType", inspectType);
					userId = (Integer) params.get("id");
					image = (String)params.get("image");
					//xml中的workernumber
					locationData.setUserId(userId);
					//xml中的worker
					locationData.setUserName((String) params.get("userName"));
					locationData.setImage(image);
					if(!userDao.findUserById(userId)){
						userDao.addUser(params);
						for(int i=0;i<list_roles.size();i++){
							roleDao.addUserRole(userId, list_roles.get(i).get("name"));
						}
					}
					break;
				case 2://所有配置文件下载完成,再开启定位服务
					//停止数据初始化服务
					stopService(serviceIntent);
					//开始定位服务
					startService(locationIntent);
					break;
				case 3://定位数据获取完毕，发送到服务器端
					city.setText(city_info);
					new Thread(new SendLocationThread()).start();
					break;
				case 4://发送位置信息成功
					stopService(locationIntent);
					new Thread(new HandleProjectThread()).start();
					//停止定位服务
					break;
				case 5://解析点检项完成
					project_list = (List<String>) msg.obj;
					new Thread(new HandleTaskThread()).start();
					break;
				case 6:
					List<Task> task_data = new ArrayList<Task>();
					task_data =  (List<Task>) msg.obj;
					for(int i=0;i<task_data.size();i++){
						//未找到才添加
						if(taskDao.findTask(task_data.get(i).getId())==0){
							//手动添加任务的本地状态为0（未完成）
							task_data.get(i).setLocalStatus(0);
							taskDao.addTask(task_data.get(i));
						}
					}
					new Thread(new GetLatestVersionThread()).start();
					break;
				case 7:
					if(versionCodeLocal<versionCodeOnline){
						Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
						alertDialog.setTitle("更新提示").setMessage("检测到有新版本，是否更新？").setPositiveButton("确定", new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								new Thread(new GetUpdateFileThread()).start();
								updateDialog.show();
							}
						}).setNegativeButton("取消", null).show();
					}
					new Thread(new UpdateBadageViewThread()).start();
					break;
				case 8:
					gridView.setAdapter(getGridAdapter(functions,images));
					gridView.setOnItemClickListener(new OnItemClickListener() {
						@Override
						public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
								long arg3) {
							// TODO Auto-generated method stub
							switch(arg2){
							case 0:
								if(projectNum==0){
									Toast.makeText(MainActivity.this, "暂无可点检项，请确认！", Toast.LENGTH_SHORT).show();
								}else{
									Intent it = new Intent(MainActivity.this,ProjectActivity.class);
									Bundle bundle = new Bundle();
									bundle.putSerializable("locationData", locationData);
									bundle.putStringArrayList("project_list", (ArrayList<String>) project_list);
									it.putExtras(bundle);
									startActivity(it);
								}
								break;
							case 1:
								Intent it2 = new Intent(MainActivity.this,TaskActivity.class);
								it2.putExtra("taskData", (Serializable)taskData);
								it2.putExtra("locationData", locationData);
								startActivity(it2);
								break;
							case 2:
								Intent it3 = new Intent(MainActivity.this,HistoryActivity.class);
								it3.putExtra("locationData", locationData);
								startActivity(it3);
								break;
							case 3:
								Intent it4 = new Intent(MainActivity.this,ImageUploadActivity.class);
								it4.putExtra("userId", userId);
								startActivity(it4);
								break;
							case 4:
								Intent it5 = new Intent(MainActivity.this,UpdateActivity.class);
								//it5.putExtra("versionCode",versionCode);
								startActivity(it5);
								break;
							}
						}
					});
					dialog.dismiss();
					break;
					
				case 9:
					if(msg.arg1!=100){
						updateDialog.setProgress(msg.arg1);
					}else{
						updateDialog.dismiss();
						Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
						alertDialog.setTitle("提示").setMessage("下载完成！是否现在更新？").setPositiveButton("确定", new OnClickListener() {
							
							@SuppressLint("SdCardPath")
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								Intent it = new Intent();
								it.setDataAndType(Uri.fromFile(new File("/sdcard/inspect/update/InspectPlatform.apk")), "application/vnd.android.package-archive");
								startActivity(it);
							}
						}).setNegativeButton("取消", null).show();
					
					}
					break;
				}
			}
		};

		if(userDao.findUserByUserName(userName)==0){
			//未添加的用户，则开启线程获取用户信息
			new Thread(new GetCurrentUserThread()).start();
		}else{
			//已添加的用户，则从本地数据库中取数据
			//通过登录界面传来的用户名：zhaowei，查找数据
			userId = userDao.findUserByUserName(userName);
			
			
			locationData.setUserId(userId);
			//点检人员姓名，非用户名
			String name = userDao.findNameByUserName(userName);
			locationData.setUserName(name);
			image = userDao.findImageByUserName(userName);
			locationData.setImage(image);
		}

		//开始数据初始化服务
		startService(serviceIntent);

	}



	protected int checkVersion() {
		// TODO Auto-generated method stub
		PackageManager manager;
		PackageInfo info;
		manager = this.getPackageManager();
		try {
			info = manager.getPackageInfo(this.getPackageName(), 0);
			return info.versionCode;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return 0;
	}


	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		stopService(locationIntent);
		stopService(serviceIntent);
		super.onDestroy();
	}


	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		//注册广播接受者
		reciever = new MyBroadcastReciever();
		IntentFilter filter = new IntentFilter();
		filter.addAction("org.whut.platform.MainActivity");
		registerReceiver(reciever, filter);
		super.onResume();
	}


	private ListAdapter getGridAdapter(String[] functions, int[] images) {
		ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; i < functions.length; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("itemImage", images[i]);
			map.put("itemText", functions[i]);
			data.add(map);
		}
		SimpleAdapter simperAdapter = new SimpleAdapter(this, data,
				R.layout.grid_item_main, new String[] { "itemImage", "itemText" },
				new int[] { R.id.iv_icon, R.id.tv_function }){
			@Override
			public View getView(int position, View convertView,
					ViewGroup parent) {
				// TODO Auto-generated method stub
				View view = super.getView(position, convertView, parent);

				switch(position){
				case 0:
					bv_project = new BadgeView(MainActivity.this, (ImageView) view.findViewById(R.id.iv_icon));
					bv_project.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
					bv_project.setText(projectNum+"");
					bv_project.show();
					break;
				case 1:
					bv_task = new BadgeView(MainActivity.this, (ImageView)view.findViewById(R.id.iv_icon));
					bv_task.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
					bv_task.setText(taskNum+"");
					bv_task.show();
					break;
				case 2:
					bv_history = new BadgeView(MainActivity.this, (ImageView)view.findViewById(R.id.iv_icon));
					bv_history.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
					bv_history.setText(hisNum+"");
					bv_history.show();
					break;
				case 3:
					bv_image = new BadgeView(MainActivity.this,(ImageView)view.findViewById(R.id.iv_icon));
					bv_image.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
					bv_image.setText(imgNum+"");
					bv_image.show();
					break;
				case 4:
					bv_version = new BadgeView(MainActivity.this,(ImageView)view.findViewById(R.id.iv_icon));
					bv_version.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
					bv_version.setText(versionNum+"");
					bv_version.show();
					break;
				}	
				return view;
			}
		};
		return simperAdapter;

	}

	private void initDialog(){
		dialog = new ProgressDialog(MainActivity.this);
		dialog.setTitle("提示");
		dialog.setMessage("正在准备数据，请稍后...");
		dialog.setCancelable(true);
		dialog.setIndeterminate(false);
		dialog.show();
		
		updateDialog = new ProgressDialog(MainActivity.this);
		updateDialog.setTitle("正在下载..");
		updateDialog.setCancelable(true);
		updateDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
	}


	class GetCurrentUserThread implements Runnable{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			String message = CasClient.getInstance().doGet(UrlStrings.GET_CURRENT_USER);
			Log.i("msg", "getCurrentUser"+message);
			Message msg = Message.obtain();
			try {
				msg.obj = JsonUtils.GetUserData(message);
				list_roles = JsonUtils.GetUserRoleData(message);
				if(msg.obj==null){
					msg.what = 0;
				}else{
					msg.what = 1;
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			MainActivity.handler.sendMessage(msg);
		}

	}

	class SendLocationThread implements Runnable{

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
					msg.what=4;
					handler.sendMessage(msg);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	class HandleProjectThread implements Runnable{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			List<String> userRole = roleDao.getRoleById(userId);
			Log.i("msg", userRole.toString());
			List<String> list = new ArrayList<String>();
			try {
				list = XmlUtils.getTableByUserRole(userRole);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			projectNum = list.size();
			Message msg = Message.obtain();
			msg.what = 5;
			msg.obj = list;
			handler.sendMessage(msg);

		}	
	}

	//查询点检任务
	class HandleTaskThread implements Runnable{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			String message = CasClient.getInstance().doGet(UrlStrings.GET_TASK_LIST);
			Log.i("msg", "任务"+message);
			Message msg = Message.obtain();
			try {
				taskData = JsonUtils.GetTaskList(message);

				msg.obj = taskData;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			msg.what = 6;
			handler.sendMessage(msg);
		}	
	}

	public static class UpdateBadageViewThread implements Runnable{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			//更新任务数量
			taskNum = taskDao.queryUnfinishedTask(userId);
			//更新记录数量
			hisNum = hisDao.findHistory(userId);
			//更新图片数量
			imgNum = imgDao.queryUnuploadedImage(userId);
			//更新版本信息
			if(versionCodeLocal<versionCodeOnline){
				versionNum = 1;
			}else{
				versionNum = 0;
			}
			Message msg = Message.obtain();
			msg.what = 8;
			handler.sendMessage(msg);
		}

	}

	class GetLatestVersionThread implements Runnable{

		@Override
		public void run() {
			// TODO Auto-generated method stub
//			String message  = CasClient.getInstance().doPostNoParams(UrlStrings.UPDATE_VERSION);
			String message = "{\"message\":\"操作成功！\",\"versionCode\":1}";
			Log.i("msg", message);
			Message msg = Message.obtain();
			try {
				versionCodeLocal = checkVersion();
				versionCodeOnline = JsonUtils.getLatestVersion(message);
				msg.what = 7;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			handler.sendMessage(msg);
		}
	}
	
	class GetUpdateFileThread implements Runnable{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			CasClient.getInstance().doGetUpdateFile("http://59.69.75.201:8080/inspectManagement/InspectPlatform.apk");
		}
		
	}

	class MyBroadcastReciever extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			//获取定位数据（lat lng address,存入locationData中
			Log.i("Debug", "MainActivity--------onReceive");

			Location location = (Location) intent.getSerializableExtra("locationData");
			locationData.setAddress(location.getAddress());
			locationData.setLat(location.getLat());
			locationData.setLng(location.getLng());
			//获取城市
			city_info = intent.getStringExtra("city");
			//通知主线程获取完毕
			Message msg = Message.obtain();
			msg.what = 3;
			handler.sendMessage(msg);
		}	
	}
}
