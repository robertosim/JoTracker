package com.jotracker.audio.record;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.jotracker.data.DBAdapter;

public class PhoneListener extends PhoneStateListener {
	private Context ctx;
	private DBAdapter db;
	private String phone_number, vehicle;
	private boolean recording = false;
	static public final String PREF_RECORD_CALLS = "PREF_RECORD_CALLS";
	static public final String PREF_AUDIO_SOURCE = "PREF_AUDIO_SOURCE";
	static public final String PREF_AUDIO_FORMAT = "PREF_AUDIO_FORMAT";
	static final String PREFS_PHONE_NUMBER = "phone_number";
	private String TAG;

	public PhoneListener(Context context) {
		TAG = context.getPackageName().toString();
		Log.i(TAG, "PhoneListener constructor ");
		this.ctx = context;
		db = new DBAdapter(context);
	}

	public void onCallStateChanged(int state, String incomingNumber) {
		Log.d(TAG, "PhoneListener::onCallStateChanged state:"
				+ state + " incomingNumber:" + incomingNumber.toString());
		switch (state) {
		case TelephonyManager.CALL_STATE_IDLE:
			if (recording) {
				Log.d(TAG, "CALL_STATE_IDLE, stoping recording");
				stopRecordService();
			}
			break;
		case TelephonyManager.CALL_STATE_RINGING:
			Log.d(" PhoneListener", "CALL_STATE_RINGING");
			break;
		case TelephonyManager.CALL_STATE_OFFHOOK:
			db.open();
			if (db.checkPhoneExist(getCallNum(ctx))) {
				
				vehicle = db.getVehicleName(phone_number);
				startRecordService();
				recording = true;
				Log.d(TAG,
						"CALL_STATE_OFFHOOK starting recording. Tracker phone number exist "
								+ phone_number +" "+incomingNumber);
			} else {
				Log.d(TAG,
						"CALL_STATE_OFFHOOK not starting recording. Tracker phone number not exist "
								+ phone_number +" "+ incomingNumber);
			}
			db.close();
			break;
		}
	}

	public void startRecordService() {
		Intent service = new Intent("com.jotracker.service.RecordService");
		service.putExtra("vehicle", vehicle);
		service.putExtra("phone", phone_number);
		ComponentName name = ctx.startService(service);
		if (null == name) {
			Log.e(TAG,
					"startService for RecordService returned null ComponentName");
		} else {
			Log.i(TAG,
					"startService returned " + name.flattenToString());
		}
		Log.d(TAG, "startRecordService()");
	}

	public void stopRecordService() {
		Intent service = new Intent("com.jotracker.service.RecordService");
		ctx.stopService(service);
		Log.d(TAG, "stopRecordService()");
	}

	private String getCallNum(Context context) {
		try {
			SharedPreferences sp = context.getSharedPreferences(PREFS_PHONE_NUMBER, 0);
			phone_number = sp.getString("phone_number", "");
			Log.i(TAG, "getCallNum("+phone_number+")");
		} catch (Exception ex) {
			Log.e(TAG, "Exception getCallNum()  ::" + ex.getMessage());
		}
		return phone_number;
	}

}
