package edu.harvad.law.librarylab.wtwba;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;


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
            String isbn = null;
            
            try {
				JSONObject myjson = new JSONObject(result);
				Log.w("title from add", myjson.getString("title"));
				title = myjson.getString("title");
				due_date = myjson.getString("due");
				isbn = myjson.getString("isbn");
				
				db.add_item(new Item(this.barcode, title, due_date));
			
				// If we get an isbn back, let's try to download the cover
				if(isbn != null && isbn != ""){
					new DownloadCoverTask(isbn, this.barcode).execute();
				}
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            
            
            // If error, display here
            //TextView text_title = (TextView) findViewById(R.id.title_details);
            //text_title.setText(title);
            
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
    
    private class DownloadCoverTask extends AsyncTask<Void, Void, String> {

    	private String isbn;
    	private String barcode;

        public DownloadCoverTask(String isbn, String barcode)
        {
        	this.isbn = isbn;
            this.barcode = barcode;
        }
    	
    	
        @Override
        protected String doInBackground(final Void... unused) {
        	// Build our HTTP call, pass it off to executePost for execution
            try {

            	String targetURL = "http://covers.openlibrary.org/b/isbn/" + this.isbn + "-M.jpg";
            	String urlParameters = "";
            	
            	
            	
            	return excutePost(targetURL, urlParameters);
            } catch (Exception e) {
            	Log.w("wtwba cover load", e.getMessage());
                return getResources().getString(R.string.connection_error);
            }
        }

        @Override
        protected void onPostExecute(String result) {
            setContentView(R.layout.activity_item_details);
            Log.w("wtwba", result);
            
            
            Intent intent = new Intent(getBaseContext(), MainActivity.class);
    		startActivity(intent);
            
            
        }
        
        
        // Thanks http://www.xyzws.com/Javafaq/how-to-use-httpurlconnection-post-data-to-web-server/139
        public String excutePost(String targetURL, String urlParameters) {
          
          
          try {
        	    
        	    //HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        	  URL url = new URL(targetURL);
        	  URLConnection connection = url.openConnection();
        	  HttpURLConnection httpConnection = (HttpURLConnection) connection;
        	  httpConnection.setRequestMethod("GET");
        	    //urlConnection.setDoOutput(true);
        	  httpConnection.connect();

        	  int code = httpConnection.getResponseCode();
        	  Log.w("ol cover http status", String.valueOf(code));
        	  // Open Library will redirect us to a found cover
        	  if (code == 302){
        	    
        	    FileOutputStream fos = openFileOutput(this.barcode, Context.MODE_PRIVATE);
        	    InputStream inputStream = httpConnection.getInputStream();

        	    byte[] buffer = new byte[1024];
        	    int bufferLength = 0;

        	    while ( (bufferLength = inputStream.read(buffer)) > 0 ) {
        	    	fos.write(buffer, 0, bufferLength);
        	    }
        	    fos.close();
        	    return "okay";
        	  }
        	} catch (MalformedURLException e) {
        	        e.printStackTrace();
        	        
        	} catch (IOException e) {
        	        e.printStackTrace();
        	        
        	}
        	
        	
        return "nope";	
        	
        }
    }

}
    
    
