package edu.mit.media.funf.wifiscanner;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;

public class SettingsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		
		LinearLayout container = (LinearLayout)findViewById(R.id.settingsContainer);
		
		CheckBox cb = new CheckBox(this);
		cb.setText("Enable/Disable Probe");
		//cb.setId();
		
		container.addView(cb);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_settings, menu);
		return true;
	}

}
