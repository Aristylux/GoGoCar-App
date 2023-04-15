import java.util.Random;

public class rsa {
    // Function to generate a random prime number
    public static boolean isPrime(int n) {
        for (int i = 2; i <= Math.sqrt(n); i++) {
            if (n % i == 0)
                return false;
        }
        return true;
    }

    public static int generatePrime() {
        int p;
        Random rand = new Random();
        do {
            p = rand.nextInt(100) + 1;  // Generate a random number between 1 and 100
        } while (!isPrime(p));
        return p;
    }

    // Function to find the greatest common divisor of two integers
    public static int gcd(int a, int b) {
        int temp;
        while (b != 0) {
            temp = b;
            b = a % b;
            a = temp;
        }
        return a;
    }

    // Function to find the modular inverse of a number
    public static int modInverse(int a, int m) {
        int m0 = m, t, q;
        int x0 = 0, x1 = 1;

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

    // Function to generate the RSA public and private keys
    public static void generateRSAKeys(int[] keys) {
        int p, q, phi, gcdVal;
        Random rand = new Random(); // Seed the random number generator

        // Generate two random prime numbers
        p = generatePrime();
        q = generatePrime();

        // Calculate N (modulus) and phi (totient)
        keys[0] = p * q;
        phi = (p - 1) * (q - 1);

        // Choose a random number e such that 1 < e < phi and gcd(e, phi) = 1
        do {
            keys[1] = rand.nextInt(phi - 2) + 2;  // Generate a random number between 2 and phi-1
            gcdVal = gcd(keys[1], phi);
        } while (gcdVal != 1);

        // Calculate d (private key)
        keys[2] = modInverse(keys[1], phi);
    }

    public static void main(String[] args) {
        int[] keys = new int[3];
        generateRSAKeys(keys);
        System.out.println("N: " + keys[0]);
        System.out.println("e: " + keys[1]);
        System.out.println("d: " + keys[2]);
    }
}
