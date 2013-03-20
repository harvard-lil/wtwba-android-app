package edu.harvad.law.librarylab.wtwba;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import android.os.AsyncTask;
import android.util.Log;

// Implementation of AsyncTask used to download XML feed from stackoverflow.com.
class SendEntryTask extends AsyncTask<Void, Void, String> {

	public static final String PREFS_NAME = "MyPrefsFile";

	private String barcode;
	private String location;
	private String timestamp;
	private String user_name;
	private DatabaseHandler db;

	public SendEntryTask(String barcode, String location, String timestamp,
			String user_name) {
		this.barcode = barcode;
		this.location = location;
		this.timestamp = timestamp;
		this.user_name = user_name;
	}

	@Override
	protected String doInBackground(final Void... unused) {
		try {

			String targetURL = "http://librarylab.law.harvard.edu/dev/matt/public/wtwba/receive.php";
			String urlParameters = null;

			try {
				urlParameters = "user="
						+ URLEncoder.encode(this.user_name, "UTF-8")
						+ "&barcode="
						+ URLEncoder.encode(this.barcode, "UTF-8")
						+ "&location="
						+ URLEncoder.encode(this.location, "UTF-8") + "&date="
						+ URLEncoder.encode(this.timestamp, "UTF-8");
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}

			return excutePost(targetURL, urlParameters);
		} catch (Exception e) {
			Log.w("wtwba", e.getMessage());
			return "connection error";
		}
	}

	@Override
	protected void onPostExecute(String result) {

	}

	// Thanks
	// http://www.xyzws.com/Javafaq/how-to-use-httpurlconnection-post-data-to-web-server/139
	public String excutePost(String targetURL, String urlParameters) {
		URL url;
		HttpURLConnection connection = null;

		try {
			// Create connection
			url = new URL(targetURL);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");

			connection.setRequestProperty("Content-Length",
					"" + Integer.toString(urlParameters.getBytes().length));
			connection.setRequestProperty("Content-Language", "en-US");

			connection.setUseCaches(false);
			connection.setDoInput(true);
			connection.setDoOutput(true);

			// Send request
			DataOutputStream wr = new DataOutputStream(
					connection.getOutputStream());
			wr.writeBytes(urlParameters);
			wr.flush();
			wr.close();

			// Get Response
			InputStream is = connection.getInputStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));
			String line;
			StringBuffer response = new StringBuffer();
			while ((line = rd.readLine()) != null) {
				response.append(line);
				response.append('\r');
			}
			rd.close();

			return response.toString();

		} catch (Exception e) {

			e.printStackTrace();
			return null;

		} finally {

			if (connection != null) {
				connection.disconnect();
			}
		}
	}
}