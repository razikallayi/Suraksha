package com.razikallayi.suraksha.loan;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.razikallayi.suraksha.data.SurakshaContract;
import com.razikallayi.suraksha.member.Member;
import com.razikallayi.suraksha.txn.Transaction;
import com.razikallayi.suraksha.utils.AuthUtils;
import com.razikallayi.suraksha.utils.CalendarUtils;

import java.io.Serializable;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by Razi Kallayi on 19-06-2016 08:37.
 */
public class LoanIssue implements Serializable {

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
    private long issuedAt;
    private long closedAt;
    private long createdAt;
    private long updatedAt;
    private Transaction transaction;
    private List<Transaction> loanReturnList = null;

    public LoanIssue() {
    }

    public LoanIssue(int accountNumber, double amount, String purpose, int securityAccountNo, int loanInstalmentTimes, String officeStatement, long issuedAt) {
        this.accountNumber = accountNumber;
        this.amount = amount;
        this.purpose = purpose;
        this.securityAccountNo = securityAccountNo;
        this.loanInstalmentTimes = loanInstalmentTimes;
        this.loanInstalmentAmount = amount / loanInstalmentTimes;
        this.officeStatement = officeStatement;
        this.issuedAt = issuedAt;
    }

    public static LoanIssue getLoanIssue(Context context, long loanIssueId) {
        Cursor cursor = context.getContentResolver().query(
                SurakshaContract.LoanIssueEntry.buildLoanIssueUri(loanIssueId),
                LoanIssueQuery.PROJECTION
                , null, null, null);
        if (cursor == null || cursor.getCount() == 0) {
            return null;
        }
        cursor.moveToFirst();
        return getLoanIssueFromCursor(context, cursor);
    }
/*
    public static List<LoanIssue> getLoanIssueListFromCursor(Context context, Cursor c) {
        List<LoanIssue> loanIssueList = new ArrayList<>();
        if (c == null || c.getCount() <= 0) {
            return loanIssueList;
        }
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            LoanIssue loanIssue = getLoanIssueFromCursor(context, c);
            loanIssueList.add(loanIssue);
        }
        return loanIssueList;
    }
*/

    public static ContentValues getLoanIssuedContentValues(LoanIssue loanIssue) {
        ContentValues values = new ContentValues();

        values.put(SurakshaContract.LoanIssueEntry.COLUMN_ACCOUNT_NUMBER, loanIssue.accountNumber);
        values.put(SurakshaContract.LoanIssueEntry.COLUMN_AMOUNT, loanIssue.amount);
        values.put(SurakshaContract.LoanIssueEntry.COLUMN_LOAN_INSTALMENT_TIMES, loanIssue.loanInstalmentTimes);
        values.put(SurakshaContract.LoanIssueEntry.COLUMN_LOAN_INSTALMENT_AMOUNT, loanIssue.loanInstalmentAmount);
        values.put(SurakshaContract.LoanIssueEntry.COLUMN_PURPOSE, loanIssue.purpose);
        values.put(SurakshaContract.LoanIssueEntry.COLUMN_SECURITY_ACCOUNT_NUMBER, loanIssue.securityAccountNo);
        values.put(SurakshaContract.LoanIssueEntry.COLUMN_OFFICE_STATEMENT, loanIssue.officeStatement);
        values.put(SurakshaContract.LoanIssueEntry.COLUMN_ISSUED_AT, loanIssue.issuedAt);
        values.put(SurakshaContract.LoanIssueEntry.COLUMN_CLOSED_AT, loanIssue.closedAt);
        if (loanIssue.createdAt == 0) {
            values.put(SurakshaContract.LoanIssueEntry.COLUMN_CREATED_AT, System.currentTimeMillis());
        } else {
            values.put(SurakshaContract.LoanIssueEntry.COLUMN_CREATED_AT, loanIssue.createdAt);
        }
        values.put(SurakshaContract.LoanIssueEntry.COLUMN_UPDATED_AT, loanIssue.updatedAt);

        return values;
    }

