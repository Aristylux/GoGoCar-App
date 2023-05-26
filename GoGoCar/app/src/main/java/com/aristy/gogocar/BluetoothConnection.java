package com.aristy.gogocar;

import static com.aristy.gogocar.CodesTAG.TAG_BT;
import static com.aristy.gogocar.CodesTAG.TAG_BT_COM;
import static com.aristy.gogocar.CodesTAG.TAG_BT_CON;
import static com.aristy.gogocar.CodesTAG.TAG_RSA;
import static com.aristy.gogocar.CodesTAG.TAG_CAN;
import static com.aristy.gogocar.HandlerCodes.BT_STATE_CONNECTED;
import static com.aristy.gogocar.HandlerCodes.BT_STATE_CONNECTION_FAILED;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.aristy.gogocar.RSA.PublicKey;
import com.aristy.gogocar.RSA.RSA;
import com.aristy.gogocar.RSA.RSAKeys;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.UUID;

public class BluetoothConnection extends Thread {

    private static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private BluetoothSocket bluetoothSocket = null;
    public Handler handler;

    private boolean isConnecting;


    private RSA rsa;
    private boolean waitForModulePublicKey;
  
    /**
     * Constructor, set default, prepare to be connected
     */
    public BluetoothConnection (){
        this.isConnecting = false;
        this.waitForModulePublicKey = false;
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

        rsa = new RSA();
        rsa.generateRSAKeys();

        Log.d(TAG_RSA, "connectionEstablished: E:" + rsa.getRsaKeys());

        long expo = rsa.getRsaKeys().publicKey.e;
        long mod =  rsa.getRsaKeys().publicKey.N;

        Log.d(TAG_RSA, "connectionEstablished: E:" + expo + " N:" + mod);
        Log.d(TAG_RSA, "connectionEstablished: E:" + Arrays.toString(String.valueOf(expo).getBytes()) + " N:" + Arrays.toString(String.valueOf(mod).getBytes()));

        for (int i = 0; i < 10; i++) {
            try {
                bluetoothCommunication.write(String.valueOf(expo).getBytes());
                Log.d(TAG_RSA, "connectionEstablished: E:" + expo + " N:" + mod);
                Thread.sleep(1000); // Wait for 1 second (1000 milliseconds)
            } catch (InterruptedException e) {
                // Handle the interrupted exception if necessary
            }
        }

        bluetoothCommunication.write(String.valueOf(expo).getBytes());
/*
        byte[] publicKeyBytes = rsa.getBytePublicKey();

        Log.d(TAG_RSA, "connectionEstablished: 16: " + RSA.printBytes(publicKeyBytes));

        byte [] by = RSA.convertTo8ByteArray(publicKeyBytes);

        bluetoothCommunication.write(by);
        Log.d(TAG_BT, "connectionEstablished: " + Arrays.toString(by));
*/
        this.waitForModulePublicKey = true;


/*
        // Test send
        String s = "Salut man";
        String jamer = "$deb:1";

        for (int i = 0; i < 100; i++) {
            try {
                bluetoothCommunication.write(jamer.getBytes(StandardCharsets.UTF_8));
                Log.d(TAG_BT_CON, "connectionEstablished: write: " + jamer + " - " + Arrays.toString(jamer.getBytes(StandardCharsets.UTF_8)));
                Thread.sleep(1000); // Wait for 1 second (1000 milliseconds)
            } catch (InterruptedException e) {
                // Handle the interrupted exception if necessary
            }
        }

 */
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

    public ReceiverCAN messageReceived(String message){
        Log.d(TAG_BT_COM, "run: " + Arrays.toString(message.getBytes(StandardCharsets.UTF_8)));

        if (this.waitForModulePublicKey){
            Log.d(TAG_BT_CON, "messageReceived: module public key: " + message);
            // Set module public key
            //rsa.setModulePublicKey(rsa.parsePublicKey(message));
        } else {
            Log.d(TAG_BT_CON, "messageReceived: decrypt message: " + message);
            // Decrypt the message
        }

        String type;
        String data;

        // Message : "&type:data\n"
        if (message.startsWith("$") && message.contains(":")) {
            int colonIndex = message.indexOf(":");

            type = message.substring(1, colonIndex);
            data = message.substring(colonIndex + 1);
            return CAN.transformMessage(type, data);
        } else {
            Log.e(TAG_CAN, "messageReceived: data invalid. message: '" + message + "'");
            return new ReceiverCAN();
        }
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
