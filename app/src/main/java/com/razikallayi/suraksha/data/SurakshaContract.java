package com.razikallayi.suraksha.data;


import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

import com.razikallayi.suraksha.utils.CalendarUtils;

public class SurakshaContract {

    // The "Content authority" is a name for the entire content provider, similar to the
    // relationship between a domain name and its website.  A convenient string to use for the
    // content authority is the package name for the app, which is guaranteed to be unique on the
    // device.
    public static final String CONTENT_AUTHORITY = "com.razikallayi.suraksha";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Possible paths (appended to base content URI for possible URI's)
    public static final String PATH_MEMBER = "member";
    public static final String PATH_MEMBER_JOIN_ACCOUNT = "member_join_account";

    // Possible paths (appended to base content URI for possible URI's)
    public static final String PATH_ACCOUNT = "account";
    public static final String PATH_ACCOUNT_NUMBER = "account_number";
    public static final String PATH_ACCOUNT_OF_MEMBER = "account-of-member";

    // Possible paths (appended to base content URI for possible URI's)
    public static final String PATH_TXN = "txn";
    public static final String PATH_TXN_ON_DATE = "txn-on-date";
    public static final String PATH_TXN_OF_ACCOUNT = "txn-of-account";
    public static final String PATH_TXN_GET_WMF = "txn-get-wmf";
    public static final String PATH_TXN_FETCH_DEPOSITS = "txn-fetch-deposits";
    public static final String PATH_TXN_GET_TOTAL_DEPOSIT = "txn-total-deposit";
    public static final String PATH_TXN_GET_DEPOSIT_OF_ACCOUNT = "txn-deposit-of-account";
    public static final String PATH_TXN_GET_DEPOSIT_ON_DATE = "txn-deposit-on-date";
    public static final String PATH_TXN_TOTAL_LOAN_PAYED = "txn-total-loan-payed";
    public static final String PATH_TXN_TOTAL_LOAN_RETURN = "txn-total-loan-return";

    // Possible paths (appended to base content URI for possible URI's)
    public static final String PATH_OFFICER = "officer";
    public static final String PATH_OFFICER_EXIST = "officer_exist";

    public static final String PATH_LOAN_ISSUE = "loan_issue";


    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public SurakshaContract() {
    }


    /* Inner class that defines the table contents */
    //Member Entry
    public static class MemberEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MEMBER).build();
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MEMBER;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MEMBER;

        public static final String TABLE_NAME = "members";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_ACCOUNT_NO = "account_no";
        public static final String COLUMN_MOBILE = "mobile";
        public static final String COLUMN_ADDRESS = "address";
        public static final String COLUMN_ALIAS = "alias";
        public static final String COLUMN_FATHER = "father";
        public static final String COLUMN_SPOUSE = "spouse";
        public static final String COLUMN_GENDER = "gender";
        public static final String COLUMN_OCCUPATION = "occupation";
        public static final String COLUMN_AVATAR = "avatar";
        public static final String COLUMN_AGE = "age";
        public static final String COLUMN_NOMINEE = "nominee";
        public static final String COLUMN_RELATION_WITH_NOMINEE = "relation_with_nominee";
        public static final String COLUMN_ADDRESS_OF_NOMINEE = "address_of_nominee";
        public static final String COLUMN_REMARKS = "remarks";
        public static final String COLUMN_HAS_LOAN = "has_loan";
        public static final String COLUMN_IS_LOAN_BLOCKED = "is_loan_blocked";
        public static final String COLUMN_IS_DELETED = "is_deleted";
        public static final String COLUMN_CLOSED_AT = "closed_at";
        public static final String COLUMN_CREATED_AT = "created_at";
        public static final String COLUMN_UPDATED_AT = "updated_at";