    public static LoanIssue getLoanIssueFromCursor(Context context, Cursor cursor) {
        LoanIssue loanIssue = new LoanIssue();
        if (cursor != null && cursor.getCount() > 0) {
            if (cursor.getCount() == 1) {
                cursor.moveToFirst();
            }
            loanIssue = new LoanIssue(cursor.getInt(LoanIssueQuery.COL_FK_ACCOUNT_NUMBER),
                    cursor.getDouble(LoanIssueQuery.COL_AMOUNT),
                    cursor.getString(LoanIssueQuery.COL_PURPOSE),
                    cursor.getInt(LoanIssueQuery.COL_SECURITY_ACCOUNT_NUMBER),
                    cursor.getInt(LoanIssueQuery.COL_LOAN_INSTALMENT_TIMES),
                    cursor.getString(LoanIssueQuery.COL_OFFICE_STATEMENT),
                    cursor.getLong(LoanIssueQuery.COL_ISSUED_AT));

            loanIssue.id = cursor.getLong(LoanIssueQuery.COL_ID);
            loanIssue.closedAt = cursor.getLong(LoanIssueQuery.COL_CLOSED_AT);
            loanIssue.createdAt = cursor.getLong(LoanIssueQuery.COL_CREATED_AT);
            loanIssue.updatedAt = cursor.getLong(LoanIssueQuery.COL_UPDATED_AT);

            Transaction txn = new Transaction(context, loanIssue.accountNumber,
                    loanIssue.amount, cursor.getInt(LoanIssueQuery.COL_VOUCHER_TYPE),
                    cursor.getInt(LoanIssueQuery.COL_LEDGER), cursor.getString(LoanIssueQuery.COL_NARRATION),
                    cursor.getLong(LoanIssueQuery.COL_FK_OFFICER_ID));

            txn.setId(cursor.getLong(LoanIssueQuery.COL_TXN_ID));
            txn.setDepositForDate(cursor.getLong(LoanIssueQuery.COL_DEFINED_DEPOSIT_DATE));
            txn.setLoanPayedId(loanIssue.id);
            txn.setCreatedAt(loanIssue.createdAt);
            txn.setUpdatedAt(loanIssue.updatedAt);

            loanIssue.transaction = txn;
        }
        return loanIssue;
    }

    @Override
    public String toString() {
        return "LoanIssue{" +
                "id='" + id + '\'' +
                ", amount='" + amount + '\'' +
                ", accountNumber='" + accountNumber + '\'' +
                ", securityAccNo='" + securityAccountNo + '\'' +
                ", accountNo='" + accountNumber + '\'' +
                ", instalmentAmount='" + loanInstalmentAmount + '\'' +
                ", instalmentTimes='" + loanInstalmentTimes + '\'' +
                ", purpose='" + purpose + '\'' +
                ", officeStatement='" + officeStatement + '\'' +
//                ", transaction='" + transaction.toString() + '\'' +
                ", issued='" + CalendarUtils.formatDateTime(issuedAt) + '\'' +
                ", created='" + CalendarUtils.formatDateTime(createdAt) + '\'' +
                '}';
    }

    public int nextInstalmentNumber(Context context) {
        return lastInstalmentNumber(context) + 1;
    }

    public boolean hasInstalmentDue(Context context) {
        long nextInstalmentDueDate = instalmentDueDate(nextInstalmentNumber(context));
        long currentDate = CalendarUtils.normalizeDate(System.currentTimeMillis());
        return nextInstalmentDueDate <= currentDate;
    }

    public boolean isInstalmentDue(int instalmentNo) {
        if (instalmentNo > loanInstalmentTimes) return false;
        return instalmentDueDate(instalmentNo)
                <= CalendarUtils.normalizeDate(System.currentTimeMillis());
    }

    public long instalmentDueDate(int instalmentNumber) {
        long issuedDate = CalendarUtils.normalizeDate(issuedAt);
        Calendar dueDateCal = new GregorianCalendar();
        dueDateCal.setTimeInMillis(issuedDate);
        dueDateCal.add(Calendar.MONTH, instalmentNumber);
        dueDateCal.set(Calendar.DAY_OF_MONTH, CalendarUtils.getDueDay());
        return dueDateCal.getTimeInMillis();
    }
//
//    public int nextInstalmentNumber(Context context) {
//        return getLoanReturnTxnList(context).size() + 1;
//    }

