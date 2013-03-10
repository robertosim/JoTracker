package com.jotracker;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.jotracker.data.DBAdapter;

public class CommandsTrackers extends Activity implements View.OnClickListener {

	private static final int MAX_SMS_MESSAGE_LENGTH = 160;
	private static final int SMS_PORT = 8091;
	private static final String SMS_DELIVERED = "SMS_DELIVERED";
	private static final String SMS_SENT = "SMS_SENT";
	protected static int choice = -2;
	private DBAdapter db;
	private String rowid, phoneNumber, result, vehicle, phone, password, admin,
			noadmin, auto_track, no_auto_track, monitor, tracker,
			change_password, arm, disarm, move, nomove, speed, nospeed,
			check_imei, check_tracker, stopcar, resumecar, silent, loud, reset,
			adminip, noadminip, grps_mode, sms_mode, apn, apn_user,
			apn_password, license_expiration, help_me, acc_enable, acc_disable,
			address, time_zone, begin, nooil, temperature, notemperature,
			photo, sd_save, sd_clear;
	// private ProgressDialog progressDialog;
	private EditText txtVehicle, txtPhone, txtPassword;
	private Button btnGoBack, btnCheckStatus, btnCheckImei, btnResetTracker,
			btnChangePassword, btnAdminTracker, btnNoAdminTracker,
			btnAutoTrack, btnNoAutoTrack, btnMonitorMode, btnTackerMode,
			btnAmr, btnDisarm, btnMoveAlert, btnNoMoveAlert, btnSpeedAlert,
			btnNoSpeedAlert, btnStopOilPower, btnStartOilPower, btnSilentSiren,
			bntLoudSiren, btnAccEnable, btnAccDisable, btnGprsMode, btnSmsMode,
			btnAdminIp, btnGprsUnsetting, btnApnSetting, btnApnUserPass,
			btnHelpMe, btnTimeZone, btnBegin, btnNooil, btnTemperature,
			btnNotemperature, btnPhoto, btnSdSave, btnSdClear;
	private AlertDialog.Builder alert;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.trackers_commands);

		db = new DBAdapter(CommandsTrackers.this);

		registerReceiver(sendreceiver, new IntentFilter(SMS_SENT));
		registerReceiver(deliveredreceiver, new IntentFilter(SMS_DELIVERED));
		/*
		 * registerReceiver(smsreceiver, new IntentFilter(
		 * "android.provider.Telephony.SMS_RECEIVED")); progressDialog = new
		 * ProgressDialog(CommandsTrackers.this); String wait =
		 * getString(R.string.wait); wait += "...";
		 * progressDialog.setMessage(wait); progressDialog.setCancelable(true);
		 */
		Bundle extras = getIntent().getExtras();
		setVehicle(extras.getString("vehicle"));

		txtVehicle = (EditText) findViewById(R.id.etVehicle);
		txtPhone = (EditText) findViewById(R.id.etPhone);
		txtPassword = (EditText) findViewById(R.id.etPassword);

		/**/
		if (extras != null) {
			try {
				db.open();
				setResult(db.selectRowidTracker(getVehicle()));
				db.close();

				String t[] = getResult().split(",");
				rowid = t[0];
				setPhone(t[1]);
				setPassword(t[2]);
				setAdmin(formatDisplay(t[3]));
				setNoAdmin(formatDisplay(t[4]));
				setAutoTrack(formatDisplay(t[5]));
				setNoAutoTrack(formatDisplay(t[6]));
				setMonitor(formatDisplay(t[7]));
				setTracker(formatDisplay(t[8]));
				setChangePassword(formatDisplay(t[9]));
				setArm(formatDisplay(t[10]));
				setDisarm(formatDisplay(t[11]));
				setMove(formatDisplay(t[12]));
				setNoMove(formatDisplay(t[13]));
				setSpeed(formatDisplay(t[14]));
				setNoSpeed(formatDisplay(t[15]));
				setCheckImei(formatDisplay(t[16]));
				setCheckTracker(formatDisplay(t[17]));
				setStopCar(formatDisplay(t[18]));
				setResumeCar(formatDisplay(t[19]));
				setSilent(formatDisplay(t[20]));
				setLoud(formatDisplay(t[21]));
				setReset(formatDisplay(t[22]));
				setAdminIp(formatDisplay(t[23]));
				setNoAdminIp(formatDisplay(t[24]));
				setGrpsMode(formatDisplay(t[25]));
				setSmsMode(formatDisplay(t[26]));
				setApn(formatDisplay(t[27]));
				setApnUser(formatDisplay(t[28]));
				setApnPassword(formatDisplay(t[29]));
				setHelpMe(formatDisplay(t[30]));
				setAccEnable(formatDisplay(t[31]));
				setAccDisable(formatDisplay(t[32]));
				setAddress(formatDisplay(t[33]));
				setTimeZone(formatDisplay(t[34]));
				setBegin(formatDisplay(t[35]));
				setNooil(formatDisplay(t[36]));
				setTemperature(formatDisplay(t[37]));
				setNotemperature(formatDisplay(t[38]));
				setPhoto(formatDisplay(t[39]));
				setSdSave(formatDisplay(t[40]));
				setSdClear(formatDisplay(t[41]));

				txtVehicle.setText(getVehicle());
				txtPhone.setText(getPhone());
				txtPassword.setText(getPassword());

				setPhoneNumber(txtPhone.getText().toString());
				setPassword(txtPassword.getText().toString());

			} catch (Exception e) {
				Toast.makeText(CommandsTrackers.this,
						"Exceção: " + e.getMessage(), Toast.LENGTH_LONG).show();
			}
		}

		btnCheckStatus = (Button) findViewById(R.id.btnCheckStatus);
		btnCheckStatus.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				sendSms(getPhoneNumber(), getCheckTracker(), false);
				Toast.makeText(CommandsTrackers.this,
						getString(R.string.command) + " " + getCheckTracker(),
						Toast.LENGTH_LONG).show();
				// progressDialog.show();
			}
		});

		btnCheckImei = (Button) findViewById(R.id.btnCheckImei);
		btnCheckImei.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				sendSms(getPhoneNumber(), getCheckImei(), false);
				Toast.makeText(CommandsTrackers.this,
						getString(R.string.command) + " " + getCheckImei(),
						Toast.LENGTH_LONG).show();
				// progressDialog.show();
			}
		});
		btnResetTracker = (Button) findViewById(R.id.btnResetTracker);
		btnResetTracker.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				sendSms(getPhoneNumber(), getReset(), false);
				Toast.makeText(CommandsTrackers.this,
						getString(R.string.command) + " " + getReset(),
						Toast.LENGTH_LONG).show();
				// progressDialog.show();
			}
		});
		btnChangePassword = (Button) findViewById(R.id.btnChangePassword);
		btnChangePassword.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				alert = new AlertDialog.Builder(CommandsTrackers.this);
				alert.setTitle(R.string.cmd_change_password);
				alert.setMessage(R.string.message_new_pass);
				final EditText input = new EditText(CommandsTrackers.this);
				input.setRawInputType(InputType.TYPE_CLASS_TEXT);
				InputFilter maxLengthFilter = new InputFilter.LengthFilter(6);
				input.setFilters(new InputFilter[] { maxLengthFilter });
				alert.setView(input);
				alert.setPositiveButton(R.string.ok,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								reload();
								String n = input.getText().toString();
								String o = getPassword();
								txtPassword.setText(n);
								db.open();
								db.updatePassword(rowid, n);
								if (db.updateSettingsTrackers(
										rowid,
										changePass(getAdmin(), o, n),
										changePass(getNoAdmin(), o, n),
										changePass(getAutoTrack(), o, n),
										changePass(getNoAutoTrack(), o, n),
										changePass(getMonitor(), o, n),
										changePass(getTracker(), o, n),
										changePass(getChangePassword(), o, n),
										changePass(getArm(), o, n),
										changePass(getDisarm(), o, n),
										changePass(getMove(), o, n),
										changePass(getNoMove(), o, n),
										changePass(getSpeed(), o, n),
										changePass(getNoSpeed(), o, n),
										changePass(getCheckImei(), o, n),
										changePass(getCheckTracker(), o, n),
										changePass(getStopCar(), o, n),
										changePass(getResumeCar(), o, n),
										changePass(getSilent(), o, n),
										changePass(getLoud(), o, n),
										changePass(getReset(), o, n),
										changePass(getAdminIp(), o, n),
										changePass(getNoAdminIp(), o, n),
										changePass(getGrpsMode(), o, n),
										changePass(getSmsMode(), o, n),
										changePass(getApn(), o, n),
										changePass(getApnUser(), o, n),
										changePass(getApnPassword(), o, n),
										changePass(getHelpMe(), o, n),
										changePass(getAccEnable(), o, n),
										changePass(getAccDisable(), o, n),
										changePass(getAddress(), o, n),
										changePass(getTimeZone(), o, n),
										changePass(getBegin(), o, n),
										changePass(getNooil(), o, n),
										formatCommand(changePass(
												getTemperature(), o, n)),
										changePass(getNotemperature(), o, n),
										changePass(getPhoto(), o, n),
										changePass(getSdSave(), o, n),
										changePass(getSdClear(), o, n)))
									;
								db.close();
								sendSms(getPhoneNumber(), getChangePassword()
										+ " " + input.getText(), false);
								Toast.makeText(
										CommandsTrackers.this,
										getString(R.string.command) + " "
												+ getChangePassword() + " "
												+ input.getText(),
										Toast.LENGTH_LONG).show();
								reload();
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
			}
		});
		btnAdminTracker = (Button) findViewById(R.id.btnAdminTracker);
		btnAdminTracker.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				alert = new AlertDialog.Builder(CommandsTrackers.this);
				alert.setTitle(R.string.authorization);
				alert.setMessage(R.string.message_admin);
				final EditText input = new EditText(CommandsTrackers.this);
				input.setRawInputType(InputType.TYPE_CLASS_PHONE);
				InputFilter maxLengthFilter = new InputFilter.LengthFilter(20);
				input.setFilters(new InputFilter[] { maxLengthFilter });
				alert.setView(input);
				alert.setPositiveButton(R.string.ok,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								sendSms(getPhoneNumber(), getAdmin() + " "
										+ input.getText(), false);
								Toast.makeText(
										CommandsTrackers.this,
										getString(R.string.command) + " "
												+ getAdmin() + " "
												+ input.getText(),
										Toast.LENGTH_LONG).show();
								// progressDialog.show();
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
			}
		});
		btnNoAdminTracker = (Button) findViewById(R.id.btnNoAdminTracker);
		btnNoAdminTracker.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				alert = new AlertDialog.Builder(CommandsTrackers.this);
				alert.setTitle(R.string.no_authorization);
				alert.setMessage(R.string.no_message_admin);
				final EditText input = new EditText(CommandsTrackers.this);
				input.setRawInputType(InputType.TYPE_CLASS_PHONE);
				InputFilter maxLengthFilter = new InputFilter.LengthFilter(20);
				input.setFilters(new InputFilter[] { maxLengthFilter });
				alert.setView(input);
				alert.setPositiveButton(R.string.ok,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								sendSms(getPhoneNumber(), getNoAdmin() + " "
										+ input.getText(), false);
								Toast.makeText(
										CommandsTrackers.this,
										getString(R.string.command) + " "
												+ getNoAdmin() + " "
												+ input.getText(),
										Toast.LENGTH_LONG).show();
								// progressDialog.show();
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
				// progressDialog.show();
			}
		});
		btnAutoTrack = (Button) findViewById(R.id.btnAutoTrack);
		btnAutoTrack.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				sendSms(getPhoneNumber(), getAutoTrack(), false);
				Toast.makeText(CommandsTrackers.this,
						getString(R.string.command) + " " + getAutoTrack(),
						Toast.LENGTH_LONG).show();
				// progressDialog.show();
			}
		});
		btnNoAutoTrack = (Button) findViewById(R.id.btnNoAutoTrack);
		btnNoAutoTrack.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				sendSms(getPhoneNumber(), getNoAutoTrack(), false);
				Toast.makeText(CommandsTrackers.this,
						getString(R.string.command) + " " + getNoAutoTrack(),
						Toast.LENGTH_LONG).show();
				// progressDialog.show();
			}
		});
		btnMonitorMode = (Button) findViewById(R.id.btnMonitorMode);
		btnMonitorMode.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				sendSms(getPhoneNumber(), getMonitor(), false);
				Toast.makeText(CommandsTrackers.this,
						getString(R.string.command) + " " + getMonitor(),
						Toast.LENGTH_LONG).show();
				// progressDialog.show();
			}
		});
		btnTackerMode = (Button) findViewById(R.id.btnTackerMode);
		btnTackerMode.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				sendSms(getPhoneNumber(), getTracker(), false);
				Toast.makeText(CommandsTrackers.this,
						getString(R.string.command) + " " + getTracker(),
						Toast.LENGTH_LONG).show();
				// progressDialog.show();
			}
		});
		btnAmr = (Button) findViewById(R.id.btnArm);
		btnAmr.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				sendSms(getPhoneNumber(), getArm(), false);
				Toast.makeText(CommandsTrackers.this,
						getString(R.string.command) + " " + getArm(),
						Toast.LENGTH_LONG).show();
			}
		});
		btnDisarm = (Button) findViewById(R.id.btnDisarm);
		btnDisarm.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				sendSms(getPhoneNumber(), getDisarm(), false);
				Toast.makeText(CommandsTrackers.this,
						getString(R.string.command) + " " + getDisarm(),
						Toast.LENGTH_LONG).show();
				// progressDialog.show();
			}
		});
		btnMoveAlert = (Button) findViewById(R.id.btnMoveAlert);
		btnMoveAlert.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				sendSms(getPhoneNumber(), getMove(), false);
				Toast.makeText(CommandsTrackers.this,
						getString(R.string.command) + " " + getMove(),
						Toast.LENGTH_LONG).show();
				// progressDialog.show();
			}
		});
		btnNoMoveAlert = (Button) findViewById(R.id.btnNoMoveAlert);
		btnNoMoveAlert.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				sendSms(getPhoneNumber(), getNoMove(), false);
				Toast.makeText(CommandsTrackers.this,
						getString(R.string.command) + " " + getNoMove(),
						Toast.LENGTH_LONG).show();
				// progressDialog.show();
			}
		});
		btnSpeedAlert = (Button) findViewById(R.id.btnSpeedAlert);
		btnSpeedAlert.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				alert = new AlertDialog.Builder(CommandsTrackers.this);
				alert.setTitle(R.string.setting_overspeed_alert);
				alert.setMessage(R.string.message_overspeed);
				final EditText input = new EditText(CommandsTrackers.this);
				input.setRawInputType(InputType.TYPE_CLASS_PHONE);
				InputFilter maxLengthFilter = new InputFilter.LengthFilter(3);
				input.setFilters(new InputFilter[] { maxLengthFilter });
				alert.setView(input);
				alert.setPositiveButton(R.string.ok,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								sendSms(getPhoneNumber(), getSpeed() + " "
										+ input.getText(), false);
								Toast.makeText(
										CommandsTrackers.this,
										getString(R.string.command) + " "
												+ getSpeed() + " "
												+ input.getText(),
										Toast.LENGTH_LONG).show();
								// progressDialog.show();
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
			}
		});
		btnNoSpeedAlert = (Button) findViewById(R.id.btnNoSpeedAlert);
		btnNoSpeedAlert.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				sendSms(getPhoneNumber(), getNoSpeed(), false);
				Toast.makeText(CommandsTrackers.this,
						getString(R.string.command) + " " + getNoSpeed(),
						Toast.LENGTH_LONG).show();
				// progressDialog.show();
			}
		});
		btnStopOilPower = (Button) findViewById(R.id.btnStopOilPower);
		btnStopOilPower.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				sendSms(getPhoneNumber(), getStopCar(), false);
				Toast.makeText(CommandsTrackers.this,
						getString(R.string.command) + " " + getStopCar(),
						Toast.LENGTH_LONG).show();
				// progressDialog.show();

			}
		});
		btnStartOilPower = (Button) findViewById(R.id.btnStartOilPower);
		btnStartOilPower.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				sendSms(getPhoneNumber(), getResumeCar(), false);
				Toast.makeText(CommandsTrackers.this,
						getString(R.string.command) + " " + getResumeCar(),
						Toast.LENGTH_LONG).show();
				// progressDialog.show();

			}
		});
		btnSilentSiren = (Button) findViewById(R.id.btnSilentSiren);
		btnSilentSiren.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				sendSms(getPhoneNumber(), getSilent(), false);
				Toast.makeText(CommandsTrackers.this,
						getString(R.string.command) + " " + getSilent(),
						Toast.LENGTH_LONG).show();
				// progressDialog.show();
			}
		});
		bntLoudSiren = (Button) findViewById(R.id.bntLoudSiren);
		bntLoudSiren.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				sendSms(getPhoneNumber(), getLoud(), false);
				Toast.makeText(CommandsTrackers.this,
						getString(R.string.command) + " " + getLoud(),
						Toast.LENGTH_LONG).show();
				// progressDialog.show();
			}
		});

		btnGprsMode = (Button) findViewById(R.id.btnGprsMode);
		btnGprsMode.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				sendSms(getPhoneNumber(), getGrpsMode(), false);
				Toast.makeText(CommandsTrackers.this,
						getString(R.string.command) + " " + getGrpsMode(),
						Toast.LENGTH_LONG).show();
				// progressDialog.show();

			}
		});

		btnSmsMode = (Button) findViewById(R.id.btnSmsMode);
		btnSmsMode.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				sendSms(getPhoneNumber(), getSmsMode(), false);
				Toast.makeText(CommandsTrackers.this,
						getString(R.string.command) + " " + getSmsMode(),
						Toast.LENGTH_LONG).show();
				// progressDialog.show();

			}
		});

		btnAccEnable = (Button) findViewById(R.id.btnAccEnable);
		btnAccEnable.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				sendSms(getPhoneNumber(), getAccEnable(), false);
				Toast.makeText(CommandsTrackers.this,
						getString(R.string.command) + " " + getAccEnable(),
						Toast.LENGTH_LONG).show();
				// progressDialog.show();

			}
		});
		btnAccDisable = (Button) findViewById(R.id.btnAccDisable);
		btnAccDisable.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				sendSms(getPhoneNumber(), getAccDisable(), false);
				Toast.makeText(CommandsTrackers.this,
						getString(R.string.command) + " " + getAccDisable(),
						Toast.LENGTH_LONG).show();
				// progressDialog.show();
			}
		});
		btnAdminIp = (Button) findViewById(R.id.btnAdminIp);
		btnAdminIp.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				sendSms(getPhoneNumber(), getAdminIp(), false);
				Toast.makeText(CommandsTrackers.this,
						getString(R.string.command) + " " + getAdminIp(),
						Toast.LENGTH_LONG).show();
				// progressDialog.show();

			}
		});
		btnGprsUnsetting = (Button) findViewById(R.id.btnGprsUnsetting);
		btnGprsUnsetting.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				sendSms(getPhoneNumber(), getNoAdminIp(), false);
				Toast.makeText(CommandsTrackers.this,
						getString(R.string.command) + " " + getNoAdminIp(),
						Toast.LENGTH_LONG).show();
				// progressDialog.show();

			}
		});
		btnApnSetting = (Button) findViewById(R.id.btnApnSetting);
		btnApnSetting.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				alert = new AlertDialog.Builder(CommandsTrackers.this);
				alert.setTitle(R.string.setting_apn_alert);
				alert.setMessage(R.string.message_apn);
				final EditText input = new EditText(CommandsTrackers.this);
				input.setRawInputType(InputType.TYPE_CLASS_TEXT);
				alert.setView(input);
				alert.setPositiveButton(R.string.ok,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								sendSms(getPhoneNumber(), getApn() + " "
										+ input.getText(), false);
								Toast.makeText(
										CommandsTrackers.this,
										getString(R.string.command) + " "
												+ getApn() + " "
												+ input.getText(),
										Toast.LENGTH_LONG).show();
								// progressDialog.show();
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

			}
		});
		btnApnUserPass = (Button) findViewById(R.id.btnApnUserPass);
		btnApnUserPass.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				sendSms(getPhoneNumber(), getApnUser(), false);
				sendSms(getPhoneNumber(), getApnPassword(), false);

				Toast.makeText(CommandsTrackers.this,
						getString(R.string.command) + " " + getApnUser(),
						Toast.LENGTH_LONG).show();
				Toast.makeText(CommandsTrackers.this,
						getString(R.string.command) + " " + getApnPassword(),
						Toast.LENGTH_LONG).show();
				// progressDialog.show();

			}
		});

		btnHelpMe = (Button) findViewById(R.id.btnHelpMe);
		btnHelpMe.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				sendSms(getPhoneNumber(), getHelpMe(), false);
				Toast.makeText(CommandsTrackers.this,
						getString(R.string.command) + " " + getHelpMe(),
						Toast.LENGTH_LONG).show();
				// progressDialog.show();

			}
		});
		btnTimeZone = (Button) findViewById(R.id.btnTimeZone);
		btnTimeZone.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				alert = new AlertDialog.Builder(CommandsTrackers.this);
				alert.setTitle(R.string.setting_time_zone);
				alert.setMessage(R.string.message_time_zone);
				final EditText input = new EditText(CommandsTrackers.this);
				input.setRawInputType(InputType.TYPE_CLASS_PHONE);
				InputFilter maxLengthFilter = new InputFilter.LengthFilter(3);
				input.setFilters(new InputFilter[] { maxLengthFilter });
				alert.setView(input);
				alert.setPositiveButton(R.string.ok,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								sendSms(getPhoneNumber(), getTimeZone() + " "
										+ input.getText(), false);
								Toast.makeText(
										CommandsTrackers.this,
										getString(R.string.command) + " "
												+ getTimeZone() + " "
												+ input.getText(),
										Toast.LENGTH_LONG).show();
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
			}
		});

		btnBegin = (Button) findViewById(R.id.btnBegin);
		btnBegin.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				alert = new AlertDialog.Builder(CommandsTrackers.this);
				alert.setTitle(R.string.begin_title);
				alert.setMessage(R.string.message_begin);
				alert.setPositiveButton(R.string.ok,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								sendSms(getPhoneNumber(), getBegin(), false);
								Toast.makeText(
										CommandsTrackers.this,
										getString(R.string.command) + " "
												+ getBegin(), Toast.LENGTH_LONG)
										.show();
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
			}
		});

		btnNooil = (Button) findViewById(R.id.btnNooil);
		btnNooil.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				sendSms(getPhoneNumber(), getNooil(), false);
				Toast.makeText(CommandsTrackers.this,
						getString(R.string.command) + " " + getNooil(),
						Toast.LENGTH_LONG).show();
				// progressDialog.show();
			}
		});

		btnTemperature = (Button) findViewById(R.id.btnTemperature);
		btnTemperature.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				sendSms(getPhoneNumber(), getTemperature(), false);
				Toast.makeText(CommandsTrackers.this,
						getString(R.string.command) + " " + getTemperature(),
						Toast.LENGTH_LONG).show();
				// progressDialog.show();
			}
		});
		btnNotemperature = (Button) findViewById(R.id.btnNotemperature);
		btnNotemperature.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				sendSms(getPhoneNumber(), getNotemperature(), false);
				Toast.makeText(CommandsTrackers.this,
						getString(R.string.command) + " " + getNotemperature(),
						Toast.LENGTH_LONG).show();
				// progressDialog.show();
			}
		});
		btnPhoto = (Button) findViewById(R.id.btnPhoto);
		btnPhoto.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				sendSms(getPhoneNumber(), getPhoto(), false);
				Toast.makeText(CommandsTrackers.this,
						getString(R.string.command) + " " + getPhoto(),
						Toast.LENGTH_LONG).show();
				// progressDialog.show();
			}
		});
		btnSdSave = (Button) findViewById(R.id.btnSdSave);
		btnSdSave.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				sendSms(getPhoneNumber(), getSdSave(), false);
				Toast.makeText(CommandsTrackers.this,
						getString(R.string.command) + " " + getSdSave(),
						Toast.LENGTH_LONG).show();
				// progressDialog.show();
			}
		});
		btnSdClear = (Button) findViewById(R.id.btnSdClear);
		btnSdClear.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				sendSms(getPhoneNumber(), getSdClear(), false);
				Toast.makeText(CommandsTrackers.this,
						getString(R.string.command) + " " + getSdClear(),
						Toast.LENGTH_LONG).show();
				// progressDialog.show();
			}
		});
		btnGoBack = (Button) findViewById(R.id.btnGoBack);
		btnGoBack.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});

	}

	public void showMessage(String message) {
		new AlertDialog.Builder(CommandsTrackers.this)
				.setTitle(R.string.message)
				.setMessage(message)
				.setPositiveButton(R.string.ok,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
							}
						}).show();
	}

	public void showSucess() {
		new AlertDialog.Builder(CommandsTrackers.this)
				.setTitle(R.string.message)
				.setMessage(getString(R.string.cmd_sucess))
				.setIcon(R.drawable.ic_info)
				.setPositiveButton(R.string.ok,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
							}
						}).show();
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
		unregisterReceiver(sendreceiver);
		unregisterReceiver(deliveredreceiver);
		// unregisterReceiver(smsreceiver);
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

	public void sendSms(String phonenumber, String message, boolean isBinary) {
		SmsManager manager = SmsManager.getDefault();

		PendingIntent piSend = PendingIntent.getBroadcast(this, 0, new Intent(
				SMS_SENT), 0);
		PendingIntent piDelivered = PendingIntent.getBroadcast(this, 0,
				new Intent(SMS_DELIVERED), 0);

		if (isBinary) {
			byte[] data = new byte[message.length()];

			for (int index = 0; index < message.length()
					&& index < MAX_SMS_MESSAGE_LENGTH; ++index) {
				data[index] = (byte) message.charAt(index);
			}

			manager.sendDataMessage(phonenumber, null, (short) SMS_PORT, data,
					piSend, piDelivered);
		} else {
			int length = message.length();

			if (length > MAX_SMS_MESSAGE_LENGTH) {
				ArrayList<String> messagelist = manager.divideMessage(message);

				manager.sendMultipartTextMessage(phonenumber, null,
						messagelist, null, null);
			} else {
				manager.sendTextMessage(phonenumber, null, message, piSend,
						piDelivered);
			}
		}
	}

	public void reload() {
		try {
			db.open();
			setResult(db.selectRowidTracker(getVehicle()));
			db.close();

			String t[] = getResult().split(",");
			rowid = t[0];
			setPhone(t[1]);
			setPassword(t[2]);
			setAdmin(formatDisplay(t[3]));
			setNoAdmin(formatDisplay(t[4]));
			setAutoTrack(formatDisplay(t[5]));
			setNoAutoTrack(formatDisplay(t[6]));
			setMonitor(formatDisplay(t[7]));
			setTracker(formatDisplay(t[8]));
			setChangePassword(formatDisplay(t[9]));
			setArm(formatDisplay(t[10]));
			setDisarm(formatDisplay(t[11]));
			setMove(formatDisplay(t[12]));
			setNoMove(formatDisplay(t[13]));
			setSpeed(formatDisplay(t[14]));
			setNoSpeed(formatDisplay(t[15]));
			setCheckImei(formatDisplay(t[16]));
			setCheckTracker(formatDisplay(t[17]));
			setStopCar(formatDisplay(t[18]));
			setResumeCar(formatDisplay(t[19]));
			setSilent(formatDisplay(t[20]));
			setLoud(formatDisplay(t[21]));
			setReset(formatDisplay(t[22]));
			setAdminIp(formatDisplay(t[23]));
			setNoAdminIp(formatDisplay(t[24]));
			setGrpsMode(formatDisplay(t[25]));
			setSmsMode(formatDisplay(t[26]));
			setApn(formatDisplay(t[27]));
			setApnUser(formatDisplay(t[28]));
			setApnPassword(formatDisplay(t[29]));
			setHelpMe(formatDisplay(t[30]));
			setAccEnable(formatDisplay(t[31]));
			setAccDisable(formatDisplay(t[32]));
			setAddress(formatDisplay(t[33]));
			setTimeZone(formatDisplay(t[34]));
			setBegin(formatDisplay(t[35]));
			setNooil(formatDisplay(t[36]));
			setTemperature(formatDisplay(t[37]));
			setNotemperature(formatDisplay(t[38]));
			setPhoto(formatDisplay(t[39]));
			setSdSave(formatDisplay(t[40]));
			setSdClear(formatDisplay(t[41]));

			txtVehicle.setText(getVehicle());
			txtPhone.setText(getPhone());
			txtPassword.setText(getPassword());

			setPhoneNumber(txtPhone.getText().toString());
			setPassword(txtPassword.getText().toString());

		} catch (Exception e) {
			Toast.makeText(CommandsTrackers.this, "Exceção: " + e.getMessage(),
					Toast.LENGTH_LONG).show();
		}
	}

	private String changePass(String cmd, String o, String n) {
		Log.d("LOG", "changePass() cmd = " + cmd + " o = " + o + " n = " + n);
		return cmd.replace(o, n);
	}

	private String getPhoneNumber() {
		return phoneNumber;
	}

	private void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	private String getResult() {
		return result;
	}

	private void setResult(String result) {
		this.result = result;
	}

	private String getVehicle() {
		return vehicle;
	}

	private void setVehicle(String vehicle) {
		this.vehicle = vehicle;
	}

	private String getPhone() {
		return phone;
	}

	private void setPhone(String phone) {
		this.phone = phone;
	}

	private String getPassword() {
		return password;
	}

	private void setPassword(String password) {
		this.password = password;
	}

	private String getAdmin() {
		return admin;
	}

	private void setAdmin(String admin) {
		this.admin = admin;
	}

	private String getNoAdmin() {
		return noadmin;
	}

	private void setNoAdmin(String noadmin) {
		this.noadmin = noadmin;
	}

	private String getAutoTrack() {
		return auto_track;
	}

	private void setAutoTrack(String auto_track) {
		this.auto_track = auto_track;
	}

	private String getNoAutoTrack() {
		return no_auto_track;
	}

	private void setNoAutoTrack(String no_auto_track) {
		this.no_auto_track = no_auto_track;
	}

	private String getMonitor() {
		return monitor;
	}

	private void setMonitor(String monitor) {
		this.monitor = monitor;
	}

	private String getTracker() {
		return tracker;
	}

	private void setTracker(String tracker) {
		this.tracker = tracker;
	}

	private String getChangePassword() {
		return change_password;
	}

	private void setChangePassword(String change_password) {
		this.change_password = change_password;
	}

	private String getArm() {
		return arm;
	}

	private void setArm(String arm) {
		this.arm = arm;
	}

	private String getDisarm() {
		return disarm;
	}

	private void setDisarm(String disarm) {
		this.disarm = disarm;
	}

	private String getMove() {
		return move;
	}

	private void setMove(String move) {
		this.move = move;
	}

	private String getNoMove() {
		return nomove;
	}

	private void setNoMove(String nomove) {
		this.nomove = nomove;
	}

	private String getSpeed() {
		return speed;
	}

	private void setSpeed(String speed) {
		this.speed = speed;
	}

	private String getNoSpeed() {
		return nospeed;
	}

	private void setNoSpeed(String nospeed) {
		this.nospeed = nospeed;
	}

	private String getCheckImei() {
		return check_imei;
	}

	private void setCheckImei(String check_imei) {
		this.check_imei = check_imei;
	}

	public String getCheckTracker() {
		return check_tracker;
	}

	public void setCheckTracker(String check_tracker) {
		this.check_tracker = check_tracker;
	}

	public String getStopCar() {
		return stopcar;
	}

	public void setStopCar(String stopcar) {
		this.stopcar = stopcar;
	}

	public String getResumeCar() {
		return resumecar;
	}

	public void setResumeCar(String resumecar) {
		this.resumecar = resumecar;
	}

	public String getSilent() {
		return silent;
	}

	public void setSilent(String silent) {
		this.silent = silent;
	}

	public String getLoud() {
		return loud;
	}

	public void setLoud(String loud) {
		this.loud = loud;
	}

	public String getReset() {
		return reset;
	}

	public void setReset(String reset) {
		this.reset = reset;
	}

	public String getAdminIp() {
		return adminip;
	}

	public void setAdminIp(String adminip) {
		this.adminip = adminip;
	}

	public String getNoAdminIp() {
		return noadminip;
	}

	public void setNoAdminIp(String noadminip) {
		this.noadminip = noadminip;
	}

	public String getGrpsMode() {
		return grps_mode;
	}

	public void setGrpsMode(String grps_mode) {
		this.grps_mode = grps_mode;
	}

	public String getSmsMode() {
		return sms_mode;
	}

	public void setSmsMode(String sms_mode) {
		this.sms_mode = sms_mode;
	}

	public String getApn() {
		return apn;
	}

	public void setApn(String apn) {
		this.apn = apn;
	}

	public String getApnUser() {
		return apn_user;
	}

	public void setApnUser(String apn_user) {
		this.apn_user = apn_user;
	}

	public String getApnPassword() {
		return apn_password;
	}

	public void setApnPassword(String apn_password) {
		this.apn_password = apn_password;
	}

	public String getLicenseExpiration() {
		return license_expiration;
	}

	public void setLicenseExpiration(String license_expiration) {
		this.license_expiration = license_expiration;
	}

	public String getHelpMe() {
		return help_me;
	}

	public void setHelpMe(String help_me) {
		this.help_me = help_me;
	}

	public String getAccEnable() {
		return acc_enable;
	}

	public void setAccEnable(String acc_enable) {
		this.acc_enable = acc_enable;
	}

	public String getAccDisable() {
		return acc_disable;
	}

	public void setAccDisable(String acc_disable) {
		this.acc_disable = acc_disable;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getTimeZone() {
		return time_zone;
	}

	public void setTimeZone(String time_zone) {
		this.time_zone = time_zone;
	}

	public String getBegin() {
		return begin;
	}

	public void setBegin(String begin) {
		this.begin = begin;
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

	private BroadcastReceiver sendreceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String info = "";
			switch (getResultCode()) {
			case Activity.RESULT_OK:
				info += getString(R.string.sms_success);
				// showSucess();
				break;
			case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
				// progressDialog.dismiss();
				info += getString(R.string.sms_fail);
				break;
			case SmsManager.RESULT_ERROR_NO_SERVICE:
				// progressDialog.dismiss();
				info += getString(R.string.sms_no_service);
				break;
			case SmsManager.RESULT_ERROR_NULL_PDU:
				// progressDialog.dismiss();
				info += getString(R.string.sms_pdu_null);
				break;
			case SmsManager.RESULT_ERROR_RADIO_OFF:
				// progressDialog.dismiss();
				info += getString(R.string.sms_radio_off);
				break;
			}
			Toast.makeText(getBaseContext(), info, Toast.LENGTH_SHORT).show();
		}
	};

	private BroadcastReceiver deliveredreceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String info = "";
			switch (getResultCode()) {
			case Activity.RESULT_OK:
				// info += getString(R.string.sms_delivered);
				showSucess();
				break;
			case Activity.RESULT_CANCELED:
				info += getString(R.string.sms_not_delivered);
				break;
			}
			if (!info.equals("") || info != null) {
				Toast.makeText(getBaseContext(), info, Toast.LENGTH_SHORT)
						.show();
			}
		}
	};
	/*
	 * private BroadcastReceiver smsreceiver = new BroadcastReceiver() {
	 * 
	 * @Override public void onReceive(Context context, Intent intent) { String
	 * info = ""; Bundle bundle = intent.getExtras(); SmsMessage[] msgs = null;
	 * if (null != bundle) {
	 * 
	 * Object[] pdus = (Object[]) bundle.get("pdus"); msgs = new
	 * SmsMessage[pdus.length];
	 * 
	 * for (int i = 0; i < msgs.length; i++) { msgs[i] =
	 * SmsMessage.createFromPdu((byte[]) pdus[i]); info +=
	 * getString(R.string.phoneno_info) + ": " +
	 * msgs[i].getOriginatingAddress(); info += "\n"; info +=
	 * msgs[i].getMessageBody().toString(); } progressDialog.dismiss();
	 * showMessage(info); } this.abortBroadcast(); } };
	 */
}