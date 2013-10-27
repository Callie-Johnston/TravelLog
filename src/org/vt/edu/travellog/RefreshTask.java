package org.vt.edu.travellog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Vector;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;
import android.util.Log;

public class RefreshTask extends AsyncTask<String, Void, String> {
	
	private LogActivity myActivity_ = null;
	private Vector<String> log = new Vector<String>(1,1);

	public RefreshTask(LogActivity newActivity) {
		myActivity_ = newActivity;
	}
	
	@Override
	protected String doInBackground(String... params) {
		
		//Get username, IP address, and port for URL
		String username = params[0];
		
		String ipAddress = WebSetActivity.getIPAddress();
		String port = WebSetActivity.getPort();
		
		//Set up URL
		String URL = "http://" + ipAddress + ":" + port + "/logEntry?username=" + username;
		
		//Send http GET request
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
		
		String httpResponseVal = sb.toString();
		
		String[] entries = httpResponseVal.split("</Entry>");
		log.clear();
		
		//Read in entries from XML
		for (int i = 0; i < entries.length; i++) {
			String[] fields = entries[i].split("\n");
			String addString = "";
			for (int j = 0; j < fields.length; j++) {
				if (fields[j].contains("<Location>")) {
					addString = addString + "Location: ";
					addString = addString + fields[j].substring(10, fields[j].length() - 11);
					addString = addString + "\n";
				}
				else if (fields[j].contains("<Date>")) {
					addString = addString + "Date: ";
					addString = addString + fields[j].substring(6, fields[j].length() - 7);
					addString = addString + "\n";
				}
				else if (fields[j].contains("<Notes>")) {
					addString = addString + "Notes: ";
					addString = addString + fields[j].substring(7, fields[j].length() - 8);
					addString = addString + "\n";
				}
				else if (fields[j].contains("<Rating>")) {
					addString = addString + "Rating: ";
					addString = addString + fields[j].substring(8, fields[j].length() - 9);
					addString = addString + "\n";
				}
				else if (fields[j].contains("<Photo>")) {
					addString = addString + "Photo: ";
					addString = addString + fields[j].substring(7, fields[j].length() - 8);
					addString = addString + "\n";
				}
			}
			log.add(addString);
		}
		
		return null;
	}
	
	protected void onPostExecute(String incomingString) {
		//Update UI with log entries
		myActivity_.updateListView(log);
	}

}
