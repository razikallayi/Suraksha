package com.razikallayi.suraksha.loan;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.razikallayi.suraksha.data.SurakshaContract;
import com.razikallayi.suraksha.member.Member;
import com.razikallayi.suraksha.txn.Transaction;

/**
 * Created by Razi Kallayi on 19-06-2016 08:37.
 */
public class LoanIssue {

    private long id;
    private int accountNumber;
    private double amount;
    private int securityAccountNo;
    private String purpose;
    private int loanInstalmentTimes = 10;
    private double loanInstalmentAmount;
    private String officeStatement;
    private Member member;
    private Member securityMember;
    private long closedAt;
    private long createdAt;
    private long updatedAt;

    private Transaction transaction;

    public LoanIssue() {
    }

    public LoanIssue(int accountNumber, double amount, String purpose, int securityAccountNo, int loanInstalmentTimes, String officeStatement) {
        this.accountNumber = accountNumber;
        this.amount = amount;
        this.purpose = purpose;
        this.securityAccountNo = securityAccountNo;
        this.loanInstalmentTimes = loanInstalmentTimes;
        this.loanInstalmentAmount = amount / loanInstalmentTimes;
        this.officeStatement = officeStatement;
    }

    public static LoanIssue getActiveLoanForAccountNumber(Context context, int accountNumber) {
        Cursor cursor = context.getContentResolver().query(
                SurakshaContract.LoanIssueEntry.CONTENT_URI, LoanIssueQuery.PROJECTION,
                SurakshaContract.LoanIssueEntry.COLUMN_FK_ACCOUNT_NUMBER + " = ? and "
                        + SurakshaContract.LoanIssueEntry.COLUMN_CLOSED_AT + " = 0 ",
                new String[]{String.valueOf(accountNumber)},
                null);
        return getLoanIssueFromCursor(context, cursor);
    }

    public static ContentValues getLoanIssuedContentValues(LoanIssue loanIssue) {
        ContentValues values = new ContentValues();

        values.put(SurakshaContract.LoanIssueEntry.COLUMN_FK_ACCOUNT_NUMBER, loanIssue.accountNumber);
        values.put(SurakshaContract.LoanIssueEntry.COLUMN_AMOUNT, loanIssue.amount);
        values.put(SurakshaContract.LoanIssueEntry.COLUMN_LOAN_INSTALMENT_TIMES, loanIssue.loanInstalmentTimes);
        values.put(SurakshaContract.LoanIssueEntry.COLUMN_LOAN_INSTALMENT_AMOUNT, loanIssue.loanInstalmentAmount);
        values.put(SurakshaContract.LoanIssueEntry.COLUMN_PURPOSE, loanIssue.purpose);
        values.put(SurakshaContract.LoanIssueEntry.COLUMN_SECURITY_ACCOUNT_NUMBER, loanIssue.securityAccountNo);
        values.put(SurakshaContract.LoanIssueEntry.COLUMN_OFFICE_STATEMENT, loanIssue.officeStatement);
        values.put(SurakshaContract.LoanIssueEntry.COLUMN_CLOSED_AT, loanIssue.closedAt);
        values.put(SurakshaContract.LoanIssueEntry.COLUMN_CREATED_AT, loanIssue.createdAt);
        values.put(SurakshaContract.LoanIssueEntry.COLUMN_UPDATED_AT, loanIssue.updatedAt);

        return values;
    }

