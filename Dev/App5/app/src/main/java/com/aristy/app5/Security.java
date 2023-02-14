package com.aristy.app5;

import android.annotation.SuppressLint;
import android.util.Base64;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class Security {

    private static final String pinKey = "";

    private static final String ALGORITHM = "AES";
    private static final byte[] KEY = "0000110100001000800000805F9B34FB".getBytes();

    public static String getPinKey() {
        return decrypt(pinKey);
    }

    public static String encrypt(String plainText) {
        try {
            Key key = new SecretKeySpec(KEY, ALGORITHM);
            @SuppressLint("GetInstance") Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encryptedText = cipher.doFinal(plainText.getBytes());
            return Base64.encodeToString(encryptedText, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String decrypt(String encryptedText) {
        try {
            Key key = new SecretKeySpec(KEY, ALGORITHM);
            @SuppressLint("GetInstance") Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] decryptedText = cipher.doFinal(Base64.decode(encryptedText, Base64.DEFAULT));
            return new String(decryptedText);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
