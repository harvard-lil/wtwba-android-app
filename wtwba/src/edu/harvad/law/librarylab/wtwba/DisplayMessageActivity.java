package edu.harvad.law.librarylab.wtwba;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;


public class DisplayMessageActivity extends Activity {
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

     // Gets the URL from the UI's text field.
//        String stringUrl = urlText.getText().toString();
//        ConnectivityManager connMgr = (ConnectivityManager) 
//            getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
//        if (networkInfo != null && networkInfo.isConnected()) {
            new DownloadHtmlTask().execute("http://google.com");
//        } else {
//            textView.setText("No network connection available.");
//        }
        
    }
	
	
	// Implementation of AsyncTask used to download XML feed from stackoverflow.com.
    private class DownloadHtmlTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            try {
            	
            	Log.w("wtwba", urls[0]);
            	
                return loadHtmlFromNetwork(urls[0]);
            } catch (IOException e) {
            	Log.w("wtwba", e.getMessage());
                return getResources().getString(R.string.connection_error);
            }
        }

        @Override
        protected void onPostExecute(String result) {
            setContentView(R.layout.activity_item_details);
            
            TextView text = (TextView) findViewById(R.id.TextView01);
            text.setText(result);
        	
        }
        
     // Uploads XML from stackoverflow.com, parses it, and combines it with
        // HTML markup. Returns HTML string.
        private String loadHtmlFromNetwork(String urlString) throws IOException {
            InputStream stream = null;
            String line = "";
            StringBuilder total = new StringBuilder();

            
            try {
                stream = downloadUrl(urlString);
                
             // Wrap a BufferedReader around the InputStream
                BufferedReader rd = new BufferedReader(new InputStreamReader(stream));

                // Read response until the end
                while ((line = rd.readLine()) != null) { 
                    total.append(line); 
                }
                
                

            // Makes sure that the InputStream is closed after the app is
            // finished using it.
            } finally {
                if (stream != null) {
                    stream.close();
                }
            }

            
                        
            // Return full string
            return total.toString();
            //return stream.toString();
        }

        // Given a string representation of a URL, sets up a connection and gets
        // an input stream.
        private InputStream downloadUrl(String urlString) throws IOException {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            InputStream stream = conn.getInputStream();
            
        	Log.w("wtwba", stream.toString());

            
            return stream;
        }
    }
    
    
}