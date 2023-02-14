package com.aristy.app5;

import static com.aristy.app5.HandlerCodes.BT_STATE_DISCONNECTED;
import static com.aristy.app5.HandlerCodes.BT_STATE_MESSAGE_RECEIVED;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class BluetoothCommunication extends Thread {

    private InputStream inputStream = null;
    private OutputStream outputStream = null;

    BluetoothConnection bluetoothConnection;
    private BluetoothSocket bluetoothSocket;
    Handler handler;

    private static final String TAG_BT_COM = "app5_BT_Communication";

    public BluetoothCommunication(BluetoothConnection bluetoothConnection, Handler handler){
        this.bluetoothConnection = bluetoothConnection;
        this.handler = handler;
        this.bluetoothSocket = bluetoothConnection.getBluetoothSocket();
        try {
            inputStream = bluetoothSocket.getInputStream();
            outputStream = bluetoothSocket.getOutputStream();
        } catch (IOException exception) {
            exception.printStackTrace();
            Log.e(TAG_BT_COM, "Error : ", exception);
        }
    }

    @Override
    public void run() {
        byte[] buffer = new byte[1024];
        int bytes;

        while(bluetoothSocket.isConnected()){
            try {
                bytes = inputStream.read(buffer);
                String tempMessage = new String(buffer,0, bytes);
                handler.obtainMessage(BT_STATE_MESSAGE_RECEIVED, tempMessage).sendToTarget();
            } catch (IOException exception) {
                exception.printStackTrace();
                Log.e(TAG_BT_COM, "BT disconnect from decide end. : ", exception);
                try {
                    if (bluetoothSocket != null && bluetoothSocket.isConnected()){
                        bluetoothSocket.close();
                    }
                    handler.obtainMessage(BT_STATE_DISCONNECTED).sendToTarget();
                } catch (IOException ioException){
                    ioException.printStackTrace();
                }
            }
            bluetoothSocket = bluetoothConnection.getBluetoothSocket();
        }
    }

    public void write(byte[] bytes){
        try {
            outputStream.write(bytes);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}
