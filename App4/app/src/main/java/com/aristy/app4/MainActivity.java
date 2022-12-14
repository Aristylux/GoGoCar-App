package com.aristy.app4;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_ACCESS_COARSE_LOCATION = 1;

    private static final String[] BLE_PERMISSIONS = new String[]{
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    @RequiresApi(api = Build.VERSION_CODES.S)
    private static final String[] ANDROID_12_BLE_PERMISSIONS = new String[]{
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT
    };


    BluetoothAdapter bluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get bluetooth adapter
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // Check bluetooth state
        checkBluetoothState();

        checkPermission();

        // Check Location permission on start
        //checkCoarseLocationPermission();

        // Test:
        if (bluetoothAdapter != null && bluetoothAdapter.isEnabled()) {

            if (checkCoarseLocationPermission()) {
                boolean result = bluetoothAdapter.startDiscovery();
                Log.d("TAG_BT", "onCreate: result on discovery: " + result);
            }
        }
    }


    public void checkPermission() {
        Log.d("TAG_BT", "checkPermission: ");
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }
    }

    public void requestBlePermissions(int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
            ActivityCompat.requestPermissions(MainActivity.this, ANDROID_12_BLE_PERMISSIONS, requestCode);
        else
            ActivityCompat.requestPermissions(MainActivity.this, BLE_PERMISSIONS, requestCode);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("TAG_BT", "onResume: ");
        registerReceiver(devicesFoundReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
        registerReceiver(devicesFoundReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED));
        registerReceiver(devicesFoundReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onPause() {
        super.onPause();
        Log.d("TAG_BT", "onPause: ");
        unregisterReceiver(devicesFoundReceiver);
        bluetoothAdapter.cancelDiscovery();
    }

    private boolean checkCoarseLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_ACCESS_COARSE_LOCATION);
            return false;
        } else {
            return true;
        }
    }

    @SuppressLint("MissingPermission")
    private void checkBluetoothState() {
        if (bluetoothAdapter == null) {
            Log.d("TAG_BT", "Bluetooth not supported.");
        } else {
            if (bluetoothAdapter.isEnabled()) {
                // Don't do this
                /*if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                    Log.e("TAG_BT", "checkBluetoothState: permission error");
                    return;
                }*/
                if (bluetoothAdapter.isDiscovering()) {
                    Log.d("TAG_BT", "checkBluetoothState: discovering in progress");
                } else {
                    Log.d("TAG_BT", "checkBluetoothState: Bluetooth is enabled");
                }
            } else {

                ActivityResultLauncher<Intent> activityResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        Log.d("TAG_BT", "onActivityResult: " + result.getData() + "," + result.getResultCode() + ", " + result);
                        // if(requestCade == REQUEST_ENABLE_BLUETOOTH){
                        checkBluetoothState();
                    }
                });

                // Create intent
                Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);

                // Deprecated
                //startActivityForResult(enableIntent, REQUEST_ENABLE_BLUETOOTH);

                // Launch activity to get result
                activityResult.launch(enableIntent);

            }
        }
    }

    // Show when permission return a result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        //if (requestCode == REQUEST_ACCESS_COARSE_LOCATION) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d("TAG_BT", "onRequestPermissionsResult: allowed " + permissions[0]);
        } else {
            Log.d("TAG_BT", "onRequestPermissionsResult: forbidden " + permissions[0]);
        }
        //}
    }

    // Used for broadcast receiver (to avoid multi call problems)
    String lastMacAddress = null;

    // Show action found
    private final BroadcastReceiver devicesFoundReceiver = new BroadcastReceiver() {
        @RequiresApi(api = Build.VERSION_CODES.R)
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
                if(macAddress.equals(lastMacAddress))
                    return;

                // Store new mac address
                lastMacAddress = macAddress;

                /*
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    Log.e("TAG_BT", "onReceive: permission error : BLUETOOTH_CONNECT");
                    return;
                }*/
                Log.d("TAG_BT", "onReceive: " + device.getName() + ", " + macAddress);
                Log.d("TAG_BT", "onReceive: device: " + deviceToString(device));
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                Log.d("TAG_BT", "onReceive: scanning bluetooth devices, FINISHED");
            } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                Log.d("TAG_BT", "onReceive: scanning in progress ...");
            }

        }
    };

    @RequiresApi(api = Build.VERSION_CODES.R)
    @SuppressLint("MissingPermission")
    String deviceToString(BluetoothDevice device){
        JSONObject map = new JSONObject();
        try {
            map.put("name", device.getName());
            map.put("address", device.getAddress());
            map.put("alias", device.getAlias());
            map.put("btClass", device.getBluetoothClass());
            map.put("bondState", device.getBondState());
            map.put("type", device.getType());
            map.put("uuids", device.getUuids());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return map.toString();
    }

}