package org.vt.edu.travellog;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class LogActivity extends Activity implements LocationListener{
	
	//Declare class variables
	private Button refresh_;
	private Button addEntry_;
	private LocationManager locationManager_;
	private static String locationString_ = null;
	private ListView myEntryList_ = null;
	private ArrayList<String> entries_;
	private ArrayAdapter<String> adapter_;
	float maxSensorRange_ = 0;
	
	//Declare variables for light sensor
	private Sensor lightSensor_;
	private SensorManager sensorManager_;
	
	private class LightListener implements SensorEventListener {
		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {}

		@Override
		public void onSensorChanged(SensorEvent event) {
			//Get current light reading
			float currentReading = event.values[0];
			float newScreenValue = currentReading/maxSensorRange_;
	    
			int value = (int) (newScreenValue * 255) + 10;
			
			if (value < 0)
				value = 0;
			else if (value > 255)
				value = 255;
	    
			//Change the system settings
			Settings.System.putInt(LogActivity.this.getContentResolver(), android.provider.Settings.System.SCREEN_BRIGHTNESS_MODE, 
	    			android.provider.Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
			Settings.System.putInt(LogActivity.this.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, value);
	    
			//Update the current screen
			Window w = getWindow();

			WindowManager.LayoutParams lp = w.getAttributes();

			lp.screenBrightness = value / 100;

			if (lp.screenBrightness < 0.01f) {
				lp.screenBrightness = 0.01f;
			}

			w.setAttributes(lp);
		}		
	}
	
	private LightListener myLightListener_ = new LightListener();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_log);
		setLocationString_("");
		
		//Set GUI elements
		refresh_ = (Button) findViewById(R.id.refresh_log);
		addEntry_ = (Button) findViewById(R.id.add_new_log);
		
		//Set up list view for entries
		myEntryList_ = (ListView) findViewById(R.id.entryList);
		entries_ = new ArrayList<String>();
		adapter_ = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,entries_);
		myEntryList_.setAdapter(adapter_);
		
		//Set up GPS for location 
		locationManager_ = (LocationManager) getSystemService(LOCATION_SERVICE);
		locationManager_.requestLocationUpdates(LocationManager.GPS_PROVIDER, 300000,
												Criteria.ACCURACY_FINE, this);
		
		//Set up light sensor
		sensorManager_ = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
	    lightSensor_ = sensorManager_.getDefaultSensor(Sensor.TYPE_LIGHT);
	    maxSensorRange_ =  lightSensor_.getMaximumRange();

        sensorManager_.registerListener(myLightListener_, lightSensor_, SensorManager.SENSOR_DELAY_NORMAL);
		
		refresh_.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {

				//Retrieve entries from server
				final RefreshTask taskRefresh = new RefreshTask(LogActivity.this);
				
				taskRefresh.execute(WebSetActivity.getUsername_());
				
			}
		});
		
		addEntry_.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(getApplicationContext(), AddEntryActivity.class));
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
	
	protected void onPause() {
		super.onPause();
		//Remove listeners so as to not use battery
		locationManager_.removeUpdates(this);
		sensorManager_.unregisterListener(myLightListener_);
	}
	
	protected void onResume() {
		super.onResume();
		//Register listeners
		locationManager_.requestLocationUpdates(LocationManager.GPS_PROVIDER, 300000, 
												Criteria.ACCURACY_FINE, this);
		sensorManager_.registerListener(myLightListener_, lightSensor_, 
												SensorManager.SENSOR_DELAY_NORMAL);
	}

	@Override
	public void onLocationChanged(Location location) {
		
		double longitude = location.getLongitude();
		double latitude = location.getLatitude();
		
		setLocationString_("Lat = " + latitude + ", Lon = " + longitude);
	}

	@Override
	public void onProviderDisabled(String provider) {
		locationString_ = "No location available";
	}

	@Override
	public void onProviderEnabled(String provider) {
		locationString_ = "Waiting for location";
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		Log.i("network", "status changed");
	}

	public static String getLocationString_() {
		return locationString_;
	}
	
	public static void setLocationString_(String newLocation) {
		locationString_ = newLocation;
	}

	public void updateListView(Vector<String> newEntries) {
		entries_.clear();
		
		Iterator<String> itr = newEntries.iterator();
		
		//Go through entries and set-up in listview
		while (itr.hasNext()) {
			String curString = itr.next();

			if (curString != null && curString != "") {
				entries_.add(curString);
			}
		}
		
		adapter_.notifyDataSetChanged();
	}
}
