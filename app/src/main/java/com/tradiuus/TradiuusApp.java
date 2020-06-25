package com.tradiuus;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.tradiuus.helper.UserPreference;
import com.tradiuus.models.ConfigData;
import com.tradiuus.models.Contractor;
import com.tradiuus.models.Customer;
import com.tradiuus.models.Job;
import com.tradiuus.models.Question;
import com.tradiuus.models.Service;
import com.tradiuus.models.Technician;
import com.tradiuus.models.Trade;
import com.tradiuus.network.NetworkController;

import java.util.ArrayList;

public class TradiuusApp extends Application implements Application.ActivityLifecycleCallbacks {

    public static volatile Context applicationContext = null;
    public ConfigData mConfigData;
    public Customer customer;
    public Contractor mContractor;
    public Technician mTechnician;
    public Trade mTrade;
    public Job job;
    public Service mServiceParam;
    public ArrayList<Question> qsList = new ArrayList<>();
    public ArrayList<Job> emergencyJobList = new ArrayList<>();
    public ArrayList<Job> estimateJobList = new ArrayList<>();
    public ArrayList<String> imagesList = new ArrayList<>();
    public Job jobDetail;
    public LatLng currentLatLan;
    public  Activity lastActivity;

    @Override
    public void onCreate() {
        super.onCreate();
        applicationContext = TradiuusApp.this;

        try {
            ConfigData configData = UserPreference.getConfigData(applicationContext);
            NetworkController.setContext(getApplicationContext());
            initImageLoader(getApplicationContext());
            initPushNotification();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initPushNotification() {
        try {
            FirebaseApp.initializeApp(applicationContext);
            FirebaseMessaging.getInstance().setAutoInitEnabled(true);
            FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                @Override
                public void onComplete(@NonNull Task<InstanceIdResult> task) {
                    try {
                        if (!task.isSuccessful()) {
                            Log.w("FCM", "getInstanceId failed", task.getException());
                            return;
                        }
                        // Get new Instance ID token
                        String token = task.getResult().getToken();
                        UserPreference.saveFCMToken(applicationContext, token);
                        UserPreference.setPushTokenUpdate(applicationContext, false);
                        ((TradiuusApp) getApplicationContext()).updateDeviceToken();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void initImageLoader(Context context) {
        // This configuration tuning is custom. You can tune every option, you may tune some of them,
        // or you can create default configuration by
        //  ImageLoaderConfiguration.createDefault(this);
        // method.
        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
        config.threadPriority(Thread.NORM_PRIORITY - 2);
        config.denyCacheImageMultipleSizesInMemory();
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
        config.tasksProcessingOrder(QueueProcessingType.LIFO);
        config.writeDebugLogs(); // Remove for release app

        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config.build());
    }

    public void updateDeviceToken() {
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {
        lastActivity = activity;
    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }
}
