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


# SQL Unused

```java
    public List<DBModelUser> getAllUsers(){
        List<DBModelUser> returnList = new ArrayList<>();

        String query = "SELECT * FROM " + TABLE_USER;

        if (isConnectionError("getAllUsers")) return returnList;

        // Get data from database
        try {
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(query);

            while (rs.next()) {
                // Loop through the cursor (result set) and create new user objects. Put them into the return list.
                int userID = rs.getInt(1);
                String userName = rs.getString(2);
                String userEmail = rs.getString(3);
                String userPhone = rs.getString(4);
                String userHash = rs.getString(5);
                //int userIdentityID = rs.getInt(6);

                DBModelUser user = new DBModelUser(userID, userName, userEmail, userPhone, userHash);
                Log.i(TAG_Database, user.toString());
                returnList.add(user);
            }

            // Close both result and the statement
            rs.close();
            st.close();
        } catch (Exception exception){
            Log.e(TAG_Database, "getAllUsers: ", exception);
        }
        return returnList;
    }
```

# sql error


```java
/*
        // If nothing is returned, create null vehicle object to avoid overflow
        if (returnList.isEmpty())
            returnList.add(new DBModelVehicle());
        */

```


# js moving

```html
!DOCTYPE html>
<html lang="en">
    <head>
        <link rel="stylesheet" href="moving.css"/>
    </head>
    <body>
        <section id="moving-screen">
            <!-- content -->
            
        </section>
        <script src="moving.js"></script>
    </body>
```

```css
#moving-screen {
    position: fixed;
    top: 0;
    left: 101vw;
    width: 100vw;
    height: 100vh;
    background-color: var(--background-color);
    
    transition: var(--touch-transistion) ease-in-out;
    box-shadow: 0 1px 2px rgba(0, 0, 0, 0.5);
    display: none;
    z-index: 2;
}

#moving-screen.active {
    left: 0;
}
```

```js
const setting_page_pp = document.getElementById("moving-screen");

setting_page_pp.addEventListener('touchstart', touchStart);
setting_page_pp.addEventListener('touchmove', touchMove);
setting_page_pp.addEventListener('touchend', touchEnd);
var startingX, count;

function touchStart(event) {
    startingX = event.touches[0].clientX;
    //console.log("start " + startingX);
    count = 0;
    setting_page_pp.style.transition = '0s';
}

function touchMove(event) {
    let change = event.touches[0].clientX - startingX;
    count++;
    // If move to right to left
    if(change < 0)
        return;
    
    if(change > 50)
        setting_page_pp.style.left = change - 50 + 'px';
    event.preventDefault();
}

function touchEnd(event) {
    let endedX = event.changedTouches[0].clientX;
    let change = endedX - startingX;
     setting_page_pp.style.transition = '0.3s ease-in-out';
     // If user move is < to 20px, ignore
    if (change < 20) {
        return;
    }
     if(determinateChange(startingX, endedX, change, count)){
        //console.log("rest");
        setting_page_pp.style.left = '0px';
    } else {
        //console.log("leave");
        setting_page_pp.style.left = '101vw';
    }
}

function determinateChange(start, end, change, sample) {
    let speed =  change / sample;
    //console.log("-> start=" + start + "px, end=" + end + "px, diff=" + change + "px, nbr=" + sample + ", speed=" + speed);
    
    if(speed > 10){
        //console.log("speed")
        return false;
    }
     let threshold = screen.width / 2;
    if(change < threshold){
        //console.log("threshold");
        return true;
    }
        
    return false;
}
```

# web java

```java

oncreate : 

```
// Result state page
web.setWebViewClient(new Callback());
```

//open in app
    public static class Callback extends WebViewClient {
        @Override
        public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
            return false;
        }

        public void onPageFinished(WebView view, String url){
            //Here you want to use .loadUrl again
            //on the webView object and pass in
            //"javascript:<your javaScript function"
            //Set<BluetoothDevice> bluetoothDevice = getBluetoothPairedDevices();
            //error here when bt is not activated
            //populateSpinner(bluetoothDevice);
        }
    }

```