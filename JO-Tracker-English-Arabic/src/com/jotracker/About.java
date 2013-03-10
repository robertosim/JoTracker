package com.jotracker;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.jotracker.service.ServiceSMS;

public class About extends Activity {
	// private static final String LOG = "About";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);

		final TextView tvAbout = (TextView) findViewById(R.id.tvAbout);
		String txtAbout = "<h1>JOTRACKER</h1><br>"
				+ "<p>JO Tracker is the pioneering provider of innovative, powerful and efficient satellite based GPS tracking solutions. Using contemporary technology, JO Tracker provides instant information on vehicle location, history of usage, reports, accurate engine diagnostics, routing and messages which are amalgamated to provide the most effective fleet management solutions. It is a comprehensive and adaptable fleet tracking system which includes the ability to provide a mélange of customized solutions for reports, alerts and diverse features which can be customized as per client requirements and ensure the creation of unmatched return on investment.Our contemporary Vehicle Tracking System provides efficient hardware and supplementary services including navigation and messaging services for fleet owners. The diversity of devices in hardware options permits every client to choose such combinations which are ideal for their operations.</p>"
				+ "<p><a href=\"http://www.jotracker.com\">www.jotracker.com</a></p>"
		+ "<p><a href=\"mailto:someone@example.com?Subject=JOTRACKER\">jo-tracker@hotmail.com</a></p>"
		+ "<p>00962796718110";
		tvAbout.setText(Html.fromHtml(txtAbout));
		tvAbout.setMovementMethod(LinkMovementMethod.getInstance());
		/**/
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

	@Override
	protected void onResume() {
		super.onResume();
		// Log.d(LOG, " onResume()");
	}

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
		inflater.inflate(R.menu.options_menu_about, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.settins:
			Intent settings = new Intent();
			settings.setClass(About.this, Settings.class);
			startActivity(settings);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
