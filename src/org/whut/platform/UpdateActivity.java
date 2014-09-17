package org.whut.platform;

import org.whut.inspectplatform.R;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

public class UpdateActivity extends Activity{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_update);
		Toast.makeText(getApplicationContext(), "当前已是最新版本！", Toast.LENGTH_SHORT).show();
	}
	
}
