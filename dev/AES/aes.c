#include <stdio.h>
#include <stdlib.h>
#include <time.h>
#include <stdint.h>

#define KEY_256_BITS (size_t) 32 

uint8_t* generate_aes_key(size_t key_size);
void print_aes_key(uint8_t *key, size_t key_size);
void free_aes_key(uint8_t *key);

int main() {
    const size_t key_size = 32;  // 256 bits = 32 bytes
    //uint8_t key[key_size];

    uint8_t *key = generate_aes_key(key_size);
    print_aes_key(key, key_size);
    free_aes_key(key);
    return 0;
}


uint8_t* generate_aes_key(size_t key_size) {
    uint8_t *key = (uint8_t*) malloc(key_size);
    srand(time(NULL));  // Seed the random number generator
    for (size_t i = 0; i < key_size; i++) {
        key[i] = rand() % 256;  // Generate a random byte
    }
    return key;
}

void print_aes_key(uint8_t *key, size_t key_size) {
    // Print the key in hexadecimal format
    printf("AES key: ");
    for (size_t i = 0; i < key_size; i++) {
        printf("%02x", key[i]);
    }
    printf("\n");
}

void free_aes_key(uint8_t *key){
    free(key);
}