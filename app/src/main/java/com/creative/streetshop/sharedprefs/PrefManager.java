package com.creative.streetshop.sharedprefs;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.creative.streetshop.BuildConfig;
import com.creative.streetshop.model.UserData;
import com.google.gson.Gson;


/**
 * Created by jubayer on 6/6/2017.
 */


public class PrefManager {
    private static final String TAG = PrefManager.class.getSimpleName();

    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    private static Gson GSON = new Gson();
    // Sharedpref file name
    private static final String PREF_NAME = BuildConfig.APPLICATION_ID;

    private static final String KEY_NUM_OF_TIME_USER_SET_ALARM = "num_of_time_user_set_alarm";
    private static final String KEY_SESSION = "key_session";
    private static final String KEY_USER_DATA = "user_data";

    public PrefManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);

    }

    public void setNumberOfTimeUserSetAlarm(int obj) {
        editor = pref.edit();

        editor.putInt(KEY_NUM_OF_TIME_USER_SET_ALARM, obj);

        // commit changes
        editor.commit();
    }
    public int getNumberOfTimeUserSetAlarm() {
        return pref.getInt(KEY_NUM_OF_TIME_USER_SET_ALARM,0);
    }


    public void setSession(String obj) {
        editor = pref.edit();

        editor.putString(KEY_SESSION, obj);

        // commit changes
        editor.commit();
    }
    public String getSession() {
        return pref.getString(KEY_SESSION,"");
    }

    public void setUserData(UserData updateChecker) {
        editor = pref.edit();

        editor.putString(KEY_USER_DATA, GSON.toJson(updateChecker));

        // commit changes
        editor.commit();
    }

    public void setUserData(String obj) {
        editor = pref.edit();

        editor.putString(KEY_USER_DATA, obj);

        // commit changes
        editor.commit();
    }

    public UserData getUserData() {

        String gson = pref.getString(KEY_USER_DATA, "");
        if (gson.isEmpty()) return null;
        return GSON.fromJson(gson, UserData.class);
    }


}