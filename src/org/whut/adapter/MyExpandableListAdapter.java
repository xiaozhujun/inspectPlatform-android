package org.whut.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.util.TextUtils;
import org.whut.inspectplatform.R;
import org.whut.platform.PictureActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MyExpandableListAdapter extends BaseExpandableListAdapter{

	private Context context;
	private LayoutInflater inflater;
	//组数据
	private List<String> groupList;
	//子数据
	private List<List<String>> childList;
	
	//备注数据
	private List<List<Map<String,String>>> commentList;
	//点检结果数据
	private List<List<Integer>> result;
	//点检项id数据
	private List<List<Integer>> itemIds;
	
	//点检表名称
	
	private String inspectTableName;
	
	public MyExpandableListAdapter(Context context,List<String> groupList,List<List<String>> childList,List<List<Integer>> itemIds,String inspectTableName){
		this.context = context;
		this.groupList = groupList;
		this.childList = childList;
		this.itemIds = itemIds;
		this.inspectTableName = inspectTableName;
		inflater = LayoutInflater.from(context);
		
		//初始化数据
		commentList = new ArrayList<List<Map<String,String>>>();
		result = new ArrayList<List<Integer>>();
		for(int i=0;i<groupList.size();i++){
			List<Map<String,String>> list = new ArrayList<Map<String,String>>();
			List<Integer> list2 = new ArrayList<Integer>();
			for(int j=0;j<childList.get(i).size();j++){
				Map<String,String> map = new HashMap<String,String>();
				map.put("comment", "");
				map.put("btnStatus", "备注");
				list.add(map);
				// 0 , 代表正常
				list2.add(0);
			}
			commentList.add(list);
			result.add(list2);
		}
	}
	
	
	
	
	public List<List<Map<String,String>>> getCommentList() {
		return commentList;
	}
	
	public List<List<Integer>> getResultList(){
		return result;
	}

	
	
	public String getInspectTableName() {
		return inspectTableName;
	}



	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;
		if(convertView == null){
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.listitem_group, null);
			holder.relativeLayout = (RelativeLayout) convertView.findViewById(R.id.group_bg);
			holder.textView = (TextView) convertView.findViewById(R.id.location);
			holder.imageView = (ImageView) convertView.findViewById(R.id.group_arrow);	
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.textView.setText(getGroup(groupPosition).toString());
	
		if(isExpanded){
			holder.imageView.setBackgroundResource(R.drawable.activity_loc_arrow_down);
			holder.relativeLayout.setBackgroundResource(R.drawable.activity_panel_background_click);
		}else{
			holder.imageView.setBackgroundResource(R.drawable.activity_loc_arrow);
			holder.relativeLayout.setBackgroundResource(R.drawable.activity_panel_background_normal);
		}
		return convertView;
	}
	
	
	@SuppressLint("ResourceAsColor")
	@Override
	public View getChildView(final int groupPosition, final int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ChildViewHolder holder = null;
		if(convertView == null){
			convertView = inflater.inflate(R.layout.listitem_child, null);
			holder = new ChildViewHolder();
			holder.layout = (RelativeLayout) convertView.findViewById(R.id.rl_bg);
			holder.textView = (TextView) convertView.findViewById(R.id.field);
			holder.divider = (ImageView) convertView.findViewById(R.id.my_divider);
			holder.btn_comment = (Button) convertView.findViewById(R.id.btn_comment);
			holder.btn_camera = (Button) convertView.findViewById(R.id.btn_camera);
			holder.btn_result = (Button) convertView.findViewById(R.id.btn_result);
			convertView.setTag(holder);
		}
	
		holder = (ChildViewHolder) convertView.getTag();
		
		holder.textView.setText(getChild(groupPosition, childPosition).toString());
		
		final RelativeLayout bg_color = holder.layout;
		final Button btn_com = holder.btn_comment;
		final Button btn_res = holder.btn_result;
		
		holder.btn_comment.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Builder alertDialog = new AlertDialog.Builder(context);
				final EditText editText = new EditText(context);
				editText.setText(commentList.get(groupPosition).get(childPosition).get("comment"));
				alertDialog.setTitle("备注").setView(editText).setPositiveButton("确定", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						String comment = editText.getText().toString().trim();
						commentList.get(groupPosition).get(childPosition).put("comment", comment);
						Log.i("Debug", groupPosition+";"+childPosition);
						if(!TextUtils.isEmpty(editText.getText().toString().trim())){
							btn_com.setText("已添加");
							commentList.get(groupPosition).get(childPosition).put("btnStatus", "已添加");
							btn_com.setBackgroundResource(R.drawable.common_btn_disable);
						}else{
							btn_com.setText("备注");
							commentList.get(groupPosition).get(childPosition).put("btnStatus", "备注");
							btn_com.setBackgroundResource(R.drawable.common_btn_normal);
						}
					}
				}).setNegativeButton("取消", null).show();
			}
		});
		
		if(commentList.get(groupPosition).get(childPosition).get("btnStatus").equals("已添加")){
			btn_com.setText("已添加");
		}else{
			btn_com.setText("备注");
		}
		
		
		holder.btn_camera.setText("拍照");
		holder.btn_camera.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent it = new Intent(context,PictureActivity.class);
				it.putExtra("itemName", childList.get(groupPosition).get(childPosition));
				it.putExtra("itemId", itemIds.get(groupPosition).get(childPosition));
				Log.i("Debug", childList.get(groupPosition).get(childPosition)+":"+itemIds.get(groupPosition).get(childPosition));
				it.putExtra("inspectTableName", getInspectTableName());
				context.startActivity(it);
			}
		});
		
		holder.btn_result.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Builder alertDialog = new AlertDialog.Builder(context);
				alertDialog.setTitle(childList.get(groupPosition).get(childPosition)).setSingleChoiceItems(new String[]{"正常","异常","无"}, result.get(groupPosition).get(childPosition), new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						result.get(groupPosition).set(childPosition, which);
					}
				}).setPositiveButton("确定", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						switch(result.get(groupPosition).get(childPosition)){
						case 0://正常
							bg_color.setBackgroundColor(Color.parseColor("#E0FFFF"));
							btn_res.setText("正常");
							break;
						case 1://异常
							bg_color.setBackgroundColor(Color.parseColor("#FFE4E1"));
							btn_res.setText("异常");
							break;
						case 2://无
							bg_color.setBackgroundColor(Color.parseColor("#FFF8DC"));
							btn_res.setText("无");
							break;
						}
					}
				}).setNegativeButton("取消", null).show();
			}
		});
		
		switch(result.get(groupPosition).get(childPosition)){
		case 0://正常
			holder.layout.setBackgroundColor(Color.parseColor("#E0FFFF"));
			holder.btn_result.setText("正常");
			break;
		case 1://异常
			holder.layout.setBackgroundColor(Color.parseColor("#FFE4E1"));
			holder.btn_result.setText("异常");
			break;
		case 2://无
			holder.layout.setBackgroundColor(Color.parseColor("#FFF8DC"));
			holder.btn_result.setText("无");
			break;
		}
		
		return convertView;
	}
	
	@Override
	public Object getChild(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return childList.get(groupPosition).get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return childPosition;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		// TODO Auto-generated method stub
		return childList.get(groupPosition).size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		// TODO Auto-generated method stub
		return groupList.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		// TODO Auto-generated method stub
		return groupList.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		// TODO Auto-generated method stub
		return groupPosition;
	}



	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return true;
	}
			
	
	class ViewHolder{
		RelativeLayout relativeLayout;
		TextView textView;
		ImageView imageView;
	}
	
	class ChildViewHolder{
		RelativeLayout layout;
		TextView textView;
		ImageView divider;
		Button btn_comment;
		Button btn_camera;
		Button btn_result;
	}	
}
