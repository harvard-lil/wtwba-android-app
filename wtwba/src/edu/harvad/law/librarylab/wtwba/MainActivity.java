package edu.harvad.law.librarylab.wtwba;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

public class MainActivity extends ListActivity implements OnItemClickListener {

	public final static String EXTRA_MESSAGE = "edu.harvad.law.librarylab.wtwba.MESSAGE";
	public static final String PREFS_NAME = "MyPrefsFile";


	ListView item_list;
	ArrayList<ItemDetailsInList> items_for_view;

	DatabaseHandler db;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.activity_main_menu, menu);
		return true;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {

		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		this.db = new DatabaseHandler(this);
	}

	/*
	 * @Override public boolean onCreateOptionsMenu(Menu menu) {
	 * getMenuInflater().inflate(R.menu.activity_main, menu); return true; }
	 */

	@Override
	public void onResume() {
		// Draw the list of already checked out items
		super.onResume();

		// If the user hasn't set a key, don't let them do anything (other than
		// set a key)
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		String user_name = settings.getString("user_name", "");

		if (user_name == null || user_name.length() == 0) {
			this.manageUser();
		}

		setContentView(R.layout.activity_main);

		// If we have any entries (checkins), send them out
		this.send_stored_entries(user_name);

		List<Item> items = this.db.get_all_items();

		items_for_view = new ArrayList<ItemDetailsInList>();

		for (Item item : items) {
			ItemDetailsInList idil = new ItemDetailsInList();
			idil.setIcon(R.drawable.ic_launcher);
			idil.setTitle(item.get_title());

			idil.setDue(item.due_date);
			idil.setBarcode(item.get_barcode());

			String last = item.get_last_used();
												

			if (last == null) {
				last = "Never used";
			} else {
				last = "Last used " + last;
			}
			idil.setLast(last);
			items_for_view.add(idil);
		}

		if (0 == items_for_view.size()) {
			ItemDetailsInList idil = new ItemDetailsInList();
			idil.setIcon(R.drawable.ic_launcher);
			idil.setTitle("Add items using menu above");
			// idil.setDue("welcome_date");
			idil.setBarcode("welcome_barcode");
			idil.setLast("");
			items_for_view.add(idil);
		}

		item_list = getListView();
		item_list.setOnItemClickListener(this);
		item_list.setLongClickable(true);
		item_list.setAdapter(new ItemDetailsListAdapter(items_for_view, this));

		item_list.setOnItemLongClickListener(new OnItemLongClickListener() {

			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int pos, long id) {
				ItemDetailsInList item = items_for_view.get(pos);

				// One exception: when the list is empty, we display a
				// non-clickable welcome entry
				if (item.barcode != "welcome_barcode") {
					Intent intent = new Intent(getBaseContext(),
							DeleteItemActivity.class);
					intent.putExtra("barcode", item.barcode);
					intent.putExtra("title", item.title);
					startActivity(intent);
				}
				return true;
			}
		});
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection from the menu
		switch (item.getItemId()) {
		case R.id.scanButton:
			this.scanCode();
			return true;
		case R.id.typeButton:
			this.typeCode();
			return true;
		case R.id.manageUserButton:
			this.manageUser();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/** Called when the user clicks the scan button */
	public void scanCode() {
		// When user selects the Add Item option from the menu

		Intent intent = new Intent("com.google.zxing.client.android.SCAN");
		// intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
		startActivityForResult(intent, 0);
	}

	/** Called when the user clicks the type button */
	public void typeCode() {
		// When user selects the Add Item using barcode option from the menu

		Intent intent = new Intent(this, TypeBarcodeActivity.class);
		startActivity(intent);
	}

	/** Called when the user clicks the manage user button (in the menu) */
	public void manageUser() {

		Intent intent = new Intent(this, ManageUserActivity.class);
		startActivity(intent);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// When a user clicks an item (a book they've already scanned) in the
		// list
		ItemDetailsInList item = items_for_view.get(arg2);

		// One exception: when the list is empty, we display a non-clickable
		// welcome entry
		if (item.barcode != "welcome_barcode") {
			Intent intent = new Intent(this, LocationActivity.class);
			intent.putExtra("barcode", item.barcode);
			startActivity(intent);
		}

	}

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		// When our zxing intent returns, we call this method

		String message = "no message";

		if (requestCode == 0) {
			if (resultCode == RESULT_OK) {
				String contents = intent.getStringExtra("SCAN_RESULT");
				String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
				// Handle successful scan

				message = contents;

				Intent display_intent = new Intent(this,
						DisplayItemDetailsActivity.class);
				display_intent.putExtra("barcode", message);
				startActivity(display_intent);

			} else if (resultCode == RESULT_CANCELED) {
				// Handle cancel
				message = "canceled";
			}
		} else {

			message = "requestcode not 0";
		}

	}

	private void send_stored_entries(String user_name) {
		// If we have any stored checkins, send them if we have a network connection
		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {

			List<Entry> entries = this.db.get_all_entries();

			for (Entry entry : entries) {
				String bc = entry.get_barcode();
				String loc = entry.get_location();
				String d = entry.getDate();

				new SendEntryTask(bc, loc, d, user_name).execute();
				db.delete_entry(entry);
			}

		}
	}
}