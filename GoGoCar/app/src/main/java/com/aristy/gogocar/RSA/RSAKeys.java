package com.aristy.gogocar.RSA;

import androidx.annotation.NonNull;

public class RSAKeys {

    public PublicKey publicKey;
    public PrivateKey privateKey;

    public RSAKeys() {
        publicKey = new PublicKey();
        privateKey = new PrivateKey();
    }

    public RSAKeys(long N, long e, long d, byte [] bytePublicKey){
        privateKey = new PrivateKey();
        privateKey.N = N;
        privateKey.d = d;
        publicKey = new PublicKey();
        publicKey.N = N;
        publicKey.e = e;
        publicKey.bytePublicKey = bytePublicKey;
    }

    @NonNull
    public String toString() {
        return "Keys: " + this.publicKey.toString() + " | " + this.privateKey.toString();
    }
}
