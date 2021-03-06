package com.razikallayi.suraksha_ssf.data;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.razikallayi.suraksha_ssf.utils.CalendarUtils;


public class SurakshaProvider extends ContentProvider {
    static final int MEMBER = 100;
    static final int MEMBER_ID = 101;
    static final int MEMBER_JOIN_ACCOUNT = 110;
    static final int ACCOUNT = 200;
    static final int ACCOUNT_NUMBER = 202;
    static final int ACCOUNTS_OF_MEMBER = 210;
    static final int TXN = 300;
    static final int TXN_ID = 301;
    static final int TXN_ON_DATE = 310;
    static final int TXN_OF_ACCOUNT = 320;
    static final int TXN_GET_WMF = 340; //getWorkinMoneyFund
    static final int TXN_FETCH_DEPOSITS = 345;
    static final int TXN_GET_TOTAL_DEPOSIT = 350;
    static final int TXN_GET_DEPOSIT_OF_ACCOUNT = 351;
    static final int TXN_GET_DEPOSIT_ON_DATE = 352;
    static final int TXN_TOTAL_LOAN_PAYED = 360;
    static final int TXN_TOTAL_LOAN_RETURN = 361;
    static final int OFFICER = 400;
    static final int OFFICER_ID = 401;
    static final int OFFICER_EXIST = 402;
    static final int LOAN_ISSUE = 500;
    static final int LOAN_ISSUE_ID = 501;
    static final int LOAN_ISSUE_ONLY = 600;

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private static final SQLiteQueryBuilder sAccountsOfMemberQueryBuilder;
    private static final SQLiteQueryBuilder sLoanIssueJoinTxnQueryBuilder;
    private static final SQLiteQueryBuilder sTxnAccountQueryBuilder;
    //location.location_setting = ?
    private static final String sAccountNumberSelection =
            SurakshaContract.AccountEntry.TABLE_NAME +
                    "." + SurakshaContract.AccountEntry.COLUMN_ACCOUNT_NUMBER + " = ? ";
    //location.location_setting = ? AND date >= ?
//    private static final String sMemberNameOrAccountSelection =
//            SurakshaContract.MemberEntry.TABLE_NAME +
//                    "." + SurakshaContract.MemberEntry.COLUMN_NAME + " = ? OR " +
//                    SurakshaContract.AccountEntry.COLUMN_ACCOUNT_NUMBER + " = ? ";

    static {
        sAccountsOfMemberQueryBuilder = new SQLiteQueryBuilder();

        //This is an inner join which looks like
        //member INNER JOIN account ON account.member_id = Member._id
        sAccountsOfMemberQueryBuilder.setTables(
                SurakshaContract.MemberEntry.TABLE_NAME + " JOIN " +
                        SurakshaContract.AccountEntry.TABLE_NAME +
                        " ON " + SurakshaContract.MemberEntry.TABLE_NAME +
                        "." + SurakshaContract.MemberEntry._ID +
                        " = " + SurakshaContract.AccountEntry.TABLE_NAME +
                        "." + SurakshaContract.AccountEntry.COLUMN_MEMBER_ID
        );
    }

    static {
        sLoanIssueJoinTxnQueryBuilder = new SQLiteQueryBuilder();

        //This is an inner join which looks like
        //member INNER JOIN account ON account.member_id = Member._id
        sLoanIssueJoinTxnQueryBuilder.setTables(
                SurakshaContract.LoanIssueEntry.TABLE_NAME + " LEFT JOIN " +
                        SurakshaContract.TxnEntry.TABLE_NAME +
                        " ON " + SurakshaContract.TxnEntry.TABLE_NAME +
                        "." + SurakshaContract.TxnEntry.COLUMN_FK_LOAN_PAYED_ID +
                        " = " + SurakshaContract.LoanIssueEntry.TABLE_NAME +
                        "." + SurakshaContract.LoanIssueEntry._ID
        );
    }

