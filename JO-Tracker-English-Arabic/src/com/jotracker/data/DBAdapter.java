package com.jotracker.data;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.android.maps.GeoPoint;

public class DBAdapter {

	DatabaseHelper DBHelper;
	Context context;
	SQLiteDatabase db;

	// TABLE COORDS
	public static final String TABLE_COORDS = "coords";
	public static final String ROWID = "_id";
	public static final String LATITUDE = "latitude";
	public static final String LONGITUDE = "longitude";
	public static final String SPEED = "speed";
	public static final String DATE = "date";
	public static final String HOUR = "hour";
	public static final String BAT = "bat";
	public static final String SIGNAL = "signal";
	public static final String INFO = "info";
	public static final String POWER = "power";
	public static final String ACC = "acc";
	public static final String DOOR = "door";
	//TABLE_VOICES
	public static final String TABLE_VOICES = "voices";
	public static final String FILE_AUDIO = "file_voice";
	// TABLE TRACKERS
	public static final String TABLE_TRACKERS = "trackers";
	public static final String NAME = "name";
	public static final String IMEI = "imei";
	public static final String LICENSE_NUMBER = "license_number";
	public static final String LICENSE_EXPIRATION = "license_expiration";
	public static final String PHONE = "phone";
	public static final String VEHICLE = "vehicle";
	public static final String TYPE = "type";
	public static final String DESCRIPTION = "description";
	public static final String PASSWORD = "password";
	public static final String ADMIN = "admin";
	public static final String NOADMIN = "noadmin";
	public static final String AUTO_TRACK = "auto_track";
	public static final String NO_AUTO_TRACK = "no_auto_track";
	public static final String MONITOR = "monitor";
	public static final String TRACKER = "tracker";
	public static final String CHANGE_PASSWORD = "change_password";
	public static final String ARM = "arm";
	public static final String DISARM = "disarm";
	public static final String MOVE = "move";
	public static final String NOMOVE = "nomove";
	public static final String NOSPEED = "nospeed";
	public static final String CHECK_IMEI = "check_imei";
	public static final String CHECK_TRACKER = "check_tracker";
	public static final String STOPCAR = "stopcar";
	public static final String RESUMECAR = "resumecar";
	public static final String SILENT = "silent";
	public static final String LOUD = "loud";
	public static final String RESET = "reset";
	public static final String GPRS_MODE = "gprs_enable";
	public static final String SMS_MODE = "gprs_disable";
	public static final String ADMINIP = "adminip";
	public static final String NOADMINIP = "noadminip";
	public static final String APN = "apn";
	public static final String APN_USER = "apnuser";
	public static final String APN_PASSWORD = "apnpasswd";
	public static final String HELP_ME = "help_me";
	public static final String ACC_ENABLE = "acc_enable";
	public static final String ACC_DISABLE = "acc_disable";
	public static final String ADDRESS = "address";
	public static final String TIME_ZONE = "time_zone";
	public static final String FACTORY_SETTING = "factory_setting";
	public static final String NOOIL = "nooil";
	public static final String TEMPERATURE = "temperature";
	public static final String NOTEMPERATURE = "notemperature";
	public static final String PHOTO = "photo";
	public static final String SD_SAVE = "sd_save";
	public static final String SD_CLEAR = "sd_clear";

	public DBAdapter(Context context) {
		this.context = context;
		DBHelper = new DatabaseHelper(context);
	}

	public DBAdapter open() {
		db = DBHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		db.close();
		DBHelper.close();
	}

	public void deleteAllCoords() {
		try {
			db.delete(TABLE_COORDS, null, null);
		} catch (Exception e) {
			Log.i("Exception deleteAllCoords(): ", e.getMessage());
		}
	}
	public void deleteAllVoices() {
		try {
			db.delete(TABLE_VOICES, null, null);
		} catch (Exception e) {
			Log.i("Exception deleteAllVoices(): ", e.getMessage());
		}
	}

	public void deleteAllTrackers() {
		try {
			db.delete(TABLE_TRACKERS, null, null);
		} catch (Exception e) {
			Log.i("Exception deleteAllTrackers(): ", e.getMessage());
		}
	}
	
	public long insertCoords(String phone, String imei, String vehicle,
			String lat, String lon, String speed, String date, String hour,
			String bat, String signal, String info, String power, String acc,
			String door, String photo) {
		ContentValues values = new ContentValues();
		values.put(PHONE, phone);
		values.put(IMEI, imei);
		values.put(VEHICLE, vehicle);
		values.put(LATITUDE, lat);
		values.put(LONGITUDE, lon);
		values.put(SPEED, speed);
		values.put(DATE, date);
		values.put(HOUR, hour);
		values.put(BAT, bat);
		values.put(SIGNAL, signal);
		values.put(INFO, info);
		values.put(POWER, power);
		values.put(ACC, acc);
		values.put(DOOR, door);
		values.put(PHOTO, photo);
		return db.insert(TABLE_COORDS, null, values);
	}
	
	public long insertVoices(String phone, String imei, String vehicle,
			String lat, String lon, String speed, String date, String hour,
			String bat, String signal, String info, String power, String acc,
			String door, String file_audio) {
		ContentValues values = new ContentValues();
		values.put(PHONE, phone);
		values.put(IMEI, imei);
		values.put(VEHICLE, vehicle);
		values.put(LATITUDE, lat);
		values.put(LONGITUDE, lon);
		values.put(SPEED, speed);
		values.put(DATE, date);
		values.put(HOUR, hour);
		values.put(BAT, bat);
		values.put(SIGNAL, signal);
		values.put(INFO, info);
		values.put(POWER, power);
		values.put(ACC, acc);
		values.put(DOOR, door);
		values.put(FILE_AUDIO, file_audio);
		return db.insert(TABLE_VOICES, null, values);
	}


