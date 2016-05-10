package com.razikallayi.suraksha.dailyTxn;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import java.util.Calendar;


/**
 * Created by Razi Kallayi on 29-02-2016.
 */
public class DailyTransactionAdapter extends FragmentStatePagerAdapter{

        private final String[] projection;
        private Cursor cursor;
        private DailyTransactionFragment dailyTransactionFragment;

        public DailyTransactionAdapter(FragmentManager fm, String[] projection,Cursor cursor) {
            super(fm);
            this.projection = projection;
            this.cursor = cursor;
        }

        @Override
        public Fragment getItem(int position) {
            Log.d("FISH", "getItem Adapter");
            if (cursor == null) { // shouldn't happen
                return null;
            }
            cursor.moveToPosition(position);
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.add(Calendar.DATE, position);
            Log.d("FISH", "getItem: "+calendar.getTimeInMillis());
            dailyTransactionFragment = DailyTransactionFragment.newInstance(calendar.getTimeInMillis());
            Bundle args = new Bundle();
            for (int i = 0; i < projection.length; ++i) {
                args.putString(projection[i], cursor.getString(i));
            }
            args.putLong(DailyTransactionFragment.ARG_DATE,calendar.getTimeInMillis());
            dailyTransactionFragment.setArguments(args);
            return dailyTransactionFragment;
        }

        @Override
        public int getCount() {
            if (cursor == null) {
                return 0;
            }
            else
                return cursor.getCount();
        }

        public void swapCursor(Cursor c) {
            Log.d("FISH","swapCursor Adapter");
            if (cursor == c)
                return;

            this.cursor = c;
            notifyDataSetChanged();
        }

        public Cursor getCursor() {
            Log.d("FISH","get cursor Adapter");
            return cursor;
        }
}