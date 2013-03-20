package edu.harvad.law.librarylab.wtwba;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ItemDetailsListAdapter extends BaseAdapter {
	
	// This maps our list if itemdetailsinlist to view objects.

	public static final String PREFS_NAME = "MyPrefsFile";

	private ArrayList<ItemDetailsInList> _data;
	Context _c;

	ItemDetailsListAdapter(ArrayList<ItemDetailsInList> data, Context c) {
		_data = data;
		_c = c;
	}

	public int getCount() {
		return _data.size();
	}

	public Object getItem(int position) {
		return _data.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater) _c
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.list_item_details, null);
		}

		ImageView image = (ImageView) v.findViewById(R.id.icon);
		TextView titleView = (TextView) v.findViewById(R.id.title_list);
		// TextView dueView = (TextView)v.findViewById(R.id.due_list);
		TextView barcodeView = (TextView) v.findViewById(R.id.barcode_list);
		TextView lastView = (TextView) v.findViewById(R.id.last_list);


		ItemDetailsInList msg = _data.get(position);

		// image.setImageResource(msg.icon);

		FileInputStream in;
		try {
			in = _c.getApplicationContext().openFileInput(msg.barcode);
			image.setImageBitmap(BitmapFactory.decodeStream(in));
		} catch (FileNotFoundException e) {
			image.setImageResource(R.drawable.cover_default);
			// e.printStackTrace();
		}

		String due_message;


		if (msg.due == "welcome_date") {
			// When our item list is empty, we display a welcome message. it
			// doesn't get a due date.
			due_message = "";
		} else if (msg.due != null && msg.due.length() > 0) {
			due_message = "Due on " + msg.due;
		} else {
			due_message = "No due date found";
		}

		titleView.setText(msg.title);
		// dueView.setText(due_message);
		barcodeView.setText(msg.barcode);
		lastView.setText(msg.last);

		return v;
	}
}