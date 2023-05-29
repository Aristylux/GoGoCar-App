package com.aristy.gogocar.Database;

import androidx.annotation.NonNull;

public class DBModelModule {

    private int id;
    private String name;
    private String macAddress;

    public DBModelModule(int id, String name, String macAddress) {
        this.id = id;
        this.name = name;
        this.macAddress = macAddress;
    }

    public DBModelModule() {
    }

    @NonNull
    @Override
    public String toString() {
        return "DBModelModules{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", macAddress='" + macAddress + '\'' +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }
}
