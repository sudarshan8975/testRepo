package com.example.list.androidchart.uitily;


import android.os.AsyncTask;
import android.util.Log;

import com.example.list.androidchart.CryptoClass;
import com.example.list.androidchart.CryptoUtil;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

import javax.crypto.spec.SecretKeySpec;

public class CallSoapWebService extends AsyncTask<Void, Void, String> {

    String retJSONObj;
    public static String jsonObjStr;
    String retVal = "",var5="";
    SecretKeySpec var2;
    protected String doInBackground(Void... arg0) {

        try {
            Log.e("jsonObjStr-","jsonObjStr-"+jsonObjStr);
            String keyStr=CryptoClass.Function2();
            var2=CryptoClass.getKey(keyStr);
            SoapObject request = UitilyInstance.getSoapInstance();
            request.addProperty("value1", CryptoClass.Function5(jsonObjStr, var2));
            request.addProperty("value2", CryptoClass.Function3(keyStr, EncryptionModel.getVar1()));
            request.addProperty("value3", EncryptionModel.getVar3());
            SoapSerializationEnvelope envelope = UitilyInstance.getSerializationInstance();
            envelope.setOutputSoapObject(request);
            HttpTransportSE androidHttpTransport = UitilyInstance.getHttpInstance();
            androidHttpTransport.call(Constant.soap_action, envelope);
            var5 = envelope.bodyIn.toString().trim();
            int i = envelope.bodyIn.toString().trim().indexOf("=");
            var5 = var5.substring(i + 1, var5.length() - 3);
            retJSONObj=CryptoClass.Function6(var5,var2);
            Log.e("response -","jsonObjStr-"+retJSONObj);
        }
        catch (SocketTimeoutException e) {
            e.printStackTrace();

        } catch (ConnectException e) {
            e.printStackTrace();

        } catch (Exception e) {

            e.printStackTrace();
        }
        return retJSONObj;
    }
}
