package com.razikallayi.suraksha.account;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.razikallayi.suraksha.data.SurakshaContract;
import com.razikallayi.suraksha.member.Member;
import com.razikallayi.suraksha.txn.Transaction;
import com.razikallayi.suraksha.utils.Utility;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Razi Kallayi on 29-01-2016.
 */
public class Account implements Serializable {
    private long id;
    private int accountNumber;
    private Member member;
    private double openingBalance;
    private double instalmentAmount = 1000;
    private boolean isActive;
    private String closedAt;
    private String createdAt;
    private String updatedAt;

    public interface AccountQuery {
        String[] PROJECTION = {
                SurakshaContract.AccountEntry.COLUMN_ACCOUNT_NUMBER   ,
                SurakshaContract.AccountEntry.COLUMN_MEMBER_ID        ,
                SurakshaContract.AccountEntry.COLUMN_OPENING_BALANCE  ,
                SurakshaContract.AccountEntry.COLUMN_INSTALMENT_AMOUNT,
                SurakshaContract.AccountEntry.COLUMN_IS_ACTIVE        ,
                SurakshaContract.AccountEntry.COLUMN_CLOSED_AT        ,
                SurakshaContract.AccountEntry.COLUMN_CREATED_AT       ,
                SurakshaContract.AccountEntry.COLUMN_UPDATED_AT       ,
                SurakshaContract.AccountEntry._ID   ,
        };
        int COL_ACCOUNT_NUMBER      =0;
        int COL_MEMBER_ID           =1;
        int COL_OPENING_BALANCE     =2;
        int COL_INSTALMENT_AMOUNT   =3;
        int COL_IS_ACTIVE           =4;
        int COL_CLOSED_AT           =5;
        int COL_CREATED_AT          =6;
        int COL_UPDATED_AT          =7;
        int COL_ID                  =8;
    }


    public Account() {
    }

    public Account(Member member, double openingBalance, boolean isActive) {
        this.member = member;
        this.openingBalance = openingBalance;
        this.isActive = isActive;
    }

    public static Account getAccountFromAccountNumber(Context context, int accountNumber) {
        Cursor cursor = context.getContentResolver().query(
                SurakshaContract.AccountEntry.buildAccountUriUsingAccountNumber
                        (accountNumber),
                AccountQuery.PROJECTION, SurakshaContract.AccountEntry.COLUMN_ACCOUNT_NUMBER +" = ? ",
                new String[]{String.valueOf(accountNumber)}, null);
        Account account = new Account();
        if (cursor != null) {
            cursor.moveToFirst();
            account.id = cursor.getInt(AccountQuery.COL_ID);
            account.accountNumber = cursor.getInt(AccountQuery.COL_ACCOUNT_NUMBER);
            account.member = Member.getMemberFromId(context, cursor.getLong(AccountQuery.COL_MEMBER_ID));
            account.openingBalance = cursor.getDouble(AccountQuery.COL_OPENING_BALANCE);
            account.instalmentAmount = cursor.getDouble(AccountQuery.COL_INSTALMENT_AMOUNT);
            account.isActive = cursor.getInt(AccountQuery.COL_IS_ACTIVE) == 1 ? true : false;
            account.closedAt = cursor.getString(AccountQuery.COL_CLOSED_AT);
            account.createdAt = cursor.getString(AccountQuery.COL_CREATED_AT);
            account.updatedAt = cursor.getString(AccountQuery.COL_UPDATED_AT);
        }
        cursor.close();
        return account;
        }

    /**
     * Return the next account number to be inserted to database.
     *
     * @param context
     * @return int
     */
    public static int generateAccountNumber(Context context) {
        Cursor cursor = context.getContentResolver().query(SurakshaContract.AccountEntry.CONTENT_URI,
                new String[]{"Max(" + SurakshaContract.AccountEntry.COLUMN_ACCOUNT_NUMBER + ")"}, null,
                null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int accountNumber = cursor.getInt(0) + 1;
            cursor.close();
            return accountNumber;
        }
        return 0;
    }

    /**
     * return contentValues from account object
     *
     * @param account
     * @return ContentValues
     */
    public static ContentValues getAccountContentValues(Account account) {
        ContentValues values = new ContentValues();

        values.put(SurakshaContract.AccountEntry.COLUMN_ACCOUNT_NUMBER, account.getAccountNumber());
        values.put(SurakshaContract.AccountEntry.COLUMN_MEMBER_ID, account.getMember().getId());
        values.put(SurakshaContract.AccountEntry.COLUMN_INSTALMENT_AMOUNT, account.getInstalmentAmount());
        values.put(SurakshaContract.AccountEntry.COLUMN_OPENING_BALANCE, account.getOpeningBalance());
        values.put(SurakshaContract.AccountEntry.COLUMN_IS_ACTIVE, account.isActive()?1:0);
        values.put(SurakshaContract.AccountEntry.COLUMN_CREATED_AT, System.currentTimeMillis());

        return values;
    }
    /**
     * return contentValues from account object
     *
     * @param context
     *
     * @param accountNumber
     *
     * @param date
     * 1st date of month, Month and Year should be set
     *
     * @param remarks
     *
     * @return Uri
     */
    public static Uri makeDeposit(Context context, int accountNumber, long date, String remarks) {
        Transaction txnMonthlyDeposit = new Transaction(accountNumber, Utility.getMonthlyDepositAmount(),
                SurakshaContract.TxnEntry.RECEIPT_VOUCHER, SurakshaContract.TxnEntry.DEPOSIT_LEDGER, remarks);
        txnMonthlyDeposit.setDefinedDepositDate(date);
        ContentValues values = Transaction.getTxnContentValues(txnMonthlyDeposit);
        return context.getContentResolver().insert(SurakshaContract.TxnEntry.CONTENT_URI, values);
    }

    public List<Transaction> fetchDeposits(Context context) {
        Cursor cursor = context.getContentResolver().query(
                SurakshaContract.TxnEntry.buildFetchAllDepositsUri(),
                Transaction.TxnQuery.PROJECTION,
                SurakshaContract.TxnEntry.COLUMN_LEDGER +"= ? AND "+ SurakshaContract.TxnEntry.COLUMN_FK_ACCOUNT_NUMBER +"= ?",
                new String[]{String.valueOf(SurakshaContract.TxnEntry.DEPOSIT_LEDGER), String.valueOf(accountNumber)},
                SurakshaContract.TxnEntry.COLUMN_CREATED_AT +" DESC");
        return Transaction.getTxnFromCursor(cursor);
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(int accountNumber) {
        this.accountNumber = accountNumber;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public double getOpeningBalance() {
        return openingBalance;
    }

    public void setOpeningBalance(double openingBalance) {
        this.openingBalance = openingBalance;
    }

    public double getInstalmentAmount() {
        return instalmentAmount;
    }

    public void setInstalmentAmount(double instalmentAmount) {
        this.instalmentAmount = instalmentAmount;
    }

    public String getClosedAt() {
        return closedAt;
    }

    public void setClosedAt(String closedAt) {
        this.closedAt = closedAt;
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

    @Override
    public String toString() {
        return "Account{" +
                "account_number='" + getAccountNumber() + '\'' +
                ", member='" + getMember().toString() + '\'' +
                ", opening_balance='" + getOpeningBalance() + '\'' +
                ", instalment_amount='" + getInstalmentAmount() + '\'' +
                ", closedAt=" + getClosedAt() +
                ", createdAt=" + getCreatedAt() +
                ", updatedAt=" + getUpdatedAt() +
                '}';
    }
}
