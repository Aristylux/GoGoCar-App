package com.aristy.gogocar;

import static com.aristy.gogocar.CodesTAG.TAG_BT;
import static com.aristy.gogocar.CodesTAG.TAG_BT_COM;
import static com.aristy.gogocar.HandlerCodes.BT_STATE_DISCONNECTED;
import static com.aristy.gogocar.HandlerCodes.BT_STATE_MESSAGE_RECEIVED;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

public class BluetoothCommunication extends Thread {
    private InputStream inputStream = null;
    private OutputStream outputStream = null;

    private final BluetoothConnection bluetoothConnection;
    private BluetoothSocket bluetoothSocket;
    private final Handler handler;

    private StringBuilder message = new StringBuilder();

    public BluetoothCommunication(BluetoothConnection bluetoothConnection, Handler handler){
        this.bluetoothConnection = bluetoothConnection;
        this.handler = handler;
        this.bluetoothSocket = bluetoothConnection.getBluetoothSocket();
        try {
            inputStream = bluetoothSocket.getInputStream();
            outputStream = bluetoothSocket.getOutputStream();
        } catch (IOException exception) {
            exception.printStackTrace();
            Log.e(TAG_BT_COM, "BluetoothCommunication: ", exception);
        }
    }

    @Override
    public void run() {
        byte[] buffer = new byte[1024];
        int bytes;

        // when bluetooth is connected, read buffer
        while(bluetoothSocket.isConnected()){
            try {
                // Message size
                bytes = inputStream.read(buffer);

                // Get Message
                String tempMessage = new String(buffer,0, bytes);
                //Log.d(TAG_BT, "run: " + bytes + " | " + Arrays.toString(buffer));

                //$VA:data\n

                // Format message
                for (int i = 0; i < tempMessage.length(); i++){
                    if(tempMessage.charAt(i) == '\n') {
                        handler.obtainMessage(BT_STATE_MESSAGE_RECEIVED, message).sendToTarget();
                        message = new StringBuilder();
                    } else {
                        message.append(tempMessage.charAt(i));
                    }
                }
            } catch (IOException exception) {
                exception.printStackTrace();
                Log.e(TAG_BT_COM, "BluetoothCommunication.run: BT disconnect from decide end. : ", exception);
                try {
                    // Disconnect properly
                    if (bluetoothSocket != null && bluetoothSocket.isConnected())
                        bluetoothSocket.close();
                    handler.obtainMessage(BT_STATE_DISCONNECTED).sendToTarget();
                } catch (IOException ioException){
                    ioException.printStackTrace();
                }
            }
            bluetoothSocket = bluetoothConnection.getBluetoothSocket();
        }
    }


    /**
     * write to the output stream <br>
     * (in the bluetooth line) <br>
     * String word = "hello world"; <br>
     * use: class.write(word.getBytes());
     * @param bytes data
     */
    public void write(byte[] bytes){
        try {
            outputStream.write(bytes);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}
