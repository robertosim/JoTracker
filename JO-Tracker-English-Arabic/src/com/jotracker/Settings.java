package com.jotracker;

import java.util.Locale;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.res.Configuration;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.jotracker.email.AutoEmailActivity;

public class Settings extends PreferenceActivity implements
		OnSharedPreferenceChangeListener {
	public SharedPreferences sharedPrefs;
	private String sound, language, photo_link;
	static final String PREFS_SOUND = "SOUND";
	static final String PREFS_LANGUAGE = "LANGUAGE";
	static final String PREFS_PHOTO_LINK = "PHOTO_LINK";
	static public final String PREF_RECORD_CALLS = "PREF_RECORD_CALLS";
	static public final String PREF_AUDIO_SOURCE = "PREF_AUDIO_SOURCE";
	static public final String PREF_AUDIO_FORMAT = "PREF_AUDIO_FORMAT";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);

		sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		sharedPrefs.registerOnSharedPreferenceChangeListener(this);
		setSound(sound = sharedPrefs.getString("sound", "alarm"));
		setLanguage(language = sharedPrefs.getString("language", "en"));
		setPhotoLink(sharedPrefs.getString("photo_link",
				"http://www.gpstrackerxy.com/AlarmImages/"));
	}

	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		// Let's do something a preference value changes
		if (key.equals("sound")) {
			sound = sharedPrefs.getString("sound", "alarm");
			// Log.e("PREFS_SOUND ", "onSharedPreferenceChanged()  :: " +
			// sound);
			Uri alert = Uri.parse("android.resource://" + getPackageName()
					+ "/raw/" + sound);
			Ringtone ring = RingtoneManager.getRingtone(this, alert);
			if (ring != null) {
				ring.play();
			}
			setSound(sound);
		}
		if (key.equals("language")) {
			language = sharedPrefs.getString("language", "en");
			// Log.e("PREFS_LANGUAGE ", "onSharedPreferenceChanged()  :: " +
			// language);
			setLanguage(language);
			Toast.makeText(Settings.this,
					getString(R.string.msg_change_language), Toast.LENGTH_LONG)
					.show();
		}
		if (key.equals("photo_link")) {
			photo_link = sharedPrefs.getString("photo_link",
					"http://www.gpstrackerxy.com/AlarmImages/");
			// Log.e("PREFS_PHOTO_LINK ", "onSharedPreferenceChanged()  :: " +
			// photo_link);
			setPhotoLink(photo_link);
		}
		if (key.equals("send_email")) {
			if (sharedPrefs.getBoolean("send_email", false)) {
				Intent autoemail = new Intent();
				autoemail.setClass(Settings.this, AutoEmailActivity.class);
				startActivity(autoemail);
			}
		}

	}

	public void setSound(String sound) {
		try {
			SharedPreferences settings = getSharedPreferences(PREFS_SOUND, 0);
			SharedPreferences.Editor editor = settings.edit();
			editor.putString("sound", sound);
			editor.commit();
			// Log.e("PREFS_SOUND ", "saveSound()  :: " + sound);
		} catch (Exception ex) {
			Log.e("PREFS_SOUND ", "saveSound()  :: " + ex.getMessage());
		}
	}

	private void setLanguage(String localeCode) {
		try {
			Locale locale = new Locale(localeCode);
			Locale.setDefault(locale);
			Configuration config = new Configuration();
			config.locale = locale;
			getBaseContext().getResources().updateConfiguration(config,
					getBaseContext().getResources().getDisplayMetrics());

			SharedPreferences settings = getSharedPreferences(PREFS_LANGUAGE, 0);
			SharedPreferences.Editor editor = settings.edit();
			editor.putString("language", language);
			editor.commit();
			// Log.e("PREFS_LANGUAGE ", "setLanguage()  :: " + language);
		} catch (Exception ex) {
			Log.e("PREFS_LANGUAGE ", "saveLanguage()  :: " + ex.getMessage());
		}
	}

	public String getPhotoLink() {
		return photo_link;
	}

	public void setPhotoLink(String photo_link) {
		try {
			SharedPreferences settings = getSharedPreferences(PREFS_PHOTO_LINK,
					0);
			SharedPreferences.Editor editor = settings.edit();
			editor.putString("photo_link", photo_link);
			editor.commit();
			// Log.e("PREFS_PHOTO_LINK ", "setPhotoLink()  :: " + photo_link);
		} catch (Exception ex) {
			Log.e("PREFS_PHOTO_LINK ", "setPhotoLink()  :: " + ex.getMessage());
		}
	}
}
