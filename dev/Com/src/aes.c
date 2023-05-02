#include "aes.h"

t_aes_key* generate_aes_key(size_t key_size) {
    t_aes_key *key = (t_aes_key*) malloc(sizeof(t_aes_key));
    key->key_size = key_size;
    key->key = (uint8_t*) malloc(key_size);
    srand(time(NULL));  // Seed the random number generator
    for (size_t i = 0; i < key_size; i++) {
        key->key[i] = rand() % 256;  // Generate a random byte
    }
    return key;
}

void print_aes_key(t_aes_key *key) {
    // Print the key in hexadecimal format
    printf("AES key: ");
    for (size_t i = 0; i < key->key_size; i++) {
        printf("%02x", key->key[i]);
    }
    printf("\n");
}

void free_aes_key(t_aes_key *key){
    free(key->key);
    free(key);
}

/*
 *      10 rounds for 128-bit keys.
 *      12 rounds for 192-bit keys.
 *      14 rounds for 256-bit keys.
 */

void aes_encrypt(char *plaintext, t_aes_key *key){

    // add round key


    // ENCRYPTION ROUND
    // sub bytes

    // shift row

    // mix column

    // add round key


    // LAST ROUND
    // sub bytes

    // shift row

    // add round key
}

void aes_decrypt(char *ciphertext, t_aes_key *key){

    // add round key

    // DECRYPTION ROUND
    // inv shift row

    // inv sub bytes

    // inv mix column

    // add round key

    
    // LAST ROUND
    // inv shift row

    // inv round bytes

    // add round key

}

/**
 * @brief each byte of the state is combined with a byte of the round key using bitwise xor
 * 
 */
void add_round_key(){
    
}

/**
 * @brief a non-linear substitution step where each byte is replaced with another according to a lookup table
 * 
 */
void sub_bytes(){

}

/**
 * @brief a transposition step where the last three rows of the state are shifted cyclically a certain number of steps
 * 
 */
void shift_row(){

}

/**
 * @brief a linear mixing operation which operates on the columns of the state, combining the four bytes in each column.
 * 
 */
void mix_column() {

}