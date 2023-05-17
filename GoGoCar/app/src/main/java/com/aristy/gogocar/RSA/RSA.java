package com.aristy.gogocar.RSA;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Random;

public class RSA {

    // constructor
    public RSA() {

    }

    /**
     * Method to generate the RSA public and private keys
     */
    public RSAKeys generateRSAKeys() {
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
        return new RSAKeys(N, e, d);
    }

    /**
     * Encrypt text (ciphertext = (plaintext ^ e) mod N)
     * @param plainText text
     * @param publicKey public key
     * @return array of long
     */
    public long[] encrypt(String plainText, PublicKey publicKey) {
        long[] ciphertext = new long[plainText.length()];
        for (int i = 0; i < plainText.length(); i++) {
            ciphertext[i] = modExp((int) plainText.charAt(i), publicKey.e, publicKey.N);
        }
        return ciphertext;
    }

    /**
     * Method to decrypt a message using RSA
     * @param ciphertext array of long
     * @param private_key private key
     * @return text
     */
    public String decrypt(long[] ciphertext, PrivateKey private_key) {
        StringBuilder plaintext = new StringBuilder();
        for (int i = 0; i < ciphertext.length; i++) {
            char ch = (char) modExp(ciphertext[i], private_key.d, private_key.N);
            plaintext.append(ch);
        }
        return plaintext.toString();
    }

    /**
     * Print the cipher text
     * @param ciphertext array of long
     */
    public void printCipher(long[] ciphertext){
        System.out.print("Ciphertext: '");
        for (long l : ciphertext) {
            System.out.printf("%d ", l);
        }
        System.out.print("'\n");
    }


    // ---- ----


    /**
     * Parse a string text to public key
     * @param publicKeyString public key in hexa string
     * @return public key object
     */
    public PublicKey parsePublicKey(String publicKeyString) {
        byte[] bytes = parseToBytes(publicKeyString);
        ByteBuffer buffer = ByteBuffer.wrap(bytes);

        PublicKey publicKey = new PublicKey();
        if (bytes.length == 16) {
            publicKey.N = buffer.getLong();
            publicKey.e = buffer.getLong();
        } else if (bytes.length == 8) {
            publicKey.N = buffer.getInt();
            publicKey.e = buffer.getInt();
        } else {
            throw new IllegalArgumentException("Invalid input string");
        }
        return publicKey;
    }

    /**
     * Convert a public key to an arry of bytes
     * @param keys RSA keys (which contain a public key)
     * @return array of bytes
     */
    public byte[] publicKeyToBytes(RSAKeys keys){
        byte[] byteArray = new byte[16]; // 2 uint64_t values = 16 bytes
        ByteBuffer buffer = ByteBuffer.wrap(byteArray);
        buffer.putLong(keys.publicKey.N);
        buffer.putLong(keys.publicKey.e);
        return byteArray;
    }

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
     * @param hexString text like "XX XX XX ...."
     * @return byte array
     */
    public static byte[] parseToBytes(String hexString){
        String[] hexValues = hexString.split(" ");
        byte[] byteArray = new byte[hexValues.length];
        for (int i = 0; i < hexValues.length; i++) {
            byteArray[i] = (byte) Integer.parseInt(hexValues[i], 16);
        }
        return byteArray;
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
            System.out.println(Arrays.toString(values));
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
