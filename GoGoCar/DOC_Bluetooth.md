# 


# Ask enable bluetooth

```java
void checkBluetoothState(){
    if(bluetoothAdapter == null){
        Log.d(TAG_BT, "Bluetooth not supported.");
    } else {
        if (bluetoothAdapter.isEnabled()){
            if (bluetoothAdapter.isDiscovering()){
                Log.d(TAG_BT, "checkBluetoothState: discovering in progress");
            } else {
                Log.d(TAG_BT, "checkBluetoothState: Bluetooth is enabled");
            }
        } else {
            ActivityResultLauncher<Intent> activityResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    Log.d(TAG_BT, "onActivityResult: " + result.getData() + "," + result.getResultCode() + ", " + result);
                    // if(requestCade == REQUEST_ENABLE_BLUETOOTH){
                    checkBluetoothState();
                }
            });
            // Create intent
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            //startActivityForResult(enableIntent, REQUEST_ENABLE_BLUETOOTH);
            // Launch activity to get result
            activityResult.launch(enableIntent);
        }
    }
}
```

optimised

```java
void checkBluetoothState(){
    if(bluetoothAdapter == null){
        Log.d(TAG_BT, "Bluetooth not supported.");
    } else {
        if (!bluetoothAdapter.isEnabled()){
            ActivityResultLauncher<Intent> activityResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), resultCallback);
            // Create intent
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            
            // Launch activity to get result
            activityResult.launch(enableIntent);
        }
    }
}
```

> Use `ActivityResultLauncher<>()` instead of `startActivityForResult()` because this is deprecated

# SDL save (bug connection)

```java
// Quit app without kill process
    @Override
    protected void onStop() {
        super.onStop();
        try {
            // The user leave application, close connection to the server.
            if (connectionValid()) {
                Log.d(TAG_Debug, "onStop: close SQL connection");
                SQLConnection.close();
            } else {
                Log.e(TAG_Debug, "onStop: ERROR close SQL connection: invalid");
            }
        } catch (SQLException exception) {
            Log.e(TAG_Debug, "onStop: ERROR close SQL connection: ", exception);
            exception.printStackTrace();
        }
    }
```

```java
// First time, appear after onCreate
        @Override
        protected void onStart() {
            super.onStart();
            // If the connection to the server is close, open it
            if (!connectionValid()) {
                Log.d(TAG_Database, "onStart: open SQL Connection");
                SQLConnection = connectionHelper.openConnection();
            } else {
                Log.e(TAG_Database, "onStart: ERROR open SQL Connection: invalid");
            }
        }
```