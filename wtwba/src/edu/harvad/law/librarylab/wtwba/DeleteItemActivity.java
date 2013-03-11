package edu.harvad.law.librarylab.wtwba;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;


public class DeleteItemActivity extends Activity {

	private String barcode;
	private String title;
	
	DatabaseHandler db;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_delete_item);
        
        this.db = new DatabaseHandler(this);
        
        
        Bundle bundle = getIntent().getExtras();
        this.barcode = bundle.getString("barcode");
        this.title = bundle.getString("title");
        
        String delete_message = "Have you returned " + title + "? If so, delete it.";
        
        TextView text_title = (TextView) findViewById(R.id.title_preview);
        text_title.setText(delete_message);   
    }
    
    /** Called when the user hits the delete button */
	public void deleteItem(View view) {

		Log.w("in dia", this.barcode);
		db.delete_item_by_barcode(this.barcode);

	    Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
	    
	}
	
    
}