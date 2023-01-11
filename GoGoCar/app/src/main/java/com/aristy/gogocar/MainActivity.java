package com.aristy.gogocar;

import static com.aristy.gogocar.CodesTAG.TAG_BT;
import static com.aristy.gogocar.CodesTAG.TAG_BT_CON;
import static com.aristy.gogocar.CodesTAG.TAG_Debug;
import static com.aristy.gogocar.CodesTAG.TAG_SPLASH;
import static com.aristy.gogocar.ConnectionHelper.connectionValid;
import static com.aristy.gogocar.HandlerCodes.BT_REQUEST_ENABLE;
import static com.aristy.gogocar.HandlerCodes.BT_STATE_CONNECTED;
import static com.aristy.gogocar.HandlerCodes.BT_STATE_CONNECTION_FAILED;
import static com.aristy.gogocar.HandlerCodes.BT_STATE_DISCONNECTED;
import static com.aristy.gogocar.HandlerCodes.BT_STATE_DISCOVERING;
import static com.aristy.gogocar.HandlerCodes.BT_STATE_MESSAGE_RECEIVED;
import static com.aristy.gogocar.HandlerCodes.GOTO_HOME_FRAGMENT;
import static com.aristy.gogocar.HandlerCodes.GOTO_LOGIN_FRAGMENT;
import static com.aristy.gogocar.HandlerCodes.STATUS_BAR_COLOR;
import static com.aristy.gogocar.PermissionHelper.REQUEST_ACCESS_COARSE_LOCATION;
import static com.aristy.gogocar.PermissionHelper.checkCoarseLocationPermission;
import static com.aristy.gogocar.SHAHash.DOMAIN;
import static com.aristy.gogocar.SHAHash.hashPassword;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.app.Activity;
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
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.sql.Connection;
import java.sql.SQLException;

public class MainActivity extends AppCompatActivity {

    BluetoothAdapter bluetoothAdapter;
    BluetoothConnection bluetoothConnection;

    Connection SQLConnection;
    UserPreferences userPreferences;

    ActivityResultLauncher<Intent> activityResult;

    Handler [] handlers;

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

        handlers = new Handler[]{fragmentHandler, bluetoothHandler};

        Fragment selectedFragment;
        // If the user is not logged
        if(!isLogged)
            selectedFragment = new FragmentLogin(SQLConnection, userPreferences, handlers);
        else
            selectedFragment = new FragmentApp(SQLConnection, userPreferences, handlers);

        // Set Fragment
        setFragment(selectedFragment, R.anim.from_left, R.anim.to_right);

        // For top bar and navigation bar
        setWindowVersion();

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
        bluetoothConnection.closeConnection();
    }

    // Close connection before destroy app
    @Override
    protected void onDestroy() {
        try {
            // The user leave application, close connection to the server.
            if (connectionValid(SQLConnection)) {
                Log.d(TAG_Debug, "onStop: close SQL connection");
                SQLConnection.close();
            } else {
                Log.e(TAG_Debug, "onStop: ERROR close SQL connection: invalid");
            }
        } catch (SQLException exception) {
            Log.e(TAG_Debug, "onStop: ERROR close SQL connection: ", exception);
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

    String hashMacAddressModule = "e0c6a87b46d582b0d5b5ca19cc5b0ba3d9e3ed79d113ebff9248b2f8ce5affdc52a044bd4dc8c1d70ffdf08256d7b68beff3a4ae6ae2582ad201cf8f4c6d47a9";


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
                    Log.d(TAG_BT, "onReceive: connection.");

                    // Stop discovery
                    bluetoothAdapter.cancelDiscovery();

                    // Connection with the device : Open connection
                    Log.d(TAG_BT_CON, "openConnection: ");
                    bluetoothConnection = new BluetoothConnection(device, bluetoothHandler);
                    bluetoothConnection.start();
                }

            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                Log.d(TAG_BT, "onReceive: scanning bluetooth devices FINISHED");
            } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                Log.d(TAG_BT, "onReceive: scanning bluetooth devices STARTED");
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
                            if (result) Log.d(TAG_BT, "handleMessage: isDiscovering: " + bluetoothAdapter.isDiscovering());
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
                    break;
                case BT_STATE_CONNECTION_FAILED:
                    Log.v(TAG_BT, "BT_STATE_CONNECTION_FAILED");
                    bluetoothConnection.connectionFailed();
                    break;
                case BT_STATE_MESSAGE_RECEIVED:
                    //Log.v(TAG_BT, "BT_STATE_MESSAGE_RECEIVED");
                    bluetoothConnection.messageReceived((String) message.obj);
                    break;
                case BT_STATE_DISCONNECTED:
                    Log.v(TAG_BT, "BT_STATE_DISCONNECTED");
                    bluetoothConnection.connectionFinished();
                    break;
            }
            return true;
        }
    });
    
    // ---- WINDOW settings ----
    public void setWindowVersion(){
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
    }

    public static void setWindowFlag(Activity activity, final int bits) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        winParams.flags &= ~bits;
        win.setAttributes(winParams);
    }

    // ---- FRAGMENTS ----

    /**
     * Move to old fragment from new fragment
     * @param fragment      new fragment
     * @param anim_enter    animation in
     * @param anim_exit     animation out
     */
    public void setFragment(Fragment fragment, int anim_enter, int anim_exit){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(anim_enter, anim_exit);
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }

    Handler fragmentHandler = new Handler(new Handler.Callback() {

        @SuppressLint("MissingPermission")
        @Override
        public boolean handleMessage(@NonNull Message message) {
            switch (message.what){
                case GOTO_HOME_FRAGMENT:
                    setFragment(new FragmentApp(SQLConnection, userPreferences, handlers), R.anim.from_right, R.anim.to_left);
                    break;
                case GOTO_LOGIN_FRAGMENT:
                    setFragment(new FragmentLogin(SQLConnection, userPreferences, handlers), R.anim.from_left, R.anim.to_right);
                    break;
                case STATUS_BAR_COLOR:
                    // Set color background
                    getWindow().setStatusBarColor((Integer) message.obj);
                    break;
            }
            return true;
        }
    });
}