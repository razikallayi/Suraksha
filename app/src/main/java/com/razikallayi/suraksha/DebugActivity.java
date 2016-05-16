package com.razikallayi.suraksha;

import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.razikallayi.suraksha.data.SurakshaContract;
import com.razikallayi.suraksha.utils.Utility;

import java.util.Calendar;
import java.util.Date;

public class DebugActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String TAG="FISH";
        Calendar c = Calendar.getInstance();
        Log.d(TAG, "instantiating calendar"+ Utility.formatDate(c.getTimeInMillis()));
        Log.d(TAG, "instantiating calendar Normalised"+ Utility.formatDate(SurakshaContract.normalizeDate(c.getTimeInMillis())));
        c.set(Calendar.MONTH,Calendar.FEBRUARY);
        Log.d(TAG, "Month Set"+ Utility.formatDate(c.getTimeInMillis()));
        Log.d(TAG, "Month Set Normalised"+ Utility.formatDate(SurakshaContract.normalizeDate(c.getTimeInMillis())));
        c.set(Calendar.YEAR,2017);
        Log.d(TAG, "Year Set"+ Utility.formatDate(c.getTimeInMillis()));
        Log.d(TAG, "Year Set Normalised"+ Utility.formatDate(SurakshaContract.normalizeDate(c.getTimeInMillis())));



        System.out.println("---------------------------------------------------");


        Calendar calendar = Calendar.getInstance();
        calendar.set(2016,0,1);
        TextView tv1 = new TextView(this);
        tv1.setText("TimeMillis: " + calendar.getTimeInMillis());

        TextView tv2 = new TextView(this);
        tv2.setText("Date: " + new Date(calendar.getTimeInMillis()));
        //calendar.add(calendar.MONTH, 1);
        TextView tv3 = new TextView(this);
        tv3.setText("Date: " + new Date(calendar.getTimeInMillis()));

        // print out a bunch of interesting things
//        System.out.println("ERA: " + calendar.get(Calendar.ERA));
//        System.out.println("YEAR: " + calendar.get(Calendar.YEAR));
//        System.out.println("MONTH: " + calendar.get(Calendar.MONTH));
//        System.out.println("WEEK_OF_YEAR: " + calendar.get(Calendar.WEEK_OF_YEAR));
//        System.out.println("WEEK_OF_MONTH: " + calendar.get(Calendar.WEEK_OF_MONTH));
//        System.out.println("DATE: " + calendar.get(Calendar.DATE));
//        System.out.println("DAY_OF_MONTH: " + calendar.get(Calendar.DAY_OF_MONTH));
//        System.out.println("DAY_OF_YEAR: " + calendar.get(Calendar.DAY_OF_YEAR));
//        System.out.println("DAY_OF_WEEK: " + calendar.get(Calendar.DAY_OF_WEEK));
//        System.out.println("DAY_OF_WEEK_IN_MONTH: "
//                + calendar.get(Calendar.DAY_OF_WEEK_IN_MONTH));
//        System.out.println("AM_PM: " + calendar.get(Calendar.AM_PM));
//        System.out.println("HOUR: " + calendar.get(Calendar.HOUR));
//        System.out.println("HOUR_OF_DAY: " + calendar.get(Calendar.HOUR_OF_DAY));
//        System.out.println("MINUTE: " + calendar.get(Calendar.MINUTE));
//        System.out.println("SECOND: " + calendar.get(Calendar.SECOND));
//        System.out.println("MILLISECOND: " + calendar.get(Calendar.MILLISECOND));
//        System.out.println("ZONE_OFFSET: "
//                + (calendar.get(Calendar.ZONE_OFFSET) / (60 * 60 * 1000)));
//        System.out.println("DST_OFFSET: "
//                + (calendar.get(Calendar.DST_OFFSET) / (60 * 60 * 1000)));
//
//        System.out.println("Current Time, with hour reset to 3");
//        calendar.clear(Calendar.HOUR_OF_DAY); // so doesn't override
//        calendar.set(Calendar.HOUR, 3);
//        System.out.println("ERA: " + calendar.get(Calendar.ERA));
//        System.out.println("YEAR: " + calendar.get(Calendar.YEAR));
//        System.out.println("MONTH: " + calendar.get(Calendar.MONTH));
//        System.out.println("WEEK_OF_YEAR: " + calendar.get(Calendar.WEEK_OF_YEAR));
//        System.out.println("WEEK_OF_MONTH: " + calendar.get(Calendar.WEEK_OF_MONTH));
//        System.out.println("DATE: " + calendar.get(Calendar.DATE));
//        System.out.println("DAY_OF_MONTH: " + calendar.get(Calendar.DAY_OF_MONTH));
//        System.out.println("DAY_OF_YEAR: " + calendar.get(Calendar.DAY_OF_YEAR));
//        System.out.println("DAY_OF_WEEK: " + calendar.get(Calendar.DAY_OF_WEEK));
//        System.out.println("DAY_OF_WEEK_IN_MONTH: "
//                + calendar.get(Calendar.DAY_OF_WEEK_IN_MONTH));
//        System.out.println("AM_PM: " + calendar.get(Calendar.AM_PM));
//        System.out.println("HOUR: " + calendar.get(Calendar.HOUR));
//        System.out.println("HOUR_OF_DAY: " + calendar.get(Calendar.HOUR_OF_DAY));
//        System.out.println("MINUTE: " + calendar.get(Calendar.MINUTE));
//        System.out.println("SECOND: " + calendar.get(Calendar.SECOND));
//        System.out.println("MILLISECOND: " + calendar.get(Calendar.MILLISECOND));
//        System.out.println("ZONE_OFFSET: "
//                + (calendar.get(Calendar.ZONE_OFFSET) / (60 * 60 * 1000))); // in hours
//        System.out.println("DST_OFFSET: "
//                + (calendar.get(Calendar.DST_OFFSET) / (60 * 60 * 1000))); // in hours
//

        ScrollView sv =  new ScrollView(this);

        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.addView(tv1);
        ll.addView(tv2);
        ll.addView(tv3);

        sv.addView(ll);
        setContentView(sv);
    }
}
