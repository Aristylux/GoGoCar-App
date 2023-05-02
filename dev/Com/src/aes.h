#include <stdio.h>
#include <stdlib.h>
#include <time.h>
#include <stdint.h>

#define KEY_128_BITS (size_t) 16
#define KEY_192_BITS (size_t) 24
#define KEY_256_BITS (size_t) 32

typedef struct aes_key
{
    uint8_t *key;
    size_t key_size;
} t_aes_key;


t_aes_key* generate_aes_key(size_t key_size);
void print_aes_key(t_aes_key *key);
void free_aes_key(t_aes_key *key);

void aes_encrypt(char *plaintext, t_aes_key *key);
void aes_decrypt(char *ciphertext, t_aes_key *key);