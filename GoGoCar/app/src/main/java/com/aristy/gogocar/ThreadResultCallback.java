package com.aristy.gogocar;

public interface ThreadResultCallback {

    // Test
    default void onResultCalculated(int result){
    }

    default void onResultModule(DBModelModule module){
    }
}
