package com.jotracker;

import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.jotracker.common.IActionListener;
import com.jotracker.common.Utilities;
import com.jotracker.email.AutoEmailHelper;

public class Login extends Activity implements IActionListener {

	private String login, password, language, email, photo_link;
	private boolean smtp_ssl;
	private Button btnSave, btnLogin;
	private EditText etEmail, etLogin, etPass;
	public SharedPreferences sharedPrefs;
	private AlertDialog.Builder alert;
	private ProgressDialog progress;

	private final Handler handler = new Handler();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setPhotoLink();
		getLanguage();
		if (checkLogin()) {
			setContentView(R.layout.login);
			btnLogin = (Button) findViewById(R.id.btnLogin);
			
			btnLogin.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					// get user name and password
					etLogin = (EditText) findViewById(R.id.etLogin);
					etPass = (EditText) findViewById(R.id.etPass);
					if (doLogin(etLogin.getText().toString(), etPass.getText()
							.toString())) {
						Toast.makeText(
								Login.this,
								getString(R.string.welcome) + " "
										+ etLogin.getText() + "!",
								Toast.LENGTH_LONG).show();
						Intent mainIntent = new Intent(getBaseContext(),
								Main.class);
						mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						startActivity(mainIntent);
						finish();
					} else {
						alert = new AlertDialog.Builder(Login.this);
						alert.setTitle(R.string.error_login);
						alert.setMessage(getString(R.string.remember) + " "
								+ getEmail() + "?");
						alert.setPositiveButton(R.string.ok,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int whichButton) {
										AutoEmailHelper aeh = new AutoEmailHelper(
												null, Login.this);
										aeh.RecoverPassWordAndLoginByEmail(
												getLogin(), getPassword(),
												Login.this);
										progress = new ProgressDialog(
												Login.this);
										progress.setMessage(getString(R.string.wait));
										progress.setCancelable(false);
										progress.show();

									}

								});
						alert.setNegativeButton(R.string.cancel,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int whichButton) {
										// Don\n something!
									}
								});
						alert.show();
						Toast.makeText(Login.this,
								getString(R.string.error_login),
								Toast.LENGTH_LONG).show();
					}

				}
			});/**/
		} else {
			setContentView(R.layout.add_login);
			btnSave = (Button) findViewById(R.id.btnSave);
			btnSave.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					// Add user name and password
					etEmail = (EditText) findViewById(R.id.etEmail);
					etLogin = (EditText) findViewById(R.id.etLogin);
					etPass = (EditText) findViewById(R.id.etPass);
					saveLogin(etEmail.getText().toString(), etLogin.getText()
							.toString(), etPass.getText().toString());
					if (doLogin(etLogin.getText().toString(), etPass.getText()
							.toString())) {
						Toast.makeText(
								Login.this,
								getString(R.string.welcome) + " "
										+ etLogin.getText() + "!",
								Toast.LENGTH_LONG).show();
						Intent mainIntent = new Intent(getBaseContext(),
								Main.class);
						mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						startActivity(mainIntent);
						finish();
					} else {
						alert = new AlertDialog.Builder(Login.this);
						alert.setTitle(R.string.error_login);
						alert.setMessage(R.string.remember + " " + getEmail()
								+ "?");
						alert.setPositiveButton(R.string.ok,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int whichButton) {

										AutoEmailHelper aeh = new AutoEmailHelper(
												null, Login.this);
										aeh.RecoverPassWordAndLoginByEmail(
												getLogin(), getPassword(),
												Login.this);
										progress = new ProgressDialog(
												Login.this);
										progress.setMessage(getString(R.string.wait));
										progress.setCancelable(false);
										progress.show();

									}
								});
						alert.setNegativeButton(R.string.cancel,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int whichButton) {
										// Don\n something!
									}
								});
						alert.show();
						Toast.makeText(Login.this,
								getString(R.string.error_login),
								Toast.LENGTH_LONG).show();
					}
				}
			});
		}
	}

	private boolean checkLogin() {
		try {
			sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
			login = sharedPrefs.getString("login", "0");
			password = sharedPrefs.getString("password", "0");
			if (login.equals("0") && password.equals("0"))
				return false;
			else
				return true;
		} catch (Exception ex) {
			Log.e("LOGIN ", "Exception checkLogin()  ::" + ex.getMessage());
		}
		return true;
	}

	private boolean doLogin(String l, String p) {
		try {
			sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
			login = sharedPrefs.getString("login", "0");
			password = sharedPrefs.getString("password", "0");
			if (login.equals(l) && password.equals(p))
				return true;
			else
				return false;
		} catch (Exception ex) {
			Log.e("LOGIN ", "Exception doLogin()  ::" + ex.getMessage());
		}
		return false;
	}

	private void saveLogin(String e, String l, String p) {
		try {
			sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
			SharedPreferences.Editor editor = sharedPrefs.edit();
			editor.putString("email", e);
			editor.putString("login", l);
			editor.putString("password", p);
			editor.commit();
		} catch (Exception ex) {
			Log.e("LOGIN ", "Exception saveLogin()  ::" + ex.getMessage());
		}
	}

	private void getLanguage() {
		try {
			sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
			language = sharedPrefs.getString("language", "en");
			Locale locale = new Locale(language);
			Locale.setDefault(locale);
			Configuration config = new Configuration();
			config.locale = locale;
			getBaseContext().getResources().updateConfiguration(config,
					getBaseContext().getResources().getDisplayMetrics());

		} catch (Exception ex) {
			Log.e("PREFS_LANGUAGE ",
					"Exception getLanguage()  ::" + ex.getMessage());
		}
	}

	public String getEmail() {
		try {
			sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
			email = sharedPrefs.getString("email", "your@email.com");
		} catch (Exception ex) {
			Log.e("LOGIN ", "Exception getLogin()  ::" + ex.getMessage());
		}
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getLogin() {
		try {
			sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
			login = sharedPrefs.getString("login", "0");
		} catch (Exception ex) {
			Log.e("LOGIN ", "Exception getLogin()  ::" + ex.getMessage());
		}
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword() {
		try {
			sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
			password = sharedPrefs.getString("password", "0");
		} catch (Exception ex) {
			Log.e("LOGIN ", "Exception getPassword()  ::" + ex.getMessage());
		}
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPhotoLink() {
		return photo_link;
	}

	public void setPhotoLink() {
		try {
			sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
			this.photo_link = sharedPrefs.getString("photo_link",
					" http://www.gpstrackerxy.com/AlarmImages/");
		} catch (Exception ex) {
			Log.e("LOGIN ", "Exception getPhotoLink()  ::" + ex.getMessage());
		}
	}

	private final Runnable successfullySent = new Runnable() {
		public void run() {
			SuccessfulSending();
		}
	};

	private final Runnable failedSend = new Runnable() {

		public void run() {
			FailureSending();
		}
	};

	private void FailureSending() {
		Utilities.HideProgress();
		Utilities.MsgBox(getString(R.string.sorry),
				getString(R.string.error_connection), this);
	}

	private void SuccessfulSending() {
		Utilities.HideProgress();
		Utilities.MsgBox(getString(R.string.success),
				getString(R.string.remember_sucess)+" "+getEmail().toString(), this);
	}

	public void OnComplete() {
		progress.dismiss();
		handler.post(successfullySent);
	}

	public void OnFailure() {
		progress.dismiss();
		handler.post(failedSend);
	}
}
