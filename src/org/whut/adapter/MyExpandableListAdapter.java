package org.whut.adapter;

import java.util.List;

import org.whut.inspectplatform.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MyExpandableListAdapter extends BaseExpandableListAdapter{



	@SuppressWarnings("unused")
	private Context context;
	private LayoutInflater inflater;
	private List<String> groupList;
	private List<List<String>> childList;
	
	private List<List<Integer>> bg_color;
	
	public MyExpandableListAdapter(Context context,List<String> groupList,List<List<String>> childList,List<List<Integer>> bg_color){
		this.context = context;
		this.groupList = groupList;
		this.childList = childList;
		inflater = LayoutInflater.from(context);
		this.bg_color = bg_color;
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
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if(convertView == null){
			convertView = inflater.inflate(R.layout.listitem_child, null);
			ChildViewHolder holder = new ChildViewHolder();
			holder.layout = (RelativeLayout) convertView.findViewById(R.id.rl_bg);
			holder.textView = (TextView) convertView.findViewById(R.id.field);
			convertView.setTag(holder);
		}
		ChildViewHolder holder = (ChildViewHolder) convertView.getTag();
		holder.layout.setBackgroundColor(bg_color.get(groupPosition).get(childPosition));
		holder.textView.setText(getChild(groupPosition, childPosition).toString());
		Log.i("listview", childPosition+";"+groupPosition);
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
	}

	public List<List<Integer>> getBg_color() {
		return bg_color;
	}


	public void setBg_color(List<List<Integer>> bg_color) {
		this.bg_color = bg_color;
	}
	
	

}
