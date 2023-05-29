package com.aristy.gogocar.Database;

import static com.aristy.gogocar.Database.Tables.COLUMN_ADDRESS_ID;
import static com.aristy.gogocar.Database.Tables.COLUMN_ADDRESS_STREET;
import static com.aristy.gogocar.Database.Tables.COLUMN_USER_EMAIL;
import static com.aristy.gogocar.Database.Tables.COLUMN_USER_ID;
import static com.aristy.gogocar.Database.Tables.COLUMN_USER_NAME;
import static com.aristy.gogocar.Database.Tables.COLUMN_USER_PASSWORD;
import static com.aristy.gogocar.Database.Tables.COLUMN_USER_PHONE_NUMBER;
import static com.aristy.gogocar.Database.Tables.COLUMN_USER_SALT;
import static com.aristy.gogocar.Database.Tables.COLUMN_VEHICLE_ID;
import static com.aristy.gogocar.Database.Tables.COLUMN_VEHICLE_ID_ADDRESS;
import static com.aristy.gogocar.Database.Tables.COLUMN_VEHICLE_ID_MODULE;
import static com.aristy.gogocar.Database.Tables.COLUMN_VEHICLE_ID_OWNER;
import static com.aristy.gogocar.Database.Tables.COLUMN_VEHICLE_ID_USER_BOOK;
import static com.aristy.gogocar.Database.Tables.COLUMN_VEHICLE_IS_AVAILABLE;
import static com.aristy.gogocar.Database.Tables.COLUMN_VEHICLE_IS_BOOKED;
import static com.aristy.gogocar.Database.Tables.COLUMN_VEHICLE_LICENCE_PLATE;
import static com.aristy.gogocar.Database.Tables.COLUMN_VEHICLE_MODEL;
import static com.aristy.gogocar.Database.Tables.TABLE_ADDRESSES;
import static com.aristy.gogocar.Database.Tables.TABLE_USER;
import static com.aristy.gogocar.Database.Tables.TABLE_VEHICLE;

public class Queries {

    public static final String ADD_USER_QUERY = "INSERT INTO " + TABLE_USER +
            "( " + COLUMN_USER_NAME + "," + COLUMN_USER_EMAIL + "," + COLUMN_USER_PHONE_NUMBER + "," + COLUMN_USER_PASSWORD + "," + COLUMN_USER_SALT + ") " +
            "VALUES (?,?,?,?,?)";

    public static final String DELETE_USER_QUERY = "DELETE FROM " + TABLE_USER + " WHERE " + COLUMN_USER_ID + " = ?";

    public static final String ADD_VEHICLE_QUERY = "INSERT INTO " + TABLE_VEHICLE +
            "( " + COLUMN_VEHICLE_MODEL + "," + COLUMN_VEHICLE_LICENCE_PLATE + "," + COLUMN_VEHICLE_ID_ADDRESS + "," + COLUMN_VEHICLE_ID_OWNER + "," + COLUMN_VEHICLE_IS_AVAILABLE + "," + COLUMN_VEHICLE_ID_MODULE + ") " +
            "VALUES (?,?,?,?,?,?)";

    public static final String DELETE_VEHICLE_QUERY = "DELETE FROM " + TABLE_VEHICLE + " WHERE " + COLUMN_VEHICLE_ID + " = ?";

    public static final String UPDATE_VEHICLE_QUERY = "UPDATE " + TABLE_VEHICLE + " SET " +
            COLUMN_VEHICLE_MODEL + " = ?, " + COLUMN_VEHICLE_LICENCE_PLATE + " = ?, " +
            COLUMN_VEHICLE_IS_AVAILABLE + " = ?, " + COLUMN_VEHICLE_ID_MODULE + " = ? " +
            "WHERE " + COLUMN_VEHICLE_ID + " = ?";

    public static  final String UPDATE_VEHICLE_ADDRESS_QUERY = "UPDATE " + TABLE_ADDRESSES + " SET " + COLUMN_ADDRESS_STREET + " = ? " +
            "WHERE " + COLUMN_ADDRESS_ID + " = ?";

    public static final String SET_VEHICLE_BOOKED_QUERY = "UPDATE " + TABLE_VEHICLE + " SET " +
            COLUMN_VEHICLE_ID_USER_BOOK + " = ?, " + COLUMN_VEHICLE_IS_BOOKED + " = ? " +
            "WHERE " + COLUMN_VEHICLE_ID + " = ?";

}
