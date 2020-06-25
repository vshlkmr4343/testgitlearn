package com.tradiuus.models;

import java.util.HashMap;
import java.util.Map;

public class MessageEvent {
    public String mMessage;
    public Map<String, String> data = new HashMap();

    public MessageEvent() {
    }

    public MessageEvent(String message, Map<String, String> data) {
        this.mMessage = message;
        this.data = data;
    }

    public String getMessage() {
        return mMessage;
    }

    public Map<String, String> getData() {
        return data;
    }

    public void setData(Map<String, String> data) {
        this.data = data;
    }
}
