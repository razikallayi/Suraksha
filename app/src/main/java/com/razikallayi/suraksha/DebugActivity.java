package com.razikallayi.suraksha;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.razikallayi.suraksha.data.SurakshaDbHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class DebugActivity extends BaseActivity {

    private final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 0x12;
    private final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 0x14;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.debug_activity);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        Button btnImport = new Button(this);
        btnImport.setText("Import Data from SD");
        btnImport.setBackgroundColor(getResources().getColor(R.color.red_dull));
        btnImport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = getApplicationContext();
                Activity thisActivity = DebugActivity.this;
                if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                    if (ActivityCompat.shouldShowRequestPermissionRationale(thisActivity,
                            android.Manifest.permission.READ_EXTERNAL_STORAGE)) {

                    } else {
                        ActivityCompat.requestPermissions(thisActivity,
                                new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                    }
                } else {
                    importFromCard(SurakshaDbHelper.DATABASE_NAME);
                }
            }
        });


        Button btnExport = new Button(this);
        btnExport.setText("Export Data to SD");
        btnExport.setBackgroundColor(getResources().getColor(R.color.green_dull));
        btnExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = getApplicationContext();
                Activity thisActivity = DebugActivity.this;
                if (ContextCompat.checkSelfPermission(context,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                    if (ActivityCompat.shouldShowRequestPermissionRationale(thisActivity,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                    } else {
                        ActivityCompat.requestPermissions(thisActivity,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                    }
                } else {
                    exportToCard(SurakshaDbHelper.DATABASE_NAME);
                }
            }
        });


        Button btnImportFromAsset = new Button(this);
        btnImportFromAsset.setText("Import Data from Asset");
        btnImportFromAsset.setBackgroundColor(getResources().getColor(R.color.orange_dull));
        btnImportFromAsset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                importFromAsset(SurakshaDbHelper.DATABASE_NAME);
            }
        });

        LinearLayout ll = findViewById(R.id.lldebug);
        ll.addView(btnExport);
        ll.addView(btnImport);
        ll.addView(btnImportFromAsset);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    importFromCard(SurakshaDbHelper.DATABASE_NAME);
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    Toast.makeText(this, "App doesn't have permission to read storage.", Toast.LENGTH_SHORT).show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                exportToCard(SurakshaDbHelper.DATABASE_NAME);
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
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


    private void importFromAsset(String backupName) {
//        File sd = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) +
//                File.separator + "Your Backup Folder"+
//                File.separator );


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
