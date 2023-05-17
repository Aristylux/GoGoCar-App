package com.aristy.gogocar.RSA;

import static com.aristy.gogocar.CodesTAG.TAG_RSA;

import android.util.Log;

public class RSAHelper {

    public static byte[] publicKey8bytes(){

        RSA rsa = new RSA();
        rsa.generateRSAKeys();

        byte[] publicKeyBytes = rsa.publicKeyToBytes();

        Log.d(TAG_RSA, "publicKeyBytes: 16: " + RSA.printBytes(publicKeyBytes));

        return RSA.convertTo8ByteArray(publicKeyBytes);
    }




}
