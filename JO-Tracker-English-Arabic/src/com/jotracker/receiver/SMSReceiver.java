package com.jotracker.receiver;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.jotracker.Main;
import com.jotracker.R;
import com.jotracker.ShowMessages;
import com.jotracker.data.DBAdapter;

public class SMSReceiver extends BroadcastReceiver implements
		View.OnClickListener {
	private DBAdapter db;
	private String sms, msg, phone, vehicle, latitude, longitude, speed, bat, signal, info, imei, power, acc, door, photo_link, photo,
			erro = "", date, hour;
	int tamanho = 0;
	private GregorianCalendar gc;
	private Context ctx;
	static final String PREFS_SOUND = "SOUND";
	static final String PREFS_PHOTO_LINK = "PHOTO_LINK";
	public SharedPreferences sharedPrefs;
	private String alerts[] = { "ACC,alarm", "Acc,alarm", "acc,alarm",
			"Door,alarm", "door,alarm", "Sensor,alarm", "sensor,alarm",
			"Power,alarm", "power,alarm", "Speed,alarm", "speed,alarm",
			"speed!", "Help,me", "help,me", "nofortify", "stockade", "Move",
			"move", "Temperature:",  "Oil:", "POWER:", "http://www.gpstrackerxy.com/",
			"http://www.jotracker.com/", "http://WWW.GPSTRACKERXY.com/",
			"http://www.GPSTrackerXY.com/" };
	private String words[] = { "http://", "lat:", "Lat:", "long:", "Long:",
			"Latitude", "latitude", "Longitude", "longitude" };
	private String speed_alert[] = { "Speed alarm", "speed alarm", "speed!" };
	private SharedPreferences pref_photo_link;

	@Override
	public void onReceive(Context context, Intent intent) {

		db = new DBAdapter(context);
		ctx = context;
		gc = new GregorianCalendar();
		int month = gc.get(Calendar.MONTH);
		month++;
		date = String.valueOf(month + "/" + gc.get(Calendar.DATE) + "/"
				+ gc.get(Calendar.YEAR));
		hour = String.valueOf(gc.get(Calendar.HOUR) + ":"
				+ gc.get(Calendar.MINUTE) + ":" + gc.get(Calendar.SECOND));
		if (intent.getAction()
				.equals("android.provider.Telephony.SMS_RECEIVED")) {
			Bundle bundle = intent.getExtras();
			if (bundle != null) {
				Object[] pdus = (Object[]) bundle.get("pdus");
				SmsMessage[] messages = new SmsMessage[pdus.length];
				for (int i = 0; i < pdus.length; i++)
					messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
				for (SmsMessage message : messages) {
					phone = message.getOriginatingAddress();
					msg = "Phone: " + message.getOriginatingAddress()
							+ "\nMessage: "
							+ message.getMessageBody().toString();
					sms = message.getMessageBody().toString();
				}
				// Log.i("Phone: ", phone.toString());
				// Log.i("Phone Format: ",formatPhone(phone.toString()));
				// verifica se o sms é de um numero cadastrado
				db.open();
				boolean exist = db.checkPhoneExist(formatPhone(phone));
				db.close();
				// é um SMS do rastreador
				if (exist) {
					// is tk 102 or 103
					Log.i("SMS old: ", sms);
					setPhone(phone);
					setVehicle(phone);
					// é um SMS de localização
					if (checkSMS(sms)) {
						// substitui espaços e quebras de linhas
						if (sms != null) {
							sms = sms.replaceAll(
									System.getProperty("line.separator"), ",");
							sms = sms.replaceAll(" ", ",");
							while (sms.contains(",,")) {
								sms = sms.replaceAll(",,", ",");
							}
							setTan(sms);
							Log.i("SMS new: ", sms);
							// Log.i("Tamanho SMS: ", String.valueOf(tamanho));
							// Alerta

							// Tenta gravar a localização
							if (recordData(sms)) {
								if (setInfo(sms)) {
									Log.i("getPhoto(): ", getPhoto());
									Intent intentShowSMS = new Intent(context,
											ShowMessages.class);
									intentShowSMS.putExtra("id", 2);
									intentShowSMS.putExtra("alert",
											info.toString());
									intentShowSMS.putExtra("phone", getPhone()
											.toString());
									intentShowSMS.putExtra("msg",
											msg.toString());
									intentShowSMS.putExtra("photo", getPhoto());
									intentShowSMS
											.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
									intentShowSMS
											.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
									context.startActivity(intentShowSMS);
								} else {
									notifySMS();
									Toast.makeText(context, R.string.new_sms,
											Toast.LENGTH_LONG).show();
									Intent intentMaps = new Intent(context,
											Main.class);
									intentMaps
											.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
									intentMaps
											.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
									context.startActivity(intentMaps);
								}
							} else {
								// Rastreador incompatível
								notifySMS();
								Intent intentSendSMS = new Intent(context,
										ShowMessages.class);
								intentSendSMS.putExtra("id", 1);
								intentSendSMS
										.putExtra("msg", msg.toString()
												+ "\nGPSTrackerBySMS-Pro\n"
												+ getErro());
								intentSendSMS
										.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
								intentSendSMS
										.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								context.startActivity(intentSendSMS);
								Log.e("Erro:", getErro().toString());
							}
						}
					} else {
						// exibe outros SMS
						Intent intentShowSMS = new Intent(context,
								ShowMessages.class);
						intentShowSMS.putExtra("id", 2);
						intentShowSMS.putExtra("alert", "noalert");
						intentShowSMS.putExtra("phone", getPhone().toString());
						intentShowSMS.putExtra("msg", msg.toString());
						intentShowSMS.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						intentShowSMS.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						context.startActivity(intentShowSMS);
					}
					this.abortBroadcast();
				}
			}
		}
	}

	public boolean isDouble(String input) {
		try {
			Double.parseDouble(input);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public void setTan(String input) {
		try {
			String _tan[] = input.split(",");
			tamanho = _tan.length;
		} catch (Exception e) {
			setErro(getClass().getName() + " setTan(): Exception "
					+ e.toString());
			Log.e(getClass().getName() + " setTan(): ",
					"Exception " + e.toString());
		}

	}

	public boolean recordData(String sms) {
		try {
			setLat(sms);
			setLong(sms);
			setSpeed(sms);
			//setDateHour(sms);
			setBat(sms);
			setSignal(sms);
			setImei(sms);
			setInfo(sms);/**/
			setPower(sms);
			setAcc(sms);
			setDoor(sms);
			setPhoto(sms);
			latitude = formatDirLatLon(latitude);
			longitude = formatDirLatLon(longitude);
			Log.i("Phone: ", getPhone().toString());
			Log.i("Latitude: ", latitude.toString());
			Log.i("Longitude: ", longitude.toString());
			Log.i("Bat: ", bat.toString());
			Log.i("Signal: ", signal.toString());
			Log.i("Speed: ", speed.toString());
			Log.i("Info: ", info.toString());
			Log.i("Imei: ", imei.toString());
			Log.i("Vechicle: ", getVehicle().toString());
			Log.i("Photo: ", getPhoto().toString());/**/

			if (isDouble(latitude) && isDouble(longitude)) {
				setErro(getClass().getName() + "isDouble(): Exception "
						+ latitude + ", " + longitude);
				db.open();
				db.insertCoords(getPhone(), imei, getVehicle(), latitude,
						longitude, speed, getDate(), getHour(), bat, signal, info,
						getPower(), getAcc(), getDoor(), getPhoto());
				db.close();
				return true;
			}
		} catch (Exception e) {
			setErro(getClass().getName() + " recordData(): Exception "
					+ e.toString());
			Log.e(getClass().getName() + " recordData(): ",
					"Exception " + e.toString());
		}
		return false;
	}

	public void notifySMS() {
		Uri notification = RingtoneManager
				.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		Ringtone ring = RingtoneManager.getRingtone(ctx, notification);
		if (ring != null) {
			ring.play();
		}
	}

	public void setLat(String input) {
		/*
		 * **********************************************************************
		 * http://maps.google.com/maps?f=q&hl=en&q=44.421893,008.907513&ie=UTF8&z
		 * =16&iwIoc=addr&om=1speed:000.0&imei=358948012531487
		 * ****************************************************
		 * lat:43.254784lon:76.959824 speed:0.00 T:12/05/13 14:06 bat:100%
		 * 359710040291685
		 * http://maps.google.com/maps?f=q&q=loc:43.254784,76.959824&z=16
		 * ***************************************************************
		 */
		try {
			if (input.contains("q=")) {
				String _sms[] = input.split("q=");
				String _lat[] = _sms[1].split(",");
				latitude = _lat[0].replaceAll("loc:", "").toString();
				Log.i(getClass().getName() + " setLat(): ",
						"if(input.contains(\"q=\"))");
			} else if (input.contains("lat:")) {
				String _sms[] = input.split("lat:");
				if (_sms[1].startsWith(",")) {
					String _lat[] = _sms[1].split(",");
					latitude = _lat[1].toString();
				} else {
					String _lat[] = _sms[1].split(",");
					latitude = _lat[0].toString();
				}
				Log.i(getClass().getName() + " setLat(): ",
						"if(input.contains(\"lat:\"))");
			} else if (input.contains("Lat:")) {
				String _sms[] = input.split("Lat:");
				if (_sms[1].startsWith(",")) {
					String _lat[] = _sms[1].split(",");
					latitude = _lat[1].toString();
				} else {
					String _lat[] = _sms[1].split(",");
					latitude = _lat[0].toString();
				}
				Log.i(getClass().getName() + " setLat(): ",
						"if(input.contains(\"Lat:\"))");
			} else if (input.contains("Latitude:")) {
				String _sms[] = input.split("Latitude:");
				if (_sms[1].startsWith(",")) {
					String _lat[] = _sms[1].split(",");
					latitude = _lat[1].toString();
				} else {
					String _lat[] = _sms[1].split(",");
					latitude = _lat[0].toString();
				}
				Log.i(getClass().getName() + " setLat(): ",
						"if(input.contains(\"Latitude:\"))");
			}
			// Tracker model PST T-100
			// Latitude,=,55,35,27.28N,Longitude,=,012,18,57.24E,Speed=0.0KM/h,2012-04-05,22:52
			else if (input.contains("Latitude,=,")) {
				String _sms[] = input.split("Latitude,=,");
				String _lat[] = _sms[1].split(",");
				double grau, min, seg = 0;
				// pega o sinal + ou -
				latitude = setDirLatLon(_lat[2].toString());
				// converte para notaçao decimal
				grau = Double.parseDouble(_lat[0].toString());
				min = Double.parseDouble(_lat[1].toString());
				seg = Double.parseDouble(formatSeg(_lat[2].toString())) / 60;
				min = (min + seg) / 60;
				latitude += String.valueOf(roundTo(grau + min, 6));
				Log.i(getClass().getName() + " setLat(): ",
						"if(input.contains(\"Latitude,=,\"))");
			} else if (checkMessage(input.toString())) {
				// UnArmed;ACC:on;Door:close;Power:off;LAC:16103;CID:43429;Time:08-19-12,12:28:38,Speed:000km/h,S:23d38m22s,W:046d50m09s
				if (input.contains("S:") || input.contains("N:")) {
					String _sms[] = input.split(",");
					String lat = _sms[_sms.length - 2];
					lat = lat.replaceAll("d", ",");
					lat = lat.replaceAll("m", ",");
					lat = lat.replaceAll("s", ",");
					String _lat[] = lat.split(",");
					double grau, min, seg = 0;
					// pega o sinal + ou -
					latitude = setDirLatLon(_lat[0].toString());
					// converte para notaçao decimal
					grau = Double.parseDouble(_lat[0].replaceAll("S:", "")
							.replaceAll("N:", "").toString());
					min = Double.parseDouble(_lat[1].toString());
					seg = Double.parseDouble(formatSeg(_lat[2].toString())) / 60;
					min = (min + seg) / 60;
					latitude += String.valueOf(roundTo(grau + min, 6));
					Log.i(getClass().getName() + " setLat(): ", "latitude "
							+ latitude);
				} else {
					// convert 32,01.8500N to 32,01,8500N
					String _sms[] = input.split(",");
					String input2 = _sms[1].toString();
					// convert 01.8500N to 01,8500N
					input2 = input2.replace(".", ",");
					String _lat[] = input2.split(",");
					double grau, min, seg = 0;
					String ss = "0." + formatSeg(_lat[1].toString());
					// pega o sinal + ou -
					latitude = setDirLatLon(_lat[1].toString());
					// converte para notaçao decimal
					grau = Double.parseDouble(_sms[0].toString());
					min = Double.parseDouble(_lat[0].toString());
					seg = (Double.parseDouble(ss) * 60) / 60;
					min = (min + seg) / 60;
					latitude += String.valueOf(roundTo(grau + min, 8));
				}
				Log.i(getClass().getName() + " setLat(): ",
						"if(checkMessage(input.toString()))");
			}
		} catch (Exception e) {
			setErro(getClass().getName() + " setLat(): Exception "
					+ e.toString());
			Log.e(getClass().getName() + " setLat(): ",
					"Exception " + e.toString());
		}

	}

	public void setLong(String input) {
		try {
			/*
			 * **********************************************************************
			 * http://maps.google.com/maps?f=q&hl=en&q=44.421893,008.907513&ie=UTF8
			 * &z =16&iwIoc=addr&om=1speed:000.0&imei=358948012531487
			 * ****************************************************
			 * lat:43.254784lon:76.959824 speed:0.00 T:12/05/13 14:06 bat:100%
			 * 359710040291685
			 * http://maps.google.com/maps?f=q&q=loc:43.254784,76.959824&z=16
			 * ***************************************************************
			 */
			if (input.contains("q=")) {
				String _sms[] = input.split("q=");
				String _sms2[] = _sms[1].split(",");
				String _long[] = _sms2[1].split("&");
				longitude = _long[0].toString();
				Log.i(getClass().getName() + " setLong(): ",
						"if(input.contains(\"q=\"))");
			} else if (input.contains("long:")) {
				String _sms[] = input.split("long:");
				if (_sms[1].startsWith(",")) {
					String _long[] = _sms[1].split(",");
					longitude = _long[1].toString();
				} else {
					String _long[] = _sms[1].split(",");
					longitude = _long[0].toString();
				}
				Log.i(getClass().getName() + " setLong(): ",
						"if(input.contains(\"long:\"))");
			} else if (input.contains("Long:")) {
				String _sms[] = input.split("Long:");
				if (_sms[1].startsWith(",")) {
					String _long[] = _sms[1].split(",");
					longitude = _long[1].toString();
				} else {
					String _long[] = _sms[1].split(",");
					longitude = _long[0].toString();
				}
				Log.i(getClass().getName() + " setLong(): ",
						"if(input.contains(\"Long:\"))");
			} else if (input.contains("Longitude:")) {
				String _sms[] = input.split("Longitude:");
				if (_sms[1].startsWith(",")) {
					String _long[] = _sms[1].split(",");
					longitude = _long[1].toString();
				} else {
					String _long[] = _sms[1].split(",");
					longitude = _long[0].toString();
				}
				Log.i("setLong(): ", "if(input.contains(\"Longitude:\"))");
			}
			// Tracker model PST T-100
			// Latitude,=,55,35,27.28N,Longitude,=,012,18,57.24E,Speed=0.0KM/h,2012-04-05,22:52
			else if (input.contains("Longitude,=,")) {
				String _sms[] = input.split("Longitude,=,");
				String _long[] = _sms[1].split(",");
				double grau, min, seg = 0;
				// pega o sinal + ou -
				longitude = setDirLatLon(_long[2].toString());
				// converte para notaçao decimal
				grau = Double.parseDouble(_long[0].toString());
				min = Double.parseDouble(_long[1].toString());
				seg = Double.parseDouble(formatSeg(_long[2].toString())) / 60;
				min = (min + seg) / 60;
				longitude += String.valueOf(roundTo(grau + min, 6));
				Log.i(getClass().getName() + " setLong(): ",
						"if(input.contains(\"Longitude,=,\"))");
			} else if (checkMessage(input.toString())) {
				// UnArmed;ACC:on;Door:close;Power:off;LAC:16103;CID:43429;Time:08-19-12,12:28:38,Speed:000km/h,S:23d38m22s,W:046d50m09s
				if (input.contains("W:") || input.contains("E:")) {
					String _sms[] = input.split(",");
					String lon = _sms[_sms.length - 1];
					lon = lon.replaceAll("d", ",");
					lon = lon.replaceAll("m", ",");
					lon = lon.replaceAll("s", ",");
					String _long[] = lon.split(",");
					// String lon = _long[_long.length-1];
					Log.i(getClass().getName() + " setLong(): ", "lon " + lon);
					double grau, min, seg = 0;
					// pega o sinal + ou -
					longitude = setDirLatLon(_long[0].toString());
					// converte para notaçao decimal
					grau = Double.parseDouble(_long[0].replaceAll("W:", "")
							.replaceAll("E:", "").toString());
					min = Double.parseDouble(_long[1].toString());
					seg = Double.parseDouble(formatSeg(_long[2].toString())) / 60;
					min = (min + seg) / 60;
					longitude += String.valueOf(roundTo(grau + min, 6));
					Log.i(getClass().getName() + " setLong(): ", "longitude "
							+ longitude);
				} else {
					// convert 32,01.8500N to 32,01,8500N
					String _sms[] = input.split(",");
					String input2 = _sms[3].toString();
					// convert 01.8500N to 01,8500N
					input2 = input2.replace(".", ",");
					String _lat[] = input2.split(",");
					double grau, min, seg = 0;
					String ss = "0." + formatSeg(_lat[1].toString());
					// pega o sinal + ou -
					longitude = setDirLatLon(_lat[1].toString());
					// converte para notaçao decimal
					grau = Double.parseDouble(_sms[2].toString());
					min = Double.parseDouble(_lat[0].toString());
					seg = (Double.parseDouble(ss) * 60) / 60;
					min = (min + seg) / 60;
					longitude += String.valueOf(roundTo(grau + min, 8));// ***/
				}
				Log.i(getClass().getName() + " setLong(): ",
						"if(checkMessage(input.toString()))");
			}

		} catch (Exception e) {
			setErro(getClass().getName() + " setLon(): Exception "
					+ e.toString());
			Log.e("setLong(): ",
					getClass().getName() + " Exception " + e.toString());
		}

	}

	public String setSpeed(String input) {
		if (input.contains("speed:")) {
			String _sms[] = input.split("speed:");
			if (_sms[1].startsWith(",")) {
				String _speed[] = _sms[1].split(",");
				speed = _speed[1].toString();
			} else {
				String _speed[] = _sms[1].split(",");
				speed = _speed[0].toString();
				if (speed.contains("&")) {
					String _speed2[] = _sms[1].split("&");
					speed = _speed2[0].toString();
				}
			}
		} else if (input.contains("Speed:")) {
			String _sms[] = input.split("Speed:");
			if (_sms[1].startsWith(",")) {
				String _speed[] = _sms[1].split(",");
				speed = _speed[1].toString();
			} else {
				String _speed[] = _sms[1].split(",");
				speed = _speed[0].toString();
			}
		}
		// Tracker model PST T-100
		// Latitude,=,55,35,26.55N,Longitude,=,012,18,57.50E,Speed,=,0.0Km/h,2012-04-10,22:28
		else if (input.contains("Speed,=,")) {
			String _sms[] = input.split("Speed,=,");
			String _speed[] = _sms[1].split(",");
			if (!_speed[0].toString().equals("")
					|| !_speed[0].toString().equals("null"))
				speed = _speed[0].toString();
			else
				speed = "0";
		}
		// Tracker model TK 106
		// http://maps.google.com/?q=10.153110N,064.700950W,Latitude:10.153110N,Longitude:064.700950W,Speed,=000.0km/h,2012-10-01,21:14,GMT
		else if (input.contains("Speed,=")) {
			String _sms[] = input.split("Speed,=");
			String _speed[] = _sms[1].split(",");
			if (!_speed[0].toString().equals("")
					|| !_speed[0].toString().equals("null"))
				speed = _speed[0].toString();
			else
				speed = "0";
		}
		return speed;
	}
	public void setImei(String input) {
		if (input.contains("imei:")) {
			String _sms[] = input.split("imei:");
			if (_sms[1].startsWith(",")) {
				String _imei[] = _sms[1].split(",");
				imei = _imei[1].toString();
			} else {
				String _imei[] = _sms[1].split(",");
				imei = _imei[0].toString();
			}
		} else if (input.contains("IMEI:,")) {
			String _sms[] = input.split("IMEI:,");
			imei = _sms[1].toString();
		} else if (input.contains("ID:")) {
			String _sms[] = input.split("ID:");
			String _imei[] = _sms[1].split(",");
			imei = _imei[0].toString();
		} else if (input.contains("key=")) {
			String _sms[] = input.split("key=");
			String _imei[] = _sms[1].split("@");
			imei = _imei[0].toString();
		} else {
			imei = "0";
		}
	}

	public void setBat(String input) {
		if (input.contains("Bat:")) {
			String _sms[] = input.split("Bat:");
			String _bat[] = _sms[1].split(",");
			bat = _bat[0].toString();
		} else if (input.contains("bat:")) {
			String _sms[] = input.split("bat:");
			String _str[] = _sms[1].split(",");
			if (_str[0].contains("%")) {
				String _bat[] = _str[0].split("%");
				bat = _bat[0].toString();
				bat = bat + "%";
			} else {
				bat = _str[0].toString();
			}
		} else if (input.contains("Batt:")) {
			String _sms[] = input.split("Batt:");
			String _bat[] = _sms[1].split(",");
			bat = _bat[0].toString();
		} else {
			bat = " ";
		}
	}

	public void setSignal(String input) {
		if (input.contains("signal:")) {
			String _sms[] = input.split("signal:");
			String _signal[] = _sms[1].split(",");
			signal = _signal[0].toString();
		} else if (input.contains("Signal:")) {
			String _sms[] = input.split("Signal:");
			String _signal[] = _sms[1].split(",");
			signal = _signal[0].toString();
		} else {
			signal = " ";
		}
	}

	public boolean setInfo(String input) {
		Log.i(getClass().getName() + " setInfo(): ", " getPhotoLink() = "
				+ getPhotoLink());
		for (int j = 0; j < alerts.length; j++) {
			if (input.contains(alerts[j]) || input.contains(getPhotoLink())) {
				info = alerts[j] + "!";
				return true;
			}
		}
		info = "New Location";
		return false;
	}

	public String setDirLatLon(String input) {
		if (input.contains("N")) {
			input = "+";
		} else if (input.contains("S")) {
			input = "-";
		} else if (input.contains("E")) {
			input = "+";
		} else if (input.contains("W")) {
			input = "-";
		}
		return input;
	}

	public String formatSeg(String input) {
		if (input.contains("N")) {
			input = input.replace("N", "");
		} else if (input.contains("S")) {
			input = input.replace("S", "");
		} else if (input.contains("E")) {
			input = input.replace("E", "");
		} else if (input.contains("W")) {
			input = input.replace("W", "");
		}
		return input;
	}

	public double roundTo(double input, int casas) {
		double result = 0;
		result = Math.round(input * Math.pow(10, casas)) / Math.pow(10, casas);
		return result;
	}

	public String formatDirLatLon(String input) {
		if (input.contains("N")) {
			input = "+" + input.replace("N", "");
		} else if (input.contains("S")) {
			input = "-" + input.replace("S", "");
		} else if (input.contains("E")) {
			input = "+" + input.replace("E", "");
		} else if (input.contains("W")) {
			input = "-" + input.replace("W", "");
		}
		return input;
	}

	public void onClick(View arg0) {

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

	public boolean checkSMS(String sms) {
		for (int i = 0; i < words.length; i++) {
			if (sms.contains(words[i])) {
				return true;
			}
		}
		return false;
	}

	public boolean checkMessage(String input) {
		String str[] = { "N:", "S:", "E:", "W:" };
		for (int i = 0; i < str.length; i++) {
			if (input.contains(str[i])) {
				return true;
			}
		}
		return false;
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

	/*
	 * private void displayNoticeSMS (String sms){ Notification notification =
	 * new Notification(R.drawable.notice, sms, System.currentTimeMillis());
	 * PendingIntent pi = PendingIntent.getActivity(this, 0, new Intent(this,
	 * SMSReceiver.class), 0); notification.setLatestEventInfo(this,
	 * "ServiceSMS", sms, pi);
	 * notificationManager.notify(R.string.notification_id,notification); }
	 */

	public String getErro() {
		return erro;
	}

	public void setErro(String erro) {
		this.erro = erro;
	}

	public String[] getAlerts() {
		return alerts;
	}

	public void setAlerts(String alerts[]) {
		this.alerts = alerts;
	}

	private String getPhone() {
		return phone;
	}

	private void setPhone(String phone) {
		db.open();
		this.phone = db.getPhoneNumber(phone);
		;
		db.close();
	}

	private String getVehicle() {
		return vehicle;
	}

	private void setVehicle(String phone) {
		db.open();
		this.vehicle = db.getVehicleName(phone);
		db.close();
	}

	public String getPhotoLink() {
		try {
			setPrefPhotoLink(ctx.getSharedPreferences(PREFS_PHOTO_LINK,
					Context.MODE_PRIVATE));
			this.photo_link = getPrefPhotoLink().getString("photo_link",
					"http://www.gpstrackerxy.com/AlarmImages/");
			// Log.e("PREFS_PHOTO_LINK ", "getPhotoLink()  :: " + photo_link);
		} catch (Exception ex) {
			Log.e("PREFS_PHOTO_LINK  ", "getPhotoLink()  :: " + ex.getMessage());
		}
		return photo_link;
	}

	public SharedPreferences getPrefPhotoLink() {
		return pref_photo_link;
	}

	public void setPrefPhotoLink(SharedPreferences pref_photo_link) {
		this.pref_photo_link = pref_photo_link;
	}

	public String getPower() {
		return power;
	}

	public void setPower(String power) {
		if (power.contains("PWR:")) {
			String _sms[] = power.split("PWR:");
			String _signal[] = _sms[1].split(",");
			power = _signal[0].toString();
		} else {
			power = " ";
		}
		this.power = power;
	}

	public String getAcc() {
		return acc;
	}

	public void setAcc(String acc) {
		if (acc.contains("ACC:")) {
			String _sms[] = acc.split("ACC:");
			String _signal[] = _sms[1].split(",");
			acc = _signal[0].toString();
		} else {
			acc = " ";
		}
		this.acc = acc;
	}

	public String getDoor() {
		return door;
	}

	public void setDoor(String door) {
		if (door.contains("Door:")) {
			String _sms[] = door.split("Door:");
			String _signal[] = _sms[1].split(",");
			door = _signal[0].toString();
		} else {
			door = " ";
		}
		this.door = door;
	}

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		if(photo.contains(getPhotoLink())){
		photo = photo.replace(" ", ",").replaceAll(
				System.getProperty("line.separator"), ",");
		String _sms[] = photo.split(",");
		photo = _sms[_sms.length - 1];
		}
		else {
			photo = "EMPTY";
		}
		this.photo = photo;
	}
	private String getDate() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return date = sdf.format(new Date());
	}

	private String getHour() {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		return hour = sdf.format(new Date());
	}
}
