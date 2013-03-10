package com.jotracker.service;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import com.jotracker.receiver.SMSReceiver;

public class ServiceSMS extends Service {
	
	private SMSReceiver receiver = new SMSReceiver();

	@Override
	public void onCreate() {
		super.onCreate();
		registerReceiver(receiver, new IntentFilter(
				"android.provider.Telephony.SMS_RECEIVED"));
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(receiver);
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

}
