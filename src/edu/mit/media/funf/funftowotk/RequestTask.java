package edu.mit.media.funf.funftowotk;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


import android.os.AsyncTask;
import android.util.Log;
import android.widget.Button;

class RequestTask extends AsyncTask<String, String, String>{

    @Override
    protected String doInBackground(String... uri) {
    	URL url;
        HttpURLConnection connection = null;  
        String urlParameters;
        String method = uri[1];
        String content_type = uri[2];
        
        if(uri[3]!=null) urlParameters = uri[3];
        else urlParameters = "";
        
        try {
          //Create connection
          url = new URL(uri[0]);
          connection = (HttpURLConnection)url.openConnection();
          connection.setRequestMethod(method);
          connection.setRequestProperty("Content-Type", content_type);
    			
          connection.setRequestProperty("Content-Length", "" + Integer.toString(urlParameters.getBytes().length));
          connection.setRequestProperty("Content-Language", "en-US");  
    	
          connection.setConnectTimeout(5 * 1000);
          connection.setReadTimeout(5 * 1000);
          
          connection.setUseCaches (false);
          connection.setDoInput(true);
          connection.setDoOutput(true);

          //Send request
          DataOutputStream wr = new DataOutputStream (connection.getOutputStream ());
          wr.writeBytes(urlParameters);
          wr.flush ();
          wr.close ();

          //Get Response	
          Integer response_code = connection.getResponseCode();
          
          return Integer.toString(response_code);
          
          //return "request sent!";
          
        } catch (Exception e) {

          e.printStackTrace();
          //return null;
          return "timeout!";

        } finally {

          if(connection != null) {
            connection.disconnect(); 
          }
        }
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        //Do anything with response..
        Log.i("HTTP",result);   
        
        if(result=="timeout!") MainActivity.serverStatus = "NOT RESPONDING";
        else MainActivity.serverStatus = "ALIVE & WELL";
        
    }
}