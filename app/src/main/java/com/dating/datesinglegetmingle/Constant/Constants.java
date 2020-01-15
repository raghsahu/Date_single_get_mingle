package com.dating.datesinglegetmingle.Constant;

/**
 * Created by Nitin on 28/11/2017.
 */

public class Constants {

    // global topic to receive app wide push notifications
    public static final String TOPIC_GLOBAL = "global";

    // broadcast receiver intent filters
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String PUSH_NOTIFICATION = "pushNotification";

    // id to handle the notification in the notification tray
    public static final int NOTIFICATION_ID = 100;
    public static final int NOTIFICATION_ID_BIG_IMAGE = 101;

    public static final String SHARED_PREF = "ah_firebase";

    public static final int MY_PERMISSIONS_REQUEST_CEMERA_OR_GALLERY = 105;
    public final static int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    public final static int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;
    public static final int MY_PERMISSIONS_REQUEST_CEMERA = 103;

}
