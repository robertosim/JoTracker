package com.jotracker;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.jotracker.common.IActionListener;
import com.jotracker.common.SmtpSettings;
import com.jotracker.common.Utilities;
import com.jotracker.data.DBAdapter;
import com.jotracker.email.AutoEmailActivity;
import com.jotracker.email.Mail;
import com.jotracker.player.MusicPlayerActivity;
import com.jotracker.senders.FileSenderFactory;
import com.jotracker.senders.IFileSender;
import com.jotracker.service.ServiceSMS;

public class ListCoords extends ListActivity implements OnClickListener,
		IActionListener, IFileSender {

	// private static final String LOG = "ListCoords";
	private DBAdapter db;
	private Cursor cursor;
	private String strLat, strLon, strDate, strHour, strUrlPhoto = null;
	private final String LOG = "ListCoords";
	static final String PREFS_LISTVIEW = "list";
	@SuppressLint("SdCardPath")
	public static final String DEFAULT_STORAGE_LOCATION = "JoTracker/Audio/";
	private ProgressDialog progress;
	private List<File> files = new ArrayList<File>();
	private File audioFolder = new File(
			Environment.getExternalStorageDirectory(), DEFAULT_STORAGE_LOCATION);
	private final Handler handler = new Handler();
	private final IFileSender sender = ListCoords.this;
	public SharedPreferences sharedPrefs;
	private boolean send_email = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.coords);
		getListView().setOnCreateContextMenuListener(this);
		// ((Button)findViewById(android.R.id.button1)).setOnClickListener(this);
		db = new DBAdapter(getApplicationContext());
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Log.d(LOG, " onConfigurationChanged()");
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Log.d(LOG, " onSaveInstanceState()");
	}

	@Override
	protected void onRestoreInstanceState(Bundle state) {
		super.onRestoreInstanceState(state);
		Log.d(LOG, " onRestoreInstanceState()");
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.d("LOG", " onActivityResult()");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// Log.d(LOG, " onDestroy()");
	}

	// This is used to determine if the Splash page should be displayed before
	// the
	// Activity is launched.
	@Override
	protected void onResume() {
		db.open();
		loadList();
		super.onResume();
		Log.d("LOG", " onResume()");
	}

	// storing a timestamp when the user quits the app
	@Override
	protected void onPause() {
		super.onPause();
		// Log.d("LOG", " onPause()");
	}

	private void loadList() {
		switch (Integer.parseInt(getList())) {
		case 0:
			DataBind(false);
			Log.i(LOG, "DataBind(false)");
			break;
		case 1:
			DataBind(true);
			Log.i(LOG, "DataBind(true)");
			break;
		case 2:
			DataBindAudio();
			Log.i(LOG, "DataBindAudio()");
			break;
		default:
			Log.i(LOG, "DataBind(false)");
			DataBind(false);
		}
	}

	private String getList() {
		String list = "";
		try {
			SharedPreferences sp = getSharedPreferences(PREFS_LISTVIEW, 0);
			list = sp.getString("list", "0");
		} catch (Exception ex) {
			Log.e(LOG, "Exception getList()  ::" + ex.getMessage());
		}
		return list;
	}

	private void saveList(String list) {
		try {
			SharedPreferences sp = getSharedPreferences(PREFS_LISTVIEW, 0);
			SharedPreferences.Editor editor = sp.edit();
			editor.putString("list", list);
			editor.commit();
		} catch (Exception ex) {
			Log.e(LOG, "Exception saveList()  ::" + ex.getMessage());
		}
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

	public SimpleCursorAdapter noRecords() {
		Cursor cursor = null;
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(ListCoords.this,
				R.layout.no_data, cursor, new String[] { "no_data" },
				new int[] { R.id.no_data });
		return adapter;
	}

	public void onClick(View v) {
		DataBind(false);
	}

	public void DataBind(boolean withPhotos) {
		try {
			if (withPhotos) {
				cursor = db.selectAllCoordsWithPhotos();
				startManagingCursor(cursor);
				BaseAdapter adapter = new ImageCursorAdapter(this,
						R.layout.list_coords_photos, cursor,
						new String[] { "photo", "vehicle", "latitude",
								"longitude", "speed", "date", "hour", "power",
								"acc", "door" }, new int[] { R.id.photo,
								R.id.vehicle, R.id.latitude, R.id.longitude,
								R.id.speed, R.id.date, R.id.hour, R.id.power,
								R.id.acc, R.id.door });
				if (!adapter.isEmpty()) {
					setListAdapter(adapter);
				} else {
					setListAdapter(noRecords());
				}
			} else {
				cursor = db.selectAllCoords();
				startManagingCursor(cursor);
				SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
						R.layout.list_coords, cursor, new String[] { "vehicle",
								"latitude", "longitude", "speed", "date",
								"hour", "power", "acc", "door" }, new int[] {
								R.id.vehicle, R.id.latitude, R.id.longitude,
								R.id.speed, R.id.date, R.id.hour, R.id.power,
								R.id.acc, R.id.door });
				if (!adapter.isEmpty()) {
					setListAdapter(adapter);
				} else {
					setListAdapter(noRecords());
				}
			}
		} catch (Exception e) {
			Log.i("Exeção DataBind()", e.getMessage());
		}
	}

	public void DataBindPhotos() {
		try {
			cursor = db.selectAllCoordsWithPhotos();
			startManagingCursor(cursor);
			BaseAdapter adapter = new ImageCursorAdapter(this,
					R.layout.list_coords_photos, cursor, new String[] {
							"photo", "vehicle", "latitude", "longitude",
							"speed", "date", "hour", "power", "acc", "door" },
					new int[] { R.id.photo, R.id.vehicle, R.id.latitude,
							R.id.longitude, R.id.speed, R.id.date, R.id.hour,
							R.id.power, R.id.acc, R.id.door });
			setListAdapter(adapter);
			if (!adapter.isEmpty()) {
				setListAdapter(adapter);
			} else {
				setListAdapter(noRecords());
			}
		} catch (Exception e) {
			Log.i("Exeção DataBind()", e.getMessage());
		}
	}

	public void DataBindAudio() {
		try {
			cursor = db.selectAllVoices();
			startManagingCursor(cursor);
			SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
					R.layout.list_voices, cursor, new String[] { "vehicle",
							"date", "hour", "file_voice" },
					new int[] { R.id.vehicle, R.id.date, R.id.hour,
							R.id.file_voice });
			if (!adapter.isEmpty()) {
				setListAdapter(adapter);
			} else {
				setListAdapter(noRecords());
			}
		} catch (Exception e) {
			Log.i("Exeção DataBindAudio()()", e.getMessage());
		}
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.options_menu_coords, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.show_photos:
			if (checkConn(this)) {
				saveList("1");
				loadList();
			} else {
				Toast.makeText(this, this.getString(R.string.no_network),
						Toast.LENGTH_LONG).show();
			}
			return true;
		case R.id.show_audio:
			saveList("2");
			loadList();
			return true;
		case R.id.show_location:
			saveList("0");
			loadList();
			return true;
		case R.id.clear_coords:
			AlertDialog.Builder builder = new AlertDialog.Builder(
					ListCoords.this);
			builder.setMessage(R.string.delete_all)
					.setCancelable(false)
					.setTitle(R.string.alert)
					.setIcon(R.drawable.ic_alert)
					.setPositiveButton(R.string.yes,
							new DialogInterface.OnClickListener() {
								@SuppressLint("SdCardPath")
								public void onClick(DialogInterface dialog,
										int id) {
									if (getList().equals("2")) {
										db.deleteAllVoices();
										File path = new File(
												"/sdcard/JoTracker/Audio");
										if (deleteDirectory(path)) {
											Toast.makeText(
													ListCoords.this,
													"Deleted files successfully!",
													Toast.LENGTH_SHORT).show();
										} else {
											Toast.makeText(
													ListCoords.this,
													"Failed to delete the files",
													Toast.LENGTH_SHORT).show();
										}
										loadList();
									} else {
										db.deleteAllCoords();
										loadList();
									}

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
		case R.id.send_file:
			if (isSendEmail()) {
				SelectAndEmailFile();
			} else {
				Toast.makeText(this, getString(R.string.turn_on_send_email),
						Toast.LENGTH_SHORT).show();
				Intent settings = new Intent();
				settings.setClass(ListCoords.this, Settings.class);
				startActivity(settings);
			}
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	// Selection on item the listCoords
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Cursor cursor = (Cursor) l.getAdapter().getItem(position);
		String _id = cursor.getString(cursor.getColumnIndex("_id"));
		if (getList().equals("2")) {
			Intent intent = new Intent();
			intent.putExtra("file", _id);
			intent.setClass(ListCoords.this, MusicPlayerActivity.class);
			startActivityForResult(intent, 0);
		} else {
			// int _id = cursor.getInt(cursor.getColumnIndex("_id"));
			// Toast.makeText(this," ID : "+_id, Toast.LENGTH_SHORT).show();
			Intent intent = getParent().getIntent();
			intent.putExtra("id", _id);
			intent.putExtra("loadRoute", false);
			TabActivity tabHost = (TabActivity) this.getParent();
			tabHost.getTabHost().setCurrentTab(0);
		}
	}

	// Creation on menu context
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.setHeaderTitle(this.getString(R.string.action));
		// menu.add(0, 100, 0, this.getString(R.string.see_in_map));
		if (getList().equals("0") || getList().equals("1")) {
			menu.add(0, 200, 0, this.getString(R.string.see_address));
		}
		if (getList().equals("1")) {
			menu.add(0, 300, 0, this.getString(R.string.see_photo));
		}
		menu.add(0, 400, 0, this.getString(R.string.delete));
		if (getList().equals("2")) {
			menu.add(0, 500, 0, this.getString(R.string.send_by_email));
		}

	}

	private void SelectAndEmailFile() {
		Utilities.LogDebug("GpsMainActivity.SelectAndEmailFile");

		Intent settingsIntent = new Intent(getApplicationContext(),
				AutoEmailActivity.class);

		if (Utilities.IsEmailSetup()) {

			startActivity(settingsIntent);
		} else {
			ShowFileListDialog(settingsIntent,
					FileSenderFactory.GetEmailSender(ListCoords.this,
							ListCoords.this));
		}
	}

	private void ShowFileListDialog(final Intent settingsIntent,
			final IFileSender sender) {

		if (audioFolder.exists()) {
			File[] enumeratedFiles = audioFolder.listFiles(sender);

			Arrays.sort(enumeratedFiles, new Comparator<File>() {
				public int compare(File f1, File f2) {
					return -1
							* Long.valueOf(f1.lastModified()).compareTo(
									f2.lastModified());
				}
			});

			List<String> fileList = new ArrayList<String>(
					enumeratedFiles.length);

			for (File f : enumeratedFiles) {
				fileList.add(f.getName());
			}

			final String[] file_array = fileList.toArray(new String[fileList
					.size()]);

			final Dialog dialog = new Dialog(this);
			dialog.setTitle(R.string.osm_pick_file);
			dialog.setContentView(R.layout.filelist);
			ListView displayList = (ListView) dialog
					.findViewById(R.id.listViewFiles);

			displayList.setAdapter(new ArrayAdapter<String>(
					getApplicationContext(),
					android.R.layout.simple_list_item_multiple_choice,
					file_array));
			displayList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

			displayList.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> av, View v, int index,
						long arg) {
					String chosenFileName = file_array[index];
					files.add(new File(audioFolder, chosenFileName));
					Utilities.LogInfo("files.add(" + files.toString() + ")");
				}

			});
			Button btnSend = (Button) dialog.findViewById(R.id.btnSend);
			btnSend.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					sendFiles(files, sender);
					dialog.dismiss();
				}
			});
			dialog.show();
		} else {
			Utilities.MsgBox(getString(R.string.sorry),
					getString(R.string.no_files_found), this);
		}
	}

	private void sendFiles(List<File> files, IFileSender sender) {
		progress = new ProgressDialog(ListCoords.this);
		progress.setMessage(getString(R.string.wait));
		progress.setCancelable(false);
		progress.show();
		sender.UploadFile(files);
	}

	private void sendFile(List<File> files, IFileSender sender) {
		progress = new ProgressDialog(ListCoords.this);
		progress.setMessage(getString(R.string.wait));
		progress.setCancelable(false);
		progress.show();
		sender.UploadFile(files);
	}

	// Selection on item the menu context
	public boolean onContextItemSelected(MenuItem item) {
		final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
				.getMenuInfo();
		switch (item.getItemId()) {
		/*
		 * case 100:
		 * 
		 * Cursor cursor = (Cursor) l.getAdapter().getItem(position); String _id
		 * = cursor.getString(cursor.getColumnIndex("_id")); //int _id =
		 * cursor.getInt(cursor.getColumnIndex("_id")); //
		 * Toast.makeText(this," ID : "+_id, Toast.LENGTH_SHORT).show(); Intent
		 * intent = getParent().getIntent(); intent.putExtra("id", _id);
		 * TabActivity tabHost = (TabActivity) this.getParent();
		 * tabHost.getTabHost().setCurrentTab(0);
		 * 
		 * Intent intent = getParent().getIntent(); intent.putExtra("id", info.;
		 * TabActivity ta = (TabActivity) this.getParent();
		 * ta.setDefaultTab("maps"); ta.getTabHost().setCurrentTab(0); //
		 * Toast.makeText(this, "ID: "+info.id, Toast.LENGTH_SHORT).show();
		 * break;
		 */
		case 200:

			if (checkConn(this)) {
				setDataCoords(Long.toString(info.id));
				Bundle bundle = new Bundle();
				bundle.putString("id", Long.toString(info.id));
				bundle.putString("lat", getLat());
				bundle.putString("lon", getLon());
				bundle.putString("date", getDate());
				bundle.putString("hour", getHour());
				bundle.putString("url_photo", getUrlPhoto());
				bundle.putBoolean("photo", false);
				Intent intentLocation = new Intent();
				intentLocation.putExtras(bundle);
				intentLocation.setClass(ListCoords.this, Location.class);
				startActivity(intentLocation);
			} else {
				Toast.makeText(this, this.getString(R.string.no_network),
						Toast.LENGTH_LONG).show();
			}

			break;
		case 300:
			if (checkConn(this)) {
				setDataCoords(Long.toString(info.id));
				Bundle bundle = new Bundle();
				bundle.putString("id", Long.toString(info.id));
				bundle.putString("lat", getLat());
				bundle.putString("lon", getLon());
				bundle.putString("date", getDate());
				bundle.putString("hour", getHour());
				bundle.putString("url_photo", getUrlPhoto());
				bundle.putBoolean("photo", true);
				Intent intentLocation = new Intent();
				intentLocation.putExtras(bundle);
				intentLocation.setClass(ListCoords.this, Location.class);
				startActivity(intentLocation);
			} else {
				Toast.makeText(this, this.getString(R.string.no_network),
						Toast.LENGTH_LONG).show();
			}
			break;
		case 400:
			AlertDialog.Builder builder = new AlertDialog.Builder(
					ListCoords.this);
			builder.setMessage(R.string.delete_register)
					.setCancelable(false)
					.setTitle(R.string.alert)
					.setIcon(R.drawable.ic_alert)
					.setPositiveButton(R.string.yes,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									if (getList().equals("2")) {
										Log.i(LOG, "db.deleteVoices(info.id)");
										deleteFileInSDCard(info.id);
										if (db.deleteVoices(info.id)) {
											Toast.makeText(ListCoords.this,
													R.string.delete_success,
													Toast.LENGTH_SHORT).show();
											loadList();
										}
									} else {
										Log.i(LOG, "db.deleteCoords(info.id)");
										if (db.deleteCoords(info.id)) {
											Toast.makeText(ListCoords.this,
													R.string.delete_success,
													Toast.LENGTH_SHORT).show();
											loadList();
										}
									}
								}
							})
					.setNegativeButton(R.string.no,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
								}
							});
			AlertDialog alertd = builder.create();
			alertd.show();
			break;
		case 500:
			if (isSendEmail()) {
				files.clear();
				files.add(new File(audioFolder, db.selectOneFileAudio(info.id)));
				progress = new ProgressDialog(ListCoords.this);
				progress.setMessage(getString(R.string.wait));
				progress.setCancelable(false);
				progress.show();
				UploadFile(files);
				// sendFile(files, this.sender);
				files.clear();
			} else {
				Toast.makeText(this, getString(R.string.turn_on_send_email),
						Toast.LENGTH_SHORT).show();
				Intent settings = new Intent();
				settings.setClass(ListCoords.this, Settings.class);
				startActivity(settings);
			}
			break;
		}
		return true;
	}

	@SuppressLint("SdCardPath")
	private void deleteFileInSDCard(long id) {
		String file_name = "/sdcard/JoTracker/Audio/";
		file_name += db.selectOneFileAudio(id);
		File file = new File(file_name);
		if (file.exists()) {
			boolean deleted = false;
			deleted = file.delete();
			if (!deleted) {
				Toast.makeText(
						ListCoords.this,
						"Delete "
								+ file_name.replaceAll(
										"/sdcard/JoTracker/Audio", "")
								+ " file faill!", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(
						ListCoords.this,
						"The "
								+ file_name.replaceAll(
										"/sdcard/JoTracker/Audio", "")
								+ " file was deleted!", Toast.LENGTH_SHORT)
						.show();
			}
		} else {
			Toast.makeText(
					ListCoords.this,
					file_name.replaceAll("/sdcard/JoTracker/Audio", "")
							+ " not exists!", Toast.LENGTH_SHORT).show();
		}
	}

	public boolean deleteDirectory(File path) {
		if (path.exists()) {
			File[] files = path.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					deleteDirectory(files[i]);
				} else {
					files[i].delete();
				}
			}
		}
		return (path.delete());
	}

	private String getLat() {
		return strLat;
	}

	public void setLat(String lat) {
		this.strLat = lat;
	}

	private String getLon() {
		return strLon;
	}

	public void setLon(String lon) {
		strLon = lon;
	}

	public void setDataCoords(String id) {
		String coord = null;
		try {
			coord = db.selectCoords(id);
			// Toast.makeText(this, "Data: "+coord.toString(),
			// Toast.LENGTH_LONG).show();
			String c[] = coord.split(",");
			setLat(c[3]);
			setLon(c[4]);
			setDate(c[6]);
			setHour(c[7]);
			setUrlPhoto(c[11]);
			// Log.i(" setDataCoords(): ", c[3]+","+c[4]);
		} catch (Exception e) {
			Log.i("Exception setDataCoords(): ", e.getMessage());
		}
	}

	public boolean checkConn(Context ctx) {
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

	public String getDate() {
		return strDate;
	}

	public void setDate(String strDate) {
		this.strDate = strDate;
	}

	public String getHour() {
		return strHour;
	}

	public void setHour(String strHour) {
		this.strHour = strHour;
	}

	public String getUrlPhoto() {
		return strUrlPhoto;
	}

	private boolean isSendEmail() {
		try {
			sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
			send_email = sharedPrefs.getBoolean("send_email", false);
			//Log.i(LOG, "isSendEmail(" + send_email + ")");
		} catch (Exception ex) {
			Log.e(LOG, "isSendEmail()  ::" + ex.getMessage());
		}
		return send_email;
	}

	public void setUrlPhoto(String strUrlPhoto) {
		this.strUrlPhoto = strUrlPhoto;
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
				getString(R.string.send_files_sucess), this);
	}

	public void OnComplete() {
		progress.dismiss();
		handler.post(successfullySent);
	}

	public void OnFailure() {
		progress.dismiss();
		handler.post(failedSend);
	}

	public boolean accept(File dir, String filename) {
		return false;
	}

	public void UploadFile(List<File> files) {
		ArrayList<File> filesToSend = new ArrayList<File>();
		filesToSend.clear();
		for (File f : files) {
			filesToSend.add(f);
		}
		Thread t = new Thread(new AutoSendHandler(
				filesToSend.toArray(new File[filesToSend.size()]), this,
				getBaseContext()));
		t.start();
		filesToSend.clear();
	}

	class AutoSendHandler implements Runnable {

		private SmtpSettings smtp;
		private String smtpServer, smtpPort, smtpUsername, smtpPassword,
				csvEmailTargets, fromAddress;
		boolean smtpUseSsl;
		File[] files;
		private final IActionListener helper;

		public AutoSendHandler(File[] files, IActionListener helper,
				Context context) {
			this.files = files;
			this.helper = helper;
			smtp = new SmtpSettings(context);
			this.smtpServer = smtp.getSmtpServer();
			this.smtpPort = smtp.getSmtpPort();
			this.smtpPassword = smtp.getStmpPassword();
			this.smtpUsername = smtp.getSmtpUsername();
			this.smtpUseSsl = smtp.isUseSsl();
			this.csvEmailTargets = smtp.getSmtpFrom();
			this.fromAddress = smtp.getSmtpFrom();
		}

		public void run() {
			try {

				Mail email = new Mail(smtpUsername, smtpPassword);

				String[] toArr = csvEmailTargets.split(",");
				email.setTo(toArr);

				if (fromAddress != null && fromAddress.length() > 0) {
					email.setFrom(fromAddress);
				} else {
					email.setFrom(smtpUsername);
				}

				email.setSubject("JoTracker :: File Upload at "
						+ Utilities.GetReadableDateTime(new Date()));
				StringBuffer message = new StringBuffer();
				message.append("Hello,\n\n");
				message.append("This is an automated email with attached file, please do not respond.\n\n");
				message.append("Regards,\n\n");
				message.append("JOTracker\n");
				message.append("www.jotracker.com\n");
				message.append("jo-tracker@hotmail.com\n");
				email.setBody(message.toString());
				email.setBody(message.toString());

				email.setPort(smtpPort);
				email.setSecurePort(smtpPort);
				email.setSmtpHost(smtpServer);
				email.setSsl(smtpUseSsl);
				for (File f : files) {
					email.addAttachment(f.getName(), f.getAbsolutePath());
				}
				email.setDebuggable(true);

				Utilities.LogInfo("Sending email...");
				if (email.send()) {
					helper.OnComplete();
				} else {
					helper.OnFailure();
				}
			} catch (Exception e) {
				helper.OnFailure();
				Utilities.LogError("AutoSendHandler.run", e);
			}

		}

	}

}