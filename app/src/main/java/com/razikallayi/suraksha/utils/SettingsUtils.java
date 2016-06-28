package com.razikallayi.suraksha.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.preference.PreferenceManager;

import com.razikallayi.suraksha.officer.Officer;

/**
 * Created by Razi Kallayi on 10-05-2016.
 */
public class SettingsUtils {

    /**
     * Long indicating when a sync was last ATTEMPTED (not necessarily succeeded).
     */
    public static final String PREF_IS_LOGGED_IN = "pref_is_logged_in";
    /**
     * Long indicating when a sync was last ATTEMPTED (not necessarily succeeded).
     */
    public static final String PREF_AUTH_OFFICER_ID = "auth_officer_id";

    /**
     * key to store username of recent logged in officer.
     */
    public static final String PREF_AUTH_OFFICER_USERNAME = "pref_auth_officer_username";

    /**
     * key to store username of recent logged in officer.
     */
    public static final String PREF_AUTH_OFFICER_IS_ADMIN = "pref_auth_officer_is_admin";


    /**
     * key to store username of recent logged in officer.
     */
    public static final String PREF_AUTH_OFFICER_NAME = "pref_auth_officer_name";

    /**
     * key to store username of recent logged in officer.
     */
    public static final String PREF_AUTH_TIME = "pref_auth_time";


    /**
     * Return a long representing the last time a sync was attempted (regardless of success).
     *
     * @param context Context to be used to lookup the {@link SharedPreferences}.
     */
    public static boolean isLoggedIn(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(PREF_IS_LOGGED_IN, false);
    }


    public static void setLoggedIn(final Context context, final boolean value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putBoolean(PREF_IS_LOGGED_IN, value).apply();
    }

    /**
     * Store auth officer Id
     *
     * @param context Context to be used to edit the {@link android.content.SharedPreferences}.
     */
    public static void setAuthOfficer(final Context context, Officer officer) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        if (officer == null) {
            editor.putLong(PREF_AUTH_OFFICER_ID, -1);
            editor.putString(PREF_AUTH_OFFICER_USERNAME, null);
            editor.putString(PREF_AUTH_OFFICER_NAME, null);
            editor.putBoolean(PREF_AUTH_OFFICER_IS_ADMIN, false);
            editor.putBoolean(PREF_IS_LOGGED_IN, false);
            editor.apply();
            return;
        }
        editor.putLong(PREF_AUTH_OFFICER_ID, officer.getId());
        editor.putString(PREF_AUTH_OFFICER_USERNAME, officer.getUsername());
        editor.putString(PREF_AUTH_OFFICER_NAME, officer.getName());
        editor.putBoolean(PREF_AUTH_OFFICER_IS_ADMIN, officer.isAdmin());
        editor.putBoolean(PREF_IS_LOGGED_IN, true);
        editor.apply();
        AuthUtils.writeLockTime(context);//write login time
    }

    /**
     * Store auth officer Id
     *
     * @param context Context to be used to edit the {@link android.content.SharedPreferences}.
     */
    public static Officer getAuthOfficer(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        if (sp.getBoolean(PREF_IS_LOGGED_IN, false)) {
            Officer officer = new Officer();
            officer.setAdmin(sp.getBoolean(PREF_AUTH_OFFICER_IS_ADMIN, false));
            officer.setId(sp.getLong(PREF_AUTH_OFFICER_ID, -1));
            officer.setUsername(sp.getString(PREF_AUTH_OFFICER_USERNAME, null));
            officer.setName(sp.getString(PREF_AUTH_OFFICER_NAME, null));
            return officer;
        }
        return null;
    }

    /**
     * Store auth officer Id
     *
     * @param context Context to be used to edit the {@link android.content.SharedPreferences}.
     */
    public static void setAuthTime(final Context context, final long time) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putLong(PREF_AUTH_TIME, time).apply();
    }


    public static long getAuthTime(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        long currentTime = SystemClock.elapsedRealtime();
        return sp.getLong(PREF_AUTH_TIME, currentTime - 10000);
    }


    public static long getAuthOfficerId(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getLong(PREF_AUTH_OFFICER_ID, -1);
    }

    public static String getOfficerUsername(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(PREF_AUTH_OFFICER_USERNAME, null);
    }
}
