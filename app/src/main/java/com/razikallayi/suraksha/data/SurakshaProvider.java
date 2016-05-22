package com.razikallayi.suraksha.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import com.razikallayi.suraksha.utils.CalendarUtils;


public class SurakshaProvider extends ContentProvider {
    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private SurakshaDbHelper mOpenHelper;

    static final int MEMBER  = 100;
    static final int MEMBER_ID  = 101;
    static final int MEMBER_JOIN_ACCOUNT  = 110;

    static final int ACCOUNT = 200;
    static final int ACCOUNT_NUMBER = 202;
    static final int ACCOUNTS_OF_MEMBER = 210;

    static final int TXN     = 300;
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

    private static final SQLiteQueryBuilder sAccountsOfMemberQueryBuilder;
    static{
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

    private static final SQLiteQueryBuilder sTxnAccountQueryBuilder;
    static{
        sTxnAccountQueryBuilder = new SQLiteQueryBuilder();

        //This is an inner join which looks like
        //member INNER JOIN account ON account.member_id = Member._id
//        sTxnAccountQueryBuilder.setTables(
//                SurakshaContract.TxnEntry.TABLE_NAME + " INNER JOIN " +
//                        SurakshaContract.AccountEntry.TABLE_NAME +
//                        " ON " + SurakshaContract.AccountEntry.TABLE_NAME +
//                        "." + SurakshaContract.AccountEntry.COLUMN_ACCOUNT_NUMBER +
//                        " = " + SurakshaContract.TxnEntry.TABLE_NAME +
//                        "." + SurakshaContract.TxnEntry.COLUMN_FK_ACCOUNT_NUMBER);

        sTxnAccountQueryBuilder.setTables(SurakshaContract.TxnEntry.TABLE_NAME);
    }

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

        matcher.addURI(authority, SurakshaContract.PATH_ACCOUNT, ACCOUNT);
        matcher.addURI(authority, SurakshaContract.PATH_ACCOUNT_NUMBER + "/#", ACCOUNT_NUMBER);
        matcher.addURI(authority, SurakshaContract.PATH_ACCOUNT_OF_MEMBER+"/*", ACCOUNTS_OF_MEMBER);

        matcher.addURI(authority, SurakshaContract.PATH_TXN, TXN);
        matcher.addURI(authority, SurakshaContract.PATH_TXN_ON_DATE + "/*", TXN_ON_DATE);
        matcher.addURI(authority, SurakshaContract.PATH_TXN_OF_ACCOUNT + "/*", TXN_OF_ACCOUNT);
        matcher.addURI(authority, SurakshaContract.PATH_TXN_GET_WMF, TXN_GET_WMF);
        matcher.addURI(authority, SurakshaContract.PATH_TXN_FETCH_DEPOSITS, TXN_FETCH_DEPOSITS);
        matcher.addURI(authority, SurakshaContract.PATH_TXN_GET_TOTAL_DEPOSIT, TXN_GET_TOTAL_DEPOSIT);
        matcher.addURI(authority, SurakshaContract.PATH_TXN_GET_DEPOSIT_OF_ACCOUNT + "/*", TXN_GET_DEPOSIT_OF_ACCOUNT);
        matcher.addURI(authority, SurakshaContract.PATH_TXN_GET_DEPOSIT_ON_DATE + "/*", TXN_GET_DEPOSIT_ON_DATE);
        matcher.addURI(authority, SurakshaContract.PATH_TXN_TOTAL_LOAN_PAYED, TXN_TOTAL_LOAN_PAYED);
        matcher.addURI(authority, SurakshaContract.PATH_TXN_TOTAL_LOAN_RETURN, TXN_TOTAL_LOAN_RETURN);
        return matcher;
    }


    //location.location_setting = ?
    private static final String sMemberIdSelection =
            SurakshaContract.MemberEntry.TABLE_NAME+
                    "." + SurakshaContract.MemberEntry._ID + " = ? ";


    //location.location_setting = ?
    private static final String sAccountNumberSelection =
            SurakshaContract.AccountEntry.TABLE_NAME+
                    "." + SurakshaContract.AccountEntry.COLUMN_ACCOUNT_NUMBER + " = ? ";

    //location.location_setting = ? AND date >= ?
    private static final String sMemberNameOrAccountSelection =
            SurakshaContract.MemberEntry.TABLE_NAME+
                    "." + SurakshaContract.MemberEntry.COLUMN_NAME + " = ? OR " +
                    SurakshaContract.AccountEntry.COLUMN_ACCOUNT_NUMBER + " = ? ";


