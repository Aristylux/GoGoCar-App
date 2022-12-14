package com.example.app2;

import static com.example.app2.BTCodes.*;
import static com.example.app2.TAG.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    Spinner spinner;
    TextView deviceStatus;
    Button connectButton;

    BluetoothConnection bluetoothConnection;
    DataCommunication dataCommunication;

    boolean bluetoothConnected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // -- find objects
        Button sendMessageButton = findViewById(R.id.button_send);
        spinner = findViewById(R.id.spinnerPairedDevice);
        deviceStatus = findViewById(R.id.device_status);
        connectButton = findViewById(R.id.button_connect);

        Set<BluetoothDevice> bluetoothDevice = getBluetoothPairedDevices();
        if (bluetoothDevice != null)
            populateSpinner(bluetoothDevice);

        sendMessageButton.setOnClickListener(view -> {
            Log.d(TAG_Info, "Click Send");
            String message = "8";
            sendTheMessage(message);
        });

        connectButton.setOnClickListener(view -> {
            Log.d(TAG_Debug, "click connect");
            if (!bluetoothConnected) {
                if (spinner.getSelectedItemPosition() == 0) {
                    Log.d(TAG_Debug, "Please select bluetooth device.");
                    Toast.makeText(getApplicationContext(), "Please select bluetooth device.", Toast.LENGTH_SHORT).show();
                    return;
                }
                connectButton.setText("Connecting...");
                String selectedDevice = spinner.getSelectedItem().toString();
                Log.d(TAG_Debug, "Selected device : " + selectedDevice);
                openConnection(selectedDevice);
            } else {
                Log.d(TAG_Debug, "Disconnecting...");
                BluetoothSocket bluetoothSocket = bluetoothConnection.getBluetoothSocket();
                if (bluetoothSocket != null && bluetoothSocket.isConnected()) {
                    try {
                        bluetoothSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.d(TAG_Error, "Error : " + e);
                    }
                    connectButton.setText("Connect");
                    bluetoothConnected = false;
                }
            }
        });
    }

    @SuppressLint("MissingPermission")
    public void openConnection(String deviceName) {
        Set<BluetoothDevice> bluetoothPairedDevices = getBluetoothPairedDevices();
        for (BluetoothDevice bluetoothDevice : bluetoothPairedDevices) {
            if (deviceName.equals(bluetoothDevice.getName())) {
                Log.d(TAG_Debug, "Selected device UUID : " + bluetoothDevice.getAddress());
                bluetoothConnection = new BluetoothConnection(bluetoothDevice, handler);
                bluetoothConnection.start();
            }
        }
    }

    @SuppressLint("MissingPermission")
    public Set<BluetoothDevice> getBluetoothPairedDevices() {
        int REQUEST_ENABLE_BT = 0;
        Set<BluetoothDevice> bluetoothDevices = null;
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null)
            Log.d(TAG_Error, "bluetoothAdapter - Device doesn't support Bluetooth");
        else if (!bluetoothAdapter.isEnabled()) {
            Log.d(TAG_Error, "bluetoothAdapter - no bluetooth");
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            bluetoothDevices = bluetoothAdapter.getBondedDevices();
            Log.d(TAG_Debug, "bluetoothAdapter - Paired devices count : " + bluetoothDevices.size());
        }
        return bluetoothDevices;
    }

    @SuppressLint("MissingPermission")
    private void populateSpinner(Set<BluetoothDevice> bluetoothDevices) {
        ArrayList<String> allPairedDevices = new ArrayList<>();
        allPairedDevices.add("Select");
        for (BluetoothDevice bluetoothDevice : bluetoothDevices) {
            allPairedDevices.add(bluetoothDevice.getName());
            Log.d(TAG_Debug, bluetoothDevice.getName() + ", " + bluetoothDevice.getAddress());
        }
        final ArrayAdapter<String> aaPairedDevices = new ArrayAdapter<>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, allPairedDevices);
        aaPairedDevices.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(aaPairedDevices);
    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            switch (message.what){
                case BT_STATE_CONNECTED:
                    Log.d(TAG_Debug, "BT_STATE_CONNECTED");
                    bluetoothConnected = true;
                    connectionEstablished();
                    break;
                case BT_STATE_CONNECTION_FAILED:
                    Log.d(TAG_Debug, "BT_STATE_CONNECTION_FAILED");
                    bluetoothConnected = false;
                    connectionFailed();
                    break;
                case BT_STATE_MESSAGE_RECEIVED:
                    Log.d(TAG_Debug, "BT_STATE_MESSAGE_RECEIVED");
                    break;
                case BT_STATE_DISCONNECTED:
                    Log.d(TAG_Debug, "BT_STATE_DISCONNECTED");
                    connectionFinished();
                    break;
            }
            return true;
        }
    });

    //---
    public void connectionEstablished(){
        dataCommunication = new DataCommunication(bluetoothConnection, handler);
        dataCommunication.start();
        connectButton.setText("Disconnect");
    }

    public void connectionFailed(){
        deviceStatus.setText("Fail");
        connectButton.setText("Connect");
    }

    public void connectionFinished(){
        deviceStatus.setText("Disconnected");
        connectButton.setText("Connect");
    }

    public void sendTheMessage(String message){
        //byte[] CR = 13;
        if (bluetoothConnection != null) {
            BluetoothSocket bluetoothSocket = bluetoothConnection.getBluetoothSocket();
            if (bluetoothSocket != null && bluetoothConnected) {
                if (bluetoothSocket.isConnected()) {
                    Log.d(TAG_Debug, "Send");
                    String messageFinal = message + "\r";
                    dataCommunication.write(messageFinal.getBytes());
                    Toast.makeText(getApplicationContext(), "Message send: '" + message + "'.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Please connect to bluetooth", Toast.LENGTH_SHORT).show();
            }
        }
    }

}