package edu.harvad.law.librarylab.wtwba;

public class Entry {
	
	// A container class for our entry. aka, a checkin

	// private variables
	int id;
	String barcode;
	String location;
	String date;

	// Empty constructor
	public Entry() {

	}

	// constructor
	public Entry(int incoming_id, String barcode, String location, String date) {
		this.id = incoming_id;
		this.barcode = barcode;
		this.location = location;
		this.date = date;
	}

	// constructor
	public Entry(String barcode, String location, String date) {
		this.barcode = barcode;
		this.location = location;
		this.date = date;
	}

	// getting id
	public int get_id() {
		return this.id;
	}

	// setting id
	public void set_id(int incoming_id) {
		this.id = incoming_id;
	}

	// getting barcode
	public String get_barcode() {
		return this.barcode;
	}

	// setting barcode
	public void set_barcode(String incoming_barcode) {
		this.barcode = incoming_barcode;
	}

	// getting location
	public String get_location() {
		return this.location;
	}

	// setting location
	public void set_location(String incoming_location) {
		this.location = incoming_location;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}
}