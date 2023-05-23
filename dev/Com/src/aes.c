#include "aes.h"

/**
 * @brief S-box values (Substitution box)
 */
const uint8_t sbox[256] = {
    //0     1    2      3     4    5     6     7      8    9     A      B    C     D     E     F
    0x63, 0x7c, 0x77, 0x7b, 0xf2, 0x6b, 0x6f, 0xc5, 0x30, 0x01, 0x67, 0x2b, 0xfe, 0xd7, 0xab, 0x76, //0
    0xca, 0x82, 0xc9, 0x7d, 0xfa, 0x59, 0x47, 0xf0, 0xad, 0xd4, 0xa2, 0xaf, 0x9c, 0xa4, 0x72, 0xc0, //1
    0xb7, 0xfd, 0x93, 0x26, 0x36, 0x3f, 0xf7, 0xcc, 0x34, 0xa5, 0xe5, 0xf1, 0x71, 0xd8, 0x31, 0x15, //2
    0x04, 0xc7, 0x23, 0xc3, 0x18, 0x96, 0x05, 0x9a, 0x07, 0x12, 0x80, 0xe2, 0xeb, 0x27, 0xb2, 0x75, //3
    0x09, 0x83, 0x2c, 0x1a, 0x1b, 0x6e, 0x5a, 0xa0, 0x52, 0x3b, 0xd6, 0xb3, 0x29, 0xe3, 0x2f, 0x84, //4
    0x53, 0xd1, 0x00, 0xed, 0x20, 0xfc, 0xb1, 0x5b, 0x6a, 0xcb, 0xbe, 0x39, 0x4a, 0x4c, 0x58, 0xcf, //5
    0xd0, 0xef, 0xaa, 0xfb, 0x43, 0x4d, 0x33, 0x85, 0x45, 0xf9, 0x02, 0x7f, 0x50, 0x3c, 0x9f, 0xa8, //6
    0x51, 0xa3, 0x40, 0x8f, 0x92, 0x9d, 0x38, 0xf5, 0xbc, 0xb6, 0xda, 0x21, 0x10, 0xff, 0xf3, 0xd2, //7
    0xcd, 0x0c, 0x13, 0xec, 0x5f, 0x97, 0x44, 0x17, 0xc4, 0xa7, 0x7e, 0x3d, 0x64, 0x5d, 0x19, 0x73, //8
    0x60, 0x81, 0x4f, 0xdc, 0x22, 0x2a, 0x90, 0x88, 0x46, 0xee, 0xb8, 0x14, 0xde, 0x5e, 0x0b, 0xdb, //9
    0xe0, 0x32, 0x3a, 0x0a, 0x49, 0x06, 0x24, 0x5c, 0xc2, 0xd3, 0xac, 0x62, 0x91, 0x95, 0xe4, 0x79, //A
    0xe7, 0xc8, 0x37, 0x6d, 0x8d, 0xd5, 0x4e, 0xa9, 0x6c, 0x56, 0xf4, 0xea, 0x65, 0x7a, 0xae, 0x08, //B
    0xba, 0x78, 0x25, 0x2e, 0x1c, 0xa6, 0xb4, 0xc6, 0xe8, 0xdd, 0x74, 0x1f, 0x4b, 0xbd, 0x8b, 0x8a, //C
    0x70, 0x3e, 0xb5, 0x66, 0x48, 0x03, 0xf6, 0x0e, 0x61, 0x35, 0x57, 0xb9, 0x86, 0xc1, 0x1d, 0x9e, //D
    0xe1, 0xf8, 0x98, 0x11, 0x69, 0xd9, 0x8e, 0x94, 0x9b, 0x1e, 0x87, 0xe9, 0xce, 0x55, 0x28, 0xdf, //E
    0x8c, 0xa1, 0x89, 0x0d, 0xbf, 0xe6, 0x42, 0x68, 0x41, 0x99, 0x2d, 0x0f, 0xb0, 0x54, 0xbb, 0x16  //F
};

/**
 * @brief inverted S-box values
 * 
 */
uint8_t sbox_inv[256];

/**
 * @brief Round constant values
 * 
 */
