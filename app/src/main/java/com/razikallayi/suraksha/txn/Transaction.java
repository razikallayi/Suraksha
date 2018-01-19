package com.razikallayi.suraksha.txn;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.razikallayi.suraksha.data.SurakshaContract;
import com.razikallayi.suraksha.utils.CalendarUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Razi Kallayi on 31-01-2016.
 */
public class Transaction implements Serializable {

    private long id;
    private int accountNumber;
    private double amount;
    private int voucherType;
    private int ledger;
    private String narration;
    private long officer_id;
    private long depositForDate = -1;
    private long loanPayedId = -1;
    private long paymentDate = -1;
    private int instalmentNumber = -1;
    private long createdAt;
    private long updatedAt;
    //private Officer officer;


    public Transaction(Context context, int accountNumber, double amount, int voucherType,
                       int ledger, String narration, long officerId) {
        this.accountNumber = accountNumber;
        this.amount = amount;
        this.voucherType = voucherType;
        this.ledger = ledger;
        this.narration = narration;
        this.officer_id = officerId;
    }

    public Transaction() {
    }

    public static Transaction getTxnFromId(Context context, long id) {
        Cursor cursor = context.getContentResolver().query(
                SurakshaContract.TxnEntry.buildTxnUri(id),
                Transaction.TxnQuery.PROJECTION, null, null, null);

        return getTxnFromCursor(context, cursor);
    }


    public int destroy(Context context) {
        return context.getContentResolver().delete(
                SurakshaContract.TxnEntry.CONTENT_URI,
                SurakshaContract.TxnEntry._ID + " =? ", new String[]{String.valueOf(id)});
    }

    public static String getLedgerName(int ledger) {
        //Change in SurakshaContract.TxnEntry, if you make any change here
        switch (ledger) {
            case SurakshaContract.TxnEntry.REGISTRATION_FEE_LEDGER:
                return "REGISTRATION FEE";
            case SurakshaContract.TxnEntry.DEPOSIT_LEDGER:
                return "DEPOSIT";
            case SurakshaContract.TxnEntry.LOAN_ISSUED_LEDGER:
                return "LOAN ISSUED";
            case SurakshaContract.TxnEntry.LOAN_RETURN_LEDGER:
                return "LOAN RETURN";
            case SurakshaContract.TxnEntry.WORKING_COST_LEDGER:
                return "WORKING COST";
            case SurakshaContract.TxnEntry.ACCOUNT_CLOSE_LEDGER:
                return "ACCOUNT CLOSE";
            default:
                return "UNKNOWN";
        }
    }

    public static String getVoucherName(int ledger) {
        //Change in SurakshaContract.TxnEntry, if you make any change here
        switch (ledger) {
            case 100://REGISTRATION_FEE_LEDGER
                return "PAYED";
            case 101://DEPOSIT_LEDGER
                return "RECEIVED";
            default:
                return "UNKNOWN";
        }
    }


