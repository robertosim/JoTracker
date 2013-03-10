package com.jotracker;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.google.android.maps.Projection;
import com.jotracker.data.DBAdapter;
import com.jotracker.service.ServiceSMS;

public class Maps extends MapActivity {
	// private static final String LOG = "Maps";

	private MapView mapView = null;
	private MyLocationOverlay myLocation = null;
	private DBAdapter db;
	private double latitude, longitude = 0.0;
	private String strImei, strPhone, strLat, strLon, strSpeed, strDate,
			strHour, strTracker, strVehicle, strCurLat, strCurLon, strPower,
			strAcc, strDoor = null;
	private List<Overlay> mapOverlays;
	private Drawable drawable;
	private MapItemizedOverlay itemizedOverlay;
	static final String PREFS_ZOOM = "ZOOM";

	private String zoomlevel;
	private int defaultZoom = 3;

	private ProgressDialog progresDialog;

	// Milliseconds

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.maps);
		db = new DBAdapter(this);
		mapView = (MapView) findViewById(R.id.mapView);
		mapView.setClickable(true);
		mapOverlays = mapView.getOverlays();
		drawable = getResources().getDrawable(R.drawable.marker);
		itemizedOverlay = new MapItemizedOverlay(drawable, mapView);
		GetZoom();

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
		GetZoom();
		loadCoords();
		loadRoute();
		super.onResume();
		// Log.d("LOG", " onResume()");
	}

	// storing a timestamp when the user quits the app
	@Override
	protected void onPause() {
		SaveZoom();
		myLocation.disableCompass();
		super.onPause();
		// Log.d(LOG, " onPause()");
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

	private String getCurLat() {
		return strCurLat;
	}

	private void setCurLat(String strCurLat) {
		this.strCurLat = strCurLat;
	}

	public String getCurLon() {
		return strCurLon;
	}

	public void setCurLon(String strCurLon) {
		this.strCurLon = strCurLon;
	}

	public void loadCoords() {
		Intent intent = getParent().getIntent();
		String id = intent.getStringExtra("id");
		loadCoordsInMap(id);
		// Log.i("loadCoords(): ", "loadCoordsInMap("+id+")");
	}

	public void loadRoute() {
		Intent intent = getParent().getIntent();
		boolean loadRoute = intent.getBooleanExtra("loadRoute", false);
		Log.i("loadRoute: ", String.valueOf(loadRoute).toString());
		if (loadRoute) {
			if (checkGPS()) {
				if (checkConn(this)) {
					progresDialog = ProgressDialog.show(this,
							this.getString(R.string.address),
							this.getString(R.string.loading), true, false);
					progresDialog.setCancelable(true);
					mapOverlays.clear();
					loadCurrentLoation();
					setDataRoute();
				} else {
					Toast.makeText(this, this.getString(R.string.no_network),
							Toast.LENGTH_LONG).show();
				}
			} else {
				createGpsDisabledAlert();
			}
		}
	}

	public void setDataRoute() {
		Intent intent = getParent().getIntent();
		String dest_lat, dest_lon = null;
		dest_lat = intent.getStringExtra("lat");
		dest_lon = intent.getStringExtra("lon");

		if (dest_lat != null && dest_lon != null) {
			if (getCurLat() != "0" && getCurLon() != "0") {
				if (isDouble(dest_lat) && isDouble(dest_lon)
						&& isDouble(getCurLat()) && isDouble(getCurLon())) {
					loadRouteInMap(Double.parseDouble(getCurLat()),
							Double.parseDouble(getCurLon()),
							Double.parseDouble(dest_lat),
							Double.parseDouble(dest_lon));
				}
			} else
				Toast.makeText(this, this.getString(R.string.no_gps),
						Toast.LENGTH_LONG).show();
		}
	}

	public void loadRouteInMap(double src_lat, double src_long,
			double dest_lat, double dest_long) {
		GeoPoint srcGeoPoint = new GeoPoint((int) (src_lat * 1E6),
				(int) (src_long * 1E6));
		GeoPoint destGeoPoint = new GeoPoint((int) (dest_lat * 1E6),
				(int) (dest_long * 1E6));
		DrawPath(srcGeoPoint, destGeoPoint, Color.RED, mapView);
		mapView.getController().animateTo(srcGeoPoint);
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

	public boolean checkGPS() {
		final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			return false;
		}
		return true;
	}

	private void createGpsDisabledAlert() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(this.getString(R.string.disabled_gps))
				.setCancelable(false)
				.setPositiveButton(this.getString(R.string.enable_gps),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								showGpsOptions();
							}
						});
		builder.setNegativeButton(this.getString(R.string.cancel),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
		AlertDialog alert = builder.create();
		alert.show();
	}

	private void showGpsOptions() {
		Intent gpsOptionsIntent = new Intent(
				android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		startActivity(gpsOptionsIntent);
	}

	private void loadCurrentLoation() {
		SharedPreferences sharedPreferences = getSharedPreferences(
				"SHARED_PREF_LOCATION", MODE_PRIVATE);
		String curlat = sharedPreferences.getString("latitude", "0");
		String curlon = sharedPreferences.getString("longitude", "0");
		setCurLat(curlat);
		setCurLon(curlon);
		// Log.d("Maps:", curlat.toString()+" , "+curlon.toString());
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
				OverlayItem overlayItem = new OverlayItem(getPoint(latitude,
						longitude), getVehicle() + " - "
						+ getPhone().toString(), "Lat: " + getLat() + " Lon: "
						+ getLon() + "\n" + this.getString(R.string.date_hour)
						+ " " + getDate() + " " + getHour() + "\n"
						+ this.getString(R.string.speed) + " " + getSpeed()+ " " 
						+ this.getString(R.string.power) + " " + getPower()
						+ " " + this.getString(R.string.acc) + getAcc() + " "
						+ this.getString(R.string.door) + getDoor() + "\n");
				// "\nEndereço: "+getAddress());
				itemizedOverlay.addOverlay(overlayItem);
				mapOverlays.add(itemizedOverlay);
			}
		}
		mapView.getController().setCenter(getPoint(latitude, longitude));
		mapView.getController().setZoom(Integer.valueOf(defaultZoom));
		mapView.setBuiltInZoomControls(true);
		myLocation = new MyLocationOverlay(this, mapView);
		mapView.getOverlays().add(myLocation);
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

	private void GetZoom() {
		try {
			SharedPreferences settings = getSharedPreferences(PREFS_ZOOM, 0);
			zoomlevel = settings.getString("zoom_level", "");
			// Log.i("ZOOM ", String.valueOf(zoomlevel));
			if (zoomlevel.length() > 0)
				defaultZoom = Integer.parseInt(zoomlevel);
			else
				defaultZoom = 10;
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
		inflater.inflate(R.menu.options_menu_maps, menu);
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

	public int SortMarker() {
		List<Integer> lista = new ArrayList<Integer>();
		lista.add(1);
		lista.add(2);
		lista.add(3);
		lista.add(4);
		Collections.shuffle(lista);
		return ((Integer) lista.get(0)).intValue();
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

	// //////////////////////////////////////////////////////////////////////////////////////////////////////

	private void DrawPath(GeoPoint src, GeoPoint dest, int color,
			MapView mMapView) {
		// connect to map web service
		StringBuilder urlString = new StringBuilder();
		urlString.append("http://maps.google.com/maps?f=d&hl=en");
		urlString.append("&saddr=");// from
		urlString.append(Double.toString((double) src.getLatitudeE6() / 1.0E6));
		urlString.append(",");
		urlString
				.append(Double.toString((double) src.getLongitudeE6() / 1.0E6));
		urlString.append("&daddr=");// to
		urlString
				.append(Double.toString((double) dest.getLatitudeE6() / 1.0E6));
		urlString.append(",");
		urlString
				.append(Double.toString((double) dest.getLongitudeE6() / 1.0E6));
		urlString.append("&ie=UTF8&0&om=0&output=kml");
		Log.d("xxx", "URL=" + urlString.toString());
		// get the kml (XML) doc. And parse it to get the coordinates(direction
		// route).
		Document doc = null;
		HttpURLConnection urlConnection = null;
		URL url = null;
		try {
			url = new URL(urlString.toString());
			urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setRequestMethod("GET");
			urlConnection.setDoOutput(true);
			urlConnection.setDoInput(true);
			urlConnection.connect();
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			doc = db.parse(urlConnection.getInputStream());
			if (doc.getElementsByTagName("GeometryCollection").getLength() > 0) {
				// String path =
				// doc.getElementsByTagName("GeometryCollection").item(0).getFirstChild().getFirstChild().getNodeName();
				String path = doc.getElementsByTagName("GeometryCollection")
						.item(0).getFirstChild().getFirstChild()
						.getFirstChild().getNodeValue();

				Log.d("xxx", "path=" + path);
				String[] pairs = path.split(" ");
				String[] lngLat = pairs[0].split(","); // lngLat[0]=longitude
														// lngLat[1]=latitude
														// lngLat[2]=height
				// src
				GeoPoint startGP = new GeoPoint(
						(int) (Double.parseDouble(lngLat[1]) * 1E6),
						(int) (Double.parseDouble(lngLat[0]) * 1E6));
				mMapView.getOverlays().add(
						new MapRouteOverLay(startGP, startGP, 1));
				GeoPoint gp1;
				GeoPoint gp2 = startGP;
				for (int i = 1; i < pairs.length; i++) // the last one would be
														// crash
				{
					lngLat = pairs[i].split(",");
					gp1 = gp2;
					// watch out! For GeoPoint, first:latitude, second:longitude
					gp2 = new GeoPoint(
							(int) (Double.parseDouble(lngLat[1]) * 1E6),
							(int) (Double.parseDouble(lngLat[0]) * 1E6));
					mMapView.getOverlays().add(
							new MapRouteOverLay(gp1, gp2, 2, color));
					// Log.d("xxx", "pair:" + pairs[i]);

				}
				mMapView.getOverlays().add(new MapRouteOverLay(dest, dest, 3)); // use
				// the
				// default
				// color
				progresDialog.dismiss();
			}
		} catch (MalformedURLException e) {
			progresDialog.dismiss();
			e.printStackTrace();
		} catch (IOException e) {
			progresDialog.dismiss();
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			progresDialog.dismiss();
			e.printStackTrace();
		} catch (SAXException e) {
			progresDialog.dismiss();
			e.printStackTrace();
		}
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////

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

	public class MapRouteOverLay extends Overlay {
		private GeoPoint gp1;
		private GeoPoint gp2;
		private int mRadius = 6;
		private int mode = 0;
		private int defaultColor;
		private String text = "";
		private Bitmap img = null;

		public MapRouteOverLay(GeoPoint gp1, GeoPoint gp2, int mode) // GeoPoint
																		// is a
																		// int.
		// (6E)
		{

			this.gp1 = gp1;
			this.gp2 = gp2;
			this.mode = mode;
			defaultColor = 999; // no defaultColor

		}

		public MapRouteOverLay(GeoPoint gp1, GeoPoint gp2, int mode,
				int defaultColor) {
			this.gp1 = gp1;
			this.gp2 = gp2;
			this.mode = mode;
			this.defaultColor = defaultColor;
		}

		public void setText(String t) {
			this.text = t;
		}

		public void setBitmap(Bitmap bitmap) {
			this.img = bitmap;
		}

		public int getMode() {
			return mode;
		}

		@Override
		public boolean draw(Canvas canvas, MapView mapView, boolean shadow,
				long when) {

			Projection projection = mapView.getProjection();
			if (shadow == false) {

				Paint paint = new Paint();
				paint.setAntiAlias(true);

				Point point = new Point();
				projection.toPixels(gp1, point);
				// mode=1¡Gstart
				if (mode == 1) {
					if (defaultColor == 999)
						paint.setColor(Color.BLUE);
					else
						paint.setColor(defaultColor);

					RectF oval = new RectF(point.x - mRadius,
							point.y - mRadius, point.x + mRadius, point.y
									+ mRadius);
					// start point
					canvas.drawOval(oval, paint);
				}
				// mode=2¡Gpath
				else if (mode == 2) {
					if (defaultColor == 999)
						paint.setColor(Color.RED);
					else
						paint.setColor(defaultColor);

					Point point2 = new Point();
					projection.toPixels(gp2, point2);
					paint.setStrokeWidth(5);
					paint.setAlpha(120);
					canvas.drawLine(point.x, point.y, point2.x, point2.y, paint);
				}
				/* mode=3¡Gend */
				else if (mode == 3) {
					/* the last path */

					if (defaultColor == 999)
						paint.setColor(Color.GREEN);
					else
						paint.setColor(defaultColor);

					Point point2 = new Point();
					projection.toPixels(gp2, point2);
					paint.setStrokeWidth(5);
					paint.setAlpha(120);
					canvas.drawLine(point.x, point.y, point2.x, point2.y, paint);

					RectF oval = new RectF(point2.x - mRadius, point2.y
							- mRadius, point2.x + mRadius, point2.y + mRadius);
					/* end point */
					paint.setAlpha(255);
					canvas.drawOval(oval, paint);
				}
				/* mode=4¡Gcar */
				else if (mode == 4) {

					if (defaultColor == 999)
						paint.setColor(Color.GREEN);
					else
						paint.setColor(defaultColor);

					Point point2 = new Point();
					projection.toPixels(gp2, point2);
					paint.setTextSize(20);
					paint.setAntiAlias(true);
					canvas.drawBitmap(img, point2.x, point2.y, paint);
					canvas.drawText(this.text, point2.x, point2.y, paint);
					// Log.d(TAG, "Draw the text="+this.text+
					// " at point="+point2.x
					// + "," + point2.y);
				}

				else if (mode == 5) {

					if (defaultColor == 999)
						paint.setColor(Color.GREEN);
					else
						paint.setColor(defaultColor);

					Point point2 = new Point();
					projection.toPixels(gp2, point2);
					paint.setTextSize(20);
					paint.setAntiAlias(true);
					canvas.drawBitmap(img, point2.x, point2.y, paint);

					// Log.d(TAG, "Draw the text="+this.text+
					// " at point="+point2.x
					// + "," + point2.y);
				}

			}
			return super.draw(canvas, mapView, shadow, when);
		}

	}

}