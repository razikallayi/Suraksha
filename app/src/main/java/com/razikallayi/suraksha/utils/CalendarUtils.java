package com.razikallayi.suraksha.utils;

import com.razikallayi.suraksha.txn.Transaction;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by Razi Kallayi on 19-05-2016.
 */
public class CalendarUtils extends GregorianCalendar {

    public static String formatDate(long dateInMilliseconds) {
        if(dateInMilliseconds == 0L){
            return "";
        }
        Date date = new Date(dateInMilliseconds);
        return DateFormat.getDateInstance().format(date);
    }

    public static Calendar getSurakshaStartDate(){
        Calendar c = getInstance();
        c.setTimeInMillis(0);
        c.set(2016, 0, 1);
        return c;
    }

    public static Calendar getDepositEndDate(){
        Calendar c = getInstance();
        c.set(DATE,1);
        c.set(MONTH, DECEMBER);
        c.add(YEAR,2);
        c.setTimeInMillis(normalizeDate(c.getTimeInMillis()));
        return c;
    }

    public static final String readableDepositMonth(Calendar calendar) {
        String month = new DateFormatSymbols().getMonths()[calendar.get(MONTH)];
        int year = calendar.get(YEAR);

        if(year>=getSurakshaStartDate().get(YEAR)) {
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
        String[] monthYear= monthAndYear.split(" ");
        c.setTimeInMillis(0);//Normalizing to start Date
        c.set(DATE,1);
        c.set(MONTH,getMonthInt(monthYear[0]));
        c.set(YEAR,Integer.parseInt(monthYear[1]));
        return c.getTimeInMillis();
    }

    // To make it easy to query for the exact date, we normalize all dates that go into
    // the database to the start of the the Julian day at UTC.
    public static long normalizeDate(long startDate) {
        // normalize the start date to the beginning of the (UTC) day
        Calendar c = getInstance();
        c.setTimeInMillis(0);
        Calendar date = getInstance();
        date.setTimeInMillis(startDate);
        c.set(date.get(YEAR),date.get(MONTH),date.get(DATE));
        return c.getTimeInMillis();
//        Time time = new Time();
//        time.set(startDate);
//        int julianDay = Time.getJulianDay(startDate, time.gmtoff);
//        return time.setJulianDay(julianDay);
    }

    private static int getMonthInt(String month){
        month = month.toUpperCase();
        switch (month){
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

    public static List<Calendar> getAllDepositMonthsFromStart(){
        //Initialise new calendar list
        List<Calendar> pendingDepositCalendarList = new ArrayList<>();
        Calendar surakshaStartDate = getSurakshaStartDate();// 2016, 0, 1,0,0,0 Time will be zero
        Calendar endDate = getDepositEndDate();


        //Loop from suraksha start month to current month
        Calendar i=surakshaStartDate;
        while(i.getTimeInMillis()<=endDate.getTimeInMillis()){
            //Initialise new calendar
            Calendar c = getInstance();
            //set calendar time to looping calendar
            c.setTimeInMillis(i.getTimeInMillis());
            //set date to 1
            c.set(DATE,1);
            //Add the calendar to the list
            pendingDepositCalendarList.add(c);
            //increment the looping calendar month
            i.add(MONTH,1);
        }//end while
        return pendingDepositCalendarList;
    }

    public static List<Calendar> getDepositedMonthsFromTxn(List<Transaction> existingDepositTransaction) {
        List<Calendar> depositCalendarList = new ArrayList<>();
        for (Transaction deposit:existingDepositTransaction) {
            Calendar calendar = getInstance();
            calendar.setTimeInMillis(deposit.getDefinedDepositMonth());
            depositCalendarList.add(calendar);
        }
        return depositCalendarList;
    }

    public static List<Calendar> getPendingDepositMonthsFromTxn(List<Transaction> existingDepositTransaction) {
        List<Calendar> depositCalendarList = new ArrayList<>();
        for (Transaction deposit:existingDepositTransaction) {
            Calendar calendar = getInstance();
            calendar.setTimeInMillis(deposit.getDefinedDepositMonth());
            depositCalendarList.add(calendar);
        }
        return getPendingDepositMonths(depositCalendarList);
    }

    public static List<Calendar> getPendingDepositMonths(List<Calendar> existingDepositCalendars){
        List<Calendar> pendingDepositCalendars = getAllDepositMonthsFromStart();
        //If already deposited, remove it from the calendar
        pendingDepositCalendars.removeAll(existingDepositCalendars);
        return pendingDepositCalendars;
    }
}
