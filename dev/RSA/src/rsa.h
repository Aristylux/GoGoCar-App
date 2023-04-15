#ifndef _H_RSA_
#define _H_RSA_

#include <stdio.h>

// Use malloc & free
#include <stdlib.h>

// For uint8_t, ...
#include <stdint.h>

typedef struct public_key
{
    uint64_t N;  // modulus
    uint64_t e;  // public exponent
} t_public_key;

typedef struct private_key
{
    uint64_t N;  // modulus
    uint64_t d;  // private exponent
} t_private_key;

typedef struct keys
{
    t_public_key public_key;
    t_private_key private_key;
} t_keys;

t_keys *initialize_keys(void);
void generate_rsa_keys(t_keys* keys);

void print_rsa_keys(t_keys* keys);

void free_keys(t_keys* keys);

#endif