package com.jotracker;

import java.io.File;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

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

public class ImageCursorAdapter extends SimpleCursorAdapter {

	private Cursor cursor;
	private Context context;
	private ImageLoader imageLoader;
	private DisplayImageOptions options;
	private ImageLoaderConfiguration config;
	private ProgressDialog spinner;
	private String vehicle, latitude, longitude, speed, date, hour, power, acc,
			door;
	private TextView tvVehicle, tvLatitude, tvLongitude, tvSpeed, tvDate,
			tvHour, tvPower, tvAcc, tvDoor;

	public ImageCursorAdapter(Context context, int layout, Cursor c,
			String[] from, int[] to) {
		super(context, layout, c, from, to);
		this.cursor = c;
		this.context = context;
		// Display options
		options = new DisplayImageOptions.Builder()
				.showStubImage(R.drawable.stub_image)
				.showImageForEmptyUri(R.drawable.image_for_empty_url)
				.cacheInMemory().cacheOnDisc()
				.imageScaleType(ImageScaleType.EXACTLY)
				.bitmapConfig(Bitmap.Config.RGB_565).delayBeforeLoading(1000)
				.displayer(new RoundedBitmapDisplayer(5))
				.build();
		// Config cache options
		File cacheDir = StorageUtils.getOwnCacheDirectory(context,
				"UniversalImageLoader/Cache");
		config = new ImageLoaderConfiguration.Builder(context)
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

		imageLoader = ImageLoader.getInstance();
		// Initialize ImageLoader with configuration. Do it once.
		// imageLoader.init(ImageLoaderConfiguration.createDefault(context));
		imageLoader.init(config);
		spinner = new ProgressDialog(context);
		spinner.setMessage("Loading ...");
		spinner.setCancelable(true);
		spinner.setProgressStyle(ProgressDialog.STYLE_SPINNER);
	}

	public View getView(int pos, View inView, ViewGroup parent) {

		View view = inView;
		if (view == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.list_coords_photos, null);
		}
		this.cursor.moveToPosition(pos);
		setVehicle(this.cursor.getString(this.cursor.getColumnIndex("vehicle")));
		setLatitude(this.cursor.getString(this.cursor
				.getColumnIndex("latitude")));
		setLongitude(this.cursor.getString(this.cursor
				.getColumnIndex("longitude")));
		setDate(this.cursor.getString(this.cursor.getColumnIndex("date")));
		setHour(this.cursor.getString(this.cursor.getColumnIndex("hour")));
		setPower(this.cursor.getString(this.cursor.getColumnIndex("power")));
		setAcc(this.cursor.getString(this.cursor.getColumnIndex("acc")));
		setDoor(this.cursor.getString(this.cursor.getColumnIndex("door")));
		String url = this.cursor.getString(this.cursor.getColumnIndex("photo"));
		ImageView imageView = (ImageView) view.findViewById(R.id.photo);
		// Log.i("getView(): ", power+" "+ acc+" "+ door+" "+ url);
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
		if (url == null) {
			imageView.setImageResource(R.drawable.image_for_empty_url);
		}
		tvVehicle = (TextView) view.findViewById(R.id.vehicle);
		tvVehicle.setText(getVehicle());
		tvLatitude = (TextView) view.findViewById(R.id.latitude);
		tvLatitude.setText(getLatitude());
		tvLongitude = (TextView) view.findViewById(R.id.longitude);
		tvLongitude.setText(getLongitude());
		tvSpeed = (TextView) view.findViewById(R.id.speed);
		tvSpeed.setText(getSpeed());
		tvDate = (TextView) view.findViewById(R.id.date);
		tvDate.setText(getDate());
		tvHour = (TextView) view.findViewById(R.id.hour);
		tvHour.setText(getHour());
		tvPower = (TextView) view.findViewById(R.id.power);
		tvPower.setText(getPower());
		tvAcc = (TextView) view.findViewById(R.id.acc);
		tvAcc.setText(getAcc());
		tvDoor = (TextView) view.findViewById(R.id.door);
		tvDoor.setText(getDoor());
		return (view);
	}

	public String getVehicle() {
		return vehicle;
	}

	public void setVehicle(String vehicle) {
		this.vehicle = vehicle;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getSpeed() {
		return speed;
	}

	public void setSpeed(String speed) {
		this.speed = speed;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getHour() {
		return hour;
	}

	public void setHour(String hour) {
		this.hour = hour;
	}

	public String getPower() {
		return power;
	}

	public void setPower(String power) {
		this.power = power;
	}

	public String getAcc() {
		return acc;
	}

	public void setAcc(String acc) {
		this.acc = acc;
	}

	public String getDoor() {
		return door;
	}

	public void setDoor(String door) {
		this.door = door;
	}
}
