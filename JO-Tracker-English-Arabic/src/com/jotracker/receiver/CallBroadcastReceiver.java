package com.jotracker.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.jotracker.audio.record.PhoneListener;

public class CallBroadcastReceiver extends BroadcastReceiver {
	private String numberToCall = null;
	static final String PREFS_PHONE_NUMBER = "phone_number";
	private String LOG = "CallBroadcastReceiver";

	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
			numberToCall = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
			Log.d(LOG, "CallBroadcastReceiver intent has EXTRA_PHONE_NUMBER: "
					+ numberToCall);
			PhoneListener listener = new PhoneListener(context);
			TelephonyManager telephony = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			telephony.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
			Log.d(LOG + "::onReceive()",
					"set PhoneStateListener, call number = " + numberToCall);
			setCallNum(context, numberToCall);
		}
	}

	private void setCallNum(Context context, String num) {
		try {
			SharedPreferences sp = context.getSharedPreferences(
					PREFS_PHONE_NUMBER, 0);
			SharedPreferences.Editor editor = sp.edit();
			editor.putString("phone_number", num);
			editor.commit();
			Log.i(LOG, "setCallNum(" + num + ")");
		} catch (Exception ex) {
			Log.e(LOG, "Exception setCallNum()  ::" + ex.getMessage());
		}
	}
}
