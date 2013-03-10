package com.jotracker;

import java.io.IOException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.TabHost;

import com.jotracker.service.ServiceSMS;

public class Main extends TabActivity implements Runnable {
	// private static final String LOG = "Main";
	// private SMSReceiver receiver = new SMSReceiver();
	private String serial = "0";
	public static final int ADD_SERIAL = 1;
	public SharedPreferences sharedPrefs;
	private License license = null;
	private String date_server;
	private boolean execao = false;
	static final String PREFS_DATE_SERVER = "DATE_SERVER";
	static final String PREFS_SOUND = "SOUND";
	private String sound;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		license = new License(this);

		TabHost tabHost = getTabHost(); // The activity TabHost
		TabHost.TabSpec spec; // Reusable TabSpec for each tab
		Intent intent; // Reusable Intent for each tab
		Resources res = getResources();

		// Create an Intent to launch an Activity for the tab (to be reused)
		intent = new Intent().setClass(this, Maps.class);
		// Initialize a TabSpec for each tab and add it to the TabHost
		spec = tabHost.newTabSpec(this.getString(R.string.tab_map));
		spec.setContent(intent);
		spec.setIndicator(this.getString(R.string.tab_map),
				res.getDrawable(R.drawable.tab_maps));
		tabHost.setTag("maps");
		tabHost.setId(0);
		tabHost.addTab(spec);

		// Do the same for the other tabs
		intent = new Intent().setClass(this, ListCoords.class);
		spec = tabHost.newTabSpec(this.getString(R.string.tab_historic));
		spec.setContent(intent);
		spec.setIndicator(this.getString(R.string.tab_historic),
				res.getDrawable(R.drawable.tab_coords));
		tabHost.setTag("historic");
		tabHost.setId(1);
		tabHost.addTab(spec);

		// Do the same for the other tabs
		intent = new Intent().setClass(this, Trackers.class);
		spec = tabHost.newTabSpec(this.getString(R.string.tab_trackers));
		spec.setContent(intent);
		spec.setIndicator(this.getString(R.string.tab_trackers),
				res.getDrawable(R.drawable.tab_trackers));
		tabHost.setTag("trackers");
		tabHost.setId(2);
		tabHost.addTab(spec);
		
		// Do the same for the other tabs
		intent = new Intent().setClass(this, RouteMaps.class);
		spec = tabHost.newTabSpec(this.getString(R.string.tab_route));
		spec.setContent(intent);
		spec.setIndicator(this.getString(R.string.tab_route),
				res.getDrawable(R.drawable.tab_route));
		tabHost.setTag("route");
		tabHost.setId(3);
		tabHost.addTab(spec);

		// Do the same for the other tabs
		intent = new Intent().setClass(this, About.class);
		spec = tabHost.newTabSpec(this.getString(R.string.tab_about));
		spec.setContent(intent);
		spec.setIndicator(this.getString(R.string.tab_about),
				res.getDrawable(R.drawable.tab_about));
		tabHost.setTag("about");
		tabHost.setId(4);
		tabHost.addTab(spec);

