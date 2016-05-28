package com.razikallayi.suraksha.utils;

import android.content.Context;
import android.os.SystemClock;

/**
 * Created by Razi Kallayi on 10-05-2016 03:15.
 */
public class AuthUtils {

    public static void logout(Context context) {
        SettingsUtils.setAuthOfficer(context,null);
    }

    public static boolean isLoggedIn(Context context) {
        return SettingsUtils.isLoggedIn(context);
    }

    public static boolean isAdmin(Context context) {
        return isLoggedIn(context) && SettingsUtils.getAuthOfficer(context).isAdmin();
    }

    public static long getAuthenticatedOfficerId(Context context) {
        return SettingsUtils.getAuthOfficerId(context);
    }

    public static long getTimeDiffInMillis(Context context) {
        long lockedAt = SettingsUtils.getAuthTime(context);
        long currentTime = SystemClock.elapsedRealtime();
        return Math.abs(currentTime - lockedAt);
    }

    public static void writeLockTime(Context context) {
        writeLockTime(context, SystemClock.elapsedRealtime());
    }

    public static void writeLockTime(Context context, long time) {
        SettingsUtils.setAuthTime(context,time);
    }
}
