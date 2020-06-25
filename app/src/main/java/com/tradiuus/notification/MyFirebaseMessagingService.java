package com.tradiuus.notification;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.tradiuus.R;
import com.tradiuus.TradiuusApp;
import com.tradiuus.activities.SplashActivity;
import com.tradiuus.activities.TechnicianEstimateRequestActivity;
import com.tradiuus.helper.UserPreference;
import com.tradiuus.models.MessageEvent;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();

    public static final String FCM_PARAM_SUBTITLE = "subtitle";
    public static final String FCM_PARAM_SMALL_ICON = "smallIcon";
    public static final String FCM_PARAM_SOUND = "sound";
    public static final String FCM_PARAM_TITLE = "title";
    public static final String FCM_PARAM_VIBRATE = "vibrate";
    public static final String FCM_PARAM_LARGE_ICON = "largeIcon";
    public static final String FCM_PARAM_MESSAGE = "message";
    public static final String FCM_PARAM_SEND_DATA = "sendData";
    public static final String FCM_PARAM_TICKER_TEXT = "tickerText";

    private static final String CHANNEL_NAME = "FCM";
    private static final String CHANNEL_DESC = "Firebase Cloud Messaging";
    private int numMessages = 0;

    public static final String FCM_NOTIFICATION_RECEIVER_TECH_1 = ".FCM_NOTIFICATION_RECEIVER_TECH_1";
    public static final String FCM_NOTIFICATION_RECEIVER_TECH_2 = ".FCM_NOTIFICATION_RECEIVER_TECH_2";

    public enum ActivityType {
        Customer, Technician, Contractor
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        try {
            RemoteMessage.Notification notification = remoteMessage.getNotification();
            Map<String, String> data = remoteMessage.getData();
            Log.d("FROM", remoteMessage.getFrom());
            if (UserPreference.getTechnician(MyFirebaseMessagingService.this) != null) {//For Technician
                sendNotification(MyFirebaseMessagingService.this, notification, data, ActivityType.Technician);
                EventBus.getDefault().post(new MessageEvent(data.get(FCM_PARAM_TITLE), data));
            } else if (UserPreference.getContractor(MyFirebaseMessagingService.this) != null) {//For Contractor
                sendNotification(MyFirebaseMessagingService.this, notification, data, ActivityType.Contractor);
                EventBus.getDefault().post(new MessageEvent(data.get(FCM_PARAM_TITLE), data));
            } else if (UserPreference.getCustomer(MyFirebaseMessagingService.this) != null) {//For Customer
                sendNotification(MyFirebaseMessagingService.this, notification, data, ActivityType.Customer);
                EventBus.getDefault().post(new MessageEvent(data.get(FCM_PARAM_TITLE), data));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onNewToken(String token) {
        try {
            Log.d(TAG, "Refreshed token: " + token);
            UserPreference.saveFCMToken(TradiuusApp.applicationContext, token);
            UserPreference.setPushTokenUpdate(TradiuusApp.applicationContext, false);
            ((TradiuusApp) getApplicationContext()).updateDeviceToken();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendNotification(Context context, RemoteMessage.Notification notification, Map<String, String> data, ActivityType type) {
        try {
            try {
                String dataPart = data.get(FCM_PARAM_SEND_DATA);
                JSONObject obj = new JSONObject(dataPart);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Intent intent = new Intent(this, SplashActivity.class);
            switch (type) {
                case Customer:
                    intent.putExtra(FCM_PARAM_TITLE, data.get(FCM_PARAM_TITLE));
                    intent.putExtra(FCM_PARAM_MESSAGE, data.get(FCM_PARAM_MESSAGE));
                    break;
                case Contractor:
                    intent.putExtra(FCM_PARAM_TITLE, data.get(FCM_PARAM_TITLE));
                    intent.putExtra(FCM_PARAM_MESSAGE, data.get(FCM_PARAM_MESSAGE));
                    break;
                case Technician:
                    intent.putExtra(FCM_PARAM_TITLE, data.get(FCM_PARAM_TITLE));
                    intent.putExtra(FCM_PARAM_MESSAGE, data.get(FCM_PARAM_MESSAGE));
                    break;
            }
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, getString(R.string.default_notification_channel_id))
                    .setContentTitle(data.get(FCM_PARAM_TITLE))
                    .setContentText(data.get(FCM_PARAM_MESSAGE))
                    .setAutoCancel(true)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    //.setSound(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.win))
                    .setContentIntent(pendingIntent)
                    .setContentInfo(data.get(FCM_PARAM_TITLE))
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                    //.setColor(context.getResources().getColor(R.color.app_primary_accent))
                    .setLights(Color.RED, 1000, 300)
                    .setDefaults(Notification.DEFAULT_VIBRATE)
                    .setNumber(numMessages)
                    .setSmallIcon(R.drawable.ic_notification);

            try {
                String picture = data.get(FCM_PARAM_LARGE_ICON);
                if (!TextUtils.isEmpty(picture) && (picture.contains("http://") || picture.contains("https://"))) {
                    URL url = new URL(picture);
                    Bitmap bigPicture = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                    notificationBuilder.setStyle(
                            new NotificationCompat.BigPictureStyle().bigPicture(bigPicture).setSummaryText(data.get(FCM_PARAM_MESSAGE))
                    );
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(
                        getString(R.string.default_notification_channel_id), CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT
                );
                channel.setDescription(CHANNEL_DESC);
                channel.setShowBadge(true);
                channel.canShowBadge();
                channel.enableLights(true);
                channel.setLightColor(Color.RED);
                channel.enableVibration(true);
                channel.setVibrationPattern(new long[]{100, 200, 300, 400, 500});
                assert notificationManager != null;
                notificationManager.createNotificationChannel(channel);
            }

            assert notificationManager != null;
            notificationManager.notify(NotificationID.getID(), notificationBuilder.build());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}