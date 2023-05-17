package com.aristy.gogocar.RSA;

import android.util.Log;

import java.util.Arrays;

public class RSAHelper {

    public static byte[] publicKey8bytes(){

        RSA rsa = new RSA();
        RSAKeys keys = rsa.generateRSAKeys();

        byte[] publicKeyBytes = rsa.publicKeyToBytes(keys);

        Log.d("GoGoCar_RSA", "publicKey8bytes: 16: " + RSA.printBytes(publicKeyBytes));

        return RSA.convertTo8ByteArray(publicKeyBytes);
    }




}
