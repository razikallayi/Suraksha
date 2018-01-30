package com.razikallayi.suraksha_ssf.data;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Razi Kallayi on 03-12-2015.
 */
public class SurakshaDbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION_MAY_2016 = 2; //App Version 1.0
    public static final int DATABASE_VERSION_JUNE_2016 = 3; //App Version 2.0
    public static final int DATABASE_VERSION_FEB_2017 = 4; //App Version 3.0
    public static final int DATABASE_VERSION_JUNE_2017 = 5; //App Version 3.0
    public static final int DATABASE_VERSION_JAN_2018 = 5; //App Version 3.0
    public static final int CURRENT_DATABASE_VERSION = DATABASE_VERSION_JAN_2018;
    public static final String DATABASE_NAME = "Suraksha";

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
            "DROP TABLE IF EXISTS " + SurakshaContract.LoanIssueEntry.TABLE_NAME;
    private static final String SQL_DELETE_OFFICER_ENTRIES =
            "DROP TABLE IF EXISTS " + SurakshaContract.OfficerEntry.TABLE_NAME;

    private static final String SQL_CREATE_MEMBER_ENTRIES =
            "CREATE TABLE " + SurakshaContract.MemberEntry.TABLE_NAME + " (" +
                    SurakshaContract.MemberEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    SurakshaContract.MemberEntry.COLUMN_NAME + TEXT_TYPE + NOT_NULL + COMMA_SEP +
                    SurakshaContract.MemberEntry.COLUMN_ACCOUNT_NO + INT_TYPE + COMMA_SEP +
                    SurakshaContract.MemberEntry.COLUMN_ALIAS + TEXT_TYPE + COMMA_SEP +
                    SurakshaContract.MemberEntry.COLUMN_GENDER + TEXT_TYPE + COMMA_SEP +
                    SurakshaContract.MemberEntry.COLUMN_FATHER + TEXT_TYPE + COMMA_SEP +
                    SurakshaContract.MemberEntry.COLUMN_SPOUSE + TEXT_TYPE + COMMA_SEP +
                    SurakshaContract.MemberEntry.COLUMN_OCCUPATION + TEXT_TYPE + COMMA_SEP +
                    SurakshaContract.MemberEntry.COLUMN_AVATAR + TEXT_TYPE + COMMA_SEP +
                    SurakshaContract.MemberEntry.COLUMN_AGE + INT_TYPE + COMMA_SEP +
                    SurakshaContract.MemberEntry.COLUMN_MOBILE + TEXT_TYPE + COMMA_SEP +
                    SurakshaContract.MemberEntry.COLUMN_ADDRESS + TEXT_TYPE + COMMA_SEP +
                    SurakshaContract.MemberEntry.COLUMN_NOMINEE + TEXT_TYPE + COMMA_SEP +
                    SurakshaContract.MemberEntry.COLUMN_RELATION_WITH_NOMINEE + TEXT_TYPE + COMMA_SEP +
                    SurakshaContract.MemberEntry.COLUMN_ADDRESS_OF_NOMINEE + TEXT_TYPE + COMMA_SEP +
                    SurakshaContract.MemberEntry.COLUMN_IS_DELETED + INT_TYPE + COMMA_SEP +
                    SurakshaContract.MemberEntry.COLUMN_REMARKS + TEXT_TYPE + COMMA_SEP +
                    SurakshaContract.MemberEntry.COLUMN_HAS_LOAN + INT_TYPE + COMMA_SEP +
                    SurakshaContract.MemberEntry.COLUMN_IS_LOAN_BLOCKED + INT_TYPE + COMMA_SEP +
                    SurakshaContract.MemberEntry.COLUMN_IS_SMS_ENABLED + INT_TYPE + COMMA_SEP +
                    SurakshaContract.MemberEntry.COLUMN_CLOSED_AT + TEXT_TYPE + COMMA_SEP +
                    SurakshaContract.MemberEntry.COLUMN_CREATED_AT + TEXT_TYPE + COMMA_SEP +
                    SurakshaContract.MemberEntry.COLUMN_UPDATED_AT + TEXT_TYPE +
                    " )";

    private static final String SQL_CREATE_ACCOUNT_ENTRIES =
            "CREATE TABLE " + SurakshaContract.AccountEntry.TABLE_NAME + " (" +
                    SurakshaContract.AccountEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    SurakshaContract.AccountEntry.COLUMN_ACCOUNT_NUMBER + INT_TYPE + NOT_NULL + UNIQUE + COMMA_SEP +
                    SurakshaContract.AccountEntry.COLUMN_MEMBER_ID + INT_TYPE + COMMA_SEP +
                    SurakshaContract.AccountEntry.COLUMN_OPENING_BALANCE + INT_TYPE + COMMA_SEP +
                    SurakshaContract.AccountEntry.COLUMN_INSTALMENT_AMOUNT + INT_TYPE + COMMA_SEP +
                    SurakshaContract.AccountEntry.COLUMN_IS_ACTIVE + INT_TYPE + COMMA_SEP +
                    SurakshaContract.AccountEntry.COLUMN_CLOSED_AT + TEXT_TYPE + COMMA_SEP +
                    SurakshaContract.AccountEntry.COLUMN_CREATED_AT + TEXT_TYPE + COMMA_SEP +
                    SurakshaContract.AccountEntry.COLUMN_UPDATED_AT + TEXT_TYPE +
                    " )";


    private static final String SQL_CREATE_TXN_ENTRIES =
            "CREATE TABLE " + SurakshaContract.TxnEntry.TABLE_NAME + " (" +
                    SurakshaContract.TxnEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    SurakshaContract.TxnEntry.COLUMN_FK_ACCOUNT_NUMBER + INT_TYPE + NOT_NULL + COMMA_SEP +
                    SurakshaContract.TxnEntry.COLUMN_AMOUNT + INT_TYPE + COMMA_SEP +
                    SurakshaContract.TxnEntry.COLUMN_DEPOSIT_FOR_DATE + TEXT_TYPE + COMMA_SEP +
                    SurakshaContract.TxnEntry.COLUMN_INSTALMENT_NUMBER + INT_TYPE + COMMA_SEP +
                    SurakshaContract.TxnEntry.COLUMN_FK_LOAN_PAYED_ID + INT_TYPE + NOT_NULL + COMMA_SEP +
                    SurakshaContract.TxnEntry.COLUMN_PAYMENT_DATE + TEXT_TYPE + COMMA_SEP +
                    SurakshaContract.TxnEntry.COLUMN_VOUCHER_TYPE + INT_TYPE + COMMA_SEP +
                    SurakshaContract.TxnEntry.COLUMN_LEDGER + INT_TYPE + COMMA_SEP +
                    SurakshaContract.TxnEntry.COLUMN_NARRATION + TEXT_TYPE + COMMA_SEP +
                    SurakshaContract.TxnEntry.COLUMN_FK_OFFICER_ID + INT_TYPE + NOT_NULL + COMMA_SEP +
                    SurakshaContract.TxnEntry.COLUMN_CREATED_AT + TEXT_TYPE + COMMA_SEP +
                    SurakshaContract.TxnEntry.COLUMN_UPDATED_AT + TEXT_TYPE +
                    " )";


    private static final String SQL_CREATE_LOAN_PAYED_ENTRIES =
            "CREATE TABLE " + SurakshaContract.LoanIssueEntry.TABLE_NAME + " (" +
                    SurakshaContract.LoanIssueEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    SurakshaContract.LoanIssueEntry.COLUMN_ACCOUNT_NUMBER + INT_TYPE + NOT_NULL + COMMA_SEP +
                    SurakshaContract.LoanIssueEntry.COLUMN_SECURITY_ACCOUNT_NUMBER + INT_TYPE + NOT_NULL + COMMA_SEP +
                    SurakshaContract.LoanIssueEntry.COLUMN_AMOUNT + INT_TYPE + COMMA_SEP +
                    SurakshaContract.LoanIssueEntry.COLUMN_PURPOSE + TEXT_TYPE + COMMA_SEP +
                    SurakshaContract.LoanIssueEntry.COLUMN_LOAN_INSTALMENT_TIMES + INT_TYPE + COMMA_SEP +
                    SurakshaContract.LoanIssueEntry.COLUMN_LOAN_INSTALMENT_AMOUNT + INT_TYPE + COMMA_SEP +
                    SurakshaContract.LoanIssueEntry.COLUMN_OFFICE_STATEMENT + TEXT_TYPE + COMMA_SEP +
                    SurakshaContract.LoanIssueEntry.COLUMN_ISSUED_AT + TEXT_TYPE + COMMA_SEP +
                    SurakshaContract.LoanIssueEntry.COLUMN_CLOSED_AT + TEXT_TYPE + COMMA_SEP +
                    SurakshaContract.LoanIssueEntry.COLUMN_CREATED_AT + TEXT_TYPE + COMMA_SEP +
                    SurakshaContract.LoanIssueEntry.COLUMN_UPDATED_AT + TEXT_TYPE +
                    " )";

    private static final String SQL_CREATE_OFFICER_ENTRIES =
            "CREATE TABLE " + SurakshaContract.OfficerEntry.TABLE_NAME + " (" +
                    SurakshaContract.OfficerEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    SurakshaContract.OfficerEntry.COLUMN_NAME + TEXT_TYPE + COMMA_SEP +
                    SurakshaContract.OfficerEntry.COLUMN_MOBILE + TEXT_TYPE + COMMA_SEP +
                    SurakshaContract.OfficerEntry.COLUMN_USERNAME + TEXT_TYPE + COMMA_SEP +
                    SurakshaContract.OfficerEntry.COLUMN_PASSWORD + TEXT_TYPE + COMMA_SEP +
                    SurakshaContract.OfficerEntry.COLUMN_ADDRESS + TEXT_TYPE + COMMA_SEP +
                    SurakshaContract.OfficerEntry.COLUMN_IS_ADMIN + INT_TYPE + COMMA_SEP +
                    SurakshaContract.OfficerEntry.COLUMN_CREATED_AT + TEXT_TYPE + COMMA_SEP +
                    SurakshaContract.OfficerEntry.COLUMN_UPDATED_AT + TEXT_TYPE +
                    " )";

    private static final String SQL_INSERT_DEFAULT_OFFICER =
            "INSERT INTO " + SurakshaContract.OfficerEntry.TABLE_NAME + " values ("
                    + "NULL" + COMMA_SEP
                    + "'SYS'" + COMMA_SEP
                    + "'8281266408'" + COMMA_SEP
                    + "'SYS'" + COMMA_SEP
                    + "'0797'" + COMMA_SEP
                    + "'Kooriyad'" + COMMA_SEP
                    + "1" + COMMA_SEP
                    + "'" + System.currentTimeMillis() + "'" + COMMA_SEP
                    + "''"
                    + " )";

    private static final String SQL_INSERT_DEVELOPER_OFFICER =
            "INSERT INTO " + SurakshaContract.OfficerEntry.TABLE_NAME + " values ("
                    + "NULL" + COMMA_SEP
                    + "'RAZI'" + COMMA_SEP
                    + "'9746730324'" + COMMA_SEP
                    + "'RAZI'" + COMMA_SEP
                    + "'4976'" + COMMA_SEP
                    + "'Kallayi House'" + COMMA_SEP
                    + "1" + COMMA_SEP
                    + "'" + System.currentTimeMillis() + "'" + COMMA_SEP
                    + "''"
                    + " )";


    //Updates of JUNE_2016
    private static final String SQL_ALTER_MEMBER_ADD_ACCOUNT_NUMBER =
            "alter table " + SurakshaContract.MemberEntry.TABLE_NAME + " add column "
                    + SurakshaContract.MemberEntry.COLUMN_ACCOUNT_NO + INT_TYPE;

    //    add column is_loan_blocked to member table
    private static final String SQL_ALTER_MEMBER_ADD_HAS_LOAN =
            "alter table " + SurakshaContract.MemberEntry.TABLE_NAME + " add column "
                    + SurakshaContract.MemberEntry.COLUMN_HAS_LOAN + INT_TYPE;

    //    add column is_loan_blocked to member table
    private static final String SQL_ALTER_MEMBER_ADD_IS_LOAN_BLOCKED =
            "alter table " + SurakshaContract.MemberEntry.TABLE_NAME + " add column "
                    + SurakshaContract.MemberEntry.COLUMN_IS_LOAN_BLOCKED + INT_TYPE;

    private static final String SQL_ALTER_LOAN_ISSUE_ADD_ISSUE_DATE =
            "alter table " + SurakshaContract.LoanIssueEntry.TABLE_NAME + " add column "
                    + SurakshaContract.LoanIssueEntry.COLUMN_ISSUED_AT + TEXT_TYPE;

    private static final String SQL_UPDATE_LOAN_ISSUED_TO_CREATED_AT =
            "update " + SurakshaContract.LoanIssueEntry.TABLE_NAME + " set "
                    + SurakshaContract.LoanIssueEntry.COLUMN_ISSUED_AT + " = "
                    + SurakshaContract.LoanIssueEntry.COLUMN_CREATED_AT;

    private static final String SQL_ALTER_TRANSACTION_ADD_PAYMENT_DATE =
            "alter table " + SurakshaContract.TxnEntry.TABLE_NAME + " add column "
                    + SurakshaContract.TxnEntry.COLUMN_PAYMENT_DATE + TEXT_TYPE;

    private static final String SQL_ALTER_MEMBER_ADD_IS_SMS_ENABLED =
            "alter table " + SurakshaContract.MemberEntry.TABLE_NAME + " add column "
                    + SurakshaContract.MemberEntry.COLUMN_IS_SMS_ENABLED + INT_TYPE;

    private static final String SQL_UPDATE_MEMBER_COLUMN_IS_SMS_ENABLED_TO_TRUE =
            "update " + SurakshaContract.MemberEntry.TABLE_NAME + " set "
                    + SurakshaContract.MemberEntry.COLUMN_IS_SMS_ENABLED + " = 1";

    private static final String SQL_ALTER_TRANSACTION_ADD_INSTALMENT_NUMBER =
            "alter table " + SurakshaContract.TxnEntry.TABLE_NAME + " add column "
                    + SurakshaContract.TxnEntry.COLUMN_INSTALMENT_NUMBER + INT_TYPE;

    private static final String SQL_UPDATE_TXN_COLUMN_PAYMENT_DATE_TO_CREATED_AT =
            "update " + SurakshaContract.TxnEntry.TABLE_NAME + " set "
                    + SurakshaContract.TxnEntry.COLUMN_PAYMENT_DATE + " = "
                    + SurakshaContract.TxnEntry.COLUMN_CREATED_AT;


    private static final String SQL_UPDATE_TXN_COLUMN_UPDATED_AT_TO_CREATED_AT_WHERE_NULL =
            "update " + SurakshaContract.TxnEntry.TABLE_NAME + " set "
                    + SurakshaContract.TxnEntry.COLUMN_UPDATED_AT + " = "
                    + SurakshaContract.TxnEntry.COLUMN_CREATED_AT + " where "
                    + SurakshaContract.TxnEntry.COLUMN_UPDATED_AT + " IS NULL";

    private static final String SQL_UPDATE_TXN_INSTALMENT_NO_TO_MINUS_ONE_WHERE_NULL =
            "update " + SurakshaContract.TxnEntry.TABLE_NAME + " set "
                    + SurakshaContract.TxnEntry.COLUMN_INSTALMENT_NUMBER + " = -1 where "
                    + SurakshaContract.TxnEntry.COLUMN_INSTALMENT_NUMBER + " IS NULL" +
                    "";


    //    update member account no to member id
    private static final String SQL_UPDATE_MEMBER_ACCOUNT_NUMBER =
            "update " + SurakshaContract.MemberEntry.TABLE_NAME + " set "
                    + SurakshaContract.MemberEntry.COLUMN_ACCOUNT_NO + " = "
                    + SurakshaContract.MemberEntry._ID;

    //    update member has_loan to false
    private static final String SQL_UPDATE_MEMBER_HAS_LOAN_TO_FALSE =
            "update " + SurakshaContract.MemberEntry.TABLE_NAME + " set "
                    + SurakshaContract.MemberEntry.COLUMN_HAS_LOAN + " = 0";

    //    update member loan blocked to false
    private static final String SQL_UPDATE_MEMBER_IS_LOAN_BLOCKED_TO_FALSE =
            "update " + SurakshaContract.MemberEntry.TABLE_NAME + " set "
                    + SurakshaContract.MemberEntry.COLUMN_IS_LOAN_BLOCKED + " = 0";

    private static final String DELETE_EXTRA_ACCOUNT =
            "delete from " + SurakshaContract.AccountEntry.TABLE_NAME + " where " +
                    SurakshaContract.AccountEntry.COLUMN_ACCOUNT_NUMBER + " > 150";

    private static final String DELETE_ALL_TXN_OF_EXTRA_ACCOUNTS =
            "delete from " + SurakshaContract.TxnEntry.TABLE_NAME + " where " +
                    SurakshaContract.TxnEntry.COLUMN_FK_ACCOUNT_NUMBER + " > 150";

    //    update member loan blocked to false
    private static final String SQL_UPDATE_DEFAULT_OFFICER_NAME_TO_SSP =
            "update " + SurakshaContract.OfficerEntry.TABLE_NAME + " set "
                    + SurakshaContract.OfficerEntry.COLUMN_NAME + " = 'SSP'"
                    + " where UPPER(" +
                    SurakshaContract.OfficerEntry.COLUMN_USERNAME + ") = 'SYS'";


    //    update member loan blocked to false
    private static final String SQL_UPDATE_DEFAULT_OFFICER_USERNAME_TO_SSP =
            "update " + SurakshaContract.OfficerEntry.TABLE_NAME + " set "
                    + SurakshaContract.OfficerEntry.COLUMN_USERNAME + " = 'SSP'"
                    + " where UPPER(" +
                    SurakshaContract.OfficerEntry.COLUMN_USERNAME + ") = 'SYS'";


    private static final String CAPITALIZE_OFFICER_USERNAME =
            "UPDATE " + SurakshaContract.OfficerEntry.TABLE_NAME + " SET " +
                    SurakshaContract.OfficerEntry.COLUMN_USERNAME +
                    "= UPPER(" + SurakshaContract.OfficerEntry.COLUMN_USERNAME + ")";

    private static final String CAPITALIZE_OFFICER_NAME =
            "UPDATE " + SurakshaContract.OfficerEntry.TABLE_NAME + " SET " +
                    SurakshaContract.OfficerEntry.COLUMN_NAME +
                    "= UPPER(" + SurakshaContract.OfficerEntry.COLUMN_NAME + ")";


    public SurakshaDbHelper(Context context) {
        super(context, DATABASE_NAME, null, CURRENT_DATABASE_VERSION
        );
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_MEMBER_ENTRIES);
        db.execSQL(SQL_CREATE_ACCOUNT_ENTRIES);
        db.execSQL(SQL_CREATE_TXN_ENTRIES);
        db.execSQL(SQL_CREATE_LOAN_PAYED_ENTRIES);
        db.execSQL(SQL_CREATE_OFFICER_ENTRIES);
        db.execSQL(SQL_INSERT_DEFAULT_OFFICER);
        db.execSQL(SQL_INSERT_DEVELOPER_OFFICER);


    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over

        int version = oldVersion;

        if (version == DATABASE_VERSION_MAY_2016) {
            Log.d("SurakshaDbHelper", "onUpgrade: DATABASE_VERSION_MAY_2016");
            db.execSQL(DELETE_EXTRA_ACCOUNT);
            db.execSQL(DELETE_ALL_TXN_OF_EXTRA_ACCOUNTS);
            db.execSQL(SQL_DELETE_LOAN_PAYED_ENTRIES);
            db.execSQL(SQL_CREATE_LOAN_PAYED_ENTRIES);
            db.execSQL(SQL_ALTER_MEMBER_ADD_ACCOUNT_NUMBER);
            db.execSQL(SQL_ALTER_MEMBER_ADD_HAS_LOAN);
            db.execSQL(SQL_ALTER_MEMBER_ADD_IS_LOAN_BLOCKED);
            db.execSQL(SQL_UPDATE_MEMBER_ACCOUNT_NUMBER);
            db.execSQL(SQL_UPDATE_MEMBER_HAS_LOAN_TO_FALSE);
            db.execSQL(SQL_UPDATE_MEMBER_IS_LOAN_BLOCKED_TO_FALSE);
            db.execSQL(CAPITALIZE_OFFICER_NAME);
            db.execSQL(CAPITALIZE_OFFICER_USERNAME);
            db.execSQL(SQL_UPDATE_DEFAULT_OFFICER_NAME_TO_SSP);
            db.execSQL(SQL_UPDATE_DEFAULT_OFFICER_USERNAME_TO_SSP);
            version = DATABASE_VERSION_JUNE_2016;
            Log.d("SurakshaDbHelper", "onUpgrade: DATABASE_VERSION_JUNE_2016");
        }
        if (version == DATABASE_VERSION_JUNE_2016) {
            db.execSQL(SQL_ALTER_LOAN_ISSUE_ADD_ISSUE_DATE);
            db.execSQL(SQL_UPDATE_LOAN_ISSUED_TO_CREATED_AT);
            version = DATABASE_VERSION_FEB_2017;
            Log.d("SurakshaDbHelper", "onUpgrade: DATABASE_VERSION_FEB_2017");
        }

        if (version == DATABASE_VERSION_FEB_2017) {
            db.execSQL(SQL_ALTER_TRANSACTION_ADD_PAYMENT_DATE);
            db.execSQL(SQL_ALTER_TRANSACTION_ADD_INSTALMENT_NUMBER);
            db.execSQL(SQL_UPDATE_TXN_COLUMN_PAYMENT_DATE_TO_CREATED_AT);

            version = DATABASE_VERSION_JUNE_2017;
            Log.d("SurakshaDbHelper", "onUpgrade: DATABASE_VERSION_JUNE_2017");
        }

        if (version == DATABASE_VERSION_JUNE_2017) {
            db.execSQL(SQL_UPDATE_TXN_COLUMN_UPDATED_AT_TO_CREATED_AT_WHERE_NULL);
            db.execSQL(SQL_UPDATE_TXN_INSTALMENT_NO_TO_MINUS_ONE_WHERE_NULL);
            db.execSQL(SQL_ALTER_MEMBER_ADD_IS_SMS_ENABLED);
            db.execSQL(SQL_UPDATE_MEMBER_COLUMN_IS_SMS_ENABLED_TO_TRUE);

            version = DATABASE_VERSION_JAN_2018;
            Log.d("SurakshaDbHelper", "onUpgrade: DATABASE_VERSION_JUNE_2017");
        }

        //At this point, If database not updated to current Db, we will flush all data.
        if (version != CURRENT_DATABASE_VERSION) {
            db.execSQL(SQL_DELETE_MEMBER_ENTRIES);
            db.execSQL(SQL_DELETE_ACCOUNT_ENTRIES);
            db.execSQL(SQL_DELETE_TXN_ENTRIES);
            db.execSQL(SQL_DELETE_LOAN_PAYED_ENTRIES);
            db.execSQL(SQL_DELETE_OFFICER_ENTRIES);
            onCreate(db);
        }
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

}
