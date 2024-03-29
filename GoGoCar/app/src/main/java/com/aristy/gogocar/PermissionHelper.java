package com.aristy.gogocar;

import static com.aristy.gogocar.CodesTAG.TAG_BT;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PermissionHelper {

    // Codes
    public static final int REQUEST_ACCESS_COARSE_LOCATION = 1;

    // Permissions
    public static final String ACCESS_COARSE_LOCATION_PERMISSION = Manifest.permission.ACCESS_COARSE_LOCATION;

    public static boolean checkPermission(Activity activity, String permission, int requestCode){
        // Checking if permission is not granted
        if (ContextCompat.checkSelfPermission(activity.getBaseContext(), permission) == PackageManager.PERMISSION_DENIED){
            Log.d(TAG_BT, "checkPermission: ask permission: " + permission);
            ActivityCompat.requestPermissions(activity, new String[] {permission}, requestCode);
            return false;
        } else {
            Log.d(TAG_BT, "checkPermission: permission already granted.");
            return true;
        }
    }

    public static boolean checkPermission(Activity activity){
        // Checking if permission is not granted

        if ((ContextCompat.checkSelfPermission(activity.getBaseContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                && (ContextCompat.checkSelfPermission(activity.getBaseContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            Log.d(TAG_BT, "checkPermission: ask permission");
            ActivityCompat.requestPermissions(activity, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            return false;
        } else {
            Log.d(TAG_BT, "checkPermission: permission already granted.");
            return true;
        }
    }

    /**
     * @return true if bluetooth is enabled <br>
     * false if disabled or error
     */
    public static boolean isBluetoothEnabled(){
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null)
            return false;
        return bluetoothAdapter.isEnabled();
    }

    public static boolean isCoarseLocationPermissionGranted(Context context){
        return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED;
    }

    public static void askCoarseLocationPermission(Activity activity){
        ActivityCompat.requestPermissions(activity, new String[] {Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_ACCESS_COARSE_LOCATION);
    }

    public static boolean checkCoarseLocationPermission(Activity activity) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_ACCESS_COARSE_LOCATION);
            return false;
        } else {
            return true;
        }
    }

    /**
     * @param context main context
     * @return true if location is enabled <br>
     * false if disabled
     */
    public static boolean isLocationEnabled(Context context){
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

}
