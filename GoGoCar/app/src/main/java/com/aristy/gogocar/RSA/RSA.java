package com.aristy.gogocar.RSA;

import static com.aristy.gogocar.CodesTAG.TAG_RSA;

import android.util.Log;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Random;

public class RSA {

    private RSAKeys rsaKeys;
    private PublicKey modulePublicKey;

    // constructor
    public RSA() {

    }

    public RSAKeys getRsaKeys() {
        return rsaKeys;
    }

    public byte [] getBytePublicKey(){
        return rsaKeys.publicKey.bytePublicKey;
    }

    public void setModulePublicKey(PublicKey modulePublicKey){
        this.modulePublicKey = modulePublicKey;
    }

    /**
     * Method to generate the RSA public and private keys
     */
    public void generateRSAKeys() {
        // Generate two random prime numbers
        long p = generatePrime();
        long q = generatePrime();

        // Calculate the product of the two primes (modulus), N = p * q.
        long N = p * q;

        // Calculate the totient of N, which is given by φ(N) = (p-1) * (q-1).
        long phi = (p - 1) * (q - 1);

        // Choose a random number e such that 1 < e < phi and gcd(e, phi) = 1
        long e, gcdVal;
        do {
            e = (long) (Math.random() * (phi - 2)) + 2; // Generate a random number between 2 and phi-1
            gcdVal = gcd(e, phi);
        } while (gcdVal != 1);

        // Calculate the modular inverse of e modulo φ(N), denoted as d, such that d * e
        // ≡ 1 (mod φ(N)).
        long d = modInverse(e, phi);

        rsaKeys = new RSAKeys(N, e, d);
        rsaKeys.setBytesPublicKey(publicKeyToBytes());
    }

    /**
     * Encrypt text (ciphertext = (plaintext ^ e) mod N)
     * @param plainText text
     * @return array of long (ciphertext)
     */
    public long[] encrypt(String plainText) {
        long[] ciphertext = new long[plainText.length()];
        for (int i = 0; i < plainText.length(); i++) {
            ciphertext[i] = modExp((int) plainText.charAt(i), this.modulePublicKey.e, this.modulePublicKey.N);
        }
        return ciphertext;
    }

    /**
     * Encrypt text,
     * @param plainText in bytes
     * @return array of long (ciphertext)
     */
    public long[] encrypt(byte[] plainText) {
        long[] ciphertext = new long[plainText.length];
        for (int i = 0; i < plainText.length; i++) {
            ciphertext[i] = modExp((int) plainText[i], this.modulePublicKey.e, this.modulePublicKey.N);
        }
        return ciphertext;
    }

    /**
     * Method to decrypt a message using RSA
     * @param ciphertext array of long
     * @return text
     */
    public String decrypt(long[] ciphertext) {
        StringBuilder plaintext = new StringBuilder();
        for (long l : ciphertext) {
            char ch = (char) modExp(l, this.rsaKeys.privateKey.d, this.rsaKeys.privateKey.N);
            plaintext.append(ch);
        }
        return plaintext.toString();
    }

    // ---- ----


    /**
     * Parse a string text to public key
     * @param publicKeyString public key in hexa string
     * @return public key object
     */
    public PublicKey parsePublicKey(String publicKeyString) {
        byte[] bytes = parseToBytes(formatToHex(publicKeyString));
        ByteBuffer buffer = ByteBuffer.wrap(bytes);

        PublicKey publicKey = new PublicKey();
        if (bytes.length == 16) {
            publicKey.N = buffer.getLong();
            publicKey.e = buffer.getLong();
        } else if (bytes.length == 8) {
            publicKey.N = buffer.getInt();
            publicKey.e = buffer.getInt();
        } else {
            Log.e(TAG_RSA, "parsePublicKey: Invalid input string");
        }
        return publicKey;
    }

    /**
     * Convert a public key to an array of bytes
     * @return array of bytes
     */
    private byte[] publicKeyToBytes(){
        byte[] byteArray = new byte[16]; // 2 uint64_t values = 16 bytes
        ByteBuffer buffer = ByteBuffer.wrap(byteArray);
        buffer.putLong(rsaKeys.publicKey.N);
        buffer.putLong(rsaKeys.publicKey.e);
        return byteArray;
    }

    // ---- ----

    /**
     * Convert an array of bytes to string
     * @param bytes array of bytes
     * @return string to print
     */
    public static String printBytes(byte[] bytes){
        StringBuilder sb = new StringBuilder(bytes.length * 3 - 1);
        for (int i = 0; i < bytes.length; i++) {
            sb.append(String.format("%02x", bytes[i] & 0xff));
            if (i != bytes.length - 1) sb.append(" ");
        }
        return sb.toString();
    }

    /**
     * Parse a string text to bytes array
     * @param hexString text like "XXXXXX..." ("AABBCC...")
     * @return byte array
     */
    public static byte[] parseToBytes(String hexString){
        String[] hexValues = hexString.split(" ");
        byte[] byteArray = new byte[hexValues.length];

        if (hexString.isEmpty()) return byteArray;

        for (int i = 0; i < hexValues.length; i++) {
            byteArray[i] = (byte) Integer.parseInt(hexValues[i], 16);
        }
        return byteArray;
    }

