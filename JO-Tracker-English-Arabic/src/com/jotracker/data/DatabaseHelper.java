package com.jotracker.data;

import java.io.File;
import java.io.IOException;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

	Context context;
	private static String DATABASE_NAME = "systrack";
	private static final String DB_PATH = "/data/data/com.jotracker/databases/";
	private static final int DATABASE_VERSION = 242;
	//private static final String CREATE_TABLE_COORDS = "CREATE TABLE coords (_id INTEGER PRIMARY KEY AUTOINCREMENT, imei NUMERIC(15), latitude TEXT, longitude TEXT, speed TEXT, date TEXT, hour TEXT);";
	private static final String CREATE_TABLE_COORDS = "CREATE TABLE coords (_id INTEGER PRIMARY KEY AUTOINCREMENT, phone TEXT, imei NUMERIC(15), vehicle VARCHAR, latitude VARCHAR(10), longitude VARCHAR(11), speed VARCHAR(10), date VARCHAR(10), hour VARCHAR(8), bat VARCHAR(4), signal VARCHAR(4), info VARCHAR(20), power VARCHAR(3), acc VARCHAR(3), door VARCHAR(3), photo VARCHAR(100));";
	private static final String CREATE_TABLE_VOICES = "CREATE TABLE voices (_id INTEGER PRIMARY KEY AUTOINCREMENT, phone TEXT, imei NUMERIC(15), vehicle VARCHAR, latitude VARCHAR(10), longitude VARCHAR(11), speed VARCHAR(10), date VARCHAR(10), hour VARCHAR(8), bat VARCHAR(4), signal VARCHAR(4), info VARCHAR(20), power VARCHAR(3), acc VARCHAR(3), door VARCHAR(3), file_voice VARCHAR(100));";
	//private static final String CREATE_TABLE_TRACKERS = "CREATE TABLE trackers (_id INTEGER PRIMARY KEY AUTOINCREMENT,  imei NUMERIC UNIQUE  DEFAULT 15, phone VARCHAR, name VARCHAR, license_number VARCHAR, license_expiration VARCHAR, vehicle VARCHAR, description VARCHAR, password VARCHAR, admin VARCHAR, noadmin VARCHAR, auto_track VARCHAR, no_auto_track VARCHAR, monitor VARCHAR, tracker VARCHAR, change_password VARCHAR, arm VARCHAR, disarm VARCHAR, move VARCHAR, nomove VARCHAR,  speed VARCHAR, nospeed VARCHAR, check_imei VARCHAR, check_tracker VARCHAR, stopcar VARCHAR, resumecar VARCHAR, silent VARCHAR, loud VARCHAR, reset VARCHAR, adminip VARCHAR, noadminip VARCHAR, gprs_enable VARCHAR, gprs_disable VARCHAR, apn VARCHAR, apnuser VARCHAR, apnpasswd VARCHAR);";
	private static final String CREATE_TABLE_TRACKERS = "CREATE TABLE trackers (_id INTEGER PRIMARY KEY AUTOINCREMENT,  imei NUMERIC UNIQUE  DEFAULT 15, phone VARCHAR, name VARCHAR, license_number VARCHAR, license_expiration VARCHAR, vehicle VARCHAR, description VARCHAR, password VARCHAR, admin VARCHAR, noadmin VARCHAR, auto_track VARCHAR, no_auto_track VARCHAR, monitor VARCHAR, tracker VARCHAR, change_password VARCHAR, arm VARCHAR, disarm VARCHAR, move VARCHAR, nomove VARCHAR,  speed VARCHAR, nospeed VARCHAR, check_imei VARCHAR, check_tracker VARCHAR, stopcar VARCHAR, resumecar VARCHAR, silent VARCHAR, loud VARCHAR, reset VARCHAR, adminip VARCHAR, noadminip VARCHAR, gprs_enable VARCHAR, gprs_disable VARCHAR, apn VARCHAR, apnuser VARCHAR, apnpasswd VARCHAR, help_me VARCHAR, acc_enable VARCHAR, acc_disable VARCHAR, address VARCHAR, time_zone VARCHAR, factory_setting VARCHAR, nooil VARCHAR, temperature VARCHAR, notemperature VARCHAR, photo VARCHAR,  sd_save VARCHAR,  sd_clear VARCHAR);";
	//private static final String CREATE_VIEW_COORDS_TRACKERS = "CREATE VIEW coords_trackers AS SELECT coords._id AS _id, coords.phone AS phone, coords.imei AS imei, coords.latitude AS latitude, coords.longitude AS longitude, coords.speed AS speed, coords.date AS date, coords.hour AS hour, coords.bat AS bat, coords.signal AS signal, coords.info AS info, trackers.phone AS phone, trackers.vehicle AS vehicle FROM coords JOIN trackers ON coords.phone = trackers.phone;"; 
	
	/*
	private static final String TMP_TABLE_COORDS = "ALTER TABLE coords RENAME TO tmp_coords;"; 
	private static final String NEW_TABLE_COORDS = "CREATE TABLE coords (_id INTEGER PRIMARY KEY AUTOINCREMENT, phone TEXT, imei NUMERIC(15), vehicle VARCHAR, latitude VARCHAR(10), longitude VARCHAR(11), speed VARCHAR(10), date VARCHAR(10), hour VARCHAR(8), bat VARCHAR(4), signal VARCHAR(4), info VARCHAR(20), power VARCHAR(3), acc VARCHAR(3), door VARCHAR(3), photo VARCHAR(100));"; 
	private static final String COPY_TABLE_COORDS = "INSERT INTO coords (_id, phone, imei, vehicle, latitude, longitude, speed, date, hour, bat, signal, info, power, acc, door, photo) SELECT _id, phone, imei, vehicle, latitude, longitude, speed, date, hour, bat, signal, info, \"\", \"\", \"\" \"\" FROM tmp_coords;";
	private static final String DROP_TMP_TABLE_COORDS = "DROP TABLE IF EXISTS tmp_coords;";
	*/
	private static final String TMP_TABLE_TRACKERS = "ALTER TABLE trackers RENAME TO tmp_trackers;"; 
	private static final String NEW_TABLE_TRACKERS = "CREATE TABLE trackers (_id INTEGER PRIMARY KEY AUTOINCREMENT,  imei NUMERIC UNIQUE  DEFAULT 15, phone VARCHAR, name VARCHAR, license_number VARCHAR, license_expiration VARCHAR, vehicle VARCHAR, description VARCHAR, password VARCHAR, admin VARCHAR, noadmin VARCHAR, auto_track VARCHAR, no_auto_track VARCHAR, monitor VARCHAR, tracker VARCHAR, change_password VARCHAR, arm VARCHAR, disarm VARCHAR, move VARCHAR, nomove VARCHAR,  speed VARCHAR, nospeed VARCHAR, check_imei VARCHAR, check_tracker VARCHAR, stopcar VARCHAR, resumecar VARCHAR, silent VARCHAR, loud VARCHAR, reset VARCHAR, adminip VARCHAR, noadminip VARCHAR, gprs_enable VARCHAR, gprs_disable VARCHAR, apn VARCHAR, apnuser VARCHAR, apnpasswd VARCHAR, help_me VARCHAR, acc_enable VARCHAR, acc_disable VARCHAR, address VARCHAR, time_zone VARCHAR, factory_setting VARCHAR, nooil VARCHAR, temperature VARCHAR, notemperature VARCHAR, photo VARCHAR,  sd_save VARCHAR, sd_clear VARCHAR);"; 
	private static final String COPY_TABLE_TRACKERS = "INSERT INTO trackers (_id,  imei, phone, name, license_number, license_expiration, vehicle, description, password, admin, noadmin, auto_track, no_auto_track, monitor, tracker, change_password, arm, disarm, move, nomove,  speed, nospeed, check_imei, check_tracker, stopcar, resumecar, silent, loud, reset, adminip, noadminip, gprs_enable, gprs_disable, apn, apnuser, apnpasswd, help_me, acc_enable, acc_disable, address, time_zone, factory_setting, nooil, temperature, notemperature, photo, sd_save, sd_clear) SELECT _id, imei, phone, name, license_number, license_expiration, vehicle, description, password, admin, noadmin, auto_track, no_auto_track, monitor, tracker, change_password, arm, disarm, move, nomove,  speed, nospeed, check_imei, check_tracker, stopcar, resumecar, silent, loud, reset, adminip, noadminip, gprs_enable, gprs_disable, apn, apnuser, apnpasswd, help_me, acc_enable, acc_disable, address, time_zone, factory_setting, nooil, temperature, notemperature, photo, \"save030s***n123456\", \"clear123456\" FROM tmp_trackers;";
	private static final String DROP_TMP_TABLE_TRACKERS = "DROP TABLE IF EXISTS tmp_trackers;";
	
	private SQLiteDatabase myDataBase;
	private String TAG = "DatabaseHelper: ";

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE_COORDS);
		db.execSQL(CREATE_TABLE_VOICES);
		db.execSQL(CREATE_TABLE_TRACKERS);
		/*
		Log.d(TAG, CREATE_TABLE_COORDS);
		Log.d(TAG, CREATE_TABLE_TRACKERS);*/
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (newVersion > oldVersion) {
			Log.d(TAG, "New database version exists for upgrade.");
			try {
				 db.execSQL(DROP_TMP_TABLE_TRACKERS);
				 db.execSQL(TMP_TABLE_TRACKERS);
				 db.execSQL(NEW_TABLE_TRACKERS);
				 db.execSQL(COPY_TABLE_TRACKERS);
				 db.execSQL(DROP_TMP_TABLE_TRACKERS);
				 
				 db.execSQL(CREATE_TABLE_VOICES);
				 /*
				 db.execSQL(DROP_TMP_TABLE_COORDS);
				 db.execSQL(TMP_TABLE_COORDS);
				 db.execSQL(NEW_TABLE_COORDS);
				 db.execSQL(COPY_TABLE_COORDS);
				 db.execSQL(DROP_TMP_TABLE_COORDS);
				 */
				 Log.d(TAG, TMP_TABLE_TRACKERS);
				 Log.d(TAG, NEW_TABLE_TRACKERS);
				 Log.d(TAG, COPY_TABLE_TRACKERS);
				 Log.d(TAG, DROP_TMP_TABLE_TRACKERS);
				 /*Log.d(TAG, TMP_TABLE_COORDS);
				 Log.d(TAG, NEW_TABLE_COORDS);
				 Log.d(TAG, COPY_TABLE_COORDS);
				 Log.d(TAG, DROP_TMP_TABLE_COORDS);*/
				 
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public DatabaseHelper open() throws SQLException {
		myDataBase = getWritableDatabase();
		Log.d(TAG, "DbHelper Opening Version: " + this.myDataBase.getVersion());
		return this;
	}
	

	private boolean checkOldDatabase() {
	    Log.d(TAG, "OperationDbHelper.checkDatabase");
	    File f = new File(DB_PATH + DATABASE_NAME);
	    return f.exists();
	}

	public void createDatabaseIfRequired() throws IOException, SQLiteException {
	    if (!checkOldDatabase()) {
	      // do db comparison / delete old db / copy new db
	    	DATABASE_NAME = "jotracker";
	    	Log.d(TAG, "DbHelper IF: " + DATABASE_NAME +" v: "+DATABASE_VERSION);
	    }
	    else{
	    	Log.d(TAG, "DbHelper ELSE: " + DATABASE_NAME+" v: "+DATABASE_VERSION);
	    }
	}

}
