package org.vt.edu.travellog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;
import android.util.Log;

public class SignUpTask extends AsyncTask<String, Void, String> {
	
	private SignInActivity myActivity_ = null;
	
	public SignUpTask(SignInActivity curActivity) {
		myActivity_ = curActivity;
	}

	@Override
	protected String doInBackground(String... arg0) {
		//Get parameters for URL
		String[] nameTokens = arg0[0].split(" ");
		
		String username = nameTokens[0];
		String password = nameTokens[1];
		
		String ipAddress = WebSetActivity.getIPAddress();
		String port = WebSetActivity.getPort();
		
		//Set up URL
		String URL = "http://" + ipAddress + ":" + port + "/signup?username=" + username + "&password=" + password;
		
		//Send HTTP Get request
		HttpResponse response = null;
		try {
			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet(URL);
			response = client.execute(request);
		}
		catch (Exception e){
			Log.i("ERROR", e.toString());
		}
		
		//Read in the http response to form a readable string
		String line = null;
		StringBuilder sb = new StringBuilder();
		try {
			InputStream in = response.getEntity().getContent();
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		}
		catch (IOException e) {
			Log.i("ERROR", e.toString());
		}
		
		//Allocate memory for variables
		String httpResponseVal = sb.toString();
		
		String result = null;
		String reason = null;
		
		if (httpResponseVal.contains(">true<")) {
			result = "Success";
			reason = "";
		}
		else {
			result = "Failure";
			reason = "Username already exists";
		}
		
		return result + ": " + reason;
	}

	protected void onPostExecute(String incomingString) {
		myActivity_.update(incomingString);
	}

}
