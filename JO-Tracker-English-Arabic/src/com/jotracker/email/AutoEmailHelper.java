/*
 *    This file is part of GPSLogger for Android.
 *
 *    GPSLogger for Android is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 2 of the License, or
 *    (at your option) any later version.
 *
 *    GPSLogger for Android is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with GPSLogger for Android.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.jotracker.email;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import com.jotracker.common.IActionListener;
import com.jotracker.common.SmtpSettings;
import com.jotracker.common.Utilities;
import com.jotracker.senders.IFileSender;

public class AutoEmailHelper implements IActionListener, IFileSender {

	@SuppressLint("SdCardPath")
	public static final String DEFAULT_STORAGE_LOCATION = "JoTracker/Audio/";
	public SharedPreferences sharedPrefs;
	IActionListener callback;
	private Context context;

	public AutoEmailHelper(IActionListener callback, Context context) {
		this.callback = callback;
		this.context = context;
	}

	public void UploadFile(List<File> files) {

		ArrayList<File> filesToSend = new ArrayList<File>();

		// If a zip file exists, remove others
		for (File f : files) {
			filesToSend.add(f);
			Utilities.LogInfo("filesToSend.add(f) = " + f.toString());
			/*
			 * if (f.getName().contains(".zip") || f.getName().contains(".3gpp")
			 * || f.getName().contains(".mpg") || f.getName().contains(".amr"))
			 * { //filesToSend.clear(); filesToSend.add(f); break; }
			 */
			// filesToSend.add(f);
		}

		Thread t = new Thread(new AutoSendHandler(
				filesToSend.toArray(new File[filesToSend.size()]), this,
				context));
		t.start();
	}

	void SendTestEmail(IActionListener helper) {

		Thread t = new Thread(new TestEmailHandler(helper, context));
		t.start();
	}

	public void RecoverPassWordAndLoginByEmail(String loginUser,
			String passWordUser, IActionListener helper) {

		Thread t = new Thread(new RecoverPassWordAndLoginByEmailHandler(
				loginUser, passWordUser, helper, context));
		t.start();
	}

	public void SendDataNewTracker(String userName, String userEmail,
			String userCountry, String userDeviceIMEI, String userDeviceNumber,
			String csvEmailTargets, IActionListener helper) {

		Thread t = new Thread(new SendDataNewTrackerHandler(userName,
				userEmail, userCountry, userDeviceIMEI, userDeviceNumber,
				csvEmailTargets, helper, context));
		t.start();
	}

	public void OnComplete() {
		// This was a success
		Utilities.LogInfo("Email sent");

		callback.OnComplete();
	}

	public void OnFailure() {
		callback.OnFailure();
	}

	public boolean accept(File dir, String name) {
		return name.toLowerCase().endsWith(".zip")
				|| name.toLowerCase().endsWith(".3gpp")
				|| name.toLowerCase().endsWith(".mpg")
				|| name.toLowerCase().endsWith(".amr");
	}

}

class AutoSendHandler implements Runnable {

	private SmtpSettings smtp;
	private String smtpServer, smtpPort, smtpUsername, smtpPassword,
			csvEmailTargets, fromAddress;
	boolean smtpUseSsl;
	File[] files;
	private final IActionListener helper;

	public AutoSendHandler(File[] files, IActionListener helper, Context context) {
		this.files = files;
		this.helper = helper;
		smtp = new SmtpSettings(context);
		this.smtpServer = smtp.getSmtpServer();
		this.smtpPort = smtp.getSmtpPort();
		this.smtpPassword = smtp.getStmpPassword();
		this.smtpUsername = smtp.getSmtpUsername();
		this.smtpUseSsl = smtp.isUseSsl();
		this.csvEmailTargets = smtp.getSmtpFrom();
		this.fromAddress = smtp.getSmtpFrom();
	}

