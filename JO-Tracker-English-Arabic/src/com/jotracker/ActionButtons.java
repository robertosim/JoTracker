package com.jotracker;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.PhoneStateListener;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Toast;

import com.jotracker.data.DBAdapter;
import com.jotracker.service.RecordService;

public class ActionButtons extends Activity implements OnCheckedChangeListener {
	private static final int MAX_SMS_MESSAGE_LENGTH = 160;
	private static final int SMS_PORT = 8091;
	private static final String SMS_DELIVERED = "SMS_DELIVERED";
	private static final String SMS_SENT = "SMS_SENT";
	// Interface elements
	protected Button btnLock, btnUnLock, btnCheck, btnGps, btnCall, btnReset,
			btnPhoto, btnMore, btnEdit, btnDelete;
	protected CheckBox cbStartStop;
	private DBAdapter db;
	private String id, result, phone, password, vehicle, cmd_lock, cmd_unlock,
			cmd_check, cmd_gps, cmd_start, cmd_stop, cmd_reset, cmd_photo,
			cmd_track, cmd_stop_track, command;
	private int buffKey = 0;
	private Context context;
	private static boolean recording = false;
	private String LOG;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.action_buttons);

		LOG = getLocalClassName().toString();

		context = getBaseContext();

		db = new DBAdapter(ActionButtons.this);

		registerReceiver(sendreceiver, new IntentFilter(SMS_SENT));
		registerReceiver(deliveredreceiver, new IntentFilter(SMS_DELIVERED));

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			try {
				setId(extras.getString("id"));
				db.open();
				setResult(db.selectTrackerById(getId()));
				db.close();
				Log.i("Exception deleteAllCoords(): ", getResult());

				String t[] = getResult().split(",");
				setCmdTrack(t[6]);
				setCmdStopTrack(t[7]);
				setCmdLock(t[11]);
				setCmdUnLock(t[12]);
				setCmdCheck(t[18]);
				setCmdGps(t[34]);
				setCmdStart(t[19]);
				setCmdStop(t[20]);
				setCmdReset(t[23]);
				setCmdPhoto(t[40]);
				setPhone(t[2]);
				setPassword(t[3]);
				setVehicle(t[1]);

				// Retrieve interface elements
				btnLock = (Button) findViewById(R.id.btnLock);
				btnUnLock = (Button) findViewById(R.id.btnUnLock);
				btnCheck = (Button) findViewById(R.id.btnCheck);
				btnGps = (Button) findViewById(R.id.btnGps);
				/*
				 * btnCall = (Button) findViewById(R.id.btnCall); btnReset =
				 * (Button) findViewById(R.id.btnReset); btnPhoto = (Button)
				 * findViewById(R.id.btnPhoto); btnMore = (Button)
				 * findViewById(R.id.btnMore); btnEdit = (Button)
				 * findViewById(R.id.btnEdit); btnDelete = (Button)
				 * findViewById(R.id.btnDelete);
				 */
				cbStartStop = (CheckBox) findViewById(R.id.cbStartStop);
				cbStartStop.setOnCheckedChangeListener(this);
				/*
				 * Toast.makeText(ActionButtons.this, "id = " + id,
				 * Toast.LENGTH_LONG).show();
				 */
			} catch (Exception e) {
				Toast.makeText(ActionButtons.this,
						R.string.execption + e.getMessage(), Toast.LENGTH_LONG)
						.show();
			}
		}

		btnLock.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				sendSms(getPhone(), getCmdLock(), false);
				Toast.makeText(ActionButtons.this,
						getString(R.string.command) + " " + getCmdLock(),
						Toast.LENGTH_LONG).show();
			}
		});
		btnUnLock.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				sendSms(getPhone(), getCmdUnLock(), false);
				Toast.makeText(ActionButtons.this,
						getString(R.string.command) + " " + getCmdUnLock(),
						Toast.LENGTH_LONG).show();
			}
		});
		btnCheck.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				sendSms(getPhone(), getCmdCheck(), false);
				Toast.makeText(ActionButtons.this,
						getString(R.string.command) + " " + getCmdCheck(),
						Toast.LENGTH_LONG).show();
			}
		});
		btnGps.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				showOptions();

			}
		});

	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(sendreceiver);
		unregisterReceiver(deliveredreceiver);
		super.onDestroy();
		// Log.d(LOG, " onDestroy()");
	}

	public void callTracker() {
		try {

			Intent intent = new Intent(Intent.ACTION_CALL);
			intent.setData(Uri.parse("tel:" + getPhone().toString()));
			startActivityForResult(intent, RESULT_OK);

			//TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
			//PhoneListener listener = new PhoneListener(context, getPhone());
			//tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);

		} catch (ActivityNotFoundException anfe) {
			Log.e("Exceção callTracker(): ",
					"Action Buttons Activity Not Found", anfe);
		}
	}

	public void stopRecordService() {
		Intent service = new Intent("com.jotracker.service.RecordService");
		context.stopService(service);
		Log.d(LOG, "stopRecordService()");
	}

	private boolean isRecordService() {
		ActivityManager manager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager
				.getRunningServices(Integer.MAX_VALUE)) {
			if (RecordService.class.getName().equals(
					service.service.getClassName())) {
				Log.i(LOG, "Record Service is running");
				return true;
			}
		}
		Log.i(LOG, "Record Service not running");
		return false;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {

			Log.i("nActivityResult() ", "stop()");
		}
	}

	private boolean isExternalStoragePresent() {

		boolean mExternalStorageAvailable = false;
		boolean mExternalStorageWriteable = false;
		String state = Environment.getExternalStorageState();

		if (Environment.MEDIA_MOUNTED.equals(state)) {
			// We can read and write the media
			mExternalStorageAvailable = mExternalStorageWriteable = true;
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			// We can only read the media
			mExternalStorageAvailable = true;
			mExternalStorageWriteable = false;
		} else {
			// Something else is wrong. It may be one of many other states, but
			// all we need
			// to know is we can neither read nor write
			mExternalStorageAvailable = mExternalStorageWriteable = false;
		}
		if (!((mExternalStorageAvailable) && (mExternalStorageWriteable))) {
			Toast.makeText(context, "SD card not present", Toast.LENGTH_LONG)
					.show();

		}
		return (mExternalStorageAvailable) && (mExternalStorageWriteable);
	}

	public static boolean createDirIfNotExists(String path) {
		boolean ret = true;
		File file = new File(Environment.getExternalStorageDirectory(), path);
		if (!file.exists()) {
			if (!file.mkdirs()) {
				Log.e("ActionButtons :: ", "Problem creating Audio folder");
				ret = false;
			}
		}
		return ret;
	}

	public void resetTracker() {
		sendSms(getPhone(), getCmdReset(), false);
		Toast.makeText(ActionButtons.this,
				getString(R.string.command) + " " + getCmdReset(),
				Toast.LENGTH_LONG).show();
	}

	public void photoTracker() {
		sendSms(getPhone(), getCmdPhoto(), false);
		Toast.makeText(ActionButtons.this,
				getString(R.string.command) + " " + getCmdPhoto(),
				Toast.LENGTH_LONG).show();
	}

	public void moreTracker() {
		try {
			Intent intent = new Intent();
			intent.setClass(ActionButtons.this, CommandsTrackers.class);
			intent.putExtra("vehicle", getVehicle());
			startActivity(intent);
		} catch (ActivityNotFoundException anfe) {
			Log.e("Exceção onOptionsItemSelected: ",
					"Settings Tracker Activity Not Found", anfe);
		}
	}

	public void editTracker() {
		Intent intent = new Intent();
		intent.setClass(ActionButtons.this, EditTrackers.class);
		intent.putExtra("id", getId());
		startActivity(intent);
	}

	public void deleteTracker() {
		AlertDialog.Builder builder = new AlertDialog.Builder(
				ActionButtons.this);
		builder.setMessage(R.string.delete_this)
				.setCancelable(false)
				.setTitle(R.string.alert)
				.setIcon(R.drawable.ic_alert)
				.setPositiveButton(R.string.yes,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								db.open();
								if (db.deleteTrackers(Long.valueOf(getId())))
									Toast.makeText(ActionButtons.this,
											R.string.delete_success,
											Toast.LENGTH_SHORT).show();
								db.close();
								finish();
							}
						})
				.setNegativeButton(R.string.no,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
							}
						});
		AlertDialog alert = builder.create();
		alert.show();

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

	public void showSucess() {
		new AlertDialog.Builder(ActionButtons.this)
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
	private PhoneStateListener mPhoneListener = new PhoneStateListener() {
		public void onCallStateChanged(int state, String incomingNumber) {
			try {
				switch (state) {
				case TelephonyManager.CALL_STATE_RINGING:
					Log.i(LOG,
							"CALL_STATE: CALLING " + incomingNumber.toString());
					break;
				case TelephonyManager.CALL_STATE_OFFHOOK:
					Log.i(LOG,
							"CALL_STATE: OFFHOOK " + incomingNumber.toString());
					break;
				case TelephonyManager.CALL_STATE_IDLE:
					Log.i(LOG, "CALL_STATE: IDLE " + incomingNumber.toString());
					break;
				default:
					Log.i(LOG,
							"CALL_STATE: DEFALT " + incomingNumber.toString());
					Log.i("Default", "Unknown phone state=" + state);
				}
			} catch (Exception e) {
				Log.i("Exception", "PhoneStateListener() e = " + e);
			}
		}
	};

	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (buttonView == cbStartStop) {
			if (cbStartStop.isChecked()) {
				// Log.i("Exception", "IF cbStartStop.isChecked()");
				sendSms(getPhone(), getCmdStart(), false);
				Toast.makeText(ActionButtons.this,
						getString(R.string.command) + " " + getCmdStart(),
						Toast.LENGTH_LONG).show();
			} else {
				// Log.i("Exception", "ELSE cbStartStop.isChecked()");
				sendSms(getPhone(), getCmdStop(), false);
				Toast.makeText(ActionButtons.this,
						getString(R.string.command) + " " + getCmdStop(),
						Toast.LENGTH_LONG).show();
			}
		}
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.options_menu_actions, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.call:
			if (isRecordService()) {
				stopRecordService();
				callTracker();
			} else {
				callTracker();
			}
			return true;
		case R.id.reset:
			resetTracker();
			return true;
		case R.id.photo:
			photoTracker();
			return true;
		case R.id.more:
			moreTracker();
			return true;
		case R.id.edit:
			editTracker();
			return true;
		case R.id.delete:
			deleteTracker();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void showOptions() {
		List<String> strings = new ArrayList<String>();
		strings.add(getString(R.string.cmd_sigle_track));
		strings.add(getString(R.string.stop_track));
		strings.add(getString(R.string.track_every) + " 20 "
				+ getString(R.string.seconds));
		strings.add(getString(R.string.track_every) + " 40 "
				+ getString(R.string.seconds));
		strings.add(getString(R.string.track_every) + " 50 "
				+ getString(R.string.seconds));
		strings.add(getString(R.string.track_every) + " 5 "
				+ getString(R.string.minutes));
		strings.add(getString(R.string.track_every) + " 10 "
				+ getString(R.string.minutes));
		strings.add(getString(R.string.track_every) + " 15 "
				+ getString(R.string.minutes));

		final CharSequence[] choiceList = strings.toArray(new String[strings
				.size()]);
		AlertDialog.Builder alertTracker = new AlertDialog.Builder(this);
		alertTracker.setTitle(R.string.track_list);
		alertTracker
				.setSingleChoiceItems(choiceList, -1,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								// on click action
								buffKey = which;
								/*
								 * Toast.makeText(ActionButtons.this,"Item: "+which
								 * , Toast.LENGTH_SHORT).show();
								 */
							}
						})
				.setPositiveButton(R.string.ok,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								sendSMSCommand(buffKey);
							}
						})
				.setNegativeButton(R.string.cancel,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								// on cancel button action
							}
						});
		alertTracker.show();
	}

	public void sendSMSCommand(int cmd) {
		switch (cmd) {
		case 0:
			setCommand(getCmdGps());
			break;
		case 1:
			setCommand(getCmdStopTrack());
			break;
		case 2:
			setCommand("fix020s***n" + getPassword());
			break;
		case 3:
			setCommand("fix040s***n" + getPassword());
			break;
		case 4:
			setCommand("fix050s***n" + getPassword());
			break;
		case 5:
			setCommand("fix300s***n" + getPassword());
			break;
		case 6:
			setCommand("fix600s***n" + getPassword());
			break;
		case 7:
			setCommand("fix900s***n" + getPassword());
			break;
		}

		sendSms(getPhone(), getCommand(), false);
		Toast.makeText(ActionButtons.this,
				getString(R.string.command) + " " + getCommand(),
				Toast.LENGTH_LONG).show();
	}

	public String getCmdLock() {
		return cmd_lock;
	}

	public void setCmdLock(String cmd_lock) {
		this.cmd_lock = cmd_lock;
	}

	public String getCmdUnLock() {
		return cmd_unlock;
	}

	public void setCmdUnLock(String cmd_unlock) {
		this.cmd_unlock = cmd_unlock;
	}

	public String getCmdCheck() {
		return cmd_check;
	}

	public void setCmdCheck(String cmd_check) {
		this.cmd_check = cmd_check;
	}

	public String getCmdGps() {
		return cmd_gps;
	}

	public void setCmdGps(String cmd_gps) {
		this.cmd_gps = cmd_gps;
	}

	public String getCmdStart() {
		return cmd_start;
	}

	public void setCmdStart(String cmd_start) {
		this.cmd_start = cmd_start;
	}

	public String getCmdStop() {
		return cmd_stop;
	}

	public void setCmdStop(String cmd_stop) {
		this.cmd_stop = cmd_stop;
	}

	public String getCmdReset() {
		return cmd_reset;
	}

	public void setCmdReset(String cmd_reset) {
		this.cmd_reset = cmd_reset;
	}

	public String getCmdPhoto() {
		return cmd_photo;
	}

	public void setCmdPhoto(String cmd_photo) {
		this.cmd_photo = cmd_photo;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getVehicle() {
		return vehicle;
	}

	public void setVehicle(String vehicle) {
		this.vehicle = vehicle;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public String getCmdStopTrack() {
		return cmd_stop_track;
	}

	public void setCmdStopTrack(String cmd_stop_track) {
		this.cmd_stop_track = cmd_stop_track;
	}

	public String getCmdTrack() {
		return cmd_track;
	}

	public void setCmdTrack(String cmd_track) {
		this.cmd_track = cmd_track;
	}

}