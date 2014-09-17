package org.whut.platform;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.apache.http.client.ClientProtocolException;
import org.whut.adapter.MyImageListAdapter;
import org.whut.client.CasClient;
import org.whut.database.entity.service.impl.InspectImageServiceDao;
import org.whut.inspectplatform.R;
import org.whut.strings.UrlStrings;
import org.whut.utils.BitmapUtils;
import org.whut.utils.JsonUtils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class ImageUploadActivity extends Activity{

	private static List<String> paths;
	private List<Bitmap> images;
	private ListView listView;
	private ImageView left_back;
	private RelativeLayout take_photo;
	private static MyImageListAdapter adapter;
	private static List<Map<String,String>> list;
	private static InspectImageServiceDao dao;

	
	private static Handler handler;

	private boolean no_photo = false;
	
	private static int flag; 
	
	private int userId;

	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		new Thread(new MainActivity.UpdateBadageViewThread()).start();
		finish();
	}
	
	@SuppressLint("HandlerLeak")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_imageupload);
		
		
		dao = new InspectImageServiceDao(ImageUploadActivity.this);
		userId = getIntent().getIntExtra("userId", 0);
		Log.i("database", userId+","+"--------ImageUploadActivity");
		
		listView = (ListView) findViewById(R.id.listView_image);
		left_back = (ImageView) findViewById(R.id.iv_topbar_left_back);
		take_photo = (RelativeLayout) findViewById(R.id.tv_topbar_right_map_layout);
		
		handler = new Handler(){

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				switch(msg.what){
				
				case 0://上传成功
					Toast.makeText(ImageUploadActivity.this, "图片上传成功！", Toast.LENGTH_SHORT).show();				
					adapter.notifyDataSetChanged();
					break;
				case 1://上传失败
					Toast.makeText(ImageUploadActivity.this, "图片上传失败，请稍后再试！", Toast.LENGTH_SHORT).show();
					adapter.notifyDataSetChanged();
					break;
				case 2://
					Toast.makeText(ImageUploadActivity.this, "请先在点检历史中上传点检表！", Toast.LENGTH_SHORT).show();
					break;
				}
			}
			
		};
		
		left_back.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new Thread(new MainActivity.UpdateBadageViewThread()).start();
				finish();
			}
		});
		
		take_photo.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
	
		
		init();


	}

	private void init(){
		
		paths = new ArrayList<String>();
		paths = dao.getInspectImagesByUserId(userId);	
		Log.i("database", paths.toString());
		if(paths.size()==0){
			no_photo = true;
		}
		
		if(no_photo){
			//没有图片记录
			listView.setVisibility(View.GONE);
			Toast.makeText(ImageUploadActivity.this, "暂无图片可上传！", Toast.LENGTH_SHORT).show();
		}else{
			//获取图片数据
			getImages();

			dao = new InspectImageServiceDao(ImageUploadActivity.this);


			list = new ArrayList<Map<String,String>>();
			for(int i=0;i<paths.size();i++){
				list.add(dao.getImageInfo(paths.get(i)));
			}
			
			adapter = new MyImageListAdapter(images, ImageUploadActivity.this, list);
			listView.setAdapter(adapter);

		}
	}

	private void getImages(){
		images = new ArrayList<Bitmap>();
		if(!paths.isEmpty()){
			for(int i=0;i<paths.size();i++){
				Bitmap bitmap = BitmapUtils.changeBitmap(paths.get(i));
				images.add(bitmap);
			}
		}
	}
	
	public static class UploadImageThread implements Runnable{

		int i;
		
		public UploadImageThread(int i) {
			// TODO Auto-generated constructor stub
			this.i = i;
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
				try {
					
					list = (List<Map<String, String>>) adapter.getList();
					HashMap<String, String> params = new HashMap<String, String>();
					if(dao.validateInspectImage(list.get(i).get("filePath"))!=0){
						params.put("itemId", dao.getItemId(list.get(i).get("filePath"))+"");
						params.put("itemRecordId", dao.getItemRecordId(dao.getItemId(list.get(i).get("filePath"))+""));
						params.put("tableRecordId", dao.getTableRecordId(dao.getItemId(list.get(i).get("filePath"))+""));
					}else{
						Message msg_1 = Message.obtain();
						msg_1.what = 2;
						handler.sendMessage(msg_1);
						return;
					}
					
					
					String message =  CasClient.getInstance().uploadImage(UrlStrings.UPLOAD_IMAGE_FILE, paths.get(i), params);
					Log.i("msg", message);
					flag = JsonUtils.getInfo(message);
					Message msg = Message.obtain();
					if(flag==200){
						msg.what = 0;
						//更新数据库
						msg.arg1 = i;
						dao.updateUploadFlag(paths.get(i));
						list.get(i).put("uploadFlag", 1+"");
						adapter.setList(list);
					}else{
						msg.what = 1;
					}
					Log.i("msg", flag+"");
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
