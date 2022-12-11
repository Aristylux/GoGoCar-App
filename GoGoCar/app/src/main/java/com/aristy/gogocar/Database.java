package com.aristy.gogocar;

import static com.aristy.gogocar.CodesTAG.TAG_Database;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Database {

    private static final String[] vehicle_1 = {"1", "Nissan GT", "26 rue General de Gaulle"};
    private static final String[] vehicle_2 = {"2", "Peugeot 206", "18 Boulevard Jules Ferry"};

    private static final String[][] table_vehicle = {vehicle_1, vehicle_2};

    private static final String table_vehicle_json = createBase(vehicle_1, vehicle_2);

    //private static final JSONArray[] table_vehicle = {vehicle_1_json, vehicle_2_json};

    private static int selectRow = 0;

    private static String createBase(String[]... rows){
        JSONObject map;
        JSONArray array = new JSONArray();
        try {
            for(String[] row : rows){
                map = new JSONObject();
                map.put("id", Integer.parseInt(row[0]));
                map.put("name", row[1]);
                map.put("address", row[2]);
                array.put(map);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d(TAG_Database, "createBase: " + array);
        return array.toString();
    }

    public static void selectRow(int tableId){
        selectRow = tableId - 1;
    }

    /*
    public static JSONArray[] getTable(){
        return table_vehicle;
    }
*/
    public static String getNewTable(){
        return table_vehicle_json;
    }
/*
    public static JSONArray getVehicle(){
        return table_vehicle[selectRow];
    }
*/
    public static String getVehicleId(){
        return table_vehicle[selectRow][0];
    }

    public static String getVehicleName(){
        return table_vehicle[selectRow][1];
    }

    public static String getVehiclePosition(){
        return table_vehicle[selectRow][2];
    }


}
