package com.jotracker;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.jotracker.data.DBAdapter;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.download.URLConnectionImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;

public class ShowMessages extends Activity {

	private int id = 0;
	private String msg = null, alert = null, phone = null, url_photo = null;
	String imageUrl;
	private ProgressDialog spinner;
	public SharedPreferences sharedPrefs;
	private String sound, photo_link, vehicle, temperature, speed, sensor,
			power, help, oil, engine, door, check, arm;
	private DBAdapter db;
	static final String PREFS_SOUND = "SOUND";
	static final String PREFS_PHOTO_LINK = "PHOTO_LINK";
	private String alerts[] = { "ACC,alarm", "Acc,alarm", "acc,alarm",
			"Door,alarm", "door,alarm", "Sensor,alarm", "sensor,alarm",
			"Power,alarm", "power,alarm", "Help,me", "help,me", "nofortify",
			"stockade", "Move", "move", "Temperature:", "Oil:", "POWER:" };
	private String speed_alert[] = { "Speed,alarm", "speed,alarm", "speed!" };
	private String photo[] = { "http://www.gpstrackerxy.com/",
			"http://www.jotracker.com/", "http://WWW.GPSTRACKERXY.com/",
			"http://www.GPSTrackerXY.com/" };
	private String arms[] = { "Tracker is deactivated!", "Tracker is activated!"};
	boolean activated = false;
	private Bitmap bitmap;
	int bmpWidth, bmpHeight;
	private ImageView myImageView;
	private float curScale = 1F;
	private float curRotate = 0F;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.alerts);
		db = new DBAdapter(this);

	}

	// This is used to determine if the Splash page should be displayed before
	// the
	// Activity is launched.
	@Override
	protected void onResume() {
		super.onResume();
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			id = extras.getInt("id");
			alert = extras.getString("alert");
			phone = extras.getString("phone");
			photo_link = extras.getString("photo");
			// Log.e("ALERT: " + getClass(), alert);
			msg = extras.getString("msg");
			// Log.e("ALERT: " + getClass(), msg);
			db.open();
			String data[] = db.selectOneTrackerByPhone(phone).split(",");
			db.close();
			switch (id) {
			case 1:
				sendSMS(msg.toString());
				break;
			case 2:
				if (!(photo_link == null) && !photo_link.contains("EMPTY")) {
					if (checkConn(this)) {
						showPhoto(photo_link);
					} else {
						Toast.makeText(this,
								this.getString(R.string.no_network),
								Toast.LENGTH_LONG).show();
					}
				} else {
					if (checkSMSAlarm(alert)) {
						if (alert != null) {
							Log.e("ALERT: " + getClass(),
									"if showAlert(alert.replaceAll(\",\", \" \"), data[2],data[4]);");
							showAlert(alert.replaceAll(",", " "), data[2],
									data[4]);
						}
					} else {
						Log.e("ALERT: " + getClass(),
								"Else showSMS(msg.toString(), data[2], data[4])");
						showSMS(msg.toString(), data[2], data[4]);
					}
				}
				break;
			}
		}
	}

	// storing a timestamp when the user quits the app
	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		setMsg("");
	}

	public String getSoundPref() {
		try {
			SharedPreferences settings = getSharedPreferences(PREFS_SOUND, 0);
			sound = settings.getString("sound", "alarm");
			// Log.e("PREFS_SOUND "+getClass(), "getSoundPref()  :: " + sound);
		} catch (Exception ex) {
			// Log.e("PREFS_SOUND "+getClass(), "getSoundPref()  :: " +
			// ex.getMessage());
		}
		return sound;
	}

	public void notifyAlert() {
		sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		sound = sharedPrefs.getString("sound", "alarm");
		Uri alert = Uri.parse("android.resource://" + getPackageName()
				+ "/raw/" + getSoundPref());
		Ringtone ring = RingtoneManager.getRingtone(this, alert);
		if (ring != null) {
			ring.play();
			// Log.e("ALERT "+getClass(),
			// "android.resource://"+getPackageName()+"/raw/"+ getSoundPref());
		}
	}

	public void notifySMS() {
		Uri notification = RingtoneManager
				.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		Ringtone ring = RingtoneManager.getRingtone(this, notification);
		if (ring != null) {
			ring.play();
		}
	}

	public void notifyArm() {
		Uri alert = Uri.parse("android.resource://" + getPackageName()
				+ "/raw/arm");
		Ringtone ring = RingtoneManager.getRingtone(this, alert);
		if (ring != null) {
			ring.play();
		}
	}

	public void showSMS(String message, String car, String phone) {
		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.check,
				(ViewGroup) this.findViewById(R.id.layout_alerts));
		EditText tvVehicle = (EditText) layout.findViewById(R.id.etVehicle);
		tvVehicle.setEnabled(false);
		tvVehicle.setText(car);

		if (message.contains("POWER:") || message.contains("PWR")) {
			setMsg(msg);
			setCheck(getMsg());
			String _ck[] = getCheck().split("#");
			Log.e("ALERT "+getClass(), String.valueOf(_ck.length));

			EditText tvAlert = (EditText) layout.findViewById(R.id.etAlerts);
			tvAlert.setEnabled(false);
			tvAlert.setCompoundDrawablesWithIntrinsicBounds(getResources()
					.getDrawable(R.drawable.ic_power), null, null, null);
			tvAlert.setText(_ck[0]);

			EditText tvBattery = (EditText) layout.findViewById(R.id.etBattery);
			tvBattery.setEnabled(false);
			tvBattery.setCompoundDrawablesWithIntrinsicBounds(getResources()
					.getDrawable(R.drawable.ic_battery), null, null, null);
			tvBattery.setText(_ck[1]);

			EditText tvDoor = (EditText) layout.findViewById(R.id.etDoor);
			tvDoor.setEnabled(false);
			tvDoor.setCompoundDrawablesWithIntrinsicBounds(getResources()
					.getDrawable(R.drawable.ic_door), null, null, null);
			tvDoor.setText(_ck[2]);

			EditText tvEngine = (EditText) layout.findViewById(R.id.etEngine);
			tvEngine.setEnabled(false);
			tvEngine.setCompoundDrawablesWithIntrinsicBounds(getResources()
					.getDrawable(R.drawable.ic_engine), null, null, null);
			tvEngine.setText(_ck[3]);

			EditText tvGPS = (EditText) layout.findViewById(R.id.etGPS);
			tvGPS.setEnabled(false);
			tvGPS.setCompoundDrawablesWithIntrinsicBounds(getResources()
					.getDrawable(R.drawable.ic_gps), null, null, null);
			tvGPS.setText(_ck[4]);

			EditText tvGPRS = (EditText) layout.findViewById(R.id.etGPRS);
			tvGPRS.setEnabled(false);
			tvGPRS.setCompoundDrawablesWithIntrinsicBounds(getResources()
					.getDrawable(R.drawable.ic_gprs), null, null, null);
			tvGPRS.setText(_ck[5]);

			EditText tvGSM = (EditText) layout.findViewById(R.id.etGSM);
			tvGSM.setEnabled(false);
			tvGSM.setCompoundDrawablesWithIntrinsicBounds(getResources()
					.getDrawable(R.drawable.ic_gsm), null, null, null);
			tvGSM.setText(_ck[6]);

			EditText tvOil = (EditText) layout.findViewById(R.id.etOil);
			tvOil.setEnabled(false);
			tvOil.setCompoundDrawablesWithIntrinsicBounds(getResources()
					.getDrawable(R.drawable.ic_oil), null, null, null);
			if(_ck.length > 7)
				tvOil.setText(_ck[7]);
			else
				tvOil.setText("0.0%");
			EditText tvTemp = (EditText) layout
					.findViewById(R.id.etTemperature);
			tvTemp.setEnabled(false);
			tvTemp.setCompoundDrawablesWithIntrinsicBounds(getResources()
					.getDrawable(R.drawable.ic_temperature), null, null, null);
			if(_ck.length > 8)
				tvTemp.setText(_ck[8]);
			else
				tvTemp.setText("+0.0");
			showPopUp(layout);
			notifySMS();
		} else {
			msg = message;
			if (checkSMSAlarmSpeed(alert)) {
				notifyAlert();
				/*
				 * layout = inflater.inflate(R.layout.alerts_speed,
						(ViewGroup) this.findViewById(R.id.layout_alerts));
				myImageView = (ImageView)findViewById(R.id.imageview);
		        
		        bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.pointer);
		        bmpWidth = bitmap.getWidth();
		        bmpHeight = bitmap.getHeight();
				
				EditText tvVehicle1 = (EditText) layout.findViewById(R.id.etVehicle);
				tvVehicle1.setEnabled(false);
				tvVehicle1.setText(car);
				setSpeed(80);
				
				Matrix matrix = new Matrix();
				matrix.postScale(curScale, curScale);
				matrix.postRotate(curRotate);

				Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bmpWidth,
						bmpHeight, matrix, true);
				myImageView.setImageBitmap(resizedBitmap);
				showPopUp(layout);
				notifyArm();
				*/
				new AlertDialog.Builder(this)
						.setTitle(R.string.alarm_title)
						.setMessage(
								getString(R.string.alarm_speed)
										+ "\n"
										+ getString(R.string.alarm_speed_message)
										+ " " + getSpeed(message) + "\n"
										+ getString(R.string.vehicle) + " "
										+ car + "\n"
										+ getString(R.string.phone) + " "
										+ phone)
						.setPositiveButton(R.string.ok, new OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								finish();
							}
						}).show();
				 
			} else {
				if (checkArm(msg)) {
					layout = inflater.inflate(R.layout.alerts,
							(ViewGroup) this.findViewById(R.id.layout_alerts));
					EditText tvVehicle1 = (EditText) layout.findViewById(R.id.etVehicle);
					tvVehicle1.setEnabled(false);
					tvVehicle1.setText(car);
					EditText tvAlert = (EditText) layout.findViewById(R.id.etAlerts);
					tvAlert.setEnabled(false);
					tvAlert.setCompoundDrawablesWithIntrinsicBounds(getResources()
							.getDrawable(R.drawable.ic_door), null, null, null);
					tvAlert.setText(getArm());
					showPopUp(layout);
					notifyArm();
					//Log.e("ALERT: " + getClass(), "notifyArm()");
				} else {
					notifySMS();
					//Log.e("ALERT: " + getClass(), "notifySMS();");
					new AlertDialog.Builder(this).setTitle(R.string.message)
					.setMessage(msg)
					.setPositiveButton(R.string.ok, new OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							finish();
						}
					}).show();
				}
			}
		}
	}

	public void showAlert(String message, String car, String phone) {
		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.alerts,
				(ViewGroup) this.findViewById(R.id.layout_alerts));
		EditText tvVehicle = (EditText) layout.findViewById(R.id.etVehicle);
		tvVehicle.setEnabled(false);
		tvVehicle.setText(car);

		EditText tvAlert = (EditText) layout.findViewById(R.id.etAlerts);
		tvAlert.setEnabled(false);
		if (message.contains("Temperature:")) {
			setMsg(msg);
			// Log.e("ALERT: " + getClass(), getMsg());
			setTemperature(getMsg());
			tvAlert.setCompoundDrawablesWithIntrinsicBounds(getResources()
					.getDrawable(R.drawable.ic_temperature), null, null, null);
			tvAlert.setText(getTemperature());
			showPopUp(layout);
			notifyAlert();
		} else if (message.contains("Oil:")) {
			setMsg(msg);
			// Log.e("ALERT: " + getClass(), getMsg());
			setOil(getMsg());
			tvAlert.setCompoundDrawablesWithIntrinsicBounds(getResources()
					.getDrawable(R.drawable.ic_oil), null, null, null);
			tvAlert.setText(getOil());
			showPopUp(layout);
			notifyAlert();
			notifyAlert();
		} else if (message.contains("Sensor") || message.contains("sensor")) {
			setMsg(msg);
			// Log.e("ALERT: " + getClass(), getMsg());
			setSensor(getMsg());
			tvAlert.setCompoundDrawablesWithIntrinsicBounds(getResources()
					.getDrawable(R.drawable.ic_sensor), null, null, null);
			tvAlert.setText(getSensor());
			showPopUp(layout);
			notifyAlert();
		} else if (message.contains("Power") || message.contains("power")) {
			setMsg(msg);
			// Log.e("ALERT: " + getClass(), getMsg());
			setPower(getMsg());
			tvAlert.setCompoundDrawablesWithIntrinsicBounds(getResources()
					.getDrawable(R.drawable.ic_power), null, null, null);
			tvAlert.setText(getPower());
			showPopUp(layout);
			notifyAlert();
		} else if (message.contains("ACC") || message.contains("Acc")) {
			setMsg(msg);
			// Log.e("ALERT: " + getClass(), getMsg());
			setEngine(getMsg());
			tvAlert.setCompoundDrawablesWithIntrinsicBounds(getResources()
					.getDrawable(R.drawable.ic_engine), null, null, null);
			tvAlert.setText(getEngine());
			showPopUp(layout);
			notifyAlert();
		} else if (message.contains("Door") || message.contains("door")) {
			setMsg(msg);
			// Log.e("ALERT: " + getClass(), getMsg());
			setDoor(getMsg());
			tvAlert.setCompoundDrawablesWithIntrinsicBounds(getResources()
					.getDrawable(R.drawable.ic_door), null, null, null);
			tvAlert.setText(getDoor());
			showPopUp(layout);
			notifyAlert();
		} else if (message.contains("Help") || message.contains("help")) {
			setMsg(msg);
			// Log.e("ALERT: " + getClass(), getMsg());
			setHelp(getMsg());
			tvAlert.setCompoundDrawablesWithIntrinsicBounds(getResources()
					.getDrawable(R.drawable.ic_sos), null, null, null);
			tvAlert.setText(getHelp());
			showPopUp(layout);
			notifyAlert();
		} else {
			msg = message;
			new AlertDialog.Builder(this)
					.setTitle(R.string.message)
					.setMessage(
							msg + "\n" + getString(R.string.vehicle) + " "
									+ car + "\n" + getString(R.string.phone)
									+ " " + phone)
					.setPositiveButton(R.string.ok, new OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							finish();
						}
					}).show();
			notifyAlert();
		}
	}

	public void showPopUp(View layout) {
		new AlertDialog.Builder(this).setTitle(R.string.message)
				.setView(layout)
				.setPositiveButton(R.string.ok, new OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						finish();
					}
				}).show();
	}

	public void showPhoto(String url) {
		// Log.e(getPackageName()+getClass(), "showPhoto()" +url);
		notifySMS();
		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.photo,
				(ViewGroup) this.findViewById(R.id.layout_photo));
		ImageView imageView = (ImageView) layout.findViewById(R.id.photo);
		// imageUrl =
		// "http://www.gpstrackerxy.com/AlarmImages/20121224163204278.jpg"; //
		// or
		File cacheDir = StorageUtils.getOwnCacheDirectory(
				getApplicationContext(), "UniversalImageLoader/Cache");
		// Display options
		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.showStubImage(R.drawable.stub_image)
				.showImageForEmptyUri(R.drawable.image_for_empty_url)
				.cacheInMemory().cacheOnDisc()
				.imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
				.bitmapConfig(Bitmap.Config.RGB_565).delayBeforeLoading(1000)
				.displayer(new RoundedBitmapDisplayer(5)).build();
		// Cache options
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				getApplicationContext())
				.memoryCacheExtraOptions(480, 800)
				// max width, max height
				.discCacheExtraOptions(480, 800, CompressFormat.JPEG, 75)
				// Can slow ImageLoader, use it carefully (Better don't use it)
				.threadPoolSize(3)
				.threadPriority(Thread.NORM_PRIORITY - 1)
				.denyCacheImageMultipleSizesInMemory()
				.offOutOfMemoryHandling()
				.memoryCache(new UsingFreqLimitedMemoryCache(2 * 1024 * 1024))
				// You can pass your own memory cache implementation
				.discCache(new UnlimitedDiscCache(cacheDir))
				// You can pass your own disc cache implementation
				.discCacheFileNameGenerator(new HashCodeFileNameGenerator())
				.imageDownloader(
						new URLConnectionImageDownloader(5 * 1000, 20 * 1000))
				// connectTimeout (5 s), readTimeout (20 s)
				.tasksProcessingOrder(QueueProcessingType.FIFO).enableLogging()
				.build();
		ImageLoader imageLoader = ImageLoader.getInstance();
		// Initialize ImageLoader with configuration. Do it once.
		// imageLoader.init(ImageLoaderConfiguration.createDefault(this));
		imageLoader.init(config);
		// Load and display image
		// prepare for a progress bar dialog
		spinner = new ProgressDialog(this);
		spinner.setCancelable(true);
		spinner.setMessage(getString(R.string.download_file) + " ...");
		spinner.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		imageLoader.displayImage(url, imageView, options,
				new ImageLoadingListener() {
					public void onLoadingStarted() {
						spinner.show();
					}

					public void onLoadingFailed(FailReason failReason) {
						spinner.hide();
					}

					public void onLoadingComplete(Bitmap loadedImage) {
						spinner.hide();
					}

					public void onLoadingCancelled() {
						// Do nothing
					}
				});

		new AlertDialog.Builder(this).setTitle(R.string.message)
				.setView(layout).setMessage("")
				.setPositiveButton(R.string.ok, new OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						finish();
					}
				}).show();
	}

	public void sendSMS(final String message) {
		if (message == null) {
			setMsg("null");
		} else {
			msg = message;
			new AlertDialog.Builder(this).setTitle(R.string.message)
					.setMessage(R.string.tracker_not_compatible)
					.setPositiveButton(R.string.ok, new OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							try {
								Intent emailIntent = new Intent(
										android.content.Intent.ACTION_SEND);
								emailIntent.setType("plain/text");
								emailIntent
										.putExtra(
												android.content.Intent.EXTRA_EMAIL,
												new String[] { "jo-tracker@hotmail.com" });
								emailIntent.putExtra(
										android.content.Intent.EXTRA_SUBJECT,
										"GPS Tracker by SMS (Not compatible)");
								emailIntent.putExtra(
										android.content.Intent.EXTRA_TEXT,
										"\n\n" + msg.toString());
								startActivity(Intent.createChooser(emailIntent,
										String.valueOf(R.string.email_send)));
								finish();
							} catch (android.content.ActivityNotFoundException e) {
								Toast.makeText(ShowMessages.this, e.toString(),
										Toast.LENGTH_SHORT).show();
							}
						}
					}).show();
		}

	}

	public String getSpeed(String input) {
		input = input.replace(" ", ",").replaceAll(
				System.getProperty("line.separator"), ",");
		if (input.contains("speed:")) {
			String _sms[] = input.split("speed:");
			if (_sms[1].startsWith(",")) {
				String _speed[] = _sms[1].split(",");
				speed = _speed[1].toString();
			} else {
				String _speed[] = _sms[1].split(",");
				speed = _speed[0].toString();
			}
		} else
			speed = "0";
		return speed;
	}

	public boolean checkSMSAlarm(String sms) {
		for (int i = 0; i < alerts.length; i++) {
			if (sms.contains(alerts[i])) {
				return true;
			}
		}
		return false;
	}

	public boolean checkSMSAlarmSpeed(String sms) {
		for (int i = 0; i < speed_alert.length; i++) {
			if (sms.contains(speed_alert[i])) {
				return true;
			}
		}
		return false;
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

	public String[] getAlerts() {
		return alerts;
	}

	public void setAlerts(String alerts[]) {
		this.alerts = alerts;
	}

	public String[] getSpeed_alert() {
		return speed_alert;
	}

	public void setSpeed_alert(String speed_alert[]) {
		this.speed_alert = speed_alert;
	}

	public String getVehicle() {
		return vehicle;
	}

	public void setVehicle(String vehicle) {
		this.vehicle = vehicle;
	}

	public String getTemperature() {
		return temperature;
	}

	 public void setSpeed(int speed){
		 if(speed <= 10){
			 curRotate = speed + 0; 
		 }
		 else if(speed <= 20){
			 curRotate = speed - 4;  
		 }
		 else if(speed <= 30){
			 curRotate = speed - 6;  
		 }
		 else if(speed <= 40){
			 curRotate = speed + 4;  
		 }
		 else if(speed <= 50){
			 curRotate = speed + 8;  
		 }
		 else if(speed <= 60){
			 curRotate = speed + 10;  
		 }
		 else if(speed <= 70){
			 curRotate = speed + 12;  
		 }
		 else if(speed <= 80){
			 curRotate = speed + 14;  
		 }
		 else if(speed <= 90){
			 curRotate = speed + 18;  
		 }
		 else if(speed <= 100){
			 curRotate = speed + 18;  
		 }
		 else if(speed <= 110){
			 curRotate = speed + 18;  
		 }
		 else if(speed <= 120){
			 curRotate = speed + 20;  
		 }
		 else if(speed <= 130){
			 curRotate = speed + 24;  
		 }
		 else if(speed <= 140){
			 curRotate = speed + 28;  
		 }
		 else if(speed <= 150){
			 curRotate = speed + 30;  
		 }
		 else if(speed <= 160){
			 curRotate = speed + 32;  
		 }
		 else if(speed <= 170){
			 curRotate = speed + 36;  
		 }
		 else if(speed <= 180){
			 curRotate = speed + 36;  
		 }
		 else if(speed <= 190){
			 curRotate = speed + 38;  
		 }
		 else if(speed <= 200){
			 curRotate = speed + 40;  
		 }
		 else if(speed <= 210){
			 curRotate = speed + 46;  
		 }
		 else if(speed <= 220){
			 curRotate = speed + 48;  
		 }
		 
		 drawMatrix();
	 }
	
	private void drawMatrix() {

		

	}
	
	public void setTemperature(String temperature) {
		String _sms[] = temperature.split(",");
		for (int i = 0; i < _sms.length; i++) {
			if (_sms[i].contains("Temperature:")) {
				temperature = _sms[i].replace(":", ": ");
			}
		}
		this.temperature = temperature;
	}

	public String getSensor() {
		return sensor;
	}

	public void setSensor(String sensor) {
		String _sms[] = sensor.split(",");
		for (int i = 0; i < _sms.length; i++) {
			if (_sms[i].contains("sensor") || _sms[i].contains("Sensor")) {
				sensor = _sms[i] + " " + _sms[i + 1];
			}
		}
		this.sensor = sensor;
	}

	public String getPower() {
		return power;
	}

	public void setPower(String power) {
		String _sms[] = power.split(",");
		for (int i = 0; i < _sms.length; i++) {
			if (_sms[i].contains("Power")) {
				power = _sms[i] + " " + _sms[i + 1];
			}
		}
		this.power = power;
	}

	public String getOil() {
		return oil;
	}

	public void setOil(String oil) {
		String _sms[] = oil.split(",");
		for (int i = 0; i < _sms.length; i++) {
			if (_sms[i].contains("Oil:")) {
				oil = _sms[i].replace(":", ": ");
			}
		}
		this.oil = oil;
	}

	public String getEngine() {
		return engine;
	}

	public void setEngine(String engine) {
		String _sms[] = engine.split(",");
		for (int i = 0; i < _sms.length; i++) {
			if (_sms[i].contains("ACC") || _sms[i].contains("acc")) {
				engine = _sms[i] + " " + _sms[i + 1];
			}
		}
		this.engine = engine;
	}

	public String getDoor() {
		return door;
	}

	public void setDoor(String door) {
		String _sms[] = door.split(",");
		for (int i = 0; i < _sms.length; i++) {
			if (_sms[i].contains("Door") || _sms[i].contains("door")) {
				door = _sms[i] + " " + _sms[i + 1];
			}
		}
		this.door = door;
	}

	public String getHelp() {
		return help;
	}

	public void setHelp(String help) {
		String _sms[] = help.split(",");
		for (int i = 0; i < _sms.length; i++) {
			if (_sms[i].contains("Help") || _sms[i].contains("help")) {
				help = _sms[i] + " " + _sms[i + 1];
			}
		}
		this.help = help;
	}

	public String getCheck() {
		return check;
	}

	public void setCheck(String check) {
		String _sms[] = check.split(",");
		String res = "";
		for (int i = 0; i < _sms.length; i++) {
			if (_sms[i].contains("POWER:") || _sms[i].contains("Battery:")
					|| _sms[i].contains("Door:") || _sms[i].contains("ACC:")
					|| _sms[i].contains("GPS:") || _sms[i].contains("GPRS:")
					|| _sms[i].contains("GSM") || _sms[i].contains("Oil:")
					|| _sms[i].contains("Temperature:")) {
				if (_sms[i].contains("GSM")) {
					res += _sms[i] + " " + _sms[i + 1].replace(":", ": ") + "#";
				} else {
					res += _sms[i].replace(":", ": ") + "#";
				}
				//Log.e("ALERT: " + getClass(), _sms[i]);
			}
		}
		this.check = res;
		//Log.e("ALERT: " + getClass(), getCheck());
	}

	private String getMsg() {
		return msg;
	}

	private void setMsg(String msg) {
		msg = msg.replaceAll(" ", ",").replaceAll(
				System.getProperty("line.separator"), ",");
		msg = msg.replaceAll(" ", ",");
		while (msg.contains(",,")) {
			msg = msg.replaceAll(",,", ",");
		}
		this.msg = msg;
		//Log.e("ALERT: " + getClass(), msg);
	}

	public String getArm() {
		return arm;
	}

	public void setArm(String arm) {
		this.arm = arm;
	}
	
	public boolean checkArm(String sms) {
		for (int i = 0; i < arms.length; i++) {
			if (sms.contains(arms[i])) {
				setArm(arms[i]);
				//Log.e("ALERT: " + getClass(), arms[i] + "  getArm()");
				return true;
			}
		}
		return false;
	}

}
