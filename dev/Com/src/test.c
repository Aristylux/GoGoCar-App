#include "test.h"

#include "aes.h"

// Function to test mix_columns
void test_mix_columns(void) {
    
    uint8_t state[BLOCK_SIZE_128_BITS] = {
        0xd4, 0xe0, 0xb8, 0x1e, 
        0xbf, 0xb4, 0x41, 0x27,
        0x5d, 0x52, 0x11, 0x98, 
        0x30, 0xae, 0xf1, 0xe5};

    uint8_t expected_state[BLOCK_SIZE_128_BITS] = {
        0x04, 0xe0, 0x48, 0x28, 
        0x66, 0xcb, 0xf8, 0x06, 
        0x81, 0x19, 0xd3, 0x26, 
        0xe5, 0x9a, 0x7a, 0x4c};
    

    printf("State :\n");
    for (uint8_t i = 1; i < BLOCK_SIZE_128_BITS+1; i++) {
            printf("%2.2x%c", state[i-1], (i%16) ? ' ' : '\n');
    }

    mix_columns(state);

    printf("Result:\n");
    for (uint8_t i = 1; i < BLOCK_SIZE_128_BITS+1; i++) {
            printf("%2.2x%c", state[i-1], (i%16) ? ' ' : '\n');
    }

    printf("Expected:\n");
    for (uint8_t i = 1; i < BLOCK_SIZE_128_BITS+1; i++) {
            printf("%2.2x%c", expected_state[i-1], (i%16) ? ' ' : '\n');
    }

    if (memcmp(state, expected_state, BLOCK_SIZE_128_BITS) == 0) {
        printf("mix_columns function passed the test!\n");
    } else {
        printf("mix_columns function failed the test!\n");
    }
}

void test_gmix_column(void){
    // Source: wikipedia (Rijndael_MixColumns Article)
    uint8_t state[4] = {0xdb, 0x13, 0x53, 0x45};
    uint8_t expected_state[4] = {0x8e, 0x4d, 0xa1, 0xbc};

    gmix_column(state);

    if (memcmp(state, expected_state, 4) == 0) {
        printf("gmix_column function passed the test!\n");
    } else {
        printf("gmix_column function failed the test!\n");
    }
}