package com.example.app3;

import static com.example.app3.BTCodes.*;
import static com.example.app3.TAG.*;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DataCommunication extends Thread {

    private InputStream inputStream = null;
    private OutputStream outputStream = null;

    BluetoothConnection bluetoothConnection;
    private BluetoothSocket bluetoothSocket;
    Handler handler;

    public DataCommunication(BluetoothConnection bluetoothConnection, Handler handler){
        this.bluetoothConnection = bluetoothConnection;
        this.handler = handler;
        this.bluetoothSocket = bluetoothConnection.getBluetoothSocket();
        try {
            inputStream = bluetoothSocket.getInputStream();
            outputStream = bluetoothSocket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG_Error, "Error : " + e);
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
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG_Error, "BT disconnect from decide end. : " + e);
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
