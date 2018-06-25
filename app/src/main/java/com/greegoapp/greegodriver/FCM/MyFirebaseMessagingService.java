package com.greegoapp.greegodriver.FCM;

import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.greegoapp.greegodriver.Activity.AcceptUserRequestActivity;
import com.greegoapp.greegodriver.Activity.HomeActivity;
import com.greegoapp.greegodriver.Activity.MainActivity;
import com.greegoapp.greegodriver.Activity.UserRateActivity;
import com.greegoapp.greegodriver.Model.GetDriverData;
import com.greegoapp.greegodriver.R;
import com.greegoapp.greegodriver.Utils.Applog;

import org.json.JSONObject;

import java.util.List;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();

    private NotificationUtils notificationUtils;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e(TAG, "From: " + remoteMessage.getFrom());

        if (remoteMessage == null)
            return;

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.e(TAG, "Notification Body: " + remoteMessage.getNotification().getBody());
            handleNotification(remoteMessage.getNotification().getBody());
        }

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());

            try {
                JSONObject json = new JSONObject(remoteMessage.getData().toString());
                handleDataMessage(json,remoteMessage);
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }

    }


    @Override
    public void onMessageSent(String s) {
        super.onMessageSent(s);
        Log.d("firebase", s);
    }

    private void handleNotification(String message) {
        if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
            // app is in foreground, broadcast the push message
            Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
            pushNotification.putExtra("message", message);
            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

            // play notification sound
           /* NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
            notificationUtils.playNotificationSound();*/
        } else {
            Log.d("message", "background handle notification start");
//            sendNotification("gm");
            /*if (message.contains("want to drive")) {

                *//*Intent intent = new Intent(this, AcceptUserRequestActivity.class);
                startActivity(intent);*//*
//                showNotificationMessage(getApplicationContext(),"",message,"",new Intent(getApplicationContext(),AcceptUserRequestActivity.class));
                *//*LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(MyFirebaseMessagingService.this);
                localBroadcastManager.sendBroadcast(new Intent("com.close"));*//*
            }
*/

            // If the app is in background, firebase itself handles the notification

        }
        Log.d("message", "background app notification");
    }

    private void sendNotification(String messageBody)/*, Bitmap image, String TrueOrFalse)*/ {
//        Intent intent = new Intent(this, HomeActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        intent.putExtra("AnotherActivity", "true");
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
//                PendingIntent.FLAG_ONE_SHOT);
//
//        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
//                /*.setLargeIcon(getDrawabl(R.mipmap.ic_greego_logo))*//*Notification icon image*/
//                .setSmallIcon(R.mipmap.ic_greego_logo)
//                .setContentTitle(messageBody)
////                .setStyle(new NotificationCompat.BigPictureStyle()
////                        .bigPicture(image))/*Notification with Image*/
//                .setAutoCancel(true)
//                .setSound(defaultSoundUri)
//                .setContentIntent(pendingIntent);
//
//        NotificationManager notificationManager =
//                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    private void handleDataMessage(JSONObject json, RemoteMessage remoteMessage) {
        Log.e(TAG, "push json: " + json.toString());
        Intent intent = new Intent(getApplicationContext(), AcceptUserRequestActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        String string = json.toString();
        String response = getApplicationContext().getSharedPreferences("driverData", 0).getString("driver", "0");
        GetDriverData driverData = new Gson().fromJson(String.valueOf(response), GetDriverData.class);
        Log.i("admin", driverData.getData().getIs_approve());
        if (string.contains("status")) {
            if (isForeground("com.greegoapp.greegodriver.Activity.AcceptUserRequestActivity")) {
                Applog.E("json:" + "trip going on");
                NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
                notificationUtils.playNotificationSound();
                LocalBroadcastManager localBroadcastManager = LocalBroadcastManager
                        .getInstance(MyFirebaseMessagingService.this);
                localBroadcastManager.sendBroadcast(new Intent(
                        "com.durga.action.close"));
                Applog.E("json:" + "trip going on");
            }
        } else if (string.contains("driver_id")) {
            Applog.E("json: driver accept");
            NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
            notificationUtils.playNotificationSound();
            LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(MyFirebaseMessagingService.this);
            localBroadcastManager.sendBroadcast(new Intent("com.close").putExtra("msg", remoteMessage.getNotification().getBody()));
        } else /*if (driverData.getData().getIs_approve().equals("1") && driverData.getData().getDriver_on().equals("1")) */ {
            string = string.substring(string.indexOf(":") + 1, string.length() - 1);
            intent.putExtra("message", string);
            if (!AcceptUserRequestActivity.active) {
                if (!UserRateActivity.active) {
                    NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
//                notificationUtils.playNotificationSound();

                    startActivity(intent);
                }
            }
        }
/*
        try {
            JSONObject data = json.getJSONObject("data");

            String title = data.getString("title");
            String message = data.getString("message");
            boolean isBackground = data.getBoolean("is_background");
            String imageUrl = data.getString("image");
            String timestamp = data.getString("timestamp");
            JSONObject payload = data.getJSONObject("payload");

            Log.e(TAG, "title: " + title);
            Log.e(TAG, "message: " + message);
            Log.e(TAG, "isBackground: " + isBackground);
            Log.e(TAG, "payload: " + payload.toString());
            Log.e(TAG, "imageUrl: " + imageUrl);
            Log.e(TAG, "timestamp: " + timestamp);


            if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
                // app is in foreground, broadcast the push message
                Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
                pushNotification.putExtra("message", message);
                LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
                Toast.makeText(this, "1", Toast.LENGTH_SHORT).show();
                // play notification sound
                NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
                notificationUtils.playNotificationSound();
            } else {
                // app is in background, show the notification in notification tray
                Intent resultIntent = new Intent(getApplicationContext(), AcceptUserRequestActivity.class);
                resultIntent.putExtra("message", message);
                startActivity(resultIntent);
                Toast.makeText(this, "2", Toast.LENGTH_SHORT).show();
                // check for image attachment
                if (TextUtils.isEmpty(imageUrl)) {
                    showNotificationMessage(getApplicationContext(), title, message, timestamp, resultIntent);
                    Toast.makeText(this, "3", Toast.LENGTH_SHORT).show();
                } else {
                    // image is present, show notification with image
                    showNotificationMessageWithBigImage(getApplicationContext(), title, message, timestamp, resultIntent, imageUrl);
                    Toast.makeText(this, "4", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "Json Exception: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }*/
    }

    /**
     * Showing notification with text only
     */
    private void showNotificationMessage(Context context, String title, String message, String timeStamp, Intent intent) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent);
    }

    /**
     * Showing notification with text and image
     */
    private void showNotificationMessageWithBigImage(Context context, String title, String message, String timeStamp, Intent intent, String imageUrl) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent, imageUrl);
    }

    public boolean isForeground(String myPackage) {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTaskInfo = manager.getRunningTasks(1);
        ComponentName componentInfo = runningTaskInfo.get(0).topActivity;
        Applog.E("json:" + componentInfo.getClassName());
        return componentInfo.getClassName().equals(myPackage);/*componentInfo.getPackageName().equals(myPackage);*/
    }
}
