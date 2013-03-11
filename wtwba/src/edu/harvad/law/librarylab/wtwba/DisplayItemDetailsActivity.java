package edu.harvad.law.librarylab.wtwba;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;


public class DisplayItemDetailsActivity extends Activity {
	
	public static final String PREFS_NAME = "MyPrefsFile";
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Bundle bundle = getIntent().getExtras();
        String barcode = bundle.getString("barcode");
        
     // Gets the URL from the UI's text field.
        ConnectivityManager connMgr = (ConnectivityManager) 
        getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            
            
            DatabaseHandler db = new DatabaseHandler(this);
            new DownloadHtmlTask(barcode, db).execute();
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
        private DatabaseHandler db;

        public DownloadHtmlTask(String barcode, DatabaseHandler db)
        {
            this.barcode = barcode;
            this.db = db;
        }
    	
    	
        @Override
        protected String doInBackground(final Void... unused) {
            try {

            	String targetURL = "http://librarylab.law.harvard.edu/dev/matt/public/wtwba/add.php";
            	String urlParameters = null;
            	
            	SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                String user_name = settings.getString("user_name", "");
            	
            	try {
        			urlParameters = "user=" + URLEncoder.encode(user_name, "UTF-8") +
        							"&barcode=" + URLEncoder.encode(this.barcode, "UTF-8");
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
            setContentView(R.layout.activity_item_details);
            
            String title = null;
            String due_date = null;
            
            try {
				JSONObject myjson = new JSONObject(result);
				Log.w("title from add", myjson.getString("title"));
				title = myjson.getString("title");
				due_date = myjson.getString("due");
				
				db.add_item(new Item(this.barcode, title, due_date));
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            
            TextView text_title = (TextView) findViewById(R.id.title_details);
            text_title.setText(title);
            
            
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