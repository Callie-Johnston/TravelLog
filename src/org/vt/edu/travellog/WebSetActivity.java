package org.vt.edu.travellog;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class WebSetActivity extends Activity {
	
	//Declare class variables
	private static String ipAddress_;
	private static String portNumber_;
	private static String username_;
	private static String country_;
	
	private Button setButton_;
	private TextView curIP_;
	private TextView curPort_;
	private EditText newIP_;
	private EditText newPort_;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_web_set);
		
		//Set up GUI elements
		setButton_ = (Button) findViewById(R.id.setValues);
		curIP_ = (TextView) findViewById(R.id.curIP);
		curPort_ = (TextView) findViewById(R.id.curPort);
		newIP_ = (EditText) findViewById(R.id.newIP);
		newPort_ = (EditText) findViewById(R.id.newPort);
		
		curIP_.setText(ipAddress_);
		curPort_.setText(portNumber_);
		
		setButton_.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				//Set IP address and Port
				String newAddress = newIP_.getText().toString();
				String newPort = newPort_.getText().toString();
				
				if (newAddress != null && newAddress != "") {
					ipAddress_ = newAddress;
				}
				
				if (newPort != null && newPort != "") {
					portNumber_ = newPort;
				}
				
				finish();
			}
		});
		
	}
	
	public static String getIPAddress() {
		return ipAddress_;
	}
	
	public static String getPort() {
		return portNumber_;
	}

	public static String getUsername_() {
		return username_;
	}

	public static void setUsername_(String username_) {
		WebSetActivity.username_ = username_;
	}
	
	public static void setCountry_(String newCountry) {
		WebSetActivity.country_ = newCountry;
	}
	
	public static String getCountry_() {
		return country_;
	}

}