	public long insertTrackes(String imei, String name, String license_number,
			String license_expiration, String vehicle, String description,
			String phone, String password, String admin, String noadmin,
			String auto_track, String no_auto_track, String monitor,
			String tracker, String change_password, String arm, String disarm,
			String move, String nomove, String speed, String nospeed,
			String check_imei, String check_tracker, String stopcar,
			String resumecar, String silent, String loud, String reset,
			String gprs_enable, String gprs_disable, String adminip,
			String noadminip, String apn, String apnuser, String apnpasswd,
			String help_me, String acc_enable, String acc_disable,
			String address, String time_zone, String factory_setting,
			String nooil, String temperature, String notemperature,
			String photo, String sd_save, String sd_clear) {
		ContentValues values = new ContentValues();
		values.put(IMEI, imei);
		values.put(NAME, name);
		values.put(LICENSE_NUMBER, license_number);
		values.put(LICENSE_EXPIRATION, license_expiration);
		values.put(VEHICLE, vehicle);
		values.put(DESCRIPTION, description);
		values.put(PHONE, phone);
		values.put(PASSWORD, password);
		values.put(ADMIN, admin);
		values.put(NOADMIN, noadmin);
		values.put(AUTO_TRACK, auto_track);
		values.put(NO_AUTO_TRACK, no_auto_track);
		values.put(MONITOR, monitor);
		values.put(TRACKER, tracker);
		values.put(CHANGE_PASSWORD, change_password);
		values.put(ARM, arm);
		values.put(DISARM, disarm);
		values.put(MOVE, move);
		values.put(NOMOVE, nomove);
		values.put(SPEED, speed);
		values.put(NOSPEED, nospeed);
		values.put(CHECK_IMEI, check_imei);
		values.put(CHECK_TRACKER, check_tracker);
		values.put(STOPCAR, stopcar);
		values.put(RESUMECAR, resumecar);
		values.put(SILENT, silent);
		values.put(LOUD, loud);
		values.put(RESET, reset);
		values.put(GPRS_MODE, gprs_enable);
		values.put(SMS_MODE, gprs_disable);
		values.put(ADMINIP, adminip);
		values.put(NOADMINIP, noadminip);
		values.put(APN, apn);
		values.put(APN_USER, apnuser);
		values.put(APN_PASSWORD, apnpasswd);
		values.put(HELP_ME, help_me);
		values.put(ACC_ENABLE, acc_enable);
		values.put(ACC_DISABLE, acc_disable);
		values.put(ADDRESS, address);
		values.put(TIME_ZONE, time_zone);
		values.put(FACTORY_SETTING, factory_setting);
		values.put(NOOIL, nooil);
		values.put(TEMPERATURE, temperature);
		values.put(NOTEMPERATURE, notemperature);
		values.put(PHOTO, photo);
		values.put(SD_SAVE, sd_save);
		values.put(SD_CLEAR, sd_clear);
		return db.insert(TABLE_TRACKERS, null, values);
	}

	public boolean updateTrackers(String rowId, String imei, String vehicle,
			String description, String phone, String password) {
		ContentValues values = new ContentValues();
		values.put(IMEI, imei);
		values.put(VEHICLE, vehicle);
		values.put(DESCRIPTION, description);
		values.put(PHONE, phone);
		values.put(PASSWORD, password);
		return db.update(TABLE_TRACKERS, values, ROWID + "=" + rowId, null) > 0;
	}

	public boolean updateSettingsTrackers(String rowId, String admin,
			String noadmin, String auto_track, String no_auto_track,
			String monitor, String tracker, String change_password, String arm,
			String disarm, String move, String nomove, String speed,
			String nospeed, String check_imei, String check_tracker,
			String stopcar, String resumecar, String silent, String loud,
			String reset, String gprs_mode, String sms_mode, String adminip,
			String noadminip, String apn, String apn_user, String apn_password,
			String help_me, String acc_enable, String acc_disable,
			String address, String time_zone, String factory_setting,
			String nooil, String temperature, String notemperature,
			String photo, String sd_save, String sd_clear) {
		ContentValues values = new ContentValues();
		values.put(ADMIN, admin);
		values.put(NOADMIN, noadmin);
		values.put(AUTO_TRACK, auto_track);
		values.put(NO_AUTO_TRACK, no_auto_track);
		values.put(MONITOR, monitor);
		values.put(TRACKER, tracker);
		values.put(CHANGE_PASSWORD, change_password);
		values.put(ARM, arm);
		values.put(DISARM, disarm);
		values.put(MOVE, move);
		values.put(NOMOVE, nomove);
		values.put(SPEED, speed);
		values.put(NOSPEED, nospeed);
		values.put(CHECK_IMEI, check_imei);
		values.put(CHECK_TRACKER, check_tracker);
		values.put(STOPCAR, stopcar);
		values.put(RESUMECAR, resumecar);
		values.put(SILENT, silent);
		values.put(LOUD, loud);
		values.put(RESET, reset);
		values.put(GPRS_MODE, gprs_mode);
		values.put(SMS_MODE, sms_mode);
		values.put(ADMINIP, adminip);
		values.put(NOADMINIP, noadminip);
		values.put(APN, apn);
		values.put(APN_USER, apn_user);
		values.put(APN_PASSWORD, apn_password);
		values.put(HELP_ME, help_me);
		values.put(ACC_ENABLE, acc_enable);
		values.put(ACC_DISABLE, acc_disable);
		values.put(ADDRESS, address);
		values.put(TIME_ZONE, time_zone);
		values.put(FACTORY_SETTING, factory_setting);
		values.put(NOOIL, nooil);
		values.put(TEMPERATURE, temperature);
		values.put(NOTEMPERATURE, notemperature);
		values.put(PHOTO, photo);
		values.put(SD_SAVE, sd_save);
		values.put(SD_CLEAR, sd_clear);
		return db.update(TABLE_TRACKERS, values, ROWID + "=" + rowId, null) > 0;
	}

	public boolean updateOparationMode(String rowId, String operation_mode) {
		ContentValues values = new ContentValues();
		return db.update(TABLE_TRACKERS, values, ROWID + "=" + rowId, null) > 0;
	}

	public boolean updatePassword(String rowId, String password) {
		ContentValues values = new ContentValues();
		values.put(PASSWORD, password);
		return db.update(TABLE_TRACKERS, values, ROWID + "=" + rowId, null) > 0;
	}

