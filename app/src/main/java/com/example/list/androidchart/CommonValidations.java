package com.example.list.androidchart;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * Created by OM on 10-Jul-17.
 */

public class CommonValidations
{
    DialogBox dbs;
    public static boolean validateBcCode(String bcCode)
    {
        boolean flag=true;
        if(bcCode.length()==0)
            flag=false;
        return flag;
    }
    public static boolean validateMemberId(String memberId)
    {
        boolean flag=true;
        if(memberId.length()==0)
            flag=false;
        return flag;
    }

    public static boolean validatePassword(String password)
    {
        boolean flag=true;
        if(password.length()<6 || password.length()>15)
            flag=false;
        //Toast.makeText(act,)
        return flag;
    }

    public static boolean validateMobile(String mobileno)
    {
        boolean flag=true;
        if(mobileno.length()!=13)
            flag=false;
        return flag;
    }
    public static boolean validateOTP(String otp)
    {
        boolean flag=true;
        if(otp.length()!=6)
            flag=false;
        return flag;
    }
    public int chkConnectivity(Activity act)
    {
        //Toast.makeText(act,"chkConnectivity",Toast.LENGTH_SHORT).show();
        int flag=0;
        ConnectivityManager cm = (ConnectivityManager) act.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        try
        {
            NetworkInfo.State state = ni.getState();
            boolean state1 = ni.isAvailable();

            if (state1)
            {
                switch (state)
                {
                    case CONNECTED:
                        if (ni.getType() == ConnectivityManager.TYPE_MOBILE|| ni.getType() == ConnectivityManager.TYPE_WIFI)
                        {}
                        break;
                    case DISCONNECTED:
                        flag = 1;
                       // setAlert(act,act.getString(R.string.alert_106));
                        break;
                    default:
                        flag = 1;
                       // setAlert(act,act.getString(R.string.alert_105));
                        break;
                }
            }
            else
            {
                flag = 1;
                //setAlert(act,act.getString(R.string.alert_105));
            }
        }
        catch (NullPointerException ne)
        {
            Log.i("CheckConnectivity","NullPointerException Exception" + ne);
            flag = 1;
            //setAlert(act,act.getString(R.string.alert_105));
        }
        catch (Exception e)
        {
            Log.e("CheckConnectivity", "Exception" + e);
            flag = 1;
           // setAlert(act,act.getString(R.string.alert_105));
        }
        return flag;
    }
    public void setAlert(Activity act, String str)
    {
        dbs = new DialogBox(act);
        dbs.get_adb().setTitle(act.getString(R.string.app_name));
        dbs.get_adb().setMessage(str);
        dbs.get_adb().setPositiveButton("OK",
                new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1)
                    {
                        arg0.cancel();
                    }
                });
        dbs.get_adb().show();
    }
}
