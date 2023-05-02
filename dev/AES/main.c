#include "aes.h"

int main(void) {
    const size_t key_size = 32;  // 256 bits = 32 bytes

    t_aes_key *key = generate_aes_key(key_size);
    print_aes_key(key);
    free_aes_key(key);
    return 0;
}