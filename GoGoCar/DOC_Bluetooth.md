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

