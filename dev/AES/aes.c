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