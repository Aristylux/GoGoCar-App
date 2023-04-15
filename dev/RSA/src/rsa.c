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

    // Choose two distinct prime numbers, p and q.


    // Calculate the product of the two primes (modulus), N = p * q.


    // Calculate the totient of N, which is given by φ(N) = (p-1) * (q-1).


    // Choose an integer e such that 1 < e < φ(N), and e is coprime to φ(N).


    // Calculate the modular inverse of e modulo φ(N), denoted as d, such that d * e ≡ 1 (mod φ(N)).

    keys->private_key.d = 1;
    keys->private_key.N = 2;

    keys->public_key.e = 5;
    keys->public_key.N = 2;

}

// ciphertext = (plaintext ^ e) mod N

// plaintext = (ciphertext ^ d) mod N

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

uint64_t is_prime(uint64_t number){
    for (uint64_t i = 2; i <= sqrt(number); i++){
        if (number%i == 0) return 0;
    }
    return 1;
}

uint64_t generate_prime(void){
    uint64_t p;
    do {
        // Generate a random number between 1 and 100
        p = rand() % 100 + 1;
    } while (!is_prime(p));
    return p;
}