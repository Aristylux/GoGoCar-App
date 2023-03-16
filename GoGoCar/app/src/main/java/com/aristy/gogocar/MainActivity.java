package com.aristy.gogocar;

import static com.aristy.gogocar.Animation.ANIMATE_SLIDE_LEFT;
import static com.aristy.gogocar.Animation.ANIMATE_SLIDE_RIGHT;
import static com.aristy.gogocar.Animation.ANIMATE_SLIDE_UP;
import static com.aristy.gogocar.CodesTAG.TAG_BT;
import static com.aristy.gogocar.CodesTAG.TAG_Debug;
import static com.aristy.gogocar.CodesTAG.TAG_FRAGMENT;
import static com.aristy.gogocar.CodesTAG.TAG_SPLASH;
import static com.aristy.gogocar.ConnectionHelper.connectionValid;
import static com.aristy.gogocar.FragmentApp.ARG_FUNCTION_NAME;
import static com.aristy.gogocar.FragmentApp.ARG_FUNCTION_PARAMS;
import static com.aristy.gogocar.HandlerCodes.BT_REQUEST_ENABLE;
import static com.aristy.gogocar.HandlerCodes.BT_REQUEST_STATE;
import static com.aristy.gogocar.HandlerCodes.BT_STATE_CONNECTED;
import static com.aristy.gogocar.HandlerCodes.BT_STATE_CONNECTION_FAILED;
import static com.aristy.gogocar.HandlerCodes.BT_STATE_DISCONNECTED;
import static com.aristy.gogocar.HandlerCodes.BT_STATE_DISCONNECTING;
import static com.aristy.gogocar.HandlerCodes.BT_STATE_DISCOVERING;
import static com.aristy.gogocar.HandlerCodes.BT_STATE_MESSAGE_RECEIVED;
import static com.aristy.gogocar.HandlerCodes.DATA_SET_VEHICLE;
import static com.aristy.gogocar.HandlerCodes.GOTO_ADD_VEHICLE_FRAGMENT;
import static com.aristy.gogocar.HandlerCodes.GOTO_BOOK_VEHICLE_FRAGMENT;
import static com.aristy.gogocar.HandlerCodes.GOTO_DRIVE_FRAGMENT;
import static com.aristy.gogocar.HandlerCodes.GOTO_EDIT_VEHICLE_FRAGMENT;
import static com.aristy.gogocar.HandlerCodes.GOTO_HOME_FRAGMENT;
import static com.aristy.gogocar.HandlerCodes.GOTO_LOGIN_FRAGMENT;
import static com.aristy.gogocar.HandlerCodes.GOTO_VEHICLE_FRAGMENT;
import static com.aristy.gogocar.HandlerCodes.OPEN_SLIDER;
import static com.aristy.gogocar.HandlerCodes.STATUS_BAR_COLOR;
import static com.aristy.gogocar.PermissionHelper.REQUEST_ACCESS_COARSE_LOCATION;
import static com.aristy.gogocar.PermissionHelper.checkCoarseLocationPermission;
import static com.aristy.gogocar.SHAHash.DOMAIN;
import static com.aristy.gogocar.SHAHash.hashPassword;
import static com.aristy.gogocar.Security.getPinKey;
import static com.aristy.gogocar.WebInterface.ADD_VEHICLE;
import static com.aristy.gogocar.WebInterface.BOOK_VEHICLE;
import static com.aristy.gogocar.WebInterface.Boolean.TRUE;
import static com.aristy.gogocar.WebInterface.DRIVE;
import static com.aristy.gogocar.WebInterface.EDIT_VEHICLE;
import static com.aristy.gogocar.WebInterface.ErrorCodes.DRIVING_CONNECTION_DISCONNECTED;
import static com.aristy.gogocar.WebInterface.ErrorCodes.DRIVING_CONNECTION_FAILED;
import static com.aristy.gogocar.WebInterface.ErrorCodes.DRIVING_REQUEST_CAR_NOT_FOUND;
import static com.aristy.gogocar.WebInterface.FunctionNames.DRIVING_REQUEST;
import static com.aristy.gogocar.WebInterface.FunctionNames.SET_VEHICLE_EDIT;
import static com.aristy.gogocar.WebInterface.HOME;
import static com.aristy.gogocar.WebInterface.VEHICLE;
import static com.aristy.gogocar.WindowHelper.setWindowVersion;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.sql.Connection;
import java.sql.SQLException;

