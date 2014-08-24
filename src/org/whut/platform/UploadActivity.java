package org.whut.platform;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;
import org.whut.application.MyApplication;
import org.whut.client.CasClient;
import org.whut.database.entity.service.impl.HistoryServiceDao;
import org.whut.database.entity.service.impl.InspectImageServiceDao;
import org.whut.entity.Location;
import org.whut.inspectplatform.R;
import org.whut.strings.UrlStrings;
import org.whut.utils.JsonUtils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class UploadActivity extends Activity{

	private String userName;
	private String tableName;
	private String inspectTime;
	private String filePath;
	
	private String inspectTableName;
	
	public static Handler handler;
	private Builder AlertDialog;
	private ProgressDialog ProcessDialog;

	private Location locationData;
	
	private HistoryServiceDao dao;
	private InspectImageServiceDao imageDao;

	//保存点检中图片的地址，用于上传
	private List<Map<String,String>> list;
	private List<Map<String,String>> info;
	
	@SuppressLint("HandlerLeak")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_upload);

		MyApplication.getInstance().addActivity(this);
		
		locationData = (Location) getIntent().getSerializableExtra("locationData");

		userName = locationData.getUserName();
		inspectTime=getIntent().getStringExtra("inspectTime");
		filePath=getIntent().getStringExtra("filePath");
		inspectTableName = getIntent().getStringExtra("inspectTableName");

		dao = new HistoryServiceDao(UploadActivity.this);
		imageDao = new InspectImageServiceDao(UploadActivity.this);
		
		initDialog();

		((TextView)findViewById(R.id.userName_upload)).setText(userName);
		((TextView)findViewById(R.id.tableName_upload)).setText(tableName);
		((TextView)findViewById(R.id.inspectTime_upload)).setText(inspectTime);
		((TextView)findViewById(R.id.filePath_upload)).setText(filePath);
		((ImageView)findViewById(R.id.iv_topbar_left_back)).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		handler=new Handler(){
			/**
			 * what=0 文件发送成功
			 * what=1 文件发送失败
			 * what=2文件进度信息 
			 */
			@Override
			public void handleMessage(Message msg) {

				super.handleMessage(msg);
				switch (msg.what) {
				case 0:
					ProcessDialog.dismiss();
					//更新历史表中上传状态
					dao.updateUploadFlag(filePath);
					
					//检测该点检项是否有图片
					if(list.size()>0){
						for(int i=0;i<list.size();i++){
							for(int j=0;j<info.size();j++){
								if(Integer.parseInt(list.get(i).get("itemId"))==Integer.parseInt(info.get(j).get("itemId"))){
									//更新数据库
									String temp = null;
									for(int k=0;k<list.size();k++){
										temp = list.get(k).get("filePath");
										Log.i("database", inspectTableName+";"+temp+";"+Integer.parseInt(list.get(i).get("itemId"))+";"
												+Integer.parseInt(info.get(j).get("tableRecordId"))+";"+Integer.parseInt(info.get(j).get("itemRecordId")));
										imageDao.updateInspectImage(inspectTableName,temp,Integer.parseInt(list.get(i).get("itemId")), 
												Integer.parseInt(info.get(j).get("tableRecordId")), Integer.parseInt(info.get(j).get("itemRecordId")));
									}
									
																		
								}
							}
						}
						
						AlertDialog.setTitle("提示").setMessage("点检表上传成功，是否上传点检图片？")
						.setPositiveButton("确定", new DialogInterface.OnClickListener(){

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								new Thread(new UploadImages(0)).start();
							}
							
						}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								finish();
							}
						}).show();
					}else{
						
						AlertDialog.setTitle("提示").setMessage("点检表上传成功！").setPositiveButton("确定", new DialogInterface.OnClickListener(){

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								finish();
							}
							
						}).show();
					}
					break;
				case 1:
					ProcessDialog.dismiss();
					AlertDialog.setTitle("提示")
					.setMessage("点检表上传失败，请手动上传或在点检记录里再次上传！")
					.show();
					break;
				case 2:
					ProcessDialog.setProgress(msg.arg1);
					break;
				case 3://继续上传点检图片
					//更新数据库uploadFlag
					imageDao.updateUploadFlag(list.get(msg.arg1).get("filePath"));
					if(msg.arg1==list.size()-1){
						Toast.makeText(UploadActivity.this, "图片上传完成！", Toast.LENGTH_SHORT).show();
						finish();
					}else{
						new Thread(new UploadImages(msg.arg1+1)).start();
					}
					break;
				case 4://图片上传失败
					Toast.makeText(UploadActivity.this, "部分点检图片上传失败，请在图片上传界面再次上传或手动上传！", Toast.LENGTH_SHORT).show();
					finish();
					break;
				}
			};
		};

		((RelativeLayout)findViewById(R.id.upload_now)).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				new Thread(new UploadFileThread()).start();
				ProcessDialog.show();
			}
		});
		
		((RelativeLayout)findViewById(R.id.upload_later)).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});

	}

	private void initDialog(){
		//上传对话框
		ProcessDialog = new ProgressDialog(UploadActivity.this);
		ProcessDialog.setTitle("提示");
		ProcessDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		ProcessDialog.setMessage("正在上传文件，请稍后...");
		ProcessDialog.setCancelable(false);
		//提示对话框
		AlertDialog=new Builder(UploadActivity.this);
		AlertDialog.setTitle("提示")
		.setPositiveButton("确定", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				AlertDialog.create().dismiss();
				finish();
			}
		});

	}
	

	class UploadFileThread implements Runnable{

		@Override
		public void run() {
			Message msg=Message.obtain();
			try {	
				String message = CasClient.getInstance().doSendFile2(UrlStrings.UPLOAD_FILE, filePath);
				Log.i("msg", "上传结果："+message);
				if (JsonUtils.UploadIsSuccess(message)) {
					msg.what=0;
					//先根据表名找到图片
					list = imageDao.getInspectImagesInfo(inspectTableName);
					//解析服务器返回信息
					info = JsonUtils.getReturnInfo(message);
				}
				else {
					msg.what=1;
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				msg.what = 1;
			}
			handler.sendMessage(msg);
		}
	}
	
	class UploadImages implements Runnable{

		int i;
		
		
		
		public UploadImages(int i) {
			// TODO Auto-generated constructor stub
			this.i = i;
		}



		@Override
		public void run() {
			// TODO Auto-generated method stub
			Message msg = Message.obtain();
			HashMap<String,String> params = new HashMap<String, String>();
			Log.i("msg", list.get(i).get("itemId")+";");
			params.put("itemId", list.get(i).get("itemId"));
			
			Log.i("database", "i="+i+";"+imageDao.getTableRecordId(list.get(i).get("itemId")));
			Log.i("database", "i="+i+";"+imageDao.getItemRecordId(list.get(i).get("itemId")));
			
			params.put("tableRecordId", imageDao.getTableRecordId(list.get(i).get("itemId")));
			
			
			params.put("itemRecordId", imageDao.getItemRecordId(list.get(i).get("itemId")));
			
			try {
				String message = CasClient.getInstance().uploadImage(UrlStrings.UPLOAD_IMAGE_FILE, list.get(i).get("filePath"), params);
				if(JsonUtils.getInfo(message)==200){
					msg.what = 3;
					msg.arg1 = i;
				}else{
					msg.what = 4;
					msg.arg1 = i;
				}
				handler.sendMessage(msg);
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
	}

}
