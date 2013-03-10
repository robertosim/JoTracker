package com.jotracker;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.jotracker.data.DBAdapter;

public class SettingsTrackers extends Activity implements View.OnClickListener {
	// private static final String LOG = "SettingsTrackers";

	private DBAdapter db;
	private String result, rowid, vehicle, phone, password, admin, noadmin,
			auto_track, no_auto_track, adminip, no_adminip, monitor, tracker,
			change_password, arm, disarm, move, nomove, speed, nospeed,
			check_imei, check_tracker, stopcar, resumecar, silent, loud, reset,
			acc_anable, acc_disable, gprs_mode, sms_mode, apn, apn_user,
			apn_password, help_me, address, time_zone, begin, nooil,
			temperature, notemperature, photo, sd_save, sd_clear;
	protected static int choice = -2;
	private EditText txtVehicle, txtPhone, txtPassword, txtAdmin, txtNoAdmin,
			txtAutoTrack, txtNoAutoTrack, txtAdminIp, txtNoAdminIp, txtMonitor,
			txtTracker, txtChangePassword, txtArm, txtDisarm, txtMove,
			txtNoMove, txtSpeed, txtNoSpeed, txtCheckImei, txtCheckTracker,
			txtStopcar, txtResumecar, txtSilent, txtLoud, txtReset,
			txtAccEnable, txtAccDisable, txtGprsMode, txtSmsMode, txtApn,
			txtApnUser, txtApnPassword, txtHelpMe, txtAddress, txtTimeZone,
			txtBegin, txtNooil, txtTemperature, txtNotemperature, txtPhoto,
			txtSdSave, txtSdClear;
	private Button btnGoBack, btnSave;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.trackers_settings);

		db = new DBAdapter(SettingsTrackers.this);

		Bundle extras = getIntent().getExtras();
		vehicle = extras.getString("vehicle");

		txtVehicle = (EditText) findViewById(R.id.etVehicle);
		txtPhone = (EditText) findViewById(R.id.etPhone);
		txtPassword = (EditText) findViewById(R.id.etPassword);
		txtAdmin = (EditText) findViewById(R.id.etAdmin);
		txtNoAdmin = (EditText) findViewById(R.id.etNoAdmin);
		txtAutoTrack = (EditText) findViewById(R.id.etAutoTrack);
		txtNoAutoTrack = (EditText) findViewById(R.id.etNoAutoTrack);
		txtMonitor = (EditText) findViewById(R.id.etMonitor);
		txtTracker = (EditText) findViewById(R.id.etTracker);
		txtChangePassword = (EditText) findViewById(R.id.etPasswordChange);
		txtArm = (EditText) findViewById(R.id.etArm);
		txtDisarm = (EditText) findViewById(R.id.etDisarm);
		txtMove = (EditText) findViewById(R.id.etMove);
		txtNoMove = (EditText) findViewById(R.id.etNoMove);
		txtSpeed = (EditText) findViewById(R.id.etOverSpeed);
		txtNoSpeed = (EditText) findViewById(R.id.etNoOverSpeed);
		txtCheckImei = (EditText) findViewById(R.id.etCheckImei);
		txtCheckTracker = (EditText) findViewById(R.id.etCheckTracker);
		txtStopcar = (EditText) findViewById(R.id.etStopcar);
		txtResumecar = (EditText) findViewById(R.id.etResumecar);
		txtSilent = (EditText) findViewById(R.id.etSilent);
		txtLoud = (EditText) findViewById(R.id.etLoud);
		txtReset = (EditText) findViewById(R.id.etReset);
		txtAccEnable = (EditText) findViewById(R.id.etAccEnable);
		txtAccDisable = (EditText) findViewById(R.id.etAccDisable);
		txtAdminIp = (EditText) findViewById(R.id.etAdminIp);
		txtNoAdminIp = (EditText) findViewById(R.id.etNoAdminIp);
		txtGprsMode = (EditText) findViewById(R.id.etGprsEnable);
		txtSmsMode = (EditText) findViewById(R.id.etGprsDisable);
		txtApn = (EditText) findViewById(R.id.etApn);
		txtApnUser = (EditText) findViewById(R.id.etApnUser);
		txtApnPassword = (EditText) findViewById(R.id.etApnPassword);
		txtHelpMe = (EditText) findViewById(R.id.etHelpMe);
		txtAddress = (EditText) findViewById(R.id.etAddress);
		txtTimeZone = (EditText) findViewById(R.id.etTimeZone);
		txtBegin = (EditText) findViewById(R.id.etBegin);
		txtNooil = (EditText) findViewById(R.id.etNooil);
		txtTemperature = (EditText) findViewById(R.id.etTemperature);
		txtNotemperature = (EditText) findViewById(R.id.etNotemperature);
		txtPhoto = (EditText) findViewById(R.id.etPhoto);
		txtSdSave = (EditText) findViewById(R.id.etSdSave);
		txtSdClear = (EditText) findViewById(R.id.etSdClear);
		/**/
		if (extras != null) {
			try {
				db.open();
				result = db.selectRowidTracker(vehicle);
				db.close();
				Log.d("Log:", " selectRowidTracker():" + result);
				String t[] = result.split(",");

				rowid = t[0];
				phone = t[1];
				password = t[2];
				admin = t[3];
				noadmin = t[4];
				auto_track = t[5];
				no_auto_track = t[6];
				monitor = t[7];
				tracker = t[8];
				change_password = t[9];
				arm = t[10];
				disarm = t[11];
				move = t[12];
				nomove = t[13];
				speed = t[14];
				nospeed = t[15];
				check_imei = t[16];
				check_tracker = t[17];
				stopcar = t[18];
				resumecar = t[19];
				silent = t[20];
				loud = t[21];
				reset = t[22];
				adminip = t[23];
				no_adminip = t[24];
				gprs_mode = t[25];
				sms_mode = t[26];
				apn = t[27];
				apn_user = t[28];
				apn_password = t[29];
				help_me = t[30];
				acc_anable = t[31];
				acc_disable = t[32];
				address = t[33];
				time_zone = t[34];
				begin = t[35];
				nooil = t[36];
				temperature = formatDisplay(t[37]);
				notemperature = t[38];
				photo = t[39];
				sd_save = t[40];
				sd_clear = t[41];

				txtVehicle.setText(vehicle);
				txtPhone.setText(phone);
				txtPassword.setText(password);
				txtAdmin.setText(formatDisplay(admin));
				txtNoAdmin.setText(formatDisplay(noadmin));
				txtAutoTrack.setText(formatDisplay(auto_track));
				txtNoAutoTrack.setText(formatDisplay(no_auto_track));
				txtMonitor.setText(formatDisplay(monitor));
				txtTracker.setText(formatDisplay(tracker));
				txtChangePassword.setText(formatDisplay(change_password));
				txtArm.setText(formatDisplay(arm));
				txtDisarm.setText(formatDisplay(disarm));
				txtMove.setText(formatDisplay(move));
				txtNoMove.setText(formatDisplay(nomove));
				txtSpeed.setText(formatDisplay(speed));
				txtNoSpeed.setText(formatDisplay(nospeed));
				txtCheckImei.setText(formatDisplay(check_imei));
				txtCheckTracker.setText(formatDisplay(check_tracker));
				txtStopcar.setText(formatDisplay(stopcar));
				txtResumecar.setText(formatDisplay(resumecar));
				txtSilent.setText(formatDisplay(silent));
				txtLoud.setText(formatDisplay(loud));
				txtReset.setText(formatDisplay(reset));
				txtAccEnable.setText(formatDisplay(acc_anable));
				txtAccDisable.setText(formatDisplay(acc_disable));
				txtGprsMode.setText(formatDisplay(gprs_mode));
				txtSmsMode.setText(formatDisplay(sms_mode));
				txtAdminIp.setText(formatDisplay(adminip));
				txtNoAdminIp.setText(formatDisplay(no_adminip));
				txtApn.setText(formatDisplay(apn));
				txtApnUser.setText(formatDisplay(apn_user));
				txtApnPassword.setText(formatDisplay(apn_password));
				txtHelpMe.setText(formatDisplay(help_me));
				txtAddress.setText(formatDisplay(address));
				txtTimeZone.setText(formatDisplay(time_zone));
				txtBegin.setText(formatDisplay(begin));
				txtNooil.setText(formatDisplay(nooil));
				txtTemperature.setText(formatDisplay(temperature));
				txtNotemperature.setText(formatDisplay(notemperature));
				txtPhoto.setText(formatDisplay(photo));
				txtSdSave.setText(formatDisplay(sd_save));
				txtSdClear.setText(formatDisplay(sd_clear));
				txtAdmin.requestFocus();

			} catch (Exception e) {
				Toast.makeText(SettingsTrackers.this,
						"Exceção: " + e.getMessage(), Toast.LENGTH_LONG).show();
			}
		}

		btnGoBack = (Button) findViewById(R.id.btnGoBack);
		btnGoBack.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});

		btnSave = (Button) findViewById(R.id.btnSave);
		btnSave.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				String admin, noadmin, auto_track, no_auto_track, monitor, tracker, changepassword, arm, disarm, move, nomove, speed, nospeed, checkimei, checktracker, stopcar, resumecar, silent, loud, reset, gprs_mode, sms_mode, acc_enable, acc_disable, adminip, noadminip, apn, apenuser, apnpass, help_me, address, time_zone, begin, nooil, temperature, notemperature, photo, sd_save, sd_clear;
				admin = txtAdmin.getText().toString();
				noadmin = txtNoAdmin.getText().toString();
				auto_track = txtAutoTrack.getText().toString();
				no_auto_track = txtNoAutoTrack.getText().toString();
				monitor = txtMonitor.getText().toString();
				tracker = txtTracker.getText().toString();
				changepassword = txtChangePassword.getText().toString();
				arm = txtArm.getText().toString();
				disarm = txtDisarm.getText().toString();
				move = txtMove.getText().toString();
				nomove = txtNoMove.getText().toString();
				speed = txtSpeed.getText().toString();
				nospeed = txtNoSpeed.getText().toString();
				checkimei = txtCheckImei.getText().toString();
				checktracker = txtCheckTracker.getText().toString();
				stopcar = txtStopcar.getText().toString();
				resumecar = txtResumecar.getText().toString();
				silent = txtSilent.getText().toString();
				loud = txtLoud.getText().toString();
				reset = txtReset.getText().toString();
				acc_enable = txtAccEnable.getText().toString();
				acc_disable = txtAccDisable.getText().toString();
				gprs_mode = txtGprsMode.getText().toString();
				sms_mode = txtSmsMode.getText().toString();
				adminip = txtAdminIp.getText().toString();
				noadminip = txtNoAdminIp.getText().toString();
				apn = txtApn.getText().toString();
				apenuser = txtApnUser.getText().toString();
				apnpass = txtApnPassword.getText().toString();
				help_me = txtHelpMe.getText().toString();
				address = txtAddress.getText().toString();
				time_zone = txtTimeZone.getText().toString();
				begin = txtBegin.getText().toString();
				nooil = txtNooil.getText().toString();
				temperature = txtTemperature.getText().toString();
				notemperature = txtNotemperature.getText().toString();
				photo = txtPhoto.getText().toString();
				sd_save = txtSdSave.getText().toString();
				sd_clear = txtSdClear.getText().toString();
				db.open();
				if (db.updateSettingsTrackers(rowid, formatCommand(admin),
						formatCommand(noadmin), formatCommand(auto_track),
						formatCommand(no_auto_track), formatCommand(monitor),
						formatCommand(tracker), formatCommand(changepassword),
						formatCommand(arm), formatCommand(disarm),
						formatCommand(move), formatCommand(nomove),
						formatCommand(speed), formatCommand(nospeed),
						formatCommand(checkimei), formatCommand(checktracker),
						formatCommand(stopcar), formatCommand(resumecar),
						formatCommand(silent), formatCommand(loud),
						formatCommand(reset), formatCommand(adminip),
						formatCommand(noadminip), formatCommand(gprs_mode),
						formatCommand(sms_mode), formatCommand(apn),
						formatCommand(apenuser), formatCommand(apnpass),
						formatCommand(help_me), formatCommand(acc_enable),
						formatCommand(acc_disable), formatCommand(address),
						formatCommand(time_zone), formatCommand(begin),
						formatCommand(nooil), formatCommand(temperature),
						formatCommand(notemperature), formatCommand(photo),
						formatCommand(sd_save), formatCommand(sd_clear))) {
					Toast.makeText(SettingsTrackers.this,
							getString(R.string.save_success), Toast.LENGTH_LONG)
							.show();
					finish();
				} else {
					Toast.makeText(SettingsTrackers.this, "Não atualizado! ",
							Toast.LENGTH_LONG).show();
					finish();
				}
				db.close();
			}
		});
		/**/

	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Log.d(LOG, " onConfigurationChanged()");
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		// Log.d(LOG, " onSaveInstanceState()");
	}

	// This is used to determine if the Splash page should be displayed before
	// the
	// Activity is launched.
	@Override
	protected void onResume() {
		super.onResume();
		// Log.d(LOG, " onResume()");
	}

	// storing a timestamp when the user quits the app
	@Override
	protected void onPause() {
		super.onPause();
		// Log.d(LOG, " onPause()");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// Log.d(LOG, " onDestroy()");
	}

	public void onClick(View arg0) {
		// rgOperationMode.clearCheck();
	}

	public String formatDisplay(String command) {
		return command.replaceAll("#", ",");
	}

	public String formatCommand(String command) {
		command = command.replaceAll(",", "#");
		return notEmpty(command);
	}

	public String notEmpty(String command) {
		if (command.equals("")) {
			command = " ";
		}
		return command;
	}

	public String getNooil() {
		return nooil;
	}

	public void setNooil(String nooil) {
		this.nooil = nooil;
	}

	public String getTemperature() {
		return temperature;
	}

	public void setTemperature(String temperature) {
		this.temperature = temperature;
	}

	public String getNotemperature() {
		return notemperature;
	}

	public void setNotemperature(String notemperature) {
		this.notemperature = notemperature;
	}

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}

	public String getSdSave() {
		return sd_save;
	}

	public void setSdSave(String sd_save) {
		this.sd_save = sd_save;
	}

	public String getSdClear() {
		return sd_clear;
	}

	public void setSdClear(String sd_clear) {
		this.sd_clear = sd_clear;
	}

}