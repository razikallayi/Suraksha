package com.razikallayi.suraksha_ssf.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Razi Kallayi on 28-05-2016 09:52.
 */
public class SmsUtils {
    public static final int ENABLE_SMS = 1;
    public static final int SMS_AFTER_REGISTRATION = 2;
    public static final int SMS_AFTER_DEPOSIT = 3;
    public static final int SMS_AFTER_LOAN_ISSUED = 4;
    public static final int EDIT_SUMMERY_BEFORE_SENDING = 5;

    public static boolean isEnabled(Context context, int smsEnvironment) {
        if (!smsEnabled(context)) return false;
        if (smsEnvironment == SMS_AFTER_REGISTRATION) return smsEnabledAfterRegistration(context);
        if (smsEnvironment == SMS_AFTER_DEPOSIT) return smsEnabledAfterDeposit(context);
        if (smsEnvironment == SMS_AFTER_LOAN_ISSUED) return smsEnabledAfterLoanIssued(context);
        return false;
    }


    public static boolean smsEnabledAfterRegistration(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean("enable_sms", false) && prefs.getBoolean("sms_after_registration", false);
    }

//    public static boolean smsEnabledAfterCreateAccount(Context context) {
//        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
//        return prefs.getBoolean("enable_sms", false) && prefs.getBoolean("sms_after_create_account", false);
//    }


    public static boolean smsEnabled(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean("enable_sms", false);
    }

    public static boolean smsEnabledAfterDeposit(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean("enable_sms", false) && prefs.getBoolean("sms_after_deposit", false);
    }


    public static boolean smsEnabledAfterLoanIssued(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean("enable_sms", false) && prefs.getBoolean("sms_after_loan_issued", false);
    }

    public static boolean smsEnabledAfterLoanReturn(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean("enable_sms", false) && prefs.getBoolean("sms_after_loan_return", false);
    }

    public static boolean editSummery(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean("edit_summery_message", false);
    }


//    public static boolean sendSms(Context c, String message, String mobileNumber) {
//        if (mobileNumber.isEmpty()) {
//            return false;
//        }
//        if (isValidMobileNumber(mobileNumber)) {
//            SmsManager sms = SmsManager.getDefault();
//            Log.d("FISH SmsUtils", "sendSms: sending message" + mobileNumber + " : " + message);
////            sms.sendTextMessage(mobileNumber, null, message, null, null);
//            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + mobileNumber));
//            intent.putExtra("sms_body", message);
//            c.startActivity(intent);
//
//            return true;
//        }
//        return false;
//    }

    public static boolean sendSms(String message, String mobileNumber) {
        if (mobileNumber.isEmpty()) {
            return false;
        }
        if (isValidMobileNumber(mobileNumber)) {
            SmsManager sms = SmsManager.getDefault();
//            mobileNumber = "9746730324";
            sms.sendTextMessage(mobileNumber, null, message, null, null);
            return true;
        }
        return false;
    }


    public static boolean isValidMobileNumber(String mobileNumber) {
        //      ^     #Match the beginning of the string
        //      [789] #Match a 7, 8 or 9
        //      \d    #Match a digit (0-9 and anything else that is a "digit" in the regex engine)
        //      {9}   #Repeat the previous "\d" 9 times (9 digits)
        //      $     #Match the end of the string
        //
        //          eg: 9882223456
        //              8976785768
        //              7986576783

        String regularExpression = "^[789]\\d{9}$";

        try {
            Pattern pattern = Pattern.compile(regularExpression);
            Matcher matcher = pattern.matcher(mobileNumber);
            return matcher.matches();
        } catch (RuntimeException e) {
            return false;
        }
    }
}