		tabHost.setCurrentTabByTag("maps");
		/*
		 * registerReceiver(receiver, new IntentFilter(
		 * "android.provider.Telephony.SMS_RECEIVED"));
		 */
		sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
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
		super.onDestroy();
		// Log.d(LOG, " onDestroy()");
	}

	// This is used to determine if the Splash page should be displayed before
	// the
	// Activity is launched.
	@Override
	protected void onResume() {

		serial = sharedPrefs.getString("serial", "0");

		if (isOnline(this)) {
			/*
			DateServer date = new DateServer();
			date.execute("");*/
			// Log.d("serial", serial.toString());
			// Log.d("serial", md5(getSerial()).toString());
			//license.verifyLicenses(this, "2012-11-05");
		}
		/*
		 * startService(new Intent(Main.this, ServiceGPS.class));
		 */
		startService(new Intent(Main.this, ServiceSMS.class));

		super.onResume();
		// Log.d(LOG, " onResume()");
	}

	public void showMessage() {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle(this.getString(R.string.message));
		alert.setMessage(this.getString(R.string.no_network));
		alert.setPositiveButton(R.string.ok,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						Main.this.finish();
					}
				});
		alert.show();
	}

	protected void start(Intent intent) {
		this.startActivityForResult(intent, ADD_SERIAL);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case ADD_SERIAL:
			if (resultCode == RESULT_OK) {

				break;
			}
		}
	}

	// storing a timestamp when the user quits the app
	@Override
	protected void onPause() {
		// unregisterReceiver(receiver);
		super.onPause();
		// Log.d(LOG, " onPause()");
	}

	public void run() {
	}

	public String getSerial() {
		String serial = "35" + Build.BOARD.length() % 10 + Build.BRAND.length()
				% 10 + Build.CPU_ABI.length() % 10 + Build.DEVICE.length() % 10
				+ Build.DISPLAY.length() % 10 + Build.HOST.length() % 10
				+ Build.ID.length() % 10 + Build.MANUFACTURER.length() % 10
				+ Build.MODEL.length() % 10 + Build.PRODUCT.length() % 10
				+ Build.TAGS.length() % 10 + Build.TYPE.length() % 10
				+ Build.USER.length() % 10; // 13 digits
		return serial;
	}

	public String md5(String s) {
		try {
			// Create MD5 Hash
			MessageDigest digest = java.security.MessageDigest
					.getInstance("MD5");
			digest.update(s.getBytes());
			byte messageDigest[] = digest.digest();

			// Create Hex String
			StringBuffer hexString = new StringBuffer();
			for (int i = 0; i < messageDigest.length; i++)
				hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
			return hexString.toString();

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return "";
	}

	public String getSoundPref() {
		try {
			SharedPreferences settings = getSharedPreferences(PREFS_SOUND, 0);
			sound = settings.getString("sound", "alarm");
			Log.e("PREFS_SOUND " + getClass(), "getSoundPref()  :: " + sound);
		} catch (Exception ex) {
			Log.e("PREFS_SOUND " + getClass(),
					"getSoundPref()  :: " + ex.getMessage());
		}
		return sound;
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

	public void checkLicenses(String data_server) {
		license.verifyLicenses(this, data_server);
	}

	public String getDateServer() {
		return date_server;
	}

	public void setDateServer(String date_server) {
		this.date_server = date_server;
	}

	private static String getTagValue(String tag, Element element) {
		NodeList nl = element.getElementsByTagName(tag).item(0).getChildNodes();
		Node node = (Node) nl.item(0);
		return node.getNodeValue();
	}

	public class DateServer extends AsyncTask<String, Integer, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		protected String doInBackground(String... params) {
			try {
				URL url = new URL("http://www.users.jotracker.com/date.php");
				DocumentBuilderFactory dbf = DocumentBuilderFactory
						.newInstance();
				DocumentBuilder builder = null;
				try {
					builder = dbf.newDocumentBuilder();
				} catch (ParserConfigurationException e) {
					e.printStackTrace();
				}
				Document document = builder.parse(url.openStream());
				document.getDocumentElement().normalize();
				NodeList nl = document.getElementsByTagName("Date");
				for (int i = 0; i < nl.getLength(); i++) {
					Node node = nl.item(i);
					if (node.getNodeType() == Node.ELEMENT_NODE) {
						Element element = (Element) node;
						setDateServer((getTagValue("CurrentDate", element)));
						System.out.println("Date Server => CurrentDate = "
								+ getDateServer());

					}
				}
			} catch (SAXException e) {
				execao = true;
				e.printStackTrace();
			} catch (IOException e) {
				execao = true;
				e.printStackTrace();
			}
			if (execao) {
				return getCurrentDateAndroid();
			} else {
				return getDateServer();
			}
		}

		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (execao) {
				System.out.println("onPostExecute(String result)" + execao);
			} else {
				//checkLicenses(result);
				saveDateServer(result);
				// System.out.println("onPostExecute(String result)"+ result);
			}
		}

		protected void onProgressUpdate(Integer[]... values) {
			// super.onProgressUpdate(values);
		}

		public String getCurrentDateAndroid() {
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			Calendar cal = Calendar.getInstance();
			return dateFormat.format(cal.getTime());
		}
	}

	public void saveDateServer(String date_server) {
		try {
			SharedPreferences settings = getSharedPreferences(
					PREFS_DATE_SERVER, 0);
			SharedPreferences.Editor editor = settings.edit();
			editor.putString("date_server", "" + date_server);
			editor.commit();
			Log.i("DATE_SERVER ", date_server);
		} catch (Exception ex) {
			Log.e("DATE_SERVER ", "SaveDataServer()  ::" + ex.getMessage());
		}
	}
}