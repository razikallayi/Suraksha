package com.razikallayi.suraksha.utils;

import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.util.TypedValue;
import android.widget.Toast;

import com.razikallayi.suraksha.R;
import com.razikallayi.suraksha.data.SurakshaDbHelper;

import java.util.Calendar;

/**
 * Created by Razi Kallayi on 10-01-2016.
 */
public class Utility {

    public static String formatAmountInRupees(Context context, double amount) {
        return context.getString(R.string.format_rupees, amount);
    }


    public static double getMonthlyDepositAmount() {
        return (double) 1000;
    }

    public static double getRegistrationFeeAmount() {
        return (double) 250;
    }

    public static double getOpeningDepositAmount() {
        Calendar currentDate = Calendar.getInstance();
        // TODO: 20-05-2016 Bug fix. Year not calculated here.
        int unpaidMonths = currentDate.get(Calendar.MONTH) - CalendarUtils.getSurakshaStartDate().get(Calendar.MONTH) + 1;
        return unpaidMonths * Utility.getMonthlyDepositAmount();
    }

    public static int getActionBarHeight(Context context) {
        int actionBarHeight = 0;
        TypedValue tv = new TypedValue();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            if (context.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv,
                    true))
                actionBarHeight = TypedValue.complexToDimensionPixelSize(
                        tv.data, context.getResources().getDisplayMetrics());
        } else {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,
                    context.getResources().getDisplayMetrics());
        }
        return actionBarHeight;
    }


    public static void updateColumnToUpperCase(Context context, String tableName, String columnName) {
//    String strQuery = "select sum(case when ledger = 1 and voucher_type = 101 then amount else 0 end) - sum(case when ledger = 4 and voucher_type = 100 then amount else 0 end) as balance from transactions";
        String strQuery = "UPDATE " + tableName + " SET " + columnName + " = UPPER(" + columnName + ")";
        Cursor cursor = runWritableRawQuery(context,strQuery);
        Toast.makeText(context, "Capitalised "+columnName+" of all "+tableName+".", Toast.LENGTH_SHORT).show();
    }

    public static void updateColumnToWordCase(Context context, String tableName, String columnName) {
//    String strQuery = "select sum(case when ledger = 1 and voucher_type = 101 then amount else 0 end) - sum(case when ledger = 4 and voucher_type = 100 then amount else 0 end) as balance from transactions";
        String strQuery = "UPDATE "+tableName+" SET "+columnName+" = UPPER(SUBSTR("+columnName+", 1, 1)) || SUBSTR("+columnName+", 2)";
        Cursor cursor = runWritableRawQuery(context,strQuery);
        Toast.makeText(context, "Capitalised First letter of "+columnName+" of all "+tableName+".", Toast.LENGTH_SHORT).show();
    }


    public static Cursor runWritableRawQuery(Context context, String strQuery){
        SurakshaDbHelper dbHelper = new SurakshaDbHelper(context);
        return dbHelper.getWritableDatabase().rawQuery(strQuery, null, null);
    }

    public static Cursor runReadableRawQuery(Context context, String strQuery){
        SurakshaDbHelper dbHelper = new SurakshaDbHelper(context);
        return dbHelper.getReadableDatabase().rawQuery(strQuery, null, null);
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
