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
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import edu.mit.media.funf.configured.FunfConfig;
import edu.mit.media.funf.probe.builtin.LocationProbe;
import edu.mit.media.funf.probe.builtin.WifiProbe;
import android.provider.Settings.Secure;

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
	static boolean real_time;
	static final String REAL_TIME_UPLOAD_URL = "http://142.103.25.45:9000/rt";
	//static final String REAL_TIME_UPLOAD_URL = "http://192.168.1.105:9000/rt";
	static final String UPLOAD_URL = "http://142.103.25.45:9000/upload";
	//static final String UPLOAD_URL = "http://192.168.1.105:9000/upload";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// TODO Auto-generated method stub	
		
		init();
		
		setContentView(R.layout.main);	
    	
        final Context context = this;
        
        updateMainActivity();
        
        //Button to archive data
//        Button archiveButton = (Button)findViewById(R.id.archiveButton);
//        archiveButton.setOnClickListener(new OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                        Intent archiveIntent = new Intent(context, MainPipeline.class);
//                        archiveIntent.setAction(MainPipeline.ACTION_ARCHIVE_DATA);
//                        startService(archiveIntent);
//                }
//        });
        
        //Button to scan data
//        Button scanNowButton = (Button)findViewById(R.id.scanNowButton);
//        scanNowButton.setOnClickListener(new OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                        Intent runOnceIntent = new Intent(context, MainPipeline.class);
//                        runOnceIntent.setAction(MainPipeline.ACTION_RUN_ONCE);
//                        //runOnceIntent.putExtra(MainPipeline.RUN_ONCE_PROBE_NAME, WifiProbe.class.getName());
//                        //startService(runOnceIntent);
//                        runOnceIntent.putExtra(MainPipeline.RUN_ONCE_PROBE_NAME, LocationProbe.class.getName());
//                        startService(runOnceIntent);
//                }
//        });
        
        //Enable/Disable Real-time upload
        CheckBox enableRealTime = (CheckBox)findViewById(R.id.enableRealTime);
        enableRealTime.setOnCheckedChangeListener(setRealTime);
        
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
	
	OnCheckedChangeListener setRealTime = new OnCheckedChangeListener(){

		@Override
		public void onCheckedChanged(CompoundButton checkbox, boolean isChecked) {
			// TODO Auto-generated method stub
			if(isChecked) real_time = true;
			else real_time = false;
			
			Log.i("Debug",Boolean.toString(real_time));
		}
		
	};
	
	OnClickListener listProbes = new OnClickListener(){

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			SharedPreferences prefs = getApplicationContext().getSharedPreferences(MainPipeline.MAIN_CONFIG, MODE_PRIVATE);
	    	FunfConfig config  = FunfConfig.getInstance((prefs));
	    	Map<String, Bundle[]> dataRequest = config.getDataRequests();
	    	
			Resources resources = getResources();
			String[] probe_list = resources.getStringArray(R.array.probes);						
			
			String android_id = Secure.getString(getBaseContext().getContentResolver(),Secure.ANDROID_ID); 
			Log.i("Debug",android_id);
			
			Log.i("Debug","Real-time Upload = "+Boolean.toString(real_time));
			Log.i("Debug","Upload Period: "+Long.toString(config.getDataUploadPeriod()));
			Log.i("Debug","Archive Period: "+Long.toString(config.getDataArchivePeriod()));
			
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
	    	
			updateMainActivity();
		}
		
	};
	
	OnClickListener changeSetting = new OnClickListener(){

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			SharedPreferences prefs = getApplicationContext().getSharedPreferences(MainPipeline.MAIN_CONFIG, MODE_PRIVATE);
	    	FunfConfig config  = FunfConfig.getInstance((prefs));	    	
	    	
	    	Intent uploadIntent = new Intent(getBaseContext(), MainPipeline.class);
	    	uploadIntent.setAction(MainPipeline.ACTION_UPLOAD_DATA);
	    	startService(uploadIntent);
			
//			RequestTask req = new RequestTask();
//			Log.i("Debug",req.execute("http://192.168.1.105:9000/rt","hah").toString());
			
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
        
        SharedPreferences prefs = getApplicationContext().getSharedPreferences(MainPipeline.MAIN_CONFIG, MODE_PRIVATE);
    	FunfConfig config  = FunfConfig.getInstance((prefs));
    	
		config.edit().setDataUploadPeriod(FunfConfig.DEFAULT_DATA_UPLOAD_PERIOD).commit();
		config.edit().setDataArchivePeriod(FunfConfig.DEFAULT_DATA_ARCHIVE_PERIOD).commit();
		config.edit().setDataUploadUrl(this.UPLOAD_URL).commit();
    	
	}
	
	public void updateMainActivity(){
		
		SharedPreferences prefs = getApplicationContext().getSharedPreferences(MainPipeline.MAIN_CONFIG, MODE_PRIVATE);
    	FunfConfig config  = FunfConfig.getInstance((prefs));
    	Map<String, Bundle[]> dataRequest = config.getDataRequests();
    	
		Resources resources = getResources();
		String[] probe_list = resources.getStringArray(R.array.probes);	
		
		TextView archivePeriod = (TextView)findViewById(R.id.archivePeriod);
		archivePeriod.setText("Archive Period: "+Long.toString(config.getDataArchivePeriod()) + " seconds");
		
		TextView uploadPeriod = (TextView)findViewById(R.id.uploadPeriod);
		uploadPeriod.setText("Upload Period: "+Long.toString(config.getDataUploadPeriod()) + " seconds");
		
		TextView probeSettingView = (TextView)findViewById(R.id.probeSettings);
		
		String probeSettings = "";
		
		if(dataRequest.size()==0) probeSettings = "All Probes Are OFF!";
		
		for(int i = 0; i < probe_list.length;i++){
			if(dataRequest.containsKey(SettingsActivity.probe_prefix+probe_list[i])){
				Bundle[] params = dataRequest.get(SettingsActivity.probe_prefix+probe_list[i]);
				
				for(Bundle param_set : params){
					probeSettings += probe_list[i].replace("Probe","")+" "+param_set.toString() + "\n";
				}
			}
		}
		
		probeSettingView.setText(probeSettings);
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
		updateMainActivity();
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
