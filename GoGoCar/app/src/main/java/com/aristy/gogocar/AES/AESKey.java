package com.aristy.gogocar.AES;

public class AESKey {

    private byte[] key;

    public AESKey(int keySize){
        this.key = new byte[keySize];
    }

    public byte[] getKey() {
        return key;
    }

    public String print(){
        StringBuilder sb = new StringBuilder("AES key: ");
        for (byte b : this.key) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

}
