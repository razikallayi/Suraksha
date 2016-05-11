package com.razikallayi.suraksha;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.razikallayi.suraksha.data.SurakshaContract;

/**
 * Created by Razi Kallayi on 11-05-2016.
 */
public class User {
    private Long id;
    private String name;
    private String mobile;
    private String username;
    private int password;
    private String address;
    private boolean isAdmin;
    private String createdAt;
    private String updatedAt;

    public User(Context context, String name, String username, int password) {
        this.incrementId(context);
        this.name = name;
        this.username = username;
        this.password = password;
    }

    public User() {
    }

    public User(Context context, String name, String mobile, String username, int password, String address, boolean isAdmin) {
        this.incrementId(context);
        this.name = name;
        this.mobile = mobile;
        this.username = username;
        this.password = password;
        this.address = address;
        this.isAdmin = isAdmin;
    }

    protected void incrementId(Context context) {
        Cursor cursor = context.getContentResolver().query(SurakshaContract.UserEntry.CONTENT_URI,
                new String[]{SurakshaContract.UserEntry._ID},null,null,null);
        if (cursor != null) {
            this.id = (long)(cursor.getCount() + 1);
            cursor.close();
        }
    }

    public static ContentValues getUserContentValues(User user){
        ContentValues values= new ContentValues();

        values.put(SurakshaContract.UserEntry.COLUMN_NAME,                  user.getName()              );
        values.put(SurakshaContract.UserEntry.COLUMN_MOBILE,                 user.getMobile()             );
        values.put(SurakshaContract.UserEntry.COLUMN_USERNAME,                user.getUsername()            );
        values.put(SurakshaContract.UserEntry.COLUMN_PASSWORD,                user.getPassword()            );
        values.put(SurakshaContract.UserEntry.COLUMN_ADDRESS,                user.getAddress()            );
        values.put(SurakshaContract.UserEntry.COLUMN_IS_ADMIN,               user.isAdmin()?1:0           );
        values.put(SurakshaContract.UserEntry.COLUMN_CREATED_AT, System.currentTimeMillis());

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

    public int getPassword() {
        return password;
    }

    public void setPassword(int password) {
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