package edu.mit.media.funf.wifiscanner;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.res.Resources;
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
		
		init();
		
		setContentView(R.layout.main);	
    	
        final Context context = this;
        
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
        
		Button listProbeButton = (Button)findViewById(R.id.listProbeButton);
		listProbeButton.setOnClickListener(listProbes);
		
		Button changeSettingButton = (Button)findViewById(R.id.changeSettingButton);
		changeSettingButton.setOnClickListener(changeSetting);
		
		Button resetButton = (Button)findViewById(R.id.resetButton);
		resetButton.setOnClickListener(reset);
		
		Button exitButton = (Button)findViewById(R.id.exitButton);
		exitButton.setOnClickListener(exit);
	}
	
	OnClickListener listProbes = new OnClickListener(){

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			SharedPreferences prefs = getApplicationContext().getSharedPreferences(MainPipeline.MAIN_CONFIG, MODE_PRIVATE);
	    	FunfConfig config  = FunfConfig.getInstance((prefs));
	    	Map<String, Bundle[]> dataRequest = config.getDataRequests();
	    	
			Resources resources = getResources();
			String[] probe_list = resources.getStringArray(R.array.probes);						
			
			Log.i("Debug",Long.toString(config.getDataUploadPeriod()));
			if(config.getDataUploadUrl()!=null)Log.i("Debug",config.getDataUploadUrl());
			else Log.i("Debug","Upload URL not set");
			
			if(dataRequest.size()==0) Log.i("Debug","All probes are OFF");
			
			for(int i = 0; i < probe_list.length;i++){
				if(dataRequest.containsKey(SettingsActivity.probe_prefix+probe_list[i])){
					Bundle[] params = dataRequest.get(SettingsActivity.probe_prefix+probe_list[i]);
					
					for(Bundle param_set : params){
						Log.i("Debug",probe_list[i]+" "+param_set.toString());
					}
				}
			}
	    	
		}
		
	};
	
	OnClickListener changeSetting = new OnClickListener(){

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			SharedPreferences prefs = getApplicationContext().getSharedPreferences(MainPipeline.MAIN_CONFIG, MODE_PRIVATE);
	    	FunfConfig config  = FunfConfig.getInstance((prefs));
	    	
			//config.edit().setDataUploadPeriod(15).commit();
			//config.edit().setDataUploadUrl("http://192.168.1.102:3000/upload").commit();
	    	
	    	Intent uploadIntent = new Intent(getBaseContext(), MainPipeline.class);
	    	uploadIntent.setAction(MainPipeline.ACTION_UPLOAD_DATA);
	    	startService(uploadIntent);	    
			
			Log.i("Debug","Uploading...");
		}
		
	};
	
	OnClickListener reset = new OnClickListener(){

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Log.i("Debug","RESET");
			init();
		}
		
	};
	
	OnClickListener exit = new OnClickListener(){

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent = new Intent(getBaseContext(), MainPipeline.class);
			String action = MainPipeline.ACTION_DISABLE;
			intent.setAction(action);
            startService(intent);
			
			finish();
		}
		
	};
	
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
	
	public void init(){
		
		Intent archiveIntent = new Intent(getBaseContext(), MainPipeline.class);
        archiveIntent.setAction(MainPipeline.ACTION_ENABLE);
        startService(archiveIntent);
        
//        Intent uploadIntent = new Intent(getBaseContext(), MainPipeline.class);
//    	uploadIntent.setAction(MainPipeline.ACTION_UPLOAD_DATA);
//    	startService(uploadIntent);	
        
        SharedPreferences prefs = getApplicationContext().getSharedPreferences(MainPipeline.MAIN_CONFIG, MODE_PRIVATE);
    	FunfConfig config  = FunfConfig.getInstance((prefs));
    	
		//config.edit().setDataUploadPeriod(FunfConfig.DEFAULT_DATA_UPLOAD_PERIOD).commit();
    	config.edit().setDataUploadPeriod(600).commit();
		config.edit().setDataUploadUrl("http://192.168.1.105:9000/upload").commit();
	}
	

	/* (non-Javadoc)
	 * @see android.app.Activity#onStart()
	 */
	@Override
	public void onStart() {
		super.onStart();
		// TODO Auto-generated method stub
		
		Log.i("Main","Start");
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	public void onResume() {
		super.onResume();
		// TODO Auto-generated method stub
		
		Log.i("Main","Resume");
		updateScanCount();

	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onPause()
	 */
	@Override
	public void onPause() {
		super.onPause();
		// TODO Auto-generated method stub
		
		Log.i("Main","Pause");

	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onStop()
	 */
	@Override
	public void onStop() {
		super.onStop();
		// TODO Auto-generated method stub
		
		Log.i("Main","Stop");

	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	public void onDestroy() {
		super.onDestroy();
		// TODO Auto-generated method stub
		Log.i("Main","Destroy");

	}
	
	

}
