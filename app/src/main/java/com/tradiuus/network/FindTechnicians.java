package com.tradiuus.network;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.tradiuus.TradiuusApp;
import com.tradiuus.models.AddressInfo;
import com.tradiuus.models.Customer;
import com.tradiuus.models.CustomerImpl;
import com.tradiuus.models.Job;
import com.tradiuus.models.Technician;

import java.util.ArrayList;

public class FindTechnicians extends Service {

    private static String LOG_TAG = "FindTechnicians";
    private IBinder mBinder = new MyBinder();

    private Context context;
    public Customer customer;
    public CustomerImpl customerImpl;
    private LatLng currentLatLang;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.v(LOG_TAG, "in onCreate");

        try {
            context = FindTechnicians.this;
            customer = ((TradiuusApp) context.getApplicationContext()).customer;
            customerImpl = new CustomerImpl();
            customerImpl.init(context, onCustomerDashboardListener);
            currentLatLang = new LatLng(Double.valueOf(customer.getLat()), Double.valueOf(customer.getLng()));
        } catch (Exception e) {
            Log.v(LOG_TAG + "->onCreate:", e.getMessage());
            e.printStackTrace();
        }
        syncJobAndTech();
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.v(LOG_TAG, "in onBind");
        return mBinder;
    }

    @Override
    public void onRebind(Intent intent) {
        Log.v(LOG_TAG, "in onRebind");
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.v(LOG_TAG, "in onUnbind");
        return true;
    }

    @Override
    public void onDestroy() {
        stopTask();
        super.onDestroy();
        Log.v(LOG_TAG, "in onDestroy");
    }

    public class MyBinder extends Binder {
        public FindTechnicians getService() {
            return FindTechnicians.this;
        }
    }


    private Handler handler = new Handler();
    private Runnable syncTask = new Runnable() {
        @Override
        public void run() {
            syncJobAndTech();
        }
    };

    private void startTask() {
        try {
            handler.removeCallbacks(syncTask);
            handler.postDelayed(syncTask, 3 * 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stopTask() {
        try {
            handler.removeCallbacks(syncTask);
        } catch (Exception e) {
            e.printStackTrace(); //menu_mapannotation_icon
        }
    }

    private void syncJobAndTech() {
        try {
            if (currentLatLang != null && customerImpl != null && customer != null) {
                customer = ((TradiuusApp) context.getApplicationContext()).customer;
                //String customer_id, String user_id, String latitude, String longitude, String page, String distance
                customerImpl.getTechnicianNearMe(customer.getCustomer_id(), customer.getUser_id(),
                        String.valueOf(currentLatLang.latitude), String.valueOf(currentLatLang.longitude), String.valueOf(1), String.valueOf(customer.getMax_distance()));
                customerImpl.getCustomerJobHistoryService(customer.getCustomer_id(), customer.getUser_type());
            } else {
                startTask();
            }
            Log.i("syncTask -> ", System.currentTimeMillis() + "");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private CustomerImpl.OnCustomerDashboardListener onCustomerDashboardListener = new CustomerImpl.OnCustomerDashboardListener() {
        @Override
        public void foundTechnicians(ArrayList<Technician> technicians) {
            try {
                if (customerSyncListener != null) {
                    customerSyncListener.foundTechnicians(technicians);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            startTask();
        }

        @Override
        public void jobHistory(ArrayList<Job> emergencyJobs, ArrayList<Job> estimateJobs) {
            try {
                if (customerSyncListener != null) {
                    customerSyncListener.jobHistory(emergencyJobs, estimateJobs);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void removeAllTech(String message) {
            try {
                if (customerSyncListener != null) {
                    customerSyncListener.removeAllTech(message);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            startTask();
        }

        @Override
        public void locationUpdate(AddressInfo mAddressInfo, String message) {

        }

        @Override
        public void onPasswordUpdateSuccess(String message) {

        }

        @Override
        public void logoutSuccess(String message) {

        }

        @Override
        public void onShowPgDialog() {

        }

        @Override
        public void onHidePgDialog() {

        }

        @Override
        public void showAlert(String message) {

        }
    };

    public CustomerSyncListener customerSyncListener;

    public CustomerSyncListener getCustomerSyncListener() {
        return customerSyncListener;
    }

    public void setCustomerSyncListener(CustomerSyncListener customerSyncListener) {
        this.customerSyncListener = customerSyncListener;
    }

    public interface CustomerSyncListener {
        public void foundTechnicians(ArrayList<Technician> technicians);

        public void jobHistory(ArrayList<Job> emergencyJobs, ArrayList<Job> estimateJobs);

        public void removeAllTech(String message);
    }
}