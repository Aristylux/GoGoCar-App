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

int main() {
    const size_t key_size = 32;  // 256 bits = 32 bytes

    t_aes_key *key = generate_aes_key(key_size);
    print_aes_key(key);
    free_aes_key(key);
    return 0;
}

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