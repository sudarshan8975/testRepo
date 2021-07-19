package com.example.list.androidchart;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatDelegate;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.list.androidchart.uitily.CallSoapWebService;
import com.example.list.androidchart.uitily.EncryptionModel;
import com.example.list.androidchart.uitily.UitilyInstance;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Map;

import javax.crypto.spec.SecretKeySpec;

public class Login extends AppCompatActivity implements View.OnClickListener, ActivityCompat.OnRequestPermissionsResultCallback {

    private static String NAMESPACE = "";
    private static String URL = "";
    private static String SOAP_ACTION = "";
    private static final String METHOD_NAME = "validateLogin";
    static PrivateKey var1=null;
    static PublicKey var4=null;
    Map<String, Object> keys = null;
    SecretKeySpec var2=null;
    String retVal="",var5="",var3 = "";
    EditText textUid,textPass;
    Button loginButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
       // AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        if (ContextCompat.checkSelfPermission(Login.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(Login.this,Manifest.permission.WRITE_EXTERNAL_STORAGE))
            {

            }
            else
            { ActivityCompat.requestPermissions(Login.this,new String[] { Manifest.permission.READ_PHONE_STATE }, 1);
            }
        }

            textUid = (EditText) findViewById(R.id.textUid);
            textPass = (EditText) findViewById(R.id.textPass);
            loginButton = (Button) findViewById(R.id.loginButton);
            loginButton.setOnClickListener(this);
        try {
            keys = CryptoClass.Function1();
            var1 = (PrivateKey) keys.get("private");
            var4 = (PublicKey) keys.get("public");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onClick(View view) {
        switch(view.getId())
        {
            case R.id.loginButton:
                String uid=textUid.getText().toString();
                String pass=textPass.getText().toString();
                if(uid.length()<=0){
                    Toast.makeText(getApplicationContext(),"Please Enter User Id",Toast.LENGTH_LONG).show();
                }
                else if(pass.length()<=0){
                    Toast.makeText(getApplicationContext(),"Please Enter Passward",Toast.LENGTH_LONG).show();
                }
                else{
                    CommonValidations commonObj=new CommonValidations();
                    if(commonObj.chkConnectivity(Login.this)==0){

                        new CallWSFirst().execute();
                    }

                    else{
                         Toast.makeText(getApplicationContext(),"Please Check Network Connectivity",Toast.LENGTH_LONG).show();
                    }

                }
                break;
        }
    }


    class CallWSFirst extends AsyncTask<Void, Void, Void>
    {
        JSONObject jsonObj = new JSONObject();
        LoadProgressBar loadProBarObj = new LoadProgressBar(Login.this);
        protected void onPreExecute()
        {
            try
            {
                loadProBarObj.show();
                jsonObj.put("IMEINO", MBSUtils.getImeiNumber(Login.this));
                jsonObj.put("PUBLICKEY", new String(Base64.encodeBase64(var4.getEncoded())));
                jsonObj.put("METHODCODE", "85");

            }
            catch (JSONException je)
            {
                je.printStackTrace();
            }
        }
        @Override
        protected Void doInBackground(Void... arg0) {
            // TODO Auto-generated method stub
            String value4 = getString(R.string.namespace);
            String value5 = getString(R.string.soap_action);
            String value6 = getString(R.string.url);
            final String value7 = "callWebservice";
            try {
                SoapObject request = new SoapObject(value4, value7);
                //String val=CryptoClass.Function3(jsonObj.toString(),CryptoClass.getPrivateKey());
                request.addProperty("value1", jsonObj.toString());
                request.addProperty("value2", "NA");
                request.addProperty("value3", "NA");
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                        SoapEnvelope.VER11);
                envelope.setOutputSoapObject(request);
                HttpTransportSE androidHttpTransport = new HttpTransportSE(value6,
                        45000);
                androidHttpTransport.call(value5, envelope);
                var5 = envelope.bodyIn.toString().trim();
                var5 = var5.substring(var5.indexOf("=") + 1,
                        var5.length() - 3);

            }// end try
            catch (Exception e) {
                e.printStackTrace();
                System.out.println("Exception 2");
                System.out.println("Splashpage   Exception" + e);
            }
            return null;
        }

        protected void onPostExecute(Void paramVoid)
        {
            loadProBarObj.dismiss();
            var3 = var5;
            Log.e("strvar","----"+var3);
            try {
                loadProBarObj.dismiss();
                new EncryptionModel(var1,var3);
                CallWebService cws = new CallWebService();
                cws.execute();
            }// try
            catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
    }


    class CallWebService extends AsyncTask<Void, Void, Void>
    {
        LoadProgressBar loadProBarObj = new LoadProgressBar(Login.this);

        String[] xmlTags = { "PARAMS" };
        String[] valuesToEncrypt = new String[1];
        String generatedXML ="";

        boolean isWSCalled = false;

        protected void onPreExecute()
        {
            loadProBarObj.show();
            JSONObject jsonObj=new JSONObject();
            try {
                String uid=textUid.getText().toString();
                String pass=textPass.getText().toString();
                String location="1~1";//MBSUtils.getLocation(Login.this);
                jsonObj.put("LOGINID",uid);
                jsonObj.put("PASSWORD",pass);
                jsonObj.put("IMEI", "123456789012345");//MBSUtils.getImeiNumber(Login.this));
                jsonObj.put("LATITUDE", location.split("~")[0]);
                jsonObj.put("LONGITUDE", location.split("~")[1]);
                jsonObj.put("METHODCODE","1");
            }
            catch (JSONException ex)
            {
                ex.printStackTrace();
            }

            valuesToEncrypt[0] = jsonObj.toString();

            generatedXML = CryptoUtil.generateXML(xmlTags, valuesToEncrypt);
            //System.out.println("&&&&&&&&&& generatedXML " + generatedXML);
        }

        protected Void doInBackground(Void... arg0) {
            String value4 = getString(R.string.namespace);
            String value5 = getString(R.string.soap_action);
            String value6 = getString(R.string.url);
            final String value7 = "callWebservice";
            try {
                String keyStr=CryptoClass.Function2();
                var2=CryptoClass.getKey(keyStr);
                SoapObject request = new SoapObject(value4, value7);

                request.addProperty("value1", CryptoClass.Function5(valuesToEncrypt[0], var2));
                request.addProperty("value2", CryptoClass.Function3(keyStr, var1));
                request.addProperty("value3", var3);
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.setOutputSoapObject(request);
                HttpTransportSE androidHttpTransport = new HttpTransportSE(value6,45000);
                if(androidHttpTransport!=null)
                    System.out.println("=============== androidHttpTransport is not null ");
                else
                    System.out.println("=============== androidHttpTransport is  null ");

                androidHttpTransport.call(value5, envelope);
                var5 = envelope.bodyIn.toString().trim();
                int i = envelope.bodyIn.toString().trim().indexOf("=");
                var5 = var5.substring(i + 1, var5.length() - 3);

            }// end try
            catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }// end doInBackground

        protected void onPostExecute(Void paramVoid)
        {
            loadProBarObj.dismiss();
            //String[] xml_data = CryptoUtil.readXML(retVal,new String[] { "PARAMS" });
            String str=CryptoClass.Function6(var5,var2);
            try{
                JSONObject jobj=new JSONObject(str);
                if(jobj.getString("RESPCD").equalsIgnoreCase("0")){
                    EncryptionModel ss=new EncryptionModel(var1,var3);

                    Bundle b=new Bundle();
                    Intent in=new Intent(Login.this, Dashboard.class);
                    b.putString("USERID",textUid.getText().toString());
                    in.putExtra("VAR1", var1);
                    in.putExtra("VAR3", var3);
                    in.putExtras(b);
                    startActivity(in);
                    finish();
                }
                else if(jobj.getString("RESPCD").equalsIgnoreCase("1")){
                    showAlert(getString(R.string.alertInvalid));
                }
                else{
                    showAlert(getString(R.string.alertProblem));
                }
            }catch(Exception e){
                e.printStackTrace();
            }



        }// end onPostExecute

    }// end CallLoginWebService


   /* class CallWebService extends CallSoapWebService
    {
        LoadProgressBar loadProBarObj = new LoadProgressBar(Login.this);

        protected void onPreExecute()
        {
            loadProBarObj.show();
            JSONObject jsonObj=new JSONObject();
            try {
                String uid=textUid.getText().toString();
                String pass=textPass.getText().toString();
                String location="1~1";//MBSUtils.getLocation(Login.this);
                jsonObj.put("LOGINID",uid);
                jsonObj.put("PASSWORD",pass);
                jsonObj.put("IMEI", "123456789012345");//MBSUtils.getImeiNumber(Login.this));
                jsonObj.put("LATITUDE", location.split("~")[0]);
                jsonObj.put("LONGITUDE", location.split("~")[1]);
                jsonObj.put("METHODCODE","1");
                CallSoapWebService.jsonObjStr=jsonObj.toString();
            }
            catch (JSONException ex)
            {
                ex.printStackTrace();
            }

        }

        protected void onPostExecute(String str)
        {
            loadProBarObj.dismiss();
            try{
                JSONObject jobj=new JSONObject(str);
                if(jobj.getString("RESPCD").equalsIgnoreCase("0")){
                    EncryptionModel ss=new EncryptionModel(var1,var3);

                    Bundle b=new Bundle();
                    Intent in=new Intent(Login.this, Dashboard.class);
                    b.putString("USERID",textUid.getText().toString());
                    in.putExtras(b);
                    startActivity(in);
                    finish();
                }
                else if(jobj.getString("RESPCD").equalsIgnoreCase("1")){
                    showAlert(getString(R.string.alertInvalid));
                }
                else{
                    showAlert(getString(R.string.alertProblem));
                }
            }catch(Exception e){
                e.printStackTrace();
            }



        }// end onPostExecute

    }// end CallLoginWebService

*/
    public void showAlert(final String str) {
        ErrorDialogClass alert = new ErrorDialogClass(this, "" + str) {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.btn_ok:
                            this.dismiss();
                        break;
                    default:
                        break;
                }
                dismiss();
            }
        };
        alert.show();
    }



    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults)
    {
        switch (requestCode)
        {
            case 1: {

                if (permissions[0]
                        .equalsIgnoreCase(Manifest.permission.READ_PHONE_STATE))
                {
                    if (ContextCompat.checkSelfPermission( Login.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                        // Should we show an explanation?
                        if (ActivityCompat.shouldShowRequestPermissionRationale(
                                Login.this, Manifest.permission.WRITE_EXTERNAL_STORAGE))
                        {
                        }
                        else
                        {

                            ActivityCompat
                                    .requestPermissions(
                                            Login.this,
                                            new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE },
                                            1);
                        }
                    }
                } else if (permissions[0]
                        .equalsIgnoreCase(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    if (ContextCompat.checkSelfPermission( Login.this,
                            Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {

                        // Should we show an explanation?
                        if (ActivityCompat.shouldShowRequestPermissionRationale(
                                Login.this, Manifest.permission.READ_CONTACTS)) {
                        } else {

                            ActivityCompat
                                    .requestPermissions(
                                            Login.this,
                                            new String[] { Manifest.permission.READ_CONTACTS },
                                            1);
                        }
                    }
                } else if (permissions[0]
                        .equalsIgnoreCase(Manifest.permission.READ_CONTACTS)) {
                    if (ContextCompat.checkSelfPermission( Login.this,
                            Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {

                        // Should we show an explanation?
                        if (ActivityCompat.shouldShowRequestPermissionRationale(
                                Login.this, Manifest.permission.RECEIVE_SMS)) {
                        } else {

                            ActivityCompat
                                    .requestPermissions(
                                            Login.this,
                                            new String[] { Manifest.permission.RECEIVE_SMS },
                                            1);
                        }
                    }
                } else if (permissions[0]
                        .equalsIgnoreCase(Manifest.permission.RECEIVE_SMS)) {
                    if (ContextCompat.checkSelfPermission( Login.this,
                            Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                        // Should we show an explanation?
                        if (ActivityCompat.shouldShowRequestPermissionRationale(
                                Login.this,
                                Manifest.permission.ACCESS_FINE_LOCATION)) {
                        } else {

                            ActivityCompat
                                    .requestPermissions(
                                            Login.this,
                                            new String[] { Manifest.permission.ACCESS_FINE_LOCATION },
                                            1);
                        }
                    }
                }  else if (permissions[0]
                        .equalsIgnoreCase(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    if (ContextCompat.checkSelfPermission( Login.this,
                            Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                        // Should we show an explanation?
                        if (ActivityCompat.shouldShowRequestPermissionRationale(
                                Login.this, Manifest.permission.CAMERA)) {
                        } else {

                            ActivityCompat
                                    .requestPermissions(
                                            Login.this,
                                            new String[] { Manifest.permission.CAMERA },
                                            1);
                        }
                    }
                }
                else if (permissions[0]
                        .equalsIgnoreCase(Manifest.permission.CAMERA)) {
                    if (ContextCompat.checkSelfPermission( Login.this,
                            Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                        // Should we show an explanation?
                        if (ActivityCompat.shouldShowRequestPermissionRationale(
                                Login.this, Manifest.permission.CAMERA)) {
                        } else {

                            ActivityCompat
                                    .requestPermissions(
                                            Login.this,
                                            new String[] { Manifest.permission.CAMERA },
                                            1);
                        }
                    }
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

}
