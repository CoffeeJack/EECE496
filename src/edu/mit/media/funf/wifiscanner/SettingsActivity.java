package edu.mit.media.funf.wifiscanner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import edu.mit.media.funf.configured.FunfConfig;

public class SettingsActivity extends PreferenceActivity {

	final static String probe_prefix = "edu.mit.media.funf.probe.builtin.";
	final Context context = this;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.layout.pref_activity_settings);
		
		PreferenceCategory pc = (PreferenceCategory)findPreference("Probes");
		
		Resources resources = getResources();
		String[] probe_list = resources.getStringArray(R.array.probes);

		for(int count = 0; count < probe_list.length; count++){

			CheckBoxPreference cb = new CheckBoxPreference(this);
	        
			//fill in each checkbox
			cb.setKey(probe_list[count]);
	        cb.setTitle(probe_list[count]);
			cb.setChecked(MainPipeline.isEnabled(getApplicationContext()));
	        cb.setOnPreferenceChangeListener(this.probeCheckBoxListner);
			
	        pc.addPreference(cb);
		}
		
		//NumberPicker np = new NumberPicker(this);
		//container.addView(np);
		
		//NumberPicker np = (NumberPicker)findViewById(R.id.numPicker);
		//np.setCurrent(99);
		//np.setMinValue(0);
		//np.setMaxValue(10);
		
	}
	
	Preference.OnPreferenceChangeListener probeCheckBoxListner = new Preference.OnPreferenceChangeListener() {

		@Override
		public boolean onPreferenceChange(Preference preference, Object isChecked) {
			// TODO Auto-generated method stub
			//Log.i("Debug",preference.getKey());
			
			if(Boolean.parseBoolean(isChecked.toString())) enableProbe(preference.getKey());
			else disableProbe(preference.getKey());
			
			return true;
		} 
	};
	
	
	public void enableProbe(String probe){
		SharedPreferences prefs = getApplicationContext().getSharedPreferences(MainPipeline.MAIN_CONFIG, MODE_PRIVATE);
    	FunfConfig config  = FunfConfig.getInstance((prefs));
    	Map<String, Bundle[]> dataRequest = config.getDataRequests();
    	
    	List<Bundle> tempList = new ArrayList<Bundle>();
    	tempList.add(new Bundle());
    	
    	Bundle arrBundle[] = tempList.toArray(new Bundle[tempList.size()]);
    	
    	dataRequest.put(this.probe_prefix+probe, arrBundle);
		config.edit().setDataRequests(dataRequest).commit();
	}
	
	public void disableProbe(String probe){
		SharedPreferences prefs = getApplicationContext().getSharedPreferences(MainPipeline.MAIN_CONFIG, MODE_PRIVATE);
    	FunfConfig config  = FunfConfig.getInstance((prefs));
    	Map<String, Bundle[]> dataRequest = config.getDataRequests();
    	
    	List<Bundle> tempList = new ArrayList<Bundle>();
    	tempList.add(new Bundle());
    	
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
		Log.i("Debug","stop");
		
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
		Log.i("Debug","start");
		
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

