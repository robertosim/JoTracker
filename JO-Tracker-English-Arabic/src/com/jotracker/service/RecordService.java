package com.jotracker.service;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import com.jotracker.R;
import com.jotracker.Settings;
import com.jotracker.data.DBAdapter;

@SuppressLint("SdCardPath")
public class RecordService extends Service implements
		MediaRecorder.OnInfoListener, MediaRecorder.OnErrorListener {
	private static final String TAG = "RecordService";

	public static final String DEFAULT_STORAGE_LOCATION = "/sdcard/JoTracker/Audio";
	private static final int RECORDING_NOTIFICATION_ID = 1;
	private MediaRecorder recorder = null;
	private boolean isRecording = false;
	private File recording = null;;
	private String prefix = "Audio_", suffix = "", vehicle = "", phone, date,
			hour, file;
	private DBAdapter db;
	private AudioManager audioManager;

	private File makeOutputFile(SharedPreferences prefs) {
		File dir = new File(DEFAULT_STORAGE_LOCATION);

		// SmtpSettings dir for existence and writeability
		if (!dir.exists()) {
			try {
				dir.mkdirs();
			} catch (Exception e) {
				Log.e(TAG,
						"RecordService::makeOutputFile unable to create directory "
								+ dir + ": " + e);
				// Toast t = Toast.makeText(getApplicationContext(),
				// "JoTracker was unable to create the directory " + dir +
				// " to store recordings: " + e, Toast.LENGTH_LONG);
				// t.show();
				return null;
			}
		} else {
			if (!dir.canWrite()) {
				Log.e(TAG,
						"RecordService::makeOutputFile does not have write permission for directory: "
								+ dir);
				// Toast t = Toast.makeText(getApplicationContext(),
				// "JoTracker does not have write permission for the directory directory "
				// + dir + " to store recordings", Toast.LENGTH_LONG);
				// t.show();
				return null;
			}
		}

		// SmtpSettings size

		// create filename based on call data
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss_");
		prefix += sdf.format(new Date());
		// add info to file name about what audio channel we were recording
		// int audiosource =
		// Integer.parseInt(prefs.getString(Settings.PREF_AUDIO_SOURCE, "1"));
		// prefix += "_channel" + audiosource + "_";
		Log.e(TAG, "RecordService::makeOutputFile file name: " + prefix);
		// create suffix based on format
		int audioformat = Integer.parseInt(prefs.getString(
				Settings.PREF_AUDIO_FORMAT, "1"));
		switch (audioformat) {
		case MediaRecorder.OutputFormat.THREE_GPP:
			suffix = ".3gpp";
			break;
		case MediaRecorder.OutputFormat.MPEG_4:
			suffix = ".mpg";
			break;
		case MediaRecorder.OutputFormat.RAW_AMR:
			suffix = ".amr";
			break;
		}

		try {
			return File.createTempFile(prefix, suffix, dir);
		} catch (IOException e) {
			Log.e(TAG,
					"RecordService::makeOutputFile unable to create temp file "
							+ prefix + suffix + " in " + dir + ": " + e);
			// Toast t = Toast.makeText(getApplicationContext(),
			// "JoTracker was unable to create temp file in " + dir + ": " + e,
			// Toast.LENGTH_LONG);
			// t.show();
			return null;
		}
	}

	public void onCreate() {
		super.onCreate();
		recorder = new MediaRecorder();
		db = new DBAdapter(getApplicationContext());
	}

	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);

		setMicrophoneMute(true);

		Bundle extras = intent.getExtras();
		if (extras != null) {
			vehicle = extras.getString("vehicle");
			phone = extras.getString("phone");
		}

		Log.i(TAG, "onStart vehicle = " + vehicle + ", phone = " + phone);

		Log.i(TAG, "RecordService::onStartCommand called while isRecording:"
				+ isRecording);

		if (isRecording)
			return;

		Context c = getApplicationContext();
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(c);

		Boolean shouldRecord = prefs.getBoolean(Settings.PREF_RECORD_CALLS,
				false);
		if (!shouldRecord) {
			Log.i(TAG,
					"RecordService::onStartCommand with PREF_RECORD_CALLS false, not recording");
			// return START_STICKY;
			return;
		}

		int audiosource = Integer.parseInt(prefs.getString(
				Settings.PREF_AUDIO_SOURCE, "1"));
		int audioformat = Integer.parseInt(prefs.getString(
				Settings.PREF_AUDIO_FORMAT, "1"));

		recording = makeOutputFile(prefs);
		if (recording == null) {
			recorder = null;
			return; // return 0;
		}

		Log.i(TAG, "RecordService will config MediaRecorder with audiosource: "
				+ audiosource + " audioformat: " + audioformat);
		try {
			// These calls will throw exceptions unless you set the
			// android.permission.RECORD_AUDIO permission for your app
			recorder.reset();
			recorder.setAudioSource(audiosource);
			Log.d("CallRecorder", "set audiosource " + audiosource);
			recorder.setOutputFormat(audioformat);
			Log.d("CallRecorder", "set output " + audioformat);
			recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
			Log.d("CallRecorder", "set encoder default");
			recorder.setOutputFile(recording.getAbsolutePath());
			Log.d("CallRecorder", "set file: " + recording);
			// recorder.setMaxDuration(msDuration); //1000); // 1 seconds
			// recorder.setMaxFileSize(bytesMax); //1024*1024); // 1KB

			recorder.setOnInfoListener(this);
			recorder.setOnErrorListener(this);

			try {
				recorder.prepare();
			} catch (java.io.IOException e) {
				Log.e(TAG,
						"RecordService::onStart() IOException attempting recorder.prepare()\nJoTracker was unable to start recording: "
								+ e);
				// Toast t = Toast.makeText(getApplicationContext(),
				// "JoTracker was unable to start recording: " + e,
				// Toast.LENGTH_LONG);
				// t.show();
				recorder = null;
				return; // return 0; //START_STICKY;
			}
			Log.d(TAG, "recorder.prepare() returned");

			recorder.start();
			isRecording = true;
			Log.i(TAG, "recorder.start() returned");
			updateNotification(true);
		} catch (java.lang.Exception e) {
			// Toast t = Toast.makeText(getApplicationContext(),
			// "JoTracker was unable to start recording: " + e,
			// Toast.LENGTH_LONG);
			// t.show();

			Log.e(TAG, "RecordService::onStart caught unexpected exception", e);
			recorder = null;
		}

		return; // return 0; //return START_STICKY;
	}

	public void onDestroy() {
		super.onDestroy();

		if (null != recorder) {
			Log.i(TAG, "RecordService::onDestroy calling recorder.release()");
			isRecording = false;
			recorder.release();
			if (recording != null) {
				setFile(recording.getName().toString());
				if (!getFile().equals("") || getFile() != null) {
					db.open();
					db.insertVoices(phone, "", vehicle, "", "", "", getDate(),
							getHour(), "", "", "", "", "", "", getFile());
					db.close();
					Log.i(TAG,
							"RecordService::onDestroy insertVoices() file = "
									+ getFile());
				}
			}

			// Toast t = Toast.makeText(getApplicationContext(),
			// "JoTracker finished recording call to " + recording,
			// Toast.LENGTH_LONG);
			// t.show();
		}
		setMicrophoneMute(false);
		updateNotification(false);
	}

	// methods to handle binding the service

	public IBinder onBind(Intent intent) {
		return null;
	}

	public boolean onUnbind(Intent intent) {
		return false;
	}

	public void onRebind(Intent intent) {
	}

	private void updateNotification(Boolean status) {
		Context c = getApplicationContext();
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(c);

		String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);

		if (status) {
			int icon = R.drawable.rec;
			CharSequence tickerText = "Recording call to " + vehicle;
			long when = System.currentTimeMillis();

			Notification notification = new Notification(icon, tickerText, when);

			Context context = getApplicationContext();
			CharSequence contentTitle = "Call Recorder Status";
			CharSequence contentText = "Recording call to " + vehicle + "...";
			Intent notificationIntent = new Intent(this, RecordService.class);
			PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
					notificationIntent, 0);

			notification.setLatestEventInfo(context, contentTitle, contentText,
					contentIntent);
			mNotificationManager
					.notify(RECORDING_NOTIFICATION_ID, notification);
		} else {
			mNotificationManager.cancel(RECORDING_NOTIFICATION_ID);
		}
	}

	// MediaRecorder.OnInfoListener
	public void onInfo(MediaRecorder mr, int what, int extra) {
		Log.i(TAG,
				"RecordService got MediaRecorder onInfo callback with what: "
						+ what + " extra: " + extra);
		isRecording = false;
	}

	// MediaRecorder.OnErrorListener
	public void onError(MediaRecorder mr, int what, int extra) {
		Log.e(TAG,
				"RecordService got MediaRecorder onError callback with what: "
						+ what + " extra: " + extra);
		isRecording = false;
		mr.release();
	}

	private String getDate() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return date = sdf.format(new Date());
	}

	private String getHour() {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		return hour = sdf.format(new Date());
	}

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file.replaceAll("/sdcard/JoTracker/Audio/", "");
	}

	public void setMicrophoneMute(boolean on) {
		audioManager = ((AudioManager) getApplicationContext()
				.getSystemService(Context.AUDIO_SERVICE));
		audioManager.setMode(AudioManager.MODE_IN_CALL);
		audioManager.setMicrophoneMute(on);
	}
}
