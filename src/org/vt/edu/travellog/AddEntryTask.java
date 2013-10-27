package org.vt.edu.travellog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

public class AddEntryTask extends AsyncTask<String, Void, String>{
	
	public static String newLocation_ = null;

	@Override
	protected String doInBackground(String... params) {
		
		//Wait to get the location from the geocoding task if necessary
		String location = null;
		if (params[0].contains("Lat =") && params[0].contains("Lon =")) {
			while (!GeocodingTask.getDone());
			
			location = newLocation_;
		}
		else {
			location = params[0];
		}
	
		//Get all the parameters
		String ipAddress = WebSetActivity.getIPAddress();
		String port = WebSetActivity.getPort();
		String date = params[1];
		String notes = params[2];
		String rating = params[3];
		String photo0 = params[4];
		String photo1 = params[5];
		String photo2 = params[6];
		String photo3 = params[7];
		String photo4 = params[8];
		
		//Set up URL
		String URL = "http://" + ipAddress + ":" + port + "/logEntry";
		
		HttpResponse response = null;
		try {
			HttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost(URL);
			
			//add info here for URL to add entry
			post.setHeader("username", WebSetActivity.getUsername_());
			
			List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
			urlParameters.add(new BasicNameValuePair("location", location));
			urlParameters.add(new BasicNameValuePair("date", date));
			urlParameters.add(new BasicNameValuePair("notes", notes));
			urlParameters.add(new BasicNameValuePair("rating", rating));
			urlParameters.add(new BasicNameValuePair("photo0", photo0));
			urlParameters.add(new BasicNameValuePair("photo1", photo1));
			urlParameters.add(new BasicNameValuePair("photo2", photo2));
			urlParameters.add(new BasicNameValuePair("photo3", photo3));
			urlParameters.add(new BasicNameValuePair("photo4", photo4));
			
			post.setEntity(new UrlEncodedFormEntity(urlParameters));
			
			//Send request
			response = client.execute(post);
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
		
		//Don't need to do anything with response. Will see if worked when refresh is clicked
		
		return null;
	}
	
	public static void setLocationString(String newLoc) {
		//Update location
		newLocation_ = newLoc;
	}

}
