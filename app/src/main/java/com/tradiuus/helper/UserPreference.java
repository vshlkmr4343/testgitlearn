package com.tradiuus.helper;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.tradiuus.TradiuusApp;
import com.tradiuus.models.ConfigData;
import com.tradiuus.models.Contractor;
import com.tradiuus.models.Customer;
import com.tradiuus.models.Technician;

public class UserPreference {
    public static SharedPreferences getPreference(Context context) {
        return context.getSharedPreferences(ConstantKey.KEY_SHARE_PREF, Activity.MODE_PRIVATE);
    }

    public static void saveConfigData(Context context, ConfigData mConfigData) {
        SharedPreferences.Editor prefsEditor = getPreference(context).edit();
        Gson gson = new Gson();
        String json = gson.toJson(mConfigData);
        prefsEditor.putString(ConstantKey.KEY_CONFIG, json);
        prefsEditor.commit();
        ((TradiuusApp) context.getApplicationContext()).mConfigData = mConfigData;
    }

    public static ConfigData getConfigData(Context context) {
        SharedPreferences mPrefs = getPreference(context);
        Gson gson = new Gson();
        String json = mPrefs.getString(ConstantKey.KEY_CONFIG, "");
        ConfigData obj = gson.fromJson(json, ConfigData.class);
        if (obj != null) {
            ((TradiuusApp) context.getApplicationContext()).mConfigData = obj;
        }
        return obj;
    }

    public static void saveCustomer(Context context, Customer mCustomer) {
        SharedPreferences.Editor prefsEditor = getPreference(context).edit();
        Gson gson = new Gson();
        String json = gson.toJson(mCustomer);
        prefsEditor.putString(ConstantKey.KEY_CUSTOMER, json);
        prefsEditor.commit();
        ((TradiuusApp) context.getApplicationContext()).customer = mCustomer;
    }

    public static Customer getCustomer(Context context) {
        SharedPreferences mPrefs = getPreference(context);
        Gson gson = new Gson();
        String json = mPrefs.getString(ConstantKey.KEY_CUSTOMER, "");
        Customer obj = null;
        if (!TextUtils.isEmpty(json)) {
            obj = gson.fromJson(json, Customer.class);
            if (obj != null) {
                ((TradiuusApp) context.getApplicationContext()).customer = obj;
            }
        }
        return obj;
    }

    public static void saveContractor(Context context, Contractor mContractor) {
        try {
            SharedPreferences.Editor prefsEditor = getPreference(context).edit();
            Gson gson = new Gson();
            String json = gson.toJson(mContractor);
            prefsEditor.putString(ConstantKey.KEY_CONTRACTOR, json);
            prefsEditor.commit();
            ((TradiuusApp) context.getApplicationContext()).mContractor = mContractor;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Contractor getContractor(Context context) {
        SharedPreferences mPrefs = getPreference(context);
        Gson gson = new Gson();
        String json = mPrefs.getString(ConstantKey.KEY_CONTRACTOR, "");
        Contractor obj = null;
        if (!TextUtils.isEmpty(json)) {
            obj = gson.fromJson(json, Contractor.class);
            if (obj != null) {
                ((TradiuusApp) context.getApplicationContext()).mContractor = obj;
            }
        }
        return obj;
    }

    public static void saveTechnician(Context context, Technician mTechnician) {
        SharedPreferences.Editor prefsEditor = getPreference(context).edit();
        Gson gson = new Gson();
        String json = gson.toJson(mTechnician);
        prefsEditor.putString(ConstantKey.KEY_TECHNICIAN, json);
        prefsEditor.commit();
        ((TradiuusApp) context.getApplicationContext()).mTechnician = mTechnician;
    }

    public static Technician getTechnician(Context context) {
        SharedPreferences mPrefs = getPreference(context);
        Gson gson = new Gson();
        String json = mPrefs.getString(ConstantKey.KEY_TECHNICIAN, "");
        Technician obj = null;
        if (!TextUtils.isEmpty(json)) {
            obj = gson.fromJson(json, Technician.class);
            if (obj != null) {
                ((TradiuusApp) context.getApplicationContext()).mTechnician = obj;
            }
        }
        return obj;
    }

    public static void saveFCMToken(Context context, String token) {
        try {
            SharedPreferences.Editor prefsEditor = getPreference(context).edit();
            prefsEditor.putString("token", token);
            prefsEditor.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getFCMToken(Context context) {
        SharedPreferences mPrefs = getPreference(context);
        return mPrefs.getString("token", "123456");
    }

    public static void setPushTokenUpdate(Context context, boolean isUpdated) {
        try {
            SharedPreferences.Editor prefsEditor = getPreference(context).edit();
            prefsEditor.putBoolean("PushToken", isUpdated);
            prefsEditor.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean getPushTokenUpdate(Context context) {
        SharedPreferences mPrefs = getPreference(context);
        return mPrefs.getBoolean("PushToken", false);
    }
}
