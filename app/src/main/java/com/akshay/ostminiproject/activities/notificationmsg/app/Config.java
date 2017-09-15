package com.akshay.ostminiproject.activities.notificationmsg.app;

public class Config {

    // global topic to receive app wide push notifications
    //TODO: change TOPIC_GLOBAL based on Teacher or Student
    //if student
    public static final String TOPIC_GLOBAL = "student";
    //if teacher
    // public static final String TOPIC_GLOBAL = "teacher";

    // broadcast receiver intent filters
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String PUSH_NOTIFICATION = "pushNotification";

    // id to handle the notification in the notification tray
    public static final int NOTIFICATION_ID = 100;
    public static final int NOTIFICATION_ID_BIG_IMAGE = 101;

    public static final String SHARED_PREF = "notif_firebase";
}
