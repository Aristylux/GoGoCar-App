package com.aristy.gogocar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Database {

    private static final String[] vehicle_1 = {"1", "Nissan GT", "26 rue General de Gaulle"};
    private static final String[] vehicle_2 = {"2", "Peugeot 206", "18 Boulevard Jules Ferry"};

    private static final JSONArray vehicle_1_json = convertJSON(vehicle_1);
    private static final JSONArray vehicle_2_json = convertJSON(vehicle_2);

    private static final String[][] table_vehicle = {vehicle_1, vehicle_2};

    //private static final JSONArray[] table_vehicle = {vehicle_1_json, vehicle_2_json};

    private static int selectRow = 0;

    private static JSONArray convertJSON(String[] table) {
        JSONArray jsonArray = new JSONArray();
        for (String strings : table) {
            jsonArray.put(strings);
        }
        return jsonArray;
    }

    private static String createBase(){
        JSONObject map;
        JSONArray array = new JSONArray();
        try {
            map = new JSONObject();
            map.put("id",1);
            map.put("name", vehicle_1[1]);
            map.put("address", vehicle_1[2]);
            array.put(map);

            map = new JSONObject();
            map.put("id",2);
            map.put("name", vehicle_2[1]);
            map.put("address", vehicle_2[2]);
            array.put(map);
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
        return createBase();
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
