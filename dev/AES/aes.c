#include <stdio.h>
#include <stdlib.h>
#include <time.h>

#define KEY_256_BITS (size_t) 32 

void generate_aes_key(unsigned char *key, size_t key_size) {
    srand(time(NULL));  // Seed the random number generator
    for (size_t i = 0; i < key_size; i++) {
        key[i] = rand() % 256;  // Generate a random byte
    }
}

int main() {
    const size_t key_size = 32;  // 256 bits = 32 bytes
    unsigned char key[key_size];

    generate_aes_key(key, key_size);

    // Print the key in hexadecimal format
    printf("AES key: ");
    for (size_t i = 0; i < key_size; i++) {
        printf("%02x ", key[i]);
    }
    printf("\n");

    return 0;
}
