package com.razikallayi.suraksha.utils;

import android.content.Context;
import android.text.format.Time;

import com.razikallayi.suraksha.R;
import com.razikallayi.suraksha.txn.Transaction;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by Razi Kallayi on 19-05-2016.
 */
public class CalendarUtils extends GregorianCalendar {


    // Format used for storing dates in the database.  ALso used for converting those strings
    // back into date objects for comparison/processing.
    public static final String DATE_FORMAT = "yyyyMMdd";

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
        return 10;
    }

    public static long getDueDate() {
        Calendar dueDate = getInstance();
        dueDate.set(dueDate.get(YEAR), dueDate.get(MONTH), dueDate.get(DATE), 0, 0, 0);
        dueDate.set(Calendar.DATE, getDueDay());
        return dueDate.getTimeInMillis();
    }

    public static boolean isDueDate() {
        return System.currentTimeMillis() > getDueDate();
    }

    public static Calendar getDepositEndDate() {
        Calendar c = getInstance();
        c.set(DATE, 1);
        c.set(MONTH, DECEMBER);
        c.add(YEAR, 2);
        c.setTimeInMillis(normalizeDate(c.getTimeInMillis()));
        return c;
    }

    public static final String readableDepositMonth(Calendar calendar) {
        String month = new DateFormatSymbols().getMonths()[calendar.get(MONTH)];
        int year = calendar.get(YEAR);

        if (year >= getSurakshaStartDate().get(YEAR)) {
            return month.concat(" " + String.valueOf(year));
        }
        return month;
    }

    public static final String readableDepositMonth(long longDate) {
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
        date.set(date.get(YEAR), date.get(MONTH), date.get(DATE), 0, 0, 0);
        return date.getTimeInMillis();
//        Time time = new Time();
//        time.set(startDate);
//        int julianDay = Time.getJulianDay(startDate, time.gmtoff);
//        return time.setJulianDay(julianDay);
    }

    // To make it easy to query for the exact calendar, we normalize all dates that go into
    // the database to the start of the the Julian day at UTC.
    public static Calendar normalizeDate(Calendar calendar) {
        // normalize the calendar to the beginning of the (UTC) day means 12:00:00 AM
        calendar.set(calendar.get(YEAR), calendar.get(MONTH), calendar.get(DATE), 0, 0, 0);
        return calendar;
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

        Time time = new Time();
        time.setToNow();
        long currentTime = System.currentTimeMillis();
        int julianDay = Time.getJulianDay(dateInMillis, time.gmtoff);
        int currentJulianDay = Time.getJulianDay(currentTime, time.gmtoff);

        // If the date we're building the String for is today's date, the format
        // is "Today, June 24"
        if (julianDay == currentJulianDay) {
            String today = context.getString(R.string.today);
            int formatId = R.string.format_full_friendly_date;
            return String.format(context.getString(
                    formatId,
                    today,
                    getFormattedMonthDay(context, dateInMillis)));
        } else if (julianDay < currentJulianDay + 7) {
            // If the input date is less than a week in the future, just return the day name.
            return getDayName(context, dateInMillis);
        } else {
            // Otherwise, use the form "Mon Jun 3"
            SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("EEE MMM dd");
            return shortenedDateFormat.format(dateInMillis);
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
        Time time = new Time();
        time.setToNow();
        SimpleDateFormat dbDateFormat = new SimpleDateFormat(CalendarUtils.DATE_FORMAT);
        SimpleDateFormat monthDayFormat = new SimpleDateFormat("MMMM dd");
        String monthDayString = monthDayFormat.format(dateInMillis);
        return monthDayString;
    }

    /**
     * Given a day, returns just the name to use for that day.
     * E.g "today", "tomorrow", "wednesday".
     *
     * @param context      Context to use for resource localization
     * @param dateInMillis The date in milliseconds
     * @return
     */
    public static String getDayName(Context context, long dateInMillis) {
        // If the date is today, return the localized version of "Today" instead of the actual
        // day name.

        Time t = new Time();
        t.setToNow();
        int julianDay = Time.getJulianDay(dateInMillis, t.gmtoff);
        int currentJulianDay = Time.getJulianDay(System.currentTimeMillis(), t.gmtoff);
        if (julianDay == currentJulianDay) {
            return context.getString(R.string.today);
        } else if (julianDay == currentJulianDay + 1) {
            return context.getString(R.string.tomorrow);
        } else {
            Time time = new Time();
            time.setToNow();
            // Otherwise, the format is just the day of the week (e.g "Wednesday".
            SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE");
            return dayFormat.format(dateInMillis);
        }
    }

    public static List<Calendar> getAllDepositMonthsFromStart() {
        //Initialise new calendar list
        List<Calendar> pendingDepositCalendarList = new ArrayList<>();
        Calendar surakshaStartDate = getSurakshaStartDate();// 2016, 0, 1,0,0,0 Time will be zero
        Calendar endDate = getDepositEndDate();


        //Loop from suraksha start month to current month
        Calendar i = surakshaStartDate;
        while (i.getTimeInMillis() <= endDate.getTimeInMillis()) {
            //Initialise new calendar
            Calendar c = getInstance();
            //set calendar time to looping calendar
            c.setTimeInMillis(i.getTimeInMillis());
            //set date to 1
            c.set(DATE, 1);
            //Add the calendar to the list
            pendingDepositCalendarList.add(c);
            //increment the looping calendar month
            i.add(MONTH, 1);
        }//end while
        return pendingDepositCalendarList;
    }

    public static List<Calendar> getDepositedMonthsFromTxn(List<Transaction> existingDepositTransaction) {
        List<Calendar> depositCalendarList = new ArrayList<>();
        for (Transaction deposit : existingDepositTransaction) {
            Calendar calendar = getInstance();
            calendar.setTimeInMillis(deposit.getDefinedDepositMonth());
            depositCalendarList.add(calendar);
        }
        return depositCalendarList;
    }

    public static List<Calendar> getPendingDepositMonthsFromTxn(List<Transaction> existingDepositTransaction) {
        List<Calendar> depositCalendarList = new ArrayList<>();
        for (Transaction deposit : existingDepositTransaction) {
            Calendar calendar = getInstance();
            calendar.setTimeInMillis(deposit.getDefinedDepositMonth());
            depositCalendarList.add(calendar);
        }
        return getPendingDepositMonths(depositCalendarList);
    }

    public static List<Calendar> getPendingDepositMonths(List<Calendar> existingDepositCalendars) {
        List<Calendar> pendingDepositCalendars = getAllDepositMonthsFromStart();
        //If already deposited, remove it from the calendar
        pendingDepositCalendars.removeAll(existingDepositCalendars);
        return pendingDepositCalendars;
    }
}