    public Transaction getInstalmentTxn(Context context, int instalmentNo) {
        String receiptVoucher = String.valueOf(SurakshaContract.TxnEntry.RECEIPT_VOUCHER);
        String loanReturnLedger = String.valueOf(SurakshaContract.TxnEntry.LOAN_RETURN_LEDGER);
        String selection = SurakshaContract.TxnEntry.COLUMN_FK_LOAN_PAYED_ID + " = ? and "
                + SurakshaContract.TxnEntry.COLUMN_VOUCHER_TYPE + " = ? and "
                + SurakshaContract.TxnEntry.COLUMN_LEDGER + " = ? and "
                + SurakshaContract.TxnEntry.COLUMN_INSTALMENT_NUMBER + " = ?";

        String[] selectionArgs = new String[]{String.valueOf(id),
                receiptVoucher,
                loanReturnLedger,
                String.valueOf(instalmentNo)};
        Cursor cursor = context.getContentResolver().query(
                SurakshaContract.TxnEntry.CONTENT_URI,
                Transaction.TxnQuery.PROJECTION, selection, selectionArgs, null);
        return Transaction.getTxnFromCursor(context, cursor);
    }

    public Transaction lastInstalmentTxn(Context context) {
        return getInstalmentTxn(context, lastInstalmentNumber(context));
    }

