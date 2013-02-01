package edu.mit.media.funf.wifiscanner;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
import edu.mit.media.funf.configured.FunfConfig;
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
                
        //Checkbox to enable/disable WiFi
        CheckBox enabledCheckbox = (CheckBox)findViewById(R.id.enabledCheckbox); 
        enabledCheckbox.setChecked(MainPipeline.isEnabled(context));
        enabledCheckbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                        Intent archiveIntent = new Intent(context, MainPipeline.class);
//                        String action = isChecked ? MainPipeline.ACTION_ENABLE : MainPipeline.ACTION_DISABLE;
//                        archiveIntent.setAction(action);
//                        startService(archiveIntent);
                	Intent intent = new Intent(context, MainPipeline.class);
                	
                	SharedPreferences prefs = getApplicationContext().getSharedPreferences(MainPipeline.MAIN_CONFIG, MODE_PRIVATE);
                	FunfConfig config  = FunfConfig.getInstance((prefs));
                	Map<String, Bundle[]> dataRequest = config.getDataRequests();
                	
                	List<Bundle> tempList = new ArrayList<Bundle>();
                	tempList.add(new Bundle()); //you can add some parameters in the Bundle (e.g., PERIOD, DURATION). Now is default

                	Bundle arrBundle[] = tempList.toArray(new Bundle[tempList.size()]);
                	
                	String action = isChecked ? "Checked" : "Not Checked";
                	
                	//Log.i("Debug",intent.getExtras().toString());
                	
                	//Log.i("Debug",action);
                	
                	if(action=="Checked"){
                		dataRequest.put("edu.mit.media.funf.probe.builtin.LocationProbe", arrBundle);
                		config.edit().setDataRequests(dataRequest).commit();
                	}else{
                		dataRequest.remove("edu.mit.media.funf.probe.builtin.LocationProbe");
                		config.edit().setDataRequests(dataRequest).commit();
                	}
                }
        });
        
        //Button to archive data
        Button archiveButton = (Button)findViewById(R.id.archiveButton);
        archiveButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                        Intent archiveIntent = new Intent(context, MainPipeline.class);
                        archiveIntent.setAction(MainPipeline.ACTION_ARCHIVE_DATA);
                        startService(archiveIntent);
                }
        });
        
        //Button to scan data
        Button scanNowButton = (Button)findViewById(R.id.scanNowButton);
        scanNowButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                        Intent runOnceIntent = new Intent(context, MainPipeline.class);
                        runOnceIntent.setAction(MainPipeline.ACTION_RUN_ONCE);
                        //runOnceIntent.putExtra(MainPipeline.RUN_ONCE_PROBE_NAME, WifiProbe.class.getName());
                        //startService(runOnceIntent);
                        runOnceIntent.putExtra(MainPipeline.RUN_ONCE_PROBE_NAME, LocationProbe.class.getName());
                        startService(runOnceIntent);
                }
        });
        
        //Get current config
        Button getConfigButton = (Button)findViewById(R.id.getConfigButton);
        getConfigButton.setOnClickListener(new OnClickListener(){
        	@Override
            public void onClick(View v) {
        		
        		Intent intent = new Intent(getBaseContext(),SettingsActivity.class);
        		startActivity(intent);
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
