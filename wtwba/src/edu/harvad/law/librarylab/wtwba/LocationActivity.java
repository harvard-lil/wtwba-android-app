package edu.harvad.law.librarylab.wtwba;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class LocationActivity extends ListActivity {

	// When a user registers a use (of an already added item), they select
	// where they're using it. That's what we handle here. We get the location.
	// If we have a network connection, we send it off to the datastore. If not,
	// we store it locally. (Our main will send it out once we do receive a network connection)
	
	public static final String PREFS_NAME = "MyPrefsFile";

	String selected_location;
	static String barcode;

	DatabaseHandler db = new DatabaseHandler(this);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle bundle = getIntent().getExtras();
		this.barcode = bundle.getString("barcode");

		String[] locations = db.get_all_locations();

		setListAdapter(new ArrayAdapter<String>(this,
				R.layout.activity_location, locations));

		ListView listView = getListView();
		listView.setTextFilterEnabled(true);

	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {

		// get value of location from list
		// if web connection, send it to the receive service

		TextView text_title = (TextView) l.getChildAt(position);

		String location = (String) text_title.getText();

		// We store the last checked in time for each barcode
		Date now = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, h:mm a");
		String strDate = sdf.format(now);

		Item item = this.db.get_item_by_barcode(this.barcode);
		item.set_last_used(strDate);
		int num_uses = item.get_num_uses();
		num_uses = num_uses + 1;
		item.set_num_uses(num_uses);

		db.update_item(item);
		db.update_location(location);

		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		String user_name = settings.getString("user_name", "");

		SimpleDateFormat db_friendly_format = new java.text.SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");

		String db_friendly_time = db_friendly_format.format(now);

		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			new SendEntryTask(barcode, location, db_friendly_time, user_name).execute();

		} else {
			// No network, so stick in in the db for now and we'll send it when
			// we get a network connection
			db.add_entry(new Entry(barcode, location, db_friendly_time));
		}

		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
	}
}