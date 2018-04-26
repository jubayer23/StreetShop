package com.creative.streetshop.appdata;



public class GlobalAppAccess {


    public static final String APP_NAME = "StreetShop";
    public static final String SECRET_TOKEN = "h7ZgdUgTAAmIDURV4sXUlwQ6QxFNrCME";
    public static String BaseUrl = "https://www.streetshops.mobi/api/rest/";
    //public static String BaseUrl = "https://b5e99a4d.ngrok.io/bgb/";
    public static final String URL_GET_SESSION =  BaseUrl + "session";
    public static final String URL_LOGIN_NORMAL = BaseUrl +  "login";
    public static final String URL_SOCIAL_LOGIN = BaseUrl +  "sociallogin";
    public static final String URL_REGISTER = BaseUrl +  "register";
    public static final String URL_RETRIVE_PASSWORD = BaseUrl +  "forgotten";
    public static final String URL_LOGOUT = BaseUrl +  "logout";


    public static final  int SUCCESS = 1;
    public static  final  int ERROR = 0;



    // global topic to receive app wide push notifications
    public static final String TOPIC_GLOBAL = "global";

    // broadcast receiver intent filters
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String PUSH_NOTIFICATION = "pushNotification";

    // id to handle the notification in the notification tray
    public static final int NOTIFICATION_ID = 100;
    public static final int NOTIFICATION_ID_BIG_IMAGE = 101;


    public static final String KEY_CALL_FROM = "call_from";
    public static final String KEY_NOTIFICATION_ID = "notification_id";

    public static final String TAG_ALARM_RECEIVER = "alarm_receiver";

    public static final String[] reminder_time_options = {"Select a time", "Before time expires", "15 mins before",
            "30 mins before","1 hour before"};

    public static final String[] rideshares_options = {"Uber", "Lyft", "Uber or Lyft"};

    public static final String[] seats_options = {"4 Seats", "5 Seats"};


    public static final String KEY_ERROR = "error";
    public static final String ERROR_TYPE_SUCCESS = "success";
    public static final String ERROR_TYPE_SERVER_PROBLEM = "server_problem";
    public static final String ERROR_TYPE_NETWORK_PROBLEM = "network_problem";

    public static  final  String consumerKey = "xi1FwgFSyQ9Oi076pGerW1fFP";
    public static final  String consumerKeySecrate = "DGxzXAgQVyPQptBi1E4pHReIbpuMx6e3EvqK5LIWFilYlOHPeY";
}
