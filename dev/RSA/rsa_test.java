import java.nio.ByteBuffer;
import java.util.Random;

public class rsa_test {

    public static void main(String[] args) {

        RSA.PublicKey pb_key;

        if(args.length != 0){
            System.out.println(args[0]);
            
            pb_key = new RSA().parsePublicKey(args[0]);
            pb_key.print();

            RSA.RSAKeys keys = new RSA().new RSAKeys();
            keys.publicKey = pb_key;

            byte[] publicKeyBytes = new RSA().publicKeyToBytes(keys);
            String hexString = new RSA().printBytes(publicKeyBytes);
            System.out.println(hexString);
        }
        
        

        RSA rsa = new RSA();

        // Init
        RSA.RSAKeys keys = rsa.new RSAKeys();

        // Generate
        rsa.generateRSAKeys(keys);

        // Print
        keys.print();


        byte[] publicKeyBytes = rsa.publicKeyToBytes(keys);
        String hexString = rsa.printBytes(publicKeyBytes);
        System.out.println(hexString);

        pb_key = rsa.parsePublicKey(hexString);
        pb_key.print();

        long [] ciphertext = rsa.encrypt("hello", keys.publicKey);

        rsa.printCipher(ciphertext);

        String decrypt = rsa.decrypt(ciphertext, keys.privateKey);

        System.out.print(decrypt);
    }

    public static class RSA {

        public class PublicKey {
            public long N; // modulus
            public long e; // public exponent

            public PublicKey() {}

            public void print() {
                System.out.printf("Public key:\n");
                System.out.printf("\tModulus  : %d\n", N);
                System.out.printf("\tExponent : %d\n\n", e);
            }
        }

        public class PrivateKey {
            public long N; // modulus
            public long d; // private exponent
        }

        public class RSAKeys {
            public PublicKey publicKey;
            public PrivateKey privateKey;

            public RSAKeys() {
                publicKey = new PublicKey();
                privateKey = new PrivateKey();
            }

            public void print() {
                publicKey.print();
                System.out.printf("Private key:\n");
                System.out.printf("\tModulus  : %d\n", privateKey.N);
                System.out.printf("\tExponent : %d\n", privateKey.d);
            }
        }

        // constructor
        public RSA() {

        }

        // Function to generate the RSA public and private keys
        public void generateRSAKeys(RSAKeys keys) {
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

            keys.privateKey.d = d;
            keys.privateKey.N = N;

            keys.publicKey.e = e;
            keys.publicKey.N = N;
        }

        public long[] encrypt(String plainText, PublicKey publicKey) {
            long ciphertext[] = new long[plainText.length()];
            for (int i = 0; i < plainText.length(); i++) {
                ciphertext[i] = modExp((int) plainText.charAt(i), publicKey.e, publicKey.N);
            }
            return ciphertext;
        }

        // Function to decrypt a message using RSA
        public String decrypt(long[] ciphertext, PrivateKey private_key) {
            StringBuilder plaintext = new StringBuilder();
            for (int i = 0; i < ciphertext.length; i++) {
                char ch = (char) modExp(ciphertext[i], private_key.d, private_key.N);
                plaintext.append(ch);
            }
            return plaintext.toString();
        }

        public void printCipher(long[] ciphertext){
            System.out.printf("Ciphertext: '");
            for(int i = 0; i < ciphertext.length; i++) {
                System.out.printf("%d ", ciphertext[i]);
            }
            System.out.printf("'\n");
        }


        // ---- ----

        public byte[] publicKeyToBytes(RSAKeys keys){
            byte[] byteArray = new byte[16]; // 2 uint64_t values = 16 bytes
            ByteBuffer buffer = ByteBuffer.wrap(byteArray);
            buffer.putLong(keys.publicKey.N);
            buffer.putLong(keys.publicKey.e);
            return byteArray;
        }

        public String printBytes(byte[] bytes){
            StringBuilder sb = new StringBuilder(bytes.length * 3 - 1);
            for (int i = 0; i < bytes.length; i++) {
                sb.append(String.format("%02x", bytes[i] & 0xff));
                if (i != bytes.length - 1) {
                    sb.append(" ");
                }
            }
            return sb.toString();
        }

        public PublicKey parsePublicKey(String publicKeyString) {
            String[] hexValues = publicKeyString.split(" ");
            byte[] bytes = new byte[hexValues.length];
            for (int i = 0; i < hexValues.length; i++) {
                bytes[i] = (byte) Integer.parseInt(hexValues[i], 16);
            }
            ByteBuffer buffer = ByteBuffer.wrap(bytes);
            PublicKey publicKey = new RSA().new PublicKey();
            publicKey.N = buffer.getLong();
            publicKey.e = buffer.getLong();
            return publicKey;
        }

        // ---- ----

        // Function to generate a random prime number
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

        private long generatePrime() {
            long p;
            do {
                // Generate a random number between 1000 and 1999
                p = (long) (Math.random() * 1000) + 1000;
            } while (!isPrime(p));
            return p;
        }

        // Function to find the greatest common divisor of two integers
        private long gcd(long a, long b) {
            long temp;
            while (b != 0) {
                temp = b;
                b = a % b;
                a = temp;
            }
            return a;
        }

        // Function to find the modular inverse of a number
        private long modInverse(long a, long m) {
            long m0 = m, t, q;
            long x0 = 0, x1 = 1;

            if (m == 1)
                return 0;

            while (a > 1) {
                q = a / m;
                t = m;
                m = a % m;
                a = t;
                t = x0;
                x0 = x1 - q * x0;
                x1 = t;
            }

            if (x1 < 0)
                x1 += m0;

            return x1;
        }

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

}
