package org.whut.platform;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.whut.application.MyApplication;
import org.whut.database.entity.service.impl.InspectImageServiceDao;
import org.whut.inspectplatform.R;
import org.whut.strings.FileStrings;
import org.whut.utils.BitmapUtils;
import org.whut.utils.FileUtils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

@SuppressLint("SimpleDateFormat")
public class PictureActivity extends Activity{

	private ImageView imageView;
	private Button btn_save;
	private Button btn_cancel;

	private LinearLayout buttonGroup;

	private String filePath;
	private String itemName;
	private String inspectTableName;
	private int itemId;
	private int userId;
	
	private File myPhoto;
	
	private InspectImageServiceDao dao;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_picture);

		MyApplication.getInstance().addActivity(this);
		
		imageView = (ImageView) findViewById(R.id.image_show);
		btn_save = (Button) findViewById(R.id.btn_save);
		btn_cancel = (Button) findViewById(R.id.btn_cancel);
		buttonGroup = (LinearLayout) findViewById(R.id.buttonGroup);

		//设置图片保存地址
		FileUtils.prepareImageDir(FileStrings.IMAGE_PATH);
		
		Intent it = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		itemName = getIntent().getStringExtra("itemName");

		itemId = getIntent().getIntExtra("itemId",0);
		
		userId = getIntent().getIntExtra("userId", 0);
		
		inspectTableName = getIntent().getStringExtra("inspectTableName");
		
		Log.i("tableName", inspectTableName+"----------PictureActivity");
		
		filePath = FileStrings.IMAGE_PATH+getFileName();
		
		dao = new InspectImageServiceDao(PictureActivity.this);

		Log.i("Debug",filePath);

		myPhoto = new File(filePath);
		
		if(!myPhoto.exists()){
			try {
				myPhoto.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		it.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(myPhoto));
		startActivityForResult(it, 1);
		
		
}

	
	private String getFileName(){
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		return itemName+"-"+format.format(new Date())+".jpg";
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		
		if(resultCode==Activity.RESULT_CANCELED){
			if(myPhoto.exists()){
				myPhoto.delete();
			}
			finish();
		}
		
		if (resultCode == Activity.RESULT_OK) {
			String sdStatus = Environment.getExternalStorageState();
			if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
				return;
			}

			if(requestCode==1){
				
				Bitmap bitmap = BitmapUtils.loadBitmap(filePath,true);			
				
				imageView.setImageBitmap(bitmap);	
				
				buttonGroup.setVisibility(View.VISIBLE);
				
				btn_save.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Toast.makeText(PictureActivity.this, "文件已保存在<"+filePath+">中", Toast.LENGTH_SHORT).show();
						//数据库中建立关联
						dao.addInspectImage(userId,itemId,filePath,inspectTableName,itemName,0);
						int groupPosition = getIntent().getIntExtra("groupPosition", 0);
						int childPosition = getIntent().getIntExtra("childPosition", 0);
						InspectActivity.adapter.btn_list.get(groupPosition).set(childPosition, true);
						InspectActivity.adapter.notifyDataSetChanged();
						finish();
					}
				});
				
				btn_cancel.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if(myPhoto.exists()){
							myPhoto.delete();
							finish();
						}
					}
				});
				
				super.onActivityResult(requestCode, resultCode, data);
			}
			
			
		
		}

	}
}