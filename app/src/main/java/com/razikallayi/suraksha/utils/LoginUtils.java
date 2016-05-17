package com.razikallayi.suraksha.utils;

import android.content.Context;
import android.os.SystemClock;
import android.preference.PreferenceManager;

/**
 * Created by Razi Kallayi on 10-05-2016.
 */
public class LoginUtils {

    public static void logout(Context context) {
        SettingsUtils.setLoggedIn(context,false);
        SettingsUtils.setRecentOfficer(context,null);
    }

    public static void login(Context context,String username) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit().putLong("locked_at", SystemClock.elapsedRealtime()).commit();
        SettingsUtils.setLoggedIn(context,true);
        SettingsUtils.setRecentOfficer(context,username);
    }
    public static boolean isLoggedIn(Context context) {
        return SettingsUtils.isLoggedIn(context);
    }
}