public class MainActivity extends AppCompatActivity {

    BluetoothAdapter bluetoothAdapter;
    BluetoothConnection bluetoothConnection;

    Connection SQLConnection;
    UserPreferences userPreferences;

    ActivityResultLauncher<Intent> activityResult;

    Fragment selectedFragment;
    FragmentApp fragmentApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Connect to database (do it in other thread)
        ConnectionHelper connectionHelper = new ConnectionHelper();
        connectionHelper.openConnection();
        SQLConnection = connectionHelper.getConnection();

        // ----
        Intent intent = getIntent();

        boolean isLogged = intent.getBooleanExtra("IS_USER_LOGGED", false);
        userPreferences = intent.getParcelableExtra("USER");
        Log.d(TAG_SPLASH, "onCreate: isLogged=" + isLogged);
        Log.d(TAG_SPLASH, "onCreate: user " + userPreferences.toString());
        // ----

        // If the user is not logged
        if(!isLogged)
            selectedFragment = FragmentLogin.newInstance(userPreferences, fragmentHandler, SQLConnection);
        else {
            //fragmentApp = FragmentApp.newInstance(userPreferences, fragmentHandler, bluetoothHandler, HOME, SQLConnection);
            //selectedFragment = fragmentApp;
            selectedFragment = FragmentNav.newInstance(userPreferences, fragmentHandler, bluetoothHandler, HOME, SQLConnection);
        }

        // Set Fragment
        setFragment(selectedFragment, ANIMATE_SLIDE_LEFT);

        // For top bar and navigation bar
        setWindowVersion(MainActivity.this, getWindow());

        // -----

