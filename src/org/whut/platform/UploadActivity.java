package org.whut.platform;


import org.whut.application.MyApplication;
import org.whut.client.CasClient;
import org.whut.database.entity.service.impl.HistoryServiceDao;
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

public class UploadActivity extends Activity{

	private String userName;
	private String tableName;
	private String inspectTime;
	private String filePath;
	public static Handler handler;
	private Builder AlertDialog;
	private ProgressDialog ProcessDialog;

	private Location locationData;
	
	private HistoryServiceDao dao;

	@SuppressLint("HandlerLeak")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_upload);

		MyApplication.getInstance().addActivity(this);
		
		locationData = (Location) getIntent().getSerializableExtra("locationData");

		userName = locationData.getUserName();
		tableName=getIntent().getStringExtra("tableName");
		inspectTime=getIntent().getStringExtra("inspectTime");
		filePath=getIntent().getStringExtra("filePath");

		dao = new HistoryServiceDao(UploadActivity.this);
		
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
					dao.updateUploadFlag(filePath);
					AlertDialog.setTitle("提示")
					.setMessage("文件上传成功!")
					.show();
					break;
				case 1:
					ProcessDialog.dismiss();
					AlertDialog.setTitle("提示")
					.setMessage("文件发送失败，请手动或在点检记录里再次上传！")
					.show();
					break;
				case 2:
					ProcessDialog.setProgress(msg.arg1);
					break;
				default:
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
				String result = CasClient.getInstance().doSendFile2(UrlStrings.UPLOAD_FILE, filePath);
				Log.i("msg", "上传结果："+result);
				if (JsonUtils.UploadIsSuccess(result)) {
					msg.what=0;
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

}
