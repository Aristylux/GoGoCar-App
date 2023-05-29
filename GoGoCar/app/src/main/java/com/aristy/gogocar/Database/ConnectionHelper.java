package com.aristy.gogocar.Database;

import static com.aristy.gogocar.CodesTAG.TAG_Database;
import static com.aristy.gogocar.Security.ADMIN_NAME;
import static com.aristy.gogocar.Security.ADMIN_PASSWORD;
import static com.aristy.gogocar.Security.DATABASE_NAME;
import static com.aristy.gogocar.Security.IP_SERVER;
import static com.aristy.gogocar.Security.PORT;

import android.os.StrictMode;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * ConnectionHelper:
 * <p>
 *     Establish connection to database
 * </p>
 */
public class ConnectionHelper{

    private final static String host = IP_SERVER;
    private final static String port = PORT;
    private final static String databaseName = DATABASE_NAME;
    private final static String userName = ADMIN_NAME;
    private final static String password = ADMIN_PASSWORD;

    private Connection connection;

    public ConnectionHelper() {
        this.connection = null;
    }

    /**
     * Create new connection with database
     */
    public void openConnection() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        String jdbcURL = "jdbc:postgresql://" + host + ":" + port + "/" + databaseName;
        Log.d(TAG_Database, "openConnection: " + jdbcURL);
        try {
            // Get connection to the database
            connection = DriverManager.getConnection(jdbcURL, userName, password);
            Log.d(TAG_Database, "openConnection: Connected");
        } catch (Exception exception) {
            // No connection!
            Log.e(TAG_Database, "openConnection: ", exception);
            Log.e(TAG_Database, "openConnection: " + exception.getMessage());
            exception.printStackTrace();
        }
    }

    /**
     * Getter connection
     * @return connection
     */
    public Connection getConnection() {
        return this.connection;
    }

    /**
     * verify if the current connection is valid<br>
     * -> not null<br>
     * -> not closed
     * @param connection current connection
     * @return if valid
     */
    public static boolean connectionValid(Connection connection){
        try {
            if (connection != null) {
                Log.d(TAG_Database, "connectionValid: SQLConnection=" + connection + ", close?=" + connection.isClosed());
                return !connection.isClosed();
            } else {
                Log.d(TAG_Database, "connectionValid: SQLConnection=" + null);
                return false;
            }
        } catch (SQLException exception) {
            Log.e(TAG_Database, "connectionValid: ", exception);
            exception.printStackTrace();
            return false;
        }
    }
}
