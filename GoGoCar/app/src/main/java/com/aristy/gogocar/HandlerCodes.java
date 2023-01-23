package com.aristy.gogocar;

public class HandlerCodes {

    // Handler positions
    public static final int FRAGMENT_HANDLER_POS = 0;
    public static final int BLUETOOTH_HANDLER_POS = 1;

    // Fragments codes
    public static final int GOTO_LOGIN_FRAGMENT = 1;
    public static final int GOTO_HOME_FRAGMENT = 2;
    public static final int GOTO_VEHICLE_FRAGMENT = 3;
    public static final int GOTO_ADD_VEHICLE_FRAGMENT = 31;
    public static final int GOTO_EDIT_VEHICLE_FRAGMENT = 32;
    public static final int DATA_SET_VEHICLE = 321;
    public static final int STATUS_BAR_COLOR = 20;

    // Bluetooth codes
    public static final int BT_STATE_DISCOVERING = 1;
    public static final int BT_STATE_CONNECTED = 3;
    public static final int BT_STATE_CONNECTION_FAILED = 4;
    public static final int BT_STATE_MESSAGE_RECEIVED = 5;
    public static final int BT_STATE_DISCONNECTED = 6;

    public static final int BT_REQUEST_ENABLE = 11;

}
