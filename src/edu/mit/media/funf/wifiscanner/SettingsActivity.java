package edu.mit.media.funf.wifiscanner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.michaelnovakjr.numberpicker.NumberPickerPreference;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import edu.mit.media.funf.configured.FunfConfig;
import edu.mit.media.funf.probe.Probe;
import edu.mit.media.funf.probe.builtin.LocationProbe;

public class SettingsActivity extends PreferenceActivity {

	final static String probe_prefix = "edu.mit.media.funf.probe.builtin.";
	final Context context = this;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.layout.pref_activity_settings);
		
		Resources resources = getResources();
		String[] probe_list = resources.getStringArray(R.array.probes);
		
		for(int count = 0; count < probe_list.length; count++){

	        //setup checkbox
			PreferenceCategory pc = (PreferenceCategory)findPreference("container_"+probe_list[count]);
			CheckBoxPreference cb = new CheckBoxPreference(this);
			cb.setKey("checkbox_"+probe_list[count]);			
			cb.setChecked(MainPipeline.isEnabled(getApplicationContext()));
	        cb.setOnPreferenceChangeListener(this.probeCheckBoxListener);
	        cb.setTitle("Enable/Disable");
	        pc.addPreference(cb);

	        //setup period
	        NumberPickerPreference period_npp = (NumberPickerPreference)findPreference("period_"+probe_list[count]);
	        period_npp.setOnPreferenceChangeListener(this.probePeriodListener);
	        
	        //setup duration
	        NumberPickerPreference duration_npp = (NumberPickerPreference)findPreference("duration_"+probe_list[count]);
	        duration_npp.setOnPreferenceChangeListener(this.probeDurationListener);
		}
		
	}
	
	Preference.OnPreferenceChangeListener probeCheckBoxListener = new Preference.OnPreferenceChangeListener() {

		@Override
		public boolean onPreferenceChange(Preference preference, Object isChecked) {
			// TODO Auto-generated method stub
			//Log.i("Debug",preference.getKey());
			String arg = preference.getKey().replace("checkbox_", "");
			
			if(Boolean.parseBoolean(isChecked.toString())) enableProbe(arg);
			else disableProbe(arg);
			
			return true;
		} 
	};
	
	Preference.OnPreferenceChangeListener probePeriodListener = new Preference.OnPreferenceChangeListener() {

		@Override
		public boolean onPreferenceChange(Preference preference, Object period) {
			// TODO Auto-generated method stub
			Log.i("Debug",preference.getKey());
			Log.i("Debug",period.toString());
			
			String probe = preference.getKey().replace("period_","");
			setPeriod(probe,(Integer) period);
			
			return true;
		} 
	};
	
	Preference.OnPreferenceChangeListener probeDurationListener = new Preference.OnPreferenceChangeListener() {

		@Override
		public boolean onPreferenceChange(Preference preference, Object duration) {
			// TODO Auto-generated method stub
			Log.i("Debug",preference.getKey());
			Log.i("Debug",duration.toString());
			
			String probe = preference.getKey().replace("duration_","");
			setDuration(probe,(Integer) duration);
			
			return true;
		} 
	};
	
	public void setPeriod(String probe, Integer period){
		
		SharedPreferences prefs = getApplicationContext().getSharedPreferences(MainPipeline.MAIN_CONFIG, MODE_PRIVATE);
    	FunfConfig config  = FunfConfig.getInstance((prefs));
    	Map<String, Bundle[]> dataRequest = config.getDataRequests();
    	
    	if(dataRequest.containsKey(this.probe_prefix+probe)){
			Bundle[] params = dataRequest.get(this.probe_prefix+probe);
			
			for(Bundle param_set : params){
				if(param_set.containsKey("PERIOD")){
					param_set.remove("PERIOD");
					param_set.putInt("PERIOD", period);
				}
			}
		}
    	
    	config.edit().setDataRequests(dataRequest).commit();
	}
	
	public void setDuration(String probe, Integer duration){
		
		SharedPreferences prefs = getApplicationContext().getSharedPreferences(MainPipeline.MAIN_CONFIG, MODE_PRIVATE);
    	FunfConfig config  = FunfConfig.getInstance((prefs));
    	Map<String, Bundle[]> dataRequest = config.getDataRequests();
    	
    	if(dataRequest.containsKey(this.probe_prefix+probe)){
			Bundle[] params = dataRequest.get(this.probe_prefix+probe);
			
			for(Bundle param_set : params){
				if(param_set.containsKey("DURATION")){
					param_set.remove("DURATION");
					param_set.putInt("DURATION", duration);
				}
			}
		}
    	
    	config.edit().setDataRequests(dataRequest).commit();
	}
	
	public void enableProbe(String probe){
		SharedPreferences prefs = getApplicationContext().getSharedPreferences(MainPipeline.MAIN_CONFIG, MODE_PRIVATE);
    	FunfConfig config  = FunfConfig.getInstance((prefs));
    	Map<String, Bundle[]> dataRequest = config.getDataRequests();
    	
    	Resources resources = getResources();
    	String[] probes = resources.getStringArray(R.array.probes);
    	int[] default_periods = resources.getIntArray(R.array.default_periods);
    	int[] default_durations = resources.getIntArray(R.array.default_durations);

    	//Log.i("Debug",Arrays.asList(probes).toString());
    	//Log.i("Debug",Integer.valueOf(Arrays.asList(probes).indexOf(probe)).toString());
    
    	List<Bundle> tempList = new ArrayList<Bundle>();

    	Bundle params = new Bundle();
    	params.putInt("PERIOD", default_periods[Arrays.asList(probes).indexOf(probe)]); //default value
    	if(default_durations[Arrays.asList(probes).indexOf(probe)]>0)
    		params.putInt("DURATION", default_durations[Arrays.asList(probes).indexOf(probe)]);
    	//Log.i("Debug",period.toString());
    	
    	tempList.add(params);
    	
    	Bundle arrBundle[] = tempList.toArray(new Bundle[tempList.size()]);
    	
    	dataRequest.put(this.probe_prefix+probe, arrBundle);

		config.edit().setDataRequests(dataRequest).commit();
		
	}
	
	public void disableProbe(String probe){
		SharedPreferences prefs = getApplicationContext().getSharedPreferences(MainPipeline.MAIN_CONFIG, MODE_PRIVATE);
    	FunfConfig config  = FunfConfig.getInstance((prefs));
    	Map<String, Bundle[]> dataRequest = config.getDataRequests();   	
    	
    	dataRequest.remove(this.probe_prefix+probe);
		config.edit().setDataRequests(dataRequest).commit();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_settings, menu);
		return true;
	}
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState)
	{
	  super.onSaveInstanceState(savedInstanceState);
	  Log.i("Debug","save");
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState)
	{
	  super.onRestoreInstanceState(savedInstanceState);
	  Log.i("Debug","restore");
	}
	
	@Override
	public void onPause(){
		super.onPause();
	}
	
	@Override
	public void onStop(){
		super.onStop();
		//Log.i("Debug","stop");
		
//		SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
//	    SharedPreferences.Editor editor = sharedPreferences.edit();
//		
//		Resources resources = getResources();
//		String[] probe_list = resources.getStringArray(R.array.probes);
//		
//		for(int count = 0; count < probe_list.length; count++){
//			CheckBox cb = (CheckBox)findViewById(count);
//			//String s = Boolean.toString(cb.isChecked());
//			//Log.i("Debug",s);
//			
//		    editor.putBoolean(probe_list[count], cb.isChecked());
//		    editor.commit();
//		}
	}
	
	@Override
	public void onStart(){
		super.onStart();
		//Log.i("Debug","start");
		
//		SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
//		
//		Resources resources = getResources();
//		String[] probe_list = resources.getStringArray(R.array.probes);
//		
//		for(int count = 0; count < probe_list.length; count++){
//			CheckBox cb = (CheckBox)findViewById(count);
//			//String s = Boolean.toString(sharedPreferences.getBoolean(probe_list[count], false));
//			//Log.i("Debug",s);
//			
//			cb.setChecked(sharedPreferences.getBoolean(probe_list[count], false));
//		}
	}

}

