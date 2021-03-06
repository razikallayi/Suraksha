package com.razikallayi.suraksha_ssf.utils;

import android.content.Context;
import android.text.format.DateUtils;

import com.razikallayi.suraksha_ssf.R;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * Created by Razi Kallayi on 19-05-2016.
 */
public class CalendarUtils extends GregorianCalendar {

    private static int DUE_START_DATE = 1;//Normal deposit/Loan date is due on 1 every month
    private static int DELAY_DATE = 15;//deposit/Loan is considered delayed after 15th of every month

    public static String formatDateTime(long dateInMilliseconds) {
        if (dateInMilliseconds == 0L) {
            return "";
        }
        Date date = new Date(dateInMilliseconds);
        return DateFormat.getDateTimeInstance().format(date);
    }

    public static String formatDate(long dateInMilliseconds) {
        if (dateInMilliseconds == 0L) {
            return "";
        }
        Date date = new Date(dateInMilliseconds);
        return DateFormat.getDateInstance().format(date);
    }

    public static Calendar getSurakshaStartDate() {
        Calendar c = getInstance();
        c.setTimeInMillis(0);
        c.set(2016, 0, 1);
        return c;
    }

    public static int getDueDay() {
        return DUE_START_DATE;
    }

    public static int getDelayDate() {
        return DELAY_DATE;
    }

    public static long getDepositStartDay() {
        Calendar dueDate = getInstance();
        dueDate = normalizeDate(dueDate);
        dueDate.set(Calendar.DATE, getDueDay());
        return dueDate.getTimeInMillis();
    }

    public static boolean isDepositStartDay() {
        return System.currentTimeMillis() > getDepositStartDay();
    }

    public static String readableDepositMonth(Calendar calendar) {
        String month = new DateFormatSymbols().getMonths()[calendar.get(MONTH)];
        int year = calendar.get(YEAR);
        return month.concat(" " + String.valueOf(year));
    }

    public static String readableShortDepositMonth(Calendar calendar) {
        String month = new DateFormatSymbols().getShortMonths()[calendar.get(MONTH)].toUpperCase();

//        if(calendar.get(YEAR) == Calendar.getInstance().get(YEAR)){
//            return month;
//        }
        int year = calendar.get(YEAR);
        return month.concat(" " + String.valueOf(year));
    }

    public static String readableDepositMonth(long longDate) {
        Calendar c = getInstance();
        c.setTimeInMillis(normalizeDate(longDate));
        return readableDepositMonth(c);
    }

    public static final long writableDepositMonth(String monthAndYear) {
        Calendar c = getInstance();
        String[] monthYear = monthAndYear.split(" ");
        c.setTimeInMillis(0);//Normalizing to start Date
        c.set(DATE, 1);
        c.set(MONTH, getMonthInt(monthYear[0]));
        c.set(YEAR, Integer.parseInt(monthYear[1]));
        return c.getTimeInMillis();
    }

    // To make it easy to query for the exact date, we normalize all dates that go into
    // the database to the start of the the Julian day at UTC.
    public static long normalizeDate(long dateWithTime) {
        Calendar date = new GregorianCalendar();
        date.setTimeInMillis(dateWithTime);
        date.set(HOUR_OF_DAY,0);
        date.set(MINUTE,0);
        date.set(SECOND,0);
        date.set(MILLISECOND,0);
        return date.getTimeInMillis();
    }

    // To make it easy to query for the exact calendar, we normalize all dates that go into
    // the database to the start of the the Julian day at UTC.
    public static Calendar normalizeDate(Calendar calendar) {
        // normalize the calendar to the beginning of the (UTC) day means 12:00:00 AM
        Calendar standardDate = Calendar.getInstance();
        standardDate.setTimeInMillis(0);
        standardDate.set(calendar.get(YEAR), calendar.get(MONTH), calendar.get(DATE));
        return standardDate;
    }

    public static Calendar normalizeDate(int year, int month, int date) {
        Calendar standardDate = Calendar.getInstance();
        standardDate.setTimeInMillis(0);
        standardDate.set(year, month, date);
        return standardDate;
    }

    private static int getMonthInt(String month) {
        month = month.toUpperCase();
        switch (month) {
            case "JANUARY":
                return JANUARY;
            case "FEBRUARY":
                return FEBRUARY;
            case "MARCH":
                return MARCH;
            case "APRIL":
                return APRIL;
            case "MAY":
                return MAY;
            case "JUNE":
                return JUNE;
            case "JULY":
                return JULY;
            case "AUGUST":
                return AUGUST;
            case "SEPTEMBER":
                return SEPTEMBER;
            case "OCTOBER":
                return OCTOBER;
            case "NOVEMBER":
                return NOVEMBER;
            case "DECEMBER":
                return DECEMBER;
            default:
                return -1;
        }
    }

    /**
     * Helper method to convert the database representation of the date into something to display
     * to users.  As classy and polished a user experience as "20140102" is, we can do better.
     *
     * @param context      Context to use for resource localization
     * @param dateInMillis The date in milliseconds
     * @return a user-friendly representation of the date.
     */
    public static String getFriendlyDayString(Context context, long dateInMillis) {
        // The day string for forecast uses the following logic:
        // For today: "Today, June 8"
        // For tomorrow:  "Tomorrow"
        // For the next 5 days: "Wednesday" (just the day name)
        // For all days after that: "Mon Jun 8"
        if (dateInMillis == 0L) {
            return "";
        }

        long givenDate = normalizeDate(dateInMillis);
        long currentDate = normalizeDate(System.currentTimeMillis());

        long yesterday = currentDate - DateUtils.DAY_IN_MILLIS;
        long tomorrow = currentDate + DateUtils.DAY_IN_MILLIS;
        // If the date we're building the String for is today's date, the format
        // is "Today, June 24"

//        return String.valueOf(givenDate); //TODO:Done for error correction. Once cleared, delete this line and enable below block commeents
        int formatId = R.string.format_full_friendly_date;
        if (givenDate == currentDate) {
            return context.getString(R.string.today);

        } else if (givenDate == tomorrow)
            return context.getString(R.string.tomorrow);
        else if (givenDate == yesterday)
            return context.getString(R.string.yesterday);
        else {
            // Otherwise, use the form "Mon Jun 3"
            return getFormattedMonthDay(dateInMillis);
        }
    }

    /**
     * Converts db date format to the format "Month day", e.g "June 24".
     *
     * @param dateInMillis The db formatted date string, expected to be of the form specified
     *                     in Utility.DATE_FORMAT
     * @return The day in the form of a string formatted "December 6"
     */
    public static String getFormattedMonthDay(long dateInMillis) {
//        SimpleDateFormat dbDateFormat = new SimpleDateFormat(CalendarUtils.DATE_FORMAT);
        SimpleDateFormat monthDayFormat = new SimpleDateFormat("MMM dd EEE", Locale.ENGLISH);
        if (!isCurrentYear(dateInMillis)) {
            monthDayFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH);
        }
        return monthDayFormat.format(dateInMillis);
    }

    public static boolean isCurrentYear(long dateInMillis) {
        Calendar givenDate = Calendar.getInstance();
        givenDate.setTimeInMillis(dateInMillis);
        return givenDate.get(YEAR) == getInstance().get(YEAR);
    }
}
