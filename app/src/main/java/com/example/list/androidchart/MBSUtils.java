package com.example.list.androidchart;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.format.Formatter;
import android.util.Log;
import android.os.Build;
import androidx.annotation.RequiresApi;
@RequiresApi(api = Build.VERSION_CODES.M)
public class MBSUtils {
	static double Latitude;
	static double Longitude;
	static String loc = "";

	
	public static String get16digitsAccNo(String accountStr) {// get16digitsAccNo

		String branch = "", scheme = "", accNo = "";
		try {// 1
				// 5-301-LO-5899-KADEKAR KAVITA KIRAN
			branch = lPad("" + accountStr.split("-")[0], 3, "0");
			scheme = lPad("" + accountStr.split("-")[1], 6, "0");
			accNo = lPad("" + accountStr.split("-")[3], 7, "0");
		}// 1
		catch (Exception e) {// 1
			System.out.println("Exception in get16digitsAccNo()::::" + e);
		}// 1
		return branch + scheme + accNo;

	}// get16digitsAccNo

	public static String getCustName(String accountStr) {// get16digitsAccNo

		String custName = "";
		try {// 1
				// 5-301-LO-5899-KADEKAR KAVITA KIRAN
			custName = accountStr.split("-")[4];
		}// 1
		catch (Exception e) {// 1
			System.out.println("Exception in getCustName()::::" + e);
		}// 1
		return custName;
	}

	public static String lPad(String str, int noOfChars, String padChar) {// lPad
		String retVal = "";
		try {// 1
			for (int i = 0; i < (noOfChars - str.length()); i++) {// 2
				retVal = retVal + padChar;
			}// 2
			retVal = retVal + str;
		}// 1
		catch (Exception e) {// 1
			System.out.println("Exception in lPad()::::" + e);
		}// 1
		return retVal;
	}// lPad

	public static String rPad(String str, int noOfChars, String padChar) {// lPad
		String retVal = "";
		try {// 1
			retVal = str;
			for (int i = str.length(); i < noOfChars; i++) {// 2
				retVal = retVal + padChar;
			}// 2
		}// 1
		catch (Exception e) {// 1
			System.out.println("Exception in lPad()::::" + e);
		}// 1
		return retVal;
	}// lPad

	@RequiresApi(api = Build.VERSION_CODES.M)
	public static String getImeiNumber(Activity act) {
		String imeiNo="",osVersion="";
		String deviceUniqueIdentifier = null;
	    TelephonyManager tm = (TelephonyManager) act.getSystemService(Context.TELEPHONY_SERVICE);
	    
		try
		{			
			osVersion=Build.VERSION.RELEASE;
			if (osVersion.indexOf(".")>-1)
				osVersion=osVersion.substring(0, osVersion.indexOf("."));
			
			if (Integer.parseInt(osVersion)<5)
				deviceUniqueIdentifier = tm.getDeviceId();
			else if (Integer.parseInt(osVersion)<10)
			{
				deviceUniqueIdentifier = tm.getDeviceId(0);
				if(deviceUniqueIdentifier==null || deviceUniqueIdentifier.length()<15)
		        	deviceUniqueIdentifier = tm.getDeviceId(1);	
			}
			else
		        deviceUniqueIdentifier = Settings.Secure.getString(act.getContentResolver(), Settings.Secure.ANDROID_ID);  
		    
		    imeiNo= deviceUniqueIdentifier;		
		}
		catch(SecurityException e)
		{		
			imeiNo = tm.getDeviceId();		
		}	
		return imeiNo;
	}

	public static boolean validateMobNo(String mobNo) {
		Pattern pattern = Pattern.compile("^[789]\\d{9}$");
		Matcher matcher = pattern.matcher(mobNo);

		if (matcher.matches()) {
			System.out.println("valid");
			return true;
		} else {
			System.out.println("invalid");
			return false;
		}
	}

	public static boolean validateEmail(String email) {
		String EMAIL_REGEX = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
		Boolean b;
		if (email.length() > 0) {
			b = email.matches(EMAIL_REGEX);
			System.out.println(" email: " + email + " :Valid = " + b);
		} else
			b = false;
		return b;
	}

	public static String getAccTypeDesc(String acctype) {
		String str = "";
		if (acctype.equalsIgnoreCase("SB")) {
			str = "Savings";
		} else if (acctype.equalsIgnoreCase("LO")) {
			str = "Loan";
		} else if (acctype.equalsIgnoreCase("RP")) {
			str = "Re-Investment Plan";
		} else if (acctype.equalsIgnoreCase("FD")) {
			str = "Fixed Deposit";
		} else if (acctype.equalsIgnoreCase("CA")) {
			str = "Current Account";
		} else if (acctype.equalsIgnoreCase("PG")) {
			str = "Pigmi";
		} else if (acctype.equalsIgnoreCase("RA")) {
			str = "RD Account";
		}
		if (str.length() == 0) {
			str = acctype;
		}
		return str;
	}


