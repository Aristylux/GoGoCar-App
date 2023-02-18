package com.example.app3;

import static com.example.app3.BTCodes.BT_STATE_CONNECTED;
import static com.example.app3.BTCodes.BT_STATE_CONNECTION_FAILED;
import static com.example.app3.TAG.*;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

public class BluetoothConnection extends Thread {

    private static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private BluetoothSocket bluetoothSocket = null;
    public Handler handler;

    @SuppressLint("MissingPermission")
    public BluetoothConnection (BluetoothDevice bluetoothDevice, Handler handler){
        this.handler = handler;
        try {
            Log.d(TAG_Debug, "Creating socket, my uuid : " + myUUID);
            bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(myUUID);
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG_Error, "Error : " + e);
        }
    }

    public BluetoothSocket getBluetoothSocket(){
        return bluetoothSocket;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void run(){
        Message message = Message.obtain();
        try {
            bluetoothSocket.connect();
            message.what = BT_STATE_CONNECTED;
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG_Error, "Error : " + e);
            message.what = BT_STATE_CONNECTION_FAILED;
        }
        handler.sendMessage(message);
    }
}
