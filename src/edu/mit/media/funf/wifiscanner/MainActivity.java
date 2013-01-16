package edu.mit.media.funf.wifiscanner;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import edu.mit.media.funf.probe.builtin.LocationProbe;
import edu.mit.media.funf.probe.builtin.WifiProbe;

/**
 * 
 */

/**
 * @author Cat
 *
 */
public class MainActivity extends Activity {

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// TODO Auto-generated method stub
		
        //MainPipeline.getSystemPrefs(this).registerOnSharedPreferenceChangeListener(this);
        //updateScanCount();
		
		setContentView(R.layout.main);
        
        final Context context = this;
        
        CheckBox enabledCheckbox = (CheckBox)findViewById(R.id.enabledCheckbox); 
        enabledCheckbox.setChecked(MainPipeline.isEnabled(context));
        enabledCheckbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        Intent archiveIntent = new Intent(context, MainPipeline.class);
                        String action = isChecked ? MainPipeline.ACTION_ENABLE : MainPipeline.ACTION_DISABLE;
                        archiveIntent.setAction(action);
                        startService(archiveIntent);
                }
        });
        
        Button archiveButton = (Button)findViewById(R.id.archiveButton);
        archiveButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                        Intent archiveIntent = new Intent(context, MainPipeline.class);
                        archiveIntent.setAction(MainPipeline.ACTION_ARCHIVE_DATA);
                        startService(archiveIntent);
                }
        });
        
        Button scanNowButton = (Button)findViewById(R.id.scanNowButton);
        scanNowButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                        Intent runOnceIntent = new Intent(context, MainPipeline.class);
                        runOnceIntent.setAction(MainPipeline.ACTION_RUN_ONCE);
                        runOnceIntent.putExtra(MainPipeline.RUN_ONCE_PROBE_NAME, WifiProbe.class.getName());
                        startService(runOnceIntent);
                        runOnceIntent.putExtra(MainPipeline.RUN_ONCE_PROBE_NAME, LocationProbe.class.getName());
                        startService(runOnceIntent);
                }
        });

	}
	
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
	        Log.i("WifiScanner", "SharedPref change: " + key);
	        if (MainPipeline.SCAN_COUNT_KEY.equals(key)) {
	                updateScanCount();
	        }
	}

	
	private void updateScanCount() {
		// TODO Auto-generated method stub
		TextView dataCountView = (TextView)findViewById(R.id.dataCountText);
        dataCountView.setText("Data Count: " + MainPipeline.getScanCount(this));

	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onStart()
	 */
	@Override
	public void onStart() {
		super.onStart();
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	public void onResume() {
		super.onResume();
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onPause()
	 */
	@Override
	public void onPause() {
		super.onPause();
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onStop()
	 */
	@Override
	public void onStop() {
		super.onStop();
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	public void onDestroy() {
		super.onDestroy();
		// TODO Auto-generated method stub

	}
	
	

}
