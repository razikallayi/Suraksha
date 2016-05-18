package com.razikallayi.suraksha.utils;

import android.content.Context;

import com.razikallayi.suraksha.R;
import com.razikallayi.suraksha.txn.Transaction;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by Razi Kallayi on 10-01-2016.
 */
public class Utility {
    public static String formatDate(long dateInMilliseconds) {
        if(dateInMilliseconds == 0L){
            return "";
        }
        Date date = new Date(dateInMilliseconds);
        return DateFormat.getDateInstance().format(date);
    }

    public static String formatAmountInRupees(Context context, double amount) {
        return context.getString(R.string.format_rupees, amount);
    }


    public static double getMonthlyDepositAmount(){
        double monthlyDepositAmount = 1000;
        return monthlyDepositAmount;
    }

    public static double getRegistrationFeeAmount(){
        double registrationFeeAmount = 250;
        return registrationFeeAmount;
    }

    public static Calendar getSurakshaStartDate(){
        return new GregorianCalendar(2016, 0, 1,0,0,0);
    }

    public static Calendar getDepositEndDate(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Utility.normalizeDate(System.currentTimeMillis()));
        calendar.set(Calendar.DATE,1);
        calendar.set(Calendar.MONTH,Calendar.DECEMBER);
        calendar.add(Calendar.YEAR,2);
        return calendar;
    }

    public static double getOpeningDepositAmount(){
        Calendar currentDate =Calendar.getInstance();
        int unpaidMonths = currentDate.get(Calendar.MONTH)- getSurakshaStartDate().get(Calendar.MONTH)+1;
        return unpaidMonths * Utility.getMonthlyDepositAmount();
    }

    public static final String readableDepositMonth(Calendar calendar) {
        String month = new DateFormatSymbols().getMonths()[calendar.get(Calendar.MONTH)];
        int year = calendar.get(Calendar.YEAR);

        if(year>=getSurakshaStartDate().get(Calendar.YEAR)) {
            return month.concat(" " + String.valueOf(year));
        }
        return month;
    }
    public static final String readableDepositMonth(long longDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(longDate);
        return readableDepositMonth(calendar);
    }

    public static final long writableDepositMonth(String monthAndYear) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(0);
        String[] monthYear= monthAndYear.split(" ");
        calendar.set(Calendar.DATE,1);
        calendar.set(Calendar.MONTH,getMonthInt(monthYear[0]));
        calendar.set(Calendar.YEAR,Integer.parseInt(monthYear[1]));
        return calendar.getTimeInMillis();
    }

    // To make it easy to query for the exact date, we normalize all dates that go into
    // the database to the start of the the Julian day at UTC.
    public static long normalizeDate(long startDate) {
        // normalize the start date to the beginning of the (UTC) day
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(0);
        Calendar date = Calendar.getInstance();
        date.setTimeInMillis(startDate);
        calendar.set(date.get(Calendar.YEAR),date.get(Calendar.MONTH),date.get(Calendar.DATE));
        return calendar.getTimeInMillis();
//        Time time = new Time();
//        time.set(startDate);
//        int julianDay = Time.getJulianDay(startDate, time.gmtoff);
//        return time.setJulianDay(julianDay);
    }


    private static int getMonthInt(String month){
        month = month.toUpperCase();
        switch (month){
            case "JANUARY":
                return Calendar.JANUARY;
            case "FEBRUARY":
                return Calendar.FEBRUARY;
            case "MARCH":
                return Calendar.MARCH;
            case "APRIL":
                return Calendar.APRIL;
            case "MAY":
                return Calendar.MAY;
            case "JUNE":
                return Calendar.JUNE;
            case "JULY":
                return Calendar.JULY;
            case "AUGUST":
                return Calendar.AUGUST;
            case "SEPTEMBER":
                return Calendar.SEPTEMBER;
            case "OCTOBER":
                return Calendar.OCTOBER;
            case "NOVEMBER":
                return Calendar.NOVEMBER;
            case "DECEMBER":
                return Calendar.DECEMBER;
            default:
                return -1;
        }
    }

    public static List<Calendar> getAllDepositMonthsTill(Calendar endDate){
        //Initialise new calendar list
        List<Calendar> pendingDepositCalendars = new ArrayList<>();
        Calendar surakshaStartDate = getSurakshaStartDate();// 2016, 0, 1,0,0,0 Time will be zero
        Calendar currentDate = Calendar.getInstance();

        if(endDate==null){
            endDate = currentDate;
        }
        //Loop from suraksha start month to current month
        Calendar i=surakshaStartDate;
        while(i.getTimeInMillis()<=endDate.getTimeInMillis()){
            //Initialise new calendar
            Calendar cal = Calendar.getInstance();
            //set calendar time to looping calendar
            cal.setTimeInMillis(i.getTimeInMillis());
            //set date to 1
            cal.set(Calendar.DATE,1);
            //Add the calendar to the list
            pendingDepositCalendars.add(cal);
            //increment the looping calendar month
            i.add(Calendar.MONTH,1);
        }//end while
        return pendingDepositCalendars;
    }

    public static List<Calendar> getPendingDepositMonthsFromTxn(List<Transaction> existingDepositTransaction) {
        List<Calendar> depositCalendarList = new ArrayList<Calendar>();
        for (Transaction deposit:existingDepositTransaction) {
            Calendar calendar = new GregorianCalendar();
            calendar.setTimeInMillis(deposit.getDefinedDepositMonth());
            depositCalendarList.add(calendar);
        }
        return getPendingDepositMonths(depositCalendarList);
    }


    public static List<Calendar> getPendingDepositMonths(List<Calendar> existingDepositCalendars){
        List<Calendar> pendingDepositCalendars = getAllDepositMonthsTill(getDepositEndDate());
        //If already deposited, remove it from the calendar
        pendingDepositCalendars.removeAll(existingDepositCalendars);
        return pendingDepositCalendars;
    }

