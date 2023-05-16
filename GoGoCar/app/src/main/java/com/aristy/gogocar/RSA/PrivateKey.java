package com.aristy.gogocar.RSA;

public class PrivateKey {
    public long N; // modulus
    public long d; // private exponent

    public PrivateKey() {}

    public void print() {
        System.out.println("Private key:");
        System.out.printf("\tModulus  : %d\n", N);
        System.out.printf("\tExponent : %d\n", d);
    }
}
