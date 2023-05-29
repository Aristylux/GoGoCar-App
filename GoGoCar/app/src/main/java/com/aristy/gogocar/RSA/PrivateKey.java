package com.aristy.gogocar.RSA;

import androidx.annotation.NonNull;

/**
 * Private key object
 */
public class PrivateKey {
    public long N; // modulus
    public long d; // private exponent

    public PrivateKey() {}

    @NonNull
    public String toString() {
        return "Private key: Modulus: " + this.N + ", Exponent: " + this.d + ".";
    }
}
