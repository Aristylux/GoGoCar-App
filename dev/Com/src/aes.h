#include <stdio.h>
#include <stdlib.h>
#include <time.h>
#include <stdint.h>
#include <string.h>

#define KEY_128_BITS (size_t) 16
#define KEY_192_BITS (size_t) 24
#define KEY_256_BITS (size_t) 32

#define BLOCK_SIZE_128_BITS (uint8_t) 16 // 128 bits = 16 bytes

typedef struct aes_key
{
    uint8_t *key;
    size_t key_size;
} t_aes_key;

t_aes_key* generate_aes_key(size_t key_size);
void print_aes_key(t_aes_key *key);
void free_aes_key(t_aes_key *key);

void aes_encrypt(char *plaintext, t_aes_key *key, uint8_t *ciphertext);
void aes_decrypt(char *ciphertext, t_aes_key *key);

void add_round_key(uint8_t* state, const uint8_t* round_key);
void sub_bytes(uint8_t* state, const uint8_t* sbox);
void shift_rows(uint8_t *state);
void mix_columns(uint8_t *state);

void key_expansion(const uint8_t* key, uint8_t* expanded_key);