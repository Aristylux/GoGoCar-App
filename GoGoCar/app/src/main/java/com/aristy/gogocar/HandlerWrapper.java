package com.aristy.gogocar;

import android.os.Handler;

import java.io.Serializable;

/**
 * {@link HandlerWrapper} is a custom class that holds a reference to a Handler object.<br>
 * It implements the Serializable interface,<br>
 * and provides a getter method to retrieve the Handler object.
 */
public class HandlerWrapper implements Serializable {
    private final transient Handler handler;

    public HandlerWrapper(Handler handler) {
        this.handler = handler;
    }

    public Handler getHandler() {
        return handler;
    }
}
