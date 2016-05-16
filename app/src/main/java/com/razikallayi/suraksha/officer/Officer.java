package com.razikallayi.suraksha.officer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.razikallayi.suraksha.data.SurakshaContract;

/**
 * Created by Razi Kallayi on 11-05-2016.
 */
public class Officer {
    private Long id;
    private String name;
    private String mobile;
    private String username;
    private String password;
    private String address;
    private boolean isAdmin;
    private String createdAt;
    private String updatedAt;

    public Officer(Context context, String name, String username, String password) {
        this.incrementId(context);
        this.name = name;
        this.username = username;
        this.password = password;
    }

    public Officer() {
    }

    public static String[] OFFICER_COLUMNS = new String[] {
            SurakshaContract.OfficerEntry.COLUMN_NAME,
            SurakshaContract.OfficerEntry.COLUMN_MOBILE,
            SurakshaContract.OfficerEntry.COLUMN_USERNAME,
            SurakshaContract.OfficerEntry.COLUMN_PASSWORD,
            SurakshaContract.OfficerEntry.COLUMN_ADDRESS,
            SurakshaContract.OfficerEntry.COLUMN_IS_ADMIN,
            SurakshaContract.OfficerEntry.COLUMN_CREATED_AT,
            SurakshaContract.OfficerEntry.COLUMN_UPDATED_AT
    };
    // these indices must match the projection
    public static int COL_NAME           =  0;
    public static int COL_MOBILE         =  1;
    public static int COL_USERNAME       =  2;
    public static int COL_PASSWORD       =  3;
    public static int COL_ADDRESS        =  4;
    public static int COL_IS_ADMIN       =  5;
    public static int COL_CREATED_AT     =  6;
    public static int COL_UPDATED_AT     =  7;


    public Officer(Context context, String name, String mobile, String username, String password, String address, boolean isAdmin) {
        this.incrementId(context);
        this.name = name;
        this.mobile = mobile;
        this.username = username;
        this.password = password;
        this.address = address;
        this.isAdmin = isAdmin;
    }

    public static Officer getOfficerFromId(Context context,long id) {
        Cursor cursor = context.getContentResolver().query(
                SurakshaContract.OfficerEntry.buildOfficerUri(id), Officer.OFFICER_COLUMNS,null,null,null);
        Officer o = new Officer();
        if (cursor != null) {
            cursor.moveToFirst();
            o.id = id;
            o.name = cursor.getString(COL_NAME);
            o.mobile = cursor.getString(COL_MOBILE);
            o.username = cursor.getString(COL_USERNAME);
            o.password = cursor.getString(COL_PASSWORD);
            o.address = cursor.getString(COL_ADDRESS);
            o.setAdmin(cursor.getInt(COL_IS_ADMIN)==1?true:false);
        }
        cursor.close();
        return o;
    }

    protected void incrementId(Context context) {
        //TODO use Max instead of count. otherwise it will be a problem when an entry is deleted
        Cursor cursor = context.getContentResolver().query(SurakshaContract.OfficerEntry.CONTENT_URI,
                new String[]{SurakshaContract.OfficerEntry._ID},null,null,null);
        if (cursor != null) {
            this.id = (long)(cursor.getCount() + 1);
            cursor.close();
        }
    }
    public static boolean authenticate(Context context,String username, String password) {
        Cursor cursor = context.getContentResolver().query(SurakshaContract.OfficerEntry.CONTENT_URI,
                new String[]{SurakshaContract.OfficerEntry._ID},
                SurakshaContract.OfficerEntry.COLUMN_USERNAME+" = ? AND "+
                        SurakshaContract.OfficerEntry.COLUMN_PASSWORD+" = ? ",
                new String[]{username,password},
                null);
        cursor.moveToFirst();
        if (cursor != null && cursor.getCount()>0) {
            boolean result = cursor.getCount()>0?true:false;
            cursor.close();
            return result;
        }
        return false;
    }


    public static ContentValues getOfficerContentValues(Officer officer){
        ContentValues values= new ContentValues();

        values.put(SurakshaContract.OfficerEntry.COLUMN_NAME,       officer.getName()              );
        values.put(SurakshaContract.OfficerEntry.COLUMN_MOBILE,     officer.getMobile()             );
        values.put(SurakshaContract.OfficerEntry.COLUMN_USERNAME,   officer.getUsername()            );
        values.put(SurakshaContract.OfficerEntry.COLUMN_PASSWORD,   officer.getPassword()            );
        values.put(SurakshaContract.OfficerEntry.COLUMN_ADDRESS,    officer.getAddress()            );
        values.put(SurakshaContract.OfficerEntry.COLUMN_IS_ADMIN,   officer.isAdmin()?1:0           );
        values.put(SurakshaContract.OfficerEntry.COLUMN_CREATED_AT, System.currentTimeMillis());

        return values;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
