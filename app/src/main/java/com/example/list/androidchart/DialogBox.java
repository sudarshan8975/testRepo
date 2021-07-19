package com.example.list.androidchart;

import android.app.Activity;
import android.app.AlertDialog;

public class DialogBox 
{

	AlertDialog.Builder adb;
	Activity activity;
	String msg, title;

	public DialogBox(final Activity activity) {
		this.activity = activity;
		adb = new AlertDialog.Builder(activity);
		adb.setTitle("Agency Banking");
		adb.setMessage("Are You Sure To Exit?");
		adb.create();
	}
	
	public AlertDialog.Builder get_adb()
	{
		return adb;
	}
}