    public static LoanIssue getLoanIssueFromCursor(Context context, Cursor cursor) {
        LoanIssue loanIssue = new LoanIssue();
        if (cursor.getCount()>0) {
            cursor.moveToFirst();
            loanIssue = new LoanIssue(cursor.getInt(LoanIssueQuery.COL_FK_ACCOUNT_NUMBER),
                    cursor.getDouble(LoanIssueQuery.COL_AMOUNT),
                    cursor.getString(LoanIssueQuery.COL_PURPOSE),
                    cursor.getInt(LoanIssueQuery.COL_SECURITY_ACCOUNT_NUMBER),
                    cursor.getInt(LoanIssueQuery.COL_LOAN_INSTALMENT_TIMES),
                    cursor.getString(LoanIssueQuery.COL_OFFICE_STATEMENT));

            loanIssue.id = cursor.getLong(LoanIssueQuery.COL_ID);
            loanIssue.closedAt = cursor.getLong(LoanIssueQuery.COL_CLOSED_AT);
            loanIssue.createdAt = cursor.getLong(LoanIssueQuery.COL_CREATED_AT);
            loanIssue.updatedAt = cursor.getLong(LoanIssueQuery.COL_UPDATED_AT);

            Transaction txn = new Transaction(context, loanIssue.accountNumber,
                    loanIssue.amount, cursor.getInt(LoanIssueQuery.COL_VOUCHER_TYPE),
                    cursor.getInt(LoanIssueQuery.COL_LEDGER), cursor.getString(LoanIssueQuery.COL_NARRATION),
                    cursor.getLong(LoanIssueQuery.COL_FK_OFFICER_ID));

            txn.setId(cursor.getLong(LoanIssueQuery.COL_TXN_ID));
            txn.setDefinedDepositDate(cursor.getLong(LoanIssueQuery.COL_DEFINED_DEPOSIT_DATE));
            txn.setLoanPayedId(loanIssue.id);
            txn.setCreatedAt(loanIssue.createdAt);
            txn.setUpdatedAt(loanIssue.updatedAt);

            loanIssue.transaction = txn;
        }
        return loanIssue;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Member getSecurityMember() {
        return securityMember;
    }

    public void setSecurityMember(Member securityMember) {
        this.securityMember = securityMember;
    }

    public String getOfficeStatement() {
        return officeStatement;
    }

    public void setOfficeStatement(String officeStatement) {
        this.officeStatement = officeStatement;
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

    public int getSecurityAccountNo() {
        return securityAccountNo;
    }

    public void setSecurityAccountNo(int securityAccountNo) {
        this.securityAccountNo = securityAccountNo;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public long getClosedAt() {
        return closedAt;
    }

    public void setClosedAt(long closedAt) {
        this.closedAt = closedAt;
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

    public int getLoanInstalmentTimes() {
        return loanInstalmentTimes;
    }

    public void setLoanInstalmentTimes(int loanInstalmentTimes) {
        this.loanInstalmentTimes = loanInstalmentTimes;
    }

    public double getLoanInstalmentAmount() {
        return loanInstalmentAmount;
    }

    public void setLoanInstalmentAmount(double loanInstalmentAmount) {
        this.loanInstalmentAmount = loanInstalmentAmount;
    }

    public interface LoanIssueQuery {
        String[] PROJECTION = new String[]{
                SurakshaContract.LoanIssueEntry.TABLE_NAME + "." + SurakshaContract.LoanIssueEntry._ID,
                SurakshaContract.LoanIssueEntry.TABLE_NAME + "." + SurakshaContract.LoanIssueEntry.COLUMN_FK_ACCOUNT_NUMBER,
                SurakshaContract.LoanIssueEntry.TABLE_NAME + "." + SurakshaContract.LoanIssueEntry.COLUMN_SECURITY_ACCOUNT_NUMBER,
                SurakshaContract.LoanIssueEntry.COLUMN_PURPOSE,
                SurakshaContract.LoanIssueEntry.TABLE_NAME + "." + SurakshaContract.LoanIssueEntry.COLUMN_AMOUNT,
                SurakshaContract.LoanIssueEntry.COLUMN_LOAN_INSTALMENT_AMOUNT,
                SurakshaContract.LoanIssueEntry.COLUMN_LOAN_INSTALMENT_TIMES,
                SurakshaContract.LoanIssueEntry.COLUMN_OFFICE_STATEMENT,
                SurakshaContract.LoanIssueEntry.TABLE_NAME + "." + SurakshaContract.LoanIssueEntry.COLUMN_CLOSED_AT,
                SurakshaContract.LoanIssueEntry.TABLE_NAME + "." + SurakshaContract.LoanIssueEntry.COLUMN_CREATED_AT,
                SurakshaContract.LoanIssueEntry.TABLE_NAME + "." + SurakshaContract.LoanIssueEntry.COLUMN_UPDATED_AT,

                SurakshaContract.TxnEntry.TABLE_NAME + "." + SurakshaContract.TxnEntry._ID,
                SurakshaContract.TxnEntry.COLUMN_VOUCHER_TYPE,
                SurakshaContract.TxnEntry.COLUMN_LEDGER,
                SurakshaContract.TxnEntry.COLUMN_NARRATION,
                SurakshaContract.TxnEntry.COLUMN_DEFINED_DEPOSIT_DATE,
                SurakshaContract.TxnEntry.COLUMN_FK_LOAN_PAYED_ID,
                SurakshaContract.TxnEntry.COLUMN_FK_OFFICER_ID
        };
        int COL_ID = 0;
        int COL_FK_ACCOUNT_NUMBER = 1;
        int COL_SECURITY_ACCOUNT_NUMBER = 2;
        int COL_PURPOSE = 3;
        int COL_AMOUNT = 4;
        int COL_LOAN_INSTALMENT_AMOUNT = 5;
        int COL_LOAN_INSTALMENT_TIMES = 6;
        int COL_OFFICE_STATEMENT = 7;
        int COL_CLOSED_AT = 8;
        int COL_CREATED_AT = 9;
        int COL_UPDATED_AT = 10;

        int COL_TXN_ID = 11;
        int COL_VOUCHER_TYPE = 12;
        int COL_LEDGER = 13;
        int COL_NARRATION = 14;
        int COL_DEFINED_DEPOSIT_DATE = 15;
        int COL_FK_LOAN_PAYED_ID = 16;
        int COL_FK_OFFICER_ID = 17;


    }
}