        public static Uri buildMemberUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }


        public static String getMemberId(Uri uri) {
            return uri.getPathSegments().get(1);
        }


    }


    /* Inner class that defines the table contents */
    //Account Entry
    public static class AccountEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_ACCOUNT).build();
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ACCOUNT;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ACCOUNT;

        public static final String TABLE_NAME = "accounts";
        public static final String COLUMN_ACCOUNT_NUMBER = "account_number";
        public static final String COLUMN_MEMBER_ID = "member_id";
        public static final String COLUMN_OPENING_BALANCE = "opening_balance";
        public static final String COLUMN_INSTALMENT_AMOUNT = "instalment_amount";
        public static final String COLUMN_IS_ACTIVE = "is_active";
        public static final String COLUMN_CLOSED_AT = "closed_at";
        public static final String COLUMN_CREATED_AT = "created_at";
        public static final String COLUMN_UPDATED_AT = "updated_at";

        public static Uri buildAccountUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildAccountUriUsingAccountNumber(int account_number) {
            return BASE_CONTENT_URI.buildUpon().appendPath(PATH_ACCOUNT_NUMBER).appendPath(Long.toString(account_number)).build();
        }

        public static String getAccountNumber(Uri uri) {
            return uri.getPathSegments().get(1);
        }

        public static String getMemberIdFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }


        public static Uri buildAccountsOfMemberUri(long member_id) {
            return BASE_CONTENT_URI.buildUpon().appendPath(PATH_ACCOUNT_OF_MEMBER).appendPath(Long.toString(member_id)).build();
        }


    }

    //Transaction Entry
    public static class TxnEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_TXN).build();
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TXN;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TXN;

        public static final String TABLE_NAME = "transactions";
        public static final String COLUMN_FK_ACCOUNT_NUMBER = "fk_account_number";
        public static final String COLUMN_AMOUNT = "amount";
        public static final String COLUMN_DEPOSIT_FOR_DATE = "deposit_for_date";
        public static final String COLUMN_FK_LOAN_PAYED_ID = "loan_payed_id";
        public static final String COLUMN_LOAN_RETURN_DATE = "loan_return_date";
        public static final String COLUMN_VOUCHER_TYPE = "voucher_type";
        public static final String COLUMN_LEDGER = "ledger";
        public static final String COLUMN_FK_OFFICER_ID = "fk_officer_id";
        public static final String COLUMN_NARRATION = "narration";
        public static final String COLUMN_CREATED_AT = "created_at";
        public static final String COLUMN_UPDATED_AT = "updated_at";


        //Ledger
        //Change in Transaction, if you make any change here
        public static final int REGISTRATION_FEE_LEDGER = 1;
        public static final int DEPOSIT_LEDGER = 2;
        public static final int LOAN_ISSUED_LEDGER = 3;
        public static final int LOAN_RETURN_LEDGER = 4;
        public static final int WORKING_COST_LEDGER = 5;

        //Voucher_type
        //Change in Transaction, if you make any change here
        public static final int PAYMENT_VOUCHER = 100;
        public static final int RECEIPT_VOUCHER = 101;

        public static Uri buildTxnUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildTxnOfAccountUri(int account_number) {
            return BASE_CONTENT_URI.buildUpon().appendPath(PATH_TXN_OF_ACCOUNT)
                    .appendPath(String.valueOf(account_number)).build();
        }


        public static Uri buildGetWmfUri() {
            return BASE_CONTENT_URI.buildUpon().appendPath(PATH_TXN_GET_WMF).build();
        }

        public static Uri buildFetchAllDepositsUri() {
            return BASE_CONTENT_URI.buildUpon().appendPath(PATH_TXN_FETCH_DEPOSITS).build();
        }

        public static Uri buildGetTotalDeposit() {
            return BASE_CONTENT_URI.buildUpon().appendPath(PATH_TXN_GET_TOTAL_DEPOSIT).build();
        }

        public static Uri buildGetDepositOfAccount(int account_number) {
            return BASE_CONTENT_URI.buildUpon().appendPath(PATH_TXN_GET_DEPOSIT_OF_ACCOUNT)
                    .appendPath(String.valueOf(account_number)).build();
        }

        public static Uri buildGetDepositOnDate() {
            return BASE_CONTENT_URI.buildUpon().appendPath(PATH_TXN_GET_DEPOSIT_ON_DATE).build();
        }

        public static Uri buildTotalLoanPayed() {
            return BASE_CONTENT_URI.buildUpon().appendPath(PATH_TXN_TOTAL_LOAN_PAYED).build();
        }

        public static Uri buildTotalLoanReturn() {
            return BASE_CONTENT_URI.buildUpon().appendPath(PATH_TXN_TOTAL_LOAN_RETURN).build();
        }