    static {
        sTxnAccountQueryBuilder = new SQLiteQueryBuilder();

        //This is an inner join which looks like
        //member INNER JOIN account ON account.member_id = Member._id
//        sTxnAccountQueryBuilder.setTables(
//                SurakshaContract.TxnEntry.TABLE_NAME + " INNER JOIN " +
//                        SurakshaContract.AccountEntry.TABLE_NAME +
//                        " ON " + SurakshaContract.AccountEntry.TABLE_NAME +
//                        "." + SurakshaContract.AccountEntry.COLUMN_ACCOUNT_NUMBER +
//                        " = " + SurakshaContract.TxnEntry.TABLE_NAME +
//                        "." + SurakshaContract.TxnEntry.COLUMN_ACCOUNT_NUMBER);

        sTxnAccountQueryBuilder.setTables(SurakshaContract.TxnEntry.TABLE_NAME);
    }


//    //location.location_setting = ?
//    private static final String sMemberIdSelection =
//            SurakshaContract.MemberEntry.TABLE_NAME +
//                    "." + SurakshaContract.MemberEntry._ID + " = ? ";

    private SurakshaDbHelper mOpenHelper;

    static UriMatcher buildUriMatcher() {
        // All paths added to the UriMatcher have a corresponding code to return when a match is
        // found.  The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = SurakshaContract.CONTENT_AUTHORITY;

        // For each type of URI you want to add, create a corresponding code.
        matcher.addURI(authority, SurakshaContract.PATH_MEMBER, MEMBER);
        matcher.addURI(authority, SurakshaContract.PATH_MEMBER + "/#", MEMBER_ID);
        matcher.addURI(authority, SurakshaContract.PATH_MEMBER_JOIN_ACCOUNT, MEMBER_JOIN_ACCOUNT);

        // For each type of URI you want to add, create a corresponding code.
        matcher.addURI(authority, SurakshaContract.PATH_OFFICER, OFFICER);
        matcher.addURI(authority, SurakshaContract.PATH_OFFICER + "/#", OFFICER_ID);
        matcher.addURI(authority, SurakshaContract.PATH_OFFICER_EXIST + "/*", OFFICER_EXIST);

        matcher.addURI(authority, SurakshaContract.PATH_ACCOUNT, ACCOUNT);
        matcher.addURI(authority, SurakshaContract.PATH_ACCOUNT_NUMBER + "/#", ACCOUNT_NUMBER);
        matcher.addURI(authority, SurakshaContract.PATH_ACCOUNT_OF_MEMBER + "/*", ACCOUNTS_OF_MEMBER);

        matcher.addURI(authority, SurakshaContract.PATH_TXN, TXN);
        matcher.addURI(authority, SurakshaContract.PATH_TXN + "/#", TXN_ID);
        matcher.addURI(authority, SurakshaContract.PATH_TXN_ON_DATE + "/*", TXN_ON_DATE);
        matcher.addURI(authority, SurakshaContract.PATH_TXN_OF_ACCOUNT + "/*", TXN_OF_ACCOUNT);
        matcher.addURI(authority, SurakshaContract.PATH_TXN_GET_WMF, TXN_GET_WMF);
        matcher.addURI(authority, SurakshaContract.PATH_TXN_FETCH_DEPOSITS, TXN_FETCH_DEPOSITS);
        matcher.addURI(authority, SurakshaContract.PATH_TXN_GET_TOTAL_DEPOSIT, TXN_GET_TOTAL_DEPOSIT);
        matcher.addURI(authority, SurakshaContract.PATH_TXN_GET_DEPOSIT_OF_ACCOUNT + "/*", TXN_GET_DEPOSIT_OF_ACCOUNT);
        matcher.addURI(authority, SurakshaContract.PATH_TXN_GET_DEPOSIT_ON_DATE + "/*", TXN_GET_DEPOSIT_ON_DATE);
        matcher.addURI(authority, SurakshaContract.PATH_TXN_TOTAL_LOAN_PAYED, TXN_TOTAL_LOAN_PAYED);
        matcher.addURI(authority, SurakshaContract.PATH_TXN_TOTAL_LOAN_RETURN, TXN_TOTAL_LOAN_RETURN);

        matcher.addURI(authority, SurakshaContract.PATH_LOAN_ISSUE, LOAN_ISSUE);
        matcher.addURI(authority, SurakshaContract.PATH_LOAN_ISSUE + "/#", LOAN_ISSUE_ID);
        matcher.addURI(authority, SurakshaContract.PATH_LOAN_ISSUE_ONLY, LOAN_ISSUE_ONLY);

        return matcher;
    }

