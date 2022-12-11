package com.aristy.gogocar;

import static com.aristy.gogocar.CodesTAG.TAG_Database;

import android.os.StrictMode;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;

public class ConnectionHelper {

    private String host;
    private final static String port = "5432";
    private String databaseName;
    private final static String userName = "postgres";
    private final static String password = "password";

    public Connection openConnection() {

        host = "192.168.1.187";
        databaseName = "gogocar";

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Connection connection = null;
        String jdbcURL = "jdbc:postgresql://" + host + ":" + port + "/" + databaseName;
        Log.d(TAG_Database, "ConnectionClass: " + jdbcURL);

        try {
            connection = DriverManager.getConnection(jdbcURL, userName, password);
            Log.d(TAG_Database, "ConnectionClass: Connected");
        } catch (Exception exception) {
            Log.d(TAG_Database, exception.toString());
            exception.printStackTrace();
        }

        return connection;

    }
}
