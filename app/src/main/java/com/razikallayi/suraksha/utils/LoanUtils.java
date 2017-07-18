package com.razikallayi.suraksha.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.razikallayi.suraksha.DatePreference;

/**
 * Created by Razi Kallayi on 23-06-2016 03:54.
 */
public class LoanUtils {
    public static String getMaximumLoanAmount(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString("max_loan_amount",String.valueOf(10000));
    }

    public static String getDefaultLoanInstalmentTimes(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString("loan_instalment_times", String.valueOf(10));
    }


    public static String getDefaultLoanIssueDate(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString("default_loan_issue_date", DatePreference.defaultLoanIssueDate);
    }
}
