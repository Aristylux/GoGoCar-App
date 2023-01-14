package com.aristy.gogocar;

import static com.aristy.gogocar.CodesTAG.TAG_BT;
import static com.aristy.gogocar.CodesTAG.TAG_BT_CON;
import static com.aristy.gogocar.HandlerCodes.BT_STATE_CONNECTED;
import static com.aristy.gogocar.HandlerCodes.BT_STATE_CONNECTION_FAILED;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.UUID;

public class BluetoothConnection extends Thread {

    private static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private BluetoothSocket bluetoothSocket = null;
    BluetoothDevice bluetoothDevice;
    public Handler handler;


    @SuppressLint("MissingPermission")
    public BluetoothConnection (BluetoothDevice bluetoothDevice, Handler handler){
        this.handler = handler;
        this.bluetoothDevice = bluetoothDevice;
        try {
            Log.d(TAG_BT_CON, "BluetoothConnection: Creating socket, my uuid : " + myUUID);
            bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(myUUID);
        } catch (IOException exception) {
            exception.printStackTrace();
            Log.e(TAG_BT_CON, "BluetoothConnection:", exception);
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
            Log.d(TAG_BT_CON, "BluetoothConnection.run: Connected to " + bluetoothSocket.getRemoteDevice().getName());
            bluetoothSocket.connect();
            message.what = BT_STATE_CONNECTED;
        } catch (IOException exception) {
            exception.printStackTrace();
            Log.e(TAG_BT_CON, "BluetoothConnection.run: ", exception);
            message.what = BT_STATE_CONNECTION_FAILED;
        }
        handler.sendMessage(message);
    }

    public void closeConnection(){
        if (bluetoothSocket != null && bluetoothSocket.isConnected()){
            try {
                bluetoothSocket.close();
            } catch (IOException exception) {
                exception.printStackTrace();
                Log.d(TAG_BT, "Error : ", exception);
            }
        }
    }

    public void connectionEstablished(){
        BluetoothCommunication bluetoothCommunication = new BluetoothCommunication(BluetoothConnection.this, handler);
        bluetoothCommunication.start();
        handler.obtainMessage(10, "line").sendToTarget();
        //sendToBluetooth("$P\n");//inform paired succeed
    }

    public void connectionFailed(){
        Log.e(TAG_BT_CON, "connectionFailed: ");
    }

    public void messageReceived(String message){
        // Message management

        // Extract code

        // Extract message

        // Action
    }

    public void connectionFinished(){
        Log.d(TAG_BT_CON, "connectionFinished: ");
        // unpair device
        try {
            String methodName = "removeBond";
            Method method = bluetoothDevice.getClass().getMethod(methodName, (Class[]) null);
            method.invoke(bluetoothDevice, (Object[]) null);
        } catch (Exception exception) {
            Log.e(TAG_BT_CON, "connectionFinished", exception);
        }
    }


}
