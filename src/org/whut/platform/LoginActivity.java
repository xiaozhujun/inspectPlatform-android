package org.whut.platform;


import org.whut.application.MyApplication;
import org.whut.client.CasClient;
import org.whut.inspectplatform.R;
import org.whut.strings.UrlStrings;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
//import android.content.SharedPreferences;
//import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;

public class LoginActivity extends Activity{
/*
 * 	@author Yone
 * 
 * */
	private AutoCompleteTextView edt_uname;
	private EditText edt_pwd;
	private CheckBox show_pwd;
//	private CheckBox rem_pwd;
	private Button btn_login;
	
//	private SharedPreferences preference;
//	private Editor editor;
	private Handler handler;
	
	@SuppressLint("HandlerLeak")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		MyApplication.getInstance().addActivity(this);
		
		edt_uname = (AutoCompleteTextView) findViewById(R.id.aedt_uname);
		edt_pwd = (EditText) findViewById(R.id.edt_pwd);
		show_pwd = (CheckBox) findViewById(R.id.cb_show_pwd);
//		rem_pwd = (CheckBox) findViewById(R.id.cb_rem_pwd);
		btn_login = (Button) findViewById(R.id.btn_login);
	
		edt_uname.setText("zhaowei");
		edt_pwd.setText("123456");
		
		handler = new Handler(){

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				switch(msg.what){
				case 0:
					Toast.makeText(getApplicationContext(), "登录失败，请重试！",
							Toast.LENGTH_SHORT).show();
					break;
				case 1:
					Toast.makeText(getApplicationContext(), "登录成功，正在跳转...",
							Toast.LENGTH_SHORT).show();
					Intent it = new Intent(LoginActivity.this,MainActivity.class);
					it.putExtra("userName", edt_uname.getText().toString());
					startActivity(it);
					finish();
					break;
				}
			}
			
		};
		//显示密码
		show_pwd.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if(show_pwd.isChecked()){
					edt_pwd.setTransformationMethod(HideReturnsTransformationMethod
							.getInstance());
				}else{
					edt_pwd.setTransformationMethod(PasswordTransformationMethod
							.getInstance());
				}
			}
		});
		
		
		//登录按钮
		btn_login.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				if(event.getAction()==MotionEvent.ACTION_DOWN){
					((Button)v).setBackgroundResource(R.drawable.button_my_login_down);
				}else if(event.getAction()==MotionEvent.ACTION_UP){
					new Thread(new LoginThread()).start();
				}
				return false;
			}
		});
	}	
	
	//登录线程
	class LoginThread implements Runnable{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			boolean loginResult = CasClient.getInstance().login(edt_uname.getText().toString(), edt_pwd.getText().toString(),
					UrlStrings.SECURITY_CHECK);
			Message msg = Message.obtain();
			if(loginResult){
				msg.what = 1;
				handler.sendMessage(msg);
			}else{
				msg.what = 0;
				handler.sendMessage(msg);
			}
		}
		
	}
}
