#include <stdio.h>
#include <string.h>
#include "rsa.h"

int main(void) {
    printf("Init\n");
    t_keys *keys = initialize_keys();

    printf("Generate\n");
    generate_rsa_keys(keys);

    printf("Print\n");
    print_rsa_keys(keys);

    char *text = "hello";
    uint64_t text_len = strlen(text);
    uint64_t ciphertext[text_len];

    // Print Public key (for sending)
    t_key_bytes* public_key_bytes = rsa_public_key(keys);

    printf("Public key bytes:\n");
    print_bytes(public_key_bytes);

    t_public_key p_k = bytes_to_public_key(public_key_bytes);
    printf("N: %ld e: %ld\n", p_k.N, p_k.e);



    // Encrypt the message
    rsa_encrypt(text, text_len, keys->public_key, ciphertext);

    // Print the ciphertext
    print_ciphertext(ciphertext, text_len);

    // ---- ----
    char decrypted[text_len];
    // Decrypt the ciphertext
    rsa_decrypt(ciphertext, text_len, keys->private_key, decrypted);

    // Print the decrypted message
    printf("Decrypted message: %s\n", decrypted);


    printf("Free\n");
    free_bytes(public_key_bytes);
    free_keys(keys);

    return 0; // success
}

