package org.vt.edu.travellog;

import android.app.Activity;
import android.os.Bundle;

public class Screen extends Activity{

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		finish();
	}
	
}