        // Get bluetooth adapter
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        activityResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), resultCallback);
    }

    ActivityResultCallback<ActivityResult> resultCallback = result -> {
        Log.d(TAG_BT, "onActivityResult: " + result.getData() + "," + result.getResultCode() + ", " + result);
        // if(requestCade == REQUEST_ENABLE_BLUETOOTH){
    };

    // ---- LIFE APP ----

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG_BT, "onResume: registerReceiver");
        // Register a dedicated receiver for some Bluetooth actions
        registerReceiver(devicesFoundReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
        registerReceiver(devicesFoundReceiver, new IntentFilter(BluetoothDevice.ACTION_PAIRING_REQUEST));
        registerReceiver(devicesFoundReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED));
        registerReceiver(devicesFoundReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG_BT, "onPause: unregisterReceiver");
        unregisterReceiver(devicesFoundReceiver);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG_BT, "onStop: closeConnection");
        if (bluetoothConnection != null)
            if (bluetoothConnection.getBluetoothSocket() != null)
                bluetoothConnection.closeConnection();
    }

    // Close connection before destroy app
    @Override
    protected void onDestroy() {
        try {
            // The user leave application, close connection to the server.
            if (connectionValid(SQLConnection)) {
                Log.d(TAG_Debug, "onDestroy: close SQL connection");
                SQLConnection.close();
            } else {
                Log.e(TAG_Debug, "onDestroy: ERROR close SQL connection: invalid");
            }
        } catch (SQLException exception) {
            Log.e(TAG_Debug, "onDestroy: ERROR close SQL connection: ", exception);
            exception.printStackTrace();
        }
        super.onDestroy();
    }

    // ---- PERMISSION ----

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_ACCESS_COARSE_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG_BT, "onRequestPermissionsResult: allowed " + permissions[0]);
            } else {
                Log.d(TAG_BT, "onRequestPermissionsResult: forbidden " + permissions[0]);
            }
        }

    }

    // ---- BLUETOOTH ----
    String lastMacAddress = null;

    // Get into database
    String hashMacAddressModule = "e0c6a87b46d582b0d5b5ca19cc5b0ba3d9e3ed79d113ebff9248b2f8ce5affdc52a044bd4dc8c1d70ffdf08256d7b68beff3a4ae6ae2582ad201cf8f4c6d47a9";
    //String hashMacAddressModule = "29c063acbefc433fa96073ae50cec2d8f31748775a69ef0881c4af55bc86481e42f624407111d9a81acef775844f1532f7f30fcf88e4e6c2511598852dabcca4";

    private final BroadcastReceiver devicesFoundReceiver = new BroadcastReceiver() {
        @SuppressLint("MissingPermission")
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                // If the name is null, this is not a good device
                if (device.getName() == null) return;

                // Get actual mac address
                String macAddress = device.getAddress();

                // If the mac address of the new device is the same as before quit
                if(macAddress.equals(lastMacAddress)) return;

                // Store new mac address
                lastMacAddress = macAddress;

                Log.d(TAG_BT, "onReceive: " + device.getName() + ", " + device.getAddress());

                // Hash address like a password
                String hash = hashPassword(macAddress, DOMAIN);

                // Actual device has the same mac address than our module for that car
                if (hash.equals(hashMacAddressModule)){
                    Log.d(TAG_BT, "onReceive: openConnection.");
                    bluetoothConnection.isConnecting(true);

                    // Stop discovery
                    bluetoothAdapter.cancelDiscovery();

                    // Connection with the device : Open connection
                    bluetoothConnection.openConnection(device, bluetoothHandler);
                    bluetoothConnection.start();
                }

            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                Log.d(TAG_BT, "onReceive: scanning bluetooth devices FINISHED");
                // If a connection is not in progress, we don't find the car, prevent user.
                if(!bluetoothConnection.isConnecting())
                    sendDataToFragment(DRIVING_REQUEST, DRIVING_REQUEST_CAR_NOT_FOUND);
            } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                Log.d(TAG_BT, "onReceive: scanning bluetooth devices STARTED");
            } else if (BluetoothDevice.ACTION_PAIRING_REQUEST.equals(action)){
                Log.d(TAG_BT, "onReceive: ACTION_PAIRING_REQUEST");
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                device.setPin(getPinKey().getBytes());
            }
        }
    };

    Handler bluetoothHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            switch (message.what) {
                case BT_STATE_DISCOVERING:
                    if (bluetoothAdapter != null && bluetoothAdapter.isEnabled()) {
                        if (checkCoarseLocationPermission(MainActivity.this)) {
                            boolean result = bluetoothAdapter.startDiscovery();
                            if (result) {
                                Log.d(TAG_BT, "handleMessage: isDiscovering: " + bluetoothAdapter.isDiscovering());
                                bluetoothConnection = new BluetoothConnection();
                            }
                            else Log.e(TAG_BT, "handleMessage: isDiscovering error");
                        }
                    }
                    break;
                case BT_REQUEST_ENABLE:
                    // Create intent
                    Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);

                    // Launch activity to get result
                    activityResult.launch(enableIntent);
                    break;
                case BT_STATE_CONNECTED:
                    Log.v(TAG_BT, "BT_STATE_CONNECTED");
                    bluetoothConnection.connectionEstablished();
                    //Send pairing success and connection established
                    sendDataToFragment(DRIVING_REQUEST, TRUE);
                    break;
                case BT_STATE_CONNECTION_FAILED:
                    Log.v(TAG_BT, "BT_STATE_CONNECTION_FAILED");
                    bluetoothConnection.connectionFailed();
                    sendDataToFragment(DRIVING_REQUEST, DRIVING_CONNECTION_FAILED);
                    break;
                case BT_STATE_MESSAGE_RECEIVED:
                    Log.v(TAG_BT, "BT_STATE_MESSAGE_RECEIVED");
                    Log.d(TAG_BT, "handleMessage: received: " + message.obj);
                    // TODO (test)
                    // -> bluetoothConnection.messageReceived((String) message.obj);
                    //sendDataToFragment(bluetoothConnection.getMessageFunction(), bluetoothConnection.getMessageParams());
                    break;
                case BT_STATE_DISCONNECTED:
                    Log.v(TAG_BT, "BT_STATE_DISCONNECTED");
                    bluetoothConnection.connectionFinished();
                    sendDataToFragment(DRIVING_REQUEST, DRIVING_CONNECTION_DISCONNECTED);
                    break;
                case BT_STATE_DISCONNECTING:
                    Log.v(TAG_BT, "BT_STATE_DISCONNECTING");
                    bluetoothConnection.closeConnection();
                    break;
                case BT_REQUEST_STATE:
                    if (bluetoothConnection == null || bluetoothConnection.getBluetoothSocket() == null) {
                        sendDataToFragment("setSwitchState", "false");
                        break;
                    }
                    boolean connected = bluetoothConnection.getBluetoothSocket().isConnected();
                    sendDataToFragment("setSwitchState", String.valueOf(connected));
                    break;
            }
            return true;
        }
    });

    // ---- FRAGMENTS ----

    /**
     * Move to old fragment from new fragment
     * @param fragment      new fragment
     * @param animation     animation type
     */
    public void setFragment(Fragment fragment, int animation){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Animation.animate(fragmentTransaction, animation);
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }

    /**
     * Move to old fragment from new fragment and add arguments
     * @param animation     animation type
     * @param link          arguments
     */
    public void setFragment(int animation, String link){
        fragmentApp = FragmentApp.newInstance(userPreferences, fragmentHandler, bluetoothHandler, link, SQLConnection);
        setFragment(fragmentApp, animation);
    }

    /**
     * @param function function name to call in web
     * @param params parameters in that function
     */
    public void sendDataToFragment(String function, String params){
        Bundle args = new Bundle();
        args.putString(ARG_FUNCTION_NAME, function);
        args.putString(ARG_FUNCTION_PARAMS, params);
        fragmentApp.putArguments(args);
    }

    String vehicle;

    Handler fragmentHandler = new Handler(new Handler.Callback() {
        @SuppressLint("MissingPermission")
        @Override
        public boolean handleMessage(@NonNull Message message) {
            switch (message.what){
                case GOTO_LOGIN_FRAGMENT:
                    FragmentLogin fragmentLogin = FragmentLogin.newInstance(userPreferences, fragmentHandler, SQLConnection);
                    setFragment(fragmentLogin, ANIMATE_SLIDE_LEFT);
                    break;
                case GOTO_HOME_FRAGMENT:
                    setFragment(ANIMATE_SLIDE_RIGHT, HOME);
                    break;
                case GOTO_DRIVE_FRAGMENT:
                    FragmentNav fragmentNav =  FragmentNav.newInstance(userPreferences, fragmentHandler, bluetoothHandler, DRIVE, SQLConnection);
                    setFragment(fragmentNav, ANIMATE_SLIDE_LEFT);
                    //setFragment(ANIMATE_SLIDE_LEFT, DRIVE);
                    break;
                case GOTO_BOOK_VEHICLE_FRAGMENT:
                    setFragment(ANIMATE_SLIDE_RIGHT, BOOK_VEHICLE);
                    vehicle = String.valueOf(message.obj);
                    break;
                case GOTO_ADD_VEHICLE_FRAGMENT:
                    setFragment(ANIMATE_SLIDE_UP, ADD_VEHICLE);
                    break;
                case GOTO_EDIT_VEHICLE_FRAGMENT:
                    setFragment(ANIMATE_SLIDE_LEFT, EDIT_VEHICLE);
                    vehicle = String.valueOf(message.obj);
                    break;
                case GOTO_VEHICLE_FRAGMENT:
                    FragmentNav fragmentNav1 =  FragmentNav.newInstance(userPreferences, fragmentHandler, bluetoothHandler, VEHICLE, SQLConnection);
                    setFragment(fragmentNav1, (Integer) message.obj);
                    //setFragment((Integer) message.obj, VEHICLE);
                    break;
                case STATUS_BAR_COLOR:
                    // Set color background
                    getWindow().setStatusBarColor((Integer) message.obj);
                    break;
                case DATA_SET_VEHICLE:
                    sendDataToFragment(SET_VEHICLE_EDIT, vehicle);
                    break;
                case OPEN_SLIDER:
                    // Open second activity (Which is a slider)
                    // Call in : openSlider() -> WIMainScreen class
                    Object[] arg = (Object[]) message.obj;
                    Intent intent;
                    if(arg.length > 2){
                        intent = SliderActivity.newInstance(MainActivity.this, fragmentHandler, userPreferences, String.valueOf(arg[0]), (boolean) arg[1], (String) arg[2]);
                    } else {
                        intent = SliderActivity.newInstance(MainActivity.this, fragmentHandler, userPreferences, String.valueOf(arg[0]), (boolean) arg[1]);
                    }

                    MainActivity.this.startActivity(intent);
                    MainActivity.this.overridePendingTransition(
                            R.anim.animate_slide_left_enter,
                            R.anim.animate_slide_left_exit
                    );
                    break;
                case 100:
                    Log.d(TAG_FRAGMENT, "handleMessage: 100 ");
                    break;
            }
            return true;
        }
    });
}