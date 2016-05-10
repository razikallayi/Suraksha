package com.razikallayi.suraksha.utils;

import android.content.Context;

import com.razikallayi.suraksha.R;

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

    public static double getOpeningDepositAmount(){
        Calendar currentDate =Calendar.getInstance();
        int unpaidMonths = currentDate.get(Calendar.MONTH)- getSurakshaStartDate().get(Calendar.MONTH)+1;
        return unpaidMonths * Utility.getMonthlyDepositAmount();
    }

    public static final String formatPendingDepositDate(Calendar calendar) {
        String month = new DateFormatSymbols().getMonths()[calendar.get(Calendar.MONTH)];
        int year = calendar.get(Calendar.YEAR);

        if(year>=getSurakshaStartDate().get(Calendar.YEAR)) {
            return month.concat(" " + String.valueOf(year));
        }
        return month;
    }
    public static final String formatPendingDepositDate(long longDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(longDate);
        return formatPendingDepositDate(calendar);
    }

    public static List<Calendar> getPendingDepositMonths(List<Long> existingDepositDates){
        List<Calendar> pendingDepositCalendars = new ArrayList<>();
        Calendar surakshaStartDate = getSurakshaStartDate();
        Calendar currentDate = Calendar.getInstance();

            Calendar i=surakshaStartDate;
            while(i.getTimeInMillis()<=currentDate.getTimeInMillis()){
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.DATE,1);
                cal.setTimeInMillis(i.getTimeInMillis());
                pendingDepositCalendars.add(cal);
                i.add(Calendar.MONTH,1);
            }
            if (existingDepositDates != null) {
                List<Calendar> existingDepositCalendars =new ArrayList<>();
                for (long existingDepositDate :
                        existingDepositDates) {
                    Calendar existingDepositCalendar = new GregorianCalendar();
                    existingDepositCalendar.setTimeInMillis(existingDepositDate);
                    existingDepositCalendars.add(existingDepositCalendar);
                }
                pendingDepositCalendars.removeAll(existingDepositCalendars);
            }
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