//    public static String getReadableDate(long millisecond){
//        SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, yyyy 'at' h:mm a");
//        return sdf.format(millisecond);
//    }


//    private double getAccountDue(String accountNumber,String ledger){
//        String DueSelectionFromTxnForAccount=" where "+
//                SurakshaContract.TxnEntry.COLUMN_FK_ACCOUNT_NUMBER+
//                " = ? and "+SurakshaContract.TxnEntry.COLUMN_LEDGER+" = ?";
//
//        Cursor curLastDepositDate=getContext().getContentResolver().query(SurakshaContract.TxnEntry.buildTxnsOfAccount(accountNumber),
//                new String[]{SurakshaContract.TxnEntry.TABLE_NAME+"."+SurakshaContract.TxnEntry.COLUMN_CREATED_AT},
//                DueSelectionFromTxnForAccount,
//                new String[]{SurakshaContract.TxnEntry.COLUMN_FK_ACCOUNT_NUMBER,ledger},
//                SurakshaContract.TxnEntry.TABLE_NAME+"."+SurakshaContract.TxnEntry.COLUMN_CREATED_AT+" desc limit 1"
//        );
//
//        curLastDepositDate.moveToFirst();
//        if (curLastDepositDate != null) {
//
//            Calendar lastDepositDate = Calendar.getInstance();
//            lastDepositDate.setTimeInMillis(curLastDepositDate.getLong(0));
//            curLastDepositDate.close();
//            Calendar currentDate = Calendar.getInstance();
//            int dueMonths = currentDate.get(Calendar.MONTH) - lastDepositDate.get(Calendar.MONTH);
//            Log.d("FISH", "getAccountDue: Ledger= " + ledger);
//            switch (ledger){
//                case SurakshaContract.TxnEntry.DEPOSIT_LEDGER:
//                    double due =dueMonths * Utility.getMonthlyDepositAmount();
//                    Log.d("FISH", "getAccountDue->Deposit Due: "+due);
//                    return due;
//                default:
//                    return 0;
//            }
//        }
//        return 0;
//    }



}
