package org.whut.adapter;

import java.util.List;
import java.util.Map;
import org.whut.inspectplatform.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MyAdapter extends BaseAdapter{

	private List<Map<String,Object>> list;
	private LayoutInflater inflater = null; 

	
	
	public MyAdapter(List<Map<String,Object>> list,Context context){
		this.list = list;
		inflater = LayoutInflater.from(context);
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;
		if(convertView==null){
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.listitem_task, null);
			holder.image_task = (ImageView) convertView.findViewById(R.id.image_task);
			holder.task_name = (TextView) convertView.findViewById(R.id.taskName);
			holder.task_status = (TextView) convertView.findViewById(R.id.tv_status);
			holder.arrow = (ImageView) convertView.findViewById(R.id.image_arrow);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		//检测任务的localStatus状态，若为1，表示已完成，改变图标，若为0，表示未完成。
		if(Integer.valueOf(list.get(position).get("localStatus").toString())==1){
			holder.image_task.setBackgroundResource(R.drawable.finish);
			holder.task_status.setText("已完成");
		}else{
			holder.image_task.setBackgroundResource(R.drawable.task);
			holder.task_status.setText("未完成");
		}
		holder.task_name.setText(list.get(position).get("planName").toString());
		return convertView;
	}
	
	public static class ViewHolder{
		ImageView image_task;
		TextView task_name;
		TextView task_status;
		ImageView arrow;
	}

}
