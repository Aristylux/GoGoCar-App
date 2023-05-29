package com.aristy.gogocar.Database;

import static com.aristy.gogocar.CodesTAG.TAG_Database;
import static com.aristy.gogocar.Database.Tables.*;

import android.util.Log;

import com.aristy.gogocar.ThreadManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper {

    private Connection connection;

    // Constructor
    public DatabaseHelper(Connection connection) {
        this.connection = connection;
    }

    /**
     * @param preparedQuery query to execute
     * @param elements element for '?' in preparedQuery
     * @return success
     */
    public boolean executeQuery(String preparedQuery, Object... elements){
        // Test if the connection is ok
        if (isConnectionError("executeQuery")) return false;

        // Check if the number of element is the same as provided by the prepared query
        if (!isElementsCorrects(preparedQuery, elements.length)) return false;

        try {
            PreparedStatement st = connection.prepareStatement(preparedQuery);
            for(int i = 1; i < elements.length + 1; i++ ){
                st.setObject(i, elements[i-1]);
            }

            // If it found, delete it and return true.
            // If it is not found, return false.
            int rowAffected = st.executeUpdate();

            // Close
            st.close();

            // If almost 1 row is affected, return true
            return rowAffected != 0;
        } catch (SQLException exception) {
            Log.e(TAG_Database, "executeQuery: ", exception);
            exception.printStackTrace();
            return false;
        }
    }

    /**
     * Check is the connection is null
     * @param funcName actual function
     * @return error or not
     */
    private boolean isConnectionError(String funcName){

        if (connection == null){
            // Failure. do not add anything to the list
            Log.e(TAG_Database, funcName + ": connect is null");
            return true;
        }

        try {
            if (connection.isClosed()) {
                Log.e(TAG_Database, "RESTART Connection");
                ThreadManager.getInstance().setConnection();
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Check if the prepared query as the correct number of objects
     * @param preparedQuery     query for prepared statement
     * @param num_elements      number of elements
     * @return error or not
     */
    private boolean isElementsCorrects(String preparedQuery, int num_elements){
        int count = 0;
        for (int i = 0; i < preparedQuery.length(); i++) {
            if (preparedQuery.charAt(i) == '?') count++;
        }

        if (count == num_elements) return true;
        Log.e(TAG_Database, "executeQuery: [NO NUM]: provided(" + num_elements + ") required(" + count + ")");
        return false;
    }

    /*  ---------------------------------- *
     *  --             USER             -- *
     *  ---------------------------------- */

    /**
     * @param ID user id
     * @return the user that matches this ID
     */
    public DBModelUser getUserById(int ID){
        String query = "SELECT * FROM " + TABLE_USER + " WHERE " + COLUMN_USER_ID + " = " + ID;
        return getUser(query);
    }

    /**
     * @param email user email
     * @return the user that matches this email
     */
    public DBModelUser getUserByEmail(String email){
        String query = "SELECT * FROM " + TABLE_USER + " WHERE " + COLUMN_USER_EMAIL + " = '" + email + "';";
        return getUser(query);
    }

    /**
     * @param phone user phone
     * @return the user that matches this phone
     */
    public DBModelUser getUserByPhone(String phone){
        String query = "SELECT * FROM " + TABLE_USER + " WHERE " + COLUMN_USER_PHONE_NUMBER + " = '" + phone + "';";
        return getUser(query);
    }

    /**
     * @param query query to execute
     * @return one user (the first row result)
     */
    private DBModelUser getUser(String query){
        DBModelUser user = new DBModelUser();

        if (isConnectionError("getUser")) return user;

        try {
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(query);

            if (rs.next()) {
                int user_id = rs.getInt(1);
                String name = rs.getString(2);
                String email = rs.getString(3);
                String phone = rs.getString(4);
                String hash = rs.getString(5);
                //int identity_id = rs.getInt(6);
                String salt = rs.getString("salt");

                user = new DBModelUser(user_id, name, email, phone, hash);
                user.setSalt(salt);
                Log.i(TAG_Database, "getUser: " + user);
            }

            rs.close();
            st.close();
        } catch (SQLException exception) {
            Log.e(TAG_Database, "getUser: ", exception);
            exception.printStackTrace();
        }
        return user;
    }

    /*  ---------------------------------- *
     *  --            VEHICLES          -- *
     *  ---------------------------------- */

    /**
     * @return List of all vehicles
     */
    public List<DBModelVehicle> getAllVehicles(){
        String query = "SELECT * FROM " + TABLE_VEHICLE;
        return getVehicles(query);
    }

    /**
     * @param IDUser actual user
     * @return List of vehicles which are not owned by the user, available and not booked.
     */
    public List<DBModelVehicle> getVehiclesAvailable(int IDUser) {
        //String query = "SELECT * FROM " + TABLE_VEHICLE + " WHERE " + COLUMN_VEHICLE_IS_AVAILABLE + " = true AND " + COLUMN_VEHICLE_IS_BOOKED + " = false AND " + COLUMN_VEHICLE_ID_OWNER + " != " + IDUser;

        String vehicleAlias = "vh";
        String addressesAlias = "addr";

        String queryJoin = "SELECT " + COLUMN_ADDRESS_ID + "," + COLUMN_ADDRESS_STREET + " FROM " + TABLE_ADDRESSES;
        String query = "SELECT " + vehicleAlias + ".*, " + addressesAlias + ".* FROM " + TABLE_VEHICLE + " AS " + vehicleAlias +
                " JOIN (" + queryJoin +") " + addressesAlias + " ON " + vehicleAlias + "." + COLUMN_VEHICLE_ID_ADDRESS + " = " + addressesAlias + "." + COLUMN_ADDRESS_ID +
                " WHERE " + COLUMN_VEHICLE_IS_AVAILABLE + " = true AND " + COLUMN_VEHICLE_IS_BOOKED + " = false AND " + COLUMN_VEHICLE_ID_OWNER + " != " + IDUser + ";";

        Log.d(TAG_Database, "getVehiclesAvailable (user) query: " + query);
        return getVehicles(query);
    }

    public List<DBModelVehicle> getVehiclesAvailable(int IDUser, String city, int distance) {
        String queryCity = "SELECT " + COLUMN_CITY_LOCATION + " FROM " + TABLE_CITY + " WHERE " + COLUMN_CITY_NAME + " = '" + city + "'";
        String queryAddresses = "SELECT * FROM " + TABLE_ADDRESSES + " WHERE ST_DWithin(location::geography, (" + queryCity + ")::geography, " + (distance*1000) + ")";

        String vehicleAlias = "vh";
        String addressesAlias = "addr";

        String query = "SELECT " + vehicleAlias + ".*, " + addressesAlias + ".* FROM " + TABLE_VEHICLE + " AS " + vehicleAlias +
                " JOIN (" + queryAddresses + ") " + addressesAlias + " ON " + vehicleAlias + "." + COLUMN_VEHICLE_ID_ADDRESS + " = " + addressesAlias + "." + COLUMN_ADDRESS_ID +
                " WHERE " + vehicleAlias + "." + COLUMN_VEHICLE_IS_AVAILABLE + " = true AND " + COLUMN_VEHICLE_IS_BOOKED + " = false AND " + COLUMN_VEHICLE_ID_OWNER + " != " + IDUser + ";";
        Log.d(TAG_Database, "getVehiclesAvailable: (dist) query: " + query);
        return getVehicles(query);
    }

    /**
     * @param IDUser actual user
     * @return List of vehicles booked by the user.
     */
    public List<DBModelVehicle> getVehiclesBooked(int IDUser) {
        //String query = "SELECT * FROM " + TABLE_VEHICLE + " WHERE " + COLUMN_VEHICLE_ID_USER_BOOK + " = " + IDUser;

        String vehicleAlias = "vh";
        String addressesAlias = "addr";

        String queryJoin = "SELECT " + COLUMN_ADDRESS_ID + "," + COLUMN_ADDRESS_STREET + " FROM " + TABLE_ADDRESSES;
        String query = "SELECT " + vehicleAlias + ".*, " + addressesAlias + ".* FROM " + TABLE_VEHICLE + " AS " + vehicleAlias +
                " JOIN (" + queryJoin +") " + addressesAlias + " ON " + vehicleAlias + "." + COLUMN_VEHICLE_ID_ADDRESS + " = " + addressesAlias + "." + COLUMN_ADDRESS_ID +
                " WHERE " + COLUMN_VEHICLE_ID_USER_BOOK + " = " + IDUser + ";";

        Log.d(TAG_Database, "getVehiclesBooked: " + query);
        return getVehicles(query);
    }

    /**
     * @param IDUser actual user
     * @return List of all vehicles owned by the user
     */
    public List<DBModelVehicle> getVehiclesByUser(int IDUser){
        //String query = "SELECT * FROM " + TABLE_VEHICLE + " WHERE " + COLUMN_VEHICLE_ID_OWNER + " = " + IDUser;

        String query = "SELECT " + TABLE_VEHICLE + ".*, " + TABLE_MODULE + "." + COLUMN_MODULE_NAME + "," + TABLE_ADDRESSES + "." + COLUMN_ADDRESS_STREET + " FROM " + TABLE_VEHICLE +
                " INNER JOIN " + TABLE_MODULE + " ON " + TABLE_VEHICLE + "." + COLUMN_VEHICLE_ID_MODULE + " = " + TABLE_MODULE + "." + COLUMN_MODULE_ID +
                " INNER JOIN " + TABLE_ADDRESSES + " ON " + TABLE_VEHICLE + "." + COLUMN_VEHICLE_ID_ADDRESS + " = " + TABLE_ADDRESSES + "." + COLUMN_ADDRESS_ID +
                " WHERE " + TABLE_VEHICLE + "." + COLUMN_VEHICLE_ID_OWNER + " = " + IDUser ;

        Log.d(TAG_Database, "getVehiclesByUser: " + query);
        return getVehiclesJoin(query);
    }

    /**
     * @param query query with join
     * @return list of vehicles
     */
    private List<DBModelVehicle> getVehiclesJoin(String query){
        List<DBModelVehicle> returnList = new ArrayList<>();

        if (isConnectionError("getVehiclesJoin")) return returnList;

        // Get data from database
        try {
            // Execute query
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(query);

            // For each row returned
            while (rs.next()) {
                // Get values

                //TODO : i = rs.findColumn("column name");
                int vehicle_id = rs.getInt(1);
                String model = rs.getString(2);
                String licencePlate = rs.getString(3);
                int addressId = rs.getInt(4);
                int idOwner = rs.getInt(5);
                boolean isAvailable = rs.getBoolean(6);
                boolean isBooked = rs.getBoolean(7);
                int idUser = rs.getInt(8);
                int idModule = rs.getInt(9);
                String codeModule = rs.getString(10);
                String address = rs.getString(COLUMN_ADDRESS_STREET);

                // Create object and add it to the list
                DBModelVehicle vehicle = new DBModelVehicle(vehicle_id, model, licencePlate, addressId, idOwner, isAvailable, isBooked, idUser, idModule);
                vehicle.setAddress(address);
                vehicle.setCodeModule(codeModule);
                Log.d(TAG_Database, "getVehiclesJoin: " + vehicle);
                returnList.add(vehicle);
            }

            // Close both cursor and the database
            rs.close();
            st.close();
        }catch (Exception exception){
            Log.e(TAG_Database, "getVehiclesJoin: " , exception);
            exception.printStackTrace();
        }

        return returnList;
    }

    /**
     * @param ID id vehicle
     * @return return vehicle matching id
     */
    public DBModelVehicle getVehicleById(int ID){
        String query = "SELECT * FROM " + TABLE_VEHICLE + " WHERE " + COLUMN_VEHICLE_ID + " = " + ID;
        return getVehicles(query).get(0);
    }

    public DBModelVehicle getVehicleByModule(int moduleID){
        String query = "SELECT * FROM " + TABLE_VEHICLE + " WHERE " + COLUMN_VEHICLE_ID_MODULE + " = " + moduleID;

        DBModelVehicle vh = new DBModelVehicle();
        if (isConnectionError("getVehicles")) return vh;

        // Get data from database
        try {
            // Execute query
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(query);

            // For each row returned
            while (rs.next()) {
                // Get values
                vh.setId(rs.getInt(COLUMN_VEHICLE_ID));
            }

            // Close both cursor and the database
            rs.close();
            st.close();
        }catch (Exception exception){
            Log.e(TAG_Database, "getVehicles: " , exception);
            exception.printStackTrace();
        }

        return vh;
    }

    /**
     * @param query sql query
     * @return list of vehicles
     */
    private List<DBModelVehicle> getVehicles(String query){
        List<DBModelVehicle> returnList = new ArrayList<>();

        if (isConnectionError("getVehicles")) return returnList;

        // Get data from database
        try {
            // Execute query
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(query);

            // For each row returned
            while (rs.next()) {
                // Get values
                int vehicle_id = rs.getInt(1);
                String model = rs.getString(2);
                String licencePlate = rs.getString(3);
                int addressId = rs.getInt(4);
                int idOwner = rs.getInt(5);
                boolean isAvailable = rs.getBoolean(6);
                boolean isBooked = rs.getBoolean(7);
                int idUser = rs.getInt(8);
                int idModule = rs.getInt(9);
                String address = rs.getString(COLUMN_ADDRESS_STREET);

                // Create object and add it to the list
                DBModelVehicle vehicle = new DBModelVehicle(vehicle_id, model, licencePlate, addressId, idOwner, isAvailable, isBooked, idUser, idModule);
                vehicle.setAddress(address);

                Log.d(TAG_Database, "getVehicles: " + vehicle);
                returnList.add(vehicle);
            }

            // Close both cursor and the database
            rs.close();
            st.close();
        }catch (Exception exception){
            Log.e(TAG_Database, "getVehicles: " , exception);
            exception.printStackTrace();
        }

        return returnList;
    }

    public List<DBModelVehicle> getVehiclesJoinOwner(int IDUser) {

        String query = "SELECT " + TABLE_VEHICLE + ".*, " + TABLE_USER + "." + COLUMN_USER_NAME + "," + TABLE_ADDRESSES + "." + COLUMN_ADDRESS_STREET + " FROM " + TABLE_VEHICLE +
                " INNER JOIN " + TABLE_USER + " ON " + TABLE_VEHICLE + "." + COLUMN_VEHICLE_ID_OWNER + " = " + TABLE_USER + "." + COLUMN_USER_ID +
                " INNER JOIN " + TABLE_ADDRESSES + " ON " + TABLE_VEHICLE + "." + COLUMN_VEHICLE_ID_ADDRESS + " = " + TABLE_ADDRESSES + "." + COLUMN_ADDRESS_ID +
                " WHERE " + TABLE_VEHICLE + "." + COLUMN_VEHICLE_ID_USER_BOOK + " = " + IDUser ;

        Log.d(TAG_Database, "getVehiclesJoinOwner: query: " + query);

        List<DBModelVehicle> returnList = new ArrayList<>();

        if (isConnectionError("getVehiclesJoin")) return returnList;

        // Get data from database
        try {
            // Execute query
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(query);

            // For each row returned
            while (rs.next()) {
                // Get values
                int vehicle_id = rs.getInt(1);
                String model = rs.getString(2);
                String licencePlate = rs.getString(3);
                int addressId = rs.getInt(4);
                int idOwner = rs.getInt(5);
                boolean isAvailable = rs.getBoolean(6);
                boolean isBooked = rs.getBoolean(7);
                int idUser = rs.getInt(8);
                int idModule = rs.getInt(9);
                String ownerName = rs.getString(10);
                String address = rs.getString(COLUMN_ADDRESS_STREET);

                // Create object and add it to the list
                DBModelVehicle vehicle = new DBModelVehicle(vehicle_id, model, licencePlate, addressId, idOwner, isAvailable, isBooked, idUser, idModule);
                vehicle.setOwnerName(ownerName);
                vehicle.setAddress(address);
                Log.d(TAG_Database, "getVehiclesJoin: " + vehicle);
                returnList.add(vehicle);
            }

            // Close both cursor and the database
            rs.close();
            st.close();
        } catch (Exception exception) {
            Log.e(TAG_Database, "getVehiclesJoin: ", exception);
            exception.printStackTrace();
        }

        return returnList;
    }

    /*  ---------------------------------- *
     *  --            MODULES           -- *
     *  ---------------------------------- */

    /**
     * @param ID module id
     * @return module
     */
    public DBModelModule getModuleById(int ID){
        String query = "SELECT * FROM " + TABLE_MODULE + " WHERE " + COLUMN_MODULE_ID + " = " + ID;
        //return getModules(query).get(0);
        List<DBModelModule> modules = getModules(query);
        if (modules.size() == 0)
            return new DBModelModule();
        else
            return getModules(query).get(0);
    }

    /**
     * @param name identification name
     * @return module
     */
    public DBModelModule getModuleByName(String name){
        String query = "SELECT * FROM " + TABLE_MODULE + " WHERE " + COLUMN_MODULE_NAME + " = '" + name + "';";
        List<DBModelModule> modules = getModules(query);
        if (modules.size() == 0)
            return new DBModelModule();
        else
            return getModules(query).get(0);
    }

    /**
     * @param query query
     * @return list of modules
     */
    private List<DBModelModule> getModules(String query){
        List<DBModelModule> returnList = new ArrayList<>();

        if (isConnectionError("getModules")) return returnList;

        try {
            // Execute query
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(query);

            // Get data
            while (rs.next()) {
                // Get values
                int module_id = rs.getInt(1);
                String name = rs.getString(2);
                String mac_address = rs.getString(3);

                // Create object and add it to the list
                DBModelModule module = new DBModelModule(module_id, name, mac_address);
                Log.d(TAG_Database, "getModules: " + module);
                returnList.add(module);
            }

            // Close both cursor and the database
            rs.close();
            st.close();
        } catch (Exception exception){
            Log.e(TAG_Database, "getModules: " , exception);
            exception.printStackTrace();
        }
        return returnList;
    }


    /*  ---------------------------------- *
     *  --             CITY             -- *
     *  ---------------------------------- */

    public String [] getMatchingCities(String firstChar){
        String query = "SELECT * FROM " + TABLE_CITY + " WHERE " + COLUMN_CITY_NAME + " ILIKE '%" + firstChar + "%' ORDER BY " + COLUMN_CITY_NAME + " ASC LIMIT 3;";
        return getCities(query);
    }

    public String [] getCities(String query){
        ArrayList<String> matching = new ArrayList<>();

        if (isConnectionError("getMatchingCities")) return matching.toArray(new String[0]);

        try {
            // Execute query
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(query);

            // Get data
            while (rs.next()) {
                // Get values
                //int id = rs.getInt(1);
                String city = rs.getString(COLUMN_CITY_NAME);
                //String points = rs.getString(3);

                matching.add(city);
            }

            // Close both cursor and the database
            rs.close();
            st.close();
        } catch (Exception exception){
            Log.e(TAG_Database, "getMatchingCities: " , exception);
            exception.printStackTrace();
        }

        return matching.toArray(new String[0]);
    }

}