	public static boolean isNumeric(String str) {

		try {
			Double num = Double.parseDouble(str);

		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}

	/*
	 * public static String getMyPhoneNO(Activity act) { String
	 * getSimSerialNumber =""; String getSimNumber ="";
	 * 
	 * try{ TelephonyManager telemamanger = (TelephonyManager)
	 * act.getSystemService(Context.TELEPHONY_SERVICE); getSimSerialNumber =
	 * telemamanger.getSimSerialNumber(); getSimNumber =
	 * telemamanger.getLine1Number();
	 * 
	 * if(getSimNumber.length()==0) { getSimNumber="NOTAVAILABLE"; }
	 * 
	 * } catch(Exception e) { getSimNumber="NOTAVAILABLE";
	 * 
	 * } return getSimNumber; }
	 */

	@SuppressLint("MissingPermission")
	public static String getMyPhoneNO(Activity act) {
		String getSimSerialNumber = "";
		String getSimNumber = "";

		try {
			TelephonyManager telemamanger = (TelephonyManager) act
					.getSystemService(Context.TELEPHONY_SERVICE);
			getSimSerialNumber = telemamanger.getSimSerialNumber();
			getSimNumber = telemamanger.getLine1Number();

			if (getSimSerialNumber.length() == 0) {
				getSimSerialNumber = "NOTAVAILABLE";
			}

		} catch (Exception e) {
			getSimSerialNumber = "NOTAVAILABLE";

		}
		return getSimNumber="9766939498";// getSimSerialNumber;
	}

	public static String getLocalIpAddress() {
		String ip = "", port = "";
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf
						.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						ip = Formatter.formatIpAddress(inetAddress.hashCode());
						Log.e("Pigmi", "***** IP=" + ip);
						return ip;
					}
				}
			}
		} catch (SocketException ex) {
			Log.e("Pigmi", ex.toString());
		}
		return ip;
	}

	@SuppressLint("MissingPermission")
	public static String getLocation(Activity act) {

		LocationManager locationManager = (LocationManager) act
				.getSystemService(Context.LOCATION_SERVICE);

		// Define a listener that responds to location updates
		LocationListener locationListener = new LocationListener() {

			public void onStatusChanged(String provider, int status,
					Bundle extras) {
			}

			public void onProviderEnabled(String provider) {
			}

			public void onProviderDisabled(String provider) {
			}

			@Override
			public void onLocationChanged(Location location) {
				// TODO Auto-generated method stub
				Latitude = location.getLatitude();
				Longitude = location.getLongitude();
			}
		};

		// Register the listener with the Location Manager to receive location
		// updates
		locationManager.requestLocationUpdates(
				LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
		loc = Latitude + "~" + Longitude;
		return loc;
	}

	@SuppressLint("MissingPermission")
	public static String getSimNumber(Activity act) {
		String getSimSerialNumber = "";
		String getSimNumber = "";

		try {
			TelephonyManager telemamanger = (TelephonyManager) act
					.getSystemService(Context.TELEPHONY_SERVICE);
			getSimSerialNumber = telemamanger.getSimSerialNumber();
			getSimNumber = telemamanger.getLine1Number();

			if (getSimNumber.length() == 0) {
				getSimNumber = "NOTAVAILABLE";
			}

		} catch (Exception e) {
			getSimNumber = "NOTAVAILABLE";

		}
		return getSimSerialNumber="1234567890";// getSimNumber;
	}
	public static String getYYYYMMDD() {
		java.util.Date today = new java.util.Date();
		String toDate = lPad("" + (today.getYear() + 1900), 2, "0")
				+ lPad("" + (today.getMonth() + 1), 2, "0")
				+ lPad("" + today.getDate(), 2, "0");
		return toDate;
	}
	
	public static String getHHMISS() {
		java.util.Date today = new java.util.Date();
		String toDate = lPad("" + today.getHours(), 2, "0")
				+ lPad("" + (today.getMinutes()), 2, "0")
				+ lPad("" + (today.getSeconds()), 2, "0");
		return toDate;
	}
	


	public static boolean PasswordCheck(String s)
	{
		String smallCasePattern= ".*[a-z].*";
		String upperCasePattern= ".*[A-Z].*";
		String numberCasePattern= ".*[0-9].*";


		if(!s.matches(smallCasePattern)){
			return false;
		}
		else if(!s.matches(upperCasePattern)){
			return false;
		}
		else if(!s.matches(numberCasePattern)){
			return false;
		}
		else
			return true;


	}

	public static boolean UserNameCheck(String s)
	{
		String smallCasePattern= ".*[a-zA-Z].*";

		String numberCasePattern= ".*[0-9].*";


		if(!s.matches(smallCasePattern)){
			return false;
		}
		else if(!s.matches(numberCasePattern)){
			return false;
		}
		else
			return true;
	}
}
