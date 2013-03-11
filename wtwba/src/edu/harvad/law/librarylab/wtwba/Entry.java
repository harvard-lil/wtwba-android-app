package edu.harvad.law.librarylab.wtwba;

import java.sql.Timestamp;

public class Entry {
 
    //private variables
	int id;
    String barcode;
    String location;
    Timestamp date;
 
    // Empty constructor
    public Entry(){
 
    }
 // constructor
    public Entry(int incoming_id,String barcode, String location, Timestamp date){
    	this.id = incoming_id;
        this.barcode = barcode;
        this.location = location;
        this.date = date;
    }
    
    // constructor
    public Entry(String barcode, String location, Timestamp date){
        this.barcode = barcode;
        this.location = location;
        this.date = date;
    }
    
    // getting id
    public int get_id(){
        return this.id;
    }
 
    // setting id
    public void set_id(int incoming_id){
        this.id = incoming_id;
    }
 
    
    // getting barcode
    public String get_barcode(){
        return this.barcode;
    }
 
    // setting barcode
    public void set_barcode(String incoming_barcode){
        this.barcode = incoming_barcode;
    }
 
    // getting location
    public String get_location(){
        return this.location;
    }
 
    // setting location
    public void set_location(String incoming_location){
        this.location = incoming_location;
    }
    
	public Timestamp getDate() {
		return date;
	}
	
	public void setDate(Timestamp date) {
		this.date = date;
	}
}