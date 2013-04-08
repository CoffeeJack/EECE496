package edu.mit.media.funf.funftowotk;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
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
import edu.mit.media.funf.probe.builtin.AccelerometerSensorProbe;
import edu.mit.media.funf.probe.builtin.ActivityProbe;
import edu.mit.media.funf.probe.builtin.CallLogProbe;
import edu.mit.media.funf.probe.builtin.LocationProbe;
import edu.mit.media.funf.probe.builtin.SMSProbe;
import edu.mit.media.funf.probe.builtin.WifiProbe;
import edu.mit.media.funf.funftowotk.R;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
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
	/**********CONFIGURATION**************/
	public static boolean real_time;
	
	static final String ROOT_URL = "http://142.103.25.45:9000/";
	//static final String ROOT_URL = "http://192.168.1.102:9000/";
	
	static final String REAL_TIME_UPLOAD_URL = ROOT_URL+"rt";
	static final String UPLOAD_URL = ROOT_URL+"upload";
	static final String CHECK_STATUS_URL = ROOT_URL+"status";
	
	static final Integer archive_period = 3600;
	
	public static String serverStatus = "SENSING...";
	
	private static int server_check_interval = 30000; //30 seconds
	
	private Handler m_handler;
	
	/**********CODES**************/
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// TODO Auto-generated method stub	
		
		init();
		
		setContentView(R.layout.main);	
    	
        final Context context = this;
        
        updateMainActivity();
        
        //Enable/Disable Real-time upload
        CheckBox enableRealTime = (CheckBox)findViewById(R.id.enableRealTime);
        enableRealTime.setOnCheckedChangeListener(setRealTime);
        
        //Get current config
        Button getConfigButton = (Button)findViewById(R.id.getConfigButton);
        getConfigButton.setOnClickListener(new OnClickListener(){
        	@Override
            public void onClick(View v) {
        		
        		try{
        			Intent intent = new Intent(getBaseContext(),SettingsActivity.class);
            		startActivity(intent);
        		}catch(Exception e){
        			Log.e("ERROR",e.getMessage());
        		}
        		
        	}
        });
        
//		Button listProbeButton = (Button)findViewById(R.id.listProbeButton);
//		listProbeButton.setOnClickListener(listProbes);
		
		Button changeSettingButton = (Button)findViewById(R.id.changeSettingButton);
		changeSettingButton.setOnClickListener(changeSetting);
		
