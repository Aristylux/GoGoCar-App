package com.aristy.gogocar.Database;

public class Tables {

    /* USER */
    public static final String TABLE_USER = "users";
    public static final String COLUMN_USER_ID = "id";
    public static final String COLUMN_USER_NAME = "name";
    public static final String COLUMN_USER_EMAIL = "email";
    public static final String COLUMN_USER_PHONE_NUMBER = "phone";
    public static final String COLUMN_USER_PASSWORD = "password";
    public static final String COLUMN_USER_SALT = "salt";
    // id person (identity card table) (if null => not approved)

    /* VEHICLE */
    public static final String TABLE_VEHICLE = "vehicles";
    public static final String COLUMN_VEHICLE_ID = "id";
    public static final String COLUMN_VEHICLE_MODEL = "model";
    public static final String COLUMN_VEHICLE_LICENCE_PLATE = "licence_plate";
    public static final String COLUMN_VEHICLE_ADDRESS = "address";
    public static final String COLUMN_VEHICLE_ID_ADDRESS = "id_address";
    public static final String COLUMN_VEHICLE_ID_OWNER = "id_owner";
    public static final String COLUMN_VEHICLE_IS_AVAILABLE = "is_available";
    public static final String COLUMN_VEHICLE_IS_BOOKED = "is_booked";
    public static final String COLUMN_VEHICLE_ID_USER_BOOK = "id_user_book";
    public static final String COLUMN_VEHICLE_ID_MODULE = "id_module";
    // id image

    /* MODULE */
    public static final String TABLE_MODULE = "modules";
    public static final String COLUMN_MODULE_ID = "id";
    public static final String COLUMN_MODULE_NAME = "name";
    public static final String COLUMN_MODULE_MAC_ADDRESS = "mac_address";

    /* CITY */
    public static final String TABLE_CITY = "city";
    public static final String COLUMN_CITY_ID = "id";
    public static final String COLUMN_CITY_NAME = "city_name";
    public static final String COLUMN_CITY_LOCATION = "location";

    /* ADDRESSES */
    public static final String TABLE_ADDRESSES = "addresses";
    public static final String COLUMN_ADDRESS_ID = "id";
    public static final String COLUMN_ADDRESS_STREET = "street_address";
    public static final String COLUMN_ADDRESS_CITY = "city";
    public static final String COLUMN_ADDRESS_STATE = "state";
    public static final String COLUMN_ADDRESS_ZIP_CODE = "zip_code";
    public static final String COLUMN_ADDRESS_LOCATION = "location";

}
