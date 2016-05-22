package com.razikallayi.suraksha.utils;

import android.content.Context;

import com.razikallayi.suraksha.R;

import java.util.Calendar;

/**
 * Created by Razi Kallayi on 10-01-2016.
 */
public class Utility {

    public static String formatAmountInRupees(Context context, double amount) {
        return context.getString(R.string.format_rupees, amount);
    }


    public static double getMonthlyDepositAmount(){
        return (double) 1000;
    }

    public static double getRegistrationFeeAmount(){
        return (double) 250;
    }

    public static double getOpeningDepositAmount(){
        Calendar currentDate =Calendar.getInstance();
        // TODO: 20-05-2016 Bug fix. Year not calculated here.
        int unpaidMonths = currentDate.get(Calendar.MONTH)- CalendarUtils.getSurakshaStartDate().get(Calendar.MONTH)+1;
        return unpaidMonths * Utility.getMonthlyDepositAmount();
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
