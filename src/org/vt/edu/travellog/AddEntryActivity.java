package org.vt.edu.travellog;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class AddEntryActivity extends Activity implements OnItemSelectedListener {
	//Declare needed class variables
	private Spinner spinner_;
	private Button addButton_;
	private Button addPicture_;
	private EditText notes_;
	private TextView location_;
	private TextView date_;
	private static String[] photos_ = new String[5];
	
	String ratingString_ = null;
	
	//Declare static parameter for image capture
	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_entry);
		
		//Create spinner with correct items
		spinner_ = (Spinner) findViewById(R.id.spinner1);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
		        R.array.Ratings, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		// Apply the adapter to the spinner
		spinner_.setAdapter(adapter);
		spinner_.setOnItemSelectedListener(this);
		
		//Create GUI elements
		notes_ = (EditText) findViewById(R.id.notes);
		location_ = (TextView) findViewById(R.id.location);
		date_ = (TextView) findViewById(R.id.date);
		
		location_.setText(LogActivity.getLocationString_());
		
		//Set date
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Date date = new Date();
        date_.setText(dateFormat.format(date));
        
		addButton_ = (Button) findViewById(R.id.addButton);
		
		addButton_.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				final GeocodingTask code = new GeocodingTask();
				
				//Get lat lon from string on GUI
				String[] splitString = location_.getText().toString().split(",");
				if (splitString.length > 1) { 
					String passedLat = splitString[0].substring(6);
					String passedLon = splitString[1].substring(7);
			
					code.execute(passedLat, passedLon);
				}
				
				//Go to add entry task
				final AddEntryTask entryTask = new AddEntryTask();
				
				entryTask.execute(location_.getText().toString(), date_.getText().toString(),
						notes_.getText().toString(), ratingString_, photos_[0], photos_[1], photos_[2],
						photos_[3], photos_[4]);
				
				for (int i = 0; i < 5; i++) {
					photos_[i] = "";
				}
				
				finish();
				
			}
		});
		
		addPicture_ = (Button) findViewById(R.id.addPicture);
		
		addPicture_.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				//Start camera intent
				Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				
				startActivityForResult(cameraIntent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
			}
		});
		
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		ratingString_ = (String) arg0.getItemAtPosition(arg2);
		
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		ratingString_ = null;
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
			if (resultCode == RESULT_OK) {
				
				//Get the path to the last photo taken and store
				String[] projection = {MediaStore.Images.Media.DATA};
				Cursor cursor = managedQuery(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null, null, null);
				int column_index_data = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
				cursor.moveToLast();
				
				String photoString = cursor.getString(column_index_data);
				
				for (int i = 0; i < 5; i++) {
					if (photos_[i] == "" || photos_[i] == null) {
						photos_[i] = photoString;
						break;
					}
				}
			}
		}
	}

}
