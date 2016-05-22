package com.razikallayi.suraksha.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

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
    public static final String PREF_AUTH_ID = "auth_id";

    /**
     * key to store username of recent logged in officer.
     */
    public static final String PREF_RECENT_OFFICER = "pref_recent_officer";

    /**
     * Return a long representing the last time a sync was attempted (regardless of success).
     *
     * @param context Context to be used to lookup the {@link SharedPreferences}.
     */
    public static boolean isLoggedIn(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(PREF_IS_LOGGED_IN, false);
    }

    /**
     * Mark a sync was attempted (stores current time as 'last sync attempted' preference).
     *
     * @param context Context to be used to edit the {@link android.content.SharedPreferences}.
     */
    public static void setLoggedIn(final Context context,final boolean value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putBoolean(PREF_IS_LOGGED_IN, value).apply();
    }

    /**
     * Store auth officer Id
     *
     * @param context Context to be used to edit the {@link android.content.SharedPreferences}.
     */
    public static void setAuthOfficerId(final Context context, final long value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putLong(PREF_AUTH_ID, value).apply();
    }

    /**
     * Return a long representing the auth officer id
     *
     * @param context Context to be used to lookup the {@link SharedPreferences}.
     */
    public static long getAuthOfficerId(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getLong(PREF_AUTH_ID, -1);
    }



    /**
     * Return a long representing the last time a sync was attempted (regardless of success).
     *
     * @param context Context to be used to lookup the {@link SharedPreferences}.
     */
    public static String getRecentOfficer(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(PREF_RECENT_OFFICER, null);
    }
    /**
     * Return a long representing the last time a sync was attempted (regardless of success).
     *
     * @param context Context to be used to lookup the {@link SharedPreferences}.
     */
    public static void setRecentOfficer(final Context context,final String username) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putString(PREF_RECENT_OFFICER, username).apply();
    }
}
