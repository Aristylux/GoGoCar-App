import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SHAHash {

    /**
     * It is the same domain as the class 'SHAHash.java' from the app.
     */
    public static final String DOMAIN = "com.aristy.gogocar";

    /**
     * Main method
     * @param args
     */
    public static void main(String[] args){
        if (args.length < 1) {
            System.out.println("Need an argument. abord. ");
            System.out.println("Try 'cat --help' for more information.");
            return;
        }
        if (args.length > 2){
            System.out.println("Too many arguments. abord. ");
            System.out.println("Try 'cat --help' for more information.");
            return;
        }
        if (args[0].equals("--help") || args[0].equals("-h")){
            System.out.println("Usage: java SHAHash [TEXT] [DOMAIN]");
            System.out.println("Hash TEXT using SHA-512.\n");
            System.out.println("-h, --help      display this help and exit.\n");
            return;
        }

        String domain = (args.length == 1) ? DOMAIN : args[1];
        String hash = hashPassword(args[0], domain);

        System.out.println("Hashed text: " + hash + " , len " + hash.length());
    }

    /**
     * Hash a text using SHA-512
     * @param password text
     * @param domain domain or salt
     * @return thje hash
     */
    public static String hashPassword(String password, String domain) {
        String pw = password + domain;
        MessageDigest sha;
        byte[] byteData;
        try {
            sha = MessageDigest.getInstance("SHA-512");
            byteData = sha.digest(pw.getBytes(StandardCharsets.UTF_8));
            return convertHex(byteData);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * Convert byte array to String
     * @param byteData bytes
     * @return String
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


}