const uint32_t rcon[255] = {
    0x8d, 0x01, 0x02, 0x04, 0x08, 0x10, 0x20, 0x40, 0x80, 0x1b, 0x36, 0x6c, 0xd8,
    0xab, 0x4d, 0x9a, 0x2f, 0x5e, 0xbc, 0x63, 0xc6, 0x97, 0x35, 0x6a, 0xd4, 0xb3,
    0x7d, 0xfa, 0xef, 0xc5, 0x91, 0x39, 0x72, 0xe4, 0xd3, 0xbd, 0x61, 0xc2, 0x9f,
    0x25, 0x4a, 0x94, 0x33, 0x66, 0xcc, 0x83, 0x1d, 0x3a, 0x74, 0xe8, 0xcb, 0x8d,
    0x01, 0x02, 0x04, 0x08, 0x10, 0x20, 0x40, 0x80, 0x1b, 0x36, 0x6c, 0xd8, 0xab,
    0x4d, 0x9a, 0x2f, 0x5e, 0xbc, 0x63, 0xc6, 0x97, 0x35, 0x6a, 0xd4, 0xb3, 0x7d,
    0xfa, 0xef, 0xc5, 0x91, 0x39, 0x72, 0xe4, 0xd3, 0xbd, 0x61, 0xc2, 0x9f, 0x25,
    0x4a, 0x94, 0x33, 0x66, 0xcc, 0x83, 0x1d, 0x3a, 0x74, 0xe8, 0xcb, 0x8d, 0x01,
    0x02, 0x04, 0x08, 0x10, 0x20, 0x40, 0x80, 0x1b, 0x36, 0x6c, 0xd8, 0xab, 0x4d,
    0x9a, 0x2f, 0x5e, 0xbc, 0x63, 0xc6, 0x97, 0x35, 0x6a, 0xd4, 0xb3, 0x7d, 0xfa,
    0xef, 0xc5, 0x91, 0x39, 0x72, 0xe4, 0xd3, 0xbd, 0x61, 0xc2, 0x9f, 0x25, 0x4a,
    0x94, 0x33, 0x66, 0xcc, 0x83, 0x1d, 0x3a, 0x74, 0xe8, 0xcb, 0x8d, 0x01, 0x02,
    0x04, 0x08, 0x10, 0x20, 0x40, 0x80, 0x1b, 0x36, 0x6c, 0xd8, 0xab, 0x4d, 0x9a,
    0x2f, 0x5e, 0xbc, 0x63, 0xc6, 0x97, 0x35, 0x6a, 0xd4, 0xb3, 0x7d, 0xfa, 0xef,
    0xc5, 0x91, 0x39, 0x72, 0xe4, 0xd3, 0xbd, 0x61, 0xc2, 0x9f, 0x25, 0x4a, 0x94,
    0x33, 0x66, 0xcc, 0x83, 0x1d, 0x3a, 0x74, 0xe8, 0xcb, 0x8d, 0x01, 0x02, 0x04,
    0x08, 0x10, 0x20, 0x40, 0x80, 0x1b, 0x36, 0x6c, 0xd8, 0xab, 0x4d, 0x9a, 0x2f,
    0x5e, 0xbc, 0x63, 0xc6, 0x97, 0x35, 0x6a, 0xd4, 0xb3, 0x7d, 0xfa, 0xef, 0xc5,
    0x91, 0x39, 0x72, 0xe4, 0xd3, 0xbd, 0x61, 0xc2, 0x9f, 0x25, 0x4a, 0x94, 0x33,
    0x66, 0xcc, 0x83, 0x1d, 0x3a, 0x74, 0xe8, 0xcb
};


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
    uint8_t block[BLOCK_SIZE_128_BITS] = {0}, expanded_key[240] = {0};

    // Copy the plaintext to the state array
    //memcpy(block, plaintext, BLOCK_SIZE_128_BITS);

    /* iterate over the columns */
    for (uint8_t i = 0; i < 4; i++)
    {
        /* iterate over the rows */
        for (uint8_t j = 0; j < 4; j++)
            block[(i+(j*4))] = plaintext[(i*4)+j];
    }

    // Expand the key into a set of round keys
    key_expansion(key->key, expanded_key);

    main_encrypt(block, expanded_key, 14);
    
    // Copy the final state to the ciphertext buffer
    for (uint8_t i = 0; i < 4; i++)
    {
        /* iterate over the rows */
        for (uint8_t j = 0; j < 4; j++)
            ciphertext[(i*4)+j] = block[(i+(j*4))];
    }

    //memcpy(ciphertext, block, BLOCK_SIZE_128_BITS);
}

