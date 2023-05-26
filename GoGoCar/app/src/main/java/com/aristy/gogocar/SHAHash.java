package com.aristy.gogocar;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class SHAHash {

    public static final String DOMAIN = "com.aristy.gogocar";

    /**
     * hashPassword:
     * Hash password combined with domain in SHA3-512.
     * @param password password
     * @param salt salt
     * @return hash in String (length: 128)
     */
    public static String hashPassword(String password, String salt) {
        String pw = password + salt;
        MessageDigest sha;
        byte[] byteData;
        try {
            // TODO SHA3-256 preferred
            sha = MessageDigest.getInstance("SHA-512"); //SHA3-512 is not working
            byteData = sha.digest(pw.getBytes(StandardCharsets.UTF_8));
            return convertHex(byteData);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * convertHex:
     * Covert byte into hexadecimal String.
     * @param byteData bytes
     * @return data in hexadecimal value
     */
    private static String convertHex(byte[] byteData){
        StringBuilder hexString = new StringBuilder();
        for (byte byteDatum : byteData) {
            String hex = Integer.toHexString(0xff & byteDatum);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    /**
     * Generate Salt
     * @return Salt in string
     */
    public static String generateSalt(){
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);

        return Base64.getEncoder().encodeToString(salt);
    }

}
