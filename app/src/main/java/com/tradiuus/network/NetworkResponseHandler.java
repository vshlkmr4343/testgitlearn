package com.tradiuus.network;

import android.util.Log;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public abstract class NetworkResponseHandler {
    public abstract void onSuccess(JSONObject response);

    public void withErrorDescription(String errorDescription) {
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put(NetworkController.NetworkKeys.ERROR_DESCRIPTION_KEY, errorDescription);
        this.onFailure(new JSONObject(errorMap));
    }

    public abstract void onFailure(JSONObject error);

    public String getErrorDescription(JSONObject error) {
        String errorDescription = "Unknown error occurred";
        try {
            errorDescription = error.getString(NetworkController.NetworkKeys.ERROR_DESCRIPTION_KEY);
        } catch (Exception e) {
            Log.e(getClass().getName(), "Could not determine error description from JSON");
        }
        return errorDescription;
    }
}