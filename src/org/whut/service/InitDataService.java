package org.whut.service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.whut.client.CasClient;
import org.whut.platform.MainActivity;
import org.whut.strings.FileStrings;
import org.whut.strings.UrlStrings;
import org.whut.utils.FileUtils;
import org.whut.utils.JsonUtils;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;


/***
 * 
 * @author Yone
 *
 *	获取登录用户信息、创建文件夹、下载RoleTables.xml和点检表
 */
public class InitDataService extends Service{

	
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		Log.i("MyService", "------onCreate()方法调用");
		
		super.onCreate();
	}

	
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		Log.i("MyService", "------onStartCommand()方法调用");
		
		new Thread(new GetConfigFiles()).start();
		
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		Log.i("MyService", "------onDestroy()方法调用");
		super.onDestroy();
	}
	

	
	class GetConfigFiles implements Runnable{
		
		private List<HashMap<String,Object>> fileList = new ArrayList<HashMap<String,Object>>();
		private InputStream inputStream;
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			Message msg = Message.obtain();
			String message = CasClient.getInstance().doPostNoParams(UrlStrings.GET_CONFIG_FILE_LIST);
			try {
				fileList = JsonUtils.GetFileList(message);
				
				//下载RoleTables.xml
				inputStream = CasClient.getInstance().DoGetFile(UrlStrings.GET_ROLE_TABLES);
				FileUtils.SaveCofigFiles(inputStream, FileStrings.ROLE_TABLES, FileStrings.BASE_PATH);
	
				//下载点检表
				for(HashMap<String,Object> map : fileList){
					inputStream = CasClient.getInstance().DoGetFile(UrlStrings.GET_INSPECT_TABLES+"/"+map.get("id"));
					FileUtils.SaveCofigFiles(inputStream, map.get("name")+FileStrings.BASE_SUFFIX, FileStrings.BASE_PATH);
				}
				//下载完成
				msg.what = 2;
				MainActivity.handler.sendMessage(msg);
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

}
