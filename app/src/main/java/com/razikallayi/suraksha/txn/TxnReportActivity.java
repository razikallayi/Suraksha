package com.razikallayi.suraksha.txn;

import android.database.Cursor;
import android.database.DatabaseUtils;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.razikallayi.suraksha.R;
import com.razikallayi.suraksha.data.SurakshaContract;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.Calendar;

public class TxnReportActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>,
        DatePickerDialog.OnDateSetListener
         {

    public static final int TXN_LOADER = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_txn_report);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Calendar now = Calendar.getInstance();
                    DatePickerDialog dpd = DatePickerDialog.newInstance(
                            TxnReportActivity.this,
                            now.get(Calendar.YEAR),
                            now.get(Calendar.MONTH),
                            now.get(Calendar.DAY_OF_MONTH)
                    );
                    dpd.show(getFragmentManager(), "Pick Date");
                }
            });
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getLoaderManager().initLoader(TXN_LOADER, null, this);
    }



    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
//        String time = "You picked the following time: "+hourOfDay+"h"+minute;
//        timeTextView.setText(time);
        String date = "You picked the following date: "+dayOfMonth+"/"+(monthOfYear+1)+"/"+year;
        TextView dateTextView =(TextView) findViewById(R.id.txnDate);
        dateTextView.setText(date);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d("FISH", "onCreateLoader: ");
        String selection = args.getString("selection");
        String[] selectionArgs = args.getStringArray("selectionArgs");
        String sortOrder;
        return new CursorLoader(getApplicationContext(), SurakshaContract.TxnEntry.CONTENT_URI, null,null,null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d("FISH", "onLoadFinished: "+DatabaseUtils.dumpCursorToString(data));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
