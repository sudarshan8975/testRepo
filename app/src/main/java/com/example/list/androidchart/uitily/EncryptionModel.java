package com.example.list.androidchart.uitily;

import java.security.PrivateKey;

public class EncryptionModel {

     static PrivateKey var1;
     static String var3;

    public EncryptionModel(PrivateKey var1,String var3){
        this.var1 = var1;
        this.var3 = var3;
    }

    public static PrivateKey getVar1() {
        return var1;
    }

    public static String getVar3() {
        return var3;
    }


}
