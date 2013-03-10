package com.jotracker;

import java.io.IOException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
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
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.jotracker.common.IActionListener;
import com.jotracker.common.Utilities;
import com.jotracker.email.AutoEmailHelper;

public class SendDataNewTracker extends Activity implements IActionListener {

	public EditText txtImei, txtName, txtCounty, txtEmail, txtPhone,
			txtPassword;
	public Button btnSend, btnCancel, btnClear;
	static final String PREFS_DATE_SERVER = "DATE_SERVER";
	private String url_host;
	public SharedPreferences sharedPrefs;
	private ProgressDialog progress;
	private boolean send_email;
	//private String email_to = "robsimoes@gmail.com, gpstrackerbysms@gmail.com";
	private String email_to = "altarefy@gmail.com";
	private final Handler handler = new Handler();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.send_data_new_tracker);

		txtName = (EditText) findViewById(R.id.eTName);
		txtImei = (EditText) findViewById(R.id.eTImei);
		txtPhone = (EditText) findViewById(R.id.eTPhone);
		txtCounty = (EditText) findViewById(R.id.eTCountry);
		txtEmail = (EditText) findViewById(R.id.eTEmail);
		txtName.requestFocus();

		btnSend = (Button) findViewById(R.id.btnSend);
		btnSend.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				/*
				 * setUrlHost("http://www.users.jotracker.com/new.php?name=" +
				 * txtName.getText().toString().replaceAll(" ", "*") + "&email="
				 * + txtEmail.getText().toString().replaceAll(" ", "*") +
				 * "&country=" + txtCounty.getText().toString().replaceAll(" ",
				 * "*") + "&imei=" +
				 * txtImei.getText().toString().replaceAll(" ", "*") + "&phone="
				 * + txtPhone.getText().toString() + "&action=new"); /*
				 * sendEmail(txtName.getText().toString(), txtImei.getText()
				 * .toString(), txtPhone.getText().toString(), txtCounty
				 * .getText().toString(), txtEmail.getText().toString());
				 */
				// NewUser nu = new NewUser(AddNewTracker.this, getUrlHost());
				// nu.execute();

				String userName = txtName.getText().toString();
				String userEmail = txtEmail.getText().toString();
				String userCountry = txtCounty.getText().toString();
				String userDeviceIMEI = txtImei.getText().toString();
				String userDeviceNumber = txtPhone.getText().toString();

				if (isSendEmail()) {
					AutoEmailHelper aeh = new AutoEmailHelper(null, SendDataNewTracker.this);
					aeh.SendDataNewTracker(userName, userEmail, userCountry,
							userDeviceIMEI, userDeviceNumber, email_to,
							SendDataNewTracker.this);
					progress = new ProgressDialog(SendDataNewTracker.this);
					progress.setMessage(getString(R.string.wait));
					progress.setCancelable(false);
					progress.show();
				} else {
					showMessage(1);
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
				clearAllFields();
			}
		});
	}

	public void showMessage(Integer cod) {
		String msg = getString(R.string.remember_fail);
		if (cod == 1) {
			// msg = getString(R.string.message_not_send_by_email);
		}

		AlertDialog.Builder builder = new AlertDialog.Builder(
				SendDataNewTracker.this);
		builder.setMessage(msg)
				.setCancelable(false)
				.setTitle(getString(R.string.message))
				.setIcon(R.drawable.ic_alert)
				.setPositiveButton(R.string.yes,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								enableSendEmail();
							}
						})
				.setNegativeButton(R.string.no,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								// Don\n something!
							}
						});
		AlertDialog alert = builder.create();
		alert.show();
	}

	private void enableSendEmail() {
		Intent settings = new Intent();
		settings.setClass(SendDataNewTracker.this, Settings.class);
		startActivity(settings);
	}

	public boolean isOnline() {
		ConnectivityManager conn = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = conn.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
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

	public void clearAllFields() {
		txtName.setText("");
		txtImei.setText("");
		txtPhone.setText("");
		txtCounty.setText("");
		txtEmail.setText("");
		txtName.requestFocus();
	}

	public void sendEmail(String name, String imei, String phone,
			String country, String email) {
		String message = "\nName: " + name + "\nIMEI Tracker Number: " + imei
				+ "\nDevice Number: " + phone + "\nContry: " + country
				+ "\nEmail: " + email;
		Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
		emailIntent.setType("plain/text");
		emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL,
				new String[] { "jo-tracker@hotmail.com" });
		emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
				"Add New Mobile Number");
		emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, message);
		startActivity(Intent.createChooser(emailIntent,
				String.valueOf(R.string.email_send)));
		finish();

	}

	public void showMessage(String msg, final int codigo) {
		AlertDialog.Builder builder = new AlertDialog.Builder(
				SendDataNewTracker.this);
		builder.setMessage(msg)
				.setCancelable(false)
				.setTitle(getString(R.string.message))
				.setIcon(R.drawable.ic_alert)
				.setPositiveButton(R.string.ok,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								finish();
							}
						});
		AlertDialog alert = builder.create();
		alert.show();
	}

	private static String getTagValue(String tag, Element element) {
		NodeList nl = element.getElementsByTagName(tag).item(0).getChildNodes();
		Node node = (Node) nl.item(0);
		return node.getNodeValue();
	}

	public String getUrlHost() {
		return url_host;
	}

	public void setUrlHost(String url_host) {
		this.url_host = url_host;
	}

	public class NewUser extends AsyncTask<Void, Void, String[]> {

		private ProgressDialog progress;
		private Context context;
		private String[] result = new String[2];
		private String u;
		private boolean execao = false;

		public NewUser(Context context, String url) {
			this.context = context;
			this.u = url;
			Log.d("LOG", " NewUser " + u);
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

				HttpGet method = new HttpGet(getUrlHost());
				HttpClient client = new DefaultHttpClient();

				URL url = new URL(u);
				DocumentBuilderFactory dbf = DocumentBuilderFactory
						.newInstance();
				DocumentBuilder builder = null;
				try {
					client.execute(method);
					builder = dbf.newDocumentBuilder();
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (ParserConfigurationException e) {
					e.printStackTrace();
				}

				Document document = builder.parse(url.openStream());
				document.getDocumentElement().normalize();
				NodeList nl = document.getElementsByTagName("Result");
				for (int i = 0; i < nl.getLength(); i++) {
					Node node = nl.item(i);
					if (node.getNodeType() == Node.ELEMENT_NODE) {
						Element element = (Element) node;
						if (getTagValue("Sucess", element).endsWith("true")) {
							result[0] = "true";
						} else {
							result[0] = "false";
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
				Toast.makeText(SendDataNewTracker.this,
						getString(R.string.server_no_response),
						Toast.LENGTH_LONG).show();
			} else {
				if (result[0].equals("true")) {
					showMessage(getString(R.string.sucess_new_user), 1);
				} else {
					showMessage(getString(R.string.remember_fail), 1);
				}
			}
		}

		@Override
		protected void onProgressUpdate(Void... values) {
			super.onProgressUpdate(values);
		}
	}

	private boolean isSendEmail() {
		try {
			sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
			this.send_email = sharedPrefs.getBoolean("send_email", false);
		} catch (Exception ex) {
			Log.e("LOGIN ", "Exception isSendEmail()  ::" + ex.getMessage());
		}
		return send_email;
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
				getString(R.string.autoemail_testresult_success), this);
	}

	public void OnComplete() {
		progress.dismiss();
		handler.post(successfullySent);
	}

	public void OnFailure() {
		progress.dismiss();
		handler.post(failedSend);
	}

}