void main_encrypt(uint8_t *state, uint8_t *expanded_key, uint8_t nbr_rounds){
    uint8_t round_key[16];

    // Add the initial round key to the state
    create_round_key(expanded_key, round_key);
    add_round_key(state, round_key);

    // Perform the main rounds of encryption
    for (uint8_t round = 1; round < nbr_rounds; round++){
        create_round_key(expanded_key + BLOCK_SIZE_128_BITS*round, round_key);
        sub_bytes(state);
        shift_rows(state);
        mix_columns(state);
        add_round_key(state, round_key);
    }

    // Perform the final round of encryption
    create_round_key(expanded_key + BLOCK_SIZE_128_BITS*nbr_rounds, round_key);
    sub_bytes(state);
    shift_rows(state);
    add_round_key(state, round_key);
}

void aes_decrypt(uint8_t *ciphertext, t_aes_key *key, char *plaintext){
    uint8_t state[BLOCK_SIZE_128_BITS] = {0}, round_key[240] = {0};
    generate_sbox_inv();
    
    // Copy the ciphertext into the state array
    memcpy(state, ciphertext, BLOCK_SIZE_128_BITS);

    key_expansion(key->key, round_key);

    // add round key 
    add_round_key(state, round_key + 4 * 14);

    // DECRYPTION ROUND
    uint8_t round;
    for (round = 14 - 1; round > 0; --round) {
        // inv shift row
        shift_rows_inv(state);
        // inv sub bytes
        sub_bytes_inv(state, sbox_inv);
        // add round key
        add_round_key(state, round_key + 4 * round);
        // inv mix column    
        mix_columns_inv(state);
    }
    
    // LAST ROUND
    // inv shift row
    shift_rows_inv(state);
    // inv round bytes
    sub_bytes_inv(state, sbox_inv);
    // add round key
    add_round_key(state, round_key);

    // Copy the decrypted state into the plaintext array
    memcpy(plaintext, state, BLOCK_SIZE_128_BITS);
}

