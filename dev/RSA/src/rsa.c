#include "rsa.h"

/**
 * @brief allocate memory for keys structure
 * 
 * @return t_keys* key
 */
t_keys* initialize_keys(void){
    t_keys* keys = malloc(sizeof(t_keys)); 
    return keys;
}

void generate_rsa_keys(t_keys* keys){
    srand(time(NULL));  // Seed the random number generator

    // Choose two distinct prime numbers, p and q.
    uint64_t p = generate_prime();
    uint64_t q = generate_prime();

    // Calculate the product of the two primes (modulus), N = p * q.
    uint64_t N = p * q;

    // Calculate the totient of N, which is given by φ(N) = (p-1) * (q-1).
    uint64_t phi = (p - 1) * (q - 1);

    // Choose an integer e such that 1 < e < φ(N), and e is coprime to φ(N).
    uint64_t e, gcd_val;
    do {
        e = rand() % (phi - 2) + 2;  // Generate a random number between 2 and phi-1
        gcd_val = gcd(e, phi);
    } while(gcd_val != 1);

    // Calculate the modular inverse of e modulo φ(N), denoted as d, such that d * e ≡ 1 (mod φ(N)).
    uint64_t d = mod_inverse(e, phi);


    keys->private_key.d = d;
    keys->private_key.N = N;

    keys->public_key.e = e;
    keys->public_key.N = N;

}

// ciphertext = (plaintext ^ e) mod N
// Function to encrypt a message using RSA
void rsa_encrypt(char* plaintext, uint64_t len, t_public_key public_key, uint64_t *ciphertext){
    for(uint64_t i = 0; i < len; i++) {
        ciphertext[i] = mod_exp(plaintext[i], public_key.e, public_key.N);
    }
}


// plaintext = (ciphertext ^ d) mod N
// Function to decrypt a message using RSA
void rsa_decrypt(uint64_t *ciphertext, uint64_t len, t_private_key private_key, char *plaintext){
    for(uint64_t i = 0; i < len; i++) {
        plaintext[i] = mod_exp(ciphertext[i], private_key.d, private_key.N);
    }
}

// Print the ciphertext
void print_ciphertext(uint64_t *ciphertext, uint64_t len){
    printf("Ciphertext: '");
    for(uint64_t i = 0; i < len; i++) {
        printf("%ld ", ciphertext[i]);
    }
    printf("'\n");
}

void print_rsa_keys(t_keys* keys){
    puts("Public key:");
    printf("\tModulus  : %ld\n", keys->public_key.N);
    printf("\tExponent : %ld\n\n", keys->public_key.e);

    puts("Private key:");
    printf("\tModulus  : %ld\n", keys->private_key.N);
    printf("\tExponent : %ld\n", keys->private_key.d);
}

void free_keys(t_keys* keys){
    free(keys);
}

// ---- ----

// not optimised for large numbers
uint64_t is_prime(uint64_t number) {
    /*
    printf("sqrt %f\n", sqrt(number));
    for (uint64_t i = 2; i <= sqrt(number); i++){
        if (number%i == 0) return 0;
    }
    return 1;
    */
    if (number == 2 || number == 3)
        return 1;
    if (number < 2 || number % 2 == 0)
        return 0;

    for (uint64_t i = 0; i < 10; i++) {
        uint64_t a = rand() % (number - 3) + 2;
        if (!miller_rabin(number, a))
            return 0;
    }
    return 1;
}

uint64_t miller_rabin(uint64_t n, uint64_t a) {
    uint64_t r = 0, d = n - 1;
    while (d % 2 == 0) {
        r++;
        d /= 2;
    }
    uint64_t x = powmod(a, d, n);
    if (x == 1 || x == n - 1)
        return 1;

    for (uint64_t i = 0; i < r - 1; i++) {
        x = powmod(x, 2, n);
        if (x == n - 1)
            return 1;
    }

    return 0;
}

uint64_t generate_prime(void) {
    uint64_t p;
    do {
        // Generate a random number between 1 and 100
        //p = rand() % 100 + 1;
        // Generate a random number between 1000 and 1999
        p = rand() % 1000 + 1000;
    } while (!is_prime(p));
    return p;
}

// Function to find the greatest common divisor of two integers
uint64_t gcd(uint64_t a, uint64_t b) {
    uint64_t temp;
    while(b != 0) {
        temp = b;
        b = a % b;
        a = temp;
    }
    return a;
}

// Function to find the modular inverse of a number
uint64_t mod_inverse(uint64_t a, uint64_t m) {
    uint64_t m0 = m, t, q;
    int64_t x0 = 0, x1 = 1;

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

// Function to calculate the modular exponentiation of a number
uint64_t mod_exp(uint64_t base, uint64_t exp, uint64_t mod) {
    uint64_t result = 1;
    while (exp > 0) {
        if (exp % 2 == 1) {
            result = (result * base) % mod;
        }
        base = (base * base) % mod;
        exp = exp / 2;
    }
    return result;
}

//unsigned long long powmod(unsigned long long base, unsigned long long exponent, unsigned long long modulus);
uint64_t powmod(uint64_t b, uint64_t e, uint64_t m) {
    uint64_t result = 1;

    while (e > 0) {
        if (e & 1) {
            result = (result * b) % m;
        }
        e >>= 1;
        b = (b * b) % m;
    }

    return result;
}