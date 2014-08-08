package org.whut.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.util.TextUtils;
import org.whut.inspectplatform.R;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
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
	private List<String> groupList;
	private List<List<String>> childList;
	private List<List<Map<String,String>>> commentList;
	private List<List<Integer>> bg_color;
	
	public MyExpandableListAdapter(Context context,List<String> groupList,List<List<String>> childList,List<List<Integer>> bg_color){
		this.context = context;
		this.groupList = groupList;
		this.childList = childList;
		inflater = LayoutInflater.from(context);
		this.bg_color = bg_color;
		
		//初始化备注数据
		commentList = new ArrayList<List<Map<String,String>>>();
		for(int i=0;i<groupList.size();i++){
			List<Map<String,String>> list = new ArrayList<Map<String,String>>();
			for(int j=0;j<childList.get(i).size();j++){
				Map<String,String> map = new HashMap<String,String>();
				map.put("comment", "");
				map.put("btnStatus", "添加备注");
				list.add(map);
			}
			commentList.add(list);
		}
	}
	
	
	
	
	public List<List<Map<String,String>>> getCommentList() {
		return commentList;
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
			holder.btn_comment = (Button) convertView.findViewById(R.id.btn_comment);
			convertView.setTag(holder);
		}
		holder = (ChildViewHolder) convertView.getTag();
		holder.layout.setBackgroundColor(bg_color.get(groupPosition).get(childPosition));
		holder.textView.setText(getChild(groupPosition, childPosition).toString());
		final Button btn = holder.btn_comment;
		
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
							btn.setText("已添加");
							commentList.get(groupPosition).get(childPosition).put("btnStatus", "已添加");
							btn.setBackgroundResource(R.drawable.common_btn_disable);
						}else{
							btn.setText("添加备注");
							commentList.get(groupPosition).get(childPosition).put("btnStatus", "添加备注");
							btn.setBackgroundResource(R.drawable.common_btn_normal);
						}
					}
				}).setNegativeButton("取消", null).show();
			}
		});
		
		if(commentList.get(groupPosition).get(childPosition).get("btnStatus").equals("已添加")){
			btn.setText("已添加");
			btn.setBackgroundResource(R.drawable.common_btn_disable);
		}else{
			btn.setText("添加备注");
			btn.setBackgroundResource(R.drawable.common_btn_normal);
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
		Button btn_comment;
	}

	public List<List<Integer>> getBg_color() {
		return bg_color;
	}


	public void setBg_color(List<List<Integer>> bg_color) {
		this.bg_color = bg_color;
	}
	

}
