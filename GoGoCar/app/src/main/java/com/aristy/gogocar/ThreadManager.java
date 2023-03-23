package com.aristy.gogocar;

import static com.aristy.gogocar.CodesTAG.TAG_THREAD;

import android.util.Log;

public class ThreadManager {
    private static ThreadManager instance;
    private Thread thread;

    private ThreadResultCallback callback;

    private DatabaseHelper databaseHelper;

    private ThreadManager() {}

    public static synchronized ThreadManager getInstance() {
        if (instance == null) {
            Log.d(TAG_THREAD, "getInstance: null");
            instance = new ThreadManager();
        }
        return instance;
    }

    public void setConnection(){
        ConnectionHelper connectionHelper = new ConnectionHelper();
        connectionHelper.openConnection();
        databaseHelper = new DatabaseHelper(connectionHelper.getConnection());
    }

    public void startThread() {
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG_THREAD, "run: ");
                // Thread code here
                DBModelModule module = databaseHelper.getModuleByName("#01-01-0001");

                if (callback != null) {
                    //callback.onResultCalculated(100);
                    callback.onResultModule(module);
                }

            }
        });
        thread.start();
    }

    public Thread getThread() {
        return thread;
    }

    public void setResultCallback(ThreadResultCallback callback) {
        this.callback = callback;
    }
}
