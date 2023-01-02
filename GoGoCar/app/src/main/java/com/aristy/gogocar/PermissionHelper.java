package com.aristy.gogocar;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.location.LocationManager;

public class PermissionHelper {


    /**
     * @return true if bluetooth is enabled <br>
     * false if disabled or error
     */
    boolean isBluetoothEnabled(){
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null)
            return false;
        return bluetoothAdapter.isEnabled();
    }

    boolean isLocationEnabled(Context context){
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

}