    //Get working money fund amount
    public static Double getWmf(Context context) {
        Cursor cursor = context.getContentResolver().query(SurakshaContract.TxnEntry.buildGetWmfUri(),
                null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        Double wmfAmount = cursor.getDouble(0);
        cursor.close();
        return wmfAmount;
    }

    //Get Total deposit amount in suraksha
    public static Double getTotalDeposit(Context context) {
        Cursor cursor = context.getContentResolver().query(SurakshaContract.TxnEntry.buildGetTotalDeposit(), null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        Double amount = null;
        if (cursor != null) {
            amount = cursor.getDouble(0);
            cursor.close();
        }
        return amount;
    }

    /*
        //Get Instalment count of loan return
        public int getInstalmentCount(Context context) {
            Cursor cursor = context.getContentResolver().query(SurakshaContract.TxnEntry.buildGetTotalDeposit(),
                    null, SurakshaContract.TxnEntry._ID + "= ?", new String[]{String.valueOf(this.loanPayedId)},
                    null);
            if (cursor != null) {
                cursor.moveToFirst();
            }
            Double amount = null;
            if (cursor != null) {
                amount = cursor.getDouble(0);
                cursor.close();
            }
            return amount;
        }
    */
    //Get Total Loan Payed amount in suraksha
    public static Double getTotalLoanPayed(Context context) {

        Cursor cursor = context.getContentResolver().query(SurakshaContract.TxnEntry.buildTotalLoanPayed(), null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        Double amount = null;
        if (cursor != null) {
            amount = cursor.getDouble(0);
            cursor.close();
        }
        return amount;
    }

    //Get Total Loan Return amount in suraksha
    public static Double getTotalLoanReturn(Context context) {
        Cursor cursor = context.getContentResolver().query(SurakshaContract.TxnEntry.buildTotalLoanReturn(), null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        Double amount = null;
        if (cursor != null) {
            amount = cursor.getDouble(0);
            cursor.close();
        }
        return amount;
    }

    public static List<Transaction> getTxnListFromCursor(Context context, Cursor c) {
        List<Transaction> txnList = new ArrayList<>();
        if (c == null || c.getCount() <= 0) {
            return txnList;
        }
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            Transaction txn = getTxnFromCursor(context, c);
            txnList.add(txn);
        }
        return txnList;
    }

    public static Transaction getTxnFromCursor(Context context, Cursor c) {
        Transaction txn = new Transaction();
        if (c != null) {
            if (c.getCount() == 1) {
                c.moveToFirst();
            }
            txn = new Transaction(context, c.getInt(TxnQuery.COL_FK_ACCOUNT_NUMBER),
                    c.getDouble(TxnQuery.COL_AMOUNT), c.getInt(TxnQuery.COL_VOUCHER_TYPE),
                    c.getInt(TxnQuery.COL_LEDGER), c.getString(TxnQuery.COL_NARRATION),
                    c.getLong(TxnQuery.COL_FK_OFFICER_ID));

            txn.id = c.getLong(TxnQuery.COL_ID);
            txn.depositForDate = c.getLong(TxnQuery.COL_DEFINED_DEPOSIT_DATE);
            txn.loanPayedId = c.getInt(TxnQuery.COL_FK_LOAN_PAYED_ID);
            txn.paymentDate = c.getLong(TxnQuery.COL_PAYMENT_DATE);
            txn.instalmentNumber = c.getInt(TxnQuery.COL_INSTALMENT_NUMBER);
            txn.createdAt = c.getLong(TxnQuery.COL_CREATED_AT);
            txn.updatedAt = c.getLong(TxnQuery.COL_UPDATED_AT);
        }
        return txn;
    }

    public static ContentValues getTxnContentValues(Transaction txn) {
        ContentValues values = new ContentValues();
        values.put(SurakshaContract.TxnEntry.COLUMN_FK_ACCOUNT_NUMBER, txn.getAccountNumber());
        values.put(SurakshaContract.TxnEntry.COLUMN_AMOUNT, txn.getAmount());
        values.put(SurakshaContract.TxnEntry.COLUMN_DEPOSIT_FOR_DATE, txn.getDefinedDepositMonth());
        values.put(SurakshaContract.TxnEntry.COLUMN_FK_LOAN_PAYED_ID, txn.getLoanPayedId());
        values.put(SurakshaContract.TxnEntry.COLUMN_LEDGER, txn.getLedger());
        values.put(SurakshaContract.TxnEntry.COLUMN_VOUCHER_TYPE, txn.getVoucherType());
        values.put(SurakshaContract.TxnEntry.COLUMN_NARRATION, txn.getNarration());
        values.put(SurakshaContract.TxnEntry.COLUMN_FK_OFFICER_ID, txn.getOfficer_id());
        values.put(SurakshaContract.TxnEntry.COLUMN_PAYMENT_DATE, txn.getPaymentDate());
        values.put(SurakshaContract.TxnEntry.COLUMN_INSTALMENT_NUMBER, txn.getInstalmentNumber());
        if (txn.createdAt == 0) {
            values.put(SurakshaContract.TxnEntry.COLUMN_CREATED_AT, System.currentTimeMillis());
        } else {
            values.put(SurakshaContract.TxnEntry.COLUMN_CREATED_AT, txn.createdAt);
        }
        values.put(SurakshaContract.TxnEntry.COLUMN_UPDATED_AT, System.currentTimeMillis());

        return values;
    }

    public boolean isLateDeposit() {
        int delayDate = CalendarUtils.getDelayDate();
        Calendar depositForDate = Calendar.getInstance();
        depositForDate.setTimeInMillis(this.depositForDate);
        depositForDate.set(Calendar.DAY_OF_MONTH, delayDate);
        return paymentDate > depositForDate.getTimeInMillis();
    }

    public int getVoucherType() {
        return voucherType;
    }

    public void setVoucherType(int voucherType) {
        this.voucherType = voucherType;
    }

    public String getVoucherName() {
        return Transaction.getVoucherName(voucherType);
    }

    public int getLedger() {
        return ledger;
    }

    public void setLedger(int ledger) {
        this.ledger = ledger;
    }

    public String getLedgerName() {
        return Transaction.getLedgerName(ledger);
    }

    public int getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(int accountNumber) {
        this.accountNumber = accountNumber;
    }

    public long getDefinedDepositMonth() {
        return depositForDate;
    }

    public void setDepositForDate(long depositForDate) {
        this.depositForDate = depositForDate;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getLoanPayedId() {
        return loanPayedId;
    }

    public void setLoanPayedId(long loanPayedId) {
        this.loanPayedId = loanPayedId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public long getOfficer_id() {
        return officer_id;
    }

    public void setOfficer_id(long officer_id) {
        this.officer_id = officer_id;
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

    public String getNarration() {
        return narration;
    }

    public void setNarration(String narration) {
        this.narration = narration;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id='" + id + '\'' +
                ", amount='" + amount + '\'' +
                ", voucher='" + getVoucherName(voucherType) + '\'' +
                ", ledger='" + getLedgerName(ledger) + '\'' +
                ", accountNo='" + accountNumber + '\'' +
                ", instalmentNo='" + instalmentNumber + '\'' +
                ", DepositMonth='" + CalendarUtils.readableDepositMonth(depositForDate) + '\'' +
                ", payedDate='" + CalendarUtils.formatDateTime(paymentDate) + '\'' +
                ", created='" + CalendarUtils.formatDateTime(createdAt) + '\'' +
                '}';
    }

    public long getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(long paymentDate) {
        this.paymentDate = paymentDate;
    }

    public int getInstalmentNumber() {
        return instalmentNumber;
    }

    public void setInstalmentNumber(int instalmentNumber) {
        this.instalmentNumber = instalmentNumber;
    }


    public interface TxnQuery {
        String[] PROJECTION = new String[]{
                SurakshaContract.TxnEntry.TABLE_NAME + "." + SurakshaContract.TxnEntry._ID,
                SurakshaContract.TxnEntry.COLUMN_FK_ACCOUNT_NUMBER,
                SurakshaContract.TxnEntry.COLUMN_AMOUNT,
                SurakshaContract.TxnEntry.COLUMN_VOUCHER_TYPE,
                SurakshaContract.TxnEntry.COLUMN_LEDGER,
                SurakshaContract.TxnEntry.COLUMN_NARRATION,
                SurakshaContract.TxnEntry.TABLE_NAME + "." + SurakshaContract.TxnEntry.COLUMN_CREATED_AT,
                SurakshaContract.TxnEntry.TABLE_NAME + "." + SurakshaContract.TxnEntry.COLUMN_UPDATED_AT,
                SurakshaContract.TxnEntry.COLUMN_DEPOSIT_FOR_DATE,
                SurakshaContract.TxnEntry.COLUMN_FK_LOAN_PAYED_ID,
                SurakshaContract.TxnEntry.COLUMN_FK_OFFICER_ID,
                SurakshaContract.TxnEntry.COLUMN_PAYMENT_DATE,
                SurakshaContract.TxnEntry.COLUMN_INSTALMENT_NUMBER,
        };
        int COL_ID = 0;
        int COL_FK_ACCOUNT_NUMBER = 1;
        int COL_AMOUNT = 2;
        int COL_VOUCHER_TYPE = 3;
        int COL_LEDGER = 4;
        int COL_NARRATION = 5;
        int COL_CREATED_AT = 6;
        int COL_UPDATED_AT = 7;
        int COL_DEFINED_DEPOSIT_DATE = 8;
        int COL_FK_LOAN_PAYED_ID = 9;
        int COL_FK_OFFICER_ID = 10;
        int COL_PAYMENT_DATE = 11;
        int COL_INSTALMENT_NUMBER = 12;
    }
}
