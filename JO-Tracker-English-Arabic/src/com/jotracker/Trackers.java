package com.jotracker;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.PhoneStateListener;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.jotracker.data.DBAdapter;
import com.jotracker.service.ServiceSMS;

public class Trackers extends ListActivity implements OnClickListener, Runnable {
	// private static final String LOG = "Trackers";

	private DBAdapter db;
	private static final int MAX_SMS_MESSAGE_LENGTH = 160;
	private static final int SMS_PORT = 8091;
	private static final String SMS_DELIVERED = "SMS_DELIVERED";
	private static final String SMS_SENT = "SMS_SENT";
	public static final int ADD_TRACKER = 1;
	public static final int EDIT_TRACKER = 2;
	public static final int CALL_TRACKER = 3;
	public static final int SETTING_TRACKER = 5;
	private int buffKey = 0;
	private String phoneNumber, password, command = null;
	private License license = null;
	private String date_server;
	private boolean execao = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.trackers);
		getListView().setOnCreateContextMenuListener(this);
		db = new DBAdapter(this);
		license = new License(this);

		registerReceiver(sendreceiver, new IntentFilter(SMS_SENT));
		registerReceiver(deliveredreceiver, new IntentFilter(SMS_DELIVERED));

		/*
		 * if (savedInstanceState != null) {
		 * if(savedInstanceState.getBoolean("progress")) progressDialog.show();
		 * } else { progressDialog.show(); }
		 */

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

	@Override
	protected void onDestroy() {
		unregisterReceiver(sendreceiver);
		unregisterReceiver(deliveredreceiver);
		super.onDestroy();
		// Log.d(LOG, " onDestroy()");
	}

	// This is used to determine if the Splash page should be displayed before
	// the
	// Activity is launched.
	@Override
	protected void onResume() {
		DataBind();
		if (isOnline(this)) {
			/*
			 * DateServer date = new DateServer(); date.execute("");
			 */
			// license.verifyLicenses(this,"2012-11-05");
		}
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
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			if (event.getAction() == KeyEvent.ACTION_DOWN
					&& event.getRepeatCount() == 0) {
				onClosePressed();
				return true;

			}
		}
		return (super.onKeyDown(keyCode, event));
	}

	public void onClosePressed() {
		AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
		alertbox.setTitle(R.string.message);
		alertbox.setMessage(R.string.exit_pressed);
		alertbox.setPositiveButton(R.string.yes,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {
						closeApp();
					}
				});
		alertbox.setNegativeButton(R.string.cancel,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {

					}
				});
		alertbox.show();
	}

	public void stopService() {
		stopService(new Intent(getApplicationContext(), ServiceSMS.class));
		Log.d(getClass().getName(), "stopService()");
		this.finish();
	}

	public void closeApp() {
		Log.d(getClass().getName(), "closeApp()");
		this.finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.options_menu_trackers, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.add_trackers:
			Intent AddManualIntent = new Intent();
			AddManualIntent.setClass(Trackers.this, AddNewTracker.class);
			startActivityForResult(AddManualIntent, ADD_TRACKER);
			return true;
		case R.id.commands_trackers:
			db.open();
			String cmd_trackers = db.selectAllTrackersVehicleAsc();
			db.close();

			if (cmd_trackers == "") {
				Toast.makeText(this, R.string.not_tracker_in_database,
						Toast.LENGTH_LONG).show();
			} else {
				String itens[] = cmd_trackers.split(",");
				List<String> strings = new ArrayList<String>();
				for (int i = 0; i < itens.length; i++) {
					strings.add(itens[i]);
				}
				final CharSequence[] choiceList = strings
						.toArray(new String[strings.size()]);
				AlertDialog.Builder alertTracker = new AlertDialog.Builder(this);
				alertTracker.setTitle(R.string.select_one_tracker);
				alertTracker
						.setSingleChoiceItems(choiceList, -1,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										// on click action
										buffKey = which;
										// Toast.makeText(Trackers.this,"Item: "+choiceList[which],
										// Toast.LENGTH_SHORT).show();
									}
								})
						.setPositiveButton(R.string.ok,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										db.open();
										String choice = String
												.valueOf(choiceList[buffKey]);
										String license_expiration = db
												.selectOneLicenseTracker(choice);
										db.close();
										/*
										 * if
										 * (license.isExpired(license_expiration
										 * )) {
										 * showMessageError(getString(R.string
										 * .your_tracker) + " " +
										 * choiceList[buffKey] + " " +
										 * getString(R.string.license_expired),
										 * 1); } else {
										 */

										try {
											Intent intent = new Intent();
											intent.setClass(Trackers.this,
													CommandsTrackers.class);
											intent.putExtra("vehicle",
													choiceList[buffKey]);
											startActivityForResult(intent,
													SETTING_TRACKER);
										} catch (ActivityNotFoundException anfe) {
											Log.e("Exceção onOptionsItemSelected: ",
													"Settings Tracker Activity Not Found",
													anfe);
										}
										// }/**/
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
			return true;
		case R.id.settings_trackers:
			db.open();
			String trackers = db.selectAllTrackersVehicleAsc();
			db.close();

			if (trackers == "") {
				Toast.makeText(this, R.string.not_tracker_in_database,
						Toast.LENGTH_LONG).show();
			} else {
				String itens[] = trackers.split(",");
				List<String> strings = new ArrayList<String>();
				for (int i = 0; i < itens.length; i++) {
					strings.add(itens[i]);
				}
				final CharSequence[] choiceList = strings
						.toArray(new String[strings.size()]);
				AlertDialog.Builder alertTracker = new AlertDialog.Builder(this);
				alertTracker.setTitle(R.string.select_one_tracker);
				alertTracker
						.setSingleChoiceItems(choiceList, -1,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										// on click action
										buffKey = which;
										// Toast.makeText(Trackers.this,"Item: "+choiceList[which],
										// Toast.LENGTH_SHORT).show();
									}
								})
						.setPositiveButton(R.string.ok,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										db.open();
										String choice = String
												.valueOf(choiceList[buffKey]);
										String license_expiration = db
												.selectOneLicenseTracker(choice);
										db.close();
										/*
										 * if
										 * (license.isExpired(license_expiration
										 * )) {
										 * showMessageError(getString(R.string
										 * .your_tracker) + " " +
										 * choiceList[buffKey] + " " +
										 * getString(R.string.license_expired),
										 * 1); } else {
										 */
										try {
											Intent intent = new Intent();
											intent.setClass(Trackers.this,
													SettingsTrackers.class);
											intent.putExtra("vehicle",
													choiceList[buffKey]);
											startActivityForResult(intent,
													SETTING_TRACKER);
										} catch (ActivityNotFoundException anfe) {
											Log.e("Exceção onOptionsItemSelected: ",
													"Settings Tracker Activity Not Found",
													anfe);
										}
										// }
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
			return true;
		case R.id.clear_trackers:
			AlertDialog.Builder builder = new AlertDialog.Builder(Trackers.this);
			builder.setMessage(R.string.delete_all)
					.setCancelable(false)
					.setTitle(R.string.alert)
					.setIcon(R.drawable.ic_alert)
					.setPositiveButton(R.string.yes,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									db.open();
									db.deleteAllTrackers();
									db.close();
									DataBind();
								}
							})
					.setNegativeButton(R.string.no,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
								}
							});
			AlertDialog alert = builder.create();
			alert.show();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	protected void start(Intent intent) {
		this.startActivityForResult(intent, ADD_TRACKER);
		this.startActivityForResult(intent, EDIT_TRACKER);
		this.startActivityForResult(intent, CALL_TRACKER);
	}

	// This is used to determine if it is returning to this Activity from the
	// Splash
	// Page. Please note that we use requestCode=4
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		switch (requestCode) {
		case ADD_TRACKER:
			if (resultCode == RESULT_OK) {
				/*
				 * String name = data.getStringExtra("AddedTracker");
				 * Toast.makeText(this, "Rastreador adicinado: " + " " + name,
				 * Toast.LENGTH_LONG).show();
				 */
				DataBind();
				break;
			}
		case EDIT_TRACKER:
			if (resultCode == RESULT_OK) {
				/*
				 * String name = data.getStringExtra("EditedTracker");
				 * Toast.makeText(this, "Rastreador editado: " + " " + name,
				 * Toast.LENGTH_LONG).show();
				 */
				DataBind();
				break;
			}
			/*
			 * case CALL_TRACKER: if (resultCode == RESULT_OK) {
			 * 
			 * String name = data.getStringExtra("CalledTracker");
			 * Toast.makeText(this, "Resultado: " + " " + name,
			 * Toast.LENGTH_LONG).show();
			 * 
			 * break; }
			 */
		case SETTING_TRACKER:
			if (resultCode == RESULT_OK) {
				/*
				 * String name = data.getStringExtra("CalledTracker");
				 * Toast.makeText(this, "Resultado: " + " " + name,
				 * Toast.LENGTH_LONG).show();
				 */
				break;
			}
		}

	}

	// Selection on item the list
	protected void onListItemClick(ListView l, View v, int position, long id) {
		/*
		 * Cursor cursor = (Cursor) l.getAdapter().getItem(position);
		 * 
		 * String titre = cursor.getString(cursor.getColumnIndex("imei"));
		 * Toast.makeText(this,"Rastreador "+id+" : IMEI "+titre,
		 * Toast.LENGTH_SHORT).show();
		 */
		DataBind();
		super.onListItemClick(l, v, position, id);

		Intent actionButtonActivity = new Intent(this, ActionButtons.class);
		actionButtonActivity.putExtra("id", String.valueOf(id));
		startActivity(actionButtonActivity);

	}

	/*
	 * // Creation on menu context public void onCreateContextMenu(ContextMenu
	 * menu, View v, ContextMenuInfo menuInfo) { super.onCreateContextMenu(menu,
	 * v, menuInfo); menu.setHeaderTitle(this.getString(R.string.action));
	 * menu.add(0, 100, 0, this.getString(R.string.tracker_locate)); menu.add(0,
	 * 200, 0, this.getString(R.string.tracker_call)); menu.add(0, 300, 0,
	 * this.getString(R.string.edit)); menu.add(0, 400, 0,
	 * this.getString(R.string.delete)); }
	 */
	@Override
	// Selection on item the menu context
	public boolean onContextItemSelected(MenuItem item) {
		final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
				.getMenuInfo();
		switch (item.getItemId()) {
		case 100:
			try {

				String result = null;
				db.open();
				result = db.selectCommandLocateGPS(info.id);
				String data[] = result.split(",");
				phoneNumber = data[0];
				password = data[1];
				command = formatCommand(data[2]);
				db.close();
				sendSms(phoneNumber, command, false);
				Toast.makeText(Trackers.this,
						getString(R.string.command) + " " + command,
						Toast.LENGTH_LONG).show();

			} catch (Exception e) {
				Log.i("Exceção selectPhoneOneTracker(): ", e.getMessage());
			}
			break;
		case 200:
			try {
				String result = null;
				db.open();
				result = db.selectPhoneOneTracker(info.id);
				String data[] = result.split(",");
				phoneNumber = data[0];
				password = data[1];
				db.close();

				Intent intent = new Intent(Intent.ACTION_CALL);
				intent.setData(Uri.parse("tel:" + phoneNumber.toString()));
				startActivityForResult(intent, CALL_TRACKER);

				TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
				tm.listen(mPhoneListener, PhoneStateListener.LISTEN_CALL_STATE);

			} catch (ActivityNotFoundException anfe) {
				Log.e("Exceção onOptionsItemSelected: ",
						"Edit Tracker Activity Not Found", anfe);
			}
			break;
		case 300:
			try {
				String _id = null;
				long id = info.id;
				_id = Long.toString(id);
				Intent intent = new Intent();
				intent.setClass(Trackers.this, EditTrackers.class);
				intent.putExtra("id", _id);
				startActivityForResult(intent, EDIT_TRACKER);
			} catch (ActivityNotFoundException anfe) {
				Log.e("Exceção onOptionsItemSelected: ",
						"Edit Tracker Activity Not Found", anfe);
			}
			break;
		case 400:
			AlertDialog.Builder builder = new AlertDialog.Builder(Trackers.this);
			builder.setMessage(R.string.delete_this)
					.setCancelable(false)
					.setTitle(R.string.alert)
					.setIcon(R.drawable.ic_alert)
					.setPositiveButton(R.string.yes,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									db.open();
									if (db.deleteTrackers(info.id))
										Toast.makeText(Trackers.this,
												R.string.delete_success,
												Toast.LENGTH_SHORT).show();
									DataBind();
									db.close();
								}
							})
					.setNegativeButton(R.string.no,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
								}
							});
			AlertDialog alert = builder.create();
			alert.show();
			break;
		}
		return true;
	}

	private PhoneStateListener mPhoneListener = new PhoneStateListener() {
		public void onCallStateChanged(int state, String incomingNumber) {
			try {
				switch (state) {
				case TelephonyManager.CALL_STATE_RINGING:
					Log.i("CALL_STATE: ", "CALLING");
					break;
				case TelephonyManager.CALL_STATE_OFFHOOK:
					Log.i("CALL_STATE: ", "OFFHOOK");
					break;
				case TelephonyManager.CALL_STATE_IDLE:
					Log.i("CALL_STATE: ", "IDLE");
					break;
				default:
					Log.i("CALL_STATE: ", "DEFALT");
					Log.i("Default", "Unknown phone state=" + state);
				}
			} catch (Exception e) {
				Log.i("Exception", "PhoneStateListener() e = " + e);
			}
		}
	};

	public void DataBind() {
		try {
			db.open();
			Cursor cursor = db.selectAllTrackers();
			startManagingCursor(cursor);
			SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
					R.layout.list_trackers, cursor, new String[] { "imei",
							"vehicle", "description", "phone", "password" },
					new int[] { R.id.imei, R.id.vehicle, R.id.description,
							R.id.phone, });
			if (!adapter.isEmpty()) {
				setListAdapter(adapter);
				db.close();
			} else {
				setListAdapter(null);
				/*
				 * TextView empty = (TextView) getListView().getEmptyView();
				 * empty.setText(R.string.no_data);
				 */
			}
		} catch (Exception e) {
			Log.i("Exeção DataBind()", e.getMessage());
		}
	}

	public void onClick(View v) {
		DataBind();
	}

	public void run() {
		DataBind();
		handler.sendEmptyMessage(0);
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			DataBind();
		}
	};

	public void loadSettingTrackers(CharSequence items) {
		try {
			Intent intent = new Intent();
			intent.setClass(Trackers.this, SettingsTrackers.class);
			intent.putExtra("vehicle", items);
			startActivityForResult(intent, SETTING_TRACKER);
		} catch (ActivityNotFoundException anfe) {
			Log.e("Exceção onOptionsItemSelected: ",
					"Settings Tracker Activity Not Found", anfe);
		}
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

	private BroadcastReceiver sendreceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String info = "";
			switch (getResultCode()) {
			case Activity.RESULT_OK:
				info += getString(R.string.sms_success);
				break;
			case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
				info += getString(R.string.sms_fail);
				break;
			case SmsManager.RESULT_ERROR_NO_SERVICE:
				info += getString(R.string.sms_no_service);
				break;
			case SmsManager.RESULT_ERROR_NULL_PDU:
				info += getString(R.string.sms_pdu_null);
				break;
			case SmsManager.RESULT_ERROR_RADIO_OFF:
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
				info += getString(R.string.sms_delivered);
				break;
			case Activity.RESULT_CANCELED:
				info += getString(R.string.sms_not_delivered);
				break;
			}
			Toast.makeText(getBaseContext(), info, Toast.LENGTH_SHORT).show();
		}
	};

	public String formatCommand(String command) {
		command = command.replaceAll("»", ",");
		return notEmpty(command);
	}

	public String notEmpty(String command) {
		if (command.equals("")) {
			command = " ";
		}
		return command;
	}

	public void showMessageError(String msg, final int codigo) {
		AlertDialog.Builder builder = new AlertDialog.Builder(Trackers.this);
		builder.setMessage(msg)
				.setCancelable(false)
				.setTitle(getString(R.string.message))
				.setIcon(R.drawable.ic_alert)
				.setPositiveButton(R.string.ok,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
							}
						});
		AlertDialog alert = builder.create();
		alert.show();
	}

	public boolean isOnline(Context ctx) {
		ConnectivityManager conMgr = (ConnectivityManager) ctx
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (conMgr != null) {
			NetworkInfo netinfo = conMgr.getActiveNetworkInfo();
			if (netinfo != null) {
				if (!netinfo.isConnected())
					return false;
				if (!netinfo.isAvailable())
					return false;
			}
			if (netinfo == null)
				return false;
		} else
			return false;
		return true;
	}
}