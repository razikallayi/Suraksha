package com.razikallayi.suraksha.account;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.Toast;

import com.razikallayi.suraksha.member.Member;
import com.razikallayi.suraksha.R;
import com.razikallayi.suraksha.txn.Transaction;
import com.razikallayi.suraksha.data.SurakshaContract;
import com.razikallayi.suraksha.utils.Utility;

import java.io.Serializable;

/**
 * Created by Razi Kallayi on 29-01-2016.
 */
public class Account implements Serializable {
    private long id;
    private int accountNumber;
    private Member member;
    private double openingBalance;
    private double instalmentAmount = 1000;
    private int isActive;
    private String closedAt;
    private String createdAt;
    private String updatedAt;

    public static int generateAccountNumber(Context context) {
        Cursor cursor = context.getContentResolver().query(SurakshaContract.AccountEntry.CONTENT_URI,
                new String[]{"Max("+SurakshaContract.AccountEntry.COLUMN_ACCOUNT_NUMBER+")"},null,
                null,null);
        if (cursor != null) {
            cursor.moveToFirst();
            int accountNumber = cursor.getInt(0)+1;
            cursor.close();
            return accountNumber;
        }
        return 0;
    }

    public int getIsActive() {
        return isActive;
    }

    public void setIsActive(int isActive) {
        this.isActive = isActive;
    }
    public static ContentValues getAccountContentValues(Account account){
        ContentValues values= new ContentValues();

        values.put(SurakshaContract.AccountEntry.COLUMN_ACCOUNT_NUMBER,    account.getAccountNumber()    );
        values.put(SurakshaContract.AccountEntry.COLUMN_MEMBER_ID,         account.getMember().getId()   );
        values.put(SurakshaContract.AccountEntry.COLUMN_INSTALMENT_AMOUNT, account.getInstalmentAmount() );
        values.put(SurakshaContract.AccountEntry.COLUMN_OPENING_BALANCE,   account.getOpeningBalance()   );
        values.put(SurakshaContract.AccountEntry.COLUMN_IS_ACTIVE,         account.getIsActive()            );
        values.put(SurakshaContract.AccountEntry.COLUMN_CREATED_AT,        System.currentTimeMillis()         );

        return values;
    }
    public static void makeDeposit(Context context,int accountNumber, int month, String remarks){
        Transaction txnMonthlyDeposit = new Transaction(accountNumber, Utility.getMonthlyDepositAmount(),
                SurakshaContract.TxnEntry.RECEIPT_VOUCHER,SurakshaContract.TxnEntry.DEPOSIT_LEDGER,remarks);
        txnMonthlyDeposit.setDefinedDepositDate(month);
        MakeDepositTask makeDepositTask = new MakeDepositTask(context);
        makeDepositTask.execute(txnMonthlyDeposit);
    }

    private static class MakeDepositTask extends AsyncTask<Transaction, Void, Uri> {
        Context context;

        public MakeDepositTask(Context context) {
            this.context = context;
        }
        @Override
        protected Uri doInBackground(Transaction... txnMonthlyDeposit) {
            ContentValues values = Transaction.getTxnContentValues(txnMonthlyDeposit[0]);
            Uri uriCreatedRow = context.getContentResolver().insert(SurakshaContract.TxnEntry.CONTENT_URI, values);
            return uriCreatedRow;
        };

        @Override
        protected void onPostExecute(Uri uri) {
            if (uri != null) {
                Toast.makeText(context, context.getString(R.string.deposit_success), Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(context, context.getString(R.string.deposit_failed), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public Account() {
    }

    public Account(Member member, double openingBalance, int isActive) {
        this.member = member;
        this.openingBalance = openingBalance;
        this.isActive = isActive;
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
