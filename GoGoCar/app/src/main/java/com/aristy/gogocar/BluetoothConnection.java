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

import com.aristy.gogocar.AES.AES;
import com.aristy.gogocar.AES.AESCommon;
import com.aristy.gogocar.AES.AESKey;
import com.aristy.gogocar.RSA.PublicKey;
import com.aristy.gogocar.RSA.RSA;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.UUID;

public class BluetoothConnection extends Thread {

    private static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private BluetoothSocket bluetoothSocket = null;
    public Handler handler;

    private boolean isConnecting;

    private BluetoothCommunication bluetoothCommunication;

    private RSA rsa;
    private boolean waitForModulePublicKey;
    private boolean waitForModuleAESKey;
    private AES aes;
  
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
        bluetoothCommunication = new BluetoothCommunication(BluetoothConnection.this, handler);
        bluetoothCommunication.start();

        rsa = new RSA();
        rsa.generateRSAKeys();

        Log.d(TAG_RSA, "connectionEstablished: E:" + rsa.getRsaKeys());

        long expo = rsa.getRsaKeys().publicKey.e;
        long mod =  rsa.getRsaKeys().publicKey.N;

        Log.d(TAG_RSA, "connectionEstablished: E:" + expo + " N:" + mod);
        Log.d(TAG_RSA, "connectionEstablished: E:" + Arrays.toString(String.valueOf(expo).getBytes()) + " N:" + Arrays.toString(String.valueOf(mod).getBytes()));


        StringBuilder stringBuilder = new StringBuilder(String.valueOf(expo));
        while (stringBuilder.length() < 10) {
            stringBuilder.insert(0, '$');
        }
        String expoStr = stringBuilder.toString();

        stringBuilder = new StringBuilder(String.valueOf(mod));
        while (stringBuilder.length() < 10) {
            stringBuilder.insert(0, '$');
        }
        String modStr = stringBuilder.toString();

        String data = expoStr + "|" + modStr + "\n";

        for (int i = 0; i < 2; i++) {
            try {
                bluetoothCommunication.write(data.getBytes());
                Log.d(TAG_RSA, "connectionEstablished: data:'" + data.replace("\n", "") + "'");
                Thread.sleep(100); // Wait for 1 second (1000 milliseconds)
            } catch (InterruptedException e) {
                // Handle the interrupted exception if necessary
            }
        }

/*
        byte[] publicKeyBytes = rsa.getBytePublicKey();

        Log.d(TAG_RSA, "connectionEstablished: 16: " + RSA.printBytes(publicKeyBytes));

        byte [] by = RSA.convertTo8ByteArray(publicKeyBytes);

        bluetoothCommunication.write(by);
        Log.d(TAG_BT, "connectionEstablished: " + Arrays.toString(by));
*/
        this.waitForModulePublicKey = /*true*/ false;
        this.waitForModuleAESKey = true;
        Log.wtf(TAG_BT_COM, "connectionEstablished: READY TO RECEIVE PUBIC KEY");
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

    public ReceiverCAN messageReceived(byte[] message){
        Log.d(TAG_BT_COM, "run: " + Arrays.toString(message));
        String decryptedMessage = null;
/*
        if (this.waitForModulePublicKey){
            Log.d(TAG_BT_CON, "messageReceived: module public key: '" + message + "'");
            // Set module public key
            PublicKey publicKey = rsa.parsePublicKey(message);
            rsa.setModulePublicKey(publicKey);
            Log.d(TAG_BT_COM, "messageReceived: Public key: " + publicKey.toString());

            // Generate AES Key
            aes = new AES();
            aes.generateAESKey(AESCommon.KEY_256_BITS);
            AESKey aesKey = aes.getAesKey();
            Log.d(TAG_BT_COM, "messageReceived: aes key: " + aesKey.toString());
            Log.d(TAG_BT_COM, "messageReceived: aes key: " + aesKey.toPrint());
            Log.d(TAG_BT_COM, "messageReceived: aes key: " + Arrays.toString(aesKey.getKey()));
            Log.d(TAG_BT_COM, "messageReceived: aes key: " + Arrays.toString(aesKey.toUnsignedBytes()));

            // Crypt
            long [] aesKeyCipher = rsa.encrypt(aesKey.getKey());

            // Send
            //bluetoothCommunication.write(aesKeyCipher);
            this.waitForModulePublicKey = false;
        } else */if (this.waitForModuleAESKey){
            aes = new AES();
            Log.d(TAG_BT_CON, "messageReceived: aes message: '" + aes.printBytes(message) + "' len: " + message.length);

            // decrypt aes key
            byte[] aesKey = rsa.decrypt(message);

            aes.setAesKey(aesKey);
            Log.d(TAG_BT_COM, "messageReceived: bytes: " + aes.getAesKey());

            byte [] cipher = aes.aesEncrypt("goodmorning", aes.getAesKey());

            try {
                Log.d(TAG_BT_COM, "messageReceived: aes message goodmorning: " + Arrays.toString(cipher));
                bluetoothCommunication.write(cipher);
                Thread.sleep(1000); // Wait for 1 second (1000 milliseconds)
            } catch (InterruptedException e) {
                // Handle the interrupted exception if necessary
            }

            this.waitForModuleAESKey = false;
        } else {
            Log.d(TAG_BT_CON, "messageReceived: decrypt message: '" + aes.printBytes(message) + "' len: " + message.length);

            // Decrypt the message
            decryptedMessage = aes.aesDecrypt(message, aes.getAesKey());
            Log.d(TAG_BT_COM, "messageReceived: decryptedMessage: " + decryptedMessage);
        }

        if (decryptedMessage == null) return new ReceiverCAN();

        String type;
        String data;

        // Message : "&type:data\n"
        if (decryptedMessage.startsWith("$") && decryptedMessage.contains(":")) {
            int colonIndex = decryptedMessage.indexOf(":");

            type = decryptedMessage.substring(1, colonIndex);
            data = decryptedMessage.substring(colonIndex + 1);
            return CAN.transformMessage(type, data);
        } else {
            Log.e(TAG_CAN, "messageReceived: data invalid. message: '" + aes.printBytes(message) + "'");
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
