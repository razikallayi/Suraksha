package com.razikallayi.suraksha.utils;

import android.content.Context;
import android.os.SystemClock;
import android.preference.PreferenceManager;

import com.razikallayi.suraksha.officer.Officer;

/**
 * Created by Razi Kallayi on 10-05-2016.
 */
public class AuthUtils {

    public static void logout(Context context) {
        SettingsUtils.setLoggedIn(context,false);
        SettingsUtils.setAuthOfficerId(context,-1);
        SettingsUtils.setRecentOfficer(context,null);
    }

    public static void login(Context context,long officerId, String username) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit().putLong("locked_at", SystemClock.elapsedRealtime()).commit();
        SettingsUtils.setLoggedIn(context,true);
        SettingsUtils.setAuthOfficerId(context,officerId);
        SettingsUtils.setRecentOfficer(context,username);
    }
    public static boolean isLoggedIn(Context context) {
        return SettingsUtils.isLoggedIn(context);
    }

    public static boolean isAdmin(Context context){
        return Officer.getOfficerFromId(context,getAuthenticatedOfficerId(context)).isAdmin();
    }

    public static long getAuthenticatedOfficerId(Context context){
        return SettingsUtils.getAuthOfficerId(context);
    }

    public static long getTimeDiffInMillis(Context context){
        long currentTime = SystemClock.elapsedRealtime();
        long lockedAt = PreferenceManager.getDefaultSharedPreferences(context)
                .getLong("locked_at", currentTime - 10000);
         return Math.abs(currentTime - lockedAt);
    }
}
