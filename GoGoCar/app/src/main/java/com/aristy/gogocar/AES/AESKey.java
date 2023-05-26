package com.aristy.gogocar.AES;

import androidx.annotation.NonNull;

public class AESKey {

    private byte[] key;

    public AESKey(int keySize){
        this.key = new byte[keySize];
    }

    public byte[] getKey() {
        return key;
    }

    public int getSize(){
        return  key.length;
    }

    @NonNull
    public String toString(){
        StringBuilder sb = new StringBuilder("AES key: ");
        for (byte b : this.key) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    public String toPrint(){
        StringBuilder sb = new StringBuilder("AES key: ");
        for (byte b : this.key) {
            sb.append(String.format("%02x ", b));
        }
        return sb.toString();
    }

}
