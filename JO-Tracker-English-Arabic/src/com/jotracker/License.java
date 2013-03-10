package com.jotracker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.widget.Toast;

import com.jotracker.data.DBAdapter;

public class License {

	private SimpleDateFormat sdf;
	private DBAdapter db;
	private Context ctx;
	private String date_server;

	public License(Context ctx) {
		this.setCtx(ctx);
	}

	public void verifyLicenses(Context ctx, String data_server) {
		setDateServer(data_server);
		db = new DBAdapter(ctx);
		db.open();
		String licenses = null;
		licenses = db.selectAllLicenses();
		if (!(licenses.equals("0"))) {
			String ls[] = licenses.split(";");
			for (int i = 0; i < ls.length; ++i) {
				String l[] = ls[i].split(",");
				checkLicense(ctx, l[0], l[1]);
				//Log.i("License i: " + i, ls[i]);
			}
		}
		db.close();
	}

	public void checkLicense(Context ctx, String phone,
			String license_expiration) {

		if (isExpired(license_expiration)) {
			Toast.makeText(ctx,
					"Your tracker number " + phone + " license has expired!",
					Toast.LENGTH_LONG).show();
		} else {
			int ex = (int) expiresIn(license_expiration);
			if (ex < 5) {
				Toast.makeText(
						ctx,
						"Your tracker number " + phone + " license expires in "
								+ ex + " days!", Toast.LENGTH_LONG).show();
			}
		}
	}

	public boolean isExpired(String de) {
		Date date_current = null, date_expiration = null;
		//System.out.println("DateCurrentServer() = "+getDateServer());
		//System.out.println("DateCurrentServer() = "+date_server.getDateCurrentServer() );
		
		sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			date_current = sdf.parse(getDateServer());
			date_expiration = sdf.parse(de);
			if (date_current.after(date_expiration)) {
				//System.out.println("date_expiration == " + date_expiration);
				//System.out.println("Date1 é depois Date2 == expirou");
				return true;
			}
			if (date_current.before(date_expiration)) {
				//System.out.println("date_expiration == " + date_expiration);
				//System.out.println("Date1 é antes Date2 == não expirou");
				return false;
			}
			if (date_current.equals(date_expiration)) {
				//System.out.println("date_expiration == " + date_expiration);
				//System.out.println("Date1 é iqual Date2");
				return false;
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}/**/
		return true;
	}
	
	public boolean isExpired(String de, String ds) {
		Date date_current = null, date_expiration = null;
		//System.out.println("DateCurrentServer() = "+getDateServer());
		//System.out.println("DateCurrentServer() = "+date_server.getDateCurrentServer() );
		
		sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			date_current = sdf.parse(ds);
			date_expiration = sdf.parse(de);
			if (date_current.after(date_expiration)) {
				System.out.println("date_expiration == " + de+ " server == " + ds);
				System.out.println("Date1 é depois Date2 == expirou");
				return true;
			}
			if (date_current.before(date_expiration)) {
				System.out.println("expiration == " + de + " server == " + ds);
				System.out.println("Date1 é antes Date2 == não expirou");
				return false;
			}
			if (date_current.equals(date_expiration)) {
				System.out.println("date_expiration == " + de+ " server == " + ds);
				System.out.println("Date1 é iqual Date2");
				return false;
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return true;
	}

	public double expiresIn(String de) {
		sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date_current = null, date_expiration = null;
		try {
			date_current = sdf.parse(getDateServer());
			date_expiration = sdf.parse(de);
			// System.out.println("date_current: "+getCurrentDate());
			// System.out.println("date_expiration: "+de);
			long dt = (date_expiration.getTime() - date_current.getTime()) + 3600000;
			return (int) (dt / 86400000L);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return -999;

	}

	public Context getCtx() {
		return ctx;
	}

	public void setCtx(Context ctx) {
		this.ctx = ctx;
	}
	

	public String getDateServer() {
		return date_server;
	}

	public void setDateServer(String date) {
		this.date_server = date;
	}
	
}
