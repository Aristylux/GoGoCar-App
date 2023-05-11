#include <stdio.h>
#include <string.h>
#include "rsa.h"
#include "aes.h"

void rsa(void);
void aes(void);

int main(void) {
    //rsa();

    aes();
    
    return 0; // success
}

void aes(void){
    t_aes_key *key = generate_aes_key(KEY_256_BITS);
    print_aes_key(key);

    char text[BLOCK_SIZE_128_BITS] = "hello";
    uint8_t cipher[BLOCK_SIZE_128_BITS];

    aes_encrypt(text, key, cipher);

    free_aes_key(key);
}

void rsa(void){
    printf("Generate RSA keys\n");
    t_keys *keys = generate_rsa_keys();

    // Print keys
    print_rsa_keys(keys);

    // Generate public key in byte 
    t_key_bytes* public_key_bytes = public_key_to_bytes(keys);

    // Print key in bytes
    printf("\nPublic key bytes: ");
    print_public_key_bytes(public_key_bytes);

    // Recover key
    t_public_key p_k = bytes_to_public_key(public_key_bytes);
    printf("\nKey recovery via bytes: N: %ld e: %ld\n", p_k.N, p_k.e);


    char *text = "hello";
    uint64_t text_len = strlen(text);
    uint64_t ciphertext[text_len];

    // Encrypt the message
    rsa_encrypt(text, text_len, keys->public_key, ciphertext);

    // Print the ciphertext
    print_ciphertext(ciphertext, text_len);

    // Convert the ciphertext to a byte array
    size_t ciphertext_bytes_len = sizeof(uint64_t) * text_len;
    uint8_t* ciphertext_bytes = uint64_array_to_bytes(ciphertext, ciphertext_bytes_len);
    print_bytes(ciphertext_bytes, ciphertext_bytes_len);

    printf("Decode\n");
    uint64_t *ciphertext_decode = bytes_to_uint64_array(ciphertext_bytes, ciphertext_bytes_len);
    
    printf("Print\n");
    print_ciphertext(ciphertext_decode, text_len);

    free(ciphertext_decode);

    // ---- ----
    char decrypted[text_len];
    // Decrypt the ciphertext
    rsa_decrypt(ciphertext, text_len, keys->private_key, decrypted);

    // Print the decrypted message
    printf("Decrypted message: %s\n", decrypted);


    printf("Free\n");
    free(ciphertext_bytes);
    free_public_key_bytes(public_key_bytes);
    
    free_keys(keys);
}