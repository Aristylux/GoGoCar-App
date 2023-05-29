package com.aristy.gogocar.RSA;

import androidx.annotation.NonNull;

/**
 * Public key object
 */
public class PublicKey {
    public long N; // modulus
    public long e; // public exponent

    public byte [] bytePublicKey;

    public PublicKey() {}

    @NonNull
    public String toString() {
        return "Public key: Modulus: " + this.N + ", Exponent: " + this.e + ".";
    }
}
