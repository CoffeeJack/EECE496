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

public class SettingsActivity extends Activity {

	final static String probe_prefix = "edu.mit.media.funf.probe.builtin.";
	final Context context = this;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		
		LinearLayout container = (LinearLayout)findViewById(R.id.settingsContainer);
		Resources resources = getResources();
		String[] probe_list = resources.getStringArray(R.array.probes);
		
		for(int count = 0; count < probe_list.length; count++){
			String tag = probe_list[count];
			
			CheckBox cb = new CheckBox(this);
			cb.setText(probe_list[count]);
			//cb.setChecked(MainPipeline.isEnabled(getApplicationContext()));
			cb.setOnCheckedChangeListener(this.checkUpdate);
			
			cb.setId(count);
			//cb.setId(probe_ids[count]);
			cb.setTag(tag);
			
			container.addView(cb);			
			
		}
		
		//NumberPicker np = new NumberPicker(this);
		//container.addView(np);
		
		//NumberPicker np = (NumberPicker)findViewById(R.id.numPicker);
		//np.setCurrent(99);
		//np.setMinValue(0);
		//np.setMaxValue(10);
		
		Button listProbeButton = (Button)findViewById(R.id.listProbeButton);
		listProbeButton.setOnClickListener(listProbes);
	}

	OnCheckedChangeListener checkUpdate = new OnCheckedChangeListener(){

		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			// TODO Auto-generated method stub
			String tag = (String) buttonView.getTag();
			
			//Log.i("Debug",tag);
			
			if(isChecked) enableProbe(tag);
			else disableProbe(tag);
		}
		
	};
	
	OnClickListener listProbes = new OnClickListener(){

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			SharedPreferences prefs = getApplicationContext().getSharedPreferences(MainPipeline.MAIN_CONFIG, MODE_PRIVATE);
	    	FunfConfig config  = FunfConfig.getInstance((prefs));
	    	Map<String, Bundle[]> dataRequest = config.getDataRequests();
	    	
	    	Log.i("Debug",dataRequest.toString());	
	    	
	    	//Intent intent = new Intent(getBaseContext(),PrefActivity.class);
	    	//startActivity(intent);
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
		
		SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
	    SharedPreferences.Editor editor = sharedPreferences.edit();
		
		Resources resources = getResources();
		String[] probe_list = resources.getStringArray(R.array.probes);
		
		for(int count = 0; count < probe_list.length; count++){
			CheckBox cb = (CheckBox)findViewById(count);
			//String s = Boolean.toString(cb.isChecked());
			//Log.i("Debug",s);
			
		    editor.putBoolean(probe_list[count], cb.isChecked());
		    editor.commit();
		}
	}
	
	@Override
	public void onStart(){
		super.onStart();
		Log.i("Debug","start");
		
		SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
		
		Resources resources = getResources();
		String[] probe_list = resources.getStringArray(R.array.probes);
		
		for(int count = 0; count < probe_list.length; count++){
			CheckBox cb = (CheckBox)findViewById(count);
			//String s = Boolean.toString(sharedPreferences.getBoolean(probe_list[count], false));
			//Log.i("Debug",s);
			
			cb.setChecked(sharedPreferences.getBoolean(probe_list[count], false));
		}
	}

}

