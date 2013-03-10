package com.jotracker;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.jotracker.data.DBAdapter;

public class EditTrackers extends Activity {
	// private static final String LOG = "EditTrackers";

	private EditText txtImei, txtName, txtLicenseNumber, txtLicenseExpiration,
			txtVehicle, txtDescription, txtPhone, txtPassword;
	private Button btnAdd, btnCancel, btnClear;
	private DBAdapter db;
	private String rowid, imei, name, license_number, license_expiration,
			vehicle, description, phone = null, password = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_tracker);
		db = new DBAdapter(EditTrackers.this);
		Bundle extras = getIntent().getExtras();
		String id = null;

		txtImei = (EditText) findViewById(R.id.eTImei);
		txtName = (EditText) findViewById(R.id.eTName);
		txtLicenseNumber = (EditText) findViewById(R.id.eTLicenseNumber);
		txtLicenseExpiration = (EditText) findViewById(R.id.eTLicenseExpiration);
		txtVehicle = (EditText) findViewById(R.id.eTVehicle);
		txtDescription = (EditText) findViewById(R.id.eTDescrip);
		txtPhone = (EditText) findViewById(R.id.eTPhone);
		txtPhone.setEnabled(true);
		txtPassword = (EditText) findViewById(R.id.eTPass);
		txtPassword.setEnabled(false);
		txtImei.requestFocus();

		if (extras != null) {
			try {
				id = extras.getString("id");
				String str = null;
				db.open();
				str = db.selectOneTracker(id);
				Log.d("LOG", str);
				db.close();
				String s[] = str.split(",");
				rowid = s[0];
				imei = s[1];
				name = s[2];
				license_number = s[3];
				license_expiration = s[4];
				vehicle = s[5];
				description = s[6];
				phone = s[7];
				password = s[8];
				txtImei.setText(checkStrNotEmpty(imei));
				txtName.setText(checkStrNotEmpty(name));
				txtLicenseNumber.setText(checkStrNotEmpty(license_number));
				txtLicenseExpiration
						.setText(checkStrNotEmpty(license_expiration));
				txtVehicle.setText(checkStrNotEmpty(vehicle));
				txtDescription.setText(checkStrNotEmpty(description));
				txtPhone.setText(checkStrNotEmpty(phone));
				txtPassword.setText(checkStrNotEmpty(password));
				txtVehicle.requestFocus();

				/*
				 * Toast.makeText(EditTrackers.this,"Result: "+str,
				 * Toast.LENGTH_LONG).show();
				 */
			} catch (Exception e) {
				Toast.makeText(EditTrackers.this,
						R.string.execption + e.getMessage(), Toast.LENGTH_LONG)
						.show();
			}
		}

		btnAdd = (Button) findViewById(R.id.btnAddTracker);
		btnAdd.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				String imei = txtImei.getText().toString();
				String vehicle = txtVehicle.getText().toString();
				String description = txtDescription.getText().toString();
				String phone = txtPhone.getText().toString();
				String password = txtPassword.getText().toString();
				try {
					db.open();
					String fail = null;
					fail = getString(R.string.fill_all_fields);
					if (imei.contentEquals("")) {
						Toast.makeText(EditTrackers.this, fail + " ",
								Toast.LENGTH_LONG).show();
						txtImei.requestFocus();
					} else if (vehicle.contentEquals("")) {
						Toast.makeText(EditTrackers.this, fail + " ",
								Toast.LENGTH_LONG).show();
						txtVehicle.requestFocus();
					} else if (phone.contentEquals("")) {
						Toast.makeText(EditTrackers.this, fail + " ",
								Toast.LENGTH_LONG).show();
						txtPhone.requestFocus();
					} else if (password.contentEquals("")) {
						Toast.makeText(EditTrackers.this, fail + " ",
								Toast.LENGTH_LONG).show();
						txtPassword.requestFocus();
					} else {
						db.updateTrackers(rowid, imei, vehicle, description,
								phone, password);
						String success = null;
						success = getString(R.string.tracker_update_success);
						Toast.makeText(EditTrackers.this, success,
								Toast.LENGTH_LONG).show();
						Intent returnIntent = new Intent();
						returnIntent.putExtra("EditedTracker", rowid);
						setResult(RESULT_OK, returnIntent);
						finish();
						db.close();
					}
				}

				catch (Exception e) {
					Toast.makeText(EditTrackers.this, e.getMessage(),
							Toast.LENGTH_LONG).show();
				}
			}
		});

		btnCancel = (Button) findViewById(R.id.btnGoBack);
		btnCancel.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});

		btnClear = (Button) findViewById(R.id.btnClear);
		btnClear.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				txtVehicle.setText("");
				txtDescription.setText("");
				txtVehicle.requestFocus();
			}
		});
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
		super.onResume();
		// Log.d(LOG, " onResume()");
	}

	// storing a timestamp when the user quits the app
	@Override
	protected void onPause() {
		super.onPause();
		// Log.d(LOG, " onPause()");
	}

	public String checkStrNotEmpty(String str) {
		if (str == "")
			return str = "_";
		else if (str == null)
			return str = "null";
		else
			return str;
	}

	public boolean checkStrNotNull(String str) {
		if (str == "")
			return false;
		else if (str == null)
			return false;
		else
			return true;
	}
}