	public boolean updateAPNname(String rowId, String apn_name) {
		ContentValues values = new ContentValues();
		values.put(APN, apn_name);
		return db.update(TABLE_TRACKERS, values, ROWID + "=" + rowId, null) > 0;
	}

	public boolean updateAPNuserAndpass(String rowId, String apn_user,
			String apn_password) {
		ContentValues values = new ContentValues();
		values.put(APN_USER, apn_user);
		values.put(APN_PASSWORD, apn_password);
		return db.update(TABLE_TRACKERS, values, ROWID + "=" + rowId, null) > 0;
	}

	/*
	 * public boolean updateIPAddressAndport(String rowId, String ip_address,
	 * String tcp_port) { ContentValues values = new ContentValues();
	 * values.put(IP_ADDRESS, ip_address); values.put(TCP_PORT, tcp_port);
	 * return db.update(TABLE_TRACKERS, values, ROWID + "=" + rowId, null) > 0;
	 * }
	 */
	/*
	 * public boolean updateSettingsTrackers(String rowId, String phone, String
	 * password, String operation_mode, String apn_name, String apn_user, String
	 * apn_password, String ip_address, String tcp_port) { ContentValues values
	 * = new ContentValues(); values.put(PHONE, phone); values.put(PASSWORD,
	 * password); values.put(APN_USER, apn_user); values.put(APN_PASSWORD,
	 * apn_password); return db.update(TABLE_TRACKERS, values, ROWID + "=" +
	 * rowId, null) > 0; }
	 */

	public boolean deleteCoords(long id) {
		return db.delete(TABLE_COORDS, ROWID + "=" + id, null) > 0;
	}
	
	public boolean deleteVoices(long id) {
		return db.delete(TABLE_VOICES, ROWID + "=" + id, null) > 0;
	}

	public boolean deleteTrackers(long id) {
		return db.delete(TABLE_TRACKERS, ROWID + "=" + id, null) > 0;
	}

	public Cursor selectAllCoords() {
		return db.query(TABLE_COORDS, new String[] { ROWID, PHONE, IMEI,
				VEHICLE, LATITUDE, LONGITUDE, SPEED, DATE, HOUR, BAT, SIGNAL,
				INFO, POWER, ACC, DOOR, PHOTO }, null, null, null, null, ROWID
				+ " DESC", null);
	}

	public Cursor selectAllCoordsWithPhotos() {
		return db.query(TABLE_COORDS, new String[] { ROWID, PHONE, IMEI,
				VEHICLE, LATITUDE, LONGITUDE, SPEED, DATE, HOUR, BAT, SIGNAL,
				INFO, POWER, ACC, DOOR, PHOTO }, PHOTO + " LIKE ?",
				new String[] { "%http%" }, null, null, ROWID + " DESC", null);
	}
	
	public Cursor selectAllVoices() {
		return db.query(TABLE_VOICES, new String[] { ROWID, PHONE, IMEI,
				VEHICLE, LATITUDE, LONGITUDE, SPEED, DATE, HOUR, BAT, SIGNAL,
				INFO, POWER, ACC, DOOR, FILE_AUDIO }, null, null, null, null, ROWID
				+ " DESC", null);
	}

	public Cursor selectAllTrackers() {
		return db.query(TABLE_TRACKERS, new String[] { ROWID, IMEI, VEHICLE,
				DESCRIPTION, PHONE, PASSWORD }, null, null, null, null, ROWID
				+ " DESC", null);
	}

