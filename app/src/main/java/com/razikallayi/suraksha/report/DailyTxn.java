package com.razikallayi.suraksha.report;

import android.content.Context;
import android.database.Cursor;

import com.razikallayi.suraksha.data.SurakshaContract;
import com.razikallayi.suraksha.data.SurakshaDbHelper;
import com.razikallayi.suraksha.utils.CalendarUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by razi on 17-Jan-18.
 */

public class DailyTxn {
    private static final String DATE_COLUMN = SurakshaContract.TxnEntry.COLUMN_CREATED_AT;

    public static List<DailyItem> getTxnReportOfDate(Context context, long date) {
        Calendar dayEnd = new GregorianCalendar();
        dayEnd.setTimeInMillis(date);
        dayEnd.add(Calendar.DATE, 1);
        return getTxnReport(context, date, dayEnd.getTimeInMillis());
    }

    public static List<DailyItem> getTxnReportOfMonth(Context context, long date) {
        Calendar dayStart = new GregorianCalendar();
        dayStart.setTimeInMillis(date);
        dayStart.set(Calendar.DATE, 1);

        Calendar dayEnd = new GregorianCalendar();
        dayEnd.setTimeInMillis(dayStart.getTimeInMillis());
        dayEnd.add(Calendar.MONTH, 1);
        return getTxnReport(context, dayStart.getTimeInMillis(), dayEnd.getTimeInMillis());
    }


    public static List<DailyItem> getTxnReportOfYear(Context context, long date) {
        Calendar dayStart = new GregorianCalendar();
        dayStart.setTimeInMillis(date);
        dayStart.set(Calendar.DATE, 1);
        dayStart.set(Calendar.MONTH, Calendar.JANUARY);

        Calendar dayEnd = new GregorianCalendar();
        dayEnd.setTimeInMillis(dayStart.getTimeInMillis());
        dayEnd.add(Calendar.YEAR, 1);
        return getTxnReport(context, dayStart.getTimeInMillis(), dayEnd.getTimeInMillis());
    }

    public static List<DailyItem> getTxnReport(Context context, long startDate, long endDate) {
        String sql = "SELECT count(_id) as count , sum(amount) as amount ," +
                " ledger as label FROM transactions WHERE " + DATE_COLUMN + " >= ?" +
                " and " + DATE_COLUMN + " < ? group by ledger";
        String[] selectionArgs = new String[]{
                String.valueOf(CalendarUtils.normalizeDate(startDate)),
                String.valueOf(CalendarUtils.normalizeDate(endDate))
        };

        SurakshaDbHelper db = new SurakshaDbHelper(context);
        Cursor cursor = db.getReadableDatabase().rawQuery(sql, selectionArgs);

        if (cursor == null) return null;
        if (cursor.getCount() <= 0) return null;
        List<DailyItem> dailyItems = new ArrayList<>();
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            DailyItem dailyItem = new DailyItem();
            dailyItem.count = cursor.getInt(0);
            dailyItem.amount = cursor.getDouble(1);
            dailyItem.label = cursor.getInt(2);
            dailyItems.add(dailyItem);

        }
        cursor.close();
        return dailyItems;
    }

//
//    public static long getNextTxnDate(Context context, long date, boolean isNext) {
//        Calendar day = new GregorianCalendar();
//        day.setTimeInMillis(CalendarUtils.normalizeDate(date));
//        String[] projection = new String[]{
//                DATE_COLUMN
//        };
//        String selection;
//        String sort;
//        if (isNext) {
//            selection = DATE_COLUMN + " > ? ";
//            sort = SurakshaContract.TxnEntry._ID + " asc limit 1 ";
//            day.add(Calendar.DATE, 1);
//        } else {
//            //Previous
//            selection = DATE_COLUMN + " < ? ";
//            sort = SurakshaContract.TxnEntry._ID + " desc limit 1 ";
//            day.add(Calendar.DATE, -1);
//        }
//        String[] selectionArgs = new String[]{
//                String.valueOf(CalendarUtils.normalizeDate(day.getTimeInMillis()))
//        };
//        Cursor cursor = context.getContentResolver().query(
//                SurakshaContract.TxnEntry.CONTENT_URI,
//                projection,
//                selection,
//                selectionArgs,
//                sort);
//        if (cursor == null) return 0;
//        if (cursor.getCount() <= 0) return 0;
//        cursor.moveToFirst();
//        return CalendarUtils.normalizeDate(cursor.getLong(0));
//    }

    public static long getNextTxn(Context context, long date, int field, boolean isNext) {
        Calendar day = new GregorianCalendar();
        day.setTimeInMillis(CalendarUtils.normalizeDate(date));
        if (field == Calendar.MONTH || field == Calendar.YEAR) {
            day.set(Calendar.DATE, 1);
            if (field == Calendar.YEAR) {
                day.set(Calendar.MONTH, Calendar.JANUARY);
            }
        }

        String[] projection = new String[]{
                DATE_COLUMN
        };
        String selection;
        String sort;
        if (isNext) {
            selection = DATE_COLUMN + " > ? ";
            sort = SurakshaContract.TxnEntry._ID + " asc limit 1 ";
            day.add(field, 1);
        } else {
            //Previous
            selection = DATE_COLUMN + " < ? ";
            sort = SurakshaContract.TxnEntry._ID + " desc limit 1 ";
        }
        String[] selectionArgs = new String[]{
                String.valueOf(CalendarUtils.normalizeDate(day.getTimeInMillis()))
        };
        Cursor cursor = context.getContentResolver().query(
                SurakshaContract.TxnEntry.CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                sort);
        if (cursor == null) return 0;
        if (cursor.getCount() <= 0) return 0;
        cursor.moveToFirst();
        return CalendarUtils.normalizeDate(cursor.getLong(0));
    }
}