//		Button resetButton = (Button)findViewById(R.id.resetButton);
//		resetButton.setOnClickListener(reset);
		
		Button exitButton = (Button)findViewById(R.id.exitButton);
		exitButton.setOnClickListener(exit);
		
		//start the timer
		//DO NOT MOVE THIS EARLIER, it needs the views to be created first
		m_statusChecker.run(); 
	}
	
	/**********LISTENERS**************/
	
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
			
			try{
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
			}catch(Exception e){
				Log.e("ERROR",e.getMessage());
			}
		}
		
	};
	
	OnClickListener changeSetting = new OnClickListener(){

		@Override
		public void onClick(View v) {
			
			try{
				// TODO Auto-generated method stub
				SharedPreferences prefs = getApplicationContext().getSharedPreferences(MainPipeline.MAIN_CONFIG, MODE_PRIVATE);
		    	FunfConfig config  = FunfConfig.getInstance((prefs));	    	
		    	
		    	Intent uploadIntent = new Intent(getBaseContext(), MainPipeline.class);
		    	uploadIntent.setAction(MainPipeline.ACTION_UPLOAD_DATA);
		    	startService(uploadIntent);			
				
				Log.i("Debug","Uploading...");
			}catch(Exception e){
				Log.e("ERROR",e.getMessage());
			}
		}
		
	};
	
	OnClickListener reset = new OnClickListener(){

		@Override
		public void onClick(View v) {
			try{
				// TODO Auto-generated method stub
				Log.i("Debug","RESET");
				init();
			}catch(Exception e){
				Log.e("ERROR",e.getMessage());
			}
		}
		
	};
	
	OnClickListener exit = new OnClickListener(){

		@Override
		public void onClick(View v) {
			
			try{
				// TODO Auto-generated method stub				
				
				turnOffAllProbes();
				turnOffPipeline();
				while(!checkAllProbesAreOff());
				finish();
				
			}catch(Exception e){
				Log.e("ERROR",e.getMessage());
			}
		}
		
	};
	
	/***********INITIALIZATION****************/
	
	public void init(){
		
		try{
			Intent archiveIntent = new Intent(getBaseContext(), MainPipeline.class);
	        archiveIntent.setAction(MainPipeline.ACTION_ENABLE);
	        startService(archiveIntent);
	        
	        SharedPreferences prefs = getApplicationContext().getSharedPreferences(MainPipeline.MAIN_CONFIG, MODE_PRIVATE);
	    	FunfConfig config  = FunfConfig.getInstance((prefs));
	    	
			//config.edit().setDataUploadPeriod(FunfConfig.DEFAULT_DATA_UPLOAD_PERIOD).commit();
			//config.edit().setDataArchivePeriod(FunfConfig.DEFAULT_DATA_ARCHIVE_PERIOD).commit();
	    	config.edit().setDataUploadPeriod(this.archive_period).commit(); //hourly
			config.edit().setDataArchivePeriod(this.archive_period).commit();
			config.edit().setDataUploadUrl(this.UPLOAD_URL).commit();
			
			//setup timer
			m_handler = new Handler();
			
		}catch(Exception e){
			Log.e("ERROR",e.getMessage());
		}
	}
	
	Runnable m_statusChecker = new Runnable()
	{
	     @Override 
	     public void run() {
	          updateStatus(); //this function can change value of m_interval.
	          m_handler.postDelayed(m_statusChecker, server_check_interval);
	     }
	};
	
	public void updateMainActivity(){
		
		try{
			SharedPreferences prefs = getApplicationContext().getSharedPreferences(MainPipeline.MAIN_CONFIG, MODE_PRIVATE);
	    	FunfConfig config  = FunfConfig.getInstance((prefs));
	    	Map<String, Bundle[]> dataRequest = config.getDataRequests();
	    	
			Resources resources = getResources();
			String[] probe_list = resources.getStringArray(R.array.probes);	
			
			TextView android_ID = (TextView)findViewById(R.id.androidID);
			android_ID.setText("Android ID: "+Secure.getString(getBaseContext().getContentResolver(),Secure.ANDROID_ID));
			
			TextView archivePeriod = (TextView)findViewById(R.id.archivePeriod);
			archivePeriod.setText("Archive Period: "+Long.toString(config.getDataArchivePeriod()) + " seconds");
			
			TextView uploadPeriod = (TextView)findViewById(R.id.uploadPeriod);
			uploadPeriod.setText("Upload Period: "+Long.toString(config.getDataUploadPeriod()) + " seconds");
			
			TextView uploadURL = (TextView)findViewById(R.id.uploadURL);
			if(config.getDataUploadUrl()!=null) uploadURL.setText("Upload URL: "+this.UPLOAD_URL);
			else uploadURL.setText("Upload URL: NOT SET");
			
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
			
		}catch(Exception e){
			Log.e("ERROR",e.getMessage());
		}
	}
	
	public void updateStatus(){
		//Log.i("Debug","YES");
		try{
			String data = "";
			RequestTask rq = new RequestTask();	
			rq.execute(this.CHECK_STATUS_URL,"POST","text",data); //get doesn't work
			
			TextView ss = (TextView)findViewById(R.id.serverStatus);
			ss.setText("SERVER STATUS: "+this.serverStatus);
			
		}catch(Exception e){
			Log.e("ERROR",e.getMessage());
		}
		
	}
	
	public void turnOffAllProbes(){
		try{			
			Resources resources = getResources();
			String[] probe_list = resources.getStringArray(R.array.probes);	
			
			for(int i = 0; i < probe_list.length;i++){
				turnOffProbe(probe_list[i]);
			}
			
		}catch(Exception e){
			Log.e("ERROR",e.getMessage());
		}	
	}
	
	public void turnOffProbe(String probe){
		
		SharedPreferences prefs = getApplicationContext().getSharedPreferences(MainPipeline.MAIN_CONFIG, MODE_PRIVATE);
    	FunfConfig config  = FunfConfig.getInstance((prefs));
    	Map<String, Bundle[]> dataRequest = config.getDataRequests();   	
    	
    	dataRequest.remove(SettingsActivity.probe_prefix+probe);
		config.edit().setDataRequests(dataRequest).commit();
		
	}
	
	public Boolean checkAllProbesAreOff(){
		
		Resources resources = getResources();
		String[] probe_list = resources.getStringArray(R.array.probes);
		
		SharedPreferences prefs = getApplicationContext().getSharedPreferences(MainPipeline.MAIN_CONFIG, MODE_PRIVATE);
    	FunfConfig config  = FunfConfig.getInstance((prefs));
    	Map<String, Bundle[]> dataRequest = config.getDataRequests();
		
		for(int i = 0; i < probe_list.length;i++){
			if(dataRequest.containsKey(SettingsActivity.probe_prefix+probe_list[i])){
				return false;
			}
		}
		
		return true;
	}
	
	public void turnOffPipeline(){
		
		stopService(new Intent(this, MainPipeline.class));
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
	
	private void saveState(){
		SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
	    SharedPreferences.Editor editor = sharedPreferences.edit();
	    
	    CheckBox realtime_enable = (CheckBox)findViewById(R.id.enableRealTime); 
	    Boolean state = realtime_enable.isChecked();
	    //Log.i("Debug","RT "+state.toString());
	    editor.putBoolean("RT_enabled",state);
	    editor.commit();
	    
	}
	
	private void loadState(){
		SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
		Boolean state = sharedPreferences.getBoolean("RT_enabled", false);
		//Log.i("Debug","RT "+state.toString());
		CheckBox realtime_enable = (CheckBox)findViewById(R.id.enableRealTime); 
		realtime_enable.setChecked(state);
		this.real_time = state;
	}
	

	/* (non-Javadoc)
	 * @see android.app.Activity#onStart()
	 */
	@Override
	public void onStart() {
		super.onStart();
		// TODO Auto-generated method stub
		
		Log.i("Main","Start");
		loadState();
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
		loadState();

	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onPause()
	 */
	@Override
	public void onPause() {
		super.onPause();
		// TODO Auto-generated method stub
		
		Log.i("Main","Pause");
		saveState();

	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onStop()
	 */
	@Override
	public void onStop() {
		super.onStop();
		// TODO Auto-generated method stub
		
		Log.i("Main","Stop");
		saveState();
		
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onDestroy()
	 */
	@SuppressLint("NewApi")
	@Override
	public void onDestroy() {
		super.onDestroy();
		// TODO Auto-generated method stub
		Log.i("Main","Destroy");
		
		//shutDown();
		
		//kill background process
//		ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
//		activityManager.killBackgroundProcesses("edu.mit.media.funf.funftowotk");
		
		//kill self
//		android.os.Process.killProcess(android.os.Process.myPid());
		//shutDown();

	}
	
	

}
