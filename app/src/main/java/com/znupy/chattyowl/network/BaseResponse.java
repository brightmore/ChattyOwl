package com.znupy.chattyowl.network;

/**
 * Created by samok on 27/07/14.
 */
public class BaseResponse {
    public boolean success;
    public String message;

    @Override
    public String toString() {
        return "BaseResponse (" + success + ") " + message;
    }
}
