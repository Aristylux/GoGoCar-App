#include <stdio.h>
#include <stdlib.h>
#include <time.h>
#include <stdint.h>



#define KEY_256_BITS (size_t) 32 

typedef struct aes_key
{
    uint8_t *key;
    size_t key_size;
} t_aes_key;


t_aes_key* generate_aes_key(size_t key_size);
void print_aes_key(t_aes_key *key);
void free_aes_key(t_aes_key *key);