#ifndef _H_TEST_
#define _H_TEST_

#include <stdio.h>
#include <stdint.h>
#include <string.h>



void test_mix_columns(void);
void test_gmix_column(void);
void test_rotate(void);
void test_key_expantion(void);

/** **/

void print_hex(const uint8_t *arr, const size_t size, const char *title);

#endif