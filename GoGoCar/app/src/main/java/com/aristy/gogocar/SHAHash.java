package com.aristy.gogocar;

import android.util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SHAHash {

    public String hashPassword(String password) {
        //https://www.codeurjava.com/2016/12/hashage-md5-et-sha-256-en-java.html
        Log.d("SHA", "Start");

        MessageDigest sha;
        byte[] byteData;
        try {
            sha = MessageDigest.getInstance("SHA-256");
            sha.update(password.getBytes());
            byteData = sha.digest();
            Log.d("SHA", "En format hexa : " + convertHex(byteData));
            return convertHex(byteData);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }
    }

    private String convertHex(byte[] byteData){

        StringBuilder hexString = new StringBuilder();
        for (byte byteDatum : byteData) {
            String hex = Integer.toHexString(0xff & byteDatum);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }


}
