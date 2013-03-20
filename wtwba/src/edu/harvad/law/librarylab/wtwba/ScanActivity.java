package edu.harvad.law.librarylab.wtwba;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class ScanActivity extends Activity {

	public final static String EXTRA_MESSAGE = "edu.harvad.law.librarylab.wtwba.MESSAGE";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent intent = new Intent("com.google.zxing.client.android.SCAN");
		// intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
		startActivityForResult(intent, 0);

	}

	@Override
	public void onResume() {
		super.onResume();
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
	}

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
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
}