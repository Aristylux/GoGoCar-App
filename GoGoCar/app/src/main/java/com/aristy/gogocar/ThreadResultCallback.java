package com.aristy.gogocar;

import java.util.List;

public interface ThreadResultCallback {

    default void onResultEmpty(boolean resultEmpty) {
    }

    default void onResultUser(DBModelUser user){
    }

    default void onResultModule(DBModelModule module) {
    }

    default void onResultVehicles(List<DBModelVehicle> vehicles) {
    }

    default void onResultVehicle(DBModelVehicle vehicle){
    }

    default void onResultTableUpdated(boolean isUpdated) {
    }

}
