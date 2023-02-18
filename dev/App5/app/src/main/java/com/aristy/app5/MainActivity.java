package com.aristy.app5;

import static com.aristy.app5.HandlerCodes.BT_REQUEST_ENABLE;
import static com.aristy.app5.HandlerCodes.BT_STATE_CONNECTED;
import static com.aristy.app5.HandlerCodes.BT_STATE_CONNECTION_FAILED;
import static com.aristy.app5.HandlerCodes.BT_STATE_DISCONNECTED;
import static com.aristy.app5.HandlerCodes.BT_STATE_DISCOVERING;
import static com.aristy.app5.HandlerCodes.BT_STATE_MESSAGE_RECEIVED;
import static com.aristy.app5.SHAHash.DOMAIN;
import static com.aristy.app5.SHAHash.hashPassword;
import static com.aristy.app5.Security.encrypt;
import static com.aristy.app5.Security.getPinKey;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.KeyGenerator;

public class MainActivity extends AppCompatActivity {

    BluetoothAdapter bluetoothAdapter;
    ActivityResultLauncher<Intent> activityResult;

    BluetoothConnection bluetoothConnection;
    BluetoothCommunication bluetoothCommunication;

    // Codes
    public static final int REQUEST_ACCESS_COARSE_LOCATION = 1;

    private final static String TAG_BT = "app5_BT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Check permissions
        checkPermission();

        // Get bluetooth adapter
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        activityResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), resultCallback);

        bluetoothHandler.obtainMessage(BT_STATE_DISCOVERING).sendToTarget();
    }

    ActivityResultCallback<ActivityResult> resultCallback = result -> {
        Log.d(TAG_BT, "onActivityResult: " + result.getData() + "," + result.getResultCode() + ", " + result);
        // if(requestCade == REQUEST_ENABLE_BLUETOOTH){
    };

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
        Log.d(TAG_BT, "onStop: close connection");
        closeConnection();
    }

    // ---- PERMISSIONS ----

    public void checkPermission(){
        // Checking if permission is not granted

        if ((checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                && (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            Log.d(TAG_BT, "checkPermission: ask permission");
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        } else {
            Log.d(TAG_BT, "checkPermission: permission already granted.");
        }
    }

    public static boolean checkCoarseLocationPermission(Activity activity) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_ACCESS_COARSE_LOCATION);
            Log.e(TAG_BT, "checkCoarseLocationPermission: forbidden");
            return false;
        } else {
            Log.e(TAG_BT, "checkCoarseLocationPermission: granted");
            return true;
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

                    // Connection with the device

                    // Open connection
                    openConnection(device);
                }

            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                Log.d(TAG_BT, "onReceive: scanning bluetooth devices FINISHED");
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
                    Log.v(TAG_BT, "handleMessage: BT_STATE_DISCOVERING");
                    if (bluetoothAdapter != null && bluetoothAdapter.isEnabled()) {
                        Log.d(TAG_BT, "handleMessage: bluetoothAdapter" + bluetoothAdapter);
                        if (checkCoarseLocationPermission(MainActivity.this)) {
                            boolean result = bluetoothAdapter.startDiscovery();
                            if (result) Log.d(TAG_BT, "handleMessage: isDiscovering: " + bluetoothAdapter.isDiscovering());
                            else Log.e(TAG_BT, "handleMessage: isDiscovering error");
                        }
                    }
                    break;
                case BT_REQUEST_ENABLE:
                    Log.v(TAG_BT, "handleMessage: BT_REQUEST_ENABLE");
                    // Create intent
                    Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);

                    // Launch activity to get result
                    activityResult.launch(enableIntent);
                    break;
                case BT_STATE_CONNECTED:
                    Log.v(TAG_BT, "BT_STATE_CONNECTED");
                    connectionEstablished();
                    break;
                case BT_STATE_CONNECTION_FAILED:
                    Log.v(TAG_BT, "BT_STATE_CONNECTION_FAILED");
                    connectionFailed();
                    break;
                case BT_STATE_MESSAGE_RECEIVED:
                    //Log.v(TAG_BT, "BT_STATE_MESSAGE_RECEIVED");
                    messageReceived((String) message.obj);
                    break;
                case BT_STATE_DISCONNECTED:
                    Log.v(TAG_BT, "BT_STATE_DISCONNECTED");
                    connectionFinished();
                    break;
            }
            return true;
        }
    });

    public void openConnection(BluetoothDevice bluetoothDevice){
        Log.d(TAG_BT, "openConnection: ");
        bluetoothConnection = new BluetoothConnection(bluetoothDevice, bluetoothHandler);
        bluetoothConnection.start();
    }

    public void connectionEstablished(){
        bluetoothCommunication = new BluetoothCommunication(bluetoothConnection, bluetoothHandler);
        bluetoothCommunication.start();
        //sendToBluetooth("$P\n");//inform paired succeed
    }

    public void connectionFailed(){
        Log.e(TAG_BT, "connectionFailed: ");
    }

    String line = "";
    public void messageReceived(String message){

        // Message management
        for (int i = 0; i < message.length(); i++){
            line += message.charAt(i);
            if(message.charAt(i) == '\n') {
                    Log.d("app5_DES", "line: " + line);
                line = "";
            }
        }
    }

    public void connectionFinished(){
        Log.d(TAG_BT, "connectionFinished: ");
    }

    public void closeConnection(){
        BluetoothSocket bluetoothSocket = bluetoothConnection.getBluetoothSocket();
        if (bluetoothSocket != null && bluetoothSocket.isConnected()){
            try {
                bluetoothSocket.close();
            } catch (IOException exception) {
                exception.printStackTrace();
                Log.d(TAG_BT, "Error : ", exception);
            }
        }
    }


}