    private Cursor getTxnsOnDate(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        final long date = SurakshaContract.TxnEntry.getDateFromUri(uri);

        //transactions.created_at = ?
        final String sTxnOnDateSelection =
                SurakshaContract.TxnEntry.TABLE_NAME +
                        "." + SurakshaContract.TxnEntry.COLUMN_CREATED_AT + " = ? ";

        return mOpenHelper.getReadableDatabase().query(
                SurakshaContract.TxnEntry.TABLE_NAME,
                projection,
                sTxnOnDateSelection,
                new String[]{String.valueOf(date)},
                null,
                null,
                sortOrder
        );
    }

    private Cursor getTxnOfAccount(Uri uri, String[] projection, String selection,
                                   String[] selectionArgs, String sortOrder) {
        String account_number = SurakshaContract.TxnEntry.getAccountNumberFromUri(uri);
        return sTxnAccountQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection != null ? selection : SurakshaContract.TxnEntry.COLUMN_FK_ACCOUNT_NUMBER + " = ? ",
                selection != null ? selectionArgs : new String[]{account_number},
                null,
                null,
                sortOrder
        );
    }


    @Override
    public boolean onCreate() {
        mOpenHelper = new SurakshaDbHelper(getContext());
        return true;
    }


    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        Log.d("DB", "Query in Provider: " + uri.toString());
        Log.d("DB", "Matcher Provider: " + sUriMatcher.match(uri));
        switch (sUriMatcher.match(uri)) {
            case MEMBER: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        SurakshaContract.MemberEntry.TABLE_NAME,
                        projection,
                        selection, selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            case MEMBER_ID: {
                final String memberIdSelection = SurakshaContract.MemberEntry.TABLE_NAME +
                        "." + SurakshaContract.MemberEntry._ID + " = ? ";
                String id = SurakshaContract.MemberEntry.getMemberId(uri);

                retCursor = mOpenHelper.getReadableDatabase().query(
                        SurakshaContract.MemberEntry.TABLE_NAME,
                        projection,
                        memberIdSelection,
                        new String[]{id},
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case MEMBER_JOIN_ACCOUNT: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        true,
                        sAccountsOfMemberQueryBuilder.getTables(),
                        projection,
                        selection, selectionArgs,
                        null,
                        null,
                        sortOrder,
                        null
                );
                break;
            }
//            case ACCOUNTS_OF_MEMBER: {
//                String memberId = SurakshaContract.AccountEntry.getMemberIdFromUri(uri);
//                retCursor = sAccountsOfMemberQueryBuilder.query(mOpenHelper.getReadableDatabase(),
//                        projection,
//                        sMemberIdSelection,
//                        new String[]{memberId},
//                        null,
//                        null,
//                        sortOrder
//                );
//                break;
//            }
            case ACCOUNT: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        SurakshaContract.AccountEntry.TABLE_NAME,
                        projection,
                        selection, selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case ACCOUNT_NUMBER: {
                String accountNumber = SurakshaContract.AccountEntry.getAccountNumber(uri);
                retCursor = mOpenHelper.getReadableDatabase().query(
                        SurakshaContract.AccountEntry.TABLE_NAME,
                        projection,
                        sAccountNumberSelection,
                        new String[]{accountNumber},
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case OFFICER: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        SurakshaContract.OfficerEntry.TABLE_NAME,
                        projection,
                        selection, selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case OFFICER_ID: {
                String id = SurakshaContract.OfficerEntry.getOfficerId(uri);
                String idSelection = SurakshaContract.OfficerEntry.TABLE_NAME +
                        "." + SurakshaContract.OfficerEntry._ID + " = ? ";
                retCursor = mOpenHelper.getReadableDatabase().query(
                        SurakshaContract.OfficerEntry.TABLE_NAME,
                        projection,
                        idSelection, new String[]{id},
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case OFFICER_EXIST: {
                String username = SurakshaContract.OfficerEntry.getUsername(uri);
                String selectionUsername = SurakshaContract.OfficerEntry.TABLE_NAME +
                        "." + SurakshaContract.OfficerEntry.COLUMN_USERNAME + " = ? ";
                retCursor = mOpenHelper.getReadableDatabase().query(
                        SurakshaContract.OfficerEntry.TABLE_NAME,
                        new String[]{SurakshaContract.OfficerEntry._ID},
                        selectionUsername, new String[]{username},
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case TXN: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        SurakshaContract.TxnEntry.TABLE_NAME,
                        projection,
                        selection, selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case TXN_ID: {
                final String txnIdSelection = SurakshaContract.TxnEntry.TABLE_NAME +
                        "." + SurakshaContract.TxnEntry._ID + " = ? ";
                String id = SurakshaContract.TxnEntry.getTxnId(uri);

                retCursor = mOpenHelper.getReadableDatabase().query(
                        SurakshaContract.TxnEntry.TABLE_NAME,
                        projection,
                        txnIdSelection,
                        new String[]{id},
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case TXN_ON_DATE: {
                retCursor = getTxnsOnDate(uri, projection, selection, selectionArgs, sortOrder);
                break;
            }
            case TXN_OF_ACCOUNT: {
                retCursor = getTxnOfAccount(uri, projection, selection, selectionArgs, sortOrder);
                break;
            }
            case TXN_GET_WMF: {
                //select (select sum (amount) from transactions where ledger = "REGISTRATION_FEE" )
                // - (select sum (amount) from transactions where ledger = "WORKING_COST") as balance
//                String strQuery = "select (select sum (" + SurakshaContract.TxnEntry.COLUMN_AMOUNT
//                        + ") from " + SurakshaContract.TxnEntry.TABLE_NAME + " where "
//                        + SurakshaContract.TxnEntry.COLUMN_LEDGER + " = ?)-(select sum("
//                        + SurakshaContract.TxnEntry.COLUMN_AMOUNT + ") from " + SurakshaContract.TxnEntry.TABLE_NAME
//                        + " where " + SurakshaContract.TxnEntry.COLUMN_LEDGER + " = ?) as balance";
//

                String strQuery = "select sum(case when ledger = 1 and voucher_type = 101 then amount else 0 end) - sum(case when ledger = 4 and voucher_type = 100 then amount else 0 end) as balance from transactions";
                retCursor = mOpenHelper.getReadableDatabase().rawQuery(
                        strQuery, null, null);
//                        new String[]{String.valueOf(SurakshaContract.TxnEntry.REGISTRATION_FEE_LEDGER),
//                                String.valueOf(SurakshaContract.TxnEntry.WORKING_COST_LEDGER)});
                break;
            }
            case TXN_FETCH_DEPOSITS: {
                selection = selection != null ? selection : SurakshaContract.TxnEntry.COLUMN_LEDGER + " = ? AND " + SurakshaContract.TxnEntry.COLUMN_VOUCHER_TYPE + " = ?";
                selectionArgs = selectionArgs != null ? selectionArgs : new String[]{String.valueOf(SurakshaContract.TxnEntry.DEPOSIT_LEDGER), String.valueOf(SurakshaContract.TxnEntry.RECEIPT_VOUCHER)};
                retCursor = mOpenHelper.getReadableDatabase().query(SurakshaContract.TxnEntry.TABLE_NAME,
                        projection, selection, selectionArgs,
                        null, null, SurakshaContract.TxnEntry.COLUMN_DEPOSIT_FOR_DATE + " DESC");
                break;
            }
            case TXN_GET_TOTAL_DEPOSIT: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        SurakshaContract.TxnEntry.TABLE_NAME,
                        new String[]{"sum(" + SurakshaContract.TxnEntry.COLUMN_AMOUNT + ")"},
                        SurakshaContract.TxnEntry.COLUMN_LEDGER + " = ?",
                        new String[]{String.valueOf(SurakshaContract.TxnEntry.DEPOSIT_LEDGER)},
                        null,
                        null,
                        null
                );
                break;
            }
            case TXN_TOTAL_LOAN_PAYED: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        SurakshaContract.TxnEntry.TABLE_NAME,
                        new String[]{"sum(" + SurakshaContract.TxnEntry.COLUMN_AMOUNT + ")"},
                        SurakshaContract.TxnEntry.COLUMN_LEDGER + " = ? AND " + SurakshaContract.TxnEntry.COLUMN_VOUCHER_TYPE + " = ?",
                        new String[]{String.valueOf(SurakshaContract.TxnEntry.LOAN_ISSUED_LEDGER), String.valueOf(SurakshaContract.TxnEntry.PAYMENT_VOUCHER)},
                        null,
                        null,
                        null
                );
                break;
            }
            case TXN_TOTAL_LOAN_RETURN: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        SurakshaContract.TxnEntry.TABLE_NAME,
                        new String[]{"sum(" + SurakshaContract.TxnEntry.COLUMN_AMOUNT + ")"},
                        SurakshaContract.TxnEntry.COLUMN_LEDGER + " = ? AND " + SurakshaContract.TxnEntry.COLUMN_VOUCHER_TYPE + " = ?",
                        new String[]{String.valueOf(SurakshaContract.TxnEntry.LOAN_RETURN_LEDGER), String.valueOf(SurakshaContract.TxnEntry.RECEIPT_VOUCHER)},
                        null,
                        null,
                        null
                );
                break;
            }
            case LOAN_ISSUE: {
                retCursor = sLoanIssueJoinTxnQueryBuilder.query(
                        mOpenHelper.getReadableDatabase(),
                        projection,
                        selection, selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case LOAN_ISSUE_ONLY: {
                SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
                queryBuilder.setTables(SurakshaContract.LoanIssueEntry.TABLE_NAME);
                retCursor = queryBuilder.query(
                        mOpenHelper.getReadableDatabase(),
                        projection,
                        selection, selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case LOAN_ISSUE_ID: {
                String id = SurakshaContract.LoanIssueEntry.getLoanIssueId(uri);
                String idSelection = SurakshaContract.LoanIssueEntry.TABLE_NAME +
                        "." + SurakshaContract.LoanIssueEntry._ID + " = ? ";
                retCursor = sLoanIssueJoinTxnQueryBuilder.query(
                        mOpenHelper.getReadableDatabase(),
                        projection,
                        idSelection, new String[]{id},
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            default:
                throw new UnsupportedOperationException(("Unknown uri:" + uri));
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MEMBER:
                return SurakshaContract.MemberEntry.CONTENT_TYPE;
            // Student: Uncomment and fill out these two cases
            case ACCOUNTS_OF_MEMBER:
                return SurakshaContract.MemberEntry.CONTENT_TYPE;
            case ACCOUNT:
                return SurakshaContract.AccountEntry.CONTENT_TYPE;
            case OFFICER:
                return SurakshaContract.OfficerEntry.CONTENT_TYPE;
            case TXN:
                return SurakshaContract.TxnEntry.CONTENT_TYPE;
            case TXN_ON_DATE:
                return SurakshaContract.TxnEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    private void normalizeMemberDate(ContentValues values) {
        //Member table
        // normalize the date value
//        if (values.containsKey(SurakshaContract.MemberEntry.COLUMN_CLOSED_AT)) {
//            long dateValue = values.getAsLong(SurakshaContract.MemberEntry.COLUMN_CLOSED_AT);
//            values.put(SurakshaContract.MemberEntry.COLUMN_CLOSED_AT, CalendarUtils.normalizeDate(dateValue));
//        }
//
//        if (values.containsKey(SurakshaContract.MemberEntry.COLUMN_CREATED_AT)) {
//            long dateValue = values.getAsLong(SurakshaContract.MemberEntry.COLUMN_CREATED_AT);
//            values.put(SurakshaContract.MemberEntry.COLUMN_CREATED_AT, CalendarUtils.normalizeDate(dateValue));
//        }
//        if (values.containsKey(SurakshaContract.MemberEntry.COLUMN_UPDATED_AT)) {
//            long dateValue = values.getAsLong(SurakshaContract.MemberEntry.COLUMN_UPDATED_AT);
//            values.put(SurakshaContract.MemberEntry.COLUMN_UPDATED_AT, CalendarUtils.normalizeDate(dateValue));
//        }
    }

    private void normalizeOfficerDate(ContentValues values) {
        //Officer table
        // normalize the date value
//        if (values.containsKey(SurakshaContract.OfficerEntry.COLUMN_CREATED_AT)) {
//            long dateValue = values.getAsLong(SurakshaContract.OfficerEntry.COLUMN_CREATED_AT);
//            values.put(SurakshaContract.OfficerEntry.COLUMN_CREATED_AT, CalendarUtils.normalizeDate(dateValue));
//        }
//        if (values.containsKey(SurakshaContract.OfficerEntry.COLUMN_UPDATED_AT)) {
//            long dateValue = values.getAsLong(SurakshaContract.OfficerEntry.COLUMN_UPDATED_AT);
//            values.put(SurakshaContract.OfficerEntry.COLUMN_UPDATED_AT, CalendarUtils.normalizeDate(dateValue));
//        }
    }

    private void normalizeAccountDate(ContentValues values) {
//        // normalize the date value
//        if (values.containsKey(SurakshaContract.AccountEntry.COLUMN_CLOSED_AT)) {
//            long dateValue = values.getAsLong(SurakshaContract.AccountEntry.COLUMN_CLOSED_AT);
//            values.put(SurakshaContract.AccountEntry.COLUMN_CLOSED_AT, CalendarUtils.normalizeDate(dateValue));
//        }
//
//        if (values.containsKey(SurakshaContract.AccountEntry.COLUMN_CREATED_AT)) {
//            long dateValue = values.getAsLong(SurakshaContract.AccountEntry.COLUMN_CREATED_AT);
//            values.put(SurakshaContract.AccountEntry.COLUMN_CREATED_AT, CalendarUtils.normalizeDate(dateValue));
//        }
//        if (values.containsKey(SurakshaContract.AccountEntry.COLUMN_UPDATED_AT)) {
//            long dateValue = values.getAsLong(SurakshaContract.AccountEntry.COLUMN_UPDATED_AT);
//            values.put(SurakshaContract.AccountEntry.COLUMN_UPDATED_AT, CalendarUtils.normalizeDate(dateValue));
//        }
    }

    private void normalizeTxnDate(ContentValues values) {
        // normalize the date value
        if (values.containsKey(SurakshaContract.TxnEntry.COLUMN_DEPOSIT_FOR_DATE)) {
            if (values.getAsLong(SurakshaContract.TxnEntry.COLUMN_DEPOSIT_FOR_DATE) > 0) {
                long dateValue = values.getAsLong(SurakshaContract.TxnEntry.COLUMN_DEPOSIT_FOR_DATE);
                values.put(SurakshaContract.TxnEntry.COLUMN_DEPOSIT_FOR_DATE, CalendarUtils.normalizeDate(dateValue));
            }
        }

        if (values.containsKey(SurakshaContract.TxnEntry.COLUMN_PAYMENT_DATE)) {
            if (values.getAsLong(SurakshaContract.TxnEntry.COLUMN_PAYMENT_DATE) > 0) {
                long dateValue = values.getAsLong(SurakshaContract.TxnEntry.COLUMN_PAYMENT_DATE);
                values.put(SurakshaContract.TxnEntry.COLUMN_PAYMENT_DATE, CalendarUtils.normalizeDate(dateValue));
            }
        }

        // normalize the date value
        if (values.containsKey(SurakshaContract.TxnEntry.COLUMN_CREATED_AT)) {
            long dateValue = values.getAsLong(SurakshaContract.TxnEntry.COLUMN_CREATED_AT);
            values.put(SurakshaContract.TxnEntry.COLUMN_CREATED_AT, CalendarUtils.normalizeDate(dateValue));
        }
        if (values.containsKey(SurakshaContract.TxnEntry.COLUMN_UPDATED_AT)) {
            long dateValue = values.getAsLong(SurakshaContract.TxnEntry.COLUMN_UPDATED_AT);
            values.put(SurakshaContract.TxnEntry.COLUMN_UPDATED_AT, CalendarUtils.normalizeDate(dateValue));
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case MEMBER: {
//                normalizeMemberDate(values);
                long _id = db.insert(SurakshaContract.MemberEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    returnUri = SurakshaContract.MemberEntry.buildMemberUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            case ACCOUNT: {
                long _id = db.insert(SurakshaContract.AccountEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    returnUri = SurakshaContract.AccountEntry.buildAccountUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            case OFFICER: {
                long _id = db.insert(SurakshaContract.OfficerEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    returnUri = SurakshaContract.OfficerEntry.buildOfficerUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            case TXN: {
                long _id = db.insert(SurakshaContract.TxnEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    returnUri = SurakshaContract.TxnEntry.buildTxnUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            case LOAN_ISSUE: {
                long _id = db.insert(SurakshaContract.LoanIssueEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    returnUri = SurakshaContract.LoanIssueEntry.buildLoanIssueUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        db.close();
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;

        // this makes delete all rows return the number of rows deleted
        if (null == selection) selection = "1";
        switch (match) {
            case MEMBER:
                rowsDeleted = db.delete(
                        SurakshaContract.MemberEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case ACCOUNT:
                rowsDeleted = db.delete(
                        SurakshaContract.AccountEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case OFFICER:
                rowsDeleted = db.delete(
                        SurakshaContract.OfficerEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case TXN:
                rowsDeleted = db.delete(
                        SurakshaContract.TxnEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case LOAN_ISSUE:
                rowsDeleted = db.delete(
                        SurakshaContract.LoanIssueEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case MEMBER:
//                normalizeMemberDate(values);
                rowsUpdated = db.update(
                        SurakshaContract.MemberEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case ACCOUNT:
//                normalizeAccountDate(values);
                rowsUpdated = db.update(
                        SurakshaContract.AccountEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case OFFICER:
//                normalizeOfficerDate(values);
                rowsUpdated = db.update(
                        SurakshaContract.OfficerEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case TXN:
//                normalizeTxnDate(values);
                rowsUpdated = db.update(
                        SurakshaContract.TxnEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case LOAN_ISSUE:
//                normalizeTxnDate(values);
                rowsUpdated = db.update(
                        SurakshaContract.LoanIssueEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int returnCount = 0;
        switch (match) {
            case MEMBER:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        normalizeMemberDate(value);
                        long _id = db.insert(SurakshaContract.MemberEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            case ACCOUNT:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(SurakshaContract.AccountEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            case TXN:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        normalizeTxnDate(value);
                        long _id = db.insert(SurakshaContract.TxnEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }

    }

//
//    public ContentProviderResult[] applyBatch(
//            ArrayList<ContentProviderOperation> operations)
//            throws OperationApplicationException {
//        System.out.println("starting transaction");
//        ContentProviderResult[] result;
//        try {
//            result = super.applyBatch(operations);
//        } catch (OperationApplicationException e) {
//            System.out.println("aborting transaction");
//            throw e;
//        }
//        System.out.println("ending transaction");
//        return result;
//    }


    /**
     * Apply the given set of {@link ContentProviderOperation}, executing inside
     * a {@link SQLiteDatabase} transaction. All changes will be rolled back if
     * any single one fails.
     */
/*    @Override
    public ContentProviderResult[] applyBatch(ArrayList<ContentProviderOperation> operations)
            throws OperationApplicationException {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            final int numOperations = operations.size();
            final ContentProviderResult[] results = new ContentProviderResult[numOperations];
            for (int i = 0; i < numOperations; i++) {
                results[i] = operations.get(i).apply(this, results, i);
            }
            db.setTransactionSuccessful();
            return results;
        } finally {
            db.endTransaction();
        }
    }

    */
}