//        public static Uri buildWeatherLocationWithStartDate(
//                String locationSetting, long startDate) {
//            long normalizedDate = normalizeDate(startDate);
//            return CONTENT_URI.buildUpon().appendPath(locationSetting)
//                    .appendQueryParameter(COLUMN_DATE, Long.toString(normalizedDate)).build();
//        }

//        public static Uri buildAccountTxnWithDate(String account_number, long date) {
//            return CONTENT_URI.buildUpon().appendPath(account_number)
//                    .appendPath(Long.toString(normalizeDate(date))).build();
//        }

        public static Uri buildTxnOnDate(long date) {
            return BASE_CONTENT_URI.buildUpon().appendPath(PATH_TXN_ON_DATE).appendPath(Long.toString(CalendarUtils.normalizeDate(date))).build();
        }

        public static long getDateFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(1));
        }

        public static String getAccountNumberFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

        public static String getTxnId(Uri uri) {
            return uri.getPathSegments().get(1);
        }

    }


    /* Inner class that defines the table contents */
    //LoanPayed Entry
    public static class LoanIssueEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_LOAN_ISSUE).build();
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_LOAN_ISSUE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_LOAN_ISSUE;

        public static final String TABLE_NAME = "loan_payed";
        public static final String COLUMN_FK_ACCOUNT_NUMBER = "account_number";
        public static final String COLUMN_SECURITY_ACCOUNT_NUMBER = "security_acno";
        public static final String COLUMN_PURPOSE = "purpose";
        public static final String COLUMN_AMOUNT = "amount";
        public static final String COLUMN_LOAN_INSTALMENT_TIMES = "loan_instalment_times";
        public static final String COLUMN_LOAN_INSTALMENT_AMOUNT = "loan_instalment_amount";
        public static final String COLUMN_OFFICE_STATEMENT = "office_statement";
        public static final String COLUMN_ISSUED_AT = "issued_at";
        public static final String COLUMN_CLOSED_AT = "closed_at";
        public static final String COLUMN_CREATED_AT = "created_at";
        public static final String COLUMN_UPDATED_AT = "updated_at";

        public static Uri buildLoanIssueUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static String getLoanIssueId(Uri uri) {
            return uri.getPathSegments().get(1);
        }

        public static long getLoanIssueIdFromUri(Uri uri) {
            return Long.valueOf(uri.getPathSegments().get(1));
        }
    }


    /* Inner class that defines the table contents */
    //Officer Entry
    public static class OfficerEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_OFFICER).build();
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_OFFICER;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_OFFICER;

        public static final String TABLE_NAME = "officers";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_MOBILE = "mobile";
        public static final String COLUMN_USERNAME = "username";
        public static final String COLUMN_PASSWORD = "password";
        public static final String COLUMN_ADDRESS = "address";
        public static final String COLUMN_IS_ADMIN = "isAdmin";
        public static final String COLUMN_CREATED_AT = "created_at";
        public static final String COLUMN_UPDATED_AT = "updated_at";

        public static Uri buildOfficerUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static String getOfficerId(Uri uri) {
            return uri.getPathSegments().get(1);
        }

        public static Uri buildCheckOfficerExistUri(String username) {
            return BASE_CONTENT_URI.buildUpon().appendPath(PATH_OFFICER_EXIST).appendPath(username).build();
        }

        public static String getUsername(Uri uri) {
            return uri.getPathSegments().get(1);
        }

    }


//    public static long normalizeDate(long startDate) {
//        // normalize the start date to the beginning of the (UTC) day
//        GregorianCalendar date = (GregorianCalendar)     GregorianCalendar.getInstance(TimeZone.getTimeZone("UTC"));
//        date.setTime(new Date(startDate));
//        date.set(Calendar.HOUR_OF_DAY, 0);
//        date.set(Calendar.MINUTE, 0);
//        date.set(Calendar.SECOND, 0);
//        date.set(Calendar.MILLISECOND, 0);
//        //transform your calendar to a long in the way you prefer
//        return date.getTimeInMillis();
//    }

}