    private Cursor getAccountsOfMember(Uri uri, String[] projection, String sortOrder) {
        String memberId = SurakshaContract.AccountEntry.getMemberIdFromUri(uri);
        return sAccountsOfMemberQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sMemberIdSelection,
                new String[]{memberId},
                null,
                null,
                sortOrder
        );
    }
    private Cursor getTxnsOnDate(Uri uri, String[] projection,String selection,String[] selectionArgs, String sortOrder) {
        final long date = SurakshaContract.TxnEntry.getDateFromUri(uri);

        //transactions.created_at = ?
        final String sTxnOnDateSelection =
                SurakshaContract.TxnEntry.TABLE_NAME+
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

private Cursor getTxnOfAccount(Uri uri, String[] projection,String selection,
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
Log.d("DB", "Query in Provider: "+uri.toString());
Log.d("DB", "Matcher Provider: "+sUriMatcher.match(uri));
        switch (sUriMatcher.match(uri)) {
            case MEMBER:{
                retCursor = mOpenHelper.getReadableDatabase().query(
                        SurakshaContract.MemberEntry.TABLE_NAME,
                        projection,
                        selection,selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            case MEMBER_ID:{
                String id = SurakshaContract.MemberEntry.getMemberId(uri);
                retCursor = mOpenHelper.getReadableDatabase().query(
                        SurakshaContract.MemberEntry.TABLE_NAME,
                        projection,
                        sMemberIdSelection,
                        new String[]{id},
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case MEMBER_JOIN_ACCOUNT:{
                retCursor = mOpenHelper.getReadableDatabase().query(
                        true,
                        sAccountsOfMemberQueryBuilder.getTables(),
                        projection,
                        selection,selectionArgs,
                        null,
                        null,
                        sortOrder,
                        null
                );
                break;
            }
            case ACCOUNTS_OF_MEMBER:{
                retCursor = getAccountsOfMember(uri, projection, sortOrder);
                break;
            }
            case ACCOUNT:{
                retCursor = mOpenHelper.getReadableDatabase().query(
                        SurakshaContract.AccountEntry.TABLE_NAME,
                        projection,
                        selection,selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case ACCOUNT_NUMBER:{
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
            case OFFICER:{
                retCursor = mOpenHelper.getReadableDatabase().query(
                        SurakshaContract.OfficerEntry.TABLE_NAME,
                        projection,
                        selection,selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case OFFICER_ID:{
                String id = SurakshaContract.OfficerEntry.getOfficerId(uri);
                String idSelection =SurakshaContract.OfficerEntry.TABLE_NAME+
                                "." + SurakshaContract.OfficerEntry._ID + " = ? ";
                retCursor = mOpenHelper.getReadableDatabase().query(
                        SurakshaContract.OfficerEntry.TABLE_NAME,
                        projection,
                        idSelection,
                        new String[]{id},
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case TXN:{
                retCursor = mOpenHelper.getReadableDatabase().query(
                        SurakshaContract.TxnEntry.TABLE_NAME,
                        projection,
                        selection,selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case TXN_ON_DATE:{
                retCursor = getTxnsOnDate(uri, projection, selection,selectionArgs,sortOrder);
                break;
            }
            case TXN_OF_ACCOUNT:{
                retCursor = getTxnOfAccount(uri, projection, selection, selectionArgs, sortOrder);
                break;
            }
            case TXN_GET_WMF:{
                //select (select sum (amount) from transactions where ledger = "REGISTRATION_FEE" )
                // - (select sum (amount) from transactions where ledger = "WORKING_COST") as balance
//                String strQuery = "select (select sum (" + SurakshaContract.TxnEntry.COLUMN_AMOUNT
//                        + ") from " + SurakshaContract.TxnEntry.TABLE_NAME + " where "
//                        + SurakshaContract.TxnEntry.COLUMN_LEDGER + " = ?)-(select sum("
//                        + SurakshaContract.TxnEntry.COLUMN_AMOUNT + ") from " + SurakshaContract.TxnEntry.TABLE_NAME
//                        + " where " + SurakshaContract.TxnEntry.COLUMN_LEDGER + " = ?) as balance";
//

                String strQuery = "select sum(case when ledger = 1 and voucher_type = 1 then amount else 0 end) - sum(case when ledger = 4 and voucher_type = 0 then amount else 0 end) as balance from transactions";
                retCursor = mOpenHelper.getReadableDatabase().rawQuery(
                        strQuery,null,null);
//                        new String[]{String.valueOf(SurakshaContract.TxnEntry.REGISTRATION_FEE_LEDGER),
//                                String.valueOf(SurakshaContract.TxnEntry.WORKING_COST_LEDGER)});
                break;
            }
            case TXN_FETCH_DEPOSITS:{
                selection = selection !=null?selection:SurakshaContract.TxnEntry.COLUMN_LEDGER + " = ? AND " + SurakshaContract.TxnEntry.COLUMN_VOUCHER_TYPE +" = ?";
                selectionArgs = selectionArgs != null?selectionArgs: new String[]{String.valueOf(SurakshaContract.TxnEntry.DEPOSIT_LEDGER),String.valueOf(SurakshaContract.TxnEntry.RECEIPT_VOUCHER)};
                retCursor = mOpenHelper.getReadableDatabase().query(SurakshaContract.TxnEntry.TABLE_NAME,
                        projection,selection,selectionArgs,
                        null,null, SurakshaContract.TxnEntry.COLUMN_DEFINED_DEPOSIT_DATE + " DESC");
                break;
            }
            case TXN_GET_TOTAL_DEPOSIT:{
                retCursor = mOpenHelper.getReadableDatabase().query(
                        SurakshaContract.TxnEntry.TABLE_NAME,
                        new String[]{"sum(" + SurakshaContract.TxnEntry.COLUMN_AMOUNT + ")"} ,
                        SurakshaContract.TxnEntry.COLUMN_LEDGER + " = ?",
                        new String[]{String.valueOf(SurakshaContract.TxnEntry.DEPOSIT_LEDGER)},
                        null,
                        null,
                        null
                        );
                break;
            }
            case TXN_TOTAL_LOAN_PAYED:{
                retCursor = mOpenHelper.getReadableDatabase().query(
                        SurakshaContract.TxnEntry.TABLE_NAME,
                        new String[]{"sum(" + SurakshaContract.TxnEntry.COLUMN_AMOUNT + ")"} ,
                        SurakshaContract.TxnEntry.COLUMN_LEDGER + " = ? AND "+SurakshaContract.TxnEntry.COLUMN_VOUCHER_TYPE +" = ?",
                        new String[]{String.valueOf(SurakshaContract.TxnEntry.LOAN_LEDGER), String.valueOf(SurakshaContract.TxnEntry.PAYMENT_VOUCHER)},
                        null,
                        null,
                        null
                        );
                break;
            }
            case TXN_TOTAL_LOAN_RETURN:{
                retCursor = mOpenHelper.getReadableDatabase().query(
                        SurakshaContract.TxnEntry.TABLE_NAME,
                        new String[]{"sum(" + SurakshaContract.TxnEntry.COLUMN_AMOUNT + ")"} ,
                        SurakshaContract.TxnEntry.COLUMN_LEDGER + " = ? AND "+SurakshaContract.TxnEntry.COLUMN_VOUCHER_TYPE +" = ?",
                        new String[]{String.valueOf(SurakshaContract.TxnEntry.LOAN_LEDGER), String.valueOf(SurakshaContract.TxnEntry.RECEIPT_VOUCHER)},
                        null,
                        null,
                        null
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
        if (values.containsKey(SurakshaContract.MemberEntry.COLUMN_CLOSED_AT)) {
            long dateValue = values.getAsLong(SurakshaContract.MemberEntry.COLUMN_CLOSED_AT);
            values.put(SurakshaContract.MemberEntry.COLUMN_CLOSED_AT, CalendarUtils.normalizeDate(dateValue));
        }

        if (values.containsKey(SurakshaContract.MemberEntry.COLUMN_CREATED_AT)) {
            long dateValue = values.getAsLong(SurakshaContract.MemberEntry.COLUMN_CREATED_AT);
            values.put(SurakshaContract.MemberEntry.COLUMN_CREATED_AT, CalendarUtils.normalizeDate(dateValue));
        }
        if (values.containsKey(SurakshaContract.MemberEntry.COLUMN_UPDATED_AT)) {
            long dateValue = values.getAsLong(SurakshaContract.MemberEntry.COLUMN_UPDATED_AT);
            values.put(SurakshaContract.MemberEntry.COLUMN_UPDATED_AT, CalendarUtils.normalizeDate(dateValue));
        }
    }
    private void normalizeOfficerDate(ContentValues values) {
        //Officer table
        // normalize the date value
        if (values.containsKey(SurakshaContract.OfficerEntry.COLUMN_CREATED_AT)) {
            long dateValue = values.getAsLong(SurakshaContract.OfficerEntry.COLUMN_CREATED_AT);
            values.put(SurakshaContract.OfficerEntry.COLUMN_CREATED_AT, CalendarUtils.normalizeDate(dateValue));
        }
        if (values.containsKey(SurakshaContract.OfficerEntry.COLUMN_UPDATED_AT)) {
            long dateValue = values.getAsLong(SurakshaContract.OfficerEntry.COLUMN_UPDATED_AT);
            values.put(SurakshaContract.OfficerEntry.COLUMN_UPDATED_AT, CalendarUtils.normalizeDate(dateValue));
        }
    }
    private void normalizeAccountDate(ContentValues values) {
        // normalize the date value
        if (values.containsKey(SurakshaContract.AccountEntry.COLUMN_CLOSED_AT)) {
            long dateValue = values.getAsLong(SurakshaContract.AccountEntry.COLUMN_CLOSED_AT);
            values.put(SurakshaContract.AccountEntry.COLUMN_CLOSED_AT, CalendarUtils.normalizeDate(dateValue));
        }

        if (values.containsKey(SurakshaContract.AccountEntry.COLUMN_CREATED_AT)) {
            long dateValue = values.getAsLong(SurakshaContract.AccountEntry.COLUMN_CREATED_AT);
            values.put(SurakshaContract.AccountEntry.COLUMN_CREATED_AT, CalendarUtils.normalizeDate(dateValue));
        }
        if (values.containsKey(SurakshaContract.AccountEntry.COLUMN_UPDATED_AT)) {
            long dateValue = values.getAsLong(SurakshaContract.AccountEntry.COLUMN_UPDATED_AT);
            values.put(SurakshaContract.AccountEntry.COLUMN_UPDATED_AT, CalendarUtils.normalizeDate(dateValue));
        }
    }

    private void normalizeTxnDate(ContentValues values) {
        // normalize the date value
        if (values.containsKey(SurakshaContract.TxnEntry.COLUMN_DEFINED_DEPOSIT_DATE)) {
            if(values.getAsLong(SurakshaContract.TxnEntry.COLUMN_DEFINED_DEPOSIT_DATE) > 0 ) {
                long dateValue = values.getAsLong(SurakshaContract.TxnEntry.COLUMN_DEFINED_DEPOSIT_DATE);
                values.put(SurakshaContract.TxnEntry.COLUMN_DEFINED_DEPOSIT_DATE, CalendarUtils.normalizeDate(dateValue));
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

        switch (match){
            case MEMBER: {
                normalizeMemberDate(values);
                long _id = db.insert(SurakshaContract.MemberEntry.TABLE_NAME, null, values);
                if( _id > 0){
                    returnUri = SurakshaContract.MemberEntry.buildMemberUri(_id);
                }
                else{
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            case ACCOUNT: {
                normalizeAccountDate(values);
                long _id = db.insert(SurakshaContract.AccountEntry.TABLE_NAME, null, values);
                if( _id > 0){
                    returnUri = SurakshaContract.AccountEntry.buildAccountUri(_id);
                }
                else{
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            case OFFICER: {
                normalizeOfficerDate(values);
                long _id = db.insert(SurakshaContract.OfficerEntry.TABLE_NAME, null, values);
                if( _id > 0){
                    returnUri = SurakshaContract.OfficerEntry.buildOfficerUri(_id);
                }
                else{
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            case TXN: {
                normalizeTxnDate(values);
                long _id = db.insert(SurakshaContract.TxnEntry.TABLE_NAME, null, values);
                if( _id > 0){
                    returnUri = SurakshaContract.TxnEntry.buildTxnUri(_id);
                }
                else{
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
        if ( null == selection )selection ="1";
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
            default:
                throw new UnsupportedOperationException("Unknown uri: "+ uri);
        }
        if(rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case MEMBER:
                normalizeMemberDate(values);
                rowsUpdated = db.update(
                        SurakshaContract.MemberEntry.TABLE_NAME, values, selection,selectionArgs);
                break;
            case ACCOUNT:
                normalizeAccountDate(values);
                rowsUpdated = db.update(
                        SurakshaContract.AccountEntry.TABLE_NAME, values, selection,selectionArgs);
                break;
            case OFFICER:
                normalizeOfficerDate(values);
                rowsUpdated = db.update(
                        SurakshaContract.OfficerEntry.TABLE_NAME, values, selection,selectionArgs);
                break;
            case TXN:
                normalizeTxnDate(values);
                rowsUpdated = db.update(
                        SurakshaContract.TxnEntry.TABLE_NAME, values, selection,selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: "+ uri);
        }
        if(rowsUpdated != 0) {
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
                        if(_id != -1){
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri,null);
                return returnCount;
            case ACCOUNT:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(SurakshaContract.AccountEntry.TABLE_NAME, null, value);
                        if(_id != -1){
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri,null);
                return returnCount;
            case TXN:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        normalizeTxnDate(value);
                        long _id = db.insert(SurakshaContract.TxnEntry.TABLE_NAME, null, value);
                        if(_id != -1){
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri,null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }

}
