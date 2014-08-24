package org.whut.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.whut.inspectplatform.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

@SuppressLint("UseSparseArrays")
public class MyListAdapter extends BaseAdapter{

	private List<Map<String,String>> list;
	private static HashMap<Integer,Boolean> isSelected;
	private LayoutInflater inflater = null; 
	private int visibility;
	
	public void setVisibility(int visibility){
		this.visibility = visibility;
	}

	public MyListAdapter(List<Map<String,String>> list,Context context){
		this.list = list;
		inflater = LayoutInflater.from(context);
		isSelected = new HashMap<Integer, Boolean>();	
		visibility = View.INVISIBLE;
		initData();
	}

	private void initData(){
		for(int i=0;i<list.size();i++){
			getIsSelected().put(i,false);
		}
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;
		if(convertView == null){
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.listitem_history, null);
			holder.tableName = (TextView) convertView.findViewById(R.id.content_name);
			holder.uploadFlag = (TextView) convertView.findViewById(R.id.upload);
			holder.cb = (CheckBox) convertView.findViewById(R.id.check_box);
			holder.filePath = (TextView) convertView.findViewById(R.id.filePath);
			holder.inspectTime = (TextView) convertView.findViewById(R.id.inspectTime);
			holder.userName = (TextView) convertView.findViewById(R.id.userName);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
			
		}
		holder.tableName.setText(list.get(position).get("inspectTableName").split("-")[0]);
		Log.i("msg", list.get(position).get("uploadFlag"));
		switch(Integer.parseInt(list.get(position).get("uploadFlag"))){
		case 0://未上传
			holder.uploadFlag.setText("未上传");
			holder.uploadFlag.setBackgroundResource(R.color.coral);
			break;
		case 1:
			holder.uploadFlag.setText("已上传");
			holder.uploadFlag.setBackgroundResource(R.color.bisque);
			break;
		}
		
		holder.cb.setVisibility(visibility);
		
		holder.filePath.setText("文件路径："+list.get(position).get("filePath"));
		holder.inspectTime.setText("点检时间："+list.get(position).get("inspectTime"));
		holder.userName.setText("点检人："+list.get(position).get("userName"));
		
		
		/*此处一定要注意，先设置监听，再设置checkbox的状态！不然ListView下滑时会
		*造成已选中的checkbox的状态丢失，上方图片设置同理！
		*
		*
		*/
		holder.cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				getIsSelected().put(position, isChecked);
			}
		});
		
		holder.cb.setChecked(getIsSelected().get(position));
		
		return convertView;
	}


	public static HashMap<Integer, Boolean> getIsSelected() {  
		return isSelected;  
	}
	
	public static void getIsSelected(HashMap<Integer,Boolean> isSelected){
		MyListAdapter.isSelected = isSelected;
	}
	
	public static class ViewHolder {  
		TextView tableName; 
		TextView uploadFlag;
		CheckBox cb;
		TextView filePath;
		TextView inspectTime;
		TextView userName;

	} 

}
