package com.aristy.gogocar;

import static com.aristy.gogocar.CodesTAG.TAG_BT;
import static com.aristy.gogocar.CodesTAG.TAG_BT_COM;
import static com.aristy.gogocar.CodesTAG.TAG_BT_CON;
import static com.aristy.gogocar.HandlerCodes.BT_STATE_CONNECTED;
import static com.aristy.gogocar.HandlerCodes.BT_STATE_CONNECTION_FAILED;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.aristy.gogocar.RSA.RSAHelper;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.UUID;

public class BluetoothConnection extends Thread {

    private static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private BluetoothSocket bluetoothSocket = null;
    public Handler handler;

    private boolean isConnecting;

    private String message;
    private String function;


    /**
     * Constructor, set default, prepare to be connected
     */
    public BluetoothConnection (){
        this.isConnecting = false;
    }

    /**
     * Open connection with the device
     * @param bluetoothDevice device
     * @param handler for call
     */
    @SuppressLint("MissingPermission")
    public void openConnection(BluetoothDevice bluetoothDevice, Handler handler){
        this.handler = handler;
        try {
            Log.d(TAG_BT_CON, "BluetoothConnection: Creating socket, my uuid : " + myUUID);
            bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(myUUID);
        } catch (IOException exception) {
            exception.printStackTrace();
            Log.e(TAG_BT_CON, "BluetoothConnection:", exception);
        }
    }

    /**
     * Get bluetooth socket
     * @return bluetooth socket
     */
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

    /**
     * Close connection with device
     */
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


    /**
     * Start bluetooth communication
     */
    public void connectionEstablished(){
        BluetoothCommunication bluetoothCommunication = new BluetoothCommunication(BluetoothConnection.this, handler);
        bluetoothCommunication.start();

        byte [] by = RSAHelper.publicKey8bytes();
        bluetoothCommunication.write(by);
        Log.d(TAG_BT, "connectionEstablished: " + Arrays.toString(by));



        // Test send
        String s = "Salut man";

        for (int i = 0; i < 10; i++) {
            try {
                bluetoothCommunication.write(s.getBytes(StandardCharsets.UTF_8));
                Log.d(TAG_BT_CON, "connectionEstablished: write: " + s);
                Thread.sleep(1000); // Wait for 1 second (1000 milliseconds)
            } catch (InterruptedException e) {
                // Handle the interrupted exception if necessary
            }
        }
    }

    /**
     * Connection failed
     */
    public void connectionFailed(){
        Log.e(TAG_BT_CON, "connectionFailed: ");
    }

    /**
     * When a message is received: <br>
     * Extract the specifications <br>
     *  - Code <br>
     *  - Message <br>
     * Find the best function to result
     * @param message message to extract
     */
    public void messageReceived(String message){


        Log.d(TAG_BT_COM, "run: " + Arrays.toString(message.getBytes(StandardCharsets.UTF_8)));

        StringBuilder formattedString = new StringBuilder();
        for (int i = 0; i < message.length(); i += 2) {
            formattedString.append(message.substring(i, i + 2)).append(" ");
        }

        Log.d(TAG_BT_COM, "run: " + Arrays.toString(formattedString.toString().getBytes(StandardCharsets.UTF_8)));


        // TODO
        // Message management

        // Extract code

        // Extract message
        //this.message = message;

        // Action
        //this.function = "functionTest";
    }

    public String getMessageFunction(){
        return this.function;
    }

    public String getMessageParams(){
        return this.message;
    }

    /**
     * When the connection is finished,<br>
     * Unpair the device
     */
    public void connectionFinished(){
        Log.d(TAG_BT_CON, "connectionFinished: ");
        // unpair device
        try {
            String methodName = "removeBond";
            Method method = bluetoothSocket.getRemoteDevice().getClass().getMethod(methodName, (Class[]) null);
            method.invoke(bluetoothSocket.getRemoteDevice(), (Object[]) null);
        } catch (Exception exception) {
            Log.e(TAG_BT_CON, "connectionFinished", exception);
        }
    }

    /**
     * @return true if the app has a connection in progress <br>
     * false else
     */
    public boolean isConnecting(){
        return this.isConnecting;
    }

    /**
     * @param isConnecting set the connection
     */
    public void isConnecting(boolean isConnecting){
        this.isConnecting = isConnecting;
    }

}
