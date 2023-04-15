#include <stdio.h>
#include "rsa.h"

int main(void) {
    printf("Init\n");
    t_keys *keys = initialize_keys();

    printf("Generate\n");
    generate_rsa_keys(keys);

    printf("Print\n");
    print_rsa_keys(keys);

    printf("Free\n");
    free_keys(keys);

    return 0; // success
}

