package com.jotracker;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.download.URLConnectionImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;

public class Location extends Activity implements Runnable {

	private ProgressDialog progresDialog;
	private Geocoder geocoder;
	private String strAddress, textAddress, strLat, strLon, strDate, strHour, strId, strUrl = null;
	private boolean photo = false;
	private Context context;
	private ImageLoader imageLoader;
	private DisplayImageOptions options;
	private ImageLoaderConfiguration config;
	private ProgressDialog spinner;

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.location);
		context = this;
		Bundle bundle = this.getIntent().getExtras();
		setId(bundle.getString("id"));
		setLat(bundle.getString("lat"));
		setLon(bundle.getString("lon"));
		setDate(bundle.getString("date"));
		setHour(bundle.getString("hour"));
		setUrl(bundle.getString("url_photo"));
		setPhoto(bundle.getBoolean("photo"));
		progresDialog = ProgressDialog.show(context,
				context.getString(R.string.address),
				context.getString(R.string.loading), true, false);
		progresDialog.setCancelable(true);
		Thread thread = new Thread(this);
		thread.start();


	}

	public void run() {
		handler.sendEmptyMessage(0);
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			progresDialog.dismiss();
			if(isPhoto()){
				showAddressWithPhoto(getUrl());
			}
			else{
				showAddress();
			}
		}
	};

	public void showAddress() {
		textAddress = getAddress();
		AlertDialog.Builder alert = new AlertDialog.Builder(context);
		alert.setTitle(context.getString(R.string.address));
		alert.setMessage(textAddress);
		alert.setPositiveButton(R.string.ok,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						Location.this.finish();
					}
				});
		alert.show();
	}
	
	public void showAddressWithPhoto(String url){
		textAddress = getAddress();
		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.photo_location,
				(ViewGroup) this.findViewById(R.id.layout_photo));
		ImageView imageView = (ImageView) layout.findViewById(R.id.photo);
		TextView address = (TextView) layout.findViewById(R.id.address);
		address.setText(textAddress);
		TextView date = (TextView) layout.findViewById(R.id.date);
		date.setText(getDate());
		TextView hour = (TextView) layout.findViewById(R.id.hour);
		hour.setText(getHour());
		// imageUrl =
		// "http://www.gpstrackerxy.com/AlarmImages/20121224163204278.jpg"; //
		// or
		File cacheDir = StorageUtils.getOwnCacheDirectory(getApplicationContext(), "UniversalImageLoader/Cache");
		// Display options
		options = new DisplayImageOptions.Builder()
				 .showStubImage(R.drawable.stub_image)
		         .showImageForEmptyUri(R.drawable.image_for_empty_url)
		         .cacheInMemory()
		         .cacheOnDisc()
		         .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
		         .bitmapConfig(Bitmap.Config.RGB_565)
		         .delayBeforeLoading(1000)
		         .displayer(new RoundedBitmapDisplayer(5))
		         .build();
		// Cache options
		config = new ImageLoaderConfiguration.Builder(getApplicationContext())
        .memoryCacheExtraOptions(480, 800) // max width, max height
        .discCacheExtraOptions(480, 800, CompressFormat.JPEG, 75) // Can slow ImageLoader, use it carefully (Better don't use it)
        .threadPoolSize(3)
        .threadPriority(Thread.NORM_PRIORITY - 1)
        .denyCacheImageMultipleSizesInMemory()
        .offOutOfMemoryHandling()
        .memoryCache(new UsingFreqLimitedMemoryCache(2 * 1024 * 1024)) // You can pass your own memory cache implementation
        .discCache(new UnlimitedDiscCache(cacheDir)) // You can pass your own disc cache implementation
        .discCacheFileNameGenerator(new HashCodeFileNameGenerator())
        .imageDownloader(new URLConnectionImageDownloader(5 * 1000, 20 * 1000)) // connectTimeout (5 s), readTimeout (20 s)
        .tasksProcessingOrder(QueueProcessingType.FIFO)
        .enableLogging()
        .build();
		imageLoader = ImageLoader.getInstance();
		// Initialize ImageLoader with configuration. Do it once.
		//imageLoader.init(ImageLoaderConfiguration.createDefault(this));
		imageLoader.init(config);
		// Load and display image
		imageLoader.displayImage(url, imageView, options);
		
		AlertDialog.Builder alert = new AlertDialog.Builder(context);
		alert.setTitle(context.getString(R.string.address));
		alert.setView(layout);
		alert.setPositiveButton(R.string.ok,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						Location.this.finish();
					}
				});
		alert.show();
	}

	public String getAddress() {

		try {
			geocoder = new Geocoder(this, Locale.getDefault());
			List<Address> addresses = geocoder.getFromLocation(
					Double.parseDouble(getLat()), Double.parseDouble(getLon()),
					1);
			if (addresses.size() > 0) {
				StringBuilder result = new StringBuilder();
				for (int i = 0; i < addresses.size(); i++) {
					Address address = addresses.get(i);
					int maxIndex = address.getMaxAddressLineIndex();

					for (int x = 0; x <= maxIndex; x++) {
						result.append(address.getAddressLine(x));
						result.append("\n");
					}/*
					 * postalCode = address.getPostalCode(); countryName =
					 * address.getCountryName(); locality =
					 * address.getLocality(); adminArea =
					 * address.getAdminArea(); getPremises =
					 * address.getAddressLine(0); getSubThoroughfare =
					 * address.getSubThoroughfare(); getThoroughfare =
					 * address.getThoroughfare(); result.append(" " + postalCode
					 * +" + "+ countryName +" + "+ locality +" + " + adminArea
					 * +" "+getThoroughfare+" + "+getPremises);
					 */
				}
				strAddress = result.toString();
				
			}
		} catch (IOException ex) {
			strAddress = ex.getMessage().toString();
		}
		return strAddress;
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

	// This is used to determine if the Splash page should be displayed before
	// the
	// Activity is launched.
	@Override
	protected void onResume() {
		super.onResume();
	}

	// storing a timestamp when the user quits the app
	@Override
	protected void onPause() {
		super.onPause();
	}

	public String getId() {
		return strId;
	}

	public void setId(String strId) {
		this.strId = strId;
	}

	public boolean isPhoto() {
		return photo;
	}

	public void setPhoto(boolean photo) {
		this.photo = photo;
	}

	public String getUrl() {
		return strUrl;
	}

	public void setUrl(String strUrl) {
		this.strUrl = strUrl;
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
}
