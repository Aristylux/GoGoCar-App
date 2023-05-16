package com.aristy.gogocar.RSA;

public class RSAKeys {

    public PublicKey publicKey;
    public PrivateKey privateKey;

    public RSAKeys() {
        publicKey = new PublicKey();
        privateKey = new PrivateKey();
    }

    public RSAKeys(long N, long e, long d){
        privateKey = new PrivateKey();
        privateKey.N = N;
        privateKey.d = d;
        publicKey = new PublicKey();
        publicKey.N = N;
        publicKey.e = e;
    }

    public void print() {
        publicKey.print();
        privateKey.print();
    }
}
