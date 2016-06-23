package com.razikallayi.suraksha;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.razikallayi.suraksha.data.SurakshaDbHelper;
import com.razikallayi.suraksha.utils.Utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class DebugActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.debug_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final EditText table = (EditText) findViewById(R.id.table);
        final EditText column = (EditText) findViewById(R.id.column);


        Button btnCapitalizeAll = (Button) findViewById(R.id.capAll);
        btnCapitalizeAll.setText("Capitalize All");

        btnCapitalizeAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String tableString = table.getText().toString();
                final String columnString = column.getText().toString();
                Utility.updateColumnToUpperCase(getApplicationContext(), tableString, columnString);
            }
        });


        Button btnCapitalizeFirstLetter = (Button) findViewById(R.id.capFirstLetter);
        btnCapitalizeFirstLetter.setText("Capitalize First Letter");
        btnCapitalizeFirstLetter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String tableString = table.getText().toString();
                final String columnString = column.getText().toString();
                Utility.updateColumnToWordCase(getApplicationContext(), tableString, columnString);
            }
        });


        Button btnImport = new Button(this);
        btnImport.setText("Import Data from SD");
        btnImport.setBackgroundColor(Color.RED);
        btnImport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                importFromCard(SurakshaDbHelper.DATABASE_NAME);
            }
        });

        Button btnExport = new Button(this);
        btnExport.setText("Export Data to SD");
        btnExport.setBackgroundColor(Color.GREEN);
        btnExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exportToCard(SurakshaDbHelper.DATABASE_NAME);
            }
        });


        LinearLayout ll = (LinearLayout) findViewById(R.id.lldebug);
        ll.addView(btnExport);
        ll.addView(btnImport);
    }


    private void exportToCard(String backupName) {
        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();

            if (sd.canWrite()) {
//             String currentDBPath = "//data//{package name}//databases//{database name}";
//             String backupDBPath = "{database name}";
                String backupDBPath = backupName;

//             File currentDB = new File(data, currentDBPath);
                File currentDB = getDatabasePath(SurakshaDbHelper.DATABASE_NAME);
                File backupDB = new File(sd, backupDBPath);

                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                    Toast.makeText(getBaseContext(), backupDB.toString(), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "No data found!", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            Toast.makeText(getBaseContext(), e.toString(), Toast.LENGTH_LONG).show();
        }
    }


    private void importFromCard(String backupName) {
//        File sd = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) +
//                File.separator + "Your Backup Folder"+
//                File.separator );

        String db_name = SurakshaDbHelper.DATABASE_NAME; //Backup in same name as in database

        File sd = Environment.getExternalStorageDirectory();
        File data = Environment.getDataDirectory();

        String backupDBPath = "/data/" + getPackageName() + "/databases/" + db_name;
        String currentDBPath = backupName;

        File currentDB = new File(sd, currentDBPath);
        File backupDB = new File(data, backupDBPath);

        if (currentDB.exists()) {
            try {
                FileChannel source = new FileInputStream(currentDB).getChannel();
                FileChannel destination = new FileOutputStream(backupDB).getChannel();
                destination.transferFrom(source, 0, source.size());
                source.close();
                destination.close();
                Toast.makeText(this, "importing...", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getBaseContext(), e.toString(), Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "No backups found!", Toast.LENGTH_SHORT).show();
        }
    }
}
