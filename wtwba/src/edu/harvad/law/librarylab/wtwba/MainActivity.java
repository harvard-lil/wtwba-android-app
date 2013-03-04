package edu.harvad.law.librarylab.wtwba;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;


public class MainActivity extends Activity {

	public final static String EXTRA_MESSAGE = "edu.harvad.law.librarylab.wtwba.MESSAGE";
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }*/
    
    /** Called when the user clicks the scan button */
    public void scanCode(View view) {
    	
    	Intent intent = new Intent(this, ScanActivity.class);
        startActivity(intent);
    	
        //Intent intent = new Intent("com.google.zxing.client.android.SCAN");
        //intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
        //startActivityForResult(intent, 0);
    }
    
  
}