	public void run() {
		try {

			Mail email = new Mail(smtpUsername, smtpPassword);

			String[] toArr = csvEmailTargets.split(",");
			email.setTo(toArr);

			if (fromAddress != null && fromAddress.length() > 0) {
				email.setFrom(fromAddress);
			} else {
				email.setFrom(smtpUsername);
			}

			email.setSubject("JoTracker :: File Upload at "
					+ Utilities.GetReadableDateTime(new Date()));
			StringBuffer message = new StringBuffer();
			message.append("Hello,\n\n");
			message.append("This is an automated email with attached file, please do not respond.\n\n");
			message.append("Regards,\n\n");
			message.append("JOTracker\n");
			message.append("www.jotracker.com\n");
			message.append("jo-tracker@hotmail.com\n");
			email.setBody(message.toString());
			email.setBody(message.toString());

			email.setPort(smtpPort);
			email.setSecurePort(smtpPort);
			email.setSmtpHost(smtpServer);
			email.setSsl(smtpUseSsl);
			for (File f : files) {
				email.addAttachment(f.getName(), f.getAbsolutePath());
			}
			email.setDebuggable(true);

			Utilities.LogInfo("Sending email...");
			if (email.send()) {
				helper.OnComplete();
			} else {
				helper.OnFailure();
			}
		} catch (Exception e) {
			helper.OnFailure();
			Utilities.LogError("AutoSendHandler.run", e);
		}

	}

}

class TestEmailHandler implements Runnable {

	private SmtpSettings smtp;
	private String smtpServer, smtpPort, smtpUsername, smtpPassword,
			csvEmailTargets, fromAddress;
	boolean smtpUseSsl;
	File[] files;
	IActionListener helper = null;

	public TestEmailHandler(IActionListener helper, Context context) {
		this.helper = helper;
		smtp = new SmtpSettings(context);
		this.smtpServer = smtp.getSmtpServer();
		this.smtpPort = smtp.getSmtpPort();
		this.smtpPassword = smtp.getStmpPassword();
		this.smtpUsername = smtp.getSmtpUsername();
		this.smtpUseSsl = smtp.isUseSsl();
		this.csvEmailTargets = smtp.getSmtpFrom();
		this.fromAddress = smtp.getSmtpFrom();
	}

	public void run() {
		try {

			Mail email = new Mail(smtpUsername, smtpPassword);

			String[] toArr = csvEmailTargets.split(",");
			email.setTo(toArr);

			if (fromAddress != null && fromAddress.length() > 0) {
				email.setFrom(fromAddress);
			} else {
				email.setFrom(smtpUsername);
			}

			email.setSubject("JoTracker :: Test Email at "
					+ Utilities.GetReadableDateTime(new Date()));
			StringBuffer message = new StringBuffer();
			message.append("Hello,\n\n");
			message.append("This is an automated test email, please do not respond.\n\n");
			message.append("Regards,\n\n");
			message.append("JOTracker\n");
			message.append("www.jotracker.com\n");
			message.append("jo-tracker@hotmail.com\n");
			email.setBody(message.toString());
			email.setPort(smtpPort);
			email.setSecurePort(smtpPort);
			email.setSmtpHost(smtpServer);
			email.setSsl(smtpUseSsl);
			email.setDebuggable(true);

			Utilities.LogInfo("Sending email...");
			if (email.send()) {
				helper.OnComplete();
			} else {
				helper.OnFailure();
			}
		} catch (Exception e) {
			helper.OnFailure();
			Utilities.LogError("AutoSendHandler.run", e);
		}

	}
}

class RecoverPassWordAndLoginByEmailHandler implements Runnable {

	private SmtpSettings smtp;
	private String userName, passWord, smtpServer, smtpPort, smtpUsername,
			smtpPassword, csvEmailTargets, fromAddress;
	boolean smtpUseSsl;
	File[] files;
	IActionListener helper;

	public RecoverPassWordAndLoginByEmailHandler(String userName,
			String passWord, IActionListener helper, Context context) {
		this.helper = helper;
		this.userName = userName;
		this.passWord = passWord;
		smtp = new SmtpSettings(context);
		this.smtpServer = smtp.getSmtpServer();
		this.smtpPort = smtp.getSmtpPort();
		this.smtpPassword = smtp.getStmpPassword();
		this.smtpUsername = smtp.getSmtpUsername();
		this.smtpUseSsl = smtp.isUseSsl();
		this.csvEmailTargets = smtp.getSmtpFrom();
		this.fromAddress = smtp.getSmtpFrom();
	}

