package com.dating.datesinglegetmingle.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.dating.datesinglegetmingle.Activity.ActivityLikeYou;
import com.dating.datesinglegetmingle.Activity.ActivityVisitProfile;
import com.dating.datesinglegetmingle.Activity.ChatActivity1;
import com.dating.datesinglegetmingle.Activity.MessageConversation;
import com.dating.datesinglegetmingle.Constant.Constants;
import com.dating.datesinglegetmingle.R;
import com.dating.datesinglegetmingle.globle.Session;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ravi on 14/12/2017.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();
    private String click = "";
    private String message = "";
    private String type = "";
    private String click_event = "";
    RemoteMessage remoteMessage;
    private String user_id = "";


    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        Session session = new Session(this);
        session.saveToken(s);

        Log.e("token_fcm_service", s);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e(TAG, "From: " + remoteMessage.getFrom());

        if (remoteMessage.getData().size() > 0) {
            //Log.e("remot_msg_count",""+remoteMessage.getData().size());
            Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());
            Log.e(TAG, "click:" + remoteMessage.getData().get("click"));
            String check = remoteMessage.getData().get("click");
            this.remoteMessage = remoteMessage;
            try {
                if (!check.equalsIgnoreCase("null")) {
                    //{register_id=foPNBAQXEIk:APA91bH_wIoQcuxp4c26lX_IdSuRzRUa28wWAsZ4OKpBB6hNJg-UUDqJEs7wwCPboO_kA6b6ub_GMJLvGwr-4cSDkZpEdjoJjLb6BhGeuuhQAReiNM7F_FWII2A7UZyDLc_ClDdIlw44, type=You got a notification from Single Mingle, click=click, message=Hi ravi,  You Got Like From aryansumit276}
                    //register_id = remoteMessage.getData().get("register_id");
                    click = remoteMessage.getData().get("click");
                    message = remoteMessage.getData().get("message");
                    type = remoteMessage.getData().get("type");
                    click_event = remoteMessage.getData().get("click_event");
                    user_id = remoteMessage.getData().get("user_id");

                    sendNotification(message, type, click_event, "Date Single Get Mingle", user_id);


                }
            } catch (Throwable e) {
                SetFirebaseMessage();

            }
        }

        if (remoteMessage == null)
            return;

        if (remoteMessage.getNotification() != null) {
            Log.e(TAG, "Notification Body: " + remoteMessage.getNotification().getBody());
            handleNotification(message);
        }
    }

    private void SetFirebaseMessage() {

        String fcmId = remoteMessage.getData().get("fcmId");
        //String udid = remoteMessage.getData().get("Udid");
        String userId = remoteMessage.getData().get("userId");
        String userName = remoteMessage.getData().get("userName");
        message = remoteMessage.getData().get("message");

        sendNotificationFirebase(message, userName, "Date Single Get Mingle");

        /*Intent intent = new Intent("custom-event-name");
        intent.putExtra("message", message);
        intent.putExtra("Udid", udid);*/
        // LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

        /*DatabaseHelper db = new DatabaseHelper(this);
        ChatUserModal modal = new ChatUserModal();
        modal.setFcmId(fcmId);
        modal.setUdid(udid);
        modal.setMessage(message);
        modal.setName(userName);
        modal.setUserid(userId);
        //Log.e("nudid", udid);

        if (db.checkUser(userId)) {
            db.updateUser(modal);
        } else {
            db.addUser(modal);
        }
        Log.e(TAG, "Data From Local: " + db.getAllUser());*/

                  /*else if (!check.equalsIgnoreCase("null")) {
                  //{register_id=foPNBAQXEIk:APA91bH_wIoQcuxp4c26lX_IdSuRzRUa28wWAsZ4OKpBB6hNJg-UUDqJEs7wwCPboO_kA6b6ub_GMJLvGwr-4cSDkZpEdjoJjLb6BhGeuuhQAReiNM7F_FWII2A7UZyDLc_ClDdIlw44, type=You got a notification from Single Mingle, click=click, message=Hi ravi,  You Got Like From aryansumit276}
                  //register_id = remoteMessage.getData().get("register_id");
                  click = remoteMessage.getData().get("click");
                  message = remoteMessage.getData().get("message");
                  type = remoteMessage.getData().get("type");
              }*/


    }


    private void sendNotificationFirebase(String message, String userName, String title) {
        PendingIntent pendingIntent = null;

        Intent intent = new Intent(this, MessageConversation.class);
        intent.putExtra("message", message);
        intent.putExtra("userName", userName);
        intent.putExtra("title", title);
        intent.putExtra("NOTIFICATION", "NOTIFICATION");


        /*intent.putExtra("sound", sound);
        intent.putExtra("click_action", click_action);*/

        playNotificationSound();
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);

        String CHANNEL_ID = "com.dating.datesinglegetmingle";// The id of the channel.
        CharSequence name = "MyChannal";// The user-visible name of the channel.
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel mChannel = null;


        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.logo)
                .setContentTitle(userName)
                .setContentText(message)
                .setPriority(Notification.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder.setSmallIcon(R.drawable.logo);
            notificationBuilder.setColor(getResources().getColor(R.color.colorPrimary));
        } else {
            notificationBuilder.setSmallIcon(R.drawable.logo);
        }

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(mChannel);
        }
        assert notificationManager != null;
        notificationManager.notify(1, notificationBuilder.build());

    }

    private void sendNotification(String message, String type, String click_event, String title, String user_id) {
        PendingIntent pendingIntent = null;
// 1-like, 2-super, 3-unlike, 4-visiter
        if (click_event.equalsIgnoreCase("4")) {
            Intent intent = new Intent(this, ActivityVisitProfile.class);
            intent.putExtra("message", message);
            intent.putExtra("type", type);
            intent.putExtra("title", title);
            intent.putExtra("user_id", user_id);
        /*intent.putExtra("sound", sound);
        intent.putExtra("click_action", click_action);*/

            playNotificationSound();
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);
        } else if (click_event.equalsIgnoreCase("1") || click_event.equalsIgnoreCase("2")) {
            Intent intent = new Intent(this, ActivityLikeYou.class);
            intent.putExtra("message", message);
            intent.putExtra("type", type);
            intent.putExtra("title", title);
            intent.putExtra("user_id", user_id);
        /*intent.putExtra("sound", sound);
        intent.putExtra("click_action", click_action);*/

            playNotificationSound();
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);
        }
    }


    /*private void sendNotification(String message, String type, String click_event, String title) {
        PendingIntent pendingIntent = null;

        if (click_event.equalsIgnoreCase("4")) {
            Intent intent = new Intent(this, Account.class);
            intent.putExtra("message", message);
            intent.putExtra("type", type);
            intent.putExtra("title", title);
        *//*intent.putExtra("sound", sound);
        intent.putExtra("click_action", click_action);*//*

            playNotificationSound();
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);
        } else {
            Intent intent = new Intent(this, Home.class);
            intent.putExtra("message", message);
            intent.putExtra("type", type);
            intent.putExtra("title", title);
        *//*intent.putExtra("sound", sound);
        intent.putExtra("click_action", click_action);*//*

            playNotificationSound();
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);
        }


        String CHANNEL_ID = "com.dating.datesinglegetmingle";// The id of the channel.
        CharSequence name = "MyChannal";// The user-visible name of the channel.
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel mChannel = null;


        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(Notification.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder.setSmallIcon(R.drawable.logo);
            notificationBuilder.setColor(getResources().getColor(R.color.colorPrimary));
        } else {
            notificationBuilder.setSmallIcon(R.drawable.logo);
        }

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(mChannel);
        }
        assert notificationManager != null;
        notificationManager.notify(1, notificationBuilder.build());
    }*/

    /**
     * Playing notification sound
     */
    public void playNotificationSound() {
        try {
            Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                    + "://" + getPackageName() + "/raw/notification");
            Ringtone r = RingtoneManager.getRingtone(this, alarmSound);
            r.play();

            /*Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            builder.setSound(alarmSound);*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleNotification(String message) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String format = simpleDateFormat.format(new Date());


        } catch (Exception e) {
            e.printStackTrace();
        }


        //new Code added
       /* if(NotificationUtils.isAppIsInBackground(getApplicationContext()))
        {
            Intent intent=new Intent(getApplicationContext().getPackageName()+"_FCM-MESSAGE");
            intent.putExtra("title",remoteMessage.getNotification().getTitle());
            intent.putExtra("message",remoteMessage.getNotification().getBody());
            LocalBroadcastManager localBroadcastManager=LocalBroadcastManager.getInstance(this);
            localBroadcastManager.sendBroadcast(intent);
        }else
        {
            sendNoti();
        }
        Intent pushNotification = new Intent(Constants.PUSH_NOTIFICATION);
        pushNotification.putExtra("message", message);
        NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
        notificationUtils.playNotificationSound();
        //finish new code
*/

        //old code
        if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
            // app is in foreground, broadcast the push message
            Intent pushNotification = new Intent(Constants.PUSH_NOTIFICATION);
            pushNotification.putExtra("message", message);
            pushNotification.putExtra("NOTIFICATION", "NOTIFICATION");
            // LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
            /*Intent intent = new Intent();
            Bundle b = new Bundle();
            b.putString("NOTIFICATION", "NOTIFICATION");
            intent.putExtra("NOTIFICATION", b);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);*/

            // play notification sound
            NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
            notificationUtils.playNotificationSound();
        } else {
            sendNoti();
            // If the app is in background, firebase itself handles the notification
        }
    }

    private void handleDataMessage(JSONObject json) {
        Log.e(TAG, "push json: " + json.toString());
        try {
            String fcmId = json.getString("fcmId");
            String udid = json.getString("Udid");
            String userId = json.getString("userId");
            String userName = json.getString("userName");
            String message = json.getString("message");

            Intent intent = new Intent("custom-event-name");
            intent.putExtra("message", message);
            intent.putExtra("Udid", udid);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

            /*DatabaseHelper db=new DatabaseHelper(this);
            ChatUserModal modal=new ChatUserModal();
            modal.setFcmId(fcmId);
            modal.setUdid(udid);
            modal.setMessage(message);
            modal.setUserName(userName);
            modal.setUserId(userId);
            Log.e("nudid",udid);

            if (db.checkUser(userId)){
                db.updateUser(modal);
            }else {
                db.addUser(modal);
            }
            Log.e(TAG, "Data From Local: " + db.getAllUser());*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendNoti() {
        Intent intent = new Intent(this, ChatActivity1.class);
        intent.putExtra("NOTIFICATION", "NOTIFICATION");
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder b = new NotificationCompat.Builder(this);
        String channelId = "Default";

        b.setAutoCancel(false);
        b.setDefaults(Notification.DEFAULT_ALL);
        b.setWhen(System.currentTimeMillis());
        b.setSmallIcon(R.drawable.logo);
        b.setTicker("Hearty365");
        b.setContentTitle("Default notification");
        b.setContentText("message");
        b.setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND);
        b.setContentIntent(contentIntent);
        b.setContentInfo("Info");

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "Default channel", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }
        notificationManager.notify(1, b.build());
    }


    /*private void handleNotification(String message) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            format = simpleDateFormat.format(new Date());


        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
            // app is in foreground, broadcast the push message
            Intent pushNotification = new Intent(Constants.PUSH_NOTIFICATION);
            pushNotification.putExtra("message", message);
            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
            // play notification sound
            NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
            notificationUtils.playNotificationSound();

        } else {
            // If the app is in background, firebase itself handles the notification
        }
    }

    private void handleDataMessage(JSONObject json) {
        Log.e(TAG, "push json: " + json.toString());
        System.out.println("----------------------json----------------" + json);

        try {

            JSONObject data = json.getJSONObject("message");
            System.out.println("----------------------json----------------" + data);
            String result = data.getString("result");
            String key = data.getString("key");
            String username = data.getString("username");
            String msg = data.getString("msg");
            Intent resultIntent = new Intent(getApplicationContext(), Home.class);

            try {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                format = simpleDateFormat.format(new Date());
                Log.e(TAG, "push json: " + json.toString());

            } catch (Exception e) {
                e.printStackTrace();
            }

            if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
                // app is in foreground, broadcast the push message
                Intent pushNotification = new Intent(Constants.PUSH_NOTIFICATION);
                pushNotification.putExtra("message", data.toString());
                LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

                if (key.equalsIgnoreCase("You have a geofencing message")) {
                    showNotificationMessage(getApplicationContext(), "geofencing Message from " + username, msg, format, resultIntent);

                }

                if (key.equalsIgnoreCase("You have a push notification")) {
                    showNotificationMessage(getApplicationContext(), "Message from " + username, msg, format, resultIntent);

                }


            } else {

                // app is in background, show the notification in notification tray
                Log.e("hello like", "else");

                if (key.equalsIgnoreCase("You have a push notification")) {
                    showNotificationMessage(getApplicationContext(), "Message from " + username, msg, format, resultIntent);

                }

                if (key.equalsIgnoreCase("You have a geofencing message")) {
                    showNotificationMessage(getApplicationContext(), "geofencing Message from " + username, msg, format, resultIntent);

                }


            }
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }

    *//**
     * Showing notification with text only
     *//*
    private void showNotificationMessage(Context context, String title, String message, String timeStamp, Intent intent) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent);
    }

    *//**
     * Showing notification with text and image
     *//*
    private void showNotificationMessageWithBigImage(Context context, String title, String message, String timeStamp, Intent intent, String imageUrl) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent, imageUrl);
    }*/
}