    private String formatToHex(String hexString){
        Log.d(TAG_RSA, "formatToHex: " + hexString + " : " + hexString.length());

        StringBuilder formattedString = new StringBuilder();
        hexString = hexString.replaceAll("\\s", "");

        if (hexString.length() % 2 == 0) {
            for (int i = 0; i < hexString.length(); i += 2) {
                formattedString.append(hexString.substring(i, i + 2)).append(" ");
            }
            Log.d(TAG_RSA, "formatToHex: " + formattedString);
        } else {
            Log.e(TAG_RSA, "formatToHex: Invalid input string");
        }
        return String.valueOf(formattedString);
    }

    public static byte[] parseToBytes(long[] array){
        byte[] byteArray = new byte[8 * array.length];
        ByteBuffer buffer = ByteBuffer.wrap(byteArray);
        for (long l : array) {
            buffer.putLong(l);
        }
        return byteArray;
    }

    /**
     * Convert an array of 16 bytes to 8 bytes
     * @param array16Bytes array of 16 bytes
     * @return array of 8 bytes
     */
    public static byte[] convertTo8ByteArray(byte[] array16Bytes) {
        int numChunks = array16Bytes.length / 2;
        int subArrayLength = 8;
        int numOfSubArrays = array16Bytes.length / subArrayLength;
        byte[] modified = new byte[numChunks];
        int destPos = 0;
        for (int i = 0; i < numOfSubArrays; i++){
            byte[] values = Arrays.copyOfRange(array16Bytes, 4*((i*2) + 1), 8*(i+1));
            System.arraycopy(values, 0, modified, destPos, values.length);
            destPos += values.length;
        }
        return modified;
    }

    /**
     * Convert an array of 8 bytes to 16 bytes
     * @param array8Bytes array of 8 bytes
     * @return array of 16 bytes
     */
    public static byte[] convertTo16ByteArray(byte[] array8Bytes){
        int numChunks = array8Bytes.length * 2;
        int subArrayLength = 4;
        int numOfSubArrays = array8Bytes.length / subArrayLength;
        byte[] modified = new byte[numChunks];
        int inPos = 0;
        int destPos = subArrayLength;
        for (int i = 0; i < numOfSubArrays; i++){
            byte[] values = Arrays.copyOfRange(array8Bytes, inPos, inPos + subArrayLength);
            System.arraycopy(values, 0, modified, destPos, subArrayLength);
            inPos += subArrayLength;
            destPos += (subArrayLength*2);
        }
        return modified;
    }

    // ---- ----

    /**
     * Method to generate a random prime number
     * @return prime number
     */
    private long generatePrime() {
        long p;
        do {
            // Generate a random number between 10000 and 19999
            p = (long) (Math.random() * 10000) + 10000;
        } while (!isPrime(p));
        return p;
    }

    /**
     * Check number
     * @param n a number
     * @return if the number is prime
     */
    private boolean isPrime(long n) {
        if (n == 2 || n == 3)
            return true;
        if (n < 2 || n % 2 == 0)
            return false;

        for (int i = 0; i < 10; i++) {
            long a = new Random().nextLong() % (n - 3) + 2;
            if (!millerRabin(n, a))
                return false;
        }
        return true;
    }

    /**
     * Perform Miller-Rabin test
     * @param n number
     * @param a random
     * @return probably prime
     */
    private boolean millerRabin(long n, long a) {
        long r = 0, d = n - 1;
        while (d % 2 == 0) {
            r++;
            d /= 2;
        }
        long x = powmod(a, d, n);
        if (x == 1 || x == n - 1)
            return true;

        for (long i = 0; i < r - 1; i++) {
            x = powmod(x, 2, n);
            if (x == n - 1)
                return true;
        }
        return false;
    }

    /**
     * Power modular
     * @param base  base
     * @param exp   exponant
     * @param mod   modular number
     * @return calcul result
     */
    private long powmod(long base, long exp, long mod) {
        long result = 1;

        while (exp > 0) {
            if ((exp & 1) == 1) {
                result = (result * base) % mod;
            }
            exp >>= 1;
            base = (base * base) % mod;
        }
        return result;
    }

    /**
     * Method to find the greatest common divisor of two long
     * @param a number 1
     * @param b number 2
     * @return greatest common divisor
     */
    private long gcd(long a, long b) {
        long temp;
        while (b != 0) {
            temp = b;
            b = a % b;
            a = temp;
        }
        return a;
    }

    /**
     * Method to find the modular inverse of a number
     * @param num number
     * @param mod modular number
     * @return modular inverse
     */
    private long modInverse(long num, long mod) {
        long m0 = mod, t, q;
        long x0 = 0, x1 = 1;

        if (mod == 1)
            return 0;

        while (num > 1) {
            q = num / mod;
            t = mod;
            mod = num % mod;
            num = t;
            t = x0;
            x0 = x1 - q * x0;
            x1 = t;
        }

        if (x1 < 0)
            x1 += m0;

        return x1;
    }

    /**
     * Method to calculate the modular exponentiation of a number
     * @param base  base
     * @param exp   exponent
     * @param mod   modular number
     * @return calcul result
     */
    private long modExp(long base, long exp, long mod) {
        long result = 1;
        while (exp > 0) {
            if (exp % 2 == 1) {
                result = (result * base) % mod;
            }
            base = (base * base) % mod;
            exp = exp / 2;
        }
        return result;
    }

}
