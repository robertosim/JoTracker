package com.jotracker;

import java.io.IOException;
import java.net.URL;
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

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.jotracker.data.DBAdapter;

public class AddNewTracker extends Activity {

	public EditText txtImei, txtName, txtLicenseNumber, txtLicenseExpiration,
			txtVehicle, txtDescription, txtPhone, txtPassword;
	public Button btnSearch, btnAdd, btnCancel, btnClear;
	private DBAdapter db;
	static final String PREFS_DATE_SERVER = "DATE_SERVER";
	private User user;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_new_tracker);

		db = new DBAdapter(AddNewTracker.this);
		user = new User();

		txtPhone = (EditText) findViewById(R.id.eTPhone);
		txtImei = (EditText) findViewById(R.id.eTImei);
		txtName = (EditText) findViewById(R.id.eTName);
		txtLicenseNumber = (EditText) findViewById(R.id.eTLicenseNumber);
		// txtLicenseExpiration = (EditText)
		// findViewById(R.id.eTLicenseExpiration);
		txtVehicle = (EditText) findViewById(R.id.eTVehicle);
		txtDescription = (EditText) findViewById(R.id.eTDescrip);
		txtPassword = (EditText) findViewById(R.id.eTPass);
		txtPhone.requestFocus();

		btnSearch = (Button) findViewById(R.id.btnSearch);
		btnSearch.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				String phone = txtPhone.getText().toString();
				if (isOnline()) {
					DataUser du = new DataUser(AddNewTracker.this,
							"http://www.users.jotracker.com/android.php?device_number="
									+ phone + "&action=get");
					du.execute();

				}
			}
		});

		btnAdd = (Button) findViewById(R.id.btnAddTracker);
		btnAdd.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				String imei = txtImei.getText().toString();
				String name = txtName.getText().toString();
				String license_number = txtLicenseNumber.getText().toString();
				// String license_expiration =
				// txtLicenseExpiration.getText().toString();
				String vehicle = txtVehicle.getText().toString();
				String description = txtDescription.getText().toString();
				String phone = txtPhone.getText().toString();
				String password = txtPassword.getText().toString();
				try {
					String fail = null;
					fail = getString(R.string.fill_all_fields);
					db.open();
					if (phone.contentEquals("")) {
						Toast.makeText(AddNewTracker.this, fail + " ",
								Toast.LENGTH_LONG).show();
						txtPhone.requestFocus();
					} else if (vehicle.contentEquals("")) {
						Toast.makeText(AddNewTracker.this, fail + " ",
								Toast.LENGTH_LONG).show();
						txtVehicle.requestFocus();
					} else if (description.contentEquals("")) {
						Toast.makeText(AddNewTracker.this, fail + " ",
								Toast.LENGTH_LONG).show();
						txtDescription.requestFocus();
					} else if (password.contentEquals("")) {
						Toast.makeText(AddNewTracker.this, fail + " ",
								Toast.LENGTH_LONG).show();
						txtPassword.requestFocus();
					} else {

						if (db.checkTrackerExist(imei)) {
							db.insertTrackes(imei, name, license_number,
									user.getLicense_expiration(), vehicle,
									description, phone, password, "admin"
											+ password, "noadmin" + password,
									"fix020s***n" + password, "nofix"
											+ password, "monitor" + password,
									"tracker" + password,
									"password" + password, "arm" + password,
									"disarm" + password, "move" + password
											+ " 0200", "nomove" + password,
									"speed" + password, "nospeed" + password,
									"imei" + password, "check" + password,
									"stop" + password, "resume" + password,
									"silent" + password, "loud" + password,
									"reset" + password, "adminip" + password
											+ " 202.104.150.75 9000",
									"noadminip" + password, "gprs" + password,
									"sms" + password, "apn" + password
											+ " your.apn.com", " ", " ",
									"help me" + password, "acc" + password,
									"noacc" + password, "address" + password,
									"time zone" + password, "begin" + password,
									"nooil" + password, "temperature"
											+ password + " -005C#090C",
									"notemperature" + password, "photo"
											+ password, "save030s***n"
											+ password, "clear" + password);
							String info = getString(R.string.tracker_add_success);
							info += "\n";
							info += getString(R.string.add_new_tracker);
							AlertDialog.Builder builder = new AlertDialog.Builder(
									AddNewTracker.this);
							builder.setMessage(info)
									.setCancelable(false)
									.setTitle(R.string.alert)
									.setIcon(R.drawable.ic_alert)
									.setPositiveButton(
											R.string.yes,
											new DialogInterface.OnClickListener() {
												public void onClick(
														DialogInterface dialog,
														int id) {
													txtImei.setText("");
													txtVehicle.setText("");
													txtDescription.setText("");
													txtPhone.setText("");
													txtPassword.setText("");
													txtImei.requestFocus();
												}
											})
									.setNegativeButton(
											R.string.no,
											new DialogInterface.OnClickListener() {
												public void onClick(
														DialogInterface dialog,
														int id) {
													finish();
												}
											});
							AlertDialog alert = builder.create();
							alert.show();
						} else {
							String info = getString(R.string.tracker_add_fail);
							info += "\n";
							info += getString(R.string.add_new_tracker);
							AlertDialog.Builder builder = new AlertDialog.Builder(
									AddNewTracker.this);
							builder.setMessage(info)
									.setCancelable(false)
									.setTitle(R.string.alert)
									.setIcon(R.drawable.ic_alert)
									.setPositiveButton(
											R.string.yes,
											new DialogInterface.OnClickListener() {
												public void onClick(
														DialogInterface dialog,
														int id) {
													txtImei.setText("");
													txtName.setText("");
													txtLicenseNumber
															.setText("");
													// txtLicenseExpiration.setText("");
													txtVehicle.setText("");
													txtDescription.setText("");
													txtPhone.setText("");
													txtPassword.setText("");
													txtPhone.requestFocus();
												}

											})
									.setNegativeButton(
											R.string.no,
											new DialogInterface.OnClickListener() {
												public void onClick(
														DialogInterface dialog,
														int id) {
													finish();
												}
											});
							AlertDialog alert = builder.create();
							alert.show();
						}
					}
					db.close();
				}

				catch (Exception e) {
					Toast.makeText(AddNewTracker.this, e.getMessage(),
							Toast.LENGTH_LONG).show();
				}
			}
		});

		btnCancel = (Button) findViewById(R.id.btnGoBack);
		btnCancel.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent returnIntent = new Intent();
				returnIntent.putExtra("AddedTracker", "imei");
				setResult(RESULT_OK, returnIntent);
				finish();

			}
		});

		btnClear = (Button) findViewById(R.id.btnClear);
		btnClear.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				clearAllFields();
			}
		});
	}

	public boolean isOnline() {
		ConnectivityManager conn = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = conn.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		showMessageError(getString(R.string.no_net), 999);
		return false;
	}

	public boolean checkStrNotNull(String str) {
		if (str == "")
			return false;
		else
			return true;
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	public void showMessageError(String msg, final int codigo) {
		AlertDialog.Builder builder = new AlertDialog.Builder(
				AddNewTracker.this);
		builder.setMessage(msg)
				.setCancelable(false)
				.setTitle(getString(R.string.message))
				.setIcon(R.drawable.ic_alert)
				.setPositiveButton(R.string.ok,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								if (codigo == 999) {
									Intent intent = new Intent();
									intent.setClass(AddNewTracker.this,
											SendDataNewTracker.class);
									startActivity(intent);
									//sendEmail();
								}
							}
						});
		AlertDialog alert = builder.create();
		alert.show();
	}

	public void clearAllFields() {
		txtImei.setText("");
		txtName.setText("");
		txtLicenseNumber.setText("");
		// txtLicenseExpiration.setText("");
		txtVehicle.setText("");
		txtDescription.setText("");
		txtPhone.setText("");
		txtPassword.setText("");
		txtPhone.requestFocus();
	}

	private static String getTagValue(String tag, Element element) {
		NodeList nl = element.getElementsByTagName(tag).item(0).getChildNodes();
		Node node = (Node) nl.item(0);
		return node.getNodeValue();
	}

	public String getCurrentDateAndroid() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal = Calendar.getInstance();
		return dateFormat.format(cal.getTime());
	}

	public class DataUser extends AsyncTask<Void, Void, String[]> {

		private ProgressDialog progress;
		private Context context;
		private String[] result = new String[2];
		private String u;
		private boolean execao = false;

		public DataUser(Context context, String url) {
			this.context = context;
			this.u = url;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// Cria novo um ProgressDialogo e exibe
			progress = new ProgressDialog(context);
			progress.setMessage(getString(R.string.wait));
			progress.setCancelable(false);
			progress.show();
		}

		@Override
		protected String[] doInBackground(Void... params) {
			try {
				URL url = new URL(u);
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
				NodeList nl = document.getElementsByTagName("User");
				for (int i = 0; i < nl.getLength(); i++) {
					Node node = nl.item(i);
					if (node.getNodeType() == Node.ELEMENT_NODE) {
						Element element = (Element) node;
						user.setLogin(getTagValue("Login", element));
						if (user.getLogin().endsWith("true")) {
							user.setId(getTagValue("Id", element));
							user.setName(getTagValue("Name", element));
							user.setImei(getTagValue("Imei", element));
							user.setDevice_number(getTagValue("Device_number",
									element));
							user.setUser_number(getTagValue("User_number",
									element));
							user.setExpiry_date(getTagValue("Expiry_date",
									element));
							user.setCar_number(getTagValue("Car_number",
									element));
							user.setDate(getTagValue("Date", element));
							user.setLicense_number(getTagValue(
									"License_number", element));
							user.setLicense_expiration(getTagValue(
									"License_expiration", element));

							result[0] = user.getLogin();
						} else {
							result[0] = user.getLogin();
						}
					}
				}

			} catch (SAXException e) {
				execao = true;
				e.printStackTrace();
			} catch (IOException e) {
				execao = true;
				e.printStackTrace();
			}
			return result;
		}

		protected void onPostExecute(String[] result) {
			super.onPostExecute(result);
			progress.dismiss();
			if (execao) {
				Toast.makeText(AddNewTracker.this,
						getString(R.string.server_no_response),
						Toast.LENGTH_LONG).show();
			} else {
				if (result[0].equals("true")) {
					checkIfExpired();
				} else {
					showMessageError(getString(R.string.error_search), 999);
				}
			}
		}

		@Override
		protected void onProgressUpdate(Void... values) {
			super.onProgressUpdate(values);
		}
	}

	public void sendEmail() {
		TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		String email = "Please, fill in the details below!\nIMEI Tracker Number: "
				+ "\nMobile Number: "
				+ tm.getLine1Number()
				+ "\nContry: "
				+ tm.getNetworkCountryIso().toString() + "\nName: ";
		Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
		emailIntent.setType("plain/text");
		emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL,
				new String[] { "jo-tracker@hotmail.com" });
		emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
				"Add New Mobile Number");
		emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, email);
		startActivity(Intent.createChooser(emailIntent,
				String.valueOf(R.string.email_send)));
		finish();

	}

	public void checkIfExpired() {
		/*
		 * if(license.isExpired(user.getLicense_expiration(), getDateServer())){
		 * btnAdd.setEnabled(false); btnClear.setEnabled(true);
		 * showMessageError(getString(R.string.license_expired), 999); } else{
		 */
		Toast.makeText(AddNewTracker.this,
				getString(R.string.sucess_search), Toast.LENGTH_LONG).show();
		txtImei.setText(user.getImei());
		txtName.setText(user.getName());
		txtLicenseNumber.setText(user.getLicense_number());
		// txtLicenseExpiration.setText(user.getLicense_expiration());
		txtVehicle.requestFocus();
		txtVehicle.setEnabled(true);
		txtDescription.setEnabled(true);
		txtPhone.setEnabled(true);
		txtPassword.setEnabled(true);
		btnAdd.setEnabled(true);
		btnClear.setEnabled(true);
		// }
	}

}
