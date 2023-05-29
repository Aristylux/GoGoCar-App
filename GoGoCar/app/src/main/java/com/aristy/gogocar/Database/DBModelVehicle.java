package com.aristy.gogocar.Database;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

public class DBModelVehicle {

    private int id;
    private String model;
    private String licencePlate;
    private int addressId;
    private int idOwner;    // User Id
    private boolean isAvailable;
    private boolean isBooked;
    private int idUser;
    private int idModule;

    private String ownerName;
    private String codeModule;
    private String address;

    // Constructor
    public DBModelVehicle(int id, String model, String licencePlate, int addressId, int idOwner, boolean isAvailable, boolean isBooked, int idUser, int idModule) {
        this.id = id;
        this.model = model;
        this.licencePlate = licencePlate;
        this.addressId = addressId;
        this.idOwner = idOwner;
        this.isAvailable = isAvailable;
        this.isBooked = isBooked;
        this.idUser = idUser;
        this.idModule = idModule;
    }

    public DBModelVehicle() {
    }

    // toString is necessary for printing the contents of a class object
    @NonNull
    @Override
    public String toString() {
        JSONObject map = new JSONObject();
        try {
            map.put("id", id);
            map.put("name", model);
            map.put("licencePlate", licencePlate);
            map.put("addressId", addressId);
            map.put("idOwner", idOwner);
            map.put("isAvailable", isAvailable);
            map.put("isBooked", isBooked);
            map.put("idUser", idUser);
            map.put("idModule", idModule);
            map.put("ownerName", ownerName);
            map.put("codeModule", codeModule);
            map.put("address", address);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return map.toString();
    }

    // Getters and Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getLicencePlate() {
        return licencePlate;
    }

    public void setLicencePlate(String licencePlate) {
        this.licencePlate = licencePlate;
    }

    public int getAddressID() {
        return addressId;
    }

    public void setAddressID(int addressId) {
        this.addressId = addressId;
    }

    public int getIdOwner() {
        return idOwner;
    }

    public void setIdOwner(int idOwner) {
        this.idOwner = idOwner;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public boolean isBooked() {
        return isBooked;
    }

    public void setBooked(boolean booked) {
        isBooked = booked;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public int getIdModule() {
        return idModule;
    }

    public void setIdModule(int idModule) {
        this.idModule = idModule;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getCodeModule() {
        return codeModule;
    }

    public void setCodeModule(String codeModule) {
        this.codeModule = codeModule;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

}
