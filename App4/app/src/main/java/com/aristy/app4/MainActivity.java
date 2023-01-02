package com.aristy.app4;

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
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_ACCESS_COARSE_LOCATION = 1, REQUEST_BLUETOOTH_SCAN = 2;



    BluetoothAdapter bluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get bluetooth adapter
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // Check bluetooth state
        checkBluetoothState();

        checkBluetoothScanPermission();

        // Check Location permission on start
        //checkCoarseLocationPermission();

        // Test:
        if (bluetoothAdapter != null && bluetoothAdapter.isEnabled()) {

            if (checkCoarseLocationPermission()) {
                boolean result = bluetoothAdapter.startDiscovery();
                Log.d("TAG_BT", "onCreate: result on discovery: " + result);
            }
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                    Log.e("TAG_BT", "run: permission error");
                    return;
                }
                boolean result = bluetoothAdapter.startDiscovery();
                Log.d("TAG_BT", "onCreate: result on discovery: " + result);
            }
        }, 5000);

    }

    @Override
    protected void onResume() {
        super.onResume();

        registerReceiver(devicesFoundReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
        registerReceiver(devicesFoundReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED));
        registerReceiver(devicesFoundReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(devicesFoundReceiver);
    }

    private boolean checkCoarseLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_ACCESS_COARSE_LOCATION);
            return false;
        } else {
            return true;
        }
    }

    private boolean checkBluetoothScanPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_SCAN}, REQUEST_BLUETOOTH_SCAN);
            }
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
                //startActivityForResult(enableIntent, REQUEST_ENABLE_BLUETOOTH);

                // Launch activity to get result
                activityResult.launch(enableIntent);

            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_ACCESS_COARSE_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("TAG_BT", "onRequestPermissionsResult: allowed " + permissions[0]);
            } else {
                Log.d("TAG_BT", "onRequestPermissionsResult: forbidden");
            }
        }
    }

    private final BroadcastReceiver devicesFoundReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            Log.d("TAG_BT", "onReceive: action: " + action);
        }
    };

}