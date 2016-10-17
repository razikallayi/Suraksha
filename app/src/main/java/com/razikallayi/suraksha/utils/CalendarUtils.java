package com.razikallayi.suraksha.utils;

import android.content.Context;
import android.text.format.DateUtils;

import com.razikallayi.suraksha.R;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by Razi Kallayi on 19-05-2016.
 */
public class CalendarUtils extends GregorianCalendar {

    private static int mDepositStartDay = 10;//Normal deposit date is 10 every month

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
        return mDepositStartDay;
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

        if (year >= getSurakshaStartDate().get(YEAR)) {
            return month.concat(" " + String.valueOf(year));
        }
        return month;
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
    public static long normalizeDate(long startDate) {
        // normalize the start date to the beginning of the (UTC) day
        Calendar date = getInstance();
        date.setTimeInMillis(startDate);
        Calendar standardDate = Calendar.getInstance();
        standardDate.setTimeInMillis(0);
        standardDate.set(date.get(YEAR), date.get(MONTH), date.get(DATE));
        return standardDate.getTimeInMillis();
//        Time time = new Time();
//        time.set(startDate);
//        int julianDay = Time.getJulianDay(startDate, time.gmtoff);
//        return time.setJulianDay(julianDay);
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
        int formatId = R.string.format_full_friendly_date;
        if (givenDate == currentDate) {
            return String.format(context.getString(
                    formatId,
                    context.getString(R.string.today),
                    getFormattedMonthDay(context, dateInMillis)));
        } else if (givenDate == tomorrow)
            return String.format(context.getString(
                    formatId,
                    context.getString(R.string.tomorrow),
                    getFormattedMonthDay(context, dateInMillis)));
        else if (givenDate == yesterday)
            return String.format(context.getString(
                    formatId,
                    context.getString(R.string.yesterday),
                    getFormattedMonthDay(context, dateInMillis)));
        else {
            // Otherwise, use the form "Mon Jun 3"
            return getFormattedMonthDay(context, dateInMillis);
        }
    }

    /**
     * Converts db date format to the format "Month day", e.g "June 24".
     *
     * @param context      Context to use for resource localization
     * @param dateInMillis The db formatted date string, expected to be of the form specified
     *                     in Utility.DATE_FORMAT
     * @return The day in the form of a string formatted "December 6"
     */
    public static String getFormattedMonthDay(Context context, long dateInMillis) {
//        SimpleDateFormat dbDateFormat = new SimpleDateFormat(CalendarUtils.DATE_FORMAT);
        SimpleDateFormat monthDayFormat = new SimpleDateFormat("MMM dd EEE");
        if (!isCurrentYear(dateInMillis)) {
            monthDayFormat = new SimpleDateFormat("MMM dd, yyyy");
        }
        return monthDayFormat.format(dateInMillis);
    }

    public static boolean isCurrentYear(long dateInMillis) {
        Calendar givenDate = Calendar.getInstance();
        givenDate.setTimeInMillis(dateInMillis);
        return givenDate.get(YEAR) == getInstance().get(YEAR);
    }
}
