package com.razikallayi.suraksha.data;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Razi Kallayi on 03-12-2015.
 */
public class SurakshaDbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Suraksha.db";

    private static final String TEXT_TYPE = " TEXT";
    private static final String INT_TYPE = " INT";
    private static final String COMMA_SEP = ",";
    private static final String NOT_NULL = "NOT NULL";
    private static final String UNIQUE = "UNIQUE";

    private static final String SQL_DELETE_MEMBER_ENTRIES =
            "DROP TABLE IF EXISTS " + SurakshaContract.MemberEntry.TABLE_NAME;
    private static final String SQL_DELETE_ACCOUNT_ENTRIES =
            "DROP TABLE IF EXISTS " + SurakshaContract.AccountEntry.TABLE_NAME;
    private static final String SQL_DELETE_TXN_ENTRIES =
            "DROP TABLE IF EXISTS " + SurakshaContract.TxnEntry.TABLE_NAME;
    private static final String SQL_DELETE_LOAN_PAYED_ENTRIES =
            "DROP TABLE IF EXISTS " + SurakshaContract.LoanPayedEntry.TABLE_NAME;
    private static final String SQL_DELETE_OFFICER_ENTRIES =
            "DROP TABLE IF EXISTS " + SurakshaContract.OfficerEntry.TABLE_NAME;

    private static final String SQL_CREATE_MEMBER_ENTRIES =
            "CREATE TABLE " + SurakshaContract.MemberEntry.TABLE_NAME + " (" +
                    SurakshaContract.MemberEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    SurakshaContract.MemberEntry.COLUMN_NAME                   + TEXT_TYPE + NOT_NULL  + COMMA_SEP +
                    SurakshaContract.MemberEntry.COLUMN_ALIAS                  + TEXT_TYPE + COMMA_SEP +
                    SurakshaContract.MemberEntry.COLUMN_GENDER                 + TEXT_TYPE + COMMA_SEP +
                    SurakshaContract.MemberEntry.COLUMN_FATHER                 + TEXT_TYPE + COMMA_SEP +
                    SurakshaContract.MemberEntry.COLUMN_SPOUSE                 + TEXT_TYPE + COMMA_SEP +
                    SurakshaContract.MemberEntry.COLUMN_OCCUPATION             + TEXT_TYPE + COMMA_SEP +
                    SurakshaContract.MemberEntry.COLUMN_AVATAR                 + TEXT_TYPE + COMMA_SEP +
                    SurakshaContract.MemberEntry.COLUMN_AGE                    + INT_TYPE  + COMMA_SEP +
                    SurakshaContract.MemberEntry.COLUMN_MOBILE                 + TEXT_TYPE + COMMA_SEP +
                    SurakshaContract.MemberEntry.COLUMN_ADDRESS                + TEXT_TYPE + COMMA_SEP +
                    SurakshaContract.MemberEntry.COLUMN_NOMINEE                + TEXT_TYPE + COMMA_SEP +
                    SurakshaContract.MemberEntry.COLUMN_RELATION_WITH_NOMINEE  + TEXT_TYPE + COMMA_SEP +
                    SurakshaContract.MemberEntry.COLUMN_ADDRESS_OF_NOMINEE     + TEXT_TYPE + COMMA_SEP +
                    SurakshaContract.MemberEntry.COLUMN_IS_DELETED             + INT_TYPE  + COMMA_SEP +
                    SurakshaContract.MemberEntry.COLUMN_REMARKS                + TEXT_TYPE + COMMA_SEP +
                    SurakshaContract.MemberEntry.COLUMN_CLOSED_AT              + TEXT_TYPE + COMMA_SEP +
                    SurakshaContract.MemberEntry.COLUMN_CREATED_AT             + TEXT_TYPE + COMMA_SEP +
                    SurakshaContract.MemberEntry.COLUMN_UPDATED_AT             + TEXT_TYPE +
                    " )";
    private static final String SQL_CREATE_ACCOUNT_ENTRIES =
            "CREATE TABLE " + SurakshaContract.AccountEntry.TABLE_NAME + " (" +
                    SurakshaContract.AccountEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    SurakshaContract.AccountEntry.COLUMN_ACCOUNT_NUMBER         + INT_TYPE + NOT_NULL + UNIQUE + COMMA_SEP +
                    SurakshaContract.AccountEntry.COLUMN_MEMBER_ID              + INT_TYPE + COMMA_SEP +
                    SurakshaContract.AccountEntry.COLUMN_OPENING_BALANCE        + INT_TYPE + COMMA_SEP +
                    SurakshaContract.AccountEntry.COLUMN_INSTALMENT_AMOUNT      + INT_TYPE + COMMA_SEP +
                    SurakshaContract.AccountEntry.COLUMN_IS_ACTIVE              + INT_TYPE  + COMMA_SEP +
                    SurakshaContract.AccountEntry.COLUMN_CLOSED_AT              + TEXT_TYPE + COMMA_SEP +
                    SurakshaContract.AccountEntry.COLUMN_CREATED_AT             + TEXT_TYPE + COMMA_SEP +
                    SurakshaContract.AccountEntry.COLUMN_UPDATED_AT             + TEXT_TYPE +
                    " )";


    private static final String SQL_CREATE_TXN_ENTRIES =
            "CREATE TABLE " + SurakshaContract.TxnEntry.TABLE_NAME + " (" +
                    SurakshaContract.TxnEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    SurakshaContract.TxnEntry.COLUMN_FK_ACCOUNT_NUMBER     + INT_TYPE + NOT_NULL  + COMMA_SEP +
                    SurakshaContract.TxnEntry.COLUMN_AMOUNT                + INT_TYPE + COMMA_SEP +
                    SurakshaContract.TxnEntry.COLUMN_DEFINED_DEPOSIT_DATE  + TEXT_TYPE + COMMA_SEP +
                    SurakshaContract.TxnEntry.COLUMN_FK_LOAN_PAYED_ID      + INT_TYPE + NOT_NULL  + COMMA_SEP +
                    SurakshaContract.TxnEntry.COLUMN_VOUCHER_TYPE          + INT_TYPE + COMMA_SEP +
                    SurakshaContract.TxnEntry.COLUMN_LEDGER                + INT_TYPE + COMMA_SEP +
                    SurakshaContract.TxnEntry.COLUMN_NARRATION             + TEXT_TYPE + COMMA_SEP +
                    SurakshaContract.TxnEntry.COLUMN_FK_OFFICER_ID + INT_TYPE + NOT_NULL  + COMMA_SEP +
                    SurakshaContract.TxnEntry.COLUMN_CREATED_AT            + TEXT_TYPE + COMMA_SEP +
                    SurakshaContract.TxnEntry.COLUMN_UPDATED_AT            + TEXT_TYPE +
                    " )";


    private static final String SQL_CREATE_LOAN_PAYED_ENTRIES =
            "CREATE TABLE " + SurakshaContract.LoanPayedEntry.TABLE_NAME + " (" +
                    SurakshaContract.LoanPayedEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    SurakshaContract.LoanPayedEntry.COLUMN_FK_ACCOUNT_NUMBER        + INT_TYPE + NOT_NULL  + COMMA_SEP +
                    SurakshaContract.LoanPayedEntry.COLUMN_SECURITY_ACCOUNT_NUMBER  + INT_TYPE + NOT_NULL  + COMMA_SEP +
                    SurakshaContract.LoanPayedEntry.COLUMN_AMOUNT                   + INT_TYPE + COMMA_SEP +
                    SurakshaContract.LoanPayedEntry.COLUMN_PURPOSE                  + TEXT_TYPE + COMMA_SEP +
                    SurakshaContract.LoanPayedEntry.COLUMN_CLOSED_AT                + TEXT_TYPE + COMMA_SEP +
                    SurakshaContract.LoanPayedEntry.COLUMN_CREATED_AT               + TEXT_TYPE + COMMA_SEP +
                    SurakshaContract.LoanPayedEntry.COLUMN_UPDATED_AT               + TEXT_TYPE +
                    " )";

    private static final String SQL_CREATE_OFFICER_ENTRIES =
            "CREATE TABLE " + SurakshaContract.OfficerEntry.TABLE_NAME + " (" +
                    SurakshaContract.OfficerEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    SurakshaContract.OfficerEntry.COLUMN_NAME          + TEXT_TYPE + COMMA_SEP +
                    SurakshaContract.OfficerEntry.COLUMN_MOBILE        + TEXT_TYPE + COMMA_SEP +
                    SurakshaContract.OfficerEntry.COLUMN_USERNAME      + TEXT_TYPE + COMMA_SEP +
                    SurakshaContract.OfficerEntry.COLUMN_PASSWORD      + TEXT_TYPE + COMMA_SEP +
                    SurakshaContract.OfficerEntry.COLUMN_ADDRESS       + TEXT_TYPE + COMMA_SEP +
                    SurakshaContract.OfficerEntry.COLUMN_IS_ADMIN      + INT_TYPE  + COMMA_SEP +
                    SurakshaContract.OfficerEntry.COLUMN_CREATED_AT    + TEXT_TYPE + COMMA_SEP +
                    SurakshaContract.OfficerEntry.COLUMN_UPDATED_AT    + TEXT_TYPE +
                    " )";

    private static final String SQL_INSERT_DEFAULT_OFFICER =
            "INSERT INTO " + SurakshaContract.OfficerEntry.TABLE_NAME + " values ("
                    + "NULL"                      + COMMA_SEP
                    + "'SYS'"                        + COMMA_SEP
                    + "'9746730324'"                 + COMMA_SEP
                    + "'sys'"                        + COMMA_SEP
                    + "'0797'"                       + COMMA_SEP
                    + "'Kooriyad'"                   + COMMA_SEP
                    + "1"                          + COMMA_SEP
                    + "'"+System.currentTimeMillis()+"'"   + COMMA_SEP
                    + "''"
                    +" )";




    public SurakshaDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_MEMBER_ENTRIES);
        db.execSQL(SQL_CREATE_ACCOUNT_ENTRIES);
        db.execSQL(SQL_CREATE_TXN_ENTRIES);
        db.execSQL(SQL_CREATE_LOAN_PAYED_ENTRIES);
        db.execSQL(SQL_CREATE_OFFICER_ENTRIES);
        db.execSQL(SQL_INSERT_DEFAULT_OFFICER);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over

    //ToDo Remove delete database on upgrade.find alternatives
        db.execSQL(SQL_DELETE_MEMBER_ENTRIES);
        db.execSQL(SQL_DELETE_ACCOUNT_ENTRIES);
        db.execSQL(SQL_DELETE_TXN_ENTRIES);
        db.execSQL(SQL_DELETE_LOAN_PAYED_ENTRIES);
        db.execSQL(SQL_DELETE_OFFICER_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

}
