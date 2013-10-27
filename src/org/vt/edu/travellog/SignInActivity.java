package org.vt.edu.travellog;


import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SignInActivity extends Activity {
	
	//Declare class variables
	private Button signUpButton_;
	private Button logInButton_;
	private EditText username_;
	private EditText password_;
	private TextView update_;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_in);
		
		//Set-up GUI
		signUpButton_ = (Button) findViewById(R.id.sign_up);
		logInButton_ = (Button) findViewById(R.id.login);
		username_ = (EditText) findViewById(R.id.usernameInput);
		password_ = (EditText) findViewById(R.id.passwordInput);
		update_ = (TextView) findViewById(R.id.updateString);
		
		signUpButton_.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				String params = username_.getText().toString() + " " + password_.getText().toString();
				
				WebSetActivity.setUsername_(username_.getText().toString());
				
				final SignUpTask signUp = new SignUpTask(SignInActivity.this);
				
				signUp.execute(params);
			}
     
		});
		
		logInButton_.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				String params = username_.getText().toString() + " " + password_.getText().toString();
				
				WebSetActivity.setUsername_(username_.getText().toString());
				
				final LogInTask login = new LogInTask(SignInActivity.this);
				
				login.execute(params);
			}
			
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.sign_in, menu);
		menu.add("Webserver Info");
		return true;
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
		String plan = (String) item.getTitle();
		
		if (plan == "Webserver Info") {
			startActivity(new Intent(getApplicationContext(), WebSetActivity.class));
		}

     	return super.onOptionsItemSelected(item);
    }

	//See if login/sign-up was successful
	public void update(String result) {
		update_.setText(result);
		
		if (result.contains("Success")) {
			startActivity(new Intent(getApplicationContext(), LogActivity.class));
			finish();
		}
	}
}