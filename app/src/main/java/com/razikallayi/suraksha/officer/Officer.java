package com.razikallayi.suraksha.officer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.razikallayi.suraksha.data.SurakshaContract;
import com.razikallayi.suraksha.utils.SettingsUtils;

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
    private long createdAt;
    private long updatedAt;

    public Officer(Context context, String name, String username, String password) {
        this.incrementId(context);
        this.name = name;
        this.username = username;
        this.password = password;
    }

    public Officer() {
    }

    public Officer(Context context, String name, String mobile, String username, String password, String address, boolean isAdmin) {
        this.incrementId(context);
        this.name = name;
        this.mobile = mobile;
        this.username = username;
        this.password = password;
        this.address = address;
        this.isAdmin = isAdmin;
    }

    public static Officer getOfficerFromId(Context context, long id) {
        if (id <= 0) {
            return null;
        }

        Cursor cursor = context.getContentResolver().query(
                SurakshaContract.OfficerEntry.buildOfficerUri(id), OfficerQuery.PROJECTION, null, null, null);
        Officer o = new Officer();
        if (cursor != null) {
            cursor.moveToFirst();
            o.id = id;
            o.name = cursor.getString(OfficerQuery.COL_NAME);
            o.mobile = cursor.getString(OfficerQuery.COL_MOBILE);
            o.username = cursor.getString(OfficerQuery.COL_USERNAME);
            o.password = cursor.getString(OfficerQuery.COL_PASSWORD);
            o.address = cursor.getString(OfficerQuery.COL_ADDRESS);
            o.setAdmin(cursor.getInt(OfficerQuery.COL_IS_ADMIN) == 1);
            cursor.close();
            return o;
        }
        return null;
    }

    public static boolean authenticate(Context context, String username, String password) {
        Cursor cursor = context.getContentResolver().query(SurakshaContract.OfficerEntry.CONTENT_URI,
                new String[]{SurakshaContract.OfficerEntry._ID},
                SurakshaContract.OfficerEntry.COLUMN_USERNAME + " = ? AND " +
                        SurakshaContract.OfficerEntry.COLUMN_PASSWORD + " = ? ",
                new String[]{username.trim(), password},
                null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            long officer_id = cursor.getLong(0);
            SettingsUtils.setAuthOfficer(context, getOfficerFromId(context, officer_id));
            cursor.close();
            return true;
        }
        return false;
    }

    public static ContentValues getOfficerContentValues(Officer officer) {
        ContentValues values = new ContentValues();

        values.put(SurakshaContract.OfficerEntry.COLUMN_NAME, officer.getName());
        values.put(SurakshaContract.OfficerEntry.COLUMN_MOBILE, officer.getMobile());
        values.put(SurakshaContract.OfficerEntry.COLUMN_USERNAME, officer.getUsername());
        values.put(SurakshaContract.OfficerEntry.COLUMN_PASSWORD, officer.getPassword());
        values.put(SurakshaContract.OfficerEntry.COLUMN_ADDRESS, officer.getAddress());
        values.put(SurakshaContract.OfficerEntry.COLUMN_IS_ADMIN, officer.isAdmin() ? 1 : 0);
        values.put(SurakshaContract.OfficerEntry.COLUMN_CREATED_AT, System.currentTimeMillis());

        return values;
    }

    protected void incrementId(Context context) {
        Cursor cursor = context.getContentResolver().query(SurakshaContract.OfficerEntry.CONTENT_URI,
                new String[]{"Max(" + SurakshaContract.OfficerEntry._ID + ")"}, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            this.id = (long) (cursor.getInt(0) + 1);
            cursor.close();
        }
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

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public interface OfficerQuery {
        String[] PROJECTION = {
                SurakshaContract.OfficerEntry.TABLE_NAME + "." + SurakshaContract.OfficerEntry.COLUMN_NAME,
                SurakshaContract.OfficerEntry.TABLE_NAME + "." + SurakshaContract.OfficerEntry.COLUMN_MOBILE,
                SurakshaContract.OfficerEntry.TABLE_NAME + "." + SurakshaContract.OfficerEntry.COLUMN_USERNAME,
                SurakshaContract.OfficerEntry.TABLE_NAME + "." + SurakshaContract.OfficerEntry.COLUMN_PASSWORD,
                SurakshaContract.OfficerEntry.TABLE_NAME + "." + SurakshaContract.OfficerEntry.COLUMN_ADDRESS,
                SurakshaContract.OfficerEntry.TABLE_NAME + "." + SurakshaContract.OfficerEntry.COLUMN_IS_ADMIN,
                SurakshaContract.OfficerEntry.TABLE_NAME + "." + SurakshaContract.OfficerEntry.COLUMN_CREATED_AT,
                SurakshaContract.OfficerEntry.TABLE_NAME + "." + SurakshaContract.OfficerEntry.COLUMN_UPDATED_AT
        };
        // these indices must match the projection
        int COL_NAME = 0;
        int COL_MOBILE = 1;
        int COL_USERNAME = 2;
        int COL_PASSWORD = 3;
        int COL_ADDRESS = 4;
        int COL_IS_ADMIN = 5;
        int COL_CREATED_AT = 6;
        int COL_UPDATED_AT = 7;
    }
}
