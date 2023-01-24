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

# Sql prepared query

```java
String preparedQuery = "INSERT INTO " + TABLE_USER +
                "( " + COLUMN_USER_NAME + "," + COLUMN_USER_EMAIL + "," + COLUMN_USER_PHONE_NUMBER + "," + COLUMN_USER_PASSWORD + ") VALUES (?, ?, ?, ?)";
// Add user in database.
PreparedStatement st = connection.prepareStatement(preparedQuery);
// i is '?' position
st.setString(1, userModel.getFullName());
st.setString(2, userModel.getEmail());
st.setString(3, userModel.getPhoneNumber());
st.setString(4, userModel.getPassword());

// Execute query
st.executeUpdate();

// Close
st.close();
```

```java
String query = "INSERT INTO " + TABLE_USER +
                "( " + COLUMN_USER_NAME + "," + COLUMN_USER_EMAIL + "," + COLUMN_USER_PHONE_NUMBER + "," + COLUMN_USER_PASSWORD + ") " +
                "VALUES ('" + userModel.getFullName() + "','" + userModel.getEmail() + "','" + userModel.getPhoneNumber() + "','" + userModel.getPassword() + "')";
// Find user in the database.
// If it found, delete it and return true.
// If it is not found, return false.
Statement st = connection.createStatement();
boolean result = st.execute(query);

// Close
st.close();
```


# sql error


```java
/*
        // If nothing is returned, create null vehicle object to avoid overflow
        if (returnList.isEmpty())
            returnList.add(new DBModelVehicle());
        */

```