void create_round_key(uint8_t *expanded_key, uint8_t *round_key) {
    for (uint8_t i = 0; i < 4; i++) {
        for (uint8_t j = 0; j < 4; j++) {
            round_key[(i+(j*4))] = expanded_key[(i*4)+j];
        }
    }
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
 * @param sbox Substitution box
 */
void sub_bytes(uint8_t* state){
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
    for (uint8_t i = 0; i < 4; i++){
        shift_row(state+i*4, i);
    }
}

/**
 * @brief 
 * 
 * @param state 
 * @param nbr 
 */
void shift_row(uint8_t *state, uint8_t nbr){
    uint8_t temp;

    for (uint8_t i = 0; i < nbr; i++){
        temp = state[0];
        for (uint8_t j = 0; j < 3; j++){
            state[j] = state[j+1];
        }
        state[3] = temp;
    }
}

/**
 * @brief a linear mixing operation which operates on the columns of the state, combining the four bytes in each column.
 * 
 * @param state pointer to the current state (16 bytes)
 */
void mix_columns(uint8_t *state){
    uint8_t column[4];
    for (uint8_t i = 0; i < 4; i++){
        // Construct a column by iterating over the 4 rows
        for (uint8_t j = 0; j < 4; j++)
            column[j] = state[(j*4)+i];
        
        mix_column(column);

        // Put the values back into the state
        for (uint8_t j = 0; j < 4; j++)
            state[(j*4)+i] = column[j];
    }
}

void mix_column(uint8_t *column){
    uint8_t cpy[4];
    for(uint8_t i = 0; i < 4; i++) cpy[i] = column[i];

    column[0] = gf_mul(cpy[0],2) ^ gf_mul(cpy[3],1) ^ gf_mul(cpy[2],1) ^ gf_mul(cpy[1],3);
    column[1] = gf_mul(cpy[1],2) ^ gf_mul(cpy[0],1) ^ gf_mul(cpy[3],1) ^ gf_mul(cpy[2],3);
    column[2] = gf_mul(cpy[2],2) ^ gf_mul(cpy[1],1) ^ gf_mul(cpy[0],1) ^ gf_mul(cpy[3],3);
    column[3] = gf_mul(cpy[3],2) ^ gf_mul(cpy[2],1) ^ gf_mul(cpy[1],1) ^ gf_mul(cpy[0],3);
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
    for (uint8_t i = 0; i < 8; i++) {
        if ((b & 1) == 1) p ^= a;
        hbit = (a & 0x80);
        a <<= 1;
        if (hbit == 0x80) a ^= 0x1b;
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


/**
 * @brief Rotate the word eight bits to the left
 * @note Rotate(1d2c3a4f) = 2c3a4f1d
 * 
 * @param word is an 8bits array of size 4 (32 bit)
 */
void rotate(uint8_t *word) {
    uint8_t c = word[0];
    for (uint8_t i = 0; i < 3; i++)
        word[i] = word[i+1];
    word[3] = c;
}

/**
 * @brief key substitution core
 * 
 * @param word word of 4*8bits
 * @param iteration number of iteration
 */
void core(uint8_t *word, uint32_t iteration){

    // Rotate the 32-bit word 8 bits to the left
    rotate(word);

    // Apply S-Box substitution on all 4 parts of the 32-bit word
    for (uint8_t i = 0; i < 4; ++i)
        word[i] = sbox[i];
    
    // XOR the output of the rcon operation with i to the first part (leftmost) only 
    word[0] = word[0]^rcon[iteration];
}

/* * * * * * * *
 * AES DECRYPT *
 * * * * * * * */

void sub_bytes_inv(uint8_t* state, const uint8_t* sbox){
     for (int i = 0; i < BLOCK_SIZE_128_BITS; ++i) {
        state[i] = sbox[state[i]];
    }
}

void shift_rows_inv(uint8_t *state){
    uint8_t temp;

    // Shift second row right by 1 position
    temp = state[1];
    state[1] = state[13];
    state[13] = state[9];
    state[9] = state[5];
    state[5] = temp;

    // Shift third row right by 2 positions
    temp = state[2];
    state[2] = state[10];
    state[10] = temp;
    temp = state[6];
    state[6] = state[14];
    state[14] = temp;

    // Shift fourth row right by 3 positions
    temp = state[3];
    state[3] = state[7];
    state[7] = state[11];
    state[11] = state[15];
    state[15] = temp;
}

/*
void add_round_key_inv(uint8_t* state, const uint8_t* round_key){

}
*/

void mix_columns_inv(uint8_t *state){
    uint8_t tmp[4];

    for (int i = 0; i < 4; ++i) {
        tmp[0] = (uint8_t)(gf_mul(state[i], 0x0e) ^ gf_mul(state[4 + i], 0x0b) ^ gf_mul(state[8 + i], 0x0d) ^ gf_mul(state[12 + i], 0x09));
        tmp[1] = (uint8_t)(gf_mul(state[i], 0x09) ^ gf_mul(state[4 + i], 0x0e) ^ gf_mul(state[8 + i], 0x0b) ^ gf_mul(state[12 + i], 0x0d));
        tmp[2] = (uint8_t)(gf_mul(state[i], 0x0d) ^ gf_mul(state[4 + i], 0x09) ^ gf_mul(state[8 + i], 0x0e) ^ gf_mul(state[12 + i], 0x0b));
        tmp[3] = (uint8_t)(gf_mul(state[i], 0x0b) ^ gf_mul(state[4 + i], 0x0d) ^ gf_mul(state[8 + i], 0x09) ^ gf_mul(state[12 + i], 0x0e));

        state[i] = tmp[0];
        state[4 + i] = tmp[1];
        state[8 + i] = tmp[2];
        state[12 + i] = tmp[3];
    }
}

void generate_sbox_inv(void) {
    for (int i = 0; i < 256; i++) {
        sbox_inv[sbox[i]] = i;
    }
}