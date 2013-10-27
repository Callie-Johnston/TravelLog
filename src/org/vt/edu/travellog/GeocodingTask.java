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

public class GeocodingTask extends AsyncTask<String, Void, String>{
	
	private String locationString_;
	private static Boolean done_ = false;

	@Override
	protected String doInBackground(String... arg0) {
		
		//Retrieve lat lon
		String lat = arg0[0];
		String lon = arg0[1];

		//Set up url for reverse geocoding website
		String URL = "http://api.geonames.org/findNearbyPlaceName?lat=" + lat + "&lng=" + lon + "&username=travellog";
		
		//Send http request
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
		
		//Get http response back
		String httpResponseVal = sb.toString();
		
		String[] tokens = httpResponseVal.split("\n");
		String city = null;
		String country = null;
		
		//Find name of city and country name
		for (int i = 0; i < tokens.length; i++) {
			if (tokens[i].contains("<name>")) {
				city = tokens[i].substring(6, tokens[i].length() - 7);
			}
			else if (tokens[i].contains("countryName>")) {
				country = tokens[i].substring(13, tokens[i].length() - 14);
			}
		}
		
		//Set the location string and country string
		locationString_ = city + ", " + country;
		
		AddEntryTask.setLocationString(locationString_);
		WebSetActivity.setCountry_(country);
		
		//Let the add entry task know we're done
		setDone(true);
		
		return null;
	}

	public static Boolean getDone() {
		return done_;
	}

	public static void setDone(Boolean done) {
		done_ = done;
	}

}
