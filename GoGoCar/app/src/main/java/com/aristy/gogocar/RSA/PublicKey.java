package com.aristy.gogocar.RSA;

public class PublicKey {
    public long N; // modulus
    public long e; // public exponent

    public PublicKey() {}

    public void print() {
        System.out.printf("Public key:\n");
        System.out.printf("\tModulus  : %d\n", N);
        System.out.printf("\tExponent : %d\n\n", e);
    }
}
