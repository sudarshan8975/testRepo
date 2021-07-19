package com.example.list.androidchart.uitily;

import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class UitilyInstance {

    private static SoapObject soapObject ;
    private static HttpTransportSE httpTransportSE ;
    private static SoapSerializationEnvelope soapSerializationEnvelope;
    private static EncryptionModel encryptionModel;
    public static SoapObject getSoapInstance()
    {
        if (soapObject == null) {
            Log.e("sudarshan","soap null");
            soapObject = new SoapObject(Constant.namespace, Constant.methodName);
        }
        return soapObject;
    }

    public static HttpTransportSE getHttpInstance()
    {
        if (httpTransportSE == null) {
            Log.e("sudarshan","HttpTransportSE null");
            httpTransportSE = new HttpTransportSE(Constant.url,
                    180000);
        }
        return httpTransportSE;
    }

    public static SoapSerializationEnvelope getSerializationInstance()
    {
        if (httpTransportSE == null) {
            Log.e("sudarshan","SoapSerializationEnvelope null");
            soapSerializationEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        }
        return soapSerializationEnvelope;
    }

}
