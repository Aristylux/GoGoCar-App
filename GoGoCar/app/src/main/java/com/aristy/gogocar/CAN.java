package com.aristy.gogocar;

import static com.aristy.gogocar.CodesTAG.TAG_CAN;
import static com.aristy.gogocar.WebInterfaces.WICommon.Pages.Home.JS.UP_CAN_ENGINE_COOLANT;
import static com.aristy.gogocar.WebInterfaces.WICommon.Pages.Home.JS.UP_CAN_ENGINE_OIL;
import static com.aristy.gogocar.WebInterfaces.WICommon.Pages.Home.JS.UP_CAN_ENGINE_WATER;
import static com.aristy.gogocar.WebInterfaces.WICommon.Pages.Home.JS.UP_CAN_FUEL_CONSUMPTION;
import static com.aristy.gogocar.WebInterfaces.WICommon.Pages.Home.JS.UP_CAN_FUEL_LEVEL;
import static com.aristy.gogocar.WebInterfaces.WICommon.Pages.Home.JS.UP_CAN_SPEED_VALUE;

import android.util.Log;

public class CAN {

    /* Module to App */
    private static final String CODE_ENGINE_COOLANT = "t_li";
    private static final String CODE_SPEED = "vit_";
    private static final String CODE_TIME_ENGINE = "t_st";
    private static final String CODE_DISTANCE_WITH_PROBLEM = "d_st";
    private static final String CODE_FUEL_LEVEL = "l_cb";
    private static final String CODE_TYPE_FUEL = "carb";
    private static final String CODE_ACCELERATOR_PEDAL = "pped";
    private static final String CODE_ENGINE_OIL = "t_ol";
    private static final String CODE_FUEL_CONSUMPTION = "cons";
    private static final String CODE_ENGINE_WATER = "t_wt";

    /* App to module */
    public static final String DISABLE_SCRAMBLER = "$de_b:0";
    //public static final String STOP_COMMUNICATION = "$coms:0";

    /**
     * convert message to a object
     * @param type code type
     * @param data data
     * @return transformed data
     */
    public static ReceiverCAN transformMessage(String type, String data){
        ReceiverCAN can = new ReceiverCAN();
        can.setData(data);

        switch (type){
            case CODE_ENGINE_COOLANT:
                Log.d(TAG_CAN, "transformMessage: " + CODE_ENGINE_COOLANT + ": " + data);
                can.setJSMethod(UP_CAN_ENGINE_COOLANT);
                break;
            case CODE_SPEED:
                Log.d(TAG_CAN, "transformMessage: " + CODE_SPEED + ": " + data);
                can.setJSMethod(UP_CAN_SPEED_VALUE);
                break;
            case CODE_TIME_ENGINE:
                Log.d(TAG_CAN, "transformMessage: " + CODE_SPEED + ": " + data);
                break;
            case CODE_DISTANCE_WITH_PROBLEM:
                Log.d(TAG_CAN, "transformMessage: " + CODE_DISTANCE_WITH_PROBLEM + ": " + data);
                break;
            case CODE_FUEL_LEVEL:
                Log.d(TAG_CAN, "transformMessage: " + CODE_FUEL_LEVEL + ": " + data);
                can.setJSMethod(UP_CAN_FUEL_LEVEL);
                break;
            case CODE_TYPE_FUEL:
                Log.d(TAG_CAN, "transformMessage: " + CODE_TYPE_FUEL + ": " + data);
                break;
            case CODE_ACCELERATOR_PEDAL:
                Log.d(TAG_CAN, "transformMessage: " + CODE_ACCELERATOR_PEDAL + ": " + data);
                break;
            case CODE_ENGINE_OIL:
                Log.d(TAG_CAN, "transformMessage: " + CODE_ENGINE_OIL + ": " + data);
                can.setJSMethod(UP_CAN_ENGINE_OIL);
                break;
            case CODE_FUEL_CONSUMPTION:
                Log.d(TAG_CAN, "transformMessage: " + CODE_FUEL_CONSUMPTION + ": " + data);
                can.setJSMethod(UP_CAN_FUEL_CONSUMPTION);
                break;
            case CODE_ENGINE_WATER:
                Log.d(TAG_CAN, "transformMessage: " + CODE_ENGINE_WATER + ": " + data);
                can.setJSMethod(UP_CAN_ENGINE_WATER);
                break;
        }

        return can;
    }


}

class ReceiverCAN {

    private String JSMethod;
    private String data;
    private boolean isResulted;

    public ReceiverCAN(){
        this.isResulted = false;
    }

    public boolean isResulted(){
        return isResulted;
    }

    public void setData(String data){
        this.data = data;
    }

    public String getData(){
        return this.data;
    }

    public void setJSMethod(String method){
        this.JSMethod = method;
        this.isResulted = true;
    }

    public String getMethod(){
        return this.JSMethod;
    }

}