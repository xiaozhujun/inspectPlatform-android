package org.whut.adapter;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.whut.database.entity.service.impl.InspectImageServiceDao;
import org.whut.inspectplatform.R;
import org.whut.platform.ImageUploadActivity;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MyImageListAdapter extends BaseAdapter{

	private Context context;
	private List<Bitmap> images;
	private List<Map<String,String>> list; 
	private LayoutInflater inflater;
	
	private InspectImageServiceDao dao;



	public MyImageListAdapter(List<Bitmap> images,Context context,List<Map<String,String>> list){
		this.context = context;
		this.images = images;
		this.list = list;
		inflater = LayoutInflater.from(context);
		dao = new InspectImageServiceDao(context);
	}



	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return list.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup arg2) {
		// TODO Auto-generated method stub
		if(convertView==null){
			convertView = inflater.inflate(R.layout.listitem_uploadimage, null);
			ViewHolder holder = new ViewHolder();
			holder.image = (ImageView) convertView.findViewById(R.id.show_image);

			holder.tv_table = (TextView) convertView.findViewById(R.id.tv_inspecttablename);

			holder.tv_item = (TextView) convertView.findViewById(R.id.tv_inspectitemname);

			holder.tv_time = (TextView) convertView.findViewById(R.id.tv_inspecttime);
			
			holder.rl_upload = (RelativeLayout) convertView.findViewById(R.id.button_route_layout);
			
			holder.rl_delete = (RelativeLayout) convertView.findViewById(R.id.button_call_layout);
			
			holder.btn_upload  =  (Button) convertView.findViewById(R.id.button_upload);

			holder.btn_delete = (Button) convertView.findViewById(R.id.button_delete);

			convertView.setTag(holder);
		}

		ViewHolder holder = (ViewHolder) convertView.getTag();

		holder.image.setImageBitmap(images.get(position));

		holder.tv_item.setText("所属点检项："+list.get(position).get("itemName"));

		holder.tv_table.setText(list.get(position).get("inspectTableName").split("-")[0]);
		

		File file =  new File(list.get(position).get("filePath"));
		String filename = file.getName();
		
		
		holder.tv_time.setText("创建时间："+getFormatTime(filename.split("-")[1]));
		
		holder.rl_upload.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new Thread(new ImageUploadActivity.UploadImageThread(position)).start();
			
				
				
				
			}
		});

		switch(Integer.parseInt(list.get(position).get("uploadFlag"))){
			case 0:
				holder.btn_upload.setText("上传");
				Drawable drawable1 = context.getResources().getDrawable(R.drawable.upload);
				drawable1.setBounds(0, 0, drawable1.getMinimumWidth(), drawable1.getMinimumHeight());
				holder.btn_upload.setCompoundDrawables(drawable1, null, null, null);
				holder.rl_upload.setClickable(true);
				break;
			case 1:
				Drawable drawable2 = context.getResources().getDrawable(R.drawable.uploaded);
				drawable2.setBounds(0,0,drawable2.getMinimumWidth(),drawable2.getMinimumHeight());
				holder.btn_upload.setText("已上传");
				holder.btn_upload.setCompoundDrawables(drawable2, null, null, null);
				holder.rl_upload.setClickable(false);
				break;
		}
		
		holder.rl_delete.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Builder alertDialog = new AlertDialog.Builder(context);
				
				alertDialog.setTitle("提示").setMessage("是否删除该照片？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						File f = new File(list.get(position).get("filePath"));
						if(f.exists()){
							f.delete();
							dao.deleteInspectImagesByFilePath(list.get(position).get("filePath"));
							Log.i("delete", list.get(position).get("filePath"));
							images.remove(position);
							list.remove(position);
						}
						notifyDataSetChanged();
					}
				}).setNegativeButton("取消", null).show();
			}
		});
		
		Drawable drawable2 = context.getResources().getDrawable(R.drawable.delete);
		drawable2.setBounds(0,0,drawable2.getMinimumWidth(),drawable2.getMinimumHeight());
		holder.btn_delete.setCompoundDrawables(drawable2, null, null, null);

		return convertView;
	}

	
	


	public List<Map<String, String>> getList() {
		return list;
	}



	public void setList(List<Map<String, String>> list) {
		this.list = list;
	}



	private String getFormatTime(String time){
		return time.substring(0,4)+"-"+time.substring(4,6)+"-"
				+time.substring(6,8)+" "+time.substring(8,10)+":"
				+time.substring(10,12)+":"+time.substring(12,14);
	}
	

	class ViewHolder{
		ImageView image;
		TextView tv_table;
		TextView tv_item;
		TextView tv_time;
		Button btn_upload;
		Button btn_delete;
		RelativeLayout rl_upload;
		RelativeLayout rl_delete;
		
	}	
}
