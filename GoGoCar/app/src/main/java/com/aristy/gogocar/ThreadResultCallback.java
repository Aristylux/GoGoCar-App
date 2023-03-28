package com.aristy.gogocar;

import java.util.List;

public interface ThreadResultCallback {

    // Test
    default void onResultCalculated(int result) {
    }

    default void onResultUser(DBModelUser user){
    }

    default void onResultModule(DBModelModule module) {
    }

    default void onResultVehicles(List<DBModelVehicle> vehicles) {
    }

    default void onResultTableUpdated(boolean isUpdated) {
    }

}
