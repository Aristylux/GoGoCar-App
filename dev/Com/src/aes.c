#include "aes.h"

// S-box values
const uint8_t sbox[256];/* = {
    
};*/

//Round constant values
const uint32_t rcon[11];/* = {
    
};*/

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

/**
 * @brief 
 * 
 * @param plaintext pointer to a block of plaintext data
 * @param key ointer to the secret key (32 bytes)
 * @param ciphertext return pointer to the encrypted block of data (16 bytes)
 */
void aes_encrypt(char *plaintext, t_aes_key *key, uint8_t *ciphertext){
    uint8_t state[BLOCK_SIZE_128_BITS], expanded_key[240];

    // Copy the plaintext to the state array
    memcpy(state, plaintext, BLOCK_SIZE_128_BITS);

    // Expand the key into a set of round keys
    key_expansion(key->key, expanded_key);

    // Add the initial round key to the state
    add_round_key(state, expanded_key);

    // Perform the main rounds of encryption
    int round;
    for (round = 1; round < 14; round++) {
        // Perform byte substitution
        sub_bytes(state, sbox);
        // Perform row shifting
        shift_rows(state);
        // Perform column mixing
        mix_columns(state);
        // Add the round key to the state
        add_round_key(state, expanded_key + round * BLOCK_SIZE_128_BITS);
    }

    // Perform the final round of encryption
    sub_bytes(state, sbox);
    shift_rows(state);
    add_round_key(state, expanded_key + round * BLOCK_SIZE_128_BITS);

    // Copy the final state to the ciphertext buffer
    memcpy(ciphertext, state, BLOCK_SIZE_128_BITS);
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
 * @param state pointer to the current state (16 bytes)
 * @param round_key pointer to the current round key (16 bytes)
 */
void add_round_key(uint8_t* state, const uint8_t* round_key){
    for (int i = 0; i < BLOCK_SIZE_128_BITS; i++) {
        state[i] ^= round_key[i];
    }
}

/**
 * @brief a non-linear substitution step where each byte is replaced with another according to a lookup table
 * 
 * @param state pointer to the current state (16 bytes)
 * @param sbox 
 */
void sub_bytes(uint8_t* state, const uint8_t* sbox){
    for (int i = 0; i < BLOCK_SIZE_128_BITS; i++) {
        state[i] = sbox[state[i]];
    }
}

/**
 * @brief a transposition step where the last three rows of the state are shifted cyclically a certain number of steps
 * 
 * @param state pointer to the current state (16 bytes)
 */
void shift_rows(uint8_t *state){
    uint8_t temp;

    // Shift the second row by one byte to the left
    temp = state[1];
    state[1] = state[5];
    state[5] = state[9];
    state[9] = state[13];
    state[13] = temp;

    // Shift the third row by two bytes to the left
    temp = state[2];
    state[2] = state[10];
    state[10] = temp;
    temp = state[6];
    state[6] = state[14];
    state[14] = temp;

    // Shift the fourth row by three bytes to the left
    temp = state[15];
    state[15] = state[11];
    state[11] = state[7];
    state[7] = state[3];
    state[3] = temp;
}

/**
 * @brief a linear mixing operation which operates on the columns of the state, combining the four bytes in each column.
 * 
 * @param state pointer to the current state (16 bytes)
 */
void mix_columns(uint8_t *state) {
    uint8_t tmp[16];
    for (int i = 0; i < 4; i++) {
        tmp[4*i] = gf_mul(0x02, state[4*i]) ^ gf_mul(0x03, state[4*i+1]) ^ state[4*i+2] ^ state[4*i+3];
        tmp[4*i+1] = state[4*i] ^ gf_mul(0x02, state[4*i+1]) ^ gf_mul(0x03, state[4*i+2]) ^ state[4*i+3];
        tmp[4*i+2] = state[4*i] ^ state[4*i+1] ^ gf_mul(0x02, state[4*i+2]) ^ gf_mul(0x03, state[4*i+3]);
        tmp[4*i+3] = gf_mul(0x03, state[4*i]) ^ state[4*i+1] ^ state[4*i+2] ^ gf_mul(0x02, state[4*i+3]);
    }
    memcpy(state, tmp, 16);
}

/**
 * @brief Galois Field multiplication function
 * 
 * @param a a byte value
 * @param b a byte value
 * @return uint8_t The result of multiplying a and b in the Galois Field
 */
uint8_t gf_mul(uint8_t a, uint8_t b) {
    uint8_t p = 0;
    uint8_t hbit = 0;
    for (int i = 0; i < 8; i++) {
        if (b & 1) {
            p ^= a;
        }
        hbit = a & 0x80;
        a <<= 1;
        if (hbit) {
            a ^= 0x1b;
        }
        b >>= 1;
    }
    return p;
}

/**
 * @brief Key expansion function
 * Implement AES-256 key expansion
 * This involves applying a series of transformations to the original key
 * to generate a set of round keys that will be used for each round of encryption
 * 
 * @param key pointer to the secret key (32 bytes)
 * @param expanded_key pointer to the expanded set of round keys (240 bytes)
 */
void key_expansion(const uint8_t* key, uint8_t* expanded_key) {
    int i, j, k;
    uint32_t temp;

    // Copy the original key to the first set of round keys
    memcpy(expanded_key, key, 32);

    // Generate the remaining sets of round keys
    for (i = 8, j = 32; i < 60; i += 4, j += 16) {

        // Calculate the next uint32_t of the expanded key
        temp = ((uint32_t*)expanded_key)[i/4-1];
        if (i % 8 == 0) {

            // Apply byte substitution to the uint32_t
            temp = (sbox[(temp >> 16) & 0xff] << 24)
                | (sbox[(temp >>  8) & 0xff] << 16)
                | (sbox[(temp      ) & 0xff] <<  8)
                | (sbox[(temp >> 24) & 0xff]      );

            // Apply the round constant to the first byte
            temp ^= rcon[i/8] << 24;
        }

        // XOR the previous uint32_t with the new uint32_t
        ((uint32_t*)expanded_key)[i/4] = ((uint32_t*)expanded_key)[i/4-8] ^ temp;

        // Copy the next three uint32_t from the previous set of round keys
        for (k = 1; k < 4; k++) {
            ((uint32_t*)expanded_key)[i/4+k] = ((uint32_t*)expanded_key)[i/4+k-1] ^ ((uint32_t*)expanded_key)[i/4+k-8];
        }
    }
}