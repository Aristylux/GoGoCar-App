package com.aristy.gogocar;

import static com.aristy.gogocar.CodesTAG.TAG_Database;

import android.os.StrictMode;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * ConnectionHelper:
 * <p>
 *     Establish connection to database
 * </p>
 */
public class ConnectionHelper {

    private final static String host = "192.168.1.187";
    private final static String port = "5432";
    private final static String databaseName = "gogocar";
    private final static String userName = "postgres";
    private final static String password = "password";

    public Connection openConnection() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Connection connection = null;
        String jdbcURL = "jdbc:postgresql://" + host + ":" + port + "/" + databaseName;
        Log.d(TAG_Database, "ConnectionClass: " + jdbcURL);
        try {
            // Get connection to the database
            connection = DriverManager.getConnection(jdbcURL, userName, password);
            Log.d(TAG_Database, "ConnectionClass: Connected");
        } catch (Exception exception) {
            Log.e(TAG_Database, "ConnectionClass: ", exception);
            exception.printStackTrace();
        }

        return connection;
    }
}
