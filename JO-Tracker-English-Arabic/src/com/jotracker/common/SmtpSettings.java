package com.jotracker.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class SmtpSettings {
	private String smtp_server, smtp_port, smtp_username, smtp_password,
			autoemail_target, smtp_from;
	private boolean smtp_ssl;
	public SharedPreferences sharedPrefs;
	public Context context;

	public SmtpSettings(Context context) {
		this.context = context;
	}

	public String getSmtpFrom() {
		try {
			sharedPrefs = PreferenceManager
					.getDefaultSharedPreferences(context);
			this.smtp_from = sharedPrefs.getString("smtp_from", "");
		} catch (Exception ex) {
			Log.e("LOGIN ", "Exception getFrom()  ::" + ex.getMessage());
		}
		return smtp_from;
	}

	public String getTarget() {
		try {
			sharedPrefs = PreferenceManager
					.getDefaultSharedPreferences(context);
			this.autoemail_target = sharedPrefs.getString("autoemail_target",
					"");
		} catch (Exception ex) {
			Log.e("LOGIN ", "Exception getTarget()  ::" + ex.getMessage());
		}
		return autoemail_target;
	}

	public boolean isUseSsl() {
		try {
			sharedPrefs = PreferenceManager
					.getDefaultSharedPreferences(context);
			this.smtp_ssl = sharedPrefs.getBoolean("smtp_ssl", false);
		} catch (Exception ex) {
			Log.e("LOGIN ", "Exception isUseSsl()  ::" + ex.getMessage());
		}
		return smtp_ssl;
	}

	public String getStmpPassword() {
		try {
			sharedPrefs = PreferenceManager
					.getDefaultSharedPreferences(context);
			this.smtp_password = sharedPrefs.getString("smtp_password", "");
		} catch (Exception ex) {
			Log.e("LOGIN ",
					"Exception getEmailPassword()  ::" + ex.getMessage());
		}
		return smtp_password;
	}

	public String getSmtpUsername() {
		try {
			sharedPrefs = PreferenceManager
					.getDefaultSharedPreferences(context);
			this.smtp_username = sharedPrefs.getString("smtp_username", "");
		} catch (Exception ex) {
			Log.e("LOGIN ",
					"Exception getEmailUsername()  ::" + ex.getMessage());
		}
		return smtp_username;
	}

	public String getSmtpPort() {
		try {
			sharedPrefs = PreferenceManager
					.getDefaultSharedPreferences(context);
			this.smtp_port = sharedPrefs.getString("smtp_port", "");
		} catch (Exception ex) {
			Log.e("LOGIN ", "Exception getSmtpPort()  ::" + ex.getMessage());
		}
		return smtp_port;
	}

	public String getSmtpServer() {
		try {
			sharedPrefs = PreferenceManager
					.getDefaultSharedPreferences(context);
			this.smtp_server = sharedPrefs.getString("smtp_server", "");
		} catch (Exception ex) {
			Log.e("LOGIN ", "Exception getSmtpServer()  ::" + ex.getMessage());
		}
		return smtp_server;
	}
}
