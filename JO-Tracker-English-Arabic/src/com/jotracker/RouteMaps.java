package com.jotracker;

import java.util.Calendar;
import java.util.List;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.jotracker.data.DBAdapter;
import com.jotracker.route.RoutePathOverlay;

public class RouteMaps extends MapActivity {
	// private static final String LOG = "Maps";

	private MapView mapView = null;
	private DBAdapter db;
	private double latitude, longitude = 0.0;
	private String strImei, strPhone, strLat, strLon, strSpeed, strDate,
			strHour, strTracker, strVehicle, strPower, strAcc, strDoor,
			vehicle_selected = null, date_selected = null, day, month;
	private List<Overlay> mapOverlays;
	private Drawable drawable;
	private MapItemizedOverlay itemizedOverlay;
	static final String PREFS_ZOOM = "ZOOM";

	private String zoomlevel;
	private int defaultZoom = 3;

	private Spinner spinner;
	private ImageButton imbDate, imbPlay;

	private int pYear, pMonth, pDay;
	static final int DATE_DIALOG_ID = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.route_maps);
		db = new DBAdapter(this);
		mapView = (MapView) findViewById(R.id.mapView);
		spinner = (Spinner) findViewById(R.id.spVehicles);
		imbDate = (ImageButton) findViewById(R.id.btnDate);
		imbPlay = (ImageButton) findViewById(R.id.btnPlay);
		mapView.setClickable(true);
		mapOverlays = mapView.getOverlays();
		drawable = getResources().getDrawable(R.drawable.flag);
		itemizedOverlay = new MapItemizedOverlay(drawable, mapView);

		getZoom();
		getCurrentDate();

		imbDate.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				showDialog(DATE_DIALOG_ID);
			}
		});

		imbPlay.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// clearMap();
				if (getDateSelected() == null) {
					displayToast(getString(R.string.select_date));
				} else if (getVehicleSelected() == null) {
					displayToast(getString(R.string.select_vehicle));
				} else {
					loadCoords(getVehicleSelected(), getDateSelected());
					loadRoute(getVehicleSelected(), getDateSelected());
				}
			}
		});
		spinner.setOnItemSelectedListener(new VehicleOnItemSelectedListener());
	}

	private void getCurrentDate() {
		/** Get the current date */
		final Calendar cal = Calendar.getInstance();
		pYear = cal.get(Calendar.YEAR);
		pMonth = cal.get(Calendar.MONTH);
		pDay = cal.get(Calendar.DAY_OF_MONTH);
		if (pDay < 10) {
			day = "0" + String.valueOf(pDay);
		} else {
			day = String.valueOf(pDay);
		}
		if (pMonth < 10) {
			month = "0" + String.valueOf(pMonth) + "-";
		} else {
			month = String.valueOf(pMonth) + "-";
		}
		setDateSelected(String.valueOf(pYear)  + "-" + month + day );
		Log.d("Date:", "getCurrentDate() " + getDateSelected());
	}

	/** Callback received when the user "picks" a date in the dialog */
	private DatePickerDialog.OnDateSetListener pDateSetListener = new DatePickerDialog.OnDateSetListener() {
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			pYear = year;
			pMonth = monthOfYear + 1;
			pDay = dayOfMonth;
			if (pDay < 10) {
				day = "0" + String.valueOf(pDay);
			} else {
				day = String.valueOf(pDay);
			}
			if (pMonth < 10) {
				month = "0" + String.valueOf(pMonth) + "-";
			} else {
				month = String.valueOf(pMonth) + "-";
			}
			setDateSelected(String.valueOf(pYear) + "-" + month + day );
			Log.d("Date:", " DatePickerDialog() " + getDateSelected());
		}
	};

	/** Displays a notification when the date is updated */
	private void displayToast(String message) {
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

	}

	/** Create a new dialog for date picker */
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DATE_DIALOG_ID:
			return new DatePickerDialog(this, pDateSetListener, pYear, pMonth,
					pDay);
		}
		return null;
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
		SaveZoom();
		super.onDestroy();
		// Log.d(LOG, " onDestroy()");
	}

	// This is used to determine if the Splash page should be displayed before
	// the
	// Activity is launched.
	@Override
	protected void onResume() {
		loadSpinnerData();
		getZoom();
		super.onResume();
		// Log.d("LOG", " onResume()");
	}

	// storing a timestamp when the user quits the app
	@Override
	protected void onPause() {
		SaveZoom();
		// myLocation.disableCompass();
		super.onPause();
		// Log.d(LOG, " onPause()");
	}

	/**
	 * Function to load the spinner data from SQLite database
	 * */
	private void loadSpinnerData() {
		// Spinner Drop down elements
		db.open();
		List<String> vehicles = db.getAllVehicles();
		db.close();
		// Creating adapter for spinner
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, vehicles);
		// Drop down layout style - list view with radio button
		dataAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// attaching data adapter to spinner
		spinner.setAdapter(dataAdapter);
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

	public void closeApp() {
		Log.d(getClass().getName(), "closeApp()");
		this.finish();
	}

	private String getImei() {
		return strImei;
	}

	public void setImei(String imei) {
		strImei = imei;
	}

	private String getPhone() {
		return strPhone;
	}

	private void setPhone(String strPhone) {
		this.strPhone = strPhone;
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

	private String getSpeed() {
		return strSpeed;
	}

	public void setSpeed(String speed) {
		this.strSpeed = speed;
	}

	private String getDate() {
		return strDate;
	}

	public void setDate(String date) {
		strDate = date;
	}

	private String getHour() {
		return strHour;
	}

	public void setHour(String hour) {
		this.strHour = hour;
	}

	public String getVehicle() {
		return strVehicle;
	}

	public void setVehicle(String vehicle) {
		this.strVehicle = vehicle;
	}

	public void loadCoords(String vehicle, String date) {
		db.open();
		List<String> points = db.selectRouteByVechicleAndDate(vehicle, date);
		db.close();
		if (points.size() > 0) {
			for (String value : points) {
				loadCoordsInMap(value);
				System.out.println(value);
				// displayToast(value);
			}
		} else {
			System.out.println(getString(R.string.no_results));
			displayToast(getString(R.string.no_results));
		}
	}

	public void loadRoute(String vehicle, String date) {
		db.open();
		List<GeoPoint> path = db.selectPointsByVechicleAndDate(vehicle, date);
		db.close();
		if (path.size() > 0) {
			mapView.getOverlays().add(new RoutePathOverlay(path));
		}
	}

	public void loadCoordsInMap(String id) {
		setDataCoords(id);
		// setDataTrackers(getPhone());
		String lat, lon, vehicle = null;
		lat = getLat();
		lon = getLon();
		if (isDouble(lat) && isDouble(lon)) {
			// Log.i("Lat/Lon: ", lat+","+lon);
			if (!lat.equals("0") && !lon.equals("0")) {
				latitude = Double.parseDouble(lat);
				longitude = Double.parseDouble(lon);
				vehicle = getVehicle().toString();
				// Log.i("Vehicle ", vehicle.toString());
				if (vehicle.equals("0"))
					vehicle = " ";
				//OverlayItem overlayItem = new OverlayItem()
				OverlayItem overlayItem = new OverlayItem(getPoint(latitude,
						longitude), getVehicle() + " - "
						+ getPhone().toString(), "Lat: " + getLat() + " Lon: "
						+ getLon() + "\n" + this.getString(R.string.date_hour)
						+ " " + getDate() + " " + getHour() + "\n"
						+ this.getString(R.string.speed) + " " + getSpeed()
						+ " " + this.getString(R.string.power) + " "
						+ getPower() + " " + this.getString(R.string.acc)
						+ getAcc() + " " + this.getString(R.string.door)
						+ getDoor() + "\n");
				// "\nEndereço: "+getAddress());
				itemizedOverlay.addOverlay(overlayItem);
				mapOverlays.add(itemizedOverlay);
			}
		}
		mapView.getController().setCenter(getPoint(latitude, longitude));
		mapView.getController().setZoom(Integer.valueOf(defaultZoom));
		mapView.setBuiltInZoomControls(true);
	}

	private GeoPoint getPoint(double lat, double lon) {
		return (new GeoPoint((int) (lat * 1000000.0), (int) (lon * 1000000.0)));
	}

	public boolean isDouble(String input) {
		try {
			Double.parseDouble(input);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private void getZoom() {
		try {
			SharedPreferences settings = getSharedPreferences(PREFS_ZOOM, 0);
			zoomlevel = settings.getString("zoom_level", "");
			// Log.i("ZOOM ", String.valueOf(zoomlevel));
			if (zoomlevel.length() > 0)
				defaultZoom = Integer.parseInt(zoomlevel);
			else
				defaultZoom = 3;
		} catch (Exception ex) {
			Log.e("ZOOM ", "Exception GetZoom()  ::" + ex.getMessage());
		}
	}

	private void SaveZoom() {
		try {
			SharedPreferences settings = getSharedPreferences(PREFS_ZOOM, 0);
			SharedPreferences.Editor editor = settings.edit();
			defaultZoom = mapView.getZoomLevel();
			editor.putString("zoom_level", "" + defaultZoom);
			editor.commit();
			// Log.i("ZOOM ", String.valueOf(defaultZoom));
		} catch (Exception ex) {
			Log.e("ZOOM ", "Exception SaveZoom()  ::" + ex.getMessage());
		}
	}

	private void clearMap() {
		if (!mapOverlays.isEmpty()) {
			mapOverlays.clear();
			mapView.getOverlays().clear();
			mapView.invalidate();
		}
	}

	public void setDataCoords(String id) {
		String coord = null;
		try {
			db.open();
			if (id == null) {
				coord = db.selectLastCoords();
			} else {
				coord = db.selectCoords(id);
			}
			// Toast.makeText(this, "Data: "+coord.toString(),
			// Toast.LENGTH_LONG).show();
			String c[] = coord.split(",");
			setPhone(c[0]);
			setImei(c[1]);
			setVehicle(c[2]);
			setLat(c[3]);
			setLon(c[4]);
			setSpeed(c[5]);
			setDate(c[6]);
			setHour(c[7]);
			setPower(c[8]);
			setAcc(c[9]);
			setDoor(c[10]);
			db.close();
		} catch (Exception e) {
			Log.i("Exception setDataCoords(): ", e.getMessage());
		}
	}

	public void setDataTrackers(String phone) {
		try {
			db.open();
			strTracker = db.selectOneTrackerByPhone(formatPhone(phone
					.toString()));
			String t[] = strTracker.split(",");
			setVehicle(t[2]);
			setPhone(t[4]);
			// Toast.makeText(this, "Phone: "+formatPhone(phone.toString()),
			// Toast.LENGTH_LONG).show();
			// Toast.makeText(this, "Vehicle: "+getVehicle().toString(),
			// Toast.LENGTH_LONG).show();
			db.close();
		} catch (Exception e) {
			Log.i("Exception setDataTrackers(): ", e.getMessage());
		}
	}

	public String formatPhone(String phone) {
		StringBuffer sb = new StringBuffer(phone);
		sb.reverse();
		int tam = 0, start = 0, end = 0;
		tam = sb.length();
		if (tam > 8)
			end = 8;
		else {
			end = tam;
		}
		StringBuffer sbr = new StringBuffer(sb.substring(start, end));
		return sbr.reverse().toString();
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.options_menu_route_maps, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.satellite:
			mapView.setSatellite(!mapView.isSatellite());
			return true;
		case R.id.trafic:
			mapView.setTraffic(!mapView.isTraffic());
			return true;
		case R.id.clear:
			clearMap();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.findItem(R.id.satellite)
				.setIcon(
						mapView.isSatellite() ? android.R.drawable.checkbox_on_background
								: android.R.drawable.checkbox_off_background);
		menu.findItem(R.id.trafic).setIcon(
				mapView.isTraffic() ? android.R.drawable.checkbox_on_background
						: android.R.drawable.checkbox_off_background);
		return true;
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
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
		if (keyCode == KeyEvent.KEYCODE_S) {
			mapView.setSatellite(!mapView.isSatellite());
			return (true);
		} else if (keyCode == KeyEvent.KEYCODE_Z) {
			mapView.displayZoomControls(true);
			return (true);
		}
		return (super.onKeyDown(keyCode, event));
	}

	public String getPower() {
		return strPower;
	}

	public void setPower(String strPower) {
		this.strPower = strPower;
	}

	public String getAcc() {
		return strAcc;
	}

	public void setAcc(String strAcc) {
		this.strAcc = strAcc;
	}

	public String getDoor() {
		return strDoor;
	}

	public void setDoor(String strDoor) {
		this.strDoor = strDoor;
	}

	public String getVehicleSelected() {
		return vehicle_selected;
	}

	public void setVehicleSelected(String vehicle_selected) {
		this.vehicle_selected = vehicle_selected;
	}

	public String getDateSelected() {
		return date_selected;
	}

	public void setDateSelected(String date_selected) {
		this.date_selected = date_selected;
	}

	public class VehicleOnItemSelectedListener implements
			OnItemSelectedListener {
		public void onItemSelected(AdapterView<?> parent, View view, int pos,
				long id) {
			String selected = parent.getItemAtPosition(pos).toString();
			setVehicleSelected(selected);
			Log.d("Spinner", " onItemSelected() => " + selected);
		}

		public void onNothingSelected(AdapterView parent) {
			// Do nothing.
		}
	}

}