    public int lastInstalmentNumber(Context context) {
        String[] projection = new String[]{
                "MAX(" + SurakshaContract.TxnEntry.COLUMN_INSTALMENT_NUMBER + ")"
        };

        int colInstalmentNo = 0;
        String receiptVoucher = String.valueOf(SurakshaContract.TxnEntry.RECEIPT_VOUCHER);
        String loanReturnLedger = String.valueOf(SurakshaContract.TxnEntry.LOAN_RETURN_LEDGER);
        String selection = SurakshaContract.TxnEntry.COLUMN_FK_LOAN_PAYED_ID + " = ? and "
                + SurakshaContract.TxnEntry.COLUMN_VOUCHER_TYPE + " = ? and "
                + SurakshaContract.TxnEntry.COLUMN_LEDGER + " = ? ";
        String[] selectionArgs = new String[]{String.valueOf(id), receiptVoucher, loanReturnLedger};
        Cursor cursor = context.getContentResolver().query(
                SurakshaContract.TxnEntry.CONTENT_URI,
                projection,
                selection,
                selectionArgs, null);
        int instalmentNo = 0;
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                instalmentNo = cursor.getInt(colInstalmentNo);
            }
            cursor.close();
        }
        return instalmentNo;
    }

    public List<Transaction> getLoanReturnTxnList(Context context) {
        String receiptVoucher = String.valueOf(SurakshaContract.TxnEntry.RECEIPT_VOUCHER);
        String loanReturnLedger = String.valueOf(SurakshaContract.TxnEntry.LOAN_RETURN_LEDGER);
        Cursor cursor = context.getContentResolver().query(
                SurakshaContract.TxnEntry.CONTENT_URI,
                Transaction.TxnQuery.PROJECTION,
                SurakshaContract.TxnEntry.COLUMN_FK_LOAN_PAYED_ID + " = ? and "
                        + SurakshaContract.TxnEntry.COLUMN_VOUCHER_TYPE + " = ? and "
                        + SurakshaContract.TxnEntry.COLUMN_LEDGER + " = ? ",
                new String[]{String.valueOf(id), receiptVoucher, loanReturnLedger},
                SurakshaContract.TxnEntry._ID + " DESC");
        if (cursor == null) {
            return null;
        }
        loanReturnList = Transaction.getTxnListFromCursor(context, cursor);
        cursor.close();
        return loanReturnList;
    }


    public double sumOfLoanReturns(Context context) {
        Cursor cursor = context.getContentResolver().query(
                SurakshaContract.TxnEntry.CONTENT_URI,
                new String[]{"sum(" + SurakshaContract.TxnEntry.COLUMN_AMOUNT + ")"},
                SurakshaContract.TxnEntry.COLUMN_LEDGER + "= ? AND "
                        + SurakshaContract.TxnEntry.COLUMN_VOUCHER_TYPE + "= ? AND "
                        + SurakshaContract.TxnEntry.COLUMN_FK_LOAN_PAYED_ID + "= ? AND "
                        + SurakshaContract.TxnEntry.COLUMN_FK_ACCOUNT_NUMBER + "= ?",
                new String[]{String.valueOf(SurakshaContract.TxnEntry.LOAN_RETURN_LEDGER),
                        String.valueOf(SurakshaContract.TxnEntry.RECEIPT_VOUCHER),
                        String.valueOf(id),
                        String.valueOf(accountNumber)}, null);
        if (cursor == null || cursor.getCount() <= 0) {
            return 0.0;
        } else {
            cursor.moveToFirst();
            double sum = cursor.getDouble(0);
            cursor.close();
            return sum;
        }
    }

    public int bystanderReleaseInstalment() {
        return (int) Math.ceil((double) getLoanInstalmentTimes() / 2);
    }


    public int remainingInstalments(Context context) {
        return getLoanInstalmentTimes() - lastInstalmentNumber(context);
    }


    public int remainingInstalments(int instalmentNo) {
        return getLoanInstalmentTimes() - instalmentNo;
    }


    public Transaction updateLoanReturn(Context context, Transaction loanReturnTxn,
                                        long loanReturnDate, String remarks) {
        loanReturnTxn.setPaymentDate(loanReturnDate);
        loanReturnTxn.setNarration(remarks);
        ContentValues values = Transaction.getTxnContentValues(loanReturnTxn);
        context.getContentResolver().update(SurakshaContract.TxnEntry.CONTENT_URI,
                values, SurakshaContract.TxnEntry._ID + "=?",
                new String[]{String.valueOf(loanReturnTxn.getId())});

        this.getLoanReturnTxnList(context);
        return loanReturnTxn;
    }

    public Transaction saveLoanReturn(Context context, long returnDate, String narration) {
        int nextInstalmentCount = this.nextInstalmentNumber(context);

        int maxLoanInstalmentTime = this.getLoanInstalmentTimes();
        //Check if instalment is already complete
        if (nextInstalmentCount > maxLoanInstalmentTime) {
            this.closeLoan(context);
            getMember(context).saveHasLoan(context, false);
            return null;
        }

        Transaction txnLoanReturn = new Transaction(context, accountNumber, loanInstalmentAmount,
                SurakshaContract.TxnEntry.RECEIPT_VOUCHER,
                SurakshaContract.TxnEntry.LOAN_RETURN_LEDGER,
                narration, AuthUtils.getAuthenticatedOfficerId(context));
        txnLoanReturn.setLoanPayedId(id);
        txnLoanReturn.setPaymentDate(returnDate);
        txnLoanReturn.setInstalmentNumber(nextInstalmentCount);

        ContentValues values = Transaction.getTxnContentValues(txnLoanReturn);
        context.getContentResolver().insert(SurakshaContract.TxnEntry.CONTENT_URI, values);
        this.getLoanReturnTxnList(context);

        //If maximum instalment reached, close loan and free member
        if (nextInstalmentCount == maxLoanInstalmentTime) {
            this.closeLoan(context);
            getMember(context).saveHasLoan(context, false);
        }

        //if total loan returned amount is half of loan issued, security member lock is freed
        if (nextInstalmentCount == this.bystanderReleaseInstalment()) {
            getSecurityMember(context).saveIsLoanBlocked(context, false);
        }
        return txnLoanReturn;
    }

    public boolean isByStanderReleased(int instalmentNumber) {
        return instalmentNumber >= this.bystanderReleaseInstalment();
    }

    public static void saveIssueLoan(Context context, LoanIssue loanIssue, Member member) {
        Member securityMember = Member.getMemberFromAccountNumber(context,
                loanIssue.getSecurityAccountNo());
        loanIssue.setSecurityMember(securityMember);

        ContentValues loanIssueValues = LoanIssue.getLoanIssuedContentValues(loanIssue);
        long loanIssueId;
        Uri loanIssueUri = context.getContentResolver().insert(
                SurakshaContract.LoanIssueEntry.CONTENT_URI, loanIssueValues);
        loanIssueId = SurakshaContract.LoanIssueEntry.getLoanIssueIdFromUri(loanIssueUri);

        //Save Loan Transaction
        Transaction txnIssueLoan = new Transaction(context,
                loanIssue.getAccountNumber(),
                loanIssue.getAmount(),
                SurakshaContract.TxnEntry.PAYMENT_VOUCHER,
                SurakshaContract.TxnEntry.LOAN_ISSUED_LEDGER,
                loanIssue.getPurpose(),
                AuthUtils.getAuthenticatedOfficerId(context));
        txnIssueLoan.setLoanPayedId(loanIssueId);
        txnIssueLoan.setPaymentDate(System.currentTimeMillis());
        ContentValues txnValues = Transaction.getTxnContentValues(txnIssueLoan);

        context.getContentResolver().insert(SurakshaContract.TxnEntry.CONTENT_URI, txnValues);

        //Set Member HasLoan Flag On
        member.saveHasLoan(context, true);
        member.saveIsLoanBlocked(context, true);
        //Set Security Member loan Blocked
        securityMember.saveIsLoanBlocked(context, true);
    }

    public void closeLoan(Context context) {
        ContentValues values = new ContentValues();
        long currentTime = System.currentTimeMillis();
        values.put(SurakshaContract.LoanIssueEntry.COLUMN_CLOSED_AT, currentTime);
        values.put(SurakshaContract.LoanIssueEntry.COLUMN_UPDATED_AT, currentTime);
        context.getContentResolver().update(SurakshaContract.LoanIssueEntry.CONTENT_URI, values,
                SurakshaContract.LoanIssueEntry._ID + "= ?", new String[]{String.valueOf(this.id)});
        this.closedAt = currentTime;
        this.updatedAt = currentTime;
    }

    public boolean isClosed() {
        return this.closedAt > 0;
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

    public Member getSecurityMember(Context context) {
        if (securityMember == null) {
            return Member.getMemberFromAccountNumber(context, securityAccountNo);
        }
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

    public Member getMember(Context context) {
        if (member == null) {
            return Member.getMemberFromAccountNumber(context, accountNumber);
        }
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

    public long getIssuedAt() {
        return issuedAt;
    }

    public void setIssuedAt(long issuedAt) {
        this.issuedAt = issuedAt;
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
                SurakshaContract.LoanIssueEntry.TABLE_NAME + "." + SurakshaContract.LoanIssueEntry.COLUMN_ACCOUNT_NUMBER,
                SurakshaContract.LoanIssueEntry.TABLE_NAME + "." + SurakshaContract.LoanIssueEntry.COLUMN_SECURITY_ACCOUNT_NUMBER,
                SurakshaContract.LoanIssueEntry.COLUMN_PURPOSE,
                SurakshaContract.LoanIssueEntry.TABLE_NAME + "." + SurakshaContract.LoanIssueEntry.COLUMN_AMOUNT,
                SurakshaContract.LoanIssueEntry.COLUMN_LOAN_INSTALMENT_AMOUNT,
                SurakshaContract.LoanIssueEntry.COLUMN_LOAN_INSTALMENT_TIMES,
                SurakshaContract.LoanIssueEntry.COLUMN_OFFICE_STATEMENT,
                SurakshaContract.LoanIssueEntry.TABLE_NAME + "." + SurakshaContract.LoanIssueEntry.COLUMN_ISSUED_AT,
                SurakshaContract.LoanIssueEntry.TABLE_NAME + "." + SurakshaContract.LoanIssueEntry.COLUMN_CLOSED_AT,
                SurakshaContract.LoanIssueEntry.TABLE_NAME + "." + SurakshaContract.LoanIssueEntry.COLUMN_CREATED_AT,
                SurakshaContract.LoanIssueEntry.TABLE_NAME + "." + SurakshaContract.LoanIssueEntry.COLUMN_UPDATED_AT,

                SurakshaContract.TxnEntry.TABLE_NAME + "." + SurakshaContract.TxnEntry._ID,
                SurakshaContract.TxnEntry.COLUMN_VOUCHER_TYPE,
                SurakshaContract.TxnEntry.COLUMN_LEDGER,
                SurakshaContract.TxnEntry.COLUMN_NARRATION,
                SurakshaContract.TxnEntry.COLUMN_DEPOSIT_FOR_DATE,
                SurakshaContract.TxnEntry.COLUMN_FK_LOAN_PAYED_ID,
                SurakshaContract.TxnEntry.COLUMN_FK_OFFICER_ID,
        };
        int COL_ID = 0;
        int COL_FK_ACCOUNT_NUMBER = 1;
        int COL_SECURITY_ACCOUNT_NUMBER = 2;
        int COL_PURPOSE = 3;
        int COL_AMOUNT = 4;
        int COL_LOAN_INSTALMENT_AMOUNT = 5;
        int COL_LOAN_INSTALMENT_TIMES = 6;
        int COL_OFFICE_STATEMENT = 7;
        int COL_ISSUED_AT = 8;
        int COL_CLOSED_AT = 9;
        int COL_CREATED_AT = 10;
        int COL_UPDATED_AT = 11;

        int COL_TXN_ID = 12;
        int COL_VOUCHER_TYPE = 13;
        int COL_LEDGER = 14;
        int COL_NARRATION = 15;
        int COL_DEFINED_DEPOSIT_DATE = 16;
        int COL_FK_LOAN_PAYED_ID = 17;
        int COL_FK_OFFICER_ID = 18;


    }
}