	public void run() {
		try {

			Mail email = new Mail(smtpUsername, smtpPassword);

			String[] toArr = csvEmailTargets.split(",");
			email.setTo(toArr);

			if (fromAddress != null && fromAddress.length() > 0) {
				email.setFrom(fromAddress);
			} else {
				email.setFrom(smtpUsername);
			}

			email.setSubject("JoTracker :: Login and Password Recovery at "
					+ Utilities.GetReadableDateTime(new Date()));
			StringBuffer message = new StringBuffer();
			message.append("Hello,\n\n");
			message.append("This is an automated email, please do not respond.\n\n");
			message.append("Your login is: ");
			message.append(userName + "\n");
			message.append("Your password is: ");
			message.append(passWord + "\n\n");
			message.append("Regards,\n\n");
			message.append("JOTracker\n");
			message.append("www.jotracker.com\n");
			message.append("jo-tracker@hotmail.com\n");
			email.setBody(message.toString());

			email.setPort(smtpPort);
			email.setSecurePort(smtpPort);
			email.setSmtpHost(smtpServer);
			email.setSsl(smtpUseSsl);
			email.setDebuggable(true);

			// m.addAttachment("exel.xls", DEFAULT_STORAGE_LOCATION);
			Utilities.LogInfo("Sending email...");
			if (email.send()) {
				helper.OnComplete();
			} else {
				helper.OnFailure();
			}
		} catch (Exception e) {
			helper.OnFailure();
			Utilities.LogError("AutoSendHandler.run", e);
		}

	}
}

class SendDataNewTrackerHandler implements Runnable {

	private SmtpSettings smtp;
	private String userName, userEmail, userCountry, userDeviceIMEI,
			userDeviceNumber, smtpServer, smtpPort, smtpUsername, smtpPassword,
			csvEmailTargets, fromAddress;
	boolean smtpUseSsl;
	File[] files;
	IActionListener helper;

	public SendDataNewTrackerHandler(String userName, String userEmail,
			String userCountry, String userDeviceIMEI, String userDeviceNumber,
			String csvEmailTargets, IActionListener helper, Context context) {
		this.helper = helper;
		this.userName = userName;
		this.userEmail = userEmail;
		this.userCountry = userCountry;
		this.userDeviceIMEI = userDeviceIMEI;
		this.userDeviceNumber = userDeviceNumber;
		smtp = new SmtpSettings(context);
		this.smtpServer = smtp.getSmtpServer();
		this.smtpPort = smtp.getSmtpPort();
		this.smtpPassword = smtp.getStmpPassword();
		this.smtpUsername = smtp.getSmtpUsername();
		this.smtpUseSsl = smtp.isUseSsl();
		this.csvEmailTargets = csvEmailTargets;
		this.fromAddress = smtp.getSmtpFrom();
	}

	public void run() {
		try {

			Mail email = new Mail(smtpUsername, smtpPassword);

			String[] toArr = csvEmailTargets.split(",");
			email.setTo(toArr);

			if (fromAddress != null && fromAddress.length() > 0) {
				email.setFrom(fromAddress);
			} else {
				email.setFrom(smtpUsername);
			}

			email.setSubject("JoTracker :: Register New User at "
					+ Utilities.GetReadableDateTime(new Date()));
			StringBuffer message = new StringBuffer();
			message.append("Hello,\n");
			message.append("Ashraf Khalil, a new user needs to be registered, as the following data.\n\n");
			message.append("Name:: ");
			message.append(userName + "\n");
			message.append("E-mail: ");
			message.append(userEmail + "\n");
			message.append("Coutry: ");
			message.append(userCountry + "\n");
			message.append("Device IMEI: ");
			message.append(userDeviceIMEI + "\n");
			message.append("Device phone number: ");
			message.append(userDeviceNumber + "\n\n");
			message.append("Best regards,\n\n");
			message.append("JOTracker\n");
			message.append("www.jotracker.com\n");
			message.append("jo-tracker@hotmail.com\n");
			email.setBody(message.toString());

			email.setPort(smtpPort);
			email.setSecurePort(smtpPort);
			email.setSmtpHost(smtpServer);
			email.setSsl(smtpUseSsl);
			email.setDebuggable(true);

			Utilities.LogInfo("Sending email...");
			if (email.send()) {
				helper.OnComplete();
			} else {
				helper.OnFailure();
			}
		} catch (Exception e) {
			helper.OnFailure();
			Utilities.LogError("AutoSendHandler.run", e);
		}

	}
}