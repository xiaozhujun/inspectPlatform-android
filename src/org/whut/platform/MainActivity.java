package org.whut.platform;

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
import org.whut.database.entity.service.impl.TaskServiceDao;
import org.whut.database.entity.service.impl.UserServiceDao;
import org.whut.entity.Location;
import org.whut.inspectplatform.R;
import org.whut.service.InitDataService;
import org.whut.service.LocationService;
import org.whut.strings.UrlStrings;
import org.whut.utils.JsonUtils;
import org.whut.utils.XmlUtils;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKGeneralListener;
import com.readystatesoftware.viewbadger.BadgeView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
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

	public static BMapManager mapManager;
	public static Handler handler ;

	private ProgressDialog dialog;

	private Intent serviceIntent;
	private Intent locationIntent;

	private static UserServiceDao userDao;
	private static TaskServiceDao taskDao;
	private static HistoryServiceDao hisDao;

	private static int userId;
	private String userName;

	private static Location locationData;
	private static List<Task> taskData;
	private String city_info;
	private List<String> project_list;

	private TextView city;

	private int projectNum;
	private int taskNum;
	private static int hisNum;

	private GridView gridView;
	private BadgeView bv_project;
	private BadgeView bv_task;
	private BadgeView bv_history;

	private RelativeLayout account;


	int[] images = {R.drawable.img_project,R.drawable.img_task,R.drawable.img_history};
	String[] functions ={"点检项目","待做任务","点检记录"};





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

		mapManager = new BMapManager(getApplication());
		mapManager.init(new MKGeneralListener() {

			@Override
			public void onGetPermissionState(int arg0) {

				Toast.makeText(getApplicationContext(),
						"密钥错误", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onGetNetworkState(int arg0) {

				Toast.makeText(getApplicationContext(),

						"网络错误", Toast.LENGTH_SHORT).show();
			}
		});

		MyApplication.getInstance().addActivity(MainActivity.this);

		setContentView(R.layout.activity_main);

		serviceIntent = new Intent(MainActivity.this,InitDataService.class);
		locationIntent = new Intent(MainActivity.this,LocationService.class);

		userName = getIntent().getExtras().getString("userName");

		userDao = new UserServiceDao(this);
		taskDao = new TaskServiceDao(this);
		hisDao = new HistoryServiceDao(this);

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
							CasClient.getInstance().reset();
							final Intent it = getPackageManager().getLaunchIntentForPackage(getPackageName());
							it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							startActivity(it);
							break;
						case 1:
							MyApplication.getInstance().exit();
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
					userId = (Integer) params.get("id");
					//xml中的workernumber
					locationData.setUserId(userId);
					//xml中的worker
					locationData.setUserName((String)params.get("name"));
					if(!userDao.findUserById(userId)){
						userDao.addUser(params);
					}
					break;
				case 2://所有配置文件下载完成,再开启定位服务
					startService(locationIntent);
					break;
				case 3:
					locationData.setLat(((String)(msg.obj)).split(";")[0]);
					Log.i("msg", msg.obj.toString());
					locationData.setLng(((String)(msg.obj)).split(";")[1]);
					break;
				case 4:
					Toast.makeText(getApplicationContext(), "暂时无法获取地理位置信息！", Toast.LENGTH_SHORT).show();
					break;
				case 5:
					locationData.setAddress(msg.obj.toString().split(";")[0]);
					city_info = msg.obj.toString().split(";")[1];
					city.setText(city_info);
					new Thread(new SendLocationThread()).start();
					break;
				case 6:
					//发送位置信息成功
					new Thread(new HandleProjectThread()).start();
					stopService(serviceIntent);
					stopService(locationIntent);
					break;
				case 7://解析点检项完成
					project_list = (List<String>) msg.obj;
					new Thread(new HandleTaskThread()).start();
					break;
				case 8:
					List<Task> task_data = new ArrayList<Task>();
					task_data =  (List<Task>) msg.obj;
					for(int i=0;i<task_data.size();i++){
						//未找到才添加
						if(taskDao.findTask(task_data.get(i).getId())==0){
							//手动添加任务的本地状态为0（未完成）
							task_data.get(i).setLocalStatus(0);
							taskDao.addTask(task_data.get(i));
						}else{//若存在，则更新status状态
							taskDao.updateStatus(taskDao.findTask(task_data.get(i).getId()),task_data.get(i).getStatus());
						}
					}
					new Thread(new HandleHistoryThread()).start();
					break;

				case 9:
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
									Intent it = new Intent(MainActivity.this,InspectActivity.class);
									Bundle bundle = new Bundle();
									bundle.putSerializable("locationData", locationData);
									bundle.putString("tableName", project_list.get(0));
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
							}
						}
					});

					dialog.dismiss();
					break;
				}
			}
		};

		if(userDao.findUserByUserName(userName)==0){
			new Thread(new GetCurrentUserThread()).start();
		}else{
			userId = userDao.findUserByUserName(userName);
			locationData.setUserId(userId);
			locationData.setUserName(userName);
		}

		startService(serviceIntent);

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
		dialog.setCancelable(false);
		dialog.setIndeterminate(false);
		dialog.show();
	}


	class GetCurrentUserThread implements Runnable{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			String message = CasClient.getInstance().doGet(UrlStrings.GET_CURRENT_USER);
			Message msg = Message.obtain();
			try {
				msg.obj = JsonUtils.GetUserData(message);
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
					msg.what=6;
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
			String userRole = userDao.getRoleById(userId);
			List<String> list = new ArrayList<String>();
			try {
				list = XmlUtils.getTableByUserRole(userRole);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			projectNum = list.size();
			Message msg = Message.obtain();
			msg.what = 7;
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
			Log.i("msg", message);
			Message msg = Message.obtain();
			try {
				taskData = JsonUtils.GetTaskList(message);

				msg.obj = taskData;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			msg.what = 8;
			taskNum = taskData.size();
			handler.sendMessage(msg);
		}	
	}

	//查询点检历史
	public static class HandleHistoryThread implements Runnable{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			hisNum = hisDao.findHistory(userId);
			Message msg = Message.obtain();
			msg.what = 9;
			handler.sendMessage(msg);
		}

	}
}