	public List<String> getAllVehicles() {
		List<String> vehicles = new ArrayList<String>();

		// Select All Query
		String selectQuery = "SELECT " + VEHICLE + " FROM " + TABLE_TRACKERS
				+ " ORDER BY " + VEHICLE + " ASC";
		Cursor cursor = db.rawQuery(selectQuery, null);
		int vehicle = cursor.getColumnIndex(VEHICLE);
		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				vehicles.add(cursor.getString(vehicle));
			} while (cursor.moveToNext());
		}
		// closing connection
		cursor.close();
		// returning vehicles
		return vehicles;
	}

	/*
	 * public Cursor selectRowidTracker(String vehicle) { return
	 * db.query(TABLE_TRACKERS, new String[] { ROWID, VEHICLE, PHONE, PASSWORD,
	 * ADMIN, NOADMIN, AUTO_TRACK, NO_AUTO_TRACK, MONITOR, TRACKER,
	 * CHANGE_PASSWORD, ARM, DISARM, MOVE, NOMOVE, SPEED, NOSPEED, CHECK_IMEI,
	 * CHECK_TRACKER, STOPCAR, RESUMECAR, SILENT, LOUD, RESET, ADMINIP,
	 * NOADMINIP, APN, APN_USER, APN_PASSWORD }, VEHICLE + "= '" + vehicle +
	 * "'", null, null, null, null, null); }
	 */
	public Cursor selectTrackerByImei(String imei) {
		return db.query(TABLE_TRACKERS, new String[] { IMEI }, IMEI + " = "
				+ imei, null, null, null, null, null);
	}

	public String selectDateRoute(String d) {
		String result = null;
		try {
			Cursor cursor = db.query(TABLE_COORDS, new String[] { ROWID, PHONE,
					IMEI, VEHICLE, LATITUDE, LONGITUDE, SPEED, DATE, HOUR,
					POWER, ACC, DOOR }, DATE + " = " + d, null, null, null,
					ROWID + " DESC", null);
			;
			int phone = cursor.getColumnIndex(PHONE);
			int imei = cursor.getColumnIndex(IMEI);
			int vehicle = cursor.getColumnIndex(VEHICLE);
			int lat = cursor.getColumnIndex(LATITUDE);
			int lon = cursor.getColumnIndex(LONGITUDE);
			int speed = cursor.getColumnIndex(SPEED);
			int date = cursor.getColumnIndex(DATE);
			int hour = cursor.getColumnIndex(HOUR);
			int power = cursor.getColumnIndex(POWER);
			int acc = cursor.getColumnIndex(ACC);
			int door = cursor.getColumnIndex(DOOR);
			for (cursor.moveToFirst(); !(cursor.isAfterLast()); cursor
					.moveToNext()) {
				result = cursor.getString(phone) + "," + cursor.getString(imei)
						+ "," + cursor.getString(vehicle) + ","
						+ cursor.getString(lat) + "," + cursor.getString(lon)
						+ "," + cursor.getString(speed) + ","
						+ cursor.getString(date) + "," + cursor.getString(hour)
						+ "," + cursor.getString(power) + ","
						+ cursor.getString(acc) + "," + cursor.getString(door);
			}
			cursor.close();
			if (result == null) {
				result = "0,0,0,0,0,0,0,0,0,0,0";
			}
		} catch (Exception e) {
			Log.i("Exception selectLastCoord(): ", e.getMessage());
		}
		return result;
	}

	public String selectAllLicenses() {
		String result = "";
		try {
			Cursor cursor = db.query(TABLE_TRACKERS, new String[] { ROWID,
					PHONE, LICENSE_EXPIRATION }, null, null, null, null, ROWID
					+ " DESC", null);
			int phone = cursor.getColumnIndex(PHONE);
			int license = cursor.getColumnIndex(LICENSE_EXPIRATION);
			for (cursor.moveToFirst(); !(cursor.isAfterLast()); cursor
					.moveToNext()) {
				result += cursor.getString(phone) + ","
						+ cursor.getString(license) + ";";
			}
			if (!cursor.moveToFirst()) {
				return result = "0";
			}
			cursor.close();
		} catch (Exception e) {
			Log.i("Exeption selectAllLicenses(): ", e.getMessage());
		}
		return result;
	}

	public String selectAllTrackersVehicleAsc() {
		String result = "";
		try {
			Cursor cursor = db.query(TABLE_TRACKERS, new String[] { ROWID,
					IMEI, VEHICLE, DESCRIPTION, PHONE, PASSWORD,
					LICENSE_EXPIRATION }, null, null, null, null, ROWID
					+ " DESC", null);
			int vehicle = cursor.getColumnIndex(VEHICLE);
			for (cursor.moveToFirst(); !(cursor.isAfterLast()); cursor
					.moveToNext()) {
				result += cursor.getString(vehicle) + ",";
			}
			cursor.close();
		} catch (Exception e) {
			Log.i("Exeption selectAllTrackersVehicleAsc():", e.getMessage());
		}
		return result;
	}

	public List<String> selectRouteByVechicleAndDate(String vehicle, String date) {
		List<String> result = new ArrayList<String>();
		try {
			Cursor cursor = db
					.rawQuery(
							"SELECT * FROM coords WHERE vehicle = ? AND date = ? ORDER By _id ASC",
							new String[] { vehicle, date });
			int rowid = cursor.getColumnIndex(ROWID);
			for (cursor.moveToFirst(); !(cursor.isAfterLast()); cursor
					.moveToNext()) {
				result.add(cursor.getString(rowid));
			}
			cursor.close();
		} catch (Exception e) {
			Log.i("Exeption selectOneLicenseTracke:", e.getMessage());
		}
		return result;
	}
	
	public List<GeoPoint> selectPointsByVechicleAndDate(String vehicle, String date) {
		List<GeoPoint> result = new ArrayList<GeoPoint>();
		Double latitude = 0.0, longitude = 0.0;
		try {
			Cursor cursor = db
					.rawQuery(
							"SELECT * FROM coords WHERE vehicle = ? AND date = ? ORDER By _id ASC",
							new String[] { vehicle, date });
			int lat = cursor.getColumnIndex(LATITUDE);
			int lon = cursor.getColumnIndex(LONGITUDE);
			for (cursor.moveToFirst(); !(cursor.isAfterLast()); cursor
					.moveToNext()) {
				latitude = Double.parseDouble(cursor.getString(lat));
				longitude = Double.parseDouble(cursor.getString(lon));
				result.add(new GeoPoint((int) (latitude * 1E6), (int) (longitude * 1E6)));
			}
			cursor.close();
		} catch (Exception e) {
			Log.i("selectPointsByVechicleAndDate(): ", e.getMessage());
		}
		return result;
	}
	
	public String selectOneFileAudio(long id) {
		String result = null;
		try {
			Cursor cursor = db.query(TABLE_VOICES, new String[] { FILE_AUDIO },
					ROWID + " = " + id, null, null, null, null, null);
			int file_name = cursor.getColumnIndex(FILE_AUDIO);
			for (cursor.moveToFirst(); !(cursor.isAfterLast()); cursor
					.moveToNext()) {
				result = cursor.getString(file_name);
				cursor.close();
			}

		} catch (Exception e) {
			Log.i("Exeption selectOneLicenseTracke:", e.getMessage());
		}
		return result;
	}	

	public String selectOneLicenseTracker(String vehicle) {
		String result = null;
		try {
			Cursor cursor = db.query(TABLE_TRACKERS, new String[] { ROWID,
					IMEI, VEHICLE, LICENSE_EXPIRATION }, VEHICLE + " LIKE ?",
					new String[] { "%" + vehicle + "%" }, null, null, null);
			int license_expiration = cursor.getColumnIndex(LICENSE_EXPIRATION);
			for (cursor.moveToFirst(); !(cursor.isAfterLast()); cursor
					.moveToNext()) {
				result = cursor.getString(license_expiration);
				cursor.close();
			}

		} catch (Exception e) {
			Log.i("Exeption selectOneLicenseTracke:", e.getMessage());
		}
		return result;
	}

	public String selectOneLicenseTrackerById(String rowid) {
		String result = null;
		try {
			Cursor cursor = db.query(TABLE_TRACKERS, new String[] { ROWID,
					IMEI, VEHICLE, LICENSE_EXPIRATION }, ROWID + " LIKE ?",
					new String[] { "%" + rowid + "%" }, null, null, null);
			int license_expiration = cursor.getColumnIndex(LICENSE_EXPIRATION);
			for (cursor.moveToFirst(); !(cursor.isAfterLast()); cursor
					.moveToNext()) {
				result = cursor.getString(license_expiration);
				cursor.close();
			}

		} catch (Exception e) {
			Log.i("Exeption selectOneLicenseTracke:", e.getMessage());
		}
		return result;
	}

	public String selectOneTracker(String rowid) {
		String result = null;
		try {
			Cursor cursor = db.query(TABLE_TRACKERS, new String[] { ROWID,
					IMEI, NAME, LICENSE_NUMBER, LICENSE_EXPIRATION, VEHICLE,
					DESCRIPTION, PHONE, PASSWORD }, ROWID + " = " + rowid,
					null, null, null, null, null);
			int id = cursor.getColumnIndex(ROWID);
			int imei = cursor.getColumnIndex(IMEI);
			int name = cursor.getColumnIndex(NAME);
			int license_number = cursor.getColumnIndex(LICENSE_NUMBER);
			int license_expiration = cursor.getColumnIndex(LICENSE_EXPIRATION);
			int vehicle = cursor.getColumnIndex(VEHICLE);
			int description = cursor.getColumnIndex(DESCRIPTION);
			int phone = cursor.getColumnIndex(PHONE);
			int password = cursor.getColumnIndex(PASSWORD);
			for (cursor.moveToFirst(); !(cursor.isAfterLast()); cursor
					.moveToNext()) {
				result = cursor.getString(id) + "," + cursor.getString(imei)
						+ "," + cursor.getString(name) + ","
						+ cursor.getString(license_number) + ","
						+ cursor.getString(license_expiration) + ","
						+ cursor.getString(vehicle) + ","
						+ cursor.getString(description) + ","
						+ cursor.getString(phone) + ","
						+ cursor.getString(password) + ",";
				cursor.close();
			}

		} catch (Exception e) {
			Log.i("Exeption selectOneTracker:", e.getMessage());
		}
		return result;
	}

	public String selectOneTrackerByImei(String cod) {
		String result = null;
		try {
			// "Word"+" LIKE '"+name+"%'",
			Cursor cursor = db.query(TABLE_TRACKERS, new String[] { ROWID,
					IMEI, VEHICLE, DESCRIPTION, PHONE, PASSWORD }, IMEI
					+ " LIKE ?", new String[] { "%" + cod + "%" }, null, null,
					null);
			int id = cursor.getColumnIndex(ROWID);
			int imei = cursor.getColumnIndex(IMEI);
			int vehicle = cursor.getColumnIndex(VEHICLE);
			int description = cursor.getColumnIndex(DESCRIPTION);
			int phone = cursor.getColumnIndex(PHONE);
			int password = cursor.getColumnIndex(PASSWORD);
			for (cursor.moveToFirst(); !(cursor.isAfterLast()); cursor
					.moveToNext()) {
				result = cursor.getString(id) + "," + cursor.getString(imei)
						+ "," + cursor.getString(vehicle) + ","
						+ cursor.getString(description) + ","
						+ cursor.getString(phone) + ","
						+ cursor.getString(password) + ",";
			}
			if (result == null) {
				result = "0,0,0,0,0,0,0";
			}
			cursor.close();
		} catch (Exception e) {
			Log.i("Exeption selectOneTrackerByImei():", e.getMessage());
		}
		return result;
	}

	public boolean checkPhoneExist(String phone) {
		Cursor cursor = db.query(TABLE_TRACKERS, new String[] { PHONE }, PHONE
				+ " LIKE ?", new String[] { "%" + phone + "%" }, null, null,
				null);
		try {
			if (cursor.moveToFirst()) {
				cursor.close();
				return true;
			}
		} catch (Exception e) {
			Log.i("Exeption checkTrackerExist():", e.getMessage());
		}
		cursor.close();
		return false;
	}

	public String getPhoneNumber(String phone) {
		String result = null;
		Cursor cursor = db.query(TABLE_TRACKERS, new String[] { PHONE }, PHONE
				+ " LIKE ?", new String[] { "%" + phone + "%" }, null, null,
				null);
		try {
			int phone_number = cursor.getColumnIndex(PHONE);
			for (cursor.moveToFirst(); !(cursor.isAfterLast()); cursor
					.moveToNext()) {
				result = cursor.getString(phone_number);
			}
			if (result == null) {
				result = "0";
			}

		} catch (Exception e) {
			Log.i("Exeption checkTrackerExist():", e.getMessage());
		}
		cursor.close();
		return result;
	}

	public String getVehicleName(String phone) {
		String result = null;
		Cursor cursor = db.query(TABLE_TRACKERS,
				new String[] { PHONE, VEHICLE }, PHONE + " LIKE ?",
				new String[] { "%" + phone + "%" }, null, null, null);
		try {
			int vehicle = cursor.getColumnIndex(VEHICLE);
			for (cursor.moveToFirst(); !(cursor.isAfterLast()); cursor
					.moveToNext()) {
				result = cursor.getString(vehicle);
			}
			if (result == null) {
				result = "0";
			}

		} catch (Exception e) {
			Log.i("Exeption getVehicleName():", e.getMessage());
		}
		cursor.close();
		return result;
	}

	public String selectOneTrackerByPhone(String cod) {
		String result = null;
		try {
			// "Word"+" LIKE '"+name+"%'",
			Cursor cursor = db.query(TABLE_TRACKERS, new String[] { ROWID,
					IMEI, VEHICLE, DESCRIPTION, PHONE, PASSWORD }, PHONE
					+ " LIKE ?", new String[] { "%" + cod + "%" }, null, null,
					null);
			int id = cursor.getColumnIndex(ROWID);
			int imei = cursor.getColumnIndex(IMEI);
			int vehicle = cursor.getColumnIndex(VEHICLE);
			int description = cursor.getColumnIndex(DESCRIPTION);
			int phone = cursor.getColumnIndex(PHONE);
			int password = cursor.getColumnIndex(PASSWORD);
			for (cursor.moveToFirst(); !(cursor.isAfterLast()); cursor
					.moveToNext()) {
				result = cursor.getString(id) + "," + cursor.getString(imei)
						+ "," + cursor.getString(vehicle) + ","
						+ cursor.getString(description) + ","
						+ cursor.getString(phone) + ","
						+ cursor.getString(password) + ",";
			}
			cursor.close();
			if (result == null) {
				result = "0,0,0,0,0,0,0";
			}
		} catch (Exception e) {
			Log.i("Exeption selectOneTrackerByImei():", e.getMessage());
		}
		return result;
	}

	public boolean checkTrackerExist(String cod) {
		try {
			Cursor cursor = db.query(TABLE_TRACKERS, new String[] { IMEI },
					IMEI + " = " + cod, null, null, null, null, null);
			if (cursor.moveToFirst()) {
				return false;
			}
			cursor.close();
		} catch (Exception e) {
			Log.i("Exeption checkTrackerExist():", e.getMessage());
		}
		return true;
	}

	public String selectCommandLocateGPS(long rowid) {
		String result = null;
		try {
			Cursor cursor = db.query(TABLE_TRACKERS, new String[] { ROWID,
					PHONE, PASSWORD, ADDRESS }, ROWID + "= " + rowid, null,
					null, null, null, null);
			int phone = cursor.getColumnIndex(PHONE);
			int password = cursor.getColumnIndex(PASSWORD);
			int command = cursor.getColumnIndex(ADDRESS);
			for (cursor.moveToFirst(); !(cursor.isAfterLast()); cursor
					.moveToNext()) {
				result = cursor.getString(phone) + ","
						+ cursor.getString(password) + ","
						+ cursor.getString(command) + ",";
			}
			cursor.close();
			if (result == null) {
				result = "0,0";
			}
		} catch (Exception e) {
			Log.i("Exeption selectCommandLocateGPS():", e.getMessage());
		}
		return result;
	}

	public String selectPhoneOneTracker(long rowid) {
		String result = null;
		try {
			Cursor cursor = db.query(TABLE_TRACKERS, new String[] { ROWID,
					PHONE, PASSWORD }, ROWID + "= " + rowid, null, null, null,
					null, null);
			int phone = cursor.getColumnIndex(PHONE);
			int password = cursor.getColumnIndex(PASSWORD);
			for (cursor.moveToFirst(); !(cursor.isAfterLast()); cursor
					.moveToNext()) {
				result = cursor.getString(phone) + ","
						+ cursor.getString(password);
			}
			cursor.close();
			if (result == null) {
				result = "0,0";
			}
		} catch (Exception e) {
			Log.i("Exeption callOneTracker():", e.getMessage());
		}
		return result;
	}

	public String selectRowidTracker(String vehicle) {
		String result = null;
		try {
			Cursor cursor = db.query(TABLE_TRACKERS, new String[] { ROWID,
					VEHICLE, PHONE, PASSWORD, ADMIN, NOADMIN, AUTO_TRACK,
					NO_AUTO_TRACK, MONITOR, TRACKER, CHANGE_PASSWORD, ARM,
					DISARM, MOVE, NOMOVE, SPEED, NOSPEED, CHECK_IMEI,
					CHECK_TRACKER, STOPCAR, RESUMECAR, SILENT, LOUD, RESET,
					GPRS_MODE, SMS_MODE, ADMINIP, NOADMINIP, APN, APN_USER,
					APN_PASSWORD, HELP_ME, ACC_ENABLE, ACC_DISABLE, ADDRESS,
					TIME_ZONE, FACTORY_SETTING, NOOIL, TEMPERATURE,
					NOTEMPERATURE, PHOTO, SD_SAVE, SD_CLEAR }, VEHICLE + "= '"
					+ vehicle + "'", null, null, null, null, null);
			int rowid = cursor.getColumnIndex(ROWID);
			int phone = cursor.getColumnIndex(PHONE);
			int password = cursor.getColumnIndex(PASSWORD);
			int admin = cursor.getColumnIndex(ADMIN);
			int noadmin = cursor.getColumnIndex(NOADMIN);
			int auto_track = cursor.getColumnIndex(AUTO_TRACK);
			int no_auto_track = cursor.getColumnIndex(NO_AUTO_TRACK);
			int monitor = cursor.getColumnIndex(MONITOR);
			int tracker = cursor.getColumnIndex(TRACKER);
			int change_password = cursor.getColumnIndex(CHANGE_PASSWORD);
			int arm = cursor.getColumnIndex(ARM);
			int disarm = cursor.getColumnIndex(DISARM);
			int move = cursor.getColumnIndex(MOVE);
			int nomove = cursor.getColumnIndex(NOMOVE);
			int speed = cursor.getColumnIndex(SPEED);
			int nospeed = cursor.getColumnIndex(NOSPEED);
			int check_imei = cursor.getColumnIndex(CHECK_IMEI);
			int check_tracker = cursor.getColumnIndex(CHECK_TRACKER);
			int stopcar = cursor.getColumnIndex(STOPCAR);
			int resumecar = cursor.getColumnIndex(RESUMECAR);
			int silent = cursor.getColumnIndex(SILENT);
			int loud = cursor.getColumnIndex(LOUD);
			int reset = cursor.getColumnIndex(RESET);
			int gprs_enable = cursor.getColumnIndex(GPRS_MODE);
			int gprs_disable = cursor.getColumnIndex(SMS_MODE);
			int adminip = cursor.getColumnIndex(ADMINIP);
			int noadminip = cursor.getColumnIndex(NOADMINIP);
			int apn = cursor.getColumnIndex(APN);
			int apn_user = cursor.getColumnIndex(APN_USER);
			int apn_password = cursor.getColumnIndex(APN_PASSWORD);
			int help_me = cursor.getColumnIndex(HELP_ME);
			int acc_enable = cursor.getColumnIndex(ACC_ENABLE);
			int acc_disable = cursor.getColumnIndex(ACC_DISABLE);
			int address = cursor.getColumnIndex(ADDRESS);
			int time_zone = cursor.getColumnIndex(TIME_ZONE);
			int factory_setting = cursor.getColumnIndex(FACTORY_SETTING);
			int nooil = cursor.getColumnIndex(NOOIL);
			int temperature = cursor.getColumnIndex(TEMPERATURE);
			int notemperature = cursor.getColumnIndex(NOTEMPERATURE);
			int photo = cursor.getColumnIndex(PHOTO);
			int sd_save = cursor.getColumnIndex(SD_SAVE);
			int sd_clear = cursor.getColumnIndex(SD_CLEAR);

			for (cursor.moveToFirst(); !(cursor.isAfterLast()); cursor
					.moveToNext()) {
				result = cursor.getString(rowid) + ","
						+ cursor.getString(phone) + ","
						+ cursor.getString(password) + ","
						+ cursor.getString(admin) + ","
						+ cursor.getString(noadmin) + ","
						+ cursor.getString(auto_track) + ","
						+ cursor.getString(no_auto_track) + ","
						+ cursor.getString(monitor) + ","
						+ cursor.getString(tracker) + ","
						+ cursor.getString(change_password) + ","
						+ cursor.getString(arm) + ","
						+ cursor.getString(disarm) + ","
						+ cursor.getString(move) + ","
						+ cursor.getString(nomove) + ","
						+ cursor.getString(speed) + ","
						+ cursor.getString(nospeed) + ","
						+ cursor.getString(check_imei) + ","
						+ cursor.getString(check_tracker) + ","
						+ cursor.getString(stopcar) + ","
						+ cursor.getString(resumecar) + ","
						+ cursor.getString(silent) + ","
						+ cursor.getString(loud) + ","
						+ cursor.getString(reset) + ","
						+ cursor.getString(gprs_enable) + ","
						+ cursor.getString(gprs_disable) + ","
						+ cursor.getString(adminip) + ","
						+ cursor.getString(noadminip) + ","
						+ cursor.getString(apn) + ","
						+ cursor.getString(apn_user) + ","
						+ cursor.getString(apn_password) + ","
						+ cursor.getString(help_me) + ","
						+ cursor.getString(acc_enable) + ","
						+ cursor.getString(acc_disable) + ","
						+ cursor.getString(address) + ","
						+ cursor.getString(time_zone) + ","
						+ cursor.getString(factory_setting) + ","
						+ cursor.getString(nooil) + ","
						+ cursor.getString(temperature) + ","
						+ cursor.getString(notemperature) + ","
						+ cursor.getString(photo) + ","
						+ cursor.getString(sd_save) + ","
						+ cursor.getString(sd_clear) + ",";
			}
			cursor.close();
			if (result == null) {
				result = "0,0";
			}
		} catch (Exception e) {
			Log.i("Exeption selectRowidTracker():", e.getMessage());
		}
		return result;
	}

	public String selectTrackerById(String cod) {
		String result = null;
		try {
			Cursor cursor = db.query(TABLE_TRACKERS, new String[] { ROWID,
					VEHICLE, PHONE, PASSWORD, ADMIN, NOADMIN, AUTO_TRACK,
					NO_AUTO_TRACK, MONITOR, TRACKER, CHANGE_PASSWORD, ARM,
					DISARM, MOVE, NOMOVE, SPEED, NOSPEED, CHECK_IMEI,
					CHECK_TRACKER, STOPCAR, RESUMECAR, SILENT, LOUD, RESET,
					GPRS_MODE, SMS_MODE, ADMINIP, NOADMINIP, APN, APN_USER,
					APN_PASSWORD, HELP_ME, ACC_ENABLE, ACC_DISABLE, ADDRESS,
					TIME_ZONE, FACTORY_SETTING, NOOIL, TEMPERATURE,
					NOTEMPERATURE, PHOTO, SD_SAVE, SD_CLEAR }, ROWID + "= '"
					+ cod + "'", null, null, null, null, null);
			int rowid = cursor.getColumnIndex(ROWID);
			int vehicle = cursor.getColumnIndex(VEHICLE);
			int phone = cursor.getColumnIndex(PHONE);
			int password = cursor.getColumnIndex(PASSWORD);
			int admin = cursor.getColumnIndex(ADMIN);
			int noadmin = cursor.getColumnIndex(NOADMIN);
			int auto_track = cursor.getColumnIndex(AUTO_TRACK);
			int no_auto_track = cursor.getColumnIndex(NO_AUTO_TRACK);
			int monitor = cursor.getColumnIndex(MONITOR);
			int tracker = cursor.getColumnIndex(TRACKER);
			int change_password = cursor.getColumnIndex(CHANGE_PASSWORD);
			int arm = cursor.getColumnIndex(ARM);
			int disarm = cursor.getColumnIndex(DISARM);
			int move = cursor.getColumnIndex(MOVE);
			int nomove = cursor.getColumnIndex(NOMOVE);
			int speed = cursor.getColumnIndex(SPEED);
			int nospeed = cursor.getColumnIndex(NOSPEED);
			int check_imei = cursor.getColumnIndex(CHECK_IMEI);
			int check_tracker = cursor.getColumnIndex(CHECK_TRACKER);
			int stopcar = cursor.getColumnIndex(STOPCAR);
			int resumecar = cursor.getColumnIndex(RESUMECAR);
			int silent = cursor.getColumnIndex(SILENT);
			int loud = cursor.getColumnIndex(LOUD);
			int reset = cursor.getColumnIndex(RESET);
			int gprs_enable = cursor.getColumnIndex(GPRS_MODE);
			int gprs_disable = cursor.getColumnIndex(SMS_MODE);
			int adminip = cursor.getColumnIndex(ADMINIP);
			int noadminip = cursor.getColumnIndex(NOADMINIP);
			int apn = cursor.getColumnIndex(APN);
			int apn_user = cursor.getColumnIndex(APN_USER);
			int apn_password = cursor.getColumnIndex(APN_PASSWORD);
			int help_me = cursor.getColumnIndex(HELP_ME);
			int acc_enable = cursor.getColumnIndex(ACC_ENABLE);
			int acc_disable = cursor.getColumnIndex(ACC_DISABLE);
			int address = cursor.getColumnIndex(ADDRESS);
			int time_zone = cursor.getColumnIndex(TIME_ZONE);
			int factory_setting = cursor.getColumnIndex(FACTORY_SETTING);
			int nooil = cursor.getColumnIndex(NOOIL);
			int temperature = cursor.getColumnIndex(TEMPERATURE);
			int notemperature = cursor.getColumnIndex(NOTEMPERATURE);
			int photo = cursor.getColumnIndex(PHOTO);
			int sd_save = cursor.getColumnIndex(SD_SAVE);
			int sd_clear = cursor.getColumnIndex(SD_CLEAR);

			for (cursor.moveToFirst(); !(cursor.isAfterLast()); cursor
					.moveToNext()) {
				result = cursor.getString(rowid) + ","
						+ cursor.getString(vehicle) + ","
						+ cursor.getString(phone) + ","
						+ cursor.getString(password) + ","
						+ cursor.getString(admin) + ","
						+ cursor.getString(noadmin) + ","
						+ cursor.getString(auto_track) + ","
						+ cursor.getString(no_auto_track) + ","
						+ cursor.getString(monitor) + ","
						+ cursor.getString(tracker) + ","
						+ cursor.getString(change_password) + ","
						+ cursor.getString(arm) + ","
						+ cursor.getString(disarm) + ","
						+ cursor.getString(move) + ","
						+ cursor.getString(nomove) + ","
						+ cursor.getString(speed) + ","
						+ cursor.getString(nospeed) + ","
						+ cursor.getString(check_imei) + ","
						+ cursor.getString(check_tracker) + ","
						+ cursor.getString(stopcar) + ","
						+ cursor.getString(resumecar) + ","
						+ cursor.getString(silent) + ","
						+ cursor.getString(loud) + ","
						+ cursor.getString(reset) + ","
						+ cursor.getString(gprs_enable) + ","
						+ cursor.getString(gprs_disable) + ","
						+ cursor.getString(adminip) + ","
						+ cursor.getString(noadminip) + ","
						+ cursor.getString(apn) + ","
						+ cursor.getString(apn_user) + ","
						+ cursor.getString(apn_password) + ","
						+ cursor.getString(help_me) + ","
						+ cursor.getString(acc_enable) + ","
						+ cursor.getString(acc_disable) + ","
						+ cursor.getString(address) + ","
						+ cursor.getString(time_zone) + ","
						+ cursor.getString(factory_setting) + ","
						+ cursor.getString(nooil) + ","
						+ cursor.getString(temperature) + ","
						+ cursor.getString(notemperature) + ","
						+ cursor.getString(photo) + ","
						+ cursor.getString(sd_save) + ","
						+ cursor.getString(sd_clear) + ",";
			}
			cursor.close();
			if (result == null) {
				result = "0,0";
			}
		} catch (Exception e) {
			Log.i("Exeption selectRowidTracker():", e.getMessage());
		}
		return result;
	}

	public String selectLastCoords() {
		String result = null;
		try {
			Cursor cursor = db.query(TABLE_COORDS, new String[] { ROWID, PHONE,
					IMEI, VEHICLE, LATITUDE, LONGITUDE, SPEED, DATE, HOUR,
					POWER, ACC, DOOR }, null, null, null, null,
					ROWID + " DESC", "1");
			int phone = cursor.getColumnIndex(PHONE);
			int imei = cursor.getColumnIndex(IMEI);
			int vehicle = cursor.getColumnIndex(VEHICLE);
			int lat = cursor.getColumnIndex(LATITUDE);
			int lon = cursor.getColumnIndex(LONGITUDE);
			int speed = cursor.getColumnIndex(SPEED);
			int date = cursor.getColumnIndex(DATE);
			int hour = cursor.getColumnIndex(HOUR);
			int power = cursor.getColumnIndex(POWER);
			int acc = cursor.getColumnIndex(ACC);
			int door = cursor.getColumnIndex(DOOR);
			for (cursor.moveToFirst(); !(cursor.isAfterLast()); cursor
					.moveToNext()) {
				result = cursor.getString(phone) + "," + cursor.getString(imei)
						+ "," + cursor.getString(vehicle) + ","
						+ cursor.getString(lat) + "," + cursor.getString(lon)
						+ "," + cursor.getString(speed) + ","
						+ cursor.getString(date) + "," + cursor.getString(hour)
						+ "," + cursor.getString(power) + ","
						+ cursor.getString(acc) + "," + cursor.getString(door);
			}
			cursor.close();
			if (result == null) {
				result = "0,0,0,0,0,0,0,0,0,0,0";
			}
		} catch (Exception e) {
			Log.i("Exception selectLastCoord(): ", e.getMessage());
		}
		return result;
	}

	public String selectCoords(String id) {
		String result = null;
		try {
			Cursor cursor = db.query(TABLE_COORDS, new String[] { ROWID, PHONE,
					IMEI, VEHICLE, LATITUDE, LONGITUDE, SPEED, DATE, HOUR,
					POWER, ACC, DOOR, PHOTO }, ROWID + "= " + id, null, null,
					null, null, null);
			int phone = cursor.getColumnIndex(PHONE);
			int imei = cursor.getColumnIndex(IMEI);
			int vehicle = cursor.getColumnIndex(VEHICLE);
			int lat = cursor.getColumnIndex(LATITUDE);
			int lon = cursor.getColumnIndex(LONGITUDE);
			int speed = cursor.getColumnIndex(SPEED);
			int date = cursor.getColumnIndex(DATE);
			int hour = cursor.getColumnIndex(HOUR);
			int power = cursor.getColumnIndex(POWER);
			int acc = cursor.getColumnIndex(ACC);
			int door = cursor.getColumnIndex(DOOR);
			int photo = cursor.getColumnIndex(PHOTO);
			for (cursor.moveToFirst(); !(cursor.isAfterLast()); cursor
					.moveToNext()) {
				result = cursor.getString(phone) + "," + cursor.getString(imei)
						+ "," + cursor.getString(vehicle) + ","
						+ cursor.getString(lat) + "," + cursor.getString(lon)
						+ "," + cursor.getString(speed) + ","
						+ cursor.getString(date) + "," + cursor.getString(hour)
						+ "," + cursor.getString(power) + ","
						+ cursor.getString(acc) + "," + cursor.getString(door)
						+ "," + cursor.getString(photo);
			}
			cursor.close();
			if (result == null) {
				result = "0,0,0,0,0,0,0,0,0,0,0,0";
			}
		} catch (Exception e) {
			Log.i("Exception selectLastCoord(): ", e.getMessage());
		}
		return result;
	}

	public Cursor listLastCoords() {
		return db.query(TABLE_COORDS, new String[] { ROWID, IMEI, VEHICLE,
				LATITUDE, LONGITUDE, SPEED, DATE, HOUR }, null, null, null,
				null, null);
	}
}
