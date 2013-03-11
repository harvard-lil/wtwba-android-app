package edu.harvad.law.librarylab.wtwba;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class LocationActivity extends ListActivity {

	public static final String PREFS_NAME = "MyPrefsFile";
	
	static final String[] LOCATIONS = new String[] { "Cafe Off-Campus", "Cafe On-Campus", "Home", "Library", 
		"Other Off-Campus", "Other On-Campus", "Student Lounge", "Work"};
	String selected_location;
	static String barcode; 
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        
        Bundle bundle = getIntent().getExtras();
        this.barcode = bundle.getString("barcode");

      
     		setListAdapter(new ArrayAdapter<String>(this, R.layout.activity_location,LOCATIONS));
      
     		ListView listView = getListView();
     		listView.setTextFilterEnabled(true);
      
     	

    }

    @Override 
    public void onListItemClick(ListView l, View v, int position, long id) {
        
    	// get value of location from list
    	// if web connection, send it to the receive service
    	
    	String location = LOCATIONS[position];

    	
		Log.d("from listview: ", location);

    	
    	
    	ConnectivityManager connMgr = (ConnectivityManager) 
    	        getSystemService(Context.CONNECTIVITY_SERVICE);
    	        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
    	        if (networkInfo != null && networkInfo.isConnected()) {
    	            
    	            
    	            DatabaseHandler db = new DatabaseHandler(this);
    	            new DownloadHtmlTask(barcode, location, db).execute();
    	            /**
    	             * CRUD Operations
    	             * */
    	            // Inserting Contacts
    	            Log.d("Insert: ", "Inserting ..");
    	            //db.add_entry(new Entry("junkbarcode", "junklocation"));
    	        } else {
    	            //textView.setText("No network connection available.");
    	        }
    	
    	
    }
    
    
 // Implementation of AsyncTask used to download XML feed from stackoverflow.com.
    private class DownloadHtmlTask extends AsyncTask<Void, Void, String> {

    	private String barcode;
    	private String location;
        private DatabaseHandler db;
        

        public DownloadHtmlTask(String barcode, String location, DatabaseHandler db)
        {
            this.barcode = barcode;
            this.location = location;
            this.db = db;
        }
    	
    	
        @Override
        protected String doInBackground(final Void... unused) {
            try {

            	String targetURL = "http://librarylab.law.harvard.edu/dev/matt/public/wtwba/receive.php";
            	String urlParameters = null;
            	
            	SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                String user_name = settings.getString("user_name", "");
                
                Date dt = new java.util.Date();
                SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                String currentTime = sdf.format(dt);
            	
            	try {
        			urlParameters = "user=" + URLEncoder.encode(user_name, "UTF-8") +
        							"&barcode=" + URLEncoder.encode(this.barcode, "UTF-8") +
        							"&location=" + URLEncoder.encode(this.location, "UTF-8") + 
        							"&date=" + URLEncoder.encode(currentTime, "UTF-8");
        		} catch (UnsupportedEncodingException e1) {
        			e1.printStackTrace();
        		}
            	
            	Log.w("wtwba", urlParameters);
            	
            	return excutePost(targetURL, urlParameters);
            } catch (Exception e) {
            	Log.w("wtwba", e.getMessage());
                return getResources().getString(R.string.connection_error);
            }
        }

        @Override
        protected void onPostExecute(String result) {
        	

    	    Intent intent = new Intent(getBaseContext(), MainActivity.class);
    		startActivity(intent);
        	
        	
        }
        
        
        // Thanks http://www.xyzws.com/Javafaq/how-to-use-httpurlconnection-post-data-to-web-server/139
        public String excutePost(String targetURL, String urlParameters)
        {
          URL url;
          HttpURLConnection connection = null;
          
          try {
            //Create connection
            url = new URL(targetURL);
            connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", 
                 "application/x-www-form-urlencoded");
      			
            connection.setRequestProperty("Content-Length", "" + 
                     Integer.toString(urlParameters.getBytes().length));
            connection.setRequestProperty("Content-Language", "en-US");  
      			
            connection.setUseCaches (false);
            connection.setDoInput(true);
            connection.setDoOutput(true);

            //Send request
            DataOutputStream wr = new DataOutputStream (
                        connection.getOutputStream ());
            wr.writeBytes (urlParameters);
            wr.flush ();
            wr.close ();

            //Get Response	
            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuffer response = new StringBuffer(); 
            while((line = rd.readLine()) != null) {
              response.append(line);
              response.append('\r');
            }
            rd.close();
            
            Log.w("wtwba", response.toString());
            return response.toString();

          } catch (Exception e) {

            e.printStackTrace();
            return null;

          } finally {

            if(connection != null) {
              connection.disconnect(); 
            }
          }
        }
    }
}