package com.razikallayi.suraksha.txn;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.razikallayi.suraksha.data.SurakshaContract;

/**
 * Created by Razi Kallayi on 31-01-2016.
 */
public class Transaction {

    private long id;
    private int accountNumber;
    private double amount;
    private long definedDepositDate = -1;
    private int loanPayedId = -1;
    private int voucherType;
    private int ledger;
    private String narration;
    private String createdAt;
    private String updatedAt;


    public static final String[] TXN_COLUMNS = new String[]{
            SurakshaContract.TxnEntry.TABLE_NAME + "." + SurakshaContract.TxnEntry._ID,
            SurakshaContract.TxnEntry.COLUMN_FK_ACCOUNT_NUMBER,
            SurakshaContract.TxnEntry.COLUMN_AMOUNT,
            SurakshaContract.TxnEntry.COLUMN_VOUCHER_TYPE,
            SurakshaContract.TxnEntry.COLUMN_LEDGER,
            SurakshaContract.TxnEntry.COLUMN_NARRATION,
            SurakshaContract.TxnEntry.TABLE_NAME + "." + SurakshaContract.TxnEntry.COLUMN_CREATED_AT,
            SurakshaContract.TxnEntry.TABLE_NAME + "." + SurakshaContract.TxnEntry.COLUMN_UPDATED_AT,
            SurakshaContract.TxnEntry.COLUMN_DEFINED_DEPOSIT_DATE,
            SurakshaContract.TxnEntry.COLUMN_FK_LOAN_PAYED_ID
    };


    public static final int _ID = 0;
    public static final int COL_FK_ACCOUNT_NUMBER = 1;
    public static final int COL_AMOUNT = 2;
    public static final int COL_VOUCHER_TYPE = 3;
    public static final int COL_LEDGER = 4;
    public static final int COL_NARRATION = 5;
    public static final int COL_CREATED_AT = 6;
    public static final int COL_UPDATED_AT = 7;
    public static final int COL_DEFINED_DEPOSIT_DATE = 8;
    public static final int COL_FK_LOAN_PAYED_ID = 9;


    public Transaction(int accountNumber, double amount, int voucherType, int ledger, String narration) {
        this.accountNumber = accountNumber;
        this.amount = amount;
        this.voucherType = voucherType;
        this.ledger = ledger;
        this.narration = narration;
    }

    public Transaction() {
    }

    public static final String getLedgerName(int ledger){
        switch (ledger){
            case 1://REGISTRATION_FEE_LEDGER
                return "REGISTRATION FEE";
            case 2://DEPOSIT_LEDGER
                return "DEPOSIT";
            case 3://LOAN
                return "LOAN";
            case 4://WORKING COST
                return "WORKING COST";
            default:
                return "UNKNOWN";
        }
    }

    public static final String getVoucherName(int ledger){
        switch (ledger){
            case 0://REGISTRATION_FEE_LEDGER
                return "PAYED";
            case 1://DEPOSIT_LEDGER
                return "RECEIVED";
            default:
                return "UNKNOWN";
        }
    }

    //Get working money fund amount
    public static Double getWmf(Context context){
        Cursor cursor = context.getContentResolver().query(SurakshaContract.TxnEntry.buildGetWmfUri(),null,null,null,null);
        if(cursor != null) {
            cursor.moveToFirst();
        }
        Double wmfAmount = cursor.getDouble(0);
        Log.d("FISH", "getWmf: "+wmfAmount);
        cursor.close();
        return wmfAmount;
    }

    //Get Total deposit amount in suraksha
    public static Double getTotalDeposit(Context context){
        Cursor cursor = context.getContentResolver().query(SurakshaContract.TxnEntry.buildGetTotalDeposit(),null,null,null,null);
        if(cursor != null) {
            cursor.moveToFirst();
        }
        Double amount = null;
        if (cursor != null) {
            amount = cursor.getDouble(0);
            cursor.close();
        }
        return amount;
    }

    //Get Total Loan Payed amount in suraksha
    public static Double getTotalLoanPayed(Context context){
        Cursor cursor = context.getContentResolver().query(SurakshaContract.TxnEntry.buildTotalLoanPayed(),null,null,null,null);
        if(cursor != null) {
            cursor.moveToFirst();
        }
        Double amount = null;
        if (cursor != null) {
            amount = cursor.getDouble(0);
            cursor.close();
        }
        return amount;
    }
    //Get Total Loan Payed amount in suraksha
    public static Double getTotalLoanReturn(Context context){
        Cursor cursor = context.getContentResolver().query(SurakshaContract.TxnEntry.buildTotalLoanReturn(),null,null,null,null);
        if(cursor != null) {
            cursor.moveToFirst();
        }
        Double amount = null;
        if (cursor != null) {
            amount = cursor.getDouble(0);
            cursor.close();
        }
        return amount;
    }



    public static ContentValues getTxnContentValues(Transaction txn){
        ContentValues values= new ContentValues();
        values.put(SurakshaContract.TxnEntry.COLUMN_FK_ACCOUNT_NUMBER, txn.getAccountNumber());
        values.put(SurakshaContract.TxnEntry.COLUMN_AMOUNT,            txn.getAmount());
        values.put(SurakshaContract.TxnEntry.COLUMN_DEFINED_DEPOSIT_DATE,  txn.getDefinedDepositMonth());
        values.put(SurakshaContract.TxnEntry.COLUMN_FK_LOAN_PAYED_ID,  txn.getLoanPayedId());
        values.put(SurakshaContract.TxnEntry.COLUMN_LEDGER,            txn.getLedger());
        values.put(SurakshaContract.TxnEntry.COLUMN_VOUCHER_TYPE, txn.getVoucherType());
        values.put(SurakshaContract.TxnEntry.COLUMN_NARRATION,         txn.getNarration());
        values.put(SurakshaContract.TxnEntry.COLUMN_CREATED_AT, System.currentTimeMillis());

        return values;
    }



    public int getVoucherType() {
        return voucherType;
    }

    public void setVoucherType(int voucherType) {
        this.voucherType = voucherType;
    }

    public int getLedger() {
        return ledger;
    }

    public void setLedger(int ledger) {
        this.ledger = ledger;
    }

    public int getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(int accountNumber) {
        this.accountNumber = accountNumber;
    }

    public long getDefinedDepositMonth() {
        return definedDepositDate;
    }

    public void setDefinedDepositDate(long definedDepositDate) {
        this.definedDepositDate = definedDepositDate;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getLoanPayedId() {
        return loanPayedId;
    }

    public void setLoanPayedId(int loanPayedId) {
        this.loanPayedId = loanPayedId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
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

    public String getNarration() {
        return narration;
    }

    public void setNarration(String narration) {
        this.narration = narration;
    }
}
