package com.aristy.gogocar;

import static com.aristy.gogocar.CodesTAG.TAG_BT;
import static com.aristy.gogocar.CodesTAG.TAG_Database;
import static com.aristy.gogocar.CodesTAG.TAG_Debug;
import static com.aristy.gogocar.CodesTAG.TAG_SPLASH;
import static com.aristy.gogocar.HandlerCodes.GOTO_HOME_FRAGMENT;
import static com.aristy.gogocar.HandlerCodes.GOTO_LOGIN_FRAGMENT;
import static com.aristy.gogocar.HandlerCodes.STATUS_BAR_COLOR;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.LocationManager;
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

    Connection SQLConnection;
    UserPreferences userPreferences;

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

        Fragment selectedFragment;
        // If the user is not logged
        if(!isLogged)
            selectedFragment = new FragmentLogin(SQLConnection, userPreferences, fragmentHandler);
        else
            selectedFragment = new FragmentApp(SQLConnection, userPreferences, fragmentHandler);

        // Set Fragment
        setFragment(selectedFragment, R.anim.from_left, R.anim.to_right);

        // For top bar and navigation bar
        setWindowVersion();

        // -----

        // Get bluetooth adapter
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

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

    @SuppressLint("MissingPermission")
    void checkBluetoothState(){
        if(bluetoothAdapter == null){
            Log.d(TAG_BT, "Bluetooth not supported.");
        } else {
            if (bluetoothAdapter.isEnabled()){
                if (bluetoothAdapter.isDiscovering()){
                    Log.d(TAG_BT, "checkBluetoothState: discovering in progress");
                } else {
                    Log.d(TAG_BT, "checkBluetoothState: Bluetooth is enabled");
                }
            } else {

                ActivityResultLauncher<Intent> activityResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        Log.d(TAG_BT, "onActivityResult: " + result.getData() + "," + result.getResultCode() + ", " + result);
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

        if (requestCode == PermissionHelper.REQUEST_ACCESS_COARSE_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG_BT, "onRequestPermissionsResult: allowed " + permissions[0]);
            } else {
                Log.d(TAG_BT, "onRequestPermissionsResult: forbidden");
            }
        }

    }

    /*private final*/ BroadcastReceiver devicesFoundReceiver = new BroadcastReceiver() {
        @SuppressLint("MissingPermission")
        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            Log.d(TAG_BT, "onReceive: devicesFoundReceiver: " + action);

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Log.d(TAG_BT, "onReceive: " + device.getName() + ", " + device.getAddress());
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                Log.d(TAG_BT, "onReceive: scanning bluetooth devices");
            } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                Log.d(TAG_BT, "onReceive: scanning in progress ...");
            }
        }
    };

    /*
        // First time, appear after onCreate
        @Override
        protected void onStart() {
            super.onStart();
            // If the connection to the server is close, open it
            if (!connectionValid()) {
                Log.d(TAG_Database, "onStart: open SQL Connection");
                SQLConnection = connectionHelper.openConnection();
            } else {
                Log.e(TAG_Database, "onStart: ERROR open SQL Connection: invalid");
            }
        }
        */
    public boolean connectionValid(){
        try {
            Log.d(TAG_Database, "connectionValid: SQLConnection=" + SQLConnection + ", close?=" + SQLConnection.isClosed());
            if (SQLConnection != null)
                return !SQLConnection.isClosed();
            else
                return false;
        } catch (SQLException exception) {
            Log.e(TAG_Database, "connectValid: ", exception);
            exception.printStackTrace();
            return false;
        }
    }
/*
    // Quit app without kill process
    @Override
    protected void onStop() {
        super.onStop();
        try {
            // The user leave application, close connection to the server.
            if (connectionValid()) {
                Log.d(TAG_Debug, "onStop: close SQL connection");
                SQLConnection.close();
            } else {
                Log.e(TAG_Debug, "onStop: ERROR close SQL connection: invalid");
            }
        } catch (SQLException exception) {
            Log.e(TAG_Debug, "onStop: ERROR close SQL connection: ", exception);
            exception.printStackTrace();
        }
    }
*/
    // Close connection before destroy app
    @Override
    protected void onDestroy() {
        try {
            // The user leave application, close connection to the server.
            if (connectionValid()) {
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

    // Window settings
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
                    setFragment(new FragmentApp(SQLConnection, userPreferences, fragmentHandler), R.anim.from_right, R.anim.to_left);
                    break;
                case GOTO_LOGIN_FRAGMENT:
                    setFragment(new FragmentLogin(SQLConnection, userPreferences, fragmentHandler), R.anim.from_left, R.anim.to_right);
                    break;
                case STATUS_BAR_COLOR:
                    // Set color background
                    getWindow().setStatusBarColor((Integer) message.obj);
                    break;
                case 8:
                    Log.d(TAG_BT, "onCreate: start discovering");
                    boolean result = bluetoothAdapter.startDiscovery();
                    if (result)
                        Log.d(TAG_BT, "handleMessage: isDiscovering: " + bluetoothAdapter.isDiscovering());
                    else
                        Log.d(TAG_BT, "handleMessage: isDiscovering error");
                    break;
            }
            return true;
        